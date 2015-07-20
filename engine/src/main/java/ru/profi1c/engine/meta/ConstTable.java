package ru.profi1c.engine.meta;

/**
 * Базовый класс для таблицы констант. Таблица констант может содержать только
 * одну запись
 */
public abstract class ConstTable extends Table implements IMetadata {
    private static final long serialVersionUID = 3533299266847472482L;

    public static String CONST_RECORD_KEY = Ref.EMPTY_REF;

    @Override
    public String createRecordKey() {
        return CONST_RECORD_KEY;
    }

    @Override
    public String getMetaType() {
        return MetadataObject.TYPE_CONSTANT;
    }

    /**
     * У таблицы констант нет собственного имени, всегда возвращает null
     */
    @Override
    public String getMetaName() {
        return null;
    }
}
