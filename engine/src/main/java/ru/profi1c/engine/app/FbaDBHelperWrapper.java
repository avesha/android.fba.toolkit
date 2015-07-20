package ru.profi1c.engine.app;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;

import ru.profi1c.engine.meta.DBOpenHelper;

/**
 * Обертка – помощник доступа и создания {@link ru.profi1c.engine.meta.DBOpenHelper} используется в
 * {@link FbaDBActivity,FbaDBIntentService,FbaDBPreferenceActivity}
 */
public class FbaDBHelperWrapper {

    private final Context mContext;

    private DBOpenHelper mDbHelper;
    private volatile boolean mCreated = false;
    private volatile boolean mDestroyed = false;

    public FbaDBHelperWrapper(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Get a helper for this action.
     */
    public DBOpenHelper getHelper() {

        if (mDbHelper == null) {
            if (!mCreated) {
                throw new IllegalStateException(
                        "A call has not been made to onCreate() yet so the helper is null");
            } else if (mDestroyed) {
                throw new IllegalStateException(
                        "A call to onDestroy has already been made and the helper cannot be used after that point");
            } else {
                throw new IllegalStateException("Helper is null for some unknown reason");
            }
        } else {
            return mDbHelper;
        }
    }

    /**
     * Get a connection source for this action.
     */
    public ConnectionSource getConnectionSource() {
        return getHelper().getConnectionSource();
    }

    public void onCreate() {
        if (mDbHelper == null) {
            mDbHelper = getHelperInternal();
            mCreated = true;
        }
    }

    public void onDestroy() {
        releaseHelper();
        mDestroyed = true;
    }

    /**
     * This is called internally by the class to populate the helper object
     * instance. This should not be called directly by client code unless you
     * know what you are doing. Use {@link #getHelper()} to get a helper
     * instance. If you are managing your own helper creation, override this
     * method to supply this activity with a helper instance.
     * <p/>
     * <p>
     * <b> NOTE: </b> If you override this method, you most likely will need to
     * override the 'releaseHelper' (OrmLiteSqliteOpenHelper) method as
     * well.
     * </p>
     */
    protected synchronized DBOpenHelper getHelperInternal() {
        Class<? extends DBOpenHelper> dbClass = FbaApplication.from(getContext())
                .getDBHelperClass();
        OpenHelperManager.setOpenHelperClass(dbClass);
        return OpenHelperManager.getHelper(getContext(), dbClass);
    }

    /**
     * Release the helper instance created in
     * 'getHelperInternal'. You most likely will not need to
     * call this directly since {@link #onDestroy()} does it for you.
     * <p/>
     * <p>
     * <b> NOTE: </b> If you override this method, you most likely will need to
     * override the 'getHelperInternal' method as well.
     * </p>
     */
    protected void releaseHelper() {
        if (mDbHelper != null) {
            OpenHelperManager.releaseHelper();
            mDbHelper = null;
        }
    }

}
