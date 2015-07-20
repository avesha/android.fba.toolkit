package ru.profi1c.engine;

import android.os.Looper;
import android.util.Log;

public final class Dbg {

    public static final boolean DEBUG = Const.DEBUG;

    /**
     * Печать сообщения в журнал только в режиме отладки
     *
     * @param text
     */
    public static void d(String text) {
        if (DEBUG) {
            Log.d(Const.APP, text);
        }
    }

    public static void e(String text) {
        if (DEBUG) {
            Log.e(Const.APP, text);
        }
    }

    public static void w(String text) {
        if (DEBUG) {
            Log.w(Const.APP, text);
        }
    }

    /**
     * Записывает информационное сообщение в журнал, независимо от режима отладки
     *
     * @param text
     */
    public static void i(String text) {
        Log.i(Const.APP, text);
    }

    private static final String formatMsg(String tag, String text) {
        return String.format("%s.%s", tag, text);
    }

    /**
     * Печать сообщения в журнал только в режиме отладки
     *
     * @param text
     */
    public static void d(String tag, String text) {
        d(formatMsg(tag, text));
    }

    public static void e(String tag, String text) {
        e(formatMsg(tag, text));
    }

    public static void w(String tag, String text) {
        w(formatMsg(tag, text));
    }

    public static void i(String tag, String text) {
        i(formatMsg(tag, text));
    }

    public static void d(String tag, String format, Object... args) {
        d(tag, String.format(format, args));
    }

    public static void e(String tag, String format, Object... args) {
        e(tag, String.format(format, args));
    }

    public static void w(String tag, String format, Object... args) {
        w(tag, String.format(format, args));
    }

    public static void i(String tag, String format, Object... args) {
        i(tag, String.format(format, args));
    }

    /**
     * Печать сообщения в журнал только в режиме отладки (добавить имя текущего потока)
     */
    public static void print(String tag, String format, Object... args) {
        final String threadName = getCurrentThreadName();
        d(tag, threadName + "|" + String.format(format, args));
    }

    public static void printStackTrace(Throwable e) {
        if (DEBUG) {
            e.printStackTrace();
        }
    }

    public static String getCurrentThreadName() {
        Thread t = Thread.currentThread();
        String threadName = t.getName();

        if (t == Looper.getMainLooper().getThread()) {
            threadName = "UI thread";
        }
        return threadName;
    }

    public static String getStackTraceString(Throwable t) {
        return Log.getStackTraceString(t);
    }
}
