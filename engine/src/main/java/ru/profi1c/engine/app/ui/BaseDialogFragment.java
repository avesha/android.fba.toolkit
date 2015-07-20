package ru.profi1c.engine.app.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaDialogFragment;

public abstract class BaseDialogFragment extends FbaDialogFragment {
    private static final String TAG = BaseDialogFragment.class.getSimpleName();

    private static String formatTag(Class<? extends BaseDialogFragment> classFragment) {
        return String.format("%s_%s", TAG, classFragment.getSimpleName());
    }

    protected static BaseDialogFragment show(FragmentActivity activity,
            Class<? extends BaseDialogFragment> classFragment, Bundle args) {

        final String fragmentTag = formatTag(classFragment);

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(fragmentTag);
        if (prev != null) {
            ft.remove(prev);
        }

        BaseDialogFragment fragment = newInstance(classFragment, args);
        if (fragment != null) {
            fragment.show(ft, fragmentTag);
        }
        return fragment;
    }

    protected static BaseDialogFragment newInstance(
            Class<? extends BaseDialogFragment> classFragment, Bundle args) {
        BaseDialogFragment fragment = null;
        try {
            fragment = (BaseDialogFragment) classFragment.newInstance();
            fragment.setArguments(args);
        } catch (java.lang.InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return fragment;
    }

    protected static void hide(FragmentActivity activity,
            Class<? extends BaseDialogFragment> classFragment) {
        final String fragmentTag = formatTag(classFragment);
        FragmentManager fm = activity.getSupportFragmentManager();
        BaseDialogFragment fragment = (BaseDialogFragment) fm.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragment.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        // for support lib use: 	getDialog().setOnDismissListener(null);
        // for real fragments: 		getDialog().setDismissMessage(null);
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        super.onDestroyView();
    }

    protected void setDialogResult(Object data) {
        Activity a = getActivity();
        if (a instanceof IDialogFragmentResultListener) {
            ((IDialogFragmentResultListener) a).onDialogFragmentResult(this, data);
        }
    }

}
