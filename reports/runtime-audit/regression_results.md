# On-Device Regression Results: Installed ReVanced Google Photos

**Target Device**: Google Pixel 8 (`[REDACTED-PIXEL8-SERIAL]`, Android 17, `arm64-v8a`)  
**Package**: `app.revanced.android.photos`  
**Installed Build Timestamp**: 2026-10-28 02:03:30  
**Verification Date**: 2026-10-28  

---

## 1. Process & Stability Status

The installed application was actively running during the runtime audit. The process tree was inspected via `ps -A`:

```
UID           PID   PPID   VSZ      RSS    WCHAN   ADDR S NAME
u0_a385      8467   1034 17173948 383176 0           0 S app.revanced.android.photos
u0_a385      8795   1034 15983944 164204 0           0 S app.revanced.android.photos:mediametadataservice
```

- **Process Health**: Both the main UI process and the media metadata service run stably without crashes.
- **Memory Footprint**: Normal memory consumption (~383 MB RSS for main process, ~164 MB RSS for background metadata processor).
- **ANR / Crash Traces**: Zero application crashes (`crash_dump`), ANRs, or native segfaults recorded in system event logs.

---

## 2. In-App Functionality & Compatibility

1. **Local Media Metadata Processing**:
   Logcat evidence from PID `8795` demonstrates active background processing of local storage media (`FileSource` indexing and XMP metadata parsing), confirming that:
   - Scoped storage permissions (`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`) are functioning normally.
   - Restricting `FreeUpSpaceContentProvider` and `PartnerContentProvider` did not break local gallery indexing or image thumbnail generation.
2. **Device Spoofing Integrity**:
   Inspection of `base.apk` on the device confirmed the hardcoded `"Pixel XL"` and `"marlin"` bytecodes in `classes8.dex` and `defpackage.anif`.

---

## 3. Deferred Tests Matrix (User Authorization Precondition)

In strict adherence to the project safety rule (*"Ask for my approval before any test that could change app data, upload media, alter account settings, or require elevated privileges"*), the following interactive runtime scenarios remain **NOT TESTED**:

| Test Scenario | Reason Deferred | Precondition for Execution |
| :--- | :--- | :--- |
| **Interactive Google Account Sign-In** | Modifies account tokens and auth state. | Requires user confirmation of a test account. |
| **Active Photo / Video Cloud Upload** | Modifies cloud storage quota and network state. | Requires user provision of non-sensitive sample photos. |
| **Cloud Synchronization Toggle** | Changes app preferences and server-side state. | Requires explicit authorization. |
EOF
