package ru.profi1c.engine.map.mapsforge;

import java.io.File;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import ru.profi1c.engine.map.location.PlatformSpecificImplementationFactory;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import ru.profi1c.engine.map.mapsforge.R;

/**
 * Базовый класс Mapsforge MapActivity с доступом к базе данных и отображением
 * текущей позиции
 */
public abstract class BaseMapActivity extends FbaDBMapActivity {

    private MapView mMapView;

    private TileCache mTileCache;
    private MyLocationOverlay myLocationOverlay;
    private TileRendererLayer mTileRendererLayer;

    private LocationManager mLocationManager;
    private LocationUpdateListener mLocationListener;

    /**
     * Файл карты для off-line работы. О создании карт читайте {@link http://code.google.com/p/mapsforge/wiki/GettingStartedMapWriter}.
     * Карты некоторых городов (областей) России вы можете скачать с сайта www.profi1c.ru
     */
    public abstract File getMapFile();

    /**
     * Координаты центра карты по умолчанию
     */
    public abstract LatLong getMapCenterPoint();

    /**
     * Показывать маркер текущей позиции, будет создан автоматически при
     * иницизации карты методом {@link #onCreateMapView(MapView mapView) }.
     */
    public abstract boolean showCurrentPosition();

    /**
     * Включить отслеживание изменения текущей позиции, при имении позиции
     * вызывается {@link #onLocationChanged(Location location)}
     */
    public abstract boolean requestLocationUpdates();

    /**
     * Вызывается, когда положение изменилось (при условии, что включено
     * отслеживание, см. {@link #requestLocationUpdates()}
     *
     * @param location
     */
    public abstract void onLocationChanged(Location location);

    /**
     * На сколько должна изменится текущая позиция (в метрах) прежде чем будет
     * получено новое значение координат
     */
    public abstract int getLocationMinDistance();

    /**
     * Максимальное время, которое должно пройти, прежде чем пользователь
     * получает обновление местоположения.
     */
    public abstract long getLocationMinTime();

    /**
     * Инициализация карты
     *
     * @param mapView
     */
    protected void onCreateMapView(MapView mapView) {
        mMapView = mapView;
        mTileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.getMapZoomControls().setZoomLevelMin((byte) MapHelper.ZOOM_LEVEL_MIN);
        mapView.getMapZoomControls().setZoomLevelMax((byte) MapHelper.ZOOM_LEVEL_MAX);

        if (loadMapFile(mapView)) {
            LatLong latLong = getMapCenterPoint();
            if (latLong != null) {
               mapView.getModel().mapViewPosition.setCenter(latLong);
            }
            mapView.getModel().mapViewPosition.setZoomLevel(MapHelper.ZOOM_LEVEL_AREA);

            if (showCurrentPosition()) {
                Drawable drawable = getResources().getDrawable(
                        R.mipmap.fba_map_indicator_current_position);
                Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);

                myLocationOverlay = new MyLocationOverlay(this, mapView.getModel().mapViewPosition,
                        bitmap);
                myLocationOverlay.setSnapToLocationEnabled(true);
                mapView.getLayerManager().getLayers().add(this.myLocationOverlay);
            }
        } else {
            finish();
        }

    }

    protected MapView getMapView(){
        return mMapView;
    }

    /**
     * Прочитать файл карты
     *
     * @param mapView
     * @return
     */
    protected boolean loadMapFile(MapView mapView) {

        boolean loaded = false;
        File fMap = getMapFile();
        if (fMap.exists()) {
            MapDataStore mapDataStore = new MapFile(fMap);
            mTileRendererLayer = new TileRendererLayer(mTileCache, mapDataStore,
                    mapView.getModel().mapViewPosition, false, true,
                    AndroidGraphicFactory.INSTANCE);
            mTileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

            mapView.getLayerManager().getLayers().add(mTileRendererLayer);
            loaded = true;
        } else {
            showToast(getString(R.string.fba_map_file_not_found));
        }

        return loaded;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidGraphicFactory.createInstance(getApplication());
        if (requestLocationUpdates()) {
            initRequestLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMapView != null) {
            if (mTileRendererLayer != null) {
                mMapView.getLayerManager().getLayers().remove(mTileRendererLayer);
                mTileRendererLayer.onDestroy();
            }

            if (myLocationOverlay != null) {
                mMapView.getLayerManager().getLayers().remove(myLocationOverlay);
                myLocationOverlay.onDestroy();
            }
        }

        if (mTileCache != null) {
            mTileCache.destroy();
        }

        if (mMapView != null) {
            mMapView.getModel().mapViewPosition.destroy();
            mMapView.destroy();
        }

        AndroidGraphicFactory.clearResourceMemoryCache();
        remoteListenerLocationUpdate();
    }

    /*
     * Инициализация слушателя изменения текущей позиции
     */
    private void initRequestLocationUpdates() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationUpdateListener();

        long minTime = getLocationMinTime();
        int minDistance = getLocationMinDistance();

        if (PlatformSpecificImplementationFactory.isProviderSupported(mLocationManager,
                LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
                    minDistance, mLocationListener);
        }

        if (PlatformSpecificImplementationFactory.isProviderSupported(mLocationManager,
                LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
                    minDistance, mLocationListener);
        }
    }

    /*
     * отключить слушатель изменения координат, если он есть
     */
    private void remoteListenerLocationUpdate() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    /*
     * Обработчик уведомления о получении текущей позиции
     */
    private class LocationUpdateListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            BaseMapActivity.this.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }
}
