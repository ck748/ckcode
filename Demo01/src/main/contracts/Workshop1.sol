// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2;
import "./RawMaterial.sol";

contract Workshop1 {
    RawMaterial public rawMaterial;

    // 工序映射
    mapping(string => string) private stepMap;
    int256 public currentStepIndex = -1;

    constructor(address _rawMaterialAddr) public {
        rawMaterial = RawMaterial(_rawMaterialAddr);
        stepMap[""] = "切割下料";
        stepMap["切割下料"] = "压花键";
        stepMap["压花键"] = "锻造";
    }

    function getProcess() public view returns (string[3] memory) {
        string[3] memory steps = ["切割下料", "压花键", "锻造"];
        return steps;
    }

    //推进工序方法：防跳步控制
    function completeNextStep(string memory step) public {
        string memory expected = (currentStepIndex == -1) ?
            stepMap[""] : stepMap[getProcess()[uint256(currentStepIndex)]];
        require(keccak256(abi.encodePacked(expected)) == keccak256(abi.encodePacked(step)), "工序顺序错误");
        currentStepIndex++;
    }

    // 数据
    string public axleModelId;
    uint256 public qualified;
    uint256 public unqualified;
    string public manager;
    string public workshopNo = "一车间";
    bool public dataConfirmed = false;

    function setData(
        string memory _axleModelId,
        uint256 _unqualified,
        string memory _manager
    ) public {
        require(!dataConfirmed, "数据已确认");
        require(bytes(axleModelId).length == 0, "不可重复录入");

        axleModelId = _axleModelId;
        unqualified = _unqualified;
        uint256 totalInput = rawMaterial.totalRawWeight();
        qualified = totalInput - _unqualified;
        manager = _manager;
        dataConfirmed = true;
    }

    function getData() public view returns (
        uint256, string memory, uint256, uint256, string memory, string memory
    ) {
        return (rawMaterial.totalRawWeight(), axleModelId, qualified, unqualified, manager, workshopNo);
    }
}