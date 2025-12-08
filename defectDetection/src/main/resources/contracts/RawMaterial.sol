// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;

contract RawMaterial {
    string public rawProducerAddr;
    string public axleProducerAddr;
    uint256 public totalRawWeight;
    string public batchId;

    function setRawMaterial(
        string memory _rawProducerAddr,
        string memory _axleProducerAddr,
        uint256 _totalRawWeight,
        string memory _batchId
    ) public {
        require(bytes(rawProducerAddr).length == 0, "原材料信息已录入");
        rawProducerAddr = _rawProducerAddr;
        axleProducerAddr = _axleProducerAddr;
        totalRawWeight = _totalRawWeight;
        batchId = _batchId;
    }

    function getRawMaterial() public view returns (
        string memory, string memory, uint256, string memory
    ) {
        return (rawProducerAddr, axleProducerAddr, totalRawWeight, batchId);
    }
}