package ru.profi1c.samples.tracker;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import ru.profi1c.engine.app.FbaActivityDialog;
import ru.profi1c.engine.app.FbaService;
import ru.profi1c.engine.exchange.ISimpleCallbackListener;
import ru.profi1c.engine.map.location.PlatformSpecificImplementationFactory;
import ru.profi1c.engine.util.NetHelper;
import ru.profi1c.engine.util.NotificationHelper;
import ru.profi1c.samples.tracker.exchange.ExchangeManager;

public class LocationService extends FbaService implements LocationListener {
    private static final String TAG = LocationService.class.getSimpleName();

    /*
     * Идентификатор уведомления, которое будет создано в случае ошибки отправки
     * на сервер данных
     */
    public static int NOTIFICATION_ID_ERROR_EXCHANGE = 1234;

    /*
     * Идентификатор сохраняемых данных (передается веб-сервису)
     */
    private static final String WS_DATA_ID = "change_current_location";

    private static final String EXTRA_MIN_TIME = "min_time";
    private static final String EXTRA_DISTANCE = "distance";

    private static boolean running;
    private LocationManager mLocationManager;

    private ExchangeManager mExchangeManager;
    private Gson mGson;

    private static Intent getIntent(Context ctx) {
        return new Intent(ctx, LocationService.class);
    }

    public static void start(Context ctx, long minTime, int minDistance) {
        Intent i = getIntent(ctx);
        i.putExtra(EXTRA_MIN_TIME, minTime);
        i.putExtra(EXTRA_DISTANCE, minDistance);
        ctx.startService(i);
    }

    public static void stop(Context ctx) {
        ctx.stopService(getIntent(ctx));
    }

    public static boolean isRunning() {
        return running;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            handleStart(intent);
        }
        return START_STICKY;
    }

    private void handleStart(Intent intent) {
        running = true;

        // Инициализация менеджера обмена с web-сервером
        App app = (App) getApplication();
        mExchangeManager = new ExchangeManager(app.getExchangeSettings());

        // cериализатор JSON, десериализатор в 1С понимает этот формат даты (см.
        // в 1С функцию XMLЗначение)
        mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

        // Менеджер позиционирования: инициализация и подписка на изменение
        // текущей позиции
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = intent.getLongExtra(EXTRA_MIN_TIME, 0);
        int minDistance = intent.getIntExtra(EXTRA_DISTANCE, 0);

        if (PlatformSpecificImplementationFactory.isProviderSupported(mLocationManager,
                LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
                    minDistance, this);
        }

        if (PlatformSpecificImplementationFactory.isProviderSupported(mLocationManager,
                LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
                    minDistance, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }

        running = false;
    }

    @Override
    public void onLocationChanged(Location location) {

        if (NetHelper.isConnected(this)) {
            doSendData(location);
        } else {
            Log.i(TAG, "No network connection.");
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private void doSendData(Location location) {

        LocationData data = new LocationData(location);

        // сериализовать данные в строку
        String strJson = mGson.toJson(data);

        mExchangeManager.writeShortData(WS_DATA_ID, strJson, new ISimpleCallbackListener() {

            @Override
            public void onError(String event, String msg) {
                Log.e(TAG, "error: " + msg);

                final Context ctx = getApplicationContext();

                // Показать уведомление с ошибкой
                Intent i = FbaActivityDialog.getStartIntent(ctx, msg);

                NotificationHelper.showExchangeError(ctx, NOTIFICATION_ID_ERROR_EXCHANGE, i, null);

            }

            @Override
            public void onComplete(boolean result, Object data) {
                Log.i(TAG, "onComplete: " + result);

            }

            @Override
            public void onCancel() {

            }
        });

    }

    /*
     * «Структура» данных передаваемая на сервер 1С
     */
    @SuppressWarnings("unused")
    private static class LocationData {
        Date time;
        double latitude;
        double longitude;

        public LocationData(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            time = new Date(System.currentTimeMillis());
        }
    }
}
