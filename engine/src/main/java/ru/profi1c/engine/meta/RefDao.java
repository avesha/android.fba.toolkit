package ru.profi1c.engine.meta;

import android.text.TextUtils;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.profi1c.engine.Dbg;

/**
 * Базовый менеджер для работы со ссылочными объектами базы данных (справочники
 * и документы)
 */
public abstract class RefDao<T extends Ref> extends RowDao<T> {

    protected RefDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Создает новый элемент. Примечание: использование метода не приводит к
     * записи созданного объекта в базу данных. Для записи используйте метод
     * {@link #create(T)}
     *
     * @return
     */
    @Override
    public T newItem() {
        T item = super.newItem();
        item.setRef(UUID.randomUUID());
        item.setNewItem(true);
        return item;
    }

    /**
     * Создать пустую ссылку. Может использоваться например как корень дерева
     * при построении иерархии. Внимание, эти объекты не должны сохраняться в
     * базе данных
     *
     * @return
     */
    public T emptyRef() {
        T ref = newInstance();
        ((Ref) ref).setRef(UUID.fromString(Ref.EMPTY_REF));
        return ref;
    }

    /**
     * Выбрать все измененные на мобильном клиенте элементы
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<T> selectChanged() throws SQLException {
        return selectChanged(false);
    }

    /**
     * Установить/снять признаки 'Модифицированность' и 'Это новый' по списку
     * элементов
     *
     * @param modified
     * @param isNew
     * @param lst
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void setModified(boolean modified, boolean isNew, List<Object> lst) throws SQLException {
        for (Object obj : lst) {
            if (obj instanceof Ref) {
                Ref ref = (Ref) obj;
                ref.setModified(modified);
                ref.setNewItem(isNew);
                update((T) ref);
            }
        }
    }

    /**
     * Сервисная функция, позволяет установить / снять признак 'Это новый' для
     * всех элементов. Важно! При создании новых элементов через
     * {@link #newItem()} признак выставляется автоматически.
     *
     * @param isNew признак нового элемента
     * @throws SQLException
     */
    public void setNew(boolean isNew) throws SQLException {
        setNew(isNew, new HashMap<String, Object>(0));
    }

    /**
     * Сервисная функция, позволяет установить / снять признак того что это
     * ‘Новый объект’ т.е созданный на мобильном клиенте и еще не переданный на
     * сервер 1М
     *
     * @param isNew
     * @param filter
     * @throws SQLException
     */
    public void setNew(boolean isNew, HashMap<String, Object> filter) throws SQLException {
        updateColumnValue(Ref.FIELD_NAME_NEW_ITEM, isNew, filter);
    }

    /**
     * Выбрать измененные на мобильном клиенте элементы
     *
     * @param onlyNew если true, то будут выбраны только новые (добавленные на
     *                мобильном клиенте) иначе все измененные
     * @return
     * @throws SQLException
     */
    public List<T> selectChanged(boolean onlyNew) throws SQLException {

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        where.eq(Catalog.FIELD_NAME_MODIFIED, true);

        if (onlyNew) {
            where.and();
            where.eq(Catalog.FIELD_NAME_NEW_ITEM, true);
        }
        return query(qb.prepare());

    }

    /**
     * Получить следующий номер для нумерации элементов (кодов справочника,
     * номеров документа)
     *
     * @param prefix строковое значение префикса
     * @return
     * @throws SQLException
     */
    protected int getNextNumber(String prefix, String columnName) throws SQLException {

        int newCode = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT MAX(").append(columnName).append(") ");
        sb.append("FROM ").append(getTableInfo().getTableName());
        if (!TextUtils.isEmpty(prefix)) {
            sb.append(" WHERE ").append(columnName).append(" LIKE \"%").append(prefix)
              .append("%\"");
        }

        GenericRawResults<String[]> rawResults = queryRaw(sb.toString());
        List<String[]> results = rawResults.getResults();
        String strNumber = results.get(0)[0];

        if (!TextUtils.isEmpty(strNumber)) {

            if (!TextUtils.isEmpty(prefix)) {
                strNumber = strNumber.replace(prefix, "");
            }

            try {
                newCode = Integer.parseInt(strNumber);
            } catch (NumberFormatException e) {
                Dbg.printStackTrace(e);
            }

        }
        return ++newCode;
    }

    /**
     *Рассчитывает длину кода/номера для этого справочника/документа с учетом префикса Рассчет
     * возможен если в таблице есть хоть один элемент.
     * @param prefix префикс номера докмуента (кода справочника)
    *  @param columnName имя колонки по которой производится расчет
     * @return длину номера или 0 если расчет невозможен
     * @throws SQLException
     */
    protected  int getNumberLength(String prefix, String columnName) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT MAX(LENGTH(").append(columnName).append(")) ");
        sb.append("FROM ").append(getTableInfo().getTableName());
        if(!TextUtils.isEmpty(prefix)) {
            sb.append(" WHERE ").append(columnName).append(" LIKE '%").append(prefix).append("%'");
        }

        GenericRawResults<String[]> rawResults = queryRaw(sb.toString());
        List<String[]> results = rawResults.getResults();
        String strLength = results.get(0)[0];

        int lenCode = 0;
        if (!TextUtils.isEmpty(strLength)) {

            try {
                lenCode = Integer.parseInt(strLength);
            } catch (NumberFormatException e) {
                Dbg.printStackTrace(e);
            }
        }
        return lenCode;
    }

    /**
     * Рассчитывает длину кода/номера для этого справочника/документа. Рассчет
     * возможен если в таблице есть хоть один элемент.
     *
     * @param columnName имя колонки по которой производится расчет
     * @return длину номера или 0 если расчет невозможен
     * @throws SQLException
     */
    protected int getNumberLength(String columnName) throws SQLException {
        return getNumberLength(null, columnName);
    }

    /**
     * Форматировать числовое значение как строку с лидирующими нулями
     *
     * @param value число
     * @param len   длина результирующей строки
     * @return
     */
    public String formatNumber(int value, int len) {
        return formatNumber(null, value, len);
    }

    /**
     * Форматировать числовое как строку с лидирующими нулями
     *
     * @param prefix строковое значение префикса
     * @param value  число
     * @param len    длина результирующей строки (с учетом префикса)
     * @return
     */
    public String formatNumber(String prefix, int value, int len) {
        String frm = "%0" + String.valueOf(len) + "d";
        if (!TextUtils.isEmpty(prefix)) {
            frm = prefix + "%0" + String.valueOf(len - prefix.length()) + "d";
        }
        return String.format(frm, value);
    }

    /**
     * Привести значения коллекции как список {@link IPresentation}
     *
     * @param lst список источник
     * @return
     */
    public List<IPresentation> toPresentationList(List<T> lst) {
        List<IPresentation> lstResult = new ArrayList<IPresentation>();
        for (T t : lst) {
            lstResult.add((IPresentation) t);
        }
        return lstResult;
    }
}
