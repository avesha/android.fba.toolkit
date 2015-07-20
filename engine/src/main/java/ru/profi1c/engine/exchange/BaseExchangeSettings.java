package ru.profi1c.engine.exchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.meta.DBOpenHelper;

/**
 * Настройки обмена web-сервисом 1С
 */
public abstract class BaseExchangeSettings {
    private static final String TAG = BaseExchangeSettings.class.getSimpleName();

    /**
     * Таймаут по умолчанию - 30 сек.
     */
    protected static final int DEFAULT_TIMEOUT = Const.DEFAULT_EXCHANGE_TIMEOUT;

    /**
     * Время (минуты) часа для расписания обмена по расписанию
     */
    protected static final int EXCHANGE_TIME_M = Const.DEFAULT_EXCHANGE_TIME_MINUTES;

    /**
     * Время (час) для обмена по расписанию
     */
    protected static final int EXCHANGE_TIME_H = Const.DEFAULT_EXCHANGE_TIME_HOURS;

    /**
     * Индексы дней недели (1 – воскресенье) по которым выполняется обмен в
     * автоматическом режиме
     */
    protected static final String EXCHANGE_DAYS_INDEX = Const.DEFAULT_EXCHANGE_DAYS_INDEX;

    private WeakReference<Context> mRefContext;

    /**
     * имя пользователя для подключения к web-сервису
     */
    private String mUserName;

    /**
     * пароль пользователя
     */
    private String mPassword;

    /**
     * ip-адрес сервера, если не установлен - используется по умолчанию из
     * ресурсов R.string.fba_ws_server
     */
    private String mServerIP;

    /**
     * Используется ли загрузка данных с сервера по расписанию
     */
    private boolean mEnableSchedule;

    /**
     * время для автоматического обмена с сервером (часы и минуты)
     */
    private int mExchangeTimeH, mExchangeTimeM;

    /**
     * дни недели для автоматического обмена с сервером в формате 2,3,4 где
     * число индекс дня недели
     */
    private String mExchangeWeekDays;

    /**
     * Путь к файлу мелодии, которая будет воспроизведена при успешной загрузке
     * данных (если используется процедура обмена по умолчанию)
     */
    private String mSoundSuccess;

    /**
     * Путь к файлу мелодии, которая будет воспроизведена при ошибке загрузки
     * (если используется процедура обмена по умолчанию)
     */
    private String mSoundError;

    /**
     * Путь к файлу мелодии, которая будет воспроизведена при получении с
     * сервера новой версии программы (если используется процедура обмена по
     * умолчанию)
     */
    private String mSoundNewApp;

    protected IExchangeDataProviderFactory mExchangeDataProviderFactory;

    /**
     * Возвращает уникальный номер, который используется для однозначной
     * идентификации этого устройства при обмене с базой 1С. Это может быть,
     * например: <br>
     * - IMEI для устройств в сим-картами, который никогда не изменяется; <br>
     * - ANDROID_ID который устанавливается случайным образом при первом запуске
     * устройства и изменяется только в случае перепрошивки или при сбросе ‘К
     * заводским настройкам’ <br>
     * - или любой номер установленный разработчиком
     */
    public abstract String getDeviceId();

    /**
     * Возвращает уникальный идентификатор этого приложения (задается в базе 1С
     * с которой производится обмен)
     */
    public abstract String getAppId();

    /**
     * Возвращает текущий номер версии этого приложения
     */
    protected abstract int getAppVersion();

    /**
     * Возвращает подкаталог приложения на сервере в котором опубликован
     * web-сервис
     */
    protected abstract String getAppSpace();

    /**
     * Возвращает имя опубликованного web-сервиса, если не указано -
     * используется по умолчанию ‘fbaService.1cws’
     */
    protected abstract String getServiceName();

    /**
     * Возвращает таймаут на подключение к web-сервису
     */
    protected abstract int getConnectionTimeout();

    /**
     * Событие вызывается при сохранении настроек изменяемых интерактивно,
     * можете сохранить свои настройки
     */
    protected abstract void onSave(SharedPreferences.Editor editor);

    /**
     * Событие вызывается при чтении сохраненных ранее настроек, если вы
     * сохраняли свои настройки в методе
     * {@link #onSave(SharedPreferences.Editor)} , то здесь можете прочитать их.
     */
    protected abstract void onRead(SharedPreferences preferences);

    public BaseExchangeSettings(Context context) {
        mRefContext = new WeakReference<Context>(context.getApplicationContext());
        read();
    }

    public Context getContext() {
        return mRefContext.get();
    }

    /**
     * Возвращает имя пользователя, которое используется для авторизации на
     * web-сервисе и при входе в мобильное приложение
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * Установить имя пользователя, которое используется для авторизации на
     * web-сервисе и при входе в мобильное приложение
     */
    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    /**
     * Возвращает пароль пользователя который используется для авторизации на
     * web-сервисе и при входе в мобильное приложение
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * Установить пароль пользователя который используется для авторизации на
     * web-сервисе и при входе в мобильное приложение
     */
    public void setPassword(String password) {
        this.mPassword = password;
    }

    /**
     * Возвращает ip-адрес на сервера на котором развернут web-сервис
     */
    public String getServerIP() {
        return mServerIP;
    }

    /**
     * Установить ip-адрес сервера на котором развернут web-сервис
     */
    public void setServerIP(String serverIP) {
        this.mServerIP = serverIP;
    }

    /**
     * Возвращает истина, если установлены параметры авторизации: имя
     * пользователя и адрес сервера
     */
    public boolean isSetAutorizeParams() {
        return !TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mServerIP);
    }

    /**
     * Возвращает истина, если используется обмен в автоматическом режиме по
     * расписанию
     */
    public boolean isEnableSchedule() {
        return mEnableSchedule;
    }

    /**
     * Установить флаг использования обмена в автоматическом режиме по
     * расписанию
     */
    public void setEnableSchedule(boolean enableSchedule) {
        this.mEnableSchedule = enableSchedule;
    }

    /**
     * Получить время (минуты) для расписания обмена
     */
    public int getExchangeTimeM() {
        return mExchangeTimeM;
    }

    /**
     * Установит время (минуты) для расписания обмена
     */
    public void setExchangeTimeM(int exchangeTimeM) {
        this.mExchangeTimeM = exchangeTimeM;
    }

    /**
     * Получить время (час) для расписания обмена
     */
    public int getExchangeTimeH() {
        return mExchangeTimeH;
    }

    /**
     * Установить время (час) для расписания обмена
     */
    public void setExchangeTimeH(int exchangeTimeH) {
        this.mExchangeTimeH = exchangeTimeH;
    }

    /**
     * Получить дни планировщика обмена строкой
     */
    public String getExchangeWeekDays() {
        return mExchangeWeekDays;
    }

    /**
     * Установить дни по которым выполняется обмен по расписанию
     *
     * @param indexWeekDays массив индексов дней недели
     * @param checked       массив отмеченных дней
     */
    public void setExchangeWeekDays(int[] indexWeekDays, boolean[] checked) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < checked.length; i++) {
            if (checked[i]) {
                sb.append(String.valueOf(indexWeekDays[i])).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        mExchangeWeekDays = sb.toString();

    }

    /**
     * Возвращает истина, если в этот день производить загрузку по расписанию
     *
     * @param indexWeekDay индекс дня недели
     * @return
     */
    public boolean isExchangeWeekDays(int indexWeekDay) {
        return mExchangeWeekDays.contains(String.valueOf(indexWeekDay));
    }

    public void setSoundSuccess(String soundSuccess) {
        this.mSoundSuccess = soundSuccess;
    }

    public void setSoundError(String soundError) {
        this.mSoundError = soundError;
    }

    public void setSoundNewApp(String soundNewApp) {
        this.mSoundNewApp = soundNewApp;
    }

    /**
     * Возвращает Uri на звук который будет воспроизведен при получении новой
     * версии приложения (если используется процедура обмена по умолчанию)
     */
    public Uri getSoundDownloadedApk() {
        if (!TextUtils.isEmpty(mSoundNewApp)) {
            return Uri.parse(mSoundNewApp);
        }
        return null;
    }

    /**
     * Возвращает Uri на звук который будет воспроизведен если при обмене
     * произошли ошибки (если используется процедура обмена по умолчанию)
     */
    public Uri getSoundExchangeError() {
        if (!TextUtils.isEmpty(mSoundError)) {
            return Uri.parse(mSoundError);
        }
        return null;
    }

    /**
     * Возвращает Uri на звук который будет воспроизведен если обмен завершен
     * успешно (если используется процедура обмена по умолчанию)
     */
    public Uri getSoundExchangeSuccess() {
        if (!TextUtils.isEmpty(mSoundSuccess)) {
            return Uri.parse(mSoundSuccess);
        }
        return null;
    }

    /**
     * Сохранение настроек изменяемых интерактивно: имя пользователя, пароль,
     * адрес сервера, звуки и проч.
     */
    public void save() {
        Context context = getContext();
        if (context != null) {

            SharedPreferences.Editor editor = context.getSharedPreferences(TAG, 0).edit();
            editor.putString("userName", mUserName);
            editor.putString("password", mPassword);
            editor.putString("serverIP", mServerIP);
            editor.putBoolean("enableSchedule", mEnableSchedule);
            editor.putInt("exchangeTimeM", mExchangeTimeM);
            editor.putInt("exchangeTimeH", mExchangeTimeH);
            editor.putString("exchangeWeekDays", mExchangeWeekDays);
            editor.putString("soundSuccess", mSoundSuccess);
            editor.putString("soundError", mSoundError);
            editor.putString("soundNewApp", mSoundNewApp);
            onSave(editor);

            editor.commit();
        }

    }

    /**
     * Чтение настроек
     */
    private void read() {

        Context context = getContext();
        if (context != null) {

            SharedPreferences preferences = context.getSharedPreferences(TAG, 0);
            mUserName = preferences.getString("userName", "");
            mPassword = preferences.getString("password", "");
            mServerIP = preferences.getString("serverIP", "");
            mEnableSchedule = preferences.getBoolean("enableSchedule", false);
            mExchangeTimeH = preferences.getInt("exchangeTimeH", EXCHANGE_TIME_H);
            mExchangeTimeM = preferences.getInt("exchangeTimeM", EXCHANGE_TIME_M);
            mExchangeWeekDays = preferences.getString("exchangeWeekDays", EXCHANGE_DAYS_INDEX);
            mSoundSuccess = preferences.getString("soundSucsess", "");
            mSoundError = preferences.getString("soundError", "");
            mSoundNewApp = preferences.getString("soundNewApp", "");
            onRead(preferences);
        }

    }

    /**
     * Возвращает задание обмена, исполняемое по умолчанию (используется в
     * службе обмена ExchangeService). Переопределите данный метод в наследнике,
     * если вы используете свою реализацию процедуры обмена и хотите запускать
     * обмен автоматически по расписанию.
     *
     * @param variant  Вариант обмена
     * @param dbHelper Помощник для работы с базой данных Sqlite
     * @return null
     */
    public BaseExchangeTask getDefaultExchangeTask(ExchangeVariant variant, DBOpenHelper dbHelper) {
        return null;
    }

    /**
     * Возвращает поставщика данных для процедуры обмена, если не установлен пользовательский поставщик,
     * используется поставщик по умолчанию – обмен через веб-сервис
     */
    public IExchangeDataProviderFactory getExchangeDataProviderFactory() {
        if (mExchangeDataProviderFactory == null) {
            mExchangeDataProviderFactory = new WebServiceDataProviderFactory();
        }
        return mExchangeDataProviderFactory;
    }

    /**
     * Установить пользовательский поставщик данных для процедуры обмена
     */
    public void setExchangeDataProviderFactory(IExchangeDataProviderFactory dataProviderFactory) {
        mExchangeDataProviderFactory = dataProviderFactory;
    }
}
