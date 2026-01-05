#!/usr/bin/env python3
"""
简单的HTTP文件上传服务器
在目标PC(106)上运行，接收文件
"""

from http.server import HTTPServer, BaseHTTPRequestHandler
import os
from datetime import datetime

class UploadHandler(BaseHTTPRequestHandler):
    
    def do_POST(self):
        """处理文件上传"""
        content_length = int(self.headers['Content-Length'])
        file_data = self.rfile.read(content_length)
        
        # 从URL获取文件名
        filename = self.path.strip('/')
        if not filename:
            filename = f"uploaded_{datetime.now().strftime('%Y%m%d_%H%M%S')}.jpg"
        
        # 保存文件
        save_dir = r'C:\temp'
        if not os.path.exists(save_dir):
            os.makedirs(save_dir)
        
        filepath = os.path.join(save_dir, filename)
        
        with open(filepath, 'wb') as f:
            f.write(file_data)
        
        print(f"✅ 接收文件: {filename} ({len(file_data)/1024:.2f} KB)")
        
        # 返回成功响应
        self.send_response(200)
        self.send_header('Content-type', 'text/html; charset=utf-8')
        self.end_headers()
        self.wfile.write(f"文件上传成功: {filename}".encode('utf-8'))
    
    def log_message(self, format, *args):
        """简化日志输出"""
        pass

if __name__ == '__main__':
    port = 8888
    server = HTTPServer(('0.0.0.0', port), UploadHandler)
    print(f"📡 文件接收服务启动在端口 {port}")
    print(f"📂 文件保存到: C:\\temp")
    print(f"⏳ 等待接收文件...\n")
    server.serve_forever()