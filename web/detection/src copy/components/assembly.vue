<template>
  <div class="webase-redirect-container">
    <div class="redirect-content">
      <div class="redirect-icon">
        <i class="el-icon-loading"></i>
      </div>
      <h2>正在打开WeBase区块链管理平台...</h2>
      <p class="redirect-tip">页面将在新窗口中自动打开</p>
      <div class="redirect-info">
        <el-alert type="info" :closable="false">
          <template slot="title">
            <i class="el-icon-info"></i>
            如果浏览器阻止了弹窗,请允许弹窗或手动点击下方按钮
          </template>
        </el-alert>
      </div>
      <el-button 
        type="primary" 
        size="large"
        icon="el-icon-full-screen"
        @click="openWeBase"
        class="manual-open-btn"
      >
        手动打开WeBase
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AssemblyManagement',
  data() {
    return {
      webaseUrl: 'http://192.168.1.106:5000/',
      popupBlocked: false
    }
  },
  mounted() {
    // 页面加载后自动打开WeBase
    this.openWeBase();
  },
  methods: {
    openWeBase() {
      const newWindow = window.open('http://192.168.1.106:5000/', '_blank');
      
      // 检测是否被浏览器拦截
      if (!newWindow || newWindow.closed || typeof newWindow.closed === 'undefined') {
        this.popupBlocked = true;
        this.$message.warning('浏览器阻止了弹窗,请允许弹窗或手动点击按钮打开');
      } else {
        this.$message.success('WeBase已在新窗口打开');
        // 可选:关闭当前页面或返回上一页
        // this.$router.go(-1);
      }
    }
  }
}
</script>

<style scoped>
.webase-redirect-container {
  width: 100%;
  height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.redirect-content {
  text-align: center;
  background: #ffffff;
  border-radius: 16px;
  padding: 60px 80px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  max-width: 600px;
}

.redirect-icon {
  margin-bottom: 30px;
}

.redirect-icon i {
  font-size: 80px;
  color: #1890ff;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.redirect-content h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 16px;
  font-weight: 600;
}

.redirect-tip {
  font-size: 16px;
  color: #606266;
  margin-bottom: 40px;
}

.redirect-info {
  margin-bottom: 30px;
}

.manual-open-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
}
</style>
