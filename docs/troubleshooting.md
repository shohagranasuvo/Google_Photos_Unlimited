# Troubleshooting and Known Issues

This guide addresses common errors encountered during installation, configuration, and day-to-day use.

---

## 1. Known Issue: "Manage Google Account" Crash

### Description
Tapping your profile picture and selecting **"Manage your Google Account"** causes an immediate crash to the home screen.

### Root Cause
In `smali_classes6/bfcn.smali`, the method `b` executes `startActivityForResult()` with an intent targeted at:
```
Action:  app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS
Package: app.revanced.android.gms
```
The ReVanced GmsCore redirect patch redirected this intent to MicroG, but MicroG does not declare or implement this account settings activity. The resulting `android.content.ActivityNotFoundException` is unhandled on the UI thread, causing a fatal crash.

### Workaround
- **Do not tap "Manage your Google Account" inside the app.**
- To manage your Google Account, open Chrome or your preferred browser and visit:
  [https://myaccount.google.com/](https://myaccount.google.com/)

### Code Remediation (For Builders)
In `smali_classes6/bfcn.smali`, wrap the `startActivityForResult` invocation in a `try / catch (Landroid/content/ActivityNotFoundException;)` block that falls back to launching `https://myaccount.google.com/` in the user's default browser. (See [`reports/privacy-review/manage_google_crash.md`](../reports/privacy-review/manage_google_crash.md) for the exact smali patch).

---

## 2. Installation Errors

### `INSTALL_FAILED_UPDATE_INCOMPATIBLE`
- **Cause**: An existing version of Google Photos or a previous ReVanced build is installed with a different cryptographic signing key.
- **Resolution**: You must back up any unsynced local media, completely uninstall the existing `app.revanced.android.photos` package, and then reinstall the new APK.

### `INSTALL_PARSE_FAILED_NO_CERTIFICATES`
- **Cause**: The APK was aligned after signing, corrupting the v2/v3 signature block.
- **Resolution**: Always run `zipalign -p -f 4 in.apk out.apk` **before** executing `apksigner sign`.

---

## 3. MicroG & Authentication Errors

### "Please install MicroG / GmsCore" Prompt
- **Cause**: MicroG (`app.revanced.android.gms`) is not installed on the device or its package name does not match the patched APK's target.
- **Resolution**: Install the compatible ReVanced GmsCore build from [ReVanced Releases](https://github.com/ReVanced/GmsCore/releases).

### Sign-in Infinite Spinner / Auth Failure
- **Cause**: MicroG is being killed in the background by aggressive battery optimizations, or network connectivity to `oauth2.googleapis.com` is blocked.
- **Resolution**:
  1. Go to `Settings > Apps > MicroG / GmsCore > Battery` and select **Unrestricted**.
  2. Verify that your device date and time are set automatically (SSL handshake failure occurs if clocks drift).

---

## 4. Background Backup Not Progressing

### Cause
Many modern Android ROMs (especially Xiaomi/MIUI, Samsung/OneUI, and Huawei/EMUI) terminate long-running background worker threads (`MediaUploaderWorker`) after screen-off.

### Resolution
1. Set battery usage for both **Photos** (`app.revanced.android.photos`) and **MicroG** (`app.revanced.android.gms`) to **Unrestricted**.
2. If using an OEM device with aggressive killing, follow device-specific setup steps on [dontkillmyapp.com](https://dontkillmyapp.com/).
