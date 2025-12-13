<template>
  <div class="smart-annotation-platform">
    <!-- 主标题区域 -->
    <div class="main-header">
      <div class="header-left">
        <h1>半轴生产质量智能标注平台</h1>
        <p class="header-subtitle">基于区块链的全流程质量追溯与AI智能缺陷分析系统</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" icon="el-icon-upload" @click="uploadProductionData">
          上传生产数据
        </el-button>
        <el-button type="success" icon="el-icon-cpu" @click="loadAIInspectionResults" :disabled="!selectedProduct">
          AI智能检测
        </el-button>
        <el-button type="warning" icon="el-icon-document" @click="generateInspectionReport" :disabled="!selectedProduct">
          生成报告
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card stat-primary">
            <div class="stat-icon">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(totalProducts) }}</div>
              <div class="stat-label">总生产数量</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-warning">
            <div class="stat-icon">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(defectiveProducts) }}</div>
              <div class="stat-label">缺陷产品数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-success">
            <div class="stat-icon">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(inspectedProducts) }}</div>
              <div class="stat-label">已检测数量</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-info">
            <div class="stat-icon">
              <i class="el-icon-cpu"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(aiDetected) }}</div>
              <div class="stat-label">AI识别缺陷</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 主内容区 - 单页面布局 -->
    <div class="main-container">
      <!-- 左侧：产品列表和筛选 -->
      <div class="left-section">
        <div class="section-card">
          <div class="section-header">
            <h3>
              <i class="el-icon-s-order"></i>
              车间半轴
            </h3>
            <div class="filter-actions">
              <el-select 
                v-model="productFilter.status" 
                placeholder="状态筛选" 
                size="small" 
                style="width: 110px;"
                @change="applyFilter"
              >
                <el-option label="全部状态" value="all"></el-option>
                <el-option label="待检测" value="0"></el-option>
                <el-option label="检测中" value="1"></el-option>
                <el-option label="已检测" value="2"></el-option>
                <el-option label="有缺陷" value="3"></el-option>
              </el-select>
              
              <el-select 
                v-model="productFilter.workshop" 
                placeholder="车间筛选" 
                size="small" 
                style="width: 120px;"
                @change="applyFilter"
              >
                <el-option label="全部车间" value="all"></el-option>
                <el-option label="一车间" value="1"></el-option>
                <el-option label="二车间" value="2"></el-option>
                <el-option label="三车间" value="3"></el-option>
                <el-option label="四车间" value="4"></el-option>
              </el-select>
              
              <el-button 
                size="small" 
                icon="el-icon-refresh" 
                @click="refreshProducts"
              ></el-button>
            </div>
          </div>
          
          <div class="product-list-container">
            <div v-if="filteredProducts.length === 0" class="empty-list">
              <i class="el-icon-s-opportunity"></i>
              <p>暂无产品数据</p>
            </div>
            
            <div 
              v-for="product in filteredProducts" 
              :key="product.snCode"
              class="product-item"
              :class="{
                'selected': selectedProductSn === product.snCode,
                'defect': product.defectLevel > 0
              }"
              @click="selectProduct(product)"
            >
              <div class="product-preview">
                <!-- 区块链序列号显示 -->
                <div class="blockchain-badge">
                  <i class="el-icon-link"></i>
                  <span>区块链ID: {{ product.blockchainId }}</span>
                </div>
                
                <!-- 半轴示意图 -->
                <div class="axle-visualization">
                  <div class="axle-shaft" :style="{ width: getAxleWidth(product) }"></div>
                  <div 
                    class="axle-flange" 
                    :style="{ 
                      width: getFlangeWidth(product),
                      height: getFlangeHeight(product)
                    }"
                  ></div>
                  <div class="axle-spline"></div>
                </div>
                
                <div class="product-status">
                  <el-tag 
                    size="mini" 
                    :type="getProductStatusType(product.status)"
                    effect="dark"
                  >
                    {{ getProductStatusText(product.status) }}
                  </el-tag>
                  <el-tag 
                    v-if="product.defectLevel > 0" 
                    size="mini" 
                    type="danger"
                    effect="dark"
                  >
                    {{ getDefectLevelText(product.defectLevel) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="product-details">
                <div class="product-sn">
                  <i class="el-icon-tickets"></i>
                  <span class="sn-code">{{ product.snCode }}</span>
                </div>
                
                <div class="product-specs">
                  <div class="spec-item">
                    <i class="el-icon-s-platform"></i>
                    <span>{{ product.model }}</span>
                  </div>
                  <div class="spec-item">
                    <i class="el-icon-s-help"></i>
                    <span>{{ product.material }}</span>
                  </div>
                </div>
                
                <div class="product-meta">
                  <div class="meta-item">
                    <i class="el-icon-date"></i>
                    <span>{{ formatDate(product.productionDate) }}</span>
                  </div>
                  <div class="meta-item">
                    <i class="el-icon-user"></i>
                    <span>{{ product.operator }}</span>
                  </div>
                </div>
                
                <div v-if="product.defectWorkshop" class="product-defect-info">
                  <el-tag size="mini" type="info">
                    <i class="el-icon-location-outline"></i>
                    问题车间: {{ getWorkshopName(product.defectWorkshop) }}
                  </el-tag>
                  <el-tag size="mini" type="info">
                    <i class="el-icon-s-operation"></i>
                    问题工序: {{ getProcessName(product.defectProcess) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div class="pagination-wrapper">
            <el-pagination
              @size-change="handleProductSizeChange"
              @current-change="handleProductCurrentChange"
              :current-page="productPage"
              :page-sizes="[6, 12, 18, 24]"
              :page-size="productPageSize"
              layout="total, sizes, prev, pager, next"
              :total="productTotal"
              background
            >
            </el-pagination>
          </div>
        </div>
      </div>

      <!-- 右侧：智能标注区域 -->
      <div class="right-section">
        <div class="section-card">
          <div class="section-header">
            <h3>
              <i class="el-icon-edit-outline"></i>
              智能缺陷标注系统
              <span v-if="selectedProduct" class="selected-product-info">
                [当前产品: {{ selectedProduct.snCode }}]
              </span>
            </h3>
          </div>
          
          <!-- AI智能检测结果 -->
          <div v-if="selectedProduct" class="ai-detection-panel">
            <div class="panel-header">
              <h4>
                <i class="el-icon-cpu"></i>
                AI智能检测分析结果
                <el-tag v-if="aiDetectionResults.length > 0" size="small" type="warning">
                  检测到一个潜在问题
                </el-tag>
              </h4>
              <el-button 
                size="small" 
                type="primary" 
                plain 
                @click="loadAIInspectionForProduct(selectedProduct.snCode)"
                :loading="loadingAI"
              >
                重新分析
              </el-button>
            </div>
            
            <div v-if="aiDetectionResults.length > 0" class="detection-results">
              <el-collapse v-model="activeCollapse" accordion>
                <el-collapse-item 
                  v-for="(result, index) in aiDetectionResults" 
                  :key="result.id"
                  :name="index"
                >
                  <template slot="title">
                    <div class="defect-title">
                      <el-tag 
                        :type="getDefectTagType(result.defectType)" 
                        size="small"
                        :style="getDefectTagStyle(result.defectType)"
                      >
                        {{ getDefectTypeText(result.defectType) }}
                      </el-tag>
                      <span class="defect-workshop">
                        {{ getWorkshopName(result.workshop) }} - {{ getProcessName(result.process) }}
                      </span>
                      <el-progress 
                        :percentage="Math.round(result.confidence * 100)" 
                        :status="getConfidenceStatus(result.confidence)"
                        :show-text="false"
                        style="width: 80px; margin: 0 10px;"
                      />
                      <span class="confidence-text">{{ (result.confidence * 100).toFixed(1) }}%</span>
                    </div>
                  </template>
                  
                  <div class="defect-details">
                    <div class="detail-row">
                      <label>问题描述:</label>
                      <p>{{ result.description }}</p>
                    </div>
                    <div class="detail-row">
                      <label>检测参数:</label>
                      <code>{{ result.parameters }}</code>
                    </div>
                    <div class="detail-row">
                      <label>AI建议措施:</label>
                      <p class="suggestion">{{ result.suggestedAction }}</p>
                    </div>
                    <div class="action-buttons">
                      <el-button 
                        size="mini" 
                        type="success" 
                        @click="confirmAIDefect(result, index)"
                      >
                        确认缺陷
                      </el-button>
                      <el-button 
                        size="mini" 
                        @click="markAsFalsePositive(index)"
                      >
                        标记为误报
                      </el-button>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
            
            <div v-else class="no-detection">
              <div class="empty-state">
                <i class="el-icon-check" style="color: #67C23A; font-size: 48px;"></i>
                <p>AI检测未发现明显缺陷</p>
                <p class="subtext">如需手动标注，请在下方案例中选择</p>
              </div>
            </div>
          </div>
          
          <!-- 手动标注区域 -->
          <div v-if="selectedProduct" class="manual-annotation-panel">
            <div class="panel-header">
              <h4>
                <i class="el-icon-edit"></i>
                手动缺陷标注
              </h4>
              <div class="annotation-stats">
                <span>已标注: {{ currentAnnotations.length }} 个缺陷</span>
              </div>
            </div>
            
            <!-- 标注流程向导 -->
            <div class="annotation-wizard">
              <el-steps :active="annotationStep" align-center finish-status="success" style="margin-bottom: 20px;">
                <el-step title="选择车间" icon="el-icon-office-building"></el-step>
                <el-step title="选择工序" icon="el-icon-s-operation"></el-step>
                <el-step title="选择缺陷" icon="el-icon-warning"></el-step>
                <el-step title="填写参数" icon="el-icon-edit"></el-step>
              </el-steps>
              
              <!-- 步骤1: 选择车间 -->
              <div v-show="annotationStep === 0" class="wizard-step">
                <div class="step-content">
                  <h5>请选择问题所在车间:</h5>
                  <div class="workshop-cards">
                    <div 
                      v-for="workshop in workshops" 
                      :key="workshop.id"
                      class="workshop-card"
                      :class="{ 'selected': selectedWorkshop === workshop.id }"
                      @click="selectWorkshop(workshop.id)"
                    >
                      <div class="workshop-icon">
                        <i :class="workshop.icon"></i>
                      </div>
                      <div class="workshop-info">
                        <div class="workshop-name">{{ workshop.name }}</div>
                        <div class="workshop-desc">{{ workshop.description }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 步骤2: 选择工序 -->
              <div v-show="annotationStep === 1" class="wizard-step">
                <div class="step-content">
                  <h5>请选择具体工序:</h5>
                  <div class="process-grid">
                    <div 
                      v-for="process in getProcessesByWorkshop(selectedWorkshop)" 
                      :key="process.id"
                      class="process-card"
                      :class="{ 'selected': selectedProcess === process.id }"
                      @click="selectProcess(process.id)"
                    >
                      <div class="process-icon">
                        <i :class="process.icon"></i>
                      </div>
                      <div class="process-info">
                        <div class="process-name">{{ process.name }}</div>
                        <div class="process-desc">{{ process.description }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 步骤3: 选择缺陷类型 -->
              <div v-show="annotationStep === 2" class="wizard-step">
                <div class="step-content">
                  <h5>请选择缺陷类型:</h5>
                  <div class="defect-grid">
                    <div 
                      v-for="defect in getDefectsByProcess(selectedProcess)" 
                      :key="defect.id"
                      class="defect-card"
                      :class="{ 'selected': selectedDefectType === defect.id }"
                      @click="selectDefectType(defect.id)"
                    >
                      <div class="defect-icon" :style="{ backgroundColor: getDefectColor(defect.type) }">
                        <i :class="defect.icon"></i>
                      </div>
                      <div class="defect-info">
                        <div class="defect-name">{{ defect.name }}</div>
                        <div class="defect-desc">{{ defect.description }}</div>
                        <div class="defect-standard">
                          <small>标准: {{ defect.standard }}</small>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 步骤4: 填写参数 -->
              <div v-show="annotationStep === 3" class="wizard-step">
                <div class="step-content">
                  <h5>请填写缺陷参数:</h5>
                  <el-form :model="defectParams" label-width="120px" size="small" class="defect-form">
                    <el-form-item label="缺陷位置:">
                      <el-select 
                        v-model="defectParams.location" 
                        placeholder="选择缺陷位置"
                        style="width: 300px;"
                      >
                        <el-option label="杆部" value="杆部"></el-option>
                        <el-option label="端面" value="端面"></el-option>
                        <el-option label="齿面" value="齿面"></el-option>
                        <el-option label="花键部" value="花键部"></el-option>
                        <el-option label="法兰盘" value="法兰盘"></el-option>
                        <el-option label="全表面" value="全表面"></el-option>
                        <el-option label="内部" value="内部"></el-option>
                        <el-option label="其他位置" value="其他"></el-option>
                      </el-select>
                    </el-form-item>
                    
                    <el-form-item label="缺陷尺寸:">
                      <el-input 
                        v-model="defectParams.size" 
                        placeholder="如: 长度2mm、深度0.5mm、裂纹3mm等"
                        style="width: 300px;"
                      ></el-input>
                    </el-form-item>
                    
                    <el-form-item label="严重等级:">
                      <el-rate
                        v-model="defectParams.severity"
                        :max="5"
                        show-text
                        :texts="['轻微', '一般', '中等', '严重', '致命']"
                      >
                      </el-rate>
                    </el-form-item>
                    
                    <el-form-item label="详细描述:">
                      <el-input
                        type="textarea"
                        v-model="defectParams.description"
                        :rows="3"
                        placeholder="补充说明缺陷的具体情况、可能原因等"
                        style="width: 300px;"
                      ></el-input>
                    </el-form-item>
                    
                    <el-form-item label="改进建议:">
                      <el-input
                        type="textarea"
                        v-model="defectParams.suggestion"
                        :rows="2"
                        placeholder="提供改进建议或措施"
                        style="width: 300px;"
                      ></el-input>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
              
              <!-- 步骤导航 -->
              <div class="wizard-navigation">
                <el-button 
                  size="small" 
                  :disabled="annotationStep === 0"
                  @click="prevStep"
                >
                  上一步
                </el-button>
                
                <el-button 
                  v-if="annotationStep < 3"
                  size="small" 
                  type="primary"
                  :disabled="!canProceedToNextStep"
                  @click="nextStep"
                >
                  下一步
                </el-button>
                
                <el-button 
                  v-if="annotationStep === 3"
                  size="small" 
                  type="success"
                  :disabled="!canAddAnnotation"
                  @click="addManualAnnotation"
                >
                  添加标注
                </el-button>
                
                <el-button 
                  size="small" 
                  @click="resetAnnotation"
                >
                  重置
                </el-button>
              </div>
            </div>
            
            <!-- 当前标注列表 -->
            <div v-if="currentAnnotations.length > 0" class="annotations-list">
              <div class="list-header">
                <h5>已添加的缺陷标注</h5>
                <el-button 
                  size="small" 
                  type="danger" 
                  plain 
                  @click="clearAnnotations"
                >
                  清空所有
                </el-button>
              </div>
              
              <div class="annotations-container">
                <div 
                  v-for="(annotation, index) in currentAnnotations" 
                  :key="index"
                  class="annotation-item"
                >
                  <div class="annotation-header">
                    <div class="annotation-title">
                      <span class="workshop">{{ annotation.workshopName }}</span>
                      <span class="separator">/</span>
                      <span class="process">{{ annotation.processName }}</span>
                    </div>
                    <div class="annotation-actions">
                      <el-tag 
                        size="mini" 
                        :type="getDefectTagType(annotation.defectType)"
                        effect="dark"
                      >
                        {{ annotation.defectName }}
                      </el-tag>
                      <el-button 
                        type="text" 
                        size="mini" 
                        icon="el-icon-delete"
                        @click="removeAnnotation(index)"
                      ></el-button>
                    </div>
                  </div>
                  
                  <div class="annotation-details">
                    <div v-if="annotation.location" class="detail-item">
                      <i class="el-icon-location-outline"></i>
                      <span>位置: {{ annotation.location }}</span>
                    </div>
                    <div v-if="annotation.size" class="detail-item">
                      <i class="el-icon-rank"></i>
                      <span>尺寸: {{ annotation.size }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="el-icon-s-flag"></i>
                      <span>等级: </span>
                      <el-rate
                        :value="annotation.severity"
                        disabled
                        :max="5"
                        style="display: inline-block; margin-left: 5px;"
                      >
                      </el-rate>
                    </div>
                    <div v-if="annotation.description" class="detail-item full-width">
                      <i class="el-icon-document"></i>
                      <span>描述: {{ annotation.description }}</span>
                    </div>
                    <div v-if="annotation.suggestion" class="detail-item full-width suggestion">
                      <i class="el-icon-lightbulb"></i>
                      <span>建议: {{ annotation.suggestion }}</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="annotation-actions">
                <el-button 
                  type="primary" 
                  size="medium" 
                  @click="saveSmartAnnotations"
                  :loading="savingAnnotations"
                  style="width: 100%;"
                >
                  <i class="el-icon-check"></i>
                  保存所有标注到区块链
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 未选择产品提示 -->
          <div v-else class="no-selection">
            <div class="empty-panel">
              <i class="el-icon-s-opportunity" style="font-size: 64px; color: #909399;"></i>
              <h3>请选择半轴产品</h3>
              <p>从左侧产品列表中选择一个半轴产品开始智能标注</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传对话框 -->
    <el-dialog 
      title="上传生产数据" 
      :visible.sync="uploadDialogVisible" 
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="upload-content">
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="uploadFileList"
          accept=".csv,.xlsx,.xls"
          multiple
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将生产数据文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">
            支持CSV、Excel格式，系统将自动生成区块链ID
          </div>
        </el-upload>
        
        <div class="upload-info">
          <el-alert
            title="数据将上传到区块链网络"
            type="info"
            description="每个产品将获得唯一的区块链哈希值，确保数据不可篡改"
            :closable="false"
            show-icon
            style="margin-top: 20px;"
          >
          </el-alert>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button 
          type="primary" 
          @click="confirmUpload" 
          :loading="uploading"
          :disabled="uploadFileList.length === 0"
        >
          {{ uploading ? '上传中...' : '开始上传' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'SmartAnnotationPlatform',
  data() {
    return {
      // 产品数据
      productList: [],
      selectedProduct: null,
      selectedProductSn: null,
      productFilter: {
        status: 'all',
        workshop: 'all'
      },
      productPage: 1,
      productPageSize: 6,
      productTotal: 0,
      
      // 统计数据 - 实时数据
      totalProducts: 2356,
      defectiveProducts: 156,
      inspectedProducts: 1872,
      aiDetected: 89,
      
      // AI检测结果
      aiDetectionResults: [],
      activeCollapse: 0,
      loadingAI: false,
      
      // 标注数据
      currentAnnotations: [],
      annotationStep: 0,
      selectedWorkshop: null,
      selectedProcess: null,
      selectedDefectType: null,
      defectParams: {
        location: '',
        size: '',
        severity: 3,
        description: '',
        suggestion: ''
      },
      
      // 上传相关
      uploadDialogVisible: false,
      uploadFileList: [],
      uploading: false,
      savingAnnotations: false,
      
      // 实时数据更新定时器
      statsTimer: null,
      
      // 车间、工序、缺陷数据
      workshops: [
        { id: 1, name: '一车间', code: 'workshop1', description: '毛坯制备', icon: 'el-icon-hot-water' },
        { id: 2, name: '二车间', code: 'workshop2', description: '粗/精加工', icon: 'el-icon-cpu' },
        { id: 3, name: '三车间', code: 'workshop3', description: '热处理+探伤', icon: 'el-icon-sunny' },
        { id: 4, name: '四车间', code: 'workshop4', description: '终检+包装', icon: 'el-icon-box' }
      ],
      
      processes: [
        // 一车间工序
        { id: 101, workshopId: 1, name: '切割下料', code: 'cutting', 
          description: '原材料切割下料', icon: 'el-icon-scissors', type: 'forming' },
        { id: 102, workshopId: 1, name: '压花键', code: 'spline_pressing', 
          description: '花键压装成型', icon: 'el-icon-s-grid', type: 'forming' },
        { id: 103, workshopId: 1, name: '锻造', code: 'forging', 
          description: '锻造成型', icon: 'el-icon-hot-water', type: 'forming' },
        
        // 二车间工序
        { id: 201, workshopId: 2, name: '粗抛丸/粗校', code: 'rough_shot_peening', 
          description: '粗抛丸与粗校直', icon: 'el-icon-brush', type: 'machining' },
        { id: 202, workshopId: 2, name: '调质热处理', code: 'quenching_tempering', 
          description: '调质处理', icon: 'el-icon-fire', type: 'heat' },
        { id: 203, workshopId: 2, name: '粗车/精车', code: 'turning', 
          description: '车削加工', icon: 'el-icon-refresh', type: 'machining' },
        { id: 204, workshopId: 2, name: '加工齿', code: 'gear_machining', 
          description: '齿形加工', icon: 'el-icon-s-operation', type: 'machining' },
        
        // 三车间工序
        { id: 301, workshopId: 3, name: '精车盘/钻孔', code: 'precision_turning', 
          description: '精密车削与钻孔', icon: 'el-icon-sunny', type: 'machining' },
        { id: 302, workshopId: 3, name: '表面淬火', code: 'surface_quenching', 
          description: '表面淬火处理', icon: 'el-icon-sunny', type: 'heat' },
        { id: 303, workshopId: 3, name: '磁粉探伤', code: 'magnetic_particle_testing', 
          description: '磁粉探伤检测', icon: 'el-icon-magnet', type: 'inspection' },
        { id: 304, workshopId: 3, name: '精校/回火', code: 'precision_straightening', 
          description: '精校与回火', icon: 'el-icon-s-fold', type: 'finishing' },
        
        // 四车间工序
        { id: 401, workshopId: 4, name: '超声波探伤', code: 'ultrasonic_testing', 
          description: '超声波探伤检测', icon: 'el-icon-microphone', type: 'inspection' },
        { id: 402, workshopId: 4, name: '喷漆', code: 'painting', 
          description: '表面喷漆处理', icon: 'el-icon-brush', type: 'finishing' },
        { id: 403, workshopId: 4, name: '包装', code: 'packaging', 
          description: '成品包装', icon: 'el-icon-box', type: 'finishing' },
        { id: 404, workshopId: 4, name: '终检', code: 'final_inspection', 
          description: '最终质量检测', icon: 'el-icon-check', type: 'inspection' }
      ],
      
      defectTypes: [
        // 切割下料缺陷
        { id: 10101, processId: 101, name: '尺寸超差', code: 'dimension_out_of_tolerance',
          description: '下料尺寸超出公差范围', icon: 'el-icon-rank', 
          standard: '公差±0.5mm', type: 'dimension' },
        { id: 10102, processId: 101, name: '切割毛刺', code: 'cutting_burr',
          description: '切割面存在毛刺', icon: 'el-icon-warning', 
          standard: '毛刺高度≤0.2mm', type: 'surface' },
        
        // 压花键缺陷
        { id: 10201, processId: 102, name: '花键尺寸标准', code: 'spline_dimension_deviation',
          description: '花键齿宽、齿高尺寸超差', icon: 'el-icon-s-grid', 
          standard: '齿宽20±0.02mm，齿高5±0.01mm', type: 'dimension' },
        { id: 10202, processId: 102, name: '花键变形', code: 'spline_deformation',
          description: '花键齿形变形', icon: 'el-icon-s-data', 
          standard: '齿形误差≤0.01mm', type: 'shape' },
        
        // 锻造缺陷
        { id: 10301, processId: 103, name: '锻造裂纹', code: 'forging_crack',
          description: '锻造过程中产生的裂纹', icon: 'el-icon-s-release', 
          standard: '无裂纹', type: 'crack' },
        { id: 10302, processId: 103, name: '锻造折叠', code: 'folding_defect',
          description: '金属折叠形成的缺陷', icon: 'el-icon-s-fold', 
          standard: '无折叠', type: 'surface' },
        
        // 粗抛丸/粗校缺陷
        { id: 20101, processId: 201, name: '精抛丸压力正常', code: 'high_surface_roughness',
          description: '抛丸后表面粗糙度未达标', icon: 'el-icon-s-platform', 
          standard: 'Ra≤1.6μm', type: 'surface' },
        { id: 20102, processId: 201, name: '直线度偏差', code: 'straightness_deviation',
          description: '校直后直线度超差', icon: 'el-icon-rank', 
          standard: '直线度≤0.05mm/m', type: 'dimension' },
        
        // 热处理缺陷
        { id: 20201, processId: 202, name: '硬度不足', code: 'insufficient_hardness',
          description: '调质处理后硬度未达标', icon: 'el-icon-s-flag', 
          standard: '硬度HRC55-60', type: 'property' },
        { id: 30201, processId: 302, name: '淬火裂纹', code: 'quenching_crack',
          description: '淬火过程中产生的裂纹', icon: 'el-icon-s-release', 
          standard: '无裂纹', type: 'crack' },
        
        // 车削缺陷
        { id: 20301, processId: 203, name: '尺寸超差', code: 'turning_dimension_out',
          description: '车削尺寸超出公差', icon: 'el-icon-rank', 
          standard: '公差±0.02mm', type: 'dimension' },
        
        // 精车盘/钻孔缺陷
        { id: 30101, processId: 301, name: '精车盘切削偏差', code: 'drilling_eccentric',
          description: '钻孔位置偏心或角度偏差', icon: 'el-icon-s-release', 
          standard: '偏心≤0.01mm', type: 'position' },
        { id: 30102, processId: 301, name: '表面光洁度不足', code: 'surface_finish_insufficient',
          description: '精车后表面光洁度未达标', icon: 'el-icon-s-platform', 
          standard: 'Ra≤0.8μm', type: 'surface' },
        
        // 探伤缺陷
        { id: 30301, processId: 303, name: '表面裂纹', code: 'surface_crack',
          description: '磁粉探伤发现的表面裂纹', icon: 'el-icon-s-release', 
          standard: '无裂纹', type: 'crack' },
        { id: 40101, processId: 401, name: '超声波探伤正常', code: 'internal_defect',
          description: '超声波探伤发现的内部缺陷', icon: 'el-icon-s-data', 
          standard: '缺陷深度≤2mm', type: 'internal' },
        
        // 表面处理缺陷
        { id: 40201, processId: 402, name: '漆膜厚度不足', code: 'insufficient_paint_thickness',
          description: '漆膜厚度未达到要求', icon: 'el-icon-s-management', 
          standard: '厚度80±5μm', type: 'coating' }
      ]
    }
  },
  computed: {
    filteredProducts() {
      let filtered = this.productList
      
      if (this.productFilter.status !== 'all') {
        filtered = filtered.filter(p => p.status.toString() === this.productFilter.status)
      }
      
      if (this.productFilter.workshop !== 'all') {
        filtered = filtered.filter(p => p.defectWorkshop === parseInt(this.productFilter.workshop))
      }
      
      return filtered.slice(
        (this.productPage - 1) * this.productPageSize,
        this.productPage * this.productPageSize
      )
    },
    
    canProceedToNextStep() {
      switch (this.annotationStep) {
        case 0: return this.selectedWorkshop !== null
        case 1: return this.selectedProcess !== null
        case 2: return this.selectedDefectType !== null
        default: return false
      }
    },
    
    canAddAnnotation() {
      return this.annotationStep === 3 && 
             this.selectedWorkshop && 
             this.selectedProcess && 
             this.selectedDefectType
    }
  },
  mounted() {
    this.loadProducts()
    this.startRealTimeStatsUpdate()
  },
  beforeDestroy() {
    // 清除定时器
    if (this.statsTimer) {
      clearInterval(this.statsTimer)
    }
  },
  methods: {
    // 加载产品数据
    loadProducts() {
      // 模拟区块链序列号产品数据
      this.productList = [
        {
          snCode: 'AX-20251205-001',
          model: 'Φ80×300',
          batchNumber: '20251205-01',
          rawMaterialBatch: 'MT-20251128-001',
          material: '45#钢',
          productionDate: '2025-12-05 08:30:00',
          operator: '张三',
          status: 3,
          defectLevel: 2,
          defectWorkshop: 1,
          defectProcess: 102,
          blockchainId: '0x8a7f93b2c4d5e6f7',
          blockchainHash: '0x9b8a7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0'
        },
        {
          snCode: 'AX-20251205-002',
          model: 'Φ75×280',
          batchNumber: '20251205-01',
          rawMaterialBatch: 'MT-20251128-001',
          material: '40Cr',
          productionDate: '2025-12-05 09:15:00',
          operator: '李四',
          status: 2,
          defectLevel: 0,
          defectWorkshop: null,
          defectProcess: null,
          blockchainId: '0x7b6c5d4e3f2a1b0c',
          blockchainHash: '0x8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0'
        },
        {
          snCode: 'AX-20251205-003',
          model: 'Φ85×320',
          batchNumber: '20251205-02',
          rawMaterialBatch: 'MT-20251129-002',
          material: '42CrMo',
          productionDate: '2025-12-05 10:45:00',
          operator: '王五',
          status: 1,
          defectLevel: 0,
          defectWorkshop: null,
          defectProcess: null,
          blockchainId: '0x6c5d4e3f2a1b0c9d',
          blockchainHash: '0x7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d'
        },
        {
          snCode: 'AX-20251204-001',
          model: 'Φ80×300',
          batchNumber: '20251204-01',
          rawMaterialBatch: 'MT-20251127-001',
          material: '45#钢',
          productionDate: '2025-12-04 14:20:00',
          operator: '张三',
          status: 3,
          defectLevel: 3,
          defectWorkshop: 3,
          defectProcess: 301,
          blockchainId: '0x5d4e3f2a1b0c9d8e',
          blockchainHash: '0x6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f'
        },
        {
          snCode: 'AX-20251204-002',
          model: 'Φ70×260',
          batchNumber: '20251204-02',
          rawMaterialBatch: 'MT-20251127-002',
          material: '40Cr',
          productionDate: '2025-12-04 15:30:00',
          operator: '李四',
          status: 2,
          defectLevel: 0,
          defectWorkshop: null,
          defectProcess: null,
          blockchainId: '0x4e3f2a1b0c9d8e7f',
          blockchainHash: '0x5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a'
        },
        {
          snCode: 'AX-20251130-001',
          model: 'Φ90×350',
          batchNumber: '20251130-01',
          rawMaterialBatch: 'MT-20251126-001',
          material: '42CrMo',
          productionDate: '2025-11-30 11:00:00',
          operator: '王五',
          status: 3,
          defectLevel: 1,
          defectWorkshop: 4,
          defectProcess: 401,
          blockchainId: '0x3f2a1b0c9d8e7f6a',
          blockchainHash: '0x4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b'
        }
      ]
      
      this.productTotal = this.productList.length
      this.updateStatsFromProducts()
    },
    
    // 开始实时统计数据更新
    startRealTimeStatsUpdate() {
      // 每5秒更新一次统计数据，模拟实时数据
      this.statsTimer = setInterval(() => {
        this.updateRealTimeStats()
      }, 5000)
      
      // 立即更新一次
      this.updateRealTimeStats()
    },
    
    // 更新实时统计数据
    updateRealTimeStats() {
      // 模拟生产线上不断有新产品产生
      const now = new Date()
      const hour = now.getHours()
      const minute = now.getMinutes()
      
      // 根据时间动态调整增长率
      let productionRate = 1 // 默认增长率
      if (hour >= 8 && hour < 12) {
        // 上午生产高峰期
        productionRate = 3
      } else if (hour >= 14 && hour < 18) {
        // 下午生产高峰期
        productionRate = 2.5
      } else if (hour >= 20 && hour < 23) {
        // 晚班生产
        productionRate = 2
      }
      
      // 模拟数据增长
      const randomFactor = Math.random() * 0.2 + 0.9 // 0.9-1.1的随机因子
      
      // 更新总生产数量（模拟生产线持续生产）
      const newProducts = Math.floor(productionRate * randomFactor)
      this.totalProducts += newProducts
      
      // 更新缺陷产品数（有一定概率产生缺陷）
      const defectProbability = 0.05 // 5%的缺陷率
      const newDefects = Math.random() < defectProbability ? Math.floor(Math.random() * 3) + 1 : 0
      this.defectiveProducts += newDefects
      
      // 更新已检测数量（模拟质检进度）
      const inspectionRate = Math.floor(productionRate * 0.8 * randomFactor)
      this.inspectedProducts += inspectionRate
      
      // 确保已检测数量不超过总生产数量
      if (this.inspectedProducts > this.totalProducts) {
        this.inspectedProducts = this.totalProducts
      }
      
      // 更新AI识别缺陷数
      const aiDetectionRate = Math.floor(defectProbability * productionRate * 0.7 * randomFactor)
      this.aiDetected += aiDetectionRate
      
      // 从产品列表数据也更新统计
      this.updateStatsFromProducts()
      
      // 偶尔添加新产品到列表（模拟实时数据流）
      if (Math.random() < 0.3) { // 30%概率添加新产品
        this.addNewProductToStream()
      }
    },
    
    // 添加新产品到数据流
    addNewProductToStream() {
      const newProduct = {
        snCode: `AX-${new Date().getFullYear()}${(new Date().getMonth() + 1).toString().padStart(2, '0')}${new Date().getDate().toString().padStart(2, '0')}-${this.productList.length + 100}`,
        model: ['Φ80×300', 'Φ75×280', 'Φ85×320', 'Φ70×260'][Math.floor(Math.random() * 4)],
        batchNumber: `${new Date().getFullYear()}${(new Date().getMonth() + 1).toString().padStart(2, '0')}${new Date().getDate().toString().padStart(2, '0')}-${Math.floor(Math.random() * 3) + 1}`,
        rawMaterialBatch: `MT-${new Date().getFullYear()}${(new Date().getMonth() + 1).toString().padStart(2, '0')}${(new Date().getDate() - Math.floor(Math.random() * 3)).toString().padStart(2, '0')}`,
        material: ['45#钢', '40Cr', '42CrMo'][Math.floor(Math.random() * 3)],
        productionDate: new Date(Date.now() - Math.random() * 86400000).toISOString(),
        operator: ['张三', '李四', '王五', '赵六'][Math.floor(Math.random() * 4)],
        status: Math.floor(Math.random() * 4),
        defectLevel: Math.random() < 0.2 ? Math.floor(Math.random() * 3) + 1 : 0,
        defectWorkshop: Math.random() < 0.2 ? Math.floor(Math.random() * 4) + 1 : null,
        defectProcess: Math.random() < 0.2 ? [101, 102, 201, 301, 401][Math.floor(Math.random() * 5)] : null,
        blockchainId: this.generateBlockchainId(),
        blockchainHash: this.generateBlockchainHash()
      }
      
      // 添加到产品列表
      this.productList.unshift(newProduct)
      this.productTotal = this.productList.length
      
      // 更新当前页的显示
      if (this.productPage === 1) {
        // 如果当前在第一页，确保显示最新的产品
        this.$nextTick(() => {
          // 触发重新渲染
        })
      }
    },
    
    // 从产品列表更新统计
    updateStatsFromProducts() {
      // 计算当前产品列表的统计
      const totalFromList = this.productList.length
      const defectiveFromList = this.productList.filter(p => p.defectLevel > 0).length
      const inspectedFromList = this.productList.filter(p => p.status >= 2).length
      
      // 合并实时数据和产品列表数据
      this.totalProducts = Math.max(this.totalProducts, totalFromList + 2300) // 确保有基础数量
      this.defectiveProducts = Math.max(this.defectiveProducts, defectiveFromList + 120)
      this.inspectedProducts = Math.max(this.inspectedProducts, inspectedFromList + 1800)
      this.aiDetected = Math.max(this.aiDetected, this.aiDetectionResults.length + 80)
    },
    
    // 选择产品
    selectProduct(product) {
      this.selectedProduct = product
      this.selectedProductSn = product.snCode
      this.currentAnnotations = []
      this.resetAnnotation()
      
      // 加载该产品的AI检测结果
      this.loadAIInspectionForProduct(product.snCode)
    },
    
    // 加载AI检测结果
    loadAIInspectionForProduct(snCode) {
      this.loadingAI = true
      
      // 模拟AI检测结果 - 按照要求修改数据
      setTimeout(() => {
        this.aiDetectionResults = [
          {
            id: 1,
            defectType: 'spline_dimension_deviation',
            workshop: 1,
            process: 102,
            confidence: 0.95,
            title: '压花键尺寸偏差',
            description: 'AI检测发现压花键齿宽尺寸超差，可能与压装压力不足或模具磨损有关',
            parameters: '齿宽19.8mm（标准20±0.02mm），检测位置：花键中部',
            suggestedAction: '检查压装压力是否达标（≥250kN），检查模具磨损情况，调整工艺参数'
          },
          {
            id: 2,
            defectType: 'high_surface_roughness',
            workshop: 2,
            process: 201,
            confidence: 0.94,
            title: '粗抛丸后表面粗糙度大',
            description: 'AI检测发现粗抛丸后表面粗糙度未达标，可能影响后续精加工质量',
            parameters: '表面粗糙度Ra2.1μm（标准Ra≤1.6μm），检测位置：杆部表面',
            suggestedAction: '调整抛丸时间和压力，检查钢丸粒度是否合适，优化工艺参数'
          },
          {
            id: 3,
            defectType: 'drilling_eccentric',
            workshop: 3,
            process: 301,
            confidence: 0.77,
            title: '精车盘钻孔偏心',
            description: 'AI检测发现钻孔位置存在偏心，可能与夹具定位不准确或设备精度有关',
            parameters: '钻孔偏心0.015mm（标准≤0.01mm），孔深35mm，孔径12mm',
            suggestedAction: '检查夹具定位精度，校准钻孔设备，重新标定加工基准'
          },
          {
            id: 4,
            defectType: 'internal_defect',
            workshop: 4,
            process: 401,
            confidence: 0.93,
            title: '超声波探伤内部缺陷',
            description: '超声波探伤发现内部存在微小缺陷，可能与原材料质量或锻造工艺有关',
            parameters: '缺陷深度2.3mm（标准≤2mm），缺陷长度5mm，位置：法兰盘根部',
            suggestedAction: '检查原材料质量报告，优化锻造工艺参数，加强过程监控'
          }
        ]
        
        this.activeCollapse = 0
        this.loadingAI = false
        this.updateStatsFromProducts()
      }, 800)
    },
    
    loadAIInspectionResults() {
      if (this.selectedProduct) {
        this.loadAIInspectionForProduct(this.selectedProduct.snCode)
        this.$message.success('AI检测分析已完成')
      }
    },
    
    // 确认AI检测的缺陷
    confirmAIDefect(defect, index) {
      const workshop = this.workshops.find(w => w.id === defect.workshop)
      const process = this.processes.find(p => p.id === defect.process)
      const defectType = this.defectTypes.find(d => d.code === defect.defectType)
      
      const annotation = {
        snCode: this.selectedProduct.snCode,
        workshop: defect.workshop,
        workshopName: workshop ? workshop.name : '未知车间',
        process: defect.process,
        processName: process ? process.name : '未知工序',
        defectType: defect.defectType,
        defectName: defectType ? defectType.name : '未知缺陷',
        location: 'AI自动识别',
        size: defect.parameters?.match(/\d+\.?\d*(mm|μm)/)?.[0] || '',
        severity: this.calculateSeverity(defect.confidence),
        description: defect.description,
        suggestion: defect.suggestedAction,
        source: 'ai',
        confidence: defect.confidence,
        annotationTime: new Date().toISOString(),
        blockchainHash: this.generateBlockchainHash()
      }
      
      this.currentAnnotations.push(annotation)
      
      // 从AI检测结果中移除已确认的缺陷
      this.aiDetectionResults.splice(index, 1)
      
      // 更新AI识别缺陷统计
      this.aiDetected = Math.max(0, this.aiDetected - 1)
      
      this.$message.success('缺陷已确认并添加到标注列表')
      this.updateStatsFromProducts()
    },
    
    markAsFalsePositive(index) {
      this.aiDetectionResults.splice(index, 1)
      this.$message.info('已标记为误报')
      this.updateStatsFromProducts()
    },
    
    // 标注步骤导航
    nextStep() {
      if (this.annotationStep < 3) {
        this.annotationStep++
      }
    },
    
    prevStep() {
      if (this.annotationStep > 0) {
        this.annotationStep--
      }
    },
    
    resetAnnotation() {
      this.annotationStep = 0
      this.selectedWorkshop = null
      this.selectedProcess = null
      this.selectedDefectType = null
      this.defectParams = {
        location: '',
        size: '',
        severity: 3,
        description: '',
        suggestion: ''
      }
    },
    
    // 选择车间、工序、缺陷类型
    selectWorkshop(workshopId) {
      this.selectedWorkshop = workshopId
      this.selectedProcess = null
      this.selectedDefectType = null
      this.annotationStep = 1
    },
    
    selectProcess(processId) {
      this.selectedProcess = processId
      this.selectedDefectType = null
      this.annotationStep = 2
    },
    
    selectDefectType(defectId) {
      this.selectedDefectType = defectId
      this.annotationStep = 3
    },
    
    // 获取工序和缺陷列表
    getProcessesByWorkshop(workshopId) {
      return this.processes.filter(p => p.workshopId === workshopId)
    },
    
    getDefectsByProcess(processId) {
      return this.defectTypes.filter(d => d.processId === processId)
    },
    
    // 添加手动标注
    addManualAnnotation() {
      const workshop = this.workshops.find(w => w.id === this.selectedWorkshop)
      const process = this.processes.find(p => p.id === this.selectedProcess)
      const defectType = this.defectTypes.find(d => d.id === this.selectedDefectType)
      
      const annotation = {
        snCode: this.selectedProduct.snCode,
        workshop: this.selectedWorkshop,
        workshopName: workshop ? workshop.name : '未知车间',
        process: this.selectedProcess,
        processName: process ? process.name : '未知工序',
        defectType: defectType ? defectType.code : 'unknown',
        defectName: defectType ? defectType.name : '未知缺陷',
        location: this.defectParams.location,
        size: this.defectParams.size,
        severity: this.defectParams.severity,
        description: this.defectParams.description,
        suggestion: this.defectParams.suggestion,
        source: 'manual',
        annotationTime: new Date().toISOString(),
        blockchainHash: this.generateBlockchainHash()
      }
      
      this.currentAnnotations.push(annotation)
      this.resetAnnotation()
      this.$message.success('缺陷标注已添加')
    },
    
    // 移除标注
    removeAnnotation(index) {
      this.currentAnnotations.splice(index, 1)
      this.$message.success('标注已移除')
    },
    
    clearAnnotations() {
      this.currentAnnotations = []
      this.$message.success('所有标注已清除')
    },
    
    // 保存标注
    saveSmartAnnotations() {
      if (this.currentAnnotations.length === 0) {
        this.$message.warning('请先添加标注')
        return
      }
      
      this.savingAnnotations = true
      
      // 构建保存数据
      const saveData = {
        snCode: this.selectedProduct.snCode,
        annotations: this.currentAnnotations,
        inspector: '质量检测员',
        inspectionTime: new Date().toISOString(),
        blockchainId: this.selectedProduct.blockchainId
      }
      
      // 模拟保存到区块链
      setTimeout(() => {
        // 更新产品状态
        this.selectedProduct.status = 2
        this.selectedProduct.defectLevel = this.calculateOverallDefectLevel()
        
        // 如果产品有缺陷，更新缺陷统计
        if (this.selectedProduct.defectLevel > 0) {
          this.defectiveProducts += 1
        }
        
        // 更新已检测统计
        this.inspectedProducts += 1
        
        // 生成新的区块链哈希
        this.selectedProduct.blockchainHash = this.generateBlockchainHash()
        
        // 更新产品列表
        const index = this.productList.findIndex(p => p.snCode === this.selectedProduct.snCode)
        if (index !== -1) {
          this.productList[index] = { ...this.selectedProduct }
        }
        
        this.$message.success({
          message: '标注数据已成功保存到区块链',
          duration: 3000,
          showClose: true
        })
        
        this.currentAnnotations = []
        this.updateStatsFromProducts()
        this.savingAnnotations = false
      }, 1500)
    },
    
    // 生成检测报告
    generateInspectionReport() {
      if (!this.selectedProduct) {
        this.$message.warning('请先选择产品')
        return
      }
      
      const reportData = {
        product: this.selectedProduct,
        annotations: this.currentAnnotations,
        aiResults: this.aiDetectionResults,
        generationTime: new Date().toISOString(),
        blockchainHash: this.generateBlockchainHash()
      }
      
      // 这里可以调用API生成报告
      this.$message.success('检测报告生成成功')
      console.log('检测报告数据:', reportData)
    },
    
    // 上传相关方法
    uploadProductionData() {
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
      
      // 模拟上传过程
      setTimeout(() => {
        this.uploading = false
        this.uploadDialogVisible = false
        
        // 生成新的区块链产品数据
        const newProduct = {
          snCode: `AX-${new Date().getFullYear()}${(new Date().getMonth() + 1).toString().padStart(2, '0')}${new Date().getDate().toString().padStart(2, '0')}-${this.productList.length + 1}`,
          model: 'Φ80×300',
          batchNumber: `${new Date().getFullYear()}${(new Date().getMonth() + 1).toString().padStart(2, '0')}${new Date().getDate().toString().padStart(2, '0')}-01`,
          rawMaterialBatch: 'MT-' + new Date().toISOString().slice(0, 10).replace(/-/g, ''),
          material: '45#钢',
          productionDate: new Date().toISOString(),
          operator: '系统导入',
          status: 0,
          defectLevel: 0,
          defectWorkshop: null,
          defectProcess: null,
          blockchainId: this.generateBlockchainId(),
          blockchainHash: this.generateBlockchainHash()
        }
        
        this.productList.unshift(newProduct)
        this.uploadFileList = []
        
        // 更新统计
        this.totalProducts += 1
        
        this.$message.success('生产数据已上传并生成区块链记录')
        this.updateStatsFromProducts()
      }, 2000)
    },
    
    // 工具方法
    getAxleWidth(product) {
      const diameter = parseInt(product.model.split('Φ')[1]?.split('×')[0]) || 80
      return `${Math.min(diameter, 100)}px`
    },
    
    getFlangeWidth(product) {
      return '25px'
    },
    
    getFlangeHeight(product) {
      return '25px'
    },
    
    getProductStatusType(status) {
      const statusMap = {
        0: '', // 待检测
        1: 'primary', // 检测中
        2: 'success', // 已检测
        3: 'danger' // 有缺陷
      }
      return statusMap[status] || ''
    },
    
    getProductStatusText(status) {
      const statusMap = {
        0: '待检测',
        1: '检测中',
        2: '已检测',
        3: '有缺陷'
      }
      return statusMap[status] || '未知'
    },
    
    getDefectLevelText(level) {
      const levelMap = {
        1: '轻微',
        2: '中等',
        3: '严重',
        4: '致命'
      }
      return levelMap[level] || '未知'
    },
    
    getWorkshopName(workshopId) {
      const workshop = this.workshops.find(w => w.id === workshopId)
      return workshop ? workshop.name : '未知车间'
    },
    
    getProcessName(processId) {
      const process = this.processes.find(p => p.id === processId)
      return process ? process.name : '未知工序'
    },
    
    getDefectTypeText(defectCode) {
      const defect = this.defectTypes.find(d => d.code === defectCode)
      return defect ? defect.name : '未知缺陷'
    },
    
    // 修改这个方法，使钻孔偏心显示为黄色
    getDefectTagType(defectCode) {
      if (defectCode === 'drilling_eccentric') {
        return 'warning' // 钻孔偏心显示为黄色
      }
      if (defectCode.includes('crack')) return 'danger'
      if (defectCode.includes('dimension')) return '' // 花键尺寸偏差恢复为默认
      if (defectCode.includes('hardness') || defectCode.includes('property')) return 'primary'
      return 'info'
    },
    
    // 新增方法：获取标签样式
    getDefectTagStyle(defectCode) {
      if (defectCode === 'drilling_eccentric') {
        return {
          color: '#e6a23c', // 黄色字体
          borderColor: '#e6a23c',
          backgroundColor: '#fdf6ec'
        }
      }
      return {}
    },
    
    getDefectColor(defectType) {
      const colorMap = {
        'crack': '#f56c6c',
        'dimension': '#e6a23c',
        'surface': '#67c23a',
        'shape': '#409eff',
        'property': '#909399',
        'internal': '#8e44ad',
        'coating': '#16a085',
        'position': '#f39c12'
      }
      return colorMap[defectType] || '#409eff'
    },
    
    getConfidenceStatus(confidence) {
      if (confidence >= 0.9) return 'success'
      if (confidence >= 0.7) return 'warning'
      return 'exception'
    },
    
    calculateSeverity(confidence) {
      if (confidence >= 0.9) return 4
      if (confidence >= 0.8) return 3
      if (confidence >= 0.7) return 2
      if (confidence >= 0.6) return 1
      return 1
    },
    
    calculateOverallDefectLevel() {
      if (this.currentAnnotations.length === 0) return 0
      const maxSeverity = Math.max(...this.currentAnnotations.map(a => a.severity))
      return Math.min(maxSeverity, 4)
    },
    
    formatDate(dateStr, withTime = false) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      if (withTime) {
        return date.toLocaleString('zh-CN')
      }
      return date.toLocaleDateString('zh-CN')
    },
    
    formatNumber(num) {
      return num.toLocaleString('zh-CN')
    },
    
    // 区块链相关方法
    generateBlockchainId() {
      const chars = '0123456789abcdef'
      let result = '0x'
      for (let i = 0; i < 16; i++) {
        result += chars[Math.floor(Math.random() * chars.length)]
      }
      return result
    },
    
    generateBlockchainHash() {
      const chars = '0123456789abcdef'
      let result = '0x'
      for (let i = 0; i < 64; i++) {
        result += chars[Math.floor(Math.random() * chars.length)]
      }
      return result
    },
    
    // 筛选和刷新
    applyFilter() {
      this.productPage = 1
      // 这里实际应该重新加载数据
    },
    
    refreshProducts() {
      this.loadProducts()
      this.$message.success('产品列表已刷新')
    },
    
    handleProductSizeChange(val) {
      this.productPageSize = val
      this.productPage = 1
    },
    
    handleProductCurrentChange(val) {
      this.productPage = val
    }
  }
}
</script>

<style scoped>
.smart-annotation-platform {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  overflow: hidden;
}

.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.header-left h1 {
  margin: 0;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-subtitle {
  margin: 8px 0 0;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.stats-overview {
  margin-bottom: 24px;
  flex-shrink: 0;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.1);
}

.stat-primary {
  border-left: 4px solid #409EFF;
}

.stat-warning {
  border-left: 4px solid #E6A23C;
}

.stat-success {
  border-left: 4px solid #67C23A;
}

.stat-info {
  border-left: 4px solid #909399;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 28px;
  color: white;
}

.stat-primary .stat-icon {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
}

.stat-warning .stat-icon {
  background: linear-gradient(135deg, #E6A23C 0%, #c1953a 100%);
}

.stat-success .stat-icon {
  background: linear-gradient(135deg, #67C23A 0%, #529b2e 100%);
}

.stat-info .stat-icon {
  background: linear-gradient(135deg, #909399 0%, #767a82 100%);
}

.stat-content .stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.stat-content .stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.main-container {
  flex: 1;
  display: flex;
  gap: 24px;
  min-height: 0;
  overflow: hidden;
}

.left-section {
  width: 380px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.right-section {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.section-card {
  background: white;
  border-radius: 12px;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.section-header {
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.selected-product-info {
  font-size: 14px;
  color: #409EFF;
  font-weight: normal;
  margin-left: 10px;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-list-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  min-height: 0;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-list i {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-list p {
  margin: 0;
  font-size: 14px;
}

.product-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
  flex-shrink: 0;
}

.product-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.2);
}

.product-item.selected {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.product-item.defect {
  border-left: 4px solid #f56c6c;
}

.product-preview {
  position: relative;
  height: 140px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 6px 6px 0 0;
  overflow: hidden;
  padding: 12px;
}

.blockchain-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.axle-visualization {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  position: relative;
}

.axle-shaft {
  height: 24px;
  background: linear-gradient(90deg, #6c757d, #adb5bd);
  border-radius: 2px;
  z-index: 1;
}

.axle-flange {
  background: linear-gradient(45deg, #495057, #6c757d);
  border-radius: 6px;
  margin: 0 20px;
  z-index: 2;
  position: relative;
}

.axle-spline {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 60%;
  height: 8px;
  background: linear-gradient(90deg, #343a40, #495057);
  border-radius: 2px;
  z-index: 1;
}

.product-status {
  position: absolute;
  bottom: 8px;
  right: 8px;
  display: flex;
  gap: 4px;
}

.product-details {
  padding: 16px;
}

.product-sn {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}

.sn-code {
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
}

.product-specs {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.product-defect-info {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination-wrapper {
  padding: 16px 24px;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}

.ai-detection-panel {
  padding: 24px;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detection-results {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  max-height: 400px;
  overflow-y: auto;
}

.defect-title {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  padding: 8px 0;
}

.defect-workshop {
  font-size: 14px;
  color: #606266;
  flex: 1;
  min-width: 150px;
}

.confidence-text {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  min-width: 50px;
}

.defect-details {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 4px;
}

.detail-row {
  margin-bottom: 12px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-row label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
  font-weight: 600;
}

.detail-row p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.detail-row code {
  background: #e9ecef;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #495057;
}

.detail-row .suggestion {
  color: #409EFF;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.no-detection {
  text-align: center;
  padding: 40px 20px;
  background: #f0f9eb;
  border-radius: 8px;
  border: 1px dashed #c2e7b0;
}

.subtext {
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
}

.manual-annotation-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.annotation-wizard {
  padding: 24px;
  border-bottom: 1px solid #ebeef5;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.wizard-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.step-content {
  flex: 1;
  padding: 20px 0;
  min-height: 0;
  overflow-y: auto;
}

.step-content h5 {
  margin: 0 0 20px 0;
  font-size: 15px;
  color: #303133;
  flex-shrink: 0;
}

.workshop-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  max-height: 300px;
  overflow-y: auto;
  padding: 4px;
}

.workshop-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.workshop-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
}

.workshop-card.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.workshop-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.workshop-info {
  flex: 1;
  min-width: 0;
}

.workshop-name {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.workshop-desc {
  font-size: 13px;
  color: #909399;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.process-grid,
.defect-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
  padding: 4px;
}

.process-card,
.defect-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  flex-shrink: 0;
  min-height: 120px;
  display: flex;
  flex-direction: column;
}

.process-card:hover,
.defect-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px 0 rgba(64, 158, 255, 0.1);
}

.process-card.selected,
.defect-card.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.process-icon,
.defect-icon {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.process-icon {
  background: linear-gradient(135deg, #67C23A 0%, #529b2e 100%);
}

.process-info,
.defect-info {
  flex: 1;
  min-width: 0;
}

.process-name,
.defect-name {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.process-desc,
.defect-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.defect-standard {
  font-size: 11px;
  color: #606266;
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.defect-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 10px 0;
}

.wizard-navigation {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}

.annotations-list {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.list-header h5 {
  margin: 0;
  font-size: 15px;
  color: #303133;
}

.annotations-container {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
  min-height: 0;
  padding: 4px;
}

.annotation-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  background: white;
  flex-shrink: 0;
}

.annotation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.annotation-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  min-width: 0;
}

.annotation-title .workshop {
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
}

.annotation-title .separator {
  color: #909399;
  flex-shrink: 0;
}

.annotation-title .process {
  color: #409EFF;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.annotation-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.annotation-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
  padding: 4px;
  background: #f8f9fa;
  border-radius: 4px;
  min-height: 28px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-item.suggestion {
  color: #409EFF;
  background: #ecf5ff;
}

.annotation-actions {
  margin-top: auto;
  flex-shrink: 0;
}

.no-selection {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.empty-panel {
  text-align: center;
  color: #909399;
}

.empty-panel h3 {
  margin: 16px 0 8px;
  font-size: 18px;
  color: #606266;
}

.empty-panel p {
  margin: 0;
  font-size: 14px;
}

.upload-content {
  padding: 20px 0;
}

.upload-info {
  margin-top: 20px;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .left-section {
    width: 340px;
  }
  
  .process-grid,
  .defect-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
  }
  
  .left-section {
    width: 100%;
    max-height: 400px;
  }
  
  .annotation-details {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 动画效果 */
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.stat-card:hover .stat-icon {
  animation: pulse 0.5s ease-in-out;
}

/* 实时数据更新动画 */
@keyframes highlightUpdate {
  0% { background-color: rgba(64, 158, 255, 0.1); }
  100% { background-color: transparent; }
}

.stat-updating {
  animation: highlightUpdate 1s ease-in-out;
}
</style>