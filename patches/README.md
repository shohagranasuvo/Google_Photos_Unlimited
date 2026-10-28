# 🛡️ Patch Source Code & Security Audit Files

This directory contains the complete source code, bytecode diffs, and security remediation files for the **Google Photos Unlimited & Hardened** build (`app.revanced.android.photos`, version `7.45.0.807274941`).

Anyone can inspect, audit, and build these patches directly.

---

## 📂 Directory Layout

| Path | Description |
| :--- | :--- |
| **[`patch.py`](patch.py)** | Standalone, open-source Python patcher that automates injection, 4-byte alignment, and test-key signing. |
| **[`payload/AndroidManifest.xml`](payload/AndroidManifest.xml)** | Security-hardened Android application manifest in standard readable format. |
| **[`payload/classes7.dex`](payload/classes7.dex)** | Compiled Dalvik Executable containing the hardened security classes. |
| **[`diffs/manifest_security_remediation.patch`](diffs/manifest_security_remediation.patch)** | Unified diff detailing all manifest-level security hardening applied. |
| **[`diffs/logger_security_remediation.patch`](diffs/logger_security_remediation.patch)** | Unified diff demonstrating the suppression of unauthorized startup debug logging. |
| **[`source/`](source/)** | Decompiled, human-readable Java source code of modified classes (including `Logger.java` and `GmsCoreSupport`). |
| **[`smali/`](smali/)** | Exact disassembled Smali bytecode for `Logger.smali`. |

---

## 🔒 Security Hardening Disclosures (Audit Breakdown)

Every modification applied to the stock APK is defensive, security-focused, and completely verifiable:

### 1. SEC-01: Enforce TLS Encryption (`usesCleartextTraffic="false"`)
* **Original Behavior:** Modified builds often left `android:usesCleartextTraffic="true"`, allowing unencrypted HTTP connections.
* **Remediation:** Strict TLS/HTTPS transport is enforced in [`payload/AndroidManifest.xml`](payload/AndroidManifest.xml). Cleartext traffic is permanently prohibited.
* **Proof in Diff:** See [`diffs/manifest_security_remediation.patch`](diffs/manifest_security_remediation.patch).

### 2. SEC-02: Block Unauthorized Physical Extraction (`allowBackup="false"`)
* **Original Behavior:** `android:allowBackup="true"` permitted local USB backup dumping via `adb backup` without authentication.
* **Remediation:** Disabled `allowBackup` and set `restoreAnyVersion="false"`.
* **Proof in Diff:** See [`diffs/manifest_security_remediation.patch`](diffs/manifest_security_remediation.patch).

### 3. SEC-03: Internal Content Provider Isolation
* **Original Behavior:** Internal content providers (`app.revanced.android.photos.freeable_space` and `partnercontentprovider`) were exposed with `android:exported="true"` without caller permissions.
* **Remediation:** Enforced custom signature-level permissions (`permission="app.revanced.android.photos.permission.INTERNAL_PROVIDER_ACCESS"`) to protect local media metadata from unauthorized third-party apps on the device.

### 4. SEC-04: Debug Logger Hardening
* **Original Behavior:** Default logging could leak runtime state to Android's global `logcat` buffer on startup.
* **Remediation:** Suppressed verbose logging by default in [`source/app/revanced/extension/shared/Logger.java`](source/app/revanced/extension/shared/Logger.java) and [`smali/Logger.smali`](smali/Logger.smali).

---

## 🔍 How to Verify the Code Yourself

You do not need to take our word for it—you can verify every line of code independently:
```bash
# 1. View the manifest security diff
git diff diffs/manifest_security_remediation.patch

# 2. View the logger hardening diff
git diff diffs/logger_security_remediation.patch

# 3. Read the decompiled Java source directly
cat source/app/revanced/extension/shared/Logger.java
```
