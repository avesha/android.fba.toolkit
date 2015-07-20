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
 * Табличная часть 'Принадлежность' справочника 'Внешние обработки'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=CatalogVneshnieObrabotkiTPPrinadlezhnost.TABLE_NAME, daoClass = CatalogVneshnieObrabotkiTPPrinadlezhnostDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=CatalogVneshnieObrabotkiTPPrinadlezhnost.META_NAME)
public class CatalogVneshnieObrabotkiTPPrinadlezhnost extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogVneshnieObrabotkiTPPrinadlezhnost";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Принадлежность";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +CatalogVneshnieObrabotki.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected CatalogVneshnieObrabotki owner;

	
	/**
	 * Имя поля 'Представление объекта' в таблице базы данных
	 */
	public static final String FIELD_NAME_PREDSTAVLENIE_OBEKTA = "predstavlenieObekta";

	/**
	 * Представление объекта
	 */	
	@DatabaseField(columnName = FIELD_NAME_PREDSTAVLENIE_OBEKTA)
	@MetadataField(type=MetadataFieldType.STRING,name="ПредставлениеОбъекта",description="Представление объекта")
	public String predstavlenieObekta;
	
	/**
	 * Имя поля 'Табличная часть имя' в таблице базы данных
	 */
	public static final String FIELD_NAME_TABLICHNAYA_CHAST_IMYA = "tablichnayaChastImya";

	/**
	 * Табличная часть имя
	 */	
	@DatabaseField(columnName = FIELD_NAME_TABLICHNAYA_CHAST_IMYA)
	@MetadataField(type=MetadataFieldType.STRING,name="ТабличнаяЧастьИмя",description="Табличная часть имя")
	public String tablichnayaChastImya;

	@Override
	public Ref getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Ref ref) {
		owner = (CatalogVneshnieObrabotki) ref;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
