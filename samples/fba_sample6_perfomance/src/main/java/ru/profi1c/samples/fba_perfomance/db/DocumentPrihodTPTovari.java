/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import java.math.BigInteger;
import java.util.Date;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.meta.ValueStorage;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Табличная часть 'Товары' документа 'Приход'
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 *
 */
@DatabaseTable(tableName=DocumentPrihodTPTovari.TABLE_NAME, daoClass = DocumentPrihodTPTovariDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentPrihodTPTovari.META_NAME)
public class DocumentPrihodTPTovari extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentPrihodTPTovari";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Товары";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentPrihod.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentPrihod owner;

	
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

	@Override
	public Ref getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Ref ref) {
		owner = (DocumentPrihod) ref;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
