package ru.profi1c.engine.exchange;

import android.text.TextUtils;

import com.j256.ormlite.dao.BaseDaoImpl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;
import ru.profi1c.engine.app.ApkUpdateHelper;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;
import ru.profi1c.engine.meta.RowDao;
import ru.profi1c.engine.util.IOHelper;
import ru.profi1c.engine.util.TraceExceptionHandler;

/**
 * Реализация процедуры обмена по фиксированным правилам. Требуется разрешение
 * "android.permission.WAKE_LOCK"
 */
public class ExchangeTask extends BaseExchangeTask {
    private static final String TAG = ExchangeTask.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    public static final String TMP_LOG_FILE_NAME = "trace_log.tmp";
    public static final String TMP_FOLDER_WRITE_DATA = "writeData";
    public static final String TMP_FOLDER_GET_DATA = "getData";

    /*
     * Измененные объекты, по которым надо очистить флаг модифицированности при
     * успешной передаче на сервер
     */
    private HashMap<Class<?>, List<Object>> mMapSendObject;

    public ExchangeTask(ExchangeVariant exchangeVariant, ExchangeStrategy exchangeStrategy,
            DBOpenHelper dbOpenHelper) {
        super(exchangeVariant, exchangeStrategy, dbOpenHelper);
    }

    private File getTmpDir(String subDir) {
        File fDir = new File(mAppSettings.getCacheDir(), subDir);
        return fDir;
    }

    @Override
    protected boolean doExecute() throws Exception {

        // 1. авторизация
        onStepInfo(R.string.fba_user_authorization);
        boolean isLogin = mExchangeStrategy.login();
        if (!isLogin) {
            onError("login", getString(R.string.fba_user_authorization_error));
            return false;
        }

        // 2. отправить на сервер лог ошибок программы
        doSendTraceLog();

        // 3. проверить и скачать новую версию программы
        doCheckNewVersionApp();

        // 4. принудительно очистить данные (если начальная инициализация)
        doClearDBTables();

        // 5. измененные объекты передать на сервер
        doWriteData();

        // 6. получить данные сервера (новые или измененные)
        HashMap<Class<?>, File> mapJsonData = doGetData();

        if (mapJsonData.size() > 0) {
            // 6.1 уведомить о получении
            doNotifyDataReceipt();

            // 6.2 парсинг json и обновление локальной базы данных
            doUpdateLocalDB(mapJsonData);
        }

        return true;
    }

    /**
     * Проверить и скачать новую версию приложения если требуется
     *
     * @throws ExchangeDataProviderException
     */
    private void doCheckNewVersionApp() throws ExchangeDataProviderException {
        if (DEBUG) {
            Dbg.d(TAG, "doCheckNewVersionApp");
        }

        if (!continueExecute()) {
            return;
        }

        // только для этих вариантов обмена
        if (mVariant == ExchangeVariant.FULL || mVariant == ExchangeVariant.INIT) {

            onStepInfo(R.string.fba_check_app_version);
            boolean workVersion = mExchangeStrategy.isWorkingVersionApp();
            if (!workVersion) {

                onStepInfo(R.string.fba_get_app);
                String fPath = ApkUpdateHelper.getNewVersionPath(mAppSettings).getAbsolutePath();
                File newApk = mExchangeStrategy.getApp(fPath);
                if (newApk != null) {
                    onStepInfo(R.string.fba_get_app_success);
                    ResponseHandler.downloadNewVersionApp(newApk);
                }

            }
        }

    }

    /**
     * Отправить на сервер лог ошибок программы. Лог ошибок фиксируется и
     * отправляется если это указано в настройках
     *
     * @throws ExchangeDataProviderException
     */
    private void doSendTraceLog() throws ExchangeDataProviderException {
        if (DEBUG) {
            Dbg.d(TAG, "doSendTraceLog");
        }

        if (!continueExecute()) {
            return;
        }

        if (mAppSettings.customExceptionHandler()) {

            String strTraceLog = TraceExceptionHandler.getErrorTraceLog(getContext());
            if (!TextUtils.isEmpty(strTraceLog)) {

                File fTmp = new File(mAppSettings.getCacheDir(), TMP_LOG_FILE_NAME);
                try {
                    IOHelper.writeToFile(strTraceLog, fTmp.getAbsolutePath());

                    onStepInfo(R.string.fba_send_trace_log);
                    String id = getString(R.string.fba_id_trace_log);
                    mExchangeStrategy.writeLargeData(id, null, fTmp.getAbsolutePath(), null);

                } catch (IOException e) {
                    Dbg.printStackTrace(e);
                } finally {
                    fTmp.delete();
                }
            }
        }
    }

    /**
     * Проверить и очистить все таблицы базы данных, если требуется
     * (принудительная очистка выполняется при начальной инициализации).
     *
     * @throws SQLException
     */
    private void doClearDBTables() throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "doClearDBTables");
        }

        if (!continueExecute()) {
            return;
        }

        if (mVariant == ExchangeVariant.INIT) {

			/*
             * Базы данных может и не быть, например когда используются только
			 * получение/сохранение кастомных данных и json объектов
			 */
            if (DBOpenHelper.isExistsDataBase(getContext()) && mMetadataHelper != null &&
                mDbHelper != null) {
                onStepInfo(R.string.fba_clear_local_db);
                mDbHelper.clearTables(mMetadataHelper);
            }
        }
    }

    /**
     * Выбрать измененные на клиенте объекты и передать их на сервер
     *
     * @throws SQLException
     */
    private void doWriteData() throws ExchangeDataProviderException, SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "doWriteData");
        }

        if (!continueExecute()) {
            return;
        }

        // Удалить временные файлы, оставшиеся с предыдущего обмена
        File fDir = getTmpDir(TMP_FOLDER_WRITE_DATA);
        IOHelper.removeDirectory(fDir);
        fDir.mkdirs();

		/*
		 * только для этих вариантов обмена и если есть помощник для работы с
		 * метаданными. Отсутствие помощника не является ошибкой - возможен
		 * вариант, когда используются только получение/сохранение кастомных
		 * данных и json объектов. Тогда база данных и метаданные не нужны.
		 */
        if (mMetadataHelper != null &&
            (mVariant == ExchangeVariant.FULL || mVariant == ExchangeVariant.ONLY_SAVE)) {

            HashMap<Class<?>, File> mapJsonData = getDataChanges(fDir);
            if (mapJsonData.size() > 0) {
                sendDataToServer(mapJsonData);
                doClearModifiedLocalDB();
            }

        }
    }

    /**
     * Выбрать из локальной базы данных измененные элементы этого класса,
     * преобразовать в json и сохранить в указанный файл
     *
     * @param fTmpDir каталог временных файлов
     * @return
     * @throws SQLException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private HashMap<Class<?>, File> getDataChanges(File fTmpDir) throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "getDataChanges");
        }

        mMapSendObject = new HashMap<Class<?>, List<Object>>();
        HashMap<Class<?>, File> mapJsonData = new HashMap<Class<?>, File>();

        List<Class> lstAll = mMetadataHelper.getAllObjectClasses();
        for (Class clazz : lstAll) {

            if (!continueExecute()) {
                break;
            }

            if (clazz != null) {

                String metaType = MetadataHelper.getMetadataType(clazz);
                String metaName = MetadataHelper.getMetadataName(clazz);

                String formatMsg = getString(R.string.fba_select_changes);
                if (formatMsg != null) {
                    onStepInfo(String.format(formatMsg, metaType, metaName));
                }

				/*
				 * Имя файла и вложения на английском, для 1С не важно имя, а
				 * встроенный zip не умеет работать с UTF-8 в именах вложений
				 */
                String simpleName = clazz.getSimpleName().replaceAll(".java", "");
                String fPath = new File(fTmpDir,
                        String.format("%s.json", simpleName)).getAbsolutePath();
                File f = selectChangedToJson(clazz, fPath);
                if (f != null) {
                    mapJsonData.put(clazz, f);
                }
            }
        }

        return mapJsonData;
    }

    /**
     * Выбрать из локальной базы данных измененные элементы этого класса
     *
     * @param classOfT класс данных
     * @param fPath    путь к файлу в котором сохраняются данные в формате json
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    private <T> File selectChangedToJson(Class<T> classOfT, String fPath) throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "selectChangedToJson");
        }

        File file = null;
        List<T> lst = null;

        BaseDaoImpl<T, String> dao = mDbHelper.getDao(classOfT);
        lst = (List<T>) ((RowDao<Row>) dao).selectChanged();

        if (lst != null && lst.size() > 0) {

            // сохранить в кеш для для последующей очистки флага
            // модифицированности
            mMapSendObject.put(classOfT, (List<Object>) lst);

            JSONProvider json = getJsonProvider();
            file = new File(fPath);

            boolean sendTo1c = true;
            if (mListener != null) {
                boolean update = mListener.onSerializeTable(classOfT, (List<Object>) lst);
                sendTo1c = update && lst.size() > 0;
            }

            if (sendTo1c) {
                if (!json.toJsonArray(lst, file)) {
                    file = null;
                }
            }

        }
        return file;
    }

    /**
     * Отправить данные на сервер
     *
     * @param mapJsonData
     * @throws ExchangeDataProviderException
     */
    private void sendDataToServer(HashMap<Class<?>, File> mapJsonData) throws ExchangeDataProviderException {
        if (DEBUG) {
            Dbg.d(TAG, "sendDataToServer");
        }

        if (!continueExecute()) {
            return;
        }

        for (Class<?> clazz : mapJsonData.keySet()) {

            String metaType = MetadataHelper.getMetadataType(clazz);
            String metaName = MetadataHelper.getMetadataName(clazz);

            String formatMsg = getString(R.string.fba_write_data);
            if (formatMsg != null) {
                onStepInfo(String.format(formatMsg, metaType, metaName));
            }
            String fPath = mapJsonData.get(clazz).getAbsolutePath();
            if (!mExchangeStrategy.writeData(metaType, metaName, fPath, "")) {

                if (mListener != null) {
                    mListener.onDeniedDataSavedOnServer(clazz, mMapSendObject.get(clazz));
                }

                // убрать из кеш-а
                mMapSendObject.remove(clazz);
            }

        }

    }

    /**
     * Получить данные с сервера (новые или измененные)
     *
     * @throws ExchangeDataProviderException
     */
    @SuppressWarnings("rawtypes")
    private HashMap<Class<?>, File> doGetData() throws ExchangeDataProviderException {
        if (DEBUG) {
            Dbg.d(TAG, "doGetData");
        }

        HashMap<Class<?>, File> mapJsonData = new HashMap<Class<?>, File>();

        if (!continueExecute()) {
            return mapJsonData;
        }

        // Удалить временные файлы, оставшиеся с предыдущего обмена
        File fDir = getTmpDir(TMP_FOLDER_GET_DATA);
        IOHelper.removeDirectory(fDir);
        fDir.mkdirs();

		/*
		 * Для всех вариантов кроме 'только сохранение' и если есть помощник для
		 * работы с метаданными. Отсутствие помощника не является ошибкой -
		 * возможен вариант, когда используются только получение/сохранение
		 * кастомных данных и json объектов. Тогда база данных и метаданные не
		 * нужны.
		 */
        if (mVariant != ExchangeVariant.ONLY_SAVE && mMetadataHelper != null) {

            boolean all = (mVariant == ExchangeVariant.INIT);

            List<Class> lstAll = mMetadataHelper.getAllObjectClasses();
            for (Class clazz : lstAll) {
                File f = getDataOfClass(clazz, all, fDir);
                if (f != null) {
                    mapJsonData.put(clazz, f);
                }

                if (!continueExecute()) {
                    break;
                }

            }

        }

        return mapJsonData;
    }

    /**
     * Получить данные объекта метаданных, результат сохраняется во временном
     * файле
     *
     * @param clazz
     * @param all     запрос всех данных
     * @param fTmpDir каталог временных файлов
     * @return
     * @throws ExchangeDataProviderException
     */
    @SuppressWarnings("rawtypes")
    private File getDataOfClass(Class clazz, boolean all, File fTmpDir) throws ExchangeDataProviderException {

        File fJson = null;

        if (clazz != null) {

            String metaType = MetadataHelper.getMetadataType(clazz);
            String metaName = MetadataHelper.getMetadataName(clazz);

            String formatMsg = getString(R.string.fba_get_data);
            if (formatMsg != null) {
                onStepInfo(String.format(formatMsg, metaType, metaName));
            }

			/*
			 * Имя файла и вложения на английском, для 1С не важно имя, а
			 * встроенный zip не умеет работать с UTF-8 в именах вложений
			 */
            String simpleName = clazz.getSimpleName().replaceAll(".java", "");
            String fPath = new File(fTmpDir,
                    String.format("%s.json", simpleName)).getAbsolutePath();

            fJson = mExchangeStrategy.getData(all, metaType, metaName, "", fPath);
        }
        return fJson;
    }

    /**
     * Уведомить сервер об успешности получения данных, в независимости от того
     * получены данные полностью или частично (и если не было принудительного
     * прерывания процедуры обмена)
     *
     * @throws ExchangeDataProviderException
     */
    private void doNotifyDataReceipt() throws ExchangeDataProviderException {
        if (DEBUG) {
            Dbg.d(TAG, "doNotifyDataReceipt");
        }

        if (!continueExecute()) {
            return;
        }
        onStepInfo(R.string.fba_register_data_receipt);
        mExchangeStrategy.registerDataReceipt("");
    }

    /**
     * Парсинг JSON данных и обновление локальной базы
     *
     * @param mapJsonData
     * @throws SQLException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doUpdateLocalDB(HashMap<Class<?>, File> mapJsonData) throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "doUpdateLocalDB");
        }

        if (!continueExecute()) {
            return;
        }

        if (mListener != null) {
            mListener.onDeserializeJson(mapJsonData);
        }

        if (mapJsonData.size() > 0) {

            onStepInfo(R.string.fba_update_local_db);
            JSONProvider json = getJsonProvider();

            for (Class clazz : mapJsonData.keySet()) {
                File file = mapJsonData.get(clazz);

                String metaType = MetadataHelper.getMetadataType(clazz);
                String metaName = MetadataHelper.getMetadataName(clazz);

                String formatMsg = getString(R.string.fba_update_table);
                if (formatMsg != null) {
                    onStepInfo(String.format(formatMsg, metaType, metaName));
                }

                List<Object> lstData = json.fromJsonArray(clazz, file);
                if (lstData != null) {
                    boolean saveToDb = true;
                    if (mListener != null) {
                        boolean update = mListener.onUpdateTable(clazz, lstData);
                        saveToDb = update && lstData.size() > 0;
                    }

                    if (saveToDb) {
                        dbCreateOrUpdateCollection(clazz, lstData);
                    }
                }

            }
        }

    }

    /**
     * Очистить флаг модифицированности по успешно переданным объектам.
     *
     * @throws SQLException
     */
    private void doClearModifiedLocalDB() throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "doClearModifiedLocalDB");
        }

        if (mMapSendObject.size() > 0) {
            // Очистка признака модифицированности строго по переданному списку
            // объектов
            dbClearModifiedObject(mMapSendObject, false);
            mMapSendObject.clear();
        }
    }

}
