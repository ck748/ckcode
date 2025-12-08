package com.ggbond.defectdetection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 区块链配置类
 * 
 * @author defectDetection
 * @date 2024
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "blockchain")
public class BlockchainConfig {
    
    /**
     * 是否启用区块链功能
     */
    private boolean enabled = false;
    
    /**
     * FISCO BCOS节点地址,格式: ip:port
     */
    private String peers = "127.0.0.1:5002";
    
    /**
     * 群组ID
     */
    private int groupId = 1;
    
    /**
     * 证书路径
     */
    private String certPath = "conf";
    
    /**
     * 私钥(可选)
     */
    private String hexPrivateKey;
    
    /**
     * 智能合约地址配置
     */
    private Map<String, String> contracts = new HashMap<>();
}
