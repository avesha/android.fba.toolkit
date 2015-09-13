package ru.profi1c.samples.sensus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.util.MediaHelper;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.CatalogSalesPointsDao;

public class LocationFragment extends BaseFragment {

    private static final int MAP_POINT_ZOOM_LEVEL = 17;

    //мин. расстояние до точки при увеличении MAP_POINT_ZOOM_LEVEL
    private static final int VIBRATE_TIME = 20;
    private static final int MARKER_TEXT_SIZE = 20;

    private GoogleMap mMap;
    private List<RoutePoint> mRoutePoints;
    private List<CatalogSalesPoints> mRouteList;
    private int mCurRoutePointIndex;

    private Vibrator mVibrator;

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_location, container, false);
        initControls(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        activity.setTitle(R.string.nav_drawer_item_location);
        mVibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        try {
            inflateData();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_location, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_switch_map_style) {
            switchMapMode();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initControls(View root) {
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {

            FbaApplication app = FbaApplication.from(root.getContext());
            LayoutInflater inflater =
                    (LayoutInflater) app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mMap.setInfoWindowAdapter(new PopupAdapter(inflater,
                                                       app.getFbaSettingsProvider().getAppSettings()
                                                          .getCacheDir()));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {

                    RoutePoint routePoint = findByMarker(marker);
                    if (routePoint != null) {
                        mVibrator.vibrate(VIBRATE_TIME);
                        performShowSalesPoint(routePoint.mSalesPoints);
                    }
                }
            });

            //кнопки навигации
            Button btn = (Button) root.findViewById(R.id.btnPrev);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mVibrator.vibrate(VIBRATE_TIME);
                    navigateRoute(-1);
                }
            });

            btn = (Button) root.findViewById(R.id.btnNext);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mVibrator.vibrate(VIBRATE_TIME);
                    navigateRoute(1);
                }
            });

            mMap.setMyLocationEnabled(true);

        } else {
            Toast.makeText(root.getContext(), R.string.msg_not_support_google_maps,
                           Toast.LENGTH_SHORT).show();
        }
    }

    private void inflateData() throws SQLException {
        CatalogSalesPointsDao dao = getHelper().getDao(CatalogSalesPoints.class);
        mRoutePoints = new ArrayList<RoutePoint>();
        mRouteList = dao.selectRoute(false);
        if (mRouteList.size() > 0) {
            addMarkersToMap();
            setCenterRoutePoint(0, false);
        }
    }

    /*
     * Добавить маркеры на карту
	 */
    private void addMarkersToMap() {

        final Context context = getActivity();
        mRoutePoints.clear();
        mMap.clear();

        for (int i = 0; i < mRouteList.size(); i++) {
            CatalogSalesPoints salesPoint = mRouteList.get(i);
            final double lat = salesPoint.lat;
            final double lng = salesPoint.lng;
            if (lat == 0 || lng == 0) {
                continue;
            }
            //На маркере вывести порядковый  номер
            BitmapDrawable bmp = makeNumberedMarker(context, R.mipmap.map_marker_orange, i + 1);

            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                                                              .icon(BitmapDescriptorFactory
                                                                            .fromBitmap(
                                                                                    bmp.getBitmap())));
            mRoutePoints.add(new RoutePoint(salesPoint, marker));

        }
    }

    private void setCenterRoutePoint(int index, boolean showInfo) {

        if (mRoutePoints.size() > 0) {
            RoutePoint routePoint = mRoutePoints.get(index);

            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(routePoint.mMarker.getPosition(), MAP_POINT_ZOOM_LEVEL);
            mMap.animateCamera(cameraUpdate);

            if (showInfo) {
                routePoint.mMarker.showInfoWindow();
            }
            mCurRoutePointIndex = index;
        }

    }

    /**
     * Навигация по маршруту - позиционируемся на точке
     *
     * @param step шаг, +1 - вперед на один элемент, -1 - назад на 1 элемент
     */
    protected void navigateRoute(int step) {
        int count = mRoutePoints.size() - 1;
        int nextIndex = mCurRoutePointIndex + step;

        if (nextIndex > count) {
            nextIndex = 0;
        } else if (nextIndex < 0) {
            nextIndex = count;
        }
        setCenterRoutePoint(nextIndex, true);
    }


    /*
     * Найти элемент маршрута по его маркеру
	 */
    private RoutePoint findByMarker(Marker marker) {
        RoutePoint found = null;
        for (RoutePoint item : mRoutePoints) {
            if (item.mMarker.equals(marker)) {
                found = item;
                break;
            }
        }
        return found;
    }

    /**
     * Создать пронумерованный маркер
     *
     * @param ctx           Текущий контекст
     * @param resIdDrawable Идентификатор ресурса маркера
     * @param number        Число выводимое по центру маркера
     * @return
     */
    public static BitmapDrawable makeNumberedMarker(Context ctx, int resIdDrawable, int number) {
        return MediaHelper
                .drawTextOnDrawable(ctx, resIdDrawable, String.valueOf(number), MARKER_TEXT_SIZE);
    }

    /*
     * Последовательное переключение типа карты
	 */
    private void switchMapMode() {
        int type = mMap.getMapType();
        type++;
        if (type > 4) {
            type = 1;
        }
        mMap.setMapType(type);
    }

    private void performShowSalesPoint(CatalogSalesPoints salesPoint) {
        final Activity activity = getActivity();
        Intent intent = SalesPointActivity.getStartIntent(activity, salesPoint,
                                                          activity.getResources()
                                                                  .getColor(R.color.primary), true);
        activity.startActivity(intent);
    }

    /*
    * Адаптер для всплывающих сообщений по клику на маркере
    */
    private class PopupAdapter implements GoogleMap.InfoWindowAdapter {
        private final LayoutInflater mInflater;
        private final File mCacheDir;

        PopupAdapter(LayoutInflater inflater, File cacheDir) {
            mInflater = inflater;
            mCacheDir = cacheDir;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View popup = mInflater.inflate(R.layout.map_popup_window, null);

            RoutePoint routePoint = findByMarker(marker);
            if (routePoint != null) {

                TextView desc = (TextView) popup.findViewById(R.id.tvDescription);
                TextView address = (TextView) popup.findViewById(R.id.tvAddress);
                ImageView image = (ImageView) popup.findViewById(R.id.ivPhoto);

                desc.setText(routePoint.mSalesPoints.getPresentation());
                address.setText(routePoint.mSalesPoints.getAddress());

                File file = new File(mCacheDir, routePoint.mSalesPoints.getThumbName());
                if (file.exists()) {
                    image.setImageURI(Uri.fromFile(file));
                    image.setVisibility(View.VISIBLE);
                } else {
                    image.setVisibility(View.GONE);
                }
            }
            return popup;
        }
    }

    /*
   * Точка маршрута на карте
   */
    private static class RoutePoint {
        final CatalogSalesPoints mSalesPoints;
        final Marker mMarker;

        RoutePoint(CatalogSalesPoints salesPoints, Marker marker) {
            this.mSalesPoints = salesPoints;
            this.mMarker = marker;
        }
    }
}
