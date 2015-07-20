package ru.profi1c.engine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.profi1c.engine.app.ui.BaseDialogFragment;
import ru.profi1c.engine.app.ui.IDialogFragmentResultListener;


/**
 * Базовый класс для использования активити (Activity) в приложении Android при
 * использовании библиотеки 'FBA'.
 */
public abstract class FbaActivity extends AppCompatActivity
        implements IDialogFragmentResultListener {

    final FbaActivityHelper mActivityHelper = FbaActivityHelper.createInstance(this);

    public FbaActivityHelper getActivityHelper() {
        return mActivityHelper;
    }

    public IFbaSettingsProvider getFbaSettingsProvider() {
        return getActivityHelper();
    }

    public IFbaActivityNavigation getFbaActivityNavigation() {
        return getActivityHelper();
    }

    /**
     * Показать всплывающее уведомление
     */
    public void showToast(final String message) {
        mActivityHelper.showToast(message);
    }

    /**
     * Показать всплывающее уведомление
     */
    public void showToast(int resId) {
        mActivityHelper.showToast(getString(resId));
    }

    /**
     * Показать простой диалог c сообщением и кнопкой ОК
     *
     * @param title   заголовок диалога
     * @param message сообщение
     */
    public void showMessage(String title, String message) {
        mActivityHelper.showMessage(title, message);
    }

    /**
     * Показать простой диалог c сообщением и кнопкой ОК
     *
     * @param resIdTitle идентификатор строкового ресурса для заголовка диалога
     * @param message    сообщение
     */
    public void showMessage(int resIdTitle, String message) {
        mActivityHelper.showMessage(this.getString(resIdTitle), message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogFragmentResult(BaseDialogFragment dialog, Object data) {
        //do nothing
    }
}