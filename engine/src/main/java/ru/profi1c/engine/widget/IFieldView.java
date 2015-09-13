package ru.profi1c.engine.widget;

import java.lang.reflect.Field;

/**
 * Интерфейс для пользовательских виджетов, которые допускают автоматическое
 * интерактивное изменение значения связанного поля класса объекта метаданных
 */
public interface IFieldView  extends IBaseFieldView {

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj            Изменяемый объект метаданных (справочник, документ, запись
     *                       регистра), если null то режим 'только отображение'
     * @param field          Поле класса связанного реквизита
     * @param value          Начальное значение
     * @param fieldFormatter форматер для приведения значения к строке, если требуется
     */
    void build(Object obj, Field field, Object value, FieldFormatter fieldFormatter);

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj            Изменяемый объект метаданных (справочник, документ, запись
     *                       регистра), null недопустим
     * @param fieldName      Имя поля класса связанного реквизита
     * @param fieldFormatter форматер для приведения значения к строке, если требуется
     */
    void build(Object obj, String fieldName, FieldFormatter fieldFormatter);

}
