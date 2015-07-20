/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import ru.profi1c.engine.meta.TablePartDao;

/**
 * Менеджер для работы со строками табличной части 'Товары' документа 'Внутренний заказ'
 * (создание, удаление, поиск)
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 * 
 */
public class DocumentVnutrenniiZakazTPTovariDao extends TablePartDao<DocumentVnutrenniiZakazTPTovari> {

	public DocumentVnutrenniiZakazTPTovariDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, DocumentVnutrenniiZakazTPTovari.class);
	}

}
