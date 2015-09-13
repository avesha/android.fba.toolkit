/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import java.math.BigInteger;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.meta.ValueStorage;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Справочник 'Номенклатура'
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 *
 */
@DatabaseTable(tableName=CatalogNomenklatura.TABLE_NAME, daoClass = CatalogNomenklaturaDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogNomenklatura.META_NAME)
public class CatalogNomenklatura extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogNomenklatura";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Номенклатура";
		
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
