package com.sample.map.mapsforge;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.apache.commons.io.IOUtils;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.map.mapsforge.BaseRouteMapActivity;
import ru.profi1c.engine.map.mapsforge.MapHelper;
import ru.profi1c.engine.map.mapsforge.MovableMarker;
import ru.profi1c.engine.map.mapsforge.RouteItem;
import ru.profi1c.engine.util.IOHelper;
import ru.profi1c.engine.util.StorageHelper;

public class TAMapsforgeRouteMap extends BaseRouteMapActivity {
    private static final String TAG = TAMapsforgeRouteMap.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final String ASSERT_MAP_FOLDER = "map";
    private static final String ASSERT_FILE_TEMPLATE = "orlovskaya.00%d";

    private static final String MAP_FILE = "orlovskaya.map";
    private static final String MAP_FILE_ZIP = "orlovskaya.zip";

    private static final int VIBRATE_TIME = 10;
    public static final int DUMMY_ROUTE_ITEMS_COUNT = 10;

    /*
     * На сколько должна изменится текущая позиция (в метрах) прежде чем будет
     * получено новое значение координат
     */
    public static int DEF_LOCATION_MIN_DISTANCE = 500;

    /*
     * Максимальное время, которое должно пройти, прежде чем пользователь
     * получает обновление местоположения.
     */
    public static long DEF_LOCATION_MIN_TIME = 5000 * 60;

    /*
     * Центр города
     */
    private static final double DEFAULT_GEOPOINT_LAT = 52.966667;
    private static final double DEFAULT_GEOPOINT_LNG = 36.083333;

    private File mMapFile;
    private MapView mMapView;

    private Vibrator mVibrator;

    private static void extractAssert(Context context, File resultFile, File tmpFile)
            throws IOException {
        AssetManager am = context.getAssets();
        OutputStream fos = new FileOutputStream(tmpFile);

        tmpFile.createNewFile();
        String[] files = am.list(ASSERT_MAP_FOLDER);
        Arrays.sort(files);
        int size = files.length;
        for (int i = 1; i <= size; i++) {
            String filename = String.format(ASSERT_FILE_TEMPLATE, i);
            if (Arrays.binarySearch(files, filename) < 0) {
                throw new IOException("Error unpacking the test map file!");
            }
            InputStream ios = am.open(ASSERT_MAP_FOLDER + "/" + filename);
            IOUtils.copy(ios, fos);
            IOHelper.close(ios);
        }
        IOHelper.close(fos);

        IOHelper.unZipFirst(tmpFile);
        tmpFile.delete();
    }

    @Override
    public File getMapFile() {
        return mMapFile;
    }

    @Override
    public LatLong getMapCenterPoint() {
        return MapHelper.toLatLong(DEFAULT_GEOPOINT_LAT, DEFAULT_GEOPOINT_LNG);
    }

    @Override
    public boolean showCurrentPosition() {
        return true;
    }

    @Override
    public boolean requestLocationUpdates() {
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        final String msg = "onLocationChanged lat = " + location.getLatitude() + ", lng = " +
                           location.getLongitude();
        showToast(msg);
        if (DEBUG) {
            Dbg.d(TAG, msg);
        }
    }

    @Override
    public int getLocationMinDistance() {
        return DEF_LOCATION_MIN_DISTANCE;
    }

    @Override
    public long getLocationMinTime() {
        return DEF_LOCATION_MIN_TIME;
    }

    @Override
    protected void onRouteItemSelect(RouteItem<?> item) {
        getMapView().getModel().mapViewPosition.animateTo(item.getLatLong());
        showToast(item.getSnippet());

        //меняем цвет маркера и больше не двигаем
        if (item.isMoved()) {
            Drawable marker = MapHelper.makeNumberedMarker(this, R.mipmap.fba_map_marker_blue,
                    item.getOrdinal());
            AndroidGraphicFactory.convertToBitmap(marker);
            item.setBitmap(AndroidGraphicFactory.convertToBitmap(marker));
            item.setMovable(false);
            requestRedrawRouteOverlay();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ta_map_route);
        extractMapFile();
        init();
    }

    private void extractMapFile() {
        final Context context = getApplicationContext();
        final File path = StorageHelper.getExternalAppPath(context);
        mMapFile = new File(path, MAP_FILE);
        if (!mMapFile.exists()) {
            try {
                extractAssert(context, mMapFile, new File(path, MAP_FILE_ZIP));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void init() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mMapView = (MapView) findViewById(R.id.mapView);
        onCreateMapView(mMapView);

        initDummyRoute();

        Button bnt = (Button) findViewById(R.id.btnPrev);
        bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                navigateRoute(-1);
            }
        });
        bnt = (Button) findViewById(R.id.btnNext);
        bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                navigateRoute(1);
            }
        });
    }

    private void initDummyRoute() {

        //эмулируем исходный список маршрута (точек на карте)
        double lat = DEFAULT_GEOPOINT_LAT;
        final int countPoints = DUMMY_ROUTE_ITEMS_COUNT;

        List<DummyRouteItem> lst = new ArrayList<DummyRouteItem>();
        for (int i = 1; i <= countPoints; i++) {
            lat -= 0.003f;
            lst.add(new DummyRouteItem("Route item" + i, "Comment " + i, lat,
                    DEFAULT_GEOPOINT_LNG));
        }

        //конвертация в список отображаемых элементов
        List<RouteItem<DummyRouteItem>> routeItems = new ArrayList<RouteItem<DummyRouteItem>>();

        Drawable defaultMarker = getResources().getDrawable(R.mipmap.fba_map_marker_orange);
        int resIdDrawableRed = R.mipmap.fba_map_marker_red;
        int resIdDrawableGreen = R.mipmap.fba_map_maker_green;

        for (int i = 0; i < countPoints; i++) {
            DummyRouteItem item = lst.get(i);

            Drawable marker = MapHelper.makeNumberedMarker(this,
                    (i % 2 == 0 ? resIdDrawableRed : resIdDrawableGreen), i + 1);

            RouteItem<DummyRouteItem> routePoint = new RouteItem<DummyRouteItem>(item.latLong,
                    marker, item);
            routePoint.setMovable(i % 2 == 0);
            routePoint.setTitle(item.title);
            routePoint.setSnippet(item.snippet);
            routePoint.setOrdinal(i + 1);

            routeItems.add(routePoint);

            //маркер по умолчанию
            if (i == countPoints - 1) {
                routePoint.setBitmap(null);
            }
        }
        addRouteOverlay(mMapView, routeItems, defaultMarker);
    }

    @Override
    public void onTap(MovableMarker item, MapView mapView) {
        super.onTap(item, mapView);
        mVibrator.vibrate(VIBRATE_TIME);
    }

    private static class DummyRouteItem {
        public final String title;
        public final String snippet;
        public final LatLong latLong;

        DummyRouteItem(String name, String comment, double lat, double lon) {
            title = name;
            snippet = comment;
            latLong = new LatLong(lat, lon);
        }

    }
}
