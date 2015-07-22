/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Справочник 'Контрагенты'
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = CatalogKontragenti.TABLE_NAME, daoClass = CatalogKontragentiDao.class)
@MetadataObject(type = MetadataObject.TYPE_CATALOG, name = CatalogKontragenti.META_NAME, hierarchical = true)
public class CatalogKontragenti extends Catalog {

    /**
     * Имя таблицы в базе данных
     */
    public static final String TABLE_NAME = "CatalogKontragenti";

    /**
     * Имя метаданных объекта в 1С (не изменять)
     */
    public static final String META_NAME = "Контрагенты";

    private static final long serialVersionUID = 1L;


    @Override
    public Catalog getOwner() {
        return null;
    }

    @Override
    public void setOwner(Catalog catalogRef) {

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
