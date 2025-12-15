<template>
  <div class="smart-inspection-system">
    <div class="system-header">
      <div class="header-decoration"></div>
      <h1 class="system-title">智轴链鉴系统</h1>
      <div class="current-time">{{ currentTime }}</div>
    </div>

    <div class="main-content">
      <el-row :gutter="16" class="equal-height-row">
        <!-- 左侧列 -->
        <el-col :span="6" class="left-column">
          <!-- 预警评分卡片 -->
          <el-card class="warning-score-card tech-border-card" shadow="hover">
            <div class="score-title">
              <i class="el-icon-warning score-icon"></i>
              预警评分
            </div>
            <div class="score-body">
              <div class="level-box">
                <div class="level-shield">
                  <span class="level-text">{{ warningLevel }}</span>
                </div>
                <div class="level-label">预警等级</div>
              </div>
              <div class="circle-box">
                <div class="circle">
                  <svg viewBox="0 0 100 100">
                    <defs>
                      <linearGradient id="gradStroke" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stop-color="#00f0ff" />
                        <stop offset="60%" stop-color="#4ea7ff" />
                        <stop offset="100%" stop-color="#9b7bff" />
                      </linearGradient>
                    </defs>
                    <circle class="circle-bg" cx="50" cy="50" r="42" />
                    <circle 
                      class="circle-progress" 
                      :stroke-dasharray="dashArray" 
                      cx="50" cy="50" r="42" 
                      stroke="url(#gradStroke)" 
                    />
                  </svg>
                  <div class="circle-score">{{ warningScore }}</div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 车间缺陷信息卡片 -->
          <el-card class="workshop-defect-card tech-border-card" shadow="hover">
            <div slot="header" class="card-header">
              <div class="header-title-wrapper">
                <i class="el-icon-office-building header-icon"></i>
                <span class="header-title">车间缺陷信息</span>
              </div>
            </div>
            <div class="defect-info-content">
              <div class="workshop-list-scroll">
                <!-- 只修改这里：三车间 -->
                <div 
                  class="workshop-item" 
                  :class="{ 'active-workshop': currentWorkshop === 3 }"
                  @click="selectWorkshop(3)"
                >
                  <div class="workshop-header">
                    <i class="el-icon-s-shop workshop-icon"></i>
                    <span class="workshop-name">三车间</span>
                    <el-tag 
                      type="danger"
                      size="mini"
                      class="defect-tag"
                    >
                      严重
                    </el-tag>
                  </div>
                  <div class="workshop-stats">
                    <div class="stat-item">
                      <span class="stat-label">缺陷数:</span>
                      <span class="stat-value">12</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">主要问题:</span>
                      <span class="stat-value">精车盘偏移过大</span>
                    </div>
                  </div>
                  <div class="workshop-progress">
                    <el-progress 
                      :percentage="65" 
                      :show-text="false"
                      :stroke-width="6"
                      :color="getProgressColor(65)"
                    />
                  </div>
                </div>
                
                <!-- 只修改这里：四车间 -->
                <div 
                  class="workshop-item" 
                  :class="{ 'active-workshop': currentWorkshop === 4 }"
                  @click="selectWorkshop(4)"
                >
                  <div class="workshop-header">
                    <i class="el-icon-s-shop workshop-icon"></i>
                    <span class="workshop-name">四车间</span>
                    <el-tag 
                      type="warning"
                      size="mini"
                      class="defect-tag"
                    >
                      中等
                    </el-tag>
                  </div>
                  <div class="workshop-stats">
                    <div class="stat-item">
                      <span class="stat-label">缺陷数:</span>
                      <span class="stat-value">3</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">主要问题:</span>
                      <span class="stat-value">检验缺陷率正常</span>
                    </div>
                  </div>
                  <div class="workshop-progress">
                    <el-progress 
                      :percentage="45" 
                      :show-text="false"
                      :stroke-width="6"
                      :color="getProgressColor(45)"
                    />
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 缺陷分布卡片 -->
          <el-card class="defect-distribution-card tech-border-card" shadow="hover">
            <div slot="header" class="card-header">
              <div class="header-title-wrapper">
                <i class="el-icon-s-operation header-icon"></i>
                <span class="header-title">缺陷类型分布</span>
              </div>
            </div>
            <div class="chart-section">
              <div id="defectDistributionChart" class="defect-distribution-chart"></div>
            </div>
          </el-card>
        </el-col>

        <!-- 中间列 - 车间检测状态 -->
        <el-col :span="12" class="center-column">
          <!-- 车间检测状态卡片 -->
          <el-card class="workshop-detection-status-card tech-border-card" shadow="hover">
            <div slot="header" class="card-header">
              <div class="monitoring-header">
                <div class="header-title-wrapper">
                  <i class="el-icon-s-flag header-icon"></i>
                  <span class="header-title">车间检测状态</span>
                  <span class="scanning-status" :class="scanningClass">
                    <i class="el-icon-loading"></i>
                    {{ scanningText }}
                  </span>
                </div>
              </div>
            </div>
            <div class="detection-container">
              <!-- 扫描动画 -->
              <div class="scanning-animation">
                <div class="scanning-line" :style="scanningLineStyle"></div>
                <div class="scanning-glow"></div>
              </div>
              
              <!-- 车间扫描状态 -->
              <div class="workshop-scanning">
                <div 
                  class="scan-item" 
                  v-for="workshop in scanningWorkshops" 
                  :key="workshop.id"
                  :class="{ 'active-scan': workshop.active }"
                >
                  <div class="scan-icon">
                    <i class="el-icon-s-check" v-if="workshop.status === 'completed'"></i>
                    <i class="el-icon-loading" v-else-if="workshop.status === 'scanning'"></i>
                    <i class="el-icon-s-promotion" v-else></i>
                  </div>
                  <div class="scan-info">
                    <div class="scan-name">{{ workshop.name }}</div>
                    <div class="scan-status">{{ workshop.statusText }}</div>
                  </div>
                </div>
              </div>

              <!-- 车间检测状态展示 -->
              <div class="workshop-status-display">
                <div class="status-grid">
                  <div 
                    class="status-card" 
                    v-for="status in workshopDetectionStatus" 
                    :key="status.id"
                    :class="{
                      'active-status': status.id === currentScanningWorkshopId,
                      [status.statusClass]: true
                    }"
                  >
                    <div class="status-header">
                      <i :class="status.icon"></i>
                      <span class="status-workshop">{{ status.name }}</span>
                      <span class="status-level-badge" :class="status.statusClass">
                        {{ status.level }}
                      </span>
                    </div>
                    
                    <div class="status-details">
                      <div class="detail-row">
                        <span class="detail-label">检测时间:</span>
                        <span class="detail-value">{{ status.detectionTime }}</span>
                      </div>
                      <div class="detail-row">
                        <span class="detail-label">检测进度:</span>
                        <span class="detail-value">{{ status.progress }}%</span>
                      </div>
                      <div class="detail-row">
                        <span class="detail-label">检测评分:</span>
                        <span class="detail-value score-value">{{ status.score }}/100</span>
                      </div>
                    </div>
                    
                    <div class="status-progress-container">
                      <el-progress 
                        :percentage="status.progress" 
                        :stroke-width="10"
                        :color="status.progressColor"
                        :show-text="false"
                      />
                      <div class="progress-info">
                        <span class="progress-text">检测进度</span>
                        <span class="progress-percent">{{ status.progress }}%</span>
                      </div>
                    </div>
                    
                    <div class="status-indicators">
                      <div class="indicator" v-for="indicator in status.indicators" :key="indicator.name">
                        <span class="indicator-name">{{ indicator.name }}</span>
                        <el-progress 
                          :percentage="indicator.value" 
                          :stroke-width="6"
                          :color="getIndicatorColor(indicator.value)"
                          :show-text="false"
                          class="indicator-progress"
                        />
                        <span class="indicator-value">{{ indicator.value }}%</span>
                      </div>
                    </div>
                    
                    <div class="status-footer-info">
                      <div class="footer-item">
                        <i class="el-icon-time"></i>
                        <span>持续时长: {{ status.duration }}</span>
                      </div>
                      <div class="footer-item">
                        <i class="el-icon-view"></i>
                        <span>检测次数: {{ status.detectionCount }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 预警信息趋势卡片 -->
          <el-card class="warning-trend-card tech-border-card" shadow="hover">
            <div slot="header" class="card-header">
              <div class="header-title-wrapper">
                <i class="el-icon-s-data header-icon"></i>
                <span class="header-title">预警信息趋势</span>
                <el-select 
                  v-model="trendType" 
                  size="mini" 
                  class="trend-select"
                  @change="updateTrendChart"
                >
                  <el-option label="实时趋势" value="realtime"></el-option>
                  <el-option label="每日趋势" value="daily"></el-option>
                  <el-option label="每周趋势" value="weekly"></el-option>
                </el-select>
              </div>
            </div>
            <div class="trend-container">
              <div id="warningTrendChart" class="warning-trend-chart"></div>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧列 -->
        <el-col :span="6" class="right-column">
          <!-- 车间缺陷占比分析卡片 -->
          <el-card class="workshop-analysis-card tech-border-card" shadow="hover">
            <div slot="header" class="card-header">
              <div class="header-title-wrapper">
                <i class="el-icon-pie-chart header-icon"></i>
                <span class="header-title">车间缺陷占比分析</span>
              </div>
            </div>
            <div class="chart-section">
              <div id="workshopPieChart" class="workshop-pie-chart"></div>
            </div>
          </el-card>

          <!-- 缺陷数量统计卡片 -->
          <el-card class="defect-statistics-card tech-border-card" shadow="hover">
            <div slot="header" class="card-header">
              <div class="header-title-wrapper">
                <i class="el-icon-s-marketing header-icon"></i>
                <span class="header-title">缺陷数量统计</span>
                <el-select 
                  v-model="statisticsType" 
                  size="mini" 
                  class="statistics-select"
                  @change="updateStatisticsChart"
                >
                  <el-option label="每日统计" value="daily"></el-option>
                  <el-option label="每周统计" value="weekly"></el-option>
                  <el-option label="每月统计" value="monthly"></el-option>
                </el-select>
              </div>
            </div>
            <div class="chart-section">
              <div id="defectStatisticsChart" class="defect-statistics-chart"></div>
            </div>
          </el-card>

          <!-- 系统状态卡片 -->
          <div class="status-footer tech-border-card">
            <div class="system-status">
              <div class="status-item">
                <i class="status-icon" :class="eventSourcePicture && eventSourcePicture.readyState === 1 ? 'el-icon-success' : 'el-icon-error'"></i>
                <span class="status-text">{{ eventSourcePicture && eventSourcePicture.readyState === 1 ? '实时连接中' : '连接断开' }}</span>
              </div>
              <div class="status-item">
                <i class="el-icon-refresh refresh-icon"></i>
                <span class="refresh-text">上次刷新: {{ lastRefreshTime }}</span>
              </div>
              <div class="status-item">
                <i class="el-icon-timer refresh-icon"></i>
                <span class="refresh-text">扫描间隔: 3秒</span>
              </div>
              <div class="status-item">
                <i class="el-icon-s-grid scan-icon"></i>
                <span class="scan-text">当前扫描: {{ currentScanningWorkshop }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import axios from "axios";
import moment from 'moment';

export default {
  data() {
    return {
      // 预警评分数据
      warningScore: 85,
      warningLevel: '良',
      dashArray: '0 0',
      
      // 检测轮播相关（保留但不显示）
      currentImageData: null,
      detectionImages: [],
      currentCarouselIndex: 0,
      carouselTimer: null,
      
      // 车间相关数据
      workshopDefects: [
        // 原始数据保持，但模板中不使用
        { id: 3, name: '三车间', defectCount: 12, defectLevel: 'high', mainDefect: '精车盘/钻孔的偏移过大', defectPercentage: 65 },
        { id: 4, name: '四车间', defectCount: 8, defectLevel: 'medium', mainDefect: '检验缺陷率正常', defectPercentage: 45 }
      ],
      currentWorkshop: 3, // 修改默认选中为三车间
      
      // 扫描状态
      isScanning: true,
      scanningWorkshops: [
        { id: 1, name: '一车间', status: 'completed', statusText: '已完成', active: false },
        { id: 2, name: '二车间', status: 'scanning', statusText: '扫描中', active: true },
        { id: 3, name: '三车间', status: 'pending', statusText: '等待中', active: false },
        { id: 4, name: '四车间', status: 'pending', statusText: '等待中', active: false }
      ],
      scanningLinePosition: 0,
      
      // 车间检测状态（增强版）
      workshopDetectionStatus: [
        { 
          id: 1, 
          name: '一车间', 
          level: '正常', 
          detectionTime: '2024-01-15 10:30:25', 
          progress: 100, 
          score: 92,
          duration: '2小时15分',
          detectionCount: 156,
          icon: 'el-icon-success',
          statusClass: 'status-normal',
          progressColor: '#67c23a',
          indicators: [
            { name: '温度', value: 95 },
            { name: '压力', value: 88 },
            { name: '速度', value: 92 },
            { name: '精度', value: 90 }
          ]
        },
        { 
          id: 2, 
          name: '二车间', 
          level: '良好', 
          detectionTime: '2024-01-15 10:25:18', 
          progress: 85, 
          score: 87,
          duration: '1小时45分',
          detectionCount: 128,
          icon: 'el-icon-star-on',
          statusClass: 'status-good',
          progressColor: '#409eff',
          indicators: [
            { name: '温度', value: 88 },
            { name: '压力', value: 85 },
            { name: '速度', value: 90 },
            { name: '精度', value: 86 }
          ]
        },
        { 
          id: 3, 
          name: '三车间', 
          level: '中等', 
          detectionTime: '2024-01-15 10:20:42', 
          progress: 65, 
          score: 75,
          duration: '1小时20分',
          detectionCount: 95,
          icon: 'el-icon-warning',
          statusClass: 'status-medium',
          progressColor: '#e6a23c',
          indicators: [
            { name: '温度', value: 72 },
            { name: '压力', value: 78 },
            { name: '速度', value: 70 },
            { name: '精度', value: 80 }
          ]
        },
        { 
          id: 4, 
          name: '四车间', 
          level: '良好', 
          detectionTime: '2024-01-15 10:15:33', 
          progress: 90, 
          score: 89,
          duration: '2小时05分',
          detectionCount: 142,
          icon: 'el-icon-star-on',
          statusClass: 'status-good',
          progressColor: '#409eff',
          indicators: [
            { name: '温度', value: 90 },
            { name: '压力', value: 87 },
            { name: '速度', value: 92 },
            { name: '精度', value: 88 }
          ]
        }
      ],
      currentScanningWorkshopId: 2,
      
      // 图表相关
      workshopPieChart: null,
      defectDistributionChart: null,
      warningTrendChart: null,
      defectStatisticsChart: null,
      
      // 图表类型选择
      trendType: 'realtime',
      statisticsType: 'daily',
      
      // 系统状态
      eventSourcePicture: null,
      lastRefreshTime: '--',
      currentTime: '',
      
      // 定时器
      _timeTicker: null,
      _onResize: null,
      _scanningTimer: null,
      _trendUpdateTimer: null,
      _workshopUpdateTimer: null,
      _statusUpdateTimer: null,
      
      // 车间缺陷数据源（保留原始数据，但模板不使用）
      workshopDefectPool: [
        // 三车间数据
        { id: 3, name: '三车间', defectCount: 12, defectLevel: 'high', mainDefect: '精车盘/钻孔的偏移过大', defectPercentage: 65 },
        
        // 四车间数据
        { id: 4, name: '四车间', defectCount: 8, defectLevel: 'medium', mainDefect: '检验缺陷率正常', defectPercentage: 45 }
      ],
      
      // 每个车间当前显示的数据索引
      workshopDisplayIndex: [0, 0, 0, 0]
    };
  },
  
  computed: {
    scanningText() {
      return this.isScanning ? '正在扫描检测...' : '扫描暂停';
    },
    
    scanningClass() {
      return {
        'scanning-active': this.isScanning,
        'scanning-paused': !this.isScanning
      };
    },
    
    scanningLineStyle() {
      return {
        left: `${this.scanningLinePosition}%`
      };
    },
    
    currentScanningWorkshop() {
      const workshop = this.scanningWorkshops.find(w => w.status === 'scanning');
      return workshop ? workshop.name : '等待中';
    }
  },
  
  mounted() {
    this.initSSEConnection();
    this.initData();
    this.updateCurrentTime();
    this.updateCircle();
    
    // 开始扫描动画
    this.startScanningAnimation();
    
    // 开始车间信息滚动更新
    this.startWorkshopInfoUpdate();
    
    // 开始车间检测状态更新
    this.startStatusUpdate();
    
    // 更新时间
    this._timeTicker = setInterval(() => {
      this.updateCurrentTime();
    }, 1000);
    
    // 渲染图表
    this.$nextTick(() => {
      setTimeout(() => {
        this.renderWorkshopPieChart();
        this.renderDefectDistributionChart();
        this.renderWarningTrendChart();
        this.renderDefectStatisticsChart();
      }, 500);
    });
    
    // 窗口调整监听
    this._onResize = () => {
      if (this.workshopPieChart) this.workshopPieChart.resize();
      if (this.defectDistributionChart) this.defectDistributionChart.resize();
      if (this.warningTrendChart) this.warningTrendChart.resize();
      if (this.defectStatisticsChart) this.defectStatisticsChart.resize();
    };
    window.addEventListener('resize', this._onResize);
  },
  
  beforeDestroy() {
    if (this.eventSourcePicture) this.eventSourcePicture.close();
    if (this.workshopPieChart) this.workshopPieChart.dispose();
    if (this.defectDistributionChart) this.defectDistributionChart.dispose();
    if (this.warningTrendChart) this.warningTrendChart.dispose();
    if (this.defectStatisticsChart) this.defectStatisticsChart.dispose();
    if (this._timeTicker) clearInterval(this._timeTicker);
    if (this._scanningTimer) clearInterval(this._scanningTimer);
    if (this._trendUpdateTimer) clearInterval(this._trendUpdateTimer);
    if (this._workshopUpdateTimer) clearInterval(this._workshopUpdateTimer);
    if (this._statusUpdateTimer) clearInterval(this._statusUpdateTimer);
    if (this._onResize) window.removeEventListener('resize', this._onResize);
  },
  
  methods: {
    // 初始化数据
    async initData() {
      await this.fetchDetectionImages();
      this.updateRefreshTime();
    },
    
    // SSE连接
    initSSEConnection() {
      if (this.eventSourcePicture) {
        this.eventSourcePicture.close();
      }
      
      this.eventSourcePicture = new EventSource('api/dashboard/pictureInfo', { retry: 20000 });
      
      this.eventSourcePicture.onopen = () => {
        console.log('SSE连接成功');
        this.updateWarningInfo();
      };
      
      this.eventSourcePicture.onerror = (error) => {
        console.error('SSE连接错误:', error);
      };
      
      this.eventSourcePicture.onmessage = event => {
        try {
          const data = JSON.parse(event.data);
          this.updateRealTimeData(data);
        } catch (error) {
          console.error('解析SSE数据失败:', error);
        }
      };
    },
    
    // 更新实时数据
    updateRealTimeData(data) {
      // 更新图像
      if (data.imgBase64) {
        this.currentImageData = data.imgBase64;
      }
      
      // 更新预警信息
      if (data.defections) {
        this.updateWarningInfo(data);
      }
      
      // 更新图表数据
      this.updateChartsWithRealTimeData(data);
    },
    
    // 更新预警信息
    updateWarningInfo(data = null) {
      if (data && data.defections) {
        const defectCount = data.defections.length;
        const baseScore = 100;
        const penalty = defectCount * 2;
        this.warningScore = Math.max(60, baseScore - penalty);
      } else {
        // 模拟数据
        const baseScore = 85;
        const fluctuation = Math.random() * 10 - 5;
        this.warningScore = Math.min(95, Math.max(75, baseScore + fluctuation));
      }
      
      // 确保预警评分为整数
      this.warningScore = Math.round(this.warningScore);

      // 确定预警等级
      if (this.warningScore >= 90) {
        this.warningLevel = '优';
      } else if (this.warningScore >= 80) {
        this.warningLevel = '良';
      } else if (this.warningScore >= 70) {
        this.warningLevel = '中';
      } else if (this.warningScore >= 60) {
        this.warningLevel = '较差';
      } else {
        this.warningLevel = '危险';
      }
      
      this.updateCircle();
    },
    
    // 更新图表数据
    updateChartsWithRealTimeData(data) {
      // 更新车间缺陷数据
      if (data.workshopData) {
        this.workshopDefects = data.workshopData.map((workshop, index) => ({
          ...workshop,
          defectPercentage: Math.min(100, workshop.defectCount * 5)
        }));
      }
      
      // 更新预警趋势图
      if (this.warningTrendChart && this.trendType === 'realtime') {
        this.addTrendDataPoint();
      }
    },
    
    // 获取检测图像（保留但不使用）
    async fetchDetectionImages() {
      try {
        // 模拟数据
        const mockImages = Array.from({ length: 8 }, (_, i) => ({
          id: i + 1,
          imgBase64: this.generateMockImage(),
          workshopId: (i % 4) + 1,
          timestamp: new Date(Date.now() - i * 60000).toISOString()
        }));
        
        this.detectionImages = mockImages;
        if (mockImages.length > 0) {
          this.currentImageData = mockImages[0].imgBase64;
          this.currentCarouselIndex = 0;
        }
        
        return true;
      } catch (error) {
        console.error('获取检测图像失败:', error);
        return false;
      }
    },
    
    // 生成模拟图像
    generateMockImage() {
      // 模拟base64图像数据
      return 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==';
    },
    
    // 选择车间
    selectWorkshop(workshopId) {
      this.currentWorkshop = workshopId;
      this.currentScanningWorkshopId = workshopId;
    },
    
    // 开始扫描动画
    startScanningAnimation() {
      this._scanningTimer = setInterval(() => {
        this.scanningLinePosition = (this.scanningLinePosition + 5) % 100;
        
        // 模拟车间扫描状态变化
        if (this.scanningLinePosition % 25 === 0) {
          this.updateScanningWorkshops();
        }
      }, 200);
    },
    
    // 更新扫描车间状态
    updateScanningWorkshops() {
      let foundScanning = false;
      
      for (let i = 0; i < this.scanningWorkshops.length; i++) {
        const workshop = this.scanningWorkshops[i];
        
        if (workshop.status === 'scanning') {
          workshop.status = 'completed';
          workshop.statusText = '已完成';
          workshop.active = false;
          
          // 更新当前扫描车间ID
          this.currentScanningWorkshopId = workshop.id;
          
          // 更新对应车间的检测状态
          this.updateWorkshopDetectionStatus(workshop.id);
          
          // 移动到下一个车间
          if (i < this.scanningWorkshops.length - 1) {
            this.scanningWorkshops[i + 1].status = 'scanning';
            this.scanningWorkshops[i + 1].statusText = '扫描中';
            this.scanningWorkshops[i + 1].active = true;
            foundScanning = true;
            break;
          }
        }
      }
      
      // 如果所有车间都扫描完成，重新开始
      if (!foundScanning) {
        this.scanningWorkshops.forEach(workshop => {
          workshop.status = 'pending';
          workshop.statusText = '等待中';
          workshop.active = false;
        });
        this.scanningWorkshops[0].status = 'scanning';
        this.scanningWorkshops[0].statusText = '扫描中';
        this.scanningWorkshops[0].active = true;
        this.currentScanningWorkshopId = this.scanningWorkshops[0].id;
      }
      
      // 更新预警信息
      this.updateWarningInfo();
    },
    
    // 更新车间检测状态
    updateWorkshopDetectionStatus(workshopId) {
      const statusIndex = this.workshopDetectionStatus.findIndex(s => s.id === workshopId);
      if (statusIndex !== -1) {
        // 随机生成检测结果
        const levels = ['正常', '良好', '中等'];
        const level = levels[Math.floor(Math.random() * levels.length)];
        
        let statusClass, icon, progressColor, score;
        switch(level) {
          case '正常':
            statusClass = 'status-normal';
            icon = 'el-icon-success';
            progressColor = '#67c23a';
            score = Math.floor(Math.random() * 10) + 85; // 85-95
            break;
          case '良好':
            statusClass = 'status-good';
            icon = 'el-icon-star-on';
            progressColor = '#409eff';
            score = Math.floor(Math.random() * 10) + 75; // 75-85
            break;
          case '中等':
            statusClass = 'status-medium';
            icon = 'el-icon-warning';
            progressColor = '#e6a23c';
            score = Math.floor(Math.random() * 10) + 65; // 65-75
            break;
          default:
            statusClass = 'status-normal';
            icon = 'el-icon-success';
            progressColor = '#67c23a';
            score = 90;
        }
        
        // 更新指标数据
        const indicators = [
          { name: '温度', value: Math.floor(Math.random() * 30) + 70 },
          { name: '压力', value: Math.floor(Math.random() * 30) + 70 },
          { name: '速度', value: Math.floor(Math.random() * 30) + 70 },
          { name: '精度', value: Math.floor(Math.random() * 30) + 70 }
        ];
        
        // 更新状态
        this.workshopDetectionStatus[statusIndex] = {
          ...this.workshopDetectionStatus[statusIndex],
          level: level,
          detectionTime: moment().format('YYYY-MM-DD HH:mm:ss'),
          progress: Math.floor(Math.random() * 30) + 70, // 70-100之间的随机数
          score: score,
          duration: `${Math.floor(Math.random() * 3) + 1}小时${Math.floor(Math.random() * 60)}分`,
          detectionCount: Math.floor(Math.random() * 100) + 100,
          icon: icon,
          statusClass: statusClass,
          progressColor: progressColor,
          indicators: indicators
        };
      }
    },
    
    // 获取指标颜色
    getIndicatorColor(value) {
      if (value >= 90) return '#67c23a';
      if (value >= 80) return '#409eff';
      if (value >= 70) return '#e6a23c';
      return '#f56c6c';
    },
    
    // 开始车间检测状态更新
    startStatusUpdate() {
      if (this._statusUpdateTimer) {
        clearInterval(this._statusUpdateTimer);
      }
      
      // 每10秒更新一个车间的状态
      this._statusUpdateTimer = setInterval(() => {
        const randomWorkshopId = Math.floor(Math.random() * 4) + 1;
        this.updateWorkshopDetectionStatus(randomWorkshopId);
      }, 10000);
    },
    
    // 开始车间信息滚动更新
    startWorkshopInfoUpdate() {
      if (this._workshopUpdateTimer) {
        clearInterval(this._workshopUpdateTimer);
      }
      
      // 每5秒更新一个车间的信息
      this._workshopUpdateTimer = setInterval(() => {
        this.updateWorkshopInfoRolling();
      }, 5000);
    },
    
    // 滚动更新车间信息
    updateWorkshopInfoRolling() {
      // 确定要更新的车间索引 (1-4)
      const workshopId = Math.floor(Math.random() * 4) + 1;
      const workshopIndex = workshopId - 1;
      
      // 获取该车间的可用数据
      const workshopDataPool = this.workshopDefectPool.filter(item => item.id === workshopId);
      if (workshopDataPool.length === 0) return;
      
      // 获取当前显示索引并递增
      let currentIndex = this.workshopDisplayIndex[workshopIndex];
      currentIndex = (currentIndex + 1) % workshopDataPool.length;
      this.workshopDisplayIndex[workshopIndex] = currentIndex;
      
      // 获取新的车间数据
      const newWorkshopData = { ...workshopDataPool[currentIndex] };
      
      // 更新车间缺陷列表
      const updatedWorkshopDefects = [...this.workshopDefects];
      const targetIndex = updatedWorkshopDefects.findIndex(item => item.id === workshopId);
      
      if (targetIndex !== -1) {
        // 添加更新动画效果
        updatedWorkshopDefects[targetIndex] = {
          ...newWorkshopData,
          defectPercentage: Math.min(100, newWorkshopData.defectCount * 5)
        };
        
        // 更新数据
        this.workshopDefects = updatedWorkshopDefects;
        
        // 如果当前选中的车间被更新，也更新相关图表
        if (this.currentWorkshop === workshopId) {
          this.$nextTick(() => {
            if (this.workshopPieChart) {
              this.updatePieChartWithNewData();
            }
          });
        }
        
        // 更新刷新时间
        this.updateRefreshTime();
        
        console.log(`车间 ${workshopId} 信息已更新: ${newWorkshopData.mainDefect}`);
      }
    },
    
    // 根据新数据更新饼图
    updatePieChartWithNewData() {
      if (!this.workshopPieChart) return;
      
      const pieData = this.workshopDefects.map(workshop => ({
        name: workshop.name,
        value: workshop.defectCount
      }));
      
      const option = this.workshopPieChart.getOption();
      option.series[0].data = pieData.map((d, i) => ({
        value: d.value,
        name: d.name,
        itemStyle: {
          borderColor: '#071a2a',
          borderWidth: 2,
          shadowColor: 'rgba(0, 0, 0, 0.6)',
          shadowBlur: 8
        }
      }));
      
      this.workshopPieChart.setOption(option);
    },
    
    // 获取缺陷标签类型
    getDefectTagType(level) {
      switch (level) {
        case 'high': return 'danger';
        case 'medium': return 'warning';
        case 'low': return 'success';
        default: return 'info';
      }
    },
    
    // 获取缺陷等级文本
    getDefectLevelText(level) {
      switch (level) {
        case 'high': return '严重';
        case 'medium': return '中等';
        case 'low': return '轻微';
        default: return '正常';
      }
    },
    
    // 获取进度条颜色
    getProgressColor(percentage) {
      if (percentage >= 70) return '#f56c6c';
      if (percentage >= 40) return '#e6a23c';
      return '#67c23a';
    },
    
    // Base64图片URL处理
    getBase64ImageUrl(base64Data) {
      return `data:image/jpeg;base64,${base64Data}`;
    },
    
    // 更新时间
    updateCurrentTime() {
      this.currentTime = moment().format('YYYY-MM-DD HH:mm:ss dddd');
    },
    
    // 更新刷新时间
    updateRefreshTime() {
      this.lastRefreshTime = moment().format('YYYY-MM-DD HH:mm:ss');
    },
    
    // 更新圆环
    updateCircle() {
      const r = 42;
      const circumference = 2 * Math.PI * r;
      let percent = 0;
      if (typeof this.warningScore === 'number' && !isNaN(this.warningScore)) {
        percent = Math.max(0, Math.min(100, this.warningScore)) / 100;
      }
      const dash = (circumference * percent).toFixed(2);
      this.dashArray = `${dash} ${circumference.toFixed(2)}`;
    },
    
    // ========== 图表渲染方法 ==========
    
    // 渲染车间缺陷占比分析饼图
    renderWorkshopPieChart() {
      const chartDom = document.getElementById('workshopPieChart');
      if (!chartDom) return;
      
      if (this.workshopPieChart) {
        this.workshopPieChart.dispose();
      }
      
      this.workshopPieChart = echarts.init(chartDom);
      
      const pieData = this.workshopDefects.map(workshop => ({
        name: workshop.name,
        value: workshop.defectCount
      }));
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: params => {
            return `<div style="color:#cfeeff;font-size:13px">
                      <div style="font-weight:700">${params.name}</div>
                      <div style="margin-top:6px">${params.value} ( ${params.percent}% )</div>
                    </div>`;
          },
          backgroundColor: 'rgba(3,18,40,0.92)',
          borderColor: 'rgba(0,200,255,0.12)',
          borderWidth: 1,
          textStyle: { color: '#cfeeff' }
        },
        legend: {
          show: true,
          orient: 'vertical',
          right: 10,
          top: 'center',
          textStyle: { color: '#bcdcff', fontSize: 12 },
          itemWidth: 12,
          itemHeight: 12
        },
        series: [{
          name: '缺陷占比',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['40%', '50%'],
          avoidLabelOverlap: false,
          startAngle: 90,
          data: pieData.map((d, i) => ({
            value: d.value,
            name: d.name,
            itemStyle: {
              borderColor: '#071a2a',
              borderWidth: 2,
              shadowColor: 'rgba(0, 0, 0, 0.6)',
              shadowBlur: 8
            }
          })),
          label: {
            show: false
          },
          labelLine: {
            show: false
          },
          emphasis: {
            scale: true,
            scaleSize: 10,
            itemStyle: {
              shadowBlur: 20,
              shadowColor: 'rgba(0, 180, 255, 0.3)'
            }
          },
          color: [
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#00e5ff' }, { offset: 1, color: '#0087ff' }]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#7b61ff' }, { offset: 1, color: '#4b2bff' }]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#f6a2ff' }, { offset: 1, color: '#f04a87' }]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#3ad3ff' }, { offset: 1, color: '#00a6ff' }])
          ]
        }]
      };
      
      this.workshopPieChart.setOption(option);
    },
    
    // 渲染缺陷类型分布饼图
    renderDefectDistributionChart() {
      const chartDom = document.getElementById('defectDistributionChart');
      if (!chartDom) return;
      
      if (this.defectDistributionChart) {
        this.defectDistributionChart.dispose();
      }
      
      this.defectDistributionChart = echarts.init(chartDom);
      
      const pieData = [
        { name: '压花罐温度', value: 35 },
        { name: '切割精度', value: 28 },
        { name: '钻中心孔-粗抛丸', value: 20 },
        { name: '精校/回火', value: 17 }
      ];
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: params => {
            return `<div style="color:#cfeeff;font-size:13px">
                      <div style="font-weight:700">${params.name}</div>
                      <div style="margin-top:6px">${params.value} ( ${params.percent}% )</div>
                    </div>`;
          },
          backgroundColor: 'rgba(3,18,40,0.92)',
          borderColor: 'rgba(0,200,255,0.12)',
          borderWidth: 1,
          textStyle: { color: '#cfeeff' }
        },
        legend: {
          show: false
        },
        series: [{
          name: '缺陷类型',
          type: 'pie',
          radius: ['30%', '60%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: false,
          startAngle: 120,
          data: pieData.map((d, i) => ({
            value: d.value,
            name: d.name,
            itemStyle: {
              borderColor: '#071a2a',
              borderWidth: 2,
              shadowColor: 'rgba(0, 0, 0, 0.6)',
              shadowBlur: 8
            }
          })),
          label: {
            show: true,
            formatter: '{b}\n{d}%',
            color: '#cfeeff',
            fontSize: 11,
            align: 'center'
          },
          labelLine: {
            length: 8,
            length2: 6,
            smooth: true
          },
          emphasis: {
            scale: true,
            scaleSize: 8,
            itemStyle: {
              shadowBlur: 20,
              shadowColor: 'rgba(0, 180, 255, 0.3)'
            }
          },
          color: [
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#00e5ff' }, { offset: 1, color: '#0087ff' }]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#7b61ff' }, { offset: 1, color: '#4b2bff' }]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#f6a2ff' }, { offset: 1, color: '#f04a87' }]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#3ad3ff' }, { offset: 1, color: '#00a6ff' }])
          ]
        }]
      };
      
      this.defectDistributionChart.setOption(option);
    },
    
    // 渲染预警信息趋势折线图
    renderWarningTrendChart() {
      const chartDom = document.getElementById('warningTrendChart');
      if (!chartDom) return;
      
      if (this.warningTrendChart) {
        this.warningTrendChart.dispose();
      }
      
      this.warningTrendChart = echarts.init(chartDom);
      
      // 生成时间数据
      const timeData = [];
      for (let i = 23; i >= 0; i--) {
        timeData.push(moment().subtract(i, 'hours').format('HH:00'));
      }
      
      // 生成车间数据
      const workshop1Data = Array.from({ length: 24 }, () => Math.floor(Math.random() * 30) + 50);
      const workshop2Data = Array.from({ length: 24 }, () => Math.floor(Math.random() * 30) + 50);
      const workshop3Data = Array.from({ length: 24 }, () => Math.floor(Math.random() * 30) + 60);
      const workshop4Data = Array.from({ length: 24 }, () => Math.floor(Math.random() * 30) + 70);
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(3,18,40,0.95)',
          borderColor: 'rgba(0,200,255,0.2)',
          borderWidth: 1,
          textStyle: { color: '#cfeeff' },
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        legend: {
          data: ['一车间', '二车间', '三车间', '四车间'],
          textStyle: { color: '#bcdcff', fontSize: 12 },
          right: 10,
          top: 5
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '12%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: timeData,
          axisLine: {
            lineStyle: {
              color: 'rgba(100,180,255,0.14)'
            }
          },
          axisLabel: {
            color: '#cbeaff',
            fontSize: 11,
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: 100,
          axisLine: {
            show: false
          },
          axisLabel: {
            color: '#9fcffb',
            formatter: '{value}'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(8,30,60,0.08)',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '一车间',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            showSymbol: false,
            lineStyle: {
              width: 3,
              shadowColor: 'rgba(0, 240, 255, 0.5)',
              shadowBlur: 10
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0, 240, 255, 0.3)' },
                { offset: 1, color: 'rgba(0, 240, 255, 0.05)' }
              ])
            },
            itemStyle: {
              color: '#00f0ff'
            },
            data: workshop1Data
          },
          {
            name: '二车间',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            showSymbol: false,
            lineStyle: {
              width: 3,
              shadowColor: 'rgba(123, 97, 255, 0.5)',
              shadowBlur: 10
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(123, 97, 255, 0.3)' },
                { offset: 1, color: 'rgba(123, 97, 255, 0.05)' }
              ])
            },
            itemStyle: {
              color: '#7b61ff'
            },
            data: workshop2Data
          },
          {
            name: '三车间',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            showSymbol: false,
            lineStyle: {
              width: 3,
              shadowColor: 'rgba(246, 162, 255, 0.5)',
              shadowBlur: 10
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(246, 162, 255, 0.3)' },
                { offset: 1, color: 'rgba(246, 162, 255, 0.05)' }
              ])
            },
            itemStyle: {
              color: '#f6a2ff'
            },
            data: workshop3Data
          },
          {
            name: '四车间',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            showSymbol: false,
            lineStyle: {
              width: 3,
              shadowColor: 'rgba(58, 211, 255, 0.5)',
              shadowBlur: 10
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(58, 211, 255, 0.3)' },
                { offset: 1, color: 'rgba(58, 211, 255, 0.05)' }
              ])
            },
            itemStyle: {
              color: '#3ad3ff'
            },
            data: workshop4Data
          }
        ]
      };
      
      this.warningTrendChart.setOption(option);
      
      // 开始实时更新
      this.startTrendDataUpdate();
    },
    
    // 开始趋势数据更新
    startTrendDataUpdate() {
      if (this._trendUpdateTimer) {
        clearInterval(this._trendUpdateTimer);
      }
      
      this._trendUpdateTimer = setInterval(() => {
        if (this.trendType === 'realtime') {
          this.addTrendDataPoint();
        }
      }, 3000);
    },
    
    // 添加趋势数据点
    addTrendDataPoint() {
      if (!this.warningTrendChart) return;
      
      const option = this.warningTrendChart.getOption();
      const now = moment().format('HH:mm');
      
      // 更新X轴数据
      const xData = option.xAxis[0].data;
      xData.shift();
      xData.push(now);
      
      // 更新每个系列的数据
      option.series.forEach((series, index) => {
        const data = series.data;
        data.shift();
        
        // 生成新的随机数据点
        let newValue;
        if (index === 0) newValue = Math.floor(Math.random() * 30) + 50; // 一车间
        else if (index === 1) newValue = Math.floor(Math.random() * 30) + 50; // 二车间
        else if (index === 2) newValue = Math.floor(Math.random() * 30) + 60; // 三车间
        else newValue = Math.floor(Math.random() * 30) + 70; // 四车间
        
        data.push(newValue);
      });
      
      this.warningTrendChart.setOption(option);
    },
    
    // 更新趋势图表类型
    updateTrendChart() {
      if (this.warningTrendChart) {
        this.warningTrendChart.dispose();
        this.renderWarningTrendChart();
      }
    },
    
    // 渲染缺陷数量统计折线图
    renderDefectStatisticsChart() {
      const chartDom = document.getElementById('defectStatisticsChart');
      if (!chartDom) return;
      
      if (this.defectStatisticsChart) {
        this.defectStatisticsChart.dispose();
      }
      
      this.defectStatisticsChart = echarts.init(chartDom);
      
      // 根据统计类型生成数据
      let xData, seriesData;
      
      if (this.statisticsType === 'daily') {
        xData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
        seriesData = [
          { name: '压花罐温度', data: [12, 8, 15, 10, 14, 9, 11] },
          { name: '切割精度', data: [8, 6, 10, 7, 9, 5, 8] },
          { name: '钻中心孔-粗抛丸', data: [5, 4, 7, 5, 6, 3, 5] },
          { name: '精校/回火', data: [3, 2, 4, 3, 4, 2, 3] }
        ];
      } else if (this.statisticsType === 'weekly') {
        xData = ['第1周', '第2周', '第3周', '第4周'];
        seriesData = [
          { name: '压花罐温度', data: [45, 38, 52, 41] },
          { name: '切割精度', data: [32, 28, 35, 30] },
          { name: '钻中心孔-粗抛丸', data: [20, 18, 25, 22] },
          { name: '精校/回火', data: [12, 10, 15, 13] }
        ];
      } else {
        xData = ['1月', '2月', '3月', '4月', '5月', '6月'];
        seriesData = [
          { name: '压花罐温度', data: [150, 135, 160, 145, 170, 155] },
          { name: '切割精度', data: [120, 110, 130, 125, 140, 135] },
          { name: '钻中心孔-粗抛丸', data: [80, 75, 90, 85, 95, 90] },
          { name: '精校/回火', data: [50, 45, 55, 50, 60, 55] }
        ];
      }
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(3,18,40,0.95)',
          borderColor: 'rgba(0,200,255,0.2)',
          borderWidth: 1,
          textStyle: { color: '#cfeeff' }
        },
        legend: {
          data: seriesData.map(s => s.name),
          textStyle: { color: '#bcdcff', fontSize: 12 },
          right: 10,
          top: 5
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '12%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xData,
          axisLine: {
            lineStyle: {
              color: 'rgba(100,180,255,0.14)'
            }
          },
          axisLabel: {
            color: '#cbeaff',
            fontSize: 11
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisLabel: {
            color: '#9fcffb'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(8,30,60,0.08)',
              type: 'dashed'
            }
          }
        },
        series: seriesData.map((series, index) => {
          const colors = [
            { color: '#00f0ff', gradient: 'rgba(0, 240, 255, 0.3)' },
            { color: '#7b61ff', gradient: 'rgba(123, 97, 255, 0.3)' },
            { color: '#f6a2ff', gradient: 'rgba(246, 162, 255, 0.3)' },
            { color: '#3ad3ff', gradient: 'rgba(58, 211, 255, 0.3)' }
          ];
          
          return {
            name: series.name,
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 3,
              shadowColor: `${colors[index].gradient.replace('0.3', '0.5')}`,
              shadowBlur: 10
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: colors[index].gradient },
                { offset: 1, color: colors[index].gradient.replace('0.3', '0.05') }
              ])
            },
            itemStyle: {
              color: colors[index].color
            },
            data: series.data
          };
        })
      };
      
      this.defectStatisticsChart.setOption(option);
    },
    
    // 更新统计图表类型
    updateStatisticsChart() {
      if (this.defectStatisticsChart) {
        this.defectStatisticsChart.dispose();
        this.renderDefectStatisticsChart();
      }
    }
  }
};
</script>

<style scoped>
/* =========== 全局大屏基调 ============ */
.smart-inspection-system {
  padding: 20px;
  min-height: 100vh;
  box-sizing: border-box;
  background: radial-gradient(1200px 500px at 10% 10%, rgba(20,80,150,0.25), transparent 10%),
              linear-gradient(180deg, #082548 0%, #0a2c5a 45%, #071c42 100%);
  color: #e6f6ff;
  font-family: "Microsoft YaHei", Arial, sans-serif;
  position: relative;
  overflow: auto;
}

/* 系统标题 */
.system-header {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 6px 12px;
  border-bottom: 1px solid rgba(0, 242, 255, 0.1);
  background: linear-gradient(90deg, rgba(0,20,40,0.5), transparent);
}

.system-title {
  font-size: 34px;
  font-weight: 800;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #00f0ff, #4ea7ff 60%, #9b7bff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 20px rgba(0, 240, 255, 0.3);
  margin: 0;
}

.current-time {
  font-size: 14px;
  color: #bfeaff;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(0,200,255,0.3);
  padding: 6px 16px;
  border-radius: 20px;
  font-family: 'Consolas', monospace;
}

/* ===================== 等高布局 ===================== */
.equal-height-row {
  display: flex;
  align-items: stretch;
}

.left-column,
.center-column,
.right-column {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
}

/* ===================== 科技风边框样式 (复用类) ===================== */
.tech-border-card {
  position: relative;
  border-radius: 10px;
  background: linear-gradient(180deg, rgba(12,30,58,0.65), rgba(10,24,46,0.55));
  border: 1px solid rgba(50,180,255,0.2);
  box-shadow: 0 8px 30px rgba(2,40,80,0.4), inset 0 0 15px rgba(0, 200, 255, 0.05);
  margin-bottom: 14px;
  overflow: hidden;
  flex-shrink: 0;
}

.tech-border-card::before {
  content: ""; position: absolute; top: 0; left: 0; right: 0; height: 2px;
  background: linear-gradient(90deg, transparent, #00f0ff, transparent);
  opacity: 0.5;
}

/* ===================== 通用头部样式 ===================== */
.card-header {
  padding: 10px 12px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.header-title-wrapper {
  display: flex; 
  align-items: center; 
  gap: 8px; 
  color: #4ea2ff; 
  font-weight: 700; 
  font-size: 16px;
}

.header-icon { 
  color: #00f0ff; 
  font-size: 18px; 
  text-shadow: 0 0 5px #00f0ff; 
}

.header-title { 
  color: #e6f6ff; 
  letter-spacing: 1px; 
}

/* ===================== 左侧组件样式 ===================== */

/* 预警评分卡片 */
.warning-score-card {
  padding: 12px 16px; 
  height: 180px; 
  box-sizing: border-box;
  display: flex; 
  flex-direction: column;
}

.score-title {
  display: flex; 
  align-items: center; 
  gap: 8px; 
  color: #4ea2ff; 
  font-weight: 700; 
  font-size: 16px; 
  margin-bottom: 10px;
}

.score-icon { 
  color: #ff9900; 
  font-size: 20px; 
  text-shadow: 0 0 8px rgba(255, 153, 0, 0.5);
}

.score-body {
  display: flex; 
  justify-content: space-around; 
  align-items: center; 
  flex: 1;
}

.level-box { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
}

.level-shield {
  width: 70px; 
  height: 80px;
  background: linear-gradient(180deg, #103010, #081808);
  border: 1px solid rgba(50, 255, 50, 0.3);
  border-radius: 8px;
  display: flex; 
  justify-content: center; 
  align-items: center;
  box-shadow: 0 0 15px rgba(50, 255, 50, 0.2);
}

.level-text { 
  font-size: 36px; 
  font-weight: 900; 
  color: #30ff30; 
  text-shadow: 0 0 10px rgba(50, 255, 50, 0.5); 
}

.level-label { 
  font-size: 12px; 
  color: #b3e5b3; 
  margin-top: 5px; 
}

/* 圆环 */
.circle-box { 
  width: 100px; 
  height: 100px; 
  position: relative; 
}

.circle { 
  width: 100%; 
  height: 100%; 
}

svg { 
  transform: rotate(-90deg); 
  width: 100%; 
  height: 100%; 
}

.circle-bg { 
  fill: none; 
  stroke: rgba(255,255,255,0.1); 
  stroke-width: 8; 
}

.circle-progress { 
  fill: none; 
  stroke-width: 8; 
  stroke-linecap: round; 
  transition: stroke-dasharray 1s ease; 
}

.circle-score {
  position: absolute; 
  top: 50%; 
  left: 50%; 
  transform: translate(-50%, -50%);
  color: #fff; 
  font-size: 24px; 
  font-weight: 800;
}

/* 车间缺陷信息卡片 */
.workshop-defect-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.defect-info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.workshop-list-scroll {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

/* 车间项样式 */
.workshop-item {
  padding: 12px;
  background: rgba(0,0,0,0.2);
  margin-bottom: 10px;
  border-radius: 6px;
  border-left: 4px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.workshop-item:hover {
  background: rgba(0,200,255,0.1);
  transform: translateX(5px);
}

.active-workshop {
  border-left-color: #00f0ff;
  background: rgba(0, 200, 255, 0.15);
  box-shadow: 0 0 15px rgba(0, 200, 255, 0.2);
}

.workshop-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.workshop-icon {
  color: #4ea7ff;
  font-size: 16px;
}

.workshop-name {
  color: #e6f6ff;
  font-weight: bold;
  font-size: 14px;
  flex: 1;
}

.defect-tag {
  font-size: 12px;
  padding: 2px 8px;
  height: auto;
}

.workshop-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-label {
  color: #8cbde5;
}

.stat-value {
  color: #ffd666;
  font-weight: bold;
}

.workshop-progress {
  margin-top: 5px;
}

/* 缺陷分布卡片 */
.defect-distribution-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.defect-distribution-chart {
  width: 100%;
  flex: 1;
  min-height: 200px;
}

/* ===================== 中间列样式 ===================== */
.center-column {
  gap: 14px;
}

/* 车间检测状态卡片 */
.workshop-detection-status-card {
  flex: 1.5;
  display: flex;
  flex-direction: column;
}

.monitoring-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scanning-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: 10px;
}

.scanning-active {
  background: rgba(0, 240, 255, 0.2);
  color: #00f0ff;
  border: 1px solid rgba(0, 240, 255, 0.5);
  animation: pulse 1.5s infinite;
}

.scanning-paused {
  background: rgba(255, 153, 0, 0.2);
  color: #ff9900;
  border: 1px solid rgba(255, 153, 0, 0.5);
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

.detection-container {
  flex: 1;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  position: relative;
}

/* 扫描动画 */
.scanning-animation {
  position: relative;
  height: 4px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 2px;
  overflow: hidden;
}

.scanning-line {
  position: absolute;
  top: 0;
  width: 20%;
  height: 100%;
  background: linear-gradient(90deg, 
    rgba(0, 240, 255, 0) 0%, 
    rgba(0, 240, 255, 1) 50%, 
    rgba(0, 240, 255, 0) 100%);
  animation: scanning 3s linear infinite;
}

@keyframes scanning {
  0% { left: -20%; }
  100% { left: 100%; }
}

.scanning-glow {
  position: absolute;
  top: -10px;
  width: 100%;
  height: 24px;
  background: radial-gradient(ellipse at center, 
    rgba(0, 240, 255, 0.3) 0%, 
    rgba(0, 240, 255, 0) 70%);
}

/* 车间扫描状态 */
.workshop-scanning {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.scan-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.active-scan {
  background: rgba(0, 200, 255, 0.15);
  border-color: rgba(0, 240, 255, 0.3);
  box-shadow: 0 0 10px rgba(0, 240, 255, 0.2);
}

.scan-icon {
  font-size: 20px;
}

.scan-icon .el-icon-s-check {
  color: #67c23a;
}

.scan-icon .el-icon-loading {
  color: #00f0ff;
  animation: spin 1s linear infinite;
}

.scan-icon .el-icon-s-promotion {
  color: #e6a23c;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.scan-info {
  flex: 1;
}

.scan-name {
  color: #e6f6ff;
  font-size: 14px;
  font-weight: bold;
}

.scan-status {
  color: #8cbde5;
  font-size: 12px;
}

/* 车间检测状态展示 */
.workshop-status-display {
  flex: 1;
  overflow-y: auto;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  height: 100%;
}

.status-card {
  background: rgba(0, 10, 30, 0.4);
  border-radius: 10px;
  padding: 15px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 280px;
}

.status-card:hover {
  border-color: rgba(0, 240, 255, 0.3);
  box-shadow: 0 0 15px rgba(0, 240, 255, 0.15);
  transform: translateY(-2px);
}

.active-status {
  border-color: #00f0ff !important;
  box-shadow: 0 0 15px rgba(0, 240, 255, 0.3) !important;
  background: rgba(0, 200, 255, 0.1) !important;
}

/* 状态卡片样式 */
.status-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.status-header i {
  font-size: 24px;
}

.status-normal i {
  color: #67c23a;
  text-shadow: 0 0 8px rgba(103, 194, 58, 0.5);
}

.status-good i {
  color: #409eff;
  text-shadow: 0 0 8px rgba(64, 158, 255, 0.5);
}

.status-medium i {
  color: #e6a23c;
  text-shadow: 0 0 8px rgba(230, 162, 60, 0.5);
}

.status-workshop {
  color: #e6f6ff;
  font-weight: bold;
  font-size: 18px;
  flex: 1;
}

.status-level-badge {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.status-normal .status-level-badge {
  background: #67c23a;
}

.status-good .status-level-badge {
  background: #409eff;
}

.status-medium .status-level-badge {
  background: #e6a23c;
}

/* 状态详情 */
.status-details {
  margin-bottom: 15px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
}

.detail-label {
  color: #8cbde5;
}

.detail-value {
  color: #ffd666;
  font-weight: bold;
  font-family: 'Consolas', monospace;
}

.score-value {
  color: #00f0ff;
  font-size: 14px;
}

/* 进度条容器 */
.status-progress-container {
  position: relative;
  margin-bottom: 15px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 5px;
}

.progress-text {
  color: #8cbde5;
  font-size: 12px;
}

.progress-percent {
  color: #ffd666;
  font-weight: bold;
  font-size: 14px;
}

/* 指标显示 */
.status-indicators {
  flex: 1;
  margin-bottom: 15px;
}

.indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.indicator-name {
  color: #8cbde5;
  font-size: 11px;
  width: 50px;
}

.indicator-progress {
  flex: 1;
}

.indicator-value {
  color: #ffd666;
  font-size: 11px;
  font-weight: bold;
  width: 30px;
  text-align: right;
}

/* 底部信息 */
.status-footer-info {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #8cbde5;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 10px;
  margin-top: auto;
}

.footer-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.footer-item i {
  font-size: 12px;
}

/* 预警信息趋势卡片 */
.warning-trend-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.trend-select {
  width: 100px;
  margin-left: auto;
}

.trend-container {
  flex: 1;
  padding: 10px;
}

.warning-trend-chart {
  width: 100%;
  height: 100%;
  min-height: 200px;
}

/* ===================== 右侧列样式 ===================== */
.right-column {
  gap: 14px;
}

/* 车间缺陷占比分析卡片 */
.workshop-analysis-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.workshop-pie-chart {
  width: 100%;
  flex: 1;
  min-height: 200px;
}

/* 缺陷数量统计卡片 */
.defect-statistics-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.statistics-select {
  width: 100px;
  margin-left: auto;
}

.defect-statistics-chart {
  width: 100%;
  flex: 1;
  min-height: 200px;
}

/* 系统状态卡片 */
.status-footer {
  padding: 15px;
  margin-top: auto;
}

.system-status {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.status-icon {
  font-size: 14px;
}

.el-icon-success { color: #67c23a; }
.el-icon-error { color: #f56c6c; }
.refresh-icon { color: #409eff; }
.scan-icon { color: #00f0ff; }

.status-text,
.refresh-text,
.scan-text {
  color: #bfeaff;
}

/* 图表区域通用样式 */
.chart-section {
  flex: 1;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 滚动条 */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-thumb { background: rgba(0, 200, 255, 0.2); border-radius: 3px; }
::-webkit-scrollbar-track { background: rgba(0,0,0,0.1); }

/* 深度选择器修改ElementUI组件样式 */
:deep(.el-select .el-input__inner) {
  background: rgba(0, 20, 40, 0.5);
  border-color: rgba(0, 200, 255, 0.3);
  color: #e6f6ff;
}

:deep(.el-select .el-input__inner:focus) {
  border-color: #00f0ff;
}

:deep(.el-select-dropdown) {
  background: rgba(12, 30, 58, 0.95);
  border: 1px solid rgba(0, 200, 255, 0.3);
}

:deep(.el-select-dropdown__item) {
  color: #e6f6ff;
}

:deep(.el-select-dropdown__item.hover),
:deep(.el-select-dropdown__item:hover) {
  background: rgba(0, 200, 255, 0.2);
}

:deep(.el-select-dropdown__item.selected) {
  color: #00f0ff;
  background: rgba(0, 200, 255, 0.1);
}

:deep(.el-progress-bar__inner) {
  transition: width 0.5s ease;
}

:deep(.el-tag) {
  background: transparent;
  border: 1px solid;
}

:deep(.el-tag--danger) {
  color: #f56c6c;
  border-color: #f56c6c;
}

:deep(.el-tag--warning) {
  color: #e6a23c;
  border-color: #e6a23c;
}

:deep(.el-tag--success) {
  color: #67c23a;
  border-color: #67c23a;
}
</style>