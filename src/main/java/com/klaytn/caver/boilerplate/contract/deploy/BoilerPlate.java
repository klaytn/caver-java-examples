package com.klaytn.caver.boilerplate.contract.deploy;

import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.ContractDeployParams;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import okhttp3.Credentials;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;

public class BoilerPlate {
    private static final String URL_NODE_API = "https://node-api.klaytnapi.com/v1/klaytn";

    // Configuration Part
    // Set your KAS access key and secretAccessKey.
    static String accessKey = "{your_accessKeyId}";
    static String secretAccessKey = "{your_secretAccessKey}";


    // static String CHAIN_ID_BAOBOB = "1001";
    // static String CHAIN_ID_CYPRESS = "8217";
    static String chainId = "1001";

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        Caver caver = setCaver(accessKey, secretAccessKey, chainId);

        String kvStorageAbi = "[{\"constant\":true,\"inputs\":[{\"name\":\"key\",\"type\":\"string\"}],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String kvStorageBytecode = "608060405234801561001057600080fd5b5061051f806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063693ec85e1461003b578063e942b5161461016f575b600080fd5b6100f46004803603602081101561005157600080fd5b810190808035906020019064010000000081111561006e57600080fd5b82018360208201111561008057600080fd5b803590602001918460018302840111640100000000831117156100a257600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192905050506102c1565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610134578082015181840152602081019050610119565b50505050905090810190601f1680156101615780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6102bf6004803603604081101561018557600080fd5b81019080803590602001906401000000008111156101a257600080fd5b8201836020820111156101b457600080fd5b803590602001918460018302840111640100000000831117156101d657600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192908035906020019064010000000081111561023957600080fd5b82018360208201111561024b57600080fd5b8035906020019184600183028401116401000000008311171561026d57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192905050506103cc565b005b60606000826040518082805190602001908083835b602083106102f957805182526020820191506020810190506020830392506102d6565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390208054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103c05780601f10610395576101008083540402835291602001916103c0565b820191906000526020600020905b8154815290600101906020018083116103a357829003601f168201915b50505050509050919050565b806000836040518082805190602001908083835b6020831061040357805182526020820191506020810190506020830392506103e0565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020908051906020019061044992919061044e565b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061048f57805160ff19168380011785556104bd565b828001600101855582156104bd579182015b828111156104bc5782518255916020019190600101906104a1565b5b5090506104ca91906104ce565b5090565b6104f091905b808211156104ec5760008160009055506001016104d4565b5090565b9056fea165627a7a723058200b1b223ec551863ee3feb41b5864834a9b7fb1d96f160fc5559f34ef3fa1c3d80029";
        SingleKeyring deployerKeyring = caver.wallet.keyring.create(
                "0xf5d3322418f2f2257d9255ad4a7ff1d50312d438",
                "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c1269727fc7"
        );
        try {
            Contract contract = caver.contract.create(kvStorageAbi);
            ContractDeployParams contractDeployParams = new ContractDeployParams(kvStorageBytecode);
            SendOptions sendOptions = new SendOptions(deployerKeyring.getAddress(), BigInteger.valueOf(400000));

            caver.wallet.add(deployerKeyring);
            Contract deployedContract = contract.deploy(contractDeployParams, sendOptions);

            System.out.println("Deployed address of contract: " + deployedContract.getContractAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Caver setCaver(String accessKey, String secretAccessKey, String chainID) {
        HttpService httpService = new HttpService(URL_NODE_API);
        httpService.addHeader("Authorization", Credentials.basic(accessKey, secretAccessKey));
        httpService.addHeader("x-chain-id", chainID);

        return new Caver(httpService);
    }
}
