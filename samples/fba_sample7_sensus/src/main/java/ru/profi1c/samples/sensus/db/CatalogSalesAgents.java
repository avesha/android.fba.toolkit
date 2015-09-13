/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

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
 * Справочник 'Торговые представители'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@DatabaseTable(tableName=CatalogSalesAgents.TABLE_NAME, daoClass = CatalogSalesAgentsDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogSalesAgents.META_NAME)
public class CatalogSalesAgents extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogSalesAgents";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ТорговыеПредставители";
		
	private static final long serialVersionUID = 1L;

	
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
	 * Имя поля 'Почта' в таблице базы данных
	 */
	public static final String FIELD_NAME_EMAIL = "email";

	/**
	 * Почта
	 */	
	@DatabaseField(columnName = FIELD_NAME_EMAIL)
	@MetadataField(type=MetadataFieldType.STRING,name="Почта",description="Почта")
	public String email;
	
	/**
	 * Имя поля 'Фото' в таблице базы данных
	 */
	public static final String FIELD_NAME_FOTO = "foto";

	/**
	 * Фото
	 */	
	@DatabaseField(columnName = FIELD_NAME_FOTO, dataType = DataType.SERIALIZABLE)
	@MetadataField(type=MetadataFieldType.SERIALIZABLE,name="Фото",description="Фото")
	public ValueStorage foto;

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
