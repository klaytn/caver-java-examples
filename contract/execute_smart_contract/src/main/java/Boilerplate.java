import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Boilerplate code about "How to execute Smart Contract."
 * Related reference - Korean: https://ko.docs.klaytn.com/bapp/sdk/caver-java/getting-started#smart-contract
 * Related reference - English: https://docs.klaytn.com/bapp/sdk/caver-java/getting-started#smart-contract
 */
public class Boilerplate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-examples/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
    private static String deployerAddress = ""; // e.g. "0xf5d3322418f2f2257d9255ad4a7ff1d50312d438"
    private static String deployerPrivateKey = ""; // e.g. "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c3269727fc7"


    public static void main(String[] args) {
        try {
            loadEnv();
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String objectToString(Object value) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(value);
    }

    public static void loadEnv() {
        Dotenv env;
        String workingDirectory = System.getProperty("user.dir");
        Path workingDirectoryPath = Paths.get(workingDirectory);
        String projectRootDirectory = "caver-java-examples";
        String currentDirectoryName = workingDirectoryPath.getName(workingDirectoryPath.getNameCount() - 1).toString();
        String envDirectory = currentDirectoryName.equals(projectRootDirectory) ?
                workingDirectoryPath.toString() :
                workingDirectoryPath.getParent().getParent().toString();

        // Read `/path/to/caver-java-examples/.env` file.
        env = Dotenv.configure().directory(envDirectory).load();

        nodeApiUrl = nodeApiUrl.equals("") ? env.get("NODE_API_URL") : nodeApiUrl;
        accessKeyId = accessKeyId.equals("") ? env.get("ACCESS_KEY_ID") : accessKeyId;
        secretAccessKey = secretAccessKey.equals("") ? env.get("SECRET_ACCESS_KEY") : secretAccessKey;
        chainId = chainId.equals("") ? env.get("CHAIN_ID") : chainId;
        deployerAddress = deployerAddress.equals("") ? env.get("DEPLOYER_ADDRESS") : deployerAddress;
        deployerPrivateKey = deployerPrivateKey.equals("") ? env.get("DEPLOYER_PRIVATE_KEY") : deployerPrivateKey;
    }

    public static void run() throws Exception {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        // abi is extracted by compiling caver-java-examples/resources/KVstore.sol using solc(solidity compiler)
        String abi = "[\n" +
                "  {\n" +
                "    \"constant\":true,\n" +
                "    \"inputs\":[\n" +
                "      {\n" +
                "        \"name\":\"key\",\n" +
                "        \"type\":\"string\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\":\"get\",\n" +
                "    \"outputs\":[\n" +
                "      {\n" +
                "        \"name\":\"\",\n" +
                "        \"type\":\"string\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"payable\":false,\n" +
                "    \"stateMutability\":\"view\",\n" +
                "    \"type\":\"function\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"constant\":false,\n" +
                "    \"inputs\":[\n" +
                "      {\n" +
                "        \"name\":\"key\",\n" +
                "        \"type\":\"string\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\":\"value\",\n" +
                "        \"type\":\"string\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\":\"set\",\n" +
                "    \"outputs\":[],\n" +
                "    \"payable\":false,\n" +
                "    \"stateMutability\":\"nonpayable\",\n" +
                "    \"type\":\"function\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"inputs\":[\n" +
                "      {\n" +
                "        \"name\":\"key\",\n" +
                "        \"type\":\"string\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\":\"value\",\n" +
                "        \"type\":\"string\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"payable\":false,\n" +
                "    \"stateMutability\":\"nonpayable\",\n" +
                "    \"type\":\"constructor\"\n" +
                "  }\n" +
                "]";
        // You can get contract address
        // by running caver-java-examples/contract/deploy scenario.
        String contractAddress = "0x{contractAddress}";
        SingleKeyring deployerKeyring = caver.wallet.keyring.create(
                deployerAddress,
                deployerPrivateKey
        );
        caver.wallet.add(deployerKeyring);

        Contract contract = caver.contract.create(abi, contractAddress);
        SendOptions sendOptions = new SendOptions(deployerKeyring.getAddress(), BigInteger.valueOf(400000));
        TransactionReceipt.TransactionReceiptData transactionReceiptData = contract.send(
                sendOptions,
                "set",
                "k1",
                "v1"
        );
        System.out.println(objectToString(transactionReceiptData));

        List<Type> callResult = contract.call("get", "k1");
        System.out.println("Result of calling get function with key:");
        System.out.println(objectToString(callResult));    }
}
