#!/bin/bash
#
# Generates a debug keystore for SymbolKeyboard development.
# Run this script once after cloning the repository.
#

set -e

KEYSTORE_DIR="${HOME}/.android"
KEYSTORE_PATH="${KEYSTORE_DIR}/debug.keystore"
KEYSTORE_PASSWORD="android"
KEY_ALIAS="androiddebugkey"
KEY_PASSWORD="android"
VALIDITY_DAYS=10000

echo "=== SymbolKeyboard Debug Keystore Generator ==="

if [ -f "${KEYSTORE_PATH}" ]; then
    echo "Debug keystore already exists at ${KEYSTORE_PATH}"
    echo "  Alias: ${KEY_ALIAS}"
    echo "  Store password: ${KEYSTORE_PASSWORD}"
    echo "  Key password: ${KEY_PASSWORD}"
    echo ""
    echo "To use a different location, set the following in local.properties:"
    echo "  keystore.path=${KEYSTORE_PATH}"
    echo "  keystore.password=${KEYSTORE_PASSWORD}"
    echo "  key.alias=${KEY_ALIAS}"
    echo "  key.password=${KEY_PASSWORD}"
    exit 0
fi

echo "Creating debug keystore at ${KEYSTORE_PATH}..."

mkdir -p "${KEYSTORE_DIR}"

keytool -genkey -v \
    -keystore "${KEYSTORE_PATH}" \
    -alias "${KEY_ALIAS}" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "${VALIDITY_DAYS}" \
    -storepass "${KEYSTORE_PASSWORD}" \
    -keypass "${KEY_PASSWORD}" \
    -dname "CN=Android Debug, OU=Development, O=SymbolKeyboard, L=Unknown, ST=Unknown, C=US" \
    2>/dev/null

if [ $? -eq 0 ]; then
    echo "Debug keystore created successfully!"
    echo "  Location: ${KEYSTORE_PATH}"
    echo "  Alias: ${KEY_ALIAS}"
    echo "  Store password: ${KEYSTORE_PASSWORD}"
    echo "  Key password: ${KEY_PASSWORD}"
    echo ""
    echo "Add the following to local.properties:"
    echo "  keystore.path=${KEYSTORE_PATH}"
    echo "  keystore.password=${KEYSTORE_PASSWORD}"
    echo "  key.alias=${KEY_ALIAS}"
    echo "  key.password=${KEY_PASSWORD}"
else
    echo "Error: Failed to create debug keystore."
    exit 1
fi
