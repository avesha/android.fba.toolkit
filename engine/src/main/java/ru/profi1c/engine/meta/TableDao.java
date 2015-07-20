package ru.profi1c.engine.meta;

import android.text.TextUtils;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Менеджер для работы с произвольными коллекциями данных не имеющими объектного
 * соответствия в 1С (константы,регистры сведений, табличные части документов и
 * справочников,внешние таблицы). Операции: создание, удаление, поиск и т.п
 *
 * @param <T>
 */
public abstract class TableDao<T extends Table> extends RowDao<T> {

    protected TableDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public T newItem() {
        return super.newItem();
    }

    /*
     * Установить ключ записи, если пользователь не установил его самостоятельно
     */
    private void setRecordKeyIfEmpty(T value) {
        if (TextUtils.isEmpty(value.getRecordKey())) {
            value.setRecordKey(value.createRecordKey());
        }
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(T value) throws SQLException {
        setRecordKeyIfEmpty(value);
        return super.createOrUpdate(value);
    }

    @Override
    public T createIfNotExists(T value) throws SQLException {
        setRecordKeyIfEmpty(value);
        return super.createIfNotExists(value);
    }

    @Override
    public int create(T value) throws SQLException {
        setRecordKeyIfEmpty(value);
        return super.create(value);
    }

    @Override
    public int update(T value) throws SQLException {
        value.setRecordKey(value.createRecordKey());
        return super.update(value);
    }

}
