package ru.profi1c.engine.widget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;

public abstract class BaseFieldDateView extends TextView implements IFieldView, Observer {

    private static final String DATE_FORMAT = Const.DEFAULT_WIDGET_DATE_FORMAT;
    private static final String TIME_FORMAT = Const.DEFAULT_WIDGET_TIME_FORMAT;

    enum DatePart {
        DATE, TIME
    }

    protected abstract DatePart getPickerType();

    private WeakReference<Object> mReferenceObj;
    protected DatePickerDialogHelper mDlgHelper;
    private Field mField;
    private Date mFieldValue;

    private FieldFormatter mFieldFormatter;

    private boolean mBuilded;
    private OnClickListener mCustomOnClickListener;

    public BaseFieldDateView(Context context) {
        super(context);
        init();
    }

    public BaseFieldDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseFieldDateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            super.setOnClickListener(mDefOnClickListener);
            mDlgHelper = new DatePickerDialogHelper(getContext());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterObserver();
        if (mDlgHelper != null) {
            mDlgHelper.closePickerDialog();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mCustomOnClickListener = l;
    }

    @Override
    public void build(Object obj, Field field, Object value, FieldFormatter fieldFormatter) {
        if (obj != null) {
            setReferenceObj(obj);
        }
        mField = field;
        mFieldValue = (Date) value;
        mFieldFormatter = fieldFormatter;

        formatValue();
        mBuilded = true;
    }

    @Override
    public void build(Object obj, String fieldName, FieldFormatter fieldFormatter) {
        if (obj == null) {
            throw new NullPointerException(
                    "Object metadata is not specified, use the method 'build(Object,Field,Object,FieldFormatter)' if necessary 'read only' mode!");
        }

        setReferenceObj(obj);
        Collection<Field> mFields = MetadataHelper.getFields(obj.getClass());
        mField = MetadataHelper.findField(mFields, fieldName, true);
        if (mField == null) {
            throw new FbaRuntimeException(
                    String.format("Not found a field of class '%s' named '%s'", obj.getClass(),
                                  fieldName));
        }

        mFieldValue = getObjectFieldValue();
        mFieldFormatter = fieldFormatter;

        formatValue();
        mBuilded = true;
    }

    @Override
    public Object getObject() {
        if (mReferenceObj != null) {
            return mReferenceObj.get();
        }
        return null;
    }

    private void setReferenceObj(Object obj) {
        mReferenceObj = new WeakReference<Object>(obj);
        registerObserver(obj);
    }

    private void registerObserver(Object observer) {
        if (observer instanceof Row) {
            ((Row) observer).addObserver(this);
        }
    }

    private void unRegisterObserver() {
        Object observer = getObject();
        if (observer != null && observer instanceof Row) {
            ((Row) observer).deleteObserver(this);
        }
    }

    /*
     * Получить значение поля от объекта
     */
    protected Date getObjectFieldValue() {
        Date value = null;
        Object obj = getObject();
        if (obj != null && mField != null) {
            try {
                value = (Date) mField.get(obj);
            } catch (IllegalArgumentException e) {
                Dbg.printStackTrace(e);
            } catch (IllegalAccessException e) {
                Dbg.printStackTrace(e);
            }
        }
        return value;
    }

    /*
     * Установить значение поле объекта
     */
    protected void setObjectFieldValue(Date newValue) {
        Object obj = getObject();
        if (obj != null && mField != null) {
            try {
                mField.set(obj, newValue);
                mFieldValue = newValue;

                if (obj instanceof Row) {
                    Row row = (Row) obj;
                    row.setModified(true);

                    Message msg = Row.makeObserverMessage(this.hashCode(), mField.getName());
                    row.notifyObservers(msg);
                }
            } catch (IllegalArgumentException e) {
                Dbg.printStackTrace(e);
            } catch (IllegalAccessException e) {
                Dbg.printStackTrace(e);
            }
        }
    }

    /**
     * Обработчик клика по умолчанию
     */
    private OnClickListener mDefOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mBuilded) {
                if (getPickerType() == DatePart.DATE) {
                    mDlgHelper.showDatePickerDialog(mFieldValue, mDataChangeListener);
                } else {
                    mDlgHelper.showTimePickerDialog(mFieldValue, mDataChangeListener);
                }
            }

            if (mCustomOnClickListener != null) {
                mCustomOnClickListener.onClick(v);
            }
        }
    };

    protected SimpleDateFormat getFormatter() {
        FieldFormatter ff = getFieldFormatter();
        if (ff != null) {
            return getPickerType() == DatePart.DATE ? ff.getDateFormat() : ff.getTimeFormat();
        }
        return new SimpleDateFormat(getPickerType() == DatePart.DATE ? DATE_FORMAT : TIME_FORMAT,
                                    Locale.getDefault());
    }

    /**
     * Формировать представление значения
     */
    protected void formatValue() {
        String newValue = "";
        Date dt = getValue();
        if (dt != null) {
            newValue = getFormatter().format(dt);
        }
        setText(newValue);
    }

    @Override
    public Date getValue() {
        return mFieldValue;
    }

    protected FieldFormatter getFieldFormatter() {
        return mFieldFormatter;
    }

    /*
     * Обработчик изменения даты
     */
    protected DatePickerDialogHelper.OnDateSetListener mDataChangeListener =
            new DatePickerDialogHelper.OnDateSetListener() {

                @Override
                public void onDateSet(int year, int monthOfYear, int dayOfMonth) {

                    Calendar cal = Calendar.getInstance();
                    Date dt = getValue();
                    if (dt != null) {
                        cal.setTime(dt);
                    }
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, monthOfYear);
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    setObjectFieldValue(cal.getTime());
                    formatValue();
                }

                @Override
                public void onTimeSet(int hourOfDay, int minute) {

                    Calendar cal = Calendar.getInstance();
                    Date dt = getValue();
                    if (dt != null) {
                        cal.setTime(dt);
                    }

                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    cal.set(Calendar.SECOND, 0);

                    setObjectFieldValue(cal.getTime());
                    formatValue();
                }
            };


    @Override
    public void update(Observable observable, Object data) {

        Object objCurrent = getObject();
        Message msg = (Message) data;

        if (objCurrent != null && msg != null && msg.what == Row.ID_OBSERVER_NOTIFY &&
            msg.arg1 != this.hashCode()) {

            if (mField.getName().equals(msg.obj)) {

                mFieldValue = getObjectFieldValue();
                formatValue();
            }

        }

    }

    /*
     * Для обхода ошибки при просомтре при просмотре в редакторе
     подробнее: http://code.google.com/p/android/issues/detail?id=28318
     */
    private static class DatePickerDialogHelper {

        private WeakReference<DatePickerDialog> mReferenceDateDlg;
        private WeakReference<TimePickerDialog> mReferenceTimeDlg;
        private final Context mContext;
        private OnDateSetListener mListener;

        interface OnDateSetListener {
            void onDateSet(int year, int monthOfYear, int dayOfMonth);

            void onTimeSet(int hourOfDay, int minute);
        }

        DatePickerDialogHelper(Context context) {
            mContext = context;
        }

        void closePickerDialog() {
            if (mReferenceDateDlg != null) {
                DatePickerDialog dlg = mReferenceDateDlg.get();
                if (dlg != null && dlg.isShowing()) {
                    dlg.dismiss();
                }
            }

            if (mReferenceTimeDlg != null) {
                TimePickerDialog dlg = mReferenceTimeDlg.get();
                if (dlg != null && dlg.isShowing()) {
                    dlg.dismiss();
                }
            }
        }

        /**
         * Отобразить диалог выбора даты
         */
        void showDatePickerDialog(Date value, OnDateSetListener listener) {

            mListener = listener;

            Calendar cal = Calendar.getInstance();
            if (value != null) {
                cal.setTime(value);
            }

            DatePickerDialog dlg =
                    new DatePickerDialog(mContext, mDataChangeListener, cal.get(Calendar.YEAR),
                                         cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            mReferenceDateDlg = new WeakReference<DatePickerDialog>(dlg);
            dlg.show();

        }

        void showTimePickerDialog(Date date, OnDateSetListener listener) {

            mListener = listener;

            Calendar cal = Calendar.getInstance();
            if (date != null) {
                cal.setTime(date);
            }

            TimePickerDialog dlg = new TimePickerDialog(mContext, mTimeChangeListener,
                                                        cal.get(Calendar.HOUR_OF_DAY),
                                                        cal.get(Calendar.MINUTE), true);
            mReferenceTimeDlg = new WeakReference<TimePickerDialog>(dlg);
            dlg.show();
        }

        /**
         * Обработчик изменения даты
         */
        private DatePickerDialog.OnDateSetListener mDataChangeListener =
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                            int dayOfMonth) {
                        mListener.onDateSet(year, monthOfYear, dayOfMonth);
                    }
                };

        /**
         * Обработчик изменения времени
         */
        private TimePickerDialog.OnTimeSetListener mTimeChangeListener =
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mListener.onTimeSet(hourOfDay, minute);
                    }

                };
    }
}
