/**
 * 使用轮询方式替代SSE
 * 
 */

class DataManager {
  constructor() {
    this.pollingTimer = null;
    this.listeners = new Map();
    this.isRunning = false;
    this.pollingInterval = 50; // 500毫秒轮询一次
  }

  /**
   * 启动轮询
   */
  init() {
    if (this.isRunning) {
      console.log('ℹ️ 轮询已启动，跳过初始化');
      return;
    }

    console.log('🔄 启动实时数据轮询...');
    this.isRunning = true;
    
    // 通知所有监听器连接已建立
    this.notifyListeners('connection', { connected: true });
    
    // 立即获取一次数据
    this.fetchData();
    
    // 启动定时轮询
    this.pollingTimer = setInterval(() => {
      this.fetchData();
    }, this.pollingInterval);
  }

  /**
   * 获取数据
   */
  async fetchData() {
    try {
      const response = await fetch('http://localhost:8081/dashboard/data', {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        console.error('❌ 获取数据失败:', response.status);
        return;
      }
      
      const result = await response.json();
      console.log('📨 收到响应:', result);
      
      // Result对象的data字段才是真正的数据
      const data = result.data || result;
      
      console.log('📝 数据解析:', {
        hasImgBase64: !!data.imgBase64,
        imgBase64Length: data.imgBase64 ? data.imgBase64.length : 0,
        defectionsCount: data.defections ? data.defections.length : 0,
        runTime: data.runTime
      });
      
      // 分发数据到所有监听器
      this.notifyListeners('message', data);
    } catch (error) {
      console.error('❌ 请求数据失败:', error);
    }
  }

  /**
   * 注册监听器
   */
  subscribe(componentId, callback) {
    if (!this.listeners.has(componentId)) {
      this.listeners.set(componentId, callback);
      console.log(`📌 组件 ${componentId} 已订阅数据`);
    }

    if (this.isRunning) {
      callback('connection', { connected: true });
    }
  }

  /**
   * 取消订阅
   */
  unsubscribe(componentId) {
    if (this.listeners.has(componentId)) {
      this.listeners.delete(componentId);
      console.log(`📍 组件 ${componentId} 已取消订阅`);
    }
  }

  /**
   * 通知所有监听器
   */
  notifyListeners(type, data) {
    this.listeners.forEach((callback, componentId) => {
      try {
        callback(type, data);
      } catch (error) {
        console.error(`❌ 通知组件 ${componentId} 时出错:`, error);
      }
    });
  }

  /**
   * 关闭轮询
   */
  close() {
    if (this.pollingTimer) {
      clearInterval(this.pollingTimer);
      this.pollingTimer = null;
    }
    this.isRunning = false;
    console.log('🔌 轮询已停止');
  }

  /**
   * 重置
   */
  reset() {
    this.listeners.clear();
    this.close();
    console.log('🔄 数据管理器已重置');
  }
}

// 导出单例
export default new DataManager();
