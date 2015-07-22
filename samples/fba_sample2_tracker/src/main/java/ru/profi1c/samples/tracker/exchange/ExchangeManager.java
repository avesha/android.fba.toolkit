/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tracker.exchange;

import java.util.concurrent.FutureTask;

import ru.profi1c.engine.exchange.BaseExchangeManager;
import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.exchange.ExchangeTask;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.exchange.IExchangeCallbackListener;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.samples.tracker.db.DBHelper;

/**
 * Менеджер обмена с web-сервисом
 *
 * @author ООО "Сфера" (info@sfera.ru)
 */
public class ExchangeManager extends BaseExchangeManager {

    public ExchangeManager(BaseExchangeSettings exchangeSettings) {
        super(exchangeSettings);
    }

    @Override
    protected DBOpenHelper getDBOpenHelper() {
        return new DBHelper(getContext());
    }

    /**
     * Запуск процедуры обмена в рабочем процессе.
     * Процедура обмена выполняется по фиксированным правилам определенным в  {@link ExchangeTask}
     *
     * @param exchangeVariant вариант обмена
     * @return
     */
    public boolean startExchange(ExchangeVariant exchangeVariant) throws Exception {

        if (!isRunningExchangeTask()) {
            ExchangeTask task = new ExchangeTask(exchangeVariant, getExchangeStrategy(),
                    getDBOpenHelper());
            return task.call();
        }
        return false;

    }

    /**
     * Запуск процедуры обмена в отдельном потоке.
     * Процедура обмена выполняется по фиксированным правилам определенным в  {@link ExchangeTask}
     *
     * @param exchangeVariant вариант обмена
     * @param listener        обработчик обратного вызова, слушатель результата обмена
     */
    public void startExchange(final ExchangeVariant exchangeVariant,
            final IExchangeCallbackListener listener) {

        if (!isRunningExchangeTask()) {

            ExchangeTask task = new ExchangeTask(exchangeVariant, getExchangeStrategy(),
                    getDBOpenHelper());
            task.setListener(listener);

            FutureTask<Boolean> futureTask = new FutureTask<Boolean>(task);
            Thread thread = new Thread(futureTask);
            thread.start();

        }

    }

}

