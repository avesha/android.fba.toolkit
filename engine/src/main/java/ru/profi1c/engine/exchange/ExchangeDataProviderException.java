package ru.profi1c.engine.exchange;

/**
 * Базовый класс для ошибк обмена
 */
public class ExchangeDataProviderException extends Exception {
    private static final long serialVersionUID = 2152347548983265294L;

    public ExchangeDataProviderException(String message) {
        super(message);
    }

    public ExchangeDataProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeDataProviderException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }

}
