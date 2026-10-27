# Data Flow and Sensitive Information Handling — ReVanced Google Photos

**Target Package:** `app.revanced.android.photos`  
**Application Version:** `7.45.0.807274941` (VersionCode: `50709986`)  
**Target Device:** Google Pixel 8 (`[REDACTED-PIXEL8-SERIAL]`, Android 17 / API 37, UID `10385`)

---

## 1. Overview of Data Flows

This audit mapped the movement of sensitive user data across four primary boundaries:
1. **Local Media & EXIF Metadata**: User images, videos, timestamps, GPS tags, and camera metadata.
2. **Account Credentials & Auth Tokens**: Google Account tokens, OAuth2 refresh tokens, and session headers.
3. **Hardware / Device Identity**: Device model, manufacturer, build fingerprint, and Android OS version.
4. **Local Databases & File Storage**: SQLite / Room databases, temporary cache files, and content providers.

---

## 2. Personal Media & EXIF Metadata Flow

### Ingestion and Processing
- Google Photos accesses media via Android's `MediaStore` and `MANAGE_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` permissions.
- In-memory processing extracts EXIF metadata (camera model, aperture, focal length, ISO, and GPS coordinates if location permissions are active).
- Photos are compressed or packaged into byte streams for upload according to user backup preferences (Storage Saver vs Original Quality).

### Network Dispatch
- Media streams are serialized using Protocol Buffers (gRPC) and transmitted via HTTP/2 over TLS directly to `photos-pa.googleapis.com` and `photos.googleapis.com`.
- **Exfiltration Audit**: Smali inspection of upload pipeline classes (`bbyk`, `bcfe`, and `MediaUploaderWorker`) verified that media byte buffers are never duplicated, copied to secondary network dispatchers, or forwarded to external servers.

---

## 3. Account Authentication & MicroG Token Flow

### Authentication Architecture
Because this patched APK uses a decoupled package name (`app.revanced.android.photos`), it cannot directly use the proprietary Google Play Services framework on unrooted systems due to signature mismatch restrictions. Instead, it relies on MicroG (`app.revanced.android.gms`).

```
+------------------------------------+
|    app.revanced.android.photos     |
|   (Google Photos ReVanced APK)     |
+-----------------+------------------+
                  |
         Android IPC / Binder
                  |
+-----------------v------------------+
|      app.revanced.android.gms      |
|           (MicroG GmsCore)         |
+-----------------+------------------+
                  |
              HTTPS / TLS
                  |
+-----------------v------------------+
|     oauth2.googleapis.com          |
|      (Google OAuth2 API)           |
+------------------------------------+
```

### Verification Findings
- Account authentication tokens are requested via Android Binder calls targeting `app.revanced.android.gms`.
- MicroG exchanges user credentials directly with `oauth2.googleapis.com`.
- Tokens returned to Google Photos are cached in app-private encrypted shared preferences (`/data/user/0/app.revanced.android.photos/shared_prefs/`).
- No credentials or authentication cookies are logged in logcat or stored in world-readable storage.

---

## 4. Hardware Spoofing & Identity Mutation (Pixel 1 Spoof)

### How Spoofing Operates
The application modifies system build properties via ReVanced's `SpoofProvider` patch:
- `Build.MANUFACTURER`: Spoofed to `"Google"`
- `Build.PRODUCT`: Spoofed to `"marlin"`
- `Build.MODEL`: Spoofed to `"Pixel"`
- `Build.DEVICE`: Spoofed to `"marlin"`
- `Build.BOARD`: Spoofed to `"marlin"`

### Impact on Media Uploads
When Google Photos initializes an upload session with Google's servers, it includes the device model in the client context header (e.g., `X-Google-GFE-Client-Model: Pixel`). Google's backend honors this device identifier and tags uploaded media as exempt from Google One/Drive quota limits ("Unlimited original quality backup for Pixel (2016)").

### Security & Privacy Implications
- **No Client-Side Quota Enforcement**: The application itself does not enforce storage quotas; the storage meter displayed in the UI is rendered from responses returned by Google's server.
- **Server-Side Revocation Risk**: Google can at any time introduce cryptographic attestation (such as Play Integrity hardware-backed key attestation) to verify genuine Pixel hardware. If enabled by Google, the upload stream would simply be rejected or counted against the 15GB account quota.

---

## 5. Local Database & Content Provider Boundaries

### Content Provider Access Control
In earlier audit phases, potential misconfigurations in manifest-declared content providers were remediated by enforcing `android:exported="false"`. Live ADB testing verified that inter-app data access is strictly blocked:

```bash
adb shell content query --uri content://app.revanced.android.photos.freeable_space
# Result: SecurityException: Permission Denial: not exported from UID 10385

adb shell content query --uri content://app.revanced.android.photos.partnercontentprovider
# Result: SecurityException: Permission Denial: not exported from UID 10385
```

### Local Storage Isolation
- SQLite databases (e.g. `gphotos0.db`, `local_media.db`) reside exclusively within the application's protected sandbox (`/data/user/0/app.revanced.android.photos/databases/`).
- File permissions are restricted to Linux UID `10385` (`rwx------`).
- No sensitive user images or database files are written to external public storage (`/sdcard/` or `/storage/emulated/0/Download/`).
