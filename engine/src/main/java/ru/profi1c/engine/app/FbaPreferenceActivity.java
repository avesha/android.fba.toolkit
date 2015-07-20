package ru.profi1c.engine.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

import ru.profi1c.engine.app.ui.BaseDialogFragment;
import ru.profi1c.engine.app.ui.IDialogFragmentResultListener;

/**
 * Базовый класс для редактирования предпочтений т.н настроек программы
 * (PreferenceActivity). Доступ к настройкам обмена получается методом
 * {@link FbaPreferenceActivity#getFbaSettingsProvider()#getFbaSettingsProvider()}
 */
public abstract class FbaPreferenceActivity extends PreferenceActivity
        implements IDialogFragmentResultListener {

    public static final String EXTRA_SCREEN_NAME = "extra-screen-name";

    private AppCompatDelegate mDelegate;
    private final FbaActivityHelper mActivityHelper = FbaActivityHelper.createInstance(this);

    private final Stack<Preference> mScreenStack = new Stack<Preference>();
    private CharSequence mRootTitle;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActivityIntent(getIntent());
        mRootTitle = getTitle();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setActivityIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mScreenStack.size() > 0) {
            setTitle(mRootTitle);
            Preference pref = mScreenStack.pop();
            if (pref instanceof PreferenceScreen) {
                setPreferenceScreen((PreferenceScreen) pref);
                return;
            }
        }
        super.onBackPressed();
    }

    private void restoreTitle(CharSequence title) {
        if(!TextUtils.isEmpty(title)){
            setTitle(title);
         }
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

    @Override
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        super.setPreferenceScreen(preferenceScreen);
        findPreferenceScreen(preferenceScreen);
    }

    //fix error 'hide actionbar after open child preference screen',
    //more info: http://stackoverflow.com/questions/29357292/keep-the-actionbar-displayed-in-when-changing-preferencescreen
    private void findPreferenceScreen(PreferenceGroup preferenceGroup) {
        final int count = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference pref = preferenceGroup.getPreference(i);
            if (pref instanceof PreferenceGroup) {

                findPreferenceScreen((PreferenceGroup) pref);

                if (pref instanceof PreferenceScreen) {
                    final String key = pref.getKey();
                    if (!TextUtils.isEmpty(key)) {
                        pref.setIntent(createPreferenceScreenIntent(key));
                    }
                }
            }
        }
    }

    private Intent createPreferenceScreenIntent(final String key) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setComponent(new ComponentName(getApplicationContext(), getClass()));
        i.putExtra(EXTRA_SCREEN_NAME, key);
        return i;
    }

    private void setActivityIntent(final Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)) {
                final String screenName = intent.getStringExtra(EXTRA_SCREEN_NAME);
                if (!TextUtils.isEmpty(screenName)) {
                    openPreferenceScreen(screenName);
                }
            }
        }
    }

    private boolean openPreferenceScreen(final String screenName) {
        final Preference pref = findPreference(screenName);
        if (pref instanceof PreferenceScreen) {
            pushCurrentScreenToStack();

            final PreferenceScreen preferenceScreen = (PreferenceScreen) pref;
            setTitle(preferenceScreen.getTitle());
            setPreferenceScreen((PreferenceScreen) pref);
            return true;
        }
        return false;
    }

    private void pushCurrentScreenToStack() {
        mScreenStack.push(getPreferenceScreen());
    }
}
