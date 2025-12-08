import java.math.BigInteger;
import java.util.Arrays;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class DataCenter extends Contract {
    public static final String[] BINARY_ARRAY = {"60806040523480156200001157600080fd5b5060405162001c0038038062001c008339818101604052810190620000379190620001df565b856000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555084600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555083600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555082600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050505050620002c3565b600081519050620001d981620002a9565b92915050565b60008060008060008060c08789031215620001f957600080fd5b60006200020989828a01620001c8565b96505060206200021c89828a01620001c8565b95505060406200022f89828a01620001c8565b94505060606200024289828a01620001c8565b93505060806200025589828a01620001c8565b92505060a06200026889828a01620001c8565b9150509295509295509295565b6000620002828262000289565b9050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b620002b48162000275565b8114620002c057600080fd5b50565b61192d80620002d36000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80634816edcd1161005b5780634816edcd146100dc5780637cade0cb146100fa578063d377df9314610118578063e0a890c7146101365761007d565b80632d10fa2814610082578063341b51c1146100a057806345bc9d38146100be575b600080fd5b61008a610154565b60405161009791906116e0565b60405180910390f35b6100a86110d7565b6040516100b591906116c5565b60405180910390f35b6100c66110fd565b6040516100d3919061163e565b60405180910390f35b6100e4611122565b6040516100f19190611674565b60405180910390f35b610102611148565b60405161010f9190611659565b60405180910390f35b61012061116e565b60405161012d91906116aa565b60405180910390f35b61013e611194565b60405161014b919061168f565b60405180910390f35b61015c6111ba565b604051806102e001604052806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16632d2ec9606040518163ffffffff1660e01b815260040160006040518083038186803b1580156101cf57600080fd5b505afa1580156101e3573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f8201168201806040525081019061020c919061130f565b81526020016000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166399504ed16040518163ffffffff1660e01b815260040160006040518083038186803b15801561027857600080fd5b505afa15801561028c573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f820116820180604052508101906102b5919061130f565b81526020016000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663b643dde36040518163ffffffff1660e01b815260040160206040518083038186803b15801561032157600080fd5b505afa158015610335573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103599190611350565b81526020016000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16634972134a6040518163ffffffff1660e01b815260040160006040518083038186803b1580156103c557600080fd5b505afa1580156103d9573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f82011682018060405250810190610402919061130f565b81526020016000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663b643dde36040518163ffffffff1660e01b815260040160206040518083038186803b15801561046e57600080fd5b505afa158015610482573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104a69190611350565b8152602001600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b15801561051357600080fd5b505afa158015610527573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061054b9190611350565b8152602001600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f2c12d216040518163ffffffff1660e01b815260040160206040518083038186803b1580156105b857600080fd5b505afa1580156105cc573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906105f09190611350565b8152602001600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663481c6a756040518163ffffffff1660e01b815260040160006040518083038186803b15801561065d57600080fd5b505afa158015610671573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f8201168201806040525081019061069a919061130f565b8152602001600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b15801561070757600080fd5b505afa15801561071b573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061073f9190611350565b8152602001600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b1580156107ac57600080fd5b505afa1580156107c0573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906107e49190611350565b8152602001600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f2c12d216040518163ffffffff1660e01b815260040160206040518083038186803b15801561085157600080fd5b505afa158015610865573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906108899190611350565b8152602001600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663481c6a756040518163ffffffff1660e01b815260040160006040518083038186803b1580156108f657600080fd5b505afa15801561090a573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f82011682018060405250810190610933919061130f565b8152602001600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b1580156109a057600080fd5b505afa1580156109b4573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906109d89190611350565b8152602001600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b158015610a4557600080fd5b505afa158015610a59573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610a7d9190611350565b8152602001600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f2c12d216040518163ffffffff1660e01b815260040160206040518083038186803b158015610aea57600080fd5b505afa158015610afe573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610b229190611350565b8152602001600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663481c6a756040518163ffffffff1660e01b815260040160006040518083038186803b158015610b8f57600080fd5b505afa158015610ba3573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f82011682018060405250810190610bcc919061130f565b8152602001600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b158015610c3957600080fd5b505afa158015610c4d573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610c719190611350565b8152602001600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b158015610cde57600080fd5b505afa158015610cf2573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610d169190611350565b8152602001600460009054906101000a900473ffffff","ffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f2c12d216040518163ffffffff1660e01b815260040160206040518083038186803b158015610d8357600080fd5b505afa158015610d97573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610dbb9190611350565b8152602001600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663481c6a756040518163ffffffff1660e01b815260040160006040518083038186803b158015610e2857600080fd5b505afa158015610e3c573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f82011682018060405250810190610e65919061130f565b8152602001600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f25be4f26040518163ffffffff1660e01b815260040160206040518083038186803b158015610ed257600080fd5b505afa158015610ee6573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610f0a9190611350565b8152602001600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663481c6a756040518163ffffffff1660e01b815260040160006040518083038186803b158015610f7757600080fd5b505afa158015610f8b573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f82011682018060405250810190610fb4919061130f565b8152602001600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166359b579326040518163ffffffff1660e01b815260040160206040518083038186803b15801561102157600080fd5b505afa158015611035573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061105991906112e6565b611098576040518060400160405280600981526020017fe69caae5ae8ce6889000000000000000000000000000000000000000000000008152506110cf565b6040518060400160405280600981526020017fe5b7b2e587bae5ba9300000000000000000000000000000000000000000000008152505b815250905090565b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b604051806102e0016040528060608152602001606081526020016000815260200160608152602001600081526020016000815260200160008152602001606081526020016000815260200160008152602001600081526020016060815260200160008152602001600081526020016000815260200160608152602001600081526020016000815260200160008152602001606081526020016000815260200160608152602001606081525090565b600081519050611277816118c9565b92915050565b600082601f83011261128e57600080fd5b81516112a161129c8261172f565b611702565b915080825260208301602083018583830111156112bd57600080fd5b6112c8838284611885565b50505092915050565b6000815190506112e0816118e0565b92915050565b6000602082840312156112f857600080fd5b600061130684828501611268565b91505092915050565b60006020828403121561132157600080fd5b600082015167ffffffffffffffff81111561133b57600080fd5b6113478482850161127d565b91505092915050565b60006020828403121561136257600080fd5b6000611370848285016112d1565b91505092915050565b611382816117ad565b82525050565b611391816117d1565b82525050565b6113a0816117f5565b82525050565b6113af81611819565b82525050565b6113be8161183d565b82525050565b6113cd81611861565b82525050565b60006113de8261175b565b6113e88185611766565b93506113f8818560208601611885565b611401816118b8565b840191505092915050565b60006102e083016000830151848203600086015261142a82826113d3565b9150506020830151848203602086015261144482826113d3565b9150506040830151611459604086018261162f565b506060830151848203606086015261147182826113d3565b9150506080830151611486608086018261162f565b5060a083015161149960a086018261162f565b5060c08301516114ac60c086018261162f565b5060e083015184820360e08601526114c482826113d3565b9150506101008301516114db61010086018261162f565b506101208301516114f061012086018261162f565b5061014083015161150561014086018261162f565b5061016083015184820361016086015261151f82826113d3565b91505061018083015161153661018086018261162f565b506101a083015161154b6101a086018261162f565b506101c08301516115606101c086018261162f565b506101e08301518482036101e086015261157a82826113d3565b91505061020083015161159161020086018261162f565b506102208301516115a661022086018261162f565b506102408301516115bb61024086018261162f565b506102608301518482036102608601526115d582826113d3565b9150506102808301516115ec61028086018261162f565b506102a08301518482036102a086015261160682826113d3565b9150506102c08301518482036102c086015261162282826113d3565b9150508091505092915050565b611638816117a3565b82525050565b60006020820190506116536000830184611379565b92915050565b600060208201905061166e6000830184611388565b92915050565b60006020820190506116896000830184611397565b92915050565b60006020820190506116a460008301846113a6565b92915050565b60006020820190506116bf60008301846113b5565b92915050565b60006020820190506116da60008301846113c4565b92915050565b600060208201905081810360008301526116fa818461140c565b905092915050565b6000604051905081810181811067ffffffffffffffff8211171561172557600080fd5b8060405250919050565b600067ffffffffffffffff82111561174657600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b600082825260208201905092915050565b60008115159050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b60006117b8826117bf565b9050919050565b60006117ca82611783565b9050919050565b60006117dc826117e3565b9050919050565b60006117ee82611783565b9050919050565b600061180082611807565b9050919050565b600061181282611783565b9050919050565b60006118248261182b565b9050919050565b600061183682611783565b9050919050565b60006118488261184f565b9050919050565b600061185a82611783565b9050919050565b600061186c82611873565b9050919050565b600061187e82611783565b9050919050565b60005b838110156118a3578082015181840152602081019050611888565b838111156118b2576000848401525b50505050565b6000601f19601f8301169050919050565b6118d281611777565b81146118dd57600080fd5b50565b6118e9816117a3565b81146118f457600080fd5b5056fea2646970667358221220b37bbc5e53bf913d847e16c7cb81662a34c5591d82ac110a7601cd6396397cc964736f6c634300060a0033"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"_raw\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"_ws1\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"_ws2\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"_ws3\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"_ws4\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"_ws5\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"getAllData\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"rawProducerAddr\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"axleProducerAddr\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"totalRawWeight\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"inputQty1\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"qualified1\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"unqualified1\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"manager1\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"inputQty2\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"qualified2\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"unqualified2\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"manager2\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"inputQty3\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"qualified3\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"unqualified3\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"manager3\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"inputQty4\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"qualified4\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"unqualified4\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"manager4\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"inputQty5\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"manager5\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"status\",\"type\":\"string\"}],\"internalType\":\"struct DataCenter.FullProcessData\",\"name\":\"\",\"type\":\"tuple\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"rawMat\",\"outputs\":[{\"internalType\":\"contract RawMaterial\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"ws1\",\"outputs\":[{\"internalType\":\"contract Workshop1\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"ws2\",\"outputs\":[{\"internalType\":\"contract Workshop2\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"ws3\",\"outputs\":[{\"internalType\":\"contract Workshop3\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"ws4\",\"outputs\":[{\"internalType\":\"contract Workshop4\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"ws5\",\"outputs\":[{\"internalType\":\"contract Workshop5\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GETALLDATA = "getAllData";

    public static final String FUNC_RAWMAT = "rawMat";

    public static final String FUNC_WS1 = "ws1";

    public static final String FUNC_WS2 = "ws2";

    public static final String FUNC_WS3 = "ws3";

    public static final String FUNC_WS4 = "ws4";

    public static final String FUNC_WS5 = "ws5";

    protected DataCenter(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public DataCenter.FullProcessData getAllData() throws ContractException {
        final Function function = new Function(FUNC_GETALLDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DataCenter.FullProcessData>() {}));
        return executeCallWithSingleValueReturn(function, DataCenter.FullProcessData.class);
    }

    public String rawMat() throws ContractException {
        final Function function = new Function(FUNC_RAWMAT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String ws1() throws ContractException {
        final Function function = new Function(FUNC_WS1, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String ws2() throws ContractException {
        final Function function = new Function(FUNC_WS2, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String ws3() throws ContractException {
        final Function function = new Function(FUNC_WS3, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String ws4() throws ContractException {
        final Function function = new Function(FUNC_WS4, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String ws5() throws ContractException {
        final Function function = new Function(FUNC_WS5, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public static DataCenter load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new DataCenter(contractAddress, client, credential);
    }

    public static DataCenter deploy(Client client, CryptoKeyPair credential, String _raw, String _ws1, String _ws2, String _ws3, String _ws4, String _ws5) throws ContractException {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(_raw), 
                new org.fisco.bcos.sdk.abi.datatypes.Address(_ws1), 
                new org.fisco.bcos.sdk.abi.datatypes.Address(_ws2), 
                new org.fisco.bcos.sdk.abi.datatypes.Address(_ws3), 
                new org.fisco.bcos.sdk.abi.datatypes.Address(_ws4), 
                new org.fisco.bcos.sdk.abi.datatypes.Address(_ws5)));
        return deploy(DataCenter.class, client, credential, getBinary(client.getCryptoSuite()), encodedConstructor);
    }

    public static class DataCenter.FullProcessData extends DynamicStruct {
        public String rawProducerAddr;

        public String axleProducerAddr;

        public BigInteger totalRawWeight;

        public String batchId;

        public BigInteger inputQty1;

        public BigInteger qualified1;

        public BigInteger unqualified1;

        public String manager1;

        public BigInteger inputQty2;

        public BigInteger qualified2;

        public BigInteger unqualified2;

        public String manager2;

        public BigInteger inputQty3;

        public BigInteger qualified3;

        public BigInteger unqualified3;

        public String manager3;

        public BigInteger inputQty4;

        public BigInteger qualified4;

        public BigInteger unqualified4;

        public String manager4;

        public BigInteger inputQty5;

        public String manager5;

        public String status;

        public DataCenter.FullProcessData(Utf8String rawProducerAddr, Utf8String axleProducerAddr, Uint256 totalRawWeight, Utf8String batchId, Uint256 inputQty1, Uint256 qualified1, Uint256 unqualified1, Utf8String manager1, Uint256 inputQty2, Uint256 qualified2, Uint256 unqualified2, Utf8String manager2, Uint256 inputQty3, Uint256 qualified3, Uint256 unqualified3, Utf8String manager3, Uint256 inputQty4, Uint256 qualified4, Uint256 unqualified4, Utf8String manager4, Uint256 inputQty5, Utf8String manager5, Utf8String status) {
            super(rawProducerAddr,axleProducerAddr,totalRawWeight,batchId,inputQty1,qualified1,unqualified1,manager1,inputQty2,qualified2,unqualified2,manager2,inputQty3,qualified3,unqualified3,manager3,inputQty4,qualified4,unqualified4,manager4,inputQty5,manager5,status);
            this.rawProducerAddr = rawProducerAddr.getValue();
            this.axleProducerAddr = axleProducerAddr.getValue();
            this.totalRawWeight = totalRawWeight.getValue();
            this.batchId = batchId.getValue();
            this.inputQty1 = inputQty1.getValue();
            this.qualified1 = qualified1.getValue();
            this.unqualified1 = unqualified1.getValue();
            this.manager1 = manager1.getValue();
            this.inputQty2 = inputQty2.getValue();
            this.qualified2 = qualified2.getValue();
            this.unqualified2 = unqualified2.getValue();
            this.manager2 = manager2.getValue();
            this.inputQty3 = inputQty3.getValue();
            this.qualified3 = qualified3.getValue();
            this.unqualified3 = unqualified3.getValue();
            this.manager3 = manager3.getValue();
            this.inputQty4 = inputQty4.getValue();
            this.qualified4 = qualified4.getValue();
            this.unqualified4 = unqualified4.getValue();
            this.manager4 = manager4.getValue();
            this.inputQty5 = inputQty5.getValue();
            this.manager5 = manager5.getValue();
            this.status = status.getValue();
        }

        public DataCenter.FullProcessData(String rawProducerAddr, String axleProducerAddr, BigInteger totalRawWeight, String batchId, BigInteger inputQty1, BigInteger qualified1, BigInteger unqualified1, String manager1, BigInteger inputQty2, BigInteger qualified2, BigInteger unqualified2, String manager2, BigInteger inputQty3, BigInteger qualified3, BigInteger unqualified3, String manager3, BigInteger inputQty4, BigInteger qualified4, BigInteger unqualified4, String manager4, BigInteger inputQty5, String manager5, String status) {
            super(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(rawProducerAddr),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(axleProducerAddr),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(totalRawWeight),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(batchId),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(inputQty1),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(qualified1),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(unqualified1),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manager1),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(inputQty2),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(qualified2),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(unqualified2),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manager2),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(inputQty3),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(qualified3),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(unqualified3),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manager3),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(inputQty4),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(qualified4),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(unqualified4),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manager4),new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(inputQty5),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manager5),new org.fisco.bcos.sdk.abi.datatypes.Utf8String(status));
            this.rawProducerAddr = rawProducerAddr;
            this.axleProducerAddr = axleProducerAddr;
            this.totalRawWeight = totalRawWeight;
            this.batchId = batchId;
            this.inputQty1 = inputQty1;
            this.qualified1 = qualified1;
            this.unqualified1 = unqualified1;
            this.manager1 = manager1;
            this.inputQty2 = inputQty2;
            this.qualified2 = qualified2;
            this.unqualified2 = unqualified2;
            this.manager2 = manager2;
            this.inputQty3 = inputQty3;
            this.qualified3 = qualified3;
            this.unqualified3 = unqualified3;
            this.manager3 = manager3;
            this.inputQty4 = inputQty4;
            this.qualified4 = qualified4;
            this.unqualified4 = unqualified4;
            this.manager4 = manager4;
            this.inputQty5 = inputQty5;
            this.manager5 = manager5;
            this.status = status;
        }
    }
}
