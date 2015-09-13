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
 * Табличная часть 'Товары' документа 'Установка цен'
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 *
 */
@DatabaseTable(tableName=DocumentUstanovkaCenTPTovari.TABLE_NAME, daoClass = DocumentUstanovkaCenTPTovariDao.class) 
@MetadataObject(type=MetadataObject.TYPE_TABLE_PART, name=DocumentUstanovkaCenTPTovari.META_NAME)
public class DocumentUstanovkaCenTPTovari extends TablePart {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "DocumentUstanovkaCenTPTovari";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "Товары";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Владелец табличной части
	 */
	@DatabaseField(columnName=TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
			columnDefinition = "VARCHAR REFERENCES " +DocumentUstanovkaCen.TABLE_NAME+"("+Ref.FIELD_NAME_REF+") ON DELETE CASCADE") 
	protected DocumentUstanovkaCen owner;

	
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
	public Ref getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Ref ref) {
		owner = (DocumentUstanovkaCen) ref;
	}
	
	@Override
	public String getMetaName() {
		return META_NAME;
	}
}
