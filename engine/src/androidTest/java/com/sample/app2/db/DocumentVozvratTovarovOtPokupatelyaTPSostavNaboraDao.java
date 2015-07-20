/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import ru.profi1c.engine.meta.TablePartDao;

/**
 * Менеджер для работы со строками табличной части 'Состав набора' документа 'Возврат товаров от покупателя'
 * (создание, удаление, поиск)
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 * 
 */
public class DocumentVozvratTovarovOtPokupatelyaTPSostavNaboraDao extends TablePartDao<DocumentVozvratTovarovOtPokupatelyaTPSostavNabora> {

	public DocumentVozvratTovarovOtPokupatelyaTPSostavNaboraDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, DocumentVozvratTovarovOtPokupatelyaTPSostavNabora.class);
	}

}
