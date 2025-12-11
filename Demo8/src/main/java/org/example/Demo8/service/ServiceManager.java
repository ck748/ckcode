package org.example.Demo8.service;

import java.lang.Exception;
import java.lang.String;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.Demo8.config.SystemConfig;
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
   * @notice: must use @Qualifier("WorkshopTwoService") with @Autowired to get this Bean
   */
  @Bean("WorkshopTwoService")
  public Map<String, WorkshopTwoService> initWorkshopTwoServiceManager() throws Exception {
    Map<String, WorkshopTwoService> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
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
    	WorkshopTwoService workshopTwoService = new WorkshopTwoService();
    	workshopTwoService.setAddress(this.config.getContract().getWorkshopTwoAddress());
    	workshopTwoService.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshopTwoService.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshopTwoService);
    }
    log.info("++++++++WorkshopTwoService map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("WorkshopThreeService") with @Autowired to get this Bean
   */
  @Bean("WorkshopThreeService")
  public Map<String, WorkshopThreeService> initWorkshopThreeServiceManager() throws Exception {
    Map<String, WorkshopThreeService> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
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
    	WorkshopThreeService workshopThreeService = new WorkshopThreeService();
    	workshopThreeService.setAddress(this.config.getContract().getWorkshopThreeAddress());
    	workshopThreeService.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshopThreeService.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshopThreeService);
    }
    log.info("++++++++WorkshopThreeService map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("WorkshopOneService") with @Autowired to get this Bean
   */
  @Bean("WorkshopOneService")
  public Map<String, WorkshopOneService> initWorkshopOneServiceManager() throws Exception {
    Map<String, WorkshopOneService> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
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
    	WorkshopOneService workshopOneService = new WorkshopOneService();
    	workshopOneService.setAddress(this.config.getContract().getWorkshopOneAddress());
    	workshopOneService.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshopOneService.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshopOneService);
    }
    log.info("++++++++WorkshopOneService map:{}", serviceMap);
    return serviceMap;
  }

  /**
   * @notice: must use @Qualifier("WorkshopFourService") with @Autowired to get this Bean
   */
  @Bean("WorkshopFourService")
  public Map<String, WorkshopFourService> initWorkshopFourServiceManager() throws Exception {
    Map<String, WorkshopFourService> serviceMap = new ConcurrentHashMap<>(this.hexPrivateKeyList.size());
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
    	WorkshopFourService workshopFourService = new WorkshopFourService();
    	workshopFourService.setAddress(this.config.getContract().getWorkshopFourAddress());
    	workshopFourService.setClient(this.client);
    	org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor = 
    		org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
    	workshopFourService.setTxProcessor(txProcessor);
    	serviceMap.put(userAddress, workshopFourService);
    }
    log.info("++++++++WorkshopFourService map:{}", serviceMap);
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
