/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

import ru.profi1c.engine.meta.TablePartDao;

/**
 * Менеджер для работы со строками табличной части 'Товары' документа 'Заказ покупателя'
 * (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class DocumentZakazPokupatelyaTPTovariDao
        extends TablePartDao<DocumentZakazPokupatelyaTPTovari> {

    public DocumentZakazPokupatelyaTPTovariDao(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, DocumentZakazPokupatelyaTPTovari.class);
    }

}
