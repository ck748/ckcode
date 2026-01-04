# 树莓派图片监听上传服务

自动监听树莓派指定文件夹，检测到jpg文件时自动上传到后端。

## 安装步骤

### 1. 在树莓派上安装Node.js
```bash
# 检查是否已安装
node -v

# 如未安装（推荐使用官方脚本）
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

### 2. 上传文件到树莓派
将以下文件复制到树莓派（如 `/home/pi/raspi-watcher/`）：
- `index.js`
- `config.js`
- `package.json`

### 3. 修改配置
编辑 `config.js`：
```javascript
watchDir: '/home/pi/images',  // 改成你的监听目录
uploadUrl: 'http://192.168.1.104:8081/annotation/upload/camera/auto',  // 改成你电脑的IP
deviceId: 1,  // 树莓派设备编号
```

### 4. 安装依赖
```bash
cd /home/pi/raspi-watcher
npm install
```

### 5. 创建监听目录
```bash
mkdir -p /home/pi/images
mkdir -p /home/pi/images/processed
mkdir -p /home/pi/images/failed
```

## 运行

### 手动运行
```bash
node index.js
```

### 后台运行（使用PM2）
```bash
# 安装PM2
sudo npm install -g pm2

# 启动服务
pm2 start index.js --name raspi-watcher

# 开机自启
pm2 startup
pm2 save

# 查看日志
pm2 logs raspi-watcher

# 停止服务
pm2 stop raspi-watcher
```

## 测试

在监听目录放入jpg文件：
```bash
cp test.jpg /home/pi/images/
```

服务会自动：
1. 检测到新文件
2. 上传到后端
3. 成功后移动到 `processed/`
4. 失败则移动到 `failed/`

## 常见问题

**Q: 连接后端失败？**
- 检查后端IP和端口是否正确
- 确认树莓派能ping通后端电脑
- 检查后端防火墙是否开放8081端口

**Q: 文件被立即删除？**
- 可能是其他程序在处理文件
- 检查文件权限

**Q: 上传很慢？**
- 检查网络连接
- 树莓派Wi-Fi信号是否稳定
