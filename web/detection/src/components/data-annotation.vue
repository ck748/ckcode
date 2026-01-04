<template>
  <!-- 
    【终极修正逻辑】
    1. height: 100vh -> 强行占满屏幕，不管父级给多少空间。
    2. overflow-y: scroll -> 强制在当前组件内生成滚动条，不再依赖浏览器。
    3. padding-bottom: 120px -> 在最底部预留巨大空间，防止任何东西遮挡底部按钮。
  -->
  <div class="scroll-window">
    
    <!-- 引入图标库 -->
    <div style="display: none;">
      <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet" />
    </div>

    <!-- 内容包裹层 -->
    <div class="content-wrapper">
      
      <!-- 页面顶部 Header -->
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

      <!-- 1. 新建存证任务 -->
      <div class="section-container">
        <h2 class="section-title">新建存证任务</h2>
        <div class="grid-layout">
          
          <!-- 左：图片上传 -->
          <div class="upload-area">
            <div 
              class="upload-zone"
              :class="{ 'dragging': isDraggingImg }"
              @dragover.prevent="isDraggingImg = true"
              @dragleave.prevent="isDraggingImg = false"
              @drop.prevent="handleImgDrop"
            >
              <div v-if="imgPreview" class="preview-mode">
                <img :src="imgPreview" class="preview-image" />
                <button @click="removeImg" class="btn-delete">
                  <i class="fas fa-trash-alt"></i> 删除并重新上传
                </button>
              </div>
              <div v-else class="upload-initial">
                <div class="icon-wrapper"><i class="fas fa-image"></i></div>
                <div class="upload-text-main">质检:自动采集缺陷图片</div>
                <div class="upload-text-sub">支持 JPG, PNG 格式，单张≤20M</div>
                <button class="btn-trigger" @click="triggerImgInput">
                  <i class="fas fa-cloud-upload-alt"></i> 选择文件
                </button>
                <input ref="fileInputImg" type="file" class="hidden" accept="image/*" @change="handleImgChange">
              </div>
            </div>
          </div>

          <!-- 右：视频上传 -->
          <div class="upload-area">
            <div 
              class="upload-zone"
              :class="{ 'dragging': isDraggingVideo }"
              @dragover.prevent="isDraggingVideo = true"
              @dragleave.prevent="isDraggingVideo = false"
              @drop.prevent="handleVideoDrop"
            >
              <div v-if="videoPreview" class="preview-mode">
                <video :src="videoPreview" class="preview-image" controls></video>
                <button @click="removeVideo" class="btn-delete">
                  <i class="fas fa-trash-alt"></i> 删除并重新上传
                </button>
              </div>
              <div v-else class="upload-initial">
                <div class="icon-wrapper"><i class="fas fa-video"></i></div>
                <div class="upload-text-main">机械臂分拣视频</div>
                <div class="upload-text-sub">支持 MP4, AVI 格式，单个≤500M</div>
                <button class="btn-trigger" @click="triggerVideoInput">
                  <i class="fas fa-cloud-upload-alt"></i> 选择文件
                </button>
                <input ref="fileInputVideo" type="file" class="hidden" accept="video/*" @change="handleVideoChange">
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- 2. 关联元数据 -->
      <div class="section-container">
        <h2 class="section-title">关联元数据</h2>
        <div class="meta-panel">
          <div class="grid-layout mb-4">
            <div class="form-control">
              <label>关联零件序列号</label>
              <div class="fake-select">
                <span>SN002 - 变速箱齿轮</span>
                <i class="fas fa-chevron-down"></i>
              </div>
            </div>
            <div class="form-control">
              <label>采集工位</label>
              <div class="fake-select">
                <span>热处理炉前检</span>
                <i class="fas fa-chevron-down"></i>
              </div>
            </div>
          </div>
          <div class="form-control mb-4">
            <label>数据说明</label>
            <div class="text-static">
              热处理后质量检查：表面无裂纹，硬度达标，尺寸符合图纸要求。设备编号：HT-2023-005，操作人员：张工。
            </div>
          </div>
          <button class="btn-magic">
            <i class="fas fa-wand-magic-sparkles"></i> 一键提取元数据
          </button>
        </div>
      </div>

      <!-- 3. 链上凭证预览 -->
      <div class="section-container">
        <h2 class="section-title">存证信息将生成如下链上凭证</h2>
        <div class="chain-panel">
          <div class="chain-top">
            <div class="chain-title-wrap">
              <i class="fas fa-link"></i> 
              <span>区块链存证凭证</span>
            </div>
            <div class="status-tag">
              <span class="status-indicator"></span> 待上链
            </div>
          </div>

          <div class="hash-row">
            <label><i class="fas fa-fingerprint"></i> 数据指纹 (哈希值)</label>
            <div class="hash-display">
              <span class="font-code">0x892f7d3e9c5a1b9e4f6g8h9i0j1k2l3m4n5o6p7q8r9s0t</span>
              <button class="btn-copy"><i class="far fa-copy"></i></button>
            </div>
          </div>

          <div class="info-grid-4">
            <div class="info-unit">
              <label>存证时间</label>
              <div class="info-val">2023-10-27 14:30:25</div>
            </div>
            <div class="info-unit">
              <label>区块高度</label>
              <div class="info-val font-code">#124809</div>
            </div>
            <div class="info-unit">
              <label>交易ID</label>
              <div class="info-flex">
                <span class="tag-code">0x741d...9b2e</span>
                <span class="link-expand">展开</span>
              </div>
            </div>
            <div class="info-unit">
              <label>存证节点</label>
              <div class="info-val">工厂A-私有链节点1</div>
            </div>
          </div>

          <div class="chain-bottom-info">
            <span>此凭证基于企业私有链技术生成</span>
            <span>链下存储: IPFS集群</span>
          </div>
        </div>
      </div>

      <!-- 底部栏 (直接跟在内容后面) -->
      <div class="footer-bar-final">
        <div class="footer-left">
          <div class="checkbox-line">
            <input 
              type="checkbox" 
              id="syncCheck" 
              v-model="isSyncChecked"
              class="checkbox-native"
            >
            <label for="syncCheck">存证并同步至联盟链</label>
          </div>
          <div class="footer-desc">此凭证的哈希摘要将被同步给产业链联盟链，供下游客户验证</div>
        </div>
        <div class="footer-right">
          <button class="btn-cancel">取消</button>
          <button 
            class="btn-confirm"
            :class="isSyncChecked ? 'active' : 'disabled'"
            :disabled="!isSyncChecked"
          >
            开始存证
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const isSyncChecked = ref(false); 

const fileInputImg = ref(null);
const imgPreview = ref(null);
const isDraggingImg = ref(false);

const fileInputVideo = ref(null);
const videoPreview = ref(null);
const isDraggingVideo = ref(false);

const triggerImgInput = () => { fileInputImg.value.click(); };
const handleImgChange = (event) => {
  const file = event.target.files[0];
  if (file) createImgPreview(file);
};
const handleImgDrop = (event) => {
  isDraggingImg.value = false;
  const file = event.dataTransfer.files[0];
  if (file && file.type.startsWith('image/')) createImgPreview(file);
};
const createImgPreview = (file) => { imgPreview.value = URL.createObjectURL(file); };
const removeImg = () => {
  imgPreview.value = null;
  if (fileInputImg.value) fileInputImg.value.value = '';
};

const triggerVideoInput = () => { fileInputVideo.value.click(); };
const handleVideoChange = (event) => {
  const file = event.target.files[0];
  if (file) createVideoPreview(file);
};
const handleVideoDrop = (event) => {
  isDraggingVideo.value = false;
  const file = event.dataTransfer.files[0];
  if (file && file.type.startsWith('video/')) createVideoPreview(file);
};
const createVideoPreview = (file) => { videoPreview.value = URL.createObjectURL(file); };
const removeVideo = () => {
  if (videoPreview.value) URL.revokeObjectURL(videoPreview.value); 
  videoPreview.value = null;
  if (fileInputVideo.value) fileInputVideo.value.value = '';
};
</script>

<style scoped>
/* Reset */
* { box-sizing: border-box; margin: 0; padding: 0; outline: none; }
.hidden { display: none; }

/* 
  === 终极滚动容器 ===
  这个 class 保证了无论放在哪里，它自己内部一定能滚动
*/
.scroll-window {
  /* 强制高度占满父级窗口 */
  height: 100vh;
  width: 100%;
  background-color: #fff;
  /* 强制生成 Y 轴滚动条 */
  overflow-y: scroll; 
  /* 字体设置 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  color: #333;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;
  /* 关键：在底部预留巨大的空间，确保底部栏一定能被滑出来 */
  padding-bottom: 120px; 
}

/* 
  === 底部栏样式 ===
  普通 Block 元素，不悬浮，不定位
*/
.footer-bar-final {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Footer 组件样式 */
.footer-left { display: flex; flex-direction: column; }
.checkbox-line { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.checkbox-native { width: 16px; height: 16px; cursor: pointer; accent-color: #165DFF; }
.checkbox-line label { font-size: 14px; font-weight: 500; color: #374151; cursor: pointer; user-select: none; }
.footer-desc { font-size: 12px; color: #9ca3af; padding-left: 24px; }

.footer-right { display: flex; gap: 12px; }
.btn-cancel {
  padding: 8px 24px; border: 1px solid #d1d5db; background: #fff;
  color: #374151; border-radius: 4px; cursor: pointer;
}
.btn-cancel:hover { background: #f9fafb; }

.btn-confirm {
  padding: 8px 24px; border: none; border-radius: 4px; color: #fff; font-weight: 500;
  transition: all 0.2s;
}
.btn-confirm.disabled { 
  background: #d1d5db; 
  cursor: not-allowed; 
}
.btn-confirm.active { 
  background: #165DFF; 
  cursor: pointer; 
}
.btn-confirm.active:hover { background: #165DFFE6; }


/* 
  === 其他组件样式 (保持不变) ===
*/

/* Header */
.header-section { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 30px; }
.page-title { font-size: 24px; font-weight: 700; color: #1f2937; margin-bottom: 5px; }
.page-subtitle { font-size: 14px; color: #6b7280; }
.header-actions { display: flex; gap: 12px; }

/* Buttons */
.btn { padding: 8px 16px; border-radius: 6px; font-size: 14px; cursor: pointer; display: flex; align-items: center; gap: 8px; border: 1px solid transparent; transition: all 0.2s; }
.btn-outline { background: #fff; border-color: #e5e7eb; color: #374151; }
.btn-outline:hover { background: #f9fafb; }
.btn-dark { background: #1f2937; color: #fff; }
.btn-dark:hover { background: #374151; }

/* Sections */
.section-container { margin-bottom: 30px; }
.section-title { font-size: 16px; font-weight: 700; color: #111827; margin-bottom: 15px; }
.grid-layout { display: grid; grid-template-columns: 1fr; gap: 20px; }
@media (min-width: 900px) { .grid-layout { grid-template-columns: 1fr 1fr; } }

/* Upload */
.upload-zone {
  border: 2px dashed #d1d5db; border-radius: 10px; height: 240px;
  display: flex; flex-direction: column; justify-content: center; align-items: center;
  background: #fff; transition: all 0.2s;
}
.upload-zone:hover, .upload-zone.dragging { border-color: #165DFF; background: #f0f5ff; }
.upload-initial { text-align: center; }
.icon-wrapper {
  width: 48px; height: 48px; background: #e8f3ff; color: #165DFF;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 20px; margin: 0 auto 10px;
}
.upload-text-main { font-weight: 500; color: #374151; }
.upload-text-sub { font-size: 12px; color: #9ca3af; margin: 5px 0 15px; }
.btn-trigger {
  background: #e8f3ff; color: #165DFF; border: none; padding: 8px 20px;
  border-radius: 4px; font-weight: 500; cursor: pointer; display: flex;
  align-items: center; gap: 8px; margin: 0 auto;
}
.btn-trigger:hover { background: #d1e9ff; }
.preview-mode { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.preview-image { max-height: 150px; max-width: 90%; margin-bottom: 10px; object-fit: contain; }
.btn-delete { color: #ef4444; border: none; background: none; cursor: pointer; font-size: 14px; }
.btn-delete:hover { text-decoration: underline; }

/* Metadata */
.meta-panel { background: #fff; }
.form-control label { display: block; font-size: 14px; color: #6b7280; margin-bottom: 8px; }
.fake-select {
  border: 1px solid transparent; background: #f9fafb; padding: 10px 15px;
  border-radius: 6px; display: flex; justify-content: space-between; align-items: center;
  cursor: pointer; font-size: 14px; color: #374151;
}
.fake-select:hover { background: #fff; border-color: #e5e7eb; }
.text-static { font-size: 14px; color: #374151; line-height: 1.6; }
.btn-magic {
  margin-top: 10px; border: 1px solid #165DFF; color: #165DFF; background: #fff;
  padding: 8px 16px; border-radius: 4px; font-size: 14px; cursor: pointer;
  display: flex; align-items: center; gap: 8px;
}
.btn-magic:hover { background: #f0f5ff; }

/* Chain Card */
.chain-panel { background: #F5F5F7; border-radius: 8px; padding: 24px; border: 1px solid #f3f4f6; }
.chain-top { display: flex; justify-content: space-between; margin-bottom: 20px; }
.chain-title-wrap { color: #165DFF; font-weight: 600; display: flex; align-items: center; gap: 8px; }
.status-tag {
  background: #FFF7E6; color: #FA8C16; padding: 4px 12px; border-radius: 20px;
  font-size: 12px; font-weight: 500; display: flex; align-items: center; gap: 6px;
}
.status-indicator { width: 6px; height: 6px; background: #FA8C16; border-radius: 50%; }

.hash-row { margin-bottom: 20px; }
.hash-row label { display: block; font-size: 14px; color: #6b7280; margin-bottom: 8px; display: flex; gap: 6px; align-items: center; }
.hash-display {
  background: #fff; border: 1px solid #e5e7eb; border-radius: 6px; padding: 10px;
  display: flex; justify-content: space-between; font-size: 14px; color: #374151;
}
.font-code { font-family: monospace; }
.btn-copy { border: none; background: none; color: #165DFF; cursor: pointer; }

.info-grid-4 { display: grid; grid-template-columns: 1fr; gap: 20px; }
@media (min-width: 768px) { .info-grid-4 { grid-template-columns: 1fr 1fr; column-gap: 40px; } }
.info-unit label { display: block; font-size: 14px; color: #6b7280; margin-bottom: 4px; }
.info-unit .info-val { font-weight: 500; color: #1f2937; font-size: 14px; }
.info-flex { display: flex; gap: 10px; align-items: center; }
.tag-code { background: #f3f4f6; padding: 2px 6px; border-radius: 4px; font-family: monospace; font-size: 13px; color: #4b5563; }
.link-expand { color: #165DFF; font-size: 12px; cursor: pointer; }

.chain-bottom-info {
  margin-top: 20px; padding-top: 15px; border-top: 1px solid #e5e7eb;
  display: flex; justify-content: space-between; font-size: 12px; color: #9ca3af;
}
</style>