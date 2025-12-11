<template>
  <div class="blockchain-dashboard">
    <!-- 动态科技背景 -->
    <div class="tech-bg">
      <div class="grid-overlay"></div>
      <div class="radar-sweep"></div>
      <div class="particles-container" ref="particlesContainer"></div>
      <div class="data-flow"></div>
    </div>

    <!-- 顶部标题栏 -->
    <header class="dashboard-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="main-title">
            <span class="title-glow">工业半轴</span>
            <span class="title-sub">区块链溯源监控中心</span>
          </h1>
          <div class="title-tag">
            <span class="tag-icon">◉</span>
            <span class="tag-text">LIVE DATA STREAMING</span>
          </div>
        </div>
        
        <div class="header-controls">
          <div class="time-display">
            <div class="time-label">链上时间</div>
            <div class="time-value">{{ currentTime }}</div>
          </div>
          
          <div class="control-group">
            <div class="control-btn holographic" @click="refreshData">
              <div class="btn-icon">↻</div>
              <div class="btn-label">数据同步</div>
            </div>
            
            <div class="control-btn holographic" @click="toggleDataStream">
              <div class="btn-icon">{{ dataStreaming ? '⏸' : '▶' }}</div>
              <div class="btn-label">{{ dataStreaming ? '暂停' : '实时' }}</div>
            </div>
            
            <div class="custom-select">
              <select v-model="dataMode" @change="changeDataMode">
                <option value="realtime">实时监控</option>
                <option value="history">历史追溯</option>
                <option value="analysis">智能分析</option>
              </select>
              <div class="select-arrow">▼</div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="dashboard-content">
      <!-- 左侧：上链总览 -->
      <div class="panel left-panel">
        <div class="panel-header">
          <h3 class="panel-title">
            <span class="title-line"></span>
            <span>上链总览</span>
          </h3>
          <div class="panel-subtitle">BLOCKCHAIN OVERVIEW</div>
        </div>
        
        <div class="chain-stats">
          <div class="stat-card neon-card" v-for="stat in stats" :key="stat.label">
            <div class="stat-icon">{{ stat.icon }}</div>
            <div class="stat-content">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-trend" :class="stat.trendClass">
                {{ stat.trend }}
              </div>
            </div>
          </div>
        </div>

        <!-- 实时上链曲线 -->
        <div class="realtime-chart">
          <div class="chart-header">
            <h4>实时上链趋势</h4>
            <div class="chart-controls">
              <div class="time-range">
                <span 
                  v-for="range in timeRanges" 
                  :key="range.value"
                  :class="{active: currentRange === range.value}"
                  @click="changeRange(range.value)"
                >
                  {{ range.label }}
                </span>
              </div>
            </div>
          </div>
          <div id="chainChart" class="chart-container"></div>
        </div>
      </div>

      <!-- 中间：区块链可视化 -->
      <div class="panel center-panel">
        <div class="panel-header">
          <h3 class="panel-title">
            <span class="title-line"></span>
            <span>区块链可视化</span>
          </h3>
          <div class="panel-subtitle">BLOCKCHAIN VISUALIZATION</div>
        </div>
        
        <!-- 区块链可视化 -->
        <div class="blockchain-visualization">
          <div class="visualization-container">
            <!-- 连接线 -->
            <div class="chain-line"></div>
            
            <!-- 区块节点 -->
            <div 
              class="block-node" 
              v-for="(block, index) in recentBlocks" 
              :key="block.hash"
              :style="getNodeStyle(index)"
              @click="inspectBlock(block)"
            >
              <div class="node-core" :class="{active: selectedBlock?.hash === block.hash}">
                <div class="node-pulse"></div>
                <div class="node-content">
                  <div class="node-number">#{{ block.number }}</div>
                  <div class="node-time">{{ formatBlockTime(block.timestamp) }}</div>
                </div>
              </div>
              <div class="node-connection" v-if="index < recentBlocks.length - 1"></div>
            </div>
          </div>
          
          <!-- 区块链控制 -->
          <div class="chain-controls">
            <div class="control-btn" @click="rotateVisual(-15)">
              <div class="control-icon">◀</div>
              <div class="control-label">左旋</div>
            </div>
            <div class="control-btn" @click="toggleAnimation">
              <div class="control-icon">{{ isAnimating ? '⏸' : '▶' }}</div>
              <div class="control-label">{{ isAnimating ? '暂停' : '动画' }}</div>
            </div>
            <div class="control-btn" @click="rotateVisual(15)">
              <div class="control-icon">▶</div>
              <div class="control-label">右旋</div>
            </div>
          </div>
        </div>

        <!-- 区块详情 -->
        <div class="block-detail" v-if="selectedBlock">
          <div class="detail-header">
            <h4>区块详情 <span class="block-count">(总区块数: {{ formatNumber(blockchainStats.totalBlocks) }})</span></h4>
            <div class="block-status">
              <span class="status-dot valid"></span>
              <span>已验证</span>
            </div>
          </div>
          <div class="detail-content">
            <div class="detail-grid">
              <div class="detail-item">
                <label>区块高度</label>
                <span class="value-highlight">#{{ selectedBlock.number }}</span>
              </div>
              <div class="detail-item">
                <label>哈希值</label>
                <span class="value-code">{{ selectedBlock.hash.slice(0, 20) }}...</span>
              </div>
              <div class="detail-item">
                <label>时间戳</label>
                <span>{{ formatTime(selectedBlock.timestamp) }}</span>
              </div>
              <div class="detail-item">
                <label>上链产品</label>
                <span class="product-value">{{ selectedBlock.productId }}</span>
              </div>
            </div>
            
            <div class="block-data">
              <label>区块数据</label>
              <div class="data-content">
                <div class="data-item" v-for="(value, key) in selectedBlock.data" :key="key">
                  <span class="data-key">{{ dataLabels[key] || key }}：</span>
                  <span class="data-value">{{ value }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：产品溯源 -->
      <div class="panel right-panel">
        <div class="panel-header">
          <h3 class="panel-title">
            <span class="title-line"></span>
            <span>产品溯源</span>
          </h3>
          <div class="panel-subtitle">PRODUCT TRACEABILITY</div>
        </div>
        
        <!-- 溯源查询 -->
        <div class="trace-query">
          <div class="query-input-group">
            <div class="input-prefix">
              <span class="prefix-icon">🔍</span>
              <span class="prefix-text">产品溯源查询</span>
            </div>
            <div class="custom-input">
              <input 
                v-model="queryId" 
                placeholder="输入产品ID或批次号"
                @keyup.enter="searchTrace"
              />
              <button class="query-btn" @click="searchTrace">
                查询
              </button>
            </div>
          </div>
          
          <div class="query-examples">
            <span class="example-label">示例:</span>
            <span 
              v-for="example in examples" 
              :key="example"
              class="example-item"
              @click="queryId = example; searchTrace()"
            >
              {{ example }}
            </span>
          </div>
        </div>

        <!-- 溯源结果容器 -->
        <div class="trace-result-container" ref="traceResultContainer">
          <div class="trace-result" v-if="traceResult">
            <div class="result-header">
              <div class="result-title">
                <h4>溯源结果</h4>
                <div class="product-id-display">{{ traceResult.productId }}</div>
              </div>
              <div class="result-status">
                <span class="status-badge complete">完整溯源</span>
                <span class="status-time">{{ traceResult.updateTime }}</span>
              </div>
            </div>
            
            <div class="product-quick-info">
              <div class="quick-info-item">
                <span class="info-label">产品批次</span>
                <span class="info-value">{{ traceResult.batch }}</span>
              </div>
              <div class="quick-info-item">
                <span class="info-label">生产状态</span>
                <span class="info-value success">{{ traceResult.status }}</span>
              </div>
              <div class="quick-info-item">
                <span class="info-label">质量评级</span>
                <span class="info-value excellent">{{ traceResult.quality }}</span>
              </div>
            </div>

            <!-- 可滚动的时间线 -->
            <div class="trace-timeline-wrapper">
              <div class="timeline-header">
                <div class="timeline-title">生产溯源流程</div>
                <div class="timeline-stats">
                  <span>共 {{ traceResult.steps.length }} 个工序</span>
                  <span class="scroll-hint">↕️ 可滚动查看更多</span>
                </div>
              </div>
              
              <div class="timeline-container" ref="timelineContainer">
                <div 
                  class="timeline-item" 
                  v-for="(step, index) in traceResult.steps" 
                  :key="index"
                >
                  <div class="timeline-marker">
                    <div class="marker-icon">{{ step.icon }}</div>
                    <div class="marker-line" v-if="index < traceResult.steps.length - 1"></div>
                  </div>
                  <div class="timeline-content">
                    <div class="timeline-header">
                      <span class="timeline-stage">{{ step.stage }}</span>
                      <span class="timeline-time">{{ step.time }}</span>
                    </div>
                    <div class="timeline-desc">{{ step.description }}</div>
                    <div class="timeline-meta">
                      <span class="meta-item" v-if="step.operator">
                        <span class="meta-icon">👤</span>
                        {{ step.operator }}
                      </span>
                      <span class="meta-item" v-if="step.equipment">
                        <span class="meta-icon">⚙️</span>
                        {{ step.equipment }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 最近查询记录 -->
          <div class="recent-queries" v-if="recentQueries.length > 0">
            <div class="queries-header">
              <h4>最近查询记录</h4>
              <div class="queries-controls">
                <span class="queries-count">共 {{ recentQueries.length }} 条</span>
                <button class="clear-btn" @click="clearRecentQueries">清空</button>
              </div>
            </div>
            <div class="queries-list">
              <div 
                class="query-item" 
                v-for="(query, index) in recentQueries" 
                :key="index"
                @click="queryId = query.id; searchTrace()"
              >
                <div class="query-icon">📋</div>
                <div class="query-info">
                  <div class="query-id">{{ query.id }}</div>
                  <div class="query-meta">
                    <span class="query-time">{{ query.time }}</span>
                    <span class="query-status" :class="query.statusClass">{{ query.status }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 底部状态栏 -->
    <footer class="dashboard-footer">
      <div class="footer-content">
        <div class="system-status">
          <div class="status-item">
            <span class="status-dot online"></span>
            <span>区块链网络: <strong>运行中</strong></span>
          </div>
          <div class="status-item">
            <span class="status-dot sync"></span>
            <span>数据同步: <strong>{{ syncProgress }}%</strong></span>
          </div>
          <div class="status-item">
            <span class="status-dot nodes"></span>
            <span>活跃节点: <strong>{{ blockchainStats.activeNodes }}</strong></span>
          </div>
          <div class="status-item">
            <span class="status-dot transaction"></span>
            <span>今日上链: <strong>{{ formatNumber(blockchainStats.todayTransactions) }}</strong></span>
          </div>
        </div>
        
        <div class="data-info">
          <span class="info-item">区块高度: <strong>#{{ formatNumber(blockchainStats.totalBlocks) }}</strong></span>
          <span class="info-item">出块时间: <strong>{{ blockchainStats.blockTime }}</strong></span>
          <span class="info-item">数据完整率: <strong>{{ blockchainStats.dataIntegrity }}</strong></span>
        </div>
      </div>
    </footer>

    <!-- 消息提示 -->
    <div class="message-container">
      <div v-if="showMessage" class="message" :class="messageType">
        <div class="message-icon">
          <span v-if="messageType === 'success'">✓</span>
          <span v-if="messageType === 'info'">ℹ</span>
          <span v-if="messageType === 'warning'">⚠</span>
        </div>
        <div class="message-text">{{ messageText }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'BlockchainDashboard',
  data() {
    return {
      // 系统状态
      dataMode: 'realtime',
      dataStreaming: true,
      currentTime: '',
      syncProgress: 100,
      isAnimating: true,
      visualRotation: 0,
      
      // 消息提示
      showMessage: false,
      messageText: '',
      messageType: 'success',
      
      // 统计数据
      stats: [
        {
          icon: '⛓️',
          value: '18,472',
          label: '区块总数',
          trend: '↑ 12个/小时',
          trendClass: 'trend-up'
        },
        {
          icon: '📦',
          value: '1,248',
          label: '今日上链',
          trend: '↑ 86件',
          trendClass: 'trend-up'
        },
        {
          icon: '⚡',
          value: '3.2s',
          label: '平均出块',
          trend: '↓ 0.2s',
          trendClass: 'trend-down'
        },
        {
          icon: '🔗',
          value: '99.98%',
          label: '数据完整率',
          trend: '保持稳定',
          trendClass: 'trend-stable'
        }
      ],
      
      // 区块链统计数据
      blockchainStats: {
        totalBlocks: 18472,
        todayTransactions: 5234,
        activeNodes: 12,
        blockTime: '3.2s',
        dataIntegrity: '99.98%'
      },
      
      // 查询相关
      queryId: '',
      currentRange: '1h',
      selectedBlock: null,
      traceResult: null,
      recentQueries: [],
      
      // 时间范围选项
      timeRanges: [
        { label: '1小时', value: '1h' },
        { label: '24小时', value: '24h' },
        { label: '7天', value: '7d' },
        { label: '30天', value: '30d' }
      ],
      
      // 示例产品ID
      examples: ['AX20231215001', 'AX20231214012', 'AX20231213045', 'AX20231212034'],
      
      // 数据标签映射
      dataLabels: {
        product: '产品名称',
        type: '产品型号',
        specification: '规格',
        material: '材料',
        process: '工序',
        quality: '质量等级',
        temperature: '处理温度',
        pressure: '处理压力',
        duration: '处理时长'
      },
      
      // 区块数据
      recentBlocks: [],
      
      // 图表实例
      chainChart: null,
      
      // 图表数据
      chartData: [],
      
      // 定时器
      timeInterval: null,
      dataStreamInterval: null,
      messageTimer: null,
      chartUpdateTimer: null,
      
      // 当前区块高度
      currentBlockHeight: 18472
    };
  },
  created() {
    this.initializeRecentBlocks();
  },
  mounted() {
    this.initTime();
    this.initCharts();
    this.initParticles();
    this.initDataStream();
    this.loadRecentQueries();
    
    // 默认显示第一个区块的详情
    if (this.recentBlocks.length > 0) {
      this.selectedBlock = this.recentBlocks[0];
    }
    
    // 窗口大小调整监听
    window.addEventListener('resize', this.handleResize);
  },
  beforeUnmount() {
    this.clearIntervals();
    if (this.chainChart) {
      this.chainChart.dispose();
    }
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    // 初始化最近区块
    initializeRecentBlocks() {
      const now = Date.now();
      this.recentBlocks = [
        {
          number: this.currentBlockHeight,
          hash: '0x7d3f8a1b4c9e2f6a5d8b0e7c3f9a1b4c8e2d6f5a',
          productId: 'AX20231215001',
          timestamp: now - 180000,
          data: {
            product: '工业半轴',
            type: 'A型',
            specification: 'Φ45×1200mm',
            material: '40Cr',
            process: '精车杆',
            quality: 'A级',
            temperature: '850℃',
            pressure: '120MPa',
            duration: '45min'
          }
        },
        {
          number: this.currentBlockHeight - 1,
          hash: '0x6c2e7b0a3d8f1e5c4b9a0f2e3c4d5b6a7f8e9d0c',
          productId: 'AX20231214999',
          timestamp: now - 360000,
          data: {
            product: '工业半轴',
            type: 'A型',
            specification: 'Φ45×1200mm',
            material: '40Cr',
            process: '粗抛丸',
            quality: 'A级',
            temperature: '820℃',
            pressure: '110MPa',
            duration: '40min'
          }
        },
        {
          number: this.currentBlockHeight - 2,
          hash: '0x5b1d6a2e7c3f8b4a9d5e0c6f1b7a2d8c3e9f4b5a',
          productId: 'AX20231214998',
          timestamp: now - 540000,
          data: {
            product: '工业半轴',
            type: 'A型',
            specification: 'Φ45×1200mm',
            material: '40Cr',
            process: '探伤',
            quality: 'A级',
            temperature: '800℃',
            pressure: '100MPa',
            duration: '35min'
          }
        }
      ];
    },
    
    // 显示消息
    showMessageFn(text, type = 'success') {
      this.showMessage = true;
      this.messageText = text;
      this.messageType = type;
      
      if (this.messageTimer) {
        clearTimeout(this.messageTimer);
      }
      
      this.messageTimer = setTimeout(() => {
        this.showMessage = false;
      }, 3000);
    },
    
    // 初始化时间
    initTime() {
      const updateTime = () => {
        const now = new Date();
        this.currentTime = now.toLocaleTimeString('zh-CN', { 
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        });
      };
      updateTime();
      this.timeInterval = setInterval(updateTime, 1000);
    },
    
    // 初始化图表 - 优化波动幅度
    initCharts() {
      const chartDom = document.getElementById('chainChart');
      if (!chartDom) return;
      
      this.chainChart = echarts.init(chartDom);
      
      // 初始化图表数据 - 减小波动幅度
      this.chartData = Array.from({ length: 60 }, (_, i) => {
        const base = 20;
        // 使用更平缓的正弦波和更小的随机波动
        const wave = Math.sin(i / 15) * 8; // 减少振幅
        const random = Math.random() * 4; // 减小随机波动范围
        return Math.max(10, Math.min(30, base + wave + random));
      });
      
      const option = {
        backgroundColor: 'transparent',
        animation: true,
        animationDuration: 1000,
        animationEasing: 'cubicOut',
        grid: {
          left: '3%',
          right: '3%',
          top: '15%',
          bottom: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: Array.from({ length: 60 }, (_, i) => `${i}分前`),
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 242, 255, 0.3)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.6)',
            fontSize: 11
          },
          splitLine: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          name: '上链数/分钟',
          nameTextStyle: {
            color: 'rgba(255, 255, 255, 0.5)',
            fontSize: 12
          },
          min: 10,
          max: 30,
          axisLine: {
            show: false
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.6)',
            fontSize: 11
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 242, 255, 0.1)',
              type: 'dashed'
            }
          }
        },
        series: [{
          type: 'line',
          data: this.chartData,
          smooth: true,
          symbol: 'circle',
          symbolSize: 4,
          lineStyle: {
            width: 2,
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
              offset: 0,
              color: '#00f2ff'
            }, {
              offset: 1,
              color: '#0066ff'
            }])
          },
          itemStyle: {
            color: '#00f2ff'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0,
              color: 'rgba(0, 242, 255, 0.3)'
            }, {
              offset: 1,
              color: 'rgba(0, 242, 255, 0.05)'
            }])
          },
          markPoint: {
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#00ff99'
            },
            label: {
              show: false
            },
            data: [
              { type: 'max', name: '最高值' },
              { type: 'min', name: '最低值' }
            ]
          }
        }],
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(15, 23, 54, 0.9)',
          borderColor: 'rgba(0, 242, 255, 0.3)',
          textStyle: {
            color: '#fff'
          },
          formatter: (params) => {
            const data = params[0];
            return `时间: ${data.name}<br/>上链数: ${data.value}`;
          }
        }
      };
      
      this.chainChart.setOption(option);
    },
    
    // 更新图表数据 - 减小波动
    updateChartData() {
      if (!this.chainChart) return;
      
      // 平滑地更新数据，减小波动
      this.chartData.shift();
      
      // 基于当前数据计算新值，保持趋势平稳
      const lastValue = this.chartData[this.chartData.length - 1] || 20;
      const base = 20;
      const wave = Math.sin(Date.now() / 15000) * 6; // 进一步减小振幅
      const random = (Math.random() - 0.5) * 3; // 减小随机波动
      
      // 限制波动范围
      let newValue = base + wave + random;
      newValue = Math.max(12, Math.min(28, newValue)); // 限制范围
      
      // 平滑过渡
      const smoothValue = lastValue * 0.7 + newValue * 0.3;
      this.chartData.push(smoothValue);
      
      this.chainChart.setOption({
        series: [{
          data: this.chartData
        }]
      });
    },
    
    // 初始化粒子效果
    initParticles() {
      const container = this.$refs.particlesContainer;
      if (!container) return;
      
      for (let i = 0; i < 50; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.cssText = `
          left: ${Math.random() * 100}%;
          top: ${Math.random() * 100}%;
          width: ${Math.random() * 2 + 1}px;
          height: ${Math.random() * 2 + 1}px;
          opacity: ${Math.random() * 0.4 + 0.1};
          animation-delay: ${Math.random() * 5}s;
          animation-duration: ${Math.random() * 10 + 15}s;
        `;
        container.appendChild(particle);
      }
    },
    
    // 初始化数据流
    initDataStream() {
      if (!this.dataStreaming) return;
      
      this.dataStreamInterval = setInterval(() => {
        // 生成新区块
        this.currentBlockHeight++;
        
        const newBlock = {
          number: this.currentBlockHeight,
          hash: '0x' + Array.from({ length: 40 }, () => 
            Math.floor(Math.random() * 16).toString(16)
          ).join(''),
          productId: `AX${new Date().toISOString().slice(2,10).replace(/-/g,'')}${String(Math.floor(Math.random() * 1000)).padStart(3,'0')}`,
          timestamp: Date.now(),
          data: {
            product: '工业半轴',
            type: ['A型','B型','C型'][Math.floor(Math.random() * 3)],
            specification: ['Φ45×1200mm','Φ50×1500mm','Φ40×1000mm'][Math.floor(Math.random() * 3)],
            material: ['40Cr','45#钢','42CrMo'][Math.floor(Math.random() * 3)],
            process: ['切割','压花键','钻孔','抛丸','精车','探伤','包装'][Math.floor(Math.random() * 7)],
            quality: ['A级','B级','C级'][Math.floor(Math.random() * 3)],
            temperature: `${Math.floor(Math.random() * 50) + 780}℃`,
            pressure: `${Math.floor(Math.random() * 30) + 90}MPa`,
            duration: `${Math.floor(Math.random() * 20) + 30}min`
          }
        };
        
        // 添加到最近区块列表
        this.recentBlocks.unshift(newBlock);
        if (this.recentBlocks.length > 5) {
          this.recentBlocks.pop();
        }
        
        // 更新统计数据
        this.blockchainStats.totalBlocks = this.currentBlockHeight;
        this.blockchainStats.todayTransactions = parseInt(this.blockchainStats.todayTransactions) + Math.floor(Math.random() * 3) + 1;
        
        this.stats[0].value = this.formatNumber(this.currentBlockHeight);
        this.stats[1].value = this.formatNumber(this.blockchainStats.todayTransactions);
        
        // 更新图表
        this.updateChartData();
        
        // 如果当前选中的区块是最新的，则自动更新详情
        if (this.selectedBlock && this.selectedBlock.number === this.recentBlocks[1]?.number) {
          setTimeout(() => {
            this.selectedBlock = this.recentBlocks[0];
          }, 500);
        }
        
      }, 5000); // 每5秒产生一个新区块
    },
    
    // 清理定时器
    clearIntervals() {
      if (this.timeInterval) clearInterval(this.timeInterval);
      if (this.dataStreamInterval) clearInterval(this.dataStreamInterval);
      if (this.messageTimer) clearTimeout(this.messageTimer);
      if (this.chartUpdateTimer) clearInterval(this.chartUpdateTimer);
    },
    
    // 处理窗口大小变化
    handleResize() {
      if (this.chainChart) {
        this.chainChart.resize();
      }
    },
    
    // 刷新数据
    refreshData() {
      this.syncProgress = 0;
      this.showMessageFn('开始同步数据...', 'info');
      
      // 模拟同步过程
      const syncInterval = setInterval(() => {
        this.syncProgress += 20;
        if (this.syncProgress >= 100) {
          clearInterval(syncInterval);
          this.syncProgress = 100;
          
          // 更新活跃节点数
          this.blockchainStats.activeNodes = 12 + Math.floor(Math.random() * 4);
          
          this.showMessageFn('数据已同步更新', 'success');
        }
      }, 200);
    },
    
    // 切换数据流
    toggleDataStream() {
      this.dataStreaming = !this.dataStreaming;
      if (this.dataStreaming) {
        this.initDataStream();
        this.showMessageFn('已开启实时数据流', 'success');
      } else {
        if (this.dataStreamInterval) {
          clearInterval(this.dataStreamInterval);
          this.dataStreamInterval = null;
        }
        this.showMessageFn('已暂停实时数据流', 'info');
      }
    },
    
    // 切换数据模式
    changeDataMode() {
      const modeNames = {
        realtime: '实时监控',
        history: '历史追溯',
        analysis: '智能分析'
      };
      this.showMessageFn(`切换到${modeNames[this.dataMode]}模式`, 'info');
    },
    
    // 获取节点样式
    getNodeStyle(index) {
      const baseDelay = index * 0.1;
      const translateX = index * 70;
      const rotation = this.visualRotation;
      const scale = 1 + (index * 0.05);
      
      return {
        transform: `translateX(${translateX}px) rotateY(${rotation}deg) scale(${scale})`,
        animationDelay: `${baseDelay}s`,
        zIndex: 10 - index
      };
    },
    
    // 旋转可视化
    rotateVisual(degrees) {
      this.visualRotation += degrees;
    },
    
    // 切换动画
    toggleAnimation() {
      this.isAnimating = !this.isAnimating;
      this.showMessageFn(this.isAnimating ? '区块链动画已启动' : '区块链动画已暂停', 'info');
    },
    
    // 切换时间范围
    changeRange(range) {
      this.currentRange = range;
      this.showMessageFn(`切换到${range}数据范围`, 'info');
    },
    
    // 检查区块
    inspectBlock(block) {
      this.selectedBlock = block;
      this.showMessageFn(`已选中区块 #${block.number}`, 'info');
    },
    
    // 搜索溯源
    searchTrace() {
      if (!this.queryId.trim()) {
        this.showMessageFn('请输入产品ID或批次号', 'warning');
        return;
      }
      
      // 保存到最近查询
      this.addRecentQuery(this.queryId);
      
      // 模拟溯源结果
      this.traceResult = {
        productId: this.queryId,
        batch: 'BATCH-1220',
        status: '已完成',
        quality: 'A+',
        updateTime: this.formatTime(Date.now()),
        steps: [
          {
            stage: '原料入库',
            icon: '📥',
            time: '2023-12-15 08:30',
            description: '40Cr钢材入库，批次号：MC202312001，检验合格，材料规格符合要求',
            operator: '张工',
            equipment: '行车-001'
          },
          {
            stage: '切割下料',
            icon: '✂️',
            time: '2023-12-15 09:15',
            description: '精密切割，尺寸控制±0.1mm，表面光滑无毛刺',
            operator: '李工',
            equipment: '数控切割机-002'
          },
          {
            stage: '压花键',
            icon: '⚙️',
            time: '2023-12-15 10:30',
            description: '花键压制，精度控制±0.02mm，齿形完整清晰',
            operator: '王工',
            equipment: '压力机-003'
          },
          {
            stage: '热处理',
            icon: '🔥',
            time: '2023-12-15 14:00',
            description: '调质处理，硬度HRC28-32，组织均匀，达到技术要求',
            operator: '赵工',
            equipment: '热处理炉-004'
          },
          {
            stage: '精加工',
            icon: '⚡',
            time: '2023-12-15 15:45',
            description: '数控精车，表面粗糙度Ra0.8，尺寸精准符合图纸要求',
            operator: '孙工',
            equipment: '数控车床-005'
          },
          {
            stage: '探伤检测',
            icon: '🔍',
            time: '2023-12-15 16:30',
            description: '超声波探伤，无缺陷报告，质量达标，合格品入库',
            operator: '周工',
            equipment: '探伤仪-006'
          },
          {
            stage: '质检包装',
            icon: '📦',
            time: '2023-12-15 17:15',
            description: '最终质量检验，防锈包装，入库待发',
            operator: '钱工',
            equipment: '包装线-007'
          },
          {
            stage: '出库发货',
            icon: '🚚',
            time: '2023-12-15 18:00',
            description: '产品出库，发往客户仓库，物流信息已记录',
            operator: '运输部',
            equipment: '物流系统'
          }
        ]
      };
      
      // 确保内容完全显示
      this.$nextTick(() => {
        const container = this.$refs.traceResultContainer;
        if (container) {
          container.scrollTop = 0;
        }
      });
      
      this.showMessageFn(`已找到产品 ${this.queryId} 的完整溯源记录`, 'success');
    },
    
    // 添加最近查询
    addRecentQuery(queryId) {
      // 检查是否已存在相同查询
      const existingIndex = this.recentQueries.findIndex(q => q.id === queryId);
      if (existingIndex > -1) {
        this.recentQueries.splice(existingIndex, 1);
      }
      
      const query = {
        id: queryId,
        time: new Date().toLocaleTimeString('zh-CN', {
          hour: '2-digit',
          minute: '2-digit'
        }),
        status: '成功',
        statusClass: 'success'
      };
      
      this.recentQueries.unshift(query);
      if (this.recentQueries.length > 5) {
        this.recentQueries.pop();
      }
      
      // 保存到本地存储
      localStorage.setItem('recentQueries', JSON.stringify(this.recentQueries));
    },
    
    // 清空最近查询
    clearRecentQueries() {
      if (confirm('确定要清空最近查询记录吗？')) {
        this.recentQueries = [];
        localStorage.removeItem('recentQueries');
        this.showMessageFn('已清空最近查询记录', 'info');
      }
    },
    
    // 加载最近查询
    loadRecentQueries() {
      try {
        const saved = localStorage.getItem('recentQueries');
        if (saved) {
          this.recentQueries = JSON.parse(saved);
        }
      } catch (e) {
        console.log('加载最近查询记录失败', e);
      }
    },
    
    // 格式化数字
    formatNumber(num) {
      if (typeof num === 'string') {
        num = parseInt(num.replace(/,/g, '')) || 0;
      }
      if (typeof num !== 'number') return '0';
      return num.toLocaleString('en-US');
    },
    
    // 格式化时间
    formatTime(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    // 格式化区块时间
    formatBlockTime(timestamp) {
      const now = Date.now();
      const diff = now - timestamp;
      const minutes = Math.floor(diff / 60000);
      
      if (minutes < 1) return '刚刚';
      if (minutes < 60) return `${minutes}分钟前`;
      
      const hours = Math.floor(minutes / 60);
      if (hours < 24) return `${hours}小时前`;
      
      const days = Math.floor(hours / 24);
      return `${days}天前`;
    }
  }
};
</script>

<style scoped>
.blockchain-dashboard {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #0a1028 0%, #0a1838 30%, #082048 100%);
  color: #fff;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
  overflow: hidden;
  position: relative;
}

/* 科技背景效果 */
.tech-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(0, 100, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 100, 255, 0.1) 1px, transparent 1px);
  background-size: 60px 60px;
  opacity: 0.3;
}

.radar-sweep {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 1000px;
  height: 1000px;
  transform: translate(-50%, -50%);
  background: conic-gradient(
    transparent 0deg,
    rgba(0, 242, 255, 0.1) 10deg,
    transparent 20deg
  );
  animation: radar 8s infinite linear;
  border-radius: 50%;
  opacity: 0.1;
}

@keyframes radar {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

.data-flow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent,
    rgba(0, 242, 255, 0.8),
    transparent
  );
  animation: flowMove 4s linear infinite;
  z-index: 1;
}

@keyframes flowMove {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: rgba(0, 242, 255, 0.3);
  border-radius: 50%;
  animation: float 20s infinite linear;
}

@keyframes float {
  0% {
    transform: translateY(100vh) translateX(0);
  }
  100% {
    transform: translateY(-100px) translateX(100px);
  }
}

/* 顶部标题栏 */
.dashboard-header {
  position: relative;
  z-index: 10;
  padding: 20px 30px 10px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.main-title {
  display: flex;
  align-items: baseline;
  gap: 15px;
  margin: 0;
}

.title-glow {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #00f2ff 0%, #0066ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 20px rgba(0, 242, 255, 0.3);
}

.title-sub {
  font-size: 18px;
  font-weight: 300;
  color: rgba(255, 255, 255, 0.7);
}

.title-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: rgba(0, 242, 255, 0.1);
  border: 1px solid rgba(0, 242, 255, 0.3);
  border-radius: 4px;
  font-size: 12px;
}

.tag-icon {
  color: #00ff99;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.tag-text {
  color: rgba(255, 255, 255, 0.9);
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.time-display {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.time-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 2px;
}

.time-value {
  font-size: 20px;
  font-family: 'Courier New', monospace;
  color: #00f2ff;
  text-shadow: 0 0 10px rgba(0, 242, 255, 0.5);
}

.control-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.control-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  background: rgba(15, 23, 54, 0.8);
  border: 1px solid rgba(0, 242, 255, 0.3);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 60px;
}

.control-btn.holographic {
  position: relative;
  overflow: hidden;
}

.control-btn.holographic::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, 
    transparent,
    rgba(0, 242, 255, 0.2),
    transparent
  );
  z-index: -1;
  animation: hologram 2s infinite linear;
}

@keyframes hologram {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.control-btn:hover {
  background: rgba(0, 242, 255, 0.1);
  border-color: #00f2ff;
  transform: translateY(-1px);
  box-shadow: 0 0 15px rgba(0, 242, 255, 0.2);
}

.btn-icon {
  font-size: 18px;
  color: #00f2ff;
  margin-bottom: 4px;
}

.btn-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
}

.custom-select {
  position: relative;
  width: 120px;
}

.custom-select select {
  width: 100%;
  padding: 8px 12px;
  background: rgba(15, 23, 54, 0.8);
  border: 1px solid rgba(0, 242, 255, 0.3);
  border-radius: 6px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  appearance: none;
}

.custom-select .select-arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #00f2ff;
  pointer-events: none;
}

/* 主内容区 */
.dashboard-content {
  position: relative;
  z-index: 5;
  display: grid;
  grid-template-columns: 1.2fr 1.6fr 1.2fr;
  gap: 20px;
  padding: 0 30px 20px;
  height: calc(100vh - 150px);
  overflow: hidden;
}

.panel {
  background: rgba(15, 23, 54, 0.3);
  border: 1px solid rgba(0, 242, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  padding: 20px;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent,
    rgba(0, 242, 255, 0.5),
    transparent
  );
}

.panel-header {
  margin-bottom: 20px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #fff;
}

.title-line {
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #00f2ff, #0066ff);
  border-radius: 2px;
}

.panel-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 1px;
}

/* 左侧面板样式 */
.chain-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  padding: 15px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
}

.stat-card.neon-card {
  background: rgba(0, 242, 255, 0.05);
  border: 1px solid rgba(0, 242, 255, 0.2);
}

.stat-card.neon-card:hover {
  background: rgba(0, 242, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 242, 255, 0.1);
}

.stat-icon {
  font-size: 24px;
  opacity: 0.8;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #00f2ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 4px;
}

.stat-trend {
  font-size: 11px;
}

.trend-up {
  color: #00ff99;
}

.trend-down {
  color: #ff6666;
}

.trend-stable {
  color: #ffcc00;
}

.realtime-chart {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.chart-header h4 {
  margin: 0;
  font-size: 14px;
  color: #fff;
}

.time-range {
  display: flex;
  gap: 8px;
}

.time-range span {
  padding: 4px 8px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.time-range span.active {
  background: rgba(0, 242, 255, 0.2);
  color: #00f2ff;
}

.time-range span:hover {
  color: #00f2ff;
}

.chart-container {
  flex: 1;
  min-height: 0;
}

/* 中间面板 - 区块链可视化 */
.center-panel {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.blockchain-visualization {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
}

.visualization-container {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  perspective: 1000px;
  min-height: 0;
  overflow: visible;
}

.chain-line {
  position: absolute;
  top: 50%;
  left: 10%;
  right: 10%;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent,
    rgba(0, 242, 255, 0.6),
    rgba(0, 242, 255, 0.6),
    transparent
  );
  transform: translateY(-50%);
  z-index: 1;
}

.block-node {
  position: absolute;
  left: 10%;
  top: 50%;
  transform: translateY(-50%);
  transition: all 0.5s ease;
  z-index: 2;
}

.node-core {
  width: 65px;
  height: 65px;
  background: radial-gradient(
    circle at 30% 30%,
    rgba(0, 100, 255, 0.3),
    rgba(0, 30, 80, 0.8)
  );
  border: 2px solid rgba(0, 242, 255, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: nodeFloat 3s ease-in-out infinite;
  position: relative;
}

@keyframes nodeFloat {
  0%, 100% {
    transform: translateY(0) scale(1);
    box-shadow: 0 0 20px rgba(0, 242, 255, 0.3);
  }
  50% {
    transform: translateY(-8px) scale(1.04);
    box-shadow: 0 0 30px rgba(0, 242, 255, 0.5);
  }
}

.node-core.active {
  border-color: #00ff99;
  box-shadow: 0 0 25px rgba(0, 255, 153, 0.6);
  transform: scale(1.08);
}

.node-core:hover {
  border-color: #00f2ff;
  box-shadow: 0 0 40px rgba(0, 242, 255, 0.7);
  transform: scale(1.12);
}

.node-pulse {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background: rgba(0, 242, 255, 0.3);
  animation: pulse 2s ease-out infinite;
  z-index: -1;
}

.node-content {
  text-align: center;
  z-index: 1;
}

.node-number {
  font-size: 15px;
  font-weight: bold;
  color: #00f2ff;
  text-shadow: 0 0 8px rgba(0, 242, 255, 0.4);
}

.node-time {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 2px;
}

.node-connection {
  position: absolute;
  top: 50%;
  left: 100%;
  width: 75px;
  height: 2px;
  background: linear-gradient(90deg, 
    rgba(0, 242, 255, 0.6),
    rgba(0, 242, 255, 0.3)
  );
  transform: translateY(-50%);
}

.chain-controls {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-shrink: 0;
}

.chain-controls .control-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  background: rgba(15, 23, 54, 0.8);
  border: 1px solid rgba(0, 242, 255, 0.3);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 60px;
}

.chain-controls .control-btn:hover {
  background: rgba(0, 242, 255, 0.1);
  border-color: #00f2ff;
  transform: translateY(-1px);
}

.control-icon {
  font-size: 16px;
  color: #00f2ff;
  margin-bottom: 4px;
}

.control-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
}

/* 区块详情 */
.block-detail {
  background: rgba(15, 23, 54, 0.6);
  border: 1px solid rgba(0, 242, 255, 0.2);
  border-radius: 8px;
  padding: 15px;
  margin-top: 10px;
  flex-shrink: 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.detail-header h4 {
  margin: 0;
  font-size: 14px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.block-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  font-weight: normal;
}

.block-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-dot.valid {
  background-color: #00ff99;
  box-shadow: 0 0 8px rgba(0, 255, 153, 0.5);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
}

.detail-item label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.detail-item span {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  word-break: break-all;
}

.value-highlight {
  color: #00f2ff !important;
  font-weight: 500;
}

.value-code {
  font-family: 'Courier New', monospace;
  font-size: 11px !important;
  color: #ffcc00 !important;
}

.product-value {
  color: #00f2ff !important;
  font-weight: 500;
}

.block-data {
  margin-top: 15px;
}

.block-data label {
  display: block;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 8px;
}

.data-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  max-height: 200px;
  overflow-y: auto;
  padding-right: 5px;
}

.data-content::-webkit-scrollbar {
  width: 4px;
}

.data-content::-webkit-scrollbar-track {
  background: rgba(0, 242, 255, 0.1);
  border-radius: 2px;
}

.data-content::-webkit-scrollbar-thumb {
  background: rgba(0, 242, 255, 0.3);
  border-radius: 2px;
}

.data-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 6px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 4px;
  border: 1px solid rgba(0, 242, 255, 0.1);
}

.data-key {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
}

.data-value {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

/* 右侧面板样式 - 优化高度和滚动 */
.right-panel {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.trace-query {
  background: linear-gradient(135deg, 
    rgba(15, 23, 54, 0.6),
    rgba(0, 30, 80, 0.4)
  );
  border: 1px solid rgba(0, 242, 255, 0.2);
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.query-input-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-prefix {
  display: flex;
  align-items: center;
  gap: 8px;
}

.prefix-icon {
  font-size: 16px;
  color: #00f2ff;
  animation: searchPulse 2s infinite;
}

@keyframes searchPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.prefix-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.custom-input {
  display: flex;
  gap: 8px;
}

.custom-input input {
  flex: 1;
  padding: 10px 12px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 242, 255, 0.3);
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
  transition: all 0.3s ease;
}

.custom-input input:focus {
  outline: none;
  border-color: #00f2ff;
  box-shadow: 0 0 10px rgba(0, 242, 255, 0.2);
}

.custom-input input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.query-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #0066ff, #00f2ff);
  border: none;
  border-radius: 6px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  font-weight: 500;
}

.query-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(0, 242, 255, 0.3);
}

.query-examples {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.example-label {
  color: rgba(255, 255, 255, 0.5);
  flex-shrink: 0;
}

.example-item {
  color: #00f2ff;
  cursor: pointer;
  padding: 3px 8px;
  border-radius: 4px;
  background: rgba(0, 242, 255, 0.1);
  border: 1px solid transparent;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.example-item:hover {
  background: rgba(0, 242, 255, 0.2);
  border-color: rgba(0, 242, 255, 0.3);
  transform: translateY(-1px);
}

/* 溯源结果容器 - 优化滚动 */
.trace-result-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
  padding-right: 5px;
}

.trace-result-container::-webkit-scrollbar {
  width: 4px;
}

.trace-result-container::-webkit-scrollbar-track {
  background: rgba(0, 242, 255, 0.1);
  border-radius: 2px;
}

.trace-result-container::-webkit-scrollbar-thumb {
  background: rgba(0, 242, 255, 0.3);
  border-radius: 2px;
}

.trace-result {
  background: rgba(15, 23, 54, 0.6);
  border: 1px solid rgba(0, 242, 255, 0.2);
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 242, 255, 0.1);
}

.result-title h4 {
  margin: 0 0 6px 0;
  font-size: 14px;
  color: #fff;
}

.product-id-display {
  font-size: 16px;
  font-weight: 500;
  color: #00f2ff;
  text-shadow: 0 0 8px rgba(0, 242, 255, 0.2);
}

.result-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.status-badge {
  padding: 4px 8px;
  font-size: 11px;
  border-radius: 12px;
}

.status-badge.complete {
  background: rgba(0, 255, 153, 0.1);
  color: #00ff99;
  border: 1px solid rgba(0, 255, 153, 0.3);
}

.status-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.product-quick-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 15px;
  padding: 10px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  border: 1px solid rgba(0, 242, 255, 0.1);
}

.quick-info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 6px;
}

.info-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.info-value {
  font-size: 13px;
  font-weight: 500;
}

.info-value.success {
  color: #00ff99;
}

.info-value.excellent {
  color: #ffcc00;
}

/* 时间线容器 */
.trace-timeline-wrapper {
  margin-top: 15px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  border: 1px solid rgba(0, 242, 255, 0.1);
}

.timeline-title {
  font-size: 13px;
  font-weight: 500;
  color: #fff;
}

.timeline-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 3px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
}

.scroll-hint {
  font-size: 10px;
  color: rgba(0, 242, 255, 0.6);
}

/* 可滚动的时间线容器 */
.timeline-container {
  position: relative;
  padding-left: 30px;
  max-height: 300px;
  overflow-y: auto;
  padding-right: 5px;
}

.timeline-container::-webkit-scrollbar {
  width: 4px;
}

.timeline-container::-webkit-scrollbar-track {
  background: rgba(0, 242, 255, 0.1);
  border-radius: 2px;
}

.timeline-container::-webkit-scrollbar-thumb {
  background: rgba(0, 242, 255, 0.3);
  border-radius: 2px;
}

.timeline-item {
  position: relative;
  margin-bottom: 18px;
}

.timeline-marker {
  position: absolute;
  left: -30px;
  top: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 30px;
}

.marker-icon {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(0, 242, 255, 0.1);
  border: 1px solid rgba(0, 242, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  z-index: 2;
}

.marker-line {
  flex: 1;
  width: 2px;
  background: linear-gradient(
    to bottom,
    rgba(0, 242, 255, 0.5),
    rgba(0, 242, 255, 0.1)
  );
  margin-top: 4px;
}

.timeline-content {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(0, 242, 255, 0.1);
  border-radius: 8px;
  padding: 10px 12px;
  transition: all 0.3s ease;
}

.timeline-item:hover .timeline-content {
  border-color: rgba(0, 242, 255, 0.3);
  transform: translateX(3px);
  background: rgba(15, 23, 54, 0.4);
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  padding: 0;
  background: transparent;
  border: none;
}

.timeline-stage {
  font-size: 12px;
  font-weight: 500;
  color: #fff;
}

.timeline-time {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.5);
}

.timeline-desc {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.4;
  margin-bottom: 6px;
}

.timeline-meta {
  display: flex;
  gap: 10px;
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 3px;
}

.meta-icon {
  font-size: 9px;
}

/* 最近查询 */
.recent-queries {
  background: rgba(15, 23, 54, 0.6);
  border: 1px solid rgba(0, 242, 255, 0.2);
  border-radius: 8px;
  padding: 15px;
  margin-top: 10px;
  flex-shrink: 0;
}

.queries-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.queries-header h4 {
  margin: 0;
  font-size: 13px;
  color: #fff;
}

.queries-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.queries-count {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
}

.clear-btn {
  padding: 3px 8px;
  background: rgba(255, 77, 77, 0.1);
  border: 1px solid rgba(255, 77, 77, 0.3);
  border-radius: 4px;
  color: #ff4d4d;
  font-size: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.clear-btn:hover {
  background: rgba(255, 77, 77, 0.2);
  transform: translateY(-1px);
}

.queries-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 180px;
  overflow-y: auto;
  padding-right: 5px;
}

.queries-list::-webkit-scrollbar {
  width: 3px;
}

.queries-list::-webkit-scrollbar-track {
  background: rgba(0, 242, 255, 0.1);
  border-radius: 2px;
}

.queries-list::-webkit-scrollbar-thumb {
  background: rgba(0, 242, 255, 0.3);
  border-radius: 2px;
}

.query-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  border: 1px solid rgba(0, 242, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.query-item:hover {
  background: rgba(0, 242, 255, 0.1);
  border-color: rgba(0, 242, 255, 0.3);
  transform: translateX(3px);
}

.query-icon {
  font-size: 12px;
  color: #00f2ff;
  flex-shrink: 0;
}

.query-info {
  flex: 1;
  min-width: 0;
}

.query-id {
  font-size: 12px;
  color: #00f2ff;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 2px;
}

.query-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-time {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
}

.query-status {
  font-size: 9px;
  padding: 1px 5px;
  border-radius: 10px;
  background: rgba(0, 255, 153, 0.1);
  color: #00ff99;
  border: 1px solid rgba(0, 255, 153, 0.3);
}

/* 底部状态栏 */
.dashboard-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(10, 16, 40, 0.9);
  border-top: 1px solid rgba(0, 242, 255, 0.2);
  backdrop-filter: blur(10px);
  z-index: 20;
  padding: 8px 30px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-status {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-dot.online {
  background-color: #00ff99;
  box-shadow: 0 0 8px rgba(0, 255, 153, 0.5);
}

.status-dot.sync {
  background-color: #00f2ff;
  box-shadow: 0 0 8px rgba(0, 242, 255, 0.5);
  animation: pulse 1.5s infinite;
}

.status-dot.nodes {
  background-color: #ffcc00;
  box-shadow: 0 0 8px rgba(255, 204, 0, 0.5);
}

.status-dot.transaction {
  background-color: #ff66cc;
  box-shadow: 0 0 8px rgba(255, 102, 204, 0.5);
}

.status-item strong {
  color: #fff;
  font-weight: 500;
}

.data-info {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.info-item {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.info-item strong {
  color: #00f2ff;
  font-weight: 500;
  margin-left: 2px;
}

/* 消息提示 */
.message-container {
  position: fixed;
  top: 100px;
  right: 30px;
  z-index: 1000;
}

.message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 8px;
  background: rgba(15, 23, 54, 0.95);
  border: 1px solid;
  backdrop-filter: blur(10px);
  animation: slideIn 0.3s ease;
  margin-bottom: 10px;
  min-width: 200px;
  max-width: 300px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.message-icon {
  font-size: 14px;
  font-weight: bold;
  flex-shrink: 0;
}

.message-text {
  flex: 1;
  font-size: 13px;
}

.message.success {
  border-color: rgba(0, 255, 153, 0.5);
  color: #00ff99;
  box-shadow: 0 0 20px rgba(0, 255, 153, 0.2);
}

.message.info {
  border-color: rgba(0, 242, 255, 0.5);
  color: #00f2ff;
  box-shadow: 0 0 20px rgba(0, 242, 255, 0.2);
}

.message.warning {
  border-color: rgba(255, 204, 0, 0.5);
  color: #ffcc00;
  box-shadow: 0 0 20px rgba(255, 204, 0, 0.2);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式调整 */
@media (max-width: 1600px) {
  .dashboard-content {
    grid-template-columns: 1fr 1.5fr 1fr;
    gap: 15px;
    padding: 0 20px 20px;
  }
  
  .node-core {
    width: 55px;
    height: 55px;
  }
  
  .node-number {
    font-size: 13px;
  }
}

@media (max-width: 1400px) {
  .dashboard-content {
    grid-template-columns: 1fr 1fr;
    height: auto;
    min-height: calc(100vh - 150px);
  }
  
  .center-panel {
    grid-column: span 2;
    order: 1;
    height: 500px;
  }
  
  .left-panel, .right-panel {
    order: 2;
  }
}

@media (max-width: 992px) {
  .dashboard-content {
    grid-template-columns: 1fr;
    height: auto;
    overflow-y: auto;
  }
  
  .center-panel, .left-panel, .right-panel {
    grid-column: span 1;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .header-controls {
    width: 100%;
    justify-content: space-between;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .system-status, .data-info {
    justify-content: flex-start;
  }
}
</style>