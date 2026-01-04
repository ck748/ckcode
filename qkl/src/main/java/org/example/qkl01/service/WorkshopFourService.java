package org.example.qkl01.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.qkl01.model.bo.WorkshopFourGetFullPackingDataInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetFullPaintingDataInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetPackTimeValueInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetPackingDataInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetPaintBatchByIndexInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetPaintBatchStatsInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetPaintThicknessValueInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetPaintingDataInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetQualityInspectionDataInputBO;
import org.example.qkl01.model.bo.WorkshopFourGetSprayPressureValueInputBO;
import org.example.qkl01.model.bo.WorkshopFourHasDataInputBO;
import org.example.qkl01.model.bo.WorkshopFourHasPaintBatchInputBO;
import org.example.qkl01.model.bo.WorkshopFourIsInspectedInputBO;
import org.example.qkl01.model.bo.WorkshopFourReceiveProductsInputBO;
import org.example.qkl01.model.bo.WorkshopFourRecordPackingInputBO;
import org.example.qkl01.model.bo.WorkshopFourRecordPaintingInputBO;
import org.example.qkl01.model.bo.WorkshopFourRecordQualityInspectionBatchInputBO;
import org.example.qkl01.model.bo.WorkshopFourRecordQualityInspectionSingleInputBO;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Data
public class WorkshopFourService {
  public static final String ABI = org.example.qkl01.utils.IOUtil.readResourceAsString("abi/WorkshopFour.abi");

  public static final String BINARY = org.example.qkl01.utils.IOUtil.readResourceAsString("bin/ecc/WorkshopFour.bin");

  public static final String SM_BINARY = org.example.qkl01.utils.IOUtil.readResourceAsString("bin/sm/WorkshopFour.bin");

  @Value("${system.contract.workshopFourAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse getQualityInspectionData(WorkshopFourGetQualityInspectionDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getQualityInspectionData", input.toArgs());
  }

  public CallResponse getPackTimeValue(WorkshopFourGetPackTimeValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPackTimeValue", input.toArgs());
  }

  public CallResponse getProductionStats() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProductionStats", Arrays.asList());
  }

  public CallResponse getPaintBatchStats(WorkshopFourGetPaintBatchStatsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPaintBatchStats", input.toArgs());
  }

  public TransactionResponse recordPainting(WorkshopFourRecordPaintingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordPainting", input.toArgs());
  }

  public CallResponse getPaintBatchCount() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPaintBatchCount", Arrays.asList());
  }

  public CallResponse getPaintThicknessValue(WorkshopFourGetPaintThicknessValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPaintThicknessValue", input.toArgs());
  }

  public CallResponse getPaintingData(WorkshopFourGetPaintingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPaintingData", input.toArgs());
  }

  public CallResponse hasData(WorkshopFourHasDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasData", input.toArgs());
  }

  public TransactionResponse recordQualityInspectionSingle(WorkshopFourRecordQualityInspectionSingleInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordQualityInspectionSingle", input.toArgs());
  }

  public CallResponse isInspected(WorkshopFourIsInspectedInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "isInspected", input.toArgs());
  }

  public TransactionResponse receiveProducts(WorkshopFourReceiveProductsInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "receiveProducts", input.toArgs());
  }

  public CallResponse getQualityInspectionStats() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getQualityInspectionStats", Arrays.asList());
  }

  public CallResponse getProcess() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProcess", Arrays.asList());
  }

  public TransactionResponse recordPacking(WorkshopFourRecordPackingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordPacking", input.toArgs());
  }

  public CallResponse getPaintBatchByIndex(WorkshopFourGetPaintBatchByIndexInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPaintBatchByIndex", input.toArgs());
  }

  public CallResponse getFullPackingData(WorkshopFourGetFullPackingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullPackingData", input.toArgs());
  }

  public CallResponse getPackingData(WorkshopFourGetPackingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPackingData", input.toArgs());
  }

  public CallResponse getFullPaintingData(WorkshopFourGetFullPaintingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullPaintingData", input.toArgs());
  }

  public CallResponse getSprayPressureValue(WorkshopFourGetSprayPressureValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getSprayPressureValue", input.toArgs());
  }

  public CallResponse hasPaintBatch(WorkshopFourHasPaintBatchInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasPaintBatch", input.toArgs());
  }

  public TransactionResponse recordQualityInspectionBatch(WorkshopFourRecordQualityInspectionBatchInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordQualityInspectionBatch", input.toArgs());
  }
}
