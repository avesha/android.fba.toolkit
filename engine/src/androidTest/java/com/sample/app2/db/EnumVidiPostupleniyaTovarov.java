/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import ru.profi1c.engine.meta.IMetadata;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;

/**
 * Перечисление 'Виды поступления товаров'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumVidiPostupleniyaTovarov.META_NAME)
public enum EnumVidiPostupleniyaTovarov implements IPresentation, IMetadata{
	
	@MetadataField(name="НаСклад",type=MetadataFieldType.STRING,description="На склад")
	NaSklad("На склад"),

	@MetadataField(name="ПоОрдеру",type=MetadataFieldType.STRING,description="По ордеру")
	PoOrderu("По ордеру");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ВидыПоступленияТоваров";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumVidiPostupleniyaTovarov(String descr){
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



