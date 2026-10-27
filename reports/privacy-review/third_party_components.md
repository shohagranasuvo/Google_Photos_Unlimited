# Third-Party Components and Embedded Libraries — ReVanced Google Photos

**Target Package:** `app.revanced.android.photos`  
**Application Version:** `7.45.0.807274941` (VersionCode: `50709986`)  
**Workspace:** `./`

---

## 1. Overview of Third-Party & Injected Code

This audit evaluated all non-stock components incorporated into the patched application, distinguishing between:
1. **ReVanced Injected Patches** (Bytecode hooks added by the ReVanced patcher).
2. **ReVanced Shared Extension Libraries** (Utility classes bundled into DEX during patch compilation).
3. **Commercial Analytics / Advertising SDKs** (Inspection for third-party trackers).
4. **MicroG GmsCore Dependencies** (External service integration).

---

## 2. ReVanced Patches Decompilation and Analysis

### A. GmsCoreSupport (`app.revanced.patches.shared.microg.GmsCoreSupport`)
- **Purpose**: Redefines hardcoded Google Play Services package identifiers (`com.google.android.gms`) to point to MicroG (`app.revanced.android.gms`).
- **Implementation**: Patches client instantiation routines, content provider URIs, and authorization scopes to allow Google Photos to function on devices without native Google Play Services.
- **Privacy Assessment**: Safe. Routes authentication to local MicroG rather than proprietary Google framework. Does not store or forward tokens externally.

### B. SpoofProvider (`app.revanced.patches.photos.spoof`)
- **Purpose**: Overrides Android `Build` properties (`MANUFACTURER`, `MODEL`, `PRODUCT`, `BOARD`) so the application self-identifies as a Google Pixel (2016).
- **Implementation**: Uses dynamic bytecode redirection whenever `Build` field access occurs within Google Photos upload verification methods.
- **Privacy Assessment**: Harmless from a data leakage perspective. Its sole effect is altering the device identification string sent to Google servers during photo backup.

### C. ReVanced Settings & About (`app.revanced.extension.shared.settings`)
- **Purpose**: Adds an in-app configuration menu allowing users to view ReVanced patch versions and links to the open-source community repository.
- **Implementation**: Extends Android preference screens (`PreferenceFragmentCompat`).
- **Privacy Assessment**: Safe. Does not monitor user activity or phone usage.

---

## 3. Investigation of Dead Code: `api.redgifs.com`

During static string extraction, `api.redgifs.com` was identified in class:
`app.revanced.extension.shared.fixes.redgifs.BaseFixRedgifsApiPatch`

### Detailed Smali Trace
1. **Origin**: ReVanced employs a monolithic shared extension library (`app.revanced.extension.shared`) that contains common fix implementations across all ReVanced patches (e.g. YouTube, Reddit, Boost, Twitter, Google Photos).
2. **Analysis**:
   - The class `BaseFixRedgifsApiPatch` provides an OAuth token interceptor specifically for third-party Reddit clients (such as Boost for Reddit) to restore animated GIF playback via the Redgifs API.
   - A search of all 16 DEX files confirmed that **no subclasses, references, or callers** instantiate or execute `BaseFixRedgifsApiPatch` in this Google Photos APK.
3. **Conclusion**: This is harmless dead code resulting from the compiler bundling shared utility libraries. It never runs and poses zero privacy risk.

---

## 4. Audit for Commercial Advertising and Tracking SDKs

The application was scanned for signatures of common commercial tracking, advertising, and user profiling frameworks:

| Tracking / Ad SDK | Signature Checked | Detection Status | Verdict |
| :--- | :--- | :--- | :--- |
| **Facebook Graph / Meta SDK** | `com.facebook.ads`, `com.facebook.appevents` | **NOT FOUND** | No Facebook tracking |
| **AppsFlyer** | `com.appsflyer.*` | **NOT FOUND** | No attribution tracking |
| **Adjust** | `com.adjust.sdk.*` | **NOT FOUND** | No mobile attribution |
| **Kochava** | `com.kochava.base.*` | **NOT FOUND** | No measurement tracking |
| **Flurry / Yahoo** | `com.flurry.android.*` | **NOT FOUND** | No behavioral analytics |
| **Google AdMob** | `com.google.android.gms.ads.*` | **NOT FOUND** | No in-app advertisements |
| **Unity Ads / IronSource** | `com.unity3d.ads`, `com.ironsource` | **NOT FOUND** | No ad mediation SDKs |

### First-Party Google Telemetry
The application includes standard Google diagnostic libraries:
- `com.google.android.libraries.performance.primes` (App startup and frame rate metrics).
- `com.google.android.gms.clearcut` (Google system error and crash logging).
These dispatch diagnostics exclusively to Google endpoints (`play.googleapis.com/log`).

---

## 5. Security Summary of Third-Party Elements

All third-party code in the APK consists solely of the ReVanced open-source modification framework. There are no proprietary advertising trackers, data brokers, or malicious backdoors embedded in the package.
