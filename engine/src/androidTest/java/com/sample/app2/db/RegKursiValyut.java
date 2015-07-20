/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableInfRegPeriodic;

/**
 * Периодический регистр сведений 'Курсы валют'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=RegKursiValyut.TABLE_NAME, daoClass = RegKursiValyutDao.class) 
@MetadataObject(type=MetadataObject.TYPE_INFORMATION_REGISTER, name=RegKursiValyut.META_NAME)
public class RegKursiValyut extends TableInfRegPeriodic {
 	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "RegKursiValyut";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "КурсыВалют";

	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Валюта' в таблице базы данных
	 */
	public static final String FIELD_NAME_VALYUTA = "valyuta";

	/**
	 * Валюта
	 */	
	@DatabaseField(columnName = FIELD_NAME_VALYUTA, index = true, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Валюта",description="Валюта")
	public CatalogValyuti valyuta;
	
	/**
	 * Имя поля 'Курс' в таблице базы данных
	 */
	public static final String FIELD_NAME_KURS = "kurs";

	/**
	 * Курс
	 */	
	@DatabaseField(columnName = FIELD_NAME_KURS)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Курс",description="Курс")
	public double kurs;
	
	/**
	 * Имя поля 'Кратность' в таблице базы данных
	 */
	public static final String FIELD_NAME_KRATNOST = "kratnost";

	/**
	 * Кратность
	 */	
	@DatabaseField(columnName = FIELD_NAME_KRATNOST)
	@MetadataField(type=MetadataFieldType.LONG,name="Кратность",description="Кратность")
	public long kratnost;
	
	@Override
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(valyuta);

		return sb.toString();
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
