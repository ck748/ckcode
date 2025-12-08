<template>
  <div class="blockchain-layout">
    <!-- 左侧导航栏 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapse }" :style="{ width: asideWidth }">
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo-content">
          <div class="logo-icon">
            <i class="el-icon-connection blockchain-icon"></i>
          </div>
          <transition name="fade">
            <div v-show="!isCollapse" class="logo-info">
              <h2 class="logo-title">{{ title }}</h2>
              <p class="logo-subtitle">区块链半轴溯源</p>
            </div>
          </transition>
        </div>
      </div>

      <!-- 导航菜单 -->
      <div class="nav-menu">
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          :collapse-transition="false"
          background-color="#0a1028"
          text-color="#b0bec5"
          active-text-color="#1890ff"
          class="blockchain-menu"
          router
        >
          <!-- 主要功能 -->
          <el-menu-item index="/daping" class="menu-item">
            <i class="el-icon-data-line menu-icon"></i>
            <span slot="title" class="menu-text">区块链溯源大屏</span>
          </el-menu-item>

          <el-menu-item index="/dashboard" class="menu-item">
            <i class="el-icon-monitor menu-icon"></i>
            <span slot="title" class="menu-text">生产监控</span>
          </el-menu-item>

          <!-- 溯源管理 -->
          <el-submenu index="trace">
            <template slot="title">
              <i class="el-icon-document menu-icon"></i>
              <span class="menu-text">溯源管理</span>
            </template>
            <el-menu-item index="/info" class="submenu-item">
              <i class="el-icon-tickets submenu-icon"></i>
              <span>历史检测</span>
            </el-menu-item>
            <el-menu-item index="/annotation" class="submenu-item">
              <i class="el-icon-edit-outline submenu-icon"></i>
              <span>数据标注</span>
            </el-menu-item>
            <el-menu-item index="/charts" class="submenu-item">
              <i class="el-icon-data-analysis submenu-icon"></i>
              <span>检测报表</span>
            </el-menu-item>
            <el-menu-item index="/warning" class="submenu-item">
              <i class="el-icon-warning submenu-icon"></i>
              <span>预警信息</span>
            </el-menu-item>
          </el-submenu>

          <!-- 链上管理 -->
          <el-submenu index="chain">
            <template slot="title">
              <i class="el-icon-connection menu-icon"></i>
              <span class="menu-text">双链管理</span>
            </template>
            <el-menu-item index="/production" class="submenu-item">
              <i class="el-icon-cpu submenu-icon"></i>
              <span>私有链管理</span>
            </el-menu-item>
            <el-menu-item index="/assembly" class="submenu-item">
              <i class="el-icon-set-up submenu-icon"></i>
              <span>联盟链管理</span>
            </el-menu-item>
          </el-submenu>

          <!-- 系统管理 -->
          <el-submenu index="system">
            <template slot="title">
              <i class="el-icon-setting menu-icon"></i>
              <span class="menu-text">系统管理</span>
            </template>
            <el-menu-item index="/log" class="submenu-item">
              <i class="el-icon-notebook-2 submenu-icon"></i>
              <span>日志管理</span>
            </el-menu-item>
            <el-menu-item index="/apimanager" class="submenu-item">
              <i class="el-icon-key submenu-icon"></i>
              <span>API管理</span>
            </el-menu-item>
            <el-menu-item index="/pwdmanager" class="submenu-item">
              <i class="el-icon-lock submenu-icon"></i>
              <span>密钥管理</span>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </div>

      <!-- 收缩按钮 -->
      <div class="collapse-btn" @click="toggleCollapse">
        <i :class="collapseIcon" class="collapse-icon"></i>
        <transition name="fade">
          <span v-show="!isCollapse" class="collapse-text">
            {{ isCollapse ? '展开' : '收起' }}
          </span>
        </transition>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content" :style="{ marginLeft: asideWidth }">
      <!-- 顶部栏 -->
      <div class="header">
        <div class="header-left">
          <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/daping' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 实时状态 -->
          <div class="status-indicators">
            <div class="status-item" @click="refreshBlockchain">
              <i class="el-icon-refresh status-icon"></i>
              <span class="status-text">刷新</span>
            </div>
            <div class="status-item">
              <div class="blockchain-status" :class="blockchainStatus">
                <i class="el-icon-success"></i>
                区块链连接正常
              </div>
            </div>
            <div class="status-item">
              <el-tooltip content="全屏" placement="bottom">
                <i class="el-icon-full-screen fullscreen-icon" @click="toggleFullscreen"></i>
              </el-tooltip>
            </div>
          </div>

          <!-- 用户信息 -->
          <el-dropdown @command="handleUserCommand" class="user-dropdown">
            <div class="user-info">
              <el-avatar 
                :src="userAvatar" 
                :size="36"
                class="user-avatar"
              ></el-avatar>
              <div class="user-details">
                <span class="user-name">{{ userName }}</span>
                <span class="user-role">管理员</span>
              </div>
              <i class="el-icon-arrow-down dropdown-arrow"></i>
            </div>
            <el-dropdown-menu slot="dropdown" class="user-menu">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人信息
              </el-dropdown-item>
              <el-dropdown-item command="password">
                <i class="el-icon-lock"></i> 修改密码
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="content-container">
        <router-view></router-view>
      </div>

      <!-- 底部信息栏 -->
      <div class="footer">
        <div class="footer-info">
          <span class="info-item">
            <i class="el-icon-cpu"></i>
            系统版本: V1.2.0
          </span>
          <span class="info-item">
            <i class="el-icon-s-data"></i>
            数据更新: {{ lastUpdateTime }}
          </span>
          <span class="info-item">
            <i class="el-icon-cloudy"></i>
            服务器状态: 正常
          </span>
        </div>
        <div class="footer-right">
          <span class="copyright">© 2024 智轴链鉴系统 - 区块链半轴溯源平台</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BlockchainLayout',
  data() {
    return {
      isCollapse: false,
      asideWidth: '260px',
      collapseIcon: 'el-icon-s-fold',
      title: '智轴链鉴系统',
      blockchainStatus: 'normal',
      lastUpdateTime: '',
      user: JSON.parse(localStorage.getItem('useradmin') || '{}')
    }
  },
  computed: {
    currentRouteName() {
      return this.$route.meta?.title || '首页'
    },
    userName() {
      return this.user?.name || '管理员'
    },
    userAvatar() {
      return this.user?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  mounted() {
    this.updateTime()
    this.timeInterval = setInterval(this.updateTime, 60000) // 每分钟更新一次
    this.checkBlockchainStatus()
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
      this.asideWidth = this.isCollapse ? '64px' : '260px'
      this.collapseIcon = this.isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'
    },
    updateTime() {
      const now = new Date()
      this.lastUpdateTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    checkBlockchainStatus() {
      // 模拟检查区块链连接状态
      this.blockchainStatus = 'normal'
    },
    refreshBlockchain() {
      this.$message.success('正在刷新区块链数据...')
      // 这里调用刷新接口
    },
    toggleFullscreen() {
      if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen()
      } else {
        if (document.exitFullscreen) {
          document.exitFullscreen()
        }
      }
    },
    handleUserCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/person')
          break
        case 'password':
          this.$router.push('/password')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('useradmin')
        this.$router.push('/login')
        this.$message.success('退出成功')
      })
    }
  }
}
</script>

<style scoped>
/* 整体布局 */
.blockchain-layout {
  display: flex;
  min-height: 100vh;
  background: #0a1028;
  color: #ffffff;
  font-family: 'Microsoft YaHei', 'Segoe UI', sans-serif;
}

/* 左侧导航栏 */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  background: linear-gradient(180deg, #0c1c30 0%, #1a2b3c 100%);
  border-right: 1px solid rgba(24, 144, 255, 0.2);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.3);
}

/* Logo区域 */
.logo-section {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(10, 16, 40, 0.8);
}

.logo-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #1890ff, #722ed1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.logo-info {
  flex: 1;
  overflow: hidden;
}

.logo-title {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: #ffffff;
  line-height: 1.2;
}

.logo-subtitle {
  margin: 4px 0 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
}

.blockchain-menu {
  border: none;
  background: transparent !important;
}

.blockchain-menu .menu-item {
  height: 48px !important;
  line-height: 48px !important;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.blockchain-menu .menu-item:hover {
  background: rgba(24, 144, 255, 0.1) !important;
  transform: translateX(4px);
}

.blockchain-menu .menu-item.is-active {
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.2), rgba(114, 46, 209, 0.2)) !important;
  border: 1px solid rgba(24, 144, 255, 0.3);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.menu-icon {
  font-size: 18px;
  color: #1890ff;
  margin-right: 12px;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
}

/* 子菜单 */
.el-submenu__title {
  height: 48px !important;
  line-height: 48px !important;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-submenu__title:hover {
  background: rgba(24, 144, 255, 0.1) !important;
}

.el-submenu .el-menu {
  background: rgba(10, 16, 40, 0.6) !important;
  padding: 8px 0;
}

.submenu-item {
  height: 40px !important;
  line-height: 40px !important;
  margin: 2px 12px;
  border-radius: 6px;
  min-width: 0 !important;
  transition: all 0.3s ease;
}

.submenu-item:hover {
  background: rgba(24, 144, 255, 0.1) !important;
  padding-left: 20px !important;
}

.submenu-icon {
  font-size: 14px;
  margin-right: 8px;
  color: rgba(255, 255, 255, 0.6);
}

/* 收缩按钮 */
.collapse-btn {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: rgba(10, 16, 40, 0.8);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.collapse-btn:hover {
  background: rgba(24, 144, 255, 0.1);
}

.collapse-icon {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

.collapse-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.collapse-btn:hover .collapse-icon,
.collapse-btn:hover .collapse-text {
  color: #1890ff;
}

/* 主内容区 */
.main-content {
  flex: 1;
  min-height: 100vh;
  transition: margin-left 0.3s ease;
}

/* 顶部栏 */
.header {
  height: 64px;
  padding: 0 24px;
  background: rgba(10, 16, 40, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 999;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 面包屑 */
.breadcrumb ::v-deep .el-breadcrumb__inner {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.breadcrumb ::v-deep .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: #1890ff;
  font-weight: 600;
}

.breadcrumb ::v-deep .el-breadcrumb__separator {
  color: rgba(255, 255, 255, 0.4);
}

/* 状态指示器 */
.status-indicators {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.status-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.status-icon {
  font-size: 16px;
  color: #1890ff;
}

.status-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.blockchain-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.blockchain-status.normal {
  background: rgba(82, 196, 26, 0.2);
  color: #52c41a;
}

.fullscreen-icon {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.fullscreen-icon:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #1890ff;
}

/* 用户信息 */
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  border: 2px solid rgba(24, 144, 255, 0.3);
  transition: all 0.3s ease;
}

.user-info:hover .user-avatar {
  border-color: #1890ff;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.user-role {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  margin-top: 2px;
}

.dropdown-arrow {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
  color: #1890ff;
}

.user-menu {
  background: #1a2b3c !important;
  border: 1px solid rgba(24, 144, 255, 0.2) !important;
  border-radius: 8px !important;
}

.user-menu .el-dropdown-menu__item {
  color: rgba(255, 255, 255, 0.8) !important;
  transition: all 0.3s ease;
}

.user-menu .el-dropdown-menu__item:hover {
  background: rgba(24, 144, 255, 0.1) !important;
  color: #1890ff !important;
}

/* 内容容器 */
.content-container {
  padding: 20px;
  min-height: calc(100vh - 128px);
  background: #0a1028;
}

/* 底部信息栏 */
.footer {
  height: 64px;
  padding: 0 24px;
  background: rgba(10, 16, 40, 0.8);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.footer-info {
  display: flex;
  gap: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-item i {
  color: #1890ff;
}

.copyright {
  color: rgba(255, 255, 255, 0.4);
}

/* 动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar-collapsed {
    transform: translateX(0);
  }
  
  .main-content {
    margin-left: 0 !important;
  }
  
  .header {
    padding: 0 16px;
  }
  
  .status-text,
  .user-details {
    display: none;
  }
  
  .content-container {
    padding: 16px;
  }
  
  .footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    height: auto;
    padding: 12px 16px;
  }
}
</style>