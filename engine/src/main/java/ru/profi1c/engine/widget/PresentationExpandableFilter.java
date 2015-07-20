package ru.profi1c.engine.widget;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import ru.profi1c.engine.meta.IPresentation;

/**
 * Фильтр по представлению дочерних элементов двухуровневого списка(сравнение
 * без учета регистра символов)
 */
public class PresentationExpandableFilter extends BaseExpandableListFilter<IPresentation> {

    private static final String METHOD_NAME = "getPresentation";
    private Method mMethod;

    public PresentationExpandableFilter(List<IPresentation> groupData,
            Map<IPresentation, List<IPresentation>> childData,
            BaseExpandableListFilter.FilterCompletedListener completedListener) {
        super(groupData, childData, completedListener);
    }

    @Override
    protected Method getSourceDataMethod(IPresentation t) throws NoSuchMethodException {
        if (mMethod == null) {
            mMethod = IPresentation.class.getMethod(METHOD_NAME, new Class[]{});
        }
        return mMethod;
    }

}
