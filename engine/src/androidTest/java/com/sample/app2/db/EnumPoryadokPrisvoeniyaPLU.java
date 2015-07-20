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
 * Перечисление 'Порядок присвоения PLU'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumPoryadokPrisvoeniyaPLU.META_NAME)
public enum EnumPoryadokPrisvoeniyaPLU implements IPresentation, IMetadata{
	
	@MetadataField(name="ПоПорядку",type=MetadataFieldType.STRING,description="По порядку")
	PoPoryadku("По порядку"),

	@MetadataField(name="ПоКодуНоменклатуры",type=MetadataFieldType.STRING,description="По коду номенклатуры")
	PoKoduNomenklaturi("По коду номенклатуры");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ПорядокПрисвоенияPLU";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumPoryadokPrisvoeniyaPLU(String descr){
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



