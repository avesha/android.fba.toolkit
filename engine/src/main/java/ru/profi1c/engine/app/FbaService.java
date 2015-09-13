package ru.profi1c.engine.app;

import android.app.Notification;
import android.app.Service;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.util.PowerManagerHelper;

/**
 * Базовый класс для служб при использовании библиотеки 'FBA'.
 */
public abstract class FbaService extends Service
        implements ForegroundNotificationHolder.NotificationProvider {
    private static final String TAG = FbaService.class.getSimpleName();

    private int mWakeLockId = Const.NOT_SPECIFIED;
    private ForegroundNotificationHolder mForegroundNotificationHolder;

    protected void setWakeLock() {
        mWakeLockId = PowerManagerHelper.setWakeLock(getApplicationContext(), TAG);
    }

    protected void releaseWakeLock() {
        PowerManagerHelper.releaseWakeLock(mWakeLockId);
        mWakeLockId = Const.NOT_SPECIFIED;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mForegroundNotificationHolder = new ForegroundNotificationHolder(this, this);
    }

    @Override
    public void onDestroy() {
        if (mWakeLockId != Const.NOT_SPECIFIED) {
            releaseWakeLock();
        }
        mForegroundNotificationHolder.moveToBackground();
        super.onDestroy();
    }

    /**
     * Чтобы реализовать запуск сервиса на переднем  плане переопределите это метод и вызовите 'moveToForeground'
     *
     * @param notificationId
     * @return
     */
    @Override
    public Notification getForegroundNotification(int notificationId) {
        return null;
    }

    protected void moveToForeground(int notificationId) {
        mForegroundNotificationHolder.moveToForeground(notificationId);
    }

    protected void moveToBackground() {
        mForegroundNotificationHolder.moveToBackground();
    }
}
