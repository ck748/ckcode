package org.example.Demo9.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Demo9.model.bo.WorkshopOneGetCuttingDetailsInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetCuttingMaterialBatchInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetCuttingSpeedValueInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetForgingDataInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetForgingTempValueInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetFullCuttingDataInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetFullForgingDataInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetHoldTimeValueInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetMaterialBatchStatsInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetPressingDataInputBO;
import org.example.Demo9.model.bo.WorkshopOneGetPressureValueInputBO;
import org.example.Demo9.model.bo.WorkshopOneHasDataInputBO;
import org.example.Demo9.model.bo.WorkshopOneReceiveMaterialInputBO;
import org.example.Demo9.model.bo.WorkshopOneRecordCuttingInputBO;
import org.example.Demo9.model.bo.WorkshopOneRecordForgingInputBO;
import org.example.Demo9.model.bo.WorkshopOneRecordPressingInputBO;
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
public class WorkshopOneService {
  public static final String ABI = org.example.Demo9.utils.IOUtil.readResourceAsString("abi/WorkshopOne.abi");

  public static final String BINARY = org.example.Demo9.utils.IOUtil.readResourceAsString("bin/ecc/WorkshopOne.bin");

  public static final String SM_BINARY = org.example.Demo9.utils.IOUtil.readResourceAsString("bin/sm/WorkshopOne.bin");

  @Value("${system.contract.workshopOneAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse getProductionStats() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProductionStats", Arrays.asList());
  }

  public CallResponse getForgingTempValue(WorkshopOneGetForgingTempValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getForgingTempValue", input.toArgs());
  }

  public TransactionResponse recordCutting(WorkshopOneRecordCuttingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordCutting", input.toArgs());
  }

  public CallResponse getPressureValue(WorkshopOneGetPressureValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPressureValue", input.toArgs());
  }

  public CallResponse getFullForgingData(WorkshopOneGetFullForgingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullForgingData", input.toArgs());
  }

  public CallResponse hasData(WorkshopOneHasDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasData", input.toArgs());
  }

  public TransactionResponse receiveMaterial(WorkshopOneReceiveMaterialInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "receiveMaterial", input.toArgs());
  }

  public CallResponse getCuttingSpeedValue(WorkshopOneGetCuttingSpeedValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getCuttingSpeedValue", input.toArgs());
  }

  public CallResponse getForgingData(WorkshopOneGetForgingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getForgingData", input.toArgs());
  }

  public TransactionResponse recordForging(WorkshopOneRecordForgingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordForging", input.toArgs());
  }

  public CallResponse getCuttingDetails(WorkshopOneGetCuttingDetailsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getCuttingDetails", input.toArgs());
  }

  public CallResponse getMaterialBatchStats(WorkshopOneGetMaterialBatchStatsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getMaterialBatchStats", input.toArgs());
  }

  public CallResponse getFullCuttingData(WorkshopOneGetFullCuttingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullCuttingData", input.toArgs());
  }

  public CallResponse getCuttingMaterialBatch(WorkshopOneGetCuttingMaterialBatchInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getCuttingMaterialBatch", input.toArgs());
  }

  public TransactionResponse recordPressing(WorkshopOneRecordPressingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordPressing", input.toArgs());
  }

  public CallResponse getHoldTimeValue(WorkshopOneGetHoldTimeValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHoldTimeValue", input.toArgs());
  }

  public CallResponse getPressingData(WorkshopOneGetPressingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getPressingData", input.toArgs());
  }
}
