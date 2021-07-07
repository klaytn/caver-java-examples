import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.BlockTransactionReceipts;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

/**
 * Boilerplate code about "How to get receipts included in a block."
 * Related reference - Korean: https://ko.docs.klaytn.com/bapp/json-rpc/api-references/klay/block#klay_getblockreceipts
 * Related reference - English: https://docs.klaytn.com/bapp/json-rpc/api-references/klay/block#klay_getblockreceipts
 */
public class Boilerplate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-boilerplate/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";

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
        Dotenv env = Dotenv.configure().directory("../../").ignoreIfMalformed().ignoreIfMissing().load();
        if(env.get("NODE_API_URL") == null) {
            // This handle the situation when user tries to run BoilerPlate code from project root directory
            env = Dotenv.configure().directory(System.getProperty("user.dir")).ignoreIfMalformed().ignoreIfMissing().load();
        }

        nodeApiUrl = nodeApiUrl.equals("") ? env.get("NODE_API_URL") : nodeApiUrl;
        accessKeyId = accessKeyId.equals("") ? env.get("ACCESS_KEY_ID") : accessKeyId;
        secretAccessKey = secretAccessKey.equals("") ? env.get("SECRET_ACCESS_KEY") : secretAccessKey;
        chainId = chainId.equals("") ? env.get("CHAIN_ID") : chainId;
    }

    public static void run() throws Exception {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        // This is for testing Baobab Network (Test-net)
        // If there are no transactions in the block being looked up, the returned data will be empty.
        String baobabBlockHashEmpty = "0x0f7f242e97dd0334c1c3d76b2f39846064b3766072fd4f2350c62d288477de21";
        BlockTransactionReceipts blockTransactionReceipts = caver.rpc.klay.getBlockReceipts(baobabBlockHashEmpty).send();
        System.out.println(objectToString(blockTransactionReceipts));

        String baobabBlockHash = "0x28fdc2fdce29513105fdaa605384a75ee15623ccb2271febc5b73554f17ab09d";
        blockTransactionReceipts = caver.rpc.klay.getBlockReceipts(baobabBlockHash).send();
        System.out.println(objectToString(blockTransactionReceipts));

        // // This is for testing Cypress Network (Main-net)
        // // If there are no transactions in the block being looked up, the returned data will be empty.
        //String cypressBlockHashEmpty = "0xacb410b0c43e5dcddaa27c55533699a6cbe95b150e38e67c04ba2b9e9d7a47dd";
        //BlockTransactionReceipts blockTransactionReceipts = caver.rpc.klay.getBlockReceipts(cypressBlockHashEmpty).send();
        //System.out.println(objectToString(blockTransactionReceipts));

        //String cypressBlockHash = "0xaaec69d70a85504db43a01599d92c22b74c8e03776b499258f4dd7b2fc2957d1";
        //blockTransactionReceipts = caver.rpc.klay.getBlockReceipts(cypressBlockHash).send();
        //System.out.println(objectToString(blockTransactionReceipts));
    }
}
