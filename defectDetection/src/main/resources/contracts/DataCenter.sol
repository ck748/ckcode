// SPDX-License-Identifier: MIT
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

// 车间合约接口定义
interface IWorkshopOne {
    function getCuttingMaterialBatch(string calldata sn) external view returns (string memory);
    function getCuttingDetails(string calldata sn) external view returns (
        string memory cutSize,
        string memory cutSpeed,      
        string memory operatorName
    );
    function getPressingData(string calldata sn) external view returns (
        string memory pressure,      
        string memory splineSize,
        string memory equipmentNo
    );
    function getForgingData(string calldata sn) external view returns (
        string memory forgingTemp,  
        string memory pressure,      
        string memory holdTime,      
        string memory defect
    );
    function hasData(string calldata sn) external view returns (bool);
}

interface IWorkshopTwo {
    function getDrillingData(string calldata sn) external view returns (
        string memory holeSize,
        string memory holeDepth,        
        string memory equipmentSpeed    
    );
    function getHeatTreatmentData(string calldata sn) external view returns (
        string memory heatingTemp,      
        string memory holdTime,         
        string memory coolingRate      
    );
    function getTurningData(string calldata sn) external view returns (
        string memory rotationSpeed,    
        string memory feedRate,         
        string memory tolerance,
        string memory toolType
    );
    function getGearProcessingData(string calldata sn) external view returns (
        string memory gearAccuracy,
        string memory surfaceHardness,  // 改为字符串
        string memory equipmentNo
    );
    function hasData(string calldata sn) external view returns (bool);
}

interface IWorkshopThree {
    function getQuenchingData(string calldata sn) external view returns (
        string memory quenchingTemp,    // 改为字符串
        string memory coolingMedium,
        string memory hardness          // 改为字符串
    );
    function getStraighteningData(string calldata sn) external view returns (
        string memory straighteningForce, // 改为字符串
        string memory temperingTemp,      // 改为字符串
        string memory holdTime            // 改为字符串
    );
    function getInspectionData(string calldata sn) external view returns (
        string memory defectLevel,
        string memory defectPosition,
        string memory inspector
    );
    function getFineTurningData(string calldata sn) external view returns (
        string memory tolerance,
        string memory holeAccuracy,
        string memory equipmentNo
    );
    function hasData(string calldata sn) external view returns (bool);
}

interface IWorkshopFour {
    function getPaintingData(string calldata sn) external view returns (
        string memory paintThickness,  // 改为字符串
        string memory sprayPressure,   // 改为字符串
        string memory paintBatch
    );
    function getPackingData(string calldata sn) external view returns (
        string memory packTime,        // 改为字符串
        string memory packOperator
    );
    function hasData(string calldata sn) external view returns (bool);
}

// 数据中心合约
contract DataCenter {
    // 车间合约地址
    address public workshopOneAddr;
    address public workshopTwoAddr;
    address public workshopThreeAddr;
    address public workshopFourAddr;
    
    // 事件
    event DataQueried(string indexed sn, address indexed querier, uint8 workshop);
    event FullTraceQueried(string indexed sn, address indexed querier);
    
    constructor(
        address _workshopOneAddr,
        address _workshopTwoAddr,
        address _workshopThreeAddr,
        address _workshopFourAddr
    ) public {
        workshopOneAddr = _workshopOneAddr;
        workshopTwoAddr = _workshopTwoAddr;
        workshopThreeAddr = _workshopThreeAddr;
        workshopFourAddr = _workshopFourAddr;
    }
    
    // ============ 一车间查询函数 ============
    
    // 查询切割下料数据
    function getCuttingData(string memory sn) public view returns (
        string memory materialBatch,
        string memory cutSize,
        string memory cutSpeed,      
        string memory operatorName
    ) {
        IWorkshopOne workshop = IWorkshopOne(workshopOneAddr);
        materialBatch = workshop.getCuttingMaterialBatch(sn);
        (cutSize, cutSpeed, operatorName) = workshop.getCuttingDetails(sn);
    }
    
    // 查询压花键数据
    function getPressingData(string memory sn) public view returns (
        string memory pressure,      
        string memory splineSize,
        string memory equipmentNo
    ) {
        IWorkshopOne workshop = IWorkshopOne(workshopOneAddr);
        (pressure, splineSize, equipmentNo) = workshop.getPressingData(sn);
    }
    
    // 查询锻造数据
    function getForgingData(string memory sn) public view returns (
        string memory forgingTemp,   
        string memory pressure,     
        string memory holdTime,     
        string memory defect
    ) {
        IWorkshopOne workshop = IWorkshopOne(workshopOneAddr);
        (forgingTemp, pressure, holdTime, defect) = workshop.getForgingData(sn);
    }
    
    // 检查一车间是否有数据
    function hasWorkshopOneData(string memory sn) public view returns (bool) {
        return IWorkshopOne(workshopOneAddr).hasData(sn);
    }
    
    // ============ 二车间查询函数 ============
    
    // 查询钻孔数据
    function getDrillingData(string memory sn) public view returns (
        string memory holeSize,
        string memory holeDepth,       
        string memory equipmentSpeed  
    ) {
        IWorkshopTwo workshop = IWorkshopTwo(workshopTwoAddr);
        (holeSize, holeDepth, equipmentSpeed) = workshop.getDrillingData(sn);
    }
    
    // 查询热处理数据
    function getHeatTreatmentData(string memory sn) public view returns (
        string memory heatingTemp,    
        string memory holdTime,        
        string memory coolingRate      
    ) {
        IWorkshopTwo workshop = IWorkshopTwo(workshopTwoAddr);
        (heatingTemp, holdTime, coolingRate) = workshop.getHeatTreatmentData(sn);
    }
    
    // 查询车削数据
    function getTurningData(string memory sn) public view returns (
        string memory rotationSpeed,  
        string memory feedRate,        
        string memory tolerance,
        string memory toolType
    ) {
        IWorkshopTwo workshop = IWorkshopTwo(workshopTwoAddr);
        (rotationSpeed, feedRate, tolerance, toolType) = workshop.getTurningData(sn);
    }
    
    // 查询齿轮加工数据
    function getGearData(string memory sn) public view returns (
        string memory gearAccuracy,
        string memory surfaceHardness,
        string memory equipmentNo
    ) {
        IWorkshopTwo workshop = IWorkshopTwo(workshopTwoAddr);
        (gearAccuracy, surfaceHardness, equipmentNo) = workshop.getGearProcessingData(sn);
    }
    
    // 检查二车间是否有数据
    function hasWorkshopTwoData(string memory sn) public view returns (bool) {
        return IWorkshopTwo(workshopTwoAddr).hasData(sn);
    }
    
    // ============ 三车间查询函数 ============
    
    // 查询淬火数据
    function getQuenchingData(string memory sn) public view returns (
        string memory quenchingTemp,   
        string memory coolingMedium,
        string memory hardness         
    ) {
        IWorkshopThree workshop = IWorkshopThree(workshopThreeAddr);
        (quenchingTemp, coolingMedium, hardness) = workshop.getQuenchingData(sn);
    }
    
    // 查询校直回火数据
    function getStraighteningData(string memory sn) public view returns (
        string memory straighteningForce, 
        string memory temperingTemp,      
        string memory holdTime            
    ) {
        IWorkshopThree workshop = IWorkshopThree(workshopThreeAddr);
        (straighteningForce, temperingTemp, holdTime) = workshop.getStraighteningData(sn);
    }
    
    // 查询探伤数据
    function getInspectionData(string memory sn) public view returns (
        string memory defectLevel,
        string memory defectPosition,
        string memory inspector
    ) {
        IWorkshopThree workshop = IWorkshopThree(workshopThreeAddr);
        (defectLevel, defectPosition, inspector) = workshop.getInspectionData(sn);
    }
    
    // 查询精车数据
    function getFineTurningData(string memory sn) public view returns (
        string memory tolerance,
        string memory holeAccuracy,
        string memory equipmentNo
    ) {
        IWorkshopThree workshop = IWorkshopThree(workshopThreeAddr);
        (tolerance, holeAccuracy, equipmentNo) = workshop.getFineTurningData(sn);
    }
    
    // 检查三车间是否有数据
    function hasWorkshopThreeData(string memory sn) public view returns (bool) {
        return IWorkshopThree(workshopThreeAddr).hasData(sn);
    }
    
    // ============ 四车间查询函数 ============
    
    // 查询喷漆数据
    function getPaintingData(string memory sn) public view returns (
        string memory paintThickness,  
        string memory sprayPressure,   
        string memory paintBatch
    ) {
        IWorkshopFour workshop = IWorkshopFour(workshopFourAddr);
        (paintThickness, sprayPressure, paintBatch) = workshop.getPaintingData(sn);
    }
    
    // 查询包装数据
    function getPackingData(string memory sn) public view returns (
        string memory packTime,      
        string memory packOperator
    ) {
        IWorkshopFour workshop = IWorkshopFour(workshopFourAddr);
        (packTime, packOperator) = workshop.getPackingData(sn);
    }
    
    // 检查四车间是否有数据
    function hasWorkshopFourData(string memory sn) public view returns (bool) {
        return IWorkshopFour(workshopFourAddr).hasData(sn);
    }
    
    // ============ 综合查询函数 ============
    
    // 检查所有车间数据存在性
    function checkDataExists(string memory sn) public view returns (
        bool hasOne,
        bool hasTwo,
        bool hasThree,
        bool hasFour
    ) {
        hasOne = hasWorkshopOneData(sn);
        hasTwo = hasWorkshopTwoData(sn);
        hasThree = hasWorkshopThreeData(sn);
        hasFour = hasWorkshopFourData(sn);
    }
    
    // 检查产品状态
    function getProductStatus(string memory sn) public view returns (uint8) {
        if (hasWorkshopFourData(sn)) {
            return 5; // 完成
        } else if (hasWorkshopThreeData(sn)) {
            return 4; // 在三车间
        } else if (hasWorkshopTwoData(sn)) {
            return 3; // 在二车间
        } else if (hasWorkshopOneData(sn)) {
            return 2; // 在一车间
        } else {
            return 1; // 未开始
        }
    }
    
    // 查询一车间所有数据
    function getWorkshopOneAll(string memory sn) public {
        emit DataQueried(sn, msg.sender, 1);
    }
    
    // 查询二车间所有数据
    function getWorkshopTwoAll(string memory sn) public {
        emit DataQueried(sn, msg.sender, 2);
    }
    
    // 查询三车间所有数据
    function getWorkshopThreeAll(string memory sn) public {
        emit DataQueried(sn, msg.sender, 3);
    }
    
    // 查询四车间所有数据
    function getWorkshopFourAll(string memory sn) public {
        emit DataQueried(sn, msg.sender, 4);
    }
    
    // 全流程查询事件
    function queryFullTrace(string memory sn) public {
        emit FullTraceQueried(sn, msg.sender);
    }
    
    // 获取产品基本信息
    function getProductInfo(string memory sn) public view returns (
        string memory materialBatch,
        string memory gearAccuracy,
        string memory defectLevel,
        string memory paintThickness,  
        uint8 status
    ) {
        status = getProductStatus(sn);
        
        if (status >= 2) {
            (materialBatch, , , ) = getCuttingData(sn);
        }
        
        if (status >= 3) {
            (gearAccuracy, , ) = getGearData(sn);
        }
        
        if (status >= 4) {
            (defectLevel, , ) = getInspectionData(sn);
        }
        
        if (status >= 5) {
            (paintThickness, , ) = getPaintingData(sn);
        }
    }
    
    // 更新车间地址
    function updateWorkshopAddress(uint8 workshopId, address newAddr) public {
        require(newAddr != address(0), "Invalid address");
        
        if (workshopId == 1) {
            workshopOneAddr = newAddr;
        } else if (workshopId == 2) {
            workshopTwoAddr = newAddr;
        } else if (workshopId == 3) {
            workshopThreeAddr = newAddr;
        } else if (workshopId == 4) {
            workshopFourAddr = newAddr;
        } else {
            revert("Invalid workshop ID");
        }
    }
    
    // 批量检查状态
    function batchCheckStatus(string[] memory snList) public view returns (uint8[] memory) {
        uint8[] memory statuses = new uint8[](snList.length);
        for (uint i = 0; i < snList.length; i++) {
            statuses[i] = getProductStatus(snList[i]);
        }
        return statuses;
    }
    
    // 批量检查数据存在性
    function batchCheckExists(string[] memory snList) public view returns (
        uint256 countOne,
        uint256 countTwo,
        uint256 countThree,
        uint256 countFour
    ) {
        for (uint i = 0; i < snList.length; i++) {
            if (hasWorkshopOneData(snList[i])) countOne++;
            if (hasWorkshopTwoData(snList[i])) countTwo++;
            if (hasWorkshopThreeData(snList[i])) countThree++;
            if (hasWorkshopFourData(snList[i])) countFour++;
        }
    }
    
    // 获取车间地址
    function getWorkshopAddresses() public view returns (
        address addr1,
        address addr2,
        address addr3,
        address addr4
    ) {
        return (workshopOneAddr, workshopTwoAddr, workshopThreeAddr, workshopFourAddr);
    }
    
    // ============ 字符串转数值辅助函数 ============
    
    // 辅助函数：字符串转换为uint256
    function stringToUint(string memory _str) public pure returns (uint256) {
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
    
    // ============ 数值查询函数 ============
    
    // 获取切割速度数值
    function getCuttingSpeedValue(string memory sn) public view returns (uint256) {
        (, string memory cutSpeed, , ) = getCuttingData(sn);
        return stringToUint(cutSpeed);
    }
    
    // 获取锻造温度数值
    function getForgingTempValue(string memory sn) public view returns (uint256) {
        (string memory forgingTemp, , , ) = getForgingData(sn);
        return stringToUint(forgingTemp);
    }
    
    // 获取加热温度数值
    function getHeatingTempValue(string memory sn) public view returns (uint256) {
        (string memory heatingTemp, , ) = getHeatTreatmentData(sn);
        return stringToUint(heatingTemp);
    }
    
    // 获取淬火温度数值
    function getQuenchingTempValue(string memory sn) public view returns (uint256) {
        (string memory quenchingTemp, , ) = getQuenchingData(sn);
        return stringToUint(quenchingTemp);
    }
    
    // 获取漆膜厚度数值
    function getPaintThicknessValue(string memory sn) public view returns (uint256) {
        (string memory paintThickness, , ) = getPaintingData(sn);
        return stringToUint(paintThickness);
    }
    
    // 获取包装时间数值
    function getPackTimeValue(string memory sn) public view returns (uint256) {
        (string memory packTime, ) = getPackingData(sn);
        return stringToUint(packTime);
    }
    
    // 获取完整产品信息（包含数值）
    function getProductInfoWithValues(string memory sn) public view returns (
        string memory materialBatch,
        string memory gearAccuracy,
        string memory defectLevel,
        string memory paintThickness,
        uint256 paintThicknessValue,
        uint8 status
    ) {
        status = getProductStatus(sn);
        
        if (status >= 2) {
            (materialBatch, , , ) = getCuttingData(sn);
        }
        
        if (status >= 3) {
            (gearAccuracy, , ) = getGearData(sn);
        }
        
        if (status >= 4) {
            (defectLevel, , ) = getInspectionData(sn);
        }
        
        if (status >= 5) {
            (paintThickness, , ) = getPaintingData(sn);
            paintThicknessValue = stringToUint(paintThickness);
        }
    }
}