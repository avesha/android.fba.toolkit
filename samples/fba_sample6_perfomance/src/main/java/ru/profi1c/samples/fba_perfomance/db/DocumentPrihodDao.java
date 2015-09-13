/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import java.sql.SQLException;

import ru.profi1c.engine.meta.DocumentDao;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы c документами 'Приход' (создание, удаление, поиск)
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 *
 */
public class DocumentPrihodDao extends DocumentDao<DocumentPrihod> {

	public DocumentPrihodDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, DocumentPrihod.class);
	}

}