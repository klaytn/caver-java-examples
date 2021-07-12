import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.ValueTransferMemo;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Boilerplate code about "Using IPFS with Caver."
 * Related article - Korean: https://medium.com/klaytn/caver%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-ipfs-%EC%82%AC%EC%9A%A9%EB%B2%95-4889a3b29c0b
 * Related article - English: https://medium.com/klaytn/using-ipfs-with-caver-964e1f721bfe
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
        senderAddress = senderAddress.equals("") ? env.get("SENDER_ADDRESS") : senderAddress;
        senderPrivateKey = senderPrivateKey.equals("") ? env.get("SENDER_PRIVATE_KEY") : senderPrivateKey;
        recipientAddress = recipientAddress.equals("") ? env.get("RECIPIENT_ADDRESS") : recipientAddress;
    }

    public static void run() throws Exception {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        // Set connection with IPFS Node
        caver.ipfs.setIPFSNode("ipfs.infura.io", 5001, true);
        // `ipfs.txt` is located at `caver-java-examples/ipfs/using_ipfs_with_caver/resources`.
        File testFile = new File("resources/ipfs.txt");
        if (testFile.exists() == false) {
            // Handles when you run this Boilerplate as sub-module using IDE.
            testFile = new File("ipfs/using_ipfs_with_caver/resources/ipfs.txt");
            if (testFile.exists() == false) {
                throw new FileNotFoundException("Cannot find ipfs.txt testFile.");
            }
        }

        // Add a file to IPFS with file path
        String cid = caver.ipfs.add(testFile.getAbsolutePath());
        System.out.println("cid: " + cid);

        // // Add a testFile to IPFS with testFile contents
        // String text = "IPFS test";
        // byte[] data = text.getBytes();
        // String cid = caver.ipfs.add(data);

        // Download a testFile from IPFS
        byte[] contents = caver.ipfs.get(cid);
        System.out.println("Contents downloaded from IPFS: " + new String(contents, StandardCharsets.UTF_8));

        // Convert from CID to multihash(hex formatted)
        String multihash = caver.ipfs.toHex(cid);
        System.out.println("multihash: " + multihash);

        // Add keyring to in-memory wallet
        SingleKeyring senderKeyring = caver.wallet.keyring.create(senderAddress, senderPrivateKey);
        caver.wallet.add(senderKeyring);

        // Create ValueTransferMemo transaction
        // to submit a cid to Klaytn network
        ValueTransferMemo tx = caver.transaction.valueTransferMemo.create(
                TxPropertyBuilder.valueTransferMemo()
                        .setFrom(senderKeyring.getAddress())
                        .setTo(recipientAddress)
                        .setGas(BigInteger.valueOf(25000))
                        .setValue("0x0")
                        .setInput(multihash)
        );
        // Sign to the transaction
        caver.wallet.sign(senderKeyring.getAddress(), tx);

        // Send a signed transaction to Klaytn
        String txHash = caver.rpc.klay.sendRawTransaction(tx).send().getResult();
        TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
        TransactionReceipt.TransactionReceiptData receiptData = receiptProcessor.waitForTransactionReceipt(txHash);
        System.out.println(objectToString(receiptData));
    }
}
