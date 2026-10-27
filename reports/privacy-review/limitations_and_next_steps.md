# Verification Boundaries, Residual Risks, and Next Steps — ReVanced Google Photos

**Target Package:** `app.revanced.android.photos`  
**Application Version:** `7.45.0.807274941` (VersionCode: `50709986`)  
**Workspace:** `./`

---

## 1. Technical Audit Boundaries (What Was and Was Not Tested)

In conducting this privacy and security audit, strict non-destructive constraints were adhered to. Users must understand the technical limits of client-side inspection:

### What Was Independently Verified
1. **Manifest and Sandboxing**: Verified that all application content providers are unexported and inaccessible to external apps on the device (`SecurityException` confirmed via ADB).
2. **Bytecode / Smali Analysis**: Disassembled all 16 DEX files; mapped network request classes, upload dispatchers, and intent handlers; confirmed zero third-party commercial tracking SDKs and zero data forwarding hooks.
3. **Endpoint Extraction**: Categorized all 83 URLs; established that non-Google domains are either dead code (`redgifs.com`), explicit partner dialogs (`capcut`), or user-triggered about links (`revanced.app`).
4. **Live Socket Audit**: Confirmed active kernel sockets connect solely to Google LLC (AS15169) over port 443.
5. **Crash Logcat Diagnostics**: Captured and diagnosed the exact `ActivityNotFoundException` stack trace behind the "Manage Google" button failure.

### What Remains Unknown or Untested
1. **gRPC Protocol Buffer Payload Decoding**: Communication between Google Photos and Google servers uses encrypted HTTP/2 TLS and serialized Protocol Buffers. Deep packet inspection (MITM interception) was not performed because injecting a custom CA cert into Android 17's system trust store would require root filesystem modifications and breaks Google Photos' certificate pinning. Static analysis confirms code integrity, but exact live payload bytes were not decrypted in transit.
2. **Google Server-Side Logging & Quota Policy**: We cannot inspect internal Google backend telemetry. Whether Google logs device inconsistencies (e.g., Pixel 1 model strings combined with modern Linux kernel versions) or plans future account flagging remains solely within Google's purview.
3. **MicroG Package Boundary**: This audit inspected `app.revanced.android.photos`. The companion app `app.revanced.android.gms` (MicroG) is an independent APK. While Google Photos communicates with it safely, the overall security of account tokens depends on the authenticity of the MicroG build installed on the device.

---

## 2. Residual Privacy and Security Risks

| Risk Category | Severity | Detailed Description | Mitigating Factor |
| :--- | :--- | :--- | :--- |
| **MicroG Dependency** | Medium | All Google account authentication tokens pass through the local MicroG GmsCore service. | Use only official, verified MicroG / ReVanced GmsCore builds. |
| **Account Flagging Risk** | Medium | Google could theoretically detect client spoofing (Pixel 1 identity on Android 17) and restrict quota or ban accounts. | Use a secondary/test Google account for media backup if absolute safety is required. |
| **Manual Update Burden** | Low | Patched apps do not receive automatic security updates via Google Play. | Regularly rebuild the APK when new stable releases and security patches emerge. |
| **Non-Market Signing Key** | Low | The app is signed with a local maintenance RSA key, disabling Play Protect signature verification. | Normal and unavoidable for modified APKs; local key is kept strictly within the workspace. |

---

## 3. Prioritized Actionable Roadmap

### Milestone 1: Apply the "Manage Google" Crash Fix
- **Action**: Modify `smali_classes6/bfcn.smali` to catch `ActivityNotFoundException` when invoking `startActivityForResult`, falling back to opening `https://myaccount.google.com/` in the browser.
- **Impact**: Completely eliminates the only known user-facing crash without introducing regressions.

### Milestone 2: Prune Dormant Shared Extension Code
- **Action**: In future maintenance builds, remove `app/revanced/extension/shared/fixes/redgifs/` from the build script to clean unused dead code from the compiled DEX files.
- **Impact**: Reduces APK size and eliminates extraneous non-Google domain strings.

### Milestone 3: Periodic MicroG Token Hygiene
- **Action**: Regularly inspect connected accounts and permissions within MicroG settings (`app.revanced.android.gms`) to verify that only authorized Google services hold OAuth permissions.
- **Impact**: Ensures defense-in-depth for Google Account credentials.
