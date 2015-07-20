package ru.profi1c.engine.exchange;

import android.content.Context;
import android.os.Handler;

import java.io.File;

/**
 * Интерфейс для наблюдения за изменениями связанными с процедурой обмена.
 * Приложение регистрирует экземпляр производного класса с
 * {@link ResponseHandler}. Основное приложение реализует обратные вызовы:
 */
public abstract class ExchangeObserver {

    private final Context mContext;
    private final Handler mHandler;

    public ExchangeObserver(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public Context getContext() {
        return mContext;
    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * Этот обработчик вызывается при начале обмена
     *
     * @param variant вариант обмена
     */
    public abstract void onStart(ExchangeVariant variant);

    /**
     * Этот обработчик вызывается при подключении к уже запущенному обмену
     */
    public abstract void onBuild();

    /**
     * С сервера получена новая версия приложения
     *
     * @param fApk путь к файлу приложения
     * @return Истина, если следует продолжить процесс обмена, ложь если надо
     * прервать обмена и продолжить только после обновления программы.
     * При наличии новой версии программы, пользователю будет предложено
     * обновить программу при запуске.
     */
    public abstract boolean onDownloadNewVersionApp(File fApk);

    /**
     * Уведомление о начале выполнения операции обмена
     *
     * @param msg
     */
    public abstract void onStepInfo(String msg);

    /**
     * Сообщение об ошибке
     *
     * @param msg текст сообщения, может быть null
     */
    public abstract void onError(String msg);

    /**
     * Этот обработчик вызывается по окончании обмена
     *
     * @param success флаг успешности обмена
     */
    public abstract void onFinish(boolean success);
}
