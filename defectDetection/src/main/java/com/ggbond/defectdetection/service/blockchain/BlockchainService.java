package com.ggbond.defectdetection.service.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 区块链基础服务类
 * 
 * @author defectDetection
 * @date 2024
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "blockchain.enabled", havingValue = "true")
public class BlockchainService {

    @Autowired(required = false)
    private Client client;

    /**
     * 检查区块链是否可用
     */
    public boolean isAvailable() {
        try {
            if (client == null) {
                return false;
            }
            client.getBlockNumber();
            return true;
        } catch (Exception e) {
            log.error("区块链不可用: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前区块高度
     */
    public Long getBlockNumber() {
        try {
            return client.getBlockNumber().getBlockNumber().longValue();
        } catch (Exception e) {
            log.error("获取区块高度失败: {}", e.getMessage());
            return -1L;
        }
    }

    /**
     * 获取客户端
     */
    public Client getClient() {
        return client;
    }

    /**
     * 检查交易是否成功 (2.x SDK)
     */
    public boolean isTransactionSuccess(TransactionReceipt receipt) {
        return receipt != null && "0x0".equals(receipt.getStatus());
    }
}
