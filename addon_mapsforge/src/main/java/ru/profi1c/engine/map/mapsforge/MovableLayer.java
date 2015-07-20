package ru.profi1c.engine.map.mapsforge;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.view.MotionEvent;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.util.MapViewProjection;

import java.util.ArrayList;
import java.util.List;

public class MovableLayer<Item extends MovableMarker> extends Layer implements ITouchOverlay {

    private static final int VIBRATE_TIME = 20;
    public static final double INFELICITY = 1.1;

    public interface MovableLayerItemListener {
        void onMove(MovableMarker item);

        void onTap(MovableMarker item, MapView mapView);
    }

    private final MapView mMapView;
    private final Bitmap mDefaultMarker;
    private final Vibrator mVibrator;
    private final List<Item> mData;

    private MovableLayerItemListener mMovableLayerItemListener;

    private MovableMarker mMovableItem;
    private int mDx;
    private int mDy;

    public MovableLayer(MapView mapView, Drawable defaultMarker) {
        this(mapView, new ArrayList<Item>(), defaultMarker);
    }

    public MovableLayer(MapView mapView, List<Item> items, Drawable defaultMarker) {
        mMapView = mapView;
        mDefaultMarker = AndroidGraphicFactory.convertToBitmap(defaultMarker);
        mData = items;
        mVibrator = (Vibrator) mapView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        for (Item item : mData) {
            assignDisplayModel(item);
        }
    }

    public void setMovableLayerItemListener(MovableLayerItemListener movableLayerItemListener) {
        mMovableLayerItemListener = movableLayerItemListener;
    }

    public MapView getMapView() {
        return mMapView;
    }

    public void addItem(Item item) {
        mData.add(item);
        assignDisplayModel(item);
    }

    public int size() {
        return mData.size();
    }

    public List<Item> getItems() {
        return mData;
    }

    @Override
    public void draw(BoundingBox boundingBox, byte b, Canvas canvas, Point point) {
        for (Item item : getItems()) {
            if (item.getBitmap() == null) {
                item.setBitmap(mDefaultMarker);
            }
            item.draw(boundingBox, b, canvas, point);
        }
    }

    private void assignDisplayModel(Item item) {
        item.setDisplayModel(mMapView.getModel().displayModel);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            mMovableItem = findItemOnTap((int) event.getX(), (int) event.getY(), true);
            if (mMovableItem != null) {
                mVibrator.vibrate(VIBRATE_TIME);
            }

        } else if (action == MotionEvent.ACTION_UP) {

            mMovableItem = null;
            if (mMovableLayerItemListener != null) {
                Item item = findItemOnTap((int) event.getX(), (int) event.getY(), false);
                if (item != null) {
                    mMovableLayerItemListener.onTap(item, mapView);
                }
            }

        } else if (action == MotionEvent.ACTION_MOVE) {

            if (mMovableItem != null) {
                MapViewProjection projection = new MapViewProjection(mMapView);
                LatLong latLong = projection.fromPixels(event.getX() - mDx, event.getY() + mDy);
                mMovableItem.setLatLong(latLong);

                if (mMovableLayerItemListener != null) {
                    mMovableLayerItemListener.onMove(mMovableItem);
                }

                mMovableItem.requestRedraw();
                requestRedraw();
                mMapView.invalidate();
                return true;
            }
        }
        return false;
    }


    private Item findItemOnTap(int x, int y, boolean onlyMovable) {
        MapViewProjection projection = new MapViewProjection(mMapView);
        Item found = null;

        for (Item item : getItems()) {
            if (onlyMovable && !item.isMovable()) {
                continue;
            }

            // текущие geo данные маркера конвертируем в экранные координаты
            Point p = projection.toPixels(item.getLatLong());

            // размеры маркера
            Bitmap marker = item.getBitmap();
            if (marker == null) {
                marker = mDefaultMarker;
            }

            int h = (int)(marker.getHeight() * INFELICITY);
            int wd2 = (int) (marker.getWidth() / 2 * INFELICITY);

            // если клик по маркеру
            if (x > p.x - wd2 && x < p.x + wd2 && y > p.y - h && y < p.y) {
                if (onlyMovable) {
                    mDx = (int) (x - p.x);
                    mDy = (int) (p.y - y);
                }
                found = item;
                break;
            }
        }
        return found;
    }

}
