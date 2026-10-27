# Security Policy

## 1. Supported Versions

Security maintenance and audit tracking apply to the following research target:

| Version | Package | Support Status |
| :--- | :--- | :--- |
| `7.45.0.807274941` | `app.revanced.android.photos` | **Audited / Remediated** |
| Older builds | `app.revanced.android.photos` | Deprecated |
| Upstream stock | `com.google.android.apps.photos` | Out of Scope (Report to Google VRP) |

---

## 2. Reporting a Vulnerability

If you identify a security vulnerability, data leakage vector, or privacy issue in this repository's code, scripts, or documented remediation steps:

1. **Do NOT open a public GitHub issue.** Public issues are indexed immediately and visible to all users.
2. Submit a private vulnerability report via **GitHub Private Vulnerability Reporting** (Security tab > "Report a vulnerability").
3. Alternatively, contact the maintainer directly via email: `shohagranasuvo@gmail.com` with the subject tag `[SECURITY DISCLOSURE] Google_Photos_Unlimited`.

---

## 3. Responsible Disclosure Guidance

When submitting a disclosure:
- Provide clear steps to reproduce the vulnerability.
- Include a sanitized proof of concept (PoC).
- **CRITICAL WARNING**: **Never submit real Google Account credentials, OAuth refresh tokens, session cookies, private URLs, personal photos, or unredacted device hardware serials.** Any submission containing real sensitive user data will be deleted immediately.
- Allow 30 days for remediation before publishing any findings publicly.

---

## 4. Upstream Vulnerabilities

- For vulnerabilities in **official Google Photos**, please report directly to the [Google Vulnerability Reward Program (VRP)](https://bughunter.withgoogle.com/).
- For vulnerabilities in **MicroG GmsCore**, please report directly to the [MicroG Project Security Team](https://github.com/microg/GmsCore/security).
- For vulnerabilities in **ReVanced Patcher**, please report to the [ReVanced Patcher Repository](https://github.com/ReVanced/revanced-patcher/security).
