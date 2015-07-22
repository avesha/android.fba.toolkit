/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.audit.salespoint.db;

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
 * Справочник 'Торговые точки'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@DatabaseTable(tableName=CatalogSalesPoint.TABLE_NAME, daoClass = CatalogSalesPointDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogSalesPoint.META_NAME)
public class CatalogSalesPoint extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogSalesPoint";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ТорговыеТочки";
		
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Широта' в таблице базы данных
	 */
	public static final String FIELD_NAME_LAT = "lat";

	/**
	 * Широта
	 */	
	@DatabaseField(columnName = FIELD_NAME_LAT)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Широта",description="Широта")
	public double lat;
	
	/**
	 * Имя поля 'Долгота' в таблице базы данных
	 */
	public static final String FIELD_NAME_LNG = "lng";

	/**
	 * Долгота
	 */	
	@DatabaseField(columnName = FIELD_NAME_LNG)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Долгота",description="Долгота")
	public double lng;
	
	/**
	 * Имя поля 'Адрес' в таблице базы данных
	 */
	public static final String FIELD_NAME_ADRESS = "adress";

	/**
	 * Адрес
	 */	
	@DatabaseField(columnName = FIELD_NAME_ADRESS)
	@MetadataField(type=MetadataFieldType.STRING,name="Адрес",description="Адрес")
	public String adress;
	
	/**
	 * Имя поля 'Сайт' в таблице базы данных
	 */
	public static final String FIELD_NAME_SITE = "site";

	/**
	 * Сайт
	 */	
	@DatabaseField(columnName = FIELD_NAME_SITE)
	@MetadataField(type=MetadataFieldType.STRING,name="Сайт",description="Сайт")
	public String site;
	
	/**
	 * Имя поля 'Телефон' в таблице базы данных
	 */
	public static final String FIELD_NAME_PHONE = "phone";

	/**
	 * Телефон
	 */	
	@DatabaseField(columnName = FIELD_NAME_PHONE)
	@MetadataField(type=MetadataFieldType.STRING,name="Телефон",description="Телефон")
	public String phone;
	
	/**
	 * Имя поля 'Фото' в таблице базы данных
	 */
	public static final String FIELD_NAME_FOTO = "foto";

	/**
	 * Фото
	 */	
	@DatabaseField(columnName = FIELD_NAME_FOTO, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Фото",description="Фото")
	public CatalogAddInfoStorage foto;

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
