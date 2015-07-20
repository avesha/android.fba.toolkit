package ru.profi1c.engine.map.location;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import java.util.List;

import ru.profi1c.engine.Const;

/**
 * Optimized implementation of Last Location Finder for devices running
 * Gingerbread and above.
 * <p/>
 * This class let's you find the "best" (most accurate and timely) previously
 * detected location using whatever providers are available.
 * <p/>
 * Where a timely / accurate previous location is not detected it will return
 * the newest location (where one exists) and setup a oneshot location update to
 * find the current location.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GingerbreadLastLocationFinder implements ILastLocationFinder {
    protected static String UPDATE_ACTION = Const.ACTION_SINGLE_LOCATION_UPDATE;

    protected PendingIntent mSingleUpdatePI;
    protected LocationListener mLocationListener;
    protected LocationManager mLocationManager;
    protected Context mContext;
    protected Criteria mCriteria;

    private boolean mRegisterUpdateReceiver;

    /**
     * Construct a new Gingerbread Last Location Finder.
     */
    public GingerbreadLastLocationFinder(Context context) {
        this.mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Coarse accuracy is specified here to get the fastest possible result.
        // The calling Activity will likely (or have already) request ongoing
        // updates using the Fine location provider.
        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_LOW);

        // Construct the Pending Intent that will be broadcast by the oneshot
        // location update.
        Intent updateIntent = new Intent(UPDATE_ACTION);
        mSingleUpdatePI = PendingIntent
                .getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Returns the most accurate and timely previously detected location. Where
     * the last result is beyond the specified maximum distance or latency a
     * one-off location update is returned via the {@link LocationListener}
     * specified in {@link #setChangedLocationListener}.
     *
     * @param minDistance Minimum distance before we require a location update.
     * @param minTime     Minimum time required between location updates.
     * @return The most accurate and / or timely previously detected location.
     */
    public Location getLastBestLocation(int minDistance, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Iterate through all the providers on the system, keeping
        // note of the most accurate result within the acceptable time limit.
        // If no result is found within maxTime, return the newest Location.
        List<String> matchingProviders = mLocationManager.getAllProviders();
        for (String provider : matchingProviders) {
            Location location = mLocationManager.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                } else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
                    bestResult = location;
                    bestTime = time;
                }
            }
        }

        // If the best result is beyond the allowed time limit, or the accuracy
        // of the
        // best result is wider than the acceptable maximum distance, request a
        // single update.
        // This check simply implements the same conditions we set when
        // requesting regular
        // location updates every [minTime] and [minDistance].
        if (mLocationListener != null && (bestTime < minTime || bestAccuracy > minDistance)) {
            IntentFilter locIntentFilter = new IntentFilter(UPDATE_ACTION);

            mContext.registerReceiver(mSingleUpdateReceiver, locIntentFilter);
            mRegisterUpdateReceiver = true;
            mLocationManager.requestSingleUpdate(mCriteria, mSingleUpdatePI);

        }

        return bestResult;
    }

    /**
     * This {@link BroadcastReceiver} listens for a single location update
     * before unregistering itself. The oneshot location update is returned via
     * the {@link LocationListener} specified in
     * {@link #setChangedLocationListener}.
     */
    protected BroadcastReceiver mSingleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(mSingleUpdateReceiver);
            mRegisterUpdateReceiver = false;

            String key = LocationManager.KEY_LOCATION_CHANGED;
            Location location = (Location) intent.getExtras().get(key);

            if (mLocationListener != null && location != null) {
                mLocationListener.onLocationChanged(location);
            }
            mLocationManager.removeUpdates(mSingleUpdatePI);
        }
    };

    /**
     * {@inheritDoc}
     */
    public void setChangedLocationListener(LocationListener listener) {
        mLocationListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    public void cancel() {
        if (mRegisterUpdateReceiver) {
            mContext.unregisterReceiver(mSingleUpdateReceiver);
        }
        mLocationManager.removeUpdates(mSingleUpdatePI);
    }

}
