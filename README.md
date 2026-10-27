# Google Photos Unlimited — Security & Privacy Audit

[![Security Audit](https://img.shields.io/badge/Security%20Audit-Remediated-brightgreen.svg)](docs/security-audit.md)
[![Privacy Review](https://img.shields.io/badge/Privacy%20Review-Verified-blue.svg)](PRIVACY.md)
[![License: Research](https://img.shields.io/badge/License-Defensive%20Research-orange.svg)](LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/shohagranasuvo/Google_Photos_Unlimited?color=blue&label=Latest%20Release)](https://github.com/shohagranasuvo/Google_Photos_Unlimited/releases/latest)

An open-source security research repository, privacy data-flow analysis, vulnerability remediation log, and verification guide for the modified Google Photos Android application (`app.revanced.android.photos`).

---

## 1. Project Overview & Disclaimer

This project documents defensive security research, static bytecode reverse engineering, on-device runtime analysis, and vulnerability hardening performed on a modified Google Photos client featuring Google Pixel 1 device spoofing and MicroG integration.

> [!WARNING]
> **UNOFFICIAL PROJECT & DISTRIBUTION NOTICE**:
> This repository is an independent research initiative and is **not affiliated with, maintained by, or endorsed by Google LLC or the ReVanced team**.
>
> - "Google", "Google Photos", and "Pixel" are registered trademarks of Google LLC.
> - **Why are there no pre-built APK files?** Google Photos is proprietary software. Distributing modified Google APK binaries violates copyright laws and GitHub policies. Instead, this project provides **100% legal, open-source patch tools** that let you patch your own legitimate APK in seconds!
> - **Google does not guarantee permanent unlimited storage.** Storage entitlement is enforced on Google's cloud servers based on client metadata; Google reserves the right to alter its backend policies, enforce cryptographic hardware attestation, or terminate account access at any time.

---

## 2. On-Device Storage Proofs

Below are on-device screenshots taken during verification on a Google Pixel 8, demonstrating the application interface and Google cloud storage status:

| Device Identification Banner | Account Storage Status (Redacted) |
| :---: | :---: |
| ![Pixel XL Storage Notice](docs/images/storage_proof_pixel_xl.png) | ![Account Storage Status](docs/images/storage_proof_unlimited.png) |
| *"This Pixel XL can back up unlimited photos & videos at no charge"* | *"Account storage: Unlimited" (Verified Google backend response)* |

---

## 3. 🚀 Quick Setup Guide (Choose Your Method)

Choose the method that works best for you:

| Method | Best For | Requirement | Ease |
| :--- | :--- | :--- | :---: |
| **[Method 1: Phone-Only (ReVanced Manager)](#-method-1-phone-only-setup-via-revanced-manager-recommended)** | Sideloading directly on your Android phone | Android Phone | ⭐⭐⭐⭐⭐ (Easiest) |
| **[Method 2: 1-Click Patch Tool (PC & Termux)](#-method-2-1-click-patch-tool-pc--termux)** | Fast local build on Windows, Mac, Linux, or Termux | Python 3 | ⭐⭐⭐⭐⭐ (Fastest) |

---

### 📱 Method 1: Phone-Only Setup via ReVanced Manager (Recommended)

No computer needed! You can patch and install the app in **3 minutes**:

#### Step 1: Install MicroG (Required for Google Login)
1. Download and install **MicroG GmsCore** on your phone:  
   👉 **[Download MicroG APK (Direct Link)](https://github.com/ReVanced/GmsCore/releases/latest)**
2. Open **MicroG Settings** from your app drawer > tap **Account** > sign into your Google Account.
3. In your phone's **Settings > Apps > MicroG > Battery**, set it to **Unrestricted** (so backups don't pause).

#### Step 2: Install ReVanced Manager & Add Custom Source
1. Download and install **ReVanced Manager**:  
   👉 **[Download ReVanced Manager APK](https://github.com/ReVanced/revanced-manager/releases/latest)**
2. *(Optional)* To get our hardened security fixes automatically:
   - Open ReVanced Manager > tap **Settings (gear icon)** > tap **Sources**.
   - Change **Patches organization** to: `shohagranasuvo`
   - Change **Patches source** to: `Google_Photos_Unlimited`
   - Restart ReVanced Manager.

#### Step 3: Patch Google Photos & Install
1. Download the supported clean Google Photos APK on your phone:  
   👉 **[Download Google Photos v7.45 APK (APKMirror)](https://www.apkmirror.com/apk/google-inc/photos/photos-7-45-0-807274941-release/)** *(Choose the standard APK)*.
2. Open **ReVanced Manager**:
   - Tap **Patcher** > tap **Select an application** > tap **Storage** (bottom right).
   - Select the downloaded Google Photos APK.
3. Tap **Patch** and wait ~1 minute.
4. Tap **Install**!

---

### 💻 Method 2: 1-Click Patch Tool (PC & Termux)

If you have a computer or use Termux on Android, you can build the exact security-hardened APK in **5 seconds** with **1 command**:

1. Download the patch kit from our latest release:  
   👉 **[Download google-photos-unlimited-patch-v7.45.0.zip](https://github.com/shohagranasuvo/Google_Photos_Unlimited/releases/download/v7.45.0-remediated/google-photos-unlimited-patch-v7.45.0.zip)**
2. Extract the ZIP file on your computer.
3. Download the clean Google Photos APK from [APKMirror](https://www.apkmirror.com/apk/google-inc/photos/photos-7-45-0-807274941-release/) and place it in the folder as `base.apk`.
4. Run the script:
   ```bash
   python3 patch.py base.apk
   ```
5. Done! The script automatically injects the security fixes (SEC-01 to SEC-04), 4-byte aligns the ZIP, and signs it as **`GooglePhotos_Unlimited_Remediated.apk`**.
6. Transfer to your phone and install!

---

### 🎉 How to Confirm It Works:
Open the newly installed **Google Photos** app > tap your profile icon > tap **Manage storage**.  
You will see the official Pixel banner:  
**"📱 This Pixel XL can back up unlimited photos & videos at no charge"**

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

## 5. Security Audit & Remediation Status

| ID | Issue | Baseline State | Remediated State | Status |
| :--- | :--- | :--- | :--- | :--- |
| **SEC-01** | Cleartext Network Policy | `usesCleartextTraffic="true"` | `usesCleartextTraffic="false"` (TLS enforced) | **MITIGATED** |
| **SEC-02** | Backup & Snapshot Restore | `allowBackup="true"`, `restoreAnyVersion="true"` | Attributes disabled to prevent ADB dump | **MITIGATED** |
| **SEC-03** | IPC Content Provider Exposure | `FreeUpSpace` & `Partner` providers exported | Set to `exported="false"` (Sandbox isolated) | **MITIGATED** |
| **SEC-04** | Startup Information Leakage | Verbose debug logging on uninitialized boot | Defaulted to silent mode in `Logger.smali` | **MITIGATED** |
| **SEC-05** | MicroG GmsCore Architecture | External Binder IPC trust boundary | Sandboxed via Android package manager | **VERIFIED** |

Read the full technical report in [**docs/security-audit.md**](docs/security-audit.md).

---

## 6. Known Issues & Troubleshooting

### "Manage Google Account" Crash (Known Issue)
- **Symptom**: Tapping the profile picture and selecting *"Manage your Google Account"* terminates the application immediately.
- **Root Cause**: The app attempts to launch `Intent { act=app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS pkg=app.revanced.android.gms }`. Because MicroG does not implement this activity, Android throws an unhandled `ActivityNotFoundException`.
- **Workaround**: Manage your Google account directly in any web browser at [https://myaccount.google.com/](https://myaccount.google.com/).
- **Smali Fix**: A minimal defensive patch wrapping `startActivityForResult` in `bfcn.smali` is documented in [**docs/troubleshooting.md**](docs/troubleshooting.md).

### Sideloading Permission
- If prompted with *"Your phone is not allowed to install unknown apps"*, temporarily enable **Allow from this source**, install the app, and then immediately turn that permission back **OFF** in Android Settings.

---

## 7. Limitations & Testing Scope

- **Encrypted Payload Boundaries**: Live decrypted inspection of gRPC Protobuf byte streams was not performed due to certificate pinning and non-invasive testing rules.
- **MicroG Independence**: MicroG is maintained independently; security depends on installing verified upstream builds.
- **No In-Place Upgrades with Different Keys**: Installing builds signed with different keys requires a full reinstall.

Read more in [**docs/limitations.md**](docs/limitations.md).

---

## 8. Repository Navigation

- [**SECURITY.md**](SECURITY.md) — Responsible disclosure policy and reporting guidelines.
- [**PRIVACY.md**](PRIVACY.md) — Privacy disclosures, data flow mapping, and telemetry analysis.
- [**CHANGELOG.md**](CHANGELOG.md) — Version history and remediation logs.
- [**docs/installation.md**](docs/installation.md) — Detailed installation and sideloading guide.
- [**docs/custom-patch-source.md**](docs/custom-patch-source.md) — Setting up ReVanced Manager with this repository.
- [**docs/microg-setup.md**](docs/microg-setup.md) — MicroG installation and battery setup.
- [**reports/**](reports/) — Raw audit logs, smali analyses, and test checklists.
- [**scripts/**](scripts/) — Verification and integrity test scripts.
- [**releases/**](releases/) — Release policies and patch downloads.

---

## 9. Credits & Attribution

- [Google Photos](https://photos.google.com/) by Google LLC.
- [ReVanced Team](https://github.com/ReVanced) for open-source patch components.
- [MicroG Project](https://github.com/microg) for the open-source GmsCore implementation.
