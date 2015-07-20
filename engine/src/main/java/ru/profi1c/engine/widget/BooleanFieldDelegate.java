package ru.profi1c.engine.widget;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;

final class BooleanFieldDelegate implements IFieldView, Observer {

    private final CompoundButton mCompoundButton;

    private WeakReference<Object> mReferenceObj;
    private Field mField;
    private boolean mSetAutoText;

    private boolean mBuilded;
    private View.OnClickListener mCustomOnClickListener;
    private CompoundButton.OnCheckedChangeListener mCustomOnCheckedChangeListener;

    BooleanFieldDelegate(CompoundButton button) {
        mCompoundButton = button;
    }

    /**
     * Если истина, текстовое представление будет будет устанавливаться
     * автоматически на основании аннотации MetadataField.description
     * установленной для данного поля класса
     *
     * @param setAuto
     */
    void setAutoText(boolean setAuto) {
        this.mSetAutoText = setAuto;
    }

    void setOnClickListener(View.OnClickListener listener) {
        mCustomOnClickListener = listener;
    }

    void setCustomCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mCustomOnCheckedChangeListener = listener;
    }

    void onDetachedFromWindow() {
        unRegisterObserver();
    }

    /*
    * установить заголовок диалога на основании аннотации поля
    */
    private void setTextAnnotation() {
        String text = (String) mCompoundButton.getText();
        if (TextUtils.isEmpty(text)) {
            String desc = MetadataHelper.getMetadataFieldDescription(mField);
            if (desc != null) {
                mCompoundButton.setText(desc);
            }
        }
    }

    @Override
    public void build(Object obj, Field field, Object value, FieldFormatter fieldFormatter) {
        if (value == null) {
            throw new NullPointerException(
                    String.format("Enable cast null value as boolean for field '%s'",
                            field.getName()));
        }
        if (obj != null) {
            setReferenceObj(obj);
        }
        mField = field;
        mCompoundButton.setChecked((Boolean) value);

        if (mSetAutoText) {
            setTextAnnotation();
        }
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

        boolean filedValue = getObjectFieldValue();
        mCompoundButton.setChecked(filedValue);

        if (mSetAutoText) {
            setTextAnnotation();
        }
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
        return mCompoundButton.isChecked();
    }

    @Override
    public void update(Observable observable, Object data) {
        Object objCurrent = getObject();
        Message msg = (Message) data;

        if (objCurrent != null && msg != null && msg.what == Row.ID_OBSERVER_NOTIFY &&
            msg.arg1 != this.hashCode()) {

            if (mField.getName().equals(msg.obj)) {

                boolean newValue = getObjectFieldValue();
                mCompoundButton.setChecked(newValue);
            }
        }
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
    protected boolean getObjectFieldValue() {
        boolean value = false;
        Object obj = getObject();
        if (obj != null && mField != null) {
            try {
                value = (Boolean) mField.get(obj);
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
    protected void setObjectFieldValue(boolean newValue) {
        Object obj = getObject();
        if (obj != null && mField != null) {

            try {
                mField.set(obj, newValue);

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

    void onChangedObjectFieldValue(boolean isChecked) {
        if (mBuilded) {
            boolean newValue = mCompoundButton.isChecked();
            setObjectFieldValue(newValue);
        }
    }

    /**
     * Обработчик клика по умолчанию
     */
    private View.OnClickListener mDefOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onChangedObjectFieldValue(mCompoundButton.isChecked());
            if (mCustomOnClickListener != null) {
                mCustomOnClickListener.onClick(v);
            }
        }
    };

    View.OnClickListener getDefaultOnClickListener() {
        return mDefOnClickListener;
    }

    private CompoundButton.OnCheckedChangeListener mDefOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onChangedObjectFieldValue(isChecked);
            if (mCustomOnCheckedChangeListener != null) {
                mCustomOnCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener getDefaultOnCheckedChangeListener() {
        return mDefOnCheckedChangeListener;
    }
}
