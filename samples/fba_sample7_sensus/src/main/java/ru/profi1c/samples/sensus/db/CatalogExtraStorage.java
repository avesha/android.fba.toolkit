/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.meta.ValueStorage;

/**
 * Справочник 'Хранилище дополнительной информации'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@DatabaseTable(tableName=CatalogExtraStorage.TABLE_NAME, daoClass = CatalogExtraStorageDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogExtraStorage.META_NAME)
public class CatalogExtraStorage extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogExtraStorage";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ХранилищеДополнительнойИнформации";
		
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Объект' в таблице базы данных
	 */
	public static final String FIELD_NAME_OBJECT = "object";

	/**
	 * Объект
	 */	
	@DatabaseField(columnName = FIELD_NAME_OBJECT, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Объект",description="Объект")
	public CatalogSalesPoints object;
	
	/**
	 * Имя поля 'Хранилище' в таблице базы данных
	 */
	public static final String FIELD_NAME_STORAGE = "storage";

	/**
	 * Хранилище
	 */	
	@DatabaseField(columnName = FIELD_NAME_STORAGE, dataType = DataType.SERIALIZABLE)
	@MetadataField(type=MetadataFieldType.SERIALIZABLE,name="Хранилище",description="Хранилище")
	public ValueStorage storage;

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
