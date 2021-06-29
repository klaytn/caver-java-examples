import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.klaytn.caver.Caver;
import com.klaytn.caver.account.Account;
import com.klaytn.caver.account.WeightedMultiSigOptions;
import com.klaytn.caver.methods.response.AccountKey;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.AccountUpdate;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.wallet.keyring.RoleBasedKeyring;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Credentials;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * BoilerPlate code about "How to Update Klaytn Account Keys with Caver #1 — AccountKeyRoleBased"
 * Related article - Korean: https://medium.com/klaytn/caver-js%EB%A1%9C-%EB%82%B4-%EA%B3%84%EC%A0%95%EC%9D%98-%ED%82%A4%EB%A5%BC-%EB%B0%94%EA%BE%B8%EB%8A%94-%EB%B0%A9%EB%B2%95-3-accountkeyrolebased-bef4d109b467
 * Related article - English:
 */
public class BoilerPlate {
    // You can directly input values for the variables below, or you can enter values in the caver-java-boilerplate/.env file.
    private static String nodeApiUrl = ""; // e.g. "https://node-api.klaytnapi.com/v1/klaytn";
    private static String accessKeyId = ""; // e.g. "KASK1LVNO498YT6KJQFUPY8S";
    private static String secretAccessKey = ""; // e.g. "aP/reVYHXqjw3EtQrMuJP4A3/hOb69TjnBT3ePKG";
    private static String chainId = ""; // e.g. "1001" or "8217";
    private static String privateKey = ""; // e.g. "0x42f6375b608c2572fadb2ed9fd78c5c456ca3aa860c43192ad910c3269727fc7"

    public static void main(String[] args) {
        loadEnv();
        run();
    }

    public static String objectToString(Object value) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(value);
    }

    public static void loadEnv() {
        Dotenv env = Dotenv.configure().directory("../../").ignoreIfMalformed().ignoreIfMissing().load();
        if (env.get("NODE_API_URL") == null) {
            // This handle the situation when user tries to run BoilerPlate code from project root directory
            env = Dotenv.configure().directory(System.getProperty("user.dir")).ignoreIfMalformed().ignoreIfMissing().load();
        }

        nodeApiUrl = nodeApiUrl.equals("") ? env.get("NODE_API_URL") : nodeApiUrl;
        accessKeyId = accessKeyId.equals("") ? env.get("ACCESS_KEY_ID") : accessKeyId;
        secretAccessKey = secretAccessKey.equals("") ? env.get("SECRET_ACCESS_KEY") : secretAccessKey;
        chainId = chainId.equals("") ? env.get("CHAIN_ID") : chainId;
        privateKey = privateKey.equals("") ? env.get("SENDER_PRIVATE_KEY") : privateKey;

        System.out.println(privateKey);
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

            System.out.println("=====> Update AccountKey to AccountKeyRoleBased");
            SingleKeyring keyring = caver.wallet.keyring.createFromPrivateKey(privateKey);
            caver.wallet.add(keyring);

            List<String[]> newRoleBasedKeys = caver.wallet.keyring.generateRolBasedKeys(new int[]{2, 1, 3});
            System.out.println("new private keys by role: " + objectToString(newRoleBasedKeys));

            RoleBasedKeyring newKeyring = caver.wallet.keyring.create(keyring.getAddress(), newRoleBasedKeys);

            BigInteger[][] optionWeight = {
                    {BigInteger.ONE, BigInteger.ONE},
                    {},
                    {BigInteger.valueOf(2), BigInteger.ONE, BigInteger.ONE}
            };

            WeightedMultiSigOptions[] options = {
                    new WeightedMultiSigOptions(BigInteger.valueOf(2), Arrays.asList(optionWeight[0])),
                    new WeightedMultiSigOptions(),
                    new WeightedMultiSigOptions(BigInteger.valueOf(3), Arrays.asList(optionWeight[2]))
            };

            Account account = newKeyring.toAccount(Arrays.asList(options));
            AccountUpdate accountUpdate = caver.transaction.accountUpdate.create(
                    TxPropertyBuilder.accountUpdate()
                            .setFrom(keyring.getAddress())
                            .setAccount(account)
                            .setGas(BigInteger.valueOf(150000))
            );

            caver.wallet.sign(keyring.getAddress(), accountUpdate);
            Bytes32 sendResult = caver.rpc.klay.sendRawTransaction(accountUpdate).send();
            if (sendResult.hasError()) {
                throw new TransactionException(sendResult.getError().getMessage());
            }
            String hash = sendResult.getResult();

            TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
            TransactionReceipt.TransactionReceiptData receiptData = receiptProcessor.waitForTransactionReceipt(hash);
            System.out.println("Account Update Transaction receipt => ");
            System.out.println(objectToString(receiptData));

            AccountKey accountKey = caver.rpc.klay.getAccountKey(keyring.getAddress()).send();
            System.out.println("Result of account key update to AccountKeyRoleBased");
            System.out.println("Account address: " + keyring.getAddress());
            System.out.println("accountKey => ");
            System.out.println(objectToString(accountKey));

            caver.wallet.updateKeyring(newKeyring);
            ValueTransfer vt = caver.transaction.valueTransfer.create(
                    TxPropertyBuilder.valueTransfer()
                            .setFrom(keyring.getAddress())
                            .setTo(keyring.getAddress())
                            .setValue(BigInteger.valueOf(1))
                            .setGas(BigInteger.valueOf(150000))
            );

            caver.wallet.sign(keyring.getAddress(), vt);
            Bytes32 vtResult = caver.rpc.klay.sendRawTransaction(vt).send();
            TransactionReceipt.TransactionReceiptData vtReceiptData = receiptProcessor.waitForTransactionReceipt(vtResult.getResult());
            System.out.println("After account update value transfer transaction receipt => ");
            System.out.println(objectToString(vtReceiptData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
