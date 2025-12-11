// SPDX-License-Identifier: MIT
pragma solidity ^0.6.0;

contract WorkshopFour {
    struct PaintingData {
        string paintThickness;  // 漆膜厚度
        string sprayPressure;   // 喷涂压力
        string paintBatch;      // 涂料批次
    }
    
    struct PackingData {
        string packTime;        // 包装时间
        string packOperator;    // 包装操作员
        string snCode;          // 序列号
    }
    
    mapping(string => PaintingData) private _paintingData;
    mapping(string => PackingData) private _packingData;
    
    // 统计数据
    struct ProductionStats {
        uint256 totalReceived;      // 接收总量
        uint256 qualifiedCount;     // 合格数量
        uint256 unqualifiedCount;   // 不合格数量
        uint256 finishedCount;      // 完成数量（已包装）
        uint256 waitingCount;       // 待处理数量
    }
    
    ProductionStats private _stats;
    
    // 涂料批次统计
    struct PaintBatchStats {
        uint256 totalUsed;
        uint256 qualifiedCount;
        uint256 unqualifiedCount;
        uint256 lastUpdateTime;
        uint256 totalThickness;    // 总厚度用于计算平均值
    }
    
    mapping(string => PaintBatchStats) private _paintBatchStats;
    
    // 存储批次列表
    string[] private _paintBatchArray;
    mapping(string => bool) private _batchExists;
    
    event PaintingRecorded(string indexed sn, bool qualified);
    event PackingRecorded(string indexed sn);
    event ProductReceived(string indexed fromWorkshop, uint256 count);
    event ProductFinished(string indexed sn);
    event PaintBatchUsed(string indexed batch, string thickness);
    
    // 接收上一车间的产品
    function receiveProducts(uint256 count) public {
        _stats.totalReceived += count;
        _stats.waitingCount += count;
        emit ProductReceived("WorkshopThree", count);
    }
    
    // 记录喷漆数据
    function recordPainting(
        string memory sn,
        string memory paintThickness,
        string memory sprayPressure,
        string memory paintBatch
    ) public {
        // 喷漆质量判断规则
        bool qualified = _checkPaintingQuality(paintThickness, sprayPressure);
        
        _paintingData[sn] = PaintingData(paintThickness, sprayPressure, paintBatch);
        
        // 更新涂料批次统计
        _updatePaintBatchStats(paintBatch, paintThickness, qualified);
        
        // 更新生产统计
        _updatePaintingStats(qualified);
        
        emit PaintingRecorded(sn, qualified);
        emit PaintBatchUsed(paintBatch, paintThickness);
    }
    
    // 记录包装数据
    function recordPacking(
        string memory sn,
        string memory packTime,
        string memory packOperator
    ) public {
        require(bytes(_paintingData[sn].paintThickness).length > 0, "Painting data not found");
        
        _packingData[sn] = PackingData(packTime, packOperator, sn);
        
        // 更新包装统计
        _updatePackingStats();
        
        emit PackingRecorded(sn);
        emit ProductFinished(sn);
    }
    
    // 喷漆质量判断规则
    function _checkPaintingQuality(
        string memory paintThickness,
        string memory sprayPressure
    ) private pure returns (bool) {
        // 规则1: 漆膜厚度在合理范围内 (70-90 μm)
        if (bytes(paintThickness).length == 0) return false;
        uint256 thicknessValue = _stringToUint(paintThickness);
        if (thicknessValue < 70 || thicknessValue > 90) return false;
        
        // 规则2: 喷涂压力在合理范围内 (0.3-0.6 MPa)
        // 注意：这里压力单位是MPa，但传入的值需要是整数（乘以10或100）
        if (bytes(sprayPressure).length == 0) return false;
        uint256 pressureValue = _stringToUint(sprayPressure);
        if (pressureValue < 3 || pressureValue > 6) return false;
        
        return true;
    }
    
    // 辅助函数：字符串转换为uint256
    function _stringToUint(string memory _str) private pure returns (uint256) {
        bytes memory b = bytes(_str);
        uint256 result = 0;
        
        for (uint i = 0; i < b.length; i++) {
            // 检查是否是数字字符
            if (b[i] >= 0x30 && b[i] <= 0x39) {
                result = result * 10 + (uint256(uint8(b[i])) - 48);
            } else if (b[i] != 0x20) { // 如果不是空格，停止转换
                break;
            }
        }
        return result;
    }
    
    // 更新涂料批次统计
    function _updatePaintBatchStats(
        string memory batch,
        string memory thickness,
        bool qualified
    ) private {
        PaintBatchStats storage stats = _paintBatchStats[batch];
        stats.totalUsed++;
        stats.lastUpdateTime = block.timestamp;
        
        // 转换字符串厚度为数值
        uint256 thicknessValue = _stringToUint(thickness);
        stats.totalThickness += thicknessValue;
        
        if (qualified) {
            stats.qualifiedCount++;
        } else {
            stats.unqualifiedCount++;
        }
        
        // 如果是新批次，添加到数组
        if (!_batchExists[batch]) {
            _paintBatchArray.push(batch);
            _batchExists[batch] = true;
        }
    }
    
    // 更新喷漆统计
    function _updatePaintingStats(bool qualified) private {
        if (qualified) {
            _stats.qualifiedCount++;
        } else {
            _stats.unqualifiedCount++;
        }
        _stats.waitingCount--;
    }
    
    // 更新包装统计
    function _updatePackingStats() private {
        _stats.finishedCount++;
    }
    
    // 查询函数
    function getPaintingData(string calldata sn) external view returns (
        string memory paintThickness,
        string memory sprayPressure,
        string memory paintBatch
    ) {
        PaintingData memory data = _paintingData[sn];
        return (data.paintThickness, data.sprayPressure, data.paintBatch);
    }
    
    function getPackingData(string calldata sn) external view returns (
        string memory packTime,
        string memory packOperator
    ) {
        PackingData memory data = _packingData[sn];
        return (data.packTime, data.packOperator);
    }
    
    // 数值查询函数
    function getPaintThicknessValue(string calldata sn) external view returns (uint256) {
        PaintingData memory data = _paintingData[sn];
        return _stringToUint(data.paintThickness);
    }
    
    function getSprayPressureValue(string calldata sn) external view returns (uint256) {
        PaintingData memory data = _paintingData[sn];
        return _stringToUint(data.sprayPressure);
    }
    
    function getPackTimeValue(string calldata sn) external view returns (uint256) {
        PackingData memory data = _packingData[sn];
        return _stringToUint(data.packTime);
    }
    
    // 完整数据查询函数
    function getFullPaintingData(string calldata sn) external view returns (
        string memory paintThickness,
        string memory sprayPressure,
        string memory paintBatch,
        bool exists
    ) {
        PaintingData memory data = _paintingData[sn];
        exists = bytes(data.paintThickness).length > 0;
        return (data.paintThickness, data.sprayPressure, data.paintBatch, exists);
    }
    
    function getFullPackingData(string calldata sn) external view returns (
        string memory packTime,
        string memory packOperator,
        bool exists
    ) {
        PackingData memory data = _packingData[sn];
        exists = bytes(data.packTime).length > 0;
        return (data.packTime, data.packOperator, exists);
    }
    
    // 统计查询函数
    function getProductionStats() public view returns (
        uint256 totalReceived,
        uint256 qualifiedCount,
        uint256 unqualifiedCount,
        uint256 finishedCount,
        uint256 waitingCount,
        uint256 passRate,
        uint256 completionRate
    ) {
        totalReceived = _stats.totalReceived;
        qualifiedCount = _stats.qualifiedCount;
        unqualifiedCount = _stats.unqualifiedCount;
        finishedCount = _stats.finishedCount;
        waitingCount = _stats.waitingCount;
        
        // 计算合格率
        uint256 totalProcessed = qualifiedCount + unqualifiedCount;
        if (totalProcessed > 0) {
            passRate = (qualifiedCount * 100) / totalProcessed;
        } else {
            passRate = 0;
        }
        
        // 计算完成率
        if (totalReceived > 0) {
            completionRate = (finishedCount * 100) / totalReceived;
        } else {
            completionRate = 0;
        }
    }
    
    // 查询涂料批次使用情况
    function getPaintBatchStats(string memory batch) public view returns (
        uint256 totalUsed,
        uint256 qualifiedCount,
        uint256 unqualifiedCount,
        uint256 passRate,
        uint256 lastUpdateTime,
        uint256 avgThickness
    ) {
        PaintBatchStats memory stats = _paintBatchStats[batch];
        totalUsed = stats.totalUsed;
        qualifiedCount = stats.qualifiedCount;
        unqualifiedCount = stats.unqualifiedCount;
        lastUpdateTime = stats.lastUpdateTime;
        
        // 计算合格率
        if (totalUsed > 0) {
            passRate = (qualifiedCount * 100) / totalUsed;
            avgThickness = stats.totalThickness / totalUsed;
        } else {
            passRate = 0;
            avgThickness = 0;
        }
    }
    
    // 获取涂料批次数量
    function getPaintBatchCount() public view returns (uint256) {
        return _paintBatchArray.length;
    }
    
    // 获取特定索引的批次
    function getPaintBatchByIndex(uint256 index) public view returns (
        string memory batch,
        uint256 totalUsed,
        uint256 passRate,
        uint256 avgThickness
    ) {
        require(index < _paintBatchArray.length, "Index out of bounds");
        
        batch = _paintBatchArray[index];
        PaintBatchStats memory stats = _paintBatchStats[batch];
        totalUsed = stats.totalUsed;
        
        // 计算合格率和平均厚度
        if (totalUsed > 0) {
            passRate = (stats.qualifiedCount * 100) / totalUsed;
            avgThickness = stats.totalThickness / totalUsed;
        } else {
            passRate = 0;
            avgThickness = 0;
        }
    }
    
    // 检查批次是否存在
    function hasPaintBatch(string memory batch) public view returns (bool) {
        return _paintBatchStats[batch].totalUsed > 0;
    }
    
    function hasData(string calldata sn) external view returns (bool) {
        return bytes(_paintingData[sn].paintThickness).length > 0;
    }
}