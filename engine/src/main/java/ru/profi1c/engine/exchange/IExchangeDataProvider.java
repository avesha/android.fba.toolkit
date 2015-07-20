package ru.profi1c.engine.exchange;

import java.io.File;

public interface IExchangeDataProvider {
    /**
     * Получить таймаут на соединение
     *
     * @return
     */
    int getConnectionTimeout();

    /**
     * Установить таймаут на соединение с провайдером обмена
     *
     * @param ms время в милисекундах
     */
    void setConnectionTimeout(int ms);

    /**
     * Авторизация пользователя (мобильного приложения)
     *
     * @param appId    уникальный идентификатор мобильного приложения
     * @param deviceId уникальный идентификатор устройства
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @return true, если идентификация прошла успешно и false в случае ошибки
     * @throws ExchangeDataProviderException
     */
    boolean login(String appId, String deviceId, String userName, String password)
            throws ExchangeDataProviderException;

    /**
     * Проверка актуальности версии мобильного приложения
     *
     * @param appId      уникальный идентификатор мобильного приложения
     * @param appVersion используемая на устройстве версия приложения
     * @param deviceId   уникальный идентификатор устройства
     * @return
     * @throws ExchangeDataProviderException
     */
    boolean isWorkingVersionApp(String appId, int appVersion, String deviceId)
            throws ExchangeDataProviderException;

    /**
     * Получить мобильное приложение
     *
     * @param appId      уникальный идентификатор мобильного приложения
     * @param appVersion используемая на устройстве версия приложения
     * @param deviceId   уникальный идентификатор устройства
     * @param fPath      путь, по которому будет сохранен apk файл в случае успешного
     *                   скачивания новой версии
     * @return ссылку на файл apk или null в случае ошибки
     * @throws ExchangeDataProviderException
     */
    File getApp(String appId, int appVersion, String deviceId, String fPath)
            throws ExchangeDataProviderException;

    /**
     * Получить данные мобильного приложения
     *
     * @param appId       уникальный идентификатор мобильного приложения
     * @param deviceId    уникальный идентификатор устройства
     * @param all         если истина, то требуется передать все данные данного типа
     *                    (выполняется начальная инициализация данных на мобильном
     *                    устройстве, иначе – только измененные данные
     * @param metaType    тип запрашиваемых метаданных, строка. Возможные значения:
     *                    ‘Константы’, ‘Справочник’, ‘Документ’, ‘РегистрСведений’,
     *                    ‘ВнешяяТаблица’
     * @param metaName    имя объекта метаданных (имя справочника, документа и т.п., для
     *                    констант пустое значение)
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию загрузки данных из
     *                    мобильного приложения.
     * @param fPath       путь к файлу в котором будет сохранен результат (распакованный
     *                    json)
     * @return
     * @throws ExchangeDataProviderException
     */
    File getData(String appId, String deviceId, boolean all, String metaType, String metaName,
            String addonParams, String fPath) throws ExchangeDataProviderException;

    /**
     * Сообщить об успешности получения данных
     *
     * @param appId       уникальный идентификатор мобильного приложения
     * @param deviceId    уникальный идентификатор устройства
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию)
     * @return всегда возвращает true (если не изменено поведение в 1С)
     * @throws ExchangeDataProviderException
     */
    boolean registerDataReceipt(String appId, String deviceId, String addonParams)
            throws ExchangeDataProviderException;

    /**
     * Сохранить данные мобильного приложения
     *
     * @param appId       уникальный идентификатор мобильного приложения
     * @param deviceId    уникальный идентификатор устройства
     * @param metaType    тип сохраняемых метаданных, строка. Возможные значения:
     *                    ‘Константы’, ‘Справочник’, ‘Документ’, ‘РегистрСведений’,
     *                    ‘ВнешяяТаблица’
     * @param metaName    имя объекта метаданных (имя справочника, документа и т.п., для
     *                    констант пустое значение)
     * @param fPath       fPath путь к файлу, содержимое которого будет упаковано в
     *                    архив и передано как строка64 серверу
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию.
     * @return
     * @throws ExchangeDataProviderException
     */
    boolean writeData(String appId, String deviceId, String metaType, String metaName, String fPath,
            String addonParams) throws ExchangeDataProviderException;

    /**
     * Получить данные большого объема (файл)
     *
     * @param appId       уникальный идентификатор мобильного приложения
     * @param deviceId    уникальный идентификатор устройства
     * @param id          идентификатор запрашиваемых данных, может быть произвольным
     * @param ref         дополнительный идентификатор (как правило GUID) принадлежности
     *                    (например ссылка на торговую точку которой принадлежит
     *                    фотография и т.п)
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию
     * @param fPath       путь к файлу в котором будет сохранен результат
     * @return
     * @throws ExchangeDataProviderException
     */
    File getLargeData(String appId, String deviceId, String id, String ref, String addonParams,
            String fPath) throws ExchangeDataProviderException;

    /**
     * Сохранить файл большого объема (фото, картинка и т.п.)
     *
     * @param appId       уникальный идентификатор мобильного приложения
     * @param deviceId    уникальный идентификатор устройства
     * @param id          идентификатор запрашиваемых данных, может быть произвольным
     * @param ref         дополнительный идентификатор (как правило GUID) принадлежности
     *                    (например ссылка на торговую точку которой принадлежит
     *                    фотография и т.п)
     * @param fPath       путь к файлу, содержимое которого будет упаковано в архив и
     *                    передано как строка64 серверу
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию.
     * @return
     * @throws ExchangeDataProviderException
     */
    boolean writeLargeData(String appId, String deviceId, String id, String ref, String fPath,
            String addonParams) throws ExchangeDataProviderException;

    /**
     * Получить структуру необъектных данных небольшого объема как строку
     *
     * @param appId       уникальный идентификатор мобильного приложения
     * @param deviceId    уникальный идентификатор устройства
     * @param id          идентификатор запрашиваемых данных, может быть произвольным
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию.
     * @return строку полученных данных (ожидается что сервер 1С возвращает
     * данные сериализованные по формату json) или null
     * @throws ExchangeDataProviderException
     */
    String getShortData(String appId, String deviceId, String id, String addonParams)
            throws ExchangeDataProviderException;

    /**
     * Сохранить структуру необъектных данных небольшого объема
     *
     * @param appId       уникальный идентификатор мобильного приложения
     * @param deviceId    уникальный идентификатор устройства
     * @param id          идентификатор запрашиваемых данных, может быть произвольным
     * @param strJson     json представление данных. Предполагается, что сервер 1С
     *                    ожидает к получению сериализованный json объект
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию.
     * @return Истина если данные успешно получены и обработаны сервером 1С и
     * ложь в противном случае
     * @throws ExchangeDataProviderException
     */
    boolean writeShortData(String appId, String deviceId, String id, String strJson,
            String addonParams) throws ExchangeDataProviderException;
}
