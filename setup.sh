#!/usr/bin/env bash

PROJECT_ROOT_DIR=$PWD
ASSETS_DIR=$PROJECT_ROOT_DIR/assets
BOILER_PLATE_BUILD_GRADLE=$ASSETS_DIR/build.gradle
BOILER_PLATE_TEMPLATE=$ASSETS_DIR/BoilerPlate.java
NEW_SCENARIO=$1
NEW_SCENARIO_SRC_ROOT=$NEW_SCENARIO/src/main/java

## Tokenize $NEW_SCENARIO
# account/update_account_with_account_key_weighted_multisig | COMMON_ARCHITECTURE_LAYER: account,
ARR_IN=(${NEW_SCENARIO//\// })
ARR_LEN=${#ARR_IN[@]}
COMMON_ARCHITECTURE_LAYER_NAME=${ARR_IN[0]}
SCENARIO_NAME=${ARR_IN[$ARR_LEN - 1]}

echo "Create project structure for $NEW_SCENARIO\n"

mkdir -p $NEW_SCENARIO

gradle init --type basic --dsl groovy --project-name=$SCENARIO_NAME --project-dir=$NEW_SCENARIO
mkdir -p $NEW_SCENARIO_SRC_ROOT
cp $BOILER_PLATE_BUILD_GRADLE $NEW_SCENARIO
cp $BOILER_PLATE_TEMPLATE $NEW_SCENARIO_SRC_ROOT