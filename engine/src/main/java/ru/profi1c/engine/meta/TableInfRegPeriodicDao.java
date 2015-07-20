package ru.profi1c.engine.meta;

import android.text.TextUtils;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.profi1c.engine.Dbg;

/**
 * Менеджер для работы с записями периодических регистров сведений (создание,
 * удаление, поиск). Содержит дополнительные методы для отбора по периоду
 * (аналоги 'срез первых' , 'срез последних')
 *
 * @param <T>
 */
public class TableInfRegPeriodicDao<T extends TableInfRegPeriodic> extends TableInfRegDao<T> {
    private static final String TAG = TableInfRegPeriodicDao.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final String MIN = "MIN";
    private static final String MAX = "MAX";
    private static final String GREATER_OR_EQUAL = ">=";
    private static final String LESS_OR_EQUAL = "<=";

    protected TableInfRegPeriodicDao(ConnectionSource connectionSource, Class<T> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Формирует выборку записей регистра сведений за указанный период
     *
     * @param dtBegin начало интервала, за который будут выдаваться записи
     *                периодического регистра сведений.
     * @param dtEnd   конец интервала, за который будут выдаваться записи
     *                периодического регистра сведений
     * @return
     * @throws SQLException
     */
    public List<T> select(Date dtBegin, Date dtEnd) throws SQLException {
        return select(dtBegin, dtEnd, null, null);
    }

    /**
     * Формирует выборку записей регистра сведений за указанный период (с
     * отбором и фильтрацией)
     *
     * @param dtBegin начало интервала, за который будут выдаваться записи
     *                периодического регистра сведений.
     * @param dtEnd   конец интервала, за который будут выдаваться записи
     *                периодического регистра сведений
     * @param filter  'структура' отбора в виде коллекции, где 'String' имя колонки
     *                в таблице базы данных для этого реквизита (поля класса), как
     *                правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                реквизита
     * @param order   строка с именем колонки в таблице базы данных (реквизита
     *                справочника), определяющая упорядочивание элементов в выборке.
     *                После имени реквизита через пробел может быть указано
     *                направление сортировки. "Desc" - упорядочивать по убыванию,
     *                "Asc" - упорядочивать по возрастанию.
     * @return
     * @throws SQLException
     */
    public List<T> select(Date dtBegin, Date dtEnd, HashMap<String, Object> filter, String order)
            throws SQLException {

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        // поставим фиктивное условие (верное всегда) т.к будет ошибка если
        // указана
        // только сортировка без отбора но был qb.where();
        where.isNotNull(TableInfRegPeriodic.FIELD_NAME_MODIFIED);

        if (dtBegin != null && dtEnd != null) {
            where.and();
            where.between(TableInfRegPeriodic.FIELD_NAME_PERIOD, dtBegin, dtEnd);
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

        if ((dtBegin != null && dtEnd != null) || filter != null || !TextUtils.isEmpty(order)) {
            return query(qb.prepare());
        } else {
            return queryForAll();
        }
    }

    /**
     * Получает значения ресурсов наиболее ранней записи регистра,
     * соответствующей указанным периоду и значениям измерений регистра. Поиск
     * по периоду осуществляется "включительно", т.е. если существует запись с
     * таким же значением одноименного свойства, то она и будет найдена.
     *
     * @param dtBegin определяет момент времени, начиная с которого необходимо
     *                получить значения ресурсов
     * @param filter  'структура' отбора в виде коллекции измерений регистра, где
     *                'String' имя колонки в таблице базы данных для этого реквизита
     *                (поля класса), как правило одна из констант 'FIELD_NAME_', а
     *                'Object' значение реквизита
     * @return найденная строка регистра
     * @throws SQLException
     */
    public T getFirst(Date dtBegin, HashMap<String, Object> filter) throws SQLException {
        if (dtBegin != null && filter != null) {
            return getFirstLast(dtBegin, filter, MIN, GREATER_OR_EQUAL);
        }
        return null;
    }

    /**
     * Получает значения ресурсов наиболее поздней записи регистра,
     * соответствующей указанным периоду и значениям измерений регистра.
     *
     * @param dtEnd  определяет момент времени, по который необходимо получить
     *               значения ресурсов
     * @param filter 'структура' отбора в виде коллекции измерений регистра, где
     *               'String' имя колонки в таблице базы данных для этого реквизита
     *               (поля класса), как правило одна из констант 'FIELD_NAME_', а
     *               'Object' значение реквизита
     * @return найденная строка регистра
     * @throws SQLException
     */
    public T getLast(Date dtEnd, HashMap<String, Object> filter) throws SQLException {
        if (dtEnd != null && filter != null) {
            return getFirstLast(dtEnd, filter, MAX, LESS_OR_EQUAL);
        }
        return null;
    }

    private T getFirstLast(Date date, HashMap<String, Object> filter, String func,
            String compareOperand) throws SQLException {

		/*
         * sample: SELECT t1.RecordKey FROM RegKursiValyut t1 INNER JOIN (SELECT
		 * MAX(period) AS period, valyuta FROM RegKursiValyut WHERE period
		 * <=1171659600000 and valyuta = "bd72d8fa-55bc-11d9-848a-00112f43529a"
		 * GROUP BY valyuta) AS t2 ON t1.period = t2.period and t1.valyuta =
		 * t2.valyuta
		 */

        String tableName = getTableInfo().getTableName();
        long msec = date.getTime();
        Set<String> columns = filter.keySet();

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT t1.").append(TableInfRegPeriodic.FIELD_NAME_ID).append(" FROM ")
          .append(tableName).append(" t1\n");
        sb.append(" INNER JOIN \n");
        sb.append(" (SELECT ").append(func).append("(")
          .append(TableInfRegPeriodic.FIELD_NAME_PERIOD).append(") AS ")
          .append(TableInfRegPeriodic.FIELD_NAME_PERIOD);
        // поля
        for (String s : columns) {
            sb.append(" , ").append(s);
        }

        sb.append(" FROM ").append(tableName).append(" WHERE ")
          .append(TableInfRegPeriodic.FIELD_NAME_PERIOD).append(" " + compareOperand).append(msec);
        // where поля
        for (String s : columns) {
            sb.append(" and ").append(s).append(" = ").append(valueToSqlString(filter.get(s)));
        }

        sb.append(" GROUP BY ");
        // group by поля
        Object[] arr = columns.toArray();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i != arr.length - 1) {
                sb.append(",");
            }
        }

        sb.append(" ) AS t2\n");
        sb.append("  ON t1.").append(TableInfRegPeriodic.FIELD_NAME_PERIOD).append(" = t2.")
          .append(TableInfRegPeriodic.FIELD_NAME_PERIOD);
        // join
        for (String s : columns) {
            sb.append(" and ").append("t1.").append(s).append(" = t2.").append(s);
        }

        String rawQuery = sb.toString();
        if (DEBUG) {
            Dbg.d(TAG, "getFirstLast query: " + rawQuery);
        }

        GenericRawResults<String[]> rawResults = queryRaw(rawQuery);
        List<String[]> results = rawResults.getResults();
        if (results.size() > 0) {
            String strRef = results.get(0)[0];
            return queryForId(strRef);
        }

        return null;
    }

    /*
     * Значение параметра привести к строке
     */
    private String valueToSqlString(Object value) {
        if (value instanceof Ref) {
            Ref ref = (Ref) value;
            if(Ref.isEmpty(ref)){
                return DBUtils.quoteValue(Ref.EMPTY_REF);
            } else {
                return DBUtils.quoteValue(ref.getRef().toString());
            }
        } else if (value instanceof String) {
            return DBUtils.quoteValue((String) value);
        } else if (value instanceof Date) {
            return String.valueOf(((Date) value).getTime());
        } else if (value instanceof Enum) {
            return DBUtils.quoteValue(value.toString());
        } else {
            return value.toString();
        }
    }

}
