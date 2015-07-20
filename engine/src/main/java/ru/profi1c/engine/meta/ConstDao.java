package ru.profi1c.engine.meta;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Менеджер для работы с константами. Все константы считываются и сохраняются в
 * базу данных одновременно. Использовать константы следует только в
 * исключительных случаях
 *
 * @param <T>
 */
public class ConstDao<T extends ConstTable> extends TableDao<T> {

    private T constants;

    protected ConstDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Прочитать набор констант
     *
     * @return
     * @throws SQLException
     */

    public T read() throws SQLException {
        constants = queryForId(ConstTable.CONST_RECORD_KEY);
        return constants;
    }

    /**
     * Сохранить набор констант
     *
     * @throws SQLException
     */
    public void save() throws SQLException {
        if (constants == null)
            throw new IllegalStateException("First need call the 'read' method!");

        constants.setModified(true);
        super.createOrUpdate(constants);
    }

    @Override
    public T newItem() {
        throw new IllegalStateException("Use 'read' method!");
    }

}
