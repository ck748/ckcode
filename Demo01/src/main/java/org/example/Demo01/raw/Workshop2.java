import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Bool;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.StaticArray8;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple6;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class Workshop2 extends Contract {
    public static final String[] BINARY_ARRAY = {"60806040527fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff6002556040518060400160405280600981526020017fe4ba8ce8bda6e997b4000000000000000000000000000000000000000000000081525060089080519060200190620000759291906200045b565b506000600960006101000a81548160ff0219169083151502179055503480156200009e57600080fd5b5060405162001e1a38038062001e1a8339818101604052810190620000c4919062000521565b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506040518060400160405280600c81526020017fe992bbe4b8ade5bf83e5ad94000000000000000000000000000000000000000081525060016040516200014a90620007aa565b908152602001604051809103902090805190602001906200016d9291906200045b565b506040518060400160405280600f81526020017fe8b083e8b4a8e783ade5a484e7908600000000000000000000000000000000008152506001604051620001b49062000793565b90815260200160405180910390209080519060200190620001d79291906200045b565b506040518060400160405280600981526020017fe7b297e68b8be4b8b8000000000000000000000000000000000000000000000081525060016040516200021e9062000737565b90815260200160405180910390209080519060200190620002419291906200045b565b506040518060400160405280600681526020017fe7b297e6a0a10000000000000000000000000000000000000000000000000000815250600160405162000288906200077c565b90815260200160405180910390209080519060200190620002ab9291906200045b565b506040518060400160405280600981526020017fe7b297e8bda6e79b9800000000000000000000000000000000000000000000008152506001604051620002f290620007c1565b90815260200160405180910390209080519060200190620003159291906200045b565b506040518060400160405280600981526020017fe7b297e8bda6e69d86000000000000000000000000000000000000000000000081525060016040516200035c906200074e565b908152602001604051809103902090805190602001906200037f9291906200045b565b506040518060400160405280600981526020017fe7b2bee8bda6e69d8600000000000000000000000000000000000000000000008152506001604051620003c69062000765565b90815260200160405180910390209080519060200190620003e99291906200045b565b506040518060400160405280600981526020017fe58aa0e5b7a5e9bdbf000000000000000000000000000000000000000000000081525060016040516200043090620007d8565b90815260200160405180910390209080519060200190620004539291906200045b565b505062000848565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200049e57805160ff1916838001178555620004cf565b82800160010185558215620004cf579182015b82811115620004ce578251825591602001919060010190620004b1565b5b509050620004de9190620004e2565b5090565b6200050791905b8082111562000503576000816000905550600101620004e9565b5090565b90565b6000815190506200051b816200082e565b92915050565b6000602082840312156200053457600080fd5b600062000544848285016200050a565b91505092915050565b60006200055c600f83620007ef565b91507fe8b083e8b4a8e783ade5a484e7908600000000000000000000000000000000006000830152600f82019050919050565b60006200059e600983620007ef565b91507fe7b297e8bda6e79b9800000000000000000000000000000000000000000000006000830152600982019050919050565b6000620005e0600983620007ef565b91507fe7b297e8bda6e69d8600000000000000000000000000000000000000000000006000830152600982019050919050565b600062000622600983620007ef565b91507fe7b297e68b8be4b8b800000000000000000000000000000000000000000000006000830152600982019050919050565b600062000664600c83620007ef565b91507fe992bbe4b8ade5bf83e5ad9400000000000000000000000000000000000000006000830152600c82019050919050565b6000620006a6600083620007ef565b9150600082019050919050565b6000620006c2600683620007ef565b91507fe7b297e6a0a100000000000000000000000000000000000000000000000000006000830152600682019050919050565b600062000704600983620007ef565b91507fe7b2bee8bda6e69d8600000000000000000000000000000000000000000000006000830152600982019050919050565b600062000744826200054d565b9150819050919050565b60006200075b826200058f565b9150819050919050565b60006200077282620005d1565b9150819050919050565b6000620007898262000613565b9150819050919050565b6000620007a08262000655565b9150819050919050565b6000620007b78262000697565b9150819050919050565b6000620007ce82620006b3565b9150819050919050565b6000620007e582620006f5565b9150819050919050565b600081905092915050565b600062000807826200080e565b9050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6200083981620007fa565b81146200084557600080fd5b50565b6115c280620008586000396000f3fe608060405234801561001057600080fd5b50600436106100cf5760003560e01c806359b579321161008c578063e70981d511610066578063e70981d5146101e3578063ebc0bb1f14610201578063f25be4f21461021f578063f2c12d211461023d576100cf565b806359b579321461018b578063b26f9f7c146101a9578063dc2d9af0146101c5576100cf565b806318562dae146100d45780632c3891cc146100f0578063334f45ec1461010e5780633b51782f1461012c5780633bc5de301461014a578063481c6a751461016d575b600080fd5b6100ee60048036038101906100e99190610fa1565b61025b565b005b6100f8610530565b60405161010591906112a0565b60405180910390f35b610116610555565b60405161012391906112bb565b60405180910390f35b61013461055b565b6040516101419190611358565b60405180910390f35b610152610561565b60405161016496959493929190611373565b60405180910390f35b610175610762565b60405161018291906112d6565b60405180910390f35b610193610800565b6040516101a09190611285565b60405180910390f35b6101c360048036038101906101be9190610ef6565b610813565b005b6101cd6109d6565b6040516101da9190611263565b60405180910390f35b6101eb610bd1565b6040516101f891906112d6565b60405180910390f35b610209610c6f565b60405161021691906112d6565b60405180910390f35b610227610d0d565b6040516102349190611358565b60405180910390f35b610245610d13565b6040516102529190611358565b60405180910390f35b600960009054906101000a900460ff16156102ab576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102a290611318565b60405180910390fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166359b579326040518163ffffffff1660e01b815260040160206040518083038186803b15801561031257600080fd5b505afa158015610326573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061034a9190610ecd565b610389576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161038090611338565b60405180910390fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b1580156103f057600080fd5b505afa158015610404573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104289190610f78565b6003819055508160068190555081600354036005819055506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663ebc0bb1f6040518163ffffffff1660e01b815260040160006040518083038186803b1580156104a757600080fd5b505afa1580156104bb573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f820116820180604052508101906104e49190610f37565b600490805190602001906104f9929190610d19565b508060079080519060200190610510929190610d19565b506001600960006101000a81548160ff0219169083151502179055505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60025481565b60035481565b60006060600080606080600354600460055460065460076008848054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561060f5780601f106105e45761010080835404028352916020019161060f565b820191906000526020600020905b8154815290600101906020018083116105f257829003601f168201915b50505050509450818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106ab5780601f10610680576101008083540402835291602001916106ab565b820191906000526020600020905b81548152906001019060200180831161068e57829003601f168201915b50505050509150808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107475780601f1061071c57610100808354040283529160200191610747565b820191906000526020600020905b81548152906001019060200180831161072a57829003601f168201915b50505050509050955095509550955095509550909192939495565b60078054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316","6002900480156107f85780601f106107cd576101008083540402835291602001916107f8565b820191906000526020600020905b8154815290600101906020018083116107db57829003601f168201915b505050505081565b600960009054906101000a900460ff1681565b60607fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff6002541461087a5760016108486109d6565b6002546008811061085557fe5b60200201516040516108679190611237565b9081526020016040518091039020610897565b60016040516108889061124e565b90815260200160405180910390205b8054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561092b5780601f106109005761010080835404028352916020019161092b565b820191906000526020600020905b81548152906001019060200180831161090e57829003601f168201915b50505050509050816040516020016109439190611237565b604051602081830303815290604052805190602001208160405160200161096a9190611237565b60405160208183030381529060405280519060200120146109c0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109b7906112f8565b60405180910390fd5b6002600081548092919060010191905055505050565b6109de610d99565b6109e6610d99565b6040518061010001604052806040518060400160405280600c81526020017fe992bbe4b8ade5bf83e5ad94000000000000000000000000000000000000000081525081526020016040518060400160405280600f81526020017fe8b083e8b4a8e783ade5a484e79086000000000000000000000000000000000081525081526020016040518060400160405280600981526020017fe7b297e68b8be4b8b8000000000000000000000000000000000000000000000081525081526020016040518060400160405280600681526020017fe7b297e6a0a1000000000000000000000000000000000000000000000000000081525081526020016040518060400160405280600981526020017fe7b297e8bda6e79b98000000000000000000000000000000000000000000000081525081526020016040518060400160405280600981526020017fe7b297e8bda6e69d86000000000000000000000000000000000000000000000081525081526020016040518060400160405280600981526020017fe7b2bee8bda6e69d86000000000000000000000000000000000000000000000081525081526020016040518060400160405280600981526020017fe58aa0e5b7a5e9bdbf000000000000000000000000000000000000000000000081525081525090508091505090565b60088054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610c675780601f10610c3c57610100808354040283529160200191610c67565b820191906000526020600020905b815481529060010190602001808311610c4a57829003601f168201915b505050505081565b60048054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610d055780601f10610cda57610100808354040283529160200191610d05565b820191906000526020600020905b815481529060010190602001808311610ce857829003601f168201915b505050505081565b60055481565b60065481565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610d5a57805160ff1916838001178555610d88565b82800160010185558215610d88579182015b82811115610d87578251825591602001919060010190610d6c565b5b509050610d959190610dc1565b5090565b6040518061010001604052806008905b6060815260200190600190039081610da95790505090565b610de391905b80821115610ddf576000816000905550600101610dc7565b5090565b90565b600081519050610df58161155e565b92915050565b600082601f830112610e0c57600080fd5b8135610e1f610e1a82611416565b6113e9565b91508082526020830160208301858383011115610e3b57600080fd5b610e4683828461150b565b50505092915050565b600082601f830112610e6057600080fd5b8151610e73610e6e82611416565b6113e9565b91508082526020830160208301858383011115610e8f57600080fd5b610e9a83828461151a565b50505092915050565b600081359050610eb281611575565b92915050565b600081519050610ec781611575565b92915050565b600060208284031215610edf57600080fd5b6000610eed84828501610de6565b91505092915050565b600060208284031215610f0857600080fd5b600082013567ffffffffffffffff811115610f2257600080fd5b610f2e84828501610dfb565b91505092915050565b600060208284031215610f4957600080fd5b600082015167ffffffffffffffff811115610f6357600080fd5b610f6f84828501610e4f565b91505092915050565b600060208284031215610f8a57600080fd5b6000610f9884828501610eb8565b91505092915050565b60008060408385031215610fb457600080fd5b6000610fc285828601610ea3565b925050602083013567ffffffffffffffff811115610fdf57600080fd5b610feb85828601610dfb565b9150509250929050565b600061100183836110ab565b905092915050565b60006110148261144c565b61101e818561146f565b93508360208202850161103085611442565b8060005b8581101561106c578484038952815161104d8582610ff5565b945061105883611462565b925060208a01995050600181019050611034565b50829750879550505050505092915050565b611087816114a7565b82525050565b611096816114e7565b82525050565b6110a5816114b3565b82525050565b60006110b682611457565b6110c0818561147a565b93506110d081856020860161151a565b6110d98161154d565b840191505092915050565b60006110ef82611457565b6110f9818561148b565b935061110981856020860161151a565b6111128161154d565b840191505092915050565b600061112882611457565b611132818561149c565b935061114281856020860161151a565b80840191505092915050565b600061115b60128361148b565b91507fe5b7a5e5ba8fe9a1bae5ba8fe99499e8afaf00000000000000000000000000006000830152602082019050919050565b600061119b600f8361148b565b91507fe695b0e68daee5b7b2e7a1aee8aea400000000000000000000000000000000006000830152602082019050919050565b60006111db60158361148b565b91507fe5898de5ba8fe8bda6e997b4e69caae5ae8ce6889000000000000000000000006000830152602082019050919050565b600061121b60008361149c565b9150600082019050919050565b611231816114dd565b82525050565b6000611243828461111d565b915081905092915050565b60006112598261120e565b9150819050919050565b6000602082019050818103600083015261127d8184611009565b905092915050565b600060208201905061129a600083018461107e565b92915050565b60006020820190506112b5600083018461108d565b92915050565b60006020820190506112d0600083018461109c565b92915050565b600060208201905081810360008301526112f081846110e4565b905092915050565b600060208201905081810360008301526113118161114e565b9050919050565b600060208201905081810360008301526113318161118e565b9050919050565b60006020820190508181036000830152611351816111ce565b9050919050565b600060208201905061136d6000830184611228565b92915050565b600060c0820190506113886000830189611228565b818103602083015261139a81886110e4565b90506113a96040830187611228565b6113b66060830186611228565b81810360808301526113c881856110e4565b905081810360a08301526113dc81846110e4565b9050979650505050505050565b6000604051905081810181811067ffffffffffffffff8211171561140c57600080fd5b8060405250919050565b600067ffffffffffffffff82111561142d57600080fd5b601f19601f8301169050602081019050919050565b6000819050919050565b600060089050919050565b600081519050919050565b6000602082019050919050565b600081905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b600081905092915050565b60008115159050919050565b6000819050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b60006114f2826114f9565b9050919050565b6000611504826114bd565b9050919050565b82818337600083830152505050565b60005b8381101561153857808201518184015260208101905061151d565b83811115611547576000848401525b50505050565b6000601f19601f8301169050919050565b611567816114a7565b811461157257600080fd5b50565b61157e816114dd565b811461158957600080fd5b5056fea2646970667358221220f2394345b046ffba05b7d1999993617f25ee31f575ba1e0d5e6025da8cea2f1964736f6c634300060a0033"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"_workshop1Addr\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"axleModelId\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"step\",\"type\":\"string\"}],\"name\":\"completeNextStep\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"currentStepIndex\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"dataConfirmed\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getData\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getProcess\",\"outputs\":[{\"internalType\":\"string[8]\",\"name\":\"\",\"type\":\"string[8]\"}],\"stateMutability\":\"pure\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"inputQty\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"manager\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"prevWorkshop\",\"outputs\":[{\"internalType\":\"contract Workshop1\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"qualified\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"_unqualified\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"_manager\",\"type\":\"string\"}],\"name\":\"setData\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"unqualified\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"workshopNo\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AXLEMODELID = "axleModelId";

    public static final String FUNC_COMPLETENEXTSTEP = "completeNextStep";

    public static final String FUNC_CURRENTSTEPINDEX = "currentStepIndex";

    public static final String FUNC_DATACONFIRMED = "dataConfirmed";

    public static final String FUNC_GETDATA = "getData";

    public static final String FUNC_GETPROCESS = "getProcess";

    public static final String FUNC_INPUTQTY = "inputQty";

    public static final String FUNC_MANAGER = "manager";

    public static final String FUNC_PREVWORKSHOP = "prevWorkshop";

    public static final String FUNC_QUALIFIED = "qualified";

    public static final String FUNC_SETDATA = "setData";

    public static final String FUNC_UNQUALIFIED = "unqualified";

    public static final String FUNC_WORKSHOPNO = "workshopNo";

    protected Workshop2(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public String axleModelId() throws ContractException {
        final Function function = new Function(FUNC_AXLEMODELID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt completeNextStep(String step) {
        final Function function = new Function(
                FUNC_COMPLETENEXTSTEP, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(step)), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] completeNextStep(String step, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_COMPLETENEXTSTEP, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(step)), 
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCompleteNextStep(String step) {
        final Function function = new Function(
                FUNC_COMPLETENEXTSTEP, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(step)), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getCompleteNextStepInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_COMPLETENEXTSTEP, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>(

                (String) results.get(0).getValue()
                );
    }

    public BigInteger currentStepIndex() throws ContractException {
        final Function function = new Function(FUNC_CURRENTSTEPINDEX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Boolean dataConfirmed() throws ContractException {
        final Function function = new Function(FUNC_DATACONFIRMED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Tuple6<BigInteger, String, BigInteger, BigInteger, String, String> getData() throws ContractException {
        final Function function = new Function(FUNC_GETDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple6<BigInteger, String, BigInteger, BigInteger, String, String>(
                (BigInteger) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (String) results.get(5).getValue());
    }

    public TransactionReceipt getProcess() {
        final Function function = new Function(
                FUNC_GETPROCESS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] getProcess(TransactionCallback callback) {
        final Function function = new Function(
                FUNC_GETPROCESS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGetProcess() {
        final Function function = new Function(
                FUNC_GETPROCESS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<List<String>> getGetProcessOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_GETPROCESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray8<Utf8String>>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<List<String>>(

                convertToNative((List<Utf8String>) results.get(0).getValue())
                );
    }

    public BigInteger inputQty() throws ContractException {
        final Function function = new Function(FUNC_INPUTQTY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public String manager() throws ContractException {
        final Function function = new Function(FUNC_MANAGER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String prevWorkshop() throws ContractException {
        final Function function = new Function(FUNC_PREVWORKSHOP, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public BigInteger qualified() throws ContractException {
        final Function function = new Function(FUNC_QUALIFIED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public TransactionReceipt setData(BigInteger _unqualified, String _manager) {
        final Function function = new Function(
                FUNC_SETDATA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(_unqualified), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_manager)), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] setData(BigInteger _unqualified, String _manager, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETDATA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(_unqualified), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_manager)), 
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetData(BigInteger _unqualified, String _manager) {
        final Function function = new Function(
                FUNC_SETDATA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(_unqualified), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_manager)), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<BigInteger, String> getSetDataInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, String>(

                (BigInteger) results.get(0).getValue(), 
                (String) results.get(1).getValue()
                );
    }

    public BigInteger unqualified() throws ContractException {
        final Function function = new Function(FUNC_UNQUALIFIED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public String workshopNo() throws ContractException {
        final Function function = new Function(FUNC_WORKSHOPNO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public static Workshop2 load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new Workshop2(contractAddress, client, credential);
    }

    public static Workshop2 deploy(Client client, CryptoKeyPair credential, String _workshop1Addr) throws ContractException {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(_workshop1Addr)));
        return deploy(Workshop2.class, client, credential, getBinary(client.getCryptoSuite()), encodedConstructor);
    }
}
