/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;

/**
 * Документ 'Внутренний заказ'
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
@DatabaseTable(tableName=DocumentVnutrenniiZakaz.TABLE_NAME, daoClass = DocumentVnutrenniiZakazDao.class) 
@MetadataObject(type=MetadataObject.TYPE_DOCUMENT, name=DocumentVnutrenniiZakaz.META_NAME)
public class DocumentVnutrenniiZakaz extends Document {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentVnutrenniiZakaz";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ВнутреннийЗаказ";
	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Вид заказа' в таблице базы данных
	 */
	public static final String FIELD_NAME_VID_ZAKAZA = "vidZakaza";

	/**
	 * Вид заказа
	 */	
	@DatabaseField(columnName = FIELD_NAME_VID_ZAKAZA)
	@MetadataField(type=MetadataFieldType.ENUM,name="ВидЗаказа",description="Вид заказа")
	public EnumVidiVnutrennegoZakaza vidZakaza;
	
	/**
	 * Имя поля 'Удалить время напоминания' в таблице базы данных
	 */
	public static final String FIELD_NAME_UDALIT_VREMYA_NAPOMINANIYA = "udalitVremyaNapominaniya";

	/**
	 * Удалить время напоминания
	 */	
	@DatabaseField(columnName = FIELD_NAME_UDALIT_VREMYA_NAPOMINANIYA, dataType = DataType.DATE_LONG)
	@MetadataField(type=MetadataFieldType.DATA,name="УдалитьВремяНапоминания",description="Удалить время напоминания")
	public Date udalitVremyaNapominaniya;
	
	/**
	 * Имя поля 'Дата отгрузки' в таблице базы данных
	 */
	public static final String FIELD_NAME_DATA_OTGRUZKI = "dataOtgruzki";

	/**
	 * Дата отгрузки
	 */	
	@DatabaseField(columnName = FIELD_NAME_DATA_OTGRUZKI, dataType = DataType.DATE_LONG)
	@MetadataField(type=MetadataFieldType.DATA,name="ДатаОтгрузки",description="Дата отгрузки")
	public Date dataOtgruzki;
	
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
	 * Имя поля 'Удалить напомнить о событии' в таблице базы данных
	 */
	public static final String FIELD_NAME_UDALIT_NAPOMNIT_OSOBITII = "udalitNapomnitOSobitii";

	/**
	 * Удалить напомнить о событии
	 */	
	@DatabaseField(columnName = FIELD_NAME_UDALIT_NAPOMNIT_OSOBITII)
	@MetadataField(type=MetadataFieldType.BOOL,name="УдалитьНапомнитьОСобытии",description="Удалить напомнить о событии")
	public boolean udalitNapomnitOSobitii;
	
	/**
	 * Имя поля 'Организация' в таблице базы данных
	 */
	public static final String FIELD_NAME_ORGANIZACIYA = "organizaciya";

	/**
	 * Организация
	 */	
	@DatabaseField(columnName = FIELD_NAME_ORGANIZACIYA, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Организация",description="Организация")
	public CatalogOrganizacii organizaciya;
	
	/**
	 * Имя поля 'Ответственный' в таблице базы данных
	 */
	public static final String FIELD_NAME_OTVETSTVENNII = "otvetstvennii";

	/**
	 * Ответственный
	 */	
	@DatabaseField(columnName = FIELD_NAME_OTVETSTVENNII, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Ответственный",description="Ответственный")
	public CatalogPolzovateli otvetstvennii;
	
	/**
	 * Имя поля 'Подразделение' в таблице базы данных
	 */
	public static final String FIELD_NAME_PODRAZDELENIE = "podrazdelenie";

	/**
	 * Подразделение
	 */	
	@DatabaseField(columnName = FIELD_NAME_PODRAZDELENIE, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Подразделение",description="Подразделение")
	public CatalogPodrazdeleniya podrazdelenie;
	
	/**
	 * Имя поля 'Исполнитель' в таблице базы данных
	 */
	public static final String FIELD_NAME_ISPOLNITEL = "ispolnitel";

	/**
	 * Исполнитель
	 */	
	@DatabaseField(columnName = FIELD_NAME_ISPOLNITEL, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Исполнитель",description="Исполнитель")
	public CatalogFizicheskieLica ispolnitel;
	
	/**
	 * Имя поля 'Подразделение исполнитель' в таблице базы данных
	 */
	public static final String FIELD_NAME_PODRAZDELENIE_ISPOLNITEL = "podrazdelenieIspolnitel";

	/**
	 * Подразделение исполнитель
	 */	
	@DatabaseField(columnName = FIELD_NAME_PODRAZDELENIE_ISPOLNITEL, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ПодразделениеИсполнитель",description="Подразделение исполнитель")
	public CatalogPodrazdeleniya podrazdelenieIspolnitel;
	
	/**
	 * Имя поля 'Документ основание' в таблице базы данных
	 */
	public static final String FIELD_NAME_DOKUMENT_OSNOVANIE = "dokumentOsnovanie";

	/**
	 * Документ основание
	 */	
	@DatabaseField(columnName = FIELD_NAME_DOKUMENT_OSNOVANIE, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="ДокументОснование",description="Документ основание")
	public DocumentSobitie dokumentOsnovanie;

	
	/**
	 * Табличная часть 'Товары'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="Товары",description="Товары")
	public ForeignCollection<DocumentVnutrenniiZakazTPTovari> tovari;
	
	/**
	 * Табличная часть 'Возвратная тара'
	 */
	@ForeignCollectionField(orderColumnName = TablePart.FIELD_NAME_LINE_NUMBER, eager = false)
	@MetadataField(type=MetadataFieldType.TABLE_PART,name="ВозвратнаяТара",description="Возвратная тара")
	public ForeignCollection<DocumentVnutrenniiZakazTPVozvratnayaTara> vozvratnayaTara;

	@Override
	public List<Class<? extends TablePart>> getTabularSections() {
		List<Class<? extends TablePart>> lstTabSections = new ArrayList<Class<? extends TablePart>>();
		lstTabSections.add(DocumentVnutrenniiZakazTPTovari.class);
		lstTabSections.add(DocumentVnutrenniiZakazTPVozvratnayaTara.class);
		return lstTabSections;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}

}
