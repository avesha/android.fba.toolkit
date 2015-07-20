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
 * Табличная часть 'Возвратная тара' документа 'Возврат товаров от покупателя'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVozvratTovarovOtPokupatelyaTPVozvratnayaTara.TABLE_NAME, daoClass = DocumentVozvratTovarovOtPokupatelyaTPVozvratnayaTaraDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentVozvratTovarovOtPokupatelyaTPVozvratnayaTara.META_NAME)
public class DocumentVozvratTovarovOtPokupatelyaTPVozvratnayaTara extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVozvratTovarovOtPokupatelyaTPVozvratnayaTara";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ВозвратнаяТара";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentVozvratTovarovOtPokupatelya.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentVozvratTovarovOtPokupatelya owner;

	
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
	 * Имя поля 'Цена' в таблице базы данных
	 */
	public static final String FIELD_NAME_CENA = "cena";

	/**
	 * Цена
	 */	
	@DatabaseField(columnName = FIELD_NAME_CENA)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Цена",description="Цена")
	public double cena;
	
	/**
	 * Имя поля 'Сумма' в таблице базы данных
	 */
	public static final String FIELD_NAME_SUMMA = "summa";

	/**
	 * Сумма
	 */	
	@DatabaseField(columnName = FIELD_NAME_SUMMA)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Сумма",description="Сумма")
	public double summa;
	
	/**
	 * Имя поля 'Склад' в таблице базы данных
	 */
	public static final String FIELD_NAME_SKLAD = "sklad";

	/**
	 * Склад
	 */	
	@DatabaseField(columnName = FIELD_NAME_SKLAD, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Склад",description="Склад")
	public CatalogSkladi sklad;
	
	/**
	 * Имя поля 'Приходный ордер' в таблице базы данных
	 */
	public static final String FIELD_NAME_PRIHODNII_ORDER = "prihodniiOrder";

	/**
	 * Приходный ордер
	 */	
	@DatabaseField(columnName = FIELD_NAME_PRIHODNII_ORDER, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ПриходныйОрдер",description="Приходный ордер")
	public DocumentPrihodniiOrderNaTovari prihodniiOrder;
	
	/**
	 * Имя поля 'Заказ покупателя' в таблице базы данных
	 */
	public static final String FIELD_NAME_ZAKAZ_POKUPATELYA = "zakazPokupatelya";

	/**
	 * Заказ покупателя
	 */	
	@DatabaseField(columnName = FIELD_NAME_ZAKAZ_POKUPATELYA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЗаказПокупателя",description="Заказ покупателя")
	public DocumentZakazPokupatelya zakazPokupatelya;

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
