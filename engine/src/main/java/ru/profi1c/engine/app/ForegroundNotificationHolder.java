package ru.profi1c.engine.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;

class ForegroundNotificationHolder {

    private final Service mService;
    private final NotificationProvider mNotificationProvider;

    private boolean mForeground = false;
    private boolean mCancelNotification = true;
    private int mNotificationId = 0;

    ForegroundNotificationHolder(Service service, NotificationProvider provider) {
        mService = service;
        mNotificationProvider = provider;
    }

    void moveToForeground(int id) {
        moveToForeground(id, mNotificationProvider.getForegroundNotification(id), true);
    }

    void moveToForeground(int id, boolean cancelNotification) {
        moveToForeground(id, mNotificationProvider.getForegroundNotification(id),
                         cancelNotification);
    }

    void moveToForeground(int id, Notification notification, boolean cancelNotification) {
        if (!mForeground && notification != null) {
            mForeground = true;
            mNotificationId = id;
            mCancelNotification = cancelNotification;

            mService.startForeground(id, notification);
        } else if (mNotificationId != id && id > 0 && notification != null) {
            mNotificationId = id;
            ((NotificationManager) mService.getApplicationContext()
                                           .getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(id, notification);
        }
    }

    void moveToBackground(int id, boolean cancelNotification) {
        if (mForeground) {
            mForeground = false;
            mNotificationId = 0;
            mService.stopForeground(cancelNotification);
        }
    }

    void moveToBackground(int id) {
        moveToBackground(id, mCancelNotification);
    }

    void moveToBackground() {
        moveToBackground(mNotificationId, mCancelNotification);
    }

    interface NotificationProvider {
        Notification getForegroundNotification(int notificationId);
    }
}
