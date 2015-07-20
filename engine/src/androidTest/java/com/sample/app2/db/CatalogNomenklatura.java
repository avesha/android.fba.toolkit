/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Справочник 'Номенклатура'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=CatalogNomenklatura.TABLE_NAME, daoClass = CatalogNomenklaturaDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogNomenklatura.META_NAME, hierarchical = true)
public class CatalogNomenklatura extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogNomenklatura";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Номенклатура";
		
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Статья затрат' в таблице базы данных
	 */
	public static final String FIELD_NAME_STATYA_ZATRAT = "statyaZatrat";

	/**
	 * Статья затрат
	 */	
	@DatabaseField(columnName = FIELD_NAME_STATYA_ZATRAT, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="СтатьяЗатрат",description="Статья затрат")
	public CatalogStatiZatrat statyaZatrat;
	
	/**
	 * Имя поля 'Артикул ' в таблице базы данных
	 */
	public static final String FIELD_NAME_ARTIKUL = "artikul";

	/**
	 * Артикул 
	 */	
	@DatabaseField(columnName = FIELD_NAME_ARTIKUL)
	@MetadataField(type=MetadataFieldType.STRING,name="Артикул",description="Артикул ")
	public String artikul;
	
	/**
	 * Имя поля 'Базовая единица измерения' в таблице базы данных
	 */
	public static final String FIELD_NAME_BAZOVAYA_EDINICA_IZMERENIYA = "bazovayaEdinicaIzmereniya";

	/**
	 * Базовая единица измерения
	 */	
	@DatabaseField(columnName = FIELD_NAME_BAZOVAYA_EDINICA_IZMERENIYA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="БазоваяЕдиницаИзмерения",description="Базовая единица измерения")
	public CatalogKlassifikatorEdinicIzmereniya bazovayaEdinicaIzmereniya;
	
	/**
	 * Имя поля 'Весовой' в таблице базы данных
	 */
	public static final String FIELD_NAME_VESOVOI = "vesovoi";

	/**
	 * Весовой
	 */	
	@DatabaseField(columnName = FIELD_NAME_VESOVOI)
	@MetadataField(type=MetadataFieldType.BOOL,name="Весовой",description="Весовой")
	public boolean vesovoi;
	
	/**
	 * Имя поля 'Весовой коэффициент вхождения' в таблице базы данных
	 */
	public static final String FIELD_NAME_VESOVOI_KOEFFICIENT_VHOZHDENIYA = "vesovoiKoefficientVhozhdeniya";

	/**
	 * Весовой коэффициент вхождения
	 */	
	@DatabaseField(columnName = FIELD_NAME_VESOVOI_KOEFFICIENT_VHOZHDENIYA)
	@MetadataField(type=MetadataFieldType.LONG,name="ВесовойКоэффициентВхождения",description="Весовой коэффициент вхождения")
	public long vesovoiKoefficientVhozhdeniya;
	
	/**
	 * Имя поля 'Вести партионный учет по сериям' в таблице базы данных
	 */
	public static final String FIELD_NAME_VESTI_PARTIONNII_UCHET_PO_SERIYAM = "vestiPartionniiUchetPoSeriyam";

	/**
	 * Вести партионный учет по сериям
	 */	
	@DatabaseField(columnName = FIELD_NAME_VESTI_PARTIONNII_UCHET_PO_SERIYAM)
	@MetadataField(type=MetadataFieldType.BOOL,name="ВестиПартионныйУчетПоСериям",description="Вести партионный учет по сериям")
	public boolean vestiPartionniiUchetPoSeriyam;
	
	/**
	 * Имя поля 'Вести учет по сериям' в таблице базы данных
	 */
	public static final String FIELD_NAME_VESTI_UCHET_PO_SERIYAM = "vestiUchetPoSeriyam";

	/**
	 * Вести учет по сериям
	 */	
	@DatabaseField(columnName = FIELD_NAME_VESTI_UCHET_PO_SERIYAM)
	@MetadataField(type=MetadataFieldType.BOOL,name="ВестиУчетПоСериям",description="Вести учет по сериям")
	public boolean vestiUchetPoSeriyam;
	
	/**
	 * Имя поля 'Вести учет по характеристикам' в таблице базы данных
	 */
	public static final String FIELD_NAME_VESTI_UCHET_PO_HARAKTERISTIKAM = "vestiUchetPoHarakteristikam";

	/**
	 * Вести учет по характеристикам
	 */	
	@DatabaseField(columnName = FIELD_NAME_VESTI_UCHET_PO_HARAKTERISTIKAM)
	@MetadataField(type=MetadataFieldType.BOOL,name="ВестиУчетПоХарактеристикам",description="Вести учет по характеристикам")
	public boolean vestiUchetPoHarakteristikam;
	
	/**
	 * Имя поля 'Единица для отчетов' в таблице базы данных
	 */
	public static final String FIELD_NAME_EDINICA_DLYA_OTCHETOV = "edinicaDlyaOtchetov";

	/**
	 * Единица для отчетов
	 */	
	@DatabaseField(columnName = FIELD_NAME_EDINICA_DLYA_OTCHETOV, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЕдиницаДляОтчетов",description="Единица для отчетов")
	public CatalogEdiniciIzmereniya edinicaDlyaOtchetov;
	
	/**
	 * Имя поля 'Единица хранения остатков' в таблице базы данных
	 */
	public static final String FIELD_NAME_EDINICA_HRANENIYA_OSTATKOV = "edinicaHraneniyaOstatkov";

	/**
	 * Единица хранения остатков
	 */	
	@DatabaseField(columnName = FIELD_NAME_EDINICA_HRANENIYA_OSTATKOV, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЕдиницаХраненияОстатков",description="Единица хранения остатков")
	public CatalogEdiniciIzmereniya edinicaHraneniyaOstatkov;
	
	/**
	 * Имя поля 'Комментарий' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOMMENTARII = "kommentarii";

	/**
	 * Комментарий
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOMMENTARII)
	@MetadataField(type=MetadataFieldType.STRING,name="Комментарий",description="Комментарий")
	public String kommentarii;
	
	/**
	 * Имя поля 'Набор' в таблице базы данных
	 */
	public static final String FIELD_NAME_NABOR = "nabor";

	/**
	 * Набор
	 */	
	@DatabaseField(columnName = FIELD_NAME_NABOR)
	@MetadataField(type=MetadataFieldType.BOOL,name="Набор",description="Набор")
	public boolean nabor;
	
	/**
	 * Имя поля 'Полное наименование' в таблице базы данных
	 */
	public static final String FIELD_NAME_NAIMENOVANIE_POLNOE = "naimenovaniePolnoe";

	/**
	 * Полное наименование
	 */	
	@DatabaseField(columnName = FIELD_NAME_NAIMENOVANIE_POLNOE)
	@MetadataField(type=MetadataFieldType.STRING,name="НаименованиеПолное",description="Полное наименование")
	public String naimenovaniePolnoe;
	
	/**
	 * Имя поля 'Номенклатурная группа' в таблице базы данных
	 */
	public static final String FIELD_NAME_NOMENKLATURNAYA_GRUPPA = "nomenklaturnayaGruppa";

	/**
	 * Номенклатурная группа
	 */	
	@DatabaseField(columnName = FIELD_NAME_NOMENKLATURNAYA_GRUPPA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="НоменклатурнаяГруппа",description="Номенклатурная группа")
	public CatalogNomenklaturnieGruppi nomenklaturnayaGruppa;
	
	/**
	 * Имя поля 'Номер ГТД' в таблице базы данных
	 */
	public static final String FIELD_NAME_NOMER_GTD = "nomerGTD";

	/**
	 * Номер ГТД
	 */	
	@DatabaseField(columnName = FIELD_NAME_NOMER_GTD, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="НомерГТД",description="Номер ГТД")
	public CatalogNomeraGTD nomerGTD;
	
	/**
	 * Имя поля 'Основное изображение' в таблице базы данных
	 */
	public static final String FIELD_NAME_OSNOVNOE_IZOBRAZHENIE = "osnovnoeIzobrazhenie";

	/**
	 * Основное изображение
	 */	
	@DatabaseField(columnName = FIELD_NAME_OSNOVNOE_IZOBRAZHENIE, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ОсновноеИзображение",description="Основное изображение")
	public CatalogHranilischeDopolnitelnoiInformacii osnovnoeIzobrazhenie;
	
	/**
	 * Имя поля 'Основной поставщик' в таблице базы данных
	 */
	public static final String FIELD_NAME_OSNOVNOI_POSTAVSCHIK = "osnovnoiPostavschik";

	/**
	 * Основной поставщик
	 */	
	@DatabaseField(columnName = FIELD_NAME_OSNOVNOI_POSTAVSCHIK, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ОсновнойПоставщик",description="Основной поставщик")
	public CatalogKontragenti osnovnoiPostavschik;
	
	/**
	 * Имя поля 'Ответственный менеджер за покупки' в таблице базы данных
	 */
	public static final String FIELD_NAME_OTVETSTVENNII_MENEDZHER_ZA_POKUPKI = "otvetstvenniiMenedzherZaPokupki";

	/**
	 * Ответственный менеджер за покупки
	 */	
	@DatabaseField(columnName = FIELD_NAME_OTVETSTVENNII_MENEDZHER_ZA_POKUPKI, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ОтветственныйМенеджерЗаПокупки",description="Ответственный менеджер за покупки")
	public CatalogPolzovateli otvetstvenniiMenedzherZaPokupki;
	
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
	 * Имя поля 'Страна происхождения' в таблице базы данных
	 */
	public static final String FIELD_NAME_STRANA_PROISHOZHDENIYA = "stranaProishozhdeniya";

	/**
	 * Страна происхождения
	 */	
	@DatabaseField(columnName = FIELD_NAME_STRANA_PROISHOZHDENIYA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="СтранаПроисхождения",description="Страна происхождения")
	public CatalogKlassifikatorStranMira stranaProishozhdeniya;
	
	/**
	 * Имя поля 'Услуга' в таблице базы данных
	 */
	public static final String FIELD_NAME_USLUGA = "usluga";

	/**
	 * Услуга
	 */	
	@DatabaseField(columnName = FIELD_NAME_USLUGA)
	@MetadataField(type=MetadataFieldType.BOOL,name="Услуга",description="Услуга")
	public boolean usluga;
	
	/**
	 * Имя поля 'Номенклатурная группа затрат' в таблице базы данных
	 */
	public static final String FIELD_NAME_NOMENKLATURNAYA_GRUPPA_ZATRAT = "nomenklaturnayaGruppaZatrat";

	/**
	 * Номенклатурная группа затрат
	 */	
	@DatabaseField(columnName = FIELD_NAME_NOMENKLATURNAYA_GRUPPA_ZATRAT, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="НоменклатурнаяГруппаЗатрат",description="Номенклатурная группа затрат")
	public CatalogNomenklaturnieGruppi nomenklaturnayaGruppaZatrat;
	
	/**
	 * Имя поля 'Вид номенклатуры' в таблице базы данных
	 */
	public static final String FIELD_NAME_VID_NOMENKLATURI = "vidNomenklaturi";

	/**
	 * Вид номенклатуры
	 */	
	@DatabaseField(columnName = FIELD_NAME_VID_NOMENKLATURI, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ВидНоменклатуры",description="Вид номенклатуры")
	public CatalogVidiNomenklaturi vidNomenklaturi;
	
	/**
	 * Имя поля 'Вести серийные номера' в таблице базы данных
	 */
	public static final String FIELD_NAME_VESTI_SERIINIE_NOMERA = "vestiSeriinieNomera";

	/**
	 * Вести серийные номера
	 */	
	@DatabaseField(columnName = FIELD_NAME_VESTI_SERIINIE_NOMERA)
	@MetadataField(type=MetadataFieldType.BOOL,name="ВестиСерийныеНомера",description="Вести серийные номера")
	public boolean vestiSeriinieNomera;
	
	/**
	 * Имя поля 'Комплект' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOMPLEKT = "komplekt";

	/**
	 * Комплект
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOMPLEKT)
	@MetadataField(type=MetadataFieldType.BOOL,name="Комплект",description="Комплект")
	public boolean komplekt;
	
	/**
	 * Имя поля 'Ценовая группа' в таблице базы данных
	 */
	public static final String FIELD_NAME_CENOVAYA_GRUPPA = "cenovayaGruppa";

	/**
	 * Ценовая группа
	 */	
	@DatabaseField(columnName = FIELD_NAME_CENOVAYA_GRUPPA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ЦеноваяГруппа",description="Ценовая группа")
	public CatalogCenovieGruppi cenovayaGruppa;
	
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
	 * Имя поля 'Дополнительное описание номенклатуры' в таблице базы данных
	 */
	public static final String FIELD_NAME_DOPOLNITELNOE_OPISANIE_NOMENKLATURI = "dopolnitelnoeOpisanieNomenklaturi";

	/**
	 * Дополнительное описание номенклатуры
	 */	
	@DatabaseField(columnName = FIELD_NAME_DOPOLNITELNOE_OPISANIE_NOMENKLATURI)
	@MetadataField(type=MetadataFieldType.STRING,name="ДополнительноеОписаниеНоменклатуры",description="Дополнительное описание номенклатуры")
	public String dopolnitelnoeOpisanieNomenklaturi;

	@Override
	public Catalog getOwner() {
		return null;
	}

	@Override
	public void setOwner(Catalog catalogRef) {
    	
	}
	
	@Override
	public List<Class<? extends TablePart>> getTabularSections() {
		return null;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
	
	@Override
	public String getPresentation() {
		return getDescription();
	}
}
