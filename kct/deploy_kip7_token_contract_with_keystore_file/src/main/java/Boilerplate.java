import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.kct.kip7.KIP7;
import com.klaytn.caver.kct.kip7.KIP7DeployParams;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.AbstractKeyring;
import com.klaytn.caver.wallet.keyring.KeyStore;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.math.BigInteger;

/**
 * BoilerPlate code about "How to deploy my own KIP7 token with keystore file."
 * Related article - Korean: https://medium.com/klaytn/common-architecture-of-caver-f7a7a1c554de
 * Related article - English: https://medium.com/klaytn/common-architecture-of-caver-a714224a0047
 */
public class Boilerplate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-boilerplate/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
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
        Dotenv env = Dotenv.configure().directory("../../").ignoreIfMalformed().ignoreIfMissing().load();
        if(env.get("NODE_API_URL") == null) {
            // This handle the situation when user tries to run BoilerPlate code from project root directory
            env = Dotenv.configure().directory(System.getProperty("user.dir")).ignoreIfMalformed().ignoreIfMissing().load();
        }

        nodeApiUrl = nodeApiUrl.equals("") ? env.get("NODE_API_URL") : nodeApiUrl;
        accessKeyId = accessKeyId.equals("") ? env.get("ACCESS_KEY_ID") : accessKeyId;
        secretAccessKey = secretAccessKey.equals("") ? env.get("SECRET_ACCESS_KEY") : secretAccessKey;
        chainId = chainId.equals("") ? env.get("CHAIN_ID") : chainId;
        recipientAddress = recipientAddress.equals("") ? env.get("RECIPIENT_ADDRESS") : recipientAddress;
    }

    public static void run() throws Exception {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);

        Caver caver = new Caver(httpService);

        // 1. Create your own keystore file at "https://baobab.wallet.klaytn.com/create"
        //    and place the file at `caver-java-examples/kct/deploy_kip7_token_contract_with_keystore_file/resources`.
        // 2. Get 5 KLAY at "https://baobab.wallet.klaytn.com/faucet".
        File file = new File("resources/keystore.json");
        if(file.exists() == false) {
            // Handles when you run this Boilerplate as sub-module using IDE.
            file = new File("kct/deploy_kip7_token_contract_with_keystore_file/resources/keystore.json");
            if(file.exists() == false) {
                throw new Exception("Cannot find keystore.json file.");
            }
        }
        String password = "Password!@#4"; // Put your password here.
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        KeyStore keyStore = objectMapper.readValue(file, KeyStore.class);
        AbstractKeyring keyring = caver.wallet.keyring.decrypt(keyStore, password);
        caver.wallet.add(keyring);

        KIP7DeployParams params = new KIP7DeployParams(
                "TestToken",
                "TTK",
                18,
                new BigInteger("1000000000000000000")
        );
        KIP7 kip7 = caver.kct.kip7.deploy(params, keyring.getAddress());
        System.out.println("Deployed address of KIP7 token contract: " + kip7.getContractAddress());

        SendOptions opts = new SendOptions();
        opts.setFrom(keyring.getAddress());
        TransactionReceipt.TransactionReceiptData r = kip7.transfer(
                recipientAddress,
                BigInteger.ONE,
                opts
        );
    }
}
