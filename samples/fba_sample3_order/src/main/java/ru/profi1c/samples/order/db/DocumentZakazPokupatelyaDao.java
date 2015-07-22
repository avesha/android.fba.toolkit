/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.sql.SQLException;

import ru.profi1c.engine.meta.DocumentDao;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы c документами 'Заказ покупателя' (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class DocumentZakazPokupatelyaDao extends DocumentDao<DocumentZakazPokupatelya> {

    public DocumentZakazPokupatelyaDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, DocumentZakazPokupatelya.class);
    }

}