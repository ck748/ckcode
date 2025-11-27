<template>
  <div class="annotation-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>数据标注平台</h2>
      <div class="header-actions">
        <el-button type="success" icon="el-icon-upload" @click="uploadImage">上传图片</el-button>
      </div>
    </div>

    <!-- 标注任务和图片展示区域 -->
    <div class="main-content">
      <!-- 左侧：任务列表 - 隐藏 -->
      <!-- <div class="task-panel"> ... </div> -->

      <!-- 中间：图片列表 -->
      <div class="image-panel" style="flex: 1;">
        <el-card class="image-card">
          <div slot="header" class="image-header">
            <span>待标注图片</span>
            <div class="image-header-actions">
              <el-button size="small" @click="refreshImages">刷新</el-button>
            </div>
          </div>
          <div class="image-grid">
            <div 
              v-for="image in imageList" 
              :key="image.id" 
              class="image-item"
              :class="{ 'selected': selectedImageId === image.id }"
            >
              <div class="image-preview" @click="selectImage(image)">
                <img 
                  :src="getImageUrl(image.imagePath)" 
                  :alt="image.imageName"
                  @error="handleImageError"
                />
                <div class="image-overlay">
                  <el-tag size="mini" :type="getImageStatusType(image.status)">
                    {{ getImageStatusText(image.status) }}
                  </el-tag>
                </div>
                <div class="image-actions">
                  <el-button 
                    type="danger" 
                    icon="el-icon-delete" 
                    size="mini" 
                    circle
                    @click.stop="deleteImage(image)"
                    title="删除图片"
                  ></el-button>
                </div>
              </div>
              <div class="image-info">
                <div class="image-name" :title="image.imageName">{{ image.imageName }}</div>
                <div class="image-meta">
                  <span>{{ formatFileSize(image.imageSize) }}</span>
                  <span>{{ image.imageWidth }}×{{ image.imageHeight }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="pagination-container">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="imagePage"
              :page-sizes="[12, 24, 36, 48]"
              :page-size="imagePageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="imageTotal"
              background
            >
            </el-pagination>
          </div>
        </el-card>
      </div>

      <!-- 右侧：标注区域 -->
      <div class="annotation-panel">
        <el-card class="annotation-card" v-if="selectedImage">
          <div slot="header" class="annotation-header">
            <span>图片标注</span>
            <div class="annotation-actions">
              <el-button 
                type="primary" 
                size="small" 
                @click="saveAnnotations"
                :loading="savingAnnotations"
              >
                保存标注
              </el-button>
            </div>
          </div>
          <div class="annotation-content">
            <div class="image-container">
              <div style="position: relative; display: inline-block;">
                <img 
                  :src="getImageUrl(selectedImage.imagePath)" 
                  :alt="selectedImage.imageName"
                  class="annotation-image"
                  ref="annotationImage"
                  @load="initCanvas"
                  style="display: block;"
                />
                <canvas
                  ref="annotationCanvas"
                  @mousedown="startDrawing"
                  @mousemove="drawRectangle"
                  @mouseup="finishDrawing"
                  style="position: absolute; top: 0; left: 0; cursor: crosshair; pointer-events: auto;"
                ></canvas>
              </div>
            </div>
            <div class="annotation-tools">
              <div class="tool-section">
                <h4>缺陷类别（请选择一个）</h4>
                <div class="categories">
                  <el-tag 
                    v-for="category in defectCategories" 
                    :key="category.id"
                    :type="selectedCategory === category.id ? 'success' : ''"
                    size="large"
                    @click.native="selectCategory(category.id)"
                    class="category-tag"
                    style="cursor: pointer; margin: 10px; padding: 15px 30px; font-size: 16px;"
                  >
                    {{ category.name }}
                  </el-tag>
                </div>
                <div style="margin-top: 20px; color: #909399;" v-if="selectedCategory">
                  <i class="el-icon-success" style="color: #67C23A;"></i>
                  已选择：{{ getCategoryName(selectedCategory) }}
                </div>
                <div style="margin-top: 15px;" v-if="selectedCategory && (getCategoryName(selectedCategory) === '裂痕' || getCategoryName(selectedCategory) === '划痕')">
                  <el-alert
                    title="请在图片上绘制标注框"
                    type="warning"
                    description="在图片上按住鼠标左键，拖动到缺陷位置，松开完成绘制"
                    :closable="false"
                    show-icon>
                  </el-alert>
                </div>
                <div style="margin-top: 15px;" v-if="currentAnnotations.length > 0">
                  <h4>已绘制标注框：{{ currentAnnotations.length }} 个</h4>
                  <el-button size="small" type="danger" @click="clearAnnotations">清除所有标注</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>
        <el-card class="annotation-card" v-else>
          <div class="empty-annotation">
            <i class="el-icon-picture-outline"></i>
            <p>请选择一张图片进行标注</p>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 上传图片对话框 -->
    <el-dialog title="上传图片" :visible.sync="uploadDialogVisible" width="500px">
      <el-upload
        class="upload-demo"
        drag
        action="/api/annotation/upload/camera"
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="uploadFileList"
        multiple
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过5MB</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmUpload" :loading="uploading">上 传</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Annotation',
  data() {
    return {
      // 图片相关
      imageList: [],
      selectedImage: null,
      selectedImageId: null,
      imagePage: 1,
      imagePageSize: 12,
      imageTotal: 0,
      
      // 标注相关
      selectedCategory: null,
      defectCategories: [],
      currentAnnotations: [],
      
      // Canvas绘图相关
      canvasContext: null,
      isDrawing: false,
      startX: 0,
      startY: 0,
      activeTool: 'rectangle',
      
      // 对话框相关
      uploadDialogVisible: false,
      uploadFileList: [],
      uploading: false,
      savingAnnotations: false
    }
  },
  mounted() {
    this.loadImages()
    this.loadDefectCategories()
  },
  methods: {
    // 图片相关方法
    loadImages() {
      axios.get('/api/annotation/images/pending', {
        params: {
          page: this.imagePage,
          pageSize: this.imagePageSize
        }
      }).then(response => {
        if (response.data.code === 200) {
          this.imageList = response.data.data.records || []
          this.imageTotal = response.data.data.total || 0
        }
      }).catch(error => {
        console.error('加载图片失败:', error)
        this.$message.error('加载图片失败')
      })
    },
    
    refreshImages() {
      this.loadImages()
      this.$message.success('图片列表已刷新')
    },
    
    selectImage(image) {
      this.selectedImage = image
      this.selectedImageId = image.id
      this.currentAnnotations = []
      this.$nextTick(() => {
        this.initCanvas()
        this.loadImageAnnotations(image.id)
      })
    },
    
    deleteImage(image) {
      this.$confirm(`确认删除图片 "${image.imageName}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 调用删除接口
        axios.delete(`/api/annotation/images/${image.id}`)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('删除成功')
              // 如果删除的是当前选中的图片，清空选中状态
              if (this.selectedImageId === image.id) {
                this.selectedImage = null
                this.selectedImageId = null
                this.selectedCategory = null
              }
              // 重新加载图片列表
              this.loadImages()
            } else {
              this.$message.error(response.data.msg || '删除失败')
            }
          })
          .catch(error => {
            console.error('删除图片失败:', error)
            this.$message.error('删除图片失败')
          })
      }).catch(() => {
        // 取消删除
      })
    },
    
    handleSizeChange(val) {
      this.imagePageSize = val
      this.imagePage = 1
      this.loadImages()
    },
    
    handleCurrentChange(val) {
      this.imagePage = val
      this.loadImages()
    },
    
    // 标注相关方法
    initCanvas() {
      this.$nextTick(() => {
        if (!this.$refs.annotationCanvas || !this.$refs.annotationImage) {
          console.log('Canvas或图片引用不存在')
          return
        }
        
        const canvas = this.$refs.annotationCanvas
        const image = this.$refs.annotationImage
        
        // 等待图片完全加载
        if (!image.complete) {
          console.log('图片未加载完成，等待加载...')
          image.onload = () => this.initCanvas()
          return
        }
        
        // 直接使用图片的实际显示尺寸
        const width = image.width || image.offsetWidth || image.clientWidth
        const height = image.height || image.offsetHeight || image.clientHeight
        
        // 设置Canvas尺寸与图片完全一致
        canvas.width = width
        canvas.height = height
        
        console.log('✅ Canvas初始化成功：', {
          canvasWidth: canvas.width,
          canvasHeight: canvas.height,
          imageOffsetWidth: image.offsetWidth,
          imageOffsetHeight: image.offsetHeight,
          imageClientWidth: image.clientWidth,
          imageClientHeight: image.clientHeight
        })
        
        // 初始化画布上下文
        this.canvasContext = canvas.getContext('2d')
        this.canvasContext.strokeStyle = '#ff0000'
        this.canvasContext.lineWidth = 3
        
        // 重绘已有标注
        this.redrawAnnotations()
      })
    },
    
    setActiveTool(tool) {
      this.activeTool = tool
    },
    
    selectCategory(categoryId) {
      this.selectedCategory = categoryId
      console.log('选择缺陷类别:', categoryId, this.getCategoryName(categoryId))
    },
    
    startDrawing(event) {
      if (this.activeTool !== 'rectangle') return
      
      const canvas = this.$refs.annotationCanvas
      const rect = canvas.getBoundingClientRect()
      this.startX = event.clientX - rect.left
      this.startY = event.clientY - rect.top
      this.isDrawing = true
    },
    
    drawRectangle(event) {
      if (!this.isDrawing || this.activeTool !== 'rectangle') return
      
      const canvas = this.$refs.annotationCanvas
      const rect = canvas.getBoundingClientRect()
      const currentX = event.clientX - rect.left
      const currentY = event.clientY - rect.top
      
      // 清除之前的临时矩形
      this.redrawAnnotations()
      
      // 绘制新的临时矩形
      this.canvasContext.strokeStyle = '#ff0000'
      this.canvasContext.lineWidth = 2
      this.canvasContext.strokeRect(
        this.startX, 
        this.startY, 
        currentX - this.startX, 
        currentY - this.startY
      )
    },
    
    finishDrawing(event) {
      if (!this.isDrawing || this.activeTool !== 'rectangle') return
      
      const canvas = this.$refs.annotationCanvas
      const rect = canvas.getBoundingClientRect()
      const endX = event.clientX - rect.left
      const endY = event.clientY - rect.top
      
      // 创建标注数据
      const annotation = {
        rawImageId: this.selectedImage.id,
        taskId: parseInt(this.activeTaskId) || null,
        categoryId: this.selectedCategory,
        category: this.getCategoryName(this.selectedCategory),
        x: Math.min(this.startX, endX),
        y: Math.min(this.startY, endY),
        width: Math.abs(endX - this.startX),
        height: Math.abs(endY - this.startY),
        confidence: 1.0,
        annotatorId: 1, // 假设当前用户ID为1
        annotatorName: '标注员',
        annotationTime: new Date().toISOString()
      }
      
      this.currentAnnotations.push(annotation)
      this.isDrawing = false
      this.redrawAnnotations()
    },
    
    redrawAnnotations() {
      if (!this.canvasContext) return
      
      // 清除画布
      this.canvasContext.clearRect(0, 0, this.$refs.annotationCanvas.width, this.$refs.annotationCanvas.height)
      
      // 重新绘制所有标注
      this.currentAnnotations.forEach(annotation => {
        this.canvasContext.strokeStyle = '#ff0000'
        this.canvasContext.lineWidth = 2
        this.canvasContext.strokeRect(
          annotation.x, 
          annotation.y, 
          annotation.width, 
          annotation.height
        )
        
        // 绘制标签
        this.canvasContext.fillStyle = '#ff0000'
        this.canvasContext.font = '12px Arial'
        this.canvasContext.fillText(
          annotation.category, 
          annotation.x, 
          annotation.y - 5
        )
      })
    },
    
    loadImageAnnotations(imageId) {
      axios.get(`/api/annotation/data/image/${imageId}`)
        .then(response => {
          if (response.data.code === 200) {
            this.currentAnnotations = response.data.data || []
            this.$nextTick(() => {
              this.redrawAnnotations()
            })
          }
        })
        .catch(error => {
          console.error('加载标注数据失败:', error)
          this.$message.error('加载标注数据失败')
        })
    },
    
    deleteAnnotation(annotation) {
      const index = this.currentAnnotations.indexOf(annotation)
      if (index > -1) {
        this.currentAnnotations.splice(index, 1)
        this.redrawAnnotations()
      }
    },
    
    clearAnnotations() {
      this.currentAnnotations = []
      this.redrawAnnotations()
    },
    
    saveAnnotations() {
      if (!this.selectedCategory) {
        this.$message.warning('请先选择缺陷类别')
        return
      }
      
      this.savingAnnotations = true
      
      // 检查是否是裂痕或划痕，如果是，需要有标注框
      const categoryName = this.getCategoryName(this.selectedCategory)
      const isDefect = categoryName === '裂痕' || categoryName === '划痕'
      
      if (isDefect && this.currentAnnotations.length === 0) {
        this.$message.warning('请在图片上绘制标注框')
        this.savingAnnotations = false
        return
      }
      
      let annotationDataList = []
      
      if (isDefect && this.currentAnnotations.length > 0) {
        // 如果是裂痕或划痕，使用绘制的标注框数据
        annotationDataList = this.currentAnnotations.map(annotation => ({
          ...annotation,
          categoryId: this.selectedCategory,
          category: categoryName
        }))
      } else {
        // 合格类别，不需要坐标
        annotationDataList = [{
          rawImageId: this.selectedImage.id,
          taskId: parseInt(this.activeTaskId) || null,
          categoryId: this.selectedCategory,
          category: categoryName,
          x: 0,
          y: 0,
          width: 0,
          height: 0,
          confidence: 1.0,
          annotatorId: 1,
          annotatorName: '标注员',
          annotationTime: new Date().toISOString()
        }]
      }
      
      axios.post('/api/annotation/data', annotationDataList)
        .then(response => {
          if (response.data.code === 200) {
            if (isDefect) {
              this.$message.success('保存标注成功，已自动生成带框图片')
            } else {
              this.$message.success('保存标注数据成功')
            }
            // 清空选择
            this.selectedCategory = null
            this.selectedImage = null
            this.selectedImageId = null
            this.currentAnnotations = []
            // 自动刷新图片列表，更新状态
            this.loadImages()
          } else {
            this.$message.error(response.data.msg || '保存标注数据失败')
          }
        })
        .catch(error => {
          console.error('保存标注数据失败:', error)
          this.$message.error('保存标注数据失败')
        })
        .finally(() => {
          this.savingAnnotations = false
        })
    },
    
    // 上传相关方法
    uploadImage() {
      this.uploadDialogVisible = true
    },
    
    handleFileChange(file, fileList) {
      this.uploadFileList = fileList
    },
    
    handleFileRemove(file, fileList) {
      this.uploadFileList = fileList
    },
    
    confirmUpload() {
      if (this.uploadFileList.length === 0) {
        this.$message.warning('请选择要上传的文件')
        return
      }
      
      this.uploading = true
      
      // 使用FormData上传文件
      const promises = this.uploadFileList.map(fileItem => {
        const formData = new FormData()
        formData.append('image', fileItem.raw)
        formData.append('uploadSource', 'manual')
        
        return axios.post('/api/annotation/upload/camera', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
      })
      
      Promise.all(promises)
        .then(responses => {
          const successCount = responses.filter(r => r.data.code === 200).length
          this.uploading = false
          this.uploadDialogVisible = false
          this.uploadFileList = []
          this.$message.success(`成功上传 ${successCount} 张图片`)
          // 自动刷新图片列表
          this.loadImages()
        })
        .catch(error => {
          console.error('上传失败:', error)
          this.uploading = false
          this.$message.error('上传失败，请重试')
        })
    },
    
    // 工具方法
    getImageUrl(imagePath) {
      // 如果是完整的URL，直接返回
      if (imagePath && imagePath.startsWith('http')) {
        return imagePath
      }
      // 提取文件名（假设路径格式为：uploads/images/xxx.jpg 或 uploads\\images\\xxx.jpg）
      if (imagePath) {
        const filename = imagePath.replace(/\\/g, '/').split('/').pop()
        return `/api/annotation/files/${filename}`
      }
      return ''
    },
    
    handleImageError(event) {
      event.target.src = '/default-image.png' // 默认图片
    },
    
    formatFileSize(size) {
      if (size < 1024) {
        return size + ' B'
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(1) + ' KB'
      } else {
        return (size / (1024 * 1024)).toFixed(1) + ' MB'
      }
    },
    
    getTaskStatusType(status) {
      const statusMap = {
        0: '',      // 待开始
        1: 'primary', // 进行中
        2: 'success', // 已完成
        3: 'warning'  // 已暂停
      }
      return statusMap[status] || ''
    },
    
    getTaskStatusText(status) {
      const statusMap = {
        0: '待开始',
        1: '进行中',
        2: '已完成',
        3: '已暂停'
      }
      return statusMap[status] || '未知'
    },
    
    getImageStatusType(status) {
      const statusMap = {
        0: '',      // 待标注
        1: 'primary', // 标注中
        2: 'success', // 已标注
        3: 'info'     // 已检测
      }
      return statusMap[status] || ''
    },
    
    getImageStatusText(status) {
      const statusMap = {
        0: '待标注',
        1: '标注中',
        2: '已标注',
        3: '已检测'
      }
      return statusMap[status] || '未知'
    },
    
    getCategoryName(categoryId) {
      const category = this.defectCategories.find(c => c.id === categoryId)
      return category ? category.name : '未知'
    },
    
    // 加载缺陷类别
    loadDefectCategories() {
      // 使用固定的三个类别：合格、裂痕、划痕
      this.defectCategories = [
        { id: 1, name: '合格' },
        { id: 2, name: '裂痕' },
        { id: 3, name: '划痕' }
      ]
      console.log('加载缺陷类别成功:', this.defectCategories)
    }
  }
}
</script>

<style scoped>
.annotation-page {
  padding: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.main-content {
  flex: 1;
  display: flex;
  gap: 20px;
  overflow: hidden;
}

.task-panel {
  width: 250px;
  flex-shrink: 0;
}

.task-card, .image-card, .annotation-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.task-menu {
  border: none;
}

.image-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  padding: 10px;
  overflow-y: auto;
  flex: 1;
}

.image-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.image-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.2);
}

.image-item.selected {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.4);
}

.image-preview {
  position: relative;
  height: 120px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 5px;
  right: 5px;
}

.image-actions {
  position: absolute;
  top: 5px;
  left: 5px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-item:hover .image-actions {
  opacity: 1;
}

.image-info {
  padding: 8px;
}

.image-name {
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 5px;
}

.image-meta {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  color: #909399;
}

.pagination-container {
  padding: 10px;
  border-top: 1px solid #ebeef5;
}

.annotation-panel {
  width: 400px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.annotation-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.image-container {
  position: relative;
  flex: 1;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.annotation-image {
  max-width: 100%;
  max-height: 100%;
  display: block;
  object-fit: contain;
}

.annotation-canvas {
  position: absolute;
  top: 0;
  left: 0;
  cursor: crosshair;
}

.annotation-tools {
  padding: 15px;
  border-top: 1px solid #ebeef5;
  overflow-y: auto;
  max-height: 300px;
}

.tool-section {
  margin-bottom: 20px;
}

.tool-section h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}

.tools {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.categories {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.category-tag {
  cursor: pointer;
}

.annotations-list {
  margin-top: 10px;
}

.empty-annotation {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-annotation i {
  font-size: 48px;
  margin-bottom: 10px;
}

.upload-demo {
  text-align: center;
}

.dialog-footer {
  text-align: right;
}

.image-header-actions {
  display: flex;
  align-items: center;
}
</style>