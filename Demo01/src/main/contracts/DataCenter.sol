// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2; // 必须开启才能返回 struct

import "./RawMaterial.sol";
import "./Workshop1.sol";
import "./Workshop2.sol";
import "./Workshop3.sol";
import "./Workshop4.sol";
import "./Workshop5.sol";

contract DataCenter {

    RawMaterial public rawMat;
    Workshop1 public ws1;
    Workshop2 public ws2;
    Workshop3 public ws3;
    Workshop4 public ws4;
    Workshop5 public ws5;

    constructor(
        address _raw, address _ws1, address _ws2,
        address _ws3, address _ws4, address _ws5
    ) public {
        rawMat = RawMaterial(_raw);
        ws1 = Workshop1(_ws1);
        ws2 = Workshop2(_ws2);
        ws3 = Workshop3(_ws3);
        ws4 = Workshop4(_ws4);
        ws5 = Workshop5(_ws5);
    }

    // 定义统一的数据结构
    struct FullProcessData {
        // 原材料
        string rawProducerAddr;
        string axleProducerAddr;
        uint256 totalRawWeight;
        string batchId;

        // 一车间
        uint256 inputQty1;
        uint256 qualified1;
        uint256 unqualified1;
        string manager1;

        // 二车间
        uint256 inputQty2;
        uint256 qualified2;
        uint256 unqualified2;
        string manager2;

        // 三车间
        uint256 inputQty3;
        uint256 qualified3;
        uint256 unqualified3;
        string manager3;

        // 四车间
        uint256 inputQty4;
        uint256 qualified4;
        uint256 unqualified4;
        string manager4;

        // 五车间
        uint256 inputQty5;
        string manager5;

        // 最终状态
        string status;
    }

    // 获取全流程数据（不再有“栈太深”问题）
    function getAllData() public view returns (FullProcessData memory) {
        return FullProcessData({
            // 原材料
            rawProducerAddr: rawMat.rawProducerAddr(),
            axleProducerAddr: rawMat.axleProducerAddr(),
            totalRawWeight: rawMat.totalRawWeight(),
            batchId: rawMat.batchId(),

            // 一车间
            inputQty1: rawMat.totalRawWeight(),
            qualified1: ws1.qualified(),
            unqualified1: ws1.unqualified(),
            manager1: ws1.manager(),

            // 二车间
            inputQty2: ws1.qualified(),
            qualified2: ws2.qualified(),
            unqualified2: ws2.unqualified(),
            manager2: ws2.manager(),

            // 三车间
            inputQty3: ws2.qualified(),
            qualified3: ws3.qualified(),
            unqualified3: ws3.unqualified(),
            manager3: ws3.manager(),

            // 四车间
            inputQty4: ws3.qualified(),
            qualified4: ws4.qualified(),
            unqualified4: ws4.unqualified(),
            manager4: ws4.manager(),

            // 五车间
            inputQty5: ws4.qualified(),
            manager5: ws5.manager(),

            // 状态
            status: ws5.dataConfirmed() ? "已出库" : "未完成"
        });
    }
}