// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2;
import "./Workshop4.sol";

contract Workshop5 {
    Workshop4 public prevWorkshop;

    mapping(string => string) private stepMap;
    int256 public currentStepIndex = -1;

    constructor(address _workshop4Addr) public {
        prevWorkshop = Workshop4(_workshop4Addr);
        stepMap[""] = "喷漆";
        stepMap["喷漆"] = "包装";
        stepMap["包装"] = "出库";
    }

    function getProcess() public view returns (string[3] memory) {
        string[3] memory steps = ["喷漆", "包装", "出库"];
        return steps;
    }

    // function completeNextStep(string memory step) public {
    //     string memory expected = (currentStepIndex == -1) ?
    //         stepMap[""] : stepMap[getProcess()[uint256(currentStepIndex)]];
    //     require(keccak256(abi.encodePacked(expected)) == keccak256(abi.encodePacked(step)), "工序顺序错误");
    //     currentStepIndex++;
    // }

    uint256 public inputQty;
    string public axleModelId;
    string public manager;
    string public workshopNo = "出库车间";
    bool public dataConfirmed = false;

    function setData(string memory _manager) public {
        require(!dataConfirmed, "数据已确认");
        require(prevWorkshop.dataConfirmed(), "前序未完成");

        inputQty = prevWorkshop.qualified();
        axleModelId = prevWorkshop.axleModelId();
        manager = _manager;
        dataConfirmed = true;
    }

    function getData() public view returns (
        uint256, string memory, string memory, string memory
    ) {
        return (inputQty, axleModelId, manager, workshopNo);
    }

    function shipOut() public view returns (string memory) {
        require(dataConfirmed, "未完成数据录入");
        return "出库";
    }
}