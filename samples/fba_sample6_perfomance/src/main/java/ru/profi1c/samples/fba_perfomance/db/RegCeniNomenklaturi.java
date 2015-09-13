/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import java.math.BigInteger;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableInfReg;
import ru.profi1c.engine.meta.TableInfRegPeriodic;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.meta.ValueStorage;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Периодический регистр сведений 'Цены номенклатуры'
 * @author ООО “Мобильные решения” (support@profi1c.ru)
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
	 * Имя поля 'Цена' в таблице базы данных
	 */
	public static final String FIELD_NAME_CENA = "cena";

	/**
	 * Цена
	 */	
	@DatabaseField(columnName = FIELD_NAME_CENA)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Цена",description="Цена")
	public double cena;
	
	@Override
	public String createRecordKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.createRecordKey());
		sb.append(nomenklatura);

		return sb.toString();
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
