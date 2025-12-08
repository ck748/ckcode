package com.ggbond.defectdetection.service.blockchain;

import com.ggbond.defectdetection.config.BlockchainConfig;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原材料合约服务
 * 用于记录和查询原材料信息
 * 
 * @author defectDetection
 * @date 2024
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "blockchain.enabled", havingValue = "true")
public class RawMaterialService {

    @Autowired(required = false)
    private Client client;

    @Autowired
    private BlockchainConfig blockchainConfig;

    @Autowired(required = false)
    private BlockchainService blockchainService;

    private AssembleTransactionProcessor txProcessor;
    private String contractAddress;
    private String abi;

    @PostConstruct
    public void init() throws Exception {
        if (client == null) {
            log.warn("区块链客户端未初始化,RawMaterial服务不可用");
            return;
        }

        // 获取合约地址
        contractAddress = blockchainConfig.getContracts().get("raw-material");
        if (contractAddress == null || contractAddress.isEmpty()) {
            log.warn("RawMaterial合约地址未配置");
            return;
        }

        // 加载ABI
        try {
            String abiPath = "blockchain/abi/RawMaterial.abi";
            abi = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(abiPath).toURI())
            ));
        } catch (Exception e) {
            log.error("加载RawMaterial ABI失败: {}", e.getMessage());
            // 使用内联ABI
            abi = "[{\"inputs\":[],\"name\":\"axleProducerAddr\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"batchId\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getRawMaterial\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"rawProducerAddr\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_rawProducerAddr\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_axleProducerAddr\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"_totalRawWeight\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"_batchId\",\"type\":\"string\"}],\"name\":\"setRawMaterial\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"totalRawWeight\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]";
        }

        // 初始化交易处理器
        txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(
            client,
            client.getCryptoSuite().getCryptoKeyPair(),
            abi,
            ""
        );

        log.info("✅ RawMaterial服务初始化成功, 合约地址: {}", contractAddress);
    }

    /**
     * 设置原材料信息
     */
    public Map<String, Object> setRawMaterial(
        String rawProducerAddr,
        String axleProducerAddr,
        Long totalRawWeight,
        String batchId
    ) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (txProcessor == null) {
                result.put("success", false);
                result.put("message", "交易处理器未初始化");
                return result;
            }

            List<Object> params = Arrays.asList(
                rawProducerAddr,
                axleProducerAddr,
                totalRawWeight,
                batchId
            );

            TransactionResponse response = txProcessor.sendTransactionAndGetResponse(
                contractAddress,
                abi,
                "setRawMaterial",
                params
            );

            boolean success = response.getTransactionReceipt().getStatus() == 0;
            
            result.put("success", success);
            result.put("transactionHash", response.getTransactionReceipt().getTransactionHash());
            result.put("blockNumber", response.getTransactionReceipt().getBlockNumber());
            result.put("message", success ? "原材料信息设置成功" : "交易失败");
            
            log.info("设置原材料信息: {}, 交易哈希: {}", 
                success ? "成功" : "失败",
                response.getTransactionReceipt().getTransactionHash());
            
        } catch (Exception e) {
            log.error("设置原材料信息失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "设置失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取原材料信息
     */
    public Map<String, Object> getRawMaterial() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (txProcessor == null) {
                result.put("success", false);
                result.put("message", "交易处理器未初始化");
                return result;
            }

            CallResponse response = txProcessor.sendCall(
                client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                contractAddress,
                abi,
                "getRawMaterial",
                Arrays.asList()
            );

            if (response.getReturnCode() == 0) {
                List<org.fisco.bcos.sdk.v3.codec.datatypes.Type> values = response.getResults();
                
                result.put("success", true);
                result.put("rawProducerAddr", values.get(0).getValue());
                result.put("axleProducerAddr", values.get(1).getValue());
                result.put("totalRawWeight", values.get(2).getValue());
                result.put("batchId", values.get(3).getValue());
                
                log.info("查询原材料信息成功");
            } else {
                result.put("success", false);
                result.put("message", "查询失败: " + response.getReturnMessage());
            }
            
        } catch (Exception e) {
            log.error("查询原材料信息失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取原材料生产商地址
     */
    public String getRawProducerAddr() {
        try {
            if (txProcessor == null) {
                return null;
            }

            CallResponse response = txProcessor.sendCall(
                client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                contractAddress,
                abi,
                "rawProducerAddr",
                Arrays.asList()
            );

            if (response.getReturnCode() == 0 && !response.getResults().isEmpty()) {
                return response.getResults().get(0).getValue().toString();
            }
        } catch (Exception e) {
            log.error("查询原材料生产商地址失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 获取批次ID
     */
    public String getBatchId() {
        try {
            if (txProcessor == null) {
                return null;
            }

            CallResponse response = txProcessor.sendCall(
                client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                contractAddress,
                abi,
                "batchId",
                Arrays.asList()
            );

            if (response.getReturnCode() == 0 && !response.getResults().isEmpty()) {
                return response.getResults().get(0).getValue().toString();
            }
        } catch (Exception e) {
            log.error("查询批次ID失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 检查服务是否可用
     */
    public boolean isAvailable() {
        return txProcessor != null && 
               contractAddress != null && 
               !contractAddress.isEmpty() &&
               blockchainService != null &&
               blockchainService.isAvailable();
    }
}
