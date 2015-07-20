package ru.profi1c.engine.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetHelper {

    /**
     * Check whether the currently connected in time to the Internet (the network). The connection
     * type is not important. Requires android.permission.ACCESS_NETWORK_STATE.
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean connected =
                networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        return connected;
    }

    /**
     * Returns true if currently connection in WIFI Requires
     * android.permission.ACCESS_NETWORK_STATE.
     *
     * @param context
     * @return
     */
    public static boolean isConnectedWIFI(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnect = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isConnect = true;
            } else {
                // connect to GPRS?
            }
        }
        return isConnect;
    }

    private NetHelper() {
    }
}
