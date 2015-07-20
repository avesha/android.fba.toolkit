package ru.profi1c.engine.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.ValueStorage;

/**
 * Помощник построения дочерних View объекта отображаемых в адаптерах
 */
public class MetaAdapterViewBinder extends BaseViewBinder {

    /**
     * Пользовательская реализация установки значения для дочерних View объекта
     * MetaAdapter-a
     */
    public interface ViewBinder {

        /**
         * Создать класс-врапер для эффективной работы с представлениями строк
         * адаптера
         *
         * @param root
         * @return
         */
        BaseViewHolder createViewHolder(View root);

        /**
         * Вызывается непосредственно перед построением строки данных в адаптере
         *
         * @param viewHolder созданный класс-врапер,см {@link #createViewHolder}
         * @param position   позиция в адаптере
         */
        void onBind(BaseViewHolder viewHolder, int position);

        /**
         * Выводит данные указанного поля класса на View (для адаптеров типа
         * {@link MetaArrayAdapter} )
         *
         * @return истина, если данные указанного поля класса были отображены на
         * View и ложь в противном случае
         */
        boolean setViewValue(View view, Object item, Field field);

        /**
         * Выводит данные указанного поля класса на View (для адаптеров типа
         * {@link MetaCursorAdapter}
         *
         * @param view   для отображение элемента
         * @param cursor курсор
         * @param field  поле класса
         * @return return истина, если данные указанного поля класса были
         * отображены на View и ложь в противном случае
         */
        boolean setViewValue(View view, Cursor cursor, Field field);
    }

    private final Class<?> mClass;
    private final Collection<Field> mFields;

    private ViewBinder mViewBinder;
    private final int[] mTo;
    private final String[] mFrom;

    private final LayoutInflater mInflater;

    public MetaAdapterViewBinder(Context context, Class<?> classMeta, String[] from, int[] to) {
        mClass = classMeta;
        mFields = MetadataHelper.getFields(classMeta);
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Class<?> getMetaClass() {
        return mClass;
    }

    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    private View inflateView(ViewGroup parent, int resource) {
        View view = mInflater.inflate(resource, parent, false);
        if (mViewBinder != null) {
            BaseViewHolder vh = mViewBinder.createViewHolder(view);
            if (vh != null) {
                view.setTag(vh);
            }
        }
        return view;
    }

    /**
     * Создать вид для отображения строки данных Cursor - адаптера
     *
     * @param cursor   Курсор, из которого нужно получить данные. Курсор уже
     *                 находится в корректной позиции.
     * @param parent   родитель, к которому в конечном итоге будет присоединен View
     * @param resource Идентификатор ресурса макета используемый для отображения
     *                 элементов списка
     * @return
     */
    public View newView(Cursor cursor, ViewGroup parent, int resource) {
        return inflateView(parent, resource);
    }

    /**
     * Создать вид для отображения строки данных адаптера
     *
     * @param position позиция элемента в адаптере
     * @param item     элемент, данные котрого будут отображаться в строке
     * @param parent   родитель, к которому в конечном итоге будет присоединен View
     * @param resource Идентификатор ресурса макета используемый для отображения
     *                 элементов списка
     * @return
     */
    public View newView(int position, Object item, ViewGroup parent, int resource) {
        return inflateView(parent, resource);
    }

    /**
     * Построить отображение элемента по курсору
     *
     * @param view   для отображения элемента адаптера
     * @param cursor позиционированный курсор
     */
    public void bindView(Cursor cursor, View view) {
        final int position = cursor.getPosition();
        bindViewOnDataSource(position, cursor, view);
    }

    /**
     * Построить отображение элемента
     *
     * @param position текущая позиция элемента в адаптере
     * @param item     отображаемый элемент
     * @param view     view для отображения элемента адаптера
     */
    public void bindView(int position, Object item, View view) {
        bindViewOnDataSource(position, item, view);
    }

    /*
     * Построить view по курсору или по объекту
     */
    private void bindViewOnDataSource(int position, Object source, View view) {

        final ViewBinder binder = mViewBinder;
        final BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();

        // update position on holder
        if (binder != null && viewHolder != null) {
            binder.onBind(viewHolder, position);
        }

        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = from.length;

        for (int i = 0; i < count; i++) {
            String fieldName = from[i];
            Field field = MetadataHelper.findField(mFields, fieldName, true);
            if (field != null) {

                // find filed view
                int id = to[i];
                View v = null;

                if (viewHolder != null) {
                    v = viewHolder.getViewById(id);
                }
                if (v == null) {
                    v = view.findViewById(id);
                }

                // set value to child view
                if (v != null) {

                    boolean bound = false;
                    if (binder != null) {
                        if (source instanceof Cursor) {
                            bound = binder.setViewValue(v, (Cursor) source, field);
                        } else {
                            bound = binder.setViewValue(v, source, field);
                        }
                    }

                    if (!bound) {

                        if (source instanceof Cursor) {

                            Object value = getCursorFieldValue((Cursor) source, field);
                            buildView(source, field, value, v);

                        } else {

                            Object value = getObjectFieldValue(source, field);
                            buildView(source, field, value, v);

                        }
                    }
                }
            }
        }
    }

    /*
     * Получить значение поля класса из курсора
     */
    private Object getCursorFieldValue(Cursor cursor, Field field) {
        Object value = null;
        String columnName = MetadataHelper.getDBColumnName(field);
        if (columnName != null) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex != Const.NOT_SPECIFIED && !cursor.isNull(columnIndex)) {

                Class<?> classOfF = field.getType();

                // в порядке чаще встречающихся
                if (MetadataHelper.isStringClass(classOfF)) {
                    value = cursor.getString(columnIndex);
                } else if (MetadataHelper.isBooleanClass(classOfF)) {
                    value = (cursor.getInt(columnIndex) > 0) ? Boolean.TRUE : Boolean.FALSE;
                } else if (MetadataHelper.isIntegerClass(classOfF)) {
                    value = cursor.getInt(columnIndex);
                } else if (MetadataHelper.isDoubleClass(classOfF)) {
                    value = cursor.getDouble(columnIndex);
                } else if (MetadataHelper.isDataClass(classOfF)) {
                    long msDate = cursor.getLong(columnIndex);
                    value = new Date(msDate);
                } else if (MetadataHelper.isLongClass(classOfF)) {
                    value = cursor.getLong(columnIndex);
                } else if (MetadataHelper.isBigIntegerClass(classOfF)) {
                    String strNumber = cursor.getString(columnIndex);
                    value = new BigInteger(strNumber);
                } else if (MetadataHelper.isBlobClass(classOfF)) {
                    byte[] data = cursor.getBlob(columnIndex);
                    value = new ValueStorage(data);
                } else if (MetadataHelper.isFloatClass(classOfF)) {
                    value = cursor.getFloat(columnIndex);
                } else if (MetadataHelper.isShortClass(classOfF)) {
                    value = cursor.getShort(columnIndex);
                }
            }
        }
        return value;
    }

    /*
     * Получить значение поля от объекта
     */
    private Object getObjectFieldValue(Object obj, Field field) {
        Object value = null;
        try {
            value = field.get(obj);
        } catch (IllegalArgumentException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return value;
    }

}
