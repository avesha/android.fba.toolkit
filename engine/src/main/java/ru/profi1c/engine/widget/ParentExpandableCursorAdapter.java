package ru.profi1c.engine.widget;

import android.content.Context;
import android.database.Cursor;

import java.sql.SQLException;
import java.util.UUID;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.CatalogDao;

/**
 * Адаптер для отображения иерархического справочника (уровень иерархии
 * ограничен 2 т.е родитель-подчиненный). Имеет смысл только для многоуровневых
 * справочников.
 */
public class ParentExpandableCursorAdapter<T extends Catalog> extends MetaExpandableCursorAdapter {

    private final CatalogDao<T> mDao;
    private T mTmpInstance;
    private String orderChild;

    /**
     * Конструктор. Используется один {@link MetaAdapterViewBinder} для
     * построения отображения групп и элементов, макет строк
     * родитель-подчиненный так же один
     *
     * @param context                текущий контекст
     * @param dao                    используется для извлечения дочерних элементов
     * @param cursor                 курсор с данными группировки верхнего уровня
     * @param groupLayout            идентификатор ресурса макета используемый для отображения
     *                               групп и подчиненных элементов
     * @param groupAdapterViewBinder адаптер для отображения данных.
     */
    public ParentExpandableCursorAdapter(Context context, CatalogDao<T> dao, Cursor cursor,
            int groupLayout, MetaAdapterViewBinder groupAdapterViewBinder) {
        super(context, cursor, groupLayout, groupAdapterViewBinder);
        mDao = dao;
    }

    /**
     * Конструктор. Используются различные {@link MetaAdapterViewBinder} для
     * построения групп и элементов.
     *
     * @param context                текущий контекст
     * @param dao                    используется для извлечения дочерних элементов
     * @param cursor                 курсор с данными группировки верхнего уровня
     * @param groupLayout            идентификатор ресурса макета используемый для отображения
     *                               групп
     * @param groupAdapterViewBinder адаптер для отображения данных групп
     * @param childLayout            идентификатор ресурса макета используемый для отображения
     *                               подчиненных элементов
     * @param childAdapterViewBinder адаптер для отображения данных дочерних элементов
     */
    public ParentExpandableCursorAdapter(Context context, CatalogDao<T> dao, Cursor cursor,
            int groupLayout, MetaAdapterViewBinder groupAdapterViewBinder, int childLayout,
            MetaAdapterViewBinder childAdapterViewBinder) {

        super(context, cursor, groupLayout, groupAdapterViewBinder, childLayout,
                childAdapterViewBinder);
        mDao = dao;
    }

    /**
     * Конструктор, адаптеры для отображения данных
     * {@link MetaAdapterViewBinder} создаются автоматически
     *
     * @param context     текущий контекст
     * @param dao         используется для извлечения дочерних элементов
     * @param cursor      курсор с данными группировки верхнего уровня
     * @param groupLayout идентификатор ресурса макета используемый для отображения
     *                    групп
     * @param groupFrom   имена полей класса используемых для отображения строки группы
     * @param groupTo     идентификаторы дочерних View используемых для отображения
     *                    строки группы
     * @param groupBinder внешний класс для привязки значений группы к представлениям
     *                    полей
     * @param childLayout идентификатор ресурса макета используемый для отображения
     *                    подчиненных элементов
     * @param childFrom   имена полей класса используемых для отображения строк
     *                    подчиненных элементов
     * @param childTo     идентификаторы дочерних View используемых для отображения
     *                    строки подчиненных элементов
     * @param childBinder внешний класс для привязки значений элементов к представлениям
     *                    полей
     */
    public ParentExpandableCursorAdapter(Context context, CatalogDao<T> dao, Cursor cursor,
            int groupLayout, String[] groupFrom, int[] groupTo,
            MetaAdapterViewBinder.ViewBinder groupBinder, int childLayout, String[] childFrom,
            int[] childTo, MetaAdapterViewBinder.ViewBinder childBinder) {

        super(context, dao.getDataClass(), cursor, groupLayout, groupFrom, groupTo, groupBinder,
                childLayout, childFrom, childTo, childBinder);
        mDao = dao;
    }

    /**
     * Конструктор, адаптеры для отображения данных
     * {@link MetaAdapterViewBinder} создаются автоматически
     *
     * @param context              текущий контекст
     * @param dao                  используется для извлечения дочерних элементов
     * @param cursor               курсор с данными группировки верхнего уровня
     * @param expandedGroupLayout  идентификатор ресурса макета используемый для отображения
     *                             групп в раскрытом состоянии
     * @param collapsedGroupLayout идентификатор ресурса макета используемый для отображения
     *                             групп в свернутом состоянии
     * @param groupFrom            имена полей класса используемых для отображения строки группы
     * @param groupTo              идентификаторы дочерних View используемых для отображения
     *                             строки группы
     * @param groupBinder          внешний класс для привязки значений группы к представлениям
     *                             полей
     * @param childLayout          идентификатор ресурса макета используемый для отображения
     *                             подчиненных элементов
     * @param lastChildLayout      идентификатор ресурса макета используемый для отображения
     *                             последнего подчиненного элемента в пределах группы
     * @param childFrom            имена полей класса используемых для отображения строк
     *                             подчиненных элементов
     * @param childTo              идентификаторы дочерних View используемых для отображения
     *                             строки подчиненных элементов
     * @param childBinder          внешний класс для привязки значений элементов к представлениям
     *                             полей
     */
    public ParentExpandableCursorAdapter(Context context, CatalogDao<T> dao, Cursor cursor,
            int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
            MetaAdapterViewBinder.ViewBinder groupBinder, int childLayout, int lastChildLayout,
            String[] childFrom, int[] childTo, MetaAdapterViewBinder.ViewBinder childBinder) {

        super(context, dao.getDataClass(), cursor, expandedGroupLayout, collapsedGroupLayout,
                groupFrom, groupTo, groupBinder, childLayout, lastChildLayout, childFrom, childTo,
                childBinder);
        mDao = dao;
    }

    /**
     * Установить сортировку дочерних элементов
     *
     * @param order строка с именем колонки в таблице базы данных (реквизита
     *              справочника), определяющая упорядочивание дочерних элементов в
     *              выборке. После имени реквизита через пробел может быть указано
     *              направление сортировки. "Desc" - упорядочивать по убыванию,
     *              "Asc" - упорядочивать по возрастанию.
     */
    public void setOrderChild(String order) {
        orderChild = order;
    }

    /**
     * Создает новый объект
     *
     * @return
     */
    private T newInstance() {
        T item = null;
        try {
            item = mDao.getDataClass().newInstance();
        } catch (InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return item;
    }

    protected T getItem(String ref) {
        if (mTmpInstance == null) {
            mTmpInstance = newInstance();
        }
        mTmpInstance.setRef(UUID.fromString(ref));
        return mTmpInstance;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {

        Cursor cursor = null;

        int columnIndex = groupCursor.getColumnIndex(Catalog.FIELD_NAME_REF);
        if (columnIndex != Const.NOT_SPECIFIED) {

            String ref = groupCursor.getString(columnIndex);
            T parent = getItem(ref);

            try {
                cursor = mDao.selectCursor(parent, null, null, orderChild);
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
            }
        }
        return cursor;
    }

}
