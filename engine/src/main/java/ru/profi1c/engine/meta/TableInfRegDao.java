package ru.profi1c.engine.meta;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Менеджер для работы с записями регистров сведений (создание, удаление, поиск)
 */
public class TableInfRegDao<T extends TableInfReg> extends TableDao<T> {

    protected TableInfRegDao(ConnectionSource connectionSource, Class<T> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

}
