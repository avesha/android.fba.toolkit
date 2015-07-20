package ru.profi1c.engine.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.util.tree.GenericTree;

/**
 * Простой адаптер для отображения иерархического справочника. Вводится только
 * тестовое представление элемента (с отступом по иерархии)
 */
public class SimpleHierarchyAdapter<T extends Catalog> extends BaseAdapter {

    private MetaAdapterViewBinder mAdapterViewBinder;
    private int mResource;
    private int mDropDownResource;

    private List<T> mData;

    /**
     * Иерархический выбор только групп справочника. Для элементов в списке
     * используется макет {@link android.R.layout.simple_spinner_item} т.е
     * элементы будут выглядеть внешне так же как у стандартного Spiner-а
     *
     * @param context текущий контекст
     * @param dao     используется для извлечения элементов
     * @throws SQLException
     */
    public SimpleHierarchyAdapter(Context context, CatalogDao<T> dao) throws SQLException {

        MetaAdapterViewBinder mavb = createMetaAdapterViewBinder(context, dao.getDataClass(),
                android.R.id.text1);
        List<T> data = getHierarchyLinear(dao, null, true, null);
        init(data, android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item, mavb);
    }

    /**
     * Иерархический выбор только групп справочника по родителю (если указан).
     * Для элементов в списке используется макет
     * {@link android.R.layout.simple_spinner_item} т.е элементы будут выглядеть
     * внешне так же как у стандартного Spiner-а
     *
     * @param contex текущий контекст
     * @param dao    используется для извлечения элементов
     * @param parent родитель
     * @throws SQLException
     */
    public SimpleHierarchyAdapter(Context contex, CatalogDao<T> dao, T parent) throws SQLException {

        MetaAdapterViewBinder mavb = createMetaAdapterViewBinder(contex, dao.getDataClass(),
                android.R.id.text1);
        List<T> data = getHierarchyLinear(dao, parent, true, null);
        init(data, android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item, mavb);
    }

    /**
     * Иерархический выбор только групп справочника по родителю (если указан).
     * Для элементов в списке используется макет
     * {@link android.R.layout.simple_spinner_item} т.е элементы будут выглядеть
     * внешне так же как у стандартного Spiner-а
     *
     * @param contex текущий контекст
     * @param dao    используется для извлечения элементов
     * @param parent родитель
     * @param order  сортировка списка, например если указать
     *               'Catalog.FIELD_NAME_FOLDER + " DESC, " +
     *               Catalog.FIELD_NAME_DESCRIPTION;' будет сортировка по
     *               наименованию c группами сверху
     * @throws SQLException
     */
    public SimpleHierarchyAdapter(Context contex, CatalogDao<T> dao, T parent, String order)
            throws SQLException {

        MetaAdapterViewBinder mavb = createMetaAdapterViewBinder(contex, dao.getDataClass(),
                android.R.id.text1);
        List<T> data = getHierarchyLinear(dao, parent, true, order);
        init(data, android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item, mavb);
    }

    /**
     * Иерархический выбор только справочника по родителю (если указан).
     *
     * @param context            текущий контекст
     * @param dao                используется для извлечения элементов
     * @param parent             родитель
     * @param foldersOnly        флаг выбора только групп, если ложь будут выбраны группы и
     *                           элементы
     * @param order              сортировка списка, например если указать
     *                           'Catalog.FIELD_NAME_FOLDER + " DESC, " +
     *                           Catalog.FIELD_NAME_DESCRIPTION;'
     * @param resource           идентификатор ресурса макета используемый для отображения
     *                           элементов списка
     * @param textViewResourceId идентификатор дочернего TextView используемый для отображения
     *                           текстового представления
     * @throws SQLException
     */
    public SimpleHierarchyAdapter(Context context, CatalogDao<T> dao, T parent, boolean foldersOnly,
            String order, int resource, int textViewResourceId) throws SQLException {

        MetaAdapterViewBinder mavb = createMetaAdapterViewBinder(context, dao.getDataClass(),
                textViewResourceId);
        List<T> data = getHierarchyLinear(dao, parent, foldersOnly, order);
        init(data, resource, resource, mavb);
    }

    private MetaAdapterViewBinder createMetaAdapterViewBinder(Context context, Class<?> classOf,
            int textViewResourceId) {

        MetaAdapterViewBinder mavb = new MetaAdapterViewBinder(context, classOf,
                new String[]{Catalog.FIELD_NAME_DESCRIPTION}, new int[]{textViewResourceId});
        mavb.setViewBinder(mHierarchyViewBinder);
        return mavb;
    }

    private void init(List<T> data, int resource, int dropDownResource,
            MetaAdapterViewBinder adapterViewBinder) {
        mData = data;
        mResource = resource;
        mDropDownResource = dropDownResource;
        mAdapterViewBinder = adapterViewBinder;
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

    /*
     * Получить иерархию как линейный список элементов
     */
    private List<T> getHierarchyLinear(CatalogDao<T> dao, T parent, boolean foldersOnly,
            String order) throws SQLException {

        GenericTree<T> tree = null;

        if (foldersOnly) {
            tree = dao.selectHierarchy(parent, null, order);
        } else {
            tree = dao.selectHierarchically(parent, order);
        }
        return dao.hierarchyToList(tree, false);
    }

    /*
     * Форматирование. Выводит представление с отступом (по уровню)
     */
    private MetaAdapterViewBinder.ViewBinder mHierarchyViewBinder = new MetaAdapterViewBinder.ViewBinder() {

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

                int level = ref.getLevel();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < level; i++) {
                    sb.append("  ");
                }
                sb.append(ref.getPresentation());
                ((TextView) v).setText(sb.toString());
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
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
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

}
