/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import ru.profi1c.engine.meta.TableInfRegPeriodicDao;

/**
 * Менеджер для работы c записями периодического регистра сведений 'Курсы валют' (создание, удаление, поиск)
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
public class RegKursiValyutDao extends TableInfRegPeriodicDao<RegKursiValyut> {

	public RegKursiValyutDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, RegKursiValyut.class);
	}

}