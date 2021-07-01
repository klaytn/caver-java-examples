import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * BoilerPlate code about "How to execute Smart Contract."
 */
public class Boilerplate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-boilerplate/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
    private static String deployerAddress = ""; // e.g. "0xf5d3322418f2f2257d9255ad4a7ff1d50312d438"
    private static String deployerPrivateKey = ""; // e.g. "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c3269727fc7"

    public static void main(String[] args) {
        loadEnv();
        run();
    }

    public static void loadEnv() {
        Dotenv env = Dotenv.configure().directory("../../").ignoreIfMalformed().ignoreIfMissing().load();
        if (env.get("NODE_API_URL") == null) {
            // This handle the situation when user tries to run BoilerPlate code from project root directory
            env = Dotenv.configure().directory(System.getProperty("user.dir")).ignoreIfMalformed().ignoreIfMissing().load();
        }

        nodeApiUrl = nodeApiUrl.equals("") ? env.get("NODE_API_URL") : nodeApiUrl;
        accessKeyId = accessKeyId.equals("") ? env.get("ACCESS_KEY_ID") : accessKeyId;
        secretAccessKey = secretAccessKey.equals("") ? env.get("SECRET_ACCESS_KEY") : secretAccessKey;
        chainId = chainId.equals("") ? env.get("CHAIN_ID") : chainId;
        deployerAddress = deployerAddress.equals("") ? env.get("DEPLOYER_ADDRESS") : deployerAddress;
        deployerPrivateKey = deployerPrivateKey.equals("") ? env.get("DEPLOYER_PRIVATE_KEY") : deployerPrivateKey;
    }

    public static void run() {
        try {
            HttpService httpService = new HttpService(nodeApiUrl);
            if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
                throw new Exception("accessKeyId and secretAccessKey must not be empty.");
            }
            httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
            httpService.addHeader("x-chain-id", chainId);

            Caver caver = new Caver(httpService);
            // contractAbi is extracted by compiling caver-java-boilerplate/resources/KVstore.sol using solc:0.5.6
            String contractAbi = "[{\"constant\":true,\"inputs\":[{\"name\":\"key\",\"type\":\"string\"}],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
            String contractAddress = "0x1837f616100644973cf311b5bc9d109b7b427465";
            SingleKeyring deployerKeyring = caver.wallet.keyring.create(
                    deployerAddress,
                    deployerPrivateKey
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
}
