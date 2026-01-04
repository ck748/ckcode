

const chokidar = require('chokidar');
const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');
const path = require('path');
const config = require('./config');

// 统计信息
const stats = {
  processed: 0,
  success: 0,
  failed: 0
};

// 正在处理的文件集合
const processingFiles = new Set();

/**
 * 日志输出
 */
function log(message, emoji = 'ℹ️') {
  const now = new Date().toLocaleString('zh-CN', { hour12: false });
  console.log(`[${now}] ${emoji} ${message}`);
}

/**
 * 检查是否为jpg文件
 */
function isJpgFile(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  return config.imageExtensions.includes(ext);
}

/**
 * 确保目录存在
 */
function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
    log(`创建目录: ${dir}`, '📁');
  }
}

/**
 * 上传图片到后端
 */
async function uploadImage(filePath) {
  const fileName = path.basename(filePath);
  
  try {
    log(`上传: ${fileName}`, '📤');
    const startTime = Date.now();
    
    const fileStream = fs.createReadStream(filePath);
    const formData = new FormData();
    formData.append('image', fileStream, fileName);
    
    // 拼接deviceId参数
    const url = `${config.uploadUrl}?deviceId=${config.deviceId}`;
    
    const response = await axios.post(url, formData, {
      headers: formData.getHeaders(),
      timeout: config.uploadTimeout
    });
    
    const duration = ((Date.now() - startTime) / 1000).toFixed(2);
    log(`成功: ${fileName} (${duration}s)`, '✅');
    
    return { success: true, response };
  } catch (error) {
    const errorMsg = error.response 
      ? `HTTP ${error.response.status}`
      : error.message;
    
    log(`失败: ${fileName} - ${errorMsg}`, '❌');
    return { success: false, error: errorMsg };
  }
}

/**
 * 移动文件
 */
function moveFile(sourcePath, targetDir) {
  const fileName = path.basename(sourcePath);
  
  if (!fs.existsSync(sourcePath)) {
    log(`文件不存在: ${fileName}`, '⚠️');
    return false;
  }
  
  let targetPath = path.join(targetDir, fileName);
  
  // 处理重名
  if (fs.existsSync(targetPath)) {
    const ext = path.extname(fileName);
    const nameWithoutExt = path.basename(fileName, ext);
    const timestamp = Date.now();
    targetPath = path.join(targetDir, `${nameWithoutExt}_${timestamp}${ext}`);
  }
  
  try {
    fs.renameSync(sourcePath, targetPath);
    log(`已移动: ${fileName}`, '📦');
    return true;
  } catch (error) {
    try {
      if (!fs.existsSync(sourcePath)) return false;
      fs.copyFileSync(sourcePath, targetPath);
      fs.unlinkSync(sourcePath);
      log(`已复制移动: ${fileName}`, '📦');
      return true;
    } catch (e) {
      log(`移动失败: ${e.message}`, '❌');
      return false;
    }
  }
}

/**
 * 处理图片文件
 */
async function processImageFile(filePath) {
  const fileName = path.basename(filePath);
  
  if (processingFiles.has(filePath)) {
    return;
  }
  
  processingFiles.add(filePath);
  stats.processed++;
  
  try {
    // 等待文件写入完成
    await new Promise(resolve => setTimeout(resolve, config.fileStableDelay));
    
    if (!fs.existsSync(filePath)) {
      log(`文件消失: ${fileName}`, '⚠️');
      processingFiles.delete(filePath);
      return;
    }
    
    // 上传
    const result = await uploadImage(filePath);
    
    if (!fs.existsSync(filePath)) {
      if (result.success) stats.success++;
      processingFiles.delete(filePath);
      return;
    }
    
    // 移动文件
    const targetDir = result.success 
      ? config.processedDir
      : config.failedDir;
    
    const moved = moveFile(filePath, targetDir);
    
    if (result.success) {
      stats.success++;
    } else {
      stats.failed++;
    }
    
  } catch (error) {
    log(`处理出错: ${fileName} - ${error.message}`, '❌');
    stats.failed++;
  } finally {
    processingFiles.delete(filePath);
  }
}

/**
 * 启动服务
 */
async function start() {
  console.log('\n🍓 树莓派图片监听服务启动\n');
  
  // 确保目录存在
  ensureDir(config.watchDir);
  ensureDir(config.processedDir);
  ensureDir(config.failedDir);
  
  log(`监听目录: ${config.watchDir}`, '📁');
  log(`上传接口: ${config.uploadUrl}`, '🔗');
  log(`设备ID: ${config.deviceId}`, '🆔');
  log(`监听格式: ${config.imageExtensions.join(', ')}`, '📷');
  console.log('');
  
  // 创建监听器
  const watcher = chokidar.watch(config.watchDir, {
    ignored: [
      /(^|[\/\\])\../,
      '**/processed/**',
      '**/failed/**'
    ],
    persistent: true,
    ignoreInitial: true,
    awaitWriteFinish: {
      stabilityThreshold: config.fileStableDelay,
      pollInterval: 100
    }
  });
  
  // 监听文件添加
  watcher.on('add', (filePath) => {
    const relativePath = path.relative(config.watchDir, filePath);
    const isInSubDir = relativePath.includes(path.sep);
    
    if (!isInSubDir && isJpgFile(filePath)) {
      const fileName = path.basename(filePath);
      log(`新文件: ${fileName}`, '🖼️');
      processImageFile(filePath);
    }
  });
  
  watcher.on('error', (error) => {
    log(`监听错误: ${error.message}`, '❌');
  });
  
  log('等待jpg文件...', '⏳');
  
  // 优雅退出
  setupGracefulShutdown(watcher);
}

/**
 * 优雅退出
 */
function setupGracefulShutdown(watcher) {
  let isShuttingDown = false;
  
  const shutdown = async (signal) => {
    if (isShuttingDown) return;
    isShuttingDown = true;
    
    console.log('\n');
    log(`收到${signal}信号，正在停止...`, '⏹️');
    
    if (processingFiles.size > 0) {
      log(`等待${processingFiles.size}个文件...`, '⏳');
      const maxWait = 10000;
      const startWait = Date.now();
      
      while (processingFiles.size > 0 && (Date.now() - startWait) < maxWait) {
        await new Promise(resolve => setTimeout(resolve, 500));
      }
    }
    
    await watcher.close();
    
    console.log('');
    log('服务已停止', '✅');
    log(`统计: 处理${stats.processed}, 成功${stats.success}, 失败${stats.failed}`, '📊');
    console.log('');
    
    process.exit(0);
  };
  
  process.on('SIGINT', () => shutdown('SIGINT'));
  process.on('SIGTERM', () => shutdown('SIGTERM'));
}

// 启动
start().catch((error) => {
  console.error('启动失败:', error);
  process.exit(1);
});
