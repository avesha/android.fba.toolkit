package ru.profi1c.engine.app;

import android.os.Bundle;

import com.j256.ormlite.support.ConnectionSource;

import ru.profi1c.engine.meta.DBOpenHelper;

/**
 * Базовый класс для использования ListActivity c доступом к базе данных. Вы
 * можете просто вызвать {@link #getHelper()}, чтобы получить ваш класс
 * помощника, или {@link #getConnectionSource()}, чтобы получить
 * {@link ConnectionSource}.
 */
public class FbaDBListActivity extends FbaListActivity {

    private FbaDBHelperWrapper mDBHelperWrapper;

    /**
     * Get a helper for this action.
     */
    public DBOpenHelper getHelper() {
        if (mDBHelperWrapper == null) {
            throw new IllegalStateException(
                    "DBOpenHelper was not available, because the event 'onCreate()' has not yet arrived!");
        }
        return mDBHelperWrapper.getHelper();
    }

    /**
     * Get a connection source for this action.
     */
    public ConnectionSource getConnectionSource() {
        if (mDBHelperWrapper == null) {
            throw new IllegalStateException(
                    "DBOpenHelper was not available, because the event 'onCreate()' has not yet arrived!");
        }
        return mDBHelperWrapper.getConnectionSource();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDBHelperWrapper = new FbaDBHelperWrapper(getApplicationContext());
        mDBHelperWrapper.onCreate();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBHelperWrapper.onDestroy();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }

}
