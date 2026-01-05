<template>
  <div class="scroll-window">
    
    <!-- 引入 FontAwesome 图标库 -->
    <div style="display: none;">
      <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet" />
    </div>

    <div class="content-wrapper">
      
      <!-- Header -->
      <div class="header-section">
        <div>
          <h1 class="page-title">存证工作台</h1>
          <p class="page-subtitle">创建新的存证任务并管理上链流程</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-outline">
            <i class="fas fa-circle-question"></i> 使用帮助
          </button>
          <button class="btn btn-dark">
            <i class="fas fa-file-export"></i> 导出报告
          </button>
        </div>
      </div>

      <!-- ============================================== -->
      <!-- 0. 实时检测监控 (重点美化区域) -->
      <!-- ============================================== -->
      <div class="section-container">
        <div class="section-header-row">
          <h2 class="section-title">实时检测监控</h2>
          <div class="live-badge" :class="{ 'offline': !isConnected }">
            <span class="pulse-dot"></span> 
            {{ isConnected ? '信号连接正常' : '连接断开' }}
          </div>
        </div>
        
        <div class="monitor-dashboard-tech">
          <!-- 装饰性背景网格 -->
          <div class="tech-grid-bg"></div>
          
          <div class="monitor-frame">
            <!-- 顶部半透明信息条 -->
            <div class="hud-header">
              <div class="hud-left">
                <span class="status-light"></span>
                <span class="hud-text"><i class="fas fa-video"></i> CAM-01 / 生产线主视角</span>
              </div>
              <div class="hud-right">
                <span class="hud-text font-mono">{{ currentTime }}</span>
                <span class="hud-badge">REC</span>
              </div>
            </div>

            <!-- 视频内容区 -->
            <div class="video-viewport">
              <!-- 扫描线动画 -->
              <div class="scan-line"></div>

              <!-- 有图像时 -->
              <div v-if="imageData" class="image-wrapper">
                 <!-- 装饰：取景框四角 -->
                 <div class="corner c-tl"></div>
                 <div class="corner c-tr"></div>
                 <div class="corner c-bl"></div>
                 <div class="corner c-br"></div>

                 <img 
                  :src="'data:image/jpeg;base64,' + imageData" 
                  alt="实时监控" 
                  class="live-img"
                />
                
                <!-- 浮动标签 -->
                <div class="ai-tag">
                  <i class="fas fa-crosshairs"></i> AI DETECTING
                </div>
              </div>
              
              <!-- 无图像时 -->
              <div v-else class="no-signal-tech">
                <div class="signal-loader"></div>
                <p>WAITING FOR SIGNAL...</p>
                <button class="btn-tech-retry" @click="refreshConnection">RECONNECT</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ============================================== -->
      <!-- 1. 新建存证任务 (保持之前的科技卡片样式) -->
      <!-- ============================================== -->
      <div class="section-container">
        <h2 class="section-title">新建存证任务</h2>
        <div class="auto-task-grid">
          
          <!-- 卡片1：智能抓拍 -->
          <div class="task-card card-capture">
            <div class="card-bg-decoration"></div>
            <div class="card-content">
              <div class="icon-radar-container blue-theme">
                <div class="radar-wave"></div>
                <div class="radar-wave delay"></div>
                <div class="main-icon"><i class="fas fa-camera-retro"></i></div>
              </div>
              <div class="text-group">
                <h3>智能抓拍归档</h3>
                <p>AI视觉模型实时分析，检测到缺陷时自动触发高清抓拍并上传。</p>
              </div>
              <div class="status-bar blue-theme">
                <div class="status-icon"><i class="fas fa-bolt"></i></div>
                <span class="status-text">自动触发器已激活</span>
              </div>
            </div>
          </div>

          <!-- 卡片2：异常视频 -->
          <div class="task-card card-video">
            <div class="card-bg-decoration"></div>
            <div class="card-content">
              <div class="icon-radar-container purple-theme">
                <div class="radar-wave"></div>
                <div class="radar-wave delay"></div>
                <div class="main-icon"><i class="fas fa-video"></i></div>
              </div>
              <div class="text-group">
                <h3>异常视频回溯</h3>
                <p>自动缓存最近30秒视频流，异常发生时自动截取前后片段固证。</p>
              </div>
              <div class="status-bar purple-theme">
                <div class="status-icon"><i class="fas fa-circle-notch fa-spin"></i></div>
                <span class="status-text">视频流循环缓存中</span>
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- ============================================== -->
      <!-- 2. 关联元数据 (保持原样) -->
      <!-- ============================================== -->
      <div class="section-container">
        <h2 class="section-title">关联元数据</h2>
        <div class="meta-panel-original">
          <div class="form-row-original">
            <div class="form-group">
              <label>关联零件序列号</label>
              <div class="select-box">
                <span>SN002 - 变速箱齿轮</span>
                <i class="fas fa-chevron-down"></i>
              </div>
            </div>
            <div class="form-group">
              <label>采集工位</label>
              <div class="select-box">
                <span>热处理炉前检</span>
                <i class="fas fa-chevron-down"></i>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>数据说明</label>
            <div class="text-box-static">
              热处理后质量检查：表面无裂纹，硬度达标，尺寸符合图纸要求。设备编号：HT-2023-005，操作人员：张工。
            </div>
          </div>
          <button class="btn-magic-original">
            <i class="fas fa-wand-magic-sparkles"></i> 一键提取元数据
          </button>
        </div>
      </div>

      <!-- ============================================== -->
      <!-- 2.5 SHA-256哈希计算 (新增功能) -->
      <!-- ============================================== -->
      <div class="section-container">
        <h2 class="section-title">SHA-256 图片哈希计算</h2>
        <div class="hash-calc-panel">
          <div class="upload-area" v-if="!hashResult">
            <input 
              type="file" 
              id="hashFileInput" 
              accept="image/*" 
              @change="handleHashFileSelect"
              style="display: none;"
            >
            <label for="hashFileInput" class="upload-btn-large">
              <i class="fas fa-cloud-upload-alt"></i>
              <span>点击选择图片</span>
            </label>
            <p class="upload-tip">支持 JPG, PNG, JPEG 格式</p>
          </div>

          <!-- 计算中状态 -->
          <div v-if="isCalculating" class="calculating-box">
            <div class="loader"></div>
            <p>正在计算哈希值...</p>
          </div>

          <!-- 计算结果展示（带图片预览） -->
          <div v-if="hashResult" class="hash-result-box">
            <!-- 头部 -->
            <div class="result-header">
              <div class="header-left">
                <i class="fas fa-check-circle" style="color: #10B981;"></i>
                <span class="result-title">计算完成</span>
              </div>
              <button class="btn-reupload" @click="resetUpload">
                <i class="fas fa-redo"></i> 重新上传
              </button>
            </div>
            
            <!-- 图片预览 + 信息展示 -->
            <div class="result-layout">
              <!-- 左侧：图片预览 -->
              <div class="image-preview-box">
                <div class="preview-label">
                  <i class="fas fa-image"></i> 图片预览
                </div>
                <div class="preview-wrapper">
                  <img :src="uploadedImageUrl" alt="上传的图片" class="preview-image" />
                </div>
              </div>
              
              <!-- 右侧：哈希信息 -->
              <div class="result-info">
                <div class="result-item">
                  <label><i class="fas fa-file-image"></i> 文件名</label>
                  <span>{{ hashResult.fileName }}</span>
                </div>
                <div class="result-item">
                  <label><i class="fas fa-weight"></i> 文件大小</label>
                  <span>{{ formatFileSize(hashResult.fileSize) }}</span>
                </div>
                <div class="result-item">
                  <label><i class="fas fa-clock"></i> 上传时间</label>
                  <span>{{ formatTime(hashResult.uploadTime) }}</span>
                </div>
                <div class="result-item full-width">
                  <label><i class="fas fa-fingerprint"></i> SHA-256 哈希值</label>
                  <div class="hash-value-display">
                    <span class="hash-text">{{ hashResult.sha256 }}</span>
                    <button class="btn-copy" @click="copyHash" title="复制">
                      <i class="far fa-copy"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ============================================== -->
      <!-- 2.6 视频SHA-256哈希计算 (新增功能) -->
      <!-- ============================================== -->
      <div class="section-container">
        <h2 class="section-title">🎬 视频 SHA-256 哈希计算</h2>
        <div class="hash-calc-panel">
          <div class="upload-area" v-if="!videoHashResult">
            <input 
              type="file" 
              id="videoHashFileInput" 
              accept="video/*" 
              @change="handleVideoHashFileSelect"
              style="display: none;"
            >
            <label for="videoHashFileInput" class="upload-btn-large upload-btn-video">
              <i class="fas fa-film"></i>
              <span>点击选择视频</span>
            </label>
            <p class="upload-tip">支持 MP4, AVI, MOV, MKV 等格式（最大 500MB）</p>
          </div>

          <!-- 计算中状态 -->
          <div v-if="isCalculatingVideo" class="calculating-box">
            <div class="loader"></div>
            <p>正在计算视频哈希值...</p>
          </div>

          <!-- 计算结果展示（带视频预览） -->
          <div v-if="videoHashResult" class="hash-result-box">
            <!-- 头部 -->
            <div class="result-header">
              <div class="header-left">
                <i class="fas fa-check-circle" style="color: #10B981;"></i>
                <span class="result-title">计算完成</span>
              </div>
              <button class="btn-reupload" @click="resetVideoUpload">
                <i class="fas fa-redo"></i> 重新上传
              </button>
            </div>
            
            <!-- 视频预览 + 信息展示 -->
            <div class="result-layout">
              <!-- 左侧：视频预览 -->
              <div class="image-preview-box">
                <div class="preview-label">
                  <i class="fas fa-video"></i> 视频预览
                </div>
                <div class="preview-wrapper video-preview-wrapper">
                  <video :src="uploadedVideoUrl" controls class="preview-video"></video>
                </div>
              </div>
              
              <!-- 右侧：哈希信息 -->
              <div class="result-info">
                <div class="result-item">
                  <label><i class="fas fa-file-video"></i> 文件名</label>
                  <span>{{ videoHashResult.fileName }}</span>
                </div>
                <div class="result-item">
                  <label><i class="fas fa-weight"></i> 文件大小</label>
                  <span>{{ formatFileSize(videoHashResult.fileSize) }}</span>
                </div>
                <div class="result-item">
                  <label><i class="fas fa-clock"></i> 上传时间</label>
                  <span>{{ formatTime(videoHashResult.uploadTime) }}</span>
                </div>
                <div class="result-item full-width">
                  <label><i class="fas fa-fingerprint"></i> SHA-256 哈希值</label>
                  <div class="hash-value-display">
                    <span class="hash-text">{{ videoHashResult.sha256 }}</span>
                    <button class="btn-copy" @click="copyVideoHash" title="复制">
                      <i class="far fa-copy"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ============================================== -->
      <!-- 3. 链上凭证预览 (保持原样) -->
      <!-- ============================================== -->
      <div class="section-container">
        <h2 class="section-title">存证信息将生成如下链上凭证</h2>
        <div class="chain-panel-original">
          <div class="chain-header-original">
            <div class="chain-title-o">
              <i class="fas fa-link"></i> 区块链存证凭证
            </div>
            <div class="status-tag-o">
              <span class="dot"></span> 待上链
            </div>
          </div>
          <div class="hash-row-o">
            <label><i class="fas fa-fingerprint"></i> 数据指纹 (哈希值)</label>
            <div class="hash-val-o">
              <span>0x892f7d3e9c5a1b9e4f6g8h9i0j1k2l3m4n5o6p7q8r9s0t</span>
              <i class="far fa-copy"></i>
            </div>
          </div>
          <div class="info-grid-o">
            <div class="info-item-o">
              <label>存证时间</label>
              <span>2023-10-27 14:30:25</span>
            </div>
            <div class="info-item-o">
              <label>区块高度</label>
              <span class="font-mono">#124809</span>
            </div>
            <div class="info-item-o">
              <label>交易ID</label>
              <span class="link">0x741d...9b2e</span>
            </div>
            <div class="info-item-o">
              <label>存证节点</label>
              <span>工厂A-私有链节点1</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部栏 (保持原样) -->
      <div class="footer-bar-original">
        <div class="footer-l">
          <input type="checkbox" id="syncCheck" v-model="isSyncChecked">
          <label for="syncCheck">存证并同步至联盟链</label>
          <span class="desc">此凭证的哈希摘要将被同步给产业链联盟链，供下游客户验证</span>
        </div>
        <div class="footer-r">
          <button class="btn-c">取消</button>
          <button class="btn-s" :class="{ 'active': isSyncChecked }" :disabled="!isSyncChecked">
            开始存证
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import sseManager from '@/utils/sseManager';
import request from '@/utils/request';

// --- 实时监控逻辑 ---
const imageData = ref(null);
const isConnected = ref(false);
const currentTime = ref('');
let timer = null;

// --- SHA-256 哈希计算逻辑 ---
const isCalculating = ref(false);
const hashResult = ref(null);
const uploadedImageUrl = ref(null); // 存储图片预览URL

// --- 视频 SHA-256 哈希计算逻辑 ---
const isCalculatingVideo = ref(false);
const videoHashResult = ref(null);
const uploadedVideoUrl = ref(null); // 存储视频预览URL

const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleString('zh-CN', { hour12: false });
};

const handleSSEMessage = (type, data) => {
  if (type === 'connection') {
    isConnected.value = data.connected;
  } else if (type === 'message') {
    if (data.imgBase64) {
      imageData.value = data.imgBase64;
    }
  }
};

const refreshConnection = () => {
  sseManager.close();
  setTimeout(() => {
    sseManager.init();
  }, 500);
};

// --- SHA-256 功能方法（使用原生 fetch，避免 request 拦截器干扰） ---
const handleHashFileSelect = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 检查文件类型
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png'];
  if (!validTypes.includes(file.type)) {
    alert('请上传 JPG 或 PNG 格式的图片！');
    event.target.value = '';
    return;
  }

  // 检查文件大小 (10MB 限制)
  if (file.size > 10 * 1024 * 1024) {
    alert('图片大小不能超过 10MB！');
    event.target.value = '';
    return;
  }

  isCalculating.value = true;
  hashResult.value = null;
  uploadedImageUrl.value = null; // 清空之前的图片

  try {
    // 生成图片预览 URL
    uploadedImageUrl.value = URL.createObjectURL(file);
    
    // 构建 FormData
    const formData = new FormData();
    formData.append('image', file);

    console.log('📤 开始上传图片...', {
      fileName: file.name,
      fileSize: file.size,
      fileType: file.type
    });

    // 使用原生 fetch API 调用后端接口
    const response = await fetch('/api/annotation/upload/hash', {
      method: 'POST',
      body: formData,
      headers: {
        // 注意：不要设置 Content-Type，让浏览器自动设置 multipart/form-data 边界
      }
    });

    console.log('📥 收到响应:', {
      status: response.status,
      statusText: response.statusText,
      ok: response.ok
    });

    // 检查 HTTP 响应是否成功
    if (!response.ok) {
      throw new Error(`HTTP 错误: ${response.status} ${response.statusText}`);
    }

    // 解析 JSON 响应
    const result = await response.json();
    console.log('📦 解析结果:', result);

    if (result.code === 200) {
      hashResult.value = result.data;
      console.log('✅ 哈希计算成功:', result.data);
    } else {
      console.error('❌ 后端返回错误:', result);
      alert(`计算失败: ${result.message || '未知错误'}`);
    }
  } catch (error) {
    console.error('❌ 请求异常:', error);
    alert(`计算哈希失败: ${error.message}`);
  } finally {
    isCalculating.value = false;
    event.target.value = ''; // 清空 input
  }
};

const copyHash = () => {
  if (!hashResult.value || !hashResult.value.sha256) return;
  
  navigator.clipboard.writeText(hashResult.value.sha256)
    .then(() => {
      alert('哈希值已复制到剪贴板！');
    })
    .catch(() => {
      alert('复制失败，请手动复制。');
    });
};

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
};

const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return date.toLocaleString('zh-CN', { hour12: false });
};

// 重置上传（清空结果，重新显示上传按钮）
const resetUpload = () => {
  hashResult.value = null;
  uploadedImageUrl.value = null;
  isCalculating.value = false;
};

// --- 视频哈希计算功能方法 ---
const handleVideoHashFileSelect = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 检查文件类型（宽松验证，只要是 video 开头的 MIME 类型或常见视频扩展名即可）
  const fileName = file.name.toLowerCase();
  const validExtensions = ['.mp4', '.avi', '.mov', '.mkv', '.wmv', '.flv', '.webm', '.m4v'];
  const hasValidExtension = validExtensions.some(ext => fileName.endsWith(ext));
  const isVideoMimeType = file.type.startsWith('video/');
  
  if (!hasValidExtension && !isVideoMimeType) {
    alert('请上传视频文件（支持 MP4, AVI, MOV, MKV, WMV, FLV, WebM 等格式）！');
    event.target.value = '';
    return;
  }

  // 检查文件大小 (500MB 限制)
  if (file.size > 500 * 1024 * 1024) {
    alert('视频大小不能超过 500MB！');
    event.target.value = '';
    return;
  }

  isCalculatingVideo.value = true;
  videoHashResult.value = null;
  uploadedVideoUrl.value = null;

  try {
    // 生成视频预览 URL
    uploadedVideoUrl.value = URL.createObjectURL(file);
    
    // 构建 FormData
    const formData = new FormData();
    formData.append('video', file);

    console.log('📤 [DEBUG] 开始上传视频到后端...', {
      fileName: file.name,
      fileSize: file.size,
      fileType: file.type,
      url: '/api/annotation/upload/video/hash'
    });

    // 使用原生 fetch API 调用后端接口
    const response = await fetch('/api/annotation/upload/video/hash', {
      method: 'POST',
      body: formData,
      headers: {}
    });

    console.log('📥 收到响应:', {
      status: response.status,
      statusText: response.statusText,
      ok: response.ok
    });

    if (!response.ok) {
      throw new Error(`HTTP 错误: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    console.log('📦 解析结果:', result);

    if (result.code === 200) {
      videoHashResult.value = result.data;
      console.log('✅ 视频哈希计算成功:', result.data);
    } else {
      console.error('❌ 后端返回错误:', result);
      alert(`计算失败: ${result.message || '未知错误'}`);
    }
  } catch (error) {
    console.error('❌ 请求异常:', error);
    alert(`计算视频哈希失败: ${error.message}`);
  } finally {
    isCalculatingVideo.value = false;
    event.target.value = '';
  }
};

// 复制视频哈希值
const copyVideoHash = () => {
  if (!videoHashResult.value || !videoHashResult.value.sha256) return;
  
  navigator.clipboard.writeText(videoHashResult.value.sha256)
    .then(() => {
      alert('视频哈希值已复制到剪贴板！');
    })
    .catch(() => {
      alert('复制失败，请手动复制。');
    });
};

// 重置视频上传
const resetVideoUpload = () => {
  videoHashResult.value = null;
  uploadedVideoUrl.value = null;
  isCalculatingVideo.value = false;
};

// --- 逻辑简化 ---
const isSyncChecked = ref(false); 

// --- 生命周期 ---
onMounted(() => {
  sseManager.subscribe('dashboard', handleSSEMessage);
  updateTime();
  timer = setInterval(updateTime, 1000);
});

onUnmounted(() => {
  sseManager.unsubscribe('dashboard');
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
/* Reset */
* { box-sizing: border-box; margin: 0; padding: 0; outline: none; }
.hidden { display: none; }

.scroll-window {
  height: 100vh; width: 100%;
  background-color: #fff;
  overflow-y: scroll; 
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  color: #333;
}
.content-wrapper { max-width: 1200px; margin: 0 auto; padding: 30px; padding-bottom: 120px; }

/* Header */
.header-section { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 30px; }
.page-title { font-size: 24px; font-weight: 700; color: #1f2937; margin-bottom: 5px; }
.page-subtitle { font-size: 14px; color: #6b7280; }
.header-actions { display: flex; gap: 12px; }
.btn { padding: 8px 16px; border-radius: 6px; font-size: 14px; cursor: pointer; display: flex; align-items: center; gap: 8px; border: 1px solid transparent; transition: all 0.2s; }
.btn-outline { background: #fff; border-color: #e5e7eb; color: #374151; }
.btn-outline:hover { background: #f9fafb; }
.btn-dark { background: #1f2937; color: #fff; }
.btn-dark:hover { background: #374151; }

.section-container { margin-bottom: 35px; }
.section-header-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px; }
.section-title { font-size: 18px; font-weight: 700; color: #111827; }

/* ========================================= */
/*  实时监控美化 (Industrial Tech Style)      */
/* ========================================= */
.live-badge {
  display: flex; align-items: center; gap: 6px; font-size: 12px; color: #10B981;
  font-weight: 500; background: #ECFDF5; padding: 4px 10px; border-radius: 12px;
}
.live-badge.offline { color: #EF4444; background: #FEE2E2; }
.pulse-dot { width: 8px; height: 8px; background: currentColor; border-radius: 50%; animation: pulse 1.5s infinite; }
@keyframes pulse { 0% { opacity: 1; } 50% { opacity: 0.4; } 100% { opacity: 1; } }

/* 外层容器：深色渐变背景 */
.monitor-dashboard-tech {
  width: 100%; height: 320px;
  background: radial-gradient(circle at center, #1e293b 0%, #0f172a 100%); /* 深蓝灰渐变 */
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 25px -5px rgba(0,0,0,0.3);
  border: 1px solid #334155;
}

/* 装饰性网格背景 */
.tech-grid-bg {
  position: absolute; inset: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 20px 20px;
  pointer-events: none;
}

.monitor-frame {
  position: relative; width: 100%; height: 100%;
  display: flex; flex-direction: column;
}

/* 顶部 HUD 信息条 */
.hud-header {
  height: 40px; 
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  border-bottom: 1px solid rgba(255,255,255,0.05);
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 20px;
  color: #94a3b8; font-size: 12px; z-index: 10;
}
.hud-left { display: flex; align-items: center; gap: 10px; }
.status-light { width: 6px; height: 6px; background: #10B981; border-radius: 50%; box-shadow: 0 0 8px #10B981; }
.hud-right { display: flex; align-items: center; gap: 15px; }
.font-mono { font-family: 'Courier New', Courier, monospace; letter-spacing: 1px; }
.hud-badge { background: #ef4444; color: #fff; padding: 2px 6px; border-radius: 2px; font-size: 10px; font-weight: 700; letter-spacing: 0.5px; }

/* 视频视口 */
.video-viewport {
  flex: 1; position: relative;
  display: flex; align-items: center; justify-content: center;
  overflow: hidden;
}

/* 扫描线动画 */
.scan-line {
  position: absolute; top: 0; left: 0; width: 100%; height: 2px;
  background: rgba(16, 185, 129, 0.5);
  box-shadow: 0 0 10px rgba(16, 185, 129, 0.8);
  animation: scan 3s linear infinite;
  z-index: 5; opacity: 0.3; pointer-events: none;
}
@keyframes scan { 0% { top: 0%; } 100% { top: 100%; } }

.image-wrapper {
  height: 100%; position: relative;
  display: flex; justify-content: center; align-items: center;
}

/* 取景框四角装饰 */
.corner {
  position: absolute; width: 15px; height: 15px;
  border-color: rgba(255,255,255,0.3); border-style: solid; pointer-events: none;
}
.c-tl { top: 10px; left: 10px; border-width: 2px 0 0 2px; }
.c-tr { top: 10px; right: 10px; border-width: 2px 2px 0 0; }
.c-bl { bottom: 10px; left: 10px; border-width: 0 0 2px 2px; }
.c-br { bottom: 10px; right: 10px; border-width: 0 2px 2px 0; }

.live-img { height: 100%; width: auto; object-fit: contain; display: block; }

.ai-tag {
  position: absolute; bottom: 20px; left: 50%; transform: translateX(-50%);
  background: rgba(16, 185, 129, 0.15); border: 1px solid rgba(16, 185, 129, 0.4);
  color: #10B981; padding: 4px 12px; border-radius: 20px;
  font-size: 11px; font-weight: 600; letter-spacing: 1px;
  display: flex; align-items: center; gap: 6px;
  backdrop-filter: blur(2px);
}

/* 无信号状态美化 */
.no-signal-tech {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  color: #475569; gap: 12px;
}
.signal-loader {
  width: 40px; height: 40px; border: 2px solid #334155; border-top-color: #64748b;
  border-radius: 50%; animation: spin 1s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.no-signal-tech p { font-family: monospace; letter-spacing: 2px; font-size: 12px; }
.btn-tech-retry {
  background: transparent; border: 1px solid #475569; color: #94a3b8;
  padding: 6px 16px; font-size: 11px; font-family: monospace; cursor: pointer;
  transition: all 0.2s;
}
.btn-tech-retry:hover { border-color: #10B981; color: #10B981; box-shadow: 0 0 10px rgba(16,185,129,0.2); }

/* ========================================= */
/*  自动化采集卡片样式 (保持)                 */
/* ========================================= */
.auto-task-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 25px; }
.task-card {
  position: relative; height: 220px; border-radius: 12px; overflow: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: #fff; border: 1px solid #eef2f6;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  display: flex; align-items: center; justify-content: center;
}
.task-card:hover { transform: translateY(-4px); box-shadow: 0 12px 24px rgba(0,0,0,0.06); }
.card-bg-decoration {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; opacity: 0.4;
  background-image: radial-gradient(circle at 10% 10%, rgba(255,255,255,0.05) 0%, transparent 20%);
  pointer-events: none;
}
.card-capture { background: linear-gradient(145deg, #f8fbff 0%, #eef6ff 100%); border-color: #dbeafe; }
.card-video { background: linear-gradient(145deg, #fcfaff 0%, #f7f0ff 100%); border-color: #f3e8ff; }
.card-content {
  position: relative; z-index: 2; width: 100%; padding: 0 30px;
  display: flex; flex-direction: column; align-items: center; text-align: center;
}
.icon-radar-container {
  position: relative; width: 60px; height: 60px;
  display: flex; align-items: center; justify-content: center; margin-bottom: 20px;
}
.main-icon {
  position: relative; z-index: 10; font-size: 24px; width: 44px; height: 44px;
  border-radius: 50%; background: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.radar-wave {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 100%; height: 100%; border-radius: 50%; opacity: 0;
  animation: radar-ping 2s cubic-bezier(0, 0, 0.2, 1) infinite;
}
.radar-wave.delay { animation-delay: 0.6s; }
@keyframes radar-ping { 0% { width: 80%; height: 80%; opacity: 0.6; } 100% { width: 200%; height: 200%; opacity: 0; } }
.blue-theme .main-icon { color: #165DFF; }
.blue-theme .radar-wave { border: 2px solid #165DFF; }
.purple-theme .main-icon { color: #722ED1; }
.purple-theme .radar-wave { border: 2px solid #722ED1; }
.text-group h3 { font-size: 16px; font-weight: 700; color: #1f2937; margin-bottom: 8px; }
.text-group p { font-size: 13px; color: #6b7280; line-height: 1.5; margin-bottom: 20px; max-width: 300px; }
.status-bar {
  display: flex; align-items: center; gap: 8px; background: #fff; padding: 6px 16px;
  border-radius: 20px; font-size: 12px; font-weight: 600; box-shadow: 0 2px 6px rgba(0,0,0,0.04);
}
.blue-theme.status-bar { color: #165DFF; border: 1px solid #bacefd; }
.purple-theme.status-bar { color: #722ED1; border: 1px solid #d3adf7; }

/* ========================================= */
/*  元数据 (Original)                        */
/* ========================================= */
.meta-panel-original { background: #fff; }
.form-row-original { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
.form-group label { display: block; font-size: 14px; color: #6b7280; margin-bottom: 8px; }
.select-box {
  border: 1px solid #e5e7eb; background: #f9fafb; padding: 10px 15px; border-radius: 6px;
  display: flex; justify-content: space-between; align-items: center; font-size: 14px; color: #374151;
}
.text-box-static { background: #fff; font-size: 14px; color: #374151; line-height: 1.6; }
.btn-magic-original {
  margin-top: 15px; border: 1px solid #165DFF; color: #165DFF; background: #fff;
  padding: 8px 16px; border-radius: 4px; font-size: 14px; cursor: pointer;
  display: flex; align-items: center; gap: 8px;
}

/* ========================================= */
/*  存证凭证 (Original)                      */
/* ========================================= */
.chain-panel-original { background: #F5F5F7; border-radius: 8px; padding: 24px; border: 1px solid #f3f4f6; }
.chain-header-original { display: flex; justify-content: space-between; margin-bottom: 20px; }
.chain-title-o { color: #165DFF; font-weight: 600; display: flex; align-items: center; gap: 8px; }
.status-tag-o { background: #FFF7E6; color: #FA8C16; padding: 4px 12px; border-radius: 20px; font-size: 12px; display: flex; align-items: center; gap: 6px; }
.status-tag-o .dot { width: 6px; height: 6px; background: #FA8C16; border-radius: 50%; }

.hash-row-o { margin-bottom: 20px; }
.hash-row-o label { display: block; font-size: 14px; color: #6b7280; margin-bottom: 8px; }
.hash-val-o {
  background: #fff; border: 1px solid #e5e7eb; border-radius: 6px; padding: 10px;
  display: flex; justify-content: space-between; font-size: 14px; font-family: monospace; color: #374151;
}
.info-grid-o { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.info-item-o label { display: block; font-size: 14px; color: #6b7280; margin-bottom: 4px; }
.info-item-o span { font-weight: 500; color: #1f2937; font-size: 14px; }
.font-mono { font-family: monospace; }
.link { color: #165DFF; cursor: pointer; }

/* 底部栏 */
.footer-bar-original {
  margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee;
  display: flex; justify-content: space-between; align-items: center;
}
.footer-l { display: flex; align-items: center; gap: 8px; }
.footer-l input { width: 16px; height: 16px; accent-color: #165DFF; }
.footer-l label { font-size: 14px; font-weight: 500; color: #374151; }
.footer-l .desc { font-size: 12px; color: #9ca3af; margin-left: 10px; }
.footer-r { display: flex; gap: 12px; }
.btn-c { padding: 8px 24px; border: 1px solid #d1d5db; background: #fff; color: #374151; border-radius: 4px; cursor: pointer; }
.btn-s { padding: 8px 24px; border: none; background: #d1d5db; color: #fff; border-radius: 4px; cursor: not-allowed; }
.btn-s.active { background: #165DFF; cursor: pointer; }

/* ========================================= */
/*  SHA-256 哈希计算区域样式               */
/* ========================================= */
.hash-calc-panel {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  border: 1px solid #f3f4f6;
}

.upload-area {
  text-align: center;
  padding: 40px;
  background: #f9fafb;
  border: 2px dashed #e5e7eb;
  border-radius: 8px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #165DFF;
  background: #f0f7ff;
}

.upload-btn-large {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 40px;
  background: #165DFF;
  color: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
  font-weight: 500;
}

.upload-btn-large:hover {
  background: #0e4fc5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.3);
}

.upload-btn-large i {
  font-size: 32px;
}

.upload-tip {
  margin-top: 12px;
  font-size: 13px;
  color: #9ca3af;
}

/* 计算中动画 */
.calculating-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px;
  background: #f9fafb;
  border-radius: 8px;
}

.loader {
  width: 50px;
  height: 50px;
  border: 4px solid #e5e7eb;
  border-top-color: #165DFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.calculating-box p {
  color: #6b7280;
  font-size: 14px;
}

/* 结果展示 */
.hash-result-box {
  background: #f9fafb;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid #e5e7eb;
}

.result-header {
  display: flex;
  justify-content: space-between; /* 左右两端对齐 */
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-reupload {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #fff;
  color: #165DFF;
  border: 1px solid #165DFF;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-reupload:hover {
  background: #165DFF;
  color: #fff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.3);
}

.result-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

/* 图片预览 + 信息布局 */
.result-layout {
  display: grid;
  grid-template-columns: 400px 1fr; /* 左侧固定宽度，右侧自适应 */
  gap: 24px;
  align-items: start;
}

/* 图片预览区域 */
.image-preview-box {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e5e7eb;
}

.preview-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.preview-wrapper {
  width: 100%;
  aspect-ratio: 1; /* 正方形 */
  background: #f9fafb;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e5e7eb;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 保持比例，完整显示 */
  display: block;
}

/* 视频预览区域 */
.video-preview-wrapper {
  aspect-ratio: 16 / 9; /* 视频比例 */
}

.preview-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  background: #000;
  border-radius: 4px;
}

/* 视频上传按钮样式 */
.upload-btn-video {
  background: linear-gradient(135deg, #9333ea 0%, #c026d3 100%);
}

.upload-btn-video:hover {
  background: linear-gradient(135deg, #7e22ce 0%, #a21caf 100%);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

/* 右侧信息区域 */
.result-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.result-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.result-item.full-width {
  grid-column: 1 / -1;
}

.result-item label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.result-item span {
  font-size: 14px;
  color: #1f2937;
  padding: 8px 12px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.hash-value-display {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 8px 12px;
}

.hash-text {
  flex: 1;
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  color: #165DFF;
  word-break: break-all;
  background: none;
  border: none;
  padding: 0;
}

.btn-copy {
  padding: 6px 10px;
  background: #165DFF;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-copy:hover {
  background: #0e4fc5;
  transform: scale(1.05);
}

.btn-copy i {
  font-size: 14px;
}
</style>