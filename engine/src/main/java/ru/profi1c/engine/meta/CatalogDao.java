package ru.profi1c.engine.meta;

import android.database.Cursor;
import android.text.TextUtils;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.profi1c.engine.util.tree.GenericTree;
import ru.profi1c.engine.util.tree.GenericTreeNode;

/**
 * Менеджер для работы с элементами справочников (создание, удаление, поиск)
 *
 * @param <T>
 */
public abstract class CatalogDao<T extends Catalog> extends RefDao<T> {

    protected CatalogDao(ConnectionSource connectionSource, Class<T> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Создает новую группу справочника. Примечание: использование метода не
     * приводит к записи созданного объекта в базу данных.
     *
     * @return
     */
    public T newFolder() {
        T folder = newItem();
        folder.setFolder(true);
        return folder;
    }

    /**
     * Осуществляет поиск элемента по его коду.
     *
     * @param code искомый код.
     * @return
     * @throws SQLException
     */
    public T findByCode(String code) throws SQLException {
        return findByCode(code, null, null);
    }

    /**
     * Осуществляет поиск элемента по его коду в пределах родителя
     *
     * @param code   искомый код.
     * @param parent родитель, в пределах которого нужно выполнять поиск.
     * @return
     * @throws SQLException
     */
    public T findByCode(String code, T parent) throws SQLException {
        return findByCode(code, parent, null);
    }

    /**
     * Осуществляет поиск элемента по его коду.
     *
     * @param code   искомый код.
     * @param parent родитель, в пределах которого нужно выполнять поиск. Если не
     *               указан, то поиск будет проводиться во всем справочнике.
     * @param owner  владелец, в пределах которого нужно выполнять поиск. Если не
     *               указан, то поиск будет проводиться во всем справочнике.
     * @return
     * @throws SQLException
     */
    public T findByCode(String code, T parent, Catalog owner) throws SQLException {

        T obj = null;

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        where.eq(Catalog.FIELD_NAME_CODE, code);

        if (!Catalog.isEmpty(parent)) {
            where.and();
            where.eq(Catalog.FIELD_NAME_PARENT, parent);
        }

        if (!Catalog.isEmpty(owner) && MetadataHelper.isSlaveCatalog(getDataClass())) {
            where.and();
            where.eq(Catalog.FIELD_NAME_OWNER, owner);
        }

        obj = queryForFirst(qb.prepare());
        return obj;
    }

    /**
     * Осуществляет поиск элемента по его наименованию (точное соответствие)
     *
     * @param desc Строка, содержащая искомое наименование.
     * @return
     * @throws SQLException
     */
    public T findByDescription(String desc) throws SQLException {
        return findByDescription(desc, false, null, null);
    }

    /**
     * Осуществляет поиск элемента по его наименованию (точное соответствие в
     * пределах родителя)
     *
     * @param desc   Строка, содержащая искомое наименование.
     * @param parent родитель, в пределах которого нужно выполнять поиск.
     * @return
     * @throws SQLException
     */
    public T findByDescription(String desc, T parent) throws SQLException {
        return findByDescription(desc, false, parent, null);
    }

    /**
     * Осуществляет поиск элемента по его наименованию (точное соответствие в
     * пределах родителя с отбором по владельцу)
     *
     * @param desc   Строка, содержащая искомое наименование.
     * @param parent родитель, в пределах которого нужно выполнять поиск.
     * @param owner  владелец, в пределах которого нужно выполнять поиск.
     * @return
     * @throws SQLException
     */
    public T findByDescription(String desc, T parent, Catalog owner) throws SQLException {
        return findByDescription(desc, false, parent, owner);
    }

    /**
     * Осуществляет поиск элемента по его наименованию.
     *
     * @param description Строка, содержащая искомое наименование.
     * @param like        если true, поиск будет произведен по частичному совпадению
     *                    иначе по полному
     * @param parent      родитель, в пределах которого нужно выполнять поиск. Если не
     *                    указан, то поиск будет проводиться во всем справочнике.
     * @param owner       владелец, в пределах которого нужно выполнять поиск. Если не
     *                    указан, то поиск будет проводиться во всем справочнике.
     * @return
     * @throws SQLException
     */
    public T findByDescription(String description, boolean like, T parent, Catalog owner)
            throws SQLException {

        //экранировать только одинарную кавычку, двойные ormlite экранирует автоматически
        final String desc = DBUtils.quote(description);
        T obj = null;

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();

        if (like) {
            where.like(Catalog.FIELD_NAME_DESCRIPTION, "%" + desc + "%");
        } else {
            where.eq(Catalog.FIELD_NAME_DESCRIPTION, desc);
        }

        if (!Catalog.isEmpty(parent)) {
            where.and();
            where.eq(Catalog.FIELD_NAME_PARENT, parent);
        }

        if (!Catalog.isEmpty(owner) && MetadataHelper.isSlaveCatalog(getDataClass())) {
            where.and();
            where.eq(Catalog.FIELD_NAME_OWNER, owner);
        }

        obj = queryForFirst(qb.prepare());
        return obj;
    }

    /**
     * Осуществляет поиск элемента по значению реквизита.
     *
     * @param columnName имя колонки в таблице базы данных для этого реквизита (поля
     *                   класса), как правило одна из констант 'FIELD_NAME_'
     * @param value      Значение реквизита, по которому должен выполняться поиск.
     * @return
     * @throws SQLException
     */
    public T findByAttribute(String columnName, Object value) throws SQLException {
        return findByAttribute(columnName, value, null, null);
    }

    /**
     * Осуществляет поиск элемента по значению реквизита (в пределах родителя)
     *
     * @param columnName имя колонки в таблице базы данных для этого реквизита (поля
     *                   класса), как правило одна из констант 'FIELD_NAME_'
     * @param value      Значение реквизита, по которому должен выполняться поиск.
     * @param parent     владелец, в пределах которого нужно выполнять поиск.
     * @return
     * @throws SQLException
     */
    public T findByAttribute(String columnName, Object value, T parent) throws SQLException {
        return findByAttribute(columnName, value, parent, null);
    }

    /**
     * Осуществляет поиск элемента по значению реквизита.
     *
     * @param columnName имя колонки в таблице базы данных для этого реквизита (поля
     *                   класса), как правило одна из констант 'FIELD_NAME_'
     * @param value      Значение реквизита, по которому должен выполняться поиск.
     * @param parent     родитель, в пределах которого нужно выполнять поиск. Если не
     *                   указан, то поиск будет проводиться во всем справочнике.
     * @param owner      владелец, в пределах которого нужно выполнять поиск. Если не
     *                   указан, то поиск будет проводиться во всем справочнике.
     * @return Ссылка на найденный элемент справочника. Если не существует ни
     * одного элемента с требуемым значением реквизита, то будет
     * возвращено null.
     * @throws SQLException
     */
    public T findByAttribute(String columnName, Object value, T parent, Catalog owner)
            throws SQLException {

        T obj = null;

        QueryBuilder<T, String> qb = queryBuilder();
        Where<T, String> where = qb.where();
        where.eq(columnName, value);

        if (!Catalog.isEmpty(parent)) {
            where.and();
            where.eq(Catalog.FIELD_NAME_PARENT, parent);
        }

        if (!Catalog.isEmpty(owner) && MetadataHelper.isSlaveCatalog(getDataClass())) {
            where.and();
            where.eq(Catalog.FIELD_NAME_OWNER, owner);
        }

        obj = queryForFirst(qb.prepare());
        return obj;
    }

    /**
     * Получить элемент по ссылке. Можно так же использовать аналогичный метод
     * {@link #queryForId(String id)}
     *
     * @param ref текстовое представление ссылки
     * @return
     * @throws SQLException
     */
    public T findByRef(String ref) throws SQLException {
        return queryForId(ref);
    }

    /**
     * Формирует выборку элементов справочника (все)
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<T> select() throws SQLException {
        return select(null, null, null, null);
    }

    /**
     * Формирует выборку элементов справочника (с фильтрацией)
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @return
     * @throws SQLException
     */
    @Override
    public List<T> select(HashMap<String, Object> filter) throws SQLException {
        return select(null, null, filter, null);
    }

    /**
     * Формирует выборку элементов справочника (с фильтрацией и сортировкой)
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
    @Override
    public List<T> select(HashMap<String, Object> filter, String order) throws SQLException {
        return select(null, null, filter, order);
    }

    /**
     * Формирует выборку элементов справочника (по родителю)
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников.
     * @return
     * @throws SQLException
     */
    public List<T> select(T parent) throws SQLException {
        return select(parent, null, null, null);
    }

    /**
     * Формирует выборку элементов справочника (с фильтрацией и отбором по
     * родителю)
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @return
     * @throws SQLException
     */
    public List<T> select(T parent, HashMap<String, Object> filter) throws SQLException {
        return select(parent, null, filter, null);
    }

    /**
     * Формирует выборку элементов справочника (по родителю, с отбором по
     * владельцу)
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников.
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников.
     * @return
     * @throws SQLException
     */
    public List<T> select(T parent, Catalog owner) throws SQLException {
        return select(parent, owner, null, null);
    }

    /**
     * Формирует выборку элементов справочника по заданным условиям.
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Если параметр не задан, то отбор по родителю не
     *               производится. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника.
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников. Если параметр не задан, то отбор по владельцу не
     *               производится.
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @param order  строка с именем колонки в таблице базы данных (реквизита
     *               справочника), определяющая упорядочивание элементов в выборке.
     *               После имени реквизита через пробел может быть указано
     *               направление сортировки. "Desc" - упорядочивать по убыванию,
     *               "Asc" - упорядочивать по возрастанию.
     * @return выборка данных или пустая коллекция
     * @throws SQLException
     */
    public List<T> select(T parent, Catalog owner, HashMap<String, Object> filter, String order)
            throws SQLException {

        if (filter == null) {
            filter = new HashMap<String, Object>();
        }

        // можеть быть корень как родитель
        if (parent != null) {
            filter.put(Catalog.FIELD_NAME_PARENT, parent);
        }

        if (!Catalog.isEmpty(owner) && MetadataHelper.isSlaveCatalog(getDataClass())) {
            filter.put(Catalog.FIELD_NAME_OWNER, owner);
        }

        if (filter.size() > 0) {
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
     * Формирует курсор на выборку элементов справочника (все)
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Cursor selectCursor() throws SQLException {
        return selectCursor(null, null, null, null);
    }

    /**
     * Формирует курсор на выборку элементов справочника (с фильтрацией)
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @return
     * @throws SQLException
     */
    @Override
    public Cursor selectCursor(HashMap<String, Object> filter) throws SQLException {
        return selectCursor(null, null, filter, null);
    }

    /**
     * Формирует курсор на выборку элементов справочника (с фильтрацией и
     * сортировкой)
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
    @Override
    public Cursor selectCursor(HashMap<String, Object> filter, String order) throws SQLException {
        return selectCursor(null, null, filter, order);
    }

    /**
     * Формирует курсор на выборку элементов справочника (по родителю)
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников.
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor(T parent) throws SQLException {
        return selectCursor(parent, null, null, null);
    }

    /**
     * Формирует курсор на выборку элементов справочника (с фильтрацией и
     * отбором по родителю)
     *
     * @param filter 'структура' отбора в виде коллекции, где 'String' имя колонки
     *               в таблице базы данных для этого реквизита (поля класса), как
     *               правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *               реквизита
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor(T parent, HashMap<String, Object> filter) throws SQLException {
        return selectCursor(parent, null, filter, null);
    }

    /**
     * Формирует курсор на выборку элементов справочника (по родителю, с отбором
     * по владельцу)
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников.
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников.
     * @return
     * @throws SQLException
     */
    public Cursor selectCursor(T parent, Catalog owner) throws SQLException {
        return selectCursor(parent, owner, null, null);
    }

    /**
     * Формирует выборку элементов справочника по заданным условиям.
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Если параметр не задан, то отбор по родителю не
     *               производится. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника.
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников. Если параметр не задан, то отбор по владельцу не
     *               производится.
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
    public Cursor selectCursor(T parent, Catalog owner, HashMap<String, Object> filter,
            String order) throws SQLException {

        Cursor cursor = null;

        if (filter == null) {
            filter = new HashMap<String, Object>();
        }

        // можеть быть корень как родитель
        if (parent != null) {
            filter.put(Catalog.FIELD_NAME_PARENT, parent);
        }

        if (!Catalog.isEmpty(owner) && MetadataHelper.isSlaveCatalog(getDataClass())) {
            filter.put(Catalog.FIELD_NAME_OWNER, owner);
        }

        if (filter.size() > 0) {
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

    /**
     * Выбрать иерархически все элементы справочника
     *
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchically() throws SQLException {
        return selectTree(null, null, false, false, null);
    }

    /**
     * Выбрать иерархически элементы справочника по родителю
     *
     * @param parent            отбор по родителю. Имеет смысл только для многоуровневых
     *                          справочников. Чтобы отобрать элементы верхнего уровня, нужно в
     *                          качестве данного параметра указать пустую ссылку на элемент
     *                          справочника, см {@link #emptyRef()}
     * @param hierarchyElements если истина, то подразумевается то у этого справочника
     *                          иерархия элементов, иначе (по умолчанию) иерархия групп
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchically(T parent, boolean hierarchyElements)
            throws SQLException {
        return selectTree(parent, null, false, hierarchyElements, null);
    }

    /**
     * Выбрать иерархически элементы справочника по родителю
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника, см {@link #emptyRef()}
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchically(T parent) throws SQLException {
        return selectTree(parent, null, false, false, null);
    }

    /**
     * Выбрать иерархически элементы справочника по родителю (с пользовательской
     * сортировкой)
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника, см {@link #emptyRef()}
     * @param order  строка с именем колонки в таблице базы данных (реквизита
     *               справочника), определяющая упорядочивание элементов в выборке.
     *               После имени реквизита через пробел может быть указано
     *               направление сортировки. "Desc" - упорядочивать по убыванию,
     *               "Asc" - упорядочивать по возрастанию.
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchically(T parent, String order) throws SQLException {
        return selectTree(parent, null, false, false, order);
    }

    /**
     * Выбрать иерархически элементы справочника по родителю (с отбором
     * владельцу)
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника, см {@link #emptyRef()}
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников.
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchically(T parent, Catalog owner) throws SQLException {
        return selectTree(parent, owner, false, false, null);
    }

    /**
     * Выбрать иерархически
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Если параметр не задан, то отбор по родителю не
     *               производится. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника.
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников. Если параметр не задан, то отбор по владельцу не
     *               производится.
     * @param order  строка с именем колонки в таблице базы данных (реквизита
     *               справочника), определяющая упорядочивание элементов в выборке.
     *               После имени реквизита через пробел может быть указано
     *               направление сортировки. "Desc" - упорядочивать по убыванию,
     *               "Asc" - упорядочивать по возрастанию.
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchically(T parent, Catalog owner, String order)
            throws SQLException {
        return selectTree(parent, owner, false, false, order);
    }

    /**
     * Выбрать иерархию (только группы) по всему справочнику
     *
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchy() throws SQLException {
        return selectTree(null, null, true, false, null);
    }

    /**
     * Выбрать иерархию (только группы) по родительской группе
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника, см {@link #emptyRef()}
     * @return Дерево элементов , где корень дерева указанный родитель
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchy(T parent) throws SQLException {
        return selectTree(parent, null, true, false, null);
    }

    /**
     * Выбрать иерархию (только группы) по родительской группе, с отбором по
     * владельцу
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника, см {@link #emptyRef()}
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников.
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchy(T parent, Catalog owner) throws SQLException {
        return selectTree(parent, owner, true, false, null);
    }

    /**
     * Выбрать иерархию (только группы)
     *
     * @param parent отбор по родителю. Имеет смысл только для многоуровневых
     *               справочников. Если параметр не задан, то отбор по родителю не
     *               производится. Чтобы отобрать элементы верхнего уровня, нужно в
     *               качестве данного параметра указать пустую ссылку на элемент
     *               справочника.
     * @param owner  отбор по владельцу. Имеет смысл только для подчиненных
     *               справочников. Если параметр не задан, то отбор по владельцу не
     *               производится.
     * @param order  строка с именем колонки в таблице базы данных (реквизита
     *               справочника), определяющая упорядочивание элементов в выборке.
     *               После имени реквизита через пробел может быть указано
     *               направление сортировки. "Desc" - упорядочивать по убыванию,
     *               "Asc" - упорядочивать по возрастанию.
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectHierarchy(T parent, Catalog owner, String order)
            throws SQLException {
        return selectTree(parent, owner, true, false, order);
    }

    /**
     * Выбрать данные в дерево
     *
     * @param parent            корень дерева, если не указан, то будет автоматически
     *                          установлена пустая ссылка как корень дерева
     * @param owner             отбор по владельцу. Имеет смысл только для подчиненных
     *                          справочников. Если параметр не задан, то отбор по владельцу не
     *                          производится.
     * @param foldersOnly       признак отбора только групп
     * @param hierarchyElements если истина, то подразумевается то у этого справочника
     *                          иерархия элементов, иначе (по умолчанию) иерархия групп
     * @param order             сортировка
     * @return
     * @throws SQLException
     */
    public GenericTree<T> selectTree(T parent, Catalog owner, boolean foldersOnly,
            boolean hierarchyElements, String order) throws SQLException {

        // сортировка для однопроходного создания дерева
        StringBuilder sbOrder = new StringBuilder();
        sbOrder.append(Catalog.FIELD_NAME_LEVEL).append(", ");
        sbOrder.append(Catalog.FIELD_NAME_FOLDER).append(" DESC");

        // + сортировка пользовательская
        if (!TextUtils.isEmpty(order)) {
            sbOrder.append(", ").append(order);
        }

        String fullOrder = sbOrder.toString();

        if (parent == null) {
            parent = emptyRef();
        }

        GenericTree<T> tree = new GenericTree<T>();

        GenericTreeNode<T> root = new GenericTreeNode<T>(parent);
        tree.setRoot(root);

        selectRecursive(parent.getRef(), owner, foldersOnly, hierarchyElements, fullOrder, root);
        return tree;
    }

    /**
     * Рекурсивная выборка и добавление элементов в дерево
     *
     * @param parent
     * @param owner
     * @param foldersOnly
     * @param hierarchyElements
     * @param order
     * @param node
     * @throws SQLException
     */
    private void selectRecursive(UUID parent, Catalog owner, boolean foldersOnly,
            boolean hierarchyElements, String order, GenericTreeNode<T> node) throws SQLException {

        HashMap<String, Object> filter = new HashMap<String, Object>();

        filter.put(Catalog.FIELD_NAME_PARENT, parent);

        if (!Catalog.isEmpty(owner) && MetadataHelper.isSlaveCatalog(getDataClass())) {
            filter.put(Catalog.FIELD_NAME_OWNER, owner);
        }

        if (foldersOnly) {
            filter.put(Catalog.FIELD_NAME_FOLDER, true);
        }

        List<T> list = queryForFieldValues(filter, order);
        for (T item : list) {
            GenericTreeNode<T> newNode = new GenericTreeNode<T>(item);
            node.addChild(newNode);

            if (item.isFolder() || hierarchyElements) {
                selectRecursive(item.getRef(), owner, foldersOnly, hierarchyElements, order,
                                newNode);
            }

        }

    }

    /**
     * Преобразовать дерево иерархии к линейному списку
     *
     * @param tree        дерево полученное методами {@link #selectHierarchy()} или
     *                    {@link #selectHierarchically()}
     * @param includeRoot включать или нет корень дерева в список
     * @return
     */
    public List<T> hierarchyToList(GenericTree<T> tree, boolean includeRoot) {

        List<T> lst = new ArrayList<T>();
        GenericTreeNode<T> root = tree.getRoot();
        if (root != null) {
            if (includeRoot) {
                lst.add(root.getData());
            }

            for (GenericTreeNode<T> child : root.getChildren()) {
                addTreeNodeToList(child, lst);
            }

        }
        return lst;
    }

    /*
     * Рекурсивное добавление элементов дерева в список
     */
    private void addTreeNodeToList(GenericTreeNode<T> node, List<T> lstResult) {

        lstResult.add(node.getData());

        for (GenericTreeNode<T> child : node.getChildren()) {
            addTreeNodeToList(child, lstResult);
        }
    }

    /**
     * Получить длину кода справочника. Рассчет возможен если в справочнике есть
     * хоть один элемент.
     *
     * @return длину кода справочника или 0
     * @throws SQLException
     */
    public int getCodeLength() throws SQLException {
        return getNumberLength(null, Catalog.FIELD_NAME_CODE);
    }

    /**
     * Получить длину кода справочника с учетом префикса. Рассчет возможен если в справочнике есть
     * хоть один элемент.
     *
     * @param prefix префикс кода справочника
     * @return длину кода справочника или 0
     * @throws SQLException
     */
    public int getCodeLength(String prefix) throws SQLException {
        return getNumberLength(prefix, Catalog.FIELD_NAME_CODE);
    }

    /**
     * Получить новый код для нумерации элементов. Если в таблице присутствуют
     * элементы с префиксами рекомендуется использовать функцию
     * {@link #getNextCode(String prefix)}
     *
     * @return
     * @throws SQLException
     */
    public int getNextCode() throws SQLException {
        return getNextNumber(null, Catalog.FIELD_NAME_CODE);
    }

    /**
     * Получить следующий код для нумерации элементов (кодов справочника,
     * номеров документа)
     *
     * @param prefix строковое значение префикса
     * @return
     * @throws SQLException
     */
    public int getNextCode(String prefix) throws SQLException {
        return getNextNumber(prefix, Catalog.FIELD_NAME_CODE);
    }

}
