package org.example.Demo01.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Data
public class DataCenterService {
  public static final String ABI = org.example.Demo01.utils.IOUtil.readResourceAsString("abi/DataCenter.abi");

  public static final String BINARY = org.example.Demo01.utils.IOUtil.readResourceAsString("bin/ecc/DataCenter.bin");

  public static final String SM_BINARY = org.example.Demo01.utils.IOUtil.readResourceAsString("bin/sm/DataCenter.bin");

  @Value("${system.contract.dataCenterAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse ws1() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "ws1", Arrays.asList());
  }

  public CallResponse getAllData() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getAllData", Arrays.asList());
  }

  public CallResponse rawMat() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "rawMat", Arrays.asList());
  }

  public CallResponse ws3() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "ws3", Arrays.asList());
  }

  public CallResponse ws2() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "ws2", Arrays.asList());
  }

  public CallResponse ws5() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "ws5", Arrays.asList());
  }

  public CallResponse ws4() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "ws4", Arrays.asList());
  }
}
