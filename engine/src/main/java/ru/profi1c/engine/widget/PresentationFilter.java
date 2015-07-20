package ru.profi1c.engine.widget;

import java.lang.reflect.Method;
import java.util.List;

import ru.profi1c.engine.meta.IPresentation;

/**
 * Фильтр по представлению (сравнение без учета регистра символов)
 */
public class PresentationFilter extends BaseListFilter<IPresentation> {

    private static final String METHOD_NAME = "getPresentation";
    private Method mMethod;

    public PresentationFilter(List<IPresentation> data, FilterCompletedListener completedListener) {
        super(data, completedListener);
    }

    @Override
    protected Method getSourceDataMethod(IPresentation t) throws NoSuchMethodException {
        if (mMethod == null) {
            mMethod = IPresentation.class.getMethod(METHOD_NAME, new Class[]{});
        }
        return mMethod;
    }

}
