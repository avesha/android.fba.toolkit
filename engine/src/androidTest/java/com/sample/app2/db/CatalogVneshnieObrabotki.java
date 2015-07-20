/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Справочник 'Внешние обработки'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=CatalogVneshnieObrabotki.TABLE_NAME, daoClass = CatalogVneshnieObrabotkiDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogVneshnieObrabotki.META_NAME, hierarchical = true)
public class CatalogVneshnieObrabotki extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogVneshnieObrabotki";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ВнешниеОбработки";
		
	private static final long serialVersionUID = 1L;


	
	/**
	 * Табличная часть 'Принадлежность'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="Принадлежность",description="Принадлежность")
	public ForeignCollection<CatalogVneshnieObrabotkiTPPrinadlezhnost> prinadlezhnost;

	@Override
	public Catalog getOwner() {
		return null;
	}

	@Override
	public void setOwner(Catalog catalogRef) {
    	
	}
	
	@Override
	public List<Class<? extends TablePart>> getTabularSections() {
		List<Class<? extends TablePart>> lstTabSections = new ArrayList<Class<? extends TablePart>>();
		lstTabSections.add(CatalogVneshnieObrabotkiTPPrinadlezhnost.class);
		return lstTabSections;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
	
	@Override
	public String getPresentation() {
		return getDescription();
	}
}
