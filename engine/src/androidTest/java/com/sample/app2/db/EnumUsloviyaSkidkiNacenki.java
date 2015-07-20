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
 * Перечисление 'Условия скидки наценки'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumUsloviyaSkidkiNacenki.META_NAME)
public enum EnumUsloviyaSkidkiNacenki implements IPresentation, IMetadata{
	
	@MetadataField(name="ПоВидуОплаты",type=MetadataFieldType.STRING,description="По виду оплаты")
	PoViduOplati("По виду оплаты"),

	@MetadataField(name="ПоДисконтнойКарте",type=MetadataFieldType.STRING,description="По дисконтной карте")
	PoDiskontnoiKarte("По дисконтной карте"),

	@MetadataField(name="ПоКоличествуТовара",type=MetadataFieldType.STRING,description="Количество одного товара в документе превысило")
	PoKolichestvuTovara("Количество одного товара в документе превысило"),

	@MetadataField(name="ПоСуммеДокумента",type=MetadataFieldType.STRING,description="Сумма документа продажи превысила ")
	PoSummeDokumenta("Сумма документа продажи превысила "),

	@MetadataField(name="РучнаяСкидка",type=MetadataFieldType.STRING,description="Ручная скидка")
	RuchnayaSkidka("Ручная скидка"),

	@MetadataField(name="БезУсловий",type=MetadataFieldType.STRING,description="Без условий")
	BezUslovii("Без условий"),

	@MetadataField(name="СпецПредложение",type=MetadataFieldType.STRING,description="Спец. предложение")
	SpecPredlozhenie("Спец. предложение"),

	@MetadataField(name="ПоВидуДисконтныхКарт",type=MetadataFieldType.STRING,description="По виду дисконтных карт")
	PoViduDiskontnihKart("По виду дисконтных карт");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "УсловияСкидкиНаценки";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumUsloviyaSkidkiNacenki(String descr){
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



