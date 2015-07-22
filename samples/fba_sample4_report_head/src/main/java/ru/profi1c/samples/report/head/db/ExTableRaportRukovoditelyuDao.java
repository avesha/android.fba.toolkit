/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.report.head.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import ru.profi1c.engine.meta.TableExDao;

/**
 * Менеджер для работы c записями таблицы 'РапортРуководителю' (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class ExTableRaportRukovoditelyuDao extends TableExDao<ExTableRaportRukovoditelyu> {

    public ExTableRaportRukovoditelyuDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ExTableRaportRukovoditelyu.class);
    }

}