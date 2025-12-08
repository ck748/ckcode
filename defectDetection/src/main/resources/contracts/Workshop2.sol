// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2;
import "./Workshop1.sol";

contract Workshop2 {
    Workshop1 public prevWorkshop;

    // 工序映射
    mapping(string => string) private stepMap;
    int256 public currentStepIndex = -1;

    constructor(address _workshop1Addr) public {
        prevWorkshop = Workshop1(_workshop1Addr);
        stepMap[""] = "钻中心孔";
        stepMap["钻中心孔"] = "调质热处理";
        stepMap["调质热处理"] = "粗拋丸";
        stepMap["粗拋丸"] = "粗校";
        stepMap["粗校"] = "粗车盘";
        stepMap["粗车盘"] = "粗车杆";
        stepMap["粗车杆"] = "精车杆";
        stepMap["精车杆"] = "加工齿";
    }

    function getProcess() public view returns (string[8] memory) {
        string[8] memory steps = [
            "钻中心孔", "调质热处理", "粗拋丸", "粗校",
            "粗车盘", "粗车杆", "精车杆", "加工齿"
        ];
        return steps;
    }

    function completeNextStep(string memory step) public {
        string memory expected = (currentStepIndex == -1) ?
            stepMap[""] : stepMap[getProcess()[uint256(currentStepIndex)]];
        require(keccak256(abi.encodePacked(expected)) == keccak256(abi.encodePacked(step)), "工序顺序错误");
        currentStepIndex++;
    }

    // 数据
    uint256 public inputQty;
    string public axleModelId;
    uint256 public qualified;
    uint256 public unqualified;
    string public manager;
    string public workshopNo = "二车间";
    bool public dataConfirmed = false;

    function setData(
        uint256 _unqualified,
        string memory _manager
    ) public {
        require(!dataConfirmed, "数据已确认");
        require(prevWorkshop.dataConfirmed(), "前序车间未完成");

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