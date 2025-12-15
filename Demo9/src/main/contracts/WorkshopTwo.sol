// SPDX-License-Identifier: MIT
pragma solidity ^0.6.0;

contract WorkshopTwo {
    struct DrillingData {// 钻中心孔
        string holeSize;        // 孔径尺寸
        string holeDepth;       // 钻孔深度
        string equipmentSpeed;  // 设备转速
    }
    
    struct HeatTreatmentData {// 调质热处理
        string heatingTemp;     // 加热温度
        string holdTime;        // 保温时间
        string coolingRate;     // 冷却速率
    }
    
    struct TurningData {// 粗车盘
        string rotationSpeed;   // 切削转速
        string feedRate;        // 进给量
        string tolerance;       // 盘部直径公差
        string toolType;        // 刀具型号
    }
    
    struct GearProcessingData {// 加工齿
        string gearAccuracy;    // 齿形精度
        string surfaceHardness; // 表面硬度
        string equipmentNo;     // 设备编号
    }
    
    mapping(string => DrillingData) private _drillingData;
    mapping(string => HeatTreatmentData) private _heatTreatmentData;
    mapping(string => TurningData) private _turningData;
    mapping(string => GearProcessingData) private _gearData;
    
    // 统计数据
    struct ProductionStats {
        uint256 totalReceived;      // 接收总量（来自一车间的合格品）
        uint256 qualifiedCount;     // 本车间合格数量
        uint256 unqualifiedCount;   // 本车间不合格数量
        uint256 scrapCount;         // 报废数量
    }
    
    ProductionStats private _stats;
    
    event DrillingRecorded(string indexed sn, bool qualified);
    event HeatTreatmentRecorded(string indexed sn, bool qualified);
    event TurningRecorded(string indexed sn, bool qualified);
    event GearProcessingRecorded(string indexed sn, bool qualified);
    event ProductReceived(string indexed fromWorkshop, uint256 count);
    event ProductScrapped(string indexed sn, string reason);
    
    // 接收上一车间的产品 - 公共函数使用 memory
    function receiveProducts(uint256 count) public {
        _stats.totalReceived += count;
        emit ProductReceived("WorkshopOne", count);
    }
    
    // 记录钻孔数据
    function recordDrilling(
        string memory sn,
        string memory holeSize,
        string memory holeDepth,
        string memory equipmentSpeed
    ) public {
        // 钻孔质量判断规则
        bool qualified = _checkDrillingQuality(holeSize, holeDepth, equipmentSpeed);
        
        _drillingData[sn] = DrillingData(holeSize, holeDepth, equipmentSpeed);
        
        // 更新统计
        _updateStats(qualified);
        
        emit DrillingRecorded(sn, qualified);
    }
    
    // 记录热处理数据
    function recordHeatTreatment(
        string memory sn,
        string memory heatingTemp,
        string memory holdTime,
        string memory coolingRate
    ) public {
        require(_hasDrillingData(sn), "Drilling data not found");
        
        // 热处理质量判断规则
        bool qualified = _checkHeatTreatmentQuality(heatingTemp, holdTime, coolingRate);
        
        _heatTreatmentData[sn] = HeatTreatmentData(heatingTemp, holdTime, coolingRate);
        
        // 更新统计
        _updateStats(qualified);
        
        emit HeatTreatmentRecorded(sn, qualified);
    }
    
    // 记录车削数据
    function recordTurning(
        string memory sn,
        string memory rotationSpeed,
        string memory feedRate,
        string memory tolerance,
        string memory toolType
    ) public {
        require(_hasHeatTreatmentData(sn), "Heat treatment data not found");
        
        // 车削质量判断规则
        bool qualified = _checkTurningQuality(rotationSpeed, feedRate, tolerance);
        
        _turningData[sn] = TurningData(rotationSpeed, feedRate, tolerance, toolType);
        
        // 更新统计
        _updateStats(qualified);
        
        emit TurningRecorded(sn, qualified);
    }
    
    // 记录齿轮加工数据
    function recordGearProcessing(
        string memory sn,
        string memory gearAccuracy,
        string memory surfaceHardness,
        string memory equipmentNo
    ) public {
        require(_hasTurningData(sn), "Turning data not found");
        
        // 齿轮加工质量判断规则
        bool qualified = _checkGearProcessingQuality(gearAccuracy, surfaceHardness);
        
        _gearData[sn] = GearProcessingData(gearAccuracy, surfaceHardness, equipmentNo);
        
        // 更新统计
        _updateStats(qualified);
        
        emit GearProcessingRecorded(sn, qualified);
    }
    
    // 检查是否有钻孔数据
    function _hasDrillingData(string memory sn) private view returns (bool) {
        return bytes(_drillingData[sn].holeSize).length > 0;
    }
    
    // 检查是否有热处理数据
    function _hasHeatTreatmentData(string memory sn) private view returns (bool) {
        return bytes(_heatTreatmentData[sn].heatingTemp).length > 0;
    }
    
    // 检查是否有车削数据
    function _hasTurningData(string memory sn) private view returns (bool) {
        return bytes(_turningData[sn].rotationSpeed).length > 0;
    }
    
    // 钻孔质量判断规则
    function _checkDrillingQuality(
        string memory holeSize,
        string memory holeDepth,
        string memory equipmentSpeed
    ) private pure returns (bool) {
        // 规则1: 检查尺寸格式
        bytes memory sizeBytes = bytes(holeSize);
        if (sizeBytes.length == 0) return false;
        
        // 规则2: 应包含"mm"单位
        bool hasUnit = false;
        for (uint i = 0; i < sizeBytes.length - 1; i++) {
            if (sizeBytes[i] == 0x6D && sizeBytes[i + 1] == 0x6D) { // "mm"
                hasUnit = true;
                break;
            }
        }
        if (!hasUnit) return false;
        
        // 规则3: 钻孔深度检查
        if (bytes(holeDepth).length == 0) return false;
        uint256 depthValue = _stringToUint(holeDepth);
        if (depthValue != 15) return false;
        
        // 规则4: 设备速度在合理范围内 (800-1200 RPM)
        if (bytes(equipmentSpeed).length == 0) return false;
        uint256 speedValue = _stringToUint(equipmentSpeed);
        if (speedValue < 800 || speedValue > 1200) return false;
        
        return true;
    }
    
    // 热处理质量判断规则
    function _checkHeatTreatmentQuality(
        string memory heatingTemp,
        string memory holdTime,
        string memory coolingRate
    ) private pure returns (bool) {
        // 规则1: 加热温度在合理范围内 (850-900℃)
        if (bytes(heatingTemp).length == 0) return false;
        uint256 tempValue = _stringToUint(heatingTemp);
        if (tempValue < 850 || tempValue > 900) return false;
        
        // 规则2: 保温时间在合理范围内 (60-90 min)
        if (bytes(holdTime).length == 0) return false;
        uint256 timeValue = _stringToUint(holdTime);
        if (timeValue < 60 || timeValue > 90) return false;
        
        // 规则3: 冷却速率在合理范围内 (10-20℃/s)
        if (bytes(coolingRate).length == 0) return false;
        uint256 rateValue = _stringToUint(coolingRate);
        if (rateValue < 10 || rateValue > 20) return false;
        
        return true;
    }
    
    // 车削质量判断规则
    function _checkTurningQuality(
        string memory rotationSpeed,
        string memory feedRate,
        string memory tolerance
    ) private pure returns (bool) {
        // 规则1: 转速在合理范围内 (600-1000 RPM)
        if (bytes(rotationSpeed).length == 0) return false;
        uint256 speedValue = _stringToUint(rotationSpeed);
        if (speedValue < 600 || speedValue > 1000) return false;
        
        // 规则2: 进给量在合理范围内 (0.1-0.3 mm/r)
        if (bytes(feedRate).length == 0) return false;
        uint256 feedValue = _stringToUint(feedRate);
        if (feedValue < 100 || feedValue > 300) return false;
        
        // 规则3: 公差检查（20mmX30mm）
        bytes memory toleranceBytes = bytes(tolerance);
        if (toleranceBytes.length == 0) return false;
        
        return true;
    }
    
    // 齿轮加工质量判断规则
    function _checkGearProcessingQuality(
        string memory gearAccuracy,
        string memory surfaceHardness
    ) private pure returns (bool) {
        // 规则1: 齿形精度格式检查（0.01mm）
        bytes memory accuracyBytes = bytes(gearAccuracy);
        if (accuracyBytes.length == 0) return false;
        
        // 规则2: 表面硬度在合理范围内 (HRC55-62)
        if (bytes(surfaceHardness).length == 0) return false;
        uint256 hardnessValue = _stringToUint(surfaceHardness);
        if (hardnessValue < 55 || hardnessValue > 62) return false;
        
        return true;
    }
    

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
    
    // 更新统计信息
    function _updateStats(bool qualified) private {
        if (qualified) {
            _stats.qualifiedCount++;
        } else {
            _stats.unqualifiedCount++;
        }
    }
    
    // 标记为报废
    function markAsScrap(string memory sn) public {
        _stats.scrapCount++;
        emit ProductScrapped(sn, "质量不合格");
    }
    
    // 带原因的报废标记
    function markAsScrapWithReason(string memory sn, string memory reason) public {
        _stats.scrapCount++;
        emit ProductScrapped(sn, reason);
    }
    
    // 查询函数
    function getDrillingData(string calldata sn) external view returns (
        string memory holeSize,
        string memory holeDepth,
        string memory equipmentSpeed
    ) {
        DrillingData memory data = _drillingData[sn];
        return (data.holeSize, data.holeDepth, data.equipmentSpeed);
    }
    
    function getHeatTreatmentData(string calldata sn) external view returns (
        string memory heatingTemp,
        string memory holdTime,
        string memory coolingRate
    ) {
        HeatTreatmentData memory data = _heatTreatmentData[sn];
        return (data.heatingTemp, data.holdTime, data.coolingRate);
    }
    
    function getTurningData(string calldata sn) external view returns (
        string memory rotationSpeed,
        string memory feedRate,
        string memory tolerance,
        string memory toolType
    ) {
        TurningData memory data = _turningData[sn];
        return (data.rotationSpeed, data.feedRate, data.tolerance, data.toolType);
    }
    
    function getGearProcessingData(string calldata sn) external view returns (
        string memory gearAccuracy,
        string memory surfaceHardness,
        string memory equipmentNo
    ) {
        GearProcessingData memory data = _gearData[sn];
        return (data.gearAccuracy, data.surfaceHardness, data.equipmentNo);
    }
    
    // 统计查询
    function getProductionStats() public view returns (
        uint256 totalReceived,
        uint256 qualifiedCount,
        uint256 unqualifiedCount,
        uint256 scrapCount,
        uint256 passRate,
        uint256 remainingCount
    ) {
        totalReceived = _stats.totalReceived;
        qualifiedCount = _stats.qualifiedCount;
        unqualifiedCount = _stats.unqualifiedCount;
        scrapCount = _stats.scrapCount;
        
        // 计算合格率
        uint256 totalProcessed = qualifiedCount + unqualifiedCount;
        if (totalProcessed > 0) {
            passRate = (qualifiedCount * 100) / totalProcessed;
        } else {
            passRate = 0;
        }
        
        // 计算剩余数量
        uint256 totalConsumed = qualifiedCount + unqualifiedCount + scrapCount;
        if (totalReceived > totalConsumed) {
            remainingCount = totalReceived - totalConsumed;
        } else {
            remainingCount = 0;
        }
    }
    
    // 数值查询函数
    function getHoleDepthValue(string calldata sn) external view returns (uint256) {
        DrillingData memory data = _drillingData[sn];
        return _stringToUint(data.holeDepth);
    }
    
    function getEquipmentSpeedValue(string calldata sn) external view returns (uint256) {
        DrillingData memory data = _drillingData[sn];
        return _stringToUint(data.equipmentSpeed);
    }
    
    function getHeatingTempValue(string calldata sn) external view returns (uint256) {
        HeatTreatmentData memory data = _heatTreatmentData[sn];
        return _stringToUint(data.heatingTemp);
    }
    
    function getHoldTimeValue(string calldata sn) external view returns (uint256) {
        HeatTreatmentData memory data = _heatTreatmentData[sn];
        return _stringToUint(data.holdTime);
    }
    
    function getCoolingRateValue(string calldata sn) external view returns (uint256) {
        HeatTreatmentData memory data = _heatTreatmentData[sn];
        return _stringToUint(data.coolingRate);
    }
    
    function getRotationSpeedValue(string calldata sn) external view returns (uint256) {
        TurningData memory data = _turningData[sn];
        return _stringToUint(data.rotationSpeed);
    }
    
    function getFeedRateValue(string calldata sn) external view returns (uint256) {
        TurningData memory data = _turningData[sn];
        return _stringToUint(data.feedRate);
    }
    
    function getSurfaceHardnessValue(string calldata sn) external view returns (uint256) {
        GearProcessingData memory data = _gearData[sn];
        return _stringToUint(data.surfaceHardness);
    }
    
    // 完整数据查询函数
    function getFullDrillingData(string calldata sn) external view returns (
        string memory holeSize,
        string memory holeDepth,
        string memory equipmentSpeed,
        bool exists
    ) {
        DrillingData memory data = _drillingData[sn];
        exists = bytes(data.holeSize).length > 0;
        return (data.holeSize, data.holeDepth, data.equipmentSpeed, exists);
    }
    
    function getFullHeatTreatmentData(string calldata sn) external view returns (
        string memory heatingTemp,
        string memory holdTime,
        string memory coolingRate,
        bool exists
    ) {
        HeatTreatmentData memory data = _heatTreatmentData[sn];
        exists = bytes(data.heatingTemp).length > 0;
        return (data.heatingTemp, data.holdTime, data.coolingRate, exists);
    }
    
    function getFullTurningData(string calldata sn) external view returns (
        string memory rotationSpeed,
        string memory feedRate,
        string memory tolerance,
        string memory toolType,
        bool exists
    ) {
        TurningData memory data = _turningData[sn];
        exists = bytes(data.rotationSpeed).length > 0;
        return (data.rotationSpeed, data.feedRate, data.tolerance, data.toolType, exists);
    }
    
    function getFullGearProcessingData(string calldata sn) external view returns (
        string memory gearAccuracy,
        string memory surfaceHardness,
        string memory equipmentNo,
        bool exists
    ) {
        GearProcessingData memory data = _gearData[sn];
        exists = bytes(data.gearAccuracy).length > 0;
        return (data.gearAccuracy, data.surfaceHardness, data.equipmentNo, exists);
    }
    
    function hasData(string calldata sn) external view returns (bool) {
        return _hasDrillingData(sn);
    }
}