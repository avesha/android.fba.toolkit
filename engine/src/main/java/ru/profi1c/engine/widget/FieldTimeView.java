package ru.profi1c.engine.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Отображение реквизита объекта метаданных (справочник, документ, запись
 * регистр) типа ‘Дата’ с возможностью интерактивного изменения времени
 * пользователем. Рекомендуется к использованию когда в дате содержится только
 * время или когда изменяется только время в дате
 */
public class FieldTimeView extends BaseFieldDateView {

    public FieldTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FieldTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected DatePart getPickerType() {
        return DatePart.TIME;
    }

}
