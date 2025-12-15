package com.ggbond.defectdetection.config;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FISCO BCOS SDK 2.x Bean配置
 * 
 * @author defectDetection
 * @date 2024
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "blockchain.enabled", havingValue = "true")
public class FiscoBcosSdkConfig {

    @Autowired
    private BlockchainConfig blockchainConfig;

    /**
     * 创建FISCO BCOS客户端 (2.x SDK)
     */
    @Bean
    public Client fiscoBcosClient() throws Exception {
        String certPath = blockchainConfig.getCertPath();
        
        try {
            // 2.x SDK配置
            Map<String, Object> cryptoMaterialConfig = new HashMap<>();
            cryptoMaterialConfig.put("certPath", certPath);
            
            ConfigProperty configProperty = new ConfigProperty();
            configProperty.setCryptoMaterial(cryptoMaterialConfig);
            
            // 配置网络
            Map<String, Object> networkConfig = new HashMap<>();
            String[] peers = blockchainConfig.getPeers().split(",");
            networkConfig.put("peers", Arrays.asList(peers));
            configProperty.setNetwork(networkConfig);
            
            // 创建SDK
            ConfigOption configOption = new ConfigOption(configProperty);
            BcosSDK sdk = new BcosSDK(configOption);
            
            // 获取群组客户端
            Integer groupId = blockchainConfig.getGroupId();
            Client client = sdk.getClient(groupId);
            
            // 验证连接
            BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
            log.info("✅ 区块链连接成功! 当前区块高度: {}", blockNumber);
            
            // 配置私钥
            configCryptoKeyPair(client);
            log.info("🔐 加密套件已配置, 地址: {}", client.getCryptoSuite().getCryptoKeyPair().getAddress());
            
            return client;
        } catch (Exception ex) {
            log.error("❌ 连接FISCO BCOS节点失败: {}", ex.getMessage());
            throw new ConfigException("❌ 无法连接到FISCO BCOS节点: " + blockchainConfig.getPeers());
        }
    }

    /**
     * 配置加密密钥对 (2.x SDK)
     */
    private void configCryptoKeyPair(Client client) {
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        
        String privateKey = blockchainConfig.getHexPrivateKey();
        if (privateKey == null || privateKey.isEmpty()) {
            log.info("🔑 使用随机生成的私钥");
            return;
        }
        
        // 处理多个私钥的情况
        if (privateKey.contains(",")) {
            String[] list = privateKey.split(",");
            privateKey = list[0].trim();
        }
        
        // 移除0x前缀
        if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
            privateKey = privateKey.substring(2);
        }
        
        // 2.x SDK使用createKeyPair加载私钥
        try {
            cryptoSuite.createKeyPair(privateKey);
            log.info("🔑 使用配置的私钥");
        } catch (Exception e) {
            log.warn("私钥加载失败,使用随机私钥: {}", e.getMessage());
        }
    }
}
