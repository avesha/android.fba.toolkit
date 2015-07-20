/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tasks.db;

import java.sql.SQLException;

import ru.profi1c.engine.meta.TableExDao;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы c записями таблицы 'Задачи' (создание, удаление, поиск)
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
public class ExTableTasksDao extends TableExDao<ExTableTasks> {

	public ExTableTasksDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, ExTableTasks.class);
	}

}