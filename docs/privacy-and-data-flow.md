# Privacy and Data-Flow Assessment

This document provides a technical audit of the network destinations, data flows, and telemetry mechanisms in ReVanced Google Photos.

---

## 1. Executive Privacy Statement

> [!IMPORTANT]
> **No unauthorized data transmission was observed in the tested scenarios. This is not proof that no such transmission can occur under all conditions.**

Google Photos is, by design, a cloud-connected application. When cloud backup is enabled, the application **necessarily transmits your chosen photos, videos, and associated metadata (timestamps, EXIF camera parameters, and GPS coordinates if location is enabled) to Google LLC's cloud infrastructure**.

The goal of this audit was to establish whether this modified build transmits personal data to unauthorized third-party servers, data brokers, advertising networks, or community proxies beyond Google and MicroG.

---

## 2. Tested Privacy Claims & Status

| Category | Claim / Scenario | Status | Supporting Evidence |
| :--- | :--- | :--- | :--- |
| **Media Transmission** | Uploads route exclusively to Google servers | **VERIFIED** | Smali bytecode inspection (`MediaUploaderWorker`), Linux kernel TCP socket check (`AS15169`). |
| **Credential Flow** | OAuth2 tokens route via MicroG to Google Auth | **VERIFIED** | Android Binder IPC analysis; tokens cached in private app storage. |
| **Third-Party Trackers** | Absence of commercial surveillance SDKs (AppsFlyer, Facebook, Adjust) | **VERIFIED** | Comprehensive DEX class scanning; zero tracker signatures found. |
| **Dormant Libraries** | `api.redgifs.com` does not execute | **VERIFIED** | Call-graph disassembly confirms zero callers or active subclasses in the APK. |
| **Decrypted TLS Payloads** | Deep packet inspection of Protobuf byte stream | **NOT TESTED** | Encrypted gRPC over TLS was not decrypted due to certificate pinning and non-destructive testing boundaries. |
| **Google Server Telemetry**| Google backend policy on device model spoofing | **UNCONFIRMED** | Google's internal server-side logging and enforcement mechanisms cannot be audited from the client. |

---

## 3. Storage Indication and On-Device Evidence

The application contains the `SpoofProvider` bytecode hook, which modifies Android's system build identifiers to report the device as a Google Pixel 1 (`Pixel XL` / `marlin`). 

During testing on the connected Google Pixel 8, Google's backend accepted these client-reported headers and reflected an unlimited storage entitlement in the application UI:

### Verified Storage Proofs:
1. **Device Identification & Unlimited Backup Notice**:
   ![Pixel XL Unlimited Storage Proof](images/storage_proof_pixel_xl.png)
   *Figure 1: Google Photos storage management UI showing "This Pixel XL can back up unlimited photos & videos at no charge".*

2. **Account Storage Status (Redacted)**:
   ![Account Storage Unlimited Status](images/storage_proof_unlimited.png)
   *Figure 2: Account overview confirming "Backup complete" and "Account storage: Unlimited". (Personal email address redacted).*

> [!NOTE]
> While the UI and Google's upload servers currently treat media from this client as quota-free, this is enforced entirely on Google's cloud servers. Google may change its server-side validation or require hardware-backed cryptographic attestation at any time.

---

## 4. Observed Network Destinations

An exhaustive static extraction of all 16 DEX files revealed 83 unique URLs, categorized below:

### A. Google Cloud & API Endpoints (Intended Operation)
- `photos.googleapis.com` / `photos-pa.googleapis.com`: Protocol Buffers / gRPC endpoints for metadata sync and media chunk ingestion.
- `*.googleusercontent.com`: Image rendering, thumbnails, and cache retrieval.
- `oauth2.googleapis.com`: Handled by MicroG for token acquisition.
- `play.googleapis.com/log`: Google ClearcutLogger crash metrics and diagnostic telemetry.

### B. Third-Party Endpoints Analyzed
- **`api.revanced.app`**: Static string in `AboutLinksRoutes.smali`. Traced only to the in-app "About" screen to fetch community links. Never triggered during sync or background tasks.
- **`capcut-yt.onelink.me`**: Legitimate Google Photos dialog (`adhq.smali`) directing users to the external Play Store entry for the CapCut video editor.
- **`dontkillmyapp.com`**: Web guide referenced in MicroG battery setup prompts.
- **`api.redgifs.com`**: Dormant stub from ReVanced's shared extensions library with zero callers in Google Photos.

---

## 5. Kernel Network Socket Audit

A runtime audit of active network sockets under UID `10385` was performed on the connected device:

```
4: 400F01240F009713ED2EED91A1A5A51B:9A96 60480120000445480000000000000000:01BB 01 ... UID 10385
```

- **Remote IPv6 Address**: `2001:4860:4845:4::...` (Google LLC, Autonomous System AS15169).
- **Remote Port**: `443` (`01BB` hex) - HTTPS / TLS.
- **Conclusion**: The application establishes connections strictly to verified Google data centers.
