package ru.profi1c.engine;

public class FbaRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1704869618377540753L;

    public FbaRuntimeException() {
        super();
    }

    public FbaRuntimeException(String detailMessage) {
        super(detailMessage);
    }

    public FbaRuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FbaRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
