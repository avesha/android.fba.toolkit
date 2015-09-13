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
 * Перечисление 'Статусы торговых точек'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumSalesPointStatuses.META_NAME)
public enum EnumSalesPointStatuses implements IPresentation, IMetadata{
	
	@MetadataField(name="Открыта",type=MetadataFieldType.STRING,description="Открыта")
	Open("Открыта"),

	@MetadataField(name="ВременноЗакрыта",type=MetadataFieldType.STRING,description="Временно закрыта")
	TemporarilyClosed("Временно закрыта"),

	@MetadataField(name="Закрыта",type=MetadataFieldType.STRING,description="Закрыта")
	Closed("Закрыта");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "СтатусыТорговыхТочек";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumSalesPointStatuses(String descr){
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



