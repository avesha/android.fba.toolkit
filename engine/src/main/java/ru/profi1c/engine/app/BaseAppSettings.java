package ru.profi1c.engine.app;

import android.content.Context;

import java.io.File;

/**
 * Базовый класс для глобальных настроек вашего приложения
 */
public abstract class BaseAppSettings {

    private final Context mContext;

    protected BaseAppSettings(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Путь к каталогу приложения. Это каталог, как правило, автоматически
     * удаляется при удалении приложения
     */
    public abstract File getAppDir();

    /**
     * Путь к каталогу временных файлов приложения. Это каталог, как правило,
     * автоматически удаляется при удалении приложения
     */
    public abstract File getCacheDir();

    /**
     * Путь к каталогу резервирования данных. Как правило, это каталог на
     * внешнем хранилище (SDCard ) который не удаляется автоматически.
     */
    public abstract File getBackupDir();

    /**
     * Если установлен, то при возникновении ошибки времени исполнения программа
     * будет автоматически сохранять стек ошибки в файл. Затем, при процедуре
     * обмена с сервером, ошибки будут переданы на сервер для анализа.
     *
     * @return истина, если используется свой обработчик ошибок.
     */
    public abstract boolean customExceptionHandler();

    /**
     * Если true, пользователю будет выводится максимально детальное описание
     * ошибки включая stack trace
     */
    public abstract boolean isFullErrorStack();

    /**
     * Идентификатор иконки приложения, как правило 'R.drawable.ic_launcher'
     */
    public abstract int getIdResIconLauncher();

    /**
     * Принудительное обновление. Если при запуске программы
     * обнаружена новая версия и пользователь откажется выполнять обновление –
     * программа завершает свою работу.
     */
    public abstract boolean isForceUpdateApp();

}
