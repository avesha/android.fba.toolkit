package ru.profi1c.engine.widget;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.profi1c.engine.test.RobolectricTestCase;
import ru.profi1c.engine.util.DateHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FieldFormatterTest extends RobolectricTestCase {
    private static final String TAG = FieldFormatterTest.class.getSimpleName();

    @Test
    public void testDefFormatter() {
        FieldFormatter formatter = new FieldFormatter.Builder().create();
        format(formatter);
    }

    private void format(FieldFormatter formatter){

        Boolean bTrue = true;
        Boolean bFalse = false;

        final String strTrue = formatter.format(bTrue);
        log(TAG, " Boolean = " + strTrue);
        assertEquals("true", strTrue);

        final String strFalse = formatter.format(bFalse);
        log(TAG, " Boolean = " + strFalse);
        assertEquals("false", strFalse);

        final String strFalseDef = formatter.format(false);
        log(TAG, " boolean = " + strFalseDef);
        assertEquals(strFalseDef, strFalse);

        int[] data = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 123, 123456, 12345678};
        for(int i=0; i<data.length ; i++){
            int value = data[i];
            final String strFrmValue = formatter.format(value);
            log(TAG, " int = " + strFrmValue);
            final String strValue = Integer.toString(value);
            assertEquals(strValue, strFrmValue);
        }

        Double iDouble1 = 123.456;
        Double iDouble2 = 123d;
        Double iDouble3 = 123.0456;
        Double iDouble4 = 123.00456;

        final String lang = Locale.getDefault().getLanguage();
        if (lang.equals("ru")) {

            assertEquals("123,46", formatter.format(iDouble1));
            assertEquals("123,00", formatter.format(iDouble2));
            assertEquals("123,05", formatter.format(iDouble3));
            assertEquals("123,00", formatter.format(iDouble4));

            assertEquals("789 456 123,00", formatter.format(789456123.00));
            assertEquals("123 456,00", formatter.format(123456.00));
            assertEquals("123 456,00", formatter.format(123456.00123));

        } else if (lang.equals("en")) {

            assertEquals("123.46", formatter.format(iDouble1));
            assertEquals("123.00", formatter.format(iDouble2));
            assertEquals("123.05", formatter.format(iDouble3));
            assertEquals("123.00", formatter.format(iDouble4));

            assertEquals("789,456,123.00", formatter.format(789456123.00));
            assertEquals("123,456.00", formatter.format(123456.00));
            assertEquals("123,456.00", formatter.format(123456.00123));

        } else {
            fail("No test, set unknown locale");
        }


        Object obj = null;
        assertEquals("", formatter.format(obj));

        Object str = "привет";
        assertEquals("привет", formatter.format(str));
        assertEquals("%hello word!", formatter.format("%hello word!"));

        Date date = DateHelper.date(2015, 0, 1);
        assertEquals("01.01.2015", formatter.format(date));

        Object endOfDay = DateHelper.endOfDay(date);
        Object dtOnlyTime = DateHelper.getTime((Date)endOfDay);

        assertEquals("23:59", formatter.format(dtOnlyTime));
    }

    @Test
    public void testCustomBooleanFormatter() {
        FieldFormatter formatter = new FieldFormatter.Builder()
                .setBooleanFormat("да", "нет")
                .create();

        final String strTrue = formatter.format(true);
        assertEquals("да", strTrue);

        final String strFalse = formatter.format(false);
        assertEquals("нет", strFalse);
    }

    @Test
    public void testCustomStringFormatter() {
        FieldFormatter formatter = new FieldFormatter.Builder()
                .setStringFormat(" префикс: %s")
                .create();

        assertEquals(" префикс: a50", formatter.format("a50"));
    }

    @Test
    public void testCustomDateFormatter() {
        FieldFormatter formatter = new FieldFormatter.Builder()
                .setDateFormat("yy-MMM-dd")
                .create();

        Date date = DateHelper.date(2015, Calendar.AUGUST, 4);
        final String lang = Locale.getDefault().getLanguage();
        if (lang.equals("ru")) {
            assertEquals("15-авг-04", formatter.format(date));
        } else if (lang.equals("en")) {
            assertEquals("15-Aug-04", formatter.format(date));
        } else {
            fail("No test, set unknown locale");
        }

    }

    @Test
    public void testCustomNullFormatter() {
        FieldFormatter formatter = new FieldFormatter.Builder()
                .setNullFormat("-")
                .create();
        assertEquals("-", formatter.format(null));
    }

    @Test
    public void testZeroFormatter() {
        FieldFormatter formatter = new FieldFormatter.Builder()
                .setZeroFormat("-")
                .create();
        assertEquals("-", formatter.format(0));
        assertEquals("-", formatter.format(0.0));
    }

    @Test
    public void testIntChoiceFormatter() {
        FieldFormatter formatter = new FieldFormatter.Builder()
                .setIntChoiceFormat("1#Пн|2#Вт|3#Ср|4#Чт|5#Пт|6#Сб|7#Вс")
                .create();
        assertEquals("Пн", formatter.format(1));
        assertEquals("Ср", formatter.format(3));
        assertEquals("Пт", formatter.format(5));
    }

    public void testIntZeroFormat() {
        FieldFormatter formatter = new FieldFormatter.Builder().create();
        int zeroInt = 0;
        assertEquals("", formatter.format(zeroInt));

        long zeroLong = 0L;
        assertEquals("", formatter.format(zeroLong));

        formatter = new FieldFormatter.Builder().setZeroFormat("empty").create();
        assertEquals("empty", formatter.format(zeroInt));
        assertEquals("empty", formatter.format(zeroLong));

    }

    public void testDoubleseroFormat() {

        FieldFormatter formatter = new FieldFormatter.Builder().create();
        double zeroD = 0.0;
        assertEquals("", formatter.format(zeroD));

        float zerof = 0f;
        assertEquals("", formatter.format(zerof));

        formatter = new FieldFormatter.Builder().setZeroFormat("empty").create();
        assertEquals("empty", formatter.format(zeroD));
        assertEquals("empty", formatter.format(zerof));
    }


}