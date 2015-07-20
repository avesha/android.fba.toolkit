/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.TablePart;

/**
 * Табличная часть 'Возвратная тара' документа 'Внутренний заказ'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVnutrenniiZakazTPVozvratnayaTara.TABLE_NAME, daoClass = DocumentVnutrenniiZakazTPVozvratnayaTaraDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentVnutrenniiZakazTPVozvratnayaTara.META_NAME)
public class DocumentVnutrenniiZakazTPVozvratnayaTara extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVnutrenniiZakazTPVozvratnayaTara";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ВозвратнаяТара";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentVnutrenniiZakaz.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentVnutrenniiZakaz owner;

	
	/**
	 * Имя поля 'Количество' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOLICHESTVO = "kolichestvo";

	/**
	 * Количество
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOLICHESTVO)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Количество",description="Количество")
	public double kolichestvo;
	
	/**
	 * Имя поля 'Номенклатура' в таблице базы данных
	 */
	public static final String FIELD_NAME_NOMENKLATURA = "nomenklatura";

	/**
	 * Номенклатура
	 */	
	@DatabaseField(columnName = FIELD_NAME_NOMENKLATURA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Номенклатура",description="Номенклатура")
	public CatalogNomenklatura nomenklatura;

	@Override
	public Ref getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Ref ref) {
		owner = (DocumentVnutrenniiZakaz) ref;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
