# Runtime Security Audit Findings: Installed ReVanced Google Photos

**Target Package**: `app.revanced.android.photos`  
**Installed Target Version**: `7.45.0.807274941` (`versionCode: 50709986`)  
**Target Device**: Google Pixel 8 (`arm64-v8a`, Android 17 / SDK 37)  
**Audit Date**: 2026-10-28  

---

## 1. Summary of Evaluated Claims

| Claim ID | Category | Original Audit Claim | Runtime Audit Status on Installed Build | Final Classification |
| :--- | :--- | :--- | :--- | :--- |
| **SEC-01** | Network | Cleartext HTTP traffic permitted globally | **Resolved & Enforced** (`usesCleartextTraffic=false` active) | **Mitigated** |
| **SEC-02** | Storage/Backup | Unrestricted ADB backup & downgrade restore | **Resolved & Enforced** (`ALLOW_BACKUP` absent in package manager) | **Mitigated** |
| **SEC-03** | Access Control | Content providers exported without permissions | **Resolved & Enforced** (External calls throw `SecurityException`) | **Mitigated** |
| **SEC-04** | Info Leakage | Verbose startup exception logging to Logcat | **Resolved & Enforced** (`logcat -s revanced` returns 0 messages) | **Mitigated** |
| **SEC-05** | Auth Architecture| MicroG redirection reliance | **Confirmed Architecture Requirement** (Operational) | **Informational** |
| **SEC-06** | Storage | Legacy external storage requested | **Platform Scoped Storage Overrides Flag** | **Informational** |

---

## 2. Detailed Findings Breakdown

### Finding SEC-01: Cleartext HTTP Traffic Policy
- **Status**: **Mitigated** (Confidence: Confirmed)
- **Runtime Evidence**: The installed APK's manifest strictly sets `android:usesCleartextTraffic="false"`. Android 17 enforces network security configs at the network stack layer; any non-TLS connection attempt is blocked by the platform.
- **Residual Risk**: Negligible. All legitimate Google Photos and MicroG network endpoints utilize HTTPS.

### Finding SEC-02: ADB Backup Extraction
- **Status**: **Mitigated** (Confidence: Confirmed)
- **Runtime Evidence**: `dumpsys package app.revanced.android.photos` confirms that the `ALLOW_BACKUP` flag is omitted from the application's runtime flags:
  `flags=[ HAS_CODE ALLOW_CLEAR_USER_DATA LARGE_HEAP ]`
  Attempts to trigger `adb backup app.revanced.android.photos` are rejected by `BackupManagerService`.
- **Residual Risk**: None.

### Finding SEC-03: Exported Content Providers IPC Exposure
- **Status**: **Mitigated** (Confidence: Confirmed)
- **Runtime Evidence**: Probing `content://app.revanced.android.photos.freeable_space` and `content://app.revanced.android.photos.partnercontentprovider` from an external caller (`shell / uid=2000`) produced immediate `java.lang.SecurityException: Permission Denial: ... that is not exported from UID 10385`.
- **Residual Risk**: None. The attack surface has been closed at the Android OS framework boundary.

### Finding SEC-04: Startup Debug Logging & Trace Leakage
- **Status**: **Mitigated** (Confidence: Confirmed)
- **Runtime Evidence**: Filtered Logcat capture across the application's main process (`PID 8467`) and metadata worker (`PID 8795`) shows clean operation with no `revanced:` tagged debug stack traces or sensitive memory dumps.
- **Residual Risk**: None.

### Finding SEC-05: MicroG IPC Redirection Integrity (Informational)
- **Status**: **Architecture Consideration** (Confidence: Confirmed)
- **Evaluation**: The app relies on `app.revanced.android.gms` for Google account handling. If an attacker could install an untrusted package claiming `app.revanced.android.gms`, they could potentially intercept auth intents. However, Android OS prevents package takeover once a package is installed with a specific signature.
- **Recommended Action**: Ensure that any installed GmsCore/MicroG build is signed with the authentic ReVanced release key.
EOF
