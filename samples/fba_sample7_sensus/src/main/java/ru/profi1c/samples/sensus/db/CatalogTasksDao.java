/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

import android.util.Pair;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.util.DateHelper;

/**
 * Менеджер для работы с элементами справочника 'Задачи' (создание, удаление, поиск)
 * @author ООО "Сфера" (support@sfera.ru)
 *
 */
public class CatalogTasksDao extends CatalogDao<CatalogTasks> {

	public CatalogTasksDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, CatalogTasks.class);
	}

	public Pair<Integer, Integer> getCountTasksInfo() throws SQLException {
		Date now = new Date(System.currentTimeMillis());
		int countAll = 0;
		int countNow = 0;
		List<CatalogTasks> lstAll = select();
		countAll = lstAll.size();
		if (countAll > 0) {
			for (CatalogTasks task : lstAll) {
				if (DateHelper.diffDays(task.dateBegin, now) == 0) {
					countNow++;
				}
			}
		}
		return new Pair<>(countAll, countNow);
	}
}