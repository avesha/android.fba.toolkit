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
 * Табличная часть 'Серийные номера состав набора' документа 'Возврат товаров от покупателя'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomeraSostavNabora.TABLE_NAME, daoClass = DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomeraSostavNaboraDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomeraSostavNabora.META_NAME)
public class DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomeraSostavNabora extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomeraSostavNabora";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "СерийныеНомераСоставНабора";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentVozvratTovarovOtPokupatelya.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentVozvratTovarovOtPokupatelya owner;

	
	/**
	 * Имя поля 'Ключ связи' в таблице базы данных
	 */
	public static final String FIELD_NAME_KLYUCH_SVYAZI = "klyuchSvyazi";

	/**
	 * Ключ связи
	 */	
	@DatabaseField(columnName = FIELD_NAME_KLYUCH_SVYAZI)
	@MetadataField(type=MetadataFieldType.INT,name="КлючСвязи",description="Ключ связи")
	public int klyuchSvyazi;
	
	/**
	 * Имя поля 'Серийный номер' в таблице базы данных
	 */
	public static final String FIELD_NAME_SERIINII_NOMER = "seriiniiNomer";

	/**
	 * Серийный номер
	 */	
	@DatabaseField(columnName = FIELD_NAME_SERIINII_NOMER, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="СерийныйНомер",description="Серийный номер")
	public CatalogSeriinieNomera seriiniiNomer;

	@Override
	public Ref getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Ref ref) {
		owner = (DocumentVozvratTovarovOtPokupatelya) ref;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
