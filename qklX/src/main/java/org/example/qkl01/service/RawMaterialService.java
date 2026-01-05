package org.example.qkl01.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.qkl01.model.bo.RawMaterialBatchExistsInputBO;
import org.example.qkl01.model.bo.RawMaterialCreateMaterialBatchInputBO;
import org.example.qkl01.model.bo.RawMaterialGetBatchByIndexInputBO;
import org.example.qkl01.model.bo.RawMaterialGetBatchIdBySNInputBO;
import org.example.qkl01.model.bo.RawMaterialGetBatchInfoBySNInputBO;
import org.example.qkl01.model.bo.RawMaterialGetBatchInfoInputBO;
import org.example.qkl01.model.bo.RawMaterialSnExistsInputBO;
import org.example.qkl01.model.bo.RawMaterialUseMaterialInputBO;
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
public class RawMaterialService {
  public static final String ABI = org.example.qkl01.utils.IOUtil.readResourceAsString("abi/RawMaterial.abi");

  public static final String BINARY = org.example.qkl01.utils.IOUtil.readResourceAsString("bin/ecc/RawMaterial.bin");

  public static final String SM_BINARY = org.example.qkl01.utils.IOUtil.readResourceAsString("bin/sm/RawMaterial.bin");

  @Value("${system.contract.rawMaterialAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse getBatchInfoBySN(RawMaterialGetBatchInfoBySNInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getBatchInfoBySN", input.toArgs());
  }

  public CallResponse getBatchByIndex(RawMaterialGetBatchByIndexInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getBatchByIndex", input.toArgs());
  }

  public CallResponse getAllBatchIds() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getAllBatchIds", Arrays.asList());
  }

  public CallResponse getBatchIdBySN(RawMaterialGetBatchIdBySNInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getBatchIdBySN", input.toArgs());
  }

  public CallResponse getBatchCount() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getBatchCount", Arrays.asList());
  }

  public CallResponse batchExists(RawMaterialBatchExistsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "batchExists", input.toArgs());
  }

  public TransactionResponse useMaterial(RawMaterialUseMaterialInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "useMaterial", input.toArgs());
  }

  public CallResponse getBatchInfo(RawMaterialGetBatchInfoInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getBatchInfo", input.toArgs());
  }

  public TransactionResponse createMaterialBatch(RawMaterialCreateMaterialBatchInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "createMaterialBatch", input.toArgs());
  }

  public CallResponse snExists(RawMaterialSnExistsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "snExists", input.toArgs());
  }
}
