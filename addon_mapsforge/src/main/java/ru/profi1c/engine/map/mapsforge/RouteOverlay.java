package ru.profi1c.engine.map.mapsforge;

import org.mapsforge.map.android.view.MapView;

import android.graphics.drawable.Drawable;

/**
 * Слой для отображения точек маршрута
 *
 * @param <T>
 */
public class RouteOverlay<T> extends MovableLayer<RouteItem<T>> {

    public RouteOverlay(MapView mapView, Drawable defaultMarker) {
        super(mapView, defaultMarker);
    }

}
