package ru.profi1c.engine.widget;

import android.content.Context;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;
import ru.profi1c.engine.util.NumberFormatHelper;

/**
 * Отображение реквизита объекта метаданных (справочник, документ, запись
 * регистра) типа: 'String', 'Double', 'Integer' с возможностью интерактивного
 * изменения пользователем.
 * <p>
 * Форматирование значения производится на основании установленного для этого
 * EditView свойства InputType в xml-макете или программно.
 * </p>
 * <p/>
 * Например, <br>
 * {@code android:inputType="numberSigned"} для целых чисел, <br>
 * {@code android:inputType="numberDecimal"} для десятичных чисел.
 * <p>
 * Если InputType не задан, считается что редактируется строка
 * </p>
 */
public class FieldEditText extends EditText implements IFieldView, Observer {

    private WeakReference<Object> mReferenceObj;
    private Field mField;
    private FieldFormatter mFieldFormatter;
    private Object mFieldValue;
    private boolean mInFormat, mBuilded;
    private TextChangedListener mTextChangedListener;
    private boolean mSetAutoHint;

    public FieldEditText(Context context) {
        super(context);
        init();
    }

    public FieldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            //Обработчик изменения текста будет вызван первым из всех установленных
            mTextChangedListener = new TextChangedListener();
            addTextChangedListener(mTextChangedListener);
        }
    }

    /**
     * Если истина, подсказка будет устанавливаться автоматически на основании
     * аннотации MetadataField.description установленной для данного поля класса
     *
     * @param setAuto
     */
    public void setAutoHint(boolean setAuto) {
        this.mSetAutoHint = setAuto;
    }

    /*
     * установить заголовок диалога на основании аннотации поля
     */
    private void setHintAnnotation() {
        String text = (String) getHint();
        if (TextUtils.isEmpty(text)) {
            String desc = MetadataHelper.getMetadataFieldDescription(mField);
            if (desc != null) {
                setHint(desc);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterObserver();
        removeTextChangedListener(mTextChangedListener);
    }

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

        if (mSetAutoHint) {
            setHintAnnotation();
        }
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

        if (mSetAutoHint) {
            setHintAnnotation();
        }
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

        mInFormat = true;
        String frmValue = "";

        if (mFieldValue != null) {

            if (mFieldFormatter != null) {

                frmValue = mFieldFormatter.format(mFieldValue);

            } else {

                int type = getInputType();
                if ((type & InputType.TYPE_NUMBER_FLAG_SIGNED) > 0) {

                    int val = NumberFormatHelper.parseInt(mFieldValue.toString());
                    frmValue = String.valueOf(val);

                } else if ((type & InputType.TYPE_NUMBER_FLAG_DECIMAL) > 0) {

                    double val = NumberFormatHelper.parseDouble(mFieldValue.toString());
                    frmValue = NumberFormatHelper.formatMoneyEdit(val);

                } else {
                    frmValue = mFieldValue.toString();
                }
            }
        }

        setText(frmValue);
        mInFormat = false;
    }

    protected void castAsFieldValue(String newValue) {
        Object val;
        Class<?> classOfF = mField.getType();
        if (MetadataHelper.isStringClass(classOfF)) {
            val = newValue;
        } else if (MetadataHelper.isIntegerClass(classOfF) ||
                   MetadataHelper.isLongClass(classOfF)) {
            val = NumberFormatHelper.parseInt(newValue);
        } else if (MetadataHelper.isDoubleClass(classOfF) ||
                   MetadataHelper.isFloatClass(classOfF)) {

            if (mFieldFormatter == null) {
                val = NumberFormatHelper.parseMoneyEdit(newValue);
            } else {
                val = 0;
                try {
                    val = mFieldFormatter.getDecimalFormat().parse(newValue);
                } catch (ParseException e) {
                    try {
                        val = Double.parseDouble(newValue);
                    } catch (NumberFormatException ne) {
                    }
                }
            }

        } else {
            throw new FbaRuntimeException(String.format(
                    "Type of field '%s' not support for view on 'FieldEditText' widget!",
                    mField.getName()));
        }
        setObjectFieldValue(val);
    }

    /*
     * Обработчик изменения текста
     */
    private class TextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mBuilded && !mInFormat) {
                castAsFieldValue(s.toString());
            }
        }

    }
}
