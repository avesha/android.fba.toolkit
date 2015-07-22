package ru.profi1c.samples.tracker;

public final class Const {

    /*
     * На сколько должна изменится текущая позиция (в метрах) прежде чем будет
     * получено новое значение координат
     */
    public static int LOCATION_MIN_DISTANCE = 100;

    /*
     * Максимальное время, которое должно пройти, прежде чем пользователь
     * получает обновление местоположения.
     */
    public static long LOCATION_MIN_TIME = 1000 * 60;
}
