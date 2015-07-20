package ru.profi1c.engine.exchange;

/**
 * Ошибки от web-сервиса
 */
public class WebServiceException extends ExchangeDataProviderException {

    private static final long serialVersionUID = 2022002L;

    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServiceException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}
