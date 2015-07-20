package ru.profi1c.engine.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import ru.profi1c.engine.Dbg;

/**
 * Помощник преобразования и форматирования чисел
 */
public final class NumberFormatHelper {
    public static final DecimalFormat MONEY_FORMAT = new DecimalFormat("###,##0.00");

    /**
     * Преобразовать строку к десятичному числу. Формат для Double по умолчанию
     *
     * @param value число строкой
     * @return результат преобразования и 0
     */
    public static double parseDouble(String value) {
        double dValue = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                dValue = Double.parseDouble(value.trim());
            }
        } catch (NumberFormatException e) {
            Dbg.printStackTrace(e);
        }
        return dValue;
    }

    /**
     * Преобразовать строку к десятичному числу. Формат как в функции
     * {@link #formatMoney(double)}
     *
     * @param value число строкой
     * @return результат преобразования и 0
     */
    public static double parseMoney(String value) {
        double dValue = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                dValue = MONEY_FORMAT.parse(value).doubleValue();
            }
        } catch (NumberFormatException e) {
            Dbg.printStackTrace(e);
        } catch (ParseException e) {
            Dbg.printStackTrace(e);
        }
        return dValue;
    }

    /**
     * Преобразовать строку к десятичному числу(убираются разделители разрядов и
     * округляется до 2 знаков после запятой)
     *
     * @param value
     * @return
     */
    public static double parseMoneyEdit(String value) {
        DecimalFormat nf = getDecimalFormat();
        double sum = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                sum = roundMoney(nf.parse(value).doubleValue());
            }
        } catch (ParseException e) {
            Dbg.printStackTrace(e);
        }
        return sum;
    }

    /**
     * Преобразовать строку к целому числу
     */
    public static int parseInt(String value) {
        int iValue = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                iValue = Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            Dbg.printStackTrace(e);
        }
        return iValue;
    }

    private static DecimalFormat getDecimalFormat() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');

        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance();
        nf.setDecimalFormatSymbols(dfs);
        return nf;
    }

    /**
     * Округлить сумму до 2 знаков после запятой
     */
    public static double roundMoney(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Форматировать представление денег
     */
    public static String formatMoney(double value) {
        return MONEY_FORMAT.format(value);
    }

    /**
     * Форматировать десятичное значение для редактирования его в поле ввода как
     * денежное (убираются разделители разрядов и округляется до 2 знаков после
     * запятой)
     */
    public static String formatMoneyEdit(double value) {
        DecimalFormat nf = getDecimalFormat();
        nf.setGroupingUsed(false);
        return nf.format(roundMoney(value));
    }

    private NumberFormatHelper() {
    }
}
