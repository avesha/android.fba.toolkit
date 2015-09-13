/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

import ru.profi1c.engine.meta.IMetadata;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;

/**
 * Перечисление 'Статусы задач'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumTaskStatuses.META_NAME)
public enum EnumTaskStatuses implements IPresentation, IMetadata{
	
	@MetadataField(name="Назначена",type=MetadataFieldType.STRING,description="Назначена")
	Appointed("Назначена"),

	@MetadataField(name="ВРаботе",type=MetadataFieldType.STRING,description="В работе")
	InWork("В работе"),

	@MetadataField(name="Выполнена",type=MetadataFieldType.STRING,description="Выполнена")
	Completed("Выполнена"),

	@MetadataField(name="Закрыта",type=MetadataFieldType.STRING,description="Закрыта")
	Closed("Закрыта");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "СтатусыЗадач";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumTaskStatuses(String descr){
		description = descr;
	}
	
	@Override
	public String getPresentation() {
		return description;
	}
	
	@Override
	public String getMetaType() {
		return MetadataObject.TYPE_ENUM;
	}

	@Override
	public String getMetaName() {
		return META_NAME;
	}
	
}



