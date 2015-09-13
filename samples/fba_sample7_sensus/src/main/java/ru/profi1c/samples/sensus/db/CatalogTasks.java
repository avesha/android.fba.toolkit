/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Справочник 'Задачи'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@DatabaseTable(tableName=CatalogTasks.TABLE_NAME, daoClass = CatalogTasksDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogTasks.META_NAME)
public class CatalogTasks extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogTasks";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Задачи";
		
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Дата создания' в таблице базы данных
	 */
	public static final String FIELD_NAME_DATE_CREATED = "dateCreated";

	/**
	 * Дата создания
	 */	
	@DatabaseField(columnName = FIELD_NAME_DATE_CREATED, dataType = DataType.DATE_LONG)
	@MetadataField(type=MetadataFieldType.DATA,name="ДатаСоздания",description="Дата создания")
	public Date dateCreated;
	
	/**
	 * Имя поля 'Торговый представитель' в таблице базы данных
	 */
	public static final String FIELD_NAME_SALES_AGENT = "salesAgent";

	/**
	 * Торговый представитель
	 */	
	@DatabaseField(columnName = FIELD_NAME_SALES_AGENT, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ТорговыйПредставитель",description="Торговый представитель")
	public CatalogSalesAgents salesAgent;
	
	/**
	 * Имя поля 'Торговая точка' в таблице базы данных
	 */
	public static final String FIELD_NAME_SALES_POINT = "salesPoint";

	/**
	 * Торговая точка
	 */	
	@DatabaseField(columnName = FIELD_NAME_SALES_POINT, foreign = true, foreignAutoRefresh = true)
	@MetadataField(type=MetadataFieldType.REF,name="ТорговаяТочка",description="Торговая точка")
	public CatalogSalesPoints salesPoint;
	
	/**
	 * Имя поля 'Дата начала' в таблице базы данных
	 */
	public static final String FIELD_NAME_DATE_BEGIN = "dateBegin";

	/**
	 * Дата начала
	 */	
	@DatabaseField(columnName = FIELD_NAME_DATE_BEGIN, dataType = DataType.DATE_LONG)
	@MetadataField(type=MetadataFieldType.DATA,name="ДатаНачала",description="Дата начала")
	public Date dateBegin;
	
	/**
	 * Имя поля 'Важность' в таблице базы данных
	 */
	public static final String FIELD_NAME_IMPORTANCE = "importance";

	/**
	 * Важность
	 */	
	@DatabaseField(columnName = FIELD_NAME_IMPORTANCE)
	@MetadataField(type=MetadataFieldType.ENUM,name="Важность",description="Важность")
	public EnumImportance importance;
	
	/**
	 * Имя поля 'Дата завершения' в таблице базы данных
	 */
	public static final String FIELD_NAME_DATE_COMPLETION = "dateCompletion";

	/**
	 * Дата завершения
	 */	
	@DatabaseField(columnName = FIELD_NAME_DATE_COMPLETION, dataType = DataType.DATE_LONG)
	@MetadataField(type=MetadataFieldType.DATA,name="ДатаЗавершения",description="Дата завершения")
	public Date dateCompletion;
	
	/**
	 * Имя поля 'Комментарий' в таблице базы данных
	 */
	public static final String FIELD_NAME_COMMENT = "comment";

	/**
	 * Комментарий
	 */	
	@DatabaseField(columnName = FIELD_NAME_COMMENT)
	@MetadataField(type=MetadataFieldType.STRING,name="Комментарий",description="Комментарий")
	public String comment;
	
	/**
	 * Имя поля 'Статус' в таблице базы данных
	 */
	public static final String FIELD_NAME_STATUS = "status";

	/**
	 * Статус
	 */	
	@DatabaseField(columnName = FIELD_NAME_STATUS)
	@MetadataField(type=MetadataFieldType.ENUM,name="Статус",description="Статус")
	public EnumTaskStatuses status;

	@Override
	public Catalog getOwner() {
		return null;
	}

	@Override
	public void setOwner(Catalog catalogRef) {
    	
	}
	
	@Override
	public List<Class<? extends TablePart>> getTabularSections() {
		return null;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
	
	@Override
	public String getPresentation() {
		return getCode();
	}
}
