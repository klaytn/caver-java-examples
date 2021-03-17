# caver-java-boilerplate

This project is a caver-java boilerplate project.

## How to use
To connet Klaytn EN, this project used KAS's [NODE API](https://refs.klaytnapi.com/en/node/latest).

If you haven't signed up for [KAS](https://www.klaytnapi.com/ko/landing/main) yet, you need to sign up and get an KAS access key and secret key.
- Set your access key and secret key in BoilerPlate class.
- Copy and paste a test code in test() method.  
- Before running a Main class, You have to decide which network to connect to.
  - If you want to connect testnet, you must use a `connectTestnet()` method.
  - If you want to connect mainnet, you must use a `connectMainnet()` method.
- Run a main method.
  - If you want to execute main method through console, you can you `./gradlew runMain`
     


