/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableInfRegPeriodic;

/**
 * Периодический регистр сведений 'Цены номенклатуры'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=RegCeniNomenklaturi.TABLE_NAME, daoClass = RegCeniNomenklaturiDao.class) 
@MetadataObject(type=MetadataObject.TYPE_INFORMATION_REGISTER, name=RegCeniNomenklaturi.META_NAME)
public class RegCeniNomenklaturi extends TableInfRegPeriodic {
 	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "RegCeniNomenklaturi";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ЦеныНоменклатуры";

	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Тип цен' в таблице базы данных
	 */
	public static final String FIELD_NAME_TIP_CEN = "tipCen";

	/**
	 * Тип цен
	 */	
	@DatabaseField(columnName = FIELD_NAME_TIP_CEN, index = true, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ТипЦен",description="Тип цен")
	public CatalogTipiCenNomenklaturi tipCen;
	
	/**
	 * Имя поля 'Номенклатура' в таблице базы данных
	 */
	public static final String FIELD_NAME_NOMENKLATURA = "nomenklatura";

	/**
	 * Номенклатура
	 */	
	@DatabaseField(columnName = FIELD_NAME_NOMENKLATURA, index = true, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Номенклатура",description="Номенклатура")
	public CatalogNomenklatura nomenklatura;
	
	/**
	 * Имя поля 'Характеристика номенклатуры' в таблице базы данных
	 */
	public static final String FIELD_NAME_HARAKTERISTIKA_NOMENKLATURI = "harakteristikaNomenklaturi";

	/**
	 * Характеристика номенклатуры
	 */	
	@DatabaseField(columnName = FIELD_NAME_HARAKTERISTIKA_NOMENKLATURI, index = true, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ХарактеристикаНоменклатуры",description="Характеристика номенклатуры")
	public CatalogHarakteristikiNomenklaturi harakteristikaNomenklaturi;
	
	/**
	 * Имя поля 'Валюта' в таблице базы данных
	 */
	public static final String FIELD_NAME_VALYUTA = "valyuta";

	/**
	 * Валюта
	 */	
	@DatabaseField(columnName = FIELD_NAME_VALYUTA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Валюта",description="Валюта")
	public CatalogValyuti valyuta;
	
	/**
	 * Имя поля 'Цена' в таблице базы данных
	 */
	public static final String FIELD_NAME_CENA = "cena";

	/**
	 * Цена
	 */	
	@DatabaseField(columnName = FIELD_NAME_CENA)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Цена",description="Цена")
	public double cena;
	
	/**
	 * Имя поля 'Единица измерения' в таблице базы данных
	 */
	public static final String FIELD_NAME_EDINICA_IZMERENIYA = "edinicaIzmereniya";

	/**
	 * Единица измерения
	 */	
	@DatabaseField(columnName = FIELD_NAME_EDINICA_IZMERENIYA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЕдиницаИзмерения",description="Единица измерения")
	public CatalogEdiniciIzmereniya edinicaIzmereniya;
	
	/**
	 * Имя поля 'Процент скидки или наценки' в таблице базы данных
	 */
	public static final String FIELD_NAME_PROCENT_SKIDKI_NACENKI = "procentSkidkiNacenki";

	/**
	 * Процент скидки или наценки
	 */	
	@DatabaseField(columnName = FIELD_NAME_PROCENT_SKIDKI_NACENKI)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="ПроцентСкидкиНаценки",description="Процент скидки или наценки")
	public double procentSkidkiNacenki;
	
	/**
	 * Имя поля 'Способ расчета цены' в таблице базы данных
	 */
	public static final String FIELD_NAME_SPOSOB_RASCHETA_CENI = "sposobRaschetaCeni";

	/**
	 * Способ расчета цены
	 */	
	@DatabaseField(columnName = FIELD_NAME_SPOSOB_RASCHETA_CENI)
	@MetadataField(type=MetadataFieldType.ENUM,name="СпособРасчетаЦены",description="Способ расчета цены")
	public EnumSposobiRaschetaCeni sposobRaschetaCeni;
	
	@Override
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(tipCen);
		sb.append(nomenklatura);
		sb.append(harakteristikaNomenklaturi);

		return sb.toString();
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
