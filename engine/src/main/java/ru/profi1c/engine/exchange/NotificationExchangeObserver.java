package ru.profi1c.engine.exchange;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import java.io.File;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.util.NotificationHelper;

/**
 * Интерфейс для наблюдением за изменениями связанными с процедурой
 * обмена. По окончании обмена или при ошибке отображает уведомление
 */
public abstract class NotificationExchangeObserver extends ExchangeObserver {

    /**
     * Идентификатор уведомления, которое будет создано при получении новой
     * версии приложение
     */
    public static int NOTIFICATION_ID_DOWNLOAD_APK = Const.NOTIFICATION_ID_DOWNLOAD_APK;

    /**
     * Идентификатор уведомления, которое будет создано по окончании обмена
     */
    public static int NOTIFICATION_ID_FINISH_EXCHANGE = Const.NOTIFICATION_ID_FINISH_EXCHANGE;

    /**
     * Возвращает Uri на звук который будет воспроизведен при получении новой
     * версии приложения
     *
     * @return
     */
    public abstract Uri getSoundDownloadedApk();

    /**
     * Возвращает uri на звук который будет воспроизведен если при обмене
     * произошли ошибки
     *
     * @return
     */
    public abstract Uri getSoundExchangeError();

    /**
     * Возвращает uri на звук который будет воспроизведен если обмен завершен
     * успешно
     *
     * @return
     */
    public abstract Uri getSoundExchangeSuccess();

    /**
     * Возвращает намерение, которое будет запущено по клику на уведомлении об
     * успешности загрузки
     *
     * @return
     */
    public abstract Intent getIntentOnSuccess();

    /**
     * Возвращает намерение, которое будет запущено по клику на уведомлении об
     * ошибке загрузки
     *
     * @return
     */
    public abstract Intent getIntentOnError();

    public NotificationExchangeObserver(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    public boolean onDownloadNewVersionApp(File fApk) {
        NotificationHelper.showDownloadNewVersion(getContext(), NOTIFICATION_ID_DOWNLOAD_APK,
                                                  getSoundDownloadedApk());
        return true;
    }

    @Override
    public void onFinish(boolean success) {
        if (success) {
            NotificationHelper.showExchangeSuccess(getContext(), NOTIFICATION_ID_FINISH_EXCHANGE,
                                                   getIntentOnSuccess(), getSoundExchangeSuccess());
        } else {
            NotificationHelper.showExchangeError(getContext(), NOTIFICATION_ID_FINISH_EXCHANGE,
                                                 getIntentOnError(), getSoundExchangeError());
        }
    }

}
