package ru.profi1c.samples.audit.salespoint;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.map.mapsforge.BaseRouteMapActivity;
import ru.profi1c.engine.map.mapsforge.MapHelper;
import ru.profi1c.engine.map.mapsforge.MovableMarker;
import ru.profi1c.engine.map.mapsforge.RouteItem;
import ru.profi1c.engine.util.IOHelper;
import ru.profi1c.engine.util.StorageHelper;
import ru.profi1c.engine.widget.ObjectView;
import ru.profi1c.samples.audit.salespoint.db.CatalogAddInfoStorage;
import ru.profi1c.samples.audit.salespoint.db.CatalogAddInfoStorageDao;
import ru.profi1c.samples.audit.salespoint.db.CatalogSalesPoint;
import ru.profi1c.samples.audit.salespoint.db.CatalogSalesPointDao;

/*
 * Пример отображение маршрута на оффлайн карте
 */
public class MapsforgeRouteMap extends BaseRouteMapActivity {
    private static final String TAG = MapsforgeRouteMap.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final String ASSERT_MAP_FOLDER = "map";
    private static final String ASSERT_FILE_TEMPLATE = "orlovskaya.00%d";

    private static final String MAP_FILE = "orlovskaya.map";
    private static final String MAP_FILE_ZIP = "orlovskaya.zip";

    private static final int VIBRATE_TIME = 10;

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

    private ObjectView mSalesPointView, mFotoStorageView;
    private CatalogAddInfoStorageDao mStorageDao;
    private CatalogSalesPointDao mSalesPointDao;
    private Animation mAnimationIn, mAnimationOut;

    // Список точек маршрута отображаемый на карте
    List<RouteItem<CatalogSalesPoint>> routeItems;

    /*
     * Имена реквизитов отображаемых в всплывающем окне  (имена полей класса)
     */
    private static String[] fields = new String[]{CatalogSalesPoint.FIELD_NAME_DESCRIPTION,
            CatalogSalesPoint.FIELD_NAME_ADRESS, CatalogSalesPoint.FIELD_NAME_PHONE,
            CatalogSalesPoint.FIELD_NAME_SITE};

    /*
     * Идентификаторы view-элементов для отображения реквизитов
     */
    private static int[] ids =
            new int[]{R.id.tvDescription, R.id.tvAdress, R.id.tvPhone, R.id.tvSite};

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractMapFile();
        setContentView(R.layout.activity_route_map);
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_save_coordinates) {

            saveMovedSalesPoint();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mMapView = (MapView) findViewById(R.id.mapView);

        //Инициализация карты
        onCreateMapView(mMapView);

        mSalesPointView = (ObjectView) findViewById(R.id.ovSalesPoint);
        mSalesPointView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                closePopup();
            }
        });

        mFotoStorageView = (ObjectView) findViewById(R.id.ovFotoStorage);
        mAnimationIn = AnimationUtils.loadAnimation(this, R.anim.toast_enter);
        mAnimationOut = AnimationUtils.loadAnimation(this, R.anim.toast_exit);

        try {
            //торговые точки расположить на карте
            initDummyRoute();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }

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

    private void initDummyRoute() throws SQLException {

        //DAO для отображения картинки и для записи координат
        mStorageDao = new CatalogAddInfoStorageDao(getConnectionSource());
        mSalesPointDao = new CatalogSalesPointDao(getConnectionSource());

        // Выбрать все торговые точки
        CatalogSalesPointDao salePointDao = new CatalogSalesPointDao(getConnectionSource());
        List<CatalogSalesPoint> lst = salePointDao.select();

        // Список точек маршрута отображаемый на карте
        routeItems = new ArrayList<RouteItem<CatalogSalesPoint>>();

        // Для точек, у которых не установлены координаты
        final double lng = DEFAULT_GEOPOINT_LNG;
        double lat = DEFAULT_GEOPOINT_LAT;

        int count = lst.size();
        for (int i = 0; i < count; i++) {

            CatalogSalesPoint salesPoint = lst.get(i);

            boolean movable = (salesPoint.lat == 0 || salesPoint.lng == 0);
            int resIdDrawable = R.mipmap.fba_map_maker_green;
            LatLong latLong = new LatLong(salesPoint.lat, salesPoint.lng);

            if (movable) {
                resIdDrawable = R.mipmap.fba_map_marker_red;
                lat -= 0.003f;
                latLong = new LatLong(lat, lng);
            }

            //На маркере вывести порядковый  номер
            Drawable marker = MapHelper.makeNumberedMarker(this, resIdDrawable, i + 1);

            RouteItem<CatalogSalesPoint> routePoint =
                    new RouteItem<CatalogSalesPoint>(latLong, marker, salesPoint);
            routePoint.setMovable(movable);
            routePoint.setOrdinal(i + 1);

            routeItems.add(routePoint);
        }

        Drawable defaultMarker = getResources().getDrawable(R.mipmap.fba_map_marker_orange);
        addRouteOverlay(mMapView, routeItems, defaultMarker);

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
    public boolean showCurrentPosition() {
        return true;
    }

    @Override
    public boolean requestLocationUpdates() {
        return true;
    }

    @Override
    protected void onRouteItemSelect(RouteItem<?> item) {
        getMapView().getModel().mapViewPosition.animateTo(item.getLatLong());

        try {
            CatalogSalesPoint salesPoint = (CatalogSalesPoint) item.getData();
            inflatePopup(salesPoint);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void onTap(MovableMarker item, MapView mapView) {
        super.onTap(item, mapView);
        mVibrator.vibrate(VIBRATE_TIME);
    }

    /*
     * Заполнить данные по торговой точке на всплывающем окне и отобразить его
     */
    private void inflatePopup(CatalogSalesPoint salesPoint) throws SQLException {
        closePopup();

        if (CatalogSalesPoint.isEmpty(salesPoint.foto)) {
            mFotoStorageView.setVisibility(View.GONE);
        } else {
            //Прочитать ссылку на фото
            mStorageDao.refresh(salesPoint.foto);
            mFotoStorageView.build(salesPoint.foto, getHelper(),
                                   new String[]{CatalogAddInfoStorage.FIELD_NAME_STORAGE},
                                   new int[]{R.id.ivFoto});

            mFotoStorageView.setVisibility(View.VISIBLE);
        }

        mSalesPointView.build(salesPoint, getHelper(), fields, ids);
        showPopup();
    }

    /*
     * Скрыть всплывающее окно
     */
    private void closePopup() {
        if (mSalesPointView.getVisibility() == View.VISIBLE) {
            mSalesPointView.startAnimation(mAnimationOut);
            mSalesPointView.setVisibility(View.GONE);
        }
    }

    /*
     * Показать всплывающее окно
     */
    private void showPopup() {
        if (mSalesPointView.getVisibility() != View.VISIBLE) {
            mSalesPointView.startAnimation(mAnimationIn);
            mSalesPointView.setVisibility(View.VISIBLE);
        }
    }

    /*
     * Сохранить координаты точек, которые пользователь установил на карте
     * вручную
     */
    private void saveMovedSalesPoint() {

        int count = 0;
        for (RouteItem<CatalogSalesPoint> routeItem : routeItems) {
            if (routeItem.isMoved()) {

                // Обновим и сохраним в локальную базу (с обменом будет передано
                // в 1С)
                CatalogSalesPoint salesPoint = routeItem.getData();
                salesPoint.lat = routeItem.getLatLong().latitude;
                salesPoint.lng = routeItem.getLatLong().longitude;
                salesPoint.setModified(true);

                try {
                    mSalesPointDao.update(salesPoint);
                    count++;
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Маркер больше не двигаем
                Drawable marker = MapHelper.makeNumberedMarker(this, R.mipmap.fba_map_maker_green,
                                                               routeItem.getOrdinal());
                AndroidGraphicFactory.convertToBitmap(marker);
                routeItem.setBitmap(AndroidGraphicFactory.convertToBitmap(marker));
                routeItem.setMovable(false);
            }
        }

        if (count > 0) {
            requestRedrawRouteOverlay();
            showToast(R.string.msg_save_point_coodinates);
        }

    }
}
