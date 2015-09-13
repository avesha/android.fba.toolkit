package ru.profi1c.engine.widget;

public interface IFieldPresentationSpinner extends ISpinnerFieldView  {

    /**
     * Установить обработчик образного вызова на создание адаптера, может
     * использоваться для кэширования однотипных адаптеров
     *
     * @param cacheAdapterRequest
     */
    void setCacheAdapter(SpinnerAdapterRequest cacheAdapterRequest);

    /**
     * Если текущее редактируемое значение не задано по умолчанию
     * устанавливается первое значение из списка. Чтобы отключить авто. выбор,
     * установите истина, будет отображено предложение выбрать элемент из списка
     *
     * @param selectRequest
     */
    void setSelectRequest(boolean selectRequest);

    /**
     * Если истина, заголовок диалога выбора значения будет устанавливаться
     * автоматически на основании аннотации MetadataField.description
     * установленной для данного поля класса
     *
     * @param setAuto
     */
    void setAutoPrompt(boolean setAuto);


}
