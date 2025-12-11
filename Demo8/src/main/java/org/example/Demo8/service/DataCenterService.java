package org.example.Demo8.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Demo8.model.bo.DataCenterBatchCheckExistsInputBO;
import org.example.Demo8.model.bo.DataCenterBatchCheckStatusInputBO;
import org.example.Demo8.model.bo.DataCenterCheckDataExistsInputBO;
import org.example.Demo8.model.bo.DataCenterGetCuttingDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetCuttingSpeedValueInputBO;
import org.example.Demo8.model.bo.DataCenterGetDrillingDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetFineTurningDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetForgingDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetForgingTempValueInputBO;
import org.example.Demo8.model.bo.DataCenterGetGearDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetHeatTreatmentDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetHeatingTempValueInputBO;
import org.example.Demo8.model.bo.DataCenterGetInspectionDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetPackTimeValueInputBO;
import org.example.Demo8.model.bo.DataCenterGetPackingDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetPaintThicknessValueInputBO;
import org.example.Demo8.model.bo.DataCenterGetPaintingDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetPressingDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetProductInfoInputBO;
import org.example.Demo8.model.bo.DataCenterGetProductInfoWithValuesInputBO;
import org.example.Demo8.model.bo.DataCenterGetProductStatusInputBO;
import org.example.Demo8.model.bo.DataCenterGetQuenchingDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetQuenchingTempValueInputBO;
import org.example.Demo8.model.bo.DataCenterGetStraighteningDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetTurningDataInputBO;
import org.example.Demo8.model.bo.DataCenterGetWorkshopFourAllInputBO;
import org.example.Demo8.model.bo.DataCenterGetWorkshopOneAllInputBO;
import org.example.Demo8.model.bo.DataCenterGetWorkshopThreeAllInputBO;
import org.example.Demo8.model.bo.DataCenterGetWorkshopTwoAllInputBO;
import org.example.Demo8.model.bo.DataCenterHasWorkshopFourDataInputBO;
import org.example.Demo8.model.bo.DataCenterHasWorkshopOneDataInputBO;
import org.example.Demo8.model.bo.DataCenterHasWorkshopThreeDataInputBO;
import org.example.Demo8.model.bo.DataCenterHasWorkshopTwoDataInputBO;
import org.example.Demo8.model.bo.DataCenterQueryFullTraceInputBO;
import org.example.Demo8.model.bo.DataCenterStringToUintInputBO;
import org.example.Demo8.model.bo.DataCenterUpdateWorkshopAddressInputBO;
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
public class DataCenterService {
  public static final String ABI = org.example.Demo8.utils.IOUtil.readResourceAsString("abi/DataCenter.abi");

  public static final String BINARY = org.example.Demo8.utils.IOUtil.readResourceAsString("bin/ecc/DataCenter.bin");

  public static final String SM_BINARY = org.example.Demo8.utils.IOUtil.readResourceAsString("bin/sm/DataCenter.bin");

  @Value("${system.contract.dataCenterAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse getPackTimeValue(DataCenterGetPackTimeValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPackTimeValue", input.toArgs());
  }

  public CallResponse workshopTwoAddr() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "workshopTwoAddr", Arrays.asList());
  }

  public CallResponse getForgingTempValue(DataCenterGetForgingTempValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getForgingTempValue", input.toArgs());
  }

  public TransactionResponse getWorkshopTwoAll(DataCenterGetWorkshopTwoAllInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "getWorkshopTwoAll", input.toArgs());
  }

  public CallResponse getPaintingData(DataCenterGetPaintingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPaintingData", input.toArgs());
  }

  public CallResponse hasWorkshopOneData(DataCenterHasWorkshopOneDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasWorkshopOneData", input.toArgs());
  }

  public CallResponse getCuttingSpeedValue(DataCenterGetCuttingSpeedValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getCuttingSpeedValue", input.toArgs());
  }

  public CallResponse getQuenchingData(DataCenterGetQuenchingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getQuenchingData", input.toArgs());
  }

  public CallResponse getProductStatus(DataCenterGetProductStatusInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProductStatus", input.toArgs());
  }

  public CallResponse getFineTurningData(DataCenterGetFineTurningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFineTurningData", input.toArgs());
  }

  public TransactionResponse stringToUint(DataCenterStringToUintInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "stringToUint", input.toArgs());
  }

  public CallResponse batchCheckExists(DataCenterBatchCheckExistsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "batchCheckExists", input.toArgs());
  }

  public CallResponse getHeatTreatmentData(DataCenterGetHeatTreatmentDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHeatTreatmentData", input.toArgs());
  }

  public CallResponse getWorkshopAddresses() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getWorkshopAddresses", Arrays.asList());
  }

  public CallResponse getCuttingData(DataCenterGetCuttingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getCuttingData", input.toArgs());
  }

  public CallResponse getTurningData(DataCenterGetTurningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getTurningData", input.toArgs());
  }

  public CallResponse getPressingData(DataCenterGetPressingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPressingData", input.toArgs());
  }

  public CallResponse getProductInfoWithValues(DataCenterGetProductInfoWithValuesInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProductInfoWithValues", input.toArgs());
  }

  public CallResponse getDrillingData(DataCenterGetDrillingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getDrillingData", input.toArgs());
  }

  public CallResponse workshopThreeAddr() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "workshopThreeAddr", Arrays.asList());
  }

  public CallResponse hasWorkshopFourData(DataCenterHasWorkshopFourDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasWorkshopFourData", input.toArgs());
  }

  public CallResponse getGearData(DataCenterGetGearDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getGearData", input.toArgs());
  }

  public TransactionResponse queryFullTrace(DataCenterQueryFullTraceInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "queryFullTrace", input.toArgs());
  }

  public CallResponse getPaintThicknessValue(DataCenterGetPaintThicknessValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPaintThicknessValue", input.toArgs());
  }

  public CallResponse getInspectionData(DataCenterGetInspectionDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getInspectionData", input.toArgs());
  }

  public TransactionResponse updateWorkshopAddress(DataCenterUpdateWorkshopAddressInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "updateWorkshopAddress", input.toArgs());
  }

  public TransactionResponse getWorkshopThreeAll(DataCenterGetWorkshopThreeAllInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "getWorkshopThreeAll", input.toArgs());
  }

  public CallResponse getHeatingTempValue(DataCenterGetHeatingTempValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHeatingTempValue", input.toArgs());
  }

  public TransactionResponse getWorkshopFourAll(DataCenterGetWorkshopFourAllInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "getWorkshopFourAll", input.toArgs());
  }

  public CallResponse workshopFourAddr() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "workshopFourAddr", Arrays.asList());
  }

  public CallResponse hasWorkshopTwoData(DataCenterHasWorkshopTwoDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasWorkshopTwoData", input.toArgs());
  }

  public CallResponse getStraighteningData(DataCenterGetStraighteningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getStraighteningData", input.toArgs());
  }

  public CallResponse getForgingData(DataCenterGetForgingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getForgingData", input.toArgs());
  }

  public CallResponse hasWorkshopThreeData(DataCenterHasWorkshopThreeDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasWorkshopThreeData", input.toArgs());
  }

  public TransactionResponse getWorkshopOneAll(DataCenterGetWorkshopOneAllInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "getWorkshopOneAll", input.toArgs());
  }

  public CallResponse getPackingData(DataCenterGetPackingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPackingData", input.toArgs());
  }

  public CallResponse getProductInfo(DataCenterGetProductInfoInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProductInfo", input.toArgs());
  }

  public CallResponse workshopOneAddr() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "workshopOneAddr", Arrays.asList());
  }

  public CallResponse checkDataExists(DataCenterCheckDataExistsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "checkDataExists", input.toArgs());
  }

  public CallResponse batchCheckStatus(DataCenterBatchCheckStatusInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "batchCheckStatus", input.toArgs());
  }

  public CallResponse getQuenchingTempValue(DataCenterGetQuenchingTempValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getQuenchingTempValue", input.toArgs());
  }
}
