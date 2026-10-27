# Installation and Sideloading Guide

This guide provides secure, step-by-step instructions for sideloading and verifying a locally built `app.revanced.android.photos` APK.

---

## 1. Verified Compatibility

- **Verified Target OS**: Android 14, Android 15, and Android 17 (Developer Preview / Vanilla Ice Cream / Baklava).
- **Minimum Supported SDK**: Android 6.0 (API Level 23).
- **Target Architecture**: `arm64-v8a`.
- **Companion Requirement**: MicroG GmsCore (`app.revanced.android.gms`) is required for account authentication.

---

## 2. Pre-Installation Integrity & Signature Verification

Never install an APK without verifying its cryptographic digest and package identity.

### Step 1: Check SHA-256 Hash
In a Linux/macOS terminal or Git Bash on Windows:
```bash
sha256sum path/to/revanced_photos.apk
```
Confirm the digest matches your expected build output.

### Step 2: Inspect Package Name and Certificate
Using `apksigner` (from Android SDK Build-Tools):
```bash
apksigner verify --verbose --print-certs path/to/revanced_photos.apk
```
Ensure:
1. `Verifies: true`
2. The package name is `app.revanced.android.photos`.
3. The certificate fingerprint matches your local signing keystore.

> [!CAUTION]
> If you previously installed a different build with a different signing key, Android's Package Manager will abort the installation with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Android strictly enforces that updates share the exact same cryptographic signing key as the installed package.

---

## 3. Safe Installation Procedure

### Option A: Installation via ADB (Recommended for Researchers)
Using Android Debug Bridge (ADB) via USB:
```bash
# Sideload the APK directly onto your device
adb install -r path/to/revanced_photos.apk
```

If upgrading an existing installation:
```bash
adb install -r -d path/to/revanced_photos.apk
```

### Option B: On-Device Installation via Android Package Installer
If transferring the file directly to your phone:

1. **Copy the APK** to your phone's internal storage via USB or local file sharing.
2. **Grant One-Time Sideloading Permission**:
   - Open your device's **Files** app.
   - Tap the APK file.
   - When prompted with *"For your security, your phone is not allowed to install unknown apps from this source"*, tap **Settings**.
   - Enable **Allow from this source**.
3. **Install**:
   - Tap **Install** on the system confirmation dialog.
4. **Revoke Sideloading Permission Immediately**:
   - Once installation finishes, immediately go to:
     `Settings > Apps > Special app access > Install unknown apps`
   - Select the file manager/browser you used and turn **Allow from this source** back **OFF**.
   - *Never leave global sideload permissions permanently enabled.*

---

## 4. Post-Installation Verification

Confirm that the application installed correctly and adheres to sandbox constraints:

```bash
# Query package status and version
adb shell dumpsys package app.revanced.android.photos | grep -E "versionName|versionCode|flags"
```

Verify that internal content providers are not accessible to external applications:
```bash
adb shell content query --uri content://app.revanced.android.photos.freeable_space
# Expected response: java.lang.SecurityException
```

---

## 5. Uninstallation and Local Data Preservation

To uninstall the application:
- On your phone: Long-press the **Photos** app icon > Tap **App info** > Tap **Uninstall**.
- Or via ADB:
  ```bash
  adb uninstall app.revanced.android.photos
  ```

### What Happens to Your Media:
- **Local Photos on Device Storage**: Any camera pictures and video files residing in `/sdcard/DCIM/Camera` or other public storage folders **remain completely untouched and safe**. Sideloaded apps do not delete your device media when uninstalled.
- **App-Private Data**: Cached thumbnails, search indexes, and in-app draft edits located in `/data/user/0/app.revanced.android.photos/` will be deleted by the Android OS.
- **Cloud Media**: Photos already synced to your Google Account remain stored in your Google cloud library.
