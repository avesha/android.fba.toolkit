package ru.profi1c.engine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.FbaRuntimeException;

/*
 * http://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
 Here's a general solution that overrides the Spinner view. It overrides setAdapter() to set the initial position to -1,
 and proxies the supplied SpinnerAdapter to display the prompt string for position<0.
 This has been tested on android 1.5 through 4.1, but buyer beware! Because this solution relies on reflection to call
 the private AdapterView.setNextSelectedPositionInt() and AdapterView.setSelectedPositionInt(),
 it's not guaranteed to work in future OS updates. It seems likely that it will, but it is by no means guaranteed.
 */

/**
 * A modified Spinner that doesn't automatically select the first entry in the
 * list.
 * <p/>
 * Shows the prompt if nothing is selected.
 */
public class NoDefaultSpinner extends Spinner {

    private String mSelectionPrompt;
    private boolean mUsePromptAdapter;


    public NoDefaultSpinner(Context context) {
        super(context);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapterPromptSelect(SpinnerAdapter orig, String promt) {
        final SpinnerAdapter adapter = newProxy(orig);
        mSelectionPrompt = promt;

        super.setAdapter(adapter);
        clearSelectedPosition();
        mUsePromptAdapter = true;
    }

    @Override
    public void setSelection(int position) {
        if (position < 0 && mUsePromptAdapter) {
            cleanSelection();
        } else {
            super.setSelection(position);
        }
    }

    @Override
    public void setSelection(int position, boolean animate) {
        if (position < 0 && mUsePromptAdapter) {
            cleanSelection();
        } else {
            super.setSelection(position, animate);
        }
    }

    private void cleanSelection() {
        clearSelectedPosition();
        requestLayout();
        invalidate();
    }

    protected SpinnerAdapter newProxy(SpinnerAdapter obj) {
        return (SpinnerAdapter) java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(), new Class[]{SpinnerAdapter.class},
                new SpinnerAdapterProxy(obj));
    }

    protected void clearSelectedPosition() {
        try {
            final Method m = AdapterView.class.getDeclaredMethod("setNextSelectedPositionInt",
                    int.class);
            m.setAccessible(true);
            m.invoke(this, Const.NOT_SPECIFIED);

            final Method n = AdapterView.class.getDeclaredMethod("setSelectedPositionInt",
                    int.class);
            n.setAccessible(true);
            n.invoke(this, Const.NOT_SPECIFIED);

        } catch (Exception e) {
            throw new FbaRuntimeException(e);
        }
    }

    /**
     * Intercepts getView() to display the prompt if position < 0
     */
    protected class SpinnerAdapterProxy implements InvocationHandler {

        protected final SpinnerAdapter mObj;
        protected final Method mGetView;
        protected TextView mDefaultView;

        protected SpinnerAdapterProxy(SpinnerAdapter obj) {
            mObj = obj;
            try {
                mGetView = SpinnerAdapter.class.getMethod("getView", int.class, View.class,
                        ViewGroup.class);
            } catch (Exception e) {
                throw new FbaRuntimeException(e);
            }
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            try {
                if (m.equals(mGetView) && (Integer) (args[0]) < 0) {
                    return getView((Integer) args[0], (View) args[1], (ViewGroup) args[2]);
                } else {
                    return m.invoke(mObj, args);
                }
            } catch (InvocationTargetException e) {
                throw new FbaRuntimeException(e);
            } catch (Exception e) {
                throw new FbaRuntimeException(e);
            }
        }

        protected View getView(int position, View convertView, ViewGroup parent)
                throws IllegalAccessException {
            if (position < 0) {
                return buildDefaultView();
            }
            return mObj.getView(position, convertView, parent);
        }

        protected View buildDefaultView() {
            if (mDefaultView == null) {
                mDefaultView = (TextView) ((LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        android.R.layout.simple_spinner_item, NoDefaultSpinner.this, false);
                mDefaultView.setText(mSelectionPrompt);
            }
            return mDefaultView;
        }
    }

}