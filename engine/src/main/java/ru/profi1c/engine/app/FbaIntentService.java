package ru.profi1c.engine.app;

import android.app.IntentService;
import android.app.Notification;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.util.PowerManagerHelper;

/**
 * Базовый класс для служб работающих в отдельном потоке (в очереди) при
 * использовании библиотеки 'FBA'.
 */
public abstract class FbaIntentService extends IntentService
        implements ForegroundNotificationHolder.NotificationProvider {

    private static final String TAG = FbaService.class.getSimpleName();

    private int mWakeLockId = Const.NOT_SPECIFIED;
    private final String mName;

    private ForegroundNotificationHolder mForegroundNotificationHolder;

    public FbaIntentService(String name) {
        super(name);
        mName = name;
    }

    protected void setWakeLock() {
        mWakeLockId = PowerManagerHelper.setWakeLock(getApplicationContext(), mName);
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
