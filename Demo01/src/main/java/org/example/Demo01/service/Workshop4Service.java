package org.example.Demo01.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Demo01.model.bo.Workshop4SetDataInputBO;
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
public class Workshop4Service {
  public static final String ABI = org.example.Demo01.utils.IOUtil.readResourceAsString("abi/Workshop4.abi");

  public static final String BINARY = org.example.Demo01.utils.IOUtil.readResourceAsString("bin/ecc/Workshop4.bin");

  public static final String SM_BINARY = org.example.Demo01.utils.IOUtil.readResourceAsString("bin/sm/Workshop4.bin");

  @Value("${system.contract.workshop4Address}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse workshopNo() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "workshopNo", Arrays.asList());
  }

  public CallResponse qualified() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "qualified", Arrays.asList());
  }

  public TransactionResponse getProcess() throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "getProcess", Arrays.asList());
  }

  public CallResponse prevWorkshop() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "prevWorkshop", Arrays.asList());
  }

  public CallResponse getData() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getData", Arrays.asList());
  }

  public TransactionResponse setData(Workshop4SetDataInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "setData", input.toArgs());
  }

  public CallResponse axleModelId() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "axleModelId", Arrays.asList());
  }

  public CallResponse dataConfirmed() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "dataConfirmed", Arrays.asList());
  }

  public CallResponse unqualified() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "unqualified", Arrays.asList());
  }

  public CallResponse manager() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "manager", Arrays.asList());
  }

  public CallResponse inputQty() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "inputQty", Arrays.asList());
  }
}
