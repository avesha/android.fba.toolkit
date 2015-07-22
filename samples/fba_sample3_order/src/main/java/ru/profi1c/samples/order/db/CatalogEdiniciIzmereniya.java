/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Справочник 'Единицы измерения'
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = CatalogEdiniciIzmereniya.TABLE_NAME, daoClass = CatalogEdiniciIzmereniyaDao.class)
@MetadataObject(type = MetadataObject.TYPE_CATALOG, name = CatalogEdiniciIzmereniya.META_NAME)
public class CatalogEdiniciIzmereniya extends Catalog {

    /**
     * Имя таблицы в базе данных
     */
    public static final String TABLE_NAME = "CatalogEdiniciIzmereniya";

    /**
     * Имя метаданных объекта в 1С (не изменять)
     */
    public static final String META_NAME = "ЕдиницыИзмерения";

    private static final long serialVersionUID = 1L;

    /**
     * Владелец
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_OWNER, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = Catalog.FIELD_NAME_OWNER, description = "Владелец")
    private CatalogNomenklatura owner;

    @Override
    public Catalog getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Catalog catalogRef) {
        owner = (CatalogNomenklatura) catalogRef;
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
