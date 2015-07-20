package ru.profi1c.engine.app;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import ru.profi1c.engine.util.IOHelper;
import ru.profi1c.engine.util.TraceExceptionHandler;

/**
 * Базовый класс приложения, поддержка глобального состояния вашего приложения и
 * основные настройки компоненты 'FBA'. Вы должны предоставить собственную
 * реализацию на базе этого класса, указав его имя в теге <application> вашего
 * AndroidManifest.xml
 */
public abstract class FbaApplication extends MultiDexApplication implements IFbaAppInfoProvider {

    private static Context sContext;

    public static FbaApplication from(Context context) {
        if (context instanceof FbaApplication) {
            return (FbaApplication) context;
        }
        return (FbaApplication) context.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    /**
     * Возвращает намерение для запуска основной активности (Activity)
     */
    public Intent getHomeIntent() {
        final Intent intent = new Intent(getApplicationContext(), getMainActivityClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public IFbaSettingsProvider getFbaSettingsProvider() {
        return this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        checkSettings();

        BaseAppSettings appSettings = getAppSettings();
        if (appSettings.customExceptionHandler()) {
            TraceExceptionHandler.attach(getApplicationContext());
        }

        IOHelper.createDir(appSettings.getAppDir());
        IOHelper.createDir(appSettings.getCacheDir());
        IOHelper.createDir(appSettings.getBackupDir());
    }

    /**
     * Проверить установленные настройки
     */
    private void checkSettings() {
        // Возможен вариант, когда используются только получение/сохранение
        // кастомных данных и json объектов. Тогда база данных и метаданные не нужны

        if (getExchangeSettings() == null) {
            throw new IllegalStateException(
                    "Not set exchange settings, see FbaApplication.getExchangeSettings method!");
        }

        if (getAppSettings() == null) {
            throw new IllegalStateException(
                    "Not set program settings, see FbaApplication.getAppSettings method!");
        }

    }

}
