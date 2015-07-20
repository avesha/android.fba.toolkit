package ru.profi1c.engine.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import ru.profi1c.engine.util.ReflectionHelper;

/**
 * Выбор времени в настройках (в предпочтениях)
 */
public class TimePreference extends DialogPreference {
    public static final String MIDNIGHT = "00:00";
    private int mLastHour = 0;
    private int mLastMinute = 0;
    private TimePicker mTimePicker = null;

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[1]));
    }

    public static String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, ReflectionHelper.getInternalAttribute("dialogPreferenceStyle"));
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

    }

    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(true);
        return (mTimePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mTimePicker.setCurrentHour(mLastHour);
        mTimePicker.setCurrentMinute(mLastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mLastHour = mTimePicker.getCurrentHour();
            mLastMinute = mTimePicker.getCurrentMinute();

            String time = formatTime(mLastHour, mLastMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString(MIDNIGHT);
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        mLastHour = getHour(time);
        mLastMinute = getMinute(time);
    }

    public void setLastMinute(int lastMinute) {
        mLastMinute = lastMinute;
    }

    public void setLastHour(int lastHour) {
        mLastHour = lastHour;
    }
}
