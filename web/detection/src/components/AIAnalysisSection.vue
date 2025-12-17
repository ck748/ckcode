<template>
  <div class="ai-analysis-section" v-if="analysisResult">
    <h3 class="details-title">
      <i class="el-icon-cpu"></i>
      🤖 AI智能分析
    </h3>
    
    <div class="ai-analysis-content">
      <!-- 整体评估 -->
      <div class="ai-overall-section">
        <div class="ai-overall-header">
          <div class="ai-status">
            <el-tag 
              :type="analysisResult.hasIssue ? 'danger' : 'success'" 
              size="medium"
              effect="dark"
            >
              {{ analysisResult.hasIssue ? '⚠️ 发现问题' : '✅ 未发现问题' }}
            </el-tag>
          </div>
        </div>
        <div class="ai-overall-content">
          <div class="overall-assessment">
            <h4>整体评估：</h4>
            <p>{{ analysisResult.overallAssessment || '正在分析...' }}</p>
          </div>
        </div>
      </div>
      
      <!-- 问题车间列表 -->
      <div class="problem-workshops" v-if="analysisResult.problemWorkshops && analysisResult.problemWorkshops.length > 0">
        <h4 class="problem-title">🚨 发现问题的车间：</h4>
        <div class="workshop-tags">
          <el-tag 
            v-for="workshop in analysisResult.problemWorkshops" 
            :key="workshop"
            type="danger"
            size="medium"
            style="margin-right: 10px;"
          >
            {{ getWorkshopName(workshop) }}
          </el-tag>
        </div>
      </div>
      
      <!-- 详细分析 -->
      <div class="detailed-analysis" v-if="analysisResult.detailedAnalysis">
        <h4 class="analysis-title">🔍 详细分析：</h4>
        <div 
          v-for="(analysis, workshop) in analysisResult.detailedAnalysis" 
          :key="workshop"
          class="workshop-analysis"
        >
          <div class="workshop-analysis-header">
            <span class="workshop-name">{{ getWorkshopName(workshop) }}</span>
            <el-tag 
              :type="analysis.hasIssue ? 'danger' : 'success'" 
              size="small"
            >
              {{ analysis.hasIssue ? '存在问题' : '正常' }}
            </el-tag>
          </div>
          <div class="workshop-analysis-content" v-if="analysis.hasIssue">
            <div class="issues-list" v-if="analysis.issues && analysis.issues.length > 0">
              <p class="issues-title">问题描述：</p>
              <ul>
                <li v-for="(issue, idx) in analysis.issues" :key="idx">
                  {{ issue }}
                </li>
              </ul>
            </div>
            <div class="suggestion" v-if="analysis.suggestion">
              <p class="suggestion-title">💡 处理建议：</p>
              <p class="suggestion-content">{{ analysis.suggestion }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 综合建议 -->
      <div class="ai-recommendation" v-if="analysisResult.recommendation">
        <h4 class="recommendation-title">🎯 综合建议：</h4>
        <div class="recommendation-content">
          {{ analysisResult.recommendation }}
        </div>
      </div>
      
      <!-- 后备提示 -->
      <div class="fallback-notice" v-if="analysisResult.useFallback">
        <el-alert
          title="注意"
          type="info"
          description="AI服务不可用，使用了基于规则的后备分析方案。"
          :closable="false"
          show-icon
        >
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AIAnalysisSection',
  props: {
    analysisResult: {
      type: Object,
      default: null
    }
  },
  methods: {
    getWorkshopName(workshop) {
      const names = {
        'workshop1': '一车间（锻造）',
        'workshop2': '二车间（热处理）',
        'workshop3': '三车间（机加工）',
        'workshop4': '四车间（装配）'
      }
      return names[workshop] || workshop
    }
  }
}
</script>

<style scoped>
/* AI分析样式 */
.ai-analysis-section {
  margin-top: 30px;
  padding: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.ai-analysis-section .details-title {
  color: white;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
}

.ai-analysis-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.ai-overall-section {
  margin-bottom: 25px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
}

.ai-overall-header {
  margin-bottom: 15px;
}

.ai-status .el-tag {
  font-size: 16px;
  padding: 8px 16px;
}

.overall-assessment h4 {
  color: #303133;
  font-size: 16px;
  margin-bottom: 10px;
}

.overall-assessment p {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.problem-workshops {
  margin-bottom: 25px;
  padding: 15px;
  background: #fef0f0;
  border-radius: 8px;
  border-left: 4px solid #F56C6C;
}

.problem-title {
  color: #F56C6C;
  font-size: 15px;
  margin-bottom: 10px;
}

.workshop-tags .el-tag {
  font-size: 14px;
  padding: 6px 12px;
}

.detailed-analysis {
  margin-bottom: 25px;
}

.analysis-title {
  color: #303133;
  font-size: 16px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #EBEEF5;
}

.workshop-analysis {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #DCDFE6;
}

.workshop-analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #DCDFE6;
}

.workshop-name {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
}

.workshop-analysis-content {
  padding-left: 10px;
}

.issues-list {
  margin-bottom: 15px;
}

.issues-title {
  font-weight: bold;
  color: #606266;
  margin-bottom: 8px;
}

.issues-list ul {
  margin: 0;
  padding-left: 20px;
}

.issues-list li {
  color: #909399;
  margin-bottom: 5px;
  line-height: 1.6;
}

.suggestion {
  padding: 12px;
  background: #fff;
  border-radius: 6px;
  border-left: 3px solid #E6A23C;
}

.suggestion-title {
  font-weight: bold;
  color: #E6A23C;
  margin-bottom: 8px;
}

.suggestion-content {
  color: #606266;
  line-height: 1.6;
}

.ai-recommendation {
  padding: 20px;
  background: #ecf5ff;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
  margin-bottom: 20px;
}

.recommendation-title {
  color: #409EFF;
  font-size: 16px;
  margin-bottom: 12px;
}

.recommendation-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
}

.fallback-notice {
  margin-top: 15px;
}
</style>
