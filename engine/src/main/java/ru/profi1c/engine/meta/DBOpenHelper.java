package ru.profi1c.engine.meta;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaApplication;

/**
 * Помощник для работы с базой данных Sqlite программы (через OrmLite)
 */
public abstract class DBOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DBOpenHelper.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    /*
     * Не рекомендуется изменять или обфусцировать эти строки
     */
    private static final String PREF_NAME = "DBOpenHelper";
    private static final String PREF_KEY_CREATE_TABLES = "tables_are_created";

    private final Context mContext;

    public DBOpenHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // разрешить внешние ключи (в т.ч каскадное удаление в связанных
            // таблицах)
            // доступно начиная с SQLite версии 3.6.19, Android 2.2 поставляется
            // с SQLite 3.6.22,
            // значит минимально поддерживаемая версия Android это 2.2
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * Создание базы данных (как правило, 1 раз при первом запуске программы).
     * Будут созданы таблицы для справочников, документов, регистров сведений и
     * прочие дополнительные таблицы не имеющие отражения в 1С
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            createTables(getMetadataHelper());
        } catch (SQLException e) {
            Dbg.printStackTrace(e);
            throw new FbaRuntimeException(
                    "Can not create database tables, check the metadata scheme in 1C!");
        }

    }

    /**
     * Динамическое обновление базы данных при изменении объектов пока не
     * реализовано. При обновлении версии таблицы пересоздаются заново, данные
     * при этом не сохраняются.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
            int oldVersion, int newVersion) {

        // recreate
        try {
            dropTables(getMetadataHelper());
        } catch (SQLException e) {
            Dbg.printStackTrace(e);
            throw new FbaRuntimeException(
                    "Can not delete database tables, check the metadata scheme in 1C!");
        }
        onCreate(database, connectionSource);
    }

    /**
     * Класс-помощник для работы с метаданными объектов (соответствие объектов
     * 1С и классов Java)
     *
     * @return
     */
    private MetadataHelper getMetadataHelper() {
        return FbaApplication.from(mContext).getMetadataHelper();
    }

    /**
     * Создать таблицы базы данных sqlite
     *
     * @param metaHelper
     * @throws SQLException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void createTables(MetadataHelper metaHelper) throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "create tables");
        }

        try {
            List<Class> lstAllClasses = metaHelper.getAllDBClasses();
            for (Class clazz : lstAllClasses) {
                TableUtils.createTable(connectionSource, clazz);
            }

            // сохранить флаг того, что таблицы созданы
            setBooleanPrefValues(PREF_KEY_CREATE_TABLES, true);

        } catch (InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }

    }

    /**
     * Удалить все таблицы базы данных
     *
     * @param metaHelper
     * @throws SQLException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void dropTables(MetadataHelper metaHelper) throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "drop tables");
        }

        try {
            List<Class> lstAllClasses = metaHelper.getAllDBClasses();
            for (Class clazz : lstAllClasses) {
                TableUtils.dropTable(connectionSource, clazz, true);
            }

            // очистить флаг того, что таблицы созданы
            setBooleanPrefValues(PREF_KEY_CREATE_TABLES, false);

        } catch (InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }

    }

    /**
     * Очистить все таблицы базы данных (удаляются все записи, структура колонок
     * остается)
     *
     * @param metaHelper
     * @throws SQLException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void clearTables(MetadataHelper metaHelper) throws SQLException {
        if (DEBUG) {
            Dbg.d(TAG, "clear tables");
        }

        try {
            List<Class> lstAllClasses = metaHelper.getAllDBClasses();
            for (Class clazz : lstAllClasses) {
                clearTable(clazz);
            }
        } catch (InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }

    }

    /**
     * Очистить таблицу базы данных (удаляются все записи, структура колонок
     * остается)
     *
     * @param classOfT Класс таблицы
     * @throws SQLException
     */
    public <T> void clearTable(Class<T> classOfT) throws SQLException {
        TableUtils.clearTable(connectionSource, classOfT);
    }

    /**
     * Очистить таблицу базы данных (удалить все записи или по условию, если
     * указано имя колонки).Условие может быть только одно
     *
     * @param dao        Менеджер DAO
     * @param columnName Имя колонки таблицы для условия
     * @param operator   Оператор сравнения, например (<,>,= и т..п)
     * @param value      Значение сравнения
     * @throws SQLException
     */
    public static <T> void clearTable(Dao<T, String> dao, String columnName, String operator,
            Object value) throws SQLException {

        DeleteBuilder<T, String> builder = dao.deleteBuilder();
        if (!TextUtils.isEmpty(columnName)) {
            builder.where().rawComparison(columnName, operator, value);
        }
        dao.delete(builder.prepare());
    }

    /**
     * Возвращает истина если база данных существует и в ней созданы таблицы
     * согласно схемы метаданных 1С
     *
     * @return
     */
    public static boolean isExistsDataBase(Context context) {
        return getBooleanPrefValues(context, PREF_KEY_CREATE_TABLES, false);
    }

    /**
     * Сохранить значение типа булево из настроек этого класса
     *
     * @param key
     * @param value
     */
    private void setBooleanPrefValues(String key, boolean value) {
        SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, 0);
        prefs.edit().putBoolean(key, value).commit();
    }

    /**
     * Получить значение типа булево из настроек этого класса
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    private static boolean getBooleanPrefValues(Context context, String key, boolean defValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defValue);
    }

}
