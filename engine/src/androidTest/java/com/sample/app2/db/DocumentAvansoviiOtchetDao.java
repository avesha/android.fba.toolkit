/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import ru.profi1c.engine.meta.DocumentDao;

/**
 * Менеджер для работы c документами 'Авансовый отчет' (создание, удаление, поиск)
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
public class DocumentAvansoviiOtchetDao extends DocumentDao<DocumentAvansoviiOtchet> {

	public DocumentAvansoviiOtchetDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, DocumentAvansoviiOtchet.class);
	}

}