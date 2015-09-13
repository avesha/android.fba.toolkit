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
 * Перечисление 'Важность'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumImportance.META_NAME)
public enum EnumImportance implements IPresentation, IMetadata{
	
	@MetadataField(name="Высокая",type=MetadataFieldType.STRING,description="Высокая")
	High("Высокая"),

	@MetadataField(name="Средняя",type=MetadataFieldType.STRING,description="Средняя")
	Midle("Средняя"),

	@MetadataField(name="Низкая",type=MetadataFieldType.STRING,description="Низкая")
	Low("Низкая");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Важность";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumImportance(String descr){
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



