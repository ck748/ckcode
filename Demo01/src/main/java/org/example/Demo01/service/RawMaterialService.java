package org.example.Demo01.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Demo01.model.bo.RawMaterialSetRawMaterialInputBO;
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
  public static final String ABI = org.example.Demo01.utils.IOUtil.readResourceAsString("abi/RawMaterial.abi");

  public static final String BINARY = org.example.Demo01.utils.IOUtil.readResourceAsString("bin/ecc/RawMaterial.bin");

  public static final String SM_BINARY = org.example.Demo01.utils.IOUtil.readResourceAsString("bin/sm/RawMaterial.bin");

  @Value("${system.contract.rawMaterialAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse axleProducerAddr() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "axleProducerAddr", Arrays.asList());
  }

  public TransactionResponse setRawMaterial(RawMaterialSetRawMaterialInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "setRawMaterial", input.toArgs());
  }

  public CallResponse totalRawWeight() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "totalRawWeight", Arrays.asList());
  }

  public CallResponse batchId() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "batchId", Arrays.asList());
  }

  public CallResponse rawProducerAddr() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "rawProducerAddr", Arrays.asList());
  }

  public CallResponse getRawMaterial() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getRawMaterial", Arrays.asList());
  }
}
