package ru.profi1c.engine.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.widget.BaseExpandableListFilter.ExFilterResults;

/**
 * Адаптер для отображение небольших иерархических списков (причем уровень
 * иерархии ограничен, только 2 уровня т.е родитель-подчиненный. Если в списке
 * ожидается большое количество элементов, используете
 * {@link MetaExpandableCursorAdapter}
 */
public class MetaExpandableListAdapter<T> extends BaseExpandableListAdapter implements Filterable {

    private MetaAdapterViewBinder mGroupAdapterViewBinder;
    private MetaAdapterViewBinder mChildAdapterViewBinder;

    private PresentationExpandableFilter mFilter;
    private Class<T> classOfT;

    private List<T> mGroupData;
    private int mExpandedGroupLayout;
    private int mCollapsedGroupLayout;

    private Map<T, List<T>> mChildData;
    private int mChildLayout;
    private int mLastChildLayout;

    /**
     * Конструктор адаптера, используется один {@link MetaAdapterViewBinder} для
     * построения отображения групп и элементов.
     *
     * @param groupData              список групп
     * @param groupLayout            идентификатор ресурса макета используемый для отображения
     *                               групп
     * @param groupAdapterViewBinder адаптер для отображения данных.
     * @param childData              подчиненные элементы
     * @param childLayout            идентификатор ресурса макета используемый для отображения
     *                               подчиненных элементов
     */
    public MetaExpandableListAdapter(List<T> groupData, int groupLayout,
            MetaAdapterViewBinder groupAdapterViewBinder, Map<T, List<T>> childData,
            int childLayout) {

        init(groupData, groupLayout, groupLayout, groupAdapterViewBinder, childData, childLayout,
                childLayout, groupAdapterViewBinder);
    }

    /**
     * Конструктор адаптера
     *
     * @param groupData              список групп
     * @param groupLayout            идентификатор ресурса макета используемый для отображения
     *                               групп
     * @param groupAdapterViewBinder адаптер для отображения данных групп
     * @param childData              подчиненные элементы
     * @param childLayout            идентификатор ресурса макета используемый для отображения
     *                               подчиненных элементов
     * @param childAdapterViewBinder адаптер для отображения подчиненных элементов
     */
    public MetaExpandableListAdapter(List<T> groupData, int groupLayout,
            MetaAdapterViewBinder groupAdapterViewBinder, Map<T, List<T>> childData,
            int childLayout, MetaAdapterViewBinder childAdapterViewBinder) {

        init(groupData, groupLayout, groupLayout, groupAdapterViewBinder, childData, childLayout,
                childLayout, childAdapterViewBinder);
    }

    /**
     * Конструктор адаптера, адаптеры для отображения данных
     * {@link MetaAdapterViewBinder} создаются автоматически
     *
     * @param context     текущий контекст
     * @param classOfT    класс данные которого содержатся в списке
     * @param groupData   подчиненные элементы
     * @param groupLayout идентификатор ресурса макета используемый для отображения
     *                    групп
     * @param groupFrom   имена полей класса
     * @param groupTo     идентификаторы дочерних View используемых для отображения
     *                    полей
     * @param childData   подчиненные элементы
     * @param childLayout идентификатор ресурса макета используемый для отображения
     *                    подчиненных элементов
     * @param childFrom   имена полей класса для отображения элементов
     * @param childTo     идентификаторы дочерних View используемых для отображения
     *                    полей подчиненных элементов
     */
    public MetaExpandableListAdapter(Context context, Class<T> classOfT, List<T> groupData,
            int groupLayout, String[] groupFrom, int[] groupTo, Map<T, List<T>> childData,
            int childLayout, String[] childFrom, int[] childTo) {

        MetaAdapterViewBinder groupAVB = new MetaAdapterViewBinder(context, classOfT, groupFrom,
                groupTo);
        MetaAdapterViewBinder childAVB = new MetaAdapterViewBinder(context, classOfT, childFrom,
                childTo);

        init(groupData, groupLayout, groupLayout, groupAVB, childData, childLayout, childLayout,
                childAVB);
    }

    /**
     * Конструктор адаптера, адаптеры для отображения данных
     * {@link MetaAdapterViewBinder} создаются автоматически
     *
     * @param context     текущий контекст
     * @param classOfT    класс данные которого содержатся в списке
     * @param groupData   подчиненные элементы
     * @param groupLayout идентификатор ресурса макета используемый для отображения
     *                    групп
     * @param groupFrom   имена полей класса
     * @param groupTo     идентификаторы дочерних View используемых для отображения
     *                    полей
     * @param groupBinder внешний класс для привязки значений группы к представлениям
     *                    полей
     * @param childData   подчиненные элементы
     * @param childLayout идентификатор ресурса макета используемый для отображения
     *                    подчиненных элементов
     * @param childFrom   имена полей класса для отображения элементов
     * @param childTo     идентификаторы дочерних View используемых для отображения
     *                    полей подчиненных элементов
     * @param childBinder внешний класс для привязки значений подчиненных элементов к
     *                    представлениям полей
     */
    public MetaExpandableListAdapter(Context context, Class<T> classOfT, List<T> groupData,
            int groupLayout, String[] groupFrom, int[] groupTo,
            MetaAdapterViewBinder.ViewBinder groupBinder, Map<T, List<T>> childData,
            int childLayout, String[] childFrom, int[] childTo,
            MetaAdapterViewBinder.ViewBinder childBinder) {

        MetaAdapterViewBinder groupAVB = new MetaAdapterViewBinder(context, classOfT, groupFrom,
                groupTo);
        groupAVB.setViewBinder(groupBinder);

        MetaAdapterViewBinder childAVB = new MetaAdapterViewBinder(context, classOfT, childFrom,
                childTo);
        childAVB.setViewBinder(childBinder);

        init(groupData, groupLayout, groupLayout, groupAVB, childData, childLayout, childLayout,
                childAVB);
    }

    public MetaExpandableListAdapter(Context context, Class<T> classOfT, List<T> groupData,
            int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
            MetaAdapterViewBinder.ViewBinder groupBinder, Map<T, List<T>> childData,
            int childLayout, int lastChildLayout, String[] childFrom, int[] childTo,
            MetaAdapterViewBinder.ViewBinder childBinder) {

        MetaAdapterViewBinder groupAVB = new MetaAdapterViewBinder(context, classOfT, groupFrom,
                groupTo);
        groupAVB.setViewBinder(groupBinder);

        MetaAdapterViewBinder childAVB = new MetaAdapterViewBinder(context, classOfT, childFrom,
                childTo);
        childAVB.setViewBinder(childBinder);

        init(groupData, expandedGroupLayout, collapsedGroupLayout, groupAVB, childData, childLayout,
                lastChildLayout, childAVB);

    }

    @SuppressWarnings("unchecked")
    private void init(List<T> groupData, int expandedGroupLayout, int collapsedGroupLayout,
            MetaAdapterViewBinder groupAdapterViewBinder, Map<T, List<T>> childData,
            int childLayout, int lastChildLayout, MetaAdapterViewBinder childAdapterViewBinder) {

        mGroupData = groupData;
        mExpandedGroupLayout = expandedGroupLayout;
        mCollapsedGroupLayout = collapsedGroupLayout;

        mGroupAdapterViewBinder = groupAdapterViewBinder;

        mChildData = childData;
        mChildLayout = childLayout;
        mLastChildLayout = lastChildLayout;

        mChildAdapterViewBinder = childAdapterViewBinder;

        classOfT = (Class<T>) groupAdapterViewBinder.getMetaClass();
    }

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        T group = getGroup(groupPosition);
        return mChildData.get(group).size();
    }

    @Override
    public T getGroup(int groupPosition) {
        return mGroupData.get(groupPosition);
    }

    @Override
    public T getChild(int groupPosition, int childPosition) {
        T group = getGroup(groupPosition);
        return mChildData.get(group).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {

        View v;
        T item = getGroup(groupPosition);

        if (convertView == null) {
            v = mGroupAdapterViewBinder.newView(groupPosition, item, parent,
                    (isExpanded) ? mExpandedGroupLayout : mCollapsedGroupLayout);
        } else {
            v = convertView;
        }

        mGroupAdapterViewBinder.bindView(groupPosition, item, v);
        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {

        View v;
        T item = getChild(groupPosition, childPosition);

        if (convertView == null) {
            v = mChildAdapterViewBinder.newView(childPosition, item, parent,
                    (isLastChild) ? mLastChildLayout : mChildLayout);
        } else {
            v = convertView;
        }

        mChildAdapterViewBinder.bindView(childPosition, item, v);
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Filter getFilter() {
        if (IPresentation.class.isAssignableFrom(classOfT)) {

            if (mFilter == null) {
                Map<IPresentation, List<IPresentation>> mapTmp = new HashMap<IPresentation, List<IPresentation>>(
                        mChildData.size());
                for (T group : mChildData.keySet()) {
                    List<IPresentation> childList = (List<IPresentation>) mChildData.get(group);
                    mapTmp.put((IPresentation) group, childList);
                }
                mFilter = new PresentationExpandableFilter((List<IPresentation>) mGroupData, mapTmp,
                        mCompletedFilteredListener);
            }
            return mFilter;
        }
        throw new FbaRuntimeException("Can not cast a collection as List<IPresentation>!");
    }

    private BaseExpandableListFilter.FilterCompletedListener mCompletedFilteredListener = new BaseExpandableListFilter.FilterCompletedListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void publishResults(ExFilterResults<?> result) {

            ExFilterResults<T> exResult = (ExFilterResults<T>) result;
            mGroupData = exResult.mGroupData;
            mChildData = exResult.mChildData;
            if (mGroupData.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

    };
}
