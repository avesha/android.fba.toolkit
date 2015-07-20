/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import ru.profi1c.engine.meta.ConstDao;

/**
 * Менеджер для работы с константами. Для манипуляции данными рекомендуется
 * использовать только методы 'read' и 'save' для чтения и сохранения изменений.
 * 
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 * 
 */
public final class ConstantsDao extends ConstDao<Constants> {

	public ConstantsDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, Constants.class);
	}

}
