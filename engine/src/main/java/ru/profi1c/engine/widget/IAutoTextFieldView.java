package ru.profi1c.engine.widget;

interface IAutoTextFieldView {

    /**
     * Если истина, текстовое представление будет будет устанавливаться
     * автоматически на основании аннотации MetadataField.description
     * установленной для данного поля класса
     *
     * @param setAuto
     */
    void setAutoText(boolean setAuto);
}
