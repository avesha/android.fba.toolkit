/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableEx;
import ru.profi1c.engine.meta.ValueStorage;

/**
 * Внешняя таблица 'ТестТаблица2'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=ExTableTestTablica2.TABLE_NAME, daoClass = ExTableTestTablica2Dao.class) 
@MetadataObject(type=MetadataObject.TYPE_EXTERNAL_TABLE, name="ТестТаблица2")
public class ExTableTestTablica2 extends TableEx {
	public static final String TABLE_NAME = "ExTableTestTablica2";
	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Качество' в таблице базы данных
	 */
	public static final String FIELD_NAME_KACHESTVO = "kachestvo";

	/**
	 * Качество
	 */	
	@DatabaseField(columnName = FIELD_NAME_KACHESTVO, index = true, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Качество",description="Качество")
	public CatalogKachestvo kachestvo;
	
	/**
	 * Имя поля 'ДатаНачала' в таблице базы данных
	 */
	public static final String FIELD_NAME_DATA_NACHALA = "dataNachala";

	/**
	 * ДатаНачала
	 */	
	@DatabaseField(columnName = FIELD_NAME_DATA_NACHALA, dataType = DataType.DATE_LONG, index = true)
	@MetadataField(type=MetadataFieldType.DATA,name="ДатаНачала",description="ДатаНачала")
	public Date dataNachala;
	
	/**
	 * Имя поля 'Ограничивать' в таблице базы данных
	 */
	public static final String FIELD_NAME_OGRANICHIVAT = "ogranichivat";

	/**
	 * Ограничивать
	 */	
	@DatabaseField(columnName = FIELD_NAME_OGRANICHIVAT)
	@MetadataField(type=MetadataFieldType.BOOL,name="Ограничивать",description="Ограничивать")
	public boolean ogranichivat;
	
	/**
	 * Имя поля 'ИмяПользователя' в таблице базы данных
	 */
	public static final String FIELD_NAME_IMYA_POLZOVATELYA = "imyaPolzovatelya";

	/**
	 * ИмяПользователя
	 */	
	@DatabaseField(columnName = FIELD_NAME_IMYA_POLZOVATELYA)
	@MetadataField(type=MetadataFieldType.STRING,name="ИмяПользователя",description="ИмяПользователя")
	public String imyaPolzovatelya;
	
	/**
	 * Имя поля 'СтавкаНДС' в таблице базы данных
	 */
	public static final String FIELD_NAME_STAVKA_NDS = "stavkaNDS";

	/**
	 * СтавкаНДС
	 */	
	@DatabaseField(columnName = FIELD_NAME_STAVKA_NDS)
	@MetadataField(type=MetadataFieldType.ENUM,name="СтавкаНДС",description="СтавкаНДС")
	public EnumStavkiNDS stavkaNDS;
	
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
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(kachestvo);
		sb.append(dataNachala);

		return sb.toString();
	}

}
