package ru.profi1c.engine.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;

/**
 * Базовый класс для построения адаптера иерархических списков (уровень иерархии
 * ограничен 2 т.е родитель-подчиненный) на базе курсора
 */
public abstract class MetaExpandableCursorAdapter extends CursorTreeAdapter {

    private static final boolean AUTO_REQUERY = false;

    private MetaAdapterViewBinder mGroupAdapterViewBinder;
    private MetaAdapterViewBinder mChildAdapterViewBinder;

    private int mExpandedGroupLayout;
    private int mCollapsedGroupLayout;

    private int mChildLayout;
    private int mLastChildLayout;

    /**
     * Конструктор. Используется один {@link MetaAdapterViewBinder} для
     * построения отображения групп и элементов, макет строк
     * родитель-подчиненный так же один
     *
     * @param context                текущий контекст
     * @param cursor                 курсор с данными группировки верхнего уровня
     * @param groupLayout            идентификатор ресурса макета используемый для отображения
     *                               групп и подчиненных элементов
     * @param groupAdapterViewBinder адаптер для отображения данных.
     */
    public MetaExpandableCursorAdapter(Context context, Cursor cursor, int groupLayout,
            MetaAdapterViewBinder groupAdapterViewBinder) {
        super(cursor, context, AUTO_REQUERY);
        init(groupLayout, groupLayout, groupAdapterViewBinder, groupLayout, groupLayout,
                groupAdapterViewBinder);
    }

    /**
     * Конструктор. Используются различные {@link MetaAdapterViewBinder} для
     * построения групп и элементов.
     *
     * @param context                текущий контекст
     * @param cursor                 курсор с данными группировки верхнего уровня
     * @param groupLayout            идентификатор ресурса макета используемый для отображения
     *                               групп
     * @param groupAdapterViewBinder адаптер для отображения данных.
     * @param childLayout            идентификатор ресурса макета используемый для отображения
     *                               подчиненных элементов
     * @param childAdapterViewBinder адаптер для отображения подчиненных элементов.
     */
    public MetaExpandableCursorAdapter(Context context, Cursor cursor, int groupLayout,
            MetaAdapterViewBinder groupAdapterViewBinder, int childLayout,
            MetaAdapterViewBinder childAdapterViewBinder) {
        super(cursor, context, AUTO_REQUERY);
        init(groupLayout, groupLayout, groupAdapterViewBinder, childLayout, childLayout,
                childAdapterViewBinder);
    }

    /**
     * Конструктор, адаптеры для отображения данных
     * {@link MetaAdapterViewBinder} создаются автоматически
     *
     * @param context     текущий контекст
     * @param classOf     класс данные которого содержатся в списке
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
    public MetaExpandableCursorAdapter(Context context, Class<?> classOf, Cursor cursor,
            int groupLayout, String[] groupFrom, int[] groupTo,
            MetaAdapterViewBinder.ViewBinder groupBinder, int childLayout, String[] childFrom,
            int[] childTo, MetaAdapterViewBinder.ViewBinder childBinder) {
        super(cursor, context, AUTO_REQUERY);
        MetaAdapterViewBinder groupAVB = new MetaAdapterViewBinder(context, classOf, groupFrom,
                groupTo);
        groupAVB.setViewBinder(groupBinder);
        MetaAdapterViewBinder childAVB = new MetaAdapterViewBinder(context, classOf, childFrom,
                childTo);
        childAVB.setViewBinder(childBinder);
        init(groupLayout, groupLayout, groupAVB, childLayout, childLayout, childAVB);
    }

    /**
     * Конструктор, адаптеры для отображения данных
     * {@link MetaAdapterViewBinder} создаются автоматически
     *
     * @param context              текущий контекст
     * @param classOf              класс данные которого содержатся в списке
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
    public MetaExpandableCursorAdapter(Context context, Class<?> classOf, Cursor cursor,
            int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
            MetaAdapterViewBinder.ViewBinder groupBinder, int childLayout, int lastChildLayout,
            String[] childFrom, int[] childTo, MetaAdapterViewBinder.ViewBinder childBinder) {
        super(cursor, context, AUTO_REQUERY);
        MetaAdapterViewBinder groupAVB = new MetaAdapterViewBinder(context, classOf, groupFrom,
                groupTo);
        groupAVB.setViewBinder(groupBinder);
        MetaAdapterViewBinder childAVB = new MetaAdapterViewBinder(context, classOf, childFrom,
                childTo);
        childAVB.setViewBinder(childBinder);
        init(expandedGroupLayout, collapsedGroupLayout, groupAVB, childLayout, lastChildLayout,
                childAVB);
    }

    private void init(int expandedGroupLayout, int collapsedGroupLayout,
            MetaAdapterViewBinder groupAdapterViewBinder, int childLayout, int lastChildLayout,
            MetaAdapterViewBinder childAdapterViewBinder) {

        mExpandedGroupLayout = expandedGroupLayout;
        mCollapsedGroupLayout = collapsedGroupLayout;

        mGroupAdapterViewBinder = groupAdapterViewBinder;

        mChildLayout = childLayout;
        mLastChildLayout = lastChildLayout;

        mChildAdapterViewBinder = childAdapterViewBinder;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded,
            ViewGroup parent) {
        return mGroupAdapterViewBinder.newView(cursor, parent,
                (isExpanded) ? mExpandedGroupLayout : mCollapsedGroupLayout);
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        mGroupAdapterViewBinder.bindView(cursor, view);
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild,
            ViewGroup parent) {
        return mChildAdapterViewBinder.newView(cursor, parent,
                (isLastChild) ? mLastChildLayout : mChildLayout);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        mChildAdapterViewBinder.bindView(cursor, view);
    }

}
