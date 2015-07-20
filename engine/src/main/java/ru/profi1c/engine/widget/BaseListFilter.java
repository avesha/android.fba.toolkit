package ru.profi1c.engine.widget;

import android.widget.Filter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.Dbg;

/**
 * Фильтрация элементов списка по одному полю класса (по текстовому
 * представлению значения, частичное совпадение без учета регистра). В
 * наследнике определяется метод класса (см. {@link #getSourceDataMethod(Object)
 * )}который используется для получения сравниваемых значений в коллекции
 *
 * @param <T>
 */
public abstract class BaseListFilter<T> extends Filter {

    private FilterCompletedListener mCompletedListener;
    private List<T> mData;
    private List<T> mUnfilteredData;

    public interface FilterCompletedListener {
        void publishResults(List<?> data);
    }

    /**
     * Метод класса используемый для получения фильтруемых значений, например
     * 'getCode', 'getDescription', 'getPresentation'. Ожидается что метод
     * возвращает строку и не имеет входящих параметров.
     *
     * @param t
     * @return
     * @throws NoSuchMethodException
     */
    protected abstract Method getSourceDataMethod(T t) throws NoSuchMethodException;

    public BaseListFilter(List<T> data, FilterCompletedListener completedListener) {
        mData = data;
        mCompletedListener = completedListener;
    }

    /**
     * Сохранить коллекцию всех (неотфильтрованных данных). Исходная коллекция
     * сохраняется автоматически при фильтрации, в ручную сохранять следует при
     * динамическом изменении исходных данных, например для иерархического
     * справочника при отображении подчиненных элементов
     *
     * @param data
     */
    public void setUnfilteredData(List<T> data) {
        mUnfilteredData = new ArrayList<T>(data);
    }

    private String getSourceData(T t) {
        String result = null;
        Method method;
        try {
            method = getSourceDataMethod(t);
            if (method != null) {
                Object ret;
                ret = method.invoke(t, new Object[]{});
                if (ret != null) {
                    result = ret.toString();
                }
            }

        } catch (Exception e) {
            Dbg.printStackTrace(e);
        }

        return result;
    }

    @Override
    protected FilterResults performFiltering(CharSequence prefix) {

        FilterResults results = new FilterResults();

        if (mUnfilteredData == null) {
            setUnfilteredData(mData);
        }

        if (prefix == null || prefix.length() == 0) {
            List<T> list = mUnfilteredData;
            results.values = list;
            results.count = list.size();
        } else {
            String pattern = ".*" + prefix.toString().toLowerCase() + ".*";

            List<T> unfilteredValues = mUnfilteredData;
            int count = unfilteredValues.size();

            ArrayList<T> newValues = new ArrayList<T>(count);
            for (int i = 0; i < count; i++) {

                T h = unfilteredValues.get(i);

                if (h != null) {

                    String source = getSourceData(h);
                    if (source != null) {
                        if (source.toLowerCase().matches(pattern)) {
                            newValues.add(h);
                        }
                    }

                }
            }
            results.values = newValues;
            results.count = newValues.size();
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mData = (List<T>) results.values;
        mCompletedListener.publishResults(mData);
    }

}
