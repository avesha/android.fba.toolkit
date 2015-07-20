package ru.profi1c.engine.app;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Базовый класс для фрагментов, все фрагменты должны размещаться на
 * {@link FbaActivity} или ее наследниках. Размещение фрагментов на
 * {@link FbaListActivity} недопустимо.
 */
public class FbaFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof FbaActivity)) {
            throw new IllegalStateException(
                    getClass().getSimpleName() + " must be attached to a FbaActivity.");
        }
        super.onAttach(activity);
    }

    public FbaActivity getFbaActivity() {
        Activity activity = getActivity();
        if (activity instanceof FbaActivity) {
            return (FbaActivity) activity;
        } else {
            throw new IllegalStateException(
                    "This fragment can be placed only on the 'FbaActivity'");
        }
    }

}
