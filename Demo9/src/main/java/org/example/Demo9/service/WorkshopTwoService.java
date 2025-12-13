package org.example.Demo9.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Demo9.model.bo.WorkshopTwoGetCoolingRateValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetDrillingDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetEquipmentSpeedValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetFeedRateValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetFullDrillingDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetFullGearProcessingDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetFullHeatTreatmentDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetFullTurningDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetGearProcessingDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetHeatTreatmentDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetHeatingTempValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetHoldTimeValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetHoleDepthValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetRotationSpeedValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetSurfaceHardnessValueInputBO;
import org.example.Demo9.model.bo.WorkshopTwoGetTurningDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoHasDataInputBO;
import org.example.Demo9.model.bo.WorkshopTwoMarkAsScrapInputBO;
import org.example.Demo9.model.bo.WorkshopTwoMarkAsScrapWithReasonInputBO;
import org.example.Demo9.model.bo.WorkshopTwoReceiveProductsInputBO;
import org.example.Demo9.model.bo.WorkshopTwoRecordDrillingInputBO;
import org.example.Demo9.model.bo.WorkshopTwoRecordGearProcessingInputBO;
import org.example.Demo9.model.bo.WorkshopTwoRecordHeatTreatmentInputBO;
import org.example.Demo9.model.bo.WorkshopTwoRecordTurningInputBO;
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
public class WorkshopTwoService {
  public static final String ABI = org.example.Demo9.utils.IOUtil.readResourceAsString("abi/WorkshopTwo.abi");

  public static final String BINARY = org.example.Demo9.utils.IOUtil.readResourceAsString("bin/ecc/WorkshopTwo.bin");

  public static final String SM_BINARY = org.example.Demo9.utils.IOUtil.readResourceAsString("bin/sm/WorkshopTwo.bin");

  @Value("${system.contract.workshopTwoAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse getFullDrillingData(WorkshopTwoGetFullDrillingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullDrillingData", input.toArgs());
  }

  public CallResponse getCoolingRateValue(WorkshopTwoGetCoolingRateValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getCoolingRateValue", input.toArgs());
  }

  public TransactionResponse recordHeatTreatment(WorkshopTwoRecordHeatTreatmentInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordHeatTreatment", input.toArgs());
  }

  public CallResponse getGearProcessingData(WorkshopTwoGetGearProcessingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getGearProcessingData", input.toArgs());
  }

  public CallResponse hasData(WorkshopTwoHasDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasData", input.toArgs());
  }

  public CallResponse getFullHeatTreatmentData(WorkshopTwoGetFullHeatTreatmentDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullHeatTreatmentData", input.toArgs());
  }

  public TransactionResponse recordDrilling(WorkshopTwoRecordDrillingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordDrilling", input.toArgs());
  }

  public CallResponse getHoleDepthValue(WorkshopTwoGetHoleDepthValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHoleDepthValue", input.toArgs());
  }

  public TransactionResponse markAsScrapWithReason(WorkshopTwoMarkAsScrapWithReasonInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "markAsScrapWithReason", input.toArgs());
  }

  public CallResponse getHeatTreatmentData(WorkshopTwoGetHeatTreatmentDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHeatTreatmentData", input.toArgs());
  }

  public CallResponse getFullTurningData(WorkshopTwoGetFullTurningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullTurningData", input.toArgs());
  }

  public CallResponse getTurningData(WorkshopTwoGetTurningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getTurningData", input.toArgs());
  }

  public CallResponse getDrillingData(WorkshopTwoGetDrillingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getDrillingData", input.toArgs());
  }

  public CallResponse getFullGearProcessingData(WorkshopTwoGetFullGearProcessingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullGearProcessingData", input.toArgs());
  }

  public CallResponse getSurfaceHardnessValue(WorkshopTwoGetSurfaceHardnessValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getSurfaceHardnessValue", input.toArgs());
  }

  public CallResponse getHeatingTempValue(WorkshopTwoGetHeatingTempValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHeatingTempValue", input.toArgs());
  }

  public TransactionResponse receiveProducts(WorkshopTwoReceiveProductsInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "receiveProducts", input.toArgs());
  }

  public CallResponse getRotationSpeedValue(WorkshopTwoGetRotationSpeedValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getRotationSpeedValue", input.toArgs());
  }

  public CallResponse getFeedRateValue(WorkshopTwoGetFeedRateValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFeedRateValue", input.toArgs());
  }

  public TransactionResponse recordGearProcessing(WorkshopTwoRecordGearProcessingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordGearProcessing", input.toArgs());
  }

  public CallResponse getProductionStats() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProductionStats", Arrays.asList());
  }

  public TransactionResponse markAsScrap(WorkshopTwoMarkAsScrapInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "markAsScrap", input.toArgs());
  }

  public CallResponse getHoldTimeValue(WorkshopTwoGetHoldTimeValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHoldTimeValue", input.toArgs());
  }

  public TransactionResponse recordTurning(WorkshopTwoRecordTurningInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordTurning", input.toArgs());
  }

  public CallResponse getEquipmentSpeedValue(WorkshopTwoGetEquipmentSpeedValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getEquipmentSpeedValue", input.toArgs());
  }
}
