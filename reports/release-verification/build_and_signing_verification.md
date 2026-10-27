# Build & Signing Report: ReVanced Google Photos Remediated APK

**Project**: `revanced_photos_maintenance/`  
**Output Artifact**: `build/revanced_photos_remediated.apk`  
**Date**: 2026-10-28  

---

## 1. Final Artifact Specifications

- **Absolute Path**: `./build/revanced_photos_remediated.apk`
- **File Size**: `173,785,165` bytes (~165.7 MB)
- **SHA-256 Checksum**:  
  `28a47789732c55a60f37d260d4b583ee0ca01657fa44ee5904a33caac14beae5`
- **SHA-1 Checksum**:  
  `51069502931d871755a5b51c14cb5daef30fae3b`
- **Package Identity**: `app.revanced.android.photos`
- **Version Name**: `7.45.0.807274941` (`versionCode: 50709986`)
- **SDK Target**: `minSdkVersion: 23` | `targetSdkVersion: 36`
- **Native Architecture**: `arm64-v8a`

---

## 2. Build Pipeline & Toolchain Execution

1. **Smali Compilation**:
   - Tool: `apktool` (version `2.7.0`)
   - Smali directories (`smali/` through `smali_classes8/`, including patched `Logger.smali` in `classes7.dex`) recompiled to Dalvik executable files (`classes.dex` through `classes8.dex`).
2. **Binary Manifest Patching**:
   - Tool: Python AXML chunk encoder
   - Remediated `android:usesCleartextTraffic`, `android:allowBackup`, `android:restoreAnyVersion`, and provider `android:exported` flags directly in the binary chunk tables.
3. **Archive Assembly**:
   - Reassembled the package archive containing the remediated binary manifest, freshly compiled DEX files, and untouched original `resources.arsc`, `res/`, `assets/`, and `lib/arm64-v8a/` binaries.
4. **Byte Alignment**:
   - Tool: `/home/shohag-rana/android-sdk/build-tools/35.0.0/zipalign`
   - Command: `zipalign -p -f -v 4 <unaligned> revanced_photos_remediated.apk`
   - Verification: `zipalign -c -v 4 revanced_photos_remediated.apk` $\rightarrow$ **Verification successful** (4-byte page aligned for all uncompressed shared libraries).
5. **Cryptographic Signing**:
   - Tool: `/home/shohag-rana/android-sdk/build-tools/35.0.0/apksigner`
   - Keystore: `build/revanced_photos_maintenance.keystore` (RSA 2048-bit)
   - Schemes Enabled: APK Signature Scheme v1 (JAR), Scheme v2, and Scheme v3.
   - Command:
     ```bash
     apksigner sign --ks build/revanced_photos_maintenance.keystore \
         --ks-key-alias revanced_maintenance \
         --ks-pass pass:[REDACTED_LOCAL_KEY_PASS] \
         --key-pass pass:[REDACTED_LOCAL_KEY_PASS] \
         --v1-signing-enabled true \
         --v2-signing-enabled true \
         --v3-signing-enabled true \
         build/revanced_photos_remediated.apk
     ```

---

## 3. Cryptographic Signature Verification

Verification output via `apksigner verify --verbose --print-certs`:
```
Verifies
Verified using v1 scheme (JAR signing): true
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): true
Verified using v3.1 scheme (APK Signature Scheme v3.1): false
Verified using v4 scheme (APK Signature Scheme v4): false
Verified for SourceStamp: false
Number of signers: 1
Signer #1 certificate DN: CN=ReVanced Maintenance, OU=Local Security Remediation, O=Maintenance, C=US
Signer #1 certificate SHA-256 digest: d9b9771432d6ba4076340697437b0863d39f7f60560f132a97753bddca48773b
Signer #1 key algorithm: RSA (2048 bits)
```

> [!NOTE]
> **Signing Key Notice**:
> The application was signed using a local maintenance test key (`CN=ReVanced Maintenance`). Because Android enforces signature consistency across package updates, installing this build over the previously installed ReVanced app will require an update with signature mismatch handling (`adb install -r -d` or uninstalling the previous instance first).

---

## 4. Installation Instructions (For Authorized Testing)

When ready to test on a designated test device or emulator:
```bash
# Push and install the remediated APK (replace existing build)
adb install -r -d ./build/revanced_photos_remediated.apk
```
EOF
