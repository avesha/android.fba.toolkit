package ru.profi1c.engine.exchange;

import ru.profi1c.engine.R;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.meta.IPresentation;

/**
 * Вариант процедуры обмена
 */
public enum ExchangeVariant implements IPresentation {
    /**
     * 'Начальная инициализация' - все данные на мобильном устройстве удаляются
     * и заново получаются из базы 1С
     */
    INIT(R.string.fba_exchange_variant_init),

    /**
     * На сервер передаются измененные данные, затем получаются новые
     */
    FULL(R.string.fba_exchange_variant_full),

    /**
     * Только сохранение измененных данных
     */
    ONLY_SAVE(R.string.fba_exchange_variant_only_save),

    /**
     * Только получение новых или измененных банных из базы 1С
     */
    ONLY_GET(R.string.fba_exchange_variant_only_get);

    /**
     * Представление элемента перечисления
     */
    private int mResId;

    ExchangeVariant(int resId) {
        mResId = resId;
    }

    @Override
    public String getPresentation() {
        return FbaApplication.getContext().getString(mResId);
    }
}
