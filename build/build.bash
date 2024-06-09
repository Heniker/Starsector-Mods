#!/usr/bin/env bash

W_DIR=$(realpath ../$(dirname $0))
STARSECTOR_CORE_DIR=$(realpath $W_DIR/../../starsector-core)
BUILD_DIR=$(realpath $(dirname "$0"))

if [ ! -d "$W_DIR" ]; then
  echo "$W_DIR" does not exist
  return
fi

if [ ! -d "$STARSECTOR_CORE_DIR" ]; then
  echo "$STARSECTOR_CORE_DIR" does not exist
  return
fi 

if [ ! -d "$BUILD_DIR" ]; then
  echo "$BUILD_DIR" does not exist
  return
fi 

echo Dirs:
echo working - "$W_DIR"
echo starsectore-core - "$STARSECTOR_CORE_DIR"
echo build - "$BUILD_DIR"
echo
echo Javac version:
javac -version

# ---

cd "$BUILD_DIR"
rm -rf dist
mkdir dist
rm -rf tmp
mkdir tmp

SOURCE_FILES="$W_DIR"/data/*.java\ "$W_DIR"/data/**/*.java

javac -source 1.7 -target 1.7 -cp "$STARSECTOR_CORE_DIR/starfarer.api.jar":"$STARSECTOR_CORE_DIR/lwjgl.jar":"$STARSECTOR_CORE_DIR/lwjgl_util.jar":"$STARSECTOR_CORE_DIR/xstream-1.4.10.jar":"$STARSECTOR_CORE_DIR/log4j-1.2.9.jar" $SOURCE_FILES -d tmp

jar cf ImprovisedRefinery.jar -C tmp .

cd "$BUILD_DIR"
mv ImprovisedRefinery.jar dist -f

rm -rf tmp

echo
echo Fin