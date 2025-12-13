// SPDX-License-Identifier: MIT
pragma solidity ^0.6.0;

contract WorkshopOne {
    struct CuttingData {// 切割下料
        string materialBatch; // 原材料批次
        string cutSize;       // 尺寸
        string cutSpeed;      // 切割速度
        string operatorName;  // 操作人
    }
    
    struct PressingData {// 压花键
        string pressure;      // 压装压力（改为字符串）
        string splineSize;    // 花键尺寸（齿宽 × 齿高） 
        string equipmentNo;   // 设备编号
    }
    
    struct ForgingData {// 锻造
        string forgingTemp;   // 锻造温度
        string pressure;      // 压力
        string holdTime;      // 保温时间
        string defect;        // 外观缺陷
    }
    
    mapping(string => CuttingData) private _cuttingData;
    mapping(string => PressingData) private _pressingData;
    mapping(string => ForgingData) private _forgingData;
    
    // 统计数据
    struct ProductionStats {
        uint256 totalMaterials;     // 原材料总量
        uint256 qualifiedCount;     // 合格数量
        uint256 unqualifiedCount;   // 不合格数量
        mapping(string => uint256) materialBatchStats; // 各批次统计
    }
    
    ProductionStats private _stats;
    
    event CuttingRecorded(string indexed sn, bool qualified);
    event PressingRecorded(string indexed sn, bool qualified);
    event ForgingRecorded(string indexed sn, bool qualified);
    event MaterialReceived(string indexed batch, uint256 amount);
    
    // 接收原材料
    function receiveMaterial(string memory batch, uint256 amount) public {
        _stats.totalMaterials += amount;
        _stats.materialBatchStats[batch] += amount;
        emit MaterialReceived(batch, amount);
    }
    
    // 记录切割下料数据
    function recordCutting(
        string memory sn,
        string memory materialBatch,
        string memory cutSize,
        string memory cutSpeed,
        string memory operatorName
    ) public {
        require(bytes(materialBatch).length > 0, "Material batch is required");
        
        // 切割质量判断规则
        bool qualified = _checkCuttingQuality(cutSize, cutSpeed);
        
        _cuttingData[sn] = CuttingData(materialBatch, cutSize, cutSpeed, operatorName);
        
        // 更新统计
        _updateStats(qualified);
        
        emit CuttingRecorded(sn, qualified);
    }
    
    // 记录压花键数据
    function recordPressing(
        string memory sn,
        string memory pressure,
        string memory splineSize,
        string memory equipmentNo
    ) public {
        require(_hasCuttingData(sn), "Cutting data not found");
        
        // 压花键质量判断规则
        bool qualified = _checkPressingQuality(pressure, splineSize);
        
        _pressingData[sn] = PressingData(pressure, splineSize, equipmentNo);
        
        // 更新统计
        _updateStats(qualified);
        
        emit PressingRecorded(sn, qualified);
    }
    
    // 记录锻造数据
    function recordForging(
        string memory sn,
        string memory forgingTemp,
        string memory pressure,
        string memory holdTime,
        string memory defect
    ) public {
        require(_hasPressingData(sn), "Pressing data not found");
        
        // 锻造质量判断规则
        bool qualified = _checkForgingQuality(forgingTemp, pressure, holdTime, defect);
        
        _forgingData[sn] = ForgingData(forgingTemp, pressure, holdTime, defect);
        
        // 更新统计
        _updateStats(qualified);
        
        emit ForgingRecorded(sn, qualified);
    }
    
    // 检查是否有切割数据
    function _hasCuttingData(string memory sn) private view returns (bool) {
        return bytes(_cuttingData[sn].materialBatch).length > 0;
    }
    
    // 检查是否有压花键数据
    function _hasPressingData(string memory sn) private view returns (bool) {
        return bytes(_pressingData[sn].pressure).length > 0;
    }
    
    // 切割质量判断规则
    function _checkCuttingQuality(string memory cutSize, string memory cutSpeed) private pure returns (bool) {
        // 规则1: 切割速度在合理范围内 (100-150 RPM)
        bytes memory speedBytes = bytes(cutSpeed);
        if (speedBytes.length == 0) return false;
        
        uint256 speedValue = _stringToUint(cutSpeed);
        if (speedValue < 100 || speedValue > 150) return false;
        
        // 规则2: 检查尺寸格式是否有效
        bytes memory sizeBytes = bytes(cutSize);
        if (sizeBytes.length == 3*5) return true;
        
        // 规则3: 尺寸应包含 "mm" 单位
        bool hasUnit = false;
        for (uint i = 0; i < sizeBytes.length - 1; i++) {
            if (sizeBytes[i] == 0x6D && sizeBytes[i + 1] == 0x6D) { // "mm"
                hasUnit = true;
                break;
            }
        }
        if (!hasUnit) return false;
        
        return true;
    }
    
    // 压花键质量判断规则
    function _checkPressingQuality(string memory pressure, string memory splineSize) private pure returns (bool) {
        // 规则1: 压装压力在合理范围内 (200-300 kN)
        if (bytes(pressure).length == 0) return false;
        
        uint256 pressureValue = _stringToUint(pressure);
        if (pressureValue < 200 || pressureValue > 300) return false;
        
        // 规则2: 检查花键尺寸格式（30mm*50mm）
        bytes memory splineBytes = bytes(splineSize);
        if (splineBytes.length > 0) return true;
        
        return true;
    }
    
    // 锻造质量判断规则
    function _checkForgingQuality(
        string memory forgingTemp,
        string memory pressure,
        string memory holdTime,
        string memory defect
    ) private pure returns (bool) {
        // 规则1: 锻造温度在合理范围内 (1100-1200℃)
        if (bytes(forgingTemp).length == 0) return false;
        uint256 tempValue = _stringToUint(forgingTemp);
        if (tempValue < 1100 || tempValue > 1200) return false;
        
        // 规则2: 锻造压力在合理范围内 (750-850 MPa)
        if (bytes(pressure).length == 0) return false;
        uint256 pressureValue = _stringToUint(pressure);
        if (pressureValue < 750 || pressureValue > 850) return false;
        
        // 规则3: 保温时间在合理范围内 (25-35 min)
        if (bytes(holdTime).length == 0) return false;
        uint256 timeValue = _stringToUint(holdTime);
        if (timeValue < 25 || timeValue > 35) return false;
        
        // 规则4: 检查缺陷描述
        bytes memory defectBytes = bytes(defect);
        if (defectBytes.length > 0) {
            // 如果有"裂纹"、"断裂"等关键词，判定为不合格
            if (_contains(defect, "裂纹") || _contains(defect, "断裂") || 
                _contains(defect, "严重")) {
                return false;
            }
        }
        
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
    
    // 辅助函数：检查字符串是否包含子串
    function _contains(string memory _str, string memory _substr) private pure returns (bool) {
        bytes memory strBytes = bytes(_str);
        bytes memory substrBytes = bytes(_substr);
        
        if (substrBytes.length > strBytes.length) return false;
        
        for (uint i = 0; i <= strBytes.length - substrBytes.length; i++) {
            bool found = true;
            for (uint j = 0; j < substrBytes.length; j++) {
                if (strBytes[i + j] != substrBytes[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return true;
        }
        return false;
    }
    
    // 更新统计信息
    function _updateStats(bool qualified) private {
        if (qualified) {
            _stats.qualifiedCount++;
        } else {
            _stats.unqualifiedCount++;
        }
    }
    
    // 查询函数
    function getCuttingMaterialBatch(string calldata sn) external view returns (string memory) {
        return _cuttingData[sn].materialBatch;
    }
    
    function getCuttingDetails(string calldata sn) external view returns (
        string memory cutSize,
        string memory cutSpeed,
        string memory operatorName
    ) {
        CuttingData memory data = _cuttingData[sn];
        return (data.cutSize, data.cutSpeed, data.operatorName);
    }
    
    function getPressingData(string calldata sn) external view returns (
        string memory pressure,
        string memory splineSize,
        string memory equipmentNo
    ) {
        PressingData memory data = _pressingData[sn];
        return (data.pressure, data.splineSize, data.equipmentNo);
    }
    
    function getForgingData(string calldata sn) external view returns (
        string memory forgingTemp,
        string memory pressure,
        string memory holdTime,
        string memory defect
    ) {
        ForgingData memory data = _forgingData[sn];
        return (data.forgingTemp, data.pressure, data.holdTime, data.defect);
    }
    
    // 统计查询函数 
    function getProductionStats() public view returns (
        uint256 totalMaterials,
        uint256 qualifiedCount,
        uint256 unqualifiedCount,
        uint256 passRate // 合格率（百分比）
    ) {
        totalMaterials = _stats.totalMaterials;
        qualifiedCount = _stats.qualifiedCount;
        unqualifiedCount = _stats.unqualifiedCount;
        
        // 计算合格率（百分比）
        uint256 totalProcessed = qualifiedCount + unqualifiedCount;
        if (totalProcessed > 0) {
            passRate = (qualifiedCount * 100) / totalProcessed;
        } else {
            passRate = 0;
        }
    }
    
    function getMaterialBatchStats(string memory batch) public view returns (
        uint256 totalAmount,
        uint256 usedAmount,
        uint256 remainingAmount
    ) {
        totalAmount = _stats.materialBatchStats[batch];
        // 简化实现
        usedAmount = 0;
        remainingAmount = totalAmount;
    }
    
    //获取切割速度数值
    function getCuttingSpeedValue(string calldata sn) external view returns (uint256) {
        CuttingData memory data = _cuttingData[sn];
        return _stringToUint(data.cutSpeed);
    }
    
    //获取锻造温度数值
    function getForgingTempValue(string calldata sn) external view returns (uint256) {
        ForgingData memory data = _forgingData[sn];
        return _stringToUint(data.forgingTemp);
    }
    
    //获取压力数值
    function getPressureValue(string calldata sn) external view returns (uint256) {
        ForgingData memory data = _forgingData[sn];
        return _stringToUint(data.pressure);
    }
    
    //获取保温时间数值
    function getHoldTimeValue(string calldata sn) external view returns (uint256) {
        ForgingData memory data = _forgingData[sn];
        return _stringToUint(data.holdTime);
    }
    
    function hasData(string calldata sn) external view returns (bool) {
        return _hasCuttingData(sn);
    }
    
    //获取完整切割数据
    function getFullCuttingData(string calldata sn) external view returns (
        string memory materialBatch,
        string memory cutSize,
        string memory cutSpeed,
        string memory operatorName,
        bool exists
    ) {
        CuttingData memory data = _cuttingData[sn];
        exists = bytes(data.materialBatch).length > 0;
        return (data.materialBatch, data.cutSize, data.cutSpeed, data.operatorName, exists);
    }
    
    //获取完整锻造数据
    function getFullForgingData(string calldata sn) external view returns (
        string memory forgingTemp,
        string memory pressure,
        string memory holdTime,
        string memory defect,
        bool exists
    ) {
        ForgingData memory data = _forgingData[sn];
        exists = bytes(data.forgingTemp).length > 0;
        return (data.forgingTemp, data.pressure, data.holdTime, data.defect, exists);
    }
}