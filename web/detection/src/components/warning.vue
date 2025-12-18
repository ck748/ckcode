<template>
  <div class="warning-container">
    <!-- 页面标题和统计 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-warning-outline title-icon"></i>
          车间预警监控中心
        </h1>
        <p class="page-subtitle">实时监控各车间生产状态，提前预警潜在问题</p>
      </div>
      <div class="header-stats">
        <el-tag type="success" size="large" class="stat-tag">
          <i class="el-icon-success"></i>
          系统运行正常
        </el-tag>
        <div class="last-update">
          <i class="el-icon-time"></i>
          更新时间：{{ lastUpdateTime }}
        </div>
      </div>
    </div>

    <!-- 车间状态概览 -->
    <div class="workshop-status">
      <h2 class="section-title">
        <i class="el-icon-monitor"></i>
        车间生产状态
      </h2>
      <div class="status-cards">
        <div class="status-card normal">
          <div class="card-icon">
            <i class="el-icon-success"></i>
          </div>
          <div class="card-content">
            <h3>一车间（锻造）</h3>
            <p>生产状态：正常运行</p>
            <p class="status-detail">设备运转率：98.5%</p>
          </div>
        </div>

        <div class="status-card normal">
          <div class="card-icon">
            <i class="el-icon-success"></i>
          </div>
          <div class="card-content">
            <h3>二车间（热处理）</h3>
            <p>生产状态：正常运行</p>
            <p class="status-detail">温度控制：正常</p>
          </div>
        </div>

        <div class="status-card warning">
          <div class="card-icon">
            <i class="el-icon-warning"></i>
          </div>
          <div class="card-content">
            <h3>三车间（机加工）</h3>
            <p>生产状态：存在预警</p>
            <p class="status-detail">预警数量：{{ warningStats.total }} 个</p>
          </div>
        </div>

        <div class="status-card normal">
          <div class="card-icon">
            <i class="el-icon-success"></i>
          </div>
          <div class="card-content">
            <h3>四车间（装配）</h3>
            <p>生产状态：正常运行</p>
            <p class="status-detail">装配合格率：99.2%</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 三车间预警信息 -->
    <div class="warning-section">
      <div class="section-header">
        <h2 class="section-title">
          <i class="el-icon-s-operation warning-icon"></i>
          三车间（机加工）预警信息
        </h2>
        <div class="section-actions">
          <el-button
            type="primary"
            icon="el-icon-refresh"
            @click="refreshData"
            size="small"
          >
            刷新
          </el-button>
        </div>
      </div>

      <!-- 预警统计 -->
      <div class="warning-stats">
        <div class="stat-card">
          <div class="stat-value">{{ warningStats.pending }}</div>
          <div class="stat-label">待处理预警</div>
          <div class="stat-tag pending">需要关注</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ warningStats.processing }}</div>
          <div class="stat-label">处理中预警</div>
          <div class="stat-tag processing">正在处理</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ warningStats.resolved }}</div>
          <div class="stat-label">已解决预警</div>
          <div class="stat-tag resolved">已完成</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ warningStats.total }}</div>
          <div class="stat-label">总预警数</div>
          <div class="stat-tag total">本月累计</div>
        </div>
      </div>

      <!-- 预警列表 -->
      <div class="warning-list">
        <div class="list-header">
          <h3>当前预警列表</h3>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索预警内容..."
            clearable
            size="small"
            class="search-input"
            prefix-icon="el-icon-search"
          />
        </div>

        <div class="warnings">
          <div v-for="warning in filteredWarnings" :key="warning.id" class="warning-item" :class="warning.status">
            <div class="warning-header">
              <div class="warning-title">
                <el-tag :type="getWarningLevelType(warning.level)" size="small">
                  {{ warning.level }}
                </el-tag>
                <span class="title-text">{{ warning.title }}</span>
              </div>
              <div class="warning-time">
                <i class="el-icon-time"></i>
                {{ warning.time }}
              </div>
            </div>
            
            <div class="warning-content">
              <p>{{ warning.content }}</p>
              <div v-if="warning.equipment" class="warning-meta">
                <span class="meta-item">
                  <i class="el-icon-cpu"></i>
                  设备：{{ warning.equipment }}
                </span>
                <span class="meta-item">
                  <i class="el-icon-user"></i>
                  负责人：{{ warning.operator }}
                </span>
              </div>
            </div>

            <div class="warning-footer">
              <el-tag :type="getStatusType(warning.status)" size="small">
                {{ warning.status }}
              </el-tag>
              <div class="action-buttons">
                <el-button
                  v-if="warning.status === '待处理'"
                  type="primary"
                  size="mini"
                  @click="handleProcess(warning)"
                >
                  开始处理
                </el-button>
                <el-button
                  v-else-if="warning.status === '处理中'"
                  type="success"
                  size="mini"
                  @click="handleResolve(warning)"
                >
                  标记解决
                </el-button>
                <el-button
                  type="text"
                  size="mini"
                  @click="viewDetails(warning)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </div>

          <div v-if="filteredWarnings.length === 0" class="no-warnings">
            <i class="el-icon-success"></i>
            <p>暂无预警信息</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 预警详情对话框 -->
    <el-dialog
      :title="currentWarning.title"
      :visible.sync="detailDialogVisible"
      width="500px"
    >
      <div class="warning-detail">
        <div class="detail-row">
          <span class="label">预警级别：</span>
          <el-tag :type="getWarningLevelType(currentWarning.level)" size="small">
            {{ currentWarning.level }}
          </el-tag>
        </div>
        
        <div class="detail-row">
          <span class="label">预警时间：</span>
          <span>{{ currentWarning.time }}</span>
        </div>
        
        <div v-if="currentWarning.equipment" class="detail-row">
          <span class="label">设备编号：</span>
          <span>{{ currentWarning.equipment }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">负责人：</span>
          <span>{{ currentWarning.operator }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">预警描述：</span>
          <p class="detail-content">{{ currentWarning.content }}</p>
        </div>
        
        <div v-if="currentWarning.detail" class="detail-row">
          <span class="label">详细说明：</span>
          <p class="detail-content">{{ currentWarning.detail }}</p>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
            v-if="currentWarning.status === '待处理'"
            type="primary"
            @click="handleProcess(currentWarning)"
          >
            开始处理
          </el-button>
          <el-button
            v-else-if="currentWarning.status === '处理中'"
            type="success"
            @click="handleResolve(currentWarning)"
          >
            标记解决
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'WarningCenter',
  data() {
    return {
      // 搜索关键词
      searchKeyword: '',
      // 更新时间
      lastUpdateTime: '',
      // 预警统计数据
      warningStats: {
        total: 3,
        pending: 1,
        processing: 1,
        resolved: 1
      },
      // 预警列表数据（只包含三车间）
      warningList: [
        {
          id: 1,
          level: '警告',
          title: '加工精度超差',
          content: '半轴长度偏差0.5mm，超出公差范围',
          time: '2023-12-20 14:30:25',
          status: '待处理',
          operator: '赵六',
          equipment: 'CNC-003',
          detail: '测量系统检测到半轴长度为850.5mm，超出公差范围±0.3mm。可能原因：刀具磨损或夹具松动。'
        },
        {
          id: 2,
          level: '提示',
          title: '刀具磨损提醒',
          content: 'CNC刀具已使用120小时，接近更换周期',
          time: '2023-12-20 10:15:30',
          status: '处理中',
          operator: '王五',
          equipment: 'CNC-002',
          detail: '刀具使用寿命为150小时，目前已使用120小时，建议提前准备更换。'
        },
        {
          id: 3,
          level: '提示',
          title: '润滑系统检查',
          content: '机床润滑系统油位偏低',
          time: '2023-12-19 16:45:10',
          status: '已解决',
          operator: '张三',
          equipment: 'MILL-001',
          detail: '润滑系统油位已补充至正常水平，问题已解决。'
        }
      ],
      // 对话框控制
      detailDialogVisible: false,
      currentWarning: {}
    }
  },
  computed: {
    // 过滤后的预警列表
    filteredWarnings() {
      if (!this.searchKeyword) {
        return this.warningList
      }
      const keyword = this.searchKeyword.toLowerCase()
      return this.warningList.filter(item => 
        item.title.toLowerCase().includes(keyword) ||
        item.content.toLowerCase().includes(keyword) ||
        (item.equipment && item.equipment.toLowerCase().includes(keyword)) ||
        item.operator.toLowerCase().includes(keyword)
      )
    }
  },
  mounted() {
    this.updateTime()
    // 每分钟更新一次时间
    setInterval(() => {
      this.updateTime()
    }, 60000)
  },
  methods: {
    // 更新时间
    updateTime() {
      const now = new Date()
      this.lastUpdateTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    
    // 获取预警级别标签类型
    getWarningLevelType(level) {
      const types = {
        '紧急': 'danger',
        '严重': 'warning',
        '警告': 'warning',
        '提示': 'info'
      }
      return types[level] || 'info'
    },
    
    // 获取状态标签类型
    getStatusType(status) {
      const types = {
        '待处理': 'danger',
        '处理中': 'warning',
        '已解决': 'success'
      }
      return types[status] || 'info'
    },
    
    // 刷新数据
    refreshData() {
      this.updateTime()
      this.$message.success('数据已刷新')
    },
    
    // 查看详情
    viewDetails(warning) {
      this.currentWarning = { ...warning }
      this.detailDialogVisible = true
    },
    
    // 开始处理
    handleProcess(warning) {
      this.$confirm('确定开始处理此预警吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = this.warningList.findIndex(item => item.id === warning.id)
        if (index !== -1) {
          this.warningList[index].status = '处理中'
          this.updateStats()
          this.$message.success('已开始处理预警')
        }
      })
    },
    
    // 标记解决
    handleResolve(warning) {
      this.$confirm('确认此预警已解决吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        const index = this.warningList.findIndex(item => item.id === warning.id)
        if (index !== -1) {
          this.warningList[index].status = '已解决'
          this.updateStats()
          this.$message.success('预警已标记为解决')
        }
      })
    },
    
    // 更新统计
    updateStats() {
      const total = this.warningList.length
      const pending = this.warningList.filter(item => item.status === '待处理').length
      const processing = this.warningList.filter(item => item.status === '处理中').length
      const resolved = this.warningList.filter(item => item.status === '已解决').length
      
      this.warningStats = { total, pending, processing, resolved }
    }
  }
}
</script>

<style scoped>
.warning-container {
  min-height: calc(100vh - 128px);
  background: #ffffff;
  padding: 20px;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-content {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 28px;
  color: #e6a23c;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.header-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.stat-tag {
  padding: 8px 16px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.last-update {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 车间状态 */
.workshop-status {
  margin-bottom: 30px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.status-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.status-card.normal {
  border-left: 4px solid #67c23a;
}

.status-card.warning {
  border-left: 4px solid #e6a23c;
  box-shadow: 0 2px 12px rgba(230, 162, 60, 0.1);
}

.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.status-card.normal .card-icon {
  background: #f0f9eb;
  color: #67c23a;
}

.status-card.warning .card-icon {
  background: #fdf6ec;
  color: #e6a23c;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-content p {
  margin: 4px 0;
  font-size: 14px;
  color: #606266;
}

.status-detail {
  font-size: 13px;
  color: #909399;
}

/* 预警区域 */
.warning-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.warning-icon {
  color: #e6a23c;
  font-size: 20px;
}

/* 预警统计 */
.warning-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e4e7ed;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.stat-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.stat-tag.pending {
  background: #fde2e2;
  color: #f56c6c;
}

.stat-tag.processing {
  background: #fdf6ec;
  color: #e6a23c;
}

.stat-tag.resolved {
  background: #f0f9eb;
  color: #67c23a;
}

.stat-tag.total {
  background: #ecf5ff;
  color: #409eff;
}

/* 预警列表 */
.warning-list {
  padding: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.list-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.search-input {
  width: 200px;
}

.warnings {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.warning-item {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.warning-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.warning-title {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.title-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.warning-time {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.warning-content {
  margin-bottom: 16px;
}

.warning-content p {
  margin: 0 0 12px 0;
  color: #606266;
  line-height: 1.6;
}

.warning-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.warning-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.no-warnings {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.no-warnings i {
  font-size: 48px;
  color: #67c23a;
  margin-bottom: 16px;
}

.no-warnings p {
  margin: 0;
  font-size: 16px;
}

/* 详情对话框 */
.warning-detail {
  padding: 10px 0;
}

.detail-row {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.detail-row .label {
  width: 80px;
  color: #606266;
  font-size: 14px;
  flex-shrink: 0;
}

.detail-content {
  flex: 1;
  margin: 0;
  color: #303133;
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-stats {
    align-items: flex-start;
  }
  
  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .list-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .warning-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .warning-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .warning-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .action-buttons {
    justify-content: flex-end;
  }
}
</style>