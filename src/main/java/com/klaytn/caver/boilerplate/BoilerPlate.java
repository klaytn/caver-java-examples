package com.klaytn.caver.boilerplate;

import com.klaytn.caver.Caver;
import com.klaytn.caver.account.Account;
import com.klaytn.caver.methods.response.AccountKey;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.AccountUpdate;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import okhttp3.Credentials;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

public class BoilerPlate {
    private static final String URL_NODE_API = "https://node-api.klaytnapi.com/v1/klaytn";

    //Set a your KAS access key, secretAccessKey.
    static String accessKey = "";
    static String secretAccessKey = "";

    static String CHAIN_ID_BAOBOB = "1001";
    static String CHAIN_ID_CYPRESS = "8217";

    public static void main(String[] args) {
        //build a Caver instance.
        Caver caver = connectTestnet(accessKey, secretAccessKey);

        //run a test.
        test(caver);
    }

    public static void test(Caver caver) {
        String testAddress = "0x{address}";
        String testPrivateKey = "0x{private key}";

        SingleKeyring keyring = (SingleKeyring) KeyringFactory.create(testAddress, testPrivateKey);
        caver.wallet.add(keyring);

        String newKey = KeyringFactory.generateSingleKey();
        SingleKeyring newKeyring = KeyringFactory.create(testAddress, newKey);
        Account account = newKeyring.toAccount();

        AccountUpdate accountUpdate = new AccountUpdate.Builder()
                .setKlaytnCall(caver.rpc.klay)
                .setFrom(testAddress)
                .setAccount(account)
                .setGas(BigInteger.valueOf(50000))
                .build();

        try {
            caver.wallet.sign(testAddress, accountUpdate);
            Bytes32 sendResult = caver.rpc.klay.sendRawTransaction(accountUpdate).send();

            if(sendResult.hasError()) {
                //do something to handle error
                throw new TransactionException(sendResult.getError().getMessage());
            }
            String txHash = sendResult.getResult();

            TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
            TransactionReceipt.TransactionReceiptData receiptData = receiptProcessor.waitForTransactionReceipt(txHash);

            AccountKey accountKey = caver.rpc.klay.getAccountKey(testAddress).send();
            System.out.println("Account Key Type: " + accountKey.getResult().getType());


            keyring = (SingleKeyring)caver.wallet.updateKeyring(newKeyring);

            ValueTransfer vt = new ValueTransfer.Builder()
                    .setKlaytnCall(caver.rpc.getKlay())
                    .setFrom(testAddress)
                    .setTo(testAddress)
                    .setValue(BigInteger.valueOf(1))
                    .setGas(BigInteger.valueOf(25000))
                    .build();

            caver.wallet.sign(testAddress, vt);

            Bytes32 vtResult = caver.rpc.klay.sendRawTransaction(vt).send();
            TransactionReceipt.TransactionReceiptData vtReceiptData = receiptProcessor.waitForTransactionReceipt(vtResult.getResult());
            System.out.println(vtReceiptData.getStatus());

        } catch (IOException | TransactionException e) {
            // do something to handle exception.
            e.printStackTrace();
        }
    }

    private static Caver connectTestnet(String accessKey, String secretAccessKey) {
        return setCaver(accessKey, secretAccessKey, CHAIN_ID_BAOBOB);
    }

    private static Caver connectMainnet(String accessKey, String secretAccessKey) {
        return setCaver(accessKey, secretAccessKey, CHAIN_ID_CYPRESS);
    }

    private static Caver setCaver(String accessKey, String secretAccessKey, String chainID) {
        HttpService httpService = new HttpService(URL_NODE_API);
        httpService.addHeader("Authorization", Credentials.basic(accessKey, secretAccessKey));
        httpService.addHeader("x-chain-id", chainID);

        return new Caver(httpService);
    }
}
