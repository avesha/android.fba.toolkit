package ru.profi1c.engine.widget;

import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.util.DateHelper;

/**
 * Форматирование значений (когда требуется их автоматическое преобразование к
 * строке)
 */
public class FieldFormatter {

    private Locale mLocale;

    private String mFormatNull = Const.DEFAULT_WIDGET_NULL_FORMAT;
    private String mFormatZero = null;
    private String mFormatBoolean = Const.DEFAULT_WIDGET_BOOLEAN_FORMAT;
    private String mFormatTrue = null;
    private String mFormatFalse = null;
    private String mFormatInt = Const.DEFAULT_WIDGET_INT_FORMAT;
    private ChoiceFormat mFormatIntChoice;

    private String mFormatString = Const.DEFAULT_WIDGET_STRING_FORMAT;

    private String mFormatDouble = Const.DEFAULT_WIDGET_DOUBLE_FORMAT;
    private String mFormatDate = Const.DEFAULT_WIDGET_DATE_FULL_YEAR_FORMAT;
    private String mFormatTime = Const.DEFAULT_WIDGET_TIME_FORMAT;
    private String mFormatDateTime = Const.DEFAULT_WIDGET_DATETIME_FORMAT;

    private SimpleDateFormat mDateFormat, mDateTimeFormat, mTimeFormat;
    private DecimalFormat mDecimalFormat;

    private FieldFormatter() {
        mLocale = Locale.getDefault();
    }

    public Locale getLocale() {
        return mLocale;
    }

    public SimpleDateFormat getDateFormat() {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat(mFormatDate, mLocale);
        }
        return mDateFormat;
    }

    public SimpleDateFormat getTimeFormat() {
        if (mTimeFormat == null) {
            mTimeFormat = new SimpleDateFormat(mFormatTime, mLocale);
        }
        return mTimeFormat;
    }

    public SimpleDateFormat getDateTimeFormat() {
        if (mDateTimeFormat == null) {
            mDateTimeFormat = new SimpleDateFormat(mFormatDateTime, mLocale);
        }
        return mDateTimeFormat;
    }

    public DecimalFormat getDecimalFormat() {
        if (mDecimalFormat == null) {
            mDecimalFormat = new DecimalFormat(mFormatDouble);
        }
        return mDecimalFormat;
    }

    /**
     * Форматировать значение, формат применяется в зависимости от типа
     * значения.Если не один не подходит используется приведение к строке
     * Object.toString()
     *
     * @param arg
     * @return
     */
    public String format(Object arg) {

        if (arg == null)
            return mFormatNull;
        else {
            Class<?> classOf = arg.getClass();

            if (MetadataHelper.isBooleanClass(classOf)) {

                if (mFormatTrue != null && mFormatFalse != null) {
                    return ((Boolean) arg) ? mFormatTrue : mFormatFalse;
                } else {
                    format(mFormatBoolean, arg);
                }

            } else if (MetadataHelper.isStringClass(classOf)) {
                return format(mFormatString, arg);
            } else if (MetadataHelper.isIntegerClass(classOf) ||
                       MetadataHelper.isLongClass(classOf)) {

                if (mFormatIntChoice != null) {
                    return mFormatIntChoice.format(arg);
                } else if (mFormatZero != null) {
                    if ((MetadataHelper.isIntegerClass(classOf) && (Integer) arg == 0) ||
                        (MetadataHelper.isLongClass(classOf) && (Long) arg == 0L)) {
                        return mFormatZero;
                    }
                }
                return format(mFormatInt, arg);

            } else if (MetadataHelper.isDoubleClass(classOf)) {

                double zero = ((Double) arg).doubleValue();
                if (zero == 0d && mFormatZero != null) {
                    return mFormatZero;
                }
                return getDecimalFormat().format(arg);

            } else if ( MetadataHelper.isFloatClass(classOf)) {

                float zero = ((Float) arg).floatValue();
                if (zero == 0f && mFormatZero != null) {
                    return mFormatZero;
                }
                return getDecimalFormat().format(arg);

            } else if (MetadataHelper.isDataClass(classOf)) {

                if (DateHelper.isOnlyTime((Date) arg)) {
                    return getTimeFormat().format(arg);
                } else if (DateHelper.isOnlyDate((Date) arg)) {
                    return getDateFormat().format(arg);
                } else {
                    return getDateTimeFormat().format(arg);
                }

            } else if(arg instanceof IPresentation) {
                return ((IPresentation) arg).getPresentation();
            }

            return arg.toString();
        }

    }

    /**
     * Форматировать по произвольному формату
     *
     * @param format
     * @param args
     * @return
     */
    public String format(String format, Object... args) {
        if (mLocale != null) {
            return String.format(mLocale, format, args);
        }
        return String.format(format, args);
    }

    public static class Builder {

        private FieldFormatter mFieldFormatter;

        public Builder() {
            mFieldFormatter = new FieldFormatter();
        }

        public Builder setLocale(Locale locale) {
            mFieldFormatter.mLocale = locale;
            return this;
        }

        /**
         * Установить представление null,если не указано null значение будет
         * преобразовано к пустой строке
         *
         * @param format
         * @return
         */
        public Builder setNullFormat(String format) {
            mFieldFormatter.mFormatNull = format;
            return this;
        }

        /**
         * Установить формат булево, по умолчанию используется "%b"
         *
         * @param format
         * @return
         */
        public Builder setBooleanFormat(String format) {
            mFieldFormatter.mFormatBoolean = format;
            return this;
        }

        /**
         * Установить представление значений булево, например "Да" и "Нет", если
         * не установлено используется форматирование по умолчанию
         *
         * @param formatTrue
         * @param formatFalse
         * @return
         */
        public Builder setBooleanFormat(String formatTrue, String formatFalse) {
            mFieldFormatter.mFormatTrue = formatTrue;
            mFieldFormatter.mFormatFalse = formatFalse;
            return this;
        }

        /**
         * Установить формат для целых чисел, по умолчанию используется "%d"
         *
         * @param format
         * @return
         */
        public Builder setIntFormat(String format) {
            mFieldFormatter.mFormatInt = format;
            return this;
        }

        /**
         * Установить формат для диапазона целых чисел
         *
         * @param template шаблон, например
         *                 "1#Sun|2#Mon|3#Tue|4#Wed|5#Thur|6#Fri|7#Sat"
         * @return
         */
        public Builder setIntChoiceFormat(String template) {
            mFieldFormatter.mFormatIntChoice = new ChoiceFormat(template);
            return this;
        }

        /**
         * Установить формат для диапазона целых чисел, см. {@link java.text.ChoiceFormat}
         *
         * @param choiceFormat
         * @return
         */
        public Builder setIntChoiceFormat(ChoiceFormat choiceFormat) {
            mFieldFormatter.mFormatIntChoice = choiceFormat;
            return this;
        }

        /**
         * Установить формат для дробных чисел, по умолчанию используется
         * DecimalFormat("###,##0.00")
         *
         * @param format
         * @return
         */
        public Builder setDoubleFormat(String format) {
            mFieldFormatter.mFormatDouble = format;
            return this;
        }

        /**
         * Установить формат даты (без времени), по умолчанию используется
         * SimpleDateFormat("dd.MM.yyyy");
         *
         * @param format
         * @return
         */
        public Builder setDateFormat(String format) {
            mFieldFormatter.mFormatDate = format;
            return this;
        }

        /**
         * Установить формат времени, по умолчанию используется
         * SimpleDateFormat("HH:mm");
         *
         * @param format
         * @return
         */
        public Builder setTimeFormat(String format) {
            mFieldFormatter.mFormatTime = format;
            return this;
        }

        /**
         * Установить формат даты (со временем), по умолчанию используется
         * SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
         *
         * @param format
         * @return
         */
        public Builder setDateTimeFormat(String format) {
            mFieldFormatter.mFormatDateTime = format;
            return this;
        }

        /**
         * Установить формат для строк, по умолчанию используется "%s"
         *
         * @param format
         * @return
         */
        public Builder setStringFormat(String format) {
            mFieldFormatter.mFormatString = format;
            return this;
        }

        /**
         * Установить формат для нулевых значений
         *
         * @param format
         * @return
         */
        public Builder setZeroFormat(String format) {
            mFieldFormatter.mFormatZero = format;
            return this;
        }

        public FieldFormatter create() {
            return mFieldFormatter;
        }
    }
}
