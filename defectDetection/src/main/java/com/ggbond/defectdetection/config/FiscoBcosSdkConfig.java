package com.ggbond.defectdetection.config;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FISCO BCOS SDK Bean配置
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
     * 创建FISCO BCOS客户端
     */
    @Bean
    public Client fiscoBcosClient() throws Exception {
        String certPaths = blockchainConfig.getCertPath();
        String[] possibilities = certPaths.split(",|;");
        
        for (String certPath : possibilities) {
            try {
                ConfigProperty property = new ConfigProperty();
                configNetwork(property);
                configCryptoMaterial(property, certPath.trim());

                ConfigOption configOption = new ConfigOption(property);
                BcosSDK sdk = new BcosSDK(configOption);
                Client client = sdk.getClient(String.valueOf(blockchainConfig.getGroupId()));

                BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
                log.info("✅ 区块链连接成功! 当前区块高度: {}", blockNumber);
                
                configCryptoKeyPair(client);
                log.info("🔐 加密套件已配置, 地址: {}", client.getCryptoSuite().getCryptoKeyPair().getAddress());
                
                return client;
            } catch (Exception ex) {
                log.error("❌ 尝试证书路径 {} 失败: {}", certPath, ex.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new ConfigException("❌ 无法连接到FISCO BCOS节点: " + blockchainConfig.getPeers());
    }

    /**
     * 配置网络信息
     */
    private void configNetwork(ConfigProperty configProperty) {
        String peerStr = blockchainConfig.getPeers();
        List<String> peers = Arrays.stream(peerStr.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        
        Map<String, Object> networkConfig = new HashMap<>();
        networkConfig.put("peers", peers);
        
        configProperty.setNetwork(networkConfig);
    }

    /**
     * 配置加密材料(证书)
     */
    private void configCryptoMaterial(ConfigProperty configProperty, String certPath) {
        Map<String, Object> cryptoMaterials = new HashMap<>();
        cryptoMaterials.put("certPath", certPath);
        configProperty.setCryptoMaterial(cryptoMaterials);
    }

    /**
     * 配置加密密钥对
     */
    private void configCryptoKeyPair(Client client) {
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        
        if (blockchainConfig.getHexPrivateKey() == null || blockchainConfig.getHexPrivateKey().isEmpty()) {
            cryptoSuite.setCryptoKeyPair(cryptoSuite.getCryptoKeyPair());
            log.info("🔑 使用随机生成的私钥");
            return;
        }
        
        String privateKey = blockchainConfig.getHexPrivateKey();
        if (!privateKey.contains(",")) {
            // 单个私钥
        } else {
            // 多个私钥,取第一个
            String[] list = privateKey.split(",");
            privateKey = list[0].trim();
        }
        
        if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
            privateKey = privateKey.substring(2);
        }
        
        cryptoSuite.loadAccount("pem", privateKey, null);
        log.info("🔑 使用配置的私钥");
    }
}
