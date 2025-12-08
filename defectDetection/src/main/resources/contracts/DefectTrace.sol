// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * 缺陷检测溯源智能合约
 * 用于记录缺陷检测数据,实现数据不可篡改的溯源
 */
contract DefectTrace {
    
    // 缺陷记录结构
    struct DefectRecord {
        uint256 defectId;        // 缺陷ID
        string category;         // 缺陷类别
        uint256 categoryId;      // 类别ID
        uint256 score;           // 置信度分数
        uint256 x;               // X坐标
        uint256 y;               // Y坐标
        uint256 length;          // 长度
        uint256 height;          // 高度
        uint256 severityLevel;   // 严重程度 1-5
        string repairSuggestion; // 修复建议
        string dataSource;       // 数据来源
        uint256 detectId;        // 检测记录ID
        uint256 timestamp;       // 时间戳
        address operator;        // 操作者地址
        bool exists;             // 是否存在
    }
    
    // 缺陷记录映射
    mapping(uint256 => DefectRecord) private defectRecords;
    
    // 缺陷ID列表
    uint256[] private defectIds;
    
    // 事件定义
    event DefectRecorded(
        uint256 indexed defectId,
        string category,
        uint256 timestamp,
        address operator
    );
    
    event DefectQueried(
        uint256 indexed defectId,
        address operator
    );
    
    /**
     * 记录缺陷信息
     */
    function recordDefect(
        uint256 _defectId,
        string memory _category,
        uint256 _categoryId,
        uint256 _score,
        uint256 _x,
        uint256 _y,
        uint256 _length,
        uint256 _height,
        uint256 _severityLevel,
        string memory _repairSuggestion,
        string memory _dataSource,
        uint256 _detectId
    ) public {
        require(!defectRecords[_defectId].exists, "缺陷记录已存在");
        
        defectRecords[_defectId] = DefectRecord({
            defectId: _defectId,
            category: _category,
            categoryId: _categoryId,
            score: _score,
            x: _x,
            y: _y,
            length: _length,
            height: _height,
            severityLevel: _severityLevel,
            repairSuggestion: _repairSuggestion,
            dataSource: _dataSource,
            detectId: _detectId,
            timestamp: block.timestamp,
            operator: msg.sender,
            exists: true
        });
        
        defectIds.push(_defectId);
        
        emit DefectRecorded(_defectId, _category, block.timestamp, msg.sender);
    }
    
    /**
     * 查询缺陷信息
     */
    function getDefect(uint256 _defectId) public returns (
        uint256 defectId,
        string memory category,
        uint256 categoryId,
        uint256 score,
        uint256 severityLevel,
        string memory repairSuggestion,
        uint256 timestamp,
        address operator
    ) {
        require(defectRecords[_defectId].exists, "缺陷记录不存在");
        
        DefectRecord memory record = defectRecords[_defectId];
        
        emit DefectQueried(_defectId, msg.sender);
        
        return (
            record.defectId,
            record.category,
            record.categoryId,
            record.score,
            record.severityLevel,
            record.repairSuggestion,
            record.timestamp,
            record.operator
        );
    }
    
    /**
     * 获取缺陷详细位置信息
     */
    function getDefectLocation(uint256 _defectId) public view returns (
        uint256 x,
        uint256 y,
        uint256 length,
        uint256 height
    ) {
        require(defectRecords[_defectId].exists, "缺陷记录不存在");
        
        DefectRecord memory record = defectRecords[_defectId];
        
        return (
            record.x,
            record.y,
            record.length,
            record.height
        );
    }
    
    /**
     * 检查缺陷记录是否存在
     */
    function defectExists(uint256 _defectId) public view returns (bool) {
        return defectRecords[_defectId].exists;
    }
    
    /**
     * 获取缺陷总数
     */
    function getDefectCount() public view returns (uint256) {
        return defectIds.length;
    }
    
    /**
     * 批量记录缺陷(简化版,实际使用时可能需要优化gas消耗)
     */
    function batchRecordDefects(
        uint256[] memory _defectIds,
        string[] memory _categories,
        uint256[] memory _categoryIds,
        uint256[] memory _scores,
        uint256[] memory _severityLevels
    ) public {
        require(
            _defectIds.length == _categories.length &&
            _defectIds.length == _categoryIds.length &&
            _defectIds.length == _scores.length &&
            _defectIds.length == _severityLevels.length,
            "数组长度不匹配"
        );
        
        for (uint256 i = 0; i < _defectIds.length; i++) {
            if (!defectRecords[_defectIds[i]].exists) {
                defectRecords[_defectIds[i]] = DefectRecord({
                    defectId: _defectIds[i],
                    category: _categories[i],
                    categoryId: _categoryIds[i],
                    score: _scores[i],
                    x: 0,
                    y: 0,
                    length: 0,
                    height: 0,
                    severityLevel: _severityLevels[i],
                    repairSuggestion: "",
                    dataSource: "batch",
                    detectId: 0,
                    timestamp: block.timestamp,
                    operator: msg.sender,
                    exists: true
                });
                
                defectIds.push(_defectIds[i]);
            }
        }
    }
}
