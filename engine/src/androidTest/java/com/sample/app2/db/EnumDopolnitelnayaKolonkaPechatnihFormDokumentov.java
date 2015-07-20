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
 * Перечисление 'Дополнительная колонка печатных форм документов'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumDopolnitelnayaKolonkaPechatnihFormDokumentov.META_NAME)
public enum EnumDopolnitelnayaKolonkaPechatnihFormDokumentov implements IPresentation, IMetadata{
	
	@MetadataField(name="НеВыводить",type=MetadataFieldType.STRING,description="Не выводить")
	NeVivodit("Не выводить"),

	@MetadataField(name="Артикул",type=MetadataFieldType.STRING,description="Артикул")
	Artikul("Артикул"),

	@MetadataField(name="Код",type=MetadataFieldType.STRING,description="Код")
	Kod("Код");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ДополнительнаяКолонкаПечатныхФормДокументов";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumDopolnitelnayaKolonkaPechatnihFormDokumentov(String descr){
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



