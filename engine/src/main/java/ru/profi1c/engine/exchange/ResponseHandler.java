package ru.profi1c.engine.exchange;

import java.io.File;

/**
 * Этот класс содержит методы, которые обрабатывают ответы сервиса обмена. Если
 * зарегистрирован {@link ExchangeObserver} то так же может быть выполнено
 * обновление пользовательского интерфейса
 */
public class ResponseHandler {

    /**
     * Это статическая ссылка на экземпляр {@link ExchangeObserver}.Приложение
     * создает и регистрирует класс {@link ExchangeObserver}, который
     * используется для обновления пользовательского интерфейса
     */
    private static ExchangeObserver sExchangeObserver;

    /**
     * Признак того, что обмен выполняется в настоящий момент
     */
    private static volatile boolean sRunnig;

    private static volatile boolean sCancelled;

    /**
     * Возвращает истина, если обмен выполняется в настоящий момент
     *
     * @return
     */
    public static boolean isRunnig() {
        return sRunnig;
    }

    public static boolean isCancelled() {
        return sCancelled;
    }

    /**
     * Возвращает истина, если обмен прерван
     */
    public static void cancel() {
        sCancelled = true;
    }

    /**
     * Регистрировать наблюдателя, который обновляет пользовательский интерфейс
     *
     * @param observer наблюдатель
     */
    public static synchronized void register(ExchangeObserver observer) {
        sExchangeObserver = observer;
        if (sRunnig) {
            sExchangeObserver.onBuild();
        }
    }

    /**
     * Отменяет регистрацию ранее зарегистрированных наблюдателей.
     *
     * @param observer ранее зарегистрированный наблюдатель
     */
    public static synchronized void unregister(ExchangeObserver observer) {
        sExchangeObserver = null;
    }

    /**
     * Уведомить приложение о начале обмена
     *
     * @param variant вариант обмена
     */
    public static void startExchange(ExchangeVariant variant) {
        sRunnig = true;
        sCancelled = false;
        if (sExchangeObserver != null) {
            sExchangeObserver.onStart(variant);
        }
    }

    /**
     * С сервера получена новая версия приложения
     *
     * @param fApk путь к файлу приложения
     * @return Истина, если следует продолжить процесс обмена, ложь если надо
     * прервать обмен
     */
    public static boolean downloadNewVersionApp(File fApk) {
        boolean isContinueExchange = true;
        if (sExchangeObserver != null) {
            isContinueExchange = sExchangeObserver.onDownloadNewVersionApp(fApk);
        }
        return isContinueExchange;
    }

    public static void stepInfo(String msg) {
        if (sExchangeObserver != null) {
            sExchangeObserver.onStepInfo(msg);
        }
    }

    public static void error(String msg) {
        if (sExchangeObserver != null) {
            sExchangeObserver.onError(msg);
        }
    }

    /**
     * Уведомить приложение об окончании обмена
     *
     * @param success флаг успешности обмена
     */
    public static void finishExchange(boolean success) {
        if (sExchangeObserver != null) {
            sExchangeObserver.onFinish(success);
        }
        sRunnig = false;
    }

}
