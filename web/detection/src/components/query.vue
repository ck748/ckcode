<template>
  <div class="trace-system-container">
    <!-- 头部：查询功能和切换标签 -->
    <div class="header-section">
      <div class="header-title">
        <h2><i class="el-icon-search"></i> 汽车半轴溯源查询</h2>
        <p class="subtitle">追踪汽车半轴全生命周期信息，从原料到成品的全流程溯源</p>
      </div>
      
      <!-- 查询区域 -->
      <div class="query-section">
        <div class="query-input-group">
          <el-input
            v-model="queryForm.shaftCode"
            placeholder="请输入半轴编码 (如：SN-89-63)"
            clearable
            size="medium"
            class="shaft-input"
            @keyup.enter="handleQuery"
          >
            <template #prepend>
              <span class="input-label">半轴编码</span>
            </template>
          </el-input>
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleQuery"
            :loading="queryLoading"
            size="medium"
            class="query-btn"
          >
            查询溯源
          </el-button>
          <el-button
            type="info"
            icon="el-icon-refresh"
            @click="refreshPage"
            size="medium"
            plain
          >
            重置
          </el-button>
        </div>
        
        <div class="query-tips">
          <p><i class="el-icon-info"></i> 支持编码：SN-89-63, SN-89-98, SN-64-65, SN-65-66</p>
          <p><i class="el-icon-s-data"></i> 查询范围：可查询已上链的半轴溯源信息，包含原料、生产、检测全流程</p>
        </div>
      </div>
    </div>

    <!-- 未查询时的空状态 -->
    <div v-if="!showResult && queryHistory.length === 0" class="main-content">
      <!-- 条件筛选区域（仅查询前展示） -->
      <div class="condition-section">
        <div class="condition-header">
          <h3><i class="el-icon-s-operation"></i> 筛选条件（查询前可选）</h3>
          <div class="condition-actions">
            <el-button size="small" @click="clearConditions">清空条件</el-button>
            <el-button type="primary" size="small" @click="applyConditions">应用筛选</el-button>
          </div>
        </div>
        
        <div class="condition-grid">
          <div class="condition-item" v-for="(item, idx) in commonConditions" :key="idx">
            <label class="condition-label">{{ item.label }}</label>
            <el-select 
              v-if="item.type === 'select'"
              v-model="item.value"
              :placeholder="item.placeholder"
              size="small"
              clearable
              class="condition-input"
            >
              <el-option
                v-for="option in item.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
            <el-date-picker
              v-else-if="item.type === 'date'"
              v-model="item.value"
              type="date"
              placeholder="选择日期"
              size="small"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="condition-input"
            />
            <el-input
              v-else
              v-model="item.value"
              :placeholder="item.placeholder"
              size="small"
              class="condition-input"
            />
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state">
        <div class="empty-content">
          <i class="el-icon-search empty-icon"></i>
          <h3>请输入半轴编码进行查询</h3>
          <p>请输入完整的半轴编码，系统将查询该半轴从原料到成品的全流程溯源信息</p>
          <div class="empty-examples">
            <p>查询示例：</p>
            <div class="example-codes">
              <el-tag
                v-for="(example, index) in exampleCodes"
                :key="index"
                class="example-tag"
                @click="fillExample(example)"
              >
                {{ example }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 查询历史记录 -->
    <div v-if="queryHistory.length > 0" class="history-section">
      <div class="history-header">
        <h3><i class="el-icon-time"></i> 查询历史记录</h3>
        <el-button 
          size="small" 
          @click="clearHistory" 
          type="text"
          icon="el-icon-delete"
        >
          清空记录
        </el-button>
      </div>
      
      <div class="history-table">
        <el-table
          :data="queryHistory"
          style="width: 100%"
          stripe
          size="small"
          :row-class-name="tableRowClassName"
        >
          <el-table-column prop="time" label="查询时间" width="160">
            <template #default="{ row }">
              <div class="time-cell">{{ row.time }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="shaftId" label="半轴ID" width="120">
            <template #default="{ row }">
              <el-tag size="small" class="id-tag">{{ row.shaftId }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="aiQuality" label="AI质控" width="100">
            <template #default="{ row }">
              <el-tag 
                :type="getAITagType(row.aiQuality)" 
                size="small"
                class="ai-tag"
              >
                {{ row.aiQuality }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="问题描述" min-width="200">
            <template #default="{ row }">
              <div class="description-cell">{{ row.description }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="查询状态" width="100">
            <template #default="{ row }">
              <el-tag 
                :type="row.status === '完成' ? 'success' : 'warning'" 
                size="small"
                class="status-tag"
              >
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="mini"
                @click="viewHistoryDetail(row)"
                plain
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 查询结果详情展示 -->
    <div v-if="showResult" class="result-content">
      <!-- 查询结果头部 -->
      <div class="result-card">
        <div class="result-header">
          <div class="result-title">
            <i class="el-icon-document-checked result-icon"></i>
            <span class="title-text">溯源查询结果</span>
            <el-tag :type="resultTagType" size="small" class="result-tag">
              {{ resultTagText }}
            </el-tag>
          </div>
          <div class="result-actions">
            <el-button
              type="primary"
              icon="el-icon-cpu"
              size="small"
              @click="handleAIAnalysis"
              :loading="aiAnalysisLoading"
              style="margin-right: 10px;"
            >
              <span>🤖 AI分析</span>
            </el-button>
            <el-button
              type="success"
              icon="el-icon-download"
              size="small"
              @click="exportReport"
              :loading="exportLoading"
            >
              导出报告
            </el-button>
            <el-button
              type="warning"
              icon="el-icon-printer"
              size="small"
              @click="printReport"
            >
              打印
            </el-button>
            <el-button
              type="info"
              icon="el-icon-share"
              size="small"
              @click="shareReport"
            >
              分享
            </el-button>
            <el-button
              type="text"
              icon="el-icon-close"
              size="small"
              @click="closeResult"
            >
              关闭
            </el-button>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="basic-info">
          <div class="info-header">
            <h3 class="info-title">
              <i class="el-icon-info"></i>
              半轴基本信息
            </h3>
            <div class="blockchain-info">
              <span class="blockchain-status">
                <i class="el-icon-connection"></i>
                区块链状态：{{ traceData.isOnChain ? '已上链' : '未上链' }}
              </span>
              <span class="query-time">
                <i class="el-icon-time"></i>
                查询时间：{{ queryTime }}
              </span>
            </div>
          </div>

          <el-row :gutter="20" class="info-grid">
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">半轴编码</div>
                <div class="info-value code-value">{{ traceData.shaftCode }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">产品型号</div>
                <div class="info-value">{{ traceData.productModel }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">生产批次</div>
                <div class="info-value">{{ traceData.batchNumber }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">生产日期</div>
                <div class="info-value">{{ traceData.productionDate }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">出厂日期</div>
                <div class="info-value">{{ traceData.factoryDate }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">质检状态</div>
                <div class="info-value">
                  <el-tag :type="traceData.qualityStatus === '合格' ? 'success' : 'danger'" size="small">
                    {{ traceData.qualityStatus }}
                  </el-tag>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 溯源详情 -->
        <div class="trace-details">
          <h3 class="details-title">
            <i class="el-icon-s-data"></i>
            全流程溯源信息
          </h3>
          
          <div class="details-content">
            <!-- 原料产地 -->
            <div class="trace-section">
              <div class="section-header">
                <div class="section-title">
                  <i class="el-icon-location-outline section-icon"></i>
                  <span class="title-text">原料产地</span>
                </div>
                <div class="section-status">
                  <el-tag type="success" size="mini" v-if="traceData.rawMaterial?.status === '合格'">
                    验证通过
                  </el-tag>
                </div>
              </div>
              <el-row :gutter="20" class="section-content">
                <el-col :span="24">
                  <div class="detail-item">
                    <div class="detail-label">供应商</div>
                    <div class="detail-value">{{ traceData.rawMaterial?.supplier || '--' }}</div>
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 一车间（锻造） -->
            <div class="trace-section">
              <div class="section-header">
                <div class="section-title">
                  <i class="el-icon-s-cooperation section-icon"></i>
                  <span class="title-text">一车间（锻造）</span>
                </div>
                <div class="section-status-right">
                  <div class="quality-result">
                    <span class="result-label">质检结果：</span>
                    <el-tag 
                      v-if="traceData.workshop1?.qualityCheck"
                      :type="traceData.workshop1.qualityCheck.result === '合格' ? 'success' : 'danger'"
                      size="mini"
                    >
                      {{ traceData.workshop1.qualityCheck.result || '--' }}
                    </el-tag>
                    <span v-else>--</span>
                  </div>
                  <el-tag :type="getWorkshopStatus(traceData.workshop1)" size="mini">
                    {{ traceData.workshop1?.status || '--' }}
                  </el-tag>
                </div>
              </div>
              <el-row :gutter="20" class="section-content">
                <!-- 区块链数据：只保留车间真实上链的字段 -->
                <el-col :span="24" v-if="!traceData.workshop1 || Object.keys(traceData.workshop1).length === 0">
                  <div class="detail-item">
                    <div class="detail-value" style="color: #909399; text-align: center; padding: 20px;">
                      暂无数据
                    </div>
                  </div>
                </el-col>
                <!-- 只展示从区块链获取的数据 -->
                <template v-else>
                  <el-col :span="8" v-for="(value, key) in traceData.workshop1" :key="key">
                    <div class="detail-item">
                      <div class="detail-label">{{ formatFieldName(key) }}</div>
                      <div class="detail-value">{{ formatFieldValue(value) }}</div>
                    </div>
                  </el-col>
                </template>
              </el-row>
            </div>

            <!-- 二车间（热处理） -->
            <div class="trace-section">
              <div class="section-header">
                <div class="section-title">
                  <i class="el-icon-sunny section-icon"></i>
                  <span class="title-text">二车间（热处理）</span>
                </div>
                <div class="section-status-right">
                  <div class="quality-result">
                    <span class="result-label">硬度检测：</span>
                    <span v-if="traceData.workshop2?.hardnessTest">
                      {{ traceData.workshop2.hardnessTest.value || '--' }} HRC
                    </span>
                    <span v-else>--</span>
                  </div>
                  <el-tag :type="getWorkshopStatus(traceData.workshop2)" size="mini">
                    {{ traceData.workshop2?.status || '--' }}
                  </el-tag>
                </div>
              </div>
              <el-row :gutter="20" class="section-content">
                <!-- 区块链数据：只保留车间真实上链的字段 -->
                <el-col :span="24" v-if="!traceData.workshop2 || Object.keys(traceData.workshop2).length === 0">
                  <div class="detail-item">
                    <div class="detail-value" style="color: #909399; text-align: center; padding: 20px;">
                      暂无数据
                    </div>
                  </div>
                </el-col>
                <template v-else>
                  <el-col :span="8" v-for="(value, key) in traceData.workshop2" :key="key">
                    <div class="detail-item">
                      <div class="detail-label">{{ formatFieldName(key) }}</div>
                      <div class="detail-value">{{ formatFieldValue(value) }}</div>
                    </div>
                  </el-col>
                </template>
              </el-row>
            </div>

            <!-- 三车间（机加工） -->
            <div class="trace-section">
              <div class="section-header">
                <div class="section-title">
                  <i class="el-icon-s-operation section-icon"></i>
                  <span class="title-text">三车间（机加工）</span>
                </div>
                <div class="section-status-right">
                  <div class="quality-result">
                    <span class="result-label">精度检测：</span>
                    <el-tag 
                      v-if="traceData.workshop3?.precisionCheck"
                      :type="traceData.workshop3.precisionCheck.result === '合格' ? 'success' : 'danger'"
                      size="mini"
                    >
                      {{ traceData.workshop3.precisionCheck.result || '--' }}
                    </el-tag>
                    <span v-else>--</span>
                  </div>
                  <el-tag :type="getWorkshopStatus(traceData.workshop3)" size="mini">
                    {{ traceData.workshop3?.status || '--' }}
                  </el-tag>
                </div>
              </div>
              <el-row :gutter="20" class="section-content">
                <!-- 区块链数据：只保留车间真实上链的字段 -->
                <el-col :span="24" v-if="!traceData.workshop3 || Object.keys(traceData.workshop3).length === 0">
                  <div class="detail-item">
                    <div class="detail-value" style="color: #909399; text-align: center; padding: 20px;">
                      暂无数据
                    </div>
                  </div>
                </el-col>
                <template v-else>
                  <el-col :span="8" v-for="(value, key) in traceData.workshop3" :key="key">
                    <div class="detail-item">
                      <div class="detail-label">{{ formatFieldName(key) }}</div>
                      <div class="detail-value">{{ formatFieldValue(value) }}</div>
                    </div>
                  </el-col>
                </template>
              </el-row>
            </div>

            <!-- 四车间（装配） -->
            <div class="trace-section">
              <div class="section-header">
                <div class="section-title">
                  <i class="el-icon-s-tools section-icon"></i>
                  <span class="title-text">四车间（装配）</span>
                </div>
                <div class="section-status-right">
                  <div class="quality-result">
                    <span class="result-label">装配检测：</span>
                    <el-tag 
                      v-if="traceData.workshop4?.assemblyCheck"
                      :type="traceData.workshop4.assemblyCheck.result === '合格' ? 'success' : 'danger'"
                      size="mini"
                    >
                      {{ traceData.workshop4.assemblyCheck.result || '--' }}
                    </el-tag>
                    <span v-else>--</span>
                  </div>
                  <el-tag :type="getWorkshopStatus(traceData.workshop4)" size="mini">
                    {{ traceData.workshop4?.status || '--' }}
                  </el-tag>
                </div>
              </div>
              <el-row :gutter="20" class="section-content">
                <!-- 区块链数据：只保留车间真实上链的字段 -->
                <el-col :span="24" v-if="!traceData.workshop4 || Object.keys(traceData.workshop4).length === 0">
                  <div class="detail-item">
                    <div class="detail-value" style="color: #909399; text-align: center; padding: 20px;">
                      暂无数据
                    </div>
                  </div>
                </el-col>
                <template v-else>
                  <el-col :span="8" v-for="(value, key) in traceData.workshop4" :key="key">
                    <div class="detail-item">
                      <div class="detail-label">{{ formatFieldName(key) }}</div>
                      <div class="detail-value">{{ formatFieldValue(value) }}</div>
                    </div>
                  </el-col>
                </template>
              </el-row>
            </div>

            <!-- AI认证 -->
            <div class="trace-section ai-section">
              <div class="section-header">
                <div class="section-title">
                  <i class="el-icon-cpu section-icon"></i>
                  <span class="title-text">AI智能认证</span>
                </div>
                <div class="section-status-right">
                  <div class="quality-result">
                    <span class="result-label">认证分数：</span>
                    <span v-if="traceData.aiCertification?.certificationScore">
                      <span class="score-value">{{ traceData.aiCertification.certificationScore }}</span> 分
                    </span>
                    <span v-else>--</span>
                  </div>
                  <el-tag 
                    :type="traceData.aiCertification?.certificationStatus === '认证通过' ? 'success' : 'danger'"
                    size="mini"
                  >
                    {{ traceData.aiCertification?.certificationStatus || '--' }}
                  </el-tag>
                </div>
              </div>
              <el-row :gutter="20" class="section-content">
                <el-col :span="8">
                  <div class="detail-item">
                    <div class="detail-label">认证时间</div>
                    <div class="detail-value">{{ traceData.aiCertification?.certificationTime || '--' }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="detail-item">
                    <div class="detail-label">AI模型版本</div>
                    <div class="detail-value">{{ traceData.aiCertification?.aiModelVersion || '--' }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="detail-item">
                    <div class="detail-label">检测项目数</div>
                    <div class="detail-value">
                      <span v-if="traceData.aiCertification?.certificationDetails">
                        {{ traceData.aiCertification.certificationDetails.length }} 项
                      </span>
                      <span v-else>--</span>
                    </div>
                  </div>
                </el-col>
                <el-col :span="24">
                  <div class="detail-item full-width">
                    <div class="detail-label">认证报告摘要</div>
                    <div class="detail-value">
                      <p v-if="traceData.aiCertification?.reportSummary" class="report-summary">
                        {{ traceData.aiCertification.reportSummary }}
                      </p>
                      <span v-else>--</span>
                    </div>
                  </div>
                </el-col>
                <el-col :span="24" v-if="traceData.aiCertification?.certificationDetails">
                  <div class="detail-item full-width">
                    <div class="detail-label">详细检测项</div>
                    <div class="detail-value">
                      <div class="ai-details">
                        <el-tag
                          v-for="(item, index) in traceData.aiCertification.certificationDetails"
                          :key="index"
                          :type="item.result === '通过' ? 'success' : 'danger'"
                          size="mini"
                          class="ai-tag"
                        >
                          {{ item.item }}: {{ item.result }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>

        <!-- 区块链信息 -->
        <div class="blockchain-info-card" v-if="traceData.isOnChain">
          <div class="blockchain-header">
            <h3 class="blockchain-title">
              <i class="el-icon-connection"></i>
              区块链存证信息
            </h3>
            <el-tag type="success" size="small" effect="dark">
              <i class="el-icon-circle-check"></i> 已上链验证
            </el-tag>
          </div>
          <div class="blockchain-content">
            <!-- 第一行：区块链基本信息 -->
            <el-row :gutter="20" class="blockchain-row">
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-s-grid"></i>
                    区块哈希
                  </div>
                  <div class="blockchain-value hash-value" :title="traceData.blockchainInfo?.blockHash">
                    {{ traceData.blockchainInfo?.blockHash || '--' }}
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-link"></i>
                    交易哈希
                  </div>
                  <div class="blockchain-value hash-value" :title="traceData.blockchainInfo?.transactionHash">
                    {{ traceData.blockchainInfo?.transactionHash || traceData.blockchainInfo?.transactionId || '--' }}
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-time"></i>
                    查询时间
                  </div>
                  <div class="blockchain-value time-value">
                    {{ traceData.blockchainInfo?.onChainTime || traceData.blockchainInfo?.timestamp || '--' }}
                  </div>
                </div>
              </el-col>
            </el-row>

            <!-- 第二行：区块链详细信息 -->
            <el-row :gutter="20" class="blockchain-row">
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-box"></i>
                    区块高度
                  </div>
                  <div class="blockchain-value">
                    {{ traceData.blockchainInfo?.blockNumber || traceData.blockchainInfo?.blockHeight || '--' }}
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-document"></i>
                    合约地址
                  </div>
                  <div class="blockchain-value hash-value" :title="traceData.blockchainInfo?.contractAddress">
                    {{ traceData.blockchainInfo?.contractAddress || '--' }}
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-user"></i>
                    上链账户
                  </div>
                  <div class="blockchain-value hash-value" :title="traceData.blockchainInfo?.fromAddress">
                    {{ traceData.blockchainInfo?.fromAddress || traceData.blockchainInfo?.sender || '--' }}
                  </div>
                </div>
              </el-col>
            </el-row>

            <!-- 第三行：区块链网络信息 -->
            <el-row :gutter="20" class="blockchain-row">
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-guide"></i>
                    区块链网络
                  </div>
                  <div class="blockchain-value">
                    {{ traceData.blockchainInfo?.network || 'FISCO BCOS' }}
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-data-line"></i>
                    验证状态
                  </div>
                  <div class="blockchain-value">
                    <el-tag type="success" size="mini" effect="plain">
                      <i class="el-icon-success"></i> 已验证
                    </el-tag>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="blockchain-item">
                  <div class="blockchain-label">
                    <i class="el-icon-finished"></i>
                    存证状态
                  </div>
                  <div class="blockchain-value">
                    <el-tag type="success" size="mini" effect="plain">
                      <i class="el-icon-check"></i> 不可篡改
                    </el-tag>
                  </div>
                </div>
              </el-col>
            </el-row>

            <!-- 区块链验证区域 -->
            <div class="blockchain-verify-section">
              <div class="verify-title">
                <i class="el-icon-trophy"></i>
                区块链验证保证
              </div>
              <div class="verify-content">
                <el-row :gutter="16">
                  <el-col :span="6">
                    <div class="verify-item">
                      <i class="el-icon-lock verify-icon"></i>
                      <div class="verify-text">数据加密</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="verify-item">
                      <i class="el-icon-circle-check verify-icon"></i>
                      <div class="verify-text">不可篡改</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="verify-item">
                      <i class="el-icon-time verify-icon"></i>
                      <div class="verify-text">时间戳证明</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="verify-item">
                      <i class="el-icon-view verify-icon"></i>
                      <div class="verify-text">公开可验证</div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

            <!-- 区块链浏览器链接 -->
            <div class="blockchain-explorer" v-if="traceData.blockchainInfo?.blockHash">
              <el-button 
                type="primary" 
                size="small" 
                icon="el-icon-view"
                @click="viewInExplorer"
                plain
              >
                在区块链浏览器中查看
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                icon="el-icon-download"
                @click="downloadBlockchainProof"
                plain
              >
                下载存证证书
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="loading-overlay" v-if="loading">
      <div class="loading-content">
        <i class="el-icon-loading loading-icon"></i>
        <p class="loading-text">正在查询溯源信息...</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'TraceSystem',
  data() {
    return {
      // 查询表单
      queryForm: {
        shaftCode: ''
      },
      // 显示控制
      loading: false,
      queryLoading: false,
      exportLoading: false,
      aiAnalysisLoading: false,
      showResult: false,
      // 查询时间
      queryTime: '',
      // 查询历史记录
      queryHistory: [],
      // 溯源数据
      traceData: {
        shaftCode: '',
        productModel: '',
        batchNumber: '',
        productionDate: '',
        factoryDate: '',
        qualityStatus: '',
        isOnChain: false,
        // 原料产地
        rawMaterial: null,
        // 各车间数据
        workshop1: null,
        workshop2: null,
        workshop3: null,
        workshop4: null,
        // AI认证
        aiCertification: null,
        // 区块链信息
        blockchainInfo: null
      },
      // 查询前筛选条件
      commonConditions: [
        { key: 'shaftCode', label: '半轴编码', type: 'input', placeholder: '请输入编码', value: '' },
        { key: 'qualityStatus', label: '质检状态', type: 'select', placeholder: '选择状态', value: '', options: [
          { label: '合格', value: '合格' },
          { label: '不合格', value: '不合格' },
          { label: '待检测', value: '待检测' }
        ]},
        { key: 'productionDate', label: '生产日期', type: 'date', placeholder: '选择日期', value: '' },
        { key: 'productModel', label: '产品型号', type: 'input', placeholder: '输入型号', value: '' }
      ],
      // 示例编码
      exampleCodes: ['SN-89-63', 'SN-89-98', 'SN-64-65', 'SN-65-66']
    }
  },
  computed: {
    // 结果标签类型
    resultTagType() {
      if (!this.traceData.qualityStatus) return 'info'
      return this.traceData.qualityStatus === '合格' ? 'success' : 'danger'
    },
    // 结果标签文本
    resultTagText() {
      if (!this.traceData.qualityStatus) return '未知'
      return this.traceData.qualityStatus === '合格' ? '质量合格' : '质量异常'
    }
  },//const response = await this.queryTraceData(this.queryForm.shaftCode)
  methods: {
    // 查询处理
    async handleQuery() {
      if (!this.queryForm.shaftCode.trim()) {
        this.$message.warning('请输入半轴编码')
        return
      }

      try {
        this.queryLoading = true
        this.loading = true
        
        
        const response = await this.queryTraceData(this.queryForm.shaftCode)
        
        if (response && response.success) {
          this.traceData = response.data
          this.queryTime = new Date().toLocaleString('zh-CN')
          
          // 添加到查询历史
          this.addToHistory(this.queryForm.shaftCode, response.data)
          
          // 显示结果详情
          this.showResult = true
          
          this.$message.success('查询成功！')
        } else {
          this.$message.error(response?.message || '查询失败，请检查编码是否正确')
        }
      } catch (error) {
        console.error('查询失败:', error)
        this.$message.error('查询请求失败，请稍后重试')
      } finally {
        this.queryLoading = false
        this.loading = false
      }
    },

    // 添加到查询历史
    addToHistory(shaftCode, traceData) {
      const now = new Date()
      const timeStr = `${now.getMonth() + 1}/${now.getDate()} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
      
      // 根据质量状态生成AI质控标签
      const aiQuality = this.getAIQuality(traceData.qualityStatus, traceData.workshop3?.inspection?.defectLevel)
      
      // 生成描述
      const description = this.generateDescription(traceData)
      
      const historyItem = {
        time: timeStr,
        shaftId: shaftCode,
        aiQuality: aiQuality,
        description: description,
        status: '完成',
        traceData: JSON.parse(JSON.stringify(traceData)) // 深拷贝数据
      }
      
      // 添加到历史记录开头
      this.queryHistory.unshift(historyItem)
      
      // 限制历史记录数量
      if (this.queryHistory.length > 10) {
        this.queryHistory = this.queryHistory.slice(0, 10)
      }
      
      // 保存到本地存储
      this.saveHistoryToLocal()
    },

    // 获取AI质控标签
    getAIQuality(qualityStatus, defectLevel) {
      if (qualityStatus === '不合格') {
        return '裂纹'
      }
      if (defectLevel && defectLevel !== '无') {
        return '缺陷'
      }
      return '合格'
    },

    // 生成描述
    generateDescription(traceData) {
      if (traceData.qualityStatus === '不合格') {
        return 'AI检测发现内部裂纹问题'
      }
      if (traceData.workshop3?.inspection?.defectLevel && traceData.workshop3.inspection.defectLevel !== '无') {
        return `AI检测发现${traceData.workshop3.inspection.defectLevel}缺陷`
      }
      return 'AI检测通过，各工序参数符合标准要求'
    },

    // 获取AI标签类型
    getAITagType(aiQuality) {
      switch (aiQuality) {
        case '合格': return 'success'
        case '缺陷': return 'warning'
        case '裂纹': return 'danger'
        default: return 'info'
      }
    },

    // 表格行类名
    tableRowClassName({ row, rowIndex }) {
      if (row.aiQuality === '裂纹') {
        return 'danger-row'
      } else if (row.aiQuality === '缺陷') {
        return 'warning-row'
      }
      return ''
    },

    // 查看历史详情
    viewHistoryDetail(row) {
      this.traceData = row.traceData
      this.queryTime = row.time.replace('/', '-') + ':00'
      this.showResult = true
      this.$message.info(`查看 ${row.shaftId} 的溯源详情`)
    },

    // 关闭结果详情
    closeResult() {
      this.showResult = false
    },

    // 清空历史记录
    clearHistory() {
      this.queryHistory = []
      localStorage.removeItem('traceQueryHistory')
      this.$message.success('已清空查询历史记录')
    },

    // 保存历史记录到本地存储
    saveHistoryToLocal() {
      try {
        // 只保存必要的数据，避免数据过大
        const simpleHistory = this.queryHistory.map(item => ({
          time: item.time,
          shaftId: item.shaftId,
          aiQuality: item.aiQuality,
          description: item.description,
          status: item.status
        }))
        localStorage.setItem('traceQueryHistory', JSON.stringify(simpleHistory))
      } catch (error) {
        console.error('保存历史记录失败:', error)
      }
    },

    // 从本地存储加载历史记录
    loadHistoryFromLocal() {
      try {
        const history = localStorage.getItem('traceQueryHistory')
        if (history) {
          this.queryHistory = JSON.parse(history)
        }
      } catch (error) {
        console.error('加载历史记录失败:', error)
      }
    },

    // 调用后端API查询溯源信息
    async queryTraceData(shaftCode) {
      try {
        // 调用后端真实API
        const response = await axios.get(`/api/trace/query/${shaftCode}`)
            
        if (response.data.code === 200 && response.data.data) {
          // 转换后端数据为前端展示格式
          return {
            success: true,
            message: '查询成功',
            data: this.transformTraceData(shaftCode, response.data.data)
          }
        } else {
          return {
            success: false,
            message: response.data.msg || '未查询到该产品的溯源信息'
          }
        }
      } catch (error) {
        console.error('查询溯源信息失败:', error)
        return {
          success: false,
          message: '查询失败：' + (error.response?.data?.msg || error.message || '网络错误')
        }
      }
    },
        
    // 转换后端数据为前端展示格式
    transformTraceData(shaftCode, backendData) {
      console.log('🔍 后端返回的原始数据:', backendData)
      
      // 直接使用后端返回的真实数据，不再填充模拟数据
      const result = {
        shaftCode,
        productModel: backendData.productModel || '--',
        batchNumber: backendData.materialBatch || '--',
        productionDate: backendData.productionDate || '--',
        factoryDate: backendData.updateTime || '--',
        qualityStatus: this.getQualityStatus(backendData.defectLevel, backendData.status),
        isOnChain: backendData.isOnChain !== undefined ? backendData.isOnChain : true,
            
        // 原料产地数据 - 从后端获取
        rawMaterial: backendData.rawMaterial || null,
            
        // 一车间（锻造）- 从后端获取
        workshop1: backendData.workshop1 || null,
            
        // 二车间（热处理）- 从后端获取
        workshop2: backendData.workshop2 || null,
            
        // 三车间（机加工）- 从后端获取
        workshop3: backendData.workshop3 || null,
            
        // 四车间（装配）- 从后端获取
        workshop4: backendData.workshop4 || null,
            
        // AI认证 - 从后端获取
        aiCertification: backendData.aiCertification || null,
            
        // 区块链信息 - 从后端获取
        blockchainInfo: backendData.blockchainInfo || null
      }
      
      console.log('✅ 转换后的数据:', result)
      console.log('📊 车间数据状态:', {
        workshop1: result.workshop1 ? 'OK' : 'NULL',
        workshop2: result.workshop2 ? 'OK' : 'NULL',
        workshop3: result.workshop3 ? 'OK' : 'NULL',
        workshop4: result.workshop4 ? 'OK' : 'NULL',
        blockchain: result.blockchainInfo ? 'OK' : 'NULL'
      })
      
      return result
    },



    // 获取质量状态
    getQualityStatus(defectLevel, status) {
      if (status === '合格') return '合格'
      if (status === '不合格') return '不合格'
      if (defectLevel === 0) return '合格'
      if (defectLevel >= 3) return '不合格'
      return status || '待检测'
    },
        
    // 计算评分
    calculateScore(defectLevel) {
      if (defectLevel === 0) return 98
      if (defectLevel === 1) return 92
      if (defectLevel === 2) return 85
      if (defectLevel === 3) return 65
      return 95
    },

    // 获取车间状态标签类型
    getWorkshopStatus(workshop) {
      if (!workshop) return 'info'
      return workshop.status === '已完成' ? 'success' : 
             workshop.status === '进行中' ? 'warning' : 
             workshop.status === '异常' ? 'danger' : 'info'
    },

    // 格式化字段名称（英文转中文）
    formatFieldName(key) {
      const fieldMap = {
        // 一车间（锻造）
        'pressing': '压制工序',
        'cutting': '切割工序',
        'forging': '锻造工序',
        'splineSize': '花键尺寸',
        'pressure': '压力',
        'equipmentNo': '设备编号',
        'cutSpeed': '切割速度',
        'materialBatch': '材料批次',
        'cutSize': '切割尺寸',
        'operator': '操作员',
        'defect': '缺陷',
        'forgingTemp': '锻造温度',
        'holdTime': '保温时间',
        
        // 二车间（热处理）
        'heatTreatment': '热处理工序',
        'quenching': '淬火工序',
        'tempering': '回火工序',
        'heatingTemp': '加热温度',
        'coolingMethod': '冷却方式',
        'coolingRate': '冷却速率',
        'quenchingTemp': '淬火温度',
        'temperingTemp': '回火温度',
        'hardness': '硬度',
        'hardnessValue': '硬度值',
        'treatmentTime': '处理时间',
        'furnaceNo': '炉号',
        'drilling': '钻孔工序',
        'gear': '齿轮工序',
        'hole': '孔位',
        'holeSize': '孔径',
        'drillSpeed': '钻孔速度',
        'gearAccuracy': '齿轮精度',
        'surface': '表面',
        'toothProfile': '齿形',
        '加热温度': '加热温度',
        '保温时间': '保温时间',
        
        // 三车间（机加工）
        'machining': '机加工工序',
        'turning': '车削工序',
        'milling': '铣削工序',
        'grinding': '磨削工序',
        'machineNo': '机床编号',
        'cuttingSpeed': '切削速度',
        'feedRate': '进给速度',
        'depth': '加工深度',
        'dimension': '尺寸',
        'tolerance': '公差',
        'surfaceRoughness': '表面粗糙度',
        'precision': '精度',
        'straightening': '矫直工序',
        'straighteningForce': '矫直力',
        'fine': '精加工工序',
        'holeAccuracy': '孔精度',
        '回火温度': '回火温度',
        '保温时间': '保温时间',
        '年削工序速度': '车削工序速度',
        '表面粗糙度': '表面粗糙度',
        '浮火工序': '淬火工序',
        'coolingMedium': '冷却介质',
        '硬度': '硬度',
        '浮火温度': '淬火温度',
        '检验': '检验',
        '缺陷Position': '缺陷位置',
        '缺陷等级': '缺陷等级',
        '检验员名称': '检验员名称',
        
        // 四车间（装配）
        'assembly': '装配工序',
        'preAssembly': '预装配',
        'finalAssembly': '总装配',
        'inspection': '检验',
        'assemblyNo': '装配编号',
        'assemblyLine': '装配线',
        'torque': '扭矩',
        'clearance': '间隙',
        'alignment': '对齐度',
        'assemblyTime': '装配时间',
        'inspector': '检验员',
        'testResult': '测试结果',
        'painting': '喷漆工序',
        'packing': '包装工序',
        'spray': '喷涂',
        'sprayPressure': '喷涂压力',
        'paintBatch': '油漆批次',
        'paint': '油漆',
        '厚度': '厚度',
        'pack': '包装',
        'packWorker': '包装作业员',
        'packTime': '包装时间',
        '操作员': '包装操作员',
        '时间': '包装时间',
        
        // 通用字段
        'status': '状态',
        'time': '时间',
        'date': '日期',
        'result': '结果',
        'quality': '质量',
        'timestamp': '时间戳',
        'temperature': '温度',
        'duration': '时长',
        'remark': '备注',
        'batchNo': '批次号',
        'processTime': '加工时间',
        'workshop': '车间',
        'process': '工序',
        'product': '产品',
        'material': '材料',
        'equipment': '设备',
        'worker': '工人',
        'shift': '班次',
        'location': '位置',
        'quantity': '数量',
        'weight': '重量',
        'length': '长度',
        'width': '宽度',
        'height': '高度',
        'diameter': '直径',
        'thickness': '厚度',
        'speed': '速度',
        'power': '功率',
        'voltage': '电压',
        'current': '电流',
        'frequency': '频率',
        'humidity': '湿度',
        'pH': '酸碱度',
        'density': '密度',
        'viscosity': '粘度',
        'color': '颜色',
        'texture': '纹理',
        'finish': '表面处理',
        'coating': '涂层',
        'treatment': '处理',
        'test': '测试',
        'check': '检查',
        'verify': '验证',
        'approve': '批准',
        'reject': '拒绝',
        'pass': '通过',
        'fail': '失败',
        'error': '错误',
        'warning': '警告',
        'info': '信息',
        'note': '注释',
        'comment': '评论',
        'description': '描述',
        'name': '名称',
        'code': '编码',
        'id': '编号',
        'number': '号码',
        'type': '类型',
        'category': '分类',
        'group': '组别',
        'level': '等级',
        'grade': '级别',
        'priority': '优先级',
        'version': '版本',
        'revision': '修订',
        'update': '更新',
        'create': '创建',
        'modify': '修改',
        'delete': '删除',
        'startTime': '开始时间',
        'endTime': '结束时间',
        'createTime': '创建时间',
        'updateTime': '更新时间',
        'user': '用户',
        'creator': '创建人',
        'updater': '更新人',
        'supplier': '供应商',
        'customer': '客户',
        'vendor': '供应商',
        'manufacturer': '制造商',
        'batch': '批次',
        'lot': '批号',
        'serial': '序列号',
        'model': '型号',
        'specification': '规格',
        'standard': '标准',
        'reference': '参考',
        'value': '数值',
        'unit': '单位',
        'range': '范围',
        'min': '最小值',
        'max': '最大值',
        'avg': '平均值',
        'total': '总计',
        'count': '计数',
        'rate': '速率',
        'ratio': '比率',
        'percent': '百分比',
        'force': '力',
        'medium': '介质',
        'position': '位置',
        'accuracy': '精度'
      }
      
      // 如果有映射就返回中文
      if (fieldMap[key]) {
        return fieldMap[key]
      }
      
      // 对于没有映射的字段，尝试智能转换
      // 将驼峰命名转换为带空格的形式，然后翻译常见单词
      const words = key.replace(/([A-Z])/g, ' $1').trim().split(' ')
      const translatedWords = words.map(word => {
        const lowerWord = word.toLowerCase()
        return fieldMap[lowerWord] || word
      })
      
      return translatedWords.join('')
    },

    // 格式化字段值
    formatFieldValue(value) {
      if (value === null || value === undefined) return '--'
      if (typeof value === 'object') {
        // 如果是对象，递归显示其字段
        return Object.entries(value)
          .map(([k, v]) => `${this.formatFieldName(k)}: ${this.formatFieldValue(v)}`)
          .join(', ')
      }
      return String(value)
    },

    // 刷新页面
    refreshPage() {
      this.queryForm.shaftCode = ''
      this.showResult = false
      this.clearConditions()
      this.traceData = this.getEmptyTraceData()
      this.$message.info('页面已重置')
    },

    // 清空条件
    clearConditions() {
      this.commonConditions.forEach(cond => cond.value = '')
      this.$message.info('已清空筛选条件')
    },

    // 应用筛选（查询前）
    applyConditions() {
      // 在实际应用中，这里可以设置默认查询条件
      const shaftCode = this.commonConditions.find(c => c.key === 'shaftCode')?.value
      if (shaftCode) {
        this.queryForm.shaftCode = shaftCode
        this.handleQuery()
      } else {
        this.$message.info('筛选条件已保存，请输入编码后查询')
      }
    },

    // 获取空数据
    getEmptyTraceData() {
      return {
        shaftCode: '',
        productModel: '',
        batchNumber: '',
        productionDate: '',
        factoryDate: '',
        qualityStatus: '',
        isOnChain: false,
        rawMaterial: null,
        workshop1: null,
        workshop2: null,
        workshop3: null,
        workshop4: null,
        aiCertification: null,
        blockchainInfo: null
      }
    },

    // 填充示例
    fillExample(code) {
      this.queryForm.shaftCode = code
      this.handleQuery()
    },

    // 导出报告
    async exportReport() {
      try {
        this.exportLoading = true
        this.$message.info('正在生成报告...')
        
        // 调用后端导出API
        const response = await axios.get(`/api/trace/export/${this.traceData.shaftCode}`, {
          responseType: 'blob'
        })
        
        // 创建下载链接
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `溯源报告_${this.traceData.shaftCode}_${new Date().getTime()}.pdf`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        
        this.$message.success('报告导出成功！')
      } catch (error) {
        console.error('导出报告失败:', error)
        this.$message.error('导出报告失败')
      } finally {
        this.exportLoading = false
      }
    },

    // 打印报告
    printReport() {
      this.$message.info('打印功能开发中...')
    },

    // 分享报告
    shareReport() {
      this.$message.info('分享功能开发中...')
    },

    // AI分析处理
    async handleAIAnalysis() {
      if (!this.traceData || !this.traceData.shaftCode) {
        return this.$message.warning('请先查询产品溯源信息')
      }
      
      try {
        this.aiAnalysisLoading = true
        
        // 调用AI分析API
        const response = await axios.get(`/api/trace/analyze/${this.traceData.shaftCode}`)
        
        if (response.data.code === 200 && response.data.data) {
          this.$message.success('AI分析完成！')
          // 这里可以显示AI分析结果
        } else {
          this.$message.error(response.data.message || 'AI分析失败')
        }
      } catch (error) {
        console.error('AI分析失败:', error)
        this.$message.error('AI分析请求失败，请稍后重试')
      } finally {
        this.aiAnalysisLoading = false
      }
    },

    // 查看报告
    viewReport(url) {
      this.$message.info(`打开报告：${url}`)
      // window.open(url, '_blank')
    },

    // 在区块链浏览器中查看
    viewInExplorer() {
      const blockHash = this.traceData.blockchainInfo?.blockHash
      if (blockHash) {
        // 这里可以根据实际的区块链浏览器URL进行跳转
        const explorerUrl = `http://your-blockchain-explorer.com/block/${blockHash}`
        this.$message.info('正在打开区块链浏览器...')
        // window.open(explorerUrl, '_blank')
        console.log('区块链浏览器URL:', explorerUrl)
      } else {
        this.$message.warning('区块哈希不存在')
      }
    },

    // 下载区块链存证证书
    async downloadBlockchainProof() {
      try {
        this.$message.info('正在生成存证证书...')
        
        // 调用后端API生成证书
        const response = await axios.get(
          `/api/blockchain/proof/${this.traceData.shaftCode}`,
          { responseType: 'blob' }
        )
        
        // 创建下载链接
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `区块链存证证书_${this.traceData.shaftCode}_${new Date().getTime()}.pdf`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        
        this.$message.success('存证证书下载成功！')
      } catch (error) {
        console.error('下载存证证书失败:', error)
        this.$message.error('下载失败，请稍后重试')
      }
    }
  },
  
  mounted() {
    console.log('半轴溯源查询系统初始化完成')
    // 加载历史记录
    this.loadHistoryFromLocal()
  }
}
</script>

<style scoped>
.trace-system-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4efe9 100%);
  padding: 20px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 头部区域 */
.header-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8e8e8;
}

.header-title {
  margin-bottom: 20px;
}

.header-title h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-title h2 i {
  color: #1890ff;
}

.subtitle {
  margin: 8px 0 0 0;
  color: #606266;
  font-size: 14px;
}

/* 查询区域 */
.query-section {
  margin-top: 20px;
}

.query-input-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.shaft-input {
  flex: 1;
}

.input-label {
  color: #1890ff;
  font-weight: 500;
}

.query-btn {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  border: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.query-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.query-tips {
  margin-top: 12px;
  padding: 12px;
  background: #f0f9ff;
  border-radius: 6px;
  border-left: 4px solid #1890ff;
}

.query-tips p {
  margin: 6px 0;
  color: #606266;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.query-tips i {
  color: #1890ff;
}

/* 查询历史记录区域 */
.history-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.history-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.history-header i {
  color: #1890ff;
}

.history-table {
  margin-top: 16px;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: linear-gradient(135deg, #f0f9ff, #e6f4ff) !important;
  color: #1890ff;
  font-weight: 600;
  font-size: 13px;
}

:deep(.el-table .danger-row) {
  background-color: rgba(245, 108, 108, 0.08) !important;
}

:deep(.el-table .warning-row) {
  background-color: rgba(230, 162, 60, 0.08) !important;
}

:deep(.el-table .success-row) {
  background-color: rgba(103, 194, 58, 0.08) !important;
}

.time-cell {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #666;
}

.id-tag {
  background: linear-gradient(135deg, #1890ff20, #36cfc920);
  border: none;
  color: #1890ff;
  font-weight: 500;
}

.ai-tag {
  border: none;
  border-radius: 6px;
  font-weight: 500;
}

.description-cell {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.status-tag {
  border: none;
  border-radius: 6px;
  font-weight: 500;
}

/* 条件筛选区域 */
.condition-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.condition-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.condition-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.condition-header i {
  color: #1890ff;
}

.condition-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.condition-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.condition-label {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.condition-input {
  width: 100%;
}

/* 空状态 */
.empty-state {
  background: white;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-content {
  max-width: 500px;
}

.empty-icon {
  font-size: 64px;
  color: #dcdfe6;
  margin-bottom: 20px;
}

.empty-content h3 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 20px;
  font-weight: 500;
}

.empty-content p {
  color: #606266;
  margin-bottom: 24px;
  line-height: 1.6;
}

.empty-examples {
  text-align: left;
  padding: 20px;
  background: linear-gradient(135deg, #f0f7ff, #e6f7ff);
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.empty-examples p {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.example-codes {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.example-tag {
  cursor: pointer;
  transition: all 0.3s;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  border: none;
  color: white;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 13px;
}

.example-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* 结果卡片 */
.result-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  animation: fadeIn 0.5s ease;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  background: linear-gradient(145deg, #ffffff, #fafafa);
  border: none;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e8e8e8;
}

.result-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.result-icon {
  font-size: 22px;
  color: #52c41a;
  background: linear-gradient(135deg, #52c41a, #73d13d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.title-text {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.result-tag {
  margin-left: 8px;
  font-weight: 500;
  border-radius: 12px;
}

.result-actions {
  display: flex;
  gap: 8px;
}

/* 基本信息 */
.basic-info {
  background: linear-gradient(135deg, #f0f9ff, #e6f4ff);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  border: 1px solid #d1e9ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.08);
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.info-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-title i {
  color: #1890ff;
}

.blockchain-info {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #606266;
}

.blockchain-info i {
  margin-right: 4px;
  color: #1890ff;
}

.info-grid {
  margin: 0 !important;
}

.info-item {
  margin-bottom: 20px;
}

.info-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
  word-break: break-all;
}

.code-value {
  color: #1890ff;
  font-weight: 600;
  font-size: 18px;
}

/* 溯源详情 */
.trace-details {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  border: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.details-title {
  margin: 0 0 24px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.details-title i {
  color: #1890ff;
}

.trace-section {
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  border-left: 4px solid #1890ff;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.trace-section:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.ai-section {
  border-left-color: #722ed1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.section-status-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.quality-result {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.result-label {
  color: #666;
  font-size: 13px;
}

.score-value {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-icon {
  font-size: 18px;
  color: #1890ff;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #1890ff20, #36cfc920);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.ai-section .section-icon {
  color: #722ed1;
  background: linear-gradient(135deg, #722ed120, #9254de20);
}

.section-content {
  margin: 0 !important;
}

.detail-item {
  margin-bottom: 16px;
}

.full-width {
  width: 100%;
}

.detail-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: #303133;
  word-break: break-all;
  line-height: 1.6;
}

.report-summary {
  margin: 0;
  line-height: 1.6;
  color: #606266;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #1890ff;
}

.ai-details {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.ai-tag {
  font-size: 12px;
  padding: 6px 10px;
  border-radius: 6px;
  font-weight: 500;
}

/* 区块链信息 */
.blockchain-info-card {
  background: linear-gradient(135deg, #f0f9ff, #e6f7ff);
  border-radius: 16px;
  padding: 28px;
  border: 2px solid #91d5ff;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.15);
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

.blockchain-info-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(24, 144, 255, 0.05) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.blockchain-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #91d5ff;
  position: relative;
  z-index: 1;
}

.blockchain-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 10px;
}

.blockchain-title i {
  font-size: 24px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.blockchain-content {
  position: relative;
  z-index: 1;
}

.blockchain-row {
  margin-bottom: 20px;
}

.blockchain-item {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  padding: 16px;
  height: 100%;
  border: 1px solid #d9f1ff;
  transition: all 0.3s ease;
}

.blockchain-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.2);
  border-color: #1890ff;
}

.blockchain-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 10px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.blockchain-label i {
  color: #1890ff;
  font-size: 16px;
}

.blockchain-value {
  font-size: 14px;
  color: #303133;
  word-break: break-all;
  font-family: 'Courier New', monospace;
  font-weight: 500;
  line-height: 1.6;
}

.hash-value {
  font-size: 12px;
  color: #1890ff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: help;
}

.time-value {
  color: #52c41a;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 区块链验证区域 */
.blockchain-verify-section {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  margin-top: 24px;
  border: 2px dashed #91d5ff;
}

.verify-title {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.verify-title i {
  font-size: 20px;
  color: #faad14;
}

.verify-content {
  margin-top: 12px;
}

.verify-item {
  text-align: center;
  padding: 16px 8px;
  background: linear-gradient(135deg, #f0f9ff, #ffffff);
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: default;
}

.verify-item:hover {
  background: linear-gradient(135deg, #e6f7ff, #f0f9ff);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.verify-icon {
  font-size: 32px;
  color: #52c41a;
  margin-bottom: 8px;
  display: block;
}

.verify-text {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

/* 区块链浏览器链接 */
.blockchain-explorer {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #d9f1ff;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.blockchain-explorer .el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.blockchain-explorer .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.loading-content {
  text-align: center;
}

.loading-icon {
  font-size: 48px;
  color: #1890ff;
  animation: rotate 1.5s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 16px;
  font-size: 16px;
  color: #606266;
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .trace-system-container {
    padding: 12px;
  }
  
  .header-section,
  .condition-section,
  .empty-state,
  .result-card {
    padding: 16px;
  }
  
  .query-input-group {
    flex-direction: column;
  }
  
  .query-btn,
  .shaft-input {
    width: 100%;
  }
  
  .condition-grid {
    grid-template-columns: 1fr;
  }
  
  .result-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .result-actions {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }
  
  .info-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .section-status-right {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .blockchain-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .el-col {
    margin-bottom: 12px;
  }
}

/* 主内容区域 */
.main-content {
  margin-top: 20px;
}

.result-content {
  margin-top: 20px;
}
</style>