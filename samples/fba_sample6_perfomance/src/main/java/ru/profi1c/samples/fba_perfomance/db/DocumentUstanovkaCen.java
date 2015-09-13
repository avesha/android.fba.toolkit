/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import java.math.BigInteger;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.meta.ValueStorage;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Документ 'Установка цен'
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 *
 */
@DatabaseTable(tableName=DocumentUstanovkaCen.TABLE_NAME, daoClass = DocumentUstanovkaCenDao.class) 
@MetadataObject(type=MetadataObject.TYPE_DOCUMENT, name=DocumentUstanovkaCen.META_NAME)
public class DocumentUstanovkaCen extends Document {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentUstanovkaCen";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "УстановкаЦен";
	
	private static final long serialVersionUID = 1L;


	
	/**
	 * Табличная часть 'Товары'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="Товары",description="Товары")
	public ForeignCollection<DocumentUstanovkaCenTPTovari> tovari;

	@Override
	public List<Class<? extends TablePart>> getTabularSections() {
		List<Class<? extends TablePart>> lstTabSections = new ArrayList<Class<? extends TablePart>>();
		lstTabSections.add(DocumentUstanovkaCenTPTovari.class);
		return lstTabSections;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}

}
