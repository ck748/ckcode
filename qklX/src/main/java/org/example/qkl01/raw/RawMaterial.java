import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Bool;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple6;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class RawMaterial extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50612109806100206000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c8063b6b0cfd411610066578063b6b0cfd414610173578063bce06947146101a9578063c889871a146101c7578063d54d5b92146101f7578063e79039881461022e5761009e565b8063031213b3146100a357806305e81448146100d35780635077c8a314610103578063536c32db1461011f578063a8fabfa514610155575b600080fd5b6100bd60048036038101906100b89190611707565b61025e565b6040516100ca9190611c09565b60405180910390f35b6100ed60048036038101906100e89190611707565b61029c565b6040516100fa9190611c24565b60405180910390f35b61011d60048036038101906101189190611748565b61035c565b005b61013960048036038101906101349190611707565b610648565b60405161014c9796959493929190611cd1565b60405180910390f35b61015d610a6e565b60405161016a9190611f6e565b60405180910390f35b61018d6004803603810190610188919061189d565b610a7b565b6040516101a09796959493929190611c46565b60405180910390f35b6101b1610e9c565b6040516101be9190611be7565b60405180910390f35b6101e160048036038101906101dc9190611849565b610f85565b6040516101ee9190611c09565b60405180910390f35b610211600480360381019061020c9190611707565b6111de565b604051610225989796959493929190611d5c565b60405180910390f35b61024860048036038101906102439190611707565b61157f565b6040516102559190611c09565b60405180910390f35b6000806001836040516102719190611bd0565b9081526020016040518091039020805460018160011615610100020316600290049050119050919050565b60606001826040516102ae9190611bd0565b90815260200160405180910390208054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103505780601f1061032557610100808354040283529160200191610350565b820191906000526020600020905b81548152906001019060200180831161033357829003601f168201915b50505050509050919050565b6000821161039f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161039690611e8e565b60405180910390fd5b60008651116103e3576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103da90611f2e565b60405180910390fd5b6003866040516103f39190611bd0565b908152602001604051809103902060009054906101000a900460ff161561044f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161044690611ece565b60405180910390fd5b604051806101000160405280868152602001858152602001848152602001838152602001828152602001600081526020018381526020014281525060008760405161049a9190611bd0565b908152602001604051809103902060008201518160000190805190602001906104c49291906115b4565b5060208201518160010190805190602001906104e19291906115b4565b5060408201518160020190805190602001906104fe9291906115b4565b506060820151816003015560808201518160040190805190602001906105259291906115b4565b5060a0820151816005015560c0820151816006015560e08201518160070155905050856001846040516105589190611bd0565b908152602001604051809103902090805190602001906105799291906115b4565b506002869080600181540180825580915050600190039060005260206000200160009091909190915090805190602001906105b59291906115b4565b5060016003876040516105c89190611bd0565b908152602001604051809103902060006101000a81548160ff021916908315150217905550856040516105fb9190611bd0565b60405180910390207f0c512220d6b37e8ba2f17de51e612698e67a2d3e2f132ff0751c35df90ac2749868585856040516106389493929190611df6565b60405180910390a2505050505050565b6060806060600060606000806001886040516106649190611bd0565b90815260200160405180910390208054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107065780601f106106db57610100808354040283529160200191610706565b820191906000526020600020905b8154815290600101906020018083116106e957829003601f168201915b505050505096506000875111610751576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161074890611f0e565b60405180910390fd5b610759611634565b6000886040516107699190611bd0565b908152602001604051809103902060405180610100016040529081600082018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561081c5780601f106107f15761010080835404028352916020019161081c565b820191906000526020600020905b8154815290600101906020018083116107ff57829003601f168201915b50505050508152602001600182018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108be5780601f10610893576101008083540402835291602001916108be565b820191906000526020600020905b8154815290600101906020018083116108a157829003601f168201915b50505050508152602001600282018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109605780601f1061093557610100808354040283529160200191610960565b820191906000526020600020905b81548152906001019060200180831161094357829003601f168201915b5050505050815260200160038201548152602001600482018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a0c5780601f106109e157610100808354040283529160200191610a0c565b820191906000526020600020905b8154815290600101906020018083116109ef57829003601f168201915b50505050508152602001600582015481526020016006820154815260200160078201548152505090508781600001518260200151836060015184608001518560a001518660c00151975097509750975097509750975050919395979092949650565b6000600280549050905090565b60608060608060008060006002805490508810610acd576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ac490611f4e565b60405180910390fd5b60028881548110610ada57fe5b906000526020600020018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610b785780601f10610b4d57610100808354040283529160200191610b78565b820191906000526020600020905b815481529060010190602001808311610b5b57829003601f168201915b50505050509650610b87611634565b600088604051610b979190611bd0565b908152602001604051809103902060405180610100016040529081600082018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610c4a5780601f10610c1f57610100808354040283529160200191610c4a565b820191906000526020600020905b815481529060010190602001808311610c2d57829003601f168201915b50505050508152602001600182018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610cec5780601f10610cc157610100808354040283529160200191610cec565b820191906000526020600020905b815481529060010190602001808311610ccf57829003601f168201915b50505050508152602001600282018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610d8e5780601f10610d6357610100808354040283529160200191610d8e565b820191906000526020600020905b815481529060010190602001808311610d7157829003601f168201915b5050505050815260200160038201548152602001600482018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610e3a5780601f10610e0f57610100808354040283529160200191610e3a565b820191906000526020600020905b815481529060010190602001808311610e1d57829003601f168201915b50505050508152602001600582015481526020016006820154815260200160078201548152505090508781600001518260200151836040015184606001518560a001518660c00151975097509750975097509750975050919395979092949650565b60606002805480602002602001604051908101604052809291908181526020016000905b82821015610f7c578382906000526020600020018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610f685780601f10610f3d57610100808354040283529160200191610f68565b820191906000526020600020905b815481529060010190602001808311610f4b57829003601f168201915b505050505081526020019060010190610ec0565b50505050905090565b60006060600184604051610f999190611bd0565b90815260200160405180910390208054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001","816001161561010002031660029004801561103b5780601f106110105761010080835404028352916020019161103b565b820191906000526020600020905b81548152906001019060200180831161101e57829003601f168201915b505050505090506000815111611086576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161107d90611f0e565b60405180910390fd5b6003816040516110969190611bd0565b908152602001604051809103902060009054906101000a900460ff166110f1576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016110e890611eee565b60405180910390fd5b600080826040516111029190611bd0565b908152602001604051809103902090508381600601541015611159576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161115090611eae565b60405180910390fd5b8381600501600082825401925050819055508381600601600082825403925050819055508160405161118b9190611bd0565b60405180910390207f9a8cdc6ae455ad6bb60b56c66a223cd395aeda8a3b5c16f5e637cedec8569eac868684600601546040516111ca93929190611e50565b60405180910390a260019250505092915050565b60608060606000606060008060006003896040516111fc9190611bd0565b908152602001604051809103902060009054906101000a900460ff16611257576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161124e90611eee565b60405180910390fd5b61125f611634565b60008a60405161126f9190611bd0565b908152602001604051809103902060405180610100016040529081600082018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156113225780601f106112f757610100808354040283529160200191611322565b820191906000526020600020905b81548152906001019060200180831161130557829003601f168201915b50505050508152602001600182018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156113c45780601f10611399576101008083540402835291602001916113c4565b820191906000526020600020905b8154815290600101906020018083116113a757829003601f168201915b50505050508152602001600282018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156114665780601f1061143b57610100808354040283529160200191611466565b820191906000526020600020905b81548152906001019060200180831161144957829003601f168201915b5050505050815260200160038201548152602001600482018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156115125780601f106114e757610100808354040283529160200191611512565b820191906000526020600020905b8154815290600101906020018083116114f557829003601f168201915b5050505050815260200160058201548152602001600682015481526020016007820154815250509050806000015181602001518260400151836060015184608001518560a001518660c001518760e001519850985098509850985098509850985050919395975091939597565b60006003826040516115919190611bd0565b908152602001604051809103902060009054906101000a900460ff169050919050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106115f557805160ff1916838001178555611623565b82800160010185558215611623579182015b82811115611622578251825591602001919060010190611607565b5b5090506116309190611679565b5090565b60405180610100016040528060608152602001606081526020016060815260200160008152602001606081526020016000815260200160008152602001600081525090565b61169b91905b8082111561169757600081600090555060010161167f565b5090565b90565b600082601f8301126116af57600080fd5b81356116c26116bd82611fb6565b611f89565b915080825260208301602083018583830111156116de57600080fd5b6116e9838284612069565b50505092915050565b600081359050611701816120bc565b92915050565b60006020828403121561171957600080fd5b600082013567ffffffffffffffff81111561173357600080fd5b61173f8482850161169e565b91505092915050565b60008060008060008060c0878903121561176157600080fd5b600087013567ffffffffffffffff81111561177b57600080fd5b61178789828a0161169e565b965050602087013567ffffffffffffffff8111156117a457600080fd5b6117b089828a0161169e565b955050604087013567ffffffffffffffff8111156117cd57600080fd5b6117d989828a0161169e565b945050606087013567ffffffffffffffff8111156117f657600080fd5b61180289828a0161169e565b935050608061181389828a016116f2565b92505060a087013567ffffffffffffffff81111561183057600080fd5b61183c89828a0161169e565b9150509295509295509295565b6000806040838503121561185c57600080fd5b600083013567ffffffffffffffff81111561187657600080fd5b6118828582860161169e565b9250506020611893858286016116f2565b9150509250929050565b6000602082840312156118af57600080fd5b60006118bd848285016116f2565b91505092915050565b60006118d2838361195e565b905092915050565b60006118e582611ff2565b6118ef8185612015565b93508360208202850161190185611fe2565b8060005b8581101561193d578484038952815161191e85826118c6565b945061192983612008565b925060208a01995050600181019050611905565b50829750879550505050505092915050565b61195881612053565b82525050565b600061196982611ffd565b6119738185612026565b9350611983818560208601612078565b61198c816120ab565b840191505092915050565b60006119a282611ffd565b6119ac8185612037565b93506119bc818560208601612078565b6119c5816120ab565b840191505092915050565b60006119db82611ffd565b6119e58185612048565b93506119f5818560208601612078565b80840191505092915050565b6000611a0e601f83612037565b91507f5175616e74697479206d7573742062652067726561746572207468616e2030006000830152602082019050919050565b6000611a4e601583612037565b91507f496e73756666696369656e74206d6174657269616c00000000000000000000006000830152602082019050919050565b6000611a8e601483612037565b91507f426174636820616c7265616479206578697374730000000000000000000000006000830152602082019050919050565b6000611ace601483612037565b91507f426174636820646f6573206e6f742065786973740000000000000000000000006000830152602082019050919050565b6000611b0e601983612037565b91507f534e206e6f7420666f756e6420696e20616e79206261746368000000000000006000830152602082019050919050565b6000611b4e601883612037565b91507f42617463682049442063616e6e6f7420626520656d70747900000000000000006000830152602082019050919050565b6000611b8e601383612037565b91507f496e646578206f7574206f6620626f756e6473000000000000000000000000006000830152602082019050919050565b611bca8161205f565b82525050565b6000611bdc82846119d0565b915081905092915050565b60006020820190508181036000830152611c0181846118da565b905092915050565b6000602082019050611c1e600083018461194f565b92915050565b60006020820190508181036000830152611c3e8184611997565b905092915050565b600060e0820190508181036000830152611c60818a611997565b90508181036020830152611c748189611997565b90508181036040830152611c888188611997565b90508181036060830152611c9c8187611997565b9050611cab6080830186611bc1565b611cb860a0830185611bc1565b611cc560c0830184611bc1565b98975050505050505050565b600060e0820190508181036000830152611ceb818a611997565b90508181036020830152611cff8189611997565b90508181036040830152611d138188611997565b9050611d226060830187611bc1565b8181036080830152611d348186611997565b9050611d4360a0830185611bc1565b611d5060c0830184611bc1565b98975050505050505050565b6000610100820190508181036000830152611d77818b611997565b90508181036020830152611d8b818a611997565b90508181036040830152611d9f8189611997565b9050611dae6060830188611bc1565b8181036080830152611dc08187611997565b9050611dcf60a0830186611bc1565b611ddc60c0830185611bc1565b611de960e0830184611bc1565b9998505050505050505050565b60006080820190508181036000830152611e108187611997565b90508181036020830152611e248186611997565b9050611e336040830185611bc1565b8181036060830152611e458184611997565b905095945050505050565b60006060820190508181036000830152611e6a8186611997565b9050611e796020830185611bc1565b611e866040830184611bc1565b949350505050565b60006020820190508181036000830152611ea781611a01565b9050919050565b60006020820190508181036000830152611ec781611a41565b9050919050565b60006020820190508181036000830152611ee781611a81565b9050919050565b60006020820190508181036000830152611f0781611ac1565b9050919050565b60006020820190508181036000830152611f2781611b01565b9050919050565b60006020820190508181036000830152611f4781611b41565b9050919050565b60006020820190508181036000830152611f6781611b81565b9050919050565b6000602082019050611f836000830184611bc1565b92915050565b6000604051905081810181811067ffffffffffffffff82111715611fac57600080fd5b8060405250919050565b600067ffffffffffffffff821115611fcd57600080fd5b601f19601f83011690506020810190509190","50565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b600081905092915050565b60008115159050919050565b6000819050919050565b82818337600083830152505050565b60005b8381101561209657808201518184015260208101905061207b565b838111156120a5576000848401525b50505050565b6000601f19601f8301169050919050565b6120c58161205f565b81146120d057600080fd5b5056fea2646970667358221220676f2f1256eb395e14f54c6efaf52d4b3f2278923d139fe61637b0c8cc15aa3964736f6c634300060a0033"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"manufacturer\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"totalQuantity\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"destination\",\"type\":\"string\"}],\"name\":\"BatchCreated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"usedQuantity\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"remainingQuantity\",\"type\":\"uint256\"}],\"name\":\"BatchUsed\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"}],\"name\":\"MaterialAssigned\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"}],\"name\":\"batchExists\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"manufacturer\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"shaftBatch\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"totalQuantity\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"destination\",\"type\":\"string\"}],\"name\":\"createMaterialBatch\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getAllBatchIds\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"getBatchByIndex\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"manufacturer\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"shaftBatch\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"totalQuantity\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"usedQuantity\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"remainingQuantity\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getBatchCount\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"}],\"name\":\"getBatchIdBySN\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"}],\"name\":\"getBatchInfo\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"manufacturer\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"shaftBatch\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"totalQuantity\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"destination\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"usedQuantity\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"remainingQuantity\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"createTime\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"}],\"name\":\"getBatchInfoBySN\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"batchId\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"manufacturer\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"shaftBatch\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"totalQuantity\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"destination\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"usedQuantity\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"remainingQuantity\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"}],\"name\":\"snExists\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"sn\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"quantity\",\"type\":\"uint256\"}],\"name\":\"useMaterial\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_BATCHEXISTS = "batchExists";

    public static final String FUNC_CREATEMATERIALBATCH = "createMaterialBatch";

    public static final String FUNC_GETALLBATCHIDS = "getAllBatchIds";

    public static final String FUNC_GETBATCHBYINDEX = "getBatchByIndex";

    public static final String FUNC_GETBATCHCOUNT = "getBatchCount";

    public static final String FUNC_GETBATCHIDBYSN = "getBatchIdBySN";

    public static final String FUNC_GETBATCHINFO = "getBatchInfo";

    public static final String FUNC_GETBATCHINFOBYSN = "getBatchInfoBySN";

    public static final String FUNC_SNEXISTS = "snExists";

    public static final String FUNC_USEMATERIAL = "useMaterial";

    public static final Event BATCHCREATED_EVENT = new Event("BatchCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event BATCHUSED_EVENT = new Event("BatchUsed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event MATERIALASSIGNED_EVENT = new Event("MaterialAssigned", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>() {}));
    ;

    protected RawMaterial(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public List<BatchCreatedEventResponse> getBatchCreatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BATCHCREATED_EVENT, transactionReceipt);
        ArrayList<BatchCreatedEventResponse> responses = new ArrayList<BatchCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BatchCreatedEventResponse typedResponse = new BatchCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.batchId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.manufacturer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sn = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.totalQuantity = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.destination = (String) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeBatchCreatedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(BATCHCREATED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,fromBlock,toBlock,otherTopics,callback);
    }

    public void subscribeBatchCreatedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(BATCHCREATED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,callback);
    }

    public List<BatchUsedEventResponse> getBatchUsedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BATCHUSED_EVENT, transactionReceipt);
        ArrayList<BatchUsedEventResponse> responses = new ArrayList<BatchUsedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BatchUsedEventResponse typedResponse = new BatchUsedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.batchId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.sn = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.usedQuantity = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.remainingQuantity = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeBatchUsedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(BATCHUSED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,fromBlock,toBlock,otherTopics,callback);
    }

    public void subscribeBatchUsedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(BATCHUSED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,callback);
    }

    public List<MaterialAssignedEventResponse> getMaterialAssignedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MATERIALASSIGNED_EVENT, transactionReceipt);
        ArrayList<MaterialAssignedEventResponse> responses = new ArrayList<MaterialAssignedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MaterialAssignedEventResponse typedResponse = new MaterialAssignedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sn = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.batchId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeMaterialAssignedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(MATERIALASSIGNED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,fromBlock,toBlock,otherTopics,callback);
    }

    public void subscribeMaterialAssignedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(MATERIALASSIGNED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,callback);
    }

    public Boolean batchExists(String batchId) throws ContractException {
        final Function function = new Function(FUNC_BATCHEXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(batchId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt createMaterialBatch(String batchId, String manufacturer, String shaftBatch, String sn, BigInteger totalQuantity, String destination) {
        final Function function = new Function(
                FUNC_CREATEMATERIALBATCH, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(batchId), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manufacturer), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(shaftBatch), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn), 
                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(totalQuantity), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(destination)), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] createMaterialBatch(String batchId, String manufacturer, String shaftBatch, String sn, BigInteger totalQuantity, String destination, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_CREATEMATERIALBATCH, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(batchId), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manufacturer), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(shaftBatch), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn), 
                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(totalQuantity), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(destination)), 
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateMaterialBatch(String batchId, String manufacturer, String shaftBatch, String sn, BigInteger totalQuantity, String destination) {
        final Function function = new Function(
                FUNC_CREATEMATERIALBATCH, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(batchId), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(manufacturer), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(shaftBatch), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn), 
                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(totalQuantity), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(destination)), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple6<String, String, String, String, BigInteger, String> getCreateMaterialBatchInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_CREATEMATERIALBATCH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple6<String, String, String, String, BigInteger, String>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue(), 
                (String) results.get(3).getValue(), 
                (BigInteger) results.get(4).getValue(), 
                (String) results.get(5).getValue()
                );
    }

    public List getAllBatchIds() throws ContractException {
        final Function function = new Function(FUNC_GETALLBATCHIDS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return convertToNative(result);
    }

    public Tuple7<String, String, String, String, BigInteger, BigInteger, BigInteger> getBatchByIndex(BigInteger index) throws ContractException {
        final Function function = new Function(FUNC_GETBATCHBYINDEX, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple7<String, String, String, String, BigInteger, BigInteger, BigInteger>(
                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue(), 
                (String) results.get(3).getValue(), 
                (BigInteger) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue());
    }

    public BigInteger getBatchCount() throws ContractException {
        final Function function = new Function(FUNC_GETBATCHCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public String getBatchIdBySN(String sn) throws ContractException {
        final Function function = new Function(FUNC_GETBATCHIDBYSN, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Tuple8<String, String, String, BigInteger, String, BigInteger, BigInteger, BigInteger> getBatchInfo(String batchId) throws ContractException {
        final Function function = new Function(FUNC_GETBATCHINFO, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(batchId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple8<String, String, String, BigInteger, String, BigInteger, BigInteger, BigInteger>(
                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue(), 
                (BigInteger) results.get(7).getValue());
    }

    public Tuple7<String, String, String, BigInteger, String, BigInteger, BigInteger> getBatchInfoBySN(String sn) throws ContractException {
        final Function function = new Function(FUNC_GETBATCHINFOBYSN, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple7<String, String, String, BigInteger, String, BigInteger, BigInteger>(
                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue());
    }

    public Boolean snExists(String sn) throws ContractException {
        final Function function = new Function(FUNC_SNEXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt useMaterial(String sn, BigInteger quantity) {
        final Function function = new Function(
                FUNC_USEMATERIAL, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn), 
                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(quantity)), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] useMaterial(String sn, BigInteger quantity, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_USEMATERIAL, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn), 
                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(quantity)), 
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUseMaterial(String sn, BigInteger quantity) {
        final Function function = new Function(
                FUNC_USEMATERIAL, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(sn), 
                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(quantity)), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getUseMaterialInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_USEMATERIAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(

                (String) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public Tuple1<Boolean> getUseMaterialOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_USEMATERIAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>(

                (Boolean) results.get(0).getValue()
                );
    }

    public static RawMaterial load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new RawMaterial(contractAddress, client, credential);
    }

    public static RawMaterial deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(RawMaterial.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public static class BatchCreatedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] batchId;

        public String manufacturer;

        public String sn;

        public BigInteger totalQuantity;

        public String destination;
    }

    public static class BatchUsedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] batchId;

        public String sn;

        public BigInteger usedQuantity;

        public BigInteger remainingQuantity;
    }

    public static class MaterialAssignedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] sn;

        public String batchId;
    }
}
