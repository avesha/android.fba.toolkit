package ru.profi1c.engine.meta;

import android.database.Cursor;
import android.text.TextUtils;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.profi1c.engine.Dbg;

/**
 * Базовый менеджер доступа к данным
 */
public abstract class RowDao<T extends Row> extends BaseDaoImpl<T, String> {

    protected RowDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Создает новый объект
     *
     * @return
     */
    protected T newInstance() {
        T item = null;
        try {
            item = getDataClass().newInstance();
        } catch (InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return item;
    }

    /**
     * Создает новый элемент. Примечание: использование метода не приводит к
     * записи созданного объекта в базу данных. Для записи используйте метод
     * {@link #create(T)}
     *
     * @return
     */
    protected T newItem() {
        T item = newInstance();
        item.setModified(true);
        return item;
    }

    /**
     * Удалить записи. Очистить все записи или по условию, если указано условие
     * удаления записей (columnName!=null).
     *
     * @param columnName Имя колонки таблицы для условия
     * @param operator   Оператор сравнения, например ("=", "==", "<", "<=", ">", ">=",
     *                   "!=", "<>" т..п)
     * @param value      Значение сравнения
     * @throws SQLException
     */
    public void clearTable(String columnName, String operator, Object value) throws SQLException {
        DBOpenHelper.clearTable(this, columnName, operator, value);
    }

    /**
     * Выбрать измененные на мобильном клиенте строки
     *
     * @return
     * @throws SQLException
     */
    public List<T> selectChanged() throws SQLException {

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        where.eq(Row.FIELD_NAME_MODIFIED, true);
        return query(qb.prepare());
    }

    /**
     * Установить/снять признак модифицированности по списку элементов
     *
     * @param modified
     * @param lst
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void setModified(boolean modified, List<Object> lst) throws SQLException {
        for (Object obj : lst) {
            if (obj instanceof Row) {
                Row row = (Row) obj;
                row.setModified(modified);
                update((T) row);
            }
        }
    }

    /**
     * Сервисная функция, позволяет установить / снять признак
     * модифицированности для всех элементов. Важно! При создании новых
     * элементов через {@link #newItem()} признак модифицированности
     * выставляется автоматически. При изменении элемента вы должны позаботится
     * о выставлении этого флага самостоятельно, чтобы объект передавался на
     * сервер 1С в автоматическом режиме.
     *
     * @param modified признак модификации элемента
     * @throws SQLException
     */
    public void setModified(boolean modified) throws SQLException {
        setModified(modified, new HashMap<String, Object>(0));
    }

    /**
     * Сервисная функция, позволяет установить / снять признак
     * модифицированности по условию
     *
     * @param modified признак модификации элемента
     * @param filter   'структура' отбора в виде коллекции, где 'String' имя колонки
     *                 в таблице базы данных для этого реквизита (поля класса), как
     *                 правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                 реквизита
     * @throws SQLException
     */
    public void setModified(boolean modified, HashMap<String, Object> filter) throws SQLException {
        updateColumnValue(Row.FIELD_NAME_MODIFIED, modified, filter);
    }

    /**
     * Выполнить инструкцию sql – update, обновление значения одной колонки по
     * условию
     *
     * @param columnName имя колонки
     * @param value      новое значение
     * @param filter     условия обновления
     * @throws SQLException
     */
    protected void updateColumnValue(String columnName, Object value,
            HashMap<String, Object> filter) throws SQLException {

        UpdateBuilder<T, String> ub = updateBuilder();
        ub.updateColumnValue(columnName, value);

        if (filter != null && filter.size() > 0) {
            Where<T, String> where = ub.where();
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                Object fieldValue = entry.getValue();
                where.eq(entry.getKey(), fieldValue);
            }
            where.and(filter.size());
        }

        update(ub.prepare());
    }

    /**
     * Формирует выборку всех записей
     *
     * @return
     * @throws SQLException
     */
    public List<T> select() throws SQLException {
        return select(null, null);
    }

    /**
     * Формирует выборку записей c отбором
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @return
     * @throws SQLException
     */
    public List<T> select(HashMap<String, Object> filter) throws SQLException {
        return select(filter, null);
    }

    /**
     * Формирует выборку записей (с фильтрацией и сортировкой)
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @param order  строка с именем колонки в таблице базы данных (реквизита
     *               справочника), определяющая упорядочивание элементов в выборке.
     *               После имени реквизита через пробел может быть указано
     *               направление сортировки. "Desc" - упорядочивать по убыванию,
     *               "Asc" - упорядочивать по возрастанию.
     * @return
     * @throws SQLException
     */
    public List<T> select(HashMap<String, Object> filter, String order) throws SQLException {

        if (filter != null && filter.size() > 0) {
            return queryForFieldValues(filter, order);
        } else if (TextUtils.isEmpty(order)) {
            return queryForAll();
        } else {
            QueryBuilder<T, String> qb = queryBuilder();
            qb.orderByRaw(order);
            return query(qb.prepare());
        }

    }

    /**
     * Формирует курсор на выборку всех записей
     *
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor() throws SQLException {
        return selectCursor(null, null);
    }

    /**
     * Формирует курсор на выборку элементов (с фильтрацией)
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor(HashMap<String, Object> filter) throws SQLException {
        return selectCursor(filter, null);
    }

    /**
     * Формирует выборку записей (с фильтрацией и сортировкой)
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @param order  строка с именем колонки в таблице базы данных (реквизита
     *               справочника), определяющая упорядочивание элементов в выборке.
     *               После имени реквизита через пробел может быть указано
     *               направление сортировки. "Desc" - упорядочивать по убыванию,
     *               "Asc" - упорядочивать по возрастанию.
     * @return курсор по заданным параметрам отбора или null
     * @throws SQLException
     */
    public Cursor selectCursor(HashMap<String, Object> filter, String order) throws SQLException {

        Cursor cursor = null;

        if (filter != null && filter.size() > 0) {
            return cursorForFieldValues(filter, order);
        } else if (TextUtils.isEmpty(order)) {
            // all rows
            cursor = cursorForQuery(null);
        } else {
            QueryBuilder<T, String> qb = queryBuilder();
            qb.orderByRaw(order);
            cursor = cursorForQuery(qb);
        }

        return cursor;
    }

    /*
     * Построитель запроса по коллекции отбора и установка фильтра, если
     * требуется
     */
    protected QueryBuilder<T, String> makeQueryBuilderOnMap(Map<String, Object> fieldValues,
            String order) throws SQLException {

        // основа из BaseDaoImpl
        checkForInitialized();
        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            Object fieldValue = entry.getValue();
            where.eq(entry.getKey(), fieldValue);
        }

        where.and(fieldValues.size());

        if (!TextUtils.isEmpty(order)) {
            qb.orderByRaw(order);
        }

        return qb;
    }

    /**
     * Создать курсор по параметрам запроса
     *
     * @param qb
     * @return
     * @throws SQLException
     */
    protected Cursor cursorForQuery(QueryBuilder<T, String> qb) throws SQLException {

        CloseableIterator<T> iterator = null;
        if (qb != null) {
            iterator = iterator(qb.prepare());
        } else {
            // all
            iterator = iterator();
        }

        AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
        return results.getRawCursor();
    }

    /**
     * Запрос по коллекции полей с указанием сортировки (есть есть)
     *
     * @param fieldValues
     * @param order
     * @return
     * @throws SQLException
     */
    protected List<T> queryForFieldValues(Map<String, Object> fieldValues, String order)
            throws SQLException {

        if (fieldValues.size() == 0) {
            return Collections.emptyList();
        } else {
            QueryBuilder<T, String> qb = makeQueryBuilderOnMap(fieldValues, order);
            return qb.query();
        }

    }

    /**
     * Курсор по коллекции полей с указаним сортировки (если есть)
     *
     * @param fieldValues
     * @param order
     * @return
     * @throws SQLException
     */
    protected Cursor cursorForFieldValues(Map<String, Object> fieldValues, String order)
            throws SQLException {

        Cursor cursor = null;
        if (fieldValues.size() > 0) {
            QueryBuilder<T, String> qb = makeQueryBuilderOnMap(fieldValues, order);
            cursor = cursorForQuery(qb);
        }
        return cursor;
    }
}
