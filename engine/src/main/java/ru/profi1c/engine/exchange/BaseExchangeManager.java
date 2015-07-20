package ru.profi1c.engine.exchange;

import android.content.Context;

import java.io.File;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.meta.DBOpenHelper;

/**
 * Менеджер обмена данными с сервером
 */
public abstract class BaseExchangeManager {

    private final ExchangeStrategy mExchangeStrategy;

    /**
     * Помощник для работы с базой данных Sqlite
     */
    protected abstract DBOpenHelper getDBOpenHelper();

    public BaseExchangeManager(BaseExchangeSettings exchangeSettings) {
        mExchangeStrategy = new ExchangeStrategy(exchangeSettings);
    }

    public Context getContext() {
        return mExchangeStrategy.getContext();
    }

    public ExchangeStrategy getExchangeStrategy() {
        return mExchangeStrategy;
    }

    /**
     * Возвращает истина, если обмен выполняется в настоящий момент
     *
     * @return
     */
    public boolean isRunningExchangeTask() {
        return ResponseHandler.isRunnig();
    }

    /**
     * Помощник получения данных большого объема (файл) в отдельном потоке
     *
     * @param id       идентификатор запрашиваемых данных, может быть произвольным
     * @param ref      дополнительный идентификатор (как правило GUID) принадлежности
     *                 (например ссылка на торговую точку которой принадлежит
     *                 фотография и т.п)
     * @param fPath    путь к файлу в котором будет сохранен результат
     * @param listener обработчик обратного вызова, слушатель результата обмена
     */
    public void getLargeData(final String id, final String ref, final String fPath,
            final ISimpleCallbackListener listener) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    File file = mExchangeStrategy.getLargeData(id, ref, fPath, "");
                    listener.onComplete(file != null, file);

                } catch (ExchangeDataProviderException e) {
                    Dbg.printStackTrace(e);
                    if (listener != null) {
                        listener.onError("getLargeData", e.getMessage());
                    }
                }

            }
        }).start();
    }

    /**
     * Помощник сохранения файла большого объема (фото, картинка и т.п.) в
     * отдельном потоке
     *
     * @param id       идентификатор запрашиваемых данных, может быть произвольным
     * @param ref      дополнительный идентификатор (как правило GUID) принадлежности
     *                 (например ссылка на торговую точку которой принадлежит
     *                 фотография и т.п)
     * @param fPath    путь к файлу, содержимое которого будет упаковано в архив и
     *                 передано как строка64 серверу
     * @param listener обработчик обратного вызова, слушатель результата обмена
     */
    public void writeLargeData(final String id, final String ref, final String fPath,
            final ISimpleCallbackListener listener) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    boolean result = mExchangeStrategy.writeLargeData(id, ref, fPath, "");
                    listener.onComplete(result, null);
                } catch (ExchangeDataProviderException e) {
                    Dbg.printStackTrace(e);
                    if (listener != null) {
                        listener.onError("writeLargeData", e.getMessage());
                    }
                }

            }
        }).start();
    }

    /**
     * Помощник получения структуры необъектных данных небольшого объема в
     * отдельном потоке
     *
     * @param id       идентификатор запрашиваемых данных, может быть произвольным
     * @param listener обработчик обратного вызова, слушатель результата обмена
     */
    public void getShortData(final String id, final ISimpleCallbackListener listener) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    String result = mExchangeStrategy.getShortData(id, "");
                    listener.onComplete(result != null, result);
                } catch (ExchangeDataProviderException e) {
                    Dbg.printStackTrace(e);
                    if (listener != null) {
                        listener.onError("getShortData", e.getMessage());
                    }
                }

            }
        }).start();
    }

    /**
     * Помощник сохранения необъектных данных небольшого объема в отдельном
     * потоке
     *
     * @param id       идентификатор запрашиваемых данных, может быть произвольным
     * @param strJson  json представление данных. Предполагается, что сервер 1С
     *                 ожидает к получению сериализованный json объект
     * @param listener обработчик обратного вызова, слушатель результата обмена
     */
    public void writeShortData(final String id, final String strJson,
            final ISimpleCallbackListener listener) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    boolean result = mExchangeStrategy.writeShortData(id, strJson, "");
                    listener.onComplete(result, null);
                } catch (ExchangeDataProviderException e) {
                    Dbg.printStackTrace(e);
                    if (listener != null) {
                        listener.onError("writeShortData", e.getMessage());
                    }
                }

            }
        }).start();

    }

}
