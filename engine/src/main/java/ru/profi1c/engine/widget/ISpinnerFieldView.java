package ru.profi1c.engine.widget;

import java.lang.reflect.Field;

import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.IPresentation;

public interface ISpinnerFieldView extends IBaseFieldView {

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj       Изменяемый объект метаданных (справочник, документ, запись
     *                  регистра)
     * @param fieldName Имя поля класса связанного реквизита
     * @param dbHelper  Помощник для работы с базой данных
     */
    void build(Object obj, String fieldName, DBOpenHelper dbHelper);

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj      Изменяемый объект метаданных (справочник, документ, запись
     *                 регистра)
     * @param field    Поле класса связанного реквизита
     * @param value    Начальное значение
     * @param dbHelper Помощник для работы с базой данных
     */
    public void build(Object obj, Field field, IPresentation value, DBOpenHelper dbHelper);
}
