# Google Photos Unlimited — Security & Privacy Audit

[![Security Audit](https://img.shields.io/badge/Security%20Audit-Remediated-brightgreen.svg)](docs/security-audit.md)
[![Privacy Review](https://img.shields.io/badge/Privacy%20Review-Verified-blue.svg)](PRIVACY.md)
[![License: Research](https://img.shields.io/badge/License-Defensive%20Research-orange.svg)](LICENSE)

An open-source security research repository, privacy data-flow analysis, vulnerability remediation log, and verification guide for the modified Google Photos Android application (`app.revanced.android.photos`).

---

## 1. Project Overview & Disclaimer

This project documents defensive security research, static bytecode reverse engineering, on-device runtime analysis, and vulnerability hardening performed on a modified Google Photos client featuring Google Pixel 1 device spoofing and MicroG integration.

> [!WARNING]
> **UNOFFICIAL PROJECT & DISTRIBUTION DISCLAIMER**:
> This repository is an independent research initiative and is **not affiliated with, maintained by, or endorsed by Google LLC or the ReVanced team**.
>
> - "Google", "Google Photos", and "Pixel" are registered trademarks of Google LLC.
> - **Why are there no pre-compiled APK files in GitHub Releases?** Google Photos is proprietary copyrighted software. Distributing modified Google APK binaries publicly violates copyright laws, Android terms of service, and GitHub's Acceptable Use Policies, which risks immediate DMCA takedowns and account suspension. Pre-built APKs from unknown online sources also pose severe security and malware risks to users.
> - Following standard open-source security practices (such as the ReVanced model), users obtain their own clean base APK and patch it locally using verified tools.
> - **Google does not guarantee permanent unlimited storage for modified clients.** Storage entitlement is enforced on Google's cloud servers based on client metadata; Google reserves the right to alter its backend policies, enforce cryptographic hardware attestation, or terminate account access at any time.

---

## 2. On-Device Storage Proofs

Below are on-device screenshots taken during verification on a Google Pixel 8, demonstrating the application interface and Google cloud storage status:

| Device Identification Banner | Account Storage Status (Redacted) |
| :---: | :---: |
| ![Pixel XL Storage Notice](docs/images/storage_proof_pixel_xl.png) | ![Account Storage Status](docs/images/storage_proof_unlimited.png) |
| *"This Pixel XL can back up unlimited photos & videos at no charge"* | *"Account storage: Unlimited" (Verified Google backend response)* |

---

## 3. 📱 Easy 3-Step Setup (No Computer Needed)

You can set up Unlimited Google Photos directly on your phone in **under 3 minutes**:



### 1️⃣ Step 1: Install MicroG (For Google Sign-In)
1. Download and install **MicroG GmsCore** on your phone:  
   👉 **[Download MicroG APK](https://github.com/ReVanced/GmsCore/releases/latest)**
2. Open **MicroG Settings** from your app drawer > tap **Account** > log into your Google Account.
3. In your phone's **Settings > Apps > MicroG > Battery**, set it to **Unrestricted** (so photo backups don't pause).

### 2️⃣ Step 2: Install ReVanced Manager
1. Download and install **ReVanced Manager** on your phone:  
   👉 **[Download ReVanced Manager APK](https://github.com/ReVanced/revanced-manager/releases/latest)**

### 3️⃣ Step 3: Patch Google Photos (1-Tap Automatic Setup)
1. Download the supported clean Google Photos APK on your phone:  
   👉 **[Download Google Photos v7.45 APK (APKMirror)](https://www.apkmirror.com/apk/google-inc/photos/photos-7-45-0-807274941-release/)** *(Select the standard APK)*.
2. Open **ReVanced Manager**:
   - Tap **Patcher** at the bottom.
   - Tap **Select an application** > tap **Storage** (bottom right) > choose the downloaded Google Photos APK.
3. Tap **Patch** (bottom right) and wait ~1 minute.
4. Tap **Install**!

### 🎉 Verification
Open the newly installed **Google Photos** > tap your profile icon > tap **Manage storage**.  
You will see:  
**"📱 This Pixel XL can back up unlimited photos & videos at no charge"**

*(Need a more detailed guide? Read [docs/installation.md](docs/installation.md)).*

---


---

### 📦 Alternative Setup Methods (Option B & Option C)

- **Option B: Standalone 1-Click Builder (PC / Termux)**:  
  Don't want to use ReVanced Manager? Download the [](https://github.com/shohagranasuvo/Google_Photos_Unlimited/releases/latest) patch kit, put your  next to it, and run . It will build, align, and sign your APK in 5 seconds!
- **Option C: Use this Repo as a Custom ReVanced Source**:  
  In ReVanced Manager, set your Patch Organization to  and Source to . Read the [Custom Patch Source Guide](docs/custom-patch-source.md).


---

## 4. Privacy-First Statement & Data Safety

> [!IMPORTANT]
> **No unauthorized data transmission was observed in the tested scenarios. This is not proof that no such transmission can occur under all conditions.**

- **Intended Media Upload**: Cloud backup requires transferring your chosen photos, videos, and EXIF metadata to Google LLC's servers (`photos.googleapis.com`, `photos-pa.googleapis.com`).
- **No Third-Party Trackers**: Static scanning across all 16 DEX files confirmed the total absence of commercial tracking and ad SDKs (no Facebook Graph, AppsFlyer, Adjust, Kochava, or Flurry).
- **Socket Verification**: Real-time Linux kernel socket monitoring confirmed that all active network connections from the app route exclusively to Google LLC (`AS15169`) over TLS (Port 443).
- **Authentication**: Google account sign-in is handled locally through the open-source MicroG GmsCore provider (`app.revanced.android.gms`), connecting to `oauth2.googleapis.com`.

For full disclosures, see [**PRIVACY.md**](PRIVACY.md) and [**docs/privacy-and-data-flow.md**](docs/privacy-and-data-flow.md).

---

## 5. Tested Environment & Compatibility

- **Physical Device**: Google Pixel 8 (`arm64-v8a`)
- **Android Version**: Android 14, 15, and Android 17 (Developer Preview / API Level 37)
- **Application Version**: `7.45.0.807274941` (`versionCode: 50709986`)
- **Package Name**: `app.revanced.android.photos`
- **Companion Dependency**: MicroG GmsCore (`app.revanced.android.gms`)

---

## 6. Security Audit & Remediation Status

| ID | Issue | Baseline State | Remediated State | Status |
| :--- | :--- | :--- | :--- | :--- |
| **SEC-01** | Cleartext Network Policy | `usesCleartextTraffic="true"` | `usesCleartextTraffic="false"` (TLS enforced) | **MITIGATED** |
| **SEC-02** | Backup & Snapshot Restore | `allowBackup="true"`, `restoreAnyVersion="true"` | Attributes disabled to prevent ADB dump | **MITIGATED** |
| **SEC-03** | IPC Content Provider Exposure | `FreeUpSpace` & `Partner` providers exported | Set to `exported="false"` (Sandbox isolated) | **MITIGATED** |
| **SEC-04** | Startup Information Leakage | Verbose debug logging on uninitialized boot | Defaulted to silent mode in `Logger.smali` | **MITIGATED** |
| **SEC-05** | MicroG GmsCore Architecture | External Binder IPC trust boundary | Sandboxed via Android package manager | **VERIFIED** |

Read the full technical report in [**docs/security-audit.md**](docs/security-audit.md).

---

## 7. APK Integrity Verification (For Advanced Users)

If building manually on PC, verify the cryptographic hash of your built APK before installing:
```bash
./scripts/verification/verify_apk_integrity.sh path/to/your/Photos.apk
```

To install directly via ADB:
```bash
adb install -r path/to/your/Photos.apk
```

Detailed walkthrough: [**docs/installation.md**](docs/installation.md).

---

## 8. Known Issues & Troubleshooting

### "Manage Google Account" Crash (Known Issue)
- **Symptom**: Tapping the profile picture and selecting *"Manage your Google Account"* terminates the application immediately.
- **Root Cause**: The app attempts to launch `Intent { act=app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS pkg=app.revanced.android.gms }`. Because MicroG does not implement this activity, Android throws an unhandled `ActivityNotFoundException`.
- **Workaround**: Manage your Google account directly in any web browser at [https://myaccount.google.com/](https://myaccount.google.com/).
- **Smali Fix**: A minimal defensive patch wrapping `startActivityForResult` in `bfcn.smali` is documented in [**docs/troubleshooting.md**](docs/troubleshooting.md).

### Sideloading Permission
- If prompted with *"Your phone is not allowed to install unknown apps"*, temporarily enable **Allow from this source**, install the app, and then immediately turn that permission back **OFF** in Android Settings.

---

## 9. Limitations & Testing Scope

- **Encrypted Payload Boundaries**: Live decrypted inspection of gRPC Protobuf byte streams was not performed due to certificate pinning and non-invasive testing rules.
- **MicroG Independence**: MicroG is maintained independently; security depends on installing verified upstream builds.
- **No In-Place Upgrades with Different Keys**: Installing builds signed with different keys requires a full reinstall.

Read more in [**docs/limitations.md**](docs/limitations.md).

---

## 10. Repository Navigation

- [**SECURITY.md**](SECURITY.md) — Responsible disclosure policy and reporting guidelines.
- [**PRIVACY.md**](PRIVACY.md) — Privacy disclosures, data flow mapping, and telemetry analysis.
- [**CHANGELOG.md**](CHANGELOG.md) — Version history and remediation logs.
- [**docs/**](docs/) — Comprehensive technical guides (Installation, MicroG, Security, Troubleshooting).
- [**reports/**](reports/) — Raw audit logs, smali analyses, and test checklists.
- [**scripts/**](scripts/) — Verification and integrity test scripts.
- [**releases/**](releases/) — Release policies and artifact verification hashes.

---

## 11. Credits & Attribution

- [Google Photos](https://photos.google.com/) by Google LLC.
- [ReVanced Team](https://github.com/ReVanced) for open-source patch components.
- [MicroG Project](https://github.com/microg) for the open-source GmsCore implementation.
