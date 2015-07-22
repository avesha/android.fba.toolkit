/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Документ 'Заказ покупателя'
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = DocumentZakazPokupatelya.TABLE_NAME, daoClass = DocumentZakazPokupatelyaDao.class)
@MetadataObject(type = MetadataObject.TYPE_DOCUMENT, name = DocumentZakazPokupatelya.META_NAME)
public class DocumentZakazPokupatelya extends Document {

    /**
     * Имя таблицы в базе данных
     */
    public static final String TABLE_NAME = "DocumentZakazPokupatelya";

    /**
     * Имя метаданных объекта в 1С (не изменять)
     */
    public static final String META_NAME = "ЗаказПокупателя";

    private static final long serialVersionUID = 1L;


    /**
     * Имя поля 'Организация' в таблице базы данных
     */
    public static final String FIELD_NAME_ORGANIZACIYA = "organizaciya";

    /**
     * Организация
     */
    @DatabaseField(columnName = FIELD_NAME_ORGANIZACIYA, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "Организация", description = "Организация")
    public CatalogOrganizacii organizaciya;

    /**
     * Имя поля 'Контрагент' в таблице базы данных
     */
    public static final String FIELD_NAME_KONTRAGENT = "kontragent";

    /**
     * Контрагент
     */
    @DatabaseField(columnName = FIELD_NAME_KONTRAGENT, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "Контрагент", description = "Контрагент")
    public CatalogKontragenti kontragent;

    /**
     * Имя поля 'Договор контрагента' в таблице базы данных
     */
    public static final String FIELD_NAME_DOGOVOR_KONTRAGENTA = "dogovorKontragenta";

    /**
     * Договор контрагента
     */
    @DatabaseField(columnName = FIELD_NAME_DOGOVOR_KONTRAGENTA, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ДоговорКонтрагента", description = "Договор контрагента")
    public CatalogDogovoriKontragentov dogovorKontragenta;

    /**
     * Имя поля 'Склад' в таблице базы данных
     */
    public static final String FIELD_NAME_SKLAD = "sklad";

    /**
     * Склад
     */
    @DatabaseField(columnName = FIELD_NAME_SKLAD, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "Склад", description = "Склад")
    public CatalogSkladi sklad;

    /**
     * Имя поля 'Комментарий' в таблице базы данных
     */
    public static final String FIELD_NAME_KOMMENTARII = "kommentarii";

    /**
     * Комментарий
     */
    @DatabaseField(columnName = FIELD_NAME_KOMMENTARII)
    @MetadataField(type = MetadataFieldType.STRING, name = "Комментарий", description = "Комментарий")
    public String kommentarii;

    /**
     * Имя поля 'Ответственный' в таблице базы данных
     */
    public static final String FIELD_NAME_OTVETSTVENNII = "otvetstvennii";

    /**
     * Ответственный
     */
    @DatabaseField(columnName = FIELD_NAME_OTVETSTVENNII, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "Ответственный", description = "Ответственный")
    public CatalogPolzovateli otvetstvennii;

    /**
     * Имя поля 'Тип цен' в таблице базы данных
     */
    public static final String FIELD_NAME_TIP_CEN = "tipCen";

    /**
     * Тип цен
     */
    @DatabaseField(columnName = FIELD_NAME_TIP_CEN, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ТипЦен", description = "Тип цен")
    public CatalogTipiCenNomenklaturi tipCen;

    /**
     * Имя поля 'Валюта документа' в таблице базы данных
     */
    public static final String FIELD_NAME_VALYUTA_DOKUMENTA = "valyutaDokumenta";

    /**
     * Валюта документа
     */
    @DatabaseField(columnName = FIELD_NAME_VALYUTA_DOKUMENTA, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ВалютаДокумента", description = "Валюта документа")
    public CatalogValyuti valyutaDokumenta;

    /**
     * Имя поля 'Сумма' в таблице базы данных
     */
    public static final String FIELD_NAME_SUMMA = "summa";

    /**
     * Сумма
     */
    @DatabaseField(columnName = FIELD_NAME_SUMMA)
    @MetadataField(type = MetadataFieldType.DOUBLE, name = "Сумма", description = "Сумма")
    public double summa;

    /**
     * Имя поля 'Дата отгрузки' в таблице базы данных
     */
    public static final String FIELD_NAME_DATA_OTGRUZKI = "dataOtgruzki";

    /**
     * Дата отгрузки
     */
    @DatabaseField(columnName = FIELD_NAME_DATA_OTGRUZKI, dataType = DataType.DATE_LONG)
    @MetadataField(type = MetadataFieldType.DATA, name = "ДатаОтгрузки", description = "Дата отгрузки")
    public Date dataOtgruzki;


    /**
     * Табличная часть 'Товары'
     */
    @ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
    @MetadataField(type = MetadataFieldType.TABLE_PART, name = "Товары", description = "Товары")
    public ForeignCollection<DocumentZakazPokupatelyaTPTovari> tovari;

    @Override
    public List<Class<? extends TablePart>> getTabularSections() {
        List<Class<? extends TablePart>> lstTabSections =
                new ArrayList<Class<? extends TablePart>>();
        lstTabSections.add(DocumentZakazPokupatelyaTPTovari.class);
        return lstTabSections;
    }

    @Override
    public String getMetaName() {
        return META_NAME;
    }

}
