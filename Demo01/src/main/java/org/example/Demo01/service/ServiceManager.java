package org.example.Demo01.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.Demo01.config.SystemConfig;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@Slf4j
public class ServiceManager {
  @Autowired
  private SystemConfig config;

  @Autowired
  private Client client;

  List<String> hexPrivateKeyList;

  @PostConstruct
  public void init() {
    hexPrivateKeyList = Arrays.asList(this.config.getHexPrivateKey().split(","));
  }

  /**
   * @notice: must use @Qualifier("Workshop5Service") with @Autowired to get this Bean
   */
  @Bean("Workshop5Service")
  public Map<String, Workshop5Service> initWorkshop5ServiceManager() throws Exception {
    Map<String, Workshop5Service> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
    for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
    	String privateKey = this.hexPrivateKeyList.get(i);
    	if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
    		privateKey = privateKey.substring(2);
    	}
    	if (privateKey.isEmpty()) {
    		continue;
    	}
    	org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
    	org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
    	String userAddress = cryptoKeyPair.getAddress();
    	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
    	Workshop5Service workshop5Service = new Workshop5Service();
    	workshop5Service.setAddress(this.config.getContract().getWorkshop5Address());
    	workshop5Service.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshop5Service.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshop5Service);
    }
    log.info("++++++++Workshop5Service map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("Workshop4Service") with @Autowired to get this Bean
   */
  @Bean("Workshop4Service")
  public Map<String, Workshop4Service> initWorkshop4ServiceManager() throws Exception {
    Map<String, Workshop4Service> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
    for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
    	String privateKey = this.hexPrivateKeyList.get(i);
    	if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
    		privateKey = privateKey.substring(2);
    	}
    	if (privateKey.isEmpty()) {
    		continue;
    	}
    	org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
    	org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
    	String userAddress = cryptoKeyPair.getAddress();
    	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
    	Workshop4Service workshop4Service = new Workshop4Service();
    	workshop4Service.setAddress(this.config.getContract().getWorkshop4Address());
    	workshop4Service.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshop4Service.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshop4Service);
    }
    log.info("++++++++Workshop4Service map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("Workshop3Service") with @Autowired to get this Bean
   */
  @Bean("Workshop3Service")
  public Map<String, Workshop3Service> initWorkshop3ServiceManager() throws Exception {
    Map<String, Workshop3Service> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
    for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
    	String privateKey = this.hexPrivateKeyList.get(i);
    	if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
    		privateKey = privateKey.substring(2);
    	}
    	if (privateKey.isEmpty()) {
    		continue;
    	}
    	org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
    	org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
    	String userAddress = cryptoKeyPair.getAddress();
    	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
    	Workshop3Service workshop3Service = new Workshop3Service();
    	workshop3Service.setAddress(this.config.getContract().getWorkshop3Address());
    	workshop3Service.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshop3Service.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshop3Service);
    }
    log.info("++++++++Workshop3Service map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("Workshop2Service") with @Autowired to get this Bean
   */
  @Bean("Workshop2Service")
  public Map<String, Workshop2Service> initWorkshop2ServiceManager() throws Exception {
    Map<String, Workshop2Service> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
    for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
    	String privateKey = this.hexPrivateKeyList.get(i);
    	if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
    		privateKey = privateKey.substring(2);
    	}
    	if (privateKey.isEmpty()) {
    		continue;
    	}
    	org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
    	org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
    	String userAddress = cryptoKeyPair.getAddress();
    	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
    	Workshop2Service workshop2Service = new Workshop2Service();
    	workshop2Service.setAddress(this.config.getContract().getWorkshop2Address());
    	workshop2Service.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshop2Service.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshop2Service);
    }
    log.info("++++++++Workshop2Service map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("Workshop1Service") with @Autowired to get this Bean
   */
  @Bean("Workshop1Service")
  public Map<String, Workshop1Service> initWorkshop1ServiceManager() throws Exception {
    Map<String, Workshop1Service> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
    for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
    	String privateKey = this.hexPrivateKeyList.get(i);
    	if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
    		privateKey = privateKey.substring(2);
    	}
    	if (privateKey.isEmpty()) {
    		continue;
    	}
    	org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
    	org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
    	String userAddress = cryptoKeyPair.getAddress();
    	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
    	Workshop1Service workshop1Service = new Workshop1Service();
    	workshop1Service.setAddress(this.config.getContract().getWorkshop1Address());
    	workshop1Service.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshop1Service.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshop1Service);
    }
    log.info("++++++++Workshop1Service map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("RawMaterialService") with @Autowired to get this Bean
   */
  @Bean("RawMaterialService")
  public Map<String, RawMaterialService> initRawMaterialServiceManager() throws Exception {
    Map<String, RawMaterialService> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
    for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
    	String privateKey = this.hexPrivateKeyList.get(i);
    	if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
    		privateKey = privateKey.substring(2);
    	}
    	if (privateKey.isEmpty()) {
    		continue;
    	}
    	org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
    	org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
    	String userAddress = cryptoKeyPair.getAddress();
    	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
    	RawMaterialService rawMaterialService = new RawMaterialService();
    	rawMaterialService.setAddress(this.config.getContract().getRawMaterialAddress());
    	rawMaterialService.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	rawMaterialService.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, rawMaterialService);
    }
    log.info("++++++++RawMaterialService map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("DataCenterService") with @Autowired to get this Bean
   */
  @Bean("DataCenterService")
  public Map<String, DataCenterService> initDataCenterServiceManager() throws Exception {
    Map<String, DataCenterService> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
    for (int i = 0; i < this.hexPrivateKeyList.size(); i++) {
    	String privateKey = this.hexPrivateKeyList.get(i);
    	if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
    		privateKey = privateKey.substring(2);
    	}
    	if (privateKey.isEmpty()) {
    		continue;
    	}
    	org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
    	org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
    	String userAddress = cryptoKeyPair.getAddress();
    	log.info("++++++++hexPrivateKeyList[{}]:{},userAddress:{}", i, privateKey, userAddress);
    	DataCenterService dataCenterService = new DataCenterService();
    	dataCenterService.setAddress(this.config.getContract().getDataCenterAddress());
    	dataCenterService.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	dataCenterService.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, dataCenterService);
    }
    log.info("++++++++DataCenterService map:{}", serviceMap);
    return serviceMap;
  }
}
