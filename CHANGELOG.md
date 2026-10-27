# Changelog

All notable changes to the security research, patches, and documentation in this project are documented in this file.

---

## [7.45.0-remediated] - 2026-10-28

### Security & Privacy Remediations
- **SEC-01 (Cleartext Traffic Mitigation)**: Enforced `android:usesCleartextTraffic="false"` in `AndroidManifest.xml` to mandate TLS across all network operations.
- **SEC-02 (Backup Extraction Hardening)**: Disabled `android:allowBackup` and `android:restoreAnyVersion` to prevent unauthorized ADB data dumping and snapshot downgrades.
- **SEC-03 (IPC Content Provider Protection)**: Explicitly unexported internal providers (`FreeUpSpaceContentProvider` and `PartnerContentProvider`) with `android:exported="false"`, preventing unauthorized cross-app invocation.
- **SEC-04 (Logcat Leakage Suppression)**: Patched `Logger.smali` default state to eliminate uninitialized startup logcat dumps.

### Verification & Documentation
- **Runtime Audit**: Completed on-device hardware verification on Google Pixel 8 (Android 17 / API 37).
- **Network Destination Mapping**: Extracted and audited 83 embedded URLs; confirmed zero active third-party trackers.
- **Crash Root Cause Diagnosis**: Identified exact unhandled `ActivityNotFoundException` in `bfcn.smali` / `bbka.smali` causing the "Manage Google Account" crash; documented minimal fallback fix.
- **Verification Scripts**: Added automated shell scripts to verify APK SHA-256 digests, zip alignment, signing schemes, and content provider boundaries.

### Known Limitations
- The "Manage Google Account" shortcut inside the app crashes due to MicroG lacking `app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS`. Workaround is documented in `docs/troubleshooting.md`.
