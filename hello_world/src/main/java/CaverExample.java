import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world for caver-java Klaytn SDK! Please read the README before starting :)
 * Related article - Korean: https://ko.docs.klaytn.com/bapp/sdk/caver-java/getting-started
 * Related reference - English: https://docs.klaytn.com/bapp/sdk/caver-java/getting-started
 */
public class CaverExample {
    // You can directly input values for the variables below, or you can enter values in the caver-java-examples/.env file.
    private static String nodeApiUrl = "https://node-api.klaytnapi.com/v1/klaytn"; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = "1001"; // e.g. "1001" or "8217";

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
                workingDirectoryPath.getParent().toString();

        // Read `/path/to/caver-java-examples/.env` file.
        env = Dotenv.configure().directory(envDirectory).load();

        nodeApiUrl = nodeApiUrl.equals("") ? env.get("NODE_API_URL") : nodeApiUrl;
        accessKeyId = accessKeyId.equals("") ? env.get("ACCESS_KEY_ID") : accessKeyId;
        secretAccessKey = secretAccessKey.equals("") ? env.get("SECRET_ACCESS_KEY") : secretAccessKey;
        chainId = chainId.equals("") ? env.get("CHAIN_ID") : chainId;
    }

    public static void run() throws Exception {
        System.out.println("In the caver, almost everything starts with the caver. Let's create a caver instance :)");

        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        System.out.println("Now that we have created the caver instance, we can do whatever we want with the caver.");
        System.out.println("Let's test codes at https://docs.klaytn.com/bapp/sdk/caver-java/getting-started#sending-klay-at-a-glance just for testing.");
        System.out.println();

        String password = "password";
        String keyStoreJsonString = "{\n" +
                "  \"version\": 4,\n" +
                "  \"id\": \"9c12de05-0153-41c7-a8b7-849472eb5de7\",\n" +
                "  \"address\": \"0xc02cec4d0346bf4124deeb55c5216a4138a40a8c\",\n" +
                "  \"keyring\": [\n" +
                "    {\n" +
                "      \"ciphertext\": \"eacf496cea5e80eca291251b3743bf93cdbcf7072efc3a74efeaf518e2796b15\",\n" +
                "      \"cipherparams\": {\n" +
                "        \"iv\": \"d688a4319342e872cefcf51aef3ec2da\"\n" +
                "      },\n" +
                "      \"cipher\": \"aes-128-ctr\",\n" +
                "      \"kdf\": \"scrypt\",\n" +
                "      \"kdfparams\": {\n" +
                "        \"dklen\": 32,\n" +
                "        \"salt\": \"c3cee502c7157e0faa42386c6d666116ffcdf093c345166c502e23bc34e6ba40\",\n" +
                "        \"n\": 4096,\n" +
                "        \"r\": 8,\n" +
                "        \"p\": 1\n" +
                "      },\n" +
                "      \"mac\": \"4b49574f3d3356fa0d04f73e07d5a2a6bbfdd185bedfa31f37f347bc98f2ef26\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        SingleKeyring decrypt = (SingleKeyring)caver.wallet.keyring.decrypt(keyStoreJsonString, password);
        System.out.println("Decrypted address : " + decrypt.getAddress());
        System.out.println("Decrypted key : " + decrypt.getKey().getPrivateKey());

        SingleKeyring addedKeyring = (SingleKeyring)caver.wallet.add(decrypt);
        System.out.println("address : " + addedKeyring.getAddress());
        System.out.println("key : " + addedKeyring.getKey().getPrivateKey());
        System.out.println();

        System.out.println("OK! It works :) You can test another codes whatever you want :)");
        System.out.println("Or check some scenarios in this caver-java-examples repository.");
    }
}
