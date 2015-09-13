package ru.profi1c.engine.widget;

/**
 * Базовый интерфейс для пользовательских виджетов, которые содержат значение связанного поля класса объекта метаданных
 */
public interface IBaseFieldView {

    /**
     * Возвращает объект метаданных поле которого изменяется данным виджетом или
     * null режим ‘только отображение’ и изменяемый объект не передавался
     * (первый параметр методов
     * {@link #setValue(Object, Object, FieldFormatter)} или
     * {@link #setValue(Object, String, FieldFormatter)})
     */
    Object getObject();

    /**
     * Возвращает текущее (редактируемое) значение поля класса
     */
    Object getValue();
}
