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
import org.fisco.bcos.sdk.abi.datatypes.generated.StaticArray1;
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
public class Workshop4 extends Contract {
    public static final String[] BINARY_ARRAY = {"60806040526040518060400160405280600c81526020017fe8b4a8e6a380e8bda6e997b400000000000000000000000000000000000000008152506006908051906020019062000051929190620000e7565b506000600760006101000a81548160ff0219169083151502179055503480156200007a57600080fd5b50604051620012da380380620012da8339818101604052810190620000a09190620001ad565b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505062000227565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200012a57805160ff19168380011785556200015b565b828001600101855582156200015b579182015b828111156200015a5782518255916020019190600101906200013d565b5b5090506200016a91906200016e565b5090565b6200019391905b808211156200018f57600081600090555060010162000175565b5090565b90565b600081519050620001a7816200020d565b92915050565b600060208284031215620001c057600080fd5b6000620001d08482850162000196565b91505092915050565b6000620001e682620001ed565b9050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6200021881620001d9565b81146200022457600080fd5b50565b6110a380620002376000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806359b579321161007157806359b5793214610147578063dc2d9af014610165578063e70981d514610183578063ebc0bb1f146101a1578063f25be4f2146101bf578063f2c12d21146101dd576100a9565b806318562dae146100ae5780632c3891cc146100ca5780633b51782f146100e85780633bc5de3014610106578063481c6a7514610129575b600080fd5b6100c860048036038101906100c39190610b98565b6101fb565b005b6100d26104d0565b6040516100df9190610dd1565b60405180910390f35b6100f06104f5565b6040516100fd9190610e4e565b60405180910390f35b61010e6104fb565b60405161012096959493929190610e69565b60405180910390f35b6101316106fc565b60405161013e9190610dec565b60405180910390f35b61014f61079a565b60405161015c9190610db6565b60405180910390f35b61016d6107ad565b60405161017a9190610d94565b60405180910390f35b61018b61080a565b6040516101989190610dec565b60405180910390f35b6101a96108a8565b6040516101b69190610dec565b60405180910390f35b6101c7610946565b6040516101d49190610e4e565b60405180910390f35b6101e561094c565b6040516101f29190610e4e565b60405180910390f35b600760009054906101000a900460ff161561024b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161024290610e0e565b60405180910390fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166359b579326040518163ffffffff1660e01b815260040160206040518083038186803b1580156102b257600080fd5b505afa1580156102c6573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906102ea9190610b05565b610329576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161032090610e2e565b60405180910390fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b15801561039057600080fd5b505afa1580156103a4573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103c89190610b6f565b6001819055508160048190555081600154036003819055506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663ebc0bb1f6040518163ffffffff1660e01b815260040160006040518083038186803b15801561044757600080fd5b505afa15801561045b573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f820116820180604052508101906104849190610b2e565b60029080519060200190610499929190610952565b5080600590805190602001906104b0929190610952565b506001600760006101000a81548160ff0219169083151502179055505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60015481565b60006060600080606080600154600260035460045460056006848054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105a95780601f1061057e576101008083540402835291602001916105a9565b820191906000526020600020905b81548152906001019060200180831161058c57829003601f168201915b50505050509450818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106455780601f1061061a57610100808354040283529160200191610645565b820191906000526020600020905b81548152906001019060200180831161062857829003601f168201915b50505050509150808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106e15780601f106106b6576101008083540402835291602001916106e1565b820191906000526020600020905b8154815290600101906020018083116106c457829003601f168201915b50505050509050955095509550955095509550909192939495565b60058054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107925780601f1061076757610100808354040283529160200191610792565b820191906000526020600020905b81548152906001019060200180831161077557829003601f168201915b505050505081565b600760009054906101000a900460ff1681565b6107b56109d2565b6107bd6109d2565b60405180602001604052806040518060400160405280600f81526020017fe8b685e5a3b0e6b3a2e68ea2e4bca4000000000000000000000000000000000081525081525090508091505090565b60068054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108a05780601f10610875576101008083540402835291602001916108a0565b820191906000526020600020905b81548152906001019060200180831161088357829003601f168201915b505050505081565b60028054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561093e5780601f106109135761010080835404028352916020019161093e565b820191906000526020600020905b81548152906001019060200180831161092157829003601f168201915b505050505081565b60035481565b60045481565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061099357805160ff19168380011785556109c1565b828001600101855582156109c1579182015b828111156109c05782518255916020019190600101906109a5565b5b5090506109ce91906109f9565b5090565b60405180602001604052806001905b60608152602001906001900390816109e15790505090565b610a1b91905b80821115610a175760008160009055506001016109ff565b5090565b90565b600081519050610a2d8161103f565b92915050565b600082601f830112610a4457600080fd5b8135610a57610a5282610f0c565b610edf565b91508082526020830160208301858383011115610a7357600080fd5b610a7e838284610fec565b50505092915050565b600082601f830112610a9857600080fd5b8151610aab610aa682610f0c565b610edf565b91508082526020830160208301858383011115610ac757600080fd5b610ad2838284610ffb565b50505092915050565b600081359050610aea81611056565b92915050565b600081519050610aff81611056565b92915050565b600060208284031215610b1757600080fd5b6000610b2584828501610a1e565b91505092915050565b600060208284031215610b4057600080fd5b600082015167ffffffffffffffff811115610b5a57600080fd5b610b6684828501610a87565b91505092915050565b600060208284031215610b8157600080fd5b6000610b8f84828501610af0565b91505092915050565b60008060408385031215610bab57600080fd5b6000610bb985828601610adb565b925050602083013567ffffffffffffffff811115610bd657600080fd5b610be285828601610a33565b9150509250929050565b6000610bf88383610c93565b905092915050565b6000610c0b82610f42565b610c158185610f65565b935083602082028501610c2785610f38565b8060005b85811015610c635784840389528151610c448582610bec565b9450610c4f83610f58565b925060208a01995050600181019050610c2b565b50829750879550505050505092915050565b610c7e81610f92565b82525050565b610c8d81610fc8565b82525050565b6000610c9e82610f4d565b610ca88185610f70565b9350610cb8818560208601610ffb565b610cc18161102e565b840191505092915050565b6000610cd782610f4d565b610ce18185610f81565b9350610cf1818560208601610ffb565b610cfa8161102e565b840191505092915050565b6000610d12600f83610f81565b91507fe695b0e68daee5b7b2e7a1aee8aea400000000000000000000000000000000006000830152602082019050919050565b6000610d52600f83610f81565b91507fe5898de5ba8fe69caae5ae8ce6889000000000000000000000000000000000006000830152602082019050919050565b610d8e81610fbe565b82525050565b60006020820190508181036000830152610dae8184610c00565b905092915050565b6000602082019050610dcb6000830184610c","75565b92915050565b6000602082019050610de66000830184610c84565b92915050565b60006020820190508181036000830152610e068184610ccc565b905092915050565b60006020820190508181036000830152610e2781610d05565b9050919050565b60006020820190508181036000830152610e4781610d45565b9050919050565b6000602082019050610e636000830184610d85565b92915050565b600060c082019050610e7e6000830189610d85565b8181036020830152610e908188610ccc565b9050610e9f6040830187610d85565b610eac6060830186610d85565b8181036080830152610ebe8185610ccc565b905081810360a0830152610ed28184610ccc565b9050979650505050505050565b6000604051905081810181811067ffffffffffffffff82111715610f0257600080fd5b8060405250919050565b600067ffffffffffffffff821115610f2357600080fd5b601f19601f8301169050602081019050919050565b6000819050919050565b600060019050919050565b600081519050919050565b6000602082019050919050565b600081905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b60008115159050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b6000610fd382610fda565b9050919050565b6000610fe582610f9e565b9050919050565b82818337600083830152505050565b60005b83811015611019578082015181840152602081019050610ffe565b83811115611028576000848401525b50505050565b6000601f19601f8301169050919050565b61104881610f92565b811461105357600080fd5b50565b61105f81610fbe565b811461106a57600080fd5b5056fea2646970667358221220673195fa7d5fb0229106898a8ed5f06e930517f00697dfa9dac5f4e4c0a5c80264736f6c634300060a0033"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"_workshop3Addr\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"axleModelId\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"dataConfirmed\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getData\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getProcess\",\"outputs\":[{\"internalType\":\"string[1]\",\"name\":\"\",\"type\":\"string[1]\"}],\"stateMutability\":\"pure\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"inputQty\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"manager\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"prevWorkshop\",\"outputs\":[{\"internalType\":\"contract Workshop3\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"qualified\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"_unqualified\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"_manager\",\"type\":\"string\"}],\"name\":\"setData\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"unqualified\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"workshopNo\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AXLEMODELID = "axleModelId";

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

    protected Workshop4(String contractAddress, Client client, CryptoKeyPair credential) {
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
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray1<Utf8String>>() {}));
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

    public static Workshop4 load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new Workshop4(contractAddress, client, credential);
    }

    public static Workshop4 deploy(Client client, CryptoKeyPair credential, String _workshop3Addr) throws ContractException {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(_workshop3Addr)));
        return deploy(Workshop4.class, client, credential, getBinary(client.getCryptoSuite()), encodedConstructor);
    }
}
