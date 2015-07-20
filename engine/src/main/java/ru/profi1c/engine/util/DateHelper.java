package ru.profi1c.engine.util;

import android.text.format.DateUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Помощник работы с датами: форматирование, разность дней и т.п
 */
public final class DateHelper {

    private static final TimeZone TIME_ZONE_0 = TimeZone.getTimeZone("Etc/GMT");

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");

    private static final String TIME_FORMAT = "HH:mm";
    private static final long EMPTY_DATE_MS = -62170171200000L;

    public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Форматировать дату по умолчанию, формат "dd.MM.yyyy HH:mm:ss"
     */
    public static String format(Date dt) {
        return DATE_FORMAT.format(dt);
    }

    /**
     * Форматировать дату формату
     *
     * @param dt      дата
     * @param pattern форматная строка например: "dd MMM yy 'г.'",
     *                "dd-MM-yyyy HH:mm:SS"
     * @return
     */
    public static String format(Date dt, String pattern) {
        SimpleDateFormat frm = new SimpleDateFormat(pattern);
        frm.setLenient(true);
        return frm.format(dt);
    }

    /**
     * Форматировать дату по короткому формату "dd.MM.yy"
     */
    public static String formatShortDate(Date dt) {
        return SHORT_DATE_FORMAT.format(dt);
    }

    /**
     * Форматировать время по короткому формату "HH:mm"
     */
    public static String formatShortTime(Date dt) {
        SimpleDateFormat frm = new SimpleDateFormat(TIME_FORMAT);
        return frm.format(dt);
    }

    public static String formatISO8601(Date dt) {
        SimpleDateFormat ISO8601 =
                new SimpleDateFormat(DATE_FORMAT_ISO8601, java.util.Locale.ENGLISH);
        return ISO8601.format(dt);
    }

    public static Calendar getEmptyCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        return cal;
    }

     /**
     * Конструктор календаря
     *
     * @param year  год полностью (4 знака)
     * @param month месяц, например Calendar.MAY или номер месяца (но следует
     *              помнить что нумерация месяцев начинается с нуля)
     * @param day   день месяца
     * @return
     */
    public static Calendar calendar(int year, int month, int day) {
        Calendar cal = getEmptyCalendar();
        cal.set(year, month, day, 0, 0, 0);
        return cal;
    }

    /**
     * Конструктор даты
     *
     * @param year  год полностью (4 знака)
     * @param month месяц, например Calendar.MAY или номер месяца (но следует
     *              помнить что нумерация месяцев начинается с нуля)
     * @param day   день месяца
     * @return
     */
    public static Date date(int year, int month, int day) {
        Calendar cal = calendar(year, month, day);
        return cal.getTime();
    }

    /**
     * Возвращает начало дня
     */
    public static Date beginOfDay(Date dt) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Возвращает конец дня
     */
    public static Date endOfDay(Date dt) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);
        return cal.getTime();
    }

    /**
     * Возвращает разность дней
     */
    public static int diffDays(Date dtEnd, Date dtBegin) {
        return (int) ((beginOfDay(dtEnd).getTime() - beginOfDay(dtBegin).getTime()) /
                      DateUtils.DAY_IN_MILLIS);
    }

    /**
     * Установить время в дате
     *
     * @param date - дата для которой выставляется время
     * @param time - время (как дата)
     * @return
     */
    public static Date setTime(Date date, Date time) {

        Calendar cal = getEmptyCalendar();
        cal.setTime(date);

        Calendar calTime = getEmptyCalendar();
        calTime.setTime(time);

        cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND));

        return cal.getTime();
    }

    public static Date getTime(Date datetime) {
        Date time = new Date(0);
        time.setHours(datetime.getHours());
        time.setMinutes(datetime.getMinutes());
        time.setSeconds(datetime.getSeconds());
        return time;
    }

    /**
     * Возвращает истина если у даты установлено только время
     */
    public static boolean isOnlyTime(Date dt) {

        Calendar cal = getEmptyCalendar();
        cal.setTime(dt);

        return (cal.get(Calendar.YEAR) == 1970 &&
                cal.get(Calendar.MONTH) == Calendar.JANUARY &&
                cal.get(Calendar.DAY_OF_MONTH) == 1) &&
               (cal.get(Calendar.HOUR) > 0 || cal.get(Calendar.MINUTE) > 0 ||
                cal.get(Calendar.SECOND) > 0);
    }

    /**
     * Только дата, время нулевое
     */
    public static boolean isOnlyDate(Date dt) {
        Date dtBegin = beginOfDay(dt);
        return dtBegin.equals(dt);
    }

    /**
     * Добавить день к дате
     */
    public static Date addDays(Date date, int count) {
        Calendar cal = getEmptyCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, count);
        return cal.getTime();
    }

    public static boolean isEmpty(Date date) {
        if(date == null || date.getTime() == EMPTY_DATE_MS) {
            return true;
        }
        Calendar cal = getEmptyCalendar();
        cal.setTime(date);

        return  cal.get(Calendar.YEAR) == 1970 &&
                cal.get(Calendar.MONTH) == Calendar.JANUARY &&
                cal.get(Calendar.DAY_OF_MONTH) == 1 &&
                cal.get(Calendar.HOUR) == 0 &&
                cal.get(Calendar.MINUTE) == 0 &&
                cal.get(Calendar.SECOND) == 0 &&
                cal.get(Calendar.MILLISECOND) == 0;
    }

    /**
     * Возвращает массив локализованных названий дней недели. Порядок следования
     * так же локализован, для России первый понедельник, для английской локали
     * - воскресенье
     */
    public static String[] getWeekdays() {
        String[] days = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
        return getSortWeekdays(days, Calendar.getInstance().getFirstDayOfWeek());
    }

    /**
     * Возвращает массив локализованных сокращенных названий дней недели.
     * Порядок следования так же локализован, для России первый понедельник, для
     * английской локали - воскресенье
     */
    public static String[] getShortWeekdays() {
        String[] days = new DateFormatSymbols(Locale.getDefault()).getShortWeekdays();
        return getSortWeekdays(days, Calendar.getInstance().getFirstDayOfWeek());
    }

    /**
     * Возвращает массив индексов номеров дней недель, порядок следования
     * элементов с массиве локализован, для России int[0] - понедельник,
     * значение = 2. Для английской локали int[0] - воскресенье, значение = 1
     */
    public static int[] getIndexWeekdays() {
        int firstDay = Calendar.getInstance().getFirstDayOfWeek();
        int[] dayWeek = new int[7];
        for (int i = 0; i < 7; i++) {
            dayWeek[i] = firstDay++;

            if (firstDay > 7)
                firstDay = 1;
        }
        return dayWeek;
    }

    private static String[] getSortWeekdays(String[] days, int firstDay) {
        String[] daysSort = new String[7];
        for (int i = 0; i < 7; i++) {
            daysSort[i] = days[firstDay++];

            if (firstDay > 7)
                firstDay = 1;
        }
        return daysSort;
    }

    private DateHelper() {
    }
}
