package ru.profi1c.samples.sensus;

import android.graphics.Bitmap;
import android.text.format.DateUtils;

public final class Const {

    public static final String APP = "fba-sample-sensus";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static final boolean USE_MOCK_EXCHANGE = true;
    public static final int NOT_SPECIFIED = -1;

    public static final String ACTION_TASK_CHANGED = "ru.profi1c.samples.sensus.ACTION_TASK_CHANGED";
    public static final String ACTION_SALES_POINT_CHANGED = "ru.profi1c.samples.sensus.ACTION_SALES_POINT_CHANGED";

    /**
     * Максимальный размер фото-миниатюры на карте
     */
    public static int THUMB_MAX_WIDTH = 120;

    /**
     * Качество картинки для эскиза отображаемого на карте
     */
    public static final int THUMB_COMPRESS_QUALITY = 60;

    /**
     * Формат картинки для эскиза отображаемого на карте
     */
    public static final Bitmap.CompressFormat THUMB_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    /**
     * Максимальный размер фото (при выборе или создании)
     */
    public static int PHOTO_MAX_WIDTH = 800;

    /**
     * Качаство картики при сжатии
     */
    public static final int PHOTO_COMPRESS_QUALITY = 70;

    /**
     * Формат картинки для для основного фото торговой точки
     */
    public static final Bitmap.CompressFormat PHOTO_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;

    public static final int REQUESTCODE_SELECT_VIDEO = 3228;

    /**
     * На сколько должна изменится текущая позиция (в метрах) прежде чем будет
     * получено новое значение координат
     */
    public static int LOCATION_MIN_DISTANCE = 50;

    /**
     * Максимальное время, которое должно пройти, прежде чем пользователь
     * получает обновление местоположения.
     */
    public static long LOCATION_MIN_TIME = DateUtils.MINUTE_IN_MILLIS * 5;

    private Const() {}
}
