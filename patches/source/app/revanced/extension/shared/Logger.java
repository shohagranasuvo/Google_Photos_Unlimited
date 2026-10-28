package app.revanced.extension.shared;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.revanced.extension.shared.settings.BaseSettings;
import app.revanced.extension.shared.settings.preference.LogBufferManager;
import java.io.PrintWriter;
import java.io.StringWriter;

/* loaded from: /run/media/shohag-rana/01DC49173A549750/ALL from Ubuntu/Unlimited photos/google-photos-unlimited-patch-v7.45.0/payload/classes7.dex */
public class Logger {
    private static final String LOGGER_CLASS_NAME = "app.revanced.extension.shared.Logger";
    private static final String REVANCED_LOG_TAG_PREFIX = "revanced: ";

    /* loaded from: /run/media/shohag-rana/01DC49173A549750/ALL from Ubuntu/Unlimited photos/google-photos-unlimited-patch-v7.45.0/payload/classes7.dex */
    public enum LogLevel {
        DEBUG,
        INFO,
        ERROR
    }

    @FunctionalInterface
    /* loaded from: /run/media/shohag-rana/01DC49173A549750/ALL from Ubuntu/Unlimited photos/google-photos-unlimited-patch-v7.45.0/payload/classes7.dex */
    public interface LogMessage {
        @NonNull
        String buildMessageString();
    }

    private static String getOuterClassSimpleName(Object obj) {
        Class<?> cls = obj.getClass();
        String name = cls.getName();
        int indexOf = name.indexOf(36);
        if (indexOf < 0) {
            return cls.getSimpleName();
        }
        return name.substring(name.lastIndexOf(46) + 1, indexOf);
    }

    private static void logInternal(LogLevel logLevel, LogMessage logMessage, @Nullable Throwable th, boolean z, boolean z2) {
        String message;
        String buildMessageString = logMessage.buildMessageString();
        String outerClassSimpleName = getOuterClassSimpleName(logMessage);
        if (th != null && (message = th.getMessage()) != null) {
            buildMessageString = buildMessageString + "\nException: " + message;
        }
        if (z) {
            StringWriter stringWriter = new StringWriter();
            new Throwable().printStackTrace(new PrintWriter(stringWriter));
            String stringWriter2 = stringWriter.toString();
            buildMessageString = buildMessageString + stringWriter2.substring(stringWriter2.indexOf(10, stringWriter2.lastIndexOf(LOGGER_CLASS_NAME)));
        }
        String str = outerClassSimpleName + ": " + buildMessageString;
        LogBufferManager.appendToLogBuffer(str);
        String str2 = REVANCED_LOG_TAG_PREFIX + outerClassSimpleName;
        int i = AnonymousClass1.$SwitchMap$app$revanced$extension$shared$Logger$LogLevel[logLevel.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    if (th == null) {
                        Log.e(str2, buildMessageString);
                    } else {
                        Log.e(str2, buildMessageString, th);
                    }
                }
            } else if (th == null) {
                Log.i(str2, buildMessageString);
            } else {
                Log.i(str2, buildMessageString, th);
            }
        } else if (th == null) {
            Log.d(str2, buildMessageString);
        } else {
            Log.d(str2, buildMessageString, th);
        }
        if (z2) {
            Utils.showToastLong(str);
        }
    }

    /* renamed from: app.revanced.extension.shared.Logger$1, reason: invalid class name */
    /* loaded from: /run/media/shohag-rana/01DC49173A549750/ALL from Ubuntu/Unlimited photos/google-photos-unlimited-patch-v7.45.0/payload/classes7.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$app$revanced$extension$shared$Logger$LogLevel;

        static {
            int[] iArr = new int[LogLevel.values().length];
            $SwitchMap$app$revanced$extension$shared$Logger$LogLevel = iArr;
            try {
                iArr[LogLevel.DEBUG.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$app$revanced$extension$shared$Logger$LogLevel[LogLevel.INFO.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$app$revanced$extension$shared$Logger$LogLevel[LogLevel.ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private static boolean shouldLogDebug() {
        return Utils.context != null && BaseSettings.DEBUG.get().booleanValue();
    }

    private static boolean shouldShowErrorToast() {
        return Utils.context != null && BaseSettings.DEBUG_TOAST_ON_ERROR.get().booleanValue();
    }

    private static boolean includeStackTrace() {
        return Utils.context != null && BaseSettings.DEBUG_STACKTRACE.get().booleanValue();
    }

    public static void printDebug(LogMessage logMessage) {
        printDebug(logMessage, null);
    }

    public static void printDebug(LogMessage logMessage, @Nullable Exception exc) {
        if (shouldLogDebug()) {
            logInternal(LogLevel.DEBUG, logMessage, exc, includeStackTrace(), false);
        }
    }

    public static void printInfo(LogMessage logMessage) {
        printInfo(logMessage, null);
    }

    public static void printInfo(LogMessage logMessage, @Nullable Exception exc) {
        logInternal(LogLevel.INFO, logMessage, exc, includeStackTrace(), false);
    }

    public static void printException(LogMessage logMessage) {
        printException(logMessage, null);
    }

    public static void printException(LogMessage logMessage, @Nullable Throwable th) {
        logInternal(LogLevel.ERROR, logMessage, th, includeStackTrace(), shouldShowErrorToast());
    }
}
