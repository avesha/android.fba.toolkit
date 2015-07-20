package ru.profi1c.engine.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.widget.BaseListFilter.FilterCompletedListener;

/**
 * Адаптер для отображения небольших списков (список справочника, список
 * документов). Если в списке ожидается большое количество элементов,
 * рекомендуется использовать {@link MetaCursorAdapter}
 *
 * @param <T>
 */
public class MetaArrayAdapter<T> extends BaseAdapter implements Filterable {

    private MetaAdapterViewBinder mAdapterViewBinder;
    private int mResource;
    private int mDropDownResource;

    private PresentationFilter mFilter;
    private Class<T> classOfT;
    private List<T> mData;

    /**
     * Конструктор адаптера
     *
     * @param data              список данных
     * @param resource          идентификатор ресурса макета используемый для отображения
     *                          элементов списка
     * @param adapterViewBinder адаптер для отображения данных
     */
    public MetaArrayAdapter(List<T> data, int resource, MetaAdapterViewBinder adapterViewBinder) {
        init(data, resource, adapterViewBinder);
    }

    /**
     * Конструктор адаптера. Адаптер для отображения данных
     * {@link MetaAdapterViewBinder} создается автоматически
     *
     * @param context  текущий контекст
     * @param classOfT класс данные которого содержатся в списке
     * @param data     список данных
     * @param resource идентификатор ресурса макета используемый для отображения
     *                 элементов списка
     * @param from     имена полей класса
     * @param to       идентификаторы дочерних View используемых для отображения
     *                 полей
     */
    public MetaArrayAdapter(Context context, Class<T> classOfT, List<T> data, int resource,
            String[] from, int[] to) {
        MetaAdapterViewBinder adapterViewBinder = new MetaAdapterViewBinder(context, classOfT, from,
                to);
        init(data, resource, adapterViewBinder);
    }

    /**
     * Конструктор адаптера. Адаптер для отображения данных
     * {@link MetaAdapterViewBinder} создается автоматически
     *
     * @param context    текущий контекст
     * @param classOfT   класс данные которого содержатся в списке
     * @param data       список данных
     * @param resource   идентификатор ресурса макета используемый для отображения
     *                   элементов списка
     * @param from       имена полей класса
     * @param to         идентификаторы дочерних View используемых для отображения
     *                   полей
     * @param viewBinder внешний класс для привязки значений к представлениям полей
     */
    public MetaArrayAdapter(Context context, Class<T> classOfT, List<T> data, int resource,
            String[] from, int[] to, MetaAdapterViewBinder.ViewBinder viewBinder) {
        MetaAdapterViewBinder adapterViewBinder = new MetaAdapterViewBinder(context, classOfT, from,
                to);
        adapterViewBinder.setViewBinder(viewBinder);
        init(data, resource, adapterViewBinder);
    }

    @SuppressWarnings("unchecked")
    private void init(List<T> data, int resource, MetaAdapterViewBinder adapterViewBinder) {
        mData = data;
        classOfT = (Class<T>) adapterViewBinder.getMetaClass();
        mResource = mDropDownResource = resource;
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

    @SuppressWarnings("unchecked")
    @Override
    public Filter getFilter() {
        if (IPresentation.class.isAssignableFrom(classOfT)) {
            if (mFilter == null) {
                mFilter = new PresentationFilter((List<IPresentation>) mData,
                        mCompletedFilteredListener);
            }
            return mFilter;
        }
        throw new FbaRuntimeException("Can not cast a collection as List<IPresentation>!");
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
