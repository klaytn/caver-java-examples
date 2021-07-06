import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.utils.Utils;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BoilerPlate code about "How to send Klay."
 */
public class Boilerplate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-examples/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
    private static String senderAddress = ""; // e.g. "0xeb709d59954f4cdc6b6f3bfcd8d531887b7bd199"
    private static String senderPrivateKey = ""; // e.g. "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c3269727fc7"
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
        senderAddress = senderAddress.equals("") ? env.get("SENDER_ADDRESS") : senderAddress;
        senderPrivateKey = senderPrivateKey.equals("") ? env.get("SENDER_PRIVATE_KEY") : senderPrivateKey;
        recipientAddress = recipientAddress.equals("") ? env.get("RECIPIENT_ADDRESS") : recipientAddress;
    }

    public static void run() throws Exception {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        SingleKeyring keyring = caver.wallet.keyring.create(senderAddress, senderPrivateKey);
        caver.wallet.add(keyring);

        // Send 1 KLAY.
        ValueTransfer vt = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                        .setFrom(senderAddress)
                        .setTo(recipientAddress)
                        .setValue(new BigInteger(
                                caver.utils.convertToPeb(
                                        BigDecimal.valueOf(1),
                                        Utils.KlayUnit.KLAY
                                )
                        ))
                        .setGas(BigInteger.valueOf(25000))
        );
        caver.wallet.sign(keyring.getAddress(), vt);
        Bytes32 result = caver.rpc.klay.sendRawTransaction(vt).send();
        System.out.println(objectToString(result));
    }
}
