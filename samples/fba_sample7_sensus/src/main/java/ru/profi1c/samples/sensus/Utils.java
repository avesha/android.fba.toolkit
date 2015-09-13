package ru.profi1c.samples.sensus;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public final class Utils {

    public static final void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private Utils() {}
}
