package ru.profi1c.engine.map.mapsforge;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;

import java.util.List;

import ru.profi1c.engine.Const;

/**
 * Базовый класс Mapsforge MapActivity с помощником добавления и навигации по
 * точкам маршрута, см {@link #addRouteOverlay(MapView, List, Drawable)}
 */
public abstract class BaseRouteMapActivity extends BaseMapActivity
        implements OnTouchListener, MovableLayer.MovableLayerItemListener {

    public static final int NO_SELECTION = Const.NOT_SPECIFIED;
    private static final int ON_TAP_TIMEOUT = 500;

    private RouteOverlay<?> mRouteOverlay;
    private int mCurRouteItemIndex;
    private long mOnMoveTimeMs;

    /**
     * Событие возникает при выборе точки маршрута. (интерактивно - по клику
     * пользователем по нему или при программной навигации - см
     * {@link #navigateRoute(int)})
     *
     * @param item Выбранный элемент
     */
    protected abstract void onRouteItemSelect(RouteItem<?> item);

    @Override
    protected void onCreateMapView(MapView mapView) {
        super.onCreateMapView(mapView);
        mapView.setOnTouchListener(this);
    }

    /**
     * Добавить слой с точками маршрута на карту
     *
     * @param mapView       карта
     * @param items         список точек маршрута
     * @param defaultMarker маркер по умолчанию
     */
    public <T> void addRouteOverlay(MapView mapView, List<RouteItem<T>> items,
            Drawable defaultMarker) {

        RouteOverlay<T> routeOverlay = new RouteOverlay<T>(mapView, defaultMarker);
        routeOverlay.setMovableLayerItemListener(this);

        for (RouteItem<T> routeItem : items) {
            routeOverlay.addItem(routeItem);
        }
        mapView.getLayerManager().getLayers().add(routeOverlay);

        mRouteOverlay = routeOverlay;
        mCurRouteItemIndex = NO_SELECTION;
    }

    /**
     * Навигация по маршруту - позиционируемся на точке
     *
     * @param step шаг, +1 - вперед на один элемент, -1 - назад на 1 элемент
     */
    protected void navigateRoute(int step) {

        if (mRouteOverlay == null) {
            throw new IllegalStateException(
                    "The route is not added to the map, use 'addRouteOverlay' method!");
        }

        int count = mRouteOverlay.getItems().size() - 1;
        int nextIndex = mCurRouteItemIndex + step;

        if (nextIndex > count) {
            nextIndex = 0;
        } else if (nextIndex < 0) {
            nextIndex = count;
        }

        setCenterRouteItem(nextIndex);
    }

    /**
     * Установить точку маршрута по центру карты
     *
     * @param index
     */
    protected void setCenterRouteItem(int index) {

        if (mRouteOverlay == null) {
            throw new IllegalStateException(
                    "The route is not added to the map, use 'addRouteOverlay' method!");
        }

        LatLong latLong = mRouteOverlay.getItems().get(index).getLatLong();
        mRouteOverlay.getMapView().getModel().mapViewPosition.setCenter(latLong);
        mCurRouteItemIndex = index;

        onRouteItemSelect(mRouteOverlay.getItems().get(index));
    }

    /**
     * Индекс текущей точки маршрута
     */
    protected int getCurrentRouteItemIndex() {
        return mCurRouteItemIndex;
    }

    /**
     * Получить элемент маршрута по индексу
     *
     * @param index
     * @return
     */
    protected RouteItem<?> getRouteOverlayItem(int index) {

        if (mRouteOverlay == null) {
            throw new IllegalStateException(
                    "The route is not added to the map, use 'addRouteOverlay' method!");
        }
        return mRouteOverlay.getItems().get(index);
    }

    /**
     * Обновить отображение элементов маршрута
     */
    protected void requestRedrawRouteOverlay() {
        if (mRouteOverlay == null) {
            throw new IllegalStateException(
                    "The route is not added to the map, use 'addRouteOverlay' method!");
        }
        mRouteOverlay.requestRedraw();
        mRouteOverlay.getMapView().invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof MapView) {
            MapView mapView = (MapView) v;

            for (Layer layer : mapView.getLayerManager().getLayers()) {
                if (layer instanceof ITouchOverlay) {
                    return ((ITouchOverlay) layer).onTouchEvent(event, mapView);
                }
            }
        }
        return false;
    }

    @Override
    public void onMove(MovableMarker item) {
        if (item instanceof RouteItem) {
            RouteItem<?> routeItemOverlay = (RouteItem<?>) item;
            routeItemOverlay.setMoved(true);
        }
        mOnMoveTimeMs = System.currentTimeMillis();
    }

    @Override
    public void onTap(MovableMarker item, MapView mapView) {
        /*
         * если между событиями маленький интервал, значит это автоматически
		 * сработало событие onTap по окончании перетаскивания элементов
		 */
        if (System.currentTimeMillis() - mOnMoveTimeMs > ON_TAP_TIMEOUT) {
            mCurRouteItemIndex = mRouteOverlay.getItems().lastIndexOf(item);
            onRouteItemSelect((RouteItem<?>) item);
        }

    }

    @Override
    protected void onDestroy() {
        MapView mapView = getMapView();
        if(mapView!=null && mRouteOverlay!=null){
            mapView.getLayerManager().getLayers().remove(mRouteOverlay);
            mRouteOverlay.onDestroy();
        }
        super.onDestroy();
    }
}
