# Release Policy and Distribution Notice

## 1. Proprietary Software and Redistribution Policy

This repository is dedicated to **defensive Android security research, reproducible auditing, and technical documentation**.

In compliance with copyright and licensing constraints:
- **No proprietary binaries or modified APKs are hosted in this repository or in GitHub Releases.**
- Google Photos is a proprietary software package owned by Google LLC. Modified versions cannot legally or ethically be redistributed as pre-compiled public APKs.
- No private signing keystores (`.keystore`, `.jks`), signing certificates, or authentication tokens are published or tracked in version control.

Users seeking to build or maintain a modified client must supply their own legitimate source APK from an authorized device or official backup and apply patches locally using open-source tools.

---

## 2. Verified Build Reference Artifacts

For local research and verification, the table below documents the cryptographic parameters of the audited and remediated reference build:

| Metric | Reference Parameter |
| :--- | :--- |
| **Target Package** | `app.revanced.android.photos` |
| **Target Version Name** | `7.45.0.807274941` |
| **Version Code** | `50709986` |
| **Target Architecture** | `arm64-v8a` |
| **Minimum SDK** | `23` (Android 6.0 Marshmallow) |
| **Target SDK** | `36` (Android 15+) |
| **Remediated Build SHA-256** | `28a47789732c55a60f37d260d4b583ee0ca01657fa44ee5904a33caac14beae5` |
| **Signer Certificate Digest** | `D9B9771432D6BA4076340697437B0863D39F7F60560F132A97753BDDCA48773B` |
| **Zip Alignment** | 4-byte boundary verified |

---

## 3. How to Verify a Locally Built APK

Before sideloading any locally built APK onto an Android device, verify its cryptographic hash and signing scheme using the provided verification script:

```bash
# Verify file hash and zip alignment
./scripts/verification/verify_apk_integrity.sh path/to/your/built.apk 28a47789732c55a60f37d260d4b583ee0ca01657fa44ee5904a33caac14beae5
```

Or manually inspect the certificate and digest with Android SDK tools:
```bash
# Calculate SHA-256
sha256sum path/to/your/built.apk

# Inspect cryptographic signatures
apksigner verify --verbose --print-certs path/to/your/built.apk
```

> [!WARNING]
> Never install pre-compiled APK binaries obtained from unknown third-party websites or untrusted telegram channels. Malicious actors frequently bundle Trojans and spyware into repackaged Google Photos builds.
