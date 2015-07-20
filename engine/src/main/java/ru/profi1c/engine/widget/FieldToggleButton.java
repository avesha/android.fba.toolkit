package ru.profi1c.engine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;

/**
 * Отображение реквизита объекта типа 'булево' с возможностью редактирования как
 * кнопка - переключатель
 */
public class FieldToggleButton extends ToggleButton implements IFieldView, IAutoTextFieldView, Observer {

    private BooleanFieldDelegate mDelegate;

    public FieldToggleButton(Context context) {
        super(context);
        init();
    }

    public FieldToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            mDelegate = new BooleanFieldDelegate(this);
            super.setOnCheckedChangeListener(mDelegate.getDefaultOnCheckedChangeListener());
        }
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mDelegate.setCustomCheckedChangeListener(listener);
    }

    /**
     * Если истина, текстовое представление будет будет устанавливаться
     * автоматически на основании аннотации MetadataField.description
     * установленной для данного поля класса
     *
     * @param setAuto
     */
    @Override
    public void setAutoText(boolean setAuto) {
        mDelegate.setAutoText(setAuto);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDelegate.onDetachedFromWindow();
    }

    @Override
    public void update(Observable observable, Object data) {
        mDelegate.update(observable, data);
    }

    @Override
    public void build(Object obj, Field field, Object value, FieldFormatter fieldFormatter) {
        mDelegate.build(obj, field, value, fieldFormatter);
    }

    @Override
    public void build(Object obj, String fieldName, FieldFormatter fieldFormatter) {
        mDelegate.build(obj, fieldName, fieldFormatter);
    }

    @Override
    public Object getObject() {
        return mDelegate.getObject();
    }

    @Override
    public Object getValue() {
        return mDelegate.getValue();
    }

}
