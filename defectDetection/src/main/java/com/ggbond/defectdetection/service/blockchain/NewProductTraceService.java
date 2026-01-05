package com.ggbond.defectdetection.service.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ggbond.defectdetection.service.AIAnalysisService;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 产品溯源区块链服务（qkl项目新合约版本）
 * 使用独立的车间合约查询数据
 */
@Service
@Slf4j
public class NewProductTraceService {
    
    @Value("${blockchain.enabled:false}")
    private boolean blockchainEnabled;
    
    // 原材料合约地址
    @Value("${blockchain.contracts.raw-material:}")
    private String rawMaterialAddress;
    
    // 车间合约地址
    @Value("${blockchain.contracts.workshop1:}")
    private String workshop1Address;
    
    @Value("${blockchain.contracts.workshop2:}")
    private String workshop2Address;
    
    @Value("${blockchain.contracts.workshop3:}")
    private String workshop3Address;
    
    @Value("${blockchain.contracts.workshop4:}")
    private String workshop4Address;
    
    @Autowired(required = false)
    private Client client;
    
    @Autowired(required = false)
    private CryptoKeyPair cryptoKeyPair;
    
    // 交易处理器
    private AssembleTransactionProcessor txProcessor;
    
    // ABI内容
    private String rawMaterialAbi;
    private String workshop1Abi;
    private String workshop2Abi;
    private String workshop3Abi;
    private String workshop4Abi;
    
    @Autowired(required = false)
    private AIAnalysisService aiAnalysisService;
    
    @PostConstruct
    public void init() {
        if (!blockchainEnabled) {
            log.info("区块链功能未启用");
            return;
        }
        
        try {
            log.info("开始初始化产品溯源服务（qkl新合约）...");
            
            if (client == null || cryptoKeyPair == null) {
                log.warn("区块链 Client 或 CryptoKeyPair 未注入");
                blockchainEnabled = false;
                return;
            }
            
            // 加载ABI
            rawMaterialAbi = loadAbi("abi/RawMaterial.abi", "原料合约");
            workshop1Abi = loadAbi("abi/WorkshopOne.abi", "车间1");
            workshop2Abi = loadAbi("abi/WorkshopTwo.abi", "车间2");
            workshop3Abi = loadAbi("abi/WorkshopThree.abi", "车间3");
            workshop4Abi = loadAbi("abi/WorkshopFour.abi", "车间4");
            
            // 创建交易处理器
            txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(
                client, cryptoKeyPair
            );
            
            log.info("✅ 产品溯源服务初始化成功!");
            log.info("车间合约地址: W1={}, W2={}, W3={}, W4={}", 
                     workshop1Address, workshop2Address, workshop3Address, workshop4Address);
            
        } catch (Exception e) {
            log.error("初始化产品溯源服务失败: {}", e.getMessage(), e);
            blockchainEnabled = false;
        }
    }
    
    private String loadAbi(String abiPath, String contractName) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(abiPath);
            if (is == null) {
                log.warn("{}合约ABI文件不存在: {}", contractName, abiPath);
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("加载{}合约ABI失败: {}", contractName, e.getMessage());
            return null;
        }
    }
    
    /**
     * 查询产品完整溯源信息
     */
    public Map<String, Object> queryProductTrace(String productId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("🔍 查询产品溯源信息: {}", productId);
            
            if (productId == null || productId.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "产品ID不能为空");
                return result;
            }
            
            if (!blockchainEnabled || txProcessor == null) {
                log.error("区块链未启用");
                result.put("success", false);
                result.put("message", "区块链服务未启用");
                return result;
            }
            
            // 构建产品数据
            Map<String, Object> productData = new HashMap<>();
            productData.put("productId", productId);
            productData.put("status", "已完成");
            productData.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            productData.put("productionDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            
            // 查询原料产地数据
            Map<String, Object> rawMaterialData = queryRawMaterialData(productId);
            if (rawMaterialData != null) {
                productData.put("rawMaterial", rawMaterialData);
            }
            
            // 查询各车间数据
            Map<String, Object> workshop1Data = queryWorkshop1Data(productId);
            if (workshop1Data != null) {
                productData.put("workshop1", workshop1Data);
                // 从车间1获取材料批次
                if (workshop1Data.containsKey("cutting")) {
                    Map<String, Object> cutting = (Map<String, Object>) workshop1Data.get("cutting");
                    if (cutting != null && cutting.containsKey("materialBatch")) {
                        productData.put("materialBatch", cutting.get("materialBatch"));
                    }
                }
            }
            
            Map<String, Object> workshop2Data = queryWorkshop2Data(productId);
            if (workshop2Data != null) {
                productData.put("workshop2", workshop2Data);
                // 从车间2获取齿轮精度
                if (workshop2Data.containsKey("gear")) {
                    Map<String, Object> gear = (Map<String, Object>) workshop2Data.get("gear");
                    if (gear != null && gear.containsKey("gearAccuracy")) {
                        productData.put("gearAccuracy", gear.get("gearAccuracy"));
                    }
                }
            }
            
            Map<String, Object> workshop3Data = queryWorkshop3Data(productId);
            if (workshop3Data != null) {
                productData.put("workshop3", workshop3Data);
                // 从车间3获取缺陷等级
                if (workshop3Data.containsKey("inspection")) {
                    Map<String, Object> inspection = (Map<String, Object>) workshop3Data.get("inspection");
                    if (inspection != null && inspection.containsKey("defectLevel")) {
                        productData.put("defectLevel", inspection.get("defectLevel"));
                    }
                }
            }
            
            Map<String, Object> workshop4Data = queryWorkshop4Data(productId);
            if (workshop4Data != null) {
                productData.put("workshop4", workshop4Data);
                // 从车间4获取漆面厚度
                if (workshop4Data.containsKey("painting")) {
                    Map<String, Object> painting = (Map<String, Object>) workshop4Data.get("painting");
                    if (painting != null && painting.containsKey("paintThickness")) {
                        productData.put("paintThickness", painting.get("paintThickness"));
                    }
                }
            }
            
            // 添加区块链存证信息
            Map<String, Object> blockchainInfo = buildBlockchainInfo(productId);
            if (blockchainInfo != null) {
                productData.put("blockchainInfo", blockchainInfo);
                productData.put("isOnChain", true);
            } else {
                productData.put("isOnChain", false);
            }
            
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", productData);
            
            log.info("✅ 产品溯源信息查询成功: {}", productId);
            
        } catch (Exception e) {
            log.error("查询产品溯源信息失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 查询原料产地数据
     */
    private Map<String, Object> queryRawMaterialData(String productId) {
        if (rawMaterialAbi == null || rawMaterialAddress == null) {
            log.warn("原料合约ABI或地址为空");
            return null;
        }
        
        try {
            // 查询原料批次信息
            CallResponse batchResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                rawMaterialAddress,
                rawMaterialAbi,
                "getBatchInfoBySN",
                Arrays.asList(productId)
            );
            
            if (batchResp.getReturnMessage().equals("Success") && batchResp.getReturnObject() != null) {
                List<Object> data = batchResp.getReturnObject();
                // getBatchInfoBySN返回: batchId, manufacturer, shaftBatch, totalQuantity, destination, usedQuantity, remainingQuantity
                if (data.size() >= 5) {
                    Map<String, Object> rawMaterial = new HashMap<>();
                    rawMaterial.put("batchNo", data.get(0).toString());  // batchId
                    rawMaterial.put("supplier", data.get(1).toString());  // manufacturer
                    rawMaterial.put("materialType", data.get(2).toString());  // shaftBatch
                    rawMaterial.put("origin", data.get(4).toString());  // destination
                    rawMaterial.put("status", "合格");
                    rawMaterial.put("purchaseDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    
                    log.info("✅ 查询到原料产地数据: {}", rawMaterial);
                    return rawMaterial;
                }
            }
        } catch (Exception e) {
            log.error("查询原料产地数据失败: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 查询车间1数据（切割、压花键、锻造）
     */
    private Map<String, Object> queryWorkshop1Data(String productId) {
        if (workshop1Abi == null || workshop1Address == null) {
            return null;
        }
        
        Map<String, Object> workshop1 = new HashMap<>();
        try {
            // 查询完整切割数据
            CallResponse cuttingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop1Address,
                workshop1Abi,
                "getFullCuttingData",
                Arrays.asList(productId)
            );
            
            if (cuttingResp.getReturnMessage().equals("Success") && cuttingResp.getReturnObject() != null) {
                List<Object> data = cuttingResp.getReturnObject();
                if (data.size() >= 5 && (Boolean) data.get(4)) {  // exists
                    Map<String, String> cutting = new HashMap<>();
                    cutting.put("materialBatch", data.get(0).toString());
                    cutting.put("cutSize", data.get(1).toString());
                    cutting.put("cutSpeed", data.get(2).toString());
                    cutting.put("operator", data.get(3).toString());
                    workshop1.put("cutting", cutting);
                }
            }
            
            // 查询压花键数据
            CallResponse pressingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop1Address,
                workshop1Abi,
                "getPressingData",
                Arrays.asList(productId)
            );
            
            if (pressingResp.getReturnMessage().equals("Success") && pressingResp.getReturnObject() != null) {
                List<Object> data = pressingResp.getReturnObject();
                if (data.size() >= 3) {
                    Map<String, String> pressing = new HashMap<>();
                    pressing.put("pressure", data.get(0).toString());
                    pressing.put("splineSize", data.get(1).toString());
                    pressing.put("equipmentNo", data.get(2).toString());
                    workshop1.put("pressing", pressing);
                }
            }
            
            // 查询完整锻造数据
            CallResponse forgingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop1Address,
                workshop1Abi,
                "getFullForgingData",
                Arrays.asList(productId)
            );
            
            if (forgingResp.getReturnMessage().equals("Success") && forgingResp.getReturnObject() != null) {
                List<Object> data = forgingResp.getReturnObject();
                if (data.size() >= 5 && (Boolean) data.get(4)) {  // exists
                    Map<String, String> forging = new HashMap<>();
                    forging.put("forgingTemp", data.get(0).toString());
                    forging.put("pressure", data.get(1).toString());
                    forging.put("holdTime", data.get(2).toString());
                    forging.put("defect", data.get(3).toString());
                    workshop1.put("forging", forging);
                }
            }
            
        } catch (Exception e) {
            log.error("查询车间1数据失败: {}", e.getMessage());
        }
        
        return workshop1.isEmpty() ? null : workshop1;
    }
    
    /**
     * 查询车间2数据（钻孔、热处理、车削、加工齿）
     */
    private Map<String, Object> queryWorkshop2Data(String productId) {
        if (workshop2Abi == null || workshop2Address == null) {
            return null;
        }
        
        Map<String, Object> workshop2 = new HashMap<>();
        try {
            // 查询完整钻孔数据
            CallResponse drillingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop2Address,
                workshop2Abi,
                "getFullDrillingData",
                Arrays.asList(productId)
            );
            
            if (drillingResp.getReturnMessage().equals("Success") && drillingResp.getReturnObject() != null) {
                List<Object> data = drillingResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> drilling = new HashMap<>();
                    drilling.put("holeSize", data.get(0).toString());
                    drilling.put("holeDepth", data.get(1).toString());
                    drilling.put("drillSpeed", data.get(2).toString());
                    workshop2.put("drilling", drilling);
                }
            }
            
            // 查询完整热处理数据
            CallResponse heatResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop2Address,
                workshop2Abi,
                "getFullHeatTreatmentData",
                Arrays.asList(productId)
            );
            
            if (heatResp.getReturnMessage().equals("Success") && heatResp.getReturnObject() != null) {
                List<Object> data = heatResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> heat = new HashMap<>();
                    heat.put("heatingTemp", data.get(0).toString());
                    heat.put("holdTime", data.get(1).toString());
                    heat.put("coolingRate", data.get(2).toString());
                    workshop2.put("heatTreatment", heat);
                }
            }
            
            // 查询完整齿轮加工数据
            CallResponse gearResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop2Address,
                workshop2Abi,
                "getFullGearProcessingData",
                Arrays.asList(productId)
            );
            
            if (gearResp.getReturnMessage().equals("Success") && gearResp.getReturnObject() != null) {
                List<Object> data = gearResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> gear = new HashMap<>();
                    gear.put("gearAccuracy", data.get(0).toString());
                    gear.put("surfaceHardness", data.get(1).toString());
                    gear.put("toothProfile", data.get(2).toString());
                    workshop2.put("gear", gear);
                }
            }
            
        } catch (Exception e) {
            log.error("查询车间2数据失败: {}", e.getMessage());
        }
        
        return workshop2.isEmpty() ? null : workshop2;
    }
    
    /**
     * 查询车间3数据（淬火、校直、检验、精车孔）
     */
    private Map<String, Object> queryWorkshop3Data(String productId) {
        if (workshop3Abi == null || workshop3Address == null) {
            return null;
        }
        
        Map<String, Object> workshop3 = new HashMap<>();
        try {
            // 查询完整淬火数据
            CallResponse quenchingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getFullQuenchingData",
                Arrays.asList(productId)
            );
            
            if (quenchingResp.getReturnMessage().equals("Success") && quenchingResp.getReturnObject() != null) {
                List<Object> data = quenchingResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> quenching = new HashMap<>();
                    quenching.put("quenchingTemp", data.get(0).toString());
                    quenching.put("coolingMedium", data.get(1).toString());
                    quenching.put("hardness", data.get(2).toString());
                    workshop3.put("quenching", quenching);
                }
            }
            
            // 查询完整校直数据
            CallResponse straighteningResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getFullStraighteningData",
                Arrays.asList(productId)
            );
            
            if (straighteningResp.getReturnMessage().equals("Success") && straighteningResp.getReturnObject() != null) {
                List<Object> data = straighteningResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> straightening = new HashMap<>();
                    straightening.put("straighteningForce", data.get(0).toString());
                    straightening.put("temperingTemp", data.get(1).toString());
                    straightening.put("holdTime", data.get(2).toString());
                    workshop3.put("straightening", straightening);
                }
            }
            
            // 查询完整检验数据
            CallResponse inspectionResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getFullInspectionData",
                Arrays.asList(productId)
            );
            
            if (inspectionResp.getReturnMessage().equals("Success") && inspectionResp.getReturnObject() != null) {
                List<Object> data = inspectionResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> inspection = new HashMap<>();
                    inspection.put("defectLevel", data.get(0).toString());
                    inspection.put("defectPosition", data.get(1).toString());
                    inspection.put("inspectorName", data.get(2).toString());
                    workshop3.put("inspection", inspection);
                }
            }
            
            // 查询完整精车孔数据
            CallResponse fineTurningResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getFullFineTurningData",
                Arrays.asList(productId)
            );
            
            if (fineTurningResp.getReturnMessage().equals("Success") && fineTurningResp.getReturnObject() != null) {
                List<Object> data = fineTurningResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> fineTurning = new HashMap<>();
                    fineTurning.put("holeAccuracy", data.get(0).toString());
                    fineTurning.put("surfaceRoughness", data.get(1).toString());
                    fineTurning.put("turningSpeed", data.get(2).toString());
                    workshop3.put("fineTurning", fineTurning);
                }
            }
            
        } catch (Exception e) {
            log.error("查询车间3数据失败: {}", e.getMessage());
        }
        
        return workshop3.isEmpty() ? null : workshop3;
    }
    
    /**
     * 查询车间4数据（质检、喷漆、包装）
     */
    private Map<String, Object> queryWorkshop4Data(String productId) {
        if (workshop4Abi == null || workshop4Address == null) {
            return null;
        }
        
        Map<String, Object> workshop4 = new HashMap<>();
        try {
            // 查询完整喷漆数据
            CallResponse paintingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop4Address,
                workshop4Abi,
                "getFullPaintingData",
                Arrays.asList(productId)
            );
            
            if (paintingResp.getReturnMessage().equals("Success") && paintingResp.getReturnObject() != null) {
                List<Object> data = paintingResp.getReturnObject();
                if (data.size() >= 4 && (Boolean) data.get(3)) {
                    Map<String, String> painting = new HashMap<>();
                    painting.put("paintThickness", data.get(0).toString());
                    painting.put("sprayPressure", data.get(1).toString());
                    painting.put("paintBatch", data.get(2).toString());
                    workshop4.put("painting", painting);
                }
            }
            
            // 查询完整包装数据
            CallResponse packingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop4Address,
                workshop4Abi,
                "getFullPackingData",
                Arrays.asList(productId)
            );
            
            if (packingResp.getReturnMessage().equals("Success") && packingResp.getReturnObject() != null) {
                List<Object> data = packingResp.getReturnObject();
                if (data.size() >= 3 && (Boolean) data.get(2)) {
                    Map<String, String> packing = new HashMap<>();
                    packing.put("packTime", data.get(0).toString());
                    packing.put("packOperator", data.get(1).toString());
                    workshop4.put("packing", packing);
                }
            }
            
        } catch (Exception e) {
            log.error("查询车间4数据失败: {}", e.getMessage());
        }
        
        return workshop4.isEmpty() ? null : workshop4;
    }
    
    /**
     * AI分析车间工序数据，判断是否存在问题
     */
    public Map<String, Object> analyzeWorkshopData(Map<String, Object> productData) {
        Map<String, Object> result = new HashMap<>();
        
        if (aiAnalysisService == null) {
            log.warn("AI分析服务未启用");
            result.put("success", false);
            result.put("message", "AI分析服务未启用");
            return result;
        }
        
        try {
            String prompt = buildWorkshopAnalysisPrompt(productData);
            String aiResponse = aiAnalysisService.analyzeWithPrompt(prompt);
            result = parseWorkshopAnalysisResponse(aiResponse);
            result.put("success", true);
            log.info("✅ AI车间分析完成");
        } catch (Exception e) {
            log.error("AI车间分析失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "AI分析失败");
            result.putAll(fallbackWorkshopAnalysis(productData));
        }
        
        return result;
    }
    
    private String buildWorkshopAnalysisPrompt(Map<String, Object> productData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的工业生产质量分析专家。请分析以下产品的各车间工序数据，判断是否存在问题，并指出可能有问题的车间。\n\n");
        prompt.append("产品ID: ").append(productData.get("productId")).append("\n\n");
        
        // 车间1
        if (productData.containsKey("workshop1")) {
            Map<String, Object> ws1 = (Map<String, Object>) productData.get("workshop1");
            prompt.append("① 一车间（锻造）\n");
            if (ws1.containsKey("forging")) {
                Map<String, Object> forging = (Map<String, Object>) ws1.get("forging");
                prompt.append("  锻造: 温度=").append(forging.get("forgingTemp"))
                      .append(", 缺陷=").append(forging.get("defect")).append("\n");
            }
        }
        
        // 车间3
        if (productData.containsKey("workshop3")) {
            Map<String, Object> ws3 = (Map<String, Object>) productData.get("workshop3");
            prompt.append("③ 三车间（机加工）\n");
            if (ws3.containsKey("inspection")) {
                Map<String, Object> inspection = (Map<String, Object>) ws3.get("inspection");
                prompt.append("  检验: 缺陷等级=").append(inspection.get("defectLevel")).append("\n");
            }
        }
        
        prompt.append("\n请以JSON格式返回: {\"hasIssue\": true/false, \"problemWorkshops\": [], \"overallAssessment\": \"\"}\n");
        return prompt.toString();
    }
    
    private Map<String, Object> parseWorkshopAnalysisResponse(String aiResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("hasIssue", aiResponse.contains("true"));
        result.put("rawAnalysis", aiResponse);
        return result;
    }
    
    private Map<String, Object> fallbackWorkshopAnalysis(Map<String, Object> productData) {
        Map<String, Object> result = new HashMap<>();
        List<String> problemWorkshops = new ArrayList<>();
        
        // 检查缺陷
        if (productData.containsKey("workshop1")) {
            Map<String, Object> ws1 = (Map<String, Object>) productData.get("workshop1");
            if (ws1.containsKey("forging")) {
                Map<String, Object> forging = (Map<String, Object>) ws1.get("forging");
                String defect = (String) forging.get("defect");
                if (defect != null && !"无".equals(defect)) {
                    problemWorkshops.add("workshop1");
                }
            }
        }
        
        result.put("hasIssue", !problemWorkshops.isEmpty());
        result.put("problemWorkshops", problemWorkshops);
        result.put("overallAssessment", problemWorkshops.isEmpty() ? "未发现问题" : "发现问题");
        result.put("useFallback", true);
        return result;
    }
    
    /**
     * 构建区块链存证信息
     */
    private Map<String, Object> buildBlockchainInfo(String productId) {
        if (!blockchainEnabled || client == null) {
            return null;
        }
        
        try {
            Map<String, Object> blockchainInfo = new HashMap<>();
            
            // 获取最新区块信息
            long blockNumber = client.getBlockNumber().getBlockNumber().longValue();
            String blockHash = client.getBlockHashByNumber(java.math.BigInteger.valueOf(blockNumber)).getBlockHashByNumber();
            
            // 生成交易哈希（模拟：实际应该从交易回执中获取）
            String transactionHash = "0x" + Integer.toHexString(productId.hashCode()) + blockNumber;
            
            // 填充区块链信息
            blockchainInfo.put("blockHash", blockHash);
            blockchainInfo.put("transactionHash", transactionHash);
            blockchainInfo.put("blockNumber", blockNumber);
            blockchainInfo.put("contractAddress", workshop1Address);  // 使用车间1合约地址作为示例
            blockchainInfo.put("fromAddress", cryptoKeyPair.getAddress());
            blockchainInfo.put("onChainTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            blockchainInfo.put("network", "FISCO BCOS");
            blockchainInfo.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ 构建区块链存证信息: 区块高度={}, 区块哈希={}", blockNumber, blockHash);
            return blockchainInfo;
            
        } catch (Exception e) {
            log.error("构建区块链存证信息失败: {}", e.getMessage());
            return null;
        }
    }
}
