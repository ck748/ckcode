package org.example.Demo8.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Demo8.model.bo.WorkshopThreeGetFineTurningDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetFullFineTurningDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetFullInspectionDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetFullQuenchingDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetFullStraighteningDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetHardnessValueInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetHoldTimeValueInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetInspectionDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetQuenchingDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetQuenchingTempValueInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetStraighteningDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetStraighteningForceValueInputBO;
import org.example.Demo8.model.bo.WorkshopThreeGetTemperingTempValueInputBO;
import org.example.Demo8.model.bo.WorkshopThreeHasDataInputBO;
import org.example.Demo8.model.bo.WorkshopThreeMarkAsReworkInputBO;
import org.example.Demo8.model.bo.WorkshopThreeMarkAsScrapInputBO;
import org.example.Demo8.model.bo.WorkshopThreeReceiveProductsInputBO;
import org.example.Demo8.model.bo.WorkshopThreeRecordFineTurningInputBO;
import org.example.Demo8.model.bo.WorkshopThreeRecordInspectionInputBO;
import org.example.Demo8.model.bo.WorkshopThreeRecordQuenchingInputBO;
import org.example.Demo8.model.bo.WorkshopThreeRecordStraighteningInputBO;
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
public class WorkshopThreeService {
  public static final String ABI = org.example.Demo8.utils.IOUtil.readResourceAsString("abi/WorkshopThree.abi");

  public static final String BINARY = org.example.Demo8.utils.IOUtil.readResourceAsString("bin/ecc/WorkshopThree.bin");

  public static final String SM_BINARY = org.example.Demo8.utils.IOUtil.readResourceAsString("bin/sm/WorkshopThree.bin");

  @Value("${system.contract.workshopThreeAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse getFullQuenchingData(WorkshopThreeGetFullQuenchingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullQuenchingData", input.toArgs());
  }

  public CallResponse getHardnessValue(WorkshopThreeGetHardnessValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHardnessValue", input.toArgs());
  }

  public TransactionResponse recordInspection(WorkshopThreeRecordInspectionInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordInspection", input.toArgs());
  }

  public CallResponse getInspectionData(WorkshopThreeGetInspectionDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getInspectionData", input.toArgs());
  }

  public TransactionResponse recordStraightening(WorkshopThreeRecordStraighteningInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordStraightening", input.toArgs());
  }

  public TransactionResponse recordFineTurning(WorkshopThreeRecordFineTurningInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordFineTurning", input.toArgs());
  }

  public CallResponse hasData(WorkshopThreeHasDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "hasData", input.toArgs());
  }

  public CallResponse getProductionStats() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getProductionStats", Arrays.asList());
  }

  public TransactionResponse receiveProducts(WorkshopThreeReceiveProductsInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "receiveProducts", input.toArgs());
  }

  public CallResponse getStraighteningData(WorkshopThreeGetStraighteningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getStraighteningData", input.toArgs());
  }

  public CallResponse getQuenchingData(WorkshopThreeGetQuenchingDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getQuenchingData", input.toArgs());
  }

  public TransactionResponse markAsRework(WorkshopThreeMarkAsReworkInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "markAsRework", input.toArgs());
  }

  public CallResponse getFullStraighteningData(WorkshopThreeGetFullStraighteningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullStraighteningData", input.toArgs());
  }

  public CallResponse getStraighteningForceValue(WorkshopThreeGetStraighteningForceValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getStraighteningForceValue", input.toArgs());
  }

  public CallResponse getFineTurningData(WorkshopThreeGetFineTurningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFineTurningData", input.toArgs());
  }

  public CallResponse getFullFineTurningData(WorkshopThreeGetFullFineTurningDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullFineTurningData", input.toArgs());
  }

  public TransactionResponse recordQuenching(WorkshopThreeRecordQuenchingInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "recordQuenching", input.toArgs());
  }

  public CallResponse getTemperingTempValue(WorkshopThreeGetTemperingTempValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getTemperingTempValue", input.toArgs());
  }

  public TransactionResponse markAsScrap(WorkshopThreeMarkAsScrapInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "markAsScrap", input.toArgs());
  }

  public CallResponse getFullInspectionData(WorkshopThreeGetFullInspectionDataInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getFullInspectionData", input.toArgs());
  }

  public CallResponse getHoldTimeValue(WorkshopThreeGetHoldTimeValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getHoldTimeValue", input.toArgs());
  }

  public CallResponse getQuenchingTempValue(WorkshopThreeGetQuenchingTempValueInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getQuenchingTempValue", input.toArgs());
  }
}
