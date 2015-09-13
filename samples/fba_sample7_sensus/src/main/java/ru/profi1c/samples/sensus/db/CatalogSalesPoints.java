/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.util.MediaHelper;
import ru.profi1c.samples.sensus.Const;

/**
 * Справочник 'Торговые точки'
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
@DatabaseTable(tableName=CatalogSalesPoints.TABLE_NAME, daoClass = CatalogSalesPointsDao.class) 
@MetadataObject(type=MetadataObject.TYPE_CATALOG, name=CatalogSalesPoints.META_NAME)
public class CatalogSalesPoints extends Catalog {
	
	/**
	 * Имя таблицы в базе данных
	 */
	public static final String TABLE_NAME = "CatalogSalesPoints";
	
	/**
	 * Имя метаданных объекта в 1С (не изменять)
	 */
	public static final String META_NAME = "ТорговыеТочки";
		
	private static final long serialVersionUID = 1L;

	
	/**
	 * Имя поля 'Широта' в таблице базы данных
	 */
	public static final String FIELD_NAME_LAT = "lat";

	/**
	 * Широта
	 */	
	@DatabaseField(columnName = FIELD_NAME_LAT)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Широта",description="Широта")
	public double lat;
	
	/**
	 * Имя поля 'Долгота' в таблице базы данных
	 */
	public static final String FIELD_NAME_LNG = "lng";

	/**
	 * Долгота
	 */	
	@DatabaseField(columnName = FIELD_NAME_LNG)
	@MetadataField(type=MetadataFieldType.DOUBLE,name="Долгота",description="Долгота")
	public double lng;
	
	/**
	 * Имя поля 'Адрес' в таблице базы данных
	 */
	public static final String FIELD_NAME_ADDRESS = "address";

	/**
	 * Адрес
	 */	
	@DatabaseField(columnName = FIELD_NAME_ADDRESS)
	@MetadataField(type=MetadataFieldType.STRING,name="Адрес",description="Адрес")
	public String address;
	
	/**
	 * Имя поля 'Сайт' в таблице базы данных
	 */
	public static final String FIELD_NAME_SITE = "site";

	/**
	 * Сайт
	 */	
	@DatabaseField(columnName = FIELD_NAME_SITE)
	@MetadataField(type=MetadataFieldType.STRING,name="Сайт",description="Сайт")
	public String site;
	
	/**
	 * Имя поля 'Телефон' в таблице базы данных
	 */
	public static final String FIELD_NAME_PHONE = "phone";

	/**
	 * Телефон
	 */	
	@DatabaseField(columnName = FIELD_NAME_PHONE)
	@MetadataField(type=MetadataFieldType.STRING,name="Телефон",description="Телефон")
	public String phone;
	
	/**
	 * Имя поля 'Фото' в таблице базы данных
	 */
	public static final String FIELD_NAME_FOTO = "foto";

	/**
	 * Фото
	 */	
	@DatabaseField(columnName = FIELD_NAME_FOTO, foreign = true)
	@MetadataField(type=MetadataFieldType.REF,name="Фото",description="Фото")
	public CatalogExtraStorage foto;
	
	/**
	 * Имя поля 'Комментарий' в таблице базы данных
	 */
	public static final String FIELD_NAME_COMMENT = "comment";

	/**
	 * Комментарий
	 */	
	@DatabaseField(columnName = FIELD_NAME_COMMENT)
	@MetadataField(type=MetadataFieldType.STRING,name="Комментарий",description="Комментарий")
	public String comment;
	
	/**
	 * Имя поля 'Статус' в таблице базы данных
	 */
	public static final String FIELD_NAME_STATUS = "status";

	/**
	 * Статус
	 */	
	@DatabaseField(columnName = FIELD_NAME_STATUS)
	@MetadataField(type=MetadataFieldType.ENUM,name="Статус",description="Статус")
	public EnumSalesPointStatuses status;

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

	/**
	 * Адрес и дополнительную контактную информацию по торговой точке для отображения на карте
	 */
	public String getAddress() {
		StringBuilder sb = new StringBuilder(address);
		if (!TextUtils.isEmpty(phone)) {
			sb.append("\n").append(phone);
		}
		if (!TextUtils.isEmpty(site)) {
			sb.append("\n").append(site);
		}
		return sb.toString();
	}

	/**
	 * Возвращает имя файла для фото эскиза при отображении на карте
	 */
	public String getThumbName() {
		return getRef().toString() + ".thumb";
	}

	public void createThumb(File dir) {
		if (!CatalogExtraStorage.isEmpty(foto)) {
			Bitmap bmp = foto.storage.toBitmap();
			if (bmp != null) {
				Bitmap bmpScaled = MediaHelper.scaleBitmap(bmp, Const.THUMB_MAX_WIDTH);
				if (bmpScaled != null) {
					File file = new File(dir, getThumbName());

					try {
						MediaHelper.saveImageToFile(bmpScaled, file.getAbsolutePath(),
													Const.THUMB_COMPRESS_FORMAT,
													Const.THUMB_COMPRESS_QUALITY);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				bmp.recycle();
			}
		}
	}
}
