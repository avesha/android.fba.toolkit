package ru.profi1c.engine.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.widget.BaseListFilter.FilterCompletedListener;

/**
 * Простой адаптер для отображения списка справочника, документов или значений
 * перечислений (объектов реализующих интерфейс {@link IPresentation}).
 * Отображается только тестовое представление объекта
 */
public class PresentationAdapter extends BaseAdapter implements Filterable {

    private int mDropDownResource;
    private int mResource;
    private int mTextViewResourceId;
    private LayoutInflater mLi;

    private List<IPresentation> mData;
    private PresentationFilter mFilter;

    /**
     * Конструктор. Для элементов в списке используется макет
     * {@link android.R.layout.simple_spinner_item} т.е элементы будут выглядеть
     * внешне так же как у стандартного Spiner-а
     *
     * @param context текущий контекст.
     * @param objects массив объектов для представления в элементе управления
     *                ListView.
     */
    public PresentationAdapter(Context context, IPresentation[] objects) {
        init(context, Arrays.asList(objects), android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
    }

    /**
     * Конструктор. Для элементов в списке используется макет
     * {@link android.R.layout.simple_spinner_item} т.е элементы будут выглядеть
     * внешне так же как у стандартного Spiner-а
     *
     * @param context текущий контекст.
     * @param objects список объектов для представления в элементе управления
     *                ListView.
     */
    public PresentationAdapter(Context context, List<IPresentation> objects) {
        init(context, objects, android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
    }

    /**
     * Конструктор.
     *
     * @param context  текущий контекст.
     * @param resource Идентификатор ресурса макета используемый для отображения
     *                 элементов списка (наследник от TextView).
     * @param objects  массив объектов для представления в элементе управления
     *                 ListView.
     */
    public PresentationAdapter(Context context, int resource, IPresentation[] objects) {
        init(context, Arrays.asList(objects), resource, resource, 0);
    }

    /**
     * Конструктор
     *
     * @param context            текущий контекст.
     * @param resource           идентификатор ресурса макета используемый для отображения
     *                           элементов списка
     * @param textViewResourceId идентификатор дочернего TextView используемый для отображения
     *                           текстового представления
     * @param objects            массив объектов для представления в элементе управления
     *                           ListView.
     */
    public PresentationAdapter(Context context, int resource, int textViewResourceId,
            IPresentation[] objects) {
        init(context, Arrays.asList(objects), resource, resource, textViewResourceId);
    }

    /**
     * Конструктор.
     *
     * @param context  текущий контекст.
     * @param resource Идентификатор ресурса макета используемый для отображения
     *                 элементов списка (наследник от TextView).
     * @param objects  массив объектов для представления в элементе управления
     *                 ListView.
     */
    public PresentationAdapter(Context context, int resource, List<IPresentation> objects) {
        init(context, objects, resource, resource, 0);
    }

    /**
     * Конструктор.
     *
     * @param context  текущий контекст.
     * @param resource Идентификатор ресурса макета используемый для отображения
     *                 элементов списка (наследник от TextView).
     * @param objects  список объектов для представления в элементе управления
     *                 ListView.
     */
    public PresentationAdapter(Context context, int resource, int textViewResourceId,
            List<IPresentation> objects) {
        init(context, objects, resource, resource, textViewResourceId);
    }

    private void init(Context context, List<IPresentation> data, int resource, int dropDownResource,
            int textViewResourceId) {
        mResource = resource;
        mData = data;
        mTextViewResourceId = textViewResourceId;
        mDropDownResource = dropDownResource;
        mLi = LayoutInflater.from(context);
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
    public IPresentation getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(mDropDownResource, position, convertView, parent);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        return getView(mResource, position, v, parent);
    }

    // custom view
    private View getView(int resLayout, int position, View convertView, ViewGroup parent) {

        View view;
        TextView text;

        if (convertView == null) {
            view = mLi.inflate(resLayout, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mTextViewResourceId == 0) {
                // If no custom field is assigned, assume the whole resource is
                // a TextView
                text = (TextView) view;
            } else {
                // Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mTextViewResourceId);
            }
        } catch (ClassCastException e) {
            throw new FbaRuntimeException("ArrayAdapter requires the resource ID to be a TextView",
                    e);
        }

        IPresentation item = getItem(position);
        text.setText(item.getPresentation());
        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new PresentationFilter(mData, mCompletedFilteredListener);
        }
        return mFilter;
    }

    private BaseListFilter.FilterCompletedListener mCompletedFilteredListener = new FilterCompletedListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void publishResults(List<?> data) {
            mData = (List<IPresentation>) data;
            if (data.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    };
}
