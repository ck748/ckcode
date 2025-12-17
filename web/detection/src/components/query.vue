<template>
  <div class="trace-query-container">
    <!-- 查询区域 -->
    <div class="query-card">
      <div class="query-header">
        <div class="header-left">
          <i class="el-icon-search header-icon"></i>
          <h2 class="header-title">半轴溯源查询</h2>
        </div>
        <div class="header-right">
          <el-button
            type="primary"
            icon="el-icon-refresh"
            @click="refreshPage"
            plain
            size="small"
          >
            刷新
          </el-button>
        </div>
      </div>

      <div class="query-form">
        <el-form :model="queryForm" ref="queryFormRef" :rules="queryRules" label-width="120px">
          <el-form-item label="半轴编码" prop="shaftCode" required>
            <div class="code-input-wrapper">
              <el-input
                v-model="queryForm.shaftCode"
                placeholder="请输入半轴唯一编码（如：BZ202312001）"
                clearable
                size="medium"
                class="shaft-code-input"
                @keyup.enter.native="handleQuery"
              >
                <template #prepend>
                  <span class="code-prefix">编码</span>
                </template>
              </el-input>
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="handleQuery"
                :loading="queryLoading"
                size="medium"
                class="query-button"
              >
                查询溯源
              </el-button>
            </div>
            <div class="input-tips">
              <p class="tip-item">
                <i class="el-icon-info"></i>
                提示：请输入完整的半轴编码，编码格式通常为"BZ"开头+年月+序号（如：BZ202312001）
              </p>
              <p class="tip-item">
                <i class="el-icon-s-data"></i>
                查询范围：可查询已上链的半轴溯源信息，包含原料、生产、检测全流程
              </p>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 查询结果 -->
    <div class="result-card" v-if="showResult">
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
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">供应商</div>
                  <div class="detail-value">{{ traceData.rawMaterial?.supplier || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">材料类型</div>
                  <div class="detail-value">{{ traceData.rawMaterial?.materialType || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">产地</div>
                  <div class="detail-value">{{ traceData.rawMaterial?.origin || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">采购日期</div>
                  <div class="detail-value">{{ traceData.rawMaterial?.purchaseDate || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">检验报告</div>
                  <div class="detail-value">
                    <el-button
                      v-if="traceData.rawMaterial?.reportUrl"
                      type="text"
                      @click="viewReport(traceData.rawMaterial.reportUrl)"
                      size="small"
                    >
                      查看报告
                    </el-button>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">批次编号</div>
                  <div class="detail-value">{{ traceData.rawMaterial?.batchNo || '--' }}</div>
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
              <div class="section-status">
                <el-tag :type="getWorkshopStatus(traceData.workshop1)" size="mini">
                  {{ traceData.workshop1?.status || '--' }}
                </el-tag>
              </div>
            </div>
            <el-row :gutter="20" class="section-content">
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">加工日期</div>
                  <div class="detail-value">{{ traceData.workshop1?.processDate || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">操作员</div>
                  <div class="detail-value">{{ traceData.workshop1?.operator || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">设备编号</div>
                  <div class="detail-value">{{ traceData.workshop1?.equipmentNo || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">加工参数</div>
                  <div class="detail-value">
                    <span v-if="traceData.workshop1?.parameters">
                      温度：{{ traceData.workshop1.parameters.temperature || '--' }}℃，
                      压力：{{ traceData.workshop1.parameters.pressure || '--' }}MPa
                    </span>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">质检结果</div>
                  <div class="detail-value">
                    <el-tag 
                      v-if="traceData.workshop1?.qualityCheck"
                      :type="traceData.workshop1.qualityCheck.result === '合格' ? 'success' : 'danger'"
                      size="mini"
                    >
                      {{ traceData.workshop1.qualityCheck.result || '--' }}
                    </el-tag>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <!-- 切割下料工序 -->
              <el-col :span="24" v-if="traceData.workshop1?.cutting">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #409EFF;">① 切割下料</div>
                  <div class="detail-value">
                    <span>材料批次：{{ traceData.workshop1.cutting.materialBatch }} | </span>
                    <span>切割尺寸：{{ traceData.workshop1.cutting.cutSize }} | </span>
                    <span>切割速度：{{ traceData.workshop1.cutting.cutSpeed }} | </span>
                    <span>操作员：{{ traceData.workshop1.cutting.operator }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 压花键工序 -->
              <el-col :span="24" v-if="traceData.workshop1?.pressing">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #409EFF;">② 压花键</div>
                  <div class="detail-value">
                    <span>压力：{{ traceData.workshop1.pressing.pressure }} | </span>
                    <span>花键尺寸：{{ traceData.workshop1.pressing.splineSize }} | </span>
                    <span>设备编号：{{ traceData.workshop1.pressing.equipmentNo }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 锻造工序 -->
              <el-col :span="24" v-if="traceData.workshop1?.forging">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #409EFF;">③ 锻造</div>
                  <div class="detail-value">
                    <span>锻造温度：{{ traceData.workshop1.forging.forgingTemp }} | </span>
                    <span>压力：{{ traceData.workshop1.forging.pressure }} | </span>
                    <span>保压时间：{{ traceData.workshop1.forging.holdTime }} | </span>
                    <span>缺陷：<el-tag :type="traceData.workshop1.forging.defect === '无' ? 'success' : 'danger'" size="mini">{{ traceData.workshop1.forging.defect }}</el-tag></span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 二车间（热处理） -->
          <div class="trace-section">
            <div class="section-header">
              <div class="section-title">
                <i class="el-icon-sunny section-icon"></i>
                <span class="title-text">二车间（热处理）</span>
              </div>
              <div class="section-status">
                <el-tag :type="getWorkshopStatus(traceData.workshop2)" size="mini">
                  {{ traceData.workshop2?.status || '--' }}
                </el-tag>
              </div>
            </div>
            <el-row :gutter="20" class="section-content">
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">热处理日期</div>
                  <div class="detail-value">{{ traceData.workshop2?.processDate || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">工艺类型</div>
                  <div class="detail-value">{{ traceData.workshop2?.processType || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">操作员</div>
                  <div class="detail-value">{{ traceData.workshop2?.operator || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">温度曲线</div>
                  <div class="detail-value">
                    <span v-if="traceData.workshop2?.temperatureCurve">
                      升温：{{ traceData.workshop2.temperatureCurve.heating || '--' }}℃，
                      保温：{{ traceData.workshop2.temperatureCurve.holding || '--' }}℃
                    </span>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">硬度检测</div>
                  <div class="detail-value">
                    <span v-if="traceData.workshop2?.hardnessTest">
                      {{ traceData.workshop2.hardnessTest.value || '--' }} HRC
                    </span>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <!-- 钻中心孔工序 -->
              <el-col :span="24" v-if="traceData.workshop2?.drilling">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #67C23A;">① 钻中心孔</div>
                  <div class="detail-value">
                    <span>孔尺寸：{{ traceData.workshop2.drilling.holeSize }} | </span>
                    <span>孔深：{{ traceData.workshop2.drilling.holeDepth }} | </span>
                    <span>设备转速：{{ traceData.workshop2.drilling.equipmentSpeed }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 调质热处理工序 -->
              <el-col :span="24" v-if="traceData.workshop2?.heatTreatment">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #67C23A;">② 调质热处理</div>
                  <div class="detail-value">
                    <span>加热温度：{{ traceData.workshop2.heatTreatment.heatingTemp }} | </span>
                    <span>保温时间：{{ traceData.workshop2.heatTreatment.holdTime }} | </span>
                    <span>冷却速率：{{ traceData.workshop2.heatTreatment.coolingRate }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 粗车盘工序 -->
              <el-col :span="24" v-if="traceData.workshop2?.turning">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #67C23A;">③ 粗车盘</div>
                  <div class="detail-value">
                    <span>转速：{{ traceData.workshop2.turning.rotationSpeed }} | </span>
                    <span>进给速度：{{ traceData.workshop2.turning.feedRate }} | </span>
                    <span>公差：{{ traceData.workshop2.turning.tolerance }} | </span>
                    <span>刀具型号：{{ traceData.workshop2.turning.toolType }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 加工齿工序 -->
              <el-col :span="24" v-if="traceData.workshop2?.gear">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #67C23A;">④ 加工齿</div>
                  <div class="detail-value">
                    <span>齿轮精度：{{ traceData.workshop2.gear.gearAccuracy }} | </span>
                    <span>表面硬度：{{ traceData.workshop2.gear.surfaceHardness }} | </span>
                    <span>设备编号：{{ traceData.workshop2.gear.equipmentNo }}</span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 三车间（机加工） -->
          <div class="trace-section">
            <div class="section-header">
              <div class="section-title">
                <i class="el-icon-s-operation section-icon"></i>
                <span class="title-text">三车间（机加工）</span>
              </div>
              <div class="section-status">
                <el-tag :type="getWorkshopStatus(traceData.workshop3)" size="mini">
                  {{ traceData.workshop3?.status || '--' }}
                </el-tag>
              </div>
            </div>
            <el-row :gutter="20" class="section-content">
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">加工日期</div>
                  <div class="detail-value">{{ traceData.workshop3?.processDate || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">机床编号</div>
                  <div class="detail-value">{{ traceData.workshop3?.machineNo || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">操作员</div>
                  <div class="detail-value">{{ traceData.workshop3?.operator || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">加工尺寸</div>
                  <div class="detail-value">
                    <span v-if="traceData.workshop3?.dimensions">
                      长度：{{ traceData.workshop3.dimensions.length || '--' }}mm，
                      直径：{{ traceData.workshop3.dimensions.diameter || '--' }}mm
                    </span>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">精度检测</div>
                  <div class="detail-value">
                    <el-tag 
                      v-if="traceData.workshop3?.precisionCheck"
                      :type="traceData.workshop3.precisionCheck.result === '合格' ? 'success' : 'danger'"
                      size="mini"
                    >
                      {{ traceData.workshop3.precisionCheck.result || '--' }}
                    </el-tag>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <!-- 淬火工序 -->
              <el-col :span="24" v-if="traceData.workshop3?.quenching">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #E6A23C;">① 淬火</div>
                  <div class="detail-value">
                    <span>淬火温度：{{ traceData.workshop3.quenching.quenchingTemp }} | </span>
                    <span>冷却介质：{{ traceData.workshop3.quenching.coolingMedium }} | </span>
                    <span>硬度：{{ traceData.workshop3.quenching.hardness }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 校直工序 -->
              <el-col :span="24" v-if="traceData.workshop3?.straightening">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #E6A23C;">② 校直</div>
                  <div class="detail-value">
                    <span>校直力：{{ traceData.workshop3.straightening.straighteningForce }} | </span>
                    <span>回火温度：{{ traceData.workshop3.straightening.temperingTemp }} | </span>
                    <span>保温时间：{{ traceData.workshop3.straightening.holdTime }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 检验工序 -->
              <el-col :span="24" v-if="traceData.workshop3?.inspection">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #E6A23C;">③ 检验</div>
                  <div class="detail-value">
                    <span>缺陷等级：<el-tag :type="traceData.workshop3.inspection.defectLevel === '无' ? 'success' : 'danger'" size="mini">{{ traceData.workshop3.inspection.defectLevel }}</el-tag> | </span>
                    <span>缺陷位置：{{ traceData.workshop3.inspection.defectPosition }} | </span>
                    <span>检验员：{{ traceData.workshop3.inspection.inspector }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 精车孔工序 -->
              <el-col :span="24" v-if="traceData.workshop3?.fineTurning">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #E6A23C;">④ 精车孔</div>
                  <div class="detail-value">
                    <span>公差：{{ traceData.workshop3.fineTurning.tolerance }} | </span>
                    <span>孔精度：{{ traceData.workshop3.fineTurning.holeAccuracy }} | </span>
                    <span>设备编号：{{ traceData.workshop3.fineTurning.equipmentNo }}</span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 四车间（装配） -->
          <div class="trace-section">
            <div class="section-header">
              <div class="section-title">
                <i class="el-icon-s-tools section-icon"></i>
                <span class="title-text">四车间（装配）</span>
              </div>
              <div class="section-status">
                <el-tag :type="getWorkshopStatus(traceData.workshop4)" size="mini">
                  {{ traceData.workshop4?.status || '--' }}
                </el-tag>
              </div>
            </div>
            <el-row :gutter="20" class="section-content">
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">装配日期</div>
                  <div class="detail-value">{{ traceData.workshop4?.assemblyDate || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">装配员</div>
                  <div class="detail-value">{{ traceData.workshop4?.assembler || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="detail-item">
                  <div class="detail-label">工位编号</div>
                  <div class="detail-value">{{ traceData.workshop4?.workstationNo || '--' }}</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">配件清单</div>
                  <div class="detail-value">
                    <span v-if="traceData.workshop4?.partsList?.length">
                      {{ traceData.workshop4.partsList.join(', ') }}
                    </span>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <div class="detail-label">装配检测</div>
                  <div class="detail-value">
                    <el-tag 
                      v-if="traceData.workshop4?.assemblyCheck"
                      :type="traceData.workshop4.assemblyCheck.result === '合格' ? 'success' : 'danger'"
                      size="mini"
                    >
                      {{ traceData.workshop4.assemblyCheck.result || '--' }}
                    </el-tag>
                    <span v-else>--</span>
                  </div>
                </div>
              </el-col>
              <!-- 喷漆工序 -->
              <el-col :span="24" v-if="traceData.workshop4?.painting">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #F56C6C;">① 喷漆</div>
                  <div class="detail-value">
                    <span>漆面厚度：{{ traceData.workshop4.painting.paintThickness }} | </span>
                    <span>喷涂压力：{{ traceData.workshop4.painting.sprayPressure }} | </span>
                    <span>油漆批次：{{ traceData.workshop4.painting.paintBatch }}</span>
                  </div>
                </div>
              </el-col>
              <!-- 包装工序 -->
              <el-col :span="24" v-if="traceData.workshop4?.packing">
                <div class="detail-item full-width">
                  <div class="detail-label" style="font-weight: bold; color: #F56C6C;">② 包装</div>
                  <div class="detail-value">
                    <span>包装时间：{{ traceData.workshop4.packing.packTime }} | </span>
                    <span>包装员：{{ traceData.workshop4.packing.packOperator }}</span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- AI认证 -->
          <div class="trace-section ai-section">
            <div class="section-header">
              <div class="section-title">
                <i class="el-icon-cpu section-icon"></i>
                <span class="title-text">AI智能认证</span>
              </div>
              <div class="section-status">
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
                  <div class="detail-label">认证分数</div>
                  <div class="detail-value">
                    <span v-if="traceData.aiCertification?.certificationScore">
                      {{ traceData.aiCertification.certificationScore }} 分
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
        </div>
        <div class="blockchain-content">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="blockchain-item">
                <div class="blockchain-label">区块哈希</div>
                <div class="blockchain-value hash-value">{{ traceData.blockchainInfo?.blockHash }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="blockchain-item">
                <div class="blockchain-label">交易ID</div>
                <div class="blockchain-value">{{ traceData.blockchainInfo?.transactionId }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="blockchain-item">
                <div class="blockchain-label">上链时间</div>
                <div class="blockchain-value">{{ traceData.blockchainInfo?.onChainTime }}</div>
              </div>
            </el-col>
          </el-row>
          <div class="blockchain-qr" v-if="traceData.blockchainInfo?.blockHash">
            <div class="qr-title">区块链验证二维码</div>
            <div class="qr-code-placeholder">
              <!-- 这里可以放置二维码生成组件 -->
              <div class="qr-code">
                <i class="el-icon-picture-outline"></i>
                <span>二维码区域</span>
              </div>
              <p class="qr-tip">扫描二维码验证区块链存证信息</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- AI分析结果 -->
      <AIAnalysisSection 
        v-if="showAIAnalysis" 
        :analysisResult="aiAnalysisResult"
      />
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="showEmpty">
      <div class="empty-content">
        <i class="el-icon-search empty-icon"></i>
        <h3 class="empty-title">请输入半轴编码进行查询</h3>
        <p class="empty-description">
          请输入完整的半轴编码，系统将查询该半轴从原料到成品的全流程溯源信息
        </p>
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
import AIAnalysisSection from './AIAnalysisSection.vue'

export default {
  name: 'TraceQuery',
  components: {
    AIAnalysisSection
  },
  data() {
    return {
      // 查询表单
      queryForm: {
        shaftCode: ''
      },
      // 查询规则
      queryRules: {
        shaftCode: [
          { required: true, message: '请输入半轴编码', trigger: 'blur' }
        ]
      },
      // 查询状态
      queryLoading: false,
      loading: false,
      exportLoading: false,
      aiAnalysisLoading: false,
      // 查询时间
      queryTime: '',
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
      // 示例编码
      exampleCodes: [
        'BZ202312001',
        'BZ202312002',
        'BZ202401001',
        'BZ202401002'
      ],
      // 显示控制
      showResult: false,
      showEmpty: true,
      showAIAnalysis: false,
      // AI分析结果
      aiAnalysisResult: null
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
  },
  methods: {
    // 查询处理
    handleQuery() {
      this.$refs.queryFormRef.validate(async (valid) => {
        if (!valid) {
          return this.$message.warning('请输入有效的半轴编码')
        }

        try {
          this.queryLoading = true
          this.loading = true
          
          // 这里调用后端API查询溯源信息
          const response = await this.queryTraceData(this.queryForm.shaftCode)
          
          if (response && response.success) {
            this.traceData = response.data
            this.queryTime = new Date().toLocaleString('zh-CN')
            this.showResult = true
            this.showEmpty = false
            this.$message.success('查询成功！')
          } else {
            this.$message.error(response?.message || '查询失败，请检查编码是否正确')
            this.showResult = false
            this.showEmpty = true
          }
        } catch (error) {
          console.error('查询失败:', error)
          this.$message.error('查询请求失败，请稍后重试')
          this.showResult = false
          this.showEmpty = true
        } finally {
          this.queryLoading = false
          this.loading = false
        }
      })
    },

    // 调用后端API查询溯源信息
    async queryTraceData(shaftCode) {
      try {
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
            message: response.data.message || '查询失败'
          }
        }
      } catch (error) {
        console.error('查询溯源信息失败:', error)
        return {
          success: false,
          message: error.response?.data?.message || '查询请求失败，请稍后重试'
        }
      }
    },
        
    // 转换后端数据为前端展示格式
    transformTraceData(shaftCode, backendData) {
      return {
        shaftCode,
        productModel: backendData.productModel || shaftCode.substring(0, 2) + '-' + shaftCode.substring(2, 6),
        batchNumber: backendData.materialBatch || '未知',
        productionDate: backendData.productionDate || '未知',
        factoryDate: backendData.updateTime || '未知',
        qualityStatus: this.getQualityStatus(backendData.defectLevel, backendData.status),
        isOnChain: true,
            
        // 原料产地数据
        rawMaterial: backendData.materialBatch ? {
          supplier: '原材料供应商',
          materialType: '40Cr合金钢',
          origin: '中国',
          purchaseDate: backendData.productionDate || '未知',
          batchNo: backendData.materialBatch,
          status: '合格',
          reportUrl: null
        } : null,
            
        // 一车间（锻造）
        workshop1: backendData.workshop1 ? {
          processDate: backendData.productionDate || '未知',
          // 切割下料数据
          cutting: backendData.workshop1.cutting ? {
            materialBatch: backendData.workshop1.cutting.materialBatch || '--',
            cutSize: backendData.workshop1.cutting.cutSize || '--',
            cutSpeed: backendData.workshop1.cutting.cutSpeed || '--',
            operator: backendData.workshop1.cutting.operator || '--'
          } : null,
          // 压花键数据
          pressing: backendData.workshop1.pressing ? {
            pressure: backendData.workshop1.pressing.pressure || '--',
            splineSize: backendData.workshop1.pressing.splineSize || '--',
            equipmentNo: backendData.workshop1.pressing.equipmentNo || '--'
          } : null,
          // 锻造数据
          forging: backendData.workshop1.forging ? {
            forgingTemp: backendData.workshop1.forging.forgingTemp || '--',
            pressure: backendData.workshop1.forging.pressure || '--',
            holdTime: backendData.workshop1.forging.holdTime || '--',
            defect: backendData.workshop1.forging.defect || '--'
          } : null,
          operator: backendData.workshop1.cutting?.operator || backendData.workshop1.forging?.operator || '系统',
          equipmentNo: backendData.workshop1.pressing?.equipmentNo || 'AUTO-001',
          parameters: {
            temperature: backendData.workshop1.forging?.forgingTemp || '1150',
            pressure: backendData.workshop1.forging?.pressure || '850'
          },
          qualityCheck: {
            result: backendData.workshop1.forging?.defect === '无' ? '合格' : (backendData.status || '未知'),
            inspector: '系统'
          },
          status: '已完成'
        } : null,
            
        // 二车间（热处理）
        workshop2: backendData.workshop2 ? {
          processDate: backendData.productionDate || '未知',
          // 钻中心孔数据
          drilling: backendData.workshop2.drilling ? {
            holeSize: backendData.workshop2.drilling.holeSize || '--',
            holeDepth: backendData.workshop2.drilling.holeDepth || '--',
            equipmentSpeed: backendData.workshop2.drilling.equipmentSpeed || '--'
          } : null,
          // 调质热处理数据
          heatTreatment: backendData.workshop2.heatTreatment ? {
            heatingTemp: backendData.workshop2.heatTreatment.heatingTemp || '--',
            holdTime: backendData.workshop2.heatTreatment.holdTime || '--',
            coolingRate: backendData.workshop2.heatTreatment.coolingRate || '--'
          } : null,
          // 粗车盘数据
          turning: backendData.workshop2.turning ? {
            rotationSpeed: backendData.workshop2.turning.rotationSpeed || '--',
            feedRate: backendData.workshop2.turning.feedRate || '--',
            tolerance: backendData.workshop2.turning.tolerance || '--',
            toolType: backendData.workshop2.turning.toolType || '--'
          } : null,
          // 加工齿数据
          gear: backendData.workshop2.gear ? {
            gearAccuracy: backendData.workshop2.gear.gearAccuracy || '--',
            surfaceHardness: backendData.workshop2.gear.surfaceHardness || '--',
            equipmentNo: backendData.workshop2.gear.equipmentNo || '--'
          } : null,
          operator: '系统',
          processType: '淆火+回火',
          temperatureCurve: {
            heating: backendData.workshop2.heatTreatment?.heatingTemp || '850',
            holding: '400'
          },
          hardnessTest: {
            value: backendData.workshop2.gear?.surfaceHardness || '48',
            standard: '45-50 HRC'
          },
          status: '已完成'
        } : null,
            
        // 三车间（机加工）
        workshop3: backendData.workshop3 ? {
          processDate: backendData.productionDate || '未知',
          // 淬火数据
          quenching: backendData.workshop3.quenching ? {
            quenchingTemp: backendData.workshop3.quenching.quenchingTemp || '--',
            coolingMedium: backendData.workshop3.quenching.coolingMedium || '--',
            hardness: backendData.workshop3.quenching.hardness || '--'
          } : null,
          // 校直数据
          straightening: backendData.workshop3.straightening ? {
            straighteningForce: backendData.workshop3.straightening.straighteningForce || '--',
            temperingTemp: backendData.workshop3.straightening.temperingTemp || '--',
            holdTime: backendData.workshop3.straightening.holdTime || '--'
          } : null,
          // 检验数据
          inspection: backendData.workshop3.inspection ? {
            defectLevel: backendData.workshop3.inspection.defectLevel || '--',
            defectPosition: backendData.workshop3.inspection.defectPosition || '--',
            inspector: backendData.workshop3.inspection.inspector || '--'
          } : null,
          // 精车孔数据
          fineTurning: backendData.workshop3.fineTurning ? {
            tolerance: backendData.workshop3.fineTurning.tolerance || '--',
            holeAccuracy: backendData.workshop3.fineTurning.holeAccuracy || '--',
            equipmentNo: backendData.workshop3.fineTurning.equipmentNo || '--'
          } : null,
          operator: '系统',
          machineNo: backendData.workshop3.fineTurning?.equipmentNo || 'CNC-003',
          dimensions: {
            length: '850',
            diameter: '45'
          },
          precisionCheck: {
            result: backendData.workshop3.inspection?.defectLevel === '无' ? '合格' : '未知',
            tolerance: '齿轮精度: ' + (backendData.gearAccuracy || '--')
          },
          status: '已完成'
        } : null,
            
        // 四车间（装配）
        workshop4: backendData.workshop4 ? {
          assemblyDate: backendData.productionDate || '未知',
          // 喷漆数据
          painting: backendData.workshop4.painting ? {
            paintThickness: backendData.workshop4.painting.paintThickness || '--',
            sprayPressure: backendData.workshop4.painting.sprayPressure || '--',
            paintBatch: backendData.workshop4.painting.paintBatch || '--'
          } : null,
          // 包装数据
          packing: backendData.workshop4.packing ? {
            packTime: backendData.workshop4.packing.packTime || '--',
            packOperator: backendData.workshop4.packing.packOperator || '--'
          } : null,
          assembler: backendData.workshop4.packing?.packOperator || '系统',
          workstationNo: 'ASSEM-001',
          partsList: ['油漆处理'],
          assemblyCheck: {
            result: backendData.status || '未知',
            inspector: '油漆厚度: ' + (backendData.workshop4.painting?.paintThickness || backendData.paintThickness || '--')
          },
          status: '已完成'
        } : null,
            
        // AI认证
        aiCertification: {
          certificationTime: backendData.updateTime || '未知',
          aiModelVersion: 'V2.1.0',
          certificationScore: this.calculateScore(backendData.defectLevel),
          certificationStatus: backendData.defectLevel === 0 ? '认证通过' : '存在缺陷',
          reportSummary: `该半轴缺陷等级为${backendData.defectLevel}，状态为${backendData.status || '未知'}。`,
          certificationDetails: [
            { item: '缺陷检测', result: backendData.defectLevel === 0 ? '通过' : '不通过' },
            { item: '材料检验', result: backendData.materialBatch ? '通过' : '未检测' },
            { item: '齿轮精度', result: backendData.gearAccuracy ? '通过' : '未检测' },
            { item: '油漆质量', result: backendData.paintThickness ? '通过' : '未检测' }
          ]
        },
            
        // 区块链信息
        blockchainInfo: {
          blockHash: this.generateBlockHash(shaftCode),
          transactionId: 'TX' + new Date().getTime(),
          onChainTime: backendData.updateTime || new Date().toLocaleString('zh-CN')
        }
      }
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
      if (defectLevel === 0) return 100
      if (defectLevel === 1) return 90
      if (defectLevel === 2) return 75
      if (defectLevel === 3) return 60
      return 50
    },
        
    // 生成区块哈希（模拟）
    generateBlockHash(shaftCode) {
      const hash = shaftCode.split('').reduce((acc, char) => {
        return acc + char.charCodeAt(0).toString(16)
      }, '0x')
      return hash + '0'.repeat(Math.max(0, 66 - hash.length))
    },

    // 获取车间状态标签类型
    getWorkshopStatus(workshop) {
      if (!workshop) return 'info'
      return workshop.status === '已完成' ? 'success' : 
             workshop.status === '进行中' ? 'warning' : 
             workshop.status === '异常' ? 'danger' : 'info'
    },

    // 填充示例编码
    fillExample(code) {
      this.queryForm.shaftCode = code
    },

    // 刷新页面
    refreshPage() {
      this.$refs.queryFormRef.resetFields()
      this.showResult = false
      this.showEmpty = true
      this.traceData = {
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

    // 导出报告
    exportReport() {
      this.exportLoading = true
      this.$message.success('正在生成报告...')
      // 模拟导出延迟
      setTimeout(() => {
        this.exportLoading = false
        this.$message.success('报告导出成功！')
      }, 2000)
    },

    // 打印报告
    printReport() {
      this.$message.info('打印功能开发中...')
    },

    // 分享报告
    shareReport() {
      this.$message.info('分享功能开发中...')
    },

    // 查看报告
    viewReport(url) {
      this.$message.info(`打开报告：${url}`)
      // window.open(url, '_blank')
    },
    
    // AI分析处理
    async handleAIAnalysis() {
      if (!this.traceData || !this.traceData.shaftCode) {
        return this.$message.warning('请先查询产品溯源信息')
      }
      
      try {
        this.aiAnalysisLoading = true
        
        const response = await axios.get(`/api/trace/analyze/${this.traceData.shaftCode}`)
        
        if (response.data.code === 200 && response.data.data) {
          this.aiAnalysisResult = response.data.data
          this.showAIAnalysis = true
          
          this.$message.success('AI分析完成！')
          
          // 滚动到AI分析区域
          this.$nextTick(() => {
            const el = document.querySelector('.ai-analysis-section')
            if (el) {
              el.scrollIntoView({ behavior: 'smooth', block: 'start' })
            }
          })
        } else {
          this.$message.error(response.data.message || 'AI分析失败')
        }
      } catch (error) {
        console.error('AI分析失败:', error)
        this.$message.error('AI分析请求失败，请稍后重试')
      } finally {
        this.aiAnalysisLoading = false
      }
    }
  }
}
</script>

<style scoped>
.trace-query-container {
  min-height: calc(100vh - 128px);
  background: #ffffff;
  padding: 20px;
}

/* 查询卡片 */
.query-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.query-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
  color: #1890ff;
}

.header-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 查询表单 */
.code-input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.shaft-code-input {
  flex: 1;
}

.code-prefix {
  color: #1890ff;
  font-weight: 500;
}

.query-button {
  height: 40px;
  padding: 0 24px;
}

.input-tips {
  margin-top: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  border-left: 3px solid #1890ff;
}

.tip-item {
  margin: 4px 0;
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.tip-item i {
  color: #1890ff;
  margin-top: 2px;
}

/* 结果卡片 */
.result-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  animation: fadeIn 0.5s ease;
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
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.result-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.result-icon {
  font-size: 22px;
  color: #52c41a;
}

.title-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.result-tag {
  margin-left: 8px;
}

.result-actions {
  display: flex;
  gap: 8px;
}

/* 基本信息 */
.basic-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.info-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
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
}

.info-grid {
  margin: 0 !important;
}

.info-item {
  margin-bottom: 16px;
}

.info-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
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
}

/* 溯源详情 */
.trace-details {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.details-title {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.details-title i {
  color: #722ed1;
}

.trace-section {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  border-left: 4px solid #1890ff;
  transition: all 0.3s ease;
}

.trace-section:hover {
  background: #f5f5f5;
  transform: translateX(4px);
}

.ai-section {
  border-left-color: #722ed1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-icon {
  font-size: 18px;
  color: #1890ff;
}

.title-text {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.ai-section .section-icon {
  color: #722ed1;
}

.section-content {
  margin: 0 !important;
}

.detail-item {
  margin-bottom: 12px;
}

.full-width {
  width: 100%;
}

.detail-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.detail-value {
  font-size: 14px;
  color: #303133;
  word-break: break-all;
}

.report-summary {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.ai-details {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.ai-tag {
  font-size: 12px;
  padding: 4px 8px;
}

/* 区块链信息 */
.blockchain-info-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e0f7e0;
}

.blockchain-header {
  margin-bottom: 20px;
}

.blockchain-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #52c41a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.blockchain-content {
  padding: 16px;
  background: #f0f9f0;
  border-radius: 6px;
}

.blockchain-item {
  margin-bottom: 16px;
}

.blockchain-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.blockchain-value {
  font-size: 14px;
  color: #52c41a;
  word-break: break-all;
  font-family: monospace;
}

.hash-value {
  font-size: 12px;
  color: #389e0d;
}

.blockchain-qr {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
  text-align: center;
}

.qr-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.qr-code-placeholder {
  display: inline-block;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.qr-code {
  width: 120px;
  height: 120px;
  background: #e8e8e8;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  margin-bottom: 8px;
}

.qr-code i {
  font-size: 32px;
  margin-bottom: 8px;
}

.qr-tip {
  font-size: 12px;
  color: #999;
  margin: 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 40px 20px;
  text-align: center;
  background: #ffffff;
}

.empty-content {
  max-width: 500px;
}

.empty-icon {
  font-size: 64px;
  color: #dcdfe6;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 20px;
  color: #303133;
  margin: 0 0 12px 0;
}

.empty-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 24px;
}

.empty-examples {
  text-align: left;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.empty-examples p {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
}

.example-codes {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.example-tag {
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  color: #1890ff;
}

.example-tag:hover {
  background: #d9ecff;
  transform: translateY(-2px);
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
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
}

/* 响应式调整 */
@media (max-width: 768px) {
  .trace-query-container {
    padding: 16px;
  }
  
  .query-card,
  .result-card {
    padding: 16px;
  }
  
  .code-input-wrapper {
    flex-direction: column;
  }
  
  .query-button {
    width: 100%;
  }
  
  .result-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .result-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .info-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .blockchain-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .el-col {
    margin-bottom: 12px;
  }
}
</style>