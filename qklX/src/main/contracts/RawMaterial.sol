// SPDX-License-Identifier: MIT
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

contract RawMaterial {
    struct MaterialBatch {
        string manufacturer;     // 原材料厂商
        string shaftBatch;       // 半轴批次
        string sn;               // 型号（SN）
        uint256 totalQuantity;   // 总数量
        string destination;      // 目标地点
        uint256 usedQuantity;    // 已使用数量
        uint256 remainingQuantity; // 剩余数量
        uint256 createTime;      // 创建时间
    }
    
    // 存储批次数据
    mapping(string => MaterialBatch) private _batchData;
    
    // 根据SN查询批次
    mapping(string => string) private _snToBatch;
    
    // 批次列表
    string[] private _batchList;
    
    // 批次存在性检查
    mapping(string => bool) private _batchExists;
    
    // 事件
    event BatchCreated(string indexed batchId, string manufacturer, string sn, uint256 totalQuantity, string destination);
    event BatchUsed(string indexed batchId, string sn, uint256 usedQuantity, uint256 remainingQuantity);
    event MaterialAssigned(string indexed sn, string batchId);
    
    // 创建原材料批次
    function createMaterialBatch(
        string memory batchId,
        string memory manufacturer,
        string memory shaftBatch,
        string memory sn,
        uint256 totalQuantity,
        string memory destination
    ) public {
        require(totalQuantity > 0, "Quantity must be greater than 0");
        require(bytes(batchId).length > 0, "Batch ID cannot be empty");
        require(!_batchExists[batchId], "Batch already exists");
        
        _batchData[batchId] = MaterialBatch({
            manufacturer: manufacturer,
            shaftBatch: shaftBatch,
            sn: sn,
            totalQuantity: totalQuantity,
            destination: destination,
            usedQuantity: 0,
            remainingQuantity: totalQuantity,
            createTime: block.timestamp
        });
        
        _snToBatch[sn] = batchId;
        _batchList.push(batchId);
        _batchExists[batchId] = true;
        
        emit BatchCreated(batchId, manufacturer, sn, totalQuantity, destination);
    }
    
    // 使用原材料
    function useMaterial(string memory sn, uint256 quantity) public returns (bool) {
        string memory batchId = _snToBatch[sn];
        require(bytes(batchId).length > 0, "SN not found in any batch");
        require(_batchExists[batchId], "Batch does not exist");
        
        MaterialBatch storage batch = _batchData[batchId];
        require(batch.remainingQuantity >= quantity, "Insufficient material");
        
        batch.usedQuantity += quantity;
        batch.remainingQuantity -= quantity;
        
        emit BatchUsed(batchId, sn, quantity, batch.remainingQuantity);
        
        return true;
    }
    
    // 获取批次信息（通过批次ID）
    function getBatchInfo(string memory batchId) public view returns (
        string memory manufacturer,
        string memory shaftBatch,
        string memory sn,
        uint256 totalQuantity,
        string memory destination,
        uint256 usedQuantity,
        uint256 remainingQuantity,
        uint256 createTime
    ) {
        require(_batchExists[batchId], "Batch does not exist");
        
        MaterialBatch memory batch = _batchData[batchId];
        return (
            batch.manufacturer,
            batch.shaftBatch,
            batch.sn,
            batch.totalQuantity,
            batch.destination,
            batch.usedQuantity,
            batch.remainingQuantity,
            batch.createTime
        );
    }
    
    // 获取批次信息（通过SN）
    function getBatchInfoBySN(string memory sn) public view returns (
        string memory batchId,
        string memory manufacturer,
        string memory shaftBatch,
        uint256 totalQuantity,
        string memory destination,
        uint256 usedQuantity,
        uint256 remainingQuantity
    ) {
        batchId = _snToBatch[sn];
        require(bytes(batchId).length > 0, "SN not found in any batch");
        
        MaterialBatch memory batch = _batchData[batchId];
        return (
            batchId,
            batch.manufacturer,
            batch.shaftBatch,
            batch.totalQuantity,
            batch.destination,
            batch.usedQuantity,
            batch.remainingQuantity
        );
    }
    
    // 检查批次是否存在
    function batchExists(string memory batchId) public view returns (bool) {
        return _batchExists[batchId];
    }
    
    // 检查SN是否存在
    function snExists(string memory sn) public view returns (bool) {
        return bytes(_snToBatch[sn]).length > 0;
    }
    
    // 获取所有批次ID
    function getAllBatchIds() public view returns (string[] memory) {
        return _batchList;
    }
    
    // 获取批次数量
    function getBatchCount() public view returns (uint256) {
        return _batchList.length;
    }
    
    // 获取批次信息（按索引）
    function getBatchByIndex(uint256 index) public view returns (
        string memory batchId,
        string memory manufacturer,
        string memory shaftBatch,
        string memory sn,
        uint256 totalQuantity,
        uint256 usedQuantity,
        uint256 remainingQuantity
    ) {
        require(index < _batchList.length, "Index out of bounds");
        
        batchId = _batchList[index];
        MaterialBatch memory batch = _batchData[batchId];
        return (
            batchId,
            batch.manufacturer,
            batch.shaftBatch,
            batch.sn,
            batch.totalQuantity,
            batch.usedQuantity,
            batch.remainingQuantity
        );
    }
    
    // 获取SN对应的批次ID
    function getBatchIdBySN(string memory sn) public view returns (string memory) {
        return _snToBatch[sn];
    }
}