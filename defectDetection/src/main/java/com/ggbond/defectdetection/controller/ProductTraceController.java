package com.ggbond.defectdetection.controller;

import com.ggbond.defectdetection.common.Result;
import com.ggbond.defectdetection.service.blockchain.NewProductTraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 产品溯源控制器
 * 提供产品溯源查询接口
 * 
 * @author defectDetection
 * @date 2024
 */
@RestController
@Slf4j
public class ProductTraceController {

    @Autowired
    private NewProductTraceService productTraceService;

    /**
     * 查询产品溯源信息
     * 
     * @param productId 产品ID/半轴编码
     * @return 溯源信息
     */
    @GetMapping({"/api/trace/query/{productId}", "/trace/query/{productId}"})
    public Result queryProductTrace(@PathVariable String productId) {
        try {
            log.info("查询产品溯源信息: {}", productId);
            
            if (productId == null || productId.trim().isEmpty()) {
                return Result.fail("产品ID不能为空");
            }
            
            Map<String, Object> result = productTraceService.queryProductTrace(productId);
            
            // 检查查询结果
            Boolean success = (Boolean) result.get("success");
            if (success != null && success) {
                return Result.success("查询成功", result.get("data"));
            } else {
                String message = (String) result.get("message");
                return Result.fail(message != null ? message : "查询失败");
            }
            
        } catch (Exception e) {
            log.error("查询产品溯源信息失败: {}", e.getMessage(), e);
            return Result.fail("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * AI分析车间工序数据
     * 
     * @param productId 产品ID
     * @return AI分析结果
     */
    @GetMapping({"/api/trace/analyze/{productId}", "/trace/analyze/{productId}"})
    public Result analyzeWorkshopData(@PathVariable String productId) {
        try {
            log.info("🤖 AI分析车间数据: {}", productId);
            
            if (productId == null || productId.trim().isEmpty()) {
                return Result.fail("产品ID不能为空");
            }
            
            // 先查询产品数据
            Map<String, Object> queryResult = productTraceService.queryProductTrace(productId);
            Boolean querySuccess = (Boolean) queryResult.get("success");
            
            if (querySuccess == null || !querySuccess) {
                return Result.fail("无法查询到产品数据，请先确认产品ID正确");
            }
            
            // 获取产品数据
            Map<String, Object> productData = (Map<String, Object>) queryResult.get("data");
            
            // AI分析
            Map<String, Object> analysisResult = productTraceService.analyzeWorkshopData(productData);
            Boolean analysisSuccess = (Boolean) analysisResult.get("success");
            
            if (analysisSuccess != null && analysisSuccess) {
                return Result.success("AI分析完成", analysisResult);
            } else {
                String message = (String) analysisResult.get("message");
                // 即使失败，也返回后备分析结果
                return Result.success(message != null ? message : "AI分析完成（使用后备方案）", analysisResult);
            }
            
        } catch (Exception e) {
            log.error("分析车间数据失败: {}", e.getMessage(), e);
            return Result.fail("分析失败: " + e.getMessage());
        }
    }
}
