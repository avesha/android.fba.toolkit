package ru.profi1c.engine.meta;

import com.j256.ormlite.field.DatabaseField;

import ru.profi1c.engine.Const;

/**
 * Базовый класс для подчиненных объектных таблиц (табличные части справочников
 * и документов)
 */
public abstract class TablePart extends Table implements IMetadata {
    private static final long serialVersionUID = 6394976549670838025L;

    public static final String FIELD_NAME_REF_ID = "RefId";
    public static final String FIELD_NAME_LINE_NUMBER = "lineNumber";

    /**
     * Номер строки
     */
    @DatabaseField(columnName = TablePart.FIELD_NAME_LINE_NUMBER)
    @MetadataField(type = MetadataFieldType.INT, name = TablePart.FIELD_NAME_LINE_NUMBER,
            description = Const.META_DESCRIPTION_LINE_NUMBER)
    protected int lineNumber = Const.NOT_SPECIFIED;

    /**
     * Возвращается ссылку на владельца записи табличной части (документ или
     * справочник). Поле владельца должно быть определено в классе наследнике (с
     * именем поля таблицы 'FIELD_NAME_REF_ID').
     *
     * @return
     */
    public abstract Ref getOwner();

    /**
     * Установить владельца табличной части
     *
     * @param owner ссылка на справочник или документ
     */
    public abstract void setOwner(Ref owner);

    /**
     * Возвращает номер строки
     *
     * @return
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Установить номер строки
     *
     * @param lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String createRecordKey() {
        Ref owner = getOwner();
        if (Ref.isEmpty(owner)) {
            throw new IllegalStateException("Do not set the owner of the slave records table!");
        }
        if (lineNumber == Const.NOT_SPECIFIED) {
            throw new IllegalStateException("Do not set the row number of the table!");
        }

        return String.format("%s-%s", lineNumber, owner.getRef().toString());
    }

    @Override
    public String getMetaType() {
        return MetadataObject.TYPE_TABLE_PART;
    }
}
