package ru.profi1c.engine.app;

import android.app.Activity;

import com.j256.ormlite.support.ConnectionSource;

import ru.profi1c.engine.meta.DBOpenHelper;

/**
 * Фрагмент с доступом к базе данных, доступны методы {@link #getHelper()} и
 * {@link #getConnectionSource()}, должен располагаться на {@link FbaDBActivity}
 */
public class FbaDBFragment extends FbaFragment {

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof FbaDBActivity)) {
            throw new IllegalStateException(
                    getClass().getSimpleName() + " must be attached to a FbaDBActivity.");
        }
        super.onAttach(activity);
    }

    public FbaDBActivity getFbaDBActivity() {
        Activity activity = getActivity();
        if (activity instanceof FbaDBActivity) {
            return (FbaDBActivity) activity;
        } else {
            throw new IllegalStateException(
                    "This fragment can be placed only on the 'FbaDBActivity'");
        }
    }

    /**
     * Get a helper for this action.
     */
    public DBOpenHelper getHelper() {

        FbaDBActivity activity = (FbaDBActivity) getActivity();
        if (activity != null) {
            return activity.getHelper();
        } else {
            throw new IllegalStateException(
                    "Fragment not attached (or already detached) to FbaDBActivity!");
        }
    }

    /**
     * Get a connection source for this action.
     */
    public ConnectionSource getConnectionSource() {
        FbaDBActivity activity = (FbaDBActivity) getActivity();
        if (activity != null) {
            return activity.getConnectionSource();
        } else {
            throw new IllegalStateException(
                    "Fragment not attached (or already detached) to FbaDBActivity!");
        }
    }
}
