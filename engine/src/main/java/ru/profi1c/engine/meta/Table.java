package ru.profi1c.engine.meta;

import com.j256.ormlite.field.DatabaseField;

import ru.profi1c.engine.Const;

/**
 * Базовый класс для всех необъектных таблиц (константы,регистры сведений,
 * табличные части документов и справочников)
 */
public abstract class Table extends Row {
    private static final long serialVersionUID = 385512241268489384L;

    /*
     * Если используются отражение таблицы на Cursor, колонка идентификатора
     * должна называться “_id”
     */
    public static final String FIELD_NAME_ID = "_id";
    public static final String FILED_NAME_RECORD_KEY = "recordKey";

    /**
     * Содержит уникальный ключ записи (составной по измерениям)
     */
    @DatabaseField(columnName = Table.FIELD_NAME_ID, id = true)
    @MetadataField(type = MetadataFieldType.STRING, name = "recordKey", description = Const.META_DESCRIPTION_RECORD_KEY)
    protected String recordKey;
    // имя "recordKey" не менять, используется в парсере как исключаемое

    /**
     * Получить уникальный ключ записи
     *
     * @return
     */
    public String getRecordKey() {
        return recordKey;
    }

    /**
     * Установить уникальный ключ записи
     *
     * @param key
     */
    public void setRecordKey(String key) {
        recordKey = key;
    }

    /**
     * Вызывается когда следует создать ключ записи (по уникальным полям)
     *
     * @return
     */
    public abstract String createRecordKey();

}
