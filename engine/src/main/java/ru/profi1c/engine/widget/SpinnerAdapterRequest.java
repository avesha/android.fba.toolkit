package ru.profi1c.engine.widget;

import android.widget.SpinnerAdapter;

/**
 * Интерфейс предназначен для оптимизация создания адаптеров для выпадающих списков.
 * Позволяет закешировать созданные адаптеры для повторного использования
 */
public interface SpinnerAdapterRequest {
    /**
     * Получить кеш-адаптер для отображения полного списка значений.
     * Оптимизация отображения однотипных данных в списках т. е. чтобы не
     * создавать заново адаптер, например, для перечислений можно взять из
     * кеш.
     *
     * @param classOfValue класс, значения которого отображаются в адаптере
     * @return
     */
    SpinnerAdapter getCachedAdapter(Class<?> classOfValue);

    /**
     * Создан новый адаптер, можно сохранить в кеш, если требуется
     *
     * @param classOfValue класс, значения которого отображаются в адаптере
     * @param adapter      адаптер с данными
     */
    void onNewAdapter(Class<?> classOfValue, SpinnerAdapter adapter);
}
