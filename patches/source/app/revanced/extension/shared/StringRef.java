package app.revanced.extension.shared;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import app.revanced.extension.shared.StringRef$;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /run/media/shohag-rana/01DC49173A549750/ALL from Ubuntu/Unlimited photos/google-photos-unlimited-patch-v7.45.0/payload/classes7.dex */
public class StringRef {
    private static String packageName;
    private static Resources resources;
    private boolean resolved;

    @NonNull
    private String value;
    private static final Map<String, StringRef> strings = Collections.synchronizedMap(new HashMap());

    @NonNull
    public static final StringRef empty = constant("");

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$toString$1() {
        return "Could not resolve resources!";
    }

    @NonNull
    public static StringRef sfc(@NonNull String str) {
        Map<String, StringRef> map = strings;
        StringRef stringRef = map.get(str);
        if (stringRef != null) {
            return stringRef;
        }
        StringRef stringRef2 = new StringRef(str);
        map.put(str, stringRef2);
        return stringRef2;
    }

    @NonNull
    public static StringRef sf(@NonNull String str) {
        return new StringRef(str);
    }

    @NonNull
    public static String str(@NonNull String str) {
        return sfc(str).toString();
    }

    @NonNull
    public static String str(@NonNull String str, Object... objArr) {
        return String.format(str(str), objArr);
    }

    @NonNull
    public static StringRef constant(@NonNull String str) {
        StringRef stringRef = new StringRef(str);
        stringRef.resolved = true;
        return stringRef;
    }

    public StringRef(@NonNull String str) {
        this.value = str;
    }

    @NonNull
    public String toString() {
        if (!this.resolved) {
            if (resources == null || packageName == null) {
                Context context = Utils.getContext();
                resources = context.getResources();
                packageName = context.getPackageName();
            }
            this.resolved = true;
            Resources resources2 = resources;
            if (resources2 != null) {
                int identifier = resources2.getIdentifier(this.value, "string", packageName);
                if (identifier == 0) {
                    Logger.printException(new StringRef$.ExternalSyntheticLambda0(this));
                } else {
                    this.value = resources.getString(identifier);
                }
            } else {
                Logger.printException(new StringRef$.ExternalSyntheticLambda1());
            }
        }
        return this.value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$toString$0() {
        return "Resource not found: " + this.value;
    }
}
