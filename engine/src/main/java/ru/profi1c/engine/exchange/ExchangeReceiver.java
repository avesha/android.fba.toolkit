package ru.profi1c.engine.exchange;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.util.AppHelper;

/**
 * Обработчик уведомления о перезагрузке устройства. Для программ установленных на
 * внешнюю карту, используется уведомление
 * “android.net.conn.CONNECTIVITY_CHANGE”
 */
public class ExchangeReceiver extends BroadcastReceiver {
    private static final String TAG = ExchangeReceiver.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (DEBUG) {
            Dbg.d(TAG, "onReceive intent: " + action);
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
            (ACTION_CONNECTIVITY_CHANGE.equals(action) &&
             AppHelper.isAppInstalledToSDCard(context))) {
            createSchedulerTasks(context);
        }
    }

    /**
     * Запустить/остановить планировщик обмена согласно настроек. По умолчанию
     * используется вариант обмена 'FULL', но если это первый запуск обмена
     * (база данных пустая) будет выполнен обмен по варианту 'INIT'
     */
    public static void createSchedulerTasks(Context context) {
        BaseExchangeSettings exSettings = FbaApplication.from(context).getExchangeSettings();
        if (exSettings.isEnableSchedule()) {
            ExchangeVariant variant = ExchangeVariant.FULL;
            if (!DBOpenHelper.isExistsDataBase(context)) {
                variant = ExchangeVariant.INIT;
            }
            ExchangeService.createScheduleUpdate(context, exSettings, variant);
        } else {
            ExchangeService.cancelSchedule(context, ExchangeVariant.FULL);
        }
    }

}