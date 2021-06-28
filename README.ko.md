# caver-java-boilerplate
**caver-java-boilerplate** 프로젝트는 SDK를 사용하는 분들이 좀 더 쉽고 빠르게 SDK를 사용하실 수 있게끔 도움을 드리기 위한 프로젝트입니다.
다양한 상황에 맞는 예제코드를 직접 확인하시고 실행해보실 수 있습니다.

## 디렉토리 구조
본 프로젝트에서는 "Multiple Main" 구조를 사용합니다. **"Multiple Main"** 이란 패키지 별로 main 메서드를 가진 `BoilerPlate.java` 파일이 하나씩 존재하는 구조로, BoilerPlate를 사용하시는 분들이 코드를 그대로 가져다가 바로 실행하실 수 있도록 고안한 구조입니다.  

## 선행되어야 하는 작업
Caver SDK는 [Klaytn EndpointNode(이하 Klaytn EN)](https://docs.klaytn.com/node/endpoint-node)와 함께 사용할 수 있도록 만들어져 있습니다.
EN을 직접 운영하지 않아도 본 BoilerPlate에 있는 예제코드들을 사용하실 수 있도록, caver-java-boilerplate 프로젝트는 KAS의 [NODE API](https://refs.klaytnapi.com/en/node/latest)를 사용합니다.

KAS를 사용하기 위해서는 [KAS](https://www.klaytnapi.com/ko/landing/main)에 가입하셔야 합니다. 가입하시면 BoilerPlate 코드 실행에 필요한 `access key id`와 `secret access key`를 발급받으실 수 있습니다.
발급받은 `access key id`와 `secret access key`는 BoilerPlate 코드에 사용됩니다.

## 사용법
> 본 caver-java-boiler-plate 프로젝트를 그대로 클론해서 사용하시는 방법입니다.
1. 이 프로젝트를 클론합니다. `$ git clone https://github.com/klaytn/caver-java-boilerplate.git`
2. [KAS](https://www.klaytnapi.com/ko/landing/main)에서 본인의 계정으로 발급받은 credential 정보와 실습에 사용하실 Klaytn EOA 계정들의 값을 설정합니다. (테스트 용도의 계정 사용을 권장드립니다.)
   * 옵션 1: `caver-java-boilerplate/.env` 파일의 내용을 채워주도록 합니다. 이 파일에 채우신 내용은 모든 시나리오에 공통 적용됩니다. 자세한 내용은 각 `BoilerPlate.java` 파일의 `loadEnv` 메서드를 참고해주세요.
   * 옵션 2: 원하는 시나리오에 해당하는 `BoilerPlate.java` 파일을 열고 BoilerPlate class 밑에 정의되어 있는 `private static`으로 선언된 변수들의 값을 채워줍니다.
3. 프로젝트 루트 디렉토리(`caver-java-boilerplate/`)에서 원하는 시나리오가 속한 Layer와 시나리오의 이름을 확인하신 뒤 `bpcli`를 활용하여 실행시켜줍니다. 예시: `$ ./bpcli account/update_account_with_account_key_role_based` 

## License
**caver-java-boilerplate** is released under the [MIT license](./LICENSE).

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

