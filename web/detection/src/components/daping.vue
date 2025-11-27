<template>
  <div class="big-screen">
    <div class="bg-grid"></div>

    <div class="header-bar">
      <div class="header-decoration-left"></div>
      <h1 class="title">实时监控中心</h1>
      <div class="header-decoration-right"></div>
      <el-button
        type="primary"
        icon="el-icon-refresh"
        size="mini"
        class="refresh-btn glass-btn"
        @click="Refresh"
        >刷新数据</el-button
      >
    </div>

    <div class="layout">
      <section class="panel top-left">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">设备状态</h2>
        <div class="chart-group compact-chart-group">
          <div ref="gaugeOnline" class="chart-container compact-chart"></div>
          <div ref="gaugeOffline" class="chart-container compact-chart"></div>
          <div ref="gaugeWarning" class="chart-container compact-chart"></div>
        </div>
      </section>

      <section class="panel top-center main-monitor">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">
          实时监控画面
          <span class="live-dot"></span>
        </h2>
        <div class="monitor-feed" @click="Refresh">
          <div class="scan-line"></div>
          
          <div v-if="imageData" class="image-wrapper">
            <img
              :src="'data:image/jpeg;base64,' + imageData"
              alt="监控图像"
              class="monitoring-image"
            />
            <div class="image-overlay">
              <span class="overlay-tag">AI DETECTING</span>
              <span class="overlay-time">{{ new Date().toLocaleTimeString() }}</span>
            </div>
          </div>
          <div v-else class="no-image">
            <div class="loader-ring"></div>
            <p>等待信号接入...</p>
            <p class="click-refresh-tip">点击区域手动刷新</p>
          </div>
        </div>
      </section>

      <section class="panel top-right">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">运行数据</h2>
        <div class="chart-group compact-chart-group">
          <div ref="gaugeSpeed" class="chart-container compact-chart"></div>
          <div ref="gaugeLoad" class="chart-container compact-chart"></div>
          <div ref="gaugeTemp" class="chart-container compact-chart"></div>
        </div>
      </section>

      <section class="panel mid-left">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">异常统计</h2>
        <div ref="barAbnormal" class="chart-container"></div>
      </section>

      <section class="panel mid-center">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">缺陷信息</h2>
        <div class="defect-table-box">
          <div class="table-header">
            <span class="col-seq">序号</span>
            <span class="col-name">缺陷名称</span>
            <span class="col-rate">概率</span>
          </div>
          <div class="table-body">
            <div
              v-for="(d, i) in showDefectList"
              :key="i"
              class="table-row"
            >
              <span class="col-seq">{{ d.category ? i + 1 : '-' }}</span>
              <span class="col-name">{{ d.category || '--' }}</span>
              <span class="col-rate">
                <div v-if="d.category" class="progress-bar-container">
                  <div class="progress-track">
                     <div class="progress-fill" :class="getProbabilityClass(d.score)" :style="{width: (d.score * 100) + '%'}"></div>
                  </div>
                  <span class="rate-text">{{ (d.score * 100).toFixed(1) }}%</span>
                </div>
              </span>
            </div>
          </div>
          <div class="table-footer">
            <span>当前帧缺陷总数</span>
            <strong class="glow-text">{{ defectList.length }}</strong>
          </div>
        </div>
      </section>

      <section class="panel mid-right">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">缺陷分布</h2>
        <div ref="defectDistribution" class="chart-container"></div>
      </section>

      <section class="panel bottom-left">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">区域分布</h2>
        <div ref="pieRegion" class="chart-container"></div>
      </section>

      <section class="panel bottom-center">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">趋势分析</h2>
        <div ref="lineTrend" class="chart-container"></div>
      </section>

      <section class="panel bottom-right">
        <div class="panel-decoration"><span class="angle"></span><span class="angle"></span><span class="angle"></span><span class="angle"></span></div>
        <h2 class="panel-title">告警信息</h2>
        <div class="alarm-list" ref="alarmList">
          <div
            class="alarm-item"
            v-for="(alarm, index) in alarms"
            :key="index"
          >
            <div class="alarm-icon-wrapper" :class="alarm.level">
               <i class="el-icon-warning-outline"></i>
            </div>
            <div class="alarm-content">
              <div class="alarm-header">
                <span class="alarm-title">{{ alarm.title }}</span>
                <span class="alarm-time">{{ alarm.time }}</span>
              </div>
              <p class="alarm-desc">{{ alarm.desc }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>

    <div
      class="connection-status"
      :class="{
        connected: eventSourcePicture && eventSourcePicture.readyState === 1
      }"
    >
      <span class="status-dot"></span>
      <span class="status-text">
        {{
          eventSourcePicture && eventSourcePicture.readyState === 1
            ? "SYSTEM ONLINE"
            : "SYSTEM OFFLINE"
        }}
      </span>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import sseManager from '@/utils/sseManager';

export default {
  name: "MonitorDashboard",
  data() {
    return {
      charts: {},
      devices: [
        { id: "DEV-001", name: "前门摄像头", status: "在线", time: "10:23:45" },
        { id: "DEV-002", name: "后门摄像头", status: "在线", time: "10:22:18" },
        { id: "DEV-003", name: "仓库传感器", status: "在线", time: "10:21:05" },
        { id: "DEV-004", name: "机房监控", status: "离线", time: "09:45:33" },
        { id: "DEV-005", name: "温度探测器", status: "在线", time: "10:20:37" }
      ],
      alarms: [
        {
          level: "high",
          title: "温度异常",
          desc: "机房温度超过阈值(32℃)",
          time: "10:15:22"
        },
        {
          level: "medium",
          title: "网络波动",
          desc: "后门摄像头网络不稳定",
          time: "09:58:41"
        },
        {
          level: "low",
          title: "存储不足",
          desc: "录像存储剩余空间不足20%",
          time: "09:30:15"
        },
        {
          level: "high",
          title: "设备离线",
          desc: "机房监控已离线30分钟",
          time: "09:15:07"
        },
        {
          level: "low",
          title: "电压不稳",
          desc: "仓库区域电压波动",
          time: "08:45:52"
        }
      ],
      imageData: null,
      defectList: [],
      isConnected: false // 连接状态
    };
  },
  computed: {
    /* 始终只显示前6条，不足补空行，保证表格高度恒定 */
    showDefectList() {
      const empty = { category: "", score: 0 };
      const ret = [...this.defectList];
      while (ret.length < 6) ret.push(empty);
      return ret.slice(0, 6);
    },
    // 模拟 eventSourcePicture 用于显示连接状态
    eventSourcePicture() {
      return {
        readyState: this.isConnected ? 1 : 0
      };
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts();
      // 订阅全局SSE
      sseManager.subscribe('daping', this.handleSSEMessage);
      window.addEventListener("resize", this.handleResize);
      
      // 额外保险：500ms后再次检查图表尺寸
      setTimeout(() => {
        this.handleResize();
      }, 500);
    });
  },
  beforeDestroy() {
    Object.values(this.charts).forEach((chart) => chart.dispose());
    window.removeEventListener("resize", this.handleResize);
    // 取消订阅
    sseManager.unsubscribe('daping');
  },
  methods: {
    /* ---- SSE消息处理 ---- */
    handleSSEMessage(type, data) {
      if (type === 'connection') {
        this.isConnected = data.connected;
        if (data.connected) {
          this.$message?.success('实时连接已建立');
        }
      } else if (type === 'message') {
        const img = data.imgBase64;
        if (img !== null && img !== undefined && img !== "") {
          this.imageData = img;
          this.defectList = data.defections || [];
          console.log(
            "📷 收到图片帧，长度:",
            img.length,
            "缺陷数:",
            this.defectList.length
          );
        }
      }
    },
    // 辅助方法：为CSS class提供名称
    getProbabilityClass(score) {
      if (score >= 0.7) return "bg-danger";
      if (score >= 0.4) return "bg-warning";
      return "bg-info";
    },
    Refresh() {
      console.log("🔄 手动刷新数据");
      this.$message?.info("正在刷新数据...");
      // 重新初始化SSE连接
      sseManager.close();
      sseManager.init();
      // 强制重绘图表
      this.$nextTick(() => {
        this.handleResize();
      });
    },
    handleResize() {
      Object.values(this.charts).forEach((chart) => {
        if (chart && !chart.isDisposed()) {
          try {
            chart.resize();
          } catch (error) {
            console.warn('Chart resize error:', error);
          }
        }
      });
    },

    /* ---- 图表初始化 ---- */
    initCharts() {
      this.initGaugeOnline();
      this.initGaugeOffline();
      this.initGaugeWarning();
      this.initGaugeSpeed();
      this.initGaugeLoad();
      this.initGaugeTemp();
      this.initBarAbnormal();
      this.initLineTrend();
      this.initPieRegion();
      this.initDefectDistribution();
      
      // 立即执行一次 resize
      this.$nextTick(() => {
        this.handleResize();
      });
    },

    /* ---- 仪表盘图表 ---- */
    initGaugeOnline() {
      const chart = echarts.init(this.$refs.gaugeOnline);
      this.charts.gaugeOnline = chart;
      chart.setOption({
        series: [
          {
            type: "gauge",
            radius: "90%",
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '65%'],
            axisLine: {
              lineStyle: { 
                width: 10, 
                color: [[1, "rgba(255, 255, 255, 0.1)"]]
              }
            },
            progress: {
              show: true,
              width: 10,
              roundCap: true,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: "#00ffcc" },
                  { offset: 1, color: "#00ccff" }
                ]),
                shadowBlur: 10,
                shadowColor: '#00ccff'
              }
            },
            pointer: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: { show: false },
            detail: {
              valueAnimation: true,
              formatter: "{value}%",
              offsetCenter: [0, "0%"],
              fontSize: 16,
              color: "#fff",
              fontFamily: 'Orbitron',
              fontWeight: "bold"
            },
            title: {
              offsetCenter: [0, "30%"],
              fontSize: 12,
              color: "#99ccff"
            },
            data: [{ value: 92, name: "在线率" }]
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initGaugeOffline() {
      const chart = echarts.init(this.$refs.gaugeOffline);
      this.charts.gaugeOffline = chart;
      chart.setOption({
        series: [
          {
            type: "gauge",
            radius: "90%",
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '65%'],
            axisLine: {
              lineStyle: { 
                width: 10, 
                color: [[1, "rgba(255, 255, 255, 0.1)"]]
              }
            },
            progress: {
              show: true,
              width: 10,
              roundCap: true,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: "#ff9966" },
                  { offset: 1, color: "#ff6666" }
                ]),
                shadowBlur: 10,
                shadowColor: '#ff6666'
              }
            },
            pointer: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: { show: false },
            detail: {
              valueAnimation: true,
              formatter: "{value}%",
              offsetCenter: [0, "0%"],
              fontSize: 16,
              color: "#fff",
              fontFamily: 'Orbitron',
              fontWeight: "bold"
            },
            title: {
              offsetCenter: [0, "30%"],
              fontSize: 12,
              color: "#99ccff"
            },
            data: [{ value: 5, name: "离线率" }]
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initGaugeWarning() {
      const chart = echarts.init(this.$refs.gaugeWarning);
      this.charts.gaugeWarning = chart;
      chart.setOption({
        series: [
          {
            type: "gauge",
            radius: "90%",
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '65%'],
            axisLine: {
              lineStyle: { 
                width: 10, 
                color: [[1, "rgba(255, 255, 255, 0.1)"]]
              }
            },
            progress: {
              show: true,
              width: 10,
              roundCap: true,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: "#ffcc00" },
                  { offset: 1, color: "#ff9900" }
                ]),
                shadowBlur: 10,
                shadowColor: '#ff9900'
              }
            },
            pointer: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: { show: false },
            detail: {
              valueAnimation: true,
              formatter: "{value}%",
              offsetCenter: [0, "0%"],
              fontSize: 16,
              color: "#fff",
              fontFamily: 'Orbitron',
              fontWeight: "bold"
            },
            title: {
              offsetCenter: [0, "30%"],
              fontSize: 12,
              color: "#99ccff"
            },
            data: [{ value: 3, name: "告警率" }]
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initGaugeSpeed() {
      const chart = echarts.init(this.$refs.gaugeSpeed);
      this.charts.gaugeSpeed = chart;
      chart.setOption({
        series: [
          {
            type: "gauge",
            radius: "90%",
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '65%'],
            axisLine: {
              lineStyle: { 
                width: 10, 
                color: [[1, "rgba(255, 255, 255, 0.1)"]]
              }
            },
            progress: {
              show: true,
              width: 10,
              roundCap: true,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: "#9966ff" },
                  { offset: 1, color: "#6666ff" }
                ]),
                shadowBlur: 10,
                shadowColor: '#6666ff'
              }
            },
            pointer: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: { show: false },
            detail: {
              valueAnimation: true,
              formatter: "{value}",
              offsetCenter: [0, "0%"],
              fontSize: 16,
              color: "#fff",
              fontFamily: 'Orbitron',
              fontWeight: "bold"
            },
            title: {
              offsetCenter: [0, "30%"],
              fontSize: 12,
              color: "#99ccff"
            },
            data: [{ value: 45, name: "Mbps" }]
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initGaugeLoad() {
      const chart = echarts.init(this.$refs.gaugeLoad);
      this.charts.gaugeLoad = chart;
      chart.setOption({
        series: [
          {
            type: "gauge",
            radius: "90%",
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '65%'],
            axisLine: {
              lineStyle: { 
                width: 10, 
                color: [[1, "rgba(255, 255, 255, 0.1)"]]
              }
            },
            progress: {
              show: true,
              width: 10,
              roundCap: true,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: "#66ccff" },
                  { offset: 1, color: "#3399ff" }
                ]),
                shadowBlur: 10,
                shadowColor: '#3399ff'
              }
            },
            pointer: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: { show: false },
            detail: {
              valueAnimation: true,
              formatter: "{value}%",
              offsetCenter: [0, "0%"],
              fontSize: 16,
              color: "#fff",
              fontFamily: 'Orbitron',
              fontWeight: "bold"
            },
            title: {
              offsetCenter: [0, "30%"],
              fontSize: 12,
              color: "#99ccff"
            },
            data: [{ value: 68, name: "负载率" }]
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initGaugeTemp() {
      const chart = echarts.init(this.$refs.gaugeTemp);
      this.charts.gaugeTemp = chart;
      chart.setOption({
        series: [
          {
            type: "gauge",
            radius: "90%",
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '65%'],
            axisLine: {
              lineStyle: { 
                width: 10, 
                color: [[1, "rgba(255, 255, 255, 0.1)"]]
              }
            },
            progress: {
              show: true,
              width: 10,
              roundCap: true,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: "#ff6666" },
                  { offset: 1, color: "#cc0000" }
                ]),
                shadowBlur: 10,
                shadowColor: '#cc0000'
              }
            },
            pointer: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: { show: false },
            detail: {
              valueAnimation: true,
              formatter: "{value}℃",
              offsetCenter: [0, "0%"],
              fontSize: 16,
              color: "#fff",
              fontFamily: 'Orbitron',
              fontWeight: "bold"
            },
            title: {
              offsetCenter: [0, "30%"],
              fontSize: 12,
              color: "#99ccff"
            },
            data: [{ value: 28, name: "设备温度" }]
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initBarAbnormal() {
      const chart = echarts.init(this.$refs.barAbnormal);
      this.charts.barAbnormal = chart;
      chart.setOption({
        grid: { left: "5%", right: "5%", top: "15%", bottom: "5%", containLabel: true },
        xAxis: {
          type: "category",
          data: ["闯红灯", "超速", "闯禁行", "逆行", "违停"],
          axisLine: { lineStyle: { color: "#409eff" } },
          axisLabel: { color: "#fff", fontSize: 11 },
          axisTick: { show: false }
        },
        yAxis: {
          type: "value",
          axisLine: { show: false },
          axisLabel: { color: "#8cbde5", fontSize: 11 },
          splitLine: { lineStyle: { color: "rgba(255,255,255,0.05)", type: 'dashed' } }
        },
        series: [
          {
            type: "bar",
            data: [120, 80, 50, 70, 30],
            barWidth: "30%",
            itemStyle: {
              borderRadius: [20, 20, 0, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#00f2ff" },
                { offset: 1, color: "rgba(0,242,255,0.1)" }
              ])
            }
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initLineTrend() {
      const chart = echarts.init(this.$refs.lineTrend);
      this.charts.lineTrend = chart;
      chart.setOption({
        grid: { left: "5%", right: "5%", top: "15%", bottom: "5%", containLabel: true },
        xAxis: {
          type: "category",
          data: ["1月", "3月", "5月", "7月", "9月", "11月"],
          axisLine: { lineStyle: { color: "#409eff" } },
          axisLabel: { color: "#fff", fontSize: 11 },
          axisTick: { show: false }
        },
        yAxis: {
          type: "value",
          axisLine: { show: false },
          axisLabel: { color: "#8cbde5", fontSize: 11 },
          splitLine: { lineStyle: { color: "rgba(255,255,255,0.05)", type: 'dashed' } }
        },
        series: [
          {
            type: "line",
            data: [35, 28, 42, 58, 45, 52],
            smooth: true,
            symbol: "circle",
            symbolSize: 8,
            lineStyle: { width: 3, color: "#00f2ff", shadowColor: '#00f2ff', shadowBlur: 10 },
            itemStyle: { color: "#050a1f", borderColor: "#00f2ff", borderWidth: 2 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "rgba(0,242,255,0.4)" },
                { offset: 1, color: "rgba(0,242,255,0)" }
              ])
            }
          }
        ],
        backgroundColor: "transparent"
      });
    },
    initPieRegion() {
      const chart = echarts.init(this.$refs.pieRegion);
      this.charts.pieRegion = chart;
      chart.setOption({
        series: [
          {
            type: "pie",
            radius: ["45%", "70%"],
            center: ["50%", "50%"],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 5,
              borderColor: "#050a1f",
              borderWidth: 3
            },
            label: { show: false },
            data: [
              { value: 35, name: "东区", itemStyle: { color: "#00f2ff" } },
              { value: 25, name: "西区", itemStyle: { color: "#409eff" } },
              { value: 20, name: "南区", itemStyle: { color: "#7c4dff" } },
              { value: 20, name: "北区", itemStyle: { color: "#ffcc00" } }
            ]
          }
        ],
        backgroundColor: "transparent"
      });
    },

    /* ---- 缺陷分布图表 ---- */
    initDefectDistribution() {
      const chart = echarts.init(this.$refs.defectDistribution);
      this.charts.defectDistribution = chart;
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: 'rgba(0,0,0,0.8)',
          borderColor: '#00f2ff',
          textStyle: { color: '#fff' }
        },
        legend: {
          data: ['区域A', '区域B', '区域C', '区域D', '区域E'],
          right: 0,
          top: 0,
          itemWidth: 10,
          itemHeight: 10,
          textStyle: { color: '#99ccff', fontSize: 10 }
        },
        grid: {
          left: '3%', right: '4%', bottom: '3%', top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['T1', 'T2', 'T3', 'T4'], // 缩写以节省空间
          axisLine: { lineStyle: { color: '#409eff' } },
          axisLabel: { color: '#fff', fontSize: 11 },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisLabel: { color: '#8cbde5', fontSize: 11 },
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.05)', type: 'dashed' } }
        },
        series: [
          { name: '区域A', type: 'bar', stack: 'total', data: [12, 18, 15, 8], itemStyle: { color: '#00f2ff' } },
          { name: '区域B', type: 'bar', stack: 'total', data: [8, 12, 10, 6], itemStyle: { color: '#409eff' } },
          { name: '区域C', type: 'bar', stack: 'total', data: [6, 8, 12, 10], itemStyle: { color: '#7c4dff' } },
          { name: '区域D', type: 'bar', stack: 'total', data: [10, 6, 8, 12], itemStyle: { color: '#00ff99' } },
          { name: '区域E', type: 'bar', stack: 'total', data: [15, 10, 6, 8], itemStyle: { color: '#ffcc00' } }
        ],
        backgroundColor: 'transparent'
      });
    }
  }
};
</script>

<style scoped>
/* ================= 引入字体 ================= */
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500&display=swap');

/* ================= 基础布局 ================= */
.big-screen {
  width: 100%;
  min-height: 100vh;
  background-color: #050a1f;
  color: #fff;
  font-family: 'Roboto', sans-serif;
  position: relative;
  overflow: hidden;
  padding-bottom: 20px;
}

/* 动态网格背景 */
.bg-grid {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: 
    linear-gradient(rgba(0, 242, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 242, 255, 0.05) 1px, transparent 1px);
  background-size: 30px 30px;
  z-index: 0;
  pointer-events: none;
}

/* ================= 头部区域 ================= */
.header-bar {
  position: relative;
  z-index: 2;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(90deg, transparent 0%, rgba(0, 242, 255, 0.1) 50%, transparent 100%);
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(0, 242, 255, 0.2);
}

.title {
  font-family: 'Orbitron', sans-serif;
  font-size: 32px;
  color: #00f2ff;
  margin: 0;
  letter-spacing: 4px;
  text-shadow: 0 0 15px rgba(0, 242, 255, 0.6);
}

.refresh-btn {
  position: absolute;
  right: 20px;
  top: 20px;
  background: rgba(0, 0, 0, 0.3) !important;
  border: 1px solid #00f2ff !important;
  color: #00f2ff !important;
}
.refresh-btn:hover {
  background: #00f2ff !important;
  color: #000 !important;
  box-shadow: 0 0 15px #00f2ff;
}

/* ================= 核心网格布局 ================= */
.layout {
  position: relative;
  z-index: 2;
  display: grid;
  /* 定义3列宽度比例 */
  grid-template-columns: 26% 48% 26%; 
  /* 定义3行高度 */
  grid-template-rows: 200px 250px 280px; 
  gap: 15px;
  padding: 0 20px;
  max-width: 1920px;
  margin: 0 auto;
}

/* 响应式调整 */
@media (max-width: 1600px) {
  .layout { grid-template-rows: 180px 220px 250px; }
}

/* 区域定位 */
.top-left { grid-area: 1 / 1 / 2 / 2; }
.top-center { grid-area: 1 / 2 / 2 / 3; }
.top-right { grid-area: 1 / 3 / 2 / 4; }
.mid-left { grid-area: 2 / 1 / 3 / 2; }
.mid-center { grid-area: 2 / 2 / 3 / 3; }
.mid-right { grid-area: 2 / 3 / 3 / 4; }
.bottom-left { grid-area: 3 / 1 / 4 / 2; }
.bottom-center { grid-area: 3 / 2 / 4 / 3; }
.bottom-right { grid-area: 3 / 3 / 4 / 4; }

/* ================= 通用面板样式 ================= */
.panel {
  background: rgba(13, 27, 45, 0.6);
  border: 1px solid rgba(0, 242, 255, 0.15);
  backdrop-filter: blur(10px);
  padding: 15px;
  position: relative;
  display: flex;
  flex-direction: column;
  box-shadow: inset 0 0 20px rgba(0, 100, 255, 0.05);
}

/* 面板四角装饰 */
.panel-decoration .angle {
  position: absolute;
  width: 10px; height: 10px;
  border: 2px solid #00f2ff;
  transition: all 0.3s;
  box-shadow: 0 0 5px #00f2ff;
}
.angle:nth-child(1) { top: -1px; left: -1px; border-right: 0; border-bottom: 0; }
.angle:nth-child(2) { top: -1px; right: -1px; border-left: 0; border-bottom: 0; }
.angle:nth-child(3) { bottom: -1px; left: -1px; border-right: 0; border-top: 0; }
.angle:nth-child(4) { bottom: -1px; right: -1px; border-left: 0; border-top: 0; }

/* 标题 */
.panel-title {
  font-size: 16px;
  color: #fff;
  margin: 0 0 10px 0;
  padding-left: 10px;
  border-left: 3px solid #00f2ff;
  font-weight: 500;
  letter-spacing: 1px;
  background: linear-gradient(90deg, rgba(0,242,255,0.15) 0%, transparent 100%);
  line-height: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 图表容器 */
.chart-container {
  flex: 1;
  width: 100%;
  height: 100%; /* 关键 */
  min-height: 0;
}

/* 顶部紧凑图表组 */
.compact-chart-group {
  display: flex;
  height: 100%;
  gap: 5px;
}
.compact-chart {
  flex: 1;
}

/* ================= 监控画面区 ================= */
.monitor-feed {
  flex: 1;
  background: #000;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(0, 242, 255, 0.3);
  cursor: pointer;
}
.image-wrapper { width: 100%; height: 100%; position: relative; }
.monitoring-image { width: 100%; height: 100%; object-fit: contain; }

/* 扫描线动画 */
.scan-line {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 3px;
  background: rgba(0, 242, 255, 0.8);
  box-shadow: 0 0 15px #00f2ff;
  animation: scan 2.5s linear infinite;
  z-index: 10;
  opacity: 0.6;
}
@keyframes scan {
  0% { top: 0; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

/* 覆盖层信息 */
.image-overlay {
  position: absolute;
  top: 10px; left: 10px;
  right: 10px;
  display: flex;
  justify-content: space-between;
}
.overlay-tag {
  background: #ff0055;
  color: #fff;
  padding: 2px 6px;
  font-size: 12px;
  font-weight: bold;
  border-radius: 2px;
}
.overlay-time {
  color: #00f2ff;
  font-family: 'Orbitron';
  font-size: 14px;
  text-shadow: 0 0 5px #00f2ff;
}

/* 无信号状态 */
.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #5c7c99;
}
.loader-ring {
  width: 40px; height: 40px;
  border: 3px solid rgba(0, 242, 255, 0.2);
  border-top-color: #00f2ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ================= 缺陷表格列表化 ================= */
.defect-table-box {
  display: flex;
  flex-direction: column;
  height: 100%;
  font-size: 13px;
}
.table-header {
  display: flex;
  padding: 8px 0;
  background: rgba(0, 242, 255, 0.1);
  color: #00f2ff;
  font-weight: bold;
}
.table-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  transition: all 0.2s;
}
.table-row:hover { background: rgba(0, 242, 255, 0.08); }

.col-seq { flex: 0 0 50px; text-align: center; color: #5c7c99; }
.col-name { flex: 1; padding-left: 10px; color: #fff; }
.col-rate { flex: 0 0 80px; padding-right: 10px; }

/* 进度条样式 */
.progress-bar-container { display: flex; align-items: center; gap: 5px; }
.progress-track {
  flex: 1; height: 4px; background: rgba(255,255,255,0.1); border-radius: 2px; overflow: hidden;
}
.progress-fill { height: 100%; border-radius: 2px; }
.bg-danger { background: #ff4d4f; box-shadow: 0 0 5px #ff4d4f; }
.bg-warning { background: #ffcc00; box-shadow: 0 0 5px #ffcc00; }
.bg-info { background: #00f2ff; box-shadow: 0 0 5px #00f2ff; }
.rate-text { font-size: 11px; width: 35px; text-align: right; font-family: 'Roboto'; color: #ccc; }

.table-footer {
  margin-top: auto;
  padding-top: 5px;
  border-top: 1px solid rgba(0, 242, 255, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #99ccff;
}
.glow-text { font-size: 18px; color: #00f2ff; text-shadow: 0 0 10px #00f2ff; font-family: 'Orbitron'; }

/* ================= 告警信息 ================= */
.alarm-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}
.alarm-list::-webkit-scrollbar { width: 4px; }
.alarm-list::-webkit-scrollbar-thumb { background: #00f2ff; border-radius: 2px; }
.alarm-list::-webkit-scrollbar-track { background: rgba(0,0,0,0.2); }

.alarm-item {
  display: flex;
  background: rgba(255,255,255,0.02);
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
  border-left: 2px solid transparent;
  transition: transform 0.2s;
}
.alarm-item:hover { transform: translateX(5px); background: rgba(255,255,255,0.05); }

.alarm-icon-wrapper {
  width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%;
  margin-right: 10px;
  font-size: 16px;
}
.alarm-icon-wrapper.high { color: #ff4d4f; background: rgba(255, 77, 79, 0.1); box-shadow: 0 0 10px rgba(255,77,79,0.2); }
.alarm-icon-wrapper.medium { color: #ffcc00; background: rgba(255, 204, 0, 0.1); }
.alarm-icon-wrapper.low { color: #00f2ff; background: rgba(0, 242, 255, 0.1); }

.alarm-content { flex: 1; }
.alarm-header { display: flex; justify-content: space-between; margin-bottom: 2px; }
.alarm-title { font-size: 13px; font-weight: bold; color: #fff; }
.alarm-time { font-size: 11px; color: #5c7c99; font-family: 'Roboto'; }
.alarm-desc { font-size: 12px; color: #99ccff; margin: 0; }

/* ================= 底部连接状态 ================= */
.connection-status {
  position: fixed; bottom: 15px; right: 20px;
  display: flex; align-items: center; gap: 8px;
  padding: 6px 16px;
  border-radius: 20px;
  background: rgba(5, 10, 31, 0.9);
  border: 1px solid #333;
  z-index: 100;
  font-size: 12px;
  font-family: 'Orbitron';
  letter-spacing: 1px;
}
.connection-status.connected { border-color: #00ff99; color: #00ff99; box-shadow: 0 0 10px rgba(0,255,153,0.2); }
.connection-status:not(.connected) { border-color: #ff4d4f; color: #ff4d4f; }

.status-dot { width: 8px; height: 8px; border-radius: 50%; background: currentColor; box-shadow: 0 0 8px currentColor; animation: pulse 2s infinite; }
@keyframes pulse { 0% { opacity: 1; } 50% { opacity: 0.4; } 100% { opacity: 1; } }
</style>