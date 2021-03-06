import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.account.Account;
import com.klaytn.caver.methods.response.AccountKey;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.AccountUpdate;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Example code about "How to Update Klaytn Account Keys with Caver #1 — AccountKeyPublic"
 * Related article - Korean: https://medium.com/klaytn/caver-caver%EB%A1%9C-klaytn-%EA%B3%84%EC%A0%95%EC%9D%98-%ED%82%A4%EB%A5%BC-%EB%B0%94%EA%BE%B8%EB%8A%94-%EB%B0%A9%EB%B2%95-1-accountkeypublic-7f8a7197e2d4
 * Related article - English: https://medium.com/klaytn/caver-how-to-update-klaytn-account-keys-with-caver-1-accountkeypublic-30336b8f0b50
 */
public class CaverExample {
    // You can directly input values for the variables below, or you can enter values in the caver-java-examples/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
    private static String senderAddress = ""; // e.g. "0xeb709d59954f4cdc6b6f3bfcd8d531887b7bd199"
    private static String senderPrivateKey = ""; // e.g. "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c3269727fc7"
    private static String recipientAddress= ""; // e.g. "0xeb709d59954f4cdc6b6f3bfcd8d531887b7bd199"


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
        System.out.println("=====> Update AccountKey to AccountKeyPublic");

        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        Caver caver = new Caver(httpService);

        // Add keyring to in-memory wallet
        SingleKeyring senderKeyring = caver.wallet.keyring.create(senderAddress, senderPrivateKey);
        caver.wallet.add(senderKeyring);

        // Create new private key
        String newKey = caver.wallet.keyring.generateSingleKey();
        System.out.println("new private key: " + newKey);

        // Create new Keyring as SingleKeyring instance with new private key
        SingleKeyring newKeyring = caver.wallet.keyring.create(senderKeyring.getAddress(), newKey);
        // Create an Account instance that includes the address and the public key
        Account account = newKeyring.toAccount();
        System.out.println(objectToString(account));

        // Create account update transaction instance
        AccountUpdate accountUpdate = caver.transaction.accountUpdate.create(
                TxPropertyBuilder.accountUpdate()
                        .setFrom(senderKeyring.getAddress())
                        .setAccount(account)
                        .setGas(BigInteger.valueOf(50000))
        );

        // Sign the transaction
        caver.wallet.sign(senderKeyring.getAddress(), accountUpdate);
        // Send transaction
        Bytes32 sendResult = caver.rpc.klay.sendRawTransaction(accountUpdate).send();
        if(sendResult.hasError()) {
            throw new TransactionException(sendResult.getError().getMessage());
        }
        String txHash = sendResult.getResult();
        TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
        TransactionReceipt.TransactionReceiptData receiptData = receiptProcessor.waitForTransactionReceipt(txHash);
        System.out.println("Account Update Transaction receipt => ");
        System.out.println(objectToString(receiptData));

        // Get accountKey from network
        AccountKey accountKey = caver.rpc.klay.getAccountKey(senderKeyring.getAddress()).send();
        System.out.println("Result of account key update to AccountKeyPublic");
        System.out.println("Account address: " + senderKeyring.getAddress());
        System.out.println("accountKey => ");
        System.out.println(objectToString(accountKey));

        // Update keyring with new private key in in-memory wallet
        caver.wallet.updateKeyring(newKeyring);
        // Send 1 Peb to recipient to test whether updated accountKey is well-working or not.
        ValueTransfer vt = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                        .setFrom(senderKeyring.getAddress())
                        .setTo(recipientAddress)
                        .setValue(BigInteger.valueOf(1))
                        .setGas(BigInteger.valueOf(25000))
        );

        // Sign the transaction with updated keyring
        caver.wallet.sign(senderKeyring.getAddress(), vt);
        // Send transaction
        Bytes32 vtResult = caver.rpc.klay.sendRawTransaction(vt).send();
        TransactionReceipt.TransactionReceiptData vtReceiptData = receiptProcessor.waitForTransactionReceipt(vtResult.getResult()); System.out.println("After account update value transfer transaction receipt => ");
        System.out.println("Receipt of value transfer transaction after account update => ");
        System.out.println(objectToString(vtReceiptData));
    }
}
