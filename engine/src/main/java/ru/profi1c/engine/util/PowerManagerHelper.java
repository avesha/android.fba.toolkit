package ru.profi1c.engine.util;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.util.SparseArray;

import java.lang.reflect.Method;

import ru.profi1c.engine.Dbg;


/**
 * Helper gives you control of the power state of the device.
 */
@SuppressWarnings("deprecation")
public final class PowerManagerHelper {
    private static final String TAG = PowerManagerHelper.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final SparseArray<PowerManager.WakeLock> mActiveWakeLocks =
            new SparseArray<PowerManager.WakeLock>();
    private static int mNextId = 1;
    private static final int WAKELOCK_ACQUIRE = 600 * 1000;

    @SuppressLint("NewApi")
    public static boolean isInteractive(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isScreenOn();
        } else {
            return pm.isInteractive();
        }
    }

    public static boolean isDeviceSecured(Context context) {
        String LOCKSCREEN_UTILS = "com.android.internal.widget.LockPatternUtils";
        try {
            Class<?> lockUtilsClass = Class.forName(LOCKSCREEN_UTILS);
            Object lockUtils = lockUtilsClass.getConstructor(Context.class).newInstance(context);
            Method method = lockUtilsClass.getMethod("getActivePasswordQuality");
            int lockProtectionLevel = (Integer) method.invoke(lockUtils);
            if (lockProtectionLevel >= DevicePolicyManager.PASSWORD_QUALITY_NUMERIC) {
                return true;
            }
        } catch (Exception e) {
            Dbg.printStackTrace(e);
        }

        return false;
    }

    public static int setWakeLock(Context context, String wakeName) {
        synchronized (mActiveWakeLocks) {
            int id = mNextId;
            mNextId++;
            if (mNextId <= 0) {
                mNextId = 1;
            }

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl =
                    pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wake:" + wakeName);
            wl.setReferenceCounted(false);
            wl.acquire(WAKELOCK_ACQUIRE);
            mActiveWakeLocks.put(id, wl);
            return id;
        }
    }

    public static boolean releaseWakeLock(int id) {
        if (id == 0) {
            return false;
        }
        synchronized (mActiveWakeLocks) {
            PowerManager.WakeLock wl = mActiveWakeLocks.get(id);
            if (wl != null) {
                wl.release();
                mActiveWakeLocks.remove(id);
                return true;
            }

            if (DEBUG) {
                Dbg.w(TAG, "releaseWakeLock: No active wake lock id #" + id);
            }
            return true;
        }
    }

}
