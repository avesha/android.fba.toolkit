package ru.profi1c.engine.meta;

import android.database.Cursor;
import android.text.TextUtils;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер для работы с документами (создание, удаление, поиск)
 *
 * @param <T>
 */
public abstract class DocumentDao<T extends Document> extends RefDao<T> {

    protected DocumentDao(ConnectionSource connectionSource, Class<T> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Осуществляет поиск документа по номеру.
     *
     * @param number номер искомого документа.
     * @return ссылка на найденный документ
     * @throws SQLException
     */
    public T findByNumber(String number) throws SQLException {
        return findByNumber(number, null, null);
    }

    /**
     * Осуществляет поиск документа по номеру в указанном диапазоне
     *
     * @param number  номер искомого документа.
     * @param dtBegin начало интервала
     * @param dtEnd   окончание интервала
     * @return ссылка на найденный документ
     * @throws SQLException
     */
    public T findByNumber(String number, Date dtBegin, Date dtEnd) throws SQLException {

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        where.eq(Document.FIELD_NAME_NUMBER, number);
        if (dtBegin != null && dtEnd != null) {
            where.and();
            where.between(Document.FIELD_NAME_DATE, dtBegin, dtEnd);
        }
        return queryForFirst(qb.prepare());
    }

    /**
     * Осуществляет поиск документа по реквизиту.
     *
     * @param columnName имя колонки в таблице базы данных для этого реквизита (поля
     *                   класса), как правило одна из констант 'FIELD_NAME_'
     * @param value      Значение реквизита, по которому должен выполняться поиск.
     * @return ссылка на найденный документ
     * @throws SQLException
     */
    public T findByAttribute(String columnName, Object value) throws SQLException {
        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();
        where.eq(columnName, value);
        return queryForFirst(qb.prepare());
    }

    /**
     * Формирует выборку всех документов
     *
     * @return
     * @throws SQLException
     */
    public List<T> select() throws SQLException {
        return select(null, null, null, null);
    }

    /**
     * Формирует выборку документов за определенный период
     *
     * @param dtBegin начало интервала
     * @param dtEnd   окончание интервала
     * @return
     * @throws SQLException
     */
    public List<T> select(Date dtBegin, Date dtEnd) throws SQLException {
        return select(dtBegin, dtEnd, null, null);
    }

    /**
     * Формирует выборку документов за определенный период c отбором
     *
     * @param dtBegin начало интервала
     * @param dtEnd   окончание интервала
     * @param filter  'структура' отбора в виде коллекции, где 'String' имя колонки
     *                в таблице базы данных для этого реквизита (поля класса), как
     *                правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                реквизита
     * @return
     * @throws SQLException
     */
    public List<T> select(Date dtBegin, Date dtEnd, HashMap<String, Object> filter)
            throws SQLException {
        return select(dtBegin, dtEnd, filter, null);
    }

    /*
     * Построитель запроса по коллекции отбора и установка фильтра, если
     * требуется
     */
    protected QueryBuilder<T, String> makeQueryBuilderOnSelect(Date dtBegin, Date dtEnd,
            HashMap<String, Object> filter, String order) throws SQLException {

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        // поставим фиктивное условие (верное всегда) т.к иначе будет ошибка
        // если указана
        // только сортировка без отбора но был qb.where();
        where.isNotNull(Document.FIELD_NAME_REF);

        if (dtBegin != null && dtEnd != null) {
            where.and();
            where.between(Document.FIELD_NAME_DATE, dtBegin, dtEnd);
        }

        if (filter != null) {
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                Object fieldValue = entry.getValue();
                where.and();
                where.eq(entry.getKey(), fieldValue);
            }
        }

        if (!TextUtils.isEmpty(order)) {
            qb.orderByRaw(order);
        }

        return qb;
    }

    /**
     * Формирует выборку документов за определенный период
     *
     * @param dtBegin начало интервала
     * @param dtEnd   окончание интервала
     * @param filter  'структура' отбора в виде коллекции, где 'String' имя колонки
     *                в таблице базы данных для этого реквизита (поля класса), как
     *                правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                реквизита
     * @param order   строка с именем колонки в таблице базы данных (реквизита
     *                справочника), определяющая упорядочивание элементов в выборке.
     *                После имени реквизита через пробел может быть указано
     *                направление сортировки. "Desc" - упорядочивать по убыванию,
     *                "Asc" - упорядочивать по возрастанию.
     * @return список документов
     * @throws SQLException
     */
    public List<T> select(Date dtBegin, Date dtEnd, HashMap<String, Object> filter, String order)
            throws SQLException {

        if ((dtBegin != null && dtEnd != null) || filter != null || !TextUtils.isEmpty(order)) {
            QueryBuilder<T, String> qb = makeQueryBuilderOnSelect(dtBegin, dtEnd, filter, order);
            return query(qb.prepare());
        } else
            return queryForAll();
    }

    /**
     * Формирует курсор на выборку всех документов
     *
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor() throws SQLException {
        return selectCursor(null, null, null, null);
    }

    /**
     * Формирует курсор на выборку документов за определенный период
     *
     * @param dtBegin начало интервала
     * @param dtEnd   окончание интервала
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor(Date dtBegin, Date dtEnd) throws SQLException {
        return selectCursor(dtBegin, dtEnd, null, null);
    }

    /**
     * Формирует курсор на выборку документов за определенный период c отбором
     *
     * @param dtBegin начало интервала
     * @param dtEnd   окончание интервала
     * @param filter  'структура' отбора в виде коллекции, где 'String' имя колонки
     *                в таблице базы данных для этого реквизита (поля класса), как
     *                правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                реквизита
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor(Date dtBegin, Date dtEnd, HashMap<String, Object> filter)
            throws SQLException {
        return selectCursor(dtBegin, dtEnd, filter, null);
    }

    /**
     * Формирует курсор на выборку документов за определенный период
     *
     * @param dtBegin начало интервала
     * @param dtEnd   окончание интервала
     * @param filter  'структура' отбора в виде коллекции, где 'String' имя колонки
     *                в таблице базы данных для этого реквизита (поля класса), как
     *                правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                реквизита
     * @param order   строка с именем колонки в таблице базы данных (реквизита
     *                справочника), определяющая упорядочивание элементов в выборке.
     *                После имени реквизита через пробел может быть указано
     *                направление сортировки. "Desc" - упорядочивать по убыванию,
     *                "Asc" - упорядочивать по возрастанию.
     * @return список документов
     * @throws SQLException
     */
    public Cursor selectCursor(Date dtBegin, Date dtEnd, HashMap<String, Object> filter,
            String order) throws SQLException {

        if ((dtBegin != null && dtEnd != null) || filter != null || !TextUtils.isEmpty(order)) {
            QueryBuilder<T, String> qb = makeQueryBuilderOnSelect(dtBegin, dtEnd, filter, order);
            return cursorForQuery(qb);
        } else
            // all rows
            return cursorForQuery(null);
    }

    /**
     * Получить длину номера документа. Рассчет возможен если в таблице есть
     * хоть один документ.
     *
     * @return длину номера документа или 0
     * @throws SQLException
     */
    public int getNumberLength() throws SQLException {
        return getNumberLength(null, Document.FIELD_NAME_NUMBER);
    }

    /**
     * Получить длину номера документа c учетом префикса кода. Рассчет возможен если в таблице есть
     * хоть один документ.
     *
     * @param prefix префикс номера документа
     * @return длину номера документа или 0
     * @throws SQLException
     */
    public int getNumberLength(String prefix) throws SQLException {
        return getNumberLength(prefix, Document.FIELD_NAME_NUMBER);
    }

    /**
     * Получить новый номер для нумерации элементов. Если в таблице присутствуют
     * элементы с префиксами рекомендуется использовать функцию
     * {@link #getNextNumerator(prefix)}
     *
     * @return
     * @throws SQLException
     */
    public int getNextNumber() throws SQLException {
        return getNextNumber(null, Document.FIELD_NAME_NUMBER);
    }

    /**
     * Получить следующий номер для нумерации документа
     *
     * @param prefix строковое значение префикса
     * @return
     * @throws SQLException
     */
    public int getNextNumber(String prefix) throws SQLException {
        return getNextNumber(prefix, Document.FIELD_NAME_NUMBER);
    }

}
