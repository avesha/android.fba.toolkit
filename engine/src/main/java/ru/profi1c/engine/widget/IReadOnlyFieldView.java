package ru.profi1c.engine.widget;

public interface IReadOnlyFieldView {

    /**
     * Возвращает текущее значение свойства ‘readOnly’
     */
    boolean isReadOnly();

    /**
     * Изменить значение свойства ‘readOnly’.
     *
     * @param readOnly Если установлено ‘true’ значение не может быть изменено пользователем интерактивно
     */
    void setReadOnly(boolean readOnly);
}
