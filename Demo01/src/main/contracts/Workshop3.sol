// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2;
import "./Workshop2.sol";

contract Workshop3 {
    Workshop2 public prevWorkshop;

    mapping(string => string) private stepMap;
    int256 public currentStepIndex = -1;

    constructor(address _workshop2Addr) public {
        prevWorkshop = Workshop2(_workshop2Addr);
        stepMap[""] = "表面淬火";
        stepMap["表面淬火"] = "精校";
        stepMap["精校"] = "回火";
        stepMap["回火"] = "精拋丸";
        stepMap["精拋丸"] = "磁粉探伤";
        stepMap["磁粉探伤"] = "精车盘";
        stepMap["精车盘"] = "钻孔";
    }

    function getProcess() public view returns (string[7] memory) {
        string[7] memory steps = [
            "表面淬火", "精校", "回火", "精拋丸",
            "磁粉探伤", "精车盘", "钻孔"
        ];
        return steps;
    }

    function completeNextStep(string memory step) public {
        string memory expected = (currentStepIndex == -1) ?
            stepMap[""] : stepMap[getProcess()[uint256(currentStepIndex)]];
        require(keccak256(abi.encodePacked(expected)) == keccak256(abi.encodePacked(step)), "工序顺序错误");
        currentStepIndex++;
    }

    uint256 public inputQty;
    string public axleModelId;
    uint256 public qualified;
    uint256 public unqualified;
    string public manager;
    string public workshopNo = "三车间";
    bool public dataConfirmed = false;

    function setData(
        uint256 _unqualified,
        string memory _manager
    ) public {
        require(!dataConfirmed, "数据已确认");
        require(prevWorkshop.dataConfirmed(), "前序未完成");

        inputQty = prevWorkshop.qualified();
        unqualified = _unqualified;
        qualified = inputQty - _unqualified;
        axleModelId = prevWorkshop.axleModelId();
        manager = _manager;
        dataConfirmed = true;
    }

    function getData() public view returns (
        uint256, string memory, uint256, uint256, string memory, string memory
    ) {
        return (inputQty, axleModelId, qualified, unqualified, manager, workshopNo);
    }
}