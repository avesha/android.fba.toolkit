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
 * Табличная часть 'Товары' документа 'Возврат товаров от покупателя'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVozvratTovarovOtPokupatelyaTPTovari.TABLE_NAME, daoClass = DocumentVozvratTovarovOtPokupatelyaTPTovariDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentVozvratTovarovOtPokupatelyaTPTovari.META_NAME)
public class DocumentVozvratTovarovOtPokupatelyaTPTovari extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVozvratTovarovOtPokupatelyaTPTovari";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Товары";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentVozvratTovarovOtPokupatelya.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentVozvratTovarovOtPokupatelya owner;

	
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
	 * Имя поля 'Ед. мест' в таблице базы данных
	 */
	public static final String FIELD_NAME_EDINICA_IZMERENIYA_MEST = "edinicaIzmereniyaMest";

	/**
	 * Ед. мест
	 */	
	@DatabaseField(columnName = FIELD_NAME_EDINICA_IZMERENIYA_MEST, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЕдиницаИзмеренияМест",description="Ед. мест")
	public CatalogEdiniciIzmereniya edinicaIzmereniyaMest;
	
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
	 * Имя поля 'Сумма' в таблице базы данных
	 */
	public static final String FIELD_NAME_SUMMA = "summa";

	/**
	 * Сумма
	 */	
	@DatabaseField(columnName = FIELD_NAME_SUMMA)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Сумма",description="Сумма")
	public double summa;
	
	/**
	 * Имя поля 'Ставка НДС' в таблице базы данных
	 */
	public static final String FIELD_NAME_STAVKA_NDS = "stavkaNDS";

	/**
	 * Ставка НДС
	 */	
	@DatabaseField(columnName = FIELD_NAME_STAVKA_NDS)
	@MetadataField(type=MetadataFieldType.ENUM,name="СтавкаНДС",description="Ставка НДС")
	public EnumStavkiNDS stavkaNDS;
	
	/**
	 * Имя поля 'Сумма НДС' в таблице базы данных
	 */
	public static final String FIELD_NAME_SUMMA_NDS = "summaNDS";

	/**
	 * Сумма НДС
	 */	
	@DatabaseField(columnName = FIELD_NAME_SUMMA_NDS)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="СуммаНДС",description="Сумма НДС")
	public double summaNDS;
	
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
	
	/**
	 * Имя поля 'Серия номенклатуры' в таблице базы данных
	 */
	public static final String FIELD_NAME_SERIYA_NOMENKLATURI = "seriyaNomenklaturi";

	/**
	 * Серия номенклатуры
	 */	
	@DatabaseField(columnName = FIELD_NAME_SERIYA_NOMENKLATURI, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="СерияНоменклатуры",description="Серия номенклатуры")
	public CatalogSeriiNomenklaturi seriyaNomenklaturi;
	
	/**
	 * Имя поля 'Себестоимость партии' в таблице базы данных
	 */
	public static final String FIELD_NAME_SEBESTOIMOST = "sebestoimost";

	/**
	 * Себестоимость партии
	 */	
	@DatabaseField(columnName = FIELD_NAME_SEBESTOIMOST)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Себестоимость",description="Себестоимость партии")
	public double sebestoimost;
	
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
	 * Имя поля 'Качество' в таблице базы данных
	 */
	public static final String FIELD_NAME_KACHESTVO = "kachestvo";

	/**
	 * Качество
	 */	
	@DatabaseField(columnName = FIELD_NAME_KACHESTVO, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Качество",description="Качество")
	public CatalogKachestvo kachestvo;
	
	/**
	 * Имя поля 'Склад' в таблице базы данных
	 */
	public static final String FIELD_NAME_SKLAD = "sklad";

	/**
	 * Склад
	 */	
	@DatabaseField(columnName = FIELD_NAME_SKLAD, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Склад",description="Склад")
	public CatalogSkladi sklad;
	
	/**
	 * Имя поля 'Приходный ордер' в таблице базы данных
	 */
	public static final String FIELD_NAME_PRIHODNII_ORDER = "prihodniiOrder";

	/**
	 * Приходный ордер
	 */	
	@DatabaseField(columnName = FIELD_NAME_PRIHODNII_ORDER, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ПриходныйОрдер",description="Приходный ордер")
	public DocumentPrihodniiOrderNaTovari prihodniiOrder;
	
	/**
	 * Имя поля 'Ключ строки' в таблице базы данных
	 */
	public static final String FIELD_NAME_KLYUCH_STROKI = "klyuchStroki";

	/**
	 * Ключ строки
	 */	
	@DatabaseField(columnName = FIELD_NAME_KLYUCH_STROKI)
	@MetadataField(type=MetadataFieldType.INT,name="КлючСтроки",description="Ключ строки")
	public int klyuchStroki;
	
	/**
	 * Имя поля 'Заказ покупателя' в таблице базы данных
	 */
	public static final String FIELD_NAME_ZAKAZ_POKUPATELYA = "zakazPokupatelya";

	/**
	 * Заказ покупателя
	 */	
	@DatabaseField(columnName = FIELD_NAME_ZAKAZ_POKUPATELYA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЗаказПокупателя",description="Заказ покупателя")
	public DocumentZakazPokupatelya zakazPokupatelya;
	
	/**
	 * Имя поля 'Ключ связи' в таблице базы данных
	 */
	public static final String FIELD_NAME_KLYUCH_SVYAZI = "klyuchSvyazi";

	/**
	 * Ключ связи
	 */	
	@DatabaseField(columnName = FIELD_NAME_KLYUCH_SVYAZI)
	@MetadataField(type=MetadataFieldType.INT,name="КлючСвязи",description="Ключ связи")
	public int klyuchSvyazi;
	
	/**
	 * Имя поля 'Процент автоматических скидок' в таблице базы данных
	 */
	public static final String FIELD_NAME_PROCENT_AVTOMATICHESKIH_SKIDOK = "procentAvtomaticheskihSkidok";

	/**
	 * Процент автоматических скидок
	 */	
	@DatabaseField(columnName = FIELD_NAME_PROCENT_AVTOMATICHESKIH_SKIDOK)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="ПроцентАвтоматическихСкидок",description="Процент автоматических скидок")
	public double procentAvtomaticheskihSkidok;
	
	/**
	 * Имя поля 'Условие автоматической скидки' в таблице базы данных
	 */
	public static final String FIELD_NAME_USLOVIE_AVTOMATICHESKOI_SKIDKI = "uslovieAvtomaticheskoiSkidki";

	/**
	 * Условие автоматической скидки
	 */	
	@DatabaseField(columnName = FIELD_NAME_USLOVIE_AVTOMATICHESKOI_SKIDKI)
	@MetadataField(type=MetadataFieldType.ENUM,name="УсловиеАвтоматическойСкидки",description="Условие автоматической скидки")
	public EnumUsloviyaSkidkiNacenki uslovieAvtomaticheskoiSkidki;

	@Override
	public Ref getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Ref ref) {
		owner = (DocumentVozvratTovarovOtPokupatelya) ref;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
