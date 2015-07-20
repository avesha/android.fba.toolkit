package ru.profi1c.engine.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Помощник создания простых диалогов (ввод числа, строки и т.п.)
 */
public final class InputDialogHelper {
    private static final int DEFAULT_PADDING = 5;

    /**
     * Интерфейс, используемый для обработки пользовательского ввода
     */
    public interface OnCompleteListener {

        /**
         * Этот метод будет вызываться при нажатии на кнопку «Ок» в диалоге
         *
         * @param value Введенное пользователем значение
         */
        public void onInputValue(Object value);
    }

    private static EditText mEditText;
    private static int mInputType;
    private static OnCompleteListener mOnCompleteListener;

    private static View createTextEntryView(Context context, String hint, String defValue,
            int inputType) {

        LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(
                new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ll.setOrientation(android.widget.LinearLayout.VERTICAL);
        ll.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        ll.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        mEditText = new EditText(context);
        mEditText.setLayoutParams(
                new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        if (!TextUtils.isEmpty(hint)) {
            mEditText.setHint(hint);
        }

        mEditText.setInputType(inputType);
        mEditText.setText(defValue);
        ll.addView(mEditText);

        return ll;
    }

    private static Object getEditTextValue() {
        Object value = null;

        if (mEditText != null) {
            String strValue = mEditText.getText().toString();

            if ((mInputType & InputType.TYPE_NUMBER_FLAG_SIGNED) > 0) {
                value = NumberFormatHelper.parseInt(strValue);
            } else if ((mInputType & InputType.TYPE_NUMBER_FLAG_DECIMAL) > 0) {
                value = NumberFormatHelper.parseDouble(strValue);
            } else {
                value = strValue;
            }
        }
        return value;
    }

    private static AlertDialog createAlertDialog(Context context, String prompt, String hint,
            String defValue, int inputType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (TextUtils.isEmpty(prompt)) {
            builder.setTitle(AppHelper.getAppLabel(context));
        } else {
            builder.setTitle(prompt);
        }

        builder.setView(createTextEntryView(context, hint, defValue, inputType));
        builder.setPositiveButton(android.R.string.ok, mDlgListener);
        builder.setNegativeButton(android.R.string.cancel, mDlgListener);
        return builder.create();
    }

    private static DialogInterface.OnClickListener mDlgListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mOnCompleteListener != null && which == DialogInterface.BUTTON_POSITIVE) {
                Object value = getEditTextValue();
                mOnCompleteListener.onInputValue(value);
                dialog.dismiss();
            }
        }
    };

    private static AlertDialog inputValue(Context context, String prompt, String hint,
            String defValue, OnCompleteListener listener, int type) {
        mOnCompleteListener = listener;
        mInputType = type;
        AlertDialog dlg = createAlertDialog(context, prompt, hint, defValue, type);
        dlg.show();
        return dlg;
    }

    /**
     * Ввести строку, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как строка
     *
     * @param context  Текущий контекст
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputString(Context context, OnCompleteListener listener) {
        return inputString(context, null, null, null, listener);
    }

    /**
     * Ввести строку, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как строка
     *
     * @param context  Текущий контекст
     * @param defValue значение по умолчанию
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputString(Context context, String defValue,
            OnCompleteListener listener) {
        return inputString(context, null, null, defValue, listener);
    }

    /**
     * Ввести строку, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как строка
     *
     * @param context  Текущий контекст
     * @param prompt   Текст приглашения (поясняющей надписи)
     * @param hint     Текст подсказки для поля ввода
     * @param defValue значение по умолчанию
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputString(Context context, String prompt, String hint,
            String defValue, OnCompleteListener listener) {

        return inputValue(context, prompt, hint, defValue, listener,
                          InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    /**
     * Ввести целое число, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как int
     *
     * @param context  Текущий контекст
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputInt(Context context, OnCompleteListener listener) {
        return inputInt(context, null, null, 0, listener);
    }

    /**
     * Ввести целое число, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как int
     *
     * @param context  Текущий контекст
     * @param defValue значение по умолчанию
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputInt(Context context, int defValue, OnCompleteListener listener) {
        return inputInt(context, null, null, defValue, listener);
    }

    /**
     * Ввести целое число, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как int
     *
     * @param context  Текущий контекст
     * @param prompt   Текст приглашения (поясняющей надписи)
     * @param hint     Текст подсказки для поля ввода
     * @param defValue значение по умолчанию
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputInt(Context context, String prompt, String hint, int defValue,
            OnCompleteListener listener) {

        return inputValue(context, prompt, hint, String.valueOf(defValue), listener,
                          InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    /**
     * Ввести десятичное значение, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как double
     *
     * @param context  Текущий контекст
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputDouble(Context context, OnCompleteListener listener) {
        return inputDouble(context, null, null, 0d, listener);
    }

    /**
     * Ввести десятичное значение, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как double
     *
     * @param context  Текущий контекст
     * @param defValue значение по умолчанию
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputDouble(Context context, double defValue,
            OnCompleteListener listener) {
        return inputDouble(context, null, null, defValue, listener);
    }

    /**
     * Ввести десятичное значение, результат ввода возвращается слушателю
     * OnCompleteListener.onInputValue как double
     *
     * @param context  Текущий контекст
     * @param prompt   Текст приглашения (поясняющей надписи)
     * @param hint     Текст подсказки для поля ввода
     * @param defValue значение по умолчанию
     * @param listener подписчик на обработку результата
     */
    public static AlertDialog inputDouble(Context context, String prompt, String hint,
            double defValue, OnCompleteListener listener) {

        return inputValue(context, prompt, hint, String.valueOf(defValue), listener,
                          InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private InputDialogHelper() {
    }
}
