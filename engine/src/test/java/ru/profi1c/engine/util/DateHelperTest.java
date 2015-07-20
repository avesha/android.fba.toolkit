package ru.profi1c.engine.util;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.profi1c.engine.test.RobolectricTestCase;

import static org.junit.Assert.*;

public class DateHelperTest extends RobolectricTestCase {

    @Test
    public void testFormat() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.JANUARY, 6, 23, 59, 59);
        final String strDate = DateHelper.format(cal.getTime());
        assertEquals(strDate, "06.01.2015 23:59:59");
    }

    @Test
    public void testFormat1() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.FEBRUARY, 23, 9, 12, 30);
        final String strDate = DateHelper.format(cal.getTime(), "dd MMM yy 'года'");

        final String lang = Locale.getDefault().getLanguage();
        if (lang.equals("ru")) {
            assertEquals(strDate, "23 фев 15 года");
        } else if (lang.equals("en")) {
            assertEquals(strDate, "23 Feb 15 года");
        } else {
            fail("No test, set unknown locale");
        }

    }

    @Test
    public void testFormatShortDate() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.FEBRUARY, 23, 9, 12, 30);
        final String strDate = DateHelper.formatShortDate(cal.getTime());
        assertEquals(strDate, "23.02.15");
    }

    @Test
    public void testFormatShortTime() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.FEBRUARY, 1, 9, 0, 0);
        final String strDate = DateHelper.formatShortTime(cal.getTime());
        assertEquals(strDate, "09:00");
    }

    @Test
    public void testFormatISO8601() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.FEBRUARY, 23, 9, 12, 30);
        final String strDate = DateHelper.formatISO8601(cal.getTime());
        assertEquals(strDate, "2015-02-23T09:12:30");
    }

    @Test
    public void testDate() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2015, Calendar.FEBRUARY, 23, 0, 0, 0);
        final Date date = DateHelper.date(2015, 1, 23);
        assertEquals(date, cal.getTime());
    }

    @Test
    public void testGetTime() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2015, Calendar.FEBRUARY, 23, 15, 0, 0);
        Date time = DateHelper.getTime(cal.getTime());
        assertTrue(DateHelper.isOnlyTime(time));
    }

    @Test
    public void testCalendar() throws Exception {
        Calendar cal = DateHelper.calendar(2014, Calendar.APRIL, 1);
        Date date = new Date(114, 3, 1, 0, 0);
        assertEquals(date, cal.getTime());
    }

    @Test
    public void testBeginOfDay() throws Exception {
        Calendar cal = Calendar.getInstance();
        Date date = DateHelper.beginOfDay(cal.getTime());
        assertEquals(0, date.getHours());
        assertEquals(0, date.getMinutes());
        assertEquals(0, date.getSeconds());
        assertEquals(1900 + date.getYear(), cal.get(Calendar.YEAR));
        assertEquals(date.getMonth(), cal.get(Calendar.MONTH));
        assertEquals(date.getDate(), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testEndOfDay() throws Exception {
        Calendar cal = Calendar.getInstance();
        Date date = DateHelper.endOfDay(cal.getTime());
        assertEquals(23, date.getHours());
        assertEquals(59, date.getMinutes());
        assertEquals(59, date.getSeconds());
        assertEquals(1900 + date.getYear(), cal.get(Calendar.YEAR));
        assertEquals(date.getMonth(), cal.get(Calendar.MONTH));
        assertEquals(date.getDate(), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testDiffDays() throws Exception {
        final int days = 5;
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.roll(Calendar.DAY_OF_YEAR, 5);
        int dif = DateHelper.diffDays(cal2.getTime(), cal.getTime());
        assertEquals(dif, days);

        cal2.roll(Calendar.DAY_OF_YEAR, -10);
        dif = DateHelper.diffDays(cal2.getTime(), cal.getTime());
        assertEquals(dif, -days);
    }

    @Test
    public void testSetTime() throws Exception {
        Calendar cal = Calendar.getInstance();
        Date date = new Date(114, 3, 1, 10, 30);
        Date dateResult = DateHelper.setTime(cal.getTime(), date);
        assertEquals(10, date.getHours());
        assertEquals(30, date.getMinutes());
        assertEquals(0, date.getSeconds());
    }

    @Test
    public void testIsOnlyTime() throws Exception {
        SimpleDateFormat frm = new SimpleDateFormat("HH:mm");
        Date dt = frm.parse("12:30");
        assertTrue(DateHelper.isOnlyTime(dt));
    }

    @Test
    public void testIsOnlyDate() throws Exception {
        SimpleDateFormat frm = new SimpleDateFormat("dd.MM.yyyy");
        Date dt = frm.parse("01.01.2014");
        assertTrue(DateHelper.isOnlyDate(dt));
    }

    @Test
    public void testAddDays() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, 5);

        Calendar cal2 = Calendar.getInstance();
        Date dateResult = DateHelper.addDays(cal2.getTime(), 5);

        assertEquals(0, DateHelper.diffDays(dateResult, cal.getTime()));
    }

    @Test
    public void testIsEmpty() throws Exception {
        SimpleDateFormat frm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = frm.parse("00.00.0000 00:00:00");
        assertTrue(DateHelper.isEmpty(date));
    }

    @Test
    public void testGetWeekdays() throws Exception {
        final String lang = Locale.getDefault().getLanguage();
        String[] weekdays = DateHelper.getWeekdays();
        if (lang.equals("ru")) {
            assertEquals("понедельник", weekdays[0]);
            assertEquals("вторник", weekdays[1]);
            assertEquals("среда", weekdays[2]);
            assertEquals("четверг", weekdays[3]);
            assertEquals("пятница", weekdays[4]);
            assertEquals("суббота", weekdays[5]);
            assertEquals("воскресенье", weekdays[6]);
        } else if (lang.equals("en")) {
            assertEquals("Sunday", weekdays[0]);
            assertEquals("Monday", weekdays[1]);
            assertEquals("Tuesday", weekdays[2]);
            assertEquals("Wednesday", weekdays[3]);
            assertEquals("Thursday", weekdays[4]);
            assertEquals("Friday", weekdays[5]);
            assertEquals("Saturday", weekdays[6]);
        } else {
            fail("No test, set unknown locale");
        }
    }

    @Test
    public void testGetShortWeekdays() throws Exception {
        final String lang = Locale.getDefault().getLanguage();
        String[] weekdays = DateHelper.getShortWeekdays();
        if (lang.equals("ru")) {
            assertEquals("Пн", weekdays[0]);
            assertEquals("Вт", weekdays[1]);
            assertEquals("Ср", weekdays[2]);
            assertEquals("Чт", weekdays[3]);
            assertEquals("Пт", weekdays[4]);
            assertEquals("Сб", weekdays[5]);
            assertEquals("Вс", weekdays[6]);
        } else if (lang.equals("en")) {
            assertEquals("Sun", weekdays[0]);
            assertEquals("Mon", weekdays[1]);
            assertEquals("Tue", weekdays[2]);
            assertEquals("Wed", weekdays[3]);
            assertEquals("Thu", weekdays[4]);
            assertEquals("Fri", weekdays[5]);
            assertEquals("Sat", weekdays[6]);
        } else {
            fail("No test, set unknown locale");
        }
    }

    @Test
    public void testGetIndexWeekdays() throws Exception {
        final String lang = Locale.getDefault().getLanguage();
        int[] indexWeekdays = DateHelper.getIndexWeekdays();
        if (lang.equals("ru")) {
            assertEquals(2, indexWeekdays[0]);
            assertEquals(3, indexWeekdays[1]);
            assertEquals(4, indexWeekdays[2]);
            assertEquals(5, indexWeekdays[3]);
            assertEquals(6, indexWeekdays[4]);
            assertEquals(7, indexWeekdays[5]);
            assertEquals(1, indexWeekdays[6]);
        } else if (lang.equals("en")) {
            assertEquals(1, indexWeekdays[0]);
            assertEquals(2, indexWeekdays[1]);
            assertEquals(3, indexWeekdays[2]);
            assertEquals(4, indexWeekdays[3]);
            assertEquals(5, indexWeekdays[4]);
            assertEquals(6, indexWeekdays[5]);
            assertEquals(7, indexWeekdays[6]);
        } else {
            fail("No test, set unknown locale");
        }
    }
}