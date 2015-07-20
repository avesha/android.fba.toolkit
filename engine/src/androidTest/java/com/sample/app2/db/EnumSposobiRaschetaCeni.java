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
 * Перечисление 'Способы расчета цены'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumSposobiRaschetaCeni.META_NAME)
public enum EnumSposobiRaschetaCeni implements IPresentation, IMetadata{
	
	@MetadataField(name="ПоПроцентнойНаценкеНаБазовыйТип",type=MetadataFieldType.STRING,description="По процентной наценке на базовый тип")
	PoProcentnoiNacenkeNaBazoviiTip("По процентной наценке на базовый тип"),

	@MetadataField(name="ПоВхождениюБазовойЦеныВДиапазон",type=MetadataFieldType.STRING,description="По вхождению базовой цены в диапазон")
	PoVhozhdeniyuBazovoiCeniVDiapazon("По вхождению базовой цены в диапазон");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "СпособыРасчетаЦены";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumSposobiRaschetaCeni(String descr){
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



