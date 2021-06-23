# caver-java-boilerplate

caver-java boilerplate 프로젝트입니다.

## 디렉토리 구조
BoilerPlate 프로젝트는 SDK를 사용하는 분들이 좀 더 쉽고 빠르게 SDK를 사용하실 수 있게끔 도움을 드리기 위한 프로젝트입니다. 본 프로젝트에서는 "Multiple Main"를 사용합니다.
**"Multiple Main"** 이란 디렉토리 별로 main 메서드를 가진 `BoilerPlate.java` 파일이 하나씩 존재하는 구조로, BoilerPlate를 사용하시는 분들이 코드를 그대로 가져다가 바로 실행하실 수 있도록 고안한 구조입니다.  

## 사용법
Caver SDK는 [Klaytn EndpointNode(이하 Klaytn EN)](https://docs.klaytn.com/node/endpoint-node)와 함께 사용할 수 있도록 만들어져 있습니다.
EN을 직접 운영하지 않아도 본 BoilerPlate에 있는 예제코드들을 사용하실 수 있도록, caver-java-boilerplate 프로젝트는 KAS의 [NODE API](https://refs.klaytnapi.com/en/node/latest)를 사용합니다.

KAS를 사용하기 위해서는 [KAS](https://www.klaytnapi.com/ko/landing/main)에 가입하셔야 합니다. 가입하시면 `access key id`와 `secret access key`를 받아보실 수 
- 본인 계정으로 발급하신 `access key id`와 `secret access key` 그리고 예제 코드를 테스트하고자 하는 `chain id`

If you haven't signed up for [KAS](https://www.klaytnapi.com/ko/landing/main) yet, you need to sign up and get an KAS access key and secret key.
- Set your access key, secret key, and chain id in BoilerPlate class.
- Copy and paste a test code in `test()` method.
- Run a main method.
    - If you want to execute main method through terminal, you can execute `main()` method through `./gradlew runMain` command.

-

## License
caver-java-boilerplate is released under the [MIT license](./LICENSE).

```
MIT License

Copyright (c) 2021 caver-java boilerplate Authors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

