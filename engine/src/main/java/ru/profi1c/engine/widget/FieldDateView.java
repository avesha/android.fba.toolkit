package ru.profi1c.engine.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Отображение реквизита объекта метаданных (справочник, документ, запись
 * регистр) типа ‘Дата’ с возможностью интерактивного изменения даты
 * пользователем
 */
public class FieldDateView extends BaseFieldDateView {

    public FieldDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FieldDateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected DatePart getPickerType() {
        return DatePart.DATE;
    }

}
