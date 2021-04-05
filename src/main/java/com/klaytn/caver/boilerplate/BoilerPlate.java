package com.klaytn.caver.boilerplate;

import com.klaytn.caver.Caver;
import com.klaytn.caver.account.Account;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.kct.kip17.KIP17;
import com.klaytn.caver.kct.kip7.KIP7;
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
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;

public class BoilerPlate {
    private static final String URL_NODE_API = "https://node-api.klaytnapi.com/v1/klaytn";

    // Configuration Part
    // Set your KAS access key and secretAccessKey.
    static String accessKey = "{your_accessKeyId}";
    static String secretAccessKey = "{your_secretAccessKey}";

    // static String CHAIN_ID_BAOBOB = "1001";
    // static String CHAIN_ID_CYPRESS = "8217";
    static String chainId = "1001";

    public static void main(String[] args) {
        // Build a Caver instance.
        Caver caver = setCaver(accessKey, secretAccessKey, chainId);

        // Run a test.
        test(caver);
    }

    public static void test(Caver caver) {
        String testPrivateKey = "0x{private key}";

        SingleKeyring deployerKeyring = KeyringFactory.createFromPrivateKey(testPrivateKey);
        caver.wallet.add(deployerKeyring);

        try {
            //Deploy KIP-17(NFT) token contract
            KIP17 kip17 = KIP17.deploy(caver, deployerKeyring.getAddress(), "Klaytn NFT", "KNFT");
            System.out.println("Deployed contract address : " + kip17.getContractAddress());

            //Mint a NFT token
            BigInteger tokenId = BigInteger.ONE;
            String uri = "http://test.url";
            TransactionReceipt.TransactionReceiptData mintReceiptData = kip17.mintWithTokenURI(deployerKeyring.getAddress(), tokenId, uri, new SendOptions(deployerKeyring.getAddress()));
            System.out.println("NFT mint transaction hash : " + mintReceiptData.getTransactionHash());

            //Transfer a NFT token
            TransactionReceipt.TransactionReceiptData transferReceiptData = kip17.transferFrom(deployerKeyring.getAddress(), deployerKeyring.getAddress(), tokenId, new SendOptions(deployerKeyring.getAddress()));
            System.out.println("NFT transfer transaction hash : " + transferReceiptData.getTransactionHash());

            //Burn a NFT token
            TransactionReceipt.TransactionReceiptData burnReceiptData = kip17.burn(tokenId, new SendOptions(deployerKeyring.getAddress()));
            System.out.println("NFT burn transaction hash : " + burnReceiptData.getTransactionHash());

        } catch (NoSuchMethodException | IOException | InstantiationException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | TransactionException e) {
            e.printStackTrace();
        }
    }

    private static Caver setCaver(String accessKey, String secretAccessKey, String chainID) {
        HttpService httpService = new HttpService(URL_NODE_API);
        httpService.addHeader("Authorization", Credentials.basic(accessKey, secretAccessKey));
        httpService.addHeader("x-chain-id", chainID);

        return new Caver(httpService);
    }
}
