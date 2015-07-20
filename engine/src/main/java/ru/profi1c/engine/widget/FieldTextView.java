package ru.profi1c.engine.widget;

import android.content.Context;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;
import ru.profi1c.engine.util.InputDialogHelper;

/**
 * Отображение реквизита объекта метаданных (справочник, документ, запись
 * регистра) типа: 'String', 'Double', 'Integer' с возможностью интерактивного
 * изменения пользователем в диалоге
 * <p>
 * Форматирование значения производится на основании типа данных реквизита
 * </p>
 */
public class FieldTextView extends TextView implements IFieldView, Observer {

    private WeakReference<Object> mReferenceObj;
    private WeakReference<AlertDialog> mReferenceDlg;
    private Field mField;
    private FieldFormatter mFieldFormatter;
    private Object mFieldValue;
    private String mDialogTitle;

    private boolean mBuilded;
    private OnClickListener mCustomOnClickListener;

    public FieldTextView(Context context) {
        super(context);
        init();
    }

    public FieldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            super.setOnClickListener(mDefOnClickListener);
        }
    }

    /**
     * Обработчик клика по умолчанию
     */
    private OnClickListener mDefOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mBuilded) {
                onShowInputDialog();
            }
            if (mCustomOnClickListener != null) {
                mCustomOnClickListener.onClick(v);
            }
        }
    };

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mCustomOnClickListener = listener;
    }

    /**
     * Установить текст заголовка диалога редактирования значения
     *
     * @param title
     */
    public void setDialogTitle(String title) {
        this.mDialogTitle = title;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterObserver();
        if (mReferenceDlg != null) {
            AlertDialog dlg = mReferenceDlg.get();
            if (dlg != null && dlg.isShowing()) {
                dlg.dismiss();
            }
        }
    }

    protected void onShowInputDialog() {

        String dlgHint = (String) getHint();
        if (TextUtils.isEmpty(dlgHint)) {
            dlgHint = MetadataHelper.getMetadataFieldDescription(mField);
        }

        Class<?> classOfF = mField.getType();
        AlertDialog dlg;

        if (MetadataHelper.isIntegerClass(classOfF) ) {

            dlg = InputDialogHelper.inputInt(getContext(), mDialogTitle, dlgHint,
                    ((mFieldValue != null) ? (Integer) mFieldValue : 0),
                    mOnInputListener);

        } else if(MetadataHelper.isLongClass(classOfF)) {

            dlg = InputDialogHelper.inputInt(getContext(), mDialogTitle, dlgHint,
                    ((mFieldValue != null) ? ((Long) mFieldValue).intValue() : 0),
                    mOnInputListener);

        } else if (MetadataHelper.isDoubleClass(classOfF)) {

            dlg = InputDialogHelper.inputDouble(getContext(), mDialogTitle, dlgHint,
                    ((mFieldValue != null) ? ((Double) mFieldValue).doubleValue() : 0),
                    mOnInputListener);

        } else if(MetadataHelper.isFloatClass(classOfF)) {

            dlg = InputDialogHelper.inputDouble(getContext(), mDialogTitle, dlgHint,
                    ((mFieldValue != null) ? ((Float) mFieldValue).doubleValue() : 0),
                    mOnInputListener);

        } else {

            dlg = InputDialogHelper.inputString(getContext(), mDialogTitle, dlgHint,
                    ((mFieldValue != null) ? mFieldValue.toString() : ""), mOnInputListener);
        }

        if (dlg != null) {
            mReferenceDlg = new WeakReference<AlertDialog>(dlg);
        }
    }

    private InputDialogHelper.OnCompleteListener mOnInputListener = new InputDialogHelper.OnCompleteListener() {

        @Override
        public void onInputValue(Object value) {
            if (value != null) {
                Class<?> classOfF = mField.getType();
                if (MetadataHelper.isLongClass(classOfF)) {
                    value = Long.parseLong(String.valueOf(value));
                } else if (MetadataHelper.isFloatClass(classOfF)) {
                    value = Float.parseFloat(String.valueOf(value));
                }
            }
            setObjectFieldValue(value);
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

    @Override
    public void build(Object obj, Field field, Object value, FieldFormatter fieldFormatter) {
        if (obj != null) {
            setReferenceObj(obj);
        }
        mField = field;
        mFieldValue = value;
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

    @Override
    public Object getValue() {
        return mFieldValue;
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
    protected Object getObjectFieldValue() {
        Object value = null;
        Object obj = getObject();
        if (obj != null && mField != null) {
            try {
                value = mField.get(obj);
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
    protected void setObjectFieldValue(Object newValue) {

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
     * Формировать представление значения
     */
    protected void formatValue() {

        String frmValue = "";

        if (mFieldValue != null) {
            if (mFieldFormatter != null) {
                frmValue = mFieldFormatter.format(mFieldValue);
                setText(frmValue);
            } else {
                frmValue = mFieldValue.toString();
            }
        }
        setText(frmValue);
    }

}
