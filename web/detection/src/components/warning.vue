<template>
  <div class="simple-inspection-report">
    <!-- 问题列表 -->
    <div class="defect-list-section">
      <div class="section-header">
        <div class="header-left">
          <h2 class="section-title">半轴缺陷预警列表</h2>
          <div class="quick-stats">
            <div class="stat-item total">
              <i class="el-icon-warning stat-icon"></i>
              <span class="stat-text">
                <span class="stat-value">{{ totalDefects }}</span>
                <span class="stat-label">总缺陷数</span>
              </span>
            </div>
            <div class="stat-item critical">
              <i class="el-icon-close stat-icon"></i>
              <span class="stat-text">
                <span class="stat-value">{{ criticalCount }}</span>
                <span class="stat-label">紧急缺陷</span>
              </span>
            </div>
            <div class="stat-item pending">
              <i class="el-icon-time stat-icon"></i>
              <span class="stat-text">
                <span class="stat-value">{{ pendingCount }}</span>
                <span class="stat-label">待处理</span>
              </span>
            </div>
            <div class="stat-item resolved">
              <i class="el-icon-check stat-icon"></i>
              <span class="stat-text">
                <span class="stat-value">{{ resolvedCount }}</span>
                <span class="stat-label">已解决</span>
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 其他筛选控件行 -->
      <div class="filter-controls-row">
        <!-- 搜索框和车间筛选 -->
        <div class="left-filters">
          <div class="search-box">
            <el-input
              v-model="searchQuery"
              placeholder="搜索SN编号或缺陷类型..."
              size="small"
              style="width: 280px;"
              clearable
              @input="handleSearch"
            >
              <i slot="prefix" class="el-icon-search"></i>
            </el-input>
          </div>
          
          <!-- 车间筛选器 - 移动到搜索框后面 -->
          <div class="workshop-filter">
            <span class="filter-label">车间:</span>
            <div class="workshop-buttons">
              <div 
                v-for="workshop in workshopOptions" 
                :key="workshop.value"
                :class="['workshop-btn', { active: workshopFilter === workshop.value }]"
                @click="toggleWorkshopFilter(workshop.value)"
              >
                <i :class="workshop.icon"></i>
                <span class="btn-text">{{ workshop.label }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 状态和严重程度筛选 -->
        <div class="right-filters">
          <div class="status-filter">
            <span class="filter-label">状态:</span>
            <el-select
              v-model="statusFilter"
              size="small"
              placeholder="全部状态"
              clearable
              style="width: 120px;"
            >
              <el-option 
                v-for="status in statusOptions" 
                :key="status.value"
                :label="status.label"
                :value="status.value"
              ></el-option>
            </el-select>
          </div>
          
          <div class="severity-filter">
            <span class="filter-label">严重程度:</span>
            <el-select
              v-model="severityFilter"
              size="small"
              placeholder="全部"
              clearable
              style="width: 100px;"
            >
              <el-option 
                v-for="severity in severityOptions" 
                :key="severity.value"
                :label="severity.label"
                :value="severity.value"
              ></el-option>
            </el-select>
          </div>
        </div>
      </div>

      <!-- 缺陷表格 -->
      <div class="defect-table">
        <table>
          <thead>
            <tr>
              <th style="width: 140px;">时间</th>
              <th style="width: 120px;">工序/设备</th>
              <th style="width: 200px;">缺陷描述</th>
              <th style="width: 100px;">缺陷类型</th>
              <th style="width: 100px;">严重程度</th>
              <th style="width: 100px;">当前状态</th>
              <th style="width: 120px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="defect in filteredDefects" 
              :key="defect.id"
              @click="viewBlockchainDetails(defect)"
              :class="['defect-row', getRowClass(defect)]"
            >
              <td class="time-cell">
                <div class="time-wrapper">
                  <div class="date">{{ defect.date }}</div>
                  <div class="time">{{ defect.time }}</div>
                  <div class="sn">{{ defect.sn }}</div>
                </div>
              </td>
              <td class="process-cell">
                <div class="workshop-info">
                  <i class="el-icon-office-building"></i>
                  <span>{{ defect.workshop }}</span>
                </div>
                <div class="equipment-info">
                  <i class="el-icon-cpu"></i>
                  <span>{{ defect.equipment }}</span>
                </div>
              </td>
              <td class="description-cell">
                <div class="defect-description">
                  <div class="defect-title">{{ defect.defectType }}</div>
                  <div class="defect-desc">{{ defect.description }}</div>
                </div>
              </td>
              <td>
                <div class="type-cell">
                  <span class="type-tag" :style="getTypeStyle(defect.defectType)">
                    {{ defect.defectType }}
                  </span>
                </div>
              </td>
              <td>
                <div class="severity-cell">
                  <span :class="['severity-badge', defect.severity]">
                    {{ defect.severity }}
                  </span>
                </div>
              </td>
              <td>
                <div class="status-cell">
                  <span :class="['status-badge', defect.status]">
                    {{ defect.status }}
                  </span>
                  <div class="response-time" v-if="defect.responseTime">
                    {{ defect.responseTime }}
                  </div>
                </div>
              </td>
              <td>
                <div class="action-cell">
                  <button class="detail-btn" @click.stop="viewBlockchainDetails(defect)">
                    <i class="el-icon-view"></i>
                    详情
                  </button>
                  <div class="quick-actions" v-if="defect.status === '待处理' || defect.status === '处理中'">
                    <el-button 
                      v-if="defect.status === '待处理'"
                      type="text" 
                      size="mini" 
                      icon="el-icon-edit"
                      @click.stop="handleProcess(defect)"
                      title="开始处理"
                    ></el-button>
                    <el-button 
                      v-if="defect.status === '处理中'"
                      type="text" 
                      size="mini" 
                      icon="el-icon-check"
                      @click.stop="handleResolve(defect)"
                      title="标记解决"
                    ></el-button>
                  </div>
                </div>
              </td>
            </tr>
            
            <!-- 空状态 -->
            <tr v-if="filteredDefects.length === 0">
              <td colspan="7" class="empty-state">
                <div class="empty-content">
                  <i class="el-icon-search-empty"></i>
                  <p>未找到匹配的缺陷记录</p>
                  <el-button type="text" @click="clearFilters">
                    清除筛选条件
                  </el-button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="filteredDefects.length > 0">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :page-sizes="[20, 50, 100]"
          :page-size="pageSize"
          :total="totalFilteredDefects"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 区块链详情对话框 -->
    <el-dialog
      :title="`缺陷详情 - ${currentDefect.sn}`"
      :visible.sync="blockchainDialogVisible"
      width="800px"
      custom-class="blockchain-dialog"
      :close-on-click-modal="false"
    >
      <div class="blockchain-detail" v-if="currentDefect.id">
        <!-- 区块链头部信息 -->
        <div class="blockchain-header">
          <div class="blockchain-info">
            <div class="info-item">
              <span class="label">SN编号:</span>
              <span class="value sn-value">{{ currentDefect.sn }}</span>
            </div>
            <div class="info-item">
              <span class="label">区块哈希:</span>
              <span class="hash">{{ generateBlockHash(currentDefect) }}</span>
            </div>
            <div class="info-item">
              <span class="label">前一区块:</span>
              <span class="hash">{{ getPreviousBlockHash(currentDefect) }}</span>
            </div>
          </div>
          <div class="blockchain-status">
            <div :class="['status', currentDefect.blockchainStatus || 'verified']">
              <i :class="currentDefect.blockchainStatus === 'pending' ? 'el-icon-time' : 'el-icon-success'"></i>
              {{ currentDefect.blockchainStatus === 'pending' ? '待验证' : '已验证' }}
            </div>
          </div>
        </div>

        <!-- 基础信息 -->
        <div class="detail-section">
          <h3>
            <i class="el-icon-info"></i>
            基础信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">检测时间:</span>
              <span class="value">{{ currentDefect.date }} {{ currentDefect.time }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属车间:</span>
              <span class="value">{{ currentDefect.workshop }}</span>
            </div>
            <div class="info-item">
              <span class="label">设备编号:</span>
              <span class="value">{{ currentDefect.equipment }}</span>
            </div>
            <div class="info-item">
              <span class="label">负责人:</span>
              <span class="value">{{ currentDefect.operator }}</span>
            </div>
          </div>
        </div>

        <!-- 缺陷详情 -->
        <div class="detail-section">
          <h3>
            <i class="el-icon-warning"></i>
            缺陷详情
          </h3>
          <div class="defect-details">
            <div class="detail-row">
              <span class="label">缺陷类型:</span>
              <span class="value">
                <span class="type-tag" :style="getTypeStyle(currentDefect.defectType)">
                  {{ currentDefect.defectType }}
                </span>
              </span>
            </div>
            <div class="detail-row">
              <span class="label">严重程度:</span>
              <span class="value">
                <span :class="['severity-badge', currentDefect.severity]">
                  {{ currentDefect.severity }}
                </span>
              </span>
            </div>
            <div class="detail-row">
              <span class="label">当前状态:</span>
              <span class="value">
                <span :class="['status-badge', currentDefect.status]">
                  {{ currentDefect.status }}
                </span>
              </span>
            </div>
            <div class="detail-row">
              <span class="label">详细描述:</span>
              <span class="value">{{ currentDefect.description }}</span>
            </div>
          </div>
        </div>

        <!-- 检测参数 -->
        <div class="detail-section" v-if="currentDefect.parameters && currentDefect.parameters.length > 0">
          <h3>
            <i class="el-icon-document"></i>
            检测参数
          </h3>
          <div class="params-grid">
            <div 
              v-for="param in currentDefect.parameters" 
              :key="param.name"
              class="param-item"
            >
              <span class="param-label">{{ param.name }}</span>
              <div class="param-value" :class="{ warning: !param.inRange }">
                {{ param.value }}{{ param.unit }}
                <span class="standard">(标准: {{ param.standard }})</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 区块链溯源信息 -->
        <div class="detail-section">
          <h3>
            <i class="el-icon-connection"></i>
            区块链溯源
          </h3>
          <div class="trace-timeline">
            <div 
              v-for="(node, index) in getBlockchainTrace(currentDefect)" 
              :key="index"
              class="trace-node"
            >
              <div class="node-header">
                <div class="node-index">{{ index + 1 }}</div>
                <div class="node-title">{{ node.title }}</div>
                <div class="node-time">{{ node.time }}</div>
              </div>
              <div class="node-details">
                <div class="node-detail" v-for="detail in node.details" :key="detail.label">
                  <span class="detail-label">{{ detail.label }}:</span>
                  <span class="detail-value">{{ detail.value }}</span>
                </div>
              </div>
              <div class="node-hash">
                <span class="hash-label">区块哈希:</span>
                <span class="hash-value">{{ node.hash }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作记录 -->
        <div class="detail-section" v-if="currentDefect.history && currentDefect.history.length > 0">
          <h3>
            <i class="el-icon-time"></i>
            操作记录
          </h3>
          <div class="operation-records">
            <div 
              v-for="record in currentDefect.history" 
              :key="record.time"
              class="record-item"
            >
              <div class="record-time">{{ record.time }}</div>
              <div class="record-action">{{ record.action }}</div>
              <div class="record-operator">{{ record.operator }}</div>
              <div class="record-hash">{{ record.hash || generateRecordHash(record) }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="blockchainDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentDefect.status === '待处理'"
          type="primary"
          icon="el-icon-edit"
          @click="handleProcess(currentDefect)"
        >
          开始处理
        </el-button>
        <el-button
          v-else-if="currentDefect.status === '处理中'"
          type="success"
          icon="el-icon-check"
          @click="handleResolve(currentDefect)"
        >
          标记解决
        </el-button>
        <el-button type="info" icon="el-icon-link" @click="viewFullTrace">
          查看完整追溯链
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'SimpleInspectionReport',
  data() {
    return {
      // 当前时间
      currentTime: '',
      
      // 筛选条件
      searchQuery: '',
      workshopFilter: '',
      statusFilter: '',
      severityFilter: '',
      workshopOptions: [
        { label: '全部', value: '', icon: 'el-icon-s-home' },
        { label: '一车间', value: '一车间', icon: 'el-icon-office-building' },
        { label: '二车间', value: '二车间', icon: 'el-icon-office-building' },
        { label: '三车间', value: '三车间', icon: 'el-icon-office-building' },
        { label: '四车间', value: '四车间', icon: 'el-icon-office-building' },
      ],
      statusOptions: [
        { label: '待处理', value: '待处理' },
        { label: '处理中', value: '处理中' },
        { label: '已解决', value: '已解决' },
      ],
      severityOptions: [
        { label: '轻微', value: '轻微' },
        { label: '中等', value: '中等' },
        { label: '严重', value: '严重' },
      ],
      
      // 分页
      currentPage: 1,
      pageSize: 20,
      totalFilteredDefects: 0,
      
      // 对话框
      blockchainDialogVisible: false,
      currentDefect: {},
      
      // 缺陷数据
      defectData: [],
      
      // 缺陷类型和颜色映射
      defectTypeColors: {
        '内部裂纹': { background: '#3498db', color: 'white' },
        '表面裂纹': { background: '#e74c3c', color: 'white' },
        '尺寸超差': { background: '#f39c12', color: 'white' },
        '硬度偏低': { background: '#9b59b6', color: 'white' },
        '位置偏移': { background: '#1abc9c', color: 'white' },
        '粗糙度超标': { background: '#34495e', color: 'white' },
        '变形超限': { background: '#d35400', color: 'white' },
        '配合间隙': { background: '#27ae60', color: 'white' },
        '材料缺陷': { background: '#7f8c8d', color: 'white' },
        '热处理缺陷': { background: '#c0392b', color: 'white' },
      },
      
      // 设备信息
      equipmentList: [
        'CNC-001', 'CNC-002', 'CNC-003', 'CNC-004', 'CNC-005',
        'MILL-001', 'MILL-002', 'MILL-003',
        'LATHE-001', 'LATHE-002',
        'GRIND-001', 'GRIND-002',
        'DRILL-001', 'DRILL-002',
      ],
      
      // 操作员
      operators: ['张三', '李四', '王五', '赵六', '钱七'],
      
      // 整体状态
      overallStatus: 'warning',
    }
  },
  computed: {
    // 过滤后的缺陷列表
    filteredDefects() {
      let filtered = this.defectData;
      
      // 搜索过滤
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(defect => 
          defect.sn.toLowerCase().includes(query) ||
          defect.defectType.toLowerCase().includes(query) ||
          defect.description.toLowerCase().includes(query) ||
          defect.workshop.toLowerCase().includes(query) ||
          defect.equipment.toLowerCase().includes(query)
        );
      }
      
      // 车间过滤
      if (this.workshopFilter) {
        filtered = filtered.filter(defect => defect.workshop === this.workshopFilter);
      }
      
      // 状态过滤
      if (this.statusFilter) {
        filtered = filtered.filter(defect => defect.status === this.statusFilter);
      }
      
      // 严重程度过滤
      if (this.severityFilter) {
        filtered = filtered.filter(defect => defect.severity === this.severityFilter);
      }
      
      this.totalFilteredDefects = filtered.length;
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return filtered.slice(start, end);
    },
    
    // 状态图标
    statusIcon() {
      switch(this.overallStatus) {
        case 'error': return 'el-icon-close';
        case 'warning': return 'el-icon-warning';
        case 'success': return 'el-icon-success';
        default: return 'el-icon-info';
      }
    },
    
    // 状态文本
    statusText() {
      switch(this.overallStatus) {
        case 'error': return '系统告警';
        case 'warning': return '监控正常';
        case 'success': return '全部正常';
        default: return '未知状态';
      }
    },
    
    // 统计数量
    totalDefects() {
      return this.defectData.length;
    },
    
    criticalCount() {
      return this.defectData.filter(d => d.severity === '严重').length;
    },
    
    pendingCount() {
      return this.defectData.filter(d => d.status === '待处理').length;
    },
    
    resolvedCount() {
      return this.defectData.filter(d => d.status === '已解决').length;
    }
  },
  mounted() {
    this.updateTime();
    this.generateInitialData();
    this.startRealTimeUpdates();
    this.updateOverallStatus();
  },
  methods: {
    // 更新时间
    updateTime() {
      const now = new Date();
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      });
    },
    
    // 生成初始数据
    generateInitialData() {
      const defects = [];
      const now = new Date();
      
      // 生成120条初始数据
      for (let i = 0; i < 120; i++) {
        const date = new Date(now.getTime() - Math.random() * 7 * 24 * 60 * 60 * 1000);
        const defect = this.generateRandomDefect(date, i);
        defects.push(defect);
      }
      
      // 按时间倒序排列
      defects.sort((a, b) => b.timestamp - a.timestamp);
      this.defectData = defects;
    },
    
    // 生成随机缺陷数据
    generateRandomDefect(timestamp, index) {
      const date = timestamp.toISOString().split('T')[0];
      const time = timestamp.toLocaleTimeString('zh-CN', { hour12: false });
      
      // 生成SN编号：SN-数字数字-数字数字
      const sn1 = Math.floor(Math.random() * 100).toString().padStart(2, '0');
      const sn2 = Math.floor(Math.random() * 100).toString().padStart(2, '0');
      const sn = `SN-${sn1}-${sn2}`;
      
      // 缺陷类型
      const defectTypes = Object.keys(this.defectTypeColors);
      const defectType = defectTypes[Math.floor(Math.random() * defectTypes.length)];
      
      // 严重程度
      const severities = ['轻微', '中等', '严重'];
      const severityWeights = [0.5, 0.3, 0.2];
      const severity = this.getWeightedRandom(severities, severityWeights);
      
      // 状态
      const statusOptions = ['待处理', '处理中', '已解决'];
      const status = severity === '严重' ? 
        (Math.random() > 0.3 ? '待处理' : '处理中') : 
        statusOptions[Math.floor(Math.random() * statusOptions.length)];
      
      // 车间
      const workshops = ['一车间', '二车间', '三车间', '四车间'];
      const workshop = workshops[Math.floor(Math.random() * workshops.length)];
      
      // 生成检测参数
      const parameters = this.generateRandomParameters(defectType);
      
      // 区块链状态
      const blockchainStatus = Math.random() > 0.9 ? 'pending' : 'verified';
      
      // 响应时间
      const responseTime = status === '已解决' ? 
        `${Math.floor(Math.random() * 120) + 10}分钟` : '';
      
      return {
        id: index + 1,
        sn,
        date,
        time,
        timestamp: timestamp.getTime(),
        defectType,
        description: this.generateDefectDescription(defectType),
        severity,
        status,
        workshop,
        equipment: this.equipmentList[Math.floor(Math.random() * this.equipmentList.length)],
        operator: this.operators[Math.floor(Math.random() * this.operators.length)],
        parameters,
        blockchainStatus,
        responseTime,
        history: this.generateRandomHistory(status, timestamp),
      };
    },
    
    // 生成缺陷描述
    generateDefectDescription(defectType) {
      const descriptions = {
        '内部裂纹': '超声波探伤发现内部裂纹，深度约2.5mm，位于轴心部位',
        '表面裂纹': '表面出现纵向微裂纹，长度约15mm，需要立即处理',
        '尺寸超差': '直径超差+0.25mm，超出公差范围±0.15mm',
        '硬度偏低': '硬度值HRC58，不符合要求HRC60-65',
        '位置偏移': '钻孔位置偏移0.3mm，超出允许范围',
        '粗糙度超标': '表面粗糙度Ra值3.2μm，超出标准2.5μm',
        '变形超限': '工件变形量0.15mm，超出允许范围0.1mm',
        '配合间隙': '轴承配合间隙过大，达到0.05mm',
        '材料缺陷': '材料内部存在夹杂物，影响强度',
        '热处理缺陷': '淬火硬度不均匀，存在软点',
      };
      
      return descriptions[defectType] || '发现产品质量缺陷，需要进一步检测确认';
    },
    
    // 生成随机检测参数
    generateRandomParameters(defectType) {
      const parameters = [];
      
      switch(defectType) {
        case '内部裂纹':
          parameters.push(
            { name: '裂纹深度', value: (Math.random() * 5).toFixed(2), unit: 'mm', standard: '≤1.0mm', inRange: Math.random() > 0.3 },
            { name: '裂纹长度', value: (Math.random() * 20).toFixed(1), unit: 'mm', standard: '≤10.0mm', inRange: Math.random() > 0.4 }
          );
          break;
        case '尺寸超差':
          parameters.push(
            { name: '直径偏差', value: (Math.random() * 0.5 - 0.25).toFixed(3), unit: 'mm', standard: '±0.15mm', inRange: Math.random() > 0.25 },
            { name: '圆度误差', value: (Math.random() * 0.1).toFixed(3), unit: 'mm', standard: '≤0.05mm', inRange: Math.random() > 0.35 }
          );
          break;
        case '硬度偏低':
          parameters.push(
            { name: '硬度值', value: Math.floor(Math.random() * 10 + 55), unit: 'HRC', standard: '60-65HRC', inRange: Math.random() > 0.2 },
            { name: '硬度均匀性', value: (Math.random() * 5).toFixed(1), unit: 'HRC', standard: '≤3HRC', inRange: Math.random() > 0.3 }
          );
          break;
        default:
          parameters.push(
            { name: '检测值', value: (Math.random() * 100).toFixed(2), unit: '单位', standard: '50-80单位', inRange: Math.random() > 0.3 },
            { name: '误差范围', value: (Math.random() * 10).toFixed(2), unit: '%', standard: '≤5%', inRange: Math.random() > 0.4 }
          );
      }
      
      return parameters;
    },
    
    // 生成随机处理记录
    generateRandomHistory(status, timestamp) {
      const history = [];
      let currentTime = new Date(timestamp);
      
      // 总是有检测记录
      history.push({
        time: currentTime.toLocaleTimeString('zh-CN', { hour12: false }),
        action: '缺陷检测',
        operator: '质量检测系统',
        hash: this.generateHash(currentTime.getTime() + 'detect')
      });
      
      // 根据状态添加其他记录
      if (status === '处理中' || status === '已解决') {
        currentTime.setMinutes(currentTime.getMinutes() + Math.floor(Math.random() * 30 + 5));
        history.push({
          time: currentTime.toLocaleTimeString('zh-CN', { hour12: false }),
          action: '人工确认',
          operator: this.operators[Math.floor(Math.random() * this.operators.length)],
          hash: this.generateHash(currentTime.getTime() + 'confirm')
        });
        
        currentTime.setMinutes(currentTime.getMinutes() + Math.floor(Math.random() * 45 + 15));
        history.push({
          time: currentTime.toLocaleTimeString('zh-CN', { hour12: false }),
          action: '开始处理',
          operator: this.operators[Math.floor(Math.random() * this.operators.length)],
          hash: this.generateHash(currentTime.getTime() + 'process')
        });
      }
      
      if (status === '已解决') {
        currentTime.setMinutes(currentTime.getMinutes() + Math.floor(Math.random() * 120 + 60));
        history.push({
          time: currentTime.toLocaleTimeString('zh-CN', { hour12: false }),
          action: '完成修复',
          operator: this.operators[Math.floor(Math.random() * this.operators.length)],
          hash: this.generateHash(currentTime.getTime() + 'resolve')
        });
      }
      
      return history;
    },
    
    // 加权随机选择
    getWeightedRandom(options, weights) {
      const totalWeight = weights.reduce((a, b) => a + b, 0);
      let random = Math.random() * totalWeight;
      
      for (let i = 0; i < options.length; i++) {
        if (random < weights[i]) {
          return options[i];
        }
        random -= weights[i];
      }
      
      return options[options.length - 1];
    },
    
    // 生成哈希值
    generateHash(str) {
      let hash = 0;
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash;
      }
      return '0x' + Math.abs(hash).toString(16).substring(0, 16).padStart(16, '0');
    },
    
    // 开始实时更新
    startRealTimeUpdates() {
      // 每10秒添加新缺陷
      setInterval(() => {
        this.addNewDefect();
      }, 10000);
      
      // 每30秒更新整体状态
      setInterval(() => {
        this.updateOverallStatus();
      }, 30000);
    },
    
    // 添加新缺陷
    addNewDefect() {
      const now = new Date();
      const newDefect = this.generateRandomDefect(now, this.defectData.length);
      
      // 添加到开头
      this.defectData.unshift(newDefect);
      
      // 限制数据量
      if (this.defectData.length > 500) {
        this.defectData = this.defectData.slice(0, 500);
      }
      
      // 更新状态
      this.updateOverallStatus();
    },
    
    // 更新整体状态
    updateOverallStatus() {
      const criticalCount = this.defectData.filter(d => d.severity === '严重' && d.status !== '已解决').length;
      const totalCount = this.defectData.filter(d => d.status !== '已解决').length;
      
      if (criticalCount > 5) {
        this.overallStatus = 'error';
      } else if (totalCount > 20) {
        this.overallStatus = 'warning';
      } else {
        this.overallStatus = 'success';
      }
    },
    
    // 获取行样式类
    getRowClass(defect) {
      if (defect.severity === '严重') return 'severity-critical';
      if (defect.severity === '中等') return 'severity-medium';
      if (defect.status === '待处理') return 'status-pending';
      if (defect.status === '处理中') return 'status-processing';
      return '';
    },
    
    // 切换车间筛选
    toggleWorkshopFilter(workshop) {
      this.workshopFilter = this.workshopFilter === workshop ? '' : workshop;
      this.currentPage = 1;
    },
    
    // 获取类型样式
    getTypeStyle(defectType) {
      const style = this.defectTypeColors[defectType] || { background: '#3498db', color: 'white' };
      return {
        backgroundColor: style.background,
        color: style.color,
        borderColor: style.background
      };
    },
    
    // 生成区块哈希
    generateBlockHash(defect) {
      const str = `${defect.sn}-${defect.timestamp}-${defect.workshop}-${defect.equipment}`;
      return this.generateHash(str);
    },
    
    // 获取前一区块哈希
    getPreviousBlockHash(defect) {
      const index = this.defectData.findIndex(d => d.id === defect.id);
      if (index < this.defectData.length - 1) {
        return this.generateBlockHash(this.defectData[index + 1]);
      }
      return '0x0000000000000000'; // 创世区块
    },
    
    // 生成记录哈希
    generateRecordHash(record) {
      const str = `${record.time}-${record.action}-${record.operator}`;
      return this.generateHash(str);
    },
    
    // 获取区块链溯源信息
    getBlockchainTrace(defect) {
      const trace = [
        {
          title: '生产开始',
          time: this.getEarlierTime(defect.timestamp, 60),
          details: [
            { label: '生产批次', value: 'BATCH-' + Math.floor(Math.random() * 10000) },
            { label: '原材料编号', value: 'MAT-' + Math.floor(Math.random() * 1000) },
            { label: '生产车间', value: defect.workshop }
          ],
          hash: this.generateHash(defect.sn + '-production-start')
        },
        {
          title: '加工完成',
          time: this.getEarlierTime(defect.timestamp, 30),
          details: [
            { label: '设备编号', value: defect.equipment },
            { label: '操作员', value: this.operators[Math.floor(Math.random() * this.operators.length)] },
            { label: '加工参数', value: '标准工艺' }
          ],
          hash: this.generateHash(defect.sn + '-processing-complete')
        },
        {
          title: '质量检测',
          time: `${defect.date} ${defect.time}`,
          details: [
            { label: '检测设备', value: 'QA-' + Math.floor(Math.random() * 100) },
            { label: '检测员', value: defect.operator },
            { label: '缺陷类型', value: defect.defectType }
          ],
          hash: this.generateBlockHash(defect)
        }
      ];
      
      if (defect.status === '已解决' && defect.history) {
        const resolveRecord = defect.history.find(r => r.action === '完成修复');
        if (resolveRecord) {
          trace.push({
            title: '修复完成',
            time: resolveRecord.time,
            details: [
              { label: '修复方法', value: '重新加工' },
              { label: '修复人员', value: resolveRecord.operator },
              { label: '最终状态', value: '合格' }
            ],
            hash: resolveRecord.hash
          });
        }
      }
      
      return trace;
    },
    
    // 获取更早的时间
    getEarlierTime(timestamp, minutes) {
      const time = new Date(timestamp - minutes * 60 * 1000);
      return time.toLocaleTimeString('zh-CN', { hour12: false });
    },
    
    // 查看区块链详情
    viewBlockchainDetails(defect) {
      this.currentDefect = { ...defect };
      this.blockchainDialogVisible = true;
    },
    
    // 查看完整追溯链
    viewFullTrace() {
      this.$message.info('正在生成完整追溯链...');
    },
    
    // 开始处理
    handleProcess(defect) {
      this.$confirm('确定开始处理此缺陷吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = this.defectData.findIndex(d => d.id === defect.id);
        if (index !== -1) {
          this.defectData[index].status = '处理中';
          this.defectData[index].responseTime = '处理中';
          
          // 添加处理记录
          const now = new Date();
          const record = {
            time: now.toLocaleTimeString('zh-CN', { hour12: false }),
            action: '开始处理',
            operator: '当前用户',
            hash: this.generateHash(now.getTime() + 'start-process')
          };
          
          if (!this.defectData[index].history) {
            this.defectData[index].history = [];
          }
          this.defectData[index].history.push(record);
          
          // 更新当前缺陷
          if (this.currentDefect.id === defect.id) {
            this.currentDefect.status = '处理中';
            this.currentDefect.responseTime = '处理中';
            if (!this.currentDefect.history) {
              this.currentDefect.history = [];
            }
            this.currentDefect.history.push(record);
          }
          
          this.$message.success('已开始处理缺陷');
          this.updateOverallStatus();
        }
      });
    },
    
    // 标记解决
    handleResolve(defect) {
      this.$confirm('确认此缺陷已解决吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        const index = this.defectData.findIndex(d => d.id === defect.id);
        if (index !== -1) {
          this.defectData[index].status = '已解决';
          this.defectData[index].responseTime = `${Math.floor(Math.random() * 120) + 10}分钟`;
          
          // 添加解决记录
          const now = new Date();
          const record = {
            time: now.toLocaleTimeString('zh-CN', { hour12: false }),
            action: '完成修复',
            operator: '当前用户',
            hash: this.generateHash(now.getTime() + 'complete-resolve')
          };
          
          if (!this.defectData[index].history) {
            this.defectData[index].history = [];
          }
          this.defectData[index].history.push(record);
          
          // 更新当前缺陷
          if (this.currentDefect.id === defect.id) {
            this.currentDefect.status = '已解决';
            this.currentDefect.responseTime = `${Math.floor(Math.random() * 120) + 10}分钟`;
            if (!this.currentDefect.history) {
              this.currentDefect.history = [];
            }
            this.currentDefect.history.push(record);
          }
          
          this.$message.success('缺陷已标记为解决');
          this.updateOverallStatus();
        }
      });
    },
    
    // 清除筛选条件
    clearFilters() {
      this.searchQuery = '';
      this.workshopFilter = '';
      this.statusFilter = '';
      this.severityFilter = '';
      this.currentPage = 1;
      this.$message.info('已清除所有筛选条件');
    },
    
    // 处理搜索
    handleSearch() {
      this.currentPage = 1;
    },
    
    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
    },
    
    handleCurrentChange(val) {
      this.currentPage = val;
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.simple-inspection-report {
  padding: 20px;
  min-height: 100vh;
  background: #f8fafc;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
  color: #333;
}

/* 问题列表 */
.defect-list-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  flex: 1;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #3498db;
}

/* 快速统计 */
.quick-stats {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  min-width: 140px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-item.total {
  border-left: 4px solid #3498db;
}

.stat-item.critical {
  border-left: 4px solid #e74c3c;
}

.stat-item.pending {
  border-left: 4px solid #f39c12;
}

.stat-item.resolved {
  border-left: 4px solid #27ae60;
}

.stat-icon {
  font-size: 20px;
  padding: 10px;
  border-radius: 8px;
}

.stat-item.total .stat-icon {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.stat-item.critical .stat-icon {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.stat-item.pending .stat-icon {
  background: rgba(243, 156, 18, 0.1);
  color: #f39c12;
}

.stat-item.resolved .stat-icon {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.stat-text {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #7f8c8d;
  margin-top: 4px;
}

/* 筛选控件行 */
.filter-controls-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  margin-bottom: 20px;
}

.left-filters {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 1;
}

.right-filters {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 车间筛选器 - 修改后的样式 */
.workshop-filter {
  display: flex;
  align-items: center;
  gap: 12px;
}

.workshop-filter .filter-label {
  font-size: 14px;
  color: #495057;
  font-weight: 600;
  white-space: nowrap;
}

.workshop-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.workshop-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s ease;
  color: #495057;
  min-width: 80px;
  justify-content: center;
}

.workshop-btn:hover {
  background: #e9ecef;
  border-color: #ced4da;
  transform: translateY(-1px);
}

.workshop-btn.active {
  background: #e3f2fd;
  border-color: #3498db;
  color: #3498db;
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.2);
}

.workshop-btn i {
  font-size: 16px;
}

.btn-text {
  white-space: nowrap;
}

/* 通用筛选器样式 */
.status-filter,
.severity-filter {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #495057;
  font-weight: 600;
  white-space: nowrap;
}

.search-box {
  display: flex;
  align-items: center;
}

/* 问题列表表格 */
.defect-table {
  width: 100%;
  overflow-x: auto;
  margin-top: 20px;
}

.defect-table table {
  width: 100%;
  border-collapse: collapse;
}

.defect-table thead {
  background: #f8f9fa;
}

.defect-table th {
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #e9ecef;
  white-space: nowrap;
  position: sticky;
  top: 0;
  background: #f8f9fa;
  z-index: 10;
}

.defect-table tbody tr {
  border-bottom: 1px solid #e9ecef;
  background-color: white;
  cursor: pointer;
  transition: all 0.2s ease;
}

.defect-table tbody tr:hover {
  background-color: #f1f8ff !important;
  transform: translateX(4px);
}

/* 行样式类 */
.defect-row.severity-critical {
  background: linear-gradient(90deg, rgba(231, 76, 60, 0.05) 0%, rgba(255, 255, 255, 1) 10%);
}

.defect-row.severity-medium {
  background: linear-gradient(90deg, rgba(243, 156, 18, 0.05) 0%, rgba(255, 255, 255, 1) 10%);
}

.defect-row.status-pending {
  background: linear-gradient(90deg, rgba(243, 156, 18, 0.05) 0%, rgba(255, 255, 255, 1) 10%);
}

.defect-row.status-processing {
  background: linear-gradient(90deg, rgba(52, 152, 219, 0.05) 0%, rgba(255, 255, 255, 1) 10%);
}

.defect-table td {
  padding: 16px;
  vertical-align: top;
}

/* 单元格样式 */
.time-cell .time-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.time-cell .date {
  font-family: 'Consolas', monospace;
  font-size: 13px;
  color: #7f8c8d;
  font-weight: 500;
}

.time-cell .time {
  font-family: 'Consolas', monospace;
  font-size: 12px;
  color: #95a5a6;
}

.time-cell .sn {
  font-family: 'Consolas', monospace;
  font-size: 11px;
  color: #3498db;
  font-weight: 600;
  background: rgba(52, 152, 219, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
  margin-top: 4px;
}

.process-cell .workshop-info,
.process-cell .equipment-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.process-cell .workshop-info {
  font-weight: 600;
  color: #3498db;
}

.process-cell .equipment-info {
  font-size: 13px;
  color: #7f8c8d;
}

.process-cell i {
  font-size: 14px;
}

.description-cell .defect-description {
  max-width: 300px;
}

.description-cell .defect-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 6px;
  font-size: 14px;
}

.description-cell .defect-desc {
  font-size: 13px;
  color: #495057;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 标签样式 */
.type-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
  border: 1px solid;
}

.severity-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.severity-badge.轻微 {
  background: #e8f5e8;
  color: #27ae60;
  border: 1px solid #c8e6c9;
}

.severity-badge.中等 {
  background: #fff4e6;
  color: #f39c12;
  border: 1px solid #ffd8b3;
}

.severity-badge.严重 {
  background: #ffeaea;
  color: #e74c3c;
  border: 1px solid #ffcccc;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 4px;
}

.status-badge.待处理 {
  background: #fff4e6;
  color: #f39c12;
  border: 1px solid #ffd8b3;
}

.status-badge.处理中 {
  background: #e3f2fd;
  color: #3498db;
  border: 1px solid #bbdefb;
}

.status-badge.已解决 {
  background: #e8f5e8;
  color: #27ae60;
  border: 1px solid #c8e6c9;
}

.status-cell .response-time {
  font-size: 11px;
  color: #7f8c8d;
  font-family: 'Consolas', monospace;
  margin-top: 2px;
}

/* 操作单元格 */
.action-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.detail-btn {
  padding: 6px 16px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  width: 100%;
  justify-content: center;
}

.detail-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.quick-actions .el-button {
  padding: 4px;
  border-radius: 50%;
  width: 24px;
  height: 24px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px !important;
}

.empty-state .empty-content {
  color: #95a5a6;
  font-size: 16px;
}

.empty-state .empty-content i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
  color: #bdc3c7;
}

.empty-state .empty-content p {
  margin-bottom: 12px;
}

/* 分页容器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 区块链详情对话框 */
.blockchain-dialog :deep(.el-dialog) {
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
}

.blockchain-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e9ecef;
  padding: 20px 24px;
}

.blockchain-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.blockchain-dialog :deep(.el-dialog__body) {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.blockchain-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 区块链头部 */
.blockchain-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.blockchain-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label {
  font-weight: 600;
  color: #495057;
  min-width: 80px;
}

.info-item .value {
  color: #2c3e50;
  font-family: 'Consolas', monospace;
}

.info-item .sn-value {
  color: #3498db;
  font-weight: 600;
}

.info-item .hash {
  font-size: 12px;
  color: #7f8c8d;
  word-break: break-all;
}

.blockchain-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}

.status.verified {
  background: #e8f5e8;
  color: #27ae60;
  border: 1px solid #c8e6c9;
}

.status.pending {
  background: #fff4e6;
  color: #f39c12;
  border: 1px solid #ffd8b3;
}

/* 详情区块 */
.detail-section {
  padding: 20px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.detail-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #e9ecef;
}

.detail-section h3 i {
  color: #3498db;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item .label {
  font-size: 13px;
  color: #7f8c8d;
  margin-bottom: 4px;
}

.info-item .value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 600;
}

/* 缺陷详情 */
.defect-details {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.detail-row .label {
  font-weight: 600;
  color: #495057;
  min-width: 80px;
}

.detail-row .value {
  flex: 1;
  color: #2c3e50;
}

.params-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.param-label {
  font-size: 13px;
  color: #7f8c8d;
}

.param-value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 600;
}

.param-value.warning {
  color: #e74c3c;
}

.param-value .standard {
  font-size: 12px;
  color: #95a5a6;
  margin-left: 8px;
}

/* 区块链溯源信息 */
.trace-timeline {
  position: relative;
  padding-left: 30px;
}

.trace-timeline::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, #3498db, #2ecc71);
}

.trace-node {
  position: relative;
  margin-bottom: 20px;
}

.trace-node:last-child {
  margin-bottom: 0;
}

.trace-node::before {
  content: '';
  position: absolute;
  left: -23px;
  top: 8px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: white;
  border: 3px solid #3498db;
  z-index: 1;
}

.node-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.node-index {
  width: 24px;
  height: 24px;
  background: #3498db;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.node-title {
  font-weight: 600;
  color: #2c3e50;
  flex: 1;
}

.node-time {
  font-size: 12px;
  color: #7f8c8d;
}

.node-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.node-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.detail-label {
  font-size: 12px;
  color: #7f8c8d;
}

.detail-value {
  font-size: 13px;
  color: #2c3e50;
  font-weight: 600;
}

.node-hash {
  padding: 8px 12px;
  background: #e9ecef;
  border-radius: 4px;
  font-family: 'Consolas', monospace;
  font-size: 12px;
  color: #495057;
}

.hash-label {
  font-weight: 600;
  margin-right: 8px;
}

.hash-value {
  word-break: break-all;
  color: #7f8c8d;
}

/* 操作记录 */
.operation-records {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-item {
  display: grid;
  grid-template-columns: 180px 1fr 120px auto;
  gap: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  align-items: center;
}

.record-time {
  font-size: 13px;
  color: #7f8c8d;
  font-family: 'Consolas', monospace;
}

.record-action {
  color: #2c3e50;
  font-weight: 600;
}

.record-operator {
  color: #3498db;
  font-weight: 600;
}

.record-hash {
  font-size: 11px;
  color: #95a5a6;
  font-family: 'Consolas', monospace;
  word-break: break-all;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-thumb {
  background: #bdc3c7;
  border-radius: 3px;
}

::-webkit-scrollbar-track {
  background: #f1f2f6;
}

/* 对话框页脚 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e9ecef;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .workshop-filter-container {
    width: 400px;
  }
}

@media (max-width: 992px) {
  .section-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .workshop-filter-container {
    width: 100%;
    margin-left: 0;
  }
  
  .workshop-filter {
    width: 100%;
  }
  
  .filter-controls-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .right-filters {
    width: 100%;
    justify-content: flex-start;
  }
  
  .left-filters {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-box .el-input {
    width: 100% !important;
  }
  
  .workshop-buttons {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .workshop-buttons {
    width: 100%;
    overflow-x: auto;
    padding-bottom: 8px;
  }
  
  .defect-table {
    overflow-x: auto;
  }
  
  .defect-table table {
    min-width: 800px;
  }
  
  .info-grid,
  .params-grid {
    grid-template-columns: 1fr;
  }
  
  .record-item {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .quick-stats {
    flex-wrap: wrap;
  }
  
  .stat-item {
    min-width: calc(50% - 8px);
  }
  
  .left-filters {
    width: 100%;
  }
}

@media (max-width: 576px) {
  .filter-controls-row {
    padding: 12px;
  }
  
  .left-filters,
  .right-filters {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    width: 100%;
  }
  
  .status-filter,
  .severity-filter,
  .workshop-filter {
    width: 100%;
    justify-content: space-between;
  }
  
  .workshop-btn {
    padding: 6px 12px;
    font-size: 12px;
    min-width: 70px;
  }
  
  .btn-text {
    font-size: 12px;
  }
  
  .stat-item {
    min-width: 100%;
  }
  
  .header-left h1 {
    font-size: 24px;
  }
}
</style>