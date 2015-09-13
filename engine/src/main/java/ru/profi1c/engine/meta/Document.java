package ru.profi1c.engine.meta;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.util.DateHelper;

/**
 * Базовый класс "Документ"
 */
public abstract class Document extends Ref implements IPresentation, Comparable<Document> {
    private static final long serialVersionUID = 2842751140625776249L;

    public static final String FIELD_NAME_DATE = "date";
    public static final String FIELD_NAME_NUMBER = "number";
    public static final String FIELD_NAME_POSTED = "posted";

    /**
     * Содержит дату и время документа.
     */
    @DatabaseField(columnName = Document.FIELD_NAME_DATE, dataType = DataType.DATE_LONG)
    @MetadataField(type = MetadataFieldType.DATA, name = Document.FIELD_NAME_DATE,
            description = Const.META_DESCRIPTION_DATA)
    protected Date date;

    /**
     * Содержит номер документа
     */
    @DatabaseField(columnName = Document.FIELD_NAME_NUMBER)
    @MetadataField(type = MetadataFieldType.STRING, name = Document.FIELD_NAME_NUMBER,
            description = Const.META_DESCRIPTION_NUMBER)
    protected String number;

    /**
     * Содержит признак пометки на удаление документа
     */
    @DatabaseField(columnName = Document.FIELD_NAME_POSTED)
    @MetadataField(type = MetadataFieldType.BOOL, name = Document.FIELD_NAME_POSTED,
            description = Const.META_DESCRIPTION_POSTED)
    protected boolean posted;

    /**
     * Получить дату документа
     *
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * Установить дату документа
     *
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Получить номер документа
     *
     * @return
     */
    public String getNumber() {
        return number;
    }

    /**
     * Установить номер документа
     *
     * @param number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Получить признак проведения документа
     *
     * @return
     */
    public boolean isPosted() {
        return posted;
    }

    /**
     * Установить признак проведения документа
     *
     * @param posted
     */
    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    @Override
    public String getMetaType() {
        return MetadataObject.TYPE_DOCUMENT;
    }

    /**
     * Получает строковое представление документа
     */
    @Override
    public String getPresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMetaType()).append(".").append(getMetaName());
        sb.append(": ").append(number).append(" ");
        if (date != null) {
            sb.append(DateHelper.format(date));
        }
        return sb.toString();
    }

    /**
     * Сравнение (сортировка) по дате документа и номеру (тип сравниваемых документов
     * должен быть одинаков)
     */
    @Override
    public int compareTo(Document another) {

        if (another == null) {
            return -1;
        }

        if (getClass() != another.getClass()) {
            throw new ClassCastException("It is expected instance of a '" + getClass() + "'!");
        }

        final Date aDt =another.getDate();
        final Date dt = getDate();


        int i = (aDt == null ? (dt == null ? 0 : -1) : (dt == null ? 1 : dt.compareTo(aDt)));
        if(i==0){
            final String aNumber = another.getNumber();
            final String number = getNumber();
            i = (aNumber == null ? (number == null ? 0 : -1) : (number == null ? 1 : number.compareTo(aNumber)));
        }
        return i;
    }

}
