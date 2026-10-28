#!/usr/bin/env python3
"""
Google Photos Unlimited Storage & Security Hardening Patcher
============================================================
This tool applies security hardening (SEC-01 cleartext traffic, SEC-02 ADB backup,
SEC-03 unexported content providers, SEC-04 silent startup logger) and Pixel 1 spoofing
to an Android Google Photos base APK.

Usage:
    python3 patch.py <base_photos.apk> [output_remediated.apk]
"""

import sys
import os
import zipfile
import shutil
import subprocess

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
PAYLOAD_DIR = os.path.join(SCRIPT_DIR, "payload")

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 patch.py <base_photos.apk> [output_remediated.apk]")
        sys.exit(1)

    input_apk = os.path.abspath(sys.argv[1])
    output_apk = os.path.abspath(sys.argv[2]) if len(sys.argv) > 2 else os.path.join(os.getcwd(), "GooglePhotos_Unlimited_Remediated.apk")

    if not os.path.isfile(input_apk):
        print(f"[-] Error: Input APK not found: {input_apk}")
        sys.exit(1)

    print(f"[*] Input APK:  {input_apk}")
    print(f"[*] Output APK: {output_apk}")

    manifest_patch = os.path.join(PAYLOAD_DIR, "AndroidManifest.xml")
    classes7_patch = os.path.join(PAYLOAD_DIR, "classes7.dex")

    if not os.path.isfile(manifest_patch) or not os.path.isfile(classes7_patch):
        print(f"[-] Error: Missing payload files in: {PAYLOAD_DIR}")
        sys.exit(1)

    temp_unaligned = output_apk + ".tmp"
    if os.path.exists(temp_unaligned):
        os.remove(temp_unaligned)

    print("[*] Applying security modifications and packaging APK...")
    with zipfile.ZipFile(input_apk, 'r') as zin, zipfile.ZipFile(temp_unaligned, 'w') as zout:
        for item in zin.infolist():
            # Skip old signature files
            if item.filename.startswith("META-INF/") and item.filename.endswith((".SF", ".RSA", ".DSA", ".EC", ".MF")):
                continue

            if item.filename == "AndroidManifest.xml":
                print("    [+] Injecting remediated AndroidManifest.xml (SEC-01, SEC-02, SEC-03)")
                with open(manifest_patch, "rb") as f:
                    data = f.read()
                zout.writestr(item.filename, data, compress_type=zipfile.ZIP_DEFLATED)
            elif item.filename == "classes7.dex":
                print("    [+] Injecting patched classes7.dex (SEC-04 Logger hardening)")
                with open(classes7_patch, "rb") as f:
                    data = f.read()
                zout.writestr(item.filename, data, compress_type=zipfile.ZIP_DEFLATED)
            else:
                data = zin.read(item.filename)
                zout.writestr(item, data)

    print("[+] Packaging complete.")

    # Alignment and signing
    zipalign_bin = shutil.which("zipalign")
    apksigner_bin = shutil.which("apksigner")

    if not zipalign_bin:
        # Check standard android-sdk path
        sdk_candidate = os.path.expanduser("~/android-sdk/build-tools/35.0.0/zipalign")
        if os.path.isfile(sdk_candidate):
            zipalign_bin = sdk_candidate

    if not apksigner_bin:
        sdk_candidate = os.path.expanduser("~/android-sdk/build-tools/35.0.0/apksigner")
        if os.path.isfile(sdk_candidate):
            apksigner_bin = sdk_candidate

    if zipalign_bin:
        print(f"[*] Running 4-byte zipalign via: {zipalign_bin}")
        if os.path.exists(output_apk):
            os.remove(output_apk)
        ret = subprocess.run([zipalign_bin, "-p", "-f", "4", temp_unaligned, output_apk])
        if ret.returncode == 0:
            os.remove(temp_unaligned)
            print("[+] Zipalign successful.")
        else:
            print("[-] Warning: Zipalign failed; retaining unaligned package.")
            os.rename(temp_unaligned, output_apk)
    else:
        print("[!] Note: 'zipalign' not detected; outputting unaligned APK.")
        os.rename(temp_unaligned, output_apk)

    # Check for signing key
    keystore_path = os.path.join(SCRIPT_DIR, "maintenance.keystore")
    if not os.path.isfile(keystore_path):
        keytool_bin = shutil.which("keytool")
        if keytool_bin:
            print("[*] Generating local self-signed maintenance key...")
            subprocess.run([
                keytool_bin, "-genkeypair", "-v",
                "-keystore", keystore_path,
                "-alias", "photos_remediated",
                "-keyalg", "RSA", "-keysize", "2048", "-validity", "10000",
                "-storepass", "photos123", "-keypass", "photos123",
                "-dname", "CN=PhotosRemediated, OU=Security, O=Research, C=US"
            ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    if apksigner_bin and os.path.isfile(keystore_path):
        print(f"[*] Signing APK via: {apksigner_bin}")
        ret = subprocess.run([
            apksigner_bin, "sign",
            "--ks", keystore_path,
            "--ks-key-alias", "photos_remediated",
            "--ks-pass", "pass:photos123",
            "--key-pass", "pass:photos123",
            output_apk
        ])
        if ret.returncode == 0:
            print("[+] APK successfully signed!")
            subprocess.run([apksigner_bin, "verify", output_apk])
    else:
        print("[!] Note: To install, sign the output APK using apksigner or an on-device signer.")

    print(f"\n[🎉] Done! Remediated APK created at:\n     {output_apk}")

if __name__ == "__main__":
    main()
