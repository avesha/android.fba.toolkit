package ru.profi1c.engine.app;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.profi1c.engine.app.ui.BaseDialogFragment;
import ru.profi1c.engine.app.ui.IDialogFragmentResultListener;

/**
 * Базовый класс для использования активити (ListActivity) в приложении Android
 * при использовании библиотеки 'FBA'.
 */
public class FbaListActivity extends ListActivity implements IDialogFragmentResultListener {

    private AppCompatDelegate mDelegate;
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
     *
     * @param message сообщение
     */
    public void showToast(final String message) {
        mActivityHelper.showToast(message);
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
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        mActivityHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
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

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
