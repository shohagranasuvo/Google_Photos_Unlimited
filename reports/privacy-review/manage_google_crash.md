# Root Cause Analysis and Remediation Plan: "Manage Google" Crash

**Target Package:** `app.revanced.android.photos`  
**Crash Timestamp (Device):** `10-28 02:22:38.001`  
**Device Context:** Google Pixel 8 (`[REDACTED-PIXEL8-SERIAL]`, Android 17 / API 37, PID `18929`)

---

## 1. Problem Statement

When the user taps the account profile avatar in the upper right corner of ReVanced Google Photos and selects **"Manage your Google Account"**, the application crashes immediately to the Android home screen.

---

## 2. On-Device Crash Evidence

Inspection of the Android system crash logcat (`adb logcat -b crash -d`) captured the exact unhandled exception:

```
10-28 02:22:38.001 18929 18929 E AndroidRuntime: FATAL EXCEPTION: main
10-28 02:22:38.001 18929 18929 E AndroidRuntime: Process: app.revanced.android.photos, PID: 18929
10-28 02:22:38.001 18929 18929 E AndroidRuntime: android.content.ActivityNotFoundException: No Activity found to handle Intent { act=app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS xflg=0x4 pkg=app.revanced.android.gms (has extras) }
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at android.app.Instrumentation.checkStartActivityResult(Instrumentation.java:2491)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at android.app.Instrumentation.execStartActivity(Instrumentation.java:2063)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at android.app.Activity.startActivityForResult(Activity.java:6148)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at ql.startActivityForResult(PG:4)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at bjfz.startActivityForResult(PG:5)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at android.app.Activity.startActivityForResult(Activity.java:6104)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at ql.startActivityForResult(PG:2)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at bjfz.startActivityForResult(PG:2)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at bfcn.b(PG:31)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at bfdn.a(PG:49)
10-28 02:22:38.001 18929 18929 E AndroidRuntime: 	at bfrl.onClick(PG:1041)
```

---

## 3. Disassembly & Code Path Analysis

### Construction of the Intent (`smali_classes6/bbka.smali`)
In class `bbka.smali`, method `aC(Ljava/lang/String;Lbnro;Lbliv;)Landroid/content/Intent;`:

```smali
new-instance p2, Landroid/content/Intent;
invoke-direct {p2}, Landroid/content/Intent;-><init>()V
const-string v0, "app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS"
invoke-virtual {p2, v0}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;
const-string v0, "app.revanced.android.gms"
invoke-virtual {p2, v0}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;
```

**Origin**: In stock Google Photos, this method targets `com.google.android.gms.accountsettings.action.VIEW_SETTINGS` with package `com.google.android.gms`. The ReVanced GmsCore patch performed a bulk package string replacement, redirecting it to `app.revanced.android.gms`.

### Execution of the Intent (`smali_classes6/bfcn.smali`)
In class `bfcn.smali`, method `b(Landroid/view/View;Ljava/lang/String;Lbnro;)V`:

```smali
    # Method lines 30-34
    :cond_1
    const p0, 0xc884
    invoke-virtual {v0, p1, p0}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent;I)V
    return-void
```

- Notice that `startActivityForResult` is invoked directly on the UI thread without any surrounding `try { ... } catch (ActivityNotFoundException e)` block.

### On-Device Resolution Test
A live query on the Pixel 8 verified that MicroG (`app.revanced.android.gms`) does not provide this activity:
```bash
adb shell pm resolve-activity -a "app.revanced.android.gms.accountsettings.action.VIEW_SETTINGS" -p "app.revanced.android.gms"
# Output: No activity found
```

Because the target package is explicitly set to `app.revanced.android.gms` and no matching activity exists, Android's `Instrumentation` aborts with `ActivityNotFoundException`, terminating the app.

---

## 4. Remediation Plan

To resolve the crash without requiring modifications to the external MicroG package, `bfcn.smali` can be patched defensively.

### Recommended Solution: Safe Web Fallback
Wrap the `startActivityForResult` invocation in a `try / catch (Landroid/content/ActivityNotFoundException;)` block. When caught, create and launch an `android.intent.action.VIEW` Intent pointing to `https://myaccount.google.com/`.

#### Smali Patch Design
```smali
    :cond_1
    const p0, 0xc884

    :try_start_intent
    invoke-virtual {v0, p1, p0}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent;I)V
    :try_end_intent
    .catch Landroid/content/ActivityNotFoundException; {:try_start_intent .. :try_end_intent} :catch_activity_not_found

    return-void

    :catch_activity_not_found
    # Fallback to browser intent
    new-instance p1, Landroid/content/Intent;
    const-string p0, "android.intent.action.VIEW"
    const-string p2, "https://myaccount.google.com/"
    invoke-static {p2}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
    move-result-object p2
    invoke-direct {p1, p0, p2}, Landroid/content/Intent;-><init>(Ljava/lang/String;Landroid/net/Uri;)V
    const/high16 p0, 0x10000000
    invoke-virtual {p1, p0}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;
    invoke-virtual {v0, p1}, Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V
    return-void
```

### Benefits of this Fix
1. Eliminates the fatal crash completely.
2. Directly delivers the intended user outcome (opening the user's Google Account settings in Chrome or their default browser).
3. Completely self-contained within the Photos APK with no risk of breaking photo backup or MicroG sync.
