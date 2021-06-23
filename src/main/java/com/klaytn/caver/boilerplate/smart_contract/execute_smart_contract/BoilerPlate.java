package com.klaytn.caver.boilerplate.smart_contract.execute_smart_contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class BoilerPlate {
    private static final String URL_NODE_API = "https://node-api.klaytnapi.com/v1/klaytn";
    private static final String accessKeyId = "<your-access-key-id-generated-by-KAS>"; // e.g. accessKeyId = "KASK1LVNO498YT6KJQFUPY8S";
    private static final String secretAccessKey = "<your-secret-access-key-generated-by-KAS>"; // e.g. secretAccessKey = "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static final String chainId = "[1001|8217]"; // e.g. chainId = "1001";

    public static void main(String[] args) throws IOException {
        run();
    }

    public static void run() {
        try {
            Caver caver = setCaver();
            // contractAbi and contractBytecode are extracted by compiling ../resources/kvstore.sol using solc:0.5.6
            String contractAbi = "[{\"constant\":true,\"inputs\":[{\"name\":\"key\",\"type\":\"string\"}],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"key\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"string\"}],\"name\":\"Set\",\"type\":\"event\"}]";
            String contractAddress = "0x1837f616100644973cf311b5bc9d109b7b427465";
            SingleKeyring deployerKeyring = caver.wallet.keyring.create(
                    "0xf5d3322418f2f2257d9255ad4a7ff1d50312d438",
                    "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c1269727fc7"
            );

            Contract contract = caver.contract.create(contractAbi, contractAddress);
            SendOptions sendOptions = new SendOptions(deployerKeyring.getAddress(), BigInteger.valueOf(400000));

            caver.wallet.add(deployerKeyring);
            TransactionReceipt.TransactionReceiptData transactionReceiptData = contract.send(sendOptions, "set", "k1", "v1");
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            System.out.println(ow.writeValueAsString(transactionReceiptData));

            List<Type> callResult = contract.call("get", "k1");
            System.out.println(ow.writeValueAsString(callResult));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Caver setCaver() throws Exception {
        HttpService httpService = new HttpService(URL_NODE_API);
        if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
            throw new Exception("accessKeyId and secretAccessKey must not be empty.");
        }
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);

        return new Caver(httpService);
    }
}
