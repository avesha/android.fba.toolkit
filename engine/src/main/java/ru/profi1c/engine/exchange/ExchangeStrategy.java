package ru.profi1c.engine.exchange;

import android.content.Context;

import java.io.File;

import ru.profi1c.engine.R;

/**
 * Помощник проведения процедуры обмена с учетом уставленных настроек.
 * Перед вызовом любого метода, проверяет была ли выполнена авторизация и
 * выполняет ее если требуется.
 */
public class ExchangeStrategy {

    private final BaseExchangeSettings mExchangeSettings;
    private IExchangeDataProvider mDataProvider;

    /**
     * Признак того что пользователь прошел авторизацию на web-сервере
     */
    private boolean mLogin;

    public ExchangeStrategy(BaseExchangeSettings exchangeSettings) {
        mExchangeSettings = exchangeSettings;
        mLogin = false;
    }

    /**
     * Текущий контекст
     */
    public Context getContext() {
        return mExchangeSettings.getContext();
    }

    protected IExchangeDataProvider getDataProvider() {
        if (mDataProvider == null) {
            IExchangeDataProviderFactory factory =
                    mExchangeSettings.getExchangeDataProviderFactory();
            if (factory == null) {
                throw new IllegalStateException(
                        getContext().getString(R.string.fba_msg_err_data_provider_not_specified));
            }
            mDataProvider = factory.create(mExchangeSettings);
        }
        return mDataProvider;
    }

    /**
     * Проверить или выполнить авторизацию пользователя на web-сервисе (если
     * требуется)
     *
     * @return
     * @throws ExchangeDataProviderException
     */
    private boolean doLogin() throws ExchangeDataProviderException {
        if (!mLogin) {
            login();
        }
        return mLogin;
    }

    /**
     * Авторизация пользователя
     *
     * @return
     * @throws ExchangeDataProviderException
     */
    public boolean login() throws ExchangeDataProviderException {
        mLogin = getDataProvider()
                .login(mExchangeSettings.getAppId(), mExchangeSettings.getDeviceId(),
                       mExchangeSettings.getUserName(), mExchangeSettings.getPassword());
        return mLogin;
    }

    /**
     * Проверка актуальности версии мобильного приложения
     *
     * @return
     * @throws ExchangeDataProviderException
     */
    public boolean isWorkingVersionApp() throws ExchangeDataProviderException {

        boolean result = false;
        if (doLogin()) {
            result = getDataProvider().isWorkingVersionApp(mExchangeSettings.getAppId(),
                                                           mExchangeSettings.getAppVersion(),
                                                           mExchangeSettings.getDeviceId());
        }
        return result;
    }

    /**
     * Получить мобильное приложение
     *
     * @param fPath путь, по которому будет сохранен apk файл в случае успешного
     *              скачивания новой версии
     * @return ссылку на файл apk или null в случае ошибки
     * @throws ExchangeDataProviderException
     */
    public File getApp(String fPath) throws ExchangeDataProviderException {

        File result = null;
        if (doLogin()) {
            result = getDataProvider()
                    .getApp(mExchangeSettings.getAppId(), mExchangeSettings.getAppVersion(),
                            mExchangeSettings.getDeviceId(), fPath);
        }
        return result;

    }

    /**
     * Получить данные мобильного приложения
     *
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
    public File getData(boolean all, String metaType, String metaName, String addonParams,
            String fPath) throws ExchangeDataProviderException {

        File result = null;
        if (doLogin()) {
            result = getDataProvider()
                    .getData(mExchangeSettings.getAppId(), mExchangeSettings.getDeviceId(), all,
                             metaType, metaName, addonParams, fPath);
        }
        return result;
    }

    /**
     * Сообщить об успешности получения данных
     *
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию)
     * @return всегда возвращает true (если не изменено поведение в 1С)
     * @throws ExchangeDataProviderException
     */
    public boolean registerDataReceipt(String addonParams) throws ExchangeDataProviderException {

        boolean result = false;
        if (doLogin()) {
            result = getDataProvider().registerDataReceipt(mExchangeSettings.getAppId(),
                                                           mExchangeSettings.getDeviceId(),
                                                           addonParams);
        }
        return result;
    }

    /**
     * Сохранить данные мобильного приложения
     *
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
    public boolean writeData(String metaType, String metaName, String fPath, String addonParams)
            throws ExchangeDataProviderException {

        boolean result = false;
        if (doLogin()) {
            result = getDataProvider()
                    .writeData(mExchangeSettings.getAppId(), mExchangeSettings.getDeviceId(),
                               metaType, metaName, fPath, addonParams);
        }
        return result;
    }

    /**
     * Получить данные большого объема (файл)
     *
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
    public File getLargeData(String id, String ref, String addonParams, String fPath)
            throws ExchangeDataProviderException {

        File result = null;
        if (doLogin()) {
            result = getDataProvider()
                    .getLargeData(mExchangeSettings.getAppId(), mExchangeSettings.getDeviceId(), id,
                                  ref, addonParams, fPath);
        }
        return result;
    }

    /**
     * Сохранить файл большого объема (фото, картинка и т.п.)
     *
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
    public boolean writeLargeData(String id, String ref, String fPath, String addonParams)
            throws ExchangeDataProviderException {
        boolean result = false;
        if (doLogin()) {
            result = getDataProvider()
                    .writeLargeData(mExchangeSettings.getAppId(), mExchangeSettings.getDeviceId(),
                                    id, ref, fPath, addonParams);
        }
        return result;
    }

    /**
     * Получить структуру необъектных данных небольшого объема как JSON объект
     *
     * @param id          идентификатор запрашиваемых данных, может быть произвольным
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию.
     * @return строку полученных данных (ожидается что сервер 1С возвращает
     * данные сериализованные по формату json) или null
     * @throws ExchangeDataProviderException
     */
    public String getShortData(String id, String addonParams) throws ExchangeDataProviderException {
        String result = null;
        if (doLogin()) {
            result = getDataProvider()
                    .getShortData(mExchangeSettings.getAppId(), mExchangeSettings.getDeviceId(), id,
                                  addonParams);
        }
        return result;
    }

    /**
     * Сохранить структуру необъектных данных небольшого объема
     *
     * @param id          идентификатор запрашиваемых данных, может быть произвольным
     * @param strJson     json представление данных. Предполагается, что сервер 1С
     *                    ожидает к получению сериализованный json объект
     * @param addonParams дополнительные параметры (необязательный, не используется по
     *                    умолчанию). Используете этот параметр для передачи
     *                    дополнительный параметров в функцию.
     * @return
     * @throws ExchangeDataProviderException
     */
    public boolean writeShortData(String id, String strJson, String addonParams)
            throws ExchangeDataProviderException {
        boolean result = false;
        if (doLogin()) {
            result = getDataProvider()
                    .writeShortData(mExchangeSettings.getAppId(), mExchangeSettings.getDeviceId(),
                                    id, strJson, addonParams);
        }
        return result;
    }
}
