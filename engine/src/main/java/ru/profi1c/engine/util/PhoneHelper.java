package ru.profi1c.engine.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.UUID;

import ru.profi1c.engine.Dbg;

/**
 * Module for your phone: get the phone number, a unique device number. Required
 * READ_PHONE_STATE permission.
 */
public final class PhoneHelper {

    public static String EMULE_DEVICE_ID = "emulator";

    /**
     * Type the serial number that you want to get: IMEI (MEID,ESN) - unique
     * phone number ( never change ) DEVICE - unit number ( changes by
     * flashing,Factory Reset, etc.) INSTANCE - unique number of the
     * installation (varies pi reinstalling the program )
     */
    public enum DeviceIdType {
        IMEI, DEVICE, INSTANCE
    }

    /**
     * Return current phone number. Needs READ_PHONE_STATE permission.
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager phoneManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return phoneManager.getLine1Number();
    }

    /**
     * Return network operator name
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }

    /**
     * Return serial number of this device
     */
    public static String getSerialNumber(Context context, DeviceIdType type) {
        String serial = null;
        switch (type) {
            case IMEI:
                serial = getDeviceId(context);
                break;
            case DEVICE:
                serial = getBuildSerialNumber(context);
                break;
            case INSTANCE:
                serial = Installation.id(context);
        }
        return serial;
    }

    /**
     * Get device id. Needs READ_PHONE_STATE permission.
     */
    private static String getDeviceId(Context context) {
        TelephonyManager phoneManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return phoneManager.getDeviceId();
    }

    /**
     * Get the serial number which is generated during the first boot device, it
     * is reset when the cleaning device (Factory Reset, etc.)
     *
     * @return
     */
    private static String getBuildSerialNumber(Context context) {
        String serial = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            serial = getSystemSerialNumber();
        } else {
            serial = Settings.Secure
                    .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        if (TextUtils.isEmpty(serial)) {
            return EMULE_DEVICE_ID;
        }
        return serial;
    }

    /**
     * Get the serial number for Android SDK < 2.3
     *
     * @return
     */
    private static String getSystemSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            Dbg.printStackTrace(e);
        }
        return serial;
    }

    /**
     * To monitor the installation, you can use the UUID as an identifier, and
     * then just every time you create a new, when you first start the
     * application.
     */
    private static class Installation {
        private static String sID = null;
        private static final String INSTALLATION = "INSTALLATION";

        private synchronized static String id(Context context) {
            if (sID == null) {
                File installation = new File(context.getFilesDir(), INSTALLATION);
                try {
                    if (!installation.exists())
                        writeInstallationFile(installation);
                    sID = readInstallationFile(installation);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return sID;
        }

        private static String readInstallationFile(File installation) throws IOException {
            RandomAccessFile f = new RandomAccessFile(installation, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
            return new String(bytes);
        }

        private static void writeInstallationFile(File installation) throws IOException {
            FileOutputStream out = new FileOutputStream(installation);
            String id = UUID.randomUUID().toString();
            out.write(id.getBytes());
            out.close();
        }
    }

    private PhoneHelper() {
    }
}
