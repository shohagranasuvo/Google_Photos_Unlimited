# Release Policy and Distribution Assets

## 1. Legal Release Assets (No Proprietary APKs)

In compliance with copyright laws and GitHub policies:
- **No proprietary Google APK binaries are hosted directly in this repository.**
- Instead, this repository publishes **100% legal, lightweight patch assets** and **custom ReVanced source bundles**:

| Asset | Purpose | Format |
| :--- | :--- | :--- |
| **`google-photos-unlimited-patch-v7.45.0.zip`** | **Option B**: Standalone 1-Click Builder for PC & Termux | ZIP containing `patch.py` and security payload |
| **`patches.rvp`** / **`patches-7.45.0.rvp`** | **Option C**: Custom ReVanced Manager patch package | ReVanced Patch Bundle |
| **`patches.json`** | **Option C**: Patch metadata and compatibility index | JSON format for ReVanced Manager API |

---

## 2. Option B: Standalone 1-Click Patch Tool (PC or Termux)

If you have a computer or use Termux on Android:
1. Download **`google-photos-unlimited-patch-v7.45.0.zip`** from [GitHub Releases](https://github.com/shohagranasuvo/Google_Photos_Unlimited/releases/latest).
2. Extract the ZIP file.
3. Place a clean Google Photos APK (`base.apk`) in the extracted folder.
4. Run:
   ```bash
   python3 patch.py base.apk
   ```
5. In 5 seconds, the script injects all security patches, aligns, and signs your ready-to-use **`GooglePhotos_Unlimited_Remediated.apk`**!

---

## 3. Option C: ReVanced Manager Custom Source

To use these patches directly inside **ReVanced Manager** on your phone:
1. Open ReVanced Manager > **Settings** > **Sources**.
2. Configure:
   - **Patches organization**: `shohagranasuvo`
   - **Patches source**: `Google_Photos_Unlimited`
3. Restart ReVanced Manager, select Google Photos, and tap **Patch**!

For full walkthrough, read [**docs/custom-patch-source.md**](../docs/custom-patch-source.md).
