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
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.TablePart;

/**
 * Табличная часть 'Документы расчетов с контрагентом' документа 'Возврат товаров от покупателя'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVozvratTovarovOtPokupatelyaTPDokumentiRaschetovSKontragentom.TABLE_NAME, daoClass = DocumentVozvratTovarovOtPokupatelyaTPDokumentiRaschetovSKontragentomDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentVozvratTovarovOtPokupatelyaTPDokumentiRaschetovSKontragentom.META_NAME)
public class DocumentVozvratTovarovOtPokupatelyaTPDokumentiRaschetovSKontragentom extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVozvratTovarovOtPokupatelyaTPDokumentiRaschetovSKontragentom";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ДокументыРасчетовСКонтрагентом";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentVozvratTovarovOtPokupatelya.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentVozvratTovarovOtPokupatelya owner;

	
	/**
	 * Имя поля 'Сумма взаиморасчетов' в таблице базы данных
	 */
	public static final String FIELD_NAME_SUMMA_VZAIMORASCHETOV = "summaVzaimoraschetov";

	/**
	 * Сумма взаиморасчетов
	 */	
	@DatabaseField(columnName = FIELD_NAME_SUMMA_VZAIMORASCHETOV)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="СуммаВзаиморасчетов",description="Сумма взаиморасчетов")
	public double summaVzaimoraschetov;
	
	/**
	 * Имя поля 'Сумма регл' в таблице базы данных
	 */
	public static final String FIELD_NAME_SUMMA_REGL = "summaRegl";

	/**
	 * Сумма регл
	 */	
	@DatabaseField(columnName = FIELD_NAME_SUMMA_REGL)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="СуммаРегл",description="Сумма регл")
	public double summaRegl;
	
	/**
	 * Имя поля 'Дата оплаты' в таблице базы данных
	 */
	public static final String FIELD_NAME_DATA_OPLATI = "dataOplati";

	/**
	 * Дата оплаты
	 */	
	@DatabaseField(columnName = FIELD_NAME_DATA_OPLATI, dataType = DataType.DATE_LONG)
	@MetadataField(type=MetadataFieldType.DATA,name="ДатаОплаты",description="Дата оплаты")
	public Date dataOplati;
	
	/**
	 * Имя поля 'Сделка' в таблице базы данных
	 */
	public static final String FIELD_NAME_SDELKA = "sdelka";

	/**
	 * Сделка
	 */	
	@DatabaseField(columnName = FIELD_NAME_SDELKA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Сделка",description="Сделка")
	public DocumentZakazPokupatelya sdelka;

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
