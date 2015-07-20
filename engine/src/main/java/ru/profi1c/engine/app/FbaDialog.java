package ru.profi1c.engine.app;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;

public abstract class FbaDialog extends AppCompatDialog {

    public FbaDialog(Context context) {
        super(context);
    }

    public FbaDialog(Context context, int theme) {
        super(context, theme);
    }

    protected FbaDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
