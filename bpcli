#!/usr/bin/env bash
set -e
export BPCLI_WORKDIR=$(cd $(dirname $0) && pwd)

cli_help() {
  cli_name=${0##*/}
  echo "
$cli_name
BoilerPlate CLI for caver-java
Usage: $cli_name <Commands>

Commands:
   <common_architecture_layer>/<scenario>
   -h        Help
   --help        Help

Usage examples:
-------------------------------------------------
$ ./$cli_name account/update_account_with_account_key_public
$ ./$cli_name account/update_account_with_account_key_role_based
$ ./$cli_name account/update_account_with_account_key_weighted_multisig
$ ./$cli_name contract/deploy_smart_contract
$ ./$cli_name contract/execute_smart_contract
$ ./$cli_name contract/fee_delegation
"
  exit 1
}

case "$1" in
  account/update_account_with_account_key_public)
    ./gradlew account:update_account_with_account_key_public
    ;;
  account/update_account_with_account_key_role_based)
    ./gradlew account:update_account_with_account_key_role_based
    ;;
  account/update_account_with_account_key_weighted_multisig)
    ./gradlew account:update_account_with_account_key_weighted_multisig
    ;;
  contract/deploy_smart_contract)
    ./gradlew contract:deploy_smart_contract
    ;;
  contract/execute_smart_contract)
    ./gradlew contract:execute_smart_contract
    ;;
  rpc/klay_get_block_receipts)
    ./gradlew rpc:klay_get_block_receipts
    ;;
  transaction:send_klay)
    ./gradlew transaction:send_klay
    ;;
  -h|--help|*)
    cli_help
    ;;
esac