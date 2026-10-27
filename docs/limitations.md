# Technical Limitations and Scope Boundaries

This document defines the technical boundaries of this research project, what can and cannot be guaranteed, and untested areas.

---

## 1. What This Project Does Not and Cannot Guarantee

1. **No Guarantee of Permanent Unlimited Storage**:
   The unlimited original quality backup for Google Pixel 1 ("marlin") is an entitlement granted by Google's cloud servers based on device metadata headers. Google reserves the right to modify its cloud service policies, introduce cryptographic hardware attestation (e.g. Play Integrity / KeyMint), or restrict account access at any time without warning.

2. **No Claim of Official Endorsement**:
   This project is an independent defensive security research initiative. It is neither affiliated with, supported by, nor endorsed by Google LLC or the ReVanced team.

3. **No Automatic Vulnerability Updates**:
   Because patched applications are decoupled from the Google Play Store, they do not receive automatic security updates. Any future zero-day vulnerabilities discovered in upstream Google Photos will require manual recompilation and patching.

---

## 2. Technical Research Boundaries

### A. Encrypted Payload Inspection
- **Boundary**: All network communications between Google Photos and Google cloud servers are encrypted via TLS and formatted using Protocol Buffers.
- **Limitation**: While static disassembly confirmed the absence of unauthorized exfiltration endpoints, live dynamic decryption of Protobuf streams was not performed, as injecting custom CA certificates into modern Android (API 37) requires modifying system trust stores and breaking certificate pinning.

### B. MicroG Component Independence
- **Boundary**: The audit focused on `app.revanced.android.photos`.
- **Limitation**: MicroG (`app.revanced.android.gms`) is an external companion package. Although the interface between Photos and MicroG was verified, the internal security of MicroG itself is governed by its own upstream maintainers.

### C. Server-Side Logging
- **Boundary**: Client-side inspection cannot evaluate Google's internal server-side abuse detection mechanisms. Whether Google flags accounts with anomalous client signatures is outside the scope of local analysis.

---

## 3. Testing Status Classification

| Scenario | Audit Status | Description |
| :--- | :--- | :--- |
| **Static Manifest Integrity** | **VERIFIED** | Attributes enforced (`usesCleartextTraffic=false`, unexported providers). |
| **Local Inter-App Isolation** | **VERIFIED** | Live ADB content query confirmed blocked by `SecurityException`. |
| **Media Upload Routing** | **VERIFIED** | Sockets connect exclusively to Google LLC over TLS. |
| **Third-Party Tracker Absence** | **VERIFIED** | Zero commercial tracking SDKs found across all DEX files. |
| **Manage Google Crash** | **CONFIRMED** | Unhandled `ActivityNotFoundException` due to missing MicroG intent filter. |
| **Live Protobuf Decryption** | **NOT TESTED** | Encrypted payload streams were not decrypted dynamically in transit. |
| **Server-Side Policy Longevity** | **UNCONFIRMED**| Longevity of Google cloud entitlement is unknowable from client code. |
