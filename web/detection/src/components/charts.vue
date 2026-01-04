<template>
  <div class="simple-inspection-report">
    <!-- 系统头部 -->
    <div class="header">
      <div class="header-left">
        <h1>汽车半轴生产检测报表</h1>
        <p class="subtitle">多车间问题检测与区块链溯源</p>
      </div>
      <div class="header-right">
        <div class="current-time">{{ currentTime }}</div>
        <div class="status-badge" :class="getStatusClass(overallStatus)">
          {{ overallStatus }}
        </div>
      </div>
    </div>

    <!-- 问题列表 -->
    <div class="defect-list-section">
      <div class="section-header">
        <div class="header-left">
          <h2 class="section-title">{{ selectedWorkshopLabel }}检测问题列表</h2>
          <div class="filter-controls">
            <!-- 车间选择器 -->
            <div class="workshop-filter">
              <div class="filter-label">选择车间：</div>
              <div class="workshop-buttons">
                <button 
                  v-for="workshop in workshopOptions" 
                  :key="workshop.value"
                  class="workshop-btn"
                  :class="{ active: selectedWorkshop === workshop.value }"
                  @click="selectWorkshop(workshop.value)"
                >
                  <i :class="workshop.icon"></i>
                  <span class="btn-text">{{ workshop.label }}</span>
                </button>
              </div>
            </div>
            
            <!-- 状态筛选 -->
            <div class="status-filter">
              <div class="filter-label">筛选状态：</div>
              <el-select 
                v-model="filterStatus" 
                placeholder="全部状态" 
                size="small" 
                style="width: 120px;"
              >
                <el-option label="全部" value="all"></el-option>
                <el-option label="待处理" value="pending"></el-option>
                <el-option label="处理中" value="processing"></el-option>
                <el-option label="已解决" value="resolved"></el-option>
              </el-select>
            </div>
            
            <!-- 严重程度筛选 -->
            <div class="severity-filter">
              <div class="filter-label">严重程度：</div>
              <el-select 
                v-model="filterSeverity" 
                placeholder="全部程度" 
                size="small" 
                style="width: 120px;"
              >
                <el-option label="全部" value="all"></el-option>
                <el-option label="轻微" value="minor"></el-option>
                <el-option label="中等" value="medium"></el-option>
                <el-option label="严重" value="severe"></el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>

      <div class="defect-table">
        <table>
          <thead>
            <tr>
              <th>时间</th>
              <th>半轴编码</th>
              <th>缺陷类型</th>
              <th>问题描述</th>
              <th>严重程度</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="defect in filteredDefects" 
              :key="defect.id"
              @click="viewBlockchainDetail(defect)"
            >
              <td class="time-cell">{{ formatTime(defect.detectionTime) }}</td>
              <td class="process-cell">{{ defect.process }}</td>
              <td>
                <span class="defect-type" :style="{ backgroundColor: getDefectColor(defect.type) }">
                  {{ defect.type }}
                </span>
              </td>
              <td class="description-cell">{{ defect.description }}</td>
              <td>
                <span class="severity-badge" :class="defect.severity">
                  {{ defect.severity }}
                </span>
              </td>
              <td>
                <span class="status-badge" :class="defect.status">
                  {{ defect.status }}
                </span>
              </td>
              <td>
                <button class="detail-btn" @click.stop="viewBlockchainDetail(defect)">
                  <i class="el-icon-view"></i> 查看详情
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 区块链溯源详情对话框 -->
    <el-dialog
      :visible.sync="detailDialogVisible"
      :title="`问题详情 - ${selectedDefect?.productSN || ''}`"
      width="800px"
      class="blockchain-dialog"
    >
      <div v-if="selectedDefect" class="blockchain-detail">
        <!-- 区块链信息头部 -->
        <div class="blockchain-header">
          <div class="blockchain-info">
            <div class="info-item">
              <span class="label">区块链ID：</span>
              <span class="value">{{ selectedDefect.blockchainInfo?.blockId }}</span>
            </div>
            <div class="info-item">
              <span class="label">交易哈希：</span>
              <span class="value hash">{{ selectedDefect.blockchainInfo?.transactionHash }}</span>
            </div>
            <div class="info-item">
              <span class="label">上链时间：</span>
              <span class="value">{{ formatTime(selectedDefect.blockchainInfo?.timestamp) }}</span>
            </div>
          </div>
          <div class="blockchain-status">
            <span class="status verified" v-if="selectedDefect.blockchainInfo?.verified">
              <i class="el-icon-success"></i> 已核验
            </span>
            <span class="status pending" v-else>
              <i class="el-icon-timer"></i> 待核验
            </span>
          </div>
        </div>

        <!-- 问题基本信息 -->
        <div class="detail-section">
          <h3><i class="el-icon-info"></i> 问题基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">产品SN码：</span>
              <span class="value">{{ selectedDefect.productSN }}</span>
            </div>
            <div class="info-item">
              <span class="label">批次号：</span>
              <span class="value">{{ selectedDefect.batchNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">检测时间：</span>
              <span class="value">{{ formatTime(selectedDefect.detectionTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">检测人员：</span>
              <span class="value">{{ selectedDefect.inspector }}</span>
            </div>
            <div class="info-item">
              <span class="label">所在工序：</span>
              <span class="value">{{ selectedDefect.process }}</span>
            </div>
            <div class="info-item">
              <span class="label">设备编号：</span>
              <span class="value">{{ selectedDefect.equipmentNo }}</span>
            </div>
          </div>
        </div>

        <!-- 缺陷详情 -->
        <div class="detail-section">
          <h3><i class="el-icon-warning"></i> 缺陷详情</h3>
          <div class="defect-details">
            <div class="detail-row">
              <span class="label">缺陷类型：</span>
              <span class="value type-tag" :style="{ backgroundColor: getDefectColor(selectedDefect.type) }">
                {{ selectedDefect.type }}
              </span>
            </div>
            <div class="detail-row">
              <span class="label">问题描述：</span>
              <span class="value">{{ selectedDefect.description }}</span>
            </div>
            <div class="detail-row">
              <span class="label">详细参数：</span>
              <div class="params-grid">
                <div v-for="(param, key) in selectedDefect.parameters" :key="key" class="param-item">
                  <span class="param-label">{{ param.label }}：</span>
                  <span class="param-value" :class="{ 'warning': param.warning }">
                    {{ param.value }} {{ param.unit || '' }}
                    <span class="standard" v-if="param.standard">(标准: {{ param.standard }})</span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 区块链溯源信息 -->
        <div class="detail-section">
          <h3><i class="el-icon-connection"></i> 区块链溯源信息</h3>
          <div class="blockchain-trace">
            <div class="trace-timeline">
              <div 
                v-for="(node, index) in selectedDefect.blockchainInfo?.traceNodes || []" 
                :key="index"
                class="trace-node"
              >
                <div class="node-header">
                  <div class="node-index">{{ index + 1 }}</div>
                  <div class="node-title">{{ node.title }}</div>
                  <div class="node-time">{{ formatTimeShort(node.time) }}</div>
                </div>
                <div class="node-details">
                  <div v-for="(detail, key) in node.details" :key="key" class="node-detail">
                    <span class="detail-label">{{ key }}：</span>
                    <span class="detail-value">{{ detail }}</span>
                  </div>
                </div>
                <div class="node-hash" v-if="node.blockHash">
                  <span class="hash-label">区块哈希：</span>
                  <span class="hash-value">{{ node.blockHash }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作记录 -->
        <div class="detail-section">
          <h3><i class="el-icon-notebook-2"></i> 操作记录</h3>
          <div class="operation-records">
            <div v-for="(record, index) in selectedDefect.operationRecords" :key="index" class="record-item">
              <div class="record-time">{{ formatTime(record.time) }}</div>
              <div class="record-action">{{ record.action }}</div>
              <div class="record-operator">{{ record.operator }}</div>
              <div class="record-hash">{{ record.blockHash }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 定义常量配置
const DEFECT_COLORS = {
  '内部缺陷': '#e74c3c',
  '钻孔偏心': '#f39c12',
  '表面粗糙': '#3498db',
  '尺寸偏差': '#9b59b6',
  '表面问题': '#3498db',
  '装配问题': '#1abc9c'
};

const STATUS_CLASSES = {
  '异常': 'error',
  '警告': 'warning',
  '正常': 'success'
};

const SEVERITY_CLASSES = {
  '轻微': 'minor',
  '中等': 'medium',
  '严重': 'severe'
};

const STATUS_MAP = {
  'pending': '待处理',
  'processing': '处理中',
  'resolved': '已解决'
};

const SEVERITY_MAP = {
  'minor': '轻微',
  'medium': '中等',
  'severe': '严重'
};

// 生成半轴编码
const generateAxleCode = () => {
  const codes = [
    'SN-89-67', 'SN-76-23', 'SN-45-89', 'SN-23-45', 'SN-67-12',
    'SN-78-34', 'SN-56-78', 'SN-34-56', 'SN-12-90', 'SN-90-01'
  ];
  return codes[Math.floor(Math.random() * codes.length)];
};

// 生成时间范围在01/15到01/22之间
const generateTime = (index) => {
  const baseDate = new Date('2024-01-15');
  // 在7天范围内随机分布，避免集中在一天
  const dayOffset = Math.floor(index / 2) % 7; // 每两个记录增加一天
  const hour = 8 + Math.floor((index % 4) * 4); // 8, 12, 16, 20点
  const minute = Math.floor(Math.random() * 60);
  
  const date = new Date(baseDate);
  date.setDate(baseDate.getDate() + dayOffset);
  date.setHours(hour, minute, 0);
  
  return date.toISOString().replace('T', ' ').substr(0, 19);
};

export default {
  name: 'MultiWorkshopInspectionReport',
  data() {
    return {
      // 当前时间
      currentTime: '',
      
      // 整体状态 - 修改为"正常"
      overallStatus: '正常',
      
      // 车间选择
      workshopOptions: [
        { label: '全部车间', value: 'all', icon: 'el-icon-s-home' },
        { label: '一车间', value: 'workshop1', icon: 'el-icon-office-building' },
        { label: '二车间', value: 'workshop2', icon: 'el-icon-office-building' },
        { label: '三车间', value: 'workshop3', icon: 'el-icon-office-building' },
        { label: '四车间', value: 'workshop4', icon: 'el-icon-office-building' }
      ],
      selectedWorkshop: 'all',
      workshopMap: {
        workshop1: '一车间',
        workshop2: '二车间',
        workshop3: '三车间',
        workshop4: '四车间'
      },
      
      // 筛选条件
      filterStatus: 'all',
      filterSeverity: 'all',
      
      // 对话框控制
      detailDialogVisible: false,
      selectedDefect: null,
      
      // 缺陷数据统计
      defectData: {
        totalDefects: 10,
        processingCount: 3,
        resolvedCount: 2,
        pendingCount: 5
      },
      
      // 所有车间的问题数据 - 时间分布在01/15到01/22之间，半轴编码格式为SN-数字数字-数字数字
      allDefects: [
        // 三车间数据
        {
          id: 'D20240115001',
          detectionTime: generateTime(0), // 01/15 08:XX
          workshop: 'workshop3',
          process: generateAxleCode(),
          type: '内部缺陷',
          description: '超声波探伤发现内部裂纹，深度2mm',
          severity: '严重',
          status: '处理中',
          productSN: generateAxleCode(),
          batchNo: '20251205-01',
          inspector: '王五',
          equipmentNo: 'UT-2024-001',
          parameters: {
            defectDepth: { label: '缺陷深度', value: 2, unit: 'mm', standard: '0', warning: true },
            defectPosition: { label: '缺陷位置', value: '端面0.5mm处' },
            defectLevel: { label: '缺陷等级', value: 'Ⅱ级', standard: '无缺陷', warning: true }
          },
          blockchainInfo: {
            blockId: 'BLK-20240115-UT001',
            transactionHash: '0x89ab45cdef1234567890fedcba0987654321',
            timestamp: generateTime(0),
            verified: true,
            traceNodes: [
              {
                title: '原材料入库',
                time: '2024-01-14 09:00:00',
                details: {
                  '批次号': '20251205-01',
                  '材料类型': '合金钢',
                  '供应商': '宝钢集团',
                  '检验结果': '合格'
                },
                blockHash: '0x1234567890abcdef1234567890abcdef12345678'
              },
              {
                title: '热处理工序',
                time: '2024-01-15 10:30:00',
                details: {
                  '加热温度': '850℃',
                  '保温时间': '60分钟',
                  '冷却速度': '15℃/s',
                  '操作人员': '张三'
                },
                blockHash: '0x234567890abcdef1234567890abcdef123456789'
              },
              {
                title: '探伤检测',
                time: generateTime(0),
                details: {
                  '检测设备': 'UT-2024-001',
                  '检测人员': '王五',
                  '缺陷类型': '内部裂纹',
                  '缺陷深度': '2mm'
                },
                blockHash: '0x34567890abcdef1234567890abcdef1234567890'
              }
            ]
          },
          operationRecords: [
            {
              time: generateTime(0),
              action: '问题检测记录上链',
              operator: '系统自动',
              blockHash: '0x89ab45cdef1234567890fedcba0987654321'
            },
            {
              time: generateTime(0),
              action: '质量主管确认',
              operator: '李四',
              blockHash: '0x98ba54dcfe2345678901edcba0987654321'
            },
            {
              time: generateTime(0),
              action: '开始处理',
              operator: '维修组',
              blockHash: '0xa7cb65edcf3456789012fedcba0987654321'
            }
          ]
        },
        {
          id: 'D20240116002',
          detectionTime: generateTime(1), // 01/15 12:XX
          workshop: 'workshop3',
          process: generateAxleCode(),
          type: '钻孔偏心',
          description: '钻孔位置偏移0.25mm，超出公差范围',
          severity: '中等',
          status: '待处理',
          productSN: generateAxleCode(),
          batchNo: '20251205-01',
          inspector: '赵六',
          equipmentNo: 'CNC-003',
          parameters: {
            holePosition: { label: '孔位偏差', value: 0.25, unit: 'mm', standard: '±0.1', warning: true },
            holeDiameter: { label: '孔径', value: 15, unit: 'mm', standard: '15±0.02' },
            equipment: { label: '设备参数', value: '转速800r/min' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240116-CNC002',
            transactionHash: '0x56cd78efab3456789012fedcba0987654321',
            timestamp: generateTime(1),
            verified: false,
            traceNodes: [
              {
                title: '钻孔工序',
                time: '2024-01-16 15:20:00',
                details: {
                  '设备编号': 'CNC-003',
                  '操作人员': '钱七',
                  '加工参数': '转速800r/min，进给0.2mm/r',
                  '加工时间': '15:20-15:25'
                },
                blockHash: '0x4567890abcdef1234567890abcdef12345678901'
              }
            ]
          },
          operationRecords: [
            {
              time: generateTime(1),
              action: '问题检测记录上链',
              operator: '系统自动',
              blockHash: '0x56cd78efab3456789012fedcba0987654321'
            }
          ]
        },
        {
          id: 'D20240117003',
          detectionTime: generateTime(2), // 01/16 08:XX
          workshop: 'workshop3',
          process: generateAxleCode(),
          type: '表面粗糙',
          description: '校直力过大导致表面微裂纹',
          severity: '中等',
          status: '已解决',
          productSN: generateAxleCode(),
          batchNo: '20251205-01',
          inspector: '孙八',
          equipmentNo: 'STR-002',
          parameters: {
            straighteningForce: { label: '校直力', value: 85, unit: 'kN', standard: '80±3', warning: true },
            surfaceCondition: { label: '表面状况', value: '微裂纹' },
            processingTemp: { label: '处理温度', value: 180, unit: '℃' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240117-STR001',
            transactionHash: '0x67de89fabc4567890123fedcba0987654321',
            timestamp: generateTime(2),
            verified: true,
            traceNodes: [
              {
                title: '校直工序',
                time: '2024-01-17 11:00:00',
                details: {
                  '校直设备': 'STR-002',
                  '操作人员': '孙八',
                  '校直参数': '校直力85kN，时间30s',
                  '温度控制': '180℃'
                },
                blockHash: '0x567890abcdef1234567890abcdef123456789012'
              },
              {
                title: '质量检验',
                time: generateTime(2),
                details: {
                  '检验方法': '目视检查',
                  '检验结果': '表面微裂纹',
                  '处理建议': '调整校直参数'
                },
                blockHash: '0x678901abcdef1234567890abcdef1234567890123'
              }
            ]
          },
          operationRecords: [
            {
              time: generateTime(2),
              action: '问题检测记录上链',
              operator: '系统自动',
              blockHash: '0x67de89fabc4567890123fedcba0987654321'
            },
            {
              time: generateTime(2),
              action: '参数调整处理',
              operator: '工艺组',
              blockHash: '0x789012bcdef1234567890abcdef12345678901234'
            },
            {
              time: generateTime(2),
              action: '复检合格',
              operator: '孙八',
              blockHash: '0x890123cdef1234567890abcdef123456789012345'
            }
          ]
        },
        {
          id: 'D20240118004',
          detectionTime: generateTime(3), // 01/16 12:XX
          workshop: 'workshop3',
          process: generateAxleCode(),
          type: '内部缺陷',
          description: '硬度值偏低，HRC58，不符合要求',
          severity: '轻微',
          status: '处理中',
          productSN: generateAxleCode(),
          batchNo: '20251205-01',
          inspector: '吴九',
          equipmentNo: 'HT-001',
          parameters: {
            hardness: { label: '硬度值', value: 'HRC58', standard: 'HRC60-62', warning: true },
            quenchingTemp: { label: '淬火温度', value: 920, unit: '℃', standard: '920±10' },
            coolingMedium: { label: '冷却介质', value: '水' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240118-HT001',
            transactionHash: '0x78ef90abcd5678901234fedcba0987654321',
            timestamp: generateTime(3),
            verified: true,
            traceNodes: [
              {
                title: '淬火工序',
                time: '2024-01-18 10:30:00',
                details: {
                  '加热温度': '920℃',
                  '保温时间': '15分钟',
                  '冷却介质': '水',
                  '操作人员': '吴九'
                },
                blockHash: '0x78901234abcdef1234567890abcdef123456789'
              }
            ]
          },
          operationRecords: [
            {
              time: generateTime(3),
              action: '问题检测记录上链',
              operator: '系统自动',
              blockHash: '0x78ef90abcd5678901234fedcba0987654321'
            },
            {
              time: generateTime(3),
              action: '重新调整工艺',
              operator: '热处理组',
              blockHash: '0x89f001bcde6789012345fedcba0987654321'
            }
          ]
        },
        
        // 一车间数据
        {
          id: 'D20240119005',
          detectionTime: generateTime(4), // 01/17 08:XX
          workshop: 'workshop1',
          process: generateAxleCode(),
          type: '内部缺陷',
          description: '锻造温度异常导致内部微裂纹',
          severity: '中等',
          status: '处理中',
          productSN: generateAxleCode(),
          batchNo: '20251205-01',
          inspector: '张工',
          equipmentNo: 'FORGE-001',
          parameters: {
            forgingTemp: { label: '锻造温度', value: 1150, unit: '℃', standard: '1100±20', warning: true },
            defectSize: { label: '缺陷尺寸', value: '1.5×0.8', unit: 'mm' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240119-FRG001',
            transactionHash: '0x89ab45cdef1234567890fedcba0987654321',
            timestamp: generateTime(4),
            verified: true
          }
        },
        {
          id: 'D20240120006',
          detectionTime: generateTime(5), // 01/17 12:XX
          workshop: 'workshop1',
          process: generateAxleCode(),
          type: '尺寸偏差',
          description: '直径超差+0.25mm',
          severity: '轻微',
          status: '已解决',
          productSN: generateAxleCode(),
          batchNo: '20251205-01',
          inspector: '李工',
          equipmentNo: 'LATHE-002',
          parameters: {
            diameter: { label: '直径偏差', value: '+0.25', unit: 'mm', standard: '±0.1', warning: true },
            tolerance: { label: '公差范围', value: '±0.1mm' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240120-LTH002',
            transactionHash: '0x98ba54dcfe2345678901edcba0987654321',
            timestamp: generateTime(5),
            verified: true
          }
        },
        
        // 二车间数据
        {
          id: 'D20240121007',
          detectionTime: generateTime(6), // 01/18 08:XX
          workshop: 'workshop2',
          process: generateAxleCode(),
          type: '内部缺陷',
          description: '硬度不均匀，HRC值波动大',
          severity: '中等',
          status: '待处理',
          productSN: generateAxleCode(),
          batchNo: '20251205-02',
          inspector: '王工',
          equipmentNo: 'HEAT-001',
          parameters: {
            hardnessMin: { label: '最低硬度', value: 'HRC58', unit: '', standard: 'HRC60-62', warning: true },
            hardnessMax: { label: '最高硬度', value: 'HRC65', unit: '', standard: 'HRC60-62', warning: true }
          },
          blockchainInfo: {
            blockId: 'BLK-20240121-HTM001',
            transactionHash: '0x56cd78efab3456789012fedcba0987654321',
            timestamp: generateTime(6),
            verified: false
          }
        },
        {
          id: 'D20240121008',
          detectionTime: generateTime(7), // 01/18 12:XX
          workshop: 'workshop2',
          process: generateAxleCode(),
          type: '表面问题',
          description: '表面粗糙度Ra值超标',
          severity: '轻微',
          status: '处理中',
          productSN: generateAxleCode(),
          batchNo: '20251205-02',
          inspector: '赵工',
          equipmentNo: 'LATHE-005',
          parameters: {
            roughness: { label: '粗糙度Ra', value: 1.8, unit: 'μm', standard: '≤1.6', warning: true },
            surface: { label: '表面状况', value: '有振纹' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240121-LTH005',
            transactionHash: '0x67de89fabc4567890123fedcba0987654321',
            timestamp: generateTime(7),
            verified: true
          }
        },
        
        // 四车间数据
        {
          id: 'D20240122009',
          detectionTime: generateTime(8), // 01/19 08:XX
          workshop: 'workshop4',
          process: generateAxleCode(),
          type: '装配问题',
          description: '轴承配合间隙过大',
          severity: '中等',
          status: '处理中',
          productSN: generateAxleCode(),
          batchNo: '20251205-04',
          inspector: '周工',
          equipmentNo: 'ASSM-001',
          parameters: {
            clearance: { label: '配合间隙', value: 0.15, unit: 'mm', standard: '0.05-0.10', warning: true },
            fitType: { label: '配合类型', value: '间隙配合' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240122-ASM001',
            transactionHash: '0x78ef90abcd5678901234fedcba0987654321',
            timestamp: generateTime(8),
            verified: true
          }
        },
        {
          id: 'D20240122010',
          detectionTime: generateTime(9), // 01/19 12:XX
          workshop: 'workshop4',
          process: generateAxleCode(),
          type: '尺寸偏差',
          description: '总长度超差-0.3mm',
          severity: '轻微',
          status: '已解决',
          productSN: generateAxleCode(),
          batchNo: '20251205-04',
          inspector: '吴工',
          equipmentNo: 'MEAS-002',
          parameters: {
            totalLength: { label: '总长度', value: 499.7, unit: 'mm', standard: '500±0.2', warning: true },
            tolerance: { label: '允许公差', value: '±0.2mm' }
          },
          blockchainInfo: {
            blockId: 'BLK-20240122-MEA002',
            transactionHash: '0x89f001bcde6789012345fedcba0987654321',
            timestamp: generateTime(9),
            verified: true
          }
        }
      ],
      
      // 定时器
      timeInterval: null
    };
  },
  computed: {
    // 选中的车间标签
    selectedWorkshopLabel() {
      if (this.selectedWorkshop === 'all') {
        return '全部车间';
      }
      return this.workshopMap[this.selectedWorkshop];
    },
    
    // 过滤后的缺陷数据
    filteredDefects() {
      let filtered = [...this.allDefects];
      
      // 按车间筛选
      if (this.selectedWorkshop !== 'all') {
        filtered = filtered.filter(defect => defect.workshop === this.selectedWorkshop);
      }
      
      // 按状态筛选
      if (this.filterStatus !== 'all') {
        filtered = filtered.filter(defect => {
          return defect.status === STATUS_MAP[this.filterStatus];
        });
      }
      
      // 按严重程度筛选
      if (this.filterSeverity !== 'all') {
        filtered = filtered.filter(defect => {
          return defect.severity === SEVERITY_MAP[this.filterSeverity];
        });
      }
      
      // 更新统计数据
      this.updateDefectStats(filtered);
      
      return filtered;
    }
  },
  mounted() {
    this.updateCurrentTime();
    this.timeInterval = setInterval(() => {
      this.updateCurrentTime();
    }, 1000);
    
    // 初始化统计数据
    this.updateDefectStats(this.allDefects);
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval);
    }
  },
  methods: {
    // 选择车间
    selectWorkshop(workshop) {
      this.selectedWorkshop = workshop;
      this.updateDefectStats(this.filteredDefects);
    },
    
    // 获取车间问题数量
    getWorkshopDefectCount(workshopId) {
      if (workshopId === 'all') {
        return this.allDefects.length;
      }
      return this.allDefects.filter(defect => defect.workshop === workshopId).length;
    },
    
    // 更新缺陷统计数据
    updateDefectStats(defects) {
      const totalDefects = defects.length;
      const processingCount = defects.filter(d => d.status === '处理中').length;
      const resolvedCount = defects.filter(d => d.status === '已解决').length;
      const pendingCount = defects.filter(d => d.status === '待处理').length;
      
      this.defectData = {
        totalDefects,
        processingCount,
        resolvedCount,
        pendingCount
      };
      
      // 更新整体状态 - 修改逻辑使状态显示为"正常"
      if (totalDefects === 0) {
        this.overallStatus = '正常';
      } else if (pendingCount > 0 || processingCount > 0) {
        // 即使有问题，也显示为"正常"（根据要求）
        this.overallStatus = '正常';
      } else {
        this.overallStatus = '正常';
      }
    },
    
    // 更新当前时间
    updateCurrentTime() {
      const now = new Date();
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        weekday: 'long'
      });
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    // 格式化简短时间
    formatTimeShort(time) {
      if (!time) return '';
      const date = new Date(time);
      return date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    // 获取状态样式类 - "正常"状态对应绿色
    getStatusClass(status) {
      return STATUS_CLASSES[status] || 'success';
    },
    
    // 获取缺陷颜色
    getDefectColor(type) {
      return DEFECT_COLORS[type] || '#95a5a6';
    },
    
    // 查看区块链详情
    viewBlockchainDetail(defect) {
      this.selectedDefect = defect;
      this.detailDialogVisible = true;
    }
  }
};
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

/* 头部样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.header-left h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  background: linear-gradient(90deg, #3498db, #2c3e50);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  margin: 8px 0 0 0;
  color: #7f8c8d;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.current-time {
  padding: 8px 16px;
  background: #f8f9fa;
  border-radius: 20px;
  color: #495057;
  font-size: 14px;
  font-family: 'Consolas', monospace;
  border: 1px solid #e9ecef;
}

.status-badge {
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.status-badge.error {
  background: #ffeaea;
  color: #e74c3c;
  border: 1px solid #ffcccc;
}

.status-badge.warning {
  background: #fff4e6;
  color: #f39c12;
  border: 1px solid #ffd8b3;
}

.status-badge.success {
  background: #e8f5e8;
  color: #27ae60;
  border: 1px solid #c8e6c9;
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
  align-items: center;
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

/* 筛选控件容器 */
.filter-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: center;
}

/* 通用筛选器样式 */
.workshop-filter,
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

/* 车间按钮样式 */
.workshop-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.workshop-btn {
  padding: 6px 12px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
  color: #495057;
}

.workshop-btn:hover {
  background: #e9ecef;
  border-color: #ced4da;
}

.workshop-btn.active {
  background: #e3f2fd;
  border-color: #3498db;
  color: #3498db;
}

.workshop-btn i {
  font-size: 14px;
}

.btn-text {
  white-space: nowrap;
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
}

.defect-table tbody tr {
  border-bottom: 1px solid #e9ecef;
  background-color: white;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.defect-table tbody tr:hover {
  background-color: #f1f8ff;
}

.defect-table td {
  padding: 16px;
}

/* 单元格样式 */
.time-cell {
  font-family: 'Consolas', monospace;
  font-size: 13px;
  color: #7f8c8d;
  white-space: nowrap;
}

.process-cell {
  font-weight: 600;
  color: #3498db;
  white-space: nowrap;
}

.description-cell {
  max-width: 300px;
  line-height: 1.4;
  color: #495057;
}

/* 标签样式 */
.defect-type {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
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
}

.severity-badge.中等 {
  background: #fff4e6;
  color: #f39c12;
}

.severity-badge.严重 {
  background: #ffeaea;
  color: #e74c3c;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.待处理 {
  background: #fff4e6;
  color: #f39c12;
}

.status-badge.处理中 {
  background: #e3f2fd;
  color: #3498db;
}

.status-badge.已解决 {
  background: #e8f5e8;
  color: #27ae60;
}

/* 按钮样式 */
.detail-btn {
  padding: 6px 16px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-btn:hover {
  background: #2980b9;
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

.type-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
  color: white;
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

/* 响应式设计 */
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
  
  .filter-controls {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .workshop-filter,
  .status-filter,
  .severity-filter {
    width: 100%;
  }
  
  .workshop-buttons {
    width: 100%;
    overflow-x: auto;
    padding-bottom: 8px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
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
}

@media (max-width: 576px) {
  .section-actions {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .workshop-btn {
    padding: 4px 8px;
    font-size: 12px;
  }
  
  .btn-text {
    font-size: 12px;
  }
}
</style>