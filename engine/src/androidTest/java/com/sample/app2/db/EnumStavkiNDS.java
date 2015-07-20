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
 * Перечисление 'Ставки НДС'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumStavkiNDS.META_NAME)
public enum EnumStavkiNDS implements IPresentation, IMetadata{
	
	@MetadataField(name="НДС18",type=MetadataFieldType.STRING,description="18%")
	NDS18("18%"),

	@MetadataField(name="НДС18_118",type=MetadataFieldType.STRING,description="18% / 118%")
	NDS18_118("18% / 118%"),

	@MetadataField(name="НДС10",type=MetadataFieldType.STRING,description="10%")
	NDS10("10%"),

	@MetadataField(name="НДС10_110",type=MetadataFieldType.STRING,description="10% / 110%")
	NDS10_110("10% / 110%"),

	@MetadataField(name="НДС0",type=MetadataFieldType.STRING,description="0%")
	NDS0("0%"),

	@MetadataField(name="БезНДС",type=MetadataFieldType.STRING,description="Без НДС")
	BezNDS("Без НДС"),

	@MetadataField(name="НДС20",type=MetadataFieldType.STRING,description="20%")
	NDS20("20%"),

	@MetadataField(name="НДС20_120",type=MetadataFieldType.STRING,description="20% / 120%")
	NDS20_120("20% / 120%");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "СтавкиНДС";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumStavkiNDS(String descr){
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



