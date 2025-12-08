package com.ggbond.defectdetection.controller;

import com.ggbond.defectdetection.common.Result;
import com.ggbond.defectdetection.service.blockchain.BlockchainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 区块链功能控制器
 * 
 * @author defectDetection
 * @date 2024
 */
@RestController
@RequestMapping("/blockchain")
@Slf4j
@ConditionalOnProperty(name = "blockchain.enabled", havingValue = "true")
public class BlockchainController {

    @Autowired(required = false)
    private BlockchainService blockchainService;

    /**
     * 检查区块链状态
     */
    @GetMapping("/status")
    public Result getBlockchainStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            boolean available = blockchainService != null && blockchainService.isAvailable();
            status.put("available", available);
            
            if (available) {
                Long blockNumber = blockchainService.getBlockNumber();
                status.put("blockNumber", blockNumber);
                status.put("message", "区块链服务运行正常");
            } else {
                status.put("message", "区块链服务不可用");
            }
            
            return Result.success("获取区块链状态成功", status);
            
        } catch (Exception e) {
            log.error("获取区块链状态失败: {}", e.getMessage(), e);
            return Result.fail("获取区块链状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前区块高度
     */
    @GetMapping("/blockNumber")
    public Result getBlockNumber() {
        try {
            if (blockchainService == null || !blockchainService.isAvailable()) {
                return Result.fail("区块链服务不可用");
            }
            
            Long blockNumber = blockchainService.getBlockNumber();
            return Result.success("获取区块高度成功", blockNumber);
            
        } catch (Exception e) {
            log.error("获取区块高度失败: {}", e.getMessage(), e);
            return Result.fail("获取区块高度失败: " + e.getMessage());
        }
    }
}
