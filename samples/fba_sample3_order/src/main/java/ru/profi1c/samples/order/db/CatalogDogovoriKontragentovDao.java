/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.sql.SQLException;

import ru.profi1c.engine.meta.CatalogDao;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы с элементами справочника 'Договоры контрагентов' (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class CatalogDogovoriKontragentovDao extends CatalogDao<CatalogDogovoriKontragentov> {

    public CatalogDogovoriKontragentovDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CatalogDogovoriKontragentov.class);
    }

    /**
     * Получить последний договор (по дате создания)
     *
     * @param owner контрагент владелец
     * @return
     */
    public CatalogDogovoriKontragentov getLast(CatalogKontragenti owner) {
        CatalogDogovoriKontragentov dogovor = null;

        QueryBuilder<CatalogDogovoriKontragentov, String> builder = queryBuilder();
        try {
            builder.where().eq(CatalogDogovoriKontragentov.FIELD_NAME_OWNER, owner);
            builder.orderBy(CatalogDogovoriKontragentov.FIELD_NAME_DATA, false);
            builder.limit(1L);

            dogovor = queryForFirst(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dogovor;

    }
}