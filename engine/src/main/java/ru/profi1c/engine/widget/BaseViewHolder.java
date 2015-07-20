package ru.profi1c.engine.widget;

import android.view.View;

/**
 * Базовый класс для всех паттернов ViewHolder для эффективной работы со
 * строками адаптеров
 */
public abstract class BaseViewHolder {
    public final View rootView;

    /**
     * Возвращает дочерний View по это идентификатору
     */
    public abstract View getViewById(int id);

    public BaseViewHolder(View root) {
        rootView = root;
    }

}
