package ru.profi1c.engine.exchange;

/**
 * Слушатель событий обмена с сервером
 */
public interface ISimpleCallbackListener {

    /**
     * Произошла ошибка во время обмена
     *
     * @param event событие
     * @param msg   описание ошибки
     */
    void onError(String event, String msg);

    /**
     * Обмен отменен пользователем интерактивно (при закрытии диалога) или
     * программно
     */
    void onCancel();

    /**
     * Обмен завершен
     *
     * @param result флаг успешности
     * @param data   полученные данные
     */
    void onComplete(boolean result, Object data);

}
