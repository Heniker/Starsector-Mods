#!/usr/bin/env bash

shopt -s extglob globstar

BUILD_DIR=$(realpath $(dirname "$0"))
W_DIR=$(realpath "$BUILD_DIR/..")
STARSECTOR_CORE_DIR=$(realpath $W_DIR/../../starsector-core)

FILE_NAME="ImprovisedRefinery"

if [ ! -d "$W_DIR" ]; then
  echo "$W_DIR" does not exist
  exit
fi

if [ ! -d "$STARSECTOR_CORE_DIR" ]; then
  echo "$STARSECTOR_CORE_DIR" does not exist
  exit
fi

if [ ! -d "$BUILD_DIR" ]; then
  echo "$BUILD_DIR" does not exist
  exit
fi

echo Dirs:
echo working - "$W_DIR"
echo starsectore-core - "$STARSECTOR_CORE_DIR"
echo build - "$BUILD_DIR"
echo
echo Javac version:
javac -version
echo

# ---

cd "$BUILD_DIR"
rm -rf dist
mkdir dist
rm -rf tmp
mkdir tmp

SOURCE_FILES="$W_DIR"/data/**/*.java

javac -source 1.7 -target 1.7 -cp "$STARSECTOR_CORE_DIR/starfarer.api.jar":"$STARSECTOR_CORE_DIR/lwjgl.jar":"$STARSECTOR_CORE_DIR/lwjgl_util.jar":"$STARSECTOR_CORE_DIR/xstream-1.4.10.jar":"$STARSECTOR_CORE_DIR/log4j-1.2.9.jar" $SOURCE_FILES -d tmp

jar cf "$FILE_NAME.jar" -C tmp .
mv "$FILE_NAME.jar" dist -f

if [ -f "$FILE_NAME.jar" ]; then
  echo "!> Move failed. Close the game before building"
  rm "$FILE_NAME.jar"
  rm -rf tmp
  exit
fi

rm -rf tmp

# optional

cd "$W_DIR"

cat mod_info.json | sed -e "s/build\/dist\///" >build/dist/mod_info.json

CSV_FILES="$W_DIR"/data/**/!(*.java)

for i in $CSV_FILES; do
  cp $(realpath -s --relative-to="$W_DIR" "$i") ./build/dist --parents -r
done

echo
echo Fin
