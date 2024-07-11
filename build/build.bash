#!/usr/bin/env bash

FILE_NAME="${1}"

if [[ -z "${1}" ]]; then
  ./build.bash dre
  ./build.bash ir
  exit
fi

if [[ "${FILE_NAME}" != "dre" ]] && [[ "${FILE_NAME}" != "ir" ]]; then
  echo "Unknown argument"
  exit
fi

readonly BUILD_DIR=$(realpath $(dirname "${0}"))
readonly OUT_DIR=$(realpath "${BUILD_DIR}/${FILE_NAME}")
readonly ROOT_DIR=$(realpath ${BUILD_DIR}/..)
readonly W_DIR=$(realpath "${ROOT_DIR}/mods/${FILE_NAME}")
readonly COMMON_DIR=$(realpath "${ROOT_DIR}/mods/common")
readonly STARSECTOR_CORE_DIR=$(realpath "${BUILD_DIR}/../../../starsector-core")
readonly STARSECTOR_MOD_DIR=$(realpath "${STARSECTOR_CORE_DIR}/../mods/${FILE_NAME}")

readonly LUNALIB=$(realpath "${STARSECTOR_CORE_DIR}/../mods/LunaLib/jars/LunaLib.jar")

BUNDLE_NAME="unnamed_bundle"
if [[ "${FILE_NAME}" == "dre" ]]; then
  readonly BUNDLE_NAME="Dedicated Repair Equipment"
elif [[ "${FILE_NAME}" == "ir" ]]; then
  readonly BUNDLE_NAME="Improvised Refinery"
fi

# ---

rm -rf "${OUT_DIR}"
mkdir "${OUT_DIR}"
cd "${OUT_DIR}"
rm -rf tmp
mkdir tmp

readonly SOURCE_FILES="$(find ${W_DIR} -type f -name "*.java") $(find ${COMMON_DIR} -type f -name "*.java")"

javac -source 1.7 -target 1.7 -cp "${LUNALIB}":"${STARSECTOR_CORE_DIR}/starfarer.api.jar":"${STARSECTOR_CORE_DIR}/lwjgl.jar":"${STARSECTOR_CORE_DIR}/lwjgl_util.jar":"${STARSECTOR_CORE_DIR}/xstream-1.4.10.jar":"${STARSECTOR_CORE_DIR}/log4j-1.2.9.jar" ${SOURCE_FILES} -d tmp

if [[ ! $? -eq 0 ]]; then
  echo Error happened. Exiting...
  exit
fi

jar cf "${FILE_NAME}.jar" -C tmp .

if [[ ! $? -eq 0 ]]; then
  echo Error happened. Exiting...
  exit
fi

rm -rf tmp

cp "${W_DIR}/mod_info.json" "${OUT_DIR}/mod_info.json"

CSV_FILES=$(find "${W_DIR}" -type f -not -name "*.java")

cd "${W_DIR}"
for i in ${CSV_FILES}; do
  cp $(realpath -s --relative-to="${W_DIR}" "$i") "${OUT_DIR}" --parents -r
done

cp "${ROOT_DIR}/README.md" "${OUT_DIR}/README.md"

rm -rf "${STARSECTOR_MOD_DIR}"
mkdir "${STARSECTOR_MOD_DIR}"
cp -r "${BUILD_DIR}/${FILE_NAME}"/* "${STARSECTOR_MOD_DIR}"

# create release archive
7z > /dev/null

if [[ ! $? -eq 0 ]]; then
  echo 7z is not avaliable
else
  rm -f "${BUILD_DIR}/${BUNDLE_NAME}.zip"
  7z a "${BUILD_DIR}/${BUNDLE_NAME}.zip" "${OUT_DIR}" > /dev/null
  7z rn "${BUILD_DIR}/${BUNDLE_NAME}.zip" "${FILE_NAME}" "${BUNDLE_NAME}" > /dev/null
fi

echo Fin
