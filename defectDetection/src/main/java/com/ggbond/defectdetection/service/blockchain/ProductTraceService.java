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
import java.util.*;

/**
 * 产品溯源区块链服务
 * 对接demo的DataCenter智能合约查询产品溯源信息
 * 
 * @author defectDetection
 * @date 2024
 */
@Service
@Slf4j
public class ProductTraceService {
    
    @Value("${blockchain.enabled:false}")
    private boolean blockchainEnabled;
    
    @Value("${blockchain.contracts.data-center:}")
    private String dataCenterAddress;
    
    @Autowired(required = false)
    private Client client;
    
    @Autowired(required = false)
    private CryptoKeyPair cryptoKeyPair;
    
    // 交易处理器
    private AssembleTransactionProcessor txProcessor;
    
    // ABI内容
    private String abi;
    private String workshop3Abi;
    private String workshop4Abi;
    
    @Value("${blockchain.contracts.workshop3}")
    private String workshop3Address;
    
    @Value("${blockchain.contracts.workshop4}")
    private String workshop4Address;
    
    @Autowired(required = false)
    private AIAnalysisService aiAnalysisService;
    
    /**
     * 初始化区块链连接
     */
    @PostConstruct
    public void init() {
        if (!blockchainEnabled) {
            log.info("区块链功能未启用");
            return;
        }
        
        try {
            log.info("开始初始化产品溯源服务...");
            
            // 检查Client是否注入成功
            if (client == null) {
                log.warn("区块链 Client 未注入");
                blockchainEnabled = false;
                return;
            }
            
            if (cryptoKeyPair == null) {
                log.warn("区块链 CryptoKeyPair 未注入");
                blockchainEnabled = false;
                return;
            }
            
            // 加载ABI文件
            abi = loadAbi();
            if (abi == null || abi.isEmpty()) {
                log.error("加载ABI文件失败");
                blockchainEnabled = false;
                return;
            }
            
            // 加载车间3 ABI
            workshop3Abi = loadWorkshop3Abi();
            if (workshop3Abi == null || workshop3Abi.isEmpty()) {
                log.warn("加载车间3 ABI失败，车间3数据查询可能不可用");
            }
            
            // 加载车间4 ABI
            workshop4Abi = loadWorkshop4Abi();
            if (workshop4Abi == null || workshop4Abi.isEmpty()) {
                log.warn("加载车间4 ABI失败，车间4数据查询可能不可用");
            }
            
            // 创建交易处理器
            txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(
                client, cryptoKeyPair
            );
            
            log.info("产品溯源服务初始化成功!");
            log.info("合约地址: {}", dataCenterAddress);
            log.info("账户地址: {}", cryptoKeyPair.getAddress());
            
        } catch (Exception e) {
            log.error("初始化产品溯源服务失败: {}", e.getMessage(), e);
            blockchainEnabled = false;
        }
    }
    
    /**
     * 加载ABI文件
     */
    private String loadAbi() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("abi/DataCenter.abi");
            if (is == null) {
                log.error("ABI文件不存在: abi/DataCenter.abi");
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("加载ABI文件失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 加载车间3 ABI文件
     */
    private String loadWorkshop3Abi() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("abi/WorkshopThree.abi");
            if (is == null) {
                log.warn("ABI文件不存在: abi/WorkshopThree.abi");
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("加载车间3 ABI文件失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 加载车间4 ABI文件
     */
    private String loadWorkshop4Abi() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("abi/WorkshopFour.abi");
            if (is == null) {
                log.warn("ABI文件不存在: abi/WorkshopFour.abi");
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("加载车间4 ABI文件失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 查询产品完整溯源信息
     * 
     * @param productId 产品ID/序列号
     * @return 溯源信息Map
     */
    public Map<String, Object> queryProductTrace(String productId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("查询产品溯源信息: {}", productId);
            
            // 验证产品ID格式
            if (productId == null || productId.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "产品ID不能为空");
                return result;
            }
            
            // 如果区块链未启用,返回错误
            if (!blockchainEnabled || txProcessor == null) {
                log.error("区块链未启用，无法查询产品数据");
                result.put("success", false);
                result.put("message", "区块链服务未启用");
                return result;
            }
            
            // 1. 调用区块链查询产品是否存在
            log.info("调用智能合约 checkDataExists,产品ID: {}", productId);
            CallResponse existsResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "checkDataExists",
                Arrays.asList(productId)
            );
            
            // 检查调用结果
            if (!existsResp.getReturnMessage().equals("Success")) {
                log.error("查询失败: {}", existsResp.getReturnMessage());
                result.put("success", false);
                result.put("message", "区块链查询失败: " + existsResp.getReturnMessage());
                return result;
            }
            
            // 解析返回值 - checkDataExists返回4个bool
            List<Object> returnValues = existsResp.getReturnObject();
            log.info("checkDataExists返回值: {}", returnValues);
            
            // 检查是否有任何数据存在
            boolean hasData = false;
            if (returnValues != null && returnValues.size() >= 4) {
                for (Object val : returnValues) {
                    if (val instanceof Boolean && (Boolean) val) {
                        hasData = true;
                        break;
                    }
                }
            }
            
            if (!hasData) {
                log.warn("产品在链上不存在: {}", productId);
                result.put("success", false);
                result.put("message", "产品 " + productId + " 在区块链上不存在，请检查产品ID是否正确");
                return result;
            }
            
            // 2. 查询产品基础信息
            log.info("调用智能合约 getProductInfo,产品ID: {}", productId);
            CallResponse infoResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getProductInfo",
                Arrays.asList(productId)
            );
            
            if (!infoResp.getReturnMessage().equals("Success")) {
                log.error("查询产品信息失败: {}", infoResp.getReturnMessage());
                result.put("success", false);
                result.put("message", "查询产品信息失败: " + infoResp.getReturnMessage());
                return result;
            }
            
            // 解析产品信息
            List<Object> infoValues = infoResp.getReturnObject();
            log.info("getProductInfo返回值: {}", infoValues);
            
            if (infoValues == null || infoValues.isEmpty()) {
                log.warn("getProductInfo 返回空数据, 产品可能未在链上完成登记: {}", productId);
                result.put("success", false);
                result.put("message", "产品 " + productId + " 在区块链上不存在或数据不完整");
                return result;
            }
            
            // 构建返回数据
            Map<String, Object> productData = new HashMap<>();
            productData.put("productId", productId);
            
            // 根据返回值解析数据（按照合约定义的顺序）
            if (infoValues != null && infoValues.size() >= 5) {
                productData.put("materialBatch", infoValues.get(0).toString());
                productData.put("gearAccuracy", infoValues.get(1).toString());
                productData.put("defectLevel", infoValues.get(2).toString());
                productData.put("paintThickness", infoValues.get(3).toString());
                productData.put("status", parseToInt(infoValues.get(4)));
            }
            
            // 添加默认字段
            productData.put("productionDate", getCurrentDate());
            productData.put("updateTime", getCurrentDateTime());
            
            // 3. 查询各车间详细数据
            Map<String, Object> workshop1Data = queryWorkshop1Data(productId);
            if (workshop1Data != null) {
                productData.put("workshop1", workshop1Data);
            }
            
            Map<String, Object> workshop2Data = queryWorkshop2Data(productId);
            if (workshop2Data != null) {
                productData.put("workshop2", workshop2Data);
            }
            
            // 查询车间3数据
            Map<String, Object> workshop3Data = queryWorkshop3Data(productId);
            if (workshop3Data != null) {
                productData.put("workshop3", workshop3Data);
            }
            
            // 查询车间4数据
            Map<String, Object> workshop4Data = queryWorkshop4Data(productId);
            if (workshop4Data != null) {
                productData.put("workshop4", workshop4Data);
            }
            
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", productData);
            
            log.info("✅ 产品溯源查询成功: {}", productId);
            return result;
            
        } catch (Exception e) {
            log.error("查询产品溯源信息失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 将对象转换为整数
     */
    private int parseToInt(Object obj) {
        if (obj == null) return 0;
        try {
            if (obj instanceof Number) {
                return ((Number) obj).intValue();
            }
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            log.warn("转换整数失败: {}", obj);
            return 0;
        }
    }
    
    /**
     * 获取当前日期
     */
    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    
    /**
     * 获取当前日期时间
     */
    private String getCurrentDateTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    /**
     * 查询车间1数据（切割、压花键、锻造）
     */
    private Map<String, Object> queryWorkshop1Data(String productId) {
        Map<String, Object> workshop1 = new HashMap<>();
        try {
            // 查询切割数据
            CallResponse cuttingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getCuttingData",
                Arrays.asList(productId)
            );
            if (cuttingResp.getReturnMessage().equals("Success")) {
                List<Object> cuttingData = cuttingResp.getReturnObject();
                if (cuttingData != null && cuttingData.size() >= 4) {
                    Map<String, Object> cutting = new HashMap<>();
                    cutting.put("materialBatch", cuttingData.get(0).toString());
                    cutting.put("cutSize", cuttingData.get(1).toString());
                    cutting.put("cutSpeed", cuttingData.get(2).toString());
                    cutting.put("operator", cuttingData.get(3).toString());
                    workshop1.put("cutting", cutting);
                }
            }
            
            // 查询压花键数据
            CallResponse pressingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getPressingData",
                Arrays.asList(productId)
            );
            if (pressingResp.getReturnMessage().equals("Success")) {
                List<Object> pressingData = pressingResp.getReturnObject();
                if (pressingData != null && pressingData.size() >= 3) {
                    Map<String, Object> pressing = new HashMap<>();
                    pressing.put("pressure", pressingData.get(0).toString());
                    pressing.put("splineSize", pressingData.get(1).toString());
                    pressing.put("equipmentNo", pressingData.get(2).toString());
                    workshop1.put("pressing", pressing);
                }
            }
            
            // 查询锻造数据
            CallResponse forgingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getForgingData",
                Arrays.asList(productId)
            );
            if (forgingResp.getReturnMessage().equals("Success")) {
                List<Object> forgingData = forgingResp.getReturnObject();
                if (forgingData != null && forgingData.size() >= 4) {
                    Map<String, Object> forging = new HashMap<>();
                    forging.put("forgingTemp", forgingData.get(0).toString());
                    forging.put("pressure", forgingData.get(1).toString());
                    forging.put("holdTime", forgingData.get(2).toString());
                    forging.put("defect", forgingData.get(3).toString());
                    workshop1.put("forging", forging);
                }
            }
            
            return workshop1.isEmpty() ? null : workshop1;
        } catch (Exception e) {
            log.error("查询车间1数据失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 查询车间2数据（钻孔、热处理、车盘、齿轮加工）
     */
    private Map<String, Object> queryWorkshop2Data(String productId) {
        Map<String, Object> workshop2 = new HashMap<>();
        try {
            // 查询钻孔数据
            CallResponse drillingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getDrillingData",
                Arrays.asList(productId)
            );
            if (drillingResp.getReturnMessage().equals("Success")) {
                List<Object> drillingData = drillingResp.getReturnObject();
                if (drillingData != null && drillingData.size() >= 3) {
                    Map<String, Object> drilling = new HashMap<>();
                    drilling.put("holeSize", drillingData.get(0).toString());
                    drilling.put("holeDepth", drillingData.get(1).toString());
                    drilling.put("equipmentSpeed", drillingData.get(2).toString());
                    workshop2.put("drilling", drilling);
                }
            }
            
            // 查询热处理数据
            CallResponse heatResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getHeatTreatmentData",
                Arrays.asList(productId)
            );
            if (heatResp.getReturnMessage().equals("Success")) {
                List<Object> heatData = heatResp.getReturnObject();
                if (heatData != null && heatData.size() >= 3) {
                    Map<String, Object> heatTreatment = new HashMap<>();
                    heatTreatment.put("heatingTemp", heatData.get(0).toString());
                    heatTreatment.put("holdTime", heatData.get(1).toString());
                    heatTreatment.put("coolingRate", heatData.get(2).toString());
                    workshop2.put("heatTreatment", heatTreatment);
                }
            }
            
            // 查询车盘数据
            CallResponse turningResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getTurningData",
                Arrays.asList(productId)
            );
            if (turningResp.getReturnMessage().equals("Success")) {
                List<Object> turningData = turningResp.getReturnObject();
                if (turningData != null && turningData.size() >= 4) {
                    Map<String, Object> turning = new HashMap<>();
                    turning.put("rotationSpeed", turningData.get(0).toString());
                    turning.put("feedRate", turningData.get(1).toString());
                    turning.put("tolerance", turningData.get(2).toString());
                    turning.put("toolType", turningData.get(3).toString());
                    workshop2.put("turning", turning);
                }
            }
            
            // 查询齿轮加工数据
            CallResponse gearResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                dataCenterAddress,
                abi,
                "getGearData",
                Arrays.asList(productId)
            );
            if (gearResp.getReturnMessage().equals("Success")) {
                List<Object> gearData = gearResp.getReturnObject();
                if (gearData != null && gearData.size() >= 3) {
                    Map<String, Object> gear = new HashMap<>();
                    gear.put("gearAccuracy", gearData.get(0).toString());
                    gear.put("surfaceHardness", gearData.get(1).toString());
                    gear.put("equipmentNo", gearData.get(2).toString());
                    workshop2.put("gear", gear);
                }
            }
            
            return workshop2.isEmpty() ? null : workshop2;
        } catch (Exception e) {
            log.error("查询车间2数据失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 查询车间3数据（按真实合约结构）
     */
    private Map<String, Object> queryWorkshop3Data(String productId) {
        Map<String, Object> workshop3 = new HashMap<>();
        
        // 如果没有workshop3 ABI，跳过查询
        if (workshop3Abi == null || workshop3Abi.isEmpty()) {
            log.warn("车间3 ABI未加载，跳过查询");
            return null;
        }
        
        try {
            // 查询淬火数据 QuenchingData: quenchingTemp, coolingMedium, hardness
            CallResponse quenchingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getQuenchingData",
                Arrays.asList(productId)
            );
            if (quenchingResp.getReturnMessage().equals("Success") && 
                quenchingResp.getReturnObject() != null && 
                !quenchingResp.getReturnObject().isEmpty()) {
                List<Object> quenchingData = quenchingResp.getReturnObject();
                if (quenchingData.size() >= 3) {
                    Map<String, String> quenching = new HashMap<>();
                    quenching.put("quenchingTemp", quenchingData.get(0).toString());
                    quenching.put("coolingMedium", quenchingData.get(1).toString());
                    quenching.put("hardness", quenchingData.get(2).toString());
                    workshop3.put("quenching", quenching);
                }
            }

            // 查询校直数据 StraighteningData: straighteningForce, temperingTemp, holdTime
            CallResponse straighteningResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getStraighteningData",
                Arrays.asList(productId)
            );
            if (straighteningResp.getReturnMessage().equals("Success") && 
                straighteningResp.getReturnObject() != null && 
                !straighteningResp.getReturnObject().isEmpty()) {
                List<Object> straighteningData = straighteningResp.getReturnObject();
                if (straighteningData.size() >= 3) {
                    Map<String, String> straightening = new HashMap<>();
                    straightening.put("straighteningForce", straighteningData.get(0).toString());
                    straightening.put("temperingTemp", straighteningData.get(1).toString());
                    straightening.put("holdTime", straighteningData.get(2).toString());
                    workshop3.put("straightening", straightening);
                }
            }

            // 查询检验数据 InspectionData: defectLevel, defectPosition, inspector
            CallResponse inspectionResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getInspectionData",
                Arrays.asList(productId)
            );
            if (inspectionResp.getReturnMessage().equals("Success") && 
                inspectionResp.getReturnObject() != null && 
                !inspectionResp.getReturnObject().isEmpty()) {
                List<Object> inspectionData = inspectionResp.getReturnObject();
                if (inspectionData.size() >= 3) {
                    Map<String, String> inspection = new HashMap<>();
                    inspection.put("defectLevel", inspectionData.get(0).toString());
                    inspection.put("defectPosition", inspectionData.get(1).toString());
                    inspection.put("inspector", inspectionData.get(2).toString());
                    workshop3.put("inspection", inspection);
                }
            }

            // 查询精车孔数据 FineTurningData: tolerance, holeAccuracy, equipmentNo
            CallResponse fineTurningResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop3Address,
                workshop3Abi,
                "getFineTurningData",
                Arrays.asList(productId)
            );
            if (fineTurningResp.getReturnMessage().equals("Success") && 
                fineTurningResp.getReturnObject() != null && 
                !fineTurningResp.getReturnObject().isEmpty()) {
                List<Object> fineTurningData = fineTurningResp.getReturnObject();
                if (fineTurningData.size() >= 3) {
                    Map<String, String> fineTurning = new HashMap<>();
                    fineTurning.put("tolerance", fineTurningData.get(0).toString());
                    fineTurning.put("holeAccuracy", fineTurningData.get(1).toString());
                    fineTurning.put("equipmentNo", fineTurningData.get(2).toString());
                    workshop3.put("fineTurning", fineTurning);
                }
            }

            return workshop3.isEmpty() ? null : workshop3;
        } catch (Exception e) {
            log.error("查询车间3数据失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 查询车间4数据（按真实合约结构）
     */
    private Map<String, Object> queryWorkshop4Data(String productId) {
        Map<String, Object> workshop4 = new HashMap<>();
        
        // 如果没有workshop4 ABI，跳过查询
        if (workshop4Abi == null || workshop4Abi.isEmpty()) {
            log.warn("车间4 ABI未加载，跳过查询");
            return null;
        }
        
        try {
            // 查询喷漆数据 PaintingData: paintThickness, sprayPressure, paintBatch
            CallResponse paintingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop4Address,
                workshop4Abi,
                "getPaintingData",
                Arrays.asList(productId)
            );
            if (paintingResp.getReturnMessage().equals("Success") && 
                paintingResp.getReturnObject() != null && 
                !paintingResp.getReturnObject().isEmpty()) {
                List<Object> paintingData = paintingResp.getReturnObject();
                if (paintingData.size() >= 3) {
                    Map<String, String> painting = new HashMap<>();
                    painting.put("paintThickness", paintingData.get(0).toString());
                    painting.put("sprayPressure", paintingData.get(1).toString());
                    painting.put("paintBatch", paintingData.get(2).toString());
                    workshop4.put("painting", painting);
                }
            }

            // 查询包装数据 PackingData: packTime, packOperator, snCode
            CallResponse packingResp = txProcessor.sendCall(
                cryptoKeyPair.getAddress(),
                workshop4Address,
                workshop4Abi,
                "getPackingData",
                Arrays.asList(productId)
            );
            if (packingResp.getReturnMessage().equals("Success") && 
                packingResp.getReturnObject() != null && 
                !packingResp.getReturnObject().isEmpty()) {
                List<Object> packingData = packingResp.getReturnObject();
                if (packingData.size() >= 2) {
                    Map<String, String> packing = new HashMap<>();
                    packing.put("packTime", packingData.get(0).toString());
                    packing.put("packOperator", packingData.get(1).toString());
                    // 注意：getPackingData只返回2个字段，没有snCode
                    workshop4.put("packing", packing);
                }
            }

            return workshop4.isEmpty() ? null : workshop4;
        } catch (Exception e) {
            log.error("查询车间4数据失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * AI分析车间工序数据，判断是否存在问题
     * 
     * @param productData 完整的产品数据（包含所有车间数据）
     * @return AI分析结果
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
            // 构建提示词
            String prompt = buildWorkshopAnalysisPrompt(productData);
            
            // 调用AI服务分析
            String aiResponse = callAIForWorkshopAnalysis(prompt);
            
            // 解析AI响应
            result = parseWorkshopAnalysisResponse(aiResponse);
            result.put("success", true);
            
            log.info("✅ AI车间分析完成: {}", productData.get("productId"));
            
        } catch (Exception e) {
            log.error("AI车间分析失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "AI分析失败: " + e.getMessage());
            
            // 使用基础规则分析作为后备
            result.putAll(fallbackWorkshopAnalysis(productData));
        }
        
        return result;
    }
    
    /**
     * 构建车间分析提示词
     */
    private String buildWorkshopAnalysisPrompt(Map<String, Object> productData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的工业生产质量分析专家。请分析以下产品的各车间工序数据，判断是否存在问题，并指出可能有问题的车间。\n\n");
        
        prompt.append("产品ID: ").append(productData.get("productId")).append("\n");
        prompt.append("基本信息:\n");
        prompt.append("  - 材料批次: ").append(productData.get("materialBatch")).append("\n");
        prompt.append("  - 齿轮精度: ").append(productData.get("gearAccuracy")).append("\n");
        prompt.append("  - 缺陷等级: ").append(productData.get("defectLevel")).append("\n");
        prompt.append("  - 漆面厚度: ").append(productData.get("paintThickness")).append("\n\n");
        
        // 一车间数据
        if (productData.containsKey("workshop1")) {
            Map<String, Object> ws1 = (Map<String, Object>) productData.get("workshop1");
            prompt.append("① 一车间（锻造）\n");
            if (ws1.containsKey("cutting")) {
                Map<String, Object> cutting = (Map<String, Object>) ws1.get("cutting");
                prompt.append("  切割下料: 材料批次=").append(cutting.get("materialBatch"))
                      .append(", 尺寸=").append(cutting.get("cutSize"))
                      .append(", 速度=").append(cutting.get("cutSpeed")).append("\n");
            }
            if (ws1.containsKey("pressing")) {
                Map<String, Object> pressing = (Map<String, Object>) ws1.get("pressing");
                prompt.append("  压花键: 压力=").append(pressing.get("pressure"))
                      .append(", 花键尺寸=").append(pressing.get("splineSize")).append("\n");
            }
            if (ws1.containsKey("forging")) {
                Map<String, Object> forging = (Map<String, Object>) ws1.get("forging");
                prompt.append("  锻造: 温度=").append(forging.get("forgingTemp"))
                      .append(", 压力=").append(forging.get("pressure"))
                      .append(", 保压时间=").append(forging.get("holdTime"))
                      .append(", 缺陷=").append(forging.get("defect")).append("\n");
            }
            prompt.append("\n");
        }
        
        // 二车间数据
        if (productData.containsKey("workshop2")) {
            Map<String, Object> ws2 = (Map<String, Object>) productData.get("workshop2");
            prompt.append("② 二车间（热处理）\n");
            if (ws2.containsKey("drilling")) {
                Map<String, Object> drilling = (Map<String, Object>) ws2.get("drilling");
                prompt.append("  钻中心孔: 孔尺寸=").append(drilling.get("holeSize"))
                      .append(", 孔深=").append(drilling.get("holeDepth")).append("\n");
            }
            if (ws2.containsKey("heatTreatment")) {
                Map<String, Object> heat = (Map<String, Object>) ws2.get("heatTreatment");
                prompt.append("  调质热处理: 加热温度=").append(heat.get("heatingTemp"))
                      .append(", 保温时间=").append(heat.get("holdTime"))
                      .append(", 冷却速率=").append(heat.get("coolingRate")).append("\n");
            }
            if (ws2.containsKey("gear")) {
                Map<String, Object> gear = (Map<String, Object>) ws2.get("gear");
                prompt.append("  加工齿: 齿轮精度=").append(gear.get("gearAccuracy"))
                      .append(", 表面硬度=").append(gear.get("surfaceHardness")).append("\n");
            }
            prompt.append("\n");
        }
        
        // 三车间数据
        if (productData.containsKey("workshop3")) {
            Map<String, Object> ws3 = (Map<String, Object>) productData.get("workshop3");
            prompt.append("③ 三车间（机加工）\n");
            if (ws3.containsKey("quenching")) {
                Map<String, Object> quenching = (Map<String, Object>) ws3.get("quenching");
                prompt.append("  淬火: 温度=").append(quenching.get("quenchingTemp"))
                      .append(", 冷却介质=").append(quenching.get("coolingMedium"))
                      .append(", 硬度=").append(quenching.get("hardness")).append("\n");
            }
            if (ws3.containsKey("inspection")) {
                Map<String, Object> inspection = (Map<String, Object>) ws3.get("inspection");
                prompt.append("  检验: 缺陷等级=").append(inspection.get("defectLevel"))
                      .append(", 缺陷位置=").append(inspection.get("defectPosition")).append("\n");
            }
            prompt.append("\n");
        }
        
        // 四车间数据
        if (productData.containsKey("workshop4")) {
            Map<String, Object> ws4 = (Map<String, Object>) productData.get("workshop4");
            prompt.append("④ 四车间（装配）\n");
            if (ws4.containsKey("painting")) {
                Map<String, Object> painting = (Map<String, Object>) ws4.get("painting");
                prompt.append("  喷漆: 厚度=").append(painting.get("paintThickness"))
                      .append(", 压力=").append(painting.get("sprayPressure")).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("请以JSON格式返回分析结果，格式如下：\n");
        prompt.append("{\n");
        prompt.append("  \"hasIssue\": true/false,\n");
        prompt.append("  \"overallAssessment\": \"整体质量评估描述\",\n");
        prompt.append("  \"problemWorkshops\": [\"workshop1\", \"workshop3\"],\n");
        prompt.append("  \"detailedAnalysis\": {\n");
        prompt.append("    \"workshop1\": {\n");
        prompt.append("      \"hasIssue\": true,\n");
        prompt.append("      \"issues\": [\"问题1\", \"问题2\"],\n");
        prompt.append("      \"suggestion\": \"处理建议\"\n");
        prompt.append("    }\n");
        prompt.append("  },\n");
        prompt.append("  \"recommendation\": \"综合处理建议\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }
    
    /**
     * 调用AI进行车间分析
     */
    private String callAIForWorkshopAnalysis(String prompt) {
        return aiAnalysisService.analyzeWithPrompt(prompt);
    }
    
    /**
     * 解析AI车间分析响应
     */
    private Map<String, Object> parseWorkshopAnalysisResponse(String aiResponse) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 提取JSON部分
            String jsonStr = aiResponse;
            if (aiResponse.contains("```json")) {
                jsonStr = aiResponse.substring(
                    aiResponse.indexOf("```json") + 7,
                    aiResponse.lastIndexOf("```")
                ).trim();
            } else if (aiResponse.contains("```")) {
                jsonStr = aiResponse.substring(
                    aiResponse.indexOf("```") + 3,
                    aiResponse.lastIndexOf("```")
                ).trim();
            }
            
            // 简单解析JSON（使用基础解析）
            // 注意：这里需要引入JSON库，或使用简单字符串解析
            result.put("hasIssue", jsonStr.contains("\"hasIssue\": true"));
            result.put("rawAnalysis", jsonStr);
            
        } catch (Exception e) {
            log.error("解析AI车间分析响应失败: {}", e.getMessage());
            result.put("hasIssue", false);
            result.put("error", "解析失败");
        }
        
        return result;
    }
    
    /**
     * 后备车间分析（基于规则）
     */
    private Map<String, Object> fallbackWorkshopAnalysis(Map<String, Object> productData) {
        Map<String, Object> result = new HashMap<>();
        List<String> problemWorkshops = new ArrayList<>();
        Map<String, Object> detailedAnalysis = new HashMap<>();
        
        // 检查缺陷等级
        String defectLevel = (String) productData.get("defectLevel");
        if (defectLevel != null && !"无".equals(defectLevel)) {
            // 有缺陷，需要分析哪个车间
            
            // 检查一车间
            if (productData.containsKey("workshop1")) {
                Map<String, Object> ws1 = (Map<String, Object>) productData.get("workshop1");
                if (ws1.containsKey("forging")) {
                    Map<String, Object> forging = (Map<String, Object>) ws1.get("forging");
                    String forgingDefect = (String) forging.get("defect");
                    if (forgingDefect != null && !"无".equals(forgingDefect)) {
                        problemWorkshops.add("workshop1");
                        Map<String, Object> ws1Analysis = new HashMap<>();
                        ws1Analysis.put("hasIssue", true);
                        ws1Analysis.put("issues", List.of("锻造工序存在缺陷: " + forgingDefect));
                        ws1Analysis.put("suggestion", "建议检查锻造温度和压力参数是否合适");
                        detailedAnalysis.put("workshop1", ws1Analysis);
                    }
                }
            }
            
            // 检查三车间检验数据
            if (productData.containsKey("workshop3")) {
                Map<String, Object> ws3 = (Map<String, Object>) productData.get("workshop3");
                if (ws3.containsKey("inspection")) {
                    Map<String, Object> inspection = (Map<String, Object>) ws3.get("inspection");
                    String inspectionDefect = (String) inspection.get("defectLevel");
                    if (inspectionDefect != null && !"无".equals(inspectionDefect)) {
                        problemWorkshops.add("workshop3");
                        Map<String, Object> ws3Analysis = new HashMap<>();
                        ws3Analysis.put("hasIssue", true);
                        ws3Analysis.put("issues", List.of("检验发现缺陷: " + inspectionDefect));
                        ws3Analysis.put("suggestion", "建议回溯检查前序工序参数");
                        detailedAnalysis.put("workshop3", ws3Analysis);
                    }
                }
            }
        }
        
        result.put("hasIssue", !problemWorkshops.isEmpty());
        result.put("problemWorkshops", problemWorkshops);
        result.put("detailedAnalysis", detailedAnalysis);
        result.put("overallAssessment", problemWorkshops.isEmpty() ? 
            "各车间工序数据正常，未发现明显问题" : 
            "发现 " + problemWorkshops.size() + " 个车间存在潜在问题，建议重点关注");
        result.put("useFallback", true);
        
        return result;
    }
}
