/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableEx;

/**
 * Внешняя таблица 'ГеоДанные'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=ExTableGeoDannie.TABLE_NAME, daoClass = ExTableGeoDannieDao.class) 
@MetadataObject(type=MetadataObject.TYPE_EXTERNAL_TABLE, name="ГеоДанные")
public class ExTableGeoDannie extends TableEx {
	public static final String TABLE_NAME = "ExTableGeoDannie";
	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'ИмяПользователя' в таблице базы данных
	 */
	public static final String FIELD_NAME_USER_NAME = "userName";

	/**
	 * ИмяПользователя
	 */	
	@DatabaseField(columnName = FIELD_NAME_USER_NAME, index = true)
	@MetadataField(type=MetadataFieldType.STRING,name="ИмяПользователя",description="ИмяПользователя")
	public String userName;
	
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
	
	@Override
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(userName);

		return sb.toString();
	}

}
