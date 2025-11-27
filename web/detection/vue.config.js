module.exports={
  devServer:{
    port:"8082", // 设置端口 默认8080
    open:true, // 项目运行自动打开浏览器
    // 在与port open 设置服务代理
    proxy:{
      // /api 自定义服务代理名字
      "/api":{
        target:"http://localhost:8081", //代理帮助你请求的具体服务http://localhost:8081
        changeOrigin:true, // 开启代理
        ws: true, // 启用WebSocket代理（SSE需要）
        pathRewrite:{  // 格式化path
          "^/api":""
        },
        onProxyReq: (proxyReq, req, res) => {
          // 设置正确的请求头，确保SSE能正常工作
          if (req.headers.accept && req.headers.accept.includes('text/event-stream')) {
            proxyReq.setHeader('Accept', 'text/event-stream');
            proxyReq.setHeader('Cache-Control', 'no-cache');
            proxyReq.setHeader('Connection', 'keep-alive');
          }
        },
        onProxyRes: (proxyRes, req, res) => {
          // 确保响应头正确
          if (proxyRes.headers['content-type'] && proxyRes.headers['content-type'].includes('text/event-stream')) {
            proxyRes.headers['Cache-Control'] = 'no-cache';
            proxyRes.headers['Connection'] = 'keep-alive';
            proxyRes.headers['X-Accel-Buffering'] = 'no';
          }
        }
      }
    }
  }
}
