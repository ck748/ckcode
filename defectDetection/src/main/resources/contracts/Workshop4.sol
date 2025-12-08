// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2;
import "./Workshop3.sol";

contract Workshop4 {
    Workshop3 public prevWorkshop;

    constructor(address _workshop3Addr) public {
        prevWorkshop = Workshop3(_workshop3Addr);
    }

    function getProcess() public view returns (string[1] memory) {
        string[1] memory steps = ["超声波探伤"];
        return steps;
    }

    // 不合格分类（仅说明，不上链）
    // 裂痕：内部→原料问题，外部→加工问题
    // 划痕：加工问题

    uint256 public inputQty;
    string public axleModelId;
    uint256 public qualified;
    uint256 public unqualified;
    string public manager;
    string public workshopNo = "质检车间";
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