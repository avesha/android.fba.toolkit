package ru.profi1c.engine.map.location;

import android.content.Context;
import android.location.LocationManager;

/**
 * Factory class to create the correct instances of a variety of classes with
 * platform specific implementations.
 */
public class PlatformSpecificImplementationFactory {

    /**
     * Create a new LastLocationFinder instance
     *
     * @param context Context
     * @return LastLocationFinder
     */
    public static ILastLocationFinder getLastLocationFinder(Context context) {
        return new GingerbreadLastLocationFinder(context);
    }

    /**
     * Check support location provider by name. (Emulator 3.0 and above crashed
     * for network provider on "requestLocationUpdates" method)
     */
    public static boolean isProviderSupported(LocationManager locationManager, String provider) {
        return locationManager.isProviderEnabled(provider);
    }

}
