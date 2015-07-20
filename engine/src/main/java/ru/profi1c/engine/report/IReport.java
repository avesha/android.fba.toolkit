package ru.profi1c.engine.report;

import android.content.Context;

/**
 * Общий интерфейс для всех отчетов
 */
public interface IReport {

    /**
     * Возвращает идентификатор ресурса картинки отчета, которая используется
     * для отображения в списках
     */
    int getResIdIcon();

    /**
     * Возвращает идентификатор ресурса строки названия отчета
     */
    int getResIdTitle();

    /**
     * Построить отчет и отобразить пользователю
     *
     * @param context текущий контекст
     */
    void onShow(Context context);

    /**
     * Закрыть отчет (если показан), освободить все связанные ресурсы
     */
    void onDestroy();
}
