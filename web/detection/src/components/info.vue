<template>
  <div class="ai-inspection-system">
    <!-- 顶部状态栏 -->
    <div class="top-bar">
      <div class="system-info">
        <div class="logo">
          <i class="el-icon-s-platform"></i>
          <span class="system-name">车间数据大屏</span>
        </div>
        <div class="current-time">{{ currentTime }}</div>
      </div>
      <div class="system-status">
        <span class="status-dot active"></span>
        <span class="status-text">系统运行正常</span>
      
      </div>
    </div>

    <div class="main-container">
      <!-- 左侧面板 -->
      <div class="left-panel">
        <!-- 车间概览 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-shop"></i>
            <span>车间概览</span>
            <el-button 
              size="mini" 
              type="text" 
              @click="resetWorkshopData"
              class="reset-btn"
            >
              <i class="el-icon-refresh-left"></i>
              重置数据
            </el-button>
          </div>
          <div class="workshop-list">
            <div 
              v-for="workshop in workshops" 
              :key="workshop.id"
              class="workshop-item"
              :class="{ active: activeWorkshop === workshop.id }"
              @click="switchWorkshop(workshop.id)"
            >
              <div class="workshop-icon">
                <i :class="workshop.icon"></i>
              </div>
              <div class="workshop-details">
                <div class="workshop-name">{{ workshop.name }}</div>
                <div class="workshop-status">
                  <span class="status-badge" :class="getStatusClass(workshop.status)">
                    {{ workshop.status }}
                  </span>
                  <span class="efficiency">{{ workshop.efficiency }}%</span>
                </div>
                <div class="workshop-process">{{ workshop.process }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 生产效率趋势 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-data"></i>
            <span>生产效率趋势</span>
          </div>
          <div class="chart-container">
            <div id="efficiencyChart" class="chart"></div>
          </div>
        </div>
      </div>

      <!-- 中间主视图 -->
      <div class="center-panel">
        <!-- 车间工序监控 -->
        <div class="workshop-monitor">
          <div class="monitor-header">
            <div class="workshop-title">
              <i class="el-icon-s-shop"></i>
              <span>{{ currentWorkshop.name }} - 工序数据监控</span>
              <span class="workshop-id">(ID: WS-{{ currentWorkshop.id.toString().padStart(3, '0') }})</span>
            </div>
            <div class="monitor-info">
              <div class="update-info">
                <i class="el-icon-refresh"></i>
                数据更新于 {{ lastUpdateTime }}
                <span class="data-version">v{{ dataVersion }}</span>
              </div>
              <div class="monitor-actions">
                <el-button 
                  size="small" 
                  type="primary"
                  @click="refreshData"
                  :loading="refreshing"
                >
                  <i class="el-icon-refresh"></i>
                  刷新数据
                </el-button>
                <el-button 
                  size="small"
                  @click="forceRefresh"
                >
                  <i class="el-icon-refresh-right"></i>
                  强制刷新
                </el-button>
              </div>
            </div>
          </div>

          <!-- 工序数据柱状图 -->
          <div class="process-bar-chart">
            <div class="chart-title">
              <i class="el-icon-s-data"></i>
              工序关键参数对比
              <span class="chart-tip">数据采集</span>
            </div>
            <div class="chart-container-large">
              <div id="processChart" class="chart-large"></div>
            </div>
          </div>

          <!-- 工序详情列表 -->
          <div class="process-detail-list">
            <div class="list-header">
              <div class="header-title">
                <i class="el-icon-s-order"></i>
                工序详细信息
                <span class="data-freshness" :class="getFreshnessClass()">
                  {{ getFreshnessText() }}
                </span>
              </div>
              <div class="header-actions">
                <span class="total-count">共 {{ currentProcesses.length }} 个工序</span>
                <span class="data-timestamp">生成时间: {{ dataTimestamp }}</span>
              </div>
            </div>
            
            <div class="process-list">
              <div 
                v-for="(process, index) in currentProcesses" 
                :key="process.id"
                class="process-detail-item"
                :class="{ active: process.status === 'running' }"
              >
                <div class="process-header">
                  <div class="process-index">
                    <div class="index-number">{{ index + 1 }}</div>
                    <div class="status-indicator" :class="process.status"></div>
                  </div>
                  <div class="process-title">
                    <div class="process-name">{{ process.name }}</div>
                    <div class="process-duration">{{ process.duration }}</div>
                  </div>
                  <div class="process-progress">
                    <div class="progress-bar">
                      <div 
                        class="progress-fill" 
                        :style="{ width: process.progress + '%' }"
                      ></div>
                    </div>
                    <span class="progress-text">{{ process.progress }}%</span>
                  </div>
                </div>
                
                <div class="process-content">
                  <div class="parameter-grid">
                    <div 
                      v-for="param in process.params" 
                      :key="param.name"
                      class="parameter-item"
                    >
                      <div class="param-label">{{ param.label }}</div>
                      <div class="param-value">
                        {{ param.value }}
                        <span class="param-unit">{{ param.unit }}</span>
                      </div>
                      <div class="param-status" :class="getParamStatus(param)">
                        <i class="el-icon-check" v-if="getParamStatus(param) === 'normal'"></i>
                        <i class="el-icon-warning" v-else></i>
                      </div>
                    </div>
                  </div>
                  
                  <div class="process-meta">
                    <div class="meta-item">
                      <i class="el-icon-s-tools"></i>
                      <span>{{ process.equipment }}</span>
                    </div>
                    <div class="meta-item">
                      <i class="el-icon-user"></i>
                      <span>{{ process.operator }}</span>
                    </div>
                    <div class="meta-item">
                      <i class="el-icon-time"></i>
                      <span>开始时间: {{ process.startTime }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="process-actions">
                  <el-button size="mini" @click="viewProcessDetail(process)">
                    <i class="el-icon-view"></i>
                    查看详情
                  </el-button>
                  <el-button 
                    size="mini" 
                    type="primary" 
                    v-if="process.status === 'running'"
                    @click="pauseProcess(process)"
                  >
                    <i class="el-icon-video-pause"></i>
                    暂停
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <!-- 质量统计扇形图 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-claim"></i>
            <span>车间质量统计</span>
            <el-button 
              size="mini" 
              type="text" 
              @click="regenerateQualityData"
              class="refresh-chart-btn"
            >
              <i class="el-icon-refresh"></i>
              刷新质量数据
            </el-button>
          </div>
          <div class="chart-container">
            <div id="qualityChart" class="chart"></div>
          </div>
          <div class="quality-summary">
            <div class="summary-item">
              <span class="label">平均合格率:</span>
              <span class="value">{{ averageQuality }}%</span>
            </div>
            <div class="summary-item">
              <span class="label">数据版本:</span>
              <span class="value">v{{ qualityDataVersion }}</span>
            </div>
            <div class="summary-item">
              <span class="label">更新时间:</span>
              <span class="value">{{ qualityUpdateTime }}</span>
            </div>
          </div>
        </div>

        <!-- 产量统计折线图 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-marketing"></i>
            <span>产量趋势分析</span>
            <el-button 
              size="mini" 
              type="text" 
              @click="regenerateProductionData"
              class="refresh-chart-btn"
            >
              <i class="el-icon-refresh"></i>
              刷新产量数据
            </el-button>
          </div>
          <div class="chart-container">
            <div id="productionChart" class="chart"></div>
          </div>
          <div class="production-summary">
            <div class="summary-item">
              <span class="label">今日预计:</span>
              <span class="value">{{ estimatedToday }} 件</span>
            </div>
            <div class="summary-item">
              <span class="label">数据版本:</span>
              <span class="value">v{{ productionDataVersion }}</span>
            </div>
          </div>
        </div>

        <!-- 设备状态 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-tools"></i>
            <span>设备状态</span>
            <el-button 
              size="mini" 
              type="text" 
              @click="refreshEquipmentStatus"
              class="refresh-chart-btn"
            >
              <i class="el-icon-refresh"></i>
              刷新设备状态
            </el-button>
          </div>
          <div class="equipment-status">
            <div 
              v-for="equip in equipmentList" 
              :key="equip.id"
              class="equipment-item"
              :class="{ warning: equip.status === 'warning', error: equip.status === 'error' }"
            >
              <div class="equip-info">
                <div class="equip-name">{{ equip.name }}</div>
                <div class="equip-desc">{{ equip.desc }}</div>
              </div>
              <div class="equip-status">
                <div class="status-dot" :class="equip.status"></div>
                <span class="status-text">{{ getStatusText(equip.status) }}</span>
              </div>
            </div>
          </div>
          <div class="equipment-summary">
            <span class="summary-text">设备正常率: {{ equipmentNormalRate }}%</span>
            <span class="summary-text">更新时间: {{ equipmentUpdateTime }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部状态栏 -->
    <div class="bottom-bar">
      <div class="status-items">
        <div class="status-item">
          <i class="el-icon-cpu"></i>
          <span>系统负荷</span>
          <span class="value">{{ systemStatus.load }}%</span>
        </div>
        <div class="status-item">
          <i class="el-icon-connection"></i>
          <span>数据采集</span>
          <span class="value">{{ systemStatus.dataCollectionRate }}%</span>
        </div>
        <div class="status-item">
          <i class="el-icon-timer"></i>
          <span>运行时长</span>
          <span class="value">{{ formatTime(systemStatus.totalRunningTime) }}</span>
        </div>
        <div class="status-item">
          <i class="el-icon-document-checked"></i>
          <span>追溯批次</span>
          <span class="value">{{ systemStatus.traceabilityBatches }}</span>
        </div>
        <div class="status-item">
          <i class="el-icon-s-opportunity"></i>
          <span>在线设备</span>
          <span class="value">{{ systemStatus.onlineEquipment }}</span>
        </div>
      </div>
      <div class="data-refresh">
        <span class="refresh-text" :class="{ refreshing: isAutoRefreshing }">
          {{ isAutoRefreshing ? '数据自动刷新中' : '数据刷新已暂停' }}
        </span>
        <span class="refresh-dot" :class="{ refreshing: isAutoRefreshing }"></span>
        <el-switch
          v-model="isAutoRefreshing"
          active-text="自动刷新"
          inactive-text="手动刷新"
          size="mini"
          @change="toggleAutoRefresh"
        ></el-switch>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import moment from 'moment';

export default {
  name: 'AIInspectionSystem',
  data() {
    return {
      // 系统时间
      currentTime: '',
      lastUpdateTime: '',
      dataTimestamp: '',
      
      // 数据版本
      dataVersion: '1.0.0',
      qualityDataVersion: '1.0.0',
      productionDataVersion: '1.0.0',
      refreshCount: 0,
      
      // 车间数据
      workshops: [],
      activeWorkshop: 1,
      
      // 各车间工序数据
      workshopProcesses: {},
      
      // 质量数据（每次刷新都会不同）
      workshopQualityData: {},
      
      // 产量数据（每次刷新都会不同）
      productionData: {
        today: [],
        thisWeek: [],
        thisMonth: []
      },
      
      // 设备状态
      equipmentList: [],
      equipmentUpdateTime: '',
      
      // 系统状态
      systemStatus: {
        load: 85,
        dataCollectionRate: 98.5,
        totalRunningTime: 4526,
        traceabilityBatches: 156,
        onlineEquipment: '24/26'
      },
      
      // 刷新状态
      refreshing: false,
      isAutoRefreshing: false, // 默认关闭自动刷新
      lastDataFreshness: moment(),
      
      // 图表实例
      efficiencyChart: null,
      processChart: null,
      qualityChart: null,
      productionChart: null,
      
      // 定时器
      timeInterval: null,
      dataRefreshInterval: null,
      
      // 其他
      qualityUpdateTime: '',
      estimatedToday: 0,
      
      // 窗口resize事件监听器
      resizeListener: null
    };
  },
  
  computed: {
    currentWorkshop() {
      return this.workshops.find(w => w.id === this.activeWorkshop) || this.workshops[0];
    },
    
    currentProcesses() {
      return this.workshopProcesses[this.activeWorkshop] || [];
    },
    
    averageQuality() {
      const values = Object.values(this.workshopQualityData).map(d => d.qualifiedRate);
      const sum = values.reduce((a, b) => a + b, 0);
      return (sum / values.length).toFixed(1);
    },
    
    equipmentNormalRate() {
      if (this.equipmentList.length === 0) return 0;
      const normalCount = this.equipmentList.filter(e => e.status === 'normal').length;
      return ((normalCount / this.equipmentList.length) * 100).toFixed(1);
    }
  },
  
  mounted() {
    this.initTime();
    this.generateFreshData();
    this.initCharts();
    this.setupResizeListener();
  },
  
  beforeDestroy() {
    this.clearTimers();
    this.clearCharts();
    this.removeResizeListener();
  },
  
  methods: {
    // 初始化时间
    initTime() {
      this.updateCurrentTime();
      this.timeInterval = setInterval(() => {
        this.updateCurrentTime();
      }, 1000);
    },
    
    updateCurrentTime() {
      this.currentTime = moment().format('YYYY-MM-DD HH:mm:ss');
    },
    
    // 生成全新数据
    generateFreshData() {
      this.refreshCount++;
      this.dataVersion = `1.0.${this.refreshCount}`;
      this.dataTimestamp = moment().format('HH:mm:ss');
      this.lastDataFreshness = moment();
      
      // 生成车间数据
      this.generateWorkshopData();
      
      // 生成工序数据
      this.generateProcessData();
      
      // 生成质量数据（每次不同）
      this.generateQualityData();
      
      // 生成产量数据（每次不同）
      this.generateProductionData();
      
      // 生成设备状态
      this.generateEquipmentData();
      
      // 更新系统状态
      this.updateSystemStatus();
      
      this.updateLastUpdateTime();
    },
    
    generateWorkshopData() {
      const efficiencyVariations = [
        [88.5, 92.1, 85.3, 95.8],
        [87.2, 93.5, 86.8, 94.2],
        [89.1, 91.8, 84.7, 96.3],
        [86.9, 92.7, 85.9, 95.1]
      ];
      
      const variationIndex = this.refreshCount % efficiencyVariations.length;
      const efficiencies = efficiencyVariations[variationIndex];
      
      this.workshops = [
        {
          id: 1,
          name: '一车间',
          process: '毛坯制造（切割下料→压花键→锻造）',
          icon: 'el-icon-s-shop',
          status: '运行中',
          efficiency: efficiencies[0]
        },
        {
          id: 2,
          name: '二车间',
          process: '粗/精加工（钻孔→热处理→车削）',
          icon: 'el-icon-s-tools',
          status: '运行中',
          efficiency: efficiencies[1]
        },
        {
          id: 3,
          name: '三车间',
          process: '热处理+探伤（淬火→探伤→精加工）',
          icon: 'el-icon-s-promotion',
          status: '运行中',
          efficiency: efficiencies[2]
        },
        {
          id: 4,
          name: '四车间',
          process: '探伤包装（喷漆→包装→入库）',
          icon: 'el-icon-s-claim',
          status: '运行中',
          efficiency: efficiencies[3]
        }
      ];
    },
    
    generateProcessData() {
      const baseValues = {
        1: [
          { name: 'cuttingSize', label: '下料尺寸', unit: 'mm', min: 280, max: 320 },
          { name: 'cuttingSpeed', label: '切割速度', unit: 'mm/min', min: 100, max: 150 },
          { name: 'temperature', label: '设备温度', unit: '℃', min: 40, max: 70 },
          { name: 'precision', label: '切割精度', unit: 'mm', min: 0.3, max: 0.8 }
        ],
        2: [
          { name: 'holeSize', label: '孔径', unit: 'mm', min: 14.5, max: 15.5 },
          { name: 'depth', label: '钻孔深度', unit: 'mm', min: 48, max: 52 },
          { name: 'speed', label: '设备转速', unit: 'rpm', min: 600, max: 1000 },
          { name: 'precision', label: '孔位精度', unit: 'mm', min: 0.005, max: 0.02 }
        ],
        3: [
          { name: 'quenchTemp', label: '淬火温度', unit: '℃', min: 900, max: 950 },
          { name: 'coolingRate', label: '冷却速度', unit: '℃/s', min: 10, max: 20 },
          { name: 'hardness', label: '表面硬度', unit: 'HRC', min: 55, max: 60 },
          { name: 'depth', label: '淬硬层深', unit: 'mm', min: 2.0, max: 3.0 }
        ],
        4: [
          { name: 'paintThickness', label: '漆膜厚度', unit: 'μm', min: 70, max: 90 },
          { name: 'sprayPressure', label: '喷涂压力', unit: 'MPa', min: 0.3, max: 0.5 },
          { name: 'dryingTime', label: '干燥时间', unit: 'min', min: 10, max: 20 },
          { name: 'paintConsumption', label: '涂料消耗', unit: 'kg', min: 1.0, max: 1.5 }
        ]
      };
      
      this.workshopProcesses = {
        1: [
          {
            id: 1,
            name: '切割下料',
            duration: '2.5分钟',
            status: 'completed',
            progress: 100,
            equipment: `数控切割机-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `张三(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 30 + 30, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[1])
          },
          {
            id: 2,
            name: '压花键',
            duration: '3分钟',
            status: 'running',
            progress: Math.floor(Math.random() * 30 + 50),
            equipment: `压花机-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `李四(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 10 + 5, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[1])
          },
          {
            id: 3,
            name: '锻造',
            duration: '5分钟',
            status: 'pending',
            progress: 0,
            equipment: `锻造机-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `王五(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: '--:--:--',
            params: this.generateRandomParams(baseValues[1])
          }
        ],
        2: [
          {
            id: 1,
            name: '钻中心孔',
            duration: '1.5分钟',
            status: 'completed',
            progress: 100,
            equipment: `钻孔机-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `赵六(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 45 + 15, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[2])
          },
          {
            id: 2,
            name: '调质热处理',
            duration: '60分钟',
            status: 'running',
            progress: Math.floor(Math.random() * 40 + 20),
            equipment: `热处理炉-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `张三(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 40 + 20, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[2])
          },
          {
            id: 3,
            name: '粗车盘',
            duration: '4分钟',
            status: 'pending',
            progress: 0,
            equipment: `数控车床-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `李四(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: '--:--:--',
            params: this.generateRandomParams(baseValues[2])
          }
        ],
        3: [
          {
            id: 1,
            name: '表面淬火',
            duration: '2分钟',
            status: 'completed',
            progress: 100,
            equipment: `淬火设备-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `王五(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 35 + 25, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[3])
          },
          {
            id: 2,
            name: '磁粉探伤',
            duration: '3分钟',
            status: 'running',
            progress: Math.floor(Math.random() * 40 + 40),
            equipment: `磁粉探伤仪-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `赵六(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 15 + 10, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[3])
          },
          {
            id: 3,
            name: '精车杆',
            duration: '5分钟',
            status: 'pending',
            progress: 0,
            equipment: `精车床-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `张三(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: '--:--:--',
            params: this.generateRandomParams(baseValues[3])
          }
        ],
        4: [
          {
            id: 1,
            name: '喷漆',
            duration: '3分钟',
            status: 'completed',
            progress: 100,
            equipment: `自动喷漆线-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `李四(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 25 + 35, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[4])
          },
          {
            id: 2,
            name: '终检',
            duration: '2分钟',
            status: 'running',
            progress: Math.floor(Math.random() * 40 + 30),
            equipment: `视觉检测仪-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `王五(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: moment().subtract(Math.random() * 10 + 5, 'minutes').format('HH:mm:ss'),
            params: this.generateRandomParams(baseValues[4])
          },
          {
            id: 3,
            name: '包装入库',
            duration: '1.5分钟',
            status: 'pending',
            progress: 0,
            equipment: `自动包装线-${String(Math.floor(Math.random() * 100)).padStart(3, '0')}`,
            operator: `赵六(ID:${String(Math.floor(Math.random() * 100)).padStart(3, '0')})`,
            startTime: '--:--:--',
            params: this.generateRandomParams(baseValues[4])
          }
        ]
      };
    },
    
    generateRandomParams(baseParams) {
      return baseParams.map(param => {
        const min = param.min;
        const max = param.max;
        const current = min + Math.random() * (max - min);
        
        let value;
        if (param.unit === 'mm' || param.unit === 'μm' || param.unit === 'MPa' || param.unit === 'kg') {
          value = current.toFixed(2);
        } else if (param.unit === '℃' || param.unit === 'rpm' || param.unit === 'HRC') {
          value = current.toFixed(0);
        } else if (param.unit === 'mm/min' || param.unit === '℃/s') {
          value = current.toFixed(1);
        } else {
          value = current.toFixed(2);
        }
        
        // 特殊处理下料尺寸
        if (param.name === 'cuttingSize') {
          const width = current;
          const height = width * 0.267;
          value = `${width.toFixed(0)}×${height.toFixed(0)}`;
        }
        
        return {
          ...param,
          current: current,
          value: value
        };
      });
    },
    
    generateQualityData() {
      this.qualityDataVersion = `1.0.${this.refreshCount}`;
      this.qualityUpdateTime = moment().format('HH:mm:ss');
      
      // 生成随机质量数据，每次刷新都不同
      const baseQualifiedRates = [92.7, 94.5, 91.2, 96.8];
      const variations = [-2, -1, 0, 1, 2];
      
      this.workshopQualityData = {
        1: {
          qualifiedRate: this.getRandomVariation(baseQualifiedRates[0], variations),
          defectiveRate: 100 - this.getRandomVariation(baseQualifiedRates[0], variations)
        },
        2: {
          qualifiedRate: this.getRandomVariation(baseQualifiedRates[1], variations),
          defectiveRate: 100 - this.getRandomVariation(baseQualifiedRates[1], variations)
        },
        3: {
          qualifiedRate: this.getRandomVariation(baseQualifiedRates[2], variations),
          defectiveRate: 100 - this.getRandomVariation(baseQualifiedRates[2], variations)
        },
        4: {
          qualifiedRate: this.getRandomVariation(baseQualifiedRates[3], variations),
          defectiveRate: 100 - this.getRandomVariation(baseQualifiedRates[3], variations)
        }
      };
    },
    
    getRandomVariation(base, variations) {
      const variation = variations[Math.floor(Math.random() * variations.length)];
      const newValue = base + variation;
      return Math.max(85, Math.min(99, newValue));
    },
    
    generateProductionData() {
      this.productionDataVersion = `1.0.${this.refreshCount}`;
      
      // 生成今日产量数据（随机变化）
      const baseToday = [820, 932, 901, 934, 1290, 1330, 1450];
      this.productionData.today = baseToday.map(value => 
        value + Math.floor(Math.random() * 100 - 50)
      );
      this.estimatedToday = Math.max(...this.productionData.today) + Math.floor(Math.random() * 200);
      
      // 生成本周产量数据
      const baseWeek = [5200, 5320, 5280, 5410, 5600, 5780, 5920];
      this.productionData.thisWeek = baseWeek.map(value => 
        value + Math.floor(Math.random() * 200 - 100)
      );
      
      // 生成本月产量数据
      const baseMonth = [];
      let current = 5000;
      for (let i = 0; i < 15; i++) {
        current += Math.floor(Math.random() * 600 + 400);
        baseMonth.push(current);
      }
      this.productionData.thisMonth = baseMonth;
    },
    
    generateEquipmentData() {
      this.equipmentUpdateTime = moment().format('HH:mm:ss');
      
      const equipmentTemplates = {
        1: [
          { name: '数控切割机', desc: '切割下料' },
          { name: '压花机', desc: '压装花键' },
          { name: '锻造机', desc: '毛坯锻造' },
          { name: '传送带', desc: '物料转运' }
        ],
        2: [
          { name: '钻孔机', desc: '钻中心孔' },
          { name: '热处理炉', desc: '调质处理' },
          { name: '数控车床', desc: '粗车盘' },
          { name: '抛丸机', desc: '表面处理' }
        ],
        3: [
          { name: '淬火设备', desc: '表面淬火' },
          { name: '磁粉探伤仪', desc: '缺陷检测' },
          { name: '精车床', desc: '精车杆' },
          { name: '超声波探伤', desc: '内部检测' }
        ],
        4: [
          { name: '自动喷漆线', desc: '表面喷漆' },
          { name: '视觉检测仪', desc: '终检设备' },
          { name: '包装线', desc: '自动包装' },
          { name: '入库机器人', desc: '成品入库' }
        ]
      };
      
      const template = equipmentTemplates[this.activeWorkshop] || equipmentTemplates[1];
      this.equipmentList = template.map((item, index) => {
        // 随机生成设备状态
        const random = Math.random();
        let status = 'normal';
        if (random < 0.1) {
          status = 'error';
        } else if (random < 0.3) {
          status = 'warning';
        }
        
        return {
          id: index + 1,
          name: item.name,
          desc: item.desc,
          status: status
        };
      });
    },
    
    updateSystemStatus() {
      this.systemStatus = {
        load: 75 + Math.floor(Math.random() * 20),
        dataCollectionRate: 95 + Math.floor(Math.random() * 5),
        totalRunningTime: this.systemStatus.totalRunningTime + 1,
        traceabilityBatches: this.systemStatus.traceabilityBatches + Math.floor(Math.random() * 3),
        onlineEquipment: `${Math.floor(Math.random() * 2 + 24)}/26`
      };
    },
    
    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        this.renderEfficiencyChart();
        this.renderProcessChart();
        this.renderQualityChart();
        this.renderProductionChart();
      });
    },
    
    renderEfficiencyChart() {
      const chartDom = document.getElementById('efficiencyChart');
      if (!chartDom) return;
      
      if (this.efficiencyChart) {
        this.efficiencyChart.dispose();
      }
      
      this.efficiencyChart = echarts.init(chartDom);
      
      const efficiencies = this.workshops.map(w => w.efficiency);
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e8e8e8',
          textStyle: { color: '#333' },
          formatter: '{b}: {c}%'
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '15%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.workshops.map(w => w.name),
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666',
            fontSize: 12
          }
        },
        yAxis: {
          type: 'value',
          name: '效率(%)',
          min: 0,
          max: 100,
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666',
            fontSize: 12,
            formatter: '{value}%'
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          }
        },
        series: [{
          data: efficiencies,
          type: 'bar',
          barWidth: '40%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#1890ff' },
              { offset: 0.5, color: '#36cfc9' },
              { offset: 1, color: '#36cfc9' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}%',
            color: '#333',
            fontWeight: 'bold',
            fontSize: 12
          }
        }]
      };
      
      this.efficiencyChart.setOption(option);
    },
    
    renderProcessChart() {
      const chartDom = document.getElementById('processChart');
      if (!chartDom) return;
      
      if (this.processChart) {
        this.processChart.dispose();
      }
      
      this.processChart = echarts.init(chartDom);
      
      const processes = this.currentProcesses;
      if (processes.length === 0) return;
      
      // 为每个工序选择一个代表性的参数进行展示
      const chartData = [];
      const categories = [];
      const seriesData = [];
      
      processes.forEach((process, index) => {
        categories.push(process.name);
        
        // 选择一个数值型参数进行展示
        const numericParam = process.params.find(p => typeof p.current === 'number' && p.unit !== '');
        if (numericParam) {
          seriesData.push({
            name: process.name,
            value: numericParam.current,
            itemStyle: {
              color: this.getChartColor(index)
            }
          });
        }
      });
      
      if (seriesData.length > 0) {
        const option = {
          backgroundColor: 'transparent',
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: '#e8e8e8',
            textStyle: { color: '#333' },
            formatter: (params) => {
              const data = params[0];
              const process = processes[params[0].dataIndex];
              const numericParam = process.params.find(p => typeof p.current === 'number' && p.unit !== '');
              return `${process.name}<br/>${numericParam.label}: ${numericParam.value}${numericParam.unit}`;
            }
          },
          grid: {
            left: '10%',
            right: '10%',
            bottom: '15%',
            top: '10%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: categories,
            axisLine: {
              lineStyle: {
                color: '#d9d9d9'
              }
            },
            axisLabel: {
              color: '#666',
              fontSize: 12,
              interval: 0,
              rotate: 0
            }
          },
          yAxis: {
            type: 'value',
            name: '参数值',
            axisLine: {
              lineStyle: {
                color: '#d9d9d9'
              }
            },
            axisLabel: {
              color: '#666',
              fontSize: 12
            },
            splitLine: {
              lineStyle: {
                color: '#f0f0f0'
              }
            }
          },
          series: [{
            name: '工序参数',
            type: 'bar',
            data: seriesData,
            barWidth: '40%',
            itemStyle: {
              borderRadius: [4, 4, 0, 0]
            },
            label: {
              show: true,
              position: 'top',
              formatter: (params) => {
                const process = processes[params.dataIndex];
                const numericParam = process.params.find(p => typeof p.current === 'number' && p.unit !== '');
                return numericParam ? `${numericParam.value}${numericParam.unit}` : '';
              },
              fontSize: 12,
              color: '#333'
            }
          }]
        };
        
        const values = seriesData.map(item => item.value);
        const maxValue = Math.max(...values);
        const minValue = Math.min(...values);
        const range = maxValue - minValue;
        
        option.yAxis.min = Math.max(0, minValue - range * 0.1);
        option.yAxis.max = maxValue + range * 0.1;
        
        if (maxValue - minValue < maxValue * 0.1) {
          option.yAxis.min = minValue * 0.5;
          option.yAxis.max = maxValue * 1.5;
        }
        
        this.processChart.setOption(option);
      }
    },
    
    renderQualityChart() {
      const chartDom = document.getElementById('qualityChart');
      if (!chartDom) return;
      
      if (this.qualityChart) {
        this.qualityChart.dispose();
      }
      
      this.qualityChart = echarts.init(chartDom);
      
      const data = [
        { 
          value: this.workshopQualityData[1].qualifiedRate, 
          name: '一车间', 
          itemStyle: { color: '#1890ff' } 
        },
        { 
          value: this.workshopQualityData[2].qualifiedRate, 
          name: '二车间', 
          itemStyle: { color: '#52c41a' } 
        },
        { 
          value: this.workshopQualityData[3].qualifiedRate, 
          name: '三车间', 
          itemStyle: { color: '#fa8c16' } 
        },
        { 
          value: this.workshopQualityData[4].qualifiedRate, 
          name: '四车间', 
          itemStyle: { color: '#722ed1' } 
        }
      ];
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e8e8e8',
          textStyle: { color: '#333' },
          formatter: '{b}: {c}%'
        },
        legend: {
          top: '5%',
          left: 'center',
          textStyle: {
            color: '#666',
            fontSize: 12
          }
        },
        series: [
          {
            name: '车间合格率',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold',
                formatter: '{b}\n{c}%'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      };
      
      this.qualityChart.setOption(option);
    },
    
    renderProductionChart() {
      const chartDom = document.getElementById('productionChart');
      if (!chartDom) return;
      
      if (this.productionChart) {
        this.productionChart.dispose();
      }
      
      this.productionChart = echarts.init(chartDom);
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e8e8e8',
          textStyle: { color: '#333' },
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        legend: {
          data: ['今日产量', '本周产量', '本月累计'],
          top: '5%',
          textStyle: {
            color: '#666',
            fontSize: 12
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '20%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: ['6:00', '8:00', '10:00', '12:00', '14:00', '16:00', '18:00'],
            axisLine: {
              lineStyle: {
                color: '#d9d9d9'
              }
            },
            axisLabel: {
              color: '#666',
              fontSize: 12
            }
          },
          {
            type: 'category',
            boundaryGap: false,
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
            axisLine: {
              lineStyle: {
                color: '#d9d9d9'
              }
            },
            axisLabel: {
              color: '#666',
              fontSize: 12
            },
            show: false
          },
          {
            type: 'category',
            boundaryGap: false,
            data: ['1', '5', '10', '15', '20', '25', '30'],
            axisLine: {
              lineStyle: {
                color: '#d9d9d9'
              }
            },
            axisLabel: {
              color: '#666',
              fontSize: 12
            },
            show: false
          }
        ],
        yAxis: {
          type: 'value',
          name: '产量(件)',
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          }
        },
        series: [
          {
            name: '今日产量',
            type: 'line',
            xAxisIndex: 0,
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3,
              color: '#1890ff'
            },
            itemStyle: {
              color: '#1890ff'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
                { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
              ])
            },
            data: this.productionData.today
          },
          {
            name: '本周产量',
            type: 'line',
            xAxisIndex: 1,
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3,
              color: '#52c41a'
            },
            itemStyle: {
              color: '#52c41a'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
                { offset: 1, color: 'rgba(82, 196, 26, 0.1)' }
              ])
            },
            data: this.productionData.thisWeek
          },
          {
            name: '本月累计',
            type: 'line',
            xAxisIndex: 2,
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3,
              color: '#722ed1'
            },
            itemStyle: {
              color: '#722ed1'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(114, 46, 209, 0.3)' },
                { offset: 1, color: 'rgba(114, 46, 209, 0.1)' }
              ])
            },
            data: this.productionData.thisMonth
          }
        ]
      };
      
      this.productionChart.setOption(option);
    },
    
    getChartColor(index) {
      const colors = [
        '#1890ff', '#52c41a', '#fa8c16', '#722ed1',
        '#36cfc9', '#fadb14', '#eb2f96', '#13c2c2'
      ];
      return colors[index % colors.length];
    },
    
    // 切换车间
    switchWorkshop(id) {
      this.activeWorkshop = id;
      this.generateEquipmentData();
      this.updateLastUpdateTime();
      this.renderProcessChart(); // 重新渲染工序图表
      this.renderEfficiencyChart(); // 重新渲染效率图表
    },
    
    // 刷新数据
    refreshData() {
      this.refreshing = true;
      setTimeout(() => {
        this.simulateDataUpdate();
        this.updateLastUpdateTime();
        this.renderProcessChart(); // 只更新工序图表
        this.refreshing = false;
      }, 800);
    },
    
    // 强制刷新（生成全新数据）
    forceRefresh() {
      this.refreshing = true;
      setTimeout(() => {
        this.generateFreshData();
        this.updateAllCharts();
        this.refreshing = false;
        this.$message.success('数据已强制刷新');
      }, 800);
    },
    
    simulateDataUpdate() {
      // 更新工序进度
      const processes = this.workshopProcesses[this.activeWorkshop];
      if (processes) {
        processes.forEach(process => {
          if (process.status === 'running') {
            process.progress = Math.min(100, process.progress + Math.random() * 5);
            if (process.progress >= 100) {
              process.status = 'completed';
              const nextProcess = processes.find(p => p.status === 'pending');
              if (nextProcess) {
                nextProcess.status = 'running';
                nextProcess.progress = 5;
                nextProcess.startTime = moment().format('HH:mm:ss');
              }
            }
          }
        });
      }
      
      this.lastDataFreshness = moment();
    },
    
    // 开始自动刷新
    startAutoRefresh() {
      if (this.dataRefreshInterval) {
        clearInterval(this.dataRefreshInterval);
      }
      this.dataRefreshInterval = setInterval(() => {
        if (this.isAutoRefreshing) {
          this.simulateDataUpdate();
          this.updateLastUpdateTime();
          this.renderProcessChart(); // 只更新工序图表
        }
      }, 10000);
    },
    
    // 切换自动刷新
    toggleAutoRefresh(value) {
      this.isAutoRefreshing = value;
      if (value) {
        this.startAutoRefresh();
        this.$message.success('已开启自动刷新');
      } else {
        if (this.dataRefreshInterval) {
          clearInterval(this.dataRefreshInterval);
          this.dataRefreshInterval = null;
        }
        this.$message.info('已关闭自动刷新');
      }
    },
    
    // 更新所有图表
    updateAllCharts() {
      this.renderEfficiencyChart();
      this.renderProcessChart();
      this.renderQualityChart();
      this.renderProductionChart();
    },
    
    updateLastUpdateTime() {
      this.lastUpdateTime = moment().format('HH:mm:ss');
    },
    
    // 设置窗口resize监听
    setupResizeListener() {
      this.resizeListener = () => {
        // 安全地调用图表resize方法
        const charts = [
          this.efficiencyChart,
          this.processChart,
          this.qualityChart,
          this.productionChart
        ];
        
        charts.forEach(chart => {
          if (chart && typeof chart.resize === 'function') {
            try {
              chart.resize();
            } catch (error) {
              console.warn('图表resize失败:', error);
            }
          }
        });
      };
      
      window.addEventListener('resize', this.resizeListener);
    },
    
    // 移除窗口resize监听
    removeResizeListener() {
      if (this.resizeListener) {
        window.removeEventListener('resize', this.resizeListener);
        this.resizeListener = null;
      }
    },
    
    // 工具方法
    getStatusClass(status) {
      const map = {
        '运行中': 'status-running',
        '待检中': 'status-maintenance',
        '异常': 'status-error'
      };
      return map[status] || 'status-running';
    },
    
    getStatusText(status) {
      const map = {
        normal: '正常',
        warning: '警告',
        error: '异常'
      };
      return map[status] || '正常';
    },
    
    getParamStatus(param) {
      if (!param.min || !param.max || typeof param.current !== 'number') {
        return 'normal';
      }
      const min = param.min;
      const max = param.max;
      const range = max - min;
      const warningRange = range * 0.2;
      
      if (param.current < min + warningRange || param.current > max - warningRange) {
        return 'warning';
      }
      return 'normal';
    },
    
    getFreshnessClass() {
      const minutesAgo = moment().diff(this.lastDataFreshness, 'minutes');
      if (minutesAgo < 1) return 'fresh';
      if (minutesAgo < 3) return 'normal';
      return 'stale';
    },
    
    getFreshnessText() {
      const minutesAgo = moment().diff(this.lastDataFreshness, 'minutes');
      if (minutesAgo < 1) return '数据实时';
      if (minutesAgo < 3) return '数据正常';
      return '数据待更新';
    },
    
    formatTime(seconds) {
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    },
    
    // 工序操作
    viewProcessDetail(process) {
      this.$message.info(`查看 ${process.name} 的详细数据`);
    },
    
    pauseProcess(process) {
      process.status = 'pending';
      this.$message.success(`${process.name} 已暂停`);
    },
    
    // 专门刷新质量数据
    regenerateQualityData() {
      this.generateQualityData();
      this.renderQualityChart();
      this.$message.success('质量数据已刷新');
    },
    
    // 专门刷新产量数据
    regenerateProductionData() {
      this.generateProductionData();
      this.renderProductionChart();
      this.$message.success('产量数据已刷新');
    },
    
    // 刷新设备状态
    refreshEquipmentStatus() {
      this.generateEquipmentData();
      this.$message.success('设备状态已刷新');
    },
    
    // 重置车间数据
    resetWorkshopData() {
      this.generateFreshData();
      this.updateAllCharts();
      this.$message.success('车间数据已重置');
    },
    
    // 清理定时器
    clearTimers() {
      if (this.timeInterval) clearInterval(this.timeInterval);
      if (this.dataRefreshInterval) clearInterval(this.dataRefreshInterval);
    },
    
    // 清理图表
    clearCharts() {
      const charts = [
        { name: 'efficiencyChart', chart: this.efficiencyChart },
        { name: 'processChart', chart: this.processChart },
        { name: 'qualityChart', chart: this.qualityChart },
        { name: 'productionChart', chart: this.productionChart }
      ];
      
      charts.forEach(item => {
        if (item.chart) {
          try {
            item.chart.dispose();
            this[item.name] = null;
          } catch (error) {
            console.warn(`清理图表 ${item.name} 时出错:`, error);
          }
        }
      });
    }
  }
};
</script>

<style scoped>
/* 样式优化，新增了一些控制按钮和数据状态显示 */
.ai-inspection-system {
  height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  overflow: hidden;
}

.top-bar {
  height: 60px;
  background: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-bottom: 1px solid #f0f0f0;
  z-index: 100;
}

.system-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo .el-icon-s-platform {
  font-size: 24px;
  color: #1890ff;
}

.system-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.current-time {
  font-family: 'Consolas', monospace;
  color: #666;
  font-size: 14px;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 20px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d9d9d9;
}

.status-dot.active {
  background: #52c41a;
  animation: pulse 2s infinite;
}

.status-text {
  color: #52c41a;
  font-size: 14px;
}

.refresh-counter {
  margin-left: 8px;
  color: #1890ff;
  font-size: 12px;
  font-weight: 600;
}

.main-container {
  flex: 1;
  display: flex;
  padding: 16px;
  gap: 16px;
  overflow: hidden;
  min-height: 0;
}

.panel-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.panel-header {
  padding: 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 500;
  color: #333;
}

.panel-header i {
  color: #1890ff;
}

.reset-btn {
  margin-left: auto;
  padding: 4px 8px;
  font-size: 12px;
}

.left-panel {
  width: 320px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.workshop-list {
  padding: 12px;
  flex: 1;
  overflow-y: auto;
}

.workshop-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
  border: 1px solid transparent;
}

.workshop-item:hover {
  background: #f5f5f5;
  border-color: #e8e8e8;
}

.workshop-item.active {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
}

.workshop-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.workshop-details {
  flex: 1;
  min-width: 0;
}

.workshop-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  font-size: 14px;
}

.workshop-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  white-space: nowrap;
}

.status-running {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-maintenance {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.status-error {
  background: #fff1f0;
  color: #f5222d;
  border: 1px solid #ffa39e;
}

.efficiency {
  color: #1890ff;
  font-weight: 600;
  font-size: 12px;
  white-space: nowrap;
}

.workshop-process {
  color: #666;
  font-size: 12px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.chart-container {
  padding: 16px;
  height: 220px;
}

.chart {
  width: 100%;
  height: 100%;
}

.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
  min-height: 0;
}

.workshop-monitor {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.monitor-header {
  padding: 20px 24px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.workshop-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.workshop-title i {
  color: #1890ff;
}

.workshop-id {
  color: #999;
  font-size: 12px;
  margin-left: 4px;
  font-weight: normal;
}

.monitor-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.update-info {
  color: #666;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.update-info i {
  color: #1890ff;
  animation: spin 2s linear infinite;
}

.data-version {
  margin-left: 8px;
  padding: 2px 6px;
  background: #f0f0f0;
  border-radius: 4px;
  font-size: 10px;
  color: #666;
}

.monitor-actions {
  display: flex;
  gap: 8px;
}

.process-bar-chart {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.chart-title i {
  color: #1890ff;
}

.chart-tip {
  margin-left: 8px;
  padding: 2px 6px;
  background: #fff7e6;
  color: #fa8c16;
  border-radius: 4px;
  font-size: 10px;
}

.chart-container-large {
  height: 280px;
  width: 100%;
}

.chart-large {
  width: 100%;
  height: 100%;
}

.process-detail-list {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 16px 24px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.header-title i {
  color: #1890ff;
}

.data-freshness {
  margin-left: 8px;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
}

.data-freshness.fresh {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.data-freshness.normal {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.data-freshness.stale {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.total-count {
  color: #666;
  font-size: 12px;
}

.data-timestamp {
  color: #999;
  font-size: 11px;
}

.process-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.process-detail-item {
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s;
}

.process-detail-item.active {
  background: #e6f7ff;
  border-color: #91d5ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
}

.process-detail-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.process-header {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.process-index {
  position: relative;
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.index-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.status-indicator {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid white;
}

.status-indicator.running {
  background: #1890ff;
  animation: pulse 1.5s infinite;
}

.status-indicator.completed {
  background: #52c41a;
}

.status-indicator.pending {
  background: #d9d9d9;
}

.process-title {
  flex: 1;
  min-width: 0;
}

.process-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  font-size: 14px;
}

.process-duration {
  color: #666;
  font-size: 12px;
}

.process-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 180px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #36cfc9, #1890ff);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-text {
  color: #1890ff;
  font-weight: 600;
  font-size: 12px;
  min-width: 36px;
  text-align: right;
}

.process-content {
  padding: 16px;
}

.parameter-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.parameter-item {
  background: white;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  gap: 8px;
}

.param-label {
  flex: 1;
  color: #666;
  font-size: 12px;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}

.param-value {
  font-weight: 600;
  color: #333;
  font-size: 14px;
  white-space: nowrap;
}

.param-unit {
  font-size: 12px;
  color: #999;
  font-weight: normal;
  margin-left: 2px;
}

.param-status {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.param-status.normal {
  background: #f6ffed;
  color: #52c41a;
}

.param-status.warning {
  background: #fff7e6;
  color: #fa8c16;
}

.process-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 12px;
}

.meta-item i {
  color: #1890ff;
  font-size: 14px;
}

.process-actions {
  padding: 0 16px 16px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.right-panel {
  width: 320px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.refresh-chart-btn {
  padding: 2px 4px;
  font-size: 11px;
  margin-left: auto;
}

.quality-summary,
.production-summary,
.equipment-summary {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 11px;
  color: #666;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.summary-item .label {
  color: #999;
}

.summary-item .value {
  color: #333;
  font-weight: 600;
}

.equipment-status {
  padding: 12px;
  flex: 1;
  overflow-y: auto;
}

.equipment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 8px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.equipment-item:hover {
  background: #f5f5f5;
}

.equipment-item.warning {
  background: #fff7e6;
  border-color: #ffd591;
}

.equipment-item.error {
  background: #fff1f0;
  border-color: #ffa39e;
}

.equip-info {
  flex: 1;
}

.equip-name {
  color: #333;
  margin-bottom: 4px;
  font-weight: 500;
  font-size: 13px;
}

.equip-desc {
  color: #666;
  font-size: 11px;
}

.equip-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.normal {
  background: #52c41a;
}

.status-dot.warning {
  background: #fa8c16;
  animation: blink 1s infinite;
}

.status-dot.error {
  background: #f5222d;
  animation: blink 0.5s infinite;
}

.status-text {
  color: #666;
  font-size: 11px;
}

.bottom-bar {
  height: 50px;
  background: white;
  border-top: 1px solid #f0f0f0;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.status-items {
  display: flex;
  align-items: center;
  gap: 32px;
  flex: 1;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 13px;
}

.status-item i {
  color: #1890ff;
}

.status-item .value {
  margin-left: 4px;
  font-weight: 600;
  color: #333;
}

.data-refresh {
  display: flex;
  align-items: center;
  gap: 12px;
}

.refresh-text {
  color: #666;
  font-size: 12px;
}

.refresh-text.refreshing {
  color: #1890ff;
}

.refresh-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d9d9d9;
}

.refresh-dot.refreshing {
  background: #52c41a;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@media (max-width: 1400px) {
  .parameter-grid {
    grid-template-columns: 1fr;
  }
  
  .left-panel,
  .right-panel {
    width: 280px;
  }
}

@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
  }
  
  .left-panel,
  .right-panel {
    width: 100%;
  }
  
  .left-panel,
  .center-panel,
  .right-panel {
    height: auto;
  }
  
  .workshop-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .equipment-status {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .parameter-grid {
    grid-template-columns: 1fr;
  }
  
  .workshop-list {
    grid-template-columns: 1fr;
  }
  
  .equipment-status {
    grid-template-columns: 1fr;
  }
  
  .monitor-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .monitor-info {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .process-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .process-progress {
    width: 100%;
  }
  
  .status-items {
    flex-wrap: wrap;
    gap: 16px;
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}

.process-list::-webkit-scrollbar,
.workshop-list::-webkit-scrollbar,
.equipment-status::-webkit-scrollbar {
  width: 4px;
}

.process-list::-webkit-scrollbar-track,
.workshop-list::-webkit-scrollbar-track,
.equipment-status::-webkit-scrollbar-track {
  background: transparent;
}

.process-list::-webkit-scrollbar-thumb,
.workshop-list::-webkit-scrollbar-thumb,
.equipment-status::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 2px;
}
</style>