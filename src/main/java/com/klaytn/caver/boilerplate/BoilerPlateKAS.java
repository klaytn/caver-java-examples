package com.klaytn.caver.boilerplate;


import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import org.web3j.protocol.exceptions.TransactionException;
import xyz.groundx.caver_ext_kas.CaverExtKAS;
import xyz.groundx.caver_ext_kas.rest_client.io.swagger.client.ApiException;
import xyz.groundx.caver_ext_kas.rest_client.io.swagger.client.api.kip17.model.Kip17TransactionStatusResponse;
import xyz.groundx.caver_ext_kas.rest_client.io.swagger.client.api.wallet.model.Account;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

public class BoilerPlateKAS {
    // Configuration Part
    // Set your KAS access key and secretAccessKey.
    static String accessKey = "{your_accessKeyId}";
    static String secretAccessKey = "{your_secretAccessKey}";

    // static String CHAIN_ID_BAOBOB = "1001";
    // static String CHAIN_ID_CYPRESS = "8217";
    static String chainId = "1001";

    public static void main(String[] args) {
        CaverExtKAS caverExtKAS = setCaverExtKAS(accessKey, secretAccessKey, chainId);

        test(caverExtKAS);
    }

    public static void test(CaverExtKAS caver) {
        String contractAlias = "kip17-" + new Date().getTime();
        caver.kas.kip17.getApiClient().setDebugging(true);
        caver.kas.wallet.getApiClient().setDebugging(true);

        TransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(caver, 1000, 15);

        try {
            Account account = caver.kas.wallet.createAccount();

            //Deploy KIP-17(NFT) token contract
            Kip17TransactionStatusResponse deployedResponse = caver.kas.kip17.deploy("Klaytn NFT", "NFT", contractAlias);
            TransactionReceipt.TransactionReceiptData deployedReceiptData = processor.waitForTransactionReceipt(deployedResponse.getTransactionHash());
            System.out.println("---------------> Deployed contract address : " + deployedReceiptData.getContractAddress());

            Thread.sleep(2000);

            //Mint a NFT token
            BigInteger tokenId = BigInteger.ONE;
            String uri = "http://test.url";
            Kip17TransactionStatusResponse mintedResponse = caver.kas.kip17.mint(contractAlias, account.getAddress(), tokenId, uri);
            TransactionReceipt.TransactionReceiptData mintReceiptData = processor.waitForTransactionReceipt(mintedResponse.getTransactionHash());
            System.out.println("---------------> NFT mint transaction hash : " + mintReceiptData.getContractAddress());

            Thread.sleep(2000);

            //Transfer a NFT token
            Kip17TransactionStatusResponse transferResponse = caver.kas.kip17.transfer(contractAlias, account.getAddress(), account.getAddress(), account.getAddress(), tokenId);
            TransactionReceipt.TransactionReceiptData transferReceiptData = processor.waitForTransactionReceipt(transferResponse.getTransactionHash());
            System.out.println("---------------> NFT transfer transaction hash : " + transferReceiptData.getTransactionHash());

            Thread.sleep(2000);

            //Burn a NFT token
            Kip17TransactionStatusResponse burnResponse = caver.kas.kip17.burn(contractAlias, account.getAddress(), tokenId);
            TransactionReceipt.TransactionReceiptData burnReceiptData = processor.waitForTransactionReceipt(burnResponse.getTransactionHash());
            System.out.println("---------------> NFT burn transaction hash : " + burnReceiptData.getTransactionHash());
        } catch (ApiException | TransactionException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static CaverExtKAS setCaverExtKAS(String accessKey, String secretAccessKey, String chainID) {
        CaverExtKAS caverExtKAS = new CaverExtKAS(chainID, accessKey, secretAccessKey);
        caverExtKAS.initKIP17API(chainID, accessKey, secretAccessKey, "https://kip17-api.klaytnapi.com");
        return caverExtKAS;
    }
}
