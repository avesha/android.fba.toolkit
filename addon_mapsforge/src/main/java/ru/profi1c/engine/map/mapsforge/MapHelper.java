package ru.profi1c.engine.map.mapsforge;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;

import org.mapsforge.core.model.LatLong;

import ru.profi1c.engine.util.MediaHelper;

public class MapHelper {

    public static final byte ZOOM_LEVEL_MIN = 10;
    public static final byte ZOOM_LEVEL_CITY = 13;
    public static final byte ZOOM_LEVEL_AREA = 14;
    public static final byte ZOOM_LEVEL_KVARTAL = 15;
    public static final byte ZOOM_LEVEL_STREET = 16;
    public static final byte ZOOM_LEVEL_MAX = 21;

    public static final int MARKER_TEXT_SIZE = 20;

    /**
     * Представление (широта и долгота в градусах)
     */
    public static String format(LatLong p) {
        String strLatitude = Location.convert(p.latitude, Location.FORMAT_DEGREES);
        String strLongitude = Location.convert(p.longitude, Location.FORMAT_DEGREES);
        return strLatitude + "," + strLongitude;
    }

    public static LatLong toLatLong(final double latitude, final double longitude) {
        return new LatLong(latitude, longitude);
    }

    /**
     * Как географическое местоположение (время текущее).
     *
     * @param lat Широта в градусах
     * @param lng Долгота в градусах
     * @return
     */
    public static Location getLocation(double lat, double lng) {
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        loc.setTime(System.currentTimeMillis());
        return loc;
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
        return MediaHelper.drawTextOnDrawable(ctx, resIdDrawable, String.valueOf(number),
                MARKER_TEXT_SIZE);
    }

}
