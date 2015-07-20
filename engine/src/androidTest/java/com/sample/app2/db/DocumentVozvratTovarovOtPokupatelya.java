/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Документ 'Возврат товаров от покупателя'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVozvratTovarovOtPokupatelya.TABLE_NAME, daoClass = DocumentVozvratTovarovOtPokupatelyaDao.class) 
@MetadataObject(type=MetadataObject.TYPE_DOCUMENT, name=DocumentVozvratTovarovOtPokupatelya.META_NAME)
public class DocumentVozvratTovarovOtPokupatelya extends Document {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVozvratTovarovOtPokupatelya";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ВозвратТоваровОтПокупателя";
	
	private static final long serialVersionUID = 1L;


	
	/**
	 * Табличная часть 'Товары'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="Товары",description="Товары")
	public ForeignCollection<DocumentVozvratTovarovOtPokupatelyaTPTovari> tovari;
	
	/**
	 * Табличная часть 'Возвратная тара'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="ВозвратнаяТара",description="Возвратная тара")
	public ForeignCollection<DocumentVozvratTovarovOtPokupatelyaTPVozvratnayaTara> vozvratnayaTara;
	
	/**
	 * Табличная часть 'Состав набора'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="СоставНабора",description="Состав набора")
	public ForeignCollection<DocumentVozvratTovarovOtPokupatelyaTPSostavNabora> sostavNabora;
	
	/**
	 * Табличная часть 'Серийные номера'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="СерийныеНомера",description="Серийные номера")
	public ForeignCollection<DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomera> seriinieNomera;
	
	/**
	 * Табличная часть 'Серийные номера состав набора'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="СерийныеНомераСоставНабора",description="Серийные номера состав набора")
	public ForeignCollection<DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomeraSostavNabora> seriinieNomeraSostavNabora;
	
	/**
	 * Табличная часть 'Документы расчетов с контрагентом'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="ДокументыРасчетовСКонтрагентом",description="Документы расчетов с контрагентом")
	public ForeignCollection<DocumentVozvratTovarovOtPokupatelyaTPDokumentiRaschetovSKontragentom> dokumentiRaschetovSKontragentom;

	@Override
	public List<Class<? extends TablePart>> getTabularSections() {
		List<Class<? extends TablePart>> lstTabSections = new ArrayList<Class<? extends TablePart>>();
		lstTabSections.add(DocumentVozvratTovarovOtPokupatelyaTPTovari.class);
		lstTabSections.add(DocumentVozvratTovarovOtPokupatelyaTPVozvratnayaTara.class);
		lstTabSections.add(DocumentVozvratTovarovOtPokupatelyaTPSostavNabora.class);
		lstTabSections.add(DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomera.class);
		lstTabSections.add(DocumentVozvratTovarovOtPokupatelyaTPSeriinieNomeraSostavNabora.class);
		lstTabSections.add(DocumentVozvratTovarovOtPokupatelyaTPDokumentiRaschetovSKontragentom.class);
		return lstTabSections;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}

}
