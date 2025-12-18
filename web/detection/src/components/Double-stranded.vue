<template>
  <div class="blockchain-panel">
    <!-- 第一行：四个信息块 -->
    <div class="info-blocks">
      <div class="info-block card">
        <div class="block-icon primary">
          <i class="el-icon-link"></i>
        </div>
        <div class="block-content">
          <div class="block-title">区块链</div>
          <div class="block-subtitle">已部署区块链</div>
          <div class="transaction-item">私有链</div>
          <div class="transaction-item">联盟链</div>
        </div>
      </div>
      
      <div class="info-block card">
        <div class="block-icon success">
          <i class="el-icon-position"></i>
        </div>
        <div class="block-content">
          <div class="block-title">路由</div>
          <div class="block-subtitle">已接入路由</div>
          <div class="block-value">2条</div>
        </div>
      </div>
      
      <div class="info-block card">
        <div class="block-icon warning">
          <i class="el-icon-box"></i>
        </div>
        <div class="block-content">
          <div class="block-title">资源</div>
          <div class="block-subtitle">已有资源</div>
          <div class="block-value">8个</div>
        </div>
      </div>
      
      <div class="info-block card">
        <div class="block-icon danger">
          <i class="el-icon-refresh"></i>
        </div>
        <div class="block-content">
          <div class="block-title">事务支持</div>
          <div class="block-subtitle">支持事务形式</div>
          <div class="block-value">8个</div>
          <div class="transaction-list">
            <div class="transaction-item">两阶段事务</div>
            <div class="transaction-item">HTLC</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 本地系统信息 -->
    <div class="system-info card">
      <div class="section-header">
        <div class="header-content">
          <i class="el-icon-monitor icon-title"></i>
          <span class="section-title">本地系统信息</span>
        </div>
      </div>
      <div class="info-content">
        <table class="info-table">
          <tr>
            <td class="label">操作系统名称:</td>
            <td class="value">Ubuntu 64位</td>
          </tr>
          <tr>
            <td class="label">操作系统架构:</td>
            <td class="value">x86_64</td>
          </tr>
          <tr>
            <td class="label">操作系统版本:</td>
            <td class="value">10.16</td>
          </tr>
          <tr>
            <td class="label">JVM名称:</td>
            <td class="value">OpenJDK 64-Bit Server VM</td>
          </tr>
          <tr>
            <td class="label">JVM成员:</td>
            <td class="value">AdoptOpenJDK</td>
          </tr>
          <tr>
            <td class="label">JVM版本:</td>
            <td class="value">11.0.8+10</td>
          </tr>
          <tr>
            <td class="label">密码学组件名:</td>
            <td class="value">SUN</td>
          </tr>
          <tr>
            <td class="label">密码学组件版本:</td>
            <td class="value">11.0</td>
          </tr>
          <tr>
            <td class="label">已配置的隔离路径:</td>
            <td class="value path-value">secp256k1,x25519,secp256r1,secp384r1,secp521r1,x448,fifnk2048,fifnk3072,ifdnk4096,fifnk6144,fifnk6182</td>
          </tr>
          <tr>
            <td class="label">密码学组件详细信息:</td>
            <td class="value">
              SUN (DSA keyParameter generation; DSA signing; SHA-1, MD5 digits);<br>
              SecureRandom X.509 certificates; PKCS12_UKS & KOS keystores; PKIX<br>
              CertPathValidator; PKIX CertPathBuilder; LDAP Collection CertStores;<br>
              JavaPolicy Policy; JavaLoginConfig Configuration)
            </td>
          </tr>
        </table>
      </div>
    </div>

    <!-- 区块链信息 -->
    <div class="blockchain-info card">
      <div class="section-header">
        <div class="header-content">
          <i class="el-icon-data-line icon-title"></i>
          <span class="section-title">区块链信息</span>
        </div>
      </div>
      <div class="info-content">
        <el-table :data="blockchainData" style="width: 100%" stripe>
          <el-table-column prop="chainPath" label="链路径" />
          <el-table-column prop="chainType" label="链类型" />
          <el-table-column prop="blockHeight" label="区块高度" />
          <el-table-column label="资源详情">
            <template #default="scope">
              <el-button type="text" @click="viewDetails(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 本地路由信息 -->
    <div class="routing-info card">
      <div class="section-header">
        <div class="header-content">
          <i class="el-icon-guide icon-title"></i>
          <span class="section-title">本地路由信息</span>
        </div>
      </div>
      <div class="info-content">
        <table class="info-table">
          <tr>
            <td class="label">跨链路由版本:</td>
            <td class="value">v1.1.0</td>
          </tr>
          <tr>
            <td class="label">已加载的硬件:</td>
            <td class="value hardware-value">BOOS2.0,GAL_BOOS2.0,Fabric1.4</td>
          </tr>
          <tr>
            <td class="label">RPC接入配置:</td>
            <td class="value">127.0.0.1:8250</td>
          </tr>
          <tr>
            <td class="label">P2P接入配置:</td>
            <td class="value">0.0.0.0:25500</td>
          </tr>
          <tr>
            <td class="label">管理员账号:</td>
            <td class="value">org1-admin</td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BlockchainDashboard',
  data() {
    return {
      blockchainData: [
        {
          chainPath: 'payment.boos-group1',
          chainType: 'BOOS2.0',
          blockHeight: 62
        },
        {
          chainPath: 'payment.boos-group2',
          chainType: 'BOOS2.0',
          blockHeight: 85
        },
        {
          chainPath: 'payment.fabric-mychannel',
          chainType: 'Fabric1.4',
          blockHeight: 34
        },
        {
          chainPath: 'newment.boos-cnn',
          chainType: 'GAL BOOS5.0',
          blockHeight: 5
        }
      ]
    }
  },
  methods: {
    viewDetails(row) {
      console.log('查看详情:', row);
      // 这里可以添加查看详情的逻辑
    }
  }
}
</script>

<style scoped>
.blockchain-panel {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

/* 卡片样式 */
.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.card:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

/* 第一行：四个信息块 */
.info-blocks {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.info-block {
  display: flex;
  padding: 20px;
  align-items: center;
}

.block-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.block-icon:hover {
  transform: translateY(-3px);
}

.block-icon.primary {
  background: linear-gradient(135deg, #409eff, #2d8cf0);
}

.block-icon.success {
  background: linear-gradient(135deg, #67c23a, #5daf34);
}

.block-icon.warning {
  background: linear-gradient(135deg, #e6a23c, #d6942c);
}

.block-icon.danger {
  background: linear-gradient(135deg, #f56c6c, #e45757);
}

.block-icon i {
  font-size: 28px;
  color: white;
}

.block-content {
  flex: 1;
}

.block-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.block-subtitle {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.block-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 6px;
}

.transaction-list {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.transaction-item {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  position: relative;
  padding-left: 16px;
}

.transaction-item::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #409eff;
  font-size: 18px;
}

/* 通用信息区块样式 */
.system-info,
.blockchain-info,
.routing-info {
  margin-bottom: 20px;
}

.section-header {
  background: linear-gradient(to right, #ecf5ff, #f2f6fc);
  padding: 16px 20px;
  border-radius: 8px 8px 0 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
}

.header-content {
  display: flex;
  align-items: center;
}

.icon-title {
  font-size: 20px;
  margin-right: 10px;
  color: #409eff;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-badge {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  font-size: 14px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.info-content {
  padding: 0;
}

/* 信息表格样式 */
.info-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.info-table tr {
  border-bottom: 1px solid #f0f0f0;
}

.info-table tr:last-child {
  border-bottom: none;
}

.info-table td {
  padding: 14px 20px;
  vertical-align: top;
}

.info-table .label {
  width: 200px;
  background-color: #fafafa;
  color: #606266;
  font-weight: 500;
  border-right: 1px solid #f0f0f0;
}

.info-table .value {
  color: #303133;
  word-break: break-word;
  line-height: 1.6;
  padding-left: 20px;
}

.path-value, .hardware-value {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 12px;
  background-color: #f5f7fa;
  padding: 8px 12px;
  border-radius: 4px;
  margin: 4px 0;
}

/* 区块链表格样式 */
.blockchain-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.blockchain-table thead {
  background-color: #fafafa;
}

.blockchain-table th {
  padding: 14px 20px;
  text-align: left;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e8e8e8;
}

.blockchain-table td {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
  color: #303133;
}

.blockchain-table tbody tr:last-child td {
  border-bottom: none;
}

.blockchain-table tbody tr:hover {
  background-color: #f5f7fa;
}

.detail-link {
  color: #409eff;
  text-decoration: none;
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.detail-link:hover {
  background-color: #ecf5ff;
  text-decoration: none;
}

/* 路由信息样式调整 */
.routing-info .info-table td {
  padding: 12px 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .info-blocks {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .blockchain-panel {
    padding: 15px;
  }
  
  .info-blocks {
    grid-template-columns: 1fr;
  }
  
  .info-block {
    padding: 16px;
  }
  
  .block-icon {
    width: 50px;
    height: 50px;
  }
  
  .block-icon i {
    font-size: 24px;
  }
  
  .block-title {
    font-size: 16px;
  }
  
  .block-value {
    font-size: 22px;
  }
  
  .info-table .label {
    width: 150px;
  }
  
  .section-header {
    padding: 14px 16px;
  }
  
  .info-table td {
    padding: 12px 16px;
  }
}

@media (max-width: 480px) {
  .blockchain-panel {
    padding: 10px;
  }
  
  .info-table tr {
    display: block;
    padding: 10px 0;
  }
  
  .info-table .label {
    width: 100%;
    display: block;
    padding: 0 0 8px 16px;
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .info-table .value {
    display: block;
    padding: 8px 16px 0 16px;
  }
  
  .path-value, .hardware-value {
    padding: 6px 10px;
    font-size: 11px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>