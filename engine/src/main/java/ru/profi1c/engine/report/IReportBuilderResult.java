package ru.profi1c.engine.report;

/**
 * Результат работы построителя отчета
 */
public interface IReportBuilderResult {

    /**
     * Построение отчета завершено.
     *
     * @param data строка или uri-ссылка на внешний скомпилированный отчета. Если
     *             null, не ошибка, а нет исходных данных для построения отчета
     */
    void onComplete(Object data);

    /**
     * Произошла ошибка во время построения отчета
     *
     * @param msg Сообщение об ошибке
     */
    void onError(String msg);
}
