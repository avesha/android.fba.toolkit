package ru.profi1c.engine.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Универсальный адаптер для отображения списка (справочников, документов,
 * внешних таблиц)
 */
public class MetaCursorAdapter extends CursorAdapter {

    private MetaAdapterViewBinder mAdapterViewBinder;
    private int mResource;
    private int mDropDownResource;

    /**
     * Конструктор адаптера
     *
     * @param context           текущий контекст
     * @param cursor            курсор с данными
     * @param resource          идентификатор ресурса макета используемый для отображения
     *                          элементов списка
     * @param adapterViewBinder адаптер для отображения данных
     */
    public MetaCursorAdapter(Context context, Cursor cursor, int resource,
            MetaAdapterViewBinder adapterViewBinder) {
        super(context, cursor, false);
        init(resource, adapterViewBinder);
    }

    /**
     * Конструктор адаптера. Адаптер для отображения данных
     * {@link MetaAdapterViewBinder} создается автоматически
     *
     * @param context  текущий контекст
     * @param classOf  класс данные которого содержатся в списке
     * @param cursor   курсор с данными
     * @param resource идентификатор ресурса макета используемый для отображения
     *                 элементов списка
     * @param from     имена полей класса
     * @param to       идентификаторы дочерних View используемых для отображения
     *                 полей
     */
    public MetaCursorAdapter(Context context, Class<?> classOf, Cursor cursor, int resource,
            String[] from, int[] to) {
        super(context, cursor, false);
        MetaAdapterViewBinder adapterViewBinder = new MetaAdapterViewBinder(context, classOf, from,
                to);
        init(resource, adapterViewBinder);
    }

    /**
     * Конструктор адаптера. Адаптер для отображения данных
     * {@link MetaAdapterViewBinder} создается автоматически
     *
     * @param context    текущий контекст
     * @param classOf    класс данные которого содержатся в списке
     * @param cursor     курсор с данными
     * @param resource   идентификатор ресурса макета используемый для отображения
     *                   элементов списка
     * @param from       имена полей класса
     * @param to         идентификаторы дочерних View используемых для отображения
     *                   полей
     * @param viewBinder внешний класс для привязки значений к представлениям полей
     */
    public MetaCursorAdapter(Context context, Class<?> classOf, Cursor cursor, int resource,
            String[] from, int[] to, MetaAdapterViewBinder.ViewBinder viewBinder) {
        super(context, cursor, false);
        MetaAdapterViewBinder adapterViewBinder = new MetaAdapterViewBinder(context, classOf, from,
                to);
        adapterViewBinder.setViewBinder(viewBinder);
        init(resource, adapterViewBinder);
    }

    private void init(int resource, MetaAdapterViewBinder adapterViewBinder) {
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

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mAdapterViewBinder.newView(cursor, parent, mResource);
    }

    @Override
    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        return mAdapterViewBinder.newView(cursor, parent, mDropDownResource);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mAdapterViewBinder.bindView(cursor, view);
    }

}
