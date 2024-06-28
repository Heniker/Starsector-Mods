#!/usr/bin/env bash

FILE_NAME="${1}"

if [ -z "${1}" ]; then
  ./build.bash dre
  ./build.bash ir
  exit
fi

readonly BUILD_DIR=$(realpath $(dirname "${0}"))
readonly OUT_DIR=$(realpath "${BUILD_DIR}/${FILE_NAME}")
readonly W_DIR=$(realpath "${BUILD_DIR}/../mods/${FILE_NAME}")
readonly COMMON_DIR=$(realpath "${W_DIR}/../common")
readonly STARSECTOR_CORE_DIR=$(realpath "${BUILD_DIR}/../../../starsector-core")
readonly STARSECTOR_MOD_DIR=$(realpath "${STARSECTOR_CORE_DIR}/../mods/${FILE_NAME}")

# ---

rm -rf "${OUT_DIR}"
mkdir "${OUT_DIR}"
cd "${OUT_DIR}"
rm -rf tmp
mkdir tmp

SOURCE_FILES="$(find ${W_DIR} -type f -name "*.java") $(find ${COMMON_DIR} -type f -name "*.java")"

javac -source 1.7 -target 1.7 -cp "${STARSECTOR_CORE_DIR}/starfarer.api.jar":"${STARSECTOR_CORE_DIR}/lwjgl.jar":"${STARSECTOR_CORE_DIR}/lwjgl_util.jar":"${STARSECTOR_CORE_DIR}/xstream-1.4.10.jar":"${STARSECTOR_CORE_DIR}/log4j-1.2.9.jar" ${SOURCE_FILES} -d tmp

jar cf "${FILE_NAME}.jar" -C tmp .

rm -rf tmp

cd "${W_DIR}"

cp mod_info.json ${OUT_DIR}/mod_info.json

CSV_FILES=$(find ${W_DIR} -type f -not -name "*.java")

for i in $CSV_FILES; do
  cp $(realpath -s --relative-to="${W_DIR}" "$i") "${OUT_DIR}" --parents -r
done

cd "${BUILD_DIR}"
rm -rf "${STARSECTOR_MOD_DIR}"
mkdir "${STARSECTOR_MOD_DIR}"
cp -r "${FILE_NAME}"/* "${STARSECTOR_MOD_DIR}"

echo
echo Fin
