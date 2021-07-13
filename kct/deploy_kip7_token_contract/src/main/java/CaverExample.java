import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.kct.kip7.KIP7;
import com.klaytn.caver.kct.kip7.KIP7DeployParams;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Example code about "How to deploy my own KIP7 token."
 * Related reference: https://javadoc.io/doc/com.klaytn.caver/core/latest/com/klaytn/caver/kct/kip7/KIP7.html
 */
public class CaverExample {
    // You can directly input values for the variables below, or you can enter values in the caver-java-examples/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
    private static String deployerAddress = ""; // e.g. "0xf5d3322418f2f2257d9255ad4a7ff1d50312d438"
    private static String deployerPrivateKey = ""; // e.g. "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c3269727fc7"
    private static String recipientAddress = ""; // e.g. "0xeb709d59954f4cdc6b6f3bfcd8d531887b7bd199"

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
        recipientAddress = recipientAddress.equals("") ? env.get("RECIPIENT_ADDRESS") : recipientAddress;
    }

    public static void run() throws Exception {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        // Deployer account must have KLAY enough to deploy contract.
        // If you are testing on the Baobab testnet,
        // you can get 5 KLAY at "https://baobab.wallet.klaytn.com/faucet".
        SingleKeyring deployerKeyring = caver.wallet.keyring.create(deployerAddress, deployerPrivateKey);
        caver.wallet.add(deployerKeyring);

        KIP7DeployParams params = new KIP7DeployParams(
                "TestToken",
                "TTK",
                18,
                new BigInteger("1000000000000000000")
        );
        KIP7 kip7 = caver.kct.kip7.deploy(params, deployerKeyring.getAddress());
        System.out.println("Deployed address of KIP7 token contract: " + kip7.getContractAddress());

        String name = kip7.name();
        System.out.println("The name of the KIP-7 token contract: " + name);

        SendOptions opts = new SendOptions();
        opts.setFrom(deployerKeyring.getAddress());
        TransactionReceipt.TransactionReceiptData receiptData = kip7.transfer(
                recipientAddress,
                BigInteger.ONE,
                opts
        );
        System.out.println(objectToString(receiptData));
    }
}
