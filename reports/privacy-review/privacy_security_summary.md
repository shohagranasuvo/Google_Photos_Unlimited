# Privacy and Security Audit Summary — ReVanced Google Photos

**Target Package:** `app.revanced.android.photos`  
**Application Version:** `7.45.0.807274941` (VersionCode: `50709986`)  
**Installed Target Binary SHA-256:** `28a47789732c55a60f37d260d4b583ee0ca01657fa44ee5904a33caac14beae5`  
**Host / Device:** Google Pixel 8 (`[REDACTED-PIXEL8-SERIAL]`, Android 17 / API 37, UID `10385`)  
**Audit Scope:** On-device runtime verification, static smali disassembly, network connection audit, and crash root-cause investigation.

---

## 1. Executive Summary

This comprehensive privacy and security audit examined the remediated build of ReVanced Google Photos installed on the connected Google Pixel 8. The evaluation focused specifically on whether sensitive user data (photos, videos, account credentials, metadata, or device identifiers) is exfiltrated to unauthorized third-party servers, whether community/ReVanced servers receive traffic, and the root cause of an application crash when tapping "Manage Google".

### Key Takeaways
1. **No Evidence of Covert Data Exfiltration**: Disassembly of all 16 DEX files and runtime socket inspections confirm that user media (photos and videos) and account credentials are transmitted exclusively to Google infrastructure (`*.google.com`, `*.googleapis.com`, `*.googleusercontent.com`) and the local MicroG GmsCore provider (`app.revanced.android.gms`).
2. **Absence of Commercial Trackers**: The application contains no embedded third-party ad networks or surveillance SDKs (such as Facebook Graph, AppsFlyer, Adjust, Kochava, or Flurry).
3. **Third-Party Domain Context**: Extracted non-Google URLs were verified to be non-threatening:
   - `api.redgifs.com`: Dead, dormant library code in ReVanced's shared extensions with **zero** invocation sites in the Photos codebase.
   - `capcut-yt.onelink.me`: Legitimate official Google video editor partner integration dialog.
   - `api.revanced.app`: Query endpoint confined to user-initiated clicks within the ReVanced "About" menu.
   - `dontkillmyapp.com`: Informational link in MicroG setup guidance for aggressive OEM battery managers.
4. **"Manage Google" Crash Root Cause Confirmed**: The crash is caused by an unhandled `android.content.ActivityNotFoundException` when launching an Intent with action `app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS` targeted at `app.revanced.android.gms`. MicroG does not implement this account settings activity, causing an instant FATAL EXCEPTION on the main thread.
5. **Pixel 1 Quota Spoofing Reality**: The Pixel 1 spoofing operates at the client level by intercepting device model queries (`Build.MANUFACTURER`, `Build.MODEL`, `Build.PRODUCT`). Google's backend currently credits these uploads as quota-free based on the client handshake; however, Google maintains complete server-side control and could enforce hardware-backed attestation (KeyMint/Play Integrity) at any time.

---

## 2. Answers to Core Threat Assessment Questions

### Question 1: Does the installed application transmit personal photos, videos, or account credentials to third parties other than Google and MicroG?
**Answer: NO.**
Exhaustive decompilation of network dispatchers, upload worker tasks, and active socket inspections confirm that media upload streams and authentication tokens are routed strictly to:
- Google's upload servers (`photos.googleapis.com`, `photos-pa.googleapis.com`, `*.googleusercontent.com`).
- The local MicroG GmsCore provider (`app.revanced.android.gms`) via standard Android IPC / ContentProviders for OAuth2 token acquisition.
No external telemetry endpoints, proxy servers, or third-party cloud buckets receive media files or user credentials.

### Question 2: Does the app contact ReVanced or community servers during normal operation?
**Answer: NO.**
During background sync, photo browsing, editing, and media backup, the app makes **zero** requests to ReVanced or community infrastructure. The sole reference to `api.revanced.app` is located in `AboutLinksRoutes.smali` (`app/revanced/extension/shared/settings/about/AboutLinksRoutes`), which is executed only when the user explicitly opens the ReVanced "About" menu and clicks social links.

### Question 3: Are photo uploads subject to server-side Google account quota enforcement, or is the Pixel 1 unlimited upload status purely a client-side display?
**Answer: Quota bypass relies on Google server-side acceptance of client-reported model strings; it is not purely cosmetic, but entirely server-dependent.**
The ReVanced patch injects a hook (`SpoofProvider`) intercepting Android system properties to report the device as a Google Pixel 1 ("marlin"). Google Photos transmits these identity properties in the gRPC/Protobuf request headers during upload initialization. Google's cloud backend currently evaluates these headers and exempts the uploaded media from the user's Google Drive/One 15GB storage quota (as observed by the user). However, this privilege is granted server-side by Google's backend trusting the reported metadata. If Google deploys Play Integrity API or server-side hardware-backed cryptographic attestation for Photos uploads, this mechanism will cease functioning without prior notice.

### Question 4: Does the application send analytics or telemetry to third parties?
**Answer: NO.**
Analytics and telemetry collected by the app (crash metrics, UI interaction funnels, usage logs) are handled by Google's native ClearcutLogger and Firebase libraries and dispatched exclusively to Google endpoints (`play.googleapis.com/log`, `firebaseinstallations.googleapis.com`). There are no third-party telemetry collectors present in the APK.

### Question 5: What causes the "Manage Google" crash, and what is the minimal fix?
**Answer: Unhandled `ActivityNotFoundException` when invoking an intent targeting MicroG's non-existent account settings activity.**
- *Mechanism*: In `smali_classes6/bfcn.smali` (method `b`), Google Photos calls `startActivityForResult(intent, 0xc884)` with an Intent built in `smali_classes6/bbka.smali` (`aC`). The ReVanced GmsCore redirect patch transformed the action and package to `app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS` and `app.revanced.android.gms`. MicroG does not implement this intent filter. Android throws `ActivityNotFoundException`, which is uncaught, terminating the process.
- *Minimal Fix*: Wrap the `startActivityForResult` invocation in `bfcn.smali` with a `try ... catch (Landroid/content/ActivityNotFoundException;)` block. In the catch handler, fall back to launching an `android.intent.action.VIEW` intent with the URL `https://myaccount.google.com/` in the user's default browser.

### Question 6: What are the remaining privacy and security risks of continuing to use this modified app?
**Answer: The primary residual risks are architectural and ecosystem-related:**
1. **MicroG Account Security**: MicroG (`app.revanced.android.gms`) handles the master OAuth refresh token. You must ensure that the installed MicroG build originates from a reputable, verified source.
2. **Account Flagging Risk**: Google's backend servers could theoretically detect that a device reporting as an original Pixel 1 (from 2016) is communicating with modern Android 17 / API 37 TLS fingerprints and carrier metadata, risking account suspension or storage quota revocation.
3. **No Automatic Security Patches**: Modified APKs do not receive updates via the Google Play Store. Security fixes must be manually audited and rebuilt.
4. **Third-Party Signing Key**: The APK is signed with a local debug/maintenance key, which is standard for side-loaded patched apps but forfeits Play Protect validation chains.

---

## 3. Related Reports

- Detailed Network Endpoints: [network_destinations.md](./network_destinations.md)
- Data Flow & Media Transmission: [data_flow_findings.md](./data_flow_findings.md)
- Third-Party & Shared Code Inspection: [third_party_components.md](./third_party_components.md)
- "Manage Google" Crash Analysis: [manage_google_crash.md](./manage_google_crash.md)
- Audit Limitations & Roadmap: [limitations_and_next_steps.md](./limitations_and_next_steps.md)
