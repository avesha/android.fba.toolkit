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
 * Перечисление 'Виды дополнительных внешних обработок'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@MetadataObject(type=MetadataObject.TYPE_ENUM, name=EnumVidiDopolnitelnihVneshnihObrabotok.META_NAME)
public enum EnumVidiDopolnitelnihVneshnihObrabotok implements IPresentation, IMetadata{
	
	@MetadataField(name="ПечатнаяФорма",type=MetadataFieldType.STRING,description="Печатная форма")
	PechatnayaForma("Печатная форма"),

	@MetadataField(name="ЗаполнениеТабличныхЧастей",type=MetadataFieldType.STRING,description="Заполнение табличных частей")
	ZapolnenieTablichnihChastei("Заполнение табличных частей"),

	@MetadataField(name="Обработка",type=MetadataFieldType.STRING,description="Обработка")
	Obrabotka("Обработка"),

	@MetadataField(name="Отчет",type=MetadataFieldType.STRING,description="Отчет")
	Otchet("Отчет");

	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ВидыДополнительныхВнешнихОбработок";
	
	/**
	 * Представление элемента перечисления
	 */
	private String description;

	EnumVidiDopolnitelnihVneshnihObrabotok(String descr){
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



