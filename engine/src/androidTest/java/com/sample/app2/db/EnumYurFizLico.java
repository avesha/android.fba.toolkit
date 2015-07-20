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
 * Перечисление 'Юр. / физ. лицо'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumYurFizLico.META_NAME)
public enum EnumYurFizLico implements IPresentation, IMetadata{
	
	@MetadataField(name="ЮрЛицо",type=MetadataFieldType.STRING,description="Юр. лицо")
	YurLico("Юр. лицо"),

	@MetadataField(name="ФизЛицо",type=MetadataFieldType.STRING,description="Физ. лицо")
	FizLico("Физ. лицо");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ЮрФизЛицо";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumYurFizLico(String descr){
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



