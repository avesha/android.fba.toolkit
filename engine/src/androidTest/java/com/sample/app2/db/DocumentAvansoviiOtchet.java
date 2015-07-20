/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Документ 'Авансовый отчет'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentAvansoviiOtchet.TABLE_NAME, daoClass = DocumentAvansoviiOtchetDao.class) 
@MetadataObject(type=MetadataObject.TYPE_DOCUMENT, name=DocumentAvansoviiOtchet.META_NAME)
public class DocumentAvansoviiOtchet extends Document {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentAvansoviiOtchet";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "АвансовыйОтчет";
	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Комментарий' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOMMENTARII = "kommentarii";

	/**
	 * Комментарий
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOMMENTARII)
	@MetadataField(type=MetadataFieldType.STRING,name="Комментарий",description="Комментарий")
	public String kommentarii;
	
	/**
	 * Имя поля 'Вид поступления' в таблице базы данных
	 */
	public static final String FIELD_NAME_VID_POSTUPLENIYA = "vidPostupleniya";

	/**
	 * Вид поступления
	 */	
	@DatabaseField(columnName = FIELD_NAME_VID_POSTUPLENIYA)
	@MetadataField(type=MetadataFieldType.ENUM,name="ВидПоступления",description="Вид поступления")
	public EnumVidiPostupleniyaTovarov vidPostupleniya;

	@Override
	public List<Class<? extends TablePart>> getTabularSections() {
		return null;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}

}
