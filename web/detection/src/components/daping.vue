<template>
  <div class="blockchain-dashboard">
    <!-- 顶部标题栏 -->
    <div class="dashboard-header">
      <div class="title-section">
        <h1 class="main-title">
          <span class="title-gradient">半轴溯源</span>
          <span class="title-sub">区块链数据监控平台</span>
        </h1>
        <div class="header-info">
          <div class="real-time-indicator">
            <div class="pulse"></div>
            实时数据同步中
          </div>
          <div class="timestamp">
            最后更新: {{ lastUpdateTime }}
          </div>
        </div>
      </div>
      
      <div class="network-status">
        <div class="status-item">
          <div class="status-label">网络状态</div>
          <div class="status-value active">
            <i class="status-dot"></i> 运行正常
          </div>
        </div>
        <div class="status-item">
          <div class="status-label">节点数量</div>
          <div class="status-value">{{ networkStats.nodes }} 个</div>
        </div>
        <div class="status-item">
          <div class="status-label">区块高度</div>
          <div class="status-value highlight">{{ networkStats.blockHeight }}</div>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="dashboard-content">
      <!-- 左侧面板 -->
      <div class="left-panel">
        <!-- 区块信息卡片 -->
        <div class="card blockchain-stats">
          <div class="card-header">
            <h3>区块链实时状态</h3>
            <div class="card-refresh" @click="refreshData">
              <i class="refresh-icon">↻</i>
              刷新
            </div>
          </div>
          <div class="stats-grid">
            <div class="stat-item" v-for="stat in blockchainStats" :key="stat.id">
              <div class="stat-icon" :style="{ background: stat.color }">
                {{ stat.icon }}
              </div>
              <div class="stat-content">
                <div class="stat-label">{{ stat.label }}</div>
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-trend" :class="stat.trend">
                  {{ stat.change }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 溯源流转图 -->
        <div class="card trace-flow">
          <div class="card-header">
            <h3>半轴生产溯源流转</h3>
          </div>
          <div class="flow-container">
            <div class="flow-line">
              <div class="flow-node" v-for="(node, index) in traceNodes" :key="index"
                   :class="{ active: node.active }">
                <div class="node-icon">{{ node.icon }}</div>
                <div class="node-label">{{ node.label }}</div>
                <div class="node-time">{{ node.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间主视图 -->
      <div class="center-panel">
        <!-- 区块链可视化 -->
        <div class="card blockchain-visualization">
          <div class="card-header">
            <h3>区块链网络拓扑</h3>
            <div class="visualization-controls">
              <button @click="toggleAutoRotate" class="control-btn">
                {{ autoRotate ? '暂停' : '旋转' }}
              </button>
              <button @click="resetView" class="control-btn">重置视角</button>
            </div>
          </div>
          <div class="visualization-container">
            <div class="blockchain-3d" ref="blockchain3d">
              <!-- 区块链节点模拟图 -->
              <div class="node-graph">
                <div class="graph-center">
                  <div class="center-node">
                    <div class="node-ring"></div>
                    <div class="node-core">⛓</div>
                  </div>
                </div>
                <div class="satellite-node" v-for="node in satelliteNodes" 
                     :key="node.id"
                     :style="{
                       left: node.x + '%',
                       top: node.y + '%',
                       animationDelay: node.delay + 's'
                     }">
                  <div class="satellite-icon">{{ node.icon }}</div>
                  <div class="satellite-label">{{ node.label }}</div>
                </div>
              </div>
            </div>
            <div class="visualization-stats">
              <div class="viz-stat">
                <div class="viz-label">活跃节点</div>
                <div class="viz-value">{{ activeNodes }}</div>
              </div>
              <div class="viz-stat">
                <div class="viz-label">数据流</div>
                <div class="viz-value">{{ dataFlow }} TPS</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 实时交易流 -->
        <div class="card transaction-stream">
          <div class="card-header">
            <h3>实时交易数据流</h3>
          </div>
          <div class="stream-container">
            <div class="stream-items">
              <div v-for="tx in recentTransactions" :key="tx.id" 
                   class="stream-item" :class="tx.type">
                <div class="stream-icon">{{ tx.icon }}</div>
                <div class="stream-content">
                  <div class="stream-header">
                    <span class="stream-id">TX#{{ tx.id }}</span>
                    <span class="stream-time">{{ tx.time }}</span>
                  </div>
                  <div class="stream-desc">{{ tx.description }}</div>
                  <div class="stream-meta">
                    <span class="stream-amount">{{ tx.amount }}</span>
                    <span class="stream-status" :class="tx.status">{{ tx.statusText }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <!-- 数据统计图表 -->
        <div class="card data-charts">
          <div class="card-header">
            <h3>数据统计图表</h3>
          </div>
          <div class="chart-container">
            <div class="chart-wrapper">
              <div class="chart-placeholder" v-if="!chartLoaded">
                <div class="placeholder-content">
                  <div class="placeholder-icon">📊</div>
                  <div class="placeholder-text">加载图表数据...</div>
                </div>
              </div>
              <div class="chart-canvas" ref="chartCanvas"></div>
            </div>
          </div>
        </div>

        <!-- 智能合约监控 -->
        <div class="card contract-monitor">
          <div class="card-header">
            <h3>智能合约监控</h3>
          </div>
          <div class="contract-list">
            <div v-for="contract in smartContracts" :key="contract.id" 
                 class="contract-item" :class="{ active: contract.active }">
              <div class="contract-icon">
                <div class="contract-pulse" v-if="contract.active"></div>
                {{ contract.icon }}
              </div>
              <div class="contract-info">
                <div class="contract-name">{{ contract.name }}</div>
                <div class="contract-address">{{ contract.address }}</div>
                <div class="contract-status">
                  <span :class="contract.status">{{ contract.statusText }}</span>
                  <span class="contract-calls">{{ contract.calls }} 次调用</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部状态栏 -->
    <div class="dashboard-footer">
      <div class="footer-stats">
        <div class="footer-stat">
          <span class="stat-label">总交易量:</span>
          <span class="stat-value">{{ totalTransactions.toLocaleString() }}</span>
        </div>
        <div class="footer-stat">
          <span class="stat-label">溯源记录:</span>
          <span class="stat-value">{{ traceRecords.toLocaleString() }} 条</span>
        </div>
        <div class="footer-stat">
          <span class="stat-label">系统运行:</span>
          <span class="stat-value">{{ uptime }}</span>
        </div>
      </div>
      <div class="data-warning">
        <div class="warning-icon">⚠</div>
        <span>所有数据已上链存证，不可篡改</span>
      </div>
    </div>

    <!-- 区块链粒子背景 -->
    <div class="blockchain-particles"></div>
  </div>
</template>

<script>
export default {
  name: 'BlockchainDashboard',
  data() {
    return {
      // 区块链统计数据
      blockchainStats: [
        { id: 1, icon: '⛓', label: '区块生成速度', value: '3.2s/块', change: '+0.1s', trend: 'up', color: '#4F46E5' },
        { id: 2, icon: '💰', label: '交易手续费', value: '0.002 ETH', change: '-5%', trend: 'down', color: '#10B981' },
        { id: 3, icon: '📦', label: '溯源产品数', value: '1,248', change: '+12', trend: 'up', color: '#F59E0B' },
        { id: 4, icon: '🔗', label: '智能合约', value: '42', change: '+2', trend: 'up', color: '#EF4444' },
        { id: 5, icon: '🌐', label: '节点分布', value: '16 城市', change: '+1', trend: 'up', color: '#8B5CF6' },
        { id: 6, icon: '⚡', label: '网络吞吐', value: '1,248 TPS', change: '+15%', trend: 'up', color: '#06B6D4' }
      ],
      
      // 网络状态
      networkStats: {
        nodes: 48,
        blockHeight: 1245896,
        status: 'active'
      },
      
      // 溯源流转节点
      traceNodes: [
        { icon: '🏭', label: '原材料采购', time: '2024-01-15', active: true },
        { icon: '⚙️', label: '粗加工', time: '2024-01-18', active: true },
        { icon: '🔧', label: '精加工', time: '2024-01-20', active: true },
        { icon: '📦', label: '质量检测', time: '2024-01-22', active: true },
        { icon: '🚚', label: '物流运输', time: '2024-01-25', active: true },
        { icon: '🏪', label: '经销商', time: '2024-01-28', active: false },
        { icon: '🚗', label: '终端客户', time: '2024-02-01', active: false }
      ],
      
      // 卫星节点数据
      satelliteNodes: [
        { id: 1, x: 20, y: 20, icon: '🏭', label: '工厂节点', delay: 0 },
        { id: 2, x: 80, y: 20, icon: '📦', label: '仓库节点', delay: 0.5 },
        { id: 3, x: 80, y: 50, icon: '🚚', label: '物流节点', delay: 1 },
        { id: 4, x: 80, y: 80, icon: '🏪', label: '销售节点', delay: 1.5 },
        { id: 5, x: 20, y: 80, icon: '🔧', label: '服务节点', delay: 2 },
        { id: 6, x: 20, y: 50, icon: '👥', label: '用户节点', delay: 2.5 }
      ],
      
      // 实时交易数据
      recentTransactions: [
        { id: '789012', icon: '🏭', type: 'manufacture', time: '刚刚', 
          description: '半轴轴承生产记录上链', amount: '轴承×50', status: 'success', statusText: '已确认' },
        { id: '789011', icon: '🔍', type: 'inspect', time: '1分钟前',
          description: '质量检测报告存证', amount: '报告×3', status: 'success', statusText: '已确认' },
        { id: '789010', icon: '🚚', type: 'logistics', time: '3分钟前',
          description: '物流信息更新', amount: '批次×1', status: 'pending', statusText: '处理中' },
        { id: '789009', icon: '🏪', type: 'sale', time: '5分钟前',
          description: '经销商入库记录', amount: '半轴×100', status: 'success', statusText: '已确认' },
        { id: '789008', icon: '🔧', type: 'repair', time: '10分钟前',
          description: '维修保养记录', amount: '服务×1', status: 'success', statusText: '已确认' }
      ],
      
      // 智能合约数据
      smartContracts: [
        { id: 1, icon: '📦', name: '溯源合约', address: '0x7a3...f2c1', active: true, status: 'active', statusText: '运行中', calls: 1248 },
        { id: 2, icon: '💰', name: '支付合约', address: '0x8b4...e3d2', active: true, status: 'active', statusText: '运行中', calls: 892 },
        { id: 3, icon: '🔒', name: '权限合约', address: '0x9c5...d4e3', active: true, status: 'active', statusText: '运行中', calls: 456 },
        { id: 4, icon: '📊', name: '数据合约', address: '0xad6...c5f4', active: false, status: 'inactive', statusText: '待激活', calls: 0 }
      ],
      
      // 状态变量
      lastUpdateTime: new Date().toLocaleTimeString(),
      autoRotate: true,
      activeNodes: 32,
      dataFlow: 1248,
      totalTransactions: 892456,
      traceRecords: 12480,
      uptime: '15天 2小时 36分',
      chartLoaded: false,
      dataInterval: null
    }
  },
  
  mounted() {
    this.initChart();
    this.startDataUpdates();
  },
  
  beforeUnmount() {
    this.stopDataUpdates();
  },
  
  methods: {
    // 初始化图表
    initChart() {
      this.chartLoaded = true;
      // 这里使用CSS绘制简单图表
      const canvas = this.$refs.chartCanvas;
      if (canvas) {
        // 创建一个简单的SVG图表
        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('width', '100%');
        svg.setAttribute('height', '100%');
        
        // 绘制折线图
        const points = '0,60 20,40 40,80 60,30 80,70 100,50 120,90';
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'polyline');
        line.setAttribute('points', points);
        line.setAttribute('fill', 'none');
        line.setAttribute('stroke', '#8B5CF6');
        line.setAttribute('stroke-width', '2');
        
        // 绘制柱状图
        const bars = [
          { x: 30, y: 70, width: 15, height: 30 },
          { x: 50, y: 40, width: 15, height: 60 },
          { x: 70, y: 60, width: 15, height: 40 },
          { x: 90, y: 30, width: 15, height: 70 },
          { x: 110, y: 50, width: 15, height: 50 }
        ];
        
        svg.appendChild(line);
        
        bars.forEach(bar => {
          const rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
          rect.setAttribute('x', bar.x);
          rect.setAttribute('y', bar.y);
          rect.setAttribute('width', bar.width);
          rect.setAttribute('height', bar.height);
          rect.setAttribute('fill', '#10B981');
          rect.setAttribute('rx', '2');
          svg.appendChild(rect);
        });
        
        canvas.appendChild(svg);
      }
    },
    
    // 刷新数据
    refreshData() {
      // 模拟数据更新
      this.networkStats.blockHeight += Math.floor(Math.random() * 10);
      this.networkStats.nodes += Math.floor(Math.random() * 3) - 1;
      this.activeNodes += Math.floor(Math.random() * 5) - 2;
      this.dataFlow += Math.floor(Math.random() * 100) - 50;
      this.lastUpdateTime = new Date().toLocaleTimeString();
      
      // 更新区块统计
      this.blockchainStats.forEach(stat => {
        if (stat.label === '区块生成速度') {
          const base = 3.2;
          const change = (Math.random() * 0.2 - 0.1).toFixed(1);
          stat.value = (base + parseFloat(change)).toFixed(1) + 's/块';
          stat.change = change >= 0 ? `+${change}s` : `${change}s`;
        }
      });
    },
    
    // 切换自动旋转
    toggleAutoRotate() {
      this.autoRotate = !this.autoRotate;
    },
    
    // 重置视角
    resetView() {
      // 重置节点位置动画
      this.satelliteNodes.forEach(node => {
        node.x = 20 + Math.random() * 60;
        node.y = 20 + Math.random() * 60;
      });
    },
    
    // 开始数据更新
    startDataUpdates() {
      this.dataInterval = setInterval(() => {
        this.refreshData();
        
        // 随机激活一个流转节点
        const inactiveNodes = this.traceNodes.filter(node => !node.active);
        if (inactiveNodes.length > 0) {
          const randomIndex = Math.floor(Math.random() * inactiveNodes.length);
          inactiveNodes[randomIndex].active = true;
        }
        
        // 添加新的交易
        const newTransaction = {
          id: (789000 + Math.floor(Math.random() * 1000)).toString(),
          icon: ['🏭', '🔍', '🚚', '🏪', '🔧'][Math.floor(Math.random() * 5)],
          type: ['manufacture', 'inspect', 'logistics', 'sale', 'repair'][Math.floor(Math.random() * 5)],
          time: '刚刚',
          description: '新的溯源记录上链',
          amount: '记录×1',
          status: Math.random() > 0.3 ? 'success' : 'pending',
          statusText: Math.random() > 0.3 ? '已确认' : '处理中'
        };
        
        this.recentTransactions.unshift(newTransaction);
        if (this.recentTransactions.length > 5) {
          this.recentTransactions.pop();
        }
        
      }, 5000); // 每5秒更新一次
    },
    
    // 停止数据更新
    stopDataUpdates() {
      if (this.dataInterval) {
        clearInterval(this.dataInterval);
      }
    }
  }
}
</script>

<style scoped>
.blockchain-dashboard {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
  color: #F9FAFB;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  overflow: hidden;
}

/* 头部样式 */
.dashboard-header {
  padding: 1.5rem 2rem;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 10;
  position: relative;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.main-title {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 0;
}

.title-gradient {
  background: linear-gradient(135deg, #8B5CF6 0%, #3B82F6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 2rem;
  font-weight: 800;
}

.title-sub {
  color: #94A3B8;
  font-size: 1rem;
  font-weight: 500;
}

.header-info {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.real-time-indicator {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #10B981;
  font-size: 0.875rem;
}

.pulse {
  width: 8px;
  height: 8px;
  background: #10B981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
  100% { opacity: 1; transform: scale(1); }
}

.network-status {
  display: flex;
  gap: 2rem;
}

.status-item {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.status-label {
  color: #94A3B8;
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}

.status-value {
  font-size: 1.25rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.status-value.active {
  color: #10B981;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #10B981;
  border-radius: 50%;
  display: inline-block;
}

.highlight {
  color: #8B5CF6;
  font-weight: 700;
}

/* 主体内容布局 */
.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  gap: 1.5rem;
  padding: 2rem;
  height: calc(100vh - 120px);
}

/* 卡片通用样式 */
.card {
  background: rgba(30, 41, 59, 0.8);
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-2px);
  border-color: rgba(139, 92, 246, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.card-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #F9FAFB;
}

.card-refresh {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #94A3B8;
  cursor: pointer;
  transition: color 0.3s ease;
  padding: 0.5rem;
  border-radius: 8px;
}

.card-refresh:hover {
  color: #8B5CF6;
  background: rgba(139, 92, 246, 0.1);
}

.refresh-icon {
  transition: transform 0.3s ease;
}

.card-refresh:hover .refresh-icon {
  transform: rotate(180deg);
}

/* 统计网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(4px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: #94A3B8;
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}

.stat-value {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.stat-trend {
  font-size: 0.875rem;
}

.stat-trend.up {
  color: #10B981;
}

.stat-trend.down {
  color: #EF4444;
}

/* 溯源流转图 */
.flow-container {
  padding: 1rem 0;
}

.flow-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.flow-line::before {
  content: '';
  position: absolute;
  top: 30px;
  left: 50px;
  right: 50px;
  height: 2px;
  background: linear-gradient(90deg, #8B5CF6, #3B82F6);
  z-index: 1;
}

.flow-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  z-index: 2;
  position: relative;
  transition: all 0.3s ease;
}

.flow-node.active .node-icon {
  background: linear-gradient(135deg, #8B5CF6, #3B82F6);
  transform: scale(1.1);
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.5);
}

.node-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.node-label {
  font-size: 0.875rem;
  font-weight: 500;
  text-align: center;
}

.node-time {
  font-size: 0.75rem;
  color: #94A3B8;
}

/* 区块链可视化 */
.visualization-container {
  position: relative;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.3);
}

.blockchain-3d {
  width: 100%;
  height: 100%;
  position: relative;
}

.node-graph {
  width: 100%;
  height: 100%;
  position: relative;
}

.graph-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.center-node {
  position: relative;
  width: 80px;
  height: 80px;
}

.node-ring {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid rgba(139, 92, 246, 0.5);
  border-radius: 50%;
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.node-core {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #8B5CF6, #3B82F6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  box-shadow: 0 0 30px rgba(139, 92, 246, 0.5);
}

.satellite-node {
  position: absolute;
  width: 60px;
  transform: translate(-50%, -50%);
  animation: float 3s ease-in-out infinite;
  text-align: center;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0); }
  50% { transform: translate(-50%, -50%) translateY(-10px); }
}

.satellite-icon {
  width: 40px;
  height: 40px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
  transition: all 0.3s ease;
}

.satellite-node:hover .satellite-icon {
  background: rgba(139, 92, 246, 0.3);
  transform: scale(1.1);
}

.satellite-label {
  font-size: 0.75rem;
  color: #94A3B8;
}

.visualization-controls {
  display: flex;
  gap: 0.5rem;
}

.control-btn {
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #F9FAFB;
  cursor: pointer;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: #8B5CF6;
}

.visualization-stats {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  display: flex;
  gap: 1rem;
}

.viz-stat {
  background: rgba(15, 23, 42, 0.8);
  padding: 0.75rem 1rem;
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.viz-label {
  color: #94A3B8;
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}

.viz-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: #8B5CF6;
}

/* 交易流样式 */
.stream-container {
  max-height: 200px;
  overflow-y: auto;
}

.stream-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  margin-bottom: 0.75rem;
  transition: all 0.3s ease;
}

.stream-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

.stream-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  background: rgba(139, 92, 246, 0.2);
}

.stream-content {
  flex: 1;
}

.stream-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.stream-id {
  font-family: 'Monaco', monospace;
  color: #8B5CF6;
  font-size: 0.875rem;
}

.stream-time {
  color: #94A3B8;
  font-size: 0.75rem;
}

.stream-desc {
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
}

.stream-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stream-amount {
  font-weight: 600;
  color: #10B981;
}

.stream-status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
}

.stream-status.success {
  background: rgba(16, 185, 129, 0.2);
  color: #10B981;
}

.stream-status.pending {
  background: rgba(245, 158, 11, 0.2);
  color: #F59E0B;
}

/* 图表容器 */
.chart-container {
  height: 250px;
}

.chart-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

.chart-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-content {
  text-align: center;
}

.placeholder-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.placeholder-text {
  color: #94A3B8;
}

.chart-canvas {
  width: 100%;
  height: 100%;
}

.chart-canvas svg {
  width: 100%;
  height: 100%;
}

/* 智能合约样式 */
.contract-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.contract-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.contract-item.active {
  border-left: 4px solid #8B5CF6;
  background: rgba(139, 92, 246, 0.1);
}

.contract-icon {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  background: rgba(139, 92, 246, 0.2);
}

.contract-pulse {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 10px;
  background: rgba(139, 92, 246, 0.4);
  animation: contractPulse 2s infinite;
}

@keyframes contractPulse {
  0% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.1); }
  100% { opacity: 1; transform: scale(1); }
}

.contract-info {
  flex: 1;
}

.contract-name {
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.contract-address {
  font-family: 'Monaco', monospace;
  font-size: 0.75rem;
  color: #94A3B8;
  margin-bottom: 0.5rem;
  word-break: break-all;
}

.contract-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contract-status .active {
  color: #10B981;
}

.contract-status .inactive {
  color: #94A3B8;
}

.contract-calls {
  font-size: 0.875rem;
  color: #94A3B8;
}

/* 底部状态栏 */
.dashboard-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1rem 2rem;
  background: rgba(15, 23, 42, 0.9);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 10;
}

.footer-stats {
  display: flex;
  gap: 2rem;
}

.footer-stat {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stat-label {
  color: #94A3B8;
  font-size: 0.875rem;
}

.stat-value {
  font-weight: 600;
  color: #F9FAFB;
}

.data-warning {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #F59E0B;
  font-size: 0.875rem;
}

.warning-icon {
  font-size: 1.25rem;
}

/* 区块链粒子背景 */
.blockchain-particles {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
  background: 
    radial-gradient(circle at 20% 80%, rgba(139, 92, 246, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(59, 130, 246, 0.1) 0%, transparent 50%);
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .dashboard-content {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
  
  .network-status {
    width: 100%;
    justify-content: space-between;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .flow-line {
    flex-wrap: wrap;
    gap: 1rem;
  }
  
  .flow-line::before {
    display: none;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #8B5CF6;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #7C3AED;
}
</style>