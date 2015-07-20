package ru.profi1c.engine.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import ru.profi1c.engine.R;

/**
 * Помощник создания уведомлений
 */
public final class NotificationHelper {

    /**
     * Показать уведомление о получении новой версии приложения
     *
     * @param context контекст
     * @param id      идентификатор уведомления
     * @param sound   звук уведомления
     */
    public static void showDownloadNewVersion(Context context, int id, Uri sound) {

        String tickerText = context.getString(R.string.fba_get_app_success);
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.fba_web_service);

        PendingIntent pi =
                PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(), 0);

        showNotification(context, id, android.R.drawable.stat_sys_download_done, bmp, tickerText,
                         tickerText, Color.BLUE, sound, pi);
    }

    /**
     * Показать уведомление об успешности обмена с сервером
     *
     * @param context контекст
     * @param id      идентификатор уведомления
     * @param doStart намерение, которое будет выполнено при клике по уведомлению
     * @param sound   звук уведомления
     */
    public static void showExchangeSuccess(Context context, int id, Intent doStart, Uri sound) {

        String tickerText = context.getString(R.string.fba_exchange_success);
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.fba_web_service);

        PendingIntent pi =
                PendingIntent.getActivity(context.getApplicationContext(), 0, doStart, 0);

        showNotification(context, id, R.mipmap.fba_stat_notify_sync, bmp, tickerText, tickerText,
                         Color.GREEN, sound, pi);
    }

    /**
     * Показать уведомление об ошибке обмена с сервером.Сам текст ошибки не
     * показывается в уведомлении, если требуется посмотреть – необходимо
     * передать намерение на соответствующую активити
     *
     * @param context контекст
     * @param id      идентификатор уведомления
     * @param doStart намерение, которое будет выполнено при клике по уведомлению
     * @param sound   звук уведомления
     */
    public static void showExchangeError(Context context, int id, Intent doStart, Uri sound) {

        String tickerText = context.getString(R.string.fba_exchange_error);

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.fba_web_service);

        PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, doStart,
                                                     PendingIntent.FLAG_UPDATE_CURRENT);

        showNotification(context, id, R.mipmap.fba_stat_notify_sync_error, bmp, tickerText,
                         tickerText, Color.RED, sound, pi);
    }

    /**
     * Создать уведомление (стиль BigTextStyle для android 16 и выше). В тексте
     * заголовка уведомления отображается имя программы.
     *
     * @param context        контекст
     * @param idNotification идентификатор уведомления
     * @param idResIconTray  идентификатор ресурса для малой иконки
     * @param bmpLargeIcon   картинка для большой иконки или null
     * @param tickerText     тикер-текст
     * @param contentText    текст уведомления
     * @param ledColor       цвет индикатора
     * @param sound          звук уведомления
     * @param pendingIntent  намерение, которое будет выполнено при клике по уведомлению
     */
    public static void showNotification(Context context, int idNotification, int idResIconTray,
            Bitmap bmpLargeIcon, String tickerText, String contentText, int ledColor, Uri sound,
            PendingIntent pendingIntent) {

        String contentTitle = context.getString(R.string.app_name);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(idResIconTray);
        if (bmpLargeIcon != null)
            builder.setLargeIcon(bmpLargeIcon);
        builder.setTicker(tickerText);
        builder.setContentText(contentText);
        builder.setContentTitle(contentTitle);

        builder.setContentIntent(pendingIntent);

        // api 16: as big text
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(contentText);
        style.setBigContentTitle(contentTitle);

        builder.setStyle(style);

        Notification notification = builder.build();

        // LED (blinks with frequency - 5 including 2 off)
        notification.ledARGB = ledColor;
        notification.ledOffMS = 2;
        notification.ledOnMS = 5;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
        notification.sound = sound;

        // def sound
        if (sound == null) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(idNotification, notification);

    }

    private NotificationHelper() {
    }

}
