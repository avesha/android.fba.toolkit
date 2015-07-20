package ru.profi1c.engine.map.mapsforge;

import org.mapsforge.map.android.view.MapView;

import android.view.MotionEvent;

public interface ITouchOverlay {
    boolean onTouchEvent(MotionEvent event, MapView mapView);
}
