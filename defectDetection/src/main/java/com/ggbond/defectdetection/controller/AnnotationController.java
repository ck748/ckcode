package com.ggbond.defectdetection.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ggbond.defectdetection.common.Result;
import com.ggbond.defectdetection.pojo.AnnotationData;
import com.ggbond.defectdetection.pojo.RawImage;
import com.ggbond.defectdetection.service.AnnotationDataService;
import com.ggbond.defectdetection.service.RawImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 数据标注控制器
 * 处理摄像头拍照上传、图片管理、标注任务、标注数据等接口
 */
@RestController
@Slf4j
@ResponseBody
@RequestMapping("/annotation")
@CrossOrigin("*")
public class AnnotationController {

    @Autowired
    private RawImageService rawImageService;

    @Autowired
    private AnnotationDataService annotationDataService;

    /**
     * 摄像头拍照上传接口
     */
    @PostMapping("/upload/camera")
    public Result uploadCameraImage(@RequestParam("image") MultipartFile image,
                                    @RequestParam(required = false) Integer deviceId,
                                    @RequestParam(required = false) Integer workOrderId,
                                    @RequestParam(required = false) Integer userId,
                                    @RequestParam(required = false) String userName) {
        try {
            // 检查文件
            if (image.isEmpty()) {
                return Result.fail("上传文件为空");
            }

            // 生成唯一文件名
            String originalFilename = image.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件到指定目录
            String uploadDir = "uploads/images/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path filePath = Paths.get(uploadDir + uniqueFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 保存到数据库
            RawImage rawImage = new RawImage();
            rawImage.setImageName(originalFilename);
            rawImage.setImagePath(filePath.toString());
            rawImage.setImageSize(image.getSize());
            rawImage.setUploadSource("camera");
            rawImage.setDeviceId(deviceId);
            rawImage.setWorkOrderId(workOrderId);
            rawImage.setUploadTime(LocalDateTime.now());
            rawImage.setUploadUserId(userId);
            rawImage.setUploadUserName(userName);
            rawImage.setStatus(0); // 待标注状态

            rawImageService.save(rawImage);

            return Result.success("图片上传成功", rawImage);
        } catch (IOException e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            return Result.fail("图片上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("图片上传异常: {}", e.getMessage(), e);
            return Result.fail("图片上传异常: " + e.getMessage());
        }
    }

    /**
     * 获取待标注图片列表
     */
    @GetMapping("/images/pending")
    public Result getPendingImages(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   @RequestParam(required = false) Integer workOrderId) {
        try {
            IPage<RawImage> pageInfo = new Page<>(page, pageSize);
            LambdaQueryWrapper<RawImage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RawImage::getStatus, 0); // 待标注状态
            queryWrapper.eq(workOrderId != null, RawImage::getWorkOrderId, workOrderId);
            queryWrapper.orderByDesc(RawImage::getUploadTime);

            rawImageService.page(pageInfo, queryWrapper);

            return Result.success("获取待标注图片成功", pageInfo);
        } catch (Exception e) {
            log.error("获取待标注图片失败: {}", e.getMessage(), e);
            return Result.fail("获取待标注图片失败: " + e.getMessage());
        }
    }



    /**
     * 保存标注数据
     */
    @PostMapping("/data")
    public Result saveAnnotationData(@RequestBody List<AnnotationData> annotationDataList) {
        try {
            if (annotationDataList == null || annotationDataList.isEmpty()) {
                return Result.fail("标注数据为空");
            }

            // 保存标注数据
            annotationDataService.saveBatch(annotationDataList);

            // 更新图片状态为已标注
            if (!annotationDataList.isEmpty()) {
                Integer rawImageId = annotationDataList.get(0).getRawImageId();
                RawImage rawImage = rawImageService.getById(rawImageId);
                if (rawImage != null) {
                    rawImage.setStatus(2); // 已标注状态
                    rawImageService.updateById(rawImage);
                }
            }

            return Result.success("保存标注数据成功");
        } catch (Exception e) {
            log.error("保存标注数据失败: {}", e.getMessage(), e);
            return Result.fail("保存标注数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取图片的标注数据
     */
    @GetMapping("/data/image/{imageId}")
    public Result getAnnotationDataByImageId(@PathVariable Integer imageId) {
        try {
            LambdaQueryWrapper<AnnotationData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AnnotationData::getRawImageId, imageId);
            queryWrapper.eq(AnnotationData::getIsDeleted, 0);
            List<AnnotationData> annotationDataList = annotationDataService.list(queryWrapper);

            return Result.success("获取标注数据成功", annotationDataList);
        } catch (Exception e) {
            log.error("获取标注数据失败: {}", e.getMessage(), e);
            return Result.fail("获取标注数据失败: " + e.getMessage());
        }
    }



    /**
     * 访问上传的图片文件
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            // 构建文件路径 - 从uploads/images/目录读取
            String uploadDir = "uploads/images/";
            File file = new File(uploadDir + filename);
            
            log.info("尝试访问文件: {}", file.getAbsolutePath());
            
            if (!file.exists()) {
                log.warn("文件不存在: {}", file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // 读取文件
            Resource resource = new FileSystemResource(file);
            
            // 获取文件MIME类型
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("读取文件失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/images/{imageId}")
    public Result deleteImage(@PathVariable Integer imageId) {
        try {
            // 查询图片信息
            RawImage rawImage = rawImageService.getById(imageId);
            if (rawImage == null) {
                return Result.fail("图片不存在");
            }

            // 先删除相关的标注数据
            LambdaQueryWrapper<AnnotationData> annotationQuery = new LambdaQueryWrapper<>();
            annotationQuery.eq(AnnotationData::getRawImageId, imageId);
            annotationDataService.remove(annotationQuery);
            log.info("已删除图片 {} 的相关标注数据", imageId);

            // 删除文件
            String imagePath = rawImage.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("已删除文件: {}", imagePath);
                    } else {
                        log.warn("文件删除失败: {}", imagePath);
                    }
                }
            }

            // 从数据库删除记录
            rawImageService.removeById(imageId);
            log.info("已从数据库删除图片记录: {}", imageId);

            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除图片失败: {}", e.getMessage(), e);
            return Result.fail("删除图片失败: " + e.getMessage());
        }
    }
}