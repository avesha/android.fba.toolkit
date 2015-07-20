package ru.profi1c.engine.report;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Базовый класс всех отчетов. Содержит ссылку на текущий контекст и построителя
 * отчета
 */
public abstract class BaseReport implements IReport, IReportBuilderResult {

    private WeakReference<Context> mRefContext;

    /**
     * Получить построитель отчета
     */
    public abstract IReportBuilder getReportBuilder();

    @Override
    public void onShow(Context context) {
        mRefContext = new WeakReference<Context>(context);

        IReportBuilder builder = getReportBuilder();
        builder.build(context, this);
    }

    /**
     * Получить контекст построения отчета, выбросит исключение
     * NullPointerException если вызвать до вызова {@link #onShow(Context)}. <br>
     * Может возвращать null, если контекст уже уничтожен сборщиком мусора
     */
    public Context getContext() {
        if (mRefContext == null) {
            throw new NullPointerException("You must first call the 'show' method!");
        }
        return mRefContext.get();
    }

}
