package ru.profi1c.engine.widget;

import android.widget.Filter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.profi1c.engine.Dbg;

/**
 * Фильтрация двухуровневого списка, поиск по частичному совпадению значения у
 * дочерних элементов В наследнике определяется метод класса (см.
 * {@link #getSourceDataMethod(Object) )} который используется для получения
 * сравниваемых значений в коллекции
 *
 * @param <T>
 */
public abstract class BaseExpandableListFilter<T> extends Filter {

    private FilterCompletedListener mCompletedListener;

    private List<T> mGroupData;
    private Map<T, List<T>> mChildData;

    private List<T> mUnfilteredGroupData;
    private Map<T, List<T>> mUnfilteredChildData;

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

    public BaseExpandableListFilter(List<T> groupData, Map<T, List<T>> childData,
            FilterCompletedListener completedListener) {
        mGroupData = groupData;
        mChildData = childData;
        mCompletedListener = completedListener;
    }

    /**
     * Сохранить коллекцию всех (неотфильтрованных данных). Исходная коллекция
     * сохраняется автоматически при фильтрации, в ручную сохранять следует при
     * динамическом изменении исходных данных, например для иерархического
     * справочника при отображении подчиненных элементов
     *
     * @param groupData
     * @param childData
     */
    public void setUnfilteredData(List<T> groupData, Map<T, List<T>> childData) {
        mUnfilteredGroupData = new ArrayList<T>(groupData);
        mUnfilteredChildData = new HashMap<T, List<T>>(childData);
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
    protected FilterResults performFiltering(final CharSequence prefix) {

        FilterResults results = new FilterResults();

        if (mUnfilteredGroupData == null) {
            setUnfilteredData(mGroupData, mChildData);
        }

        if (prefix == null || prefix.length() == 0) {
            results.values = new ExFilterResults<T>(mUnfilteredGroupData, mUnfilteredChildData);
            results.count = mUnfilteredChildData.size();
        } else {
            //фильтрация
            String pattern = ".*" + prefix.toString().toLowerCase() + ".*";

            List<T> newGroupData = new ArrayList<T>();
            Map<T, List<T>> newChildData = new HashMap<T, List<T>>();

            for (T group : mUnfilteredChildData.keySet()) {
                List<T> childList = mUnfilteredChildData.get(group);

                List<T> newChild = new ArrayList<T>();

                int count = childList.size();
                for (int i = 0; i < count; i++) {
                    T child = childList.get(i);

                    String source = getSourceData(child);
                    if (source != null) {
                        if (source.toLowerCase().matches(pattern)) {
                            newChild.add(child);
                        }
                    }
                }

                if (newChild.size() > 0) {
                    newGroupData.add(group);
                    newChildData.put(group, newChild);
                }

            }

            results.values = new ExFilterResults<T>(newGroupData, newChildData);
            results.count = newGroupData.size();

        }
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        ExFilterResults<T> exResult = (ExFilterResults<T>) results.values;
        mGroupData = exResult.mGroupData;
        mChildData = exResult.mChildData;
        mCompletedListener.publishResults(exResult);
    }

    public interface FilterCompletedListener {
        void publishResults(ExFilterResults<?> result);
    }

    /*
     * Результат фильтрации
     */
    public static class ExFilterResults<T> {
        List<T> mGroupData;
        Map<T, List<T>> mChildData;

        public ExFilterResults(List<T> groupData, Map<T, List<T>> childData) {
            this.mGroupData = groupData;
            this.mChildData = childData;
        }

    }
}
