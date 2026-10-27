# Privacy Policy & Data-Handling Disclosures

**Last Updated:** October 2026  
**Project Scope:** Security and Privacy Research for `app.revanced.android.photos`

---

## 1. Foundational Privacy Notice

> [!IMPORTANT]
> **No unauthorized data transmission was observed in the tested scenarios. This is not proof that no such transmission can occur under all conditions.**

This modified application is a cloud-integrated media management and backup client. By enabling cloud synchronization in Google Photos, **your selected images, videos, and associated EXIF metadata (date/time, camera hardware tags, exposure settings, and location coordinates if enabled) are transmitted to Google LLC's cloud storage services**.

---

## 2. Transparent Data Transmission Disclosures

Based on static reverse engineering of 16 compiled DEX files and live Linux kernel TCP socket inspection on physical hardware:

### A. Intended Cloud Operations (Google LLC Infrastructure)
- **Media Uploads**: Transmitted directly to `photos-pa.googleapis.com` and `photos.googleapis.com` using HTTP/2 over TLS.
- **Image Caching & Thumbnails**: Downloaded from `*.googleusercontent.com` over HTTPS.
- **Telemetry & Diagnostic Crash Logging**: Handled by Google's native ClearcutLogger and Primes libraries, reporting strictly to `play.googleapis.com/log`.

### B. Account Authentication Operations (MicroG Infrastructure)
- **OAuth2 Handshake**: Handled via Android Binder IPC to the companion package `app.revanced.android.gms`. MicroG communicates with `oauth2.googleapis.com` to exchange tokens.
- **Token Storage**: Scoped authorization tokens are held locally in app-private encrypted shared preferences and are never broadcast or shared with third-party apps.

### C. Third-Party Endpoints Evaluation
- **Commercial Ad/Tracking Networks**: **None.** No code from Facebook Graph, AppsFlyer, Adjust, Kochava, or Flurry exists in the APK.
- **Community Servers (`api.revanced.app`)**: Traced solely to user-initiated clicks within the in-app "About" settings screen. Zero background traffic or telemetry is sent to ReVanced servers during photo syncing or browsing.
- **Dormant Code (`api.redgifs.com`)**: Verified as dead code bundled from shared libraries; has zero callers and never executes.

---

## 3. Privacy Test Matrix and Verification Status

| Test / Scenario | Status | Evidence & Test Boundary |
| :--- | :--- | :--- |
| **Media Stream Destination** | **VERIFIED** | Active sockets connect strictly to Google LLC (`AS15169`) over port 443. |
| **Third-Party Tracker Absence** | **VERIFIED** | Static scan of DEX classes found zero third-party commercial analytics SDKs. |
| **Local App Sandboxing** | **VERIFIED** | IPC content queries from unauthorized apps throw `SecurityException`. |
| **Background Network Inactivity** | **VERIFIED** | Background network queries verified via logcat and kernel socket state. |
| **Encrypted Stream Inspection** | **NOT TESTED** | Dynamic decryption of gRPC Protobuf payloads over TLS was not performed. |
| **Google Server-Side Telemetry** | **UNCONFIRMED** | Google's internal server-side abuse and anomaly tracking cannot be inspected from client devices. |

---

## 4. Encryption and Network Safety Warning

Encrypted network traffic (HTTPS / TLS) guarantees that data is protected against tampering and eavesdropping in transit. However, **encryption alone does not prove that a destination is trustworthy or that no unnecessary data is transmitted**.

Users must recognize that by signing in with their Google Account and enabling cloud backup, they are entrusting their personal photos and metadata directly to Google LLC subject to [Google's Privacy Policy](https://policies.google.com/privacy).

---

## 5. User Data Rights and Revocation

You retain complete control over your data:
- **Revoke Cloud Backup**: Turn off "Backup & sync" in Photos Settings at any time to restrict the app to local photo viewing.
- **Revoke Account Access**: Open your Google Account permissions ([https://myaccount.google.com/permissions](https://myaccount.google.com/permissions)) and revoke OAuth access for MicroG / Photos.
- **Delete Local Data**: Uninstalling the application deletes all private application databases, caches, and stored authentication tokens.
