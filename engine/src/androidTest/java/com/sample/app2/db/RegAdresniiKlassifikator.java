/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigInteger;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableInfReg;

/**
 * Регистр сведений 'Адресный классификатор'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=RegAdresniiKlassifikator.TABLE_NAME, daoClass = RegAdresniiKlassifikatorDao.class) 
@MetadataObject(type=MetadataObject.TYPE_INFORMATION_REGISTER, name=RegAdresniiKlassifikator.META_NAME)
public class RegAdresniiKlassifikator extends TableInfReg {
 	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "RegAdresniiKlassifikator";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "АдресныйКлассификатор";

	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Тип адресного элемента' в таблице базы данных
	 */
	public static final String FIELD_NAME_TIP_ADRESNOGO_ELEMENTA = "tipAdresnogoElementa";

	/**
	 * Тип адресного элемента
	 */	
	@DatabaseField(columnName = FIELD_NAME_TIP_ADRESNOGO_ELEMENTA, index = true)
	@MetadataField(type=MetadataFieldType.INT,name="ТипАдресногоЭлемента",description="Тип адресного элемента")
	public int tipAdresnogoElementa;
	
	/**
	 * Имя поля 'Код региона в коде' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOD_REGIONA_VKODE = "kodRegionaVKode";

	/**
	 * Код региона в коде
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOD_REGIONA_VKODE, index = true)
	@MetadataField(type=MetadataFieldType.INT,name="КодРегионаВКоде",description="Код региона в коде")
	public int kodRegionaVKode;
	
	/**
	 * Имя поля 'Код района в коде' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOD_RAIONA_VKODE = "kodRaionaVKode";

	/**
	 * Код района в коде
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOD_RAIONA_VKODE, index = true)
	@MetadataField(type=MetadataFieldType.INT,name="КодРайонаВКоде",description="Код района в коде")
	public int kodRaionaVKode;
	
	/**
	 * Имя поля 'Код города в коде' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOD_GORODA_VKODE = "kodGorodaVKode";

	/**
	 * Код города в коде
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOD_GORODA_VKODE, index = true)
	@MetadataField(type=MetadataFieldType.INT,name="КодГородаВКоде",description="Код города в коде")
	public int kodGorodaVKode;
	
	/**
	 * Имя поля 'Код населенного пункта в коде' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOD_NASELENNOGO_PUNKTA_VKODE = "kodNaselennogoPunktaVKode";

	/**
	 * Код населенного пункта в коде
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOD_NASELENNOGO_PUNKTA_VKODE, index = true)
	@MetadataField(type=MetadataFieldType.INT,name="КодНаселенногоПунктаВКоде",description="Код населенного пункта в коде")
	public int kodNaselennogoPunktaVKode;
	
	/**
	 * Имя поля 'Код улицы в коде' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOD_ULICI_VKODE = "kodUliciVKode";

	/**
	 * Код улицы в коде
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOD_ULICI_VKODE, index = true)
	@MetadataField(type=MetadataFieldType.INT,name="КодУлицыВКоде",description="Код улицы в коде")
	public int kodUliciVKode;
	
	/**
	 * Имя поля 'Код' в таблице базы данных
	 */
	public static final String FIELD_NAME_KOD = "kod";

	/**
	 * Код
	 */	
	@DatabaseField(columnName = FIELD_NAME_KOD, index = true)
	@MetadataField(type=MetadataFieldType.BIGINT,name="Код",description="Код")
	public BigInteger kod;
	
	/**
	 * Имя поля 'Наименование' в таблице базы данных
	 */
	public static final String FIELD_NAME_NAIMENOVANIE = "naimenovanie";

	/**
	 * Наименование
	 */	
	@DatabaseField(columnName = FIELD_NAME_NAIMENOVANIE)
	@MetadataField(type=MetadataFieldType.STRING,name="Наименование",description="Наименование")
	public String naimenovanie;
	
	/**
	 * Имя поля 'Сокращение' в таблице базы данных
	 */
	public static final String FIELD_NAME_SOKRASCHENIE = "sokraschenie";

	/**
	 * Сокращение
	 */	
	@DatabaseField(columnName = FIELD_NAME_SOKRASCHENIE)
	@MetadataField(type=MetadataFieldType.STRING,name="Сокращение",description="Сокращение")
	public String sokraschenie;
	
	/**
	 * Имя поля 'Индекс' в таблице базы данных
	 */
	public static final String FIELD_NAME_INDEKS = "indeks";

	/**
	 * Индекс
	 */	
	@DatabaseField(columnName = FIELD_NAME_INDEKS)
	@MetadataField(type=MetadataFieldType.STRING,name="Индекс",description="Индекс")
	public String indeks;
	
	/**
	 * Имя поля 'Альтернативные названия' в таблице базы данных
	 */
	public static final String FIELD_NAME_ALTERNATIVNIE_NAZVANIYA = "alternativnieNazvaniya";

	/**
	 * Альтернативные названия
	 */	
	@DatabaseField(columnName = FIELD_NAME_ALTERNATIVNIE_NAZVANIYA)
	@MetadataField(type=MetadataFieldType.STRING,name="АльтернативныеНазвания",description="Альтернативные названия")
	public String alternativnieNazvaniya;
	
	@Override
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(tipAdresnogoElementa);
		sb.append(kodRegionaVKode);
		sb.append(kodRaionaVKode);
		sb.append(kodGorodaVKode);
		sb.append(kodNaselennogoPunktaVKode);
		sb.append(kodUliciVKode);
		sb.append(kod);

		return sb.toString();
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
