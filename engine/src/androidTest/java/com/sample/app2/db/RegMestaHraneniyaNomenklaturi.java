/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableInfReg;

/**
 * Регистр сведений 'Места хранения номенклатуры'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=RegMestaHraneniyaNomenklaturi.TABLE_NAME, daoClass = RegMestaHraneniyaNomenklaturiDao.class) 
@MetadataObject(type=MetadataObject.TYPE_INFORMATION_REGISTER, name=RegMestaHraneniyaNomenklaturi.META_NAME)
public class RegMestaHraneniyaNomenklaturi extends TableInfReg {
 	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "RegMestaHraneniyaNomenklaturi";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "МестаХраненияНоменклатуры";

	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Номенклатура' в таблице базы данных
	 */
	public static final String FIELD_NAME_NOMENKLATURA = "nomenklatura";

	/**
	 * Номенклатура
	 */	
	@DatabaseField(columnName = FIELD_NAME_NOMENKLATURA, index = true, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Номенклатура",description="Номенклатура")
	public CatalogNomenklatura nomenklatura;
	
	/**
	 * Имя поля 'Склад' в таблице базы данных
	 */
	public static final String FIELD_NAME_SKLAD = "sklad";

	/**
	 * Склад
	 */	
	@DatabaseField(columnName = FIELD_NAME_SKLAD, index = true, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Склад",description="Склад")
	public CatalogSkladi sklad;
	
	/**
	 * Имя поля 'Приоритет' в таблице базы данных
	 */
	public static final String FIELD_NAME_PRIORITET = "prioritet";

	/**
	 * Приоритет
	 */	
	@DatabaseField(columnName = FIELD_NAME_PRIORITET, index = true)
	@MetadataField(type=MetadataFieldType.INT,name="Приоритет",description="Приоритет")
	public int prioritet;
	
	/**
	 * Имя поля 'Место хранения' в таблице базы данных
	 */
	public static final String FIELD_NAME_MESTO_HRANENIYA = "mestoHraneniya";

	/**
	 * Место хранения
	 */	
	@DatabaseField(columnName = FIELD_NAME_MESTO_HRANENIYA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="МестоХранения",description="Место хранения")
	public CatalogMestaHraneniya mestoHraneniya;
	
	@Override
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(nomenklatura);
		sb.append(sklad);
		sb.append(prioritet);

		return sb.toString();
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
