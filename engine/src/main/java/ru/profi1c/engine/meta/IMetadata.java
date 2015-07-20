package ru.profi1c.engine.meta;

/**
 * Метаданные объекта 1С
 */
public interface IMetadata {

    /**
     * Тип метаданных: справочник, документ, перечисление, регистр сведений
     *
     * @return
     */
    String getMetaType();

    /**
     * Имя объекта метаданных
     *
     * @return
     */
    String getMetaName();
}
