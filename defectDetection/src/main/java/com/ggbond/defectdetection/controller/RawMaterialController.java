package com.ggbond.defectdetection.controller;

import com.ggbond.defectdetection.common.Result;
import com.ggbond.defectdetection.service.blockchain.RawMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 原材料区块链控制器
 * 
 * @author defectDetection
 * @date 2024
 */
@RestController
@RequestMapping("/blockchain/rawMaterial")
@Slf4j
@Tag(name = "原材料区块链接口", description = "原材料信息上链相关接口")
@ConditionalOnProperty(name = "blockchain.enabled", havingValue = "true")
public class RawMaterialController {

    @Autowired(required = false)
    private RawMaterialService rawMaterialService;

    /**
     * 设置原材料信息
     */
    @PostMapping("/set")
    @Operation(summary = "设置原材料信息", description = "将原材料信息记录到区块链")
    public Result setRawMaterial(
        @RequestParam String rawProducerAddr,
        @RequestParam String axleProducerAddr,
        @RequestParam Long totalRawWeight,
        @RequestParam String batchId
    ) {
        try {
            if (rawMaterialService == null || !rawMaterialService.isAvailable()) {
                return Result.fail("原材料服务不可用");
            }

            Map<String, Object> result = rawMaterialService.setRawMaterial(
                rawProducerAddr,
                axleProducerAddr,
                totalRawWeight,
                batchId
            );

            if ((Boolean) result.get("success")) {
                return Result.success("原材料信息上链成功", result);
            } else {
                return Result.fail(result.get("message").toString());
            }
            
        } catch (Exception e) {
            log.error("设置原材料信息失败: {}", e.getMessage(), e);
            return Result.fail("设置失败: " + e.getMessage());
        }
    }

    /**
     * 获取原材料信息
     */
    @GetMapping("/get")
    @Operation(summary = "获取原材料信息", description = "从区块链查询原材料信息")
    public Result getRawMaterial() {
        try {
            if (rawMaterialService == null || !rawMaterialService.isAvailable()) {
                return Result.fail("原材料服务不可用");
            }

            Map<String, Object> result = rawMaterialService.getRawMaterial();
            
            if ((Boolean) result.get("success")) {
                return Result.success("查询成功", result);
            } else {
                return Result.fail(result.get("message").toString());
            }
            
        } catch (Exception e) {
            log.error("查询原材料信息失败: {}", e.getMessage(), e);
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取批次ID
     */
    @GetMapping("/batchId")
    @Operation(summary = "获取批次ID", description = "查询原材料批次ID")
    public Result getBatchId() {
        try {
            if (rawMaterialService == null || !rawMaterialService.isAvailable()) {
                return Result.fail("原材料服务不可用");
            }

            String batchId = rawMaterialService.getBatchId();
            
            if (batchId != null) {
                return Result.success("查询成功", batchId);
            } else {
                return Result.fail("批次ID为空或未设置");
            }
            
        } catch (Exception e) {
            log.error("查询批次ID失败: {}", e.getMessage(), e);
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    /**
     * 测试示例
     */
    @PostMapping("/test")
    @Operation(summary = "测试接口", description = "快速测试原材料合约功能")
    public Result test() {
        try {
            if (rawMaterialService == null || !rawMaterialService.isAvailable()) {
                return Result.fail("原材料服务不可用");
            }

            // 设置测试数据
            Map<String, Object> setResult = rawMaterialService.setRawMaterial(
                "北京原材料生产商A",
                "上海轴承制造商B",
                1000L,
                "BATCH-2024-12-04-001"
            );

            if (!(Boolean) setResult.get("success")) {
                return Result.fail("设置测试数据失败: " + setResult.get("message"));
            }

            // 查询数据验证
            Map<String, Object> getResult = rawMaterialService.getRawMaterial();

            Map<String, Object> testResult = new HashMap<>();
            testResult.put("设置结果", setResult);
            testResult.put("查询结果", getResult);
            
            return Result.success("测试完成", testResult);
            
        } catch (Exception e) {
            log.error("测试失败: {}", e.getMessage(), e);
            return Result.fail("测试失败: " + e.getMessage());
        }
    }

    /**
     * 检查服务状态
     */
    @GetMapping("/status")
    @Operation(summary = "检查服务状态", description = "检查原材料服务是否可用")
    public Result checkStatus() {
        Map<String, Object> status = new HashMap<>();
        
        boolean available = rawMaterialService != null && rawMaterialService.isAvailable();
        status.put("available", available);
        status.put("service", "RawMaterial");
        
        if (available) {
            status.put("message", "服务正常");
            return Result.success("服务可用", status);
        } else {
            status.put("message", "服务不可用");
            return Result.fail("服务不可用", status);
        }
    }
}
