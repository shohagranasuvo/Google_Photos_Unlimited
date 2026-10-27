#!/usr/bin/env bash
# verify_exported_providers.sh
# Verifies on a connected Android device that internal content providers are not accessible across app boundaries.

set -euo pipefail

PACKAGE="${1:-app.revanced.android.photos}"

echo "[*] Testing IPC Content Provider boundaries for: $PACKAGE"

if ! command -v adb >/dev/null 2>&1; then
    echo "[-] Error: 'adb' tool not found in PATH." >&2
    exit 1
fi

DEVICE_COUNT=$(adb devices | grep -v "List" | grep "device$" | wc -l)
if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "[-] Error: No authorized ADB device connected." >&2
    exit 1
fi

PROVIDERS=(
    "content://${PACKAGE}.freeable_space"
    "content://${PACKAGE}.partnercontentprovider"
)

ALL_SECURE=true

for URI in "${PROVIDERS[@]}"; do
    echo "[*] Querying URI: $URI"
    RESULT=$(adb shell content query --uri "$URI" 2>&1 || true)
    if echo "$RESULT" | grep -qi "SecurityException"; then
        echo "[+] Protected: Inter-app query blocked by SecurityException."
    elif echo "$RESULT" | grep -qi "not exported"; then
        echo "[+] Protected: Provider is not exported."
    else
        echo "[-] WARNING: Unexpected query response: $RESULT"
        ALL_SECURE=false
    fi
done

if [ "$ALL_SECURE" = true ]; then
    echo "[+] All audited content providers are strictly isolated from third-party apps."
else
    echo "[-] Potential IPC exposure detected. Inspect manifest declarations."
    exit 2
fi
