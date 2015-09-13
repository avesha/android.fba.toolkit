/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import java.sql.SQLException;

import ru.profi1c.engine.meta.CatalogDao;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы с элементами справочника 'Номенклатура' (создание, удаление, поиск)
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 *
 */
public class CatalogNomenklaturaDao extends CatalogDao<CatalogNomenklatura> {

	public CatalogNomenklaturaDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, CatalogNomenklatura.class);
	}

}