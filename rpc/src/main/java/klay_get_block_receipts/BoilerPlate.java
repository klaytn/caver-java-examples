package klay_get_block_receipts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.BlockTransactionReceipts;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * BoilerPlate code about "How to fetch receipts included in a block identified by block hash." <br>
 * Related reference - Korean: https://ko.docs.klaytn.com/bapp/json-rpc/api-references/klay/block#klay_getblockreceipts <br>
 * Related reference - English: https://docs.klaytn.com/bapp/json-rpc/api-references/klay/block#klay_getblockreceipts
 */
public class BoilerPlate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-boilerplate/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";

    public static void main(String[] args) {
        loadEnv();
        run();
    }

    public static String objectToString(Object value) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(value);
    }

    public static void loadEnv() {
        Dotenv env = Dotenv.configure()
                .directory("..")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        nodeApiUrl = nodeApiUrl.equals("") ? env.get("NODE_API_URL") : nodeApiUrl;
        accessKeyId = accessKeyId.equals("") ? env.get("ACCESS_KEY_ID") : accessKeyId;
        secretAccessKey = secretAccessKey.equals("") ? env.get("SECRET_ACCESS_KEY") : secretAccessKey;
        chainId = chainId.equals("") ? env.get("CHAIN_ID") : chainId;
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

            BlockTransactionReceipts blockTransactionReceipts = caver.rpc.klay.getBlockReceipts("0xeb5ce356d33b63c6489e7ac5120822dc82d419cdd197dc6bc0164e550ef74c8b").send();
            System.out.println(objectToString(blockTransactionReceipts));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
