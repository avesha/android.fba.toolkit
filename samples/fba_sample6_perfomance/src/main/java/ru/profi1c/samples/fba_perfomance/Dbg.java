package ru.profi1c.samples.fba_perfomance;

import android.util.Log;

public class Dbg {
    public static final String APP = "fba-perfomance";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * Записывает информационное сообщение в журнал, независимо от режима отладки
     *
     * @param text
     */
    public static void i(String text) {
        Log.i(APP, text);
    }

    private static final String formatMsg(String tag, String text) {
        return String.format("%s.%s", tag, text);
    }

    public static void i(String tag, String text) {
        i(formatMsg(tag, text));
    }

    public static void i(String tag, String format, Object... args) {
        i(tag, String.format(format, args));
    }
}
