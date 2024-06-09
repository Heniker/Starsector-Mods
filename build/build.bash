#!/usr/bin/env bash

W_DIR=$(realpath ../$(dirname $0))
STARSECTOR_CORE_DIR=$(realpath $W_DIR/../../starsector-core)

if [ ! -d "$W_DIR" ]; then
  echo "$W_DIR" does not exist
  return
fi

if [ ! -d "$STARSECTOR_CORE_DIR" ]; then
  echo "$STARSECTOR_CORE_DIR" does not exist
  return
fi 

echo Dirs:
echo Working - "$W_DIR"
echo starsectore-core - "$STARSECTOR_CORE_DIR"
echo
echo Javac version:
javac -version

cd "$W_DIR"

mkdir tmp -p
javac -cp \
"$STARSECTOR_CORE_DIR/starfarer.api.jar":\
"$STARSECTOR_CORE_DIR/lwjgl.jar":\
"$STARSECTOR_CORE_DIR/lwjgl_util.jar":\
"$STARSECTOR_CORE_DIR/xstream-1.4.10.jar":\
"$STARSECTOR_CORE_DIR/log4j-1.2.9.jar"\
 data/**/*.java -d tmp

echo
echo
echo Success