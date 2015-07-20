package ru.profi1c.engine.report;

import android.content.Context;

/**
 * Построитель отчета
 */
public interface IReportBuilder {

    /**
     * Построить отчет
     *
     * @param context       Текущий контекст
     * @param builderResult Результат работы построителя отчета
     */
    void build(Context context, IReportBuilderResult builderResult);
}
