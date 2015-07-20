/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import ru.profi1c.engine.meta.ConstTable;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.ValueStorage;

/**
 * Константы
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=Constants.TABLE_NAME, daoClass = ConstantsDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CONSTANT)
public final class Constants extends ConstTable {
	public static final String TABLE_NAME = "Constants";
	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Валюта регламентированного учета' в таблице базы данных
	 */
	public static final String FIELD_NAME_VALYUTA_REGLAMENTIROVANNOGO_UCHETA = "valyutaReglamentirovannogoUcheta";

	/**
	 * Валюта регламентированного учета
	 */	
	@DatabaseField(columnName = FIELD_NAME_VALYUTA_REGLAMENTIROVANNOGO_UCHETA, foreign = true, foreignAutoRefresh = true)
	@MetadataField(type=MetadataFieldType.REF,name="ВалютаРегламентированногоУчета",description="Валюта регламентированного учета")
	public CatalogValyuti valyutaReglamentirovannogoUcheta;
	
	/**
	 * Имя поля 'Длина кода весового товара' в таблице базы данных
	 */
	public static final String FIELD_NAME_DLINA_KODA_VESOVOGO_TOVARA = "dlinaKodaVesovogoTovara";

	/**
	 * Длина кода весового товара
	 */	
	@DatabaseField(columnName = FIELD_NAME_DLINA_KODA_VESOVOGO_TOVARA)
	@MetadataField(type=MetadataFieldType.LONG,name="ДлинаКодаВесовогоТовара",description="Длина кода весового товара")
	public long dlinaKodaVesovogoTovara;
	
	/**
	 * Имя поля 'Дополнительная колонка печатных форм документов' в таблице базы данных
	 */
	public static final String FIELD_NAME_DOPOLNITELNAYA_KOLONKA_PECHATNIH_FORM_DOKUMENTOV = "dopolnitelnayaKolonkaPechatnihFormDokumentov";

	/**
	 * Дополнительная колонка печатных форм документов
	 */	
	@DatabaseField(columnName = FIELD_NAME_DOPOLNITELNAYA_KOLONKA_PECHATNIH_FORM_DOKUMENTOV)
	@MetadataField(type=MetadataFieldType.ENUM,name="ДополнительнаяКолонкаПечатныхФормДокументов",description="Дополнительная колонка печатных форм документов")
	public EnumDopolnitelnayaKolonkaPechatnihFormDokumentov dopolnitelnayaKolonkaPechatnihFormDokumentov;
	
	/**
	 * Имя поля 'Заголовок системы' в таблице базы данных
	 */
	public static final String FIELD_NAME_ZAGOLOVOK_SISTEMI = "zagolovokSistemi";

	/**
	 * Заголовок системы
	 */	
	@DatabaseField(columnName = FIELD_NAME_ZAGOLOVOK_SISTEMI)
	@MetadataField(type=MetadataFieldType.STRING,name="ЗаголовокСистемы",description="Заголовок системы")
	public String zagolovokSistemi;
	
	/**
	 * Имя поля 'Значения по умолчанию для нового контрагента' в таблице базы данных
	 */
	public static final String FIELD_NAME_ZNACHENIYA_PO_UMOLCHANIYU_DLYA_NOVOGO_KONTRAGENTA = "znacheniyaPoUmolchaniyuDlyaNovogoKontragenta";

	/**
	 * Значения по умолчанию для нового контрагента
	 */	
	@DatabaseField(columnName = FIELD_NAME_ZNACHENIYA_PO_UMOLCHANIYU_DLYA_NOVOGO_KONTRAGENTA, dataType = DataType.SERIALIZABLE)
	@MetadataField(type=MetadataFieldType.SERIALIZABLE,name="ЗначенияПоУмолчаниюДляНовогоКонтрагента",description="Значения по умолчанию для нового контрагента")
	public ValueStorage znacheniyaPoUmolchaniyuDlyaNovogoKontragenta;
	
	/**
	 * Имя поля 'Использование встроенного почтового клиента' в таблице базы данных
	 */
	public static final String FIELD_NAME_ISPOLZOVANIE_VSTROENNOGO_POCHTOVOGO_KLIENTA = "ispolzovanieVstroennogoPochtovogoKlienta";

	/**
	 * Использование встроенного почтового клиента
	 */	
	@DatabaseField(columnName = FIELD_NAME_ISPOLZOVANIE_VSTROENNOGO_POCHTOVOGO_KLIENTA)
	@MetadataField(type=MetadataFieldType.BOOL,name="ИспользованиеВстроенногоПочтовогоКлиента",description="Использование встроенного почтового клиента")
	public boolean ispolzovanieVstroennogoPochtovogoKlienta;
	
	/**
	 * Имя поля 'Начало рабочего дня' в таблице базы данных
	 */
	public static final String FIELD_NAME_NACHALO_RABOCHEGO_DNYA = "nachaloRabochegoDnya";

	/**
	 * Начало рабочего дня
	 */	
	@DatabaseField(columnName = FIELD_NAME_NACHALO_RABOCHEGO_DNYA, dataType = DataType.DATE_LONG)
	@MetadataField(type=MetadataFieldType.DATA,name="НачалоРабочегоДня",description="Начало рабочего дня")
	public Date nachaloRabochegoDnya;
	
	/**
	 * Имя поля 'Пользователь для выполнения регламентных заданий в файловом режиме' в таблице базы данных
	 */
	public static final String FIELD_NAME_POLZOVATEL_DLYA_VIPOLNENIYA_REGLAMENTNIH_ZADANII_VFAILOVOM_VARIANTE = "polzovatelDlyaVipolneniyaReglamentnihZadaniiVFailovomVariante";

	/**
	 * Пользователь для выполнения регламентных заданий в файловом режиме
	 */	
	@DatabaseField(columnName = FIELD_NAME_POLZOVATEL_DLYA_VIPOLNENIYA_REGLAMENTNIH_ZADANII_VFAILOVOM_VARIANTE, foreign = true, foreignAutoRefresh = true)
	@MetadataField(type=MetadataFieldType.REF,name="ПользовательДляВыполненияРегламентныхЗаданийВФайловомВарианте",description="Пользователь для выполнения регламентных заданий в файловом режиме")
	public CatalogPolzovateli polzovatelDlyaVipolneniyaReglamentnihZadaniiVFailovomVariante;
	
	/**
	 * Имя поля 'Порядок присвоения PLU' в таблице базы данных
	 */
	public static final String FIELD_NAME_PORYADOK_PRISVOENIYA_PLU = "poryadokPrisvoeniyaPLU";

	/**
	 * Порядок присвоения PLU
	 */	
	@DatabaseField(columnName = FIELD_NAME_PORYADOK_PRISVOENIYA_PLU)
	@MetadataField(type=MetadataFieldType.ENUM,name="ПорядокПрисвоенияPLU",description="Порядок присвоения PLU")
	public EnumPoryadokPrisvoeniyaPLU poryadokPrisvoeniyaPLU;
	
	/**
	 * Имя поля 'Шаблоны телефонных номеров' в таблице базы данных
	 */
	public static final String FIELD_NAME_SHABLONI_TELEFONNIH_NOMEROV = "shabloniTelefonnihNomerov";

	/**
	 * Шаблоны телефонных номеров
	 */	
	@DatabaseField(columnName = FIELD_NAME_SHABLONI_TELEFONNIH_NOMEROV, dataType = DataType.SERIALIZABLE)
	@MetadataField(type=MetadataFieldType.SERIALIZABLE,name="ШаблоныТелефонныхНомеров",description="Шаблоны телефонных номеров")
	public ValueStorage shabloniTelefonnihNomerov;
	
	/**
	 * Имя поля 'Юр физ лицо' в таблице базы данных
	 */
	public static final String FIELD_NAME_YUR_FIZ_LICO = "yurFizLico";

	/**
	 * Юр физ лицо
	 */	
	@DatabaseField(columnName = FIELD_NAME_YUR_FIZ_LICO)
	@MetadataField(type=MetadataFieldType.ENUM,name="ЮрФизЛицо",description="Юр физ лицо")
	public EnumYurFizLico yurFizLico;
	
	/**
	 * Имя поля 'Fba хранилище (тестирование,удалить)' в таблице базы данных
	 */
	public static final String FIELD_NAME_FBA_HRANILISCHE = "fbaHranilische";

	/**
	 * Fba хранилище (тестирование,удалить)
	 */	
	@DatabaseField(columnName = FIELD_NAME_FBA_HRANILISCHE, dataType = DataType.SERIALIZABLE)
	@MetadataField(type=MetadataFieldType.SERIALIZABLE,name="fbaХранилище",description="Fba хранилище (тестирование,удалить)")
	public ValueStorage fbaHranilische;
	
}
