import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
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
 * Boilerplate code about "How to use Klay Units."
 * Related article - Korean: https://ko.docs.klaytn.com/klaytn/design/klaytn-native-coin-klay#units-of-klay
 * Related reference - English: https://docs.klaytn.com/klaytn/design/klaytn-native-coin-klay#units-of-klay
 */
public class Boilerplate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-boilerplate/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
    private static String senderAddress = ""; // e.g. "0xeb709d59954f4cdc6b6f3bfcd8d531887b7bd199"
    private static String senderPrivateKey = "";
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

    public static void run() {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        SingleKeyring keyring = caver.wallet.keyring.create(senderAddress, senderPrivateKey);
        caver.wallet.add(keyring);

        // Because a field "value" always interprets its value as a unit "peb",
        // you must take care what is the actual value when you sending some KLAY.
        ValueTransfer vt = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                        .setFrom(keyring.getAddress())
                        .setTo(recipientAddress)
                        .setGas(BigInteger.valueOf(25000))
                        .setValue(BigInteger.ONE)
        );
        // Example-1: Sending 0.5 KLAY to recipient
        // 1 KLAY is actually 10^18(=1000000000000000000) peb. So if you want send 0.5 KLAY,
        // option-1: Set actual peb value directly to ValueTransfer transaction instance.
        vt.setValue(BigInteger.valueOf(500000000000000000L)); // 5 * (10^17)
        System.out.println("Example-1) The value what we set using option-1 is " + vt.getValue());

        // option-2 (Recommended): option-1 is very cumbersome and difficult to read. Let's use more elegant way.
        String KLAY = caver.utils.convertToPeb(BigDecimal.valueOf(5), Utils.KlayUnit.KLAY);
        BigInteger bigIntegerKLAY = new BigInteger(KLAY).divide(BigInteger.valueOf(10));
        vt.setValue(bigIntegerKLAY);
        System.out.println("Example-1) The value what we set using option-2 is " + vt.getValue());

        // Example-2: Sending 0.005 KLAY to recipient
        // option-1: Use bigIntegerKLAY we created at option-2 of Example-1.
        vt.setValue(bigIntegerKLAY.divide(BigInteger.valueOf(100)));
        System.out.println("Example-2) The value what we set using option-1 is " + vt.getValue());

        // option-2 (Recommended): option-1 has a high probability of being wrong by human error. Let's use more secure methomethodd.
        String mKLAY = caver.utils.convertToPeb(BigDecimal.valueOf(5), Utils.KlayUnit.mKLAY);
        BigInteger bigIntegerUKLAY = new BigInteger(mKLAY);
        vt.setValue(bigIntegerUKLAY);
        System.out.println("Example-2) The value what we set using option-2 is " + vt.getValue());
    }
}
