package ru.profi1c.engine.exchange;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.BaseAppSettings;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.RefDao;
import ru.profi1c.engine.meta.Table;
import ru.profi1c.engine.meta.TableDao;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.meta.TablePartDao;

/**
 * Базовый класс для реализация процедуры обмена по фиксированным правилам.
 * Чтобы предотвратить засыпание телефона на время выполнения задачи ставит
 * блокировку PARTIAL_WAKE_LOCK
 * <p/>
 * Требуется разрешение "android.permission.WAKE_LOCK"
 */
public abstract class BaseExchangeTask implements Callable<Boolean> {
    private static final String TAG = BaseExchangeTask.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    protected final ExchangeStrategy mExchangeStrategy;
    protected final ExchangeVariant mVariant;

    protected final BaseAppSettings mAppSettings;
    protected final MetadataHelper mMetadataHelper;
    protected final DBOpenHelper mDbHelper;

    protected IExchangeCallbackListener mListener;

    private JSONProvider mJsonProvider;

    /**
     * Реализация процедуры обмена по фиксированным правилам, должна быть
     * переопределена в наследнике
     *
     * @return истина, если обмен завершен успешно
     * @throws Exception
     */
    protected abstract boolean doExecute() throws Exception;

    public BaseExchangeTask(ExchangeVariant exchangeVariant, ExchangeStrategy exchangeStrategy,
            DBOpenHelper dbOpenHelper) {
        mExchangeStrategy = exchangeStrategy;
        mVariant = exchangeVariant;
        mDbHelper = dbOpenHelper;

        FbaApplication app = FbaApplication.from(exchangeStrategy.getContext());
        mAppSettings = app.getAppSettings();
        mMetadataHelper = app.getMetadataHelper();
    }

    public void setListener(IExchangeCallbackListener listener) {
        this.mListener = listener;
    }

    public IExchangeCallbackListener getListener() {
        return mListener;
    }

    public Context getContext() {
        return mExchangeStrategy.getContext();
    }

    protected JSONProvider getJsonProvider() {
        if (mJsonProvider == null) {
            mJsonProvider = new JSONProvider();
        }
        return mJsonProvider;
    }

    private PowerManager.WakeLock getLock(Context context) {
        PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock lock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        lock.setReferenceCounted(true);
        return lock;
    }

    protected boolean continueExecute() {
        return !ResponseHandler.isCancelled();
    }

    /**
     * Запуск обмена с блокировкой перехода устройства в спящий режим
     */
    @Override
    public Boolean call() throws Exception {
        boolean result = false;
        PowerManager.WakeLock lock = null;
        try {
            lock = getLock(getContext());
            lock.acquire();
            result = doExecuteTask();
        } finally {
            if (lock != null) {
                lock.release();
            }

        }
        return result;
    }

    /*
     * Вызов реализации процедуры обмена у наследника
     */
    private boolean doExecuteTask() {
        boolean success = false;
        try {
            ResponseHandler.startExchange(mVariant);
            success = doExecute();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (mAppSettings.isFullErrorStack()) {
                String strTrace = Dbg.getStackTraceString(e);
                msg = String.format("Message: %s\nStack trace:%s", msg, strTrace);

                // этот вывод ошибки оставить для разработчика
                Log.e(Const.APP, strTrace);
            }
            onError("BaseExchangeTask", msg);
        } finally {
            onComplete(success, null);
        }
        return success;
    }

    /**
     * Прочитать строковый ресурс
     */
    protected String getString(int resId) {
        if (getContext() != null) {
            return getContext().getString(resId);
        }
        return null;
    }

    /**
     * Предать сообщение об ошибке подписчикам
     */
    protected void onError(String event, String msg) {
        if (DEBUG) {
            Dbg.d(TAG, " onError, event: " + event + " msg: " + msg);
        }
        if (mListener != null) {
            mListener.onError(event, msg);
        }
        ResponseHandler.error(msg);
    }

    /**
     * Передать результат обмена подписчикам
     */
    protected void onComplete(boolean success, Object result) {
        if (DEBUG) {
            Dbg.d(TAG, "onComplete, success: " + success + " result: " + result);
        }
        if (mListener != null) {
            mListener.onComplete(success);
        }
        ResponseHandler.finishExchange(success);
    }

    /**
     * Передать инф. сообщение о выполнении шага подписчикам
     *
     * @param mgs сообщение
     */
    protected void onStepInfo(String mgs) {
        if (DEBUG) {
            Dbg.d(TAG, "onStepInfo, msg: " + mgs);
        }
        if (mgs != null) {
            ResponseHandler.stepInfo(mgs);
        }
    }

    /**
     * Передать инф. сообщение о выполнении шага подписчикам
     *
     * @param resId идентификатор ресурса сообщения
     */
    protected void onStepInfo(int resId) {
        onStepInfo(getString(resId));
    }

    /**
     * Сохранить или обновить коллекцию однотипных объектов в базе данных
     *
     * @param classOfT класс, элементы которого в коллекции
     * @param lstData  коллекция элементов
     * @throws SQLException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <T> void dbCreateOrUpdateCollection(Class<T> classOfT, List<T> lstData)
            throws SQLException {

        for (T obj : lstData) {

            // установить ключ записи
            if (obj instanceof Table) {
                Table row = ((Table) obj);
                row.setRecordKey(row.createRecordKey());
            }

            // сохранить сам объект
            dbCreateOrUpdate(classOfT, obj);

            // если справочник или документ, то могут быть табличные части
            if (obj instanceof Ref) {

                // очистить табличные части по этому объекту если есть
                clearTablePartsByOwner(obj);

                // полученные по обмену табличные части
                HashMap<String, List<? extends TablePart>> lstTmpTableParts = ((Ref) obj).getTmpTablePartData();
                if (lstTmpTableParts != null) {
                    for (String key : lstTmpTableParts.keySet()) {

                        List<? extends TablePart> lstTablePart = lstTmpTableParts.get(key);
                        if (lstTablePart != null && lstTablePart.size() > 0) {
                            Class classOfTp = lstTablePart.get(0).getClass();
                            for (TablePart tp : lstTablePart)
                                dbCreate(classOfTp, tp);
                        }
                    }

                    // очистить кеш табличных частей
                    ((Ref) obj).clearTmpTablePartData();
                }
            }

        }
    }

    /**
     * Очистить таблицы табличных частей по этому объекту
     *
     * @param <T>
     * @throws SQLException
     */
    protected <T> void clearTablePartsByOwner(T owner) throws SQLException {

        List<Class<? extends TablePart>> lstClassTP = ((Ref) owner).getTabularSections();
        if (lstClassTP != null) {
            for (Class<? extends TablePart> classTp : lstClassTP) {
                @SuppressWarnings("unchecked")
                TablePartDao<TablePart> dao = (TablePartDao<TablePart>) mDbHelper.getDao(classTp);
                dao.clearTable((Ref) owner);
            }
        }
        
    }

    /**
     * Создать или обновить объект в базе данных
     *
     * @param classOfT класс сохраняемого объекта
     * @param obj      сохраняемый объект
     * @throws SQLException
     */
    protected <T> void dbCreateOrUpdate(Class<T> classOfT, T obj) throws SQLException {
        Dao<T, String> dao = mDbHelper.getDao(classOfT);
        dao.createOrUpdate(obj);
    }

    /**
     * Создать объект в базе данных
     *
     * @param classOfT класс сохраняемого объекта
     * @param obj      сохраняемый объект
     * @throws SQLException
     */
    protected <T> void dbCreate(Class<T> classOfT, T obj) throws SQLException {
        Dao<T, String> dao = mDbHelper.getDao(classOfT);
        dao.create(obj);
    }

    /**
     * Очистить флаг модифицированности по успешно переданным объектам. Очистка
     * признака модифицированности по все таблице выполняется быстрей, но если
     * во время обмена (в другом потоке например) пользователь
     * добавляет/изменяет данные его изменения при полной очистке так же будут
     * сброшены.
     *
     * @param mapList  ‘Соответствие’ класс и список переданных объектов по нему
     * @param clearAny Флаг очистки признака модифицированности у всех элементов
     *                 таблицы, если ложь – модифицированность изменяется по
     *                 переданному списку объектов
     * @throws SQLException
     */
    protected void dbClearModifiedObject(HashMap<Class<?>, List<Object>> mapList, boolean clearAny)
            throws SQLException {

        for (Class<?> clazz : mapList.keySet()) {

            if (MetadataHelper.extendsOfClass(clazz, Ref.class)) {
                RefDao<Ref> dao = (RefDao<Ref>) mDbHelper.getDao(clazz);
                if (clearAny) {
                    dao.setModified(false);
                    dao.setNew(false);
                } else {
                    List<Object> lstData = mapList.get(clazz);
                    dao.setModified(false, false, lstData);
                }

            } else {
                TableDao<Table> dao = (TableDao<Table>) mDbHelper.getDao(clazz);
                if (clearAny) {
                    dao.setModified(false);
                } else {
                    List<Object> lstData = mapList.get(clazz);
                    dao.setModified(false, lstData);
                }

            }

        }
    }
}
