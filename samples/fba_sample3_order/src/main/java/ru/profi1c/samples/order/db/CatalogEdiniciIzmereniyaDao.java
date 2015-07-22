/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.sql.SQLException;

import ru.profi1c.engine.meta.CatalogDao;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы с элементами справочника 'Единицы измерения' (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class CatalogEdiniciIzmereniyaDao extends CatalogDao<CatalogEdiniciIzmereniya> {

    public CatalogEdiniciIzmereniyaDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CatalogEdiniciIzmereniya.class);
    }

}