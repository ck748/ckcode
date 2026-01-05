<template>
  <div class="blockchain-dashboard light-theme">
    <!-- 动态科技背景 - 浅色版本 -->
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
            <span class="title-glow">产业联盟数据大屏</span>
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
            <span>双链区块可视化</span>
          </h3>
          <div class="panel-subtitle">BLOCKCHAIN VISUALIZATION</div>
        </div>
        
        <!-- 双链区块链可视化 -->
        <div class="blockchain-visualization">
          <div class="visualization-container">
            <!-- 两条主链连接线 -->
            <div class="chain-line main-chain-line"></div>
            <div class="chain-line secondary-chain-line"></div>
            
            <!-- 链标签 -->
            <div class="chain-label private-chain">
              <div class="chain-tag">私有链</div>
            </div>
            <div class="chain-label alliance-chain">
              <div class="chain-tag">联盟链</div>
            </div>
            
            <!-- 第一条链：私有链区块节点 -->
            <div class="chain-nodes chain-1">
              <div 
                class="block-node" 
                v-for="(block, index) in chain1Blocks" 
                :key="'chain1-' + block.hash"
                :style="getNodeStyle(index, 'chain1')"
                @click="inspectBlock(block)"
              >
                <div class="node-core" :class="{active: selectedBlock?.hash === block.hash}">
                  <div class="node-pulse"></div>
                  <div class="node-content">
                    <div class="node-number">#{{ block.number }}</div>
                    <div class="node-time">{{ formatBlockTime(block.timestamp) }}</div>
                  </div>
                </div>
                <div class="node-connection" v-if="index < chain1Blocks.length - 1"></div>
              </div>
            </div>
            
            <!-- 第二条链：联盟链区块节点 -->
            <div class="chain-nodes chain-2">
              <div 
                class="block-node" 
                v-for="(block, index) in chain2Blocks" 
                :key="'chain2-' + block.hash"
                :style="getNodeStyle(index, 'chain2')"
                @click="inspectBlock(block)"
              >
                <div class="node-core secondary" :class="{active: selectedBlock?.hash === block.hash}">
                  <div class="node-pulse"></div>
                  <div class="node-content">
                    <div class="node-number">#{{ block.number }}</div>
                    <div class="node-time">{{ formatBlockTime(block.timestamp) }}</div>
                  </div>
                </div>
                <div class="node-connection secondary" v-if="index < chain2Blocks.length - 1"></div>
              </div>
            </div>
            
            <!-- 链间连接线 -->
            <div class="inter-chain-connections">
              <div 
                class="inter-chain-line" 
                v-for="(_, index) in chain1Blocks" 
                :key="'connection-' + index"
                :style="getInterChainLineStyle(index)"
              ></div>
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

      <!-- 右侧：联盟生态监控 (已修改) -->
      <div class="panel right-panel">
        <div class="panel-header">
          <h3 class="panel-title">
            <span class="title-line"></span>
            <span>联盟生态监控</span>
          </h3>
          <div class="panel-subtitle">ALLIANCE ECOSYSTEM</div>
        </div>
        
        <!-- 网络性能雷达图 -->
        <div class="network-radar-container">
          <div class="chart-title">联盟协作指标</div>
          <div id="radarChart" class="radar-chart"></div>
        </div>

        <!-- 智能合约状态 -->
        <div class="contracts-monitor">
          <div class="section-label">核心智能合约</div>
          <div class="contracts-grid">
            <div class="contract-card" v-for="contract in smartContracts" :key="contract.name">
              <div class="contract-icon" :class="contract.class">{{ contract.icon }}</div>
              <div class="contract-info">
                <div class="contract-name">{{ contract.name }}</div>
                <div class="contract-calls">{{ formatNumber(contract.calls) }} 次</div>
              </div>
              <div class="contract-status">
                <span class="status-dot pulse"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 联盟成员列表 (已修改：脱敏、去等级化) -->
        <div class="member-list-container">
          <div class="list-header">
            <span class="col-org">成员机构</span>
            <span class="col-role">联盟角色</span>
            <span class="col-credit">贡献积分</span>
          </div>
          <div class="member-list-content">
            <div class="member-row" v-for="member in allianceMembers" :key="member.id">
              <div class="col-org">
                <div class="org-icon">{{ member.icon }}</div>
                <div class="org-info">
                  <span class="org-name">{{ member.name }}</span>
                  <span class="org-type">{{ member.type }}</span>
                </div>
              </div>
              <div class="col-role">
                <span class="role-badge" :class="member.roleClass">{{ member.role }}</span>
              </div>
              <div class="col-credit">
                <div class="credit-bar-bg">
                  <div class="credit-bar" :style="{ width: member.credit + '%' }"></div>
                </div>
                <span class="credit-val">{{ member.credit }}</span>
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
      
      // 消息提示
      showMessage: false,
      messageText: '',
      messageType: 'success',
      
      // 统计数据
      stats: [
        {
          icon: '⛓️',
          value: '184',
          label: '区块总数',
          trend: '↑ 12个/小时',
          trendClass: 'trend-up'
        },
        {
          icon: '📦',
          value: '25',
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
        totalBlocks: 184,
        todayTransactions: 25,
        activeNodes: 12,
        blockTime: '3.2s',
        dataIntegrity: '99.98%'
      },
      
      // 右侧新数据：智能合约
      smartContracts: [
        { name: 'TraceCode.sol', calls: 15420, icon: '🏷️', class: 'trace' },
        { name: 'QualityCheck.sol', calls: 8932, icon: '🛡️', class: 'quality' },
        { name: 'SettlePay.sol', calls: 4210, icon: '💰', class: 'finance' }
      ],
      
      // 右侧新数据：联盟成员 (脱敏 + 合作角色)
      // roleClass 对应颜色：leader=橙色(核心), validator=绿色(协作), regulator=蓝色(监管), observer=灰色(服务)
      allianceMembers: [
        { id: 'm1', name: '某某某某集团', type: '核心企业', role: '战略协同节点', roleClass: 'leader', credit: 98, icon: '🏭' },
        { id: 'm2', name: '某某精密制造', type: '原材料', role: '生产协作节点', roleClass: 'validator', credit: 92, icon: '🔩' },
        { id: 'm3', name: '某某智慧物流', type: '物流运输', role: '流通溯源节点', roleClass: 'validator', credit: 88, icon: '🚚' },
        { id: 'm4', name: '某市市场监管局', type: '政府监管', role: '合规监管节点', roleClass: 'regulator', credit: 99, icon: '🏛️' },
        { id: 'm5', name: '某某商业银行', type: '金融服务', role: '绿色金融节点', roleClass: 'observer', credit: 95, icon: '🏦' },
        { id: 'm6', name: '某某零部件', type: '配件供应', role: '配套供应节点', roleClass: 'validator', credit: 85, icon: '⚙️' }
      ],
      
      radarData: [90, 85, 95, 88, 92], // 初始雷达图数据
      
      // 查询相关
      currentRange: '1h',
      selectedBlock: null,
      
      // 时间范围选项
      timeRanges: [
        { label: '1小时', value: '1h' },
        { label: '24小时', value: '24h' },
        { label: '7天', value: '7d' },
        { label: '30天', value: '30d' }
      ],
      
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
      
      // 两条链的区块数据
      chain1Blocks: [],
      chain2Blocks: [],
      
      // 图表实例
      chainChart: null,
      radarChart: null,
      
      // 图表数据
      chartData: [],
      
      // 定时器
      timeInterval: null,
      dataStreamInterval: null,
      messageTimer: null,
      
      // 当前区块高度
      currentBlockHeight: 184,
      allianceChainHeight: 56, // 联盟链区块高度
      
      // 产品型号选项
      productTypes: [
        '全浮式半轴130系列',
        '轮减式半轴153系列',
        '商用车全浮457系列',
        '乘用车半轴485/440系列',
        '乘用车半轴140/145系列'
      ],
      
      // 产品ID生成函数
      generateProductId: function() {
        const num1 = Math.floor(Math.random() * 90) + 10; // 10-99
        const num2 = Math.floor(Math.random() * 90) + 10; // 10-99
        return `SN-${num1}-${num2}`;
      }
    };
  },
  created() {
    this.initializeChains();
  },
  mounted() {
    this.initTime();
    this.initCharts();
    this.initParticles();
    this.initDataStream();
    
    // 默认显示第一个区块的详情
    if (this.chain1Blocks.length > 0) {
      this.selectedBlock = this.chain1Blocks[0];
    }
    
    // 窗口大小调整监听
    window.addEventListener('resize', this.handleResize);
  },
  beforeUnmount() {
    this.clearIntervals();
    if (this.chainChart) {
      this.chainChart.dispose();
    }
    if (this.radarChart) {
      this.radarChart.dispose();
    }
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    // 初始化双链
    initializeChains() {
      const now = Date.now();
      
      // 第一条链：私有链
      this.chain1Blocks = [
        {
          number: this.currentBlockHeight,
          hash: '0x7d3f8a1b4c9e2f6a5d8b0e7c3f9a1b4c8e2d6f5a',
          productId: this.generateProductId(),
          timestamp: now - 180000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[0],
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
          productId: this.generateProductId(),
          timestamp: now - 360000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[1],
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
          productId: this.generateProductId(),
          timestamp: now - 540000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[2],
            specification: 'Φ45×1200mm',
            material: '40Cr',
            process: '探伤',
            quality: 'A级',
            temperature: '800℃',
            pressure: '100MPa',
            duration: '35min'
          }
        },
        {
          number: this.currentBlockHeight - 3,
          hash: '0x4a0c592d7b2e6a4f8c3d1a5e0b6a1c7d2e8f3b4a',
          productId: this.generateProductId(),
          timestamp: now - 720000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[3],
            specification: 'Φ50×1500mm',
            material: '45#钢',
            process: '热处理',
            quality: 'B级',
            temperature: '860℃',
            pressure: '115MPa',
            duration: '50min'
          }
        },
        {
          number: this.currentBlockHeight - 4,
          hash: '0x3a9b481c6a1d5e7f9b2c4d3e0f5a6b7c8d9e1a2b',
          productId: this.generateProductId(),
          timestamp: now - 900000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[4],
            specification: 'Φ40×1000mm',
            material: '42CrMo',
            process: '切割',
            quality: 'A级',
            temperature: '810℃',
            pressure: '105MPa',
            duration: '38min'
          }
        }
      ];
      
      // 第二条链：联盟链
      this.chain2Blocks = [
        {
          number: this.allianceChainHeight,
          hash: '0x9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b',
          productId: this.generateProductId(),
          timestamp: now - 720000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[2],
            specification: 'Φ50×1500mm',
            material: '45#钢',
            process: '热处理',
            quality: 'B级',
            temperature: '860℃',
            pressure: '115MPa',
            duration: '50min'
          }
        },
        {
          number: this.allianceChainHeight - 1,
          hash: '0x8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d',
          productId: this.generateProductId(),
          timestamp: now - 900000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[3],
            specification: 'Φ40×1000mm',
            material: '42CrMo',
            process: '切割',
            quality: 'A级',
            temperature: '810℃',
            pressure: '105MPa',
            duration: '38min'
          }
        },
        {
          number: this.allianceChainHeight - 2,
          hash: '0x7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f',
          productId: this.generateProductId(),
          timestamp: now - 1080000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[4],
            specification: 'Φ45×1200mm',
            material: '40Cr',
            process: '压花键',
            quality: 'C级',
            temperature: '830℃',
            pressure: '112MPa',
            duration: '42min'
          }
        },
        {
          number: this.allianceChainHeight - 3,
          hash: '0x6d7c8b9a0f1e2d3c4b5a6f7e8d9c0b1a2f3e4d5c',
          productId: this.generateProductId(),
          timestamp: now - 1260000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[0],
            specification: 'Φ45×1200mm',
            material: '40Cr',
            process: '精加工',
            quality: 'A级',
            temperature: '840℃',
            pressure: '108MPa',
            duration: '48min'
          }
        },
        {
          number: this.allianceChainHeight - 4,
          hash: '0x5c6b7a9d8e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b',
          productId: this.generateProductId(),
          timestamp: now - 1440000,
          data: {
            product: this.generateProductId(),
            type: this.productTypes[1],
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
    
    // 初始化图表
    initCharts() {
      // 1. 初始化左侧上链趋势图
      const chainChartDom = document.getElementById('chainChart');
      if (chainChartDom) {
        this.chainChart = echarts.init(chainChartDom);
        this.chartData = Array.from({ length: 60 }, (_, i) => {
          const base = 20;
          const wave = Math.sin(i / 20) * 0.5;
          const random = (Math.random() - 0.5) * 0.3;
          return Math.max(19.5, Math.min(20.5, base + wave + random));
        });
        
        const chainOption = {
          backgroundColor: 'transparent',
          animation: true,
          grid: { left: '3%', right: '3%', top: '15%', bottom: '10%', containLabel: true },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: Array.from({ length: 60 }, (_, i) => `${i}分前`),
            axisLine: { lineStyle: { color: 'rgba(10, 102, 204, 0.3)' } },
            axisLabel: { color: 'rgba(0, 0, 0, 0.6)', fontSize: 11 },
            splitLine: { show: false }
          },
          yAxis: {
            type: 'value',
            min: 19,
            max: 21,
            axisLine: { show: false },
            axisLabel: { color: 'rgba(0, 0, 0, 0.6)', fontSize: 11 },
            splitLine: { lineStyle: { color: 'rgba(10, 102, 204, 0.1)', type: 'dashed' } }
          },
          series: [{
            type: 'line',
            data: this.chartData,
            smooth: true,
            symbol: 'circle',
            symbolSize: 3,
            lineStyle: {
              width: 2,
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#0a66cc' }, { offset: 1, color: '#0066ff' }])
            },
            itemStyle: { color: '#0a66cc' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(10, 102, 204, 0.2)' }, { offset: 1, color: 'rgba(10, 102, 204, 0.02)' }])
            }
          }]
        };
        this.chainChart.setOption(chainOption);
      }

      // 2. 初始化右侧雷达图
      const radarChartDom = document.getElementById('radarChart');
      if (radarChartDom) {
        this.radarChart = echarts.init(radarChartDom);
        const radarOption = {
          color: ['#0a66cc'],
          tooltip: {},
          radar: {
            indicator: [
              { name: '协作效率', max: 100 },
              { name: '数据共享', max: 100 },
              { name: '合约安全', max: 100 },
              { name: '生态扩容', max: 100 },
              { name: '合规治理', max: 100 }
            ],
            center: ['50%', '50%'],
            radius: '65%',
            axisName: {
              color: 'rgba(0, 0, 0, 0.7)',
              fontSize: 10
            },
            splitLine: {
              lineStyle: {
                color: 'rgba(10, 102, 204, 0.2)'
              }
            },
            splitArea: {
              areaStyle: {
                color: ['rgba(10, 102, 204, 0.02)', 'rgba(10, 102, 204, 0.05)']
              }
            },
            axisLine: {
              lineStyle: {
                color: 'rgba(10, 102, 204, 0.2)'
              }
            }
          },
          series: [{
            name: '联盟链指标',
            type: 'radar',
            data: [{
              value: this.radarData,
              name: '当前状态',
              symbol: 'circle',
              symbolSize: 4,
              itemStyle: { color: '#00a86b' },
              areaStyle: {
                color: 'rgba(0, 168, 107, 0.2)'
              },
              lineStyle: {
                color: '#00a86b',
                width: 2
              }
            }]
          }]
        };
        this.radarChart.setOption(radarOption);
      }
    },
    
    // 更新图表数据
    updateChartData() {
      // 1. 更新上链趋势图
      if (this.chainChart) {
        this.chartData.shift();
        const lastValue = this.chartData[this.chartData.length - 1] || 20;
        const base = 20;
        const timeFactor = Date.now() / 60000;
        let wave = Math.sin(timeFactor) * 0.1;
        const random = (Math.random() - 0.5) * 0.2;
        let newValue = Math.max(19.7, Math.min(20.3, base + wave + random));
        const smoothValue = lastValue * 0.8 + newValue * 0.2;
        this.chartData.push(smoothValue);
        this.chainChart.setOption({ series: [{ data: this.chartData }] });
      }

      // 2. 更新雷达图数据 (模拟波动)
      if (this.radarChart) {
        this.radarData = this.radarData.map(val => {
          let change = Math.floor(Math.random() * 5) - 2;
          let newVal = val + change;
          return Math.max(70, Math.min(99, newVal));
        });
        this.radarChart.setOption({
          series: [{
            data: [{ value: this.radarData }]
          }]
        });
      }
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
          opacity: ${Math.random() * 0.3 + 0.1};
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
        // 私有链：生成新区块
        this.currentBlockHeight++;
        
        const newBlock = {
          number: this.currentBlockHeight,
          hash: '0x' + Array.from({ length: 40 }, () => 
            Math.floor(Math.random() * 16).toString(16)
          ).join(''),
          productId: this.generateProductId(),
          timestamp: Date.now(),
          data: {
            product: this.generateProductId(),
            type: this.productTypes[Math.floor(Math.random() * this.productTypes.length)],
            specification: ['Φ45×1200mm','Φ50×1500mm','Φ40×1000mm'][Math.floor(Math.random() * 3)],
            material: ['40Cr','45#钢','42CrMo'][Math.floor(Math.random() * 3)],
            process: ['切割','压花键','钻孔','抛丸','精车','探伤','包装'][Math.floor(Math.random() * 7)],
            quality: ['A级','B级','C级'][Math.floor(Math.random() * 3)],
            temperature: `${Math.floor(Math.random() * 30) + 780}℃`,
            pressure: `${Math.floor(Math.random() * 20) + 90}MPa`,
            duration: `${Math.floor(Math.random() * 15) + 30}min`
          }
        };
        
        // 添加到私有链
        this.chain1Blocks.unshift(newBlock);
        if (this.chain1Blocks.length > 5) {
          this.chain1Blocks.pop();
        }
        
        // 联盟链：每三个区块添加一个
        if (this.currentBlockHeight % 3 === 0) {
          this.allianceChainHeight++;
          
          const chain2Block = {...newBlock};
          chain2Block.number = this.allianceChainHeight;
          chain2Block.hash = '0x' + Array.from({ length: 40 }, () => 
            Math.floor(Math.random() * 16).toString(16)
          ).join('');
          chain2Block.productId = this.generateProductId();
          
          this.chain2Blocks.unshift(chain2Block);
          if (this.chain2Blocks.length > 5) {
            this.chain2Blocks.pop();
          }
        }

        // 模拟智能合约调用数增加
        this.smartContracts.forEach(c => {
          c.calls += Math.floor(Math.random() * 5);
        });

        // 模拟成员贡献积分微调
        if (Math.random() > 0.7) {
          const randomMember = this.allianceMembers[Math.floor(Math.random() * this.allianceMembers.length)];
          randomMember.credit = Math.min(100, randomMember.credit + 1);
        }

        // 更新统计数据
        this.blockchainStats.totalBlocks = this.currentBlockHeight;
        this.blockchainStats.todayTransactions = parseInt(this.blockchainStats.todayTransactions) + Math.floor(Math.random() * 2) + 1;
        
        this.stats[0].value = this.formatNumber(this.currentBlockHeight);
        this.stats[1].value = this.formatNumber(this.blockchainStats.todayTransactions);
        
        // 更新图表
        this.updateChartData();
        
        // 如果当前选中的区块是最新的，则自动更新详情
        if (this.selectedBlock && this.selectedBlock.number === this.chain1Blocks[1]?.number) {
          setTimeout(() => {
            this.selectedBlock = this.chain1Blocks[0];
          }, 500);
        }
        
      }, 5000); // 每5秒产生一个新区块
    },
    
    // 清理定时器
    clearIntervals() {
      if (this.timeInterval) clearInterval(this.timeInterval);
      if (this.dataStreamInterval) clearInterval(this.dataStreamInterval);
      if (this.messageTimer) clearTimeout(this.messageTimer);
    },
    
    // 处理窗口大小变化
    handleResize() {
      if (this.chainChart) this.chainChart.resize();
      if (this.radarChart) this.radarChart.resize();
    },
    
    // 刷新数据
    refreshData() {
      this.syncProgress = 0;
      this.showMessageFn('开始同步数据...', 'info');
      
      const syncInterval = setInterval(() => {
        this.syncProgress += 20;
        if (this.syncProgress >= 100) {
          clearInterval(syncInterval);
          this.syncProgress = 100;
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
    getNodeStyle(index, chain) {
      const baseDelay = index * 0.1;
      const translateX = index * 60;
      const translateY = chain === 'chain1' ? -50 : 30;
      const scale = 1 + (index * 0.05);
      
      return {
        transform: `translate(${translateX}px, ${translateY}px) scale(${scale})`,
        animationDelay: `${baseDelay}s`,
        zIndex: 10 - index
      };
    },
    
    // 获取链间连接线样式
    getInterChainLineStyle(index) {
      const translateX = index * 60 + 25;
      const height = 80;
      return {
        left: `${translateX}px`,
        height: `${height}px`,
        animationDelay: `${index * 0.1}s`
      };
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
/* 白色主题样式 */
.blockchain-dashboard.light-theme {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #eef2f7 30%, #ffffff 100%);
  color: #333;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
  overflow: hidden;
  position: relative;
}

/* 科技背景效果 - 浅色版本 */
.light-theme .tech-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.light-theme .grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(10, 102, 204, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(10, 102, 204, 0.08) 1px, transparent 1px);
  background-size: 60px 60px;
  opacity: 0.3;
}

.light-theme .radar-sweep {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 1000px;
  height: 1000px;
  transform: translate(-50%, -50%);
  background: conic-gradient(
    transparent 0deg,
    rgba(10, 102, 204, 0.08) 10deg,
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

.light-theme .data-flow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent,
    rgba(10, 102, 204, 0.5),
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

.light-theme .particles-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.light-theme .particle {
  position: absolute;
  background: rgba(10, 102, 204, 0.15);
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
.light-theme .dashboard-header {
  position: relative;
  z-index: 10;
  padding: 20px 30px 10px;
}

.light-theme .header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.light-theme .title-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.light-theme .main-title {
  display: flex;
  align-items: baseline;
  gap: 15px;
  margin: 0;
}

.light-theme .title-glow {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #0a66cc 0%, #0066ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 15px rgba(10, 102, 204, 0.2);
}

.light-theme .title-sub {
  font-size: 18px;
  font-weight: 300;
  color: rgba(0, 0, 0, 0.7);
}

.light-theme .title-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: rgba(10, 102, 204, 0.08);
  border: 1px solid rgba(10, 102, 204, 0.2);
  border-radius: 4px;
  font-size: 12px;
}

.light-theme .tag-icon {
  color: #00a86b;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.light-theme .tag-text {
  color: rgba(0, 0, 0, 0.8);
}

.light-theme .header-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.light-theme .time-display {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.light-theme .time-label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.5);
  margin-bottom: 2px;
}

.light-theme .time-value {
  font-size: 20px;
  font-family: 'Courier New', monospace;
  color: #0a66cc;
  text-shadow: 0 0 8px rgba(10, 102, 204, 0.3);
}

.light-theme .control-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.light-theme .control-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(10, 102, 204, 0.2);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.light-theme .control-btn.holographic {
  position: relative;
  overflow: hidden;
}

.light-theme .control-btn.holographic::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, 
    transparent,
    rgba(10, 102, 204, 0.1),
    transparent
  );
  z-index: -1;
  animation: hologram 2s infinite linear;
}

@keyframes hologram {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.light-theme .control-btn:hover {
  background: rgba(10, 102, 204, 0.08);
  border-color: #0a66cc;
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(10, 102, 204, 0.15);
}

.light-theme .btn-icon {
  font-size: 18px;
  color: #0a66cc;
  margin-bottom: 4px;
}

.light-theme .btn-label {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.7);
}

.light-theme .custom-select {
  position: relative;
  width: 120px;
}

.light-theme .custom-select select {
  width: 100%;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(10, 102, 204, 0.2);
  border-radius: 6px;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  appearance: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.light-theme .custom-select .select-arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #0a66cc;
  pointer-events: none;
}

/* 主内容区 */
.light-theme .dashboard-content {
  position: relative;
  z-index: 5;
  display: grid;
  grid-template-columns: 1.2fr 1.6fr 1.2fr;
  gap: 20px;
  padding: 0 30px 20px;
  height: calc(100vh - 150px);
  overflow: hidden;
}

.light-theme .panel {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(10, 102, 204, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  padding: 20px;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.light-theme .panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent,
    rgba(10, 102, 204, 0.4),
    transparent
  );
}

.light-theme .panel-header {
  margin-bottom: 20px;
}

.light-theme .panel-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.light-theme .title-line {
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #0a66cc, #0066ff);
  border-radius: 2px;
}

.light-theme .panel-subtitle {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.5);
  letter-spacing: 1px;
}

/* 左侧面板样式 */
.light-theme .chain-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.light-theme .stat-card {
  padding: 15px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
}

.light-theme .stat-card.neon-card {
  background: rgba(10, 102, 204, 0.05);
  border: 1px solid rgba(10, 102, 204, 0.15);
}

.light-theme .stat-card.neon-card:hover {
  background: rgba(10, 102, 204, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(10, 102, 204, 0.1);
}

.light-theme .stat-icon {
  font-size: 24px;
  opacity: 0.8;
}

.light-theme .stat-content {
  flex: 1;
}

.light-theme .stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #0a66cc;
  margin-bottom: 4px;
}

.light-theme .stat-label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 4px;
}

.light-theme .stat-trend {
  font-size: 11px;
}

.light-theme .trend-up {
  color: #00a86b;
}

.light-theme .trend-down {
  color: #ff4d4d;
}

.light-theme .trend-stable {
  color: #ff9900;
}

.light-theme .realtime-chart {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.light-theme .chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.light-theme .chart-header h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.light-theme .time-range {
  display: flex;
  gap: 8px;
}

.light-theme .time-range span {
  padding: 4px 8px;
  font-size: 11px;
  color: rgba(0, 0, 0, 0.5);
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.light-theme .time-range span.active {
  background: rgba(10, 102, 204, 0.15);
  color: #0a66cc;
}

.light-theme .time-range span:hover {
  color: #0a66cc;
}

.light-theme .chart-container {
  flex: 1;
  min-height: 0;
}

/* 中间面板 - 区块链可视化 */
.light-theme .center-panel {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.light-theme .blockchain-visualization {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.light-theme .visualization-container {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 0;
  overflow: visible;
}

/* 链标签 */
.light-theme .chain-label {
  position: absolute;
  z-index: 10;
  font-size: 12px;
  font-weight: bold;
  pointer-events: none;
}

.light-theme .private-chain {
  top: 42%;
  left: 2%;
  transform: translateY(-50%);
}

.light-theme .alliance-chain {
  top: 68%;
  left: 2%;
  transform: translateY(-50%);
}

.light-theme .chain-tag {
  padding: 5px 12px;
  border-radius: 14px;
  border: 1.5px solid;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15);
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  backdrop-filter: blur(5px);
}

.light-theme .private-chain .chain-tag {
  border-color: #0a66cc;
  color: #0a66cc;
  background: rgba(10, 102, 204, 0.05);
}

.light-theme .alliance-chain .chain-tag {
  border-color: #00a86b;
  color: #00a86b;
  background: rgba(0, 168, 107, 0.05);
}

/* 两条主链连接线 */
.light-theme .chain-line {
  position: absolute;
  left: 15%;
  right: 15%;
  height: 2px;
  z-index: 1;
}

.light-theme .main-chain-line {
  top: 35%;
  transform: translateY(-50%);
  background: linear-gradient(90deg, 
    transparent,
    rgba(10, 102, 204, 0.4),
    rgba(10, 102, 204, 0.4),
    transparent
  );
}

.light-theme .secondary-chain-line {
  top: 65%;
  transform: translateY(-50%);
  background: linear-gradient(90deg, 
    transparent,
    rgba(0, 168, 107, 0.4),
    rgba(0, 168, 107, 0.4),
    transparent
  );
}

/* 链容器 */
.light-theme .chain-nodes {
  position: absolute;
  top: 0;
  left: 15%;
  width: 70%;
  height: 100%;
  z-index: 2;
}

.light-theme .chain-1 {
  top: 35%;
  transform: translateY(-50%);
}

.light-theme .chain-2 {
  top: 65%;
  transform: translateY(-50%);
}

.light-theme .block-node {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  transition: all 0.5s ease;
  z-index: 2;
}

.light-theme .node-core {
  width: 58px;
  height: 58px;
  background: radial-gradient(
    circle at 30% 30%,
    rgba(10, 102, 204, 0.2),
    rgba(255, 255, 255, 0.9)
  );
  border: 2px solid rgba(10, 102, 204, 0.4);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: nodeFloat 3s ease-in-out infinite;
  position: relative;
  box-shadow: 0 4px 12px rgba(10, 102, 204, 0.15);
}

.light-theme .node-core.secondary {
  border-color: rgba(0, 168, 107, 0.4);
  background: radial-gradient(
    circle at 30% 30%,
    rgba(0, 168, 107, 0.2),
    rgba(255, 255, 255, 0.9)
  );
  box-shadow: 0 4px 12px rgba(0, 168, 107, 0.15);
}

@keyframes nodeFloat {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-8px) scale(1.04);
  }
}

.light-theme .node-core.active {
  border-color: #00a86b;
  box-shadow: 0 0 25px rgba(0, 168, 107, 0.3);
  transform: scale(1.08);
}

.light-theme .node-core:hover {
  border-color: #0a66cc;
  box-shadow: 0 0 30px rgba(10, 102, 204, 0.4);
  transform: scale(1.12);
}

.light-theme .node-core.secondary:hover {
  border-color: #00a86b;
  box-shadow: 0 0 30px rgba(0, 168, 107, 0.4);
  transform: scale(1.12);
}

.light-theme .node-pulse {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  animation: pulse 2s ease-out infinite;
  z-index: -1;
}

.light-theme .node-core .node-pulse {
  background: rgba(10, 102, 204, 0.2);
}

.light-theme .node-core.secondary .node-pulse {
  background: rgba(0, 168, 107, 0.2);
}

.light-theme .node-content {
  text-align: center;
  z-index: 1;
}

.light-theme .node-number {
  font-size: 14px;
  font-weight: bold;
  color: #0a66cc;
  text-shadow: 0 0 5px rgba(10, 102, 204, 0.2);
}

.light-theme .node-core.secondary .node-number {
  color: #00a86b;
  text-shadow: 0 0 5px rgba(0, 168, 107, 0.2);
}

.light-theme .node-time {
  font-size: 9px;
  color: rgba(0, 0, 0, 0.6);
  margin-top: 2px;
}

.light-theme .node-connection {
  position: absolute;
  top: 50%;
  left: 100%;
  width: 55px;
  height: 2px;
  background: linear-gradient(90deg, 
    rgba(10, 102, 204, 0.4),
    rgba(10, 102, 204, 0.2)
  );
  transform: translateY(-50%);
}

.light-theme .node-connection.secondary {
  background: linear-gradient(90deg, 
    rgba(0, 168, 107, 0.4),
    rgba(0, 168, 107, 0.2)
  );
}

.light-theme .inter-chain-connections {
  position: absolute;
  top: 35%;
  left: 15%;
  width: 70%;
  height: 30%;
  z-index: 1;
}

.light-theme .inter-chain-line {
  position: absolute;
  top: 0;
  width: 1px;
  background: linear-gradient(
    to bottom,
    rgba(10, 102, 204, 0.3),
    rgba(0, 168, 107, 0.3),
    rgba(10, 102, 204, 0.3)
  );
  animation: interChainPulse 3s infinite;
  opacity: 0.5;
}

@keyframes interChainPulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.7; }
}

/* 区块详情 */
.light-theme .block-detail {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(10, 102, 204, 0.15);
  border-radius: 8px;
  padding: 15px;
  margin-top: 10px;
  flex-shrink: 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.light-theme .detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.light-theme .detail-header h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.light-theme .block-count {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.5);
  font-weight: normal;
}

.light-theme .block-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.7);
}

.light-theme .status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.light-theme .status-dot.valid {
  background-color: #00a86b;
  box-shadow: 0 0 8px rgba(0, 168, 107, 0.3);
}

.light-theme .detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 15px;
}

.light-theme .detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 6px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.light-theme .detail-item label {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.5);
}

.light-theme .detail-item span {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.9);
  word-break: break-all;
}

.light-theme .value-highlight {
  color: #0a66cc !important;
  font-weight: 500;
}

.light-theme .value-code {
  font-family: 'Courier New', monospace;
  font-size: 11px !important;
  color: #ff9900 !important;
}

.light-theme .product-value {
  color: #0a66cc !important;
  font-weight: 500;
}

.light-theme .block-data {
  margin-top: 15px;
}

.light-theme .block-data label {
  display: block;
  font-size: 11px;
  color: rgba(0, 0, 0, 0.5);
  margin-bottom: 8px;
}

.light-theme .data-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  max-height: 200px;
  overflow-y: auto;
  padding-right: 5px;
}

.light-theme .data-content::-webkit-scrollbar {
  width: 4px;
}

.light-theme .data-content::-webkit-scrollbar-track {
  background: rgba(10, 102, 204, 0.08);
  border-radius: 2px;
}

.light-theme .data-content::-webkit-scrollbar-thumb {
  background: rgba(10, 102, 204, 0.2);
  border-radius: 2px;
}

.light-theme .data-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 6px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 4px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.light-theme .data-key {
  font-size: 10px;
  color: rgba(0, 0, 0, 0.6);
  font-weight: 500;
}

.light-theme .data-value {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.9);
  font-weight: 500;
}

/* 右侧面板样式 - 联盟生态监控 (New) */
.light-theme .right-panel {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.light-theme .network-radar-container {
  flex: 0 0 35%;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  min-height: 200px;
  border-bottom: 1px solid rgba(10, 102, 204, 0.1);
  margin-bottom: 15px;
}

.light-theme .chart-title {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 5px;
  width: 100%;
  text-align: left;
}

.light-theme .radar-chart {
  width: 100%;
  height: 100%;
}

/* 智能合约监控 */
.light-theme .contracts-monitor {
  flex: 0 0 auto;
  margin-bottom: 15px;
}

.light-theme .section-label {
  font-size: 11px;
  color: rgba(0,0,0,0.5);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.light-theme .contracts-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.light-theme .contract-card {
  background: rgba(10, 102, 204, 0.03);
  border: 1px solid rgba(10, 102, 204, 0.1);
  border-radius: 6px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  transition: all 0.3s ease;
}

.light-theme .contract-card:hover {
  background: rgba(10, 102, 204, 0.08);
  transform: translateY(-2px);
}

.light-theme .contract-icon {
  font-size: 16px;
  margin-bottom: 4px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.light-theme .contract-icon.trace { background: rgba(0, 168, 107, 0.1); }
.light-theme .contract-icon.quality { background: rgba(10, 102, 204, 0.1); }
.light-theme .contract-icon.finance { background: rgba(255, 153, 0, 0.1); }

.light-theme .contract-info {
  text-align: center;
}

.light-theme .contract-name {
  font-size: 10px;
  color: #666;
  margin-bottom: 2px;
}

.light-theme .contract-calls {
  font-size: 12px;
  font-weight: bold;
  color: #333;
}

.light-theme .contract-status {
  position: absolute;
  top: 5px;
  right: 5px;
}

.light-theme .status-dot.pulse {
  background: #00a86b;
  width: 4px;
  height: 4px;
  box-shadow: 0 0 5px rgba(0, 168, 107, 0.5);
  animation: pulse 1s infinite;
}

/* 联盟成员列表 */
.light-theme .member-list-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 8px;
  border: 1px solid rgba(0,0,0,0.03);
}

.light-theme .list-header {
  display: flex;
  padding: 8px 10px;
  background: rgba(0,0,0,0.02);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  font-size: 11px;
  color: rgba(0, 0, 0, 0.5);
}

.light-theme .col-org { flex: 2; }
.light-theme .col-role { flex: 1.2; text-align: center; }
.light-theme .col-credit { flex: 1; text-align: right; }

.light-theme .member-list-content {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.light-theme .member-list-content::-webkit-scrollbar {
  width: 3px;
}
.light-theme .member-list-content::-webkit-scrollbar-track {
  background: rgba(10, 102, 204, 0.08);
}
.light-theme .member-list-content::-webkit-scrollbar-thumb {
  background: rgba(10, 102, 204, 0.2);
  border-radius: 2px;
}

.light-theme .member-row {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
  font-size: 12px;
  transition: all 0.2s ease;
}

.light-theme .member-row:hover {
  background: rgba(10, 102, 204, 0.05);
}

.light-theme .member-row .col-org {
  display: flex;
  align-items: center;
  gap: 8px;
}

.light-theme .org-icon {
  font-size: 16px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.03);
  border-radius: 4px;
}

.light-theme .org-info {
  display: flex;
  flex-direction: column;
}

.light-theme .org-name {
  font-weight: 500;
  color: #333;
}

.light-theme .org-type {
  font-size: 10px;
  color: #888;
}

.light-theme .role-badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
  border: 1px solid transparent;
}

.light-theme .role-badge.leader {
  background: rgba(255, 102, 0, 0.1);
  color: #ff6600;
  border-color: rgba(255, 102, 0, 0.2);
}

.light-theme .role-badge.validator {
  background: rgba(0, 168, 107, 0.1);
  color: #00a86b;
  border-color: rgba(0, 168, 107, 0.2);
}

.light-theme .role-badge.regulator {
  background: rgba(10, 102, 204, 0.1);
  color: #0a66cc;
  border-color: rgba(10, 102, 204, 0.2);
}

.light-theme .role-badge.observer {
  background: rgba(128, 128, 128, 0.1);
  color: #666;
  border-color: rgba(128, 128, 128, 0.2);
}

.light-theme .col-credit {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.light-theme .credit-bar-bg {
  width: 50px;
  height: 3px;
  background: rgba(0,0,0,0.05);
  border-radius: 2px;
  overflow: hidden;
}

.light-theme .credit-bar {
  height: 100%;
  background: linear-gradient(90deg, #0a66cc, #00c6ff);
  border-radius: 2px;
  transition: width 0.5s ease;
}

.light-theme .credit-val {
  font-size: 10px;
  color: #0a66cc;
  font-weight: bold;
}

/* 底部状态栏 */
.light-theme .dashboard-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  border-top: 1px solid rgba(10, 102, 204, 0.15);
  backdrop-filter: blur(10px);
  z-index: 20;
  padding: 8px 30px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.light-theme .footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.light-theme .system-status {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.light-theme .status-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.7);
}

.light-theme .status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.light-theme .status-dot.online {
  background-color: #00a86b;
  box-shadow: 0 0 8px rgba(0, 168, 107, 0.3);
}

.light-theme .status-dot.sync {
  background-color: #0a66cc;
  box-shadow: 0 0 8px rgba(10, 102, 204, 0.3);
  animation: pulse 1.5s infinite;
}

.light-theme .status-dot.nodes {
  background-color: #ff9900;
  box-shadow: 0 0 8px rgba(255, 153, 0, 0.3);
}

.light-theme .status-dot.transaction {
  background-color: #ff66cc;
  box-shadow: 0 0 8px rgba(255, 102, 204, 0.3);
}

.light-theme .status-item strong {
  color: #333;
  font-weight: 500;
}

.light-theme .data-info {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.light-theme .info-item {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.7);
}

.light-theme .info-item strong {
  color: #0a66cc;
  font-weight: 500;
  margin-left: 2px;
}

/* 消息提示 */
.light-theme .message-container {
  position: fixed;
  top: 100px;
  right: 30px;
  z-index: 1000;
}

.light-theme .message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid;
  backdrop-filter: blur(10px);
  animation: slideIn 0.3s ease;
  margin-bottom: 10px;
  min-width: 200px;
  max-width: 300px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.light-theme .message-icon {
  font-size: 14px;
  font-weight: bold;
  flex-shrink: 0;
}

.light-theme .message-text {
  flex: 1;
  font-size: 13px;
}

.light-theme .message.success {
  border-color: rgba(0, 168, 107, 0.4);
  color: #00a86b;
  box-shadow: 0 0 20px rgba(0, 168, 107, 0.15);
}

.light-theme .message.info {
  border-color: rgba(10, 102, 204, 0.4);
  color: #0a66cc;
  box-shadow: 0 0 20px rgba(10, 102, 204, 0.15);
}

.light-theme .message.warning {
  border-color: rgba(255, 153, 0, 0.4);
  color: #ff9900;
  box-shadow: 0 0 20px rgba(255, 153, 0, 0.15);
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
  .light-theme .dashboard-content {
    grid-template-columns: 1fr 1.5fr 1fr;
    gap: 15px;
    padding: 0 20px 20px;
  }
  
  .light-theme .node-core {
    width: 52px;
    height: 52px;
  }
  
  .light-theme .node-number {
    font-size: 12px;
  }
  
  .light-theme .node-connection {
    width: 50px;
  }
  
  .light-theme .private-chain {
    left: 1%;
  }
  
  .light-theme .alliance-chain {
    left: 1%;
  }
}

@media (max-width: 1400px) {
  .light-theme .dashboard-content {
    grid-template-columns: 1fr 1fr;
    height: auto;
    min-height: calc(100vh - 150px);
  }
  
  .light-theme .center-panel {
    grid-column: span 2;
    order: 1;
    height: 500px;
  }
  
  .light-theme .left-panel, .light-theme .right-panel {
    order: 2;
  }
  
  .light-theme .private-chain {
    left: 3%;
    top: 40%;
  }
  
  .light-theme .alliance-chain {
    left: 3%;
    top: 66%;
  }
}

@media (max-width: 992px) {
  .light-theme .dashboard-content {
    grid-template-columns: 1fr;
    height: auto;
    overflow-y: auto;
  }
  
  .light-theme .center-panel, .light-theme .left-panel, .light-theme .right-panel {
    grid-column: span 1;
  }
  
  .light-theme .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .light-theme .header-controls {
    width: 100%;
    justify-content: space-between;
  }
  
  .light-theme .footer-content {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .light-theme .system-status, .light-theme .data-info {
    justify-content: flex-start;
  }
  
  .light-theme .chain-label {
    font-size: 11px;
  }
  
  .light-theme .private-chain {
    left: 5%;
    top: 38%;
  }
  
  .light-theme .alliance-chain {
    left: 5%;
    top: 64%;
  }
}
</style>