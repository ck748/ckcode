<template>
  <div class="ai-inspection-system">
    <!-- 顶部状态栏 -->
    <div class="top-bar">
      <div class="system-info">
        <div class="logo">
          <i class="el-icon-s-platform"></i>
          <span class="system-name">车间AI智能检测系统</span>
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

        <!-- 生产效率图表 -->
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
        <!-- AI检测主视图 -->
        <div class="ai-detection-view">
          <div class="detection-header">
            <div class="detection-info">
              <h3 class="detection-title">
                <i class="el-icon-cpu"></i>
                AI实时检测中 - {{ currentWorkshop.name }}
              </h3>
              <div class="detection-subtitle">{{ currentWorkshop.process }}</div>
            </div>
            <div class="detection-controls">
              <el-button-group>
                <el-button size="small" @click="prevDetection" :disabled="!isDetecting">
                  <i class="el-icon-arrow-left"></i>
                </el-button>
                <el-button 
                  size="small" 
                  :type="isDetecting ? 'warning' : 'primary'" 
                  @click="toggleDetection"
                  :loading="loading"
                >
                  <i :class="isDetecting ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
                  {{ isDetecting ? '检测分析中' : '检测中' }}
                </el-button>
                <el-button size="small" @click="nextDetection" :disabled="!isDetecting">
                  <i class="el-icon-arrow-right"></i>
                </el-button>
              </el-button-group>
            </div>
          </div>

          <!-- AI检测可视化 -->
          <div class="detection-visualization">
            <div class="visualization-frame">
              <!-- 检测产品 -->
              <div class="product-container">
                <div class="product-model" :class="currentProduct.type">
                  <div class="model-inner">
                    <div class="model-glow"></div>
                  </div>
                </div>
                
                <!-- AI扫描线 -->
                <div class="ai-scan-line" :style="{ top: scanPosition + '%' }"></div>
                
                <!-- 检测点 -->
                <div 
                  v-for="point in detectionPoints" 
                  :key="point.id"
                  class="detection-point"
                  :style="{
                    left: point.x + '%',
                    top: point.y + '%'
                  }"
                  :class="point.status"
                >
                  <div class="point-pulse"></div>
                  <div class="point-info">
                    {{ point.name }}
                  </div>
                </div>
              </div>

              <!-- 检测进度 -->
              <div class="detection-progress">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: detectionProgress + '%' }"
                  ></div>
                </div>
                <div class="progress-info">
                  <span class="step-text">{{ detectionStep }}</span>
                  <span class="progress-text">{{ detectionProgress }}%</span>
                </div>
              </div>
            </div>

            <!-- 实时数据 -->
            <div class="realtime-data">
              <div class="data-header">
                <i class="el-icon-data-line"></i>
                <span>实时监测数据</span>
              </div>
              <div class="data-grid">
                <div 
                  v-for="item in realtimeData" 
                  :key="item.id"
                  class="data-item"
                >
                  <div class="data-label">{{ item.label }}</div>
                  <div class="data-value">
                    {{ item.value }}
                    <span class="data-unit">{{ item.unit }}</span>
                  </div>
                  <div class="data-trend" :class="getTrendClass(item.trend)">
                    <i :class="getTrendIcon(item.trend)"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 检测参数面板 -->
        <div class="parameter-panel">
          <div class="panel-header">
            <i class="el-icon-s-operation"></i>
            <span>检测参数配置</span>
          </div>
          <div class="parameters-grid">
            <div 
              v-for="param in currentParameters" 
              :key="param.name"
              class="parameter-item"
            >
              <div class="param-info">
                <div class="param-name">{{ param.name }}</div>
                <div class="param-value">
                  {{ param.current }}
                  <span class="param-unit">{{ param.unit }}</span>
                </div>
              </div>
              <div class="param-range">
                <div class="range-bar">
                  <div 
                    class="range-fill"
                    :style="{ width: param.percentage + '%' }"
                  ></div>
                </div>
                <div class="range-labels">
                  <span>{{ param.min }}{{ param.unit }}</span>
                  <span>{{ param.max }}{{ param.unit }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <!-- 质量统计 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-claim"></i>
            <span>质量统计</span>
          </div>
          <div class="quality-stats">
            <div class="stat-item">
              <div class="stat-header">
                <div class="stat-title">合格率</div>
                <div class="stat-trend up">
                  <i class="el-icon-top"></i>
                  <span>+1.2%</span>
                </div>
              </div>
              <div class="stat-value">92.7%</div>
              <div class="stat-progress">
                <el-progress 
                  :percentage="98.7" 
                  :show-text="false"
                  stroke-width="8"
                  color="#52c41a"
                />
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-header">
                <div class="stat-title">缺陷率</div>
                <div class="stat-trend down">
                  <i class="el-icon-bottom"></i>
                  <span>-0.8%</span>
                </div>
              </div>
              <div class="stat-value">1.3%</div>
              <div class="stat-progress">
                <el-progress 
                  :percentage="1.3" 
                  :show-text="false"
                  stroke-width="8"
                  color="#f5222d"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 产量统计 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-marketing"></i>
            <span>产量统计</span>
          </div>
          <div class="production-chart">
            <div id="productionChart" class="chart"></div>
          </div>
        </div>

        <!-- 设备状态 -->
        <div class="panel-card">
          <div class="panel-header">
            <i class="el-icon-s-tools"></i>
            <span>设备状态</span>
          </div>
          <div class="equipment-status">
            <div class="equipment-item" v-for="equip in equipmentList" :key="equip.id">
              <div class="equip-info">
                <div class="equip-name">{{ equip.name }}</div>
                <div class="equip-value">{{ equip.value }}</div>
              </div>
              <div class="equip-status">
                <div class="status-dot" :class="equip.status"></div>
                <span class="status-text">{{ getStatusText(equip.status) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部状态栏 -->
    <div class="bottom-bar">
      <div class="status-items">
        <div class="status-item">
          <i class="el-icon-cpu"></i>
          <span>AI算力</span>
          <span class="value">85%</span>
        </div>
        <div class="status-item">
          <i class="el-icon-connection"></i>
          <span>设备连接</span>
          <span class="value">24/26</span>
        </div>
        <div class="status-item">
          <i class="el-icon-timer"></i>
          <span>检测时长</span>
          <span class="value">{{ formatTime(totalDetectionTime) }}</span>
        </div>
        <div class="status-item">
          <i class="el-icon-document-checked"></i>
          <span>完成批次</span>
          <span class="value">156</span>
        </div>
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
      
      // 车间数据
      workshops: [
        {
          id: 1,
          name: '一车间',
          process: '切取TN+压花罐',
          icon: 'el-icon-s-shop',
          status: '运行中',
          efficiency: 88.5
        },
        {
          id: 2,
          name: '二车间',
          process: '钻中心孔-粗抛丸',
          icon: 'el-icon-s-tools',
          status: '运行中',
          efficiency: 92.1
        },
        {
          id: 3,
          name: '三车间',
          process: '精车杆-精装',
          icon: 'el-icon-s-promotion',
          status: '待检中',
          efficiency: 65.3
        },
        {
          id: 4,
          name: '四车间',
          process: '探伤、包装',
          icon: 'el-icon-s-claim',
          status: '运行中',
          efficiency: 95.8
        }
      ],
      activeWorkshop: 1,
      
      // AI检测状态
      isDetecting: false,
      loading: false,
      detectionProgress: 0,
      detectionStep: '等待开始检测...',
      scanPosition: 0,
      detectionTimer: null,
      progressTimer: null,
      
      // 检测点
      detectionPoints: [
        { id: 1, name: '一车间', x: 30, y: 40, status: 'normal' },
        { id: 2, name: '二车间', x: 60, y: 30, status: 'normal' },
        { id: 3, name: '三车间', x: 45, y: 60, status: 'warning' },
        { id: 4, name: '四车间', x: 70, y: 50, status: 'normal' }
      ],
      
      // 当前产品
      currentProduct: {
        type: 'cylinder',
        status: 'normal'
      },
      
      // 实时数据
      realtimeData: [
        { id: 1, label: '温度监测', value: '52.3', unit: '℃', trend: 'stable' },
        { id: 2, label: '压力监测', value: '0.041', unit: 'MPa', trend: 'up' },
        { id: 3, label: '视觉识别', value: '98.7', unit: '%', trend: 'stable' },
        { id: 4, label: '转速监控', value: '382', unit: 'rpm', trend: 'down' }
      ],
      
      // 检测参数
      currentParameters: [
        {
          name: '压花罐温度',
          current: '50',
          unit: '℃',
          min: '30',
          max: '70',
          percentage: 71
        },
        {
          name: '切割精度',
          current: '±0.005',
          unit: 'mm',
          min: '0',
          max: '±0.01',
          percentage: 50
        },
        {
          name: '设备利用率',
          current: '62',
          unit: '%',
          min: '0',
          max: '100',
          percentage: 62
        }
      ],
      
      // 设备状态
      equipmentList: [
        { id: 1, name: '压花罐', value: '50℃', status: 'normal' },
        { id: 2, name: '切割机', value: '400mm', status: 'normal' },
        { id: 3, name: '钻孔机', value: '380rpm', status: 'warning' },
        { id: 4, name: '抛丸机', value: '0.04MPa', status: 'normal' }
      ],
      
      // 总检测时间（秒）
      totalDetectionTime: 4526,
      
      // 图表实例
      efficiencyChart: null,
      productionChart: null
    };
  },
  
  computed: {
    currentWorkshop() {
      return this.workshops.find(w => w.id === this.activeWorkshop) || this.workshops[0];
    }
  },
  
  mounted() {
    this.initTime();
    this.initCharts();
    this.updateTime();
  },
  
  beforeDestroy() {
    this.stopDetection();
    this.clearCharts();
    if (this.timeInterval) clearInterval(this.timeInterval);
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
    
    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        this.renderEfficiencyChart();
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
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['一车间', '二车间', '三车间', '四车间'],
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666'
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
            color: '#666'
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          }
        },
        series: [{
          data: [88.5, 92.1, 65.3, 95.8],
          type: 'bar',
          barWidth: '60%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#1890ff' },
              { offset: 1, color: '#36cfc9' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}%',
            color: '#333',
            fontWeight: 'bold'
          }
        }]
      };
      
      this.efficiencyChart.setOption(option);
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
          textStyle: { color: '#333' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['6:00', '8:00', '10:00', '12:00', '14:00', '16:00'],
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666'
          }
        },
        yAxis: {
          type: 'value',
          name: '产量(件)',
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666'
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          }
        },
        series: [{
          data: [820, 932, 901, 934, 1290, 1330],
          type: 'line',
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
          }
        }]
      };
      
      this.productionChart.setOption(option);
    },
    
    // 切换车间
    switchWorkshop(id) {
      this.activeWorkshop = id;
      this.stopDetection();
      this.resetDetection();
      this.updateParameters();
    },
    
    updateParameters() {
      // 根据不同车间更新参数
      const paramsMap = {
        1: [
          {
            name: '压花罐温度',
            current: '50',
            unit: '℃',
            min: '30',
            max: '70',
            percentage: 71
          },
          {
            name: '切割精度',
            current: '±0.005',
            unit: 'mm',
            min: '0',
            max: '±0.01',
            percentage: 50
          },
          {
            name: '设备利用率',
            current: '62',
            unit: '%',
            min: '0',
            max: '100',
            percentage: 62
          }
        ],
        2: [
          {
            name: '钻孔孔径',
            current: '12.5',
            unit: 'mm',
            min: '10',
            max: '15',
            percentage: 50
          },
          {
            name: '钻孔速度',
            current: '380',
            unit: 'rpm',
            min: '300',
            max: '450',
            percentage: 53
          },
          {
            name: '抛丸压力',
            current: '0.04',
            unit: 'MPa',
            min: '0.03',
            max: '0.05',
            percentage: 50
          }
        ],
        3: [
          {
            name: '加工精度',
            current: '±0.005',
            unit: 'mm',
            min: '0',
            max: '±0.01',
            percentage: 50
          },
          {
            name: '设备温度',
            current: '60',
            unit: '℃',
            min: '40',
            max: '80',
            percentage: 67
          },
          {
            name: '恢复时间',
            current: '60',
            unit: '分钟',
            min: '0',
            max: '120',
            percentage: 50
          }
        ],
        4: [
          {
            name: '探伤灵敏度',
            current: '0.5',
            unit: 'mm',
            min: '0.2',
            max: '1.0',
            percentage: 43
          },
          {
            name: '检测速度',
            current: '3',
            unit: 'm/min',
            min: '2',
            max: '5',
            percentage: 33
          },
          {
            name: '包装速度',
            current: '134',
            unit: 't/分钟',
            min: '100',
            max: '150',
            percentage: 68
          }
        ]
      };
      
      this.currentParameters = paramsMap[this.activeWorkshop] || paramsMap[1];
    },
    
    // AI检测控制
    toggleDetection() {
      if (this.isDetecting) {
        this.stopDetection();
      } else {
        this.startDetection();
      }
    },
    
    startDetection() {
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
        this.isDetecting = true;
        this.startDetectionProgress();
        this.startScanAnimation();
        this.startTimeUpdate();
      }, 800);
    },
    
    startDetectionProgress() {
      this.detectionProgress = 0;
      this.detectionStep = '初始化检测环境...';
      
      const steps = [
        { progress: 20, step: '图像采集...' },
        { progress: 40, step: 'AI特征提取...' },
        { progress: 60, step: '缺陷识别...' },
        { progress: 80, step: '数据分析...' },
        { progress: 95, step: '生成报告...' }
      ];
      
      let currentStep = 0;
      
      this.progressTimer = setInterval(() => {
        if (currentStep < steps.length) {
          this.detectionProgress = steps[currentStep].progress;
          this.detectionStep = steps[currentStep].step;
          currentStep++;
        } else {
          if (this.detectionProgress < 100) {
            this.detectionProgress += 1;
          } else {
            this.detectionStep = '检测完成！';
            setTimeout(() => {
              this.stopDetection();
              this.nextDetection();
            }, 1500);
          }
        }
        
        // 更新实时数据
        this.updateRealtimeData();
      }, 300);
    },
    
    startScanAnimation() {
      this.scanPosition = 0;
      this.detectionTimer = setInterval(() => {
        this.scanPosition = (this.scanPosition + 2) % 100;
      }, 50);
    },
    
    stopDetection() {
      this.isDetecting = false;
      if (this.progressTimer) {
        clearInterval(this.progressTimer);
        this.progressTimer = null;
      }
      if (this.detectionTimer) {
        clearInterval(this.detectionTimer);
        this.detectionTimer = null;
      }
    },
    
    resetDetection() {
      this.detectionProgress = 0;
      this.detectionStep = '等待开始检测...';
      this.scanPosition = 0;
    },
    
    prevDetection() {
      this.activeWorkshop = this.activeWorkshop > 1 ? this.activeWorkshop - 1 : 4;
      this.resetDetection();
      this.updateParameters();
    },
    
    nextDetection() {
      this.activeWorkshop = this.activeWorkshop < 4 ? this.activeWorkshop + 1 : 1;
      this.resetDetection();
      this.updateParameters();
    },
    
    // 更新实时数据
    updateRealtimeData() {
      this.realtimeData = this.realtimeData.map(item => {
        const random = Math.random();
        let newTrend = item.trend;
        let newValue = parseFloat(item.value);
        
        if (random < 0.3) {
          newTrend = 'up';
          newValue += (Math.random() * 2 - 0.5);
        } else if (random < 0.6) {
          newTrend = 'down';
          newValue += (Math.random() * 1 - 0.5);
        } else {
          newTrend = 'stable';
          newValue += (Math.random() * 0.5 - 0.25);
        }
        
        // 确保值在合理范围内
        if (item.unit === '℃') {
          newValue = Math.max(30, Math.min(70, newValue));
          return {
            ...item,
            value: newValue.toFixed(1),
            trend: newTrend
          };
        } else if (item.unit === 'MPa') {
          newValue = Math.max(0.03, Math.min(0.05, newValue));
          return {
            ...item,
            value: newValue.toFixed(3),
            trend: newTrend
          };
        } else if (item.unit === '%') {
          newValue = Math.max(95, Math.min(100, newValue));
          return {
            ...item,
            value: newValue.toFixed(1),
            trend: newTrend
          };
        } else {
          newValue = Math.max(350, Math.min(400, newValue));
          return {
            ...item,
            value: Math.round(newValue),
            trend: newTrend
          };
        }
      });
    },
    
    // 更新时间
    startTimeUpdate() {
      const timer = setInterval(() => {
        if (this.isDetecting) {
          this.totalDetectionTime += 1;
        } else {
          clearInterval(timer);
        }
      }, 1000);
    },
    
    updateTime() {
      setInterval(() => {
        this.updateCurrentTime();
      }, 1000);
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
    
    getTrendClass(trend) {
      return `trend-${trend}`;
    },
    
    getTrendIcon(trend) {
      const icons = {
        up: 'el-icon-top',
        down: 'el-icon-bottom',
        stable: 'el-icon-right'
      };
      return icons[trend] || 'el-icon-right';
    },
    
    getStatusText(status) {
      const map = {
        normal: '正常',
        warning: '警告',
        error: '异常'
      };
      return map[status] || '正常';
    },
    
    formatTime(seconds) {
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    },
    
    // 清理图表
    clearCharts() {
      if (this.efficiencyChart) {
        this.efficiencyChart.dispose();
        this.efficiencyChart = null;
      }
      if (this.productionChart) {
        this.productionChart.dispose();
        this.productionChart = null;
      }
    }
  }
};
</script>

<style scoped>
.ai-inspection-system {
  height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  overflow: hidden;
}

/* 顶部状态栏 */
.top-bar {
  height: 60px;
  background: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-bottom: 1px solid #f0f0f0;
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

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  padding: 16px;
  gap: 16px;
  overflow: hidden;
}

/* 面板通用样式 */
.panel-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
  overflow: hidden;
}

.panel-header {
  padding: 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #333;
}

.panel-header i {
  color: #1890ff;
}

/* 左侧面板 */
.left-panel {
  width: 300px;
  display: flex;
  flex-direction: column;
}

.workshop-list {
  padding: 12px;
}

.workshop-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
}

.workshop-item:hover {
  background: #f5f5f5;
}

.workshop-item.active {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
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
}

.workshop-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
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
}

.workshop-process {
  color: #666;
  font-size: 12px;
}

.chart-container {
  padding: 16px;
  height: 200px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 中间面板 */
.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ai-detection-view {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.detection-header {
  padding: 20px 24px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detection-info {
  flex: 1;
}

.detection-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detection-title i {
  color: #1890ff;
}

.detection-subtitle {
  color: #666;
  font-size: 14px;
}

.detection-controls {
  margin-left: 20px;
}

.detection-visualization {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.visualization-frame {
  flex: 1;
  position: relative;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  padding: 24px;
  overflow: hidden;
}

.product-container {
  position: relative;
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-model {
  width: 180px;
  height: 180px;
  position: relative;
}

.product-model.cylinder {
  width: 180px;
  height: 240px;
}

.model-inner {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  border-radius: 50%;
  position: relative;
  overflow: hidden;
  animation: rotate 20s linear infinite;
}

.product-model.cylinder .model-inner {
  border-radius: 40% 40% 50% 50% / 60% 60% 40% 40%;
}

.model-glow {
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  background: radial-gradient(circle at center, rgba(24, 144, 255, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  animation: glow 2s ease-in-out infinite alternate;
}

.ai-scan-line {
  position: absolute;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #1890ff, transparent);
  z-index: 2;
  animation: scan 3s linear infinite;
}

@keyframes scan {
  0% { top: 0%; }
  100% { top: 100%; }
}

.detection-point {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 3;
}

.detection-point.normal .point-pulse {
  background: #52c41a;
}

.detection-point.warning .point-pulse {
  background: #fa8c16;
}

.detection-point.error .point-pulse {
  background: #f5222d;
}

.point-pulse {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  position: relative;
  animation: pointPulse 2s infinite;
}

.point-info {
  position: absolute;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
  color: #333;
}

@keyframes pointPulse {
  0% { transform: scale(1); opacity: 1; }
  100% { transform: scale(2); opacity: 0; }
}

.detection-progress {
  margin-top: 24px;
}

.progress-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #36cfc9, #1890ff);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.step-text {
  color: #666;
  font-size: 14px;
}

.progress-text {
  color: #1890ff;
  font-weight: 600;
}

.realtime-data {
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  padding: 16px;
}

.data-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #333;
  font-weight: 500;
}

.data-header i {
  color: #1890ff;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.data-item {
  background: white;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.data-label {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.data-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.data-unit {
  font-size: 14px;
  color: #999;
  font-weight: normal;
}

.data-trend {
  font-size: 12px;
}

.trend-up {
  color: #52c41a;
}

.trend-down {
  color: #f5222d;
}

.trend-stable {
  color: #1890ff;
}

.parameter-panel {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.parameters-grid {
  padding: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.parameter-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.param-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.param-name {
  color: #333;
  font-weight: 500;
}

.param-value {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.param-unit {
  font-size: 14px;
  color: #999;
  font-weight: normal;
  margin-left: 2px;
}

.param-range {
  margin-top: 12px;
}

.range-bar {
  height: 6px;
  background: #e8e8e8;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 4px;
}

.range-fill {
  height: 100%;
  background: linear-gradient(90deg, #36cfc9, #1890ff);
  border-radius: 3px;
}

.range-labels {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

/* 右侧面板 */
.right-panel {
  width: 300px;
  display: flex;
  flex-direction: column;
}

.quality-stats {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-item {
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stat-title {
  color: #333;
  font-weight: 500;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-trend.up {
  color: #52c41a;
}

.stat-trend.down {
  color: #f5222d;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.stat-progress {
  margin-top: 8px;
}

.production-chart {
  padding: 16px;
  height: 200px;
}

.equipment-status {
  padding: 12px;
}

.equipment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.equipment-item:last-child {
  border-bottom: none;
}

.equip-info {
  flex: 1;
}

.equip-name {
  color: #333;
  margin-bottom: 4px;
}

.equip-value {
  color: #666;
  font-size: 14px;
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
  font-size: 12px;
}

/* 底部状态栏 */
.bottom-bar {
  height: 50px;
  background: white;
  border-top: 1px solid #f0f0f0;
  padding: 0 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
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
  font-size: 14px;
}

.status-item i {
  color: #1890ff;
}

.status-item .value {
  margin-left: 4px;
  font-weight: 600;
  color: #333;
}

/* 动画 */
@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes glow {
  0% { opacity: 0.5; transform: scale(1); }
  100% { opacity: 0.8; transform: scale(1.1); }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 响应式 */
@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
  }
  
  .left-panel,
  .right-panel {
    width: 100%;
  }
  
  .data-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .data-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .parameters-grid {
    grid-template-columns: 1fr;
  }
}

/* 滚动条样式 */
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
</style>