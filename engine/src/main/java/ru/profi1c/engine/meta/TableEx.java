package ru.profi1c.engine.meta;

/**
 * Базовый класс для внешних таблиц
 */
public abstract class TableEx extends Table {
    private static final long serialVersionUID = 6744053274813717626L;

    @Override
    public String createRecordKey() {
        return null;
    }

}
