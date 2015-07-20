package ru.profi1c.engine.meta;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Менеджер для работы с внешними таблицами. Операции: создание, удаление, поиск
 * и т.п
 *
 * @param <T>
 */
public class TableExDao<T extends TableEx> extends TableDao<T> {

    protected TableExDao(ConnectionSource connectionSource, Class<T> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

}
