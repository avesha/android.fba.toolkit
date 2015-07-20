package ru.profi1c.engine.map.mapsforge;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Marker;

/**
 * Элемент, который хранит свойство что его можно двигать
 */
public class MovableMarker extends Marker {

    /**
     * Краткое описание элемента.
     */
    protected String mSnippet;

    /**
     * Название этого пункта.
     */
    protected String mTitle;

    protected boolean mMovable;

    public MovableMarker(LatLong latLong, Bitmap bitmap) {
        super(latLong, bitmap, 0, -bitmap.getHeight());
    }

    public MovableMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);
    }

    public synchronized boolean isMovable() {
        return mMovable;
    }

    public synchronized void setMovable(boolean movable) {
        this.mMovable = movable;
    }

    /**
     * @return краткое описание этого пункта (может быть пустым).
     */
    public synchronized String getSnippet() {
        return this.mSnippet;
    }

    /**
     * @return название данного пункта, (может быть пустым).
     */
    public synchronized String getTitle() {
        return this.mTitle;
    }

    /**
     * Задает краткое описание этого пункта.
     *
     * @param snippet краткое описание элемента (может быть пустым).
     */
    public synchronized void setSnippet(String snippet) {
        this.mSnippet = snippet;
    }

    /**
     * Задает название данного пункта.
     *
     * @param title Название элемента (может быть пустым).
     */
    public synchronized void setTitle(String title) {
        this.mTitle = title;
    }
}
