package ru.profi1c.engine.app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

import ru.profi1c.engine.R;

public abstract class BaseAlertDialogFragment extends BaseDialogFragment {

    public static final String EXTRA_DIALOG_ID = "dialog_id";
    public static final String EXTRA_DIALOG_TITLE = "dialog_title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_POSITIVE_BUTTON_TEXT = "positive_button_text";
    public static final String EXTRA_NEGATIVE_BUTTON_TEXT = "negative_button_text";
    public static final String EXTRA_NEUTRAL_BUTTON_TEXT = "neutral_button_text";
    public static final String EXTRA_CANCELABLE = "cancelable";
    public static final String EXTRA_ICON_ID = "icon_id";
    public static final String EXTRA_SINGLE_CHOICE_ITEMS = "single_choice_items";
    public static final String EXTRA_SINGLE_CHOICE_CHECKED_ITEM = "single_choice_checked_items";
    public static final String EXTRA_CUSTOM_ACTION_ITEMS = "custom_action_items";
    public static final String EXTRA_CUSTOM_ACTION_LAYOUT = "custom_action_layout";

    private int mDialogId;
    private boolean mCancelable;

    protected abstract void onBuildDialog(AlertDialog.Builder builder);

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = getArguments();
        mDialogId = bundle.getInt(EXTRA_DIALOG_ID, mDialogId);

        if (bundle.containsKey(EXTRA_DIALOG_TITLE)) {
            builder.setTitle(bundle.getString(EXTRA_DIALOG_TITLE));
        }

        if (bundle.containsKey(EXTRA_MESSAGE)) {
            builder.setMessage(bundle.getString(EXTRA_MESSAGE));
        }

        if (bundle.containsKey(EXTRA_POSITIVE_BUTTON_TEXT)) {
            builder.setPositiveButton(bundle.getString(EXTRA_POSITIVE_BUTTON_TEXT),
                    mOnDialogClickListener);
        }

        if (bundle.containsKey(EXTRA_NEGATIVE_BUTTON_TEXT)) {
            builder.setNegativeButton(bundle.getString(EXTRA_NEGATIVE_BUTTON_TEXT),
                    mOnDialogClickListener);
        }

        if (bundle.containsKey(EXTRA_NEUTRAL_BUTTON_TEXT)) {
            builder.setNeutralButton(bundle.getString(EXTRA_NEUTRAL_BUTTON_TEXT),
                    mOnDialogClickListener);
        }

        if (bundle.containsKey(EXTRA_ICON_ID)) {
            builder.setIcon(bundle.getInt(EXTRA_ICON_ID));
        }

        if (bundle.containsKey(EXTRA_SINGLE_CHOICE_ITEMS)) {
            @SuppressWarnings("unchecked")
            List<IDescription> lst = (List<IDescription>) bundle.getSerializable(
                    EXTRA_SINGLE_CHOICE_ITEMS);
            int checkedItem = bundle.getInt(EXTRA_SINGLE_CHOICE_CHECKED_ITEM, 0);

            DescriptionAdapter adapter = new DescriptionAdapter(getActivity(),
                    R.layout.fba_popup_menu_item_singlechoice, lst);
            builder.setSingleChoiceItems(adapter, checkedItem, mOnDialogClickListener);

        } else if (bundle.containsKey(EXTRA_CUSTOM_ACTION_ITEMS)) {

            @SuppressWarnings("unchecked")
            List<IActionItem> lst = (List<IActionItem>) bundle.getSerializable(
                    EXTRA_CUSTOM_ACTION_ITEMS);

            int resLayout = bundle.getInt(EXTRA_CUSTOM_ACTION_LAYOUT,
                    R.layout.fba_simple_action_dropdown_item);
            ActionsAdapter adapter = new ActionsAdapter(getActivity(), lst, resLayout);
            builder.setAdapter(adapter, mOnDialogClickListener);
        }

        mCancelable= true;
        if (bundle.containsKey(EXTRA_CANCELABLE)) {
            mCancelable = bundle.getBoolean(EXTRA_CANCELABLE);
            builder.setCancelable(mCancelable);
        }
        onBuildDialog(builder);
        Dialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(mCancelable);

        //for this dialog fragment
        setCancelable(mCancelable);
        return dlg ;
    }

    protected DialogInterface.OnClickListener mOnDialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            setDialogResult(which);
        }
    };

    public int getDialogId() {
        return mDialogId;
    }

    public static class ArgumentsBuilder {
        private final Activity mActivity;
        private final Bundle mBundle;

        public ArgumentsBuilder(Activity activity, int dialogId) {
            mActivity = activity;
            mBundle = new Bundle();
            mBundle.putInt(EXTRA_DIALOG_ID, dialogId);
        }

        private ArgumentsBuilder putString(String key, String value) {
            mBundle.putString(key, value);
            return this;
        }

        private ArgumentsBuilder putBoolean(String key, boolean value) {
            mBundle.putBoolean(key, value);
            return this;
        }

        private ArgumentsBuilder putInt(String key, int value) {
            mBundle.putInt(key, value);
            return this;
        }

        public ArgumentsBuilder setTitle(int resId) {
            setTitle(mActivity.getString(resId));
            return this;
        }

        public ArgumentsBuilder setTitle(CharSequence title) {
            if (!TextUtils.isEmpty(title)) {
                putString(EXTRA_DIALOG_TITLE, title.toString());
            }
            return this;
        }

        public ArgumentsBuilder setMessage(int resId) {
            setMessage(mActivity.getString(resId));
            return this;
        }

        public ArgumentsBuilder setMessage(CharSequence message) {
            if (!TextUtils.isEmpty(message)) {
                putString(EXTRA_MESSAGE, message.toString());
            }
            return this;
        }

        public ArgumentsBuilder setCancelable(boolean cancelable) {
            putBoolean(EXTRA_CANCELABLE, cancelable);
            return this;
        }

        public ArgumentsBuilder setIcon(int iconId) {
            putInt(EXTRA_ICON_ID, iconId);
            return this;
        }

        public ArgumentsBuilder setPositiveButton(int resId) {
            setPositiveButton(mActivity.getString(resId));
            return this;
        }

        public ArgumentsBuilder setPositiveButton(CharSequence text) {
            putString(EXTRA_POSITIVE_BUTTON_TEXT, text.toString());
            return this;
        }

        public ArgumentsBuilder setNegativeButton(int resId) {
            setNegativeButton(mActivity.getString(resId));
            return this;
        }

        public ArgumentsBuilder setNegativeButton(CharSequence text) {
            putString(EXTRA_NEGATIVE_BUTTON_TEXT, text.toString());
            return this;
        }

        public ArgumentsBuilder setNeutralButton(int resId) {
            setNeutralButton(mActivity.getString(resId));
            return this;
        }

        public ArgumentsBuilder setNeutralButton(CharSequence text) {
            putString(EXTRA_NEUTRAL_BUTTON_TEXT, text.toString());
            return this;
        }

        public ArgumentsBuilder setSingleChoiceItems(List<? extends IDescription> items,
                int checkedItem) {
            mBundle.putSerializable(EXTRA_SINGLE_CHOICE_ITEMS, (Serializable) items);
            mBundle.putInt(EXTRA_SINGLE_CHOICE_CHECKED_ITEM, checkedItem);
            return this;
        }

        public ArgumentsBuilder setCustomActionItems(List<? extends IActionItem> items,
                int layoutResId) {
            mBundle.putSerializable(EXTRA_CUSTOM_ACTION_ITEMS, (Serializable) items);
            mBundle.putInt(EXTRA_CUSTOM_ACTION_LAYOUT, layoutResId);
            return this;
        }

        public Bundle create() {
            return mBundle;
        }
    }
}
