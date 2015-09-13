/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

import android.graphics.Bitmap;

import com.j256.ormlite.support.ConnectionSource;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;

import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.meta.ValueStorage;
import ru.profi1c.samples.sensus.Const;

/**
 * Менеджер для работы с элементами справочника 'Хранилище дополнительной информации' (создание, удаление, поиск)
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
public class CatalogExtraStorageDao extends CatalogDao<CatalogExtraStorage> {

	public CatalogExtraStorageDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, CatalogExtraStorage.class);
	}

	public CatalogExtraStorage savePhotoToStorage(CatalogSalesPoints point, Bitmap bmp) throws SQLException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Const.PHOTO_COMPRESS_FORMAT, Const.PHOTO_COMPRESS_QUALITY, stream);
		byte[] byteArray = stream.toByteArray();

		CatalogExtraStorage extStorage = newItem();
		extStorage.object = point;
		extStorage.storage = new ValueStorage(byteArray);
		create(extStorage);
		return extStorage;
	}

}