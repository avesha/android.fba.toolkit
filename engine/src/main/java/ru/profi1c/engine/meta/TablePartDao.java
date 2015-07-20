package ru.profi1c.engine.meta;

import android.text.TextUtils;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.Dbg;

/**
 * Менеджер для работы со строками табличной части справочника, документа
 * (создание, удаление, поиск)
 *
 * @param <T>
 */
public abstract class TablePartDao<T extends TablePart> extends TableDao<T> {

    protected TablePartDao(ConnectionSource connectionSource, Class<T> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Создать новую строку табличной части.
     *
     * @param owner      ссылка на владельца табличной части
     * @param lineNumber номер строки табличной части
     * @return
     */
    public T newItem(Ref owner, int lineNumber) {
        T row = super.newItem();
        row.setOwner(owner);
        row.setLineNumber(lineNumber);
        return row;
    }

    /**
     * Удалить все подчиненные записи
     *
     * @param owner владелец этой табличной части (справочник или документ)
     * @throws SQLException
     */
    public void clearTable(Ref owner) throws SQLException {
        clearTable(TablePart.FIELD_NAME_REF_ID, "=", owner.getRef());
    }

    /**
     * Вспомогательная функция, возвращает следующий номер строки для нумерации
     * строк табличной части документа.
     *
     * @param owner владелец этой табличной части (справочник или документ)
     * @return
     * @throws SQLException
     */
    public int getNextLineNumber(Ref owner) throws SQLException {

        int newLineNumber = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT MAX(").append(TablePart.FIELD_NAME_LINE_NUMBER).append(") ");
        sb.append("FROM ").append(getTableInfo().getTableName());
        sb.append(" WHERE ").append(TablePart.FIELD_NAME_REF_ID).append(" = \"").append(owner)
          .append("\"");

        GenericRawResults<String[]> rawResults = queryRaw(sb.toString());
        List<String[]> results = rawResults.getResults();
        String strNumber = results.get(0)[0];

        if (!TextUtils.isEmpty(strNumber)) {

            try {
                newLineNumber = Integer.parseInt(strNumber);
            } catch (NumberFormatException e) {
                Dbg.printStackTrace(e);
            }

        }
        return ++newLineNumber;
    }

    /**
     * Сервисная функция, возвращает только измененные строки табличной части по
     * документу. Важно! При отправке объектов имеющих табличные части на сервер
     * 1С автоматически передаются все подчиненные строки табличных частей.
     *
     * @param owner владелец строк табличной части
     * @return
     * @throws SQLException
     */
    public List<T> selectChanged(Ref owner) throws SQLException {

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        where.eq(TablePart.FIELD_NAME_MODIFIED, true);
        where.and();
        where.eq(TablePart.FIELD_NAME_REF_ID, owner);

        return query(qb.prepare());
    }

    /**
     * Возвращает строки табличной части
     *
     * @param owner владелец табличной части
     * @return
     * @throws SQLException
     */
    public List<T> getTablePart(Ref owner) throws SQLException {

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();
        where.eq(TablePart.FIELD_NAME_REF_ID, owner);
        qb.orderBy(TablePart.FIELD_NAME_LINE_NUMBER, true);

        return query(qb.prepare());
    }
}
