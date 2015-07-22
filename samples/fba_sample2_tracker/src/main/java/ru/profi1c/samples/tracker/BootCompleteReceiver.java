package ru.profi1c.samples.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.profi1c.engine.util.AppHelper;

/**
 * Обработчик уведомления о перезагрузке устройства. Для программ установленных
 * на внешнюю карту, используется уведомление
 * “android.net.conn.CONNECTIVITY_CHANGE”
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
            ("android.net.conn.CONNECTIVITY_CHANGE".equals(action) &&
             AppHelper.isAppInstalledToSDCard(context))) {

            if (!LocationService.isRunning()) {
                LocationService.start(context, Const.LOCATION_MIN_TIME,
                        Const.LOCATION_MIN_DISTANCE);
            }
        }
    }

}
