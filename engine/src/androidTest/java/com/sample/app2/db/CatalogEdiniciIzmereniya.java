/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Справочник 'Единицы измерения'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=CatalogEdiniciIzmereniya.TABLE_NAME, daoClass = CatalogEdiniciIzmereniyaDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogEdiniciIzmereniya.META_NAME)
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
	@MetadataField(type=MetadataFieldType.REF,name=Catalog.FIELD_NAME_OWNER,description="Владелец")
	private CatalogNomenklatura owner;
	
	/**
	 * Имя поля 'Единица по классификатору' в таблице базы данных
	 */
	public static final String FIELD_NAME_EDINICA_PO_KLASSIFIKATORU = "edinicaPoKlassifikatoru";

	/**
	 * Единица по классификатору
	 */	
	@DatabaseField(columnName = FIELD_NAME_EDINICA_PO_KLASSIFIKATORU, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЕдиницаПоКлассификатору",description="Единица по классификатору")
	public CatalogKlassifikatorEdinicIzmereniya edinicaPoKlassifikatoru;
	
	/**
	 * Имя поля 'Вес' в таблице базы данных
	 */
	public static final String FIELD_NAME_VES = "ves";

	/**
	 * Вес
	 */	
	@DatabaseField(columnName = FIELD_NAME_VES)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Вес",description="Вес")
	public double ves;
	
	/**
	 * Имя поля 'Объем' в таблице базы данных
	 */
	public static final String FIELD_NAME_OBEM = "obem";

	/**
	 * Объем
	 */	
	@DatabaseField(columnName = FIELD_NAME_OBEM)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Объем",description="Объем")
	public double obem;
	
	/**
	 * Имя поля 'Коэффициент' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOEFFICIENT = "koefficient";

	/**
	 * Коэффициент
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOEFFICIENT)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Коэффициент",description="Коэффициент")
	public double koefficient;

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
