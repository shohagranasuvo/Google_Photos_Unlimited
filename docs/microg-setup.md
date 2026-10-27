# MicroG Architecture and Configuration Guide

This guide details the role of MicroG GmsCore, how to configure account synchronization, and the security implications of third-party Play Services implementations.

---

## 1. What is MicroG and Why is it Required?

Stock Google Photos relies extensively on proprietary Google Play Services (`com.google.android.gms`) for authentication, push tokens, and OAuth2 token issuance.

Because this modified package uses a decoupled package name (`app.revanced.android.photos`) and is signed with a non-Google private key:
1. Native Google Play Services rejects Binder IPC connections from the app due to cryptographic signature mismatch.
2. The ReVanced `GmsCoreSupport` patch rewrites all internal GMS references from `com.google.android.gms` to `app.revanced.android.gms`.
3. An open-source, decoupled Play Services implementation—**MicroG GmsCore**—must be installed on the device to handle OAuth2 handshakes.

---

## 2. Obtaining and Verifying MicroG

> [!IMPORTANT]
> **Never download MicroG APKs from random mirrors, APK aggregators, or unverified chat channels.**
> MicroG requests high-privilege access on your device (handling Google account credentials and push notifications). Always obtain it directly from the official upstream project or the verified ReVanced team repository.

### Official Upstream Resources:
- **Official MicroG Downloads**: [https://github.com/microg/GmsCore/wiki/Downloads](https://github.com/microg/GmsCore/wiki/Downloads)
- **ReVanced GmsCore (Decoupled Variant)**: [https://github.com/ReVanced/GmsCore/releases](https://github.com/ReVanced/GmsCore/releases)

### Verify Before Sideloading:
Verify the APK certificate digest before installing:
```bash
apksigner verify --verbose --print-certs path/to/GmsCore.apk
```
Ensure the signing fingerprint matches the official public key announced by the release maintainers.

---

## 3. Configuration and Account Sign-In

1. **Install MicroG GmsCore** (`app.revanced.android.gms`).
2. **Battery Optimization Whitelist**:
   - Navigate to: `Settings > Apps > MicroG / GmsCore > Battery > Set to "Unrestricted"`.
   - On aggressive OEM skins (e.g. MIUI, EMUI, ColorOS), background task termination will break background photo backups. (Refer to [dontkillmyapp.com](https://dontkillmyapp.com/) for device-specific instructions).
3. **Sign In**:
   - Open **MicroG Settings** from your app drawer.
   - Tap **Account > Add Google Account**.
   - Authenticate with your Google credentials.
4. **Cloud Messaging / Push Notifications**:
   - Confirm in MicroG Settings that **Google Cloud Messaging** is registered for `app.revanced.android.photos`.

---

## 4. Security & Architecture Disclosures

- **Separate Maintainership**: MicroG and Google Photos are entirely independent software packages maintained by different developers.
- **Credential Flow**: Your Google Account password is submitted over HTTPS directly to `oauth2.googleapis.com` via MicroG. Photos itself never receives or sees your Google password; it receives only scoped OAuth access tokens.
- **No Guarantee of Backend Operation**: Using MicroG does not guarantee that Google's servers will indefinitely accept upload sessions or that Google Photos will never encounter authentication deprecation.
- **Account Protection**: For research and testing purposes, it is recommended to use a dedicated secondary Google account rather than your primary personal account.
