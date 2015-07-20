/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.TablePart;

/**
 * Табличная часть 'Товары' документа 'Внутренний заказ'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVnutrenniiZakazTPTovari.TABLE_NAME, daoClass = DocumentVnutrenniiZakazTPTovariDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentVnutrenniiZakazTPTovari.META_NAME)
public class DocumentVnutrenniiZakazTPTovari extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVnutrenniiZakazTPTovari";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Товары";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentVnutrenniiZakaz.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentVnutrenniiZakaz owner;

	
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
	 * Имя поля 'Единица измерения мест' в таблице базы данных
	 */
	public static final String FIELD_NAME_EDINICA_IZMERENIYA_MEST = "edinicaIzmereniyaMest";

	/**
	 * Единица измерения мест
	 */	
	@DatabaseField(columnName = FIELD_NAME_EDINICA_IZMERENIYA_MEST, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЕдиницаИзмеренияМест",description="Единица измерения мест")
	public CatalogEdiniciIzmereniya edinicaIzmereniyaMest;
	
	/**
	 * Имя поля 'Количество' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOLICHESTVO = "kolichestvo";

	/**
	 * Количество
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOLICHESTVO)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Количество",description="Количество")
	public double kolichestvo;
	
	/**
	 * Имя поля 'Количество мест' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOLICHESTVO_MEST = "kolichestvoMest";

	/**
	 * Количество мест
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOLICHESTVO_MEST)
	@MetadataField(type=MetadataFieldType.LONG,name="КоличествоМест",description="Количество мест")
	public long kolichestvoMest;
	
	/**
	 * Имя поля 'Коэффициент' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOEFFICIENT = "koefficient";

	/**
	 * Коэффициент
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOEFFICIENT)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Коэффициент",description="Коэффициент")
	public double koefficient;
	
	/**
	 * Имя поля 'Номенклатура' в таблице базы данных
	 */
	public static final String FIELD_NAME_NOMENKLATURA = "nomenklatura";

	/**
	 * Номенклатура
	 */	
	@DatabaseField(columnName = FIELD_NAME_NOMENKLATURA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Номенклатура",description="Номенклатура")
	public CatalogNomenklatura nomenklatura;
	
	/**
	 * Имя поля 'Характеристика номенклатуры' в таблице базы данных
	 */
	public static final String FIELD_NAME_HARAKTERISTIKA_NOMENKLATURI = "harakteristikaNomenklaturi";

	/**
	 * Характеристика номенклатуры
	 */	
	@DatabaseField(columnName = FIELD_NAME_HARAKTERISTIKA_NOMENKLATURI, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ХарактеристикаНоменклатуры",description="Характеристика номенклатуры")
	public CatalogHarakteristikiNomenklaturi harakteristikaNomenklaturi;

	@Override
	public Ref getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Ref ref) {
		owner = (DocumentVnutrenniiZakaz) ref;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
