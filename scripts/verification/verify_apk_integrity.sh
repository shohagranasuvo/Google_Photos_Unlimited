#!/usr/bin/env bash
# verify_apk_integrity.sh
# Verifies the cryptographic digest, alignment, and signing certificates of an Android APK.

set -euo pipefail

if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <path-to-apk> [expected-sha256]"
    exit 1
fi

APK_PATH="$1"
EXPECTED_SHA256="${2:-}"

if [ ! -f "$APK_PATH" ]; then
    echo "[-] Error: File not found: $APK_PATH" >&2
    exit 1
fi

echo "[*] Calculating SHA-256 digest for: $APK_PATH"
ACTUAL_SHA256=$(sha256sum "$APK_PATH" | awk '{print $1}')
echo "    SHA-256: $ACTUAL_SHA256"

if [ -n "$EXPECTED_SHA256" ]; then
    if [ "$ACTUAL_SHA256" = "$EXPECTED_SHA256" ]; then
        echo "[+] SHA-256 matches expected digest."
    else
        echo "[-] ERROR: Digest mismatch!" >&2
        echo "    Expected: $EXPECTED_SHA256" >&2
        echo "    Actual:   $ACTUAL_SHA256" >&2
        exit 2
    fi
fi

if command -v zipalign >/dev/null 2>&1; then
    echo "[*] Checking 4-byte zip alignment..."
    if zipalign -c -v 4 "$APK_PATH" >/dev/null 2>&1; then
        echo "[+] 4-byte alignment verification: PASSED"
    else
        echo "[-] 4-byte alignment verification: FAILED"
    fi
else
    echo "[!] Warning: 'zipalign' not installed; skipping alignment check."
fi

if command -v apksigner >/dev/null 2>&1; then
    echo "[*] Inspecting APK signing scheme..."
    apksigner verify --verbose "$APK_PATH" || true
else
    echo "[!] Warning: 'apksigner' not installed; skipping cryptographic signature check."
fi

echo "[+] Integrity check completed."
