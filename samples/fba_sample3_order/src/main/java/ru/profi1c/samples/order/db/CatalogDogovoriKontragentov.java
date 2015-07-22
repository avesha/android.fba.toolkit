/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Справочник 'Договоры контрагентов'
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = CatalogDogovoriKontragentov.TABLE_NAME, daoClass = CatalogDogovoriKontragentovDao.class)
@MetadataObject(type = MetadataObject.TYPE_CATALOG, name = CatalogDogovoriKontragentov.META_NAME, hierarchical = true)
public class CatalogDogovoriKontragentov extends Catalog {

    /**
     * Имя таблицы в базе данных
     */
    public static final String TABLE_NAME = "CatalogDogovoriKontragentov";

    /**
     * Имя метаданных объекта в 1С (не изменять)
     */
    public static final String META_NAME = "ДоговорыКонтрагентов";

    private static final long serialVersionUID = 1L;

    /**
     * Владелец
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_OWNER, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = Catalog.FIELD_NAME_OWNER, description = "Владелец")
    private CatalogKontragenti owner;

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
     * Имя поля 'Дата' в таблице базы данных
     */
    public static final String FIELD_NAME_DATA = "data";

    /**
     * Дата
     */
    @DatabaseField(columnName = FIELD_NAME_DATA, dataType = DataType.DATE_LONG)
    @MetadataField(type = MetadataFieldType.DATA, name = "Дата", description = "Дата")
    public Date data;

    /**
     * Имя поля 'Номер' в таблице базы данных
     */
    public static final String FIELD_NAME_NOMER = "nomer";

    /**
     * Номер
     */
    @DatabaseField(columnName = FIELD_NAME_NOMER)
    @MetadataField(type = MetadataFieldType.STRING, name = "Номер", description = "Номер")
    public String nomer;

    @Override
    public Catalog getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Catalog catalogRef) {
        owner = (CatalogKontragenti) catalogRef;
    }

    @Override
    public List<Class<? extends TablePart>> getTabularSections() {
        return null;
    }

    @Override
    public String getMetaName() {
        return META_NAME;
    }

    @Override
    public String getPresentation() {
        return getDescription();
    }
}
