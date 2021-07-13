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
import java.util.ArrayList;
import java.util.List;

/**
 * Example code about "How to create many EOAs easily."
 * Related reference - Korean: https://ko.docs.klaytn.com/klaytn/design/accounts#externally-owned-accounts-eoas
 * Related reference - English: https://docs.klaytn.com/klaytn/design/accounts#externally-owned-accounts-eoas
 */
public class CaverExample {
    // You can directly input values for the variables below, or you can enter values in the caver-java-examples/.env file.
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
    }

    public static void run() throws Exception {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        int numEOAs = 10;
        List<SingleKeyring> eoas = new ArrayList<>();
        for(int i = 0; i < numEOAs; i++) {
            eoas.add(caver.wallet.keyring.generate());
        }
        System.out.println("Succeed to create " + numEOAs + " EOAs using SingleKey.");
        System.out.println(objectToString(eoas));
    }
}
