# caver-java-boilerplate
> caver-java boilerplate 프로젝트입니다. 다양한 상황에 맞는 예제코드를 직접 확인하시고 실행해보실 수 있습니다.

## 디렉토리 구조
BoilerPlate 프로젝트는 SDK를 사용하는 분들이 좀 더 쉽고 빠르게 SDK를 사용하실 수 있게끔 도움을 드리기 위한 프로젝트입니다. 본 프로젝트에서는 "Multiple Main"를 사용합니다.
**"Multiple Main"** 이란 디렉토리 별로 main 메서드를 가진 `BoilerPlate.java` 파일이 하나씩 존재하는 구조로, BoilerPlate를 사용하시는 분들이 코드를 그대로 가져다가 바로 실행하실 수 있도록 고안한 구조입니다.  

## 선행되어야 하는 작업
Caver SDK는 [Klaytn EndpointNode(이하 Klaytn EN)](https://docs.klaytn.com/node/endpoint-node)와 함께 사용할 수 있도록 만들어져 있습니다.
EN을 직접 운영하지 않아도 본 BoilerPlate에 있는 예제코드들을 사용하실 수 있도록, caver-java-boilerplate 프로젝트는 KAS의 [NODE API](https://refs.klaytnapi.com/en/node/latest)를 사용합니다.

KAS를 사용하기 위해서는 [KAS](https://www.klaytnapi.com/ko/landing/main)에 가입하셔야 합니다. 가입하시면 BoilerPlate 코드 실행에 필요한 `access key id`와 `secret access key`를 발급받으실 수 있습니다.
발급받은 `access key id`와 `secret access key`는 BoilerPlate 코드에 사용됩니다.

## 사용법
### 선택지 1: 프로젝트를 클론해서 그대로 활용하는 방법 (추천)
> 본 caver-java-boiler-plate 프로젝트를 그대로 클론해서 사용하시는 방법입니다.
1. 이 프로젝트를 클론합니다. `$ git clone https://github.com/klaytn/caver-java-boilerplate.git`
2. 원하는 시나리오의 BoilerPlate 소스코드를 에디터로 열고 `accessKeyId`, `secretAccessKey`, `chainId` 변수의 값을 본인 계정으로 발급받은 정보로 변경해줍니다.
3. 프로젝트 루트 디렉토리(`caver-java-boilerplate/`)에서 원하는 시나리오의 이름을 복사 하신 뒤 `bpcli`를 활용하여 실행시켜줍니다. 예시: `$ ./bpcli update_account_with_account_key_role_based`

### 선택지 2: 자신만의 Gradle Project를 생성한 뒤 원하는 시나리오에 해당하는 BoilerPlate 코드만 실행하는 방법
> 자신만의 새 Gradle 프로젝트를 생성하고 그 프로젝트에 BoilerProject 코드를 복붙하셔서 실행하는 방법입니다.
1. 새 Gradle 프로젝트를 생성합니다. 
2. caver-java-boilerplate 프로젝트의 `build.gradle` 파일의 내용을 1번에서 새로 생성한 프로젝트의 `build.gradle`에 복사 붙여넣기 합니다.
3. 1번에서 새로 생성한 프로젝트의 `src > main > java`에 BoilerPlate.java 파일을 생성합니다.
4. caver-java-boilerplate 프로젝트의 코드 중 원하는 시나리오에 해당하는 BoilerPlate 소스코드를 복사하신 뒤 2번에서 생성한 `src > main > java > BoilerPlate.java`파일에 복사 붙여넣기를 합니다.
5. 붙여넣은 내용 중 맨 위에 위치한 package 구문을 삭제하도록 합니다.
6. 붙여넣은 코드 중 `accessKeyId`와 `secretAccessKey`는 KAS에서 발급받으신 본인의 계정 정보를 넣어주셔야 하고 `chainId`는 테스트하고자 하는 체인의 아이디(Baobab TestNet의 아이디 `1001` 또는 Cypress MainNet의 아이디 `8217`)를 넣어주시면 됩니다.
7. 그 후 해당 프로젝트 루트 디렉토리에서 `./gradlew run`을 실행하시면 됩니다.

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

