// SPDX-License-Identifier: MIT
pragma solidity ^0.6.0;

contract WorkshopThree {
    struct QuenchingData {
        string quenchingTemp;    // 淬火温度
        string coolingMedium;    // 冷却介质
        string hardness;         // 硬度
    }
    
    struct StraighteningData {
        string straighteningForce; // 校直力
        string temperingTemp;      // 回火温度
        string holdTime;           // 保温时间
    }
    
    struct InspectionData {
        string defectLevel;       // 缺陷等级
        string defectPosition;    // 缺陷位置
        string inspector;         // 检验员
    }
    
    struct FineTurningData {
        string tolerance;         // 公差
        string holeAccuracy;      // 孔位精度
        string equipmentNo;       // 设备编号
    }
    
    mapping(string => QuenchingData) private _quenchingData;
    mapping(string => StraighteningData) private _straighteningData;
    mapping(string => InspectionData) private _inspectionData;
    mapping(string => FineTurningData) private _fineTurningData;
    
    // 统计数据
    struct ProductionStats {
        uint256 totalReceived;      // 接收总量
        uint256 qualifiedCount;     // 合格数量
        uint256 unqualifiedCount;   // 不合格数量
        uint256 reworkCount;        // 返修数量
        uint256 scrapCount;         // 报废数量
    }
    
    ProductionStats private _stats;
    
    event QuenchingRecorded(string indexed sn, bool qualified);
    event StraighteningRecorded(string indexed sn, bool qualified);
    event InspectionRecorded(string indexed sn, string defectLevel);
    event FineTurningRecorded(string indexed sn, bool qualified);
    event ProductReceived(string indexed fromWorkshop, uint256 count);
    event ProductReworked(string indexed sn);
    event ProductScrapped(string indexed sn);
    
    // 接收上一车间的产品
    function receiveProducts(uint256 count) public {
        _stats.totalReceived += count;
        emit ProductReceived("WorkshopTwo", count);
    }
    
    // 记录淬火数据
    function recordQuenching(
        string memory sn,
        string memory quenchingTemp,
        string memory coolingMedium,
        string memory hardness
    ) public {
        // 淬火质量判断规则
        bool qualified = _checkQuenchingQuality(quenchingTemp, coolingMedium, hardness);
        
        _quenchingData[sn] = QuenchingData(quenchingTemp, coolingMedium, hardness);
        
        // 更新统计
        _updateStats(qualified);
        
        emit QuenchingRecorded(sn, qualified);
    }
    
    // 记录校直回火数据
    function recordStraightening(
        string memory sn,
        string memory straighteningForce,
        string memory temperingTemp,
        string memory holdTime
    ) public {
        require(_hasQuenchingData(sn), "Quenching data not found");
        
        // 校直回火质量判断规则
        bool qualified = _checkStraighteningQuality(straighteningForce, temperingTemp, holdTime);
        
        _straighteningData[sn] = StraighteningData(straighteningForce, temperingTemp, holdTime);
        
        // 更新统计
        _updateStats(qualified);
        
        emit StraighteningRecorded(sn, qualified);
    }
    
    // 记录探伤数据
    function recordInspection(
        string memory sn,
        string memory defectLevel,
        string memory defectPosition,
        string memory inspector
    ) public {
        require(_hasStraighteningData(sn), "Straightening data not found");
        
        _inspectionData[sn] = InspectionData(defectLevel, defectPosition, inspector);
        
        // 根据缺陷等级更新统计
        _updateInspectionStats(defectLevel);
        
        emit InspectionRecorded(sn, defectLevel);
    }
    
    // 记录精车数据
    function recordFineTurning(
        string memory sn,
        string memory tolerance,
        string memory holeAccuracy,
        string memory equipmentNo
    ) public {
        require(_hasInspectionData(sn), "Inspection data not found");
        
        // 精车质量判断规则
        bool qualified = _checkFineTurningQuality(tolerance, holeAccuracy);
        
        _fineTurningData[sn] = FineTurningData(tolerance, holeAccuracy, equipmentNo);
        
        // 更新统计
        _updateStats(qualified);
        
        emit FineTurningRecorded(sn, qualified);
    }
    
    // 检查是否有淬火数据
    function _hasQuenchingData(string memory sn) private view returns (bool) {
        return bytes(_quenchingData[sn].quenchingTemp).length > 0;
    }
    
    // 检查是否有校直回火数据
    function _hasStraighteningData(string memory sn) private view returns (bool) {
        return bytes(_straighteningData[sn].straighteningForce).length > 0;
    }
    
    // 检查是否有探伤数据
    function _hasInspectionData(string memory sn) private view returns (bool) {
        return bytes(_inspectionData[sn].defectLevel).length > 0;
    }
    
    // 淬火质量判断规则
    function _checkQuenchingQuality(
        string memory quenchingTemp,
        string memory coolingMedium,
        string memory hardness
    ) private pure returns (bool) {
        // 规则1: 淬火温度在合理范围内 (920-950℃)
        if (bytes(quenchingTemp).length == 0) return false;
        uint256 tempValue = _stringToUint(quenchingTemp);
        if (tempValue < 920 || tempValue > 950) return false;
        
        // 规则2: 冷却介质不能为空（水）
        bytes memory mediumBytes = bytes(coolingMedium);
        if (mediumBytes.length == 0) return false;
        
        // 规则3: 硬度在合理范围内 (HRC58-65)
        if (bytes(hardness).length == 0) return false;
        uint256 hardnessValue = _stringToUint(hardness);
        if (hardnessValue < 58 || hardnessValue > 65) return false;
        
        return true;
    }
    
    // 校直回火质量判断规则
    function _checkStraighteningQuality(
        string memory straighteningForce,
        string memory temperingTemp,
        string memory holdTime
    ) private pure returns (bool) {
        // 规则1: 校直力在合理范围内 (500-800 kN)
        if (bytes(straighteningForce).length == 0) return false;
        uint256 forceValue = _stringToUint(straighteningForce);
        if (forceValue < 500 || forceValue > 800) return false;
        
        // 规则2: 回火温度在合理范围内 (400-450℃)
        if (bytes(temperingTemp).length == 0) return false;
        uint256 tempValue = _stringToUint(temperingTemp);
        if (tempValue < 400 || tempValue > 450) return false;
        
        // 规则3: 保温时间在合理范围内 (120-180 min)
        if (bytes(holdTime).length == 0) return false;
        uint256 timeValue = _stringToUint(holdTime);
        if (timeValue < 120 || timeValue > 180) return false;
        
        return true;
    }
    
    
    
    // 更新探伤统计
    function _updateInspectionStats(string memory defectLevel) private {
        // 根据缺陷等级判断
        if (_compareStrings(defectLevel, "无缺陷")) {
            _stats.qualifiedCount++;
        } else if (_compareStrings(defectLevel, "轻微缺陷") || 
                  _compareStrings(defectLevel, "可修复缺陷")) {
            _stats.reworkCount++;
        } else {
            _stats.unqualifiedCount++;
        }
    }
    
    // 更新统计信息
    function _updateStats(bool qualified) private {
        if (qualified) {
            _stats.qualifiedCount++;
        } else {
            _stats.unqualifiedCount++;
        }
    }

    // 精车质量判断规则
    function _checkFineTurningQuality(
        string memory tolerance,
        string memory holeAccuracy
    ) private pure returns (bool) {
        // 规则1: 公差格式检查
        bytes memory toleranceBytes = bytes(tolerance);
        if (toleranceBytes.length == 0) return false;
        
        // 规则2: 孔位精度检查
        bytes memory accuracyBytes = bytes(holeAccuracy);
        if (accuracyBytes.length == 0) return false;
        
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
    
    // 标记为返修
    function markAsRework(string memory sn) public {
        _stats.reworkCount++;
        emit ProductReworked(sn);
    }
    
    // 标记为报废
    function markAsScrap(string memory sn) public {
        _stats.scrapCount++;
        emit ProductScrapped(sn);
    }
    
    // 字符串比较辅助函数
    function _compareStrings(string memory a, string memory b) private pure returns (bool) {
        return keccak256(abi.encodePacked(a)) == keccak256(abi.encodePacked(b));
    }
    
    // 查询函数
    function getQuenchingData(string calldata sn) external view returns (
        string memory quenchingTemp,
        string memory coolingMedium,
        string memory hardness
    ) {
        QuenchingData memory data = _quenchingData[sn];
        return (data.quenchingTemp, data.coolingMedium, data.hardness);
    }
    
    function getStraighteningData(string calldata sn) external view returns (
        string memory straighteningForce,
        string memory temperingTemp,
        string memory holdTime
    ) {
        StraighteningData memory data = _straighteningData[sn];
        return (data.straighteningForce, data.temperingTemp, data.holdTime);
    }
    
    function getInspectionData(string calldata sn) external view returns (
        string memory defectLevel,
        string memory defectPosition,
        string memory inspector
    ) {
        InspectionData memory data = _inspectionData[sn];
        return (data.defectLevel, data.defectPosition, data.inspector);
    }
    
    function getFineTurningData(string calldata sn) external view returns (
        string memory tolerance,
        string memory holeAccuracy,
        string memory equipmentNo
    ) {
        FineTurningData memory data = _fineTurningData[sn];
        return (data.tolerance, data.holeAccuracy, data.equipmentNo);
    }
    
    // 数值查询函数
    function getQuenchingTempValue(string calldata sn) external view returns (uint256) {
        QuenchingData memory data = _quenchingData[sn];
        return _stringToUint(data.quenchingTemp);
    }
    
    function getHardnessValue(string calldata sn) external view returns (uint256) {
        QuenchingData memory data = _quenchingData[sn];
        return _stringToUint(data.hardness);
    }
    
    function getStraighteningForceValue(string calldata sn) external view returns (uint256) {
        StraighteningData memory data = _straighteningData[sn];
        return _stringToUint(data.straighteningForce);
    }
    
    function getTemperingTempValue(string calldata sn) external view returns (uint256) {
        StraighteningData memory data = _straighteningData[sn];
        return _stringToUint(data.temperingTemp);
    }
    
    function getHoldTimeValue(string calldata sn) external view returns (uint256) {
        StraighteningData memory data = _straighteningData[sn];
        return _stringToUint(data.holdTime);
    }
    
    // 完整数据查询函数
    function getFullQuenchingData(string calldata sn) external view returns (
        string memory quenchingTemp,
        string memory coolingMedium,
        string memory hardness,
        bool exists
    ) {
        QuenchingData memory data = _quenchingData[sn];
        exists = bytes(data.quenchingTemp).length > 0;
        return (data.quenchingTemp, data.coolingMedium, data.hardness, exists);
    }
    
    function getFullStraighteningData(string calldata sn) external view returns (
        string memory straighteningForce,
        string memory temperingTemp,
        string memory holdTime,
        bool exists
    ) {
        StraighteningData memory data = _straighteningData[sn];
        exists = bytes(data.straighteningForce).length > 0;
        return (data.straighteningForce, data.temperingTemp, data.holdTime, exists);
    }
    
    function getFullInspectionData(string calldata sn) external view returns (
        string memory defectLevel,
        string memory defectPosition,
        string memory inspector,
        bool exists
    ) {
        InspectionData memory data = _inspectionData[sn];
        exists = bytes(data.defectLevel).length > 0;
        return (data.defectLevel, data.defectPosition, data.inspector, exists);
    }
    
    function getFullFineTurningData(string calldata sn) external view returns (
        string memory tolerance,
        string memory holeAccuracy,
        string memory equipmentNo,
        bool exists
    ) {
        FineTurningData memory data = _fineTurningData[sn];
        exists = bytes(data.tolerance).length > 0;
        return (data.tolerance, data.holeAccuracy, data.equipmentNo, exists);
    }
    
    // 统计查询函数
    function getProductionStats() public view returns (
        uint256 totalReceived,
        uint256 qualifiedCount,
        uint256 unqualifiedCount,
        uint256 reworkCount,
        uint256 scrapCount,
        uint256 passRate,
        uint256 remainingCount
    ) {
        totalReceived = _stats.totalReceived;
        qualifiedCount = _stats.qualifiedCount;
        unqualifiedCount = _stats.unqualifiedCount;
        reworkCount = _stats.reworkCount;
        scrapCount = _stats.scrapCount;
        
        // 计算合格率
        uint256 totalProcessed = qualifiedCount + unqualifiedCount + reworkCount;
        if (totalProcessed > 0) {
            passRate = (qualifiedCount * 100) / totalProcessed;
        } else {
            passRate = 0;
        }
        
        // 计算剩余数量
        uint256 totalConsumed = qualifiedCount + unqualifiedCount + reworkCount + scrapCount;
        if (totalReceived > totalConsumed) {
            remainingCount = totalReceived - totalConsumed;
        } else {
            remainingCount = 0;
        }
    }
    
    function hasData(string calldata sn) external view returns (bool) {
        return _hasQuenchingData(sn);
    }
}