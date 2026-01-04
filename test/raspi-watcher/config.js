/**
 * 树莓派图片监听配置
 */

module.exports = {
  // 监听目录
  watchDir: '/home/pi',  
  
  // 后端上传接口URL
  uploadUrl: 'http://192.168.1.102:8081/annotation/upload/camera/auto', 
  
  // 设备ID（树莓派设备编号）
  deviceId: 1,
  
  // 只监听jpg格式
  imageExtensions: ['.jpg'],
  
  // 处理后文件的目标目录
  processedDir: '/home/pi/images/processed',
  failedDir: '/home/pi/images/failed',
  
  // 上传超时时间（毫秒）
  uploadTimeout: 30000,
  
  // 文件稳定等待时间（毫秒）
  fileStableDelay: 1000
};
