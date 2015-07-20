/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tasks.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableEx;

/**
 * Внешняя таблица 'Задачи'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@DatabaseTable(tableName=ExTableTasks.TABLE_NAME, daoClass = ExTableTasksDao.class) 
@MetadataObject(type=MetadataObject.TYPE_EXTERNAL_TABLE, name="Задачи")
public class ExTableTasks extends TableEx {
	public static final String TABLE_NAME = "ExTableTasks";
	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'СрокИсполнения' в таблице базы данных
	 */
	public static final String FIELD_NAME_DEADLINE = "deadline";

	/**
	 * СрокИсполнения
	 */	
	@DatabaseField(columnName = FIELD_NAME_DEADLINE, dataType = DataType.DATE_LONG, index = true)
	@MetadataField(type=MetadataFieldType.DATA,name="СрокИсполнения",description="СрокИсполнения")
	public Date deadline;
	
	/**
	 * Имя поля 'Задача' в таблице базы данных
	 */
	public static final String FIELD_NAME_TASK = "task";

	/**
	 * Задача
	 */	
	@DatabaseField(columnName = FIELD_NAME_TASK, index = true)
	@MetadataField(type=MetadataFieldType.STRING,name="Задача",description="Задача")
	public String task;
	
	/**
	 * Имя поля 'Выполнено' в таблице базы данных
	 */
	public static final String FIELD_NAME_COMPLETE = "complete";

	/**
	 * Выполнено
	 */	
	@DatabaseField(columnName = FIELD_NAME_COMPLETE)
	@MetadataField(type=MetadataFieldType.BOOL,name="Выполнено",description="Выполнено")
	public boolean complete;
	
	@Override
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(deadline);
		sb.append(task);

		return sb.toString();
	}

}
