package ru.profi1c.engine.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.widget.BaseListFilter.FilterCompletedListener;

/**
 * Адаптер элементов справочника с возможностью навигации (отбора) по родителю
 * см. методы {@link #pop()} и {@link #push(Catalog)} и динамической фильтрации
 * элементов см. {@link #getFilter()}
 */
public class HierarchyAdapter<T extends Catalog> extends BaseAdapter implements Filterable {

    private MetaAdapterViewBinder mAdapterViewBinder;
    private boolean mSelectedFolderBold;

    private int mResource;
    private int mDropDownResource;

    private Stack<T> mStackParent;
    private T mCurrentParent;
    private T mRoot;

    private CatalogDao<T> mDao;
    private List<T> mData;

    private HashMap<String, Object> mSelectFilter;
    private String mSelectOrder;

    private PresentationFilter mFilter;

    /**
     * Список элементов справочника с возможностью навигации. Для элементов в
     * списке используется макет {@link android.R.layout.simple_list_item_1} т.е
     * элементы будут выглядеть внешне так же как у стандартного списка
     *
     * @param context текущий контекст
     * @param dao     для доступа к данным справочника
     * @throws SQLException
     */
    public HierarchyAdapter(Context context, CatalogDao<T> dao) throws SQLException {
        init(context, dao, null, null, null, 0, null);
    }

    /**
     * Список элементов справочника с возможностью навигации. Для элементов в
     * списке используется макет {@link android.R.layout.simple_list_item_1} т.е
     * элементы будут выглядеть внешне так же как у стандартного списка
     *
     * @param context текущий контекст
     * @param dao     для доступа к данным справочника
     * @param parent  отбор по родителю
     * @throws SQLException
     */
    public HierarchyAdapter(Context context, CatalogDao<T> dao, T parent) throws SQLException {
        init(context, dao, parent, null, null, 0, null);
    }

    /**
     * Список элементов справочника с возможностью навигации. Для элементов в
     * списке используется макет {@link android.R.layout.simple_list_item_1} т.е
     * элементы будут выглядеть внешне так же как у стандартного списка внешне
     * так же как у стандартного списка
     *
     * @param context текущий контекст
     * @param dao     для доступа к данным справочника
     * @param parent  отбор по родителю
     * @param filter  'структура' отбора в виде коллекции, где 'String' имя колонки
     *                в таблице базы данных для этого реквизита (поля класса), как
     *                правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                реквизита
     * @throws SQLException
     */
    public HierarchyAdapter(Context context, CatalogDao<T> dao, T parent,
            HashMap<String, Object> filter) throws SQLException {
        init(context, dao, parent, filter, null, 0, null);
    }

    /**
     * Список элементов справочника с возможностью навигации. Для элементов в
     * списке используется макет {@link android.R.layout.simple_list_item_1} т.е
     * элементы будут выглядеть внешне так же как у стандартного списка
     *
     * @param context текущий контекст
     * @param dao     для доступа к данным справочника
     * @param parent  отбор по родителю
     * @param filter  'структура' отбора в виде коллекции, где 'String' имя колонки
     *                в таблице базы данных для этого реквизита (поля класса), как
     *                правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                реквизита
     * @param order   сортировка списка, например если указать
     *                'Catalog.FIELD_NAME_FOLDER + " DESC, " +
     *                Catalog.FIELD_NAME_DESCRIPTION;' будет сортировка по
     *                наименованию c группами сверху
     * @throws SQLException
     */
    public HierarchyAdapter(Context context, CatalogDao<T> dao, T parent,
            HashMap<String, Object> filter, String order) throws SQLException {
        init(context, dao, parent, filter, order, 0, null);
    }

    /**
     * Список элементов справочника с возможностью навигации. Для элементов
     * используется указанный макет
     *
     * @param context    текущий контекст
     * @param dao        для доступа к данным справочника
     * @param parent     отбор по родителю
     * @param filter     'структура' отбора в виде коллекции, где 'String' имя колонки
     *                   в таблице базы данных для этого реквизита (поля класса), как
     *                   правило одна из констант 'FIELD_NAME_', а 'Object' значение
     *                   реквизита
     * @param order      сортировка списка, например если указать
     *                   'Catalog.FIELD_NAME_FOLDER + " DESC, " +
     *                   Catalog.FIELD_NAME_DESCRIPTION;' будет сортировка по
     *                   наименованию c группами сверху
     * @param resource   идентификатор ресурса макета используемый для отображения
     *                   элементов списка
     * @param viewBinder адаптер для отображения данных.
     * @throws SQLException
     */
    public HierarchyAdapter(Context context, CatalogDao<T> dao, T parent,
            HashMap<String, Object> filter, String order, int resource,
            MetaAdapterViewBinder viewBinder) throws SQLException {
        init(context, dao, parent, filter, order, resource, viewBinder);
    }

    private void init(Context context, CatalogDao<T> dao, T parent, HashMap<String, Object> filter,
            String order, int resource, MetaAdapterViewBinder viewBinder) throws SQLException {

        mDao = dao;

        if (parent == null) {
            mCurrentParent = mRoot = dao.emptyRef();
        } else {
            mCurrentParent = mRoot = parent;
        }

        mStackParent = new Stack<T>();
        mSelectFilter = filter;
        mSelectOrder = order;

        if (resource == 0) {
            mResource = android.R.layout.simple_list_item_1;
            mDropDownResource = android.R.layout.simple_spinner_dropdown_item;
        } else {
            mResource = mDropDownResource = resource;
        }

        if (viewBinder == null) {
            mAdapterViewBinder = createMetaAdapterViewBinder(context, dao.getDataClass(),
                    android.R.id.text1);
        } else {
            mAdapterViewBinder = viewBinder;
        }
        fillData();
    }

    /*
     * Получить данные списка
     */
    @SuppressWarnings("unchecked")
    private void fillData() throws SQLException {
        mData = mDao.select(mCurrentParent, null, mSelectFilter, mSelectOrder);

        if (mFilter != null) {
            mFilter.setUnfilteredData((List<IPresentation>) (List<?>) mData);
        }
        notifyDataSetChanged();
    }

    /*
     * построитель view по умолчанию, выводит представление в поле
     * FIELD_NAME_DESCRIPTION
     */
    private MetaAdapterViewBinder createMetaAdapterViewBinder(Context context, Class<?> classOf,
            int textViewResourceId) {
        MetaAdapterViewBinder mavb = new MetaAdapterViewBinder(context, classOf,
                new String[]{Catalog.FIELD_NAME_DESCRIPTION}, new int[]{textViewResourceId});
        mavb.setViewBinder(mPresentationViewBinder);
        return mavb;
    }

    /**
     * Спустится по иерархии ниже, например по клику на группу отобразить по ней
     * подчиненные элементы
     *
     * @param parent элемент родитель
     * @throws SQLException
     */
    public void push(T parent) throws SQLException {
        mStackParent.push(mCurrentParent);
        mCurrentParent = parent;

        fillData();
    }

    /**
     * Подняться по иерархии на уровень выше, перезаполнить по родителю
     *
     * @return ссылку на нового родителя или на корневой элемент (заданный при
     * создании адаптера) выше которого по иерархии подниматься нельзя
     * куда
     * @throws SQLException
     */
    public T pop() throws SQLException {
        if (mStackParent.size() > 0) {
            mCurrentParent = mStackParent.pop();
            fillData();
            return mCurrentParent;
        }
        return mRoot;
    }

    /**
     * Если используется построитель элементов по умолчанию
     * (MetaAdapterViewBinder не назначен) можно выделить группы жирным
     * установкой этого флага
     *
     * @param selectedFolderBold
     */
    public void setSelectedFolderBold(boolean selectedFolderBold) {
        this.mSelectedFolderBold = selectedFolderBold;
    }

    /**
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * <p>
     * Sets the layout resource to create the drop down views.
     * </p>
     *
     * @param resource the layout resource defining the drop down views
     * @see #getDropDownView(int, android.view.View, android.view.ViewGroup)
     */
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
            int resource) {

        View v;
        T item = getItem(position);

        if (convertView == null) {
            v = mAdapterViewBinder.newView(position, item, parent, resource);
        } else {
            v = convertView;
        }

        mAdapterViewBinder.bindView(position, item, v);
        return v;
    }

    /*
     * Форматирование. Выводит представление
     */
    private MetaAdapterViewBinder.ViewBinder mPresentationViewBinder = new MetaAdapterViewBinder.ViewBinder() {

        @Override
        public boolean setViewValue(View v, Cursor cursor, Field field) {
            return false;
        }

        @Override
        public boolean setViewValue(View v, Object item, Field field) {

            @SuppressWarnings("unchecked")
            T ref = (T) item;

            final String name = field.getName();
            if (Catalog.FIELD_NAME_DESCRIPTION.equalsIgnoreCase(name)) {

                TextView tv = ((TextView) v);
                tv.setText(ref.getPresentation());

                if (mSelectedFolderBold) {
                    tv.setTypeface(null, ref.isFolder() ? Typeface.BOLD : Typeface.NORMAL);
                }
                return true;
            }
            return false;
        }

        @Override
        public BaseViewHolder createViewHolder(View root) {
            return null;
        }

        @Override
        public void onBind(BaseViewHolder viewHolder, int position) {

        }

    };

    @SuppressWarnings("unchecked")
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new PresentationFilter((List<IPresentation>) (List<?>) mData,
                    mCompletedFilteredListener);
        }
        return mFilter;
    }

    private BaseListFilter.FilterCompletedListener mCompletedFilteredListener = new FilterCompletedListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void publishResults(List<?> data) {
            mData = (List<T>) data;
            if (data.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}
