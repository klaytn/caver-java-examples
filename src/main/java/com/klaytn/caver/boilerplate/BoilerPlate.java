package com.klaytn.caver.boilerplate;

import com.klaytn.caver.Caver;
import okhttp3.Credentials;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class BoilerPlate {
    private static final String URL_NODE_API = "https://node-api.klaytnapi.com/v1/klaytn";

    //Set a your KAS access key, secretAccessKey.
    static String accessKey = "";
    static String secretAccessKey = "";

    static String CHAIN_ID_BAOBOB = "1001";
    static String CHAIN_ID_CYPRESS = "8217";

    public static void main(String[] args) {
        //Build a Caver instance.
        Caver caver = connectTestnet(accessKey, secretAccessKey);
//        Caver caver = connectMainnet(accessKey, secretAccessKey);

        //test!
        test(caver);
    }

    public static void test(Caver caver) {
        //Copy and pasted test code here!
        try {
            System.out.println(caver.rpc.klay.getBlockNumber().send().getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Caver connectTestnet(String accessKey, String secretAccessKey) {
        return setCaver(accessKey, secretAccessKey, CHAIN_ID_BAOBOB);
    }

    private static Caver connectMainnet(String accessKey, String secretAccessKey, String chainID) {
        return setCaver(accessKey, secretAccessKey, CHAIN_ID_CYPRESS);
    }

    private static Caver setCaver(String accessKey, String secretAccessKey, String chainID) {
        HttpService httpService = new HttpService(URL_NODE_API);
        httpService.addHeader("Authorization", Credentials.basic(accessKey, secretAccessKey));
        httpService.addHeader("x-chain-id", chainID);

        return new Caver(httpService);
    }
}
