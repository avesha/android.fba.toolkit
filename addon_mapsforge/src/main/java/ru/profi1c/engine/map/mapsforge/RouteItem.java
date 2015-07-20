package ru.profi1c.engine.map.mapsforge;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import android.graphics.drawable.Drawable;

/**
 * Точка маршрута с дополнительными данными
 *
 * @param <T>
 */
public class RouteItem<T> extends MovableMarker {

    private T mData;
    private boolean mMoved;
    private int mOrdinal;

    public RouteItem(LatLong latLong, Drawable drawable) {
        super(latLong, AndroidGraphicFactory.convertToBitmap(drawable));
    }

    public RouteItem(LatLong latLong, Drawable drawable, T data) {
        super(latLong, AndroidGraphicFactory.convertToBitmap(drawable));
        mData = data;
    }

    public RouteItem(LatLong latLong, Bitmap bitmap, T data, int horizontalOffset,
            int verticalOffset) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);
        mData = data;
    }

    /**
     * Возвращает данные связанные с этой точкой маршрута
     */
    public T getData() {
        return mData;
    }

    /**
     * Установить дополнительные данные
     *
     * @param data
     */
    public void setData(T data) {
        mData = data;
    }

    /**
     * Получить порядковый номер маршрута
     */
    public int getOrdinal() {
        return mOrdinal;
    }

    /**
     * Установить порядковый номер маршрута
     */
    public void setOrdinal(int ordinal) {
        mOrdinal = ordinal;
    }

    /**
     * Возвращает признак того, что элемент был перемещен на карте
     */
    public boolean isMoved() {
        return mMoved;
    }

    /**
     * Установить признак того, что элемент перемещался
     *
     * @param moved
     */
    public void setMoved(boolean moved) {
        mMoved = moved;
    }
}
