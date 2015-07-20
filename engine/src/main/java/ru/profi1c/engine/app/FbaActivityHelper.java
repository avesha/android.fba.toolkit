package ru.profi1c.engine.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;
import ru.profi1c.engine.app.ui.AlertDialogFragment;
import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.meta.MetadataHelper;

/**
 * Класс который обрабатывает некоторые общие возможности всех
 * {@link FbaActivity}
 */
public class FbaActivityHelper implements IFbaSettingsProvider, IFbaActivityNavigation {
    private static final String TAG = FbaActivityHelper.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    protected final Activity mActivity;

    /**
     * Factory method for creating {@link FbaActivityHelper} objects for a given
     * activity.
     */
    public static FbaActivityHelper createInstance(Activity activity) {
        return new FbaActivityHelper(activity);
    }

    protected FbaActivityHelper(Activity activity) {
        this.mActivity = activity;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public FbaApplication getApp() {
        return (FbaApplication) mActivity.getApplication();
    }

    /**
     * Перейти к основной активности (Activity)
     */
    @Override
    public void goHome(FbaActivity fbaActivity) {
        FbaApplication app = getApp();
        if (fbaActivity.getClass().equals(app.getMainActivityClass())) {
            return;
        }
        mActivity.startActivity(app.getHomeIntent());
    }

    /**
     * Помощник доступа к намерению для запуска основной активности (Activity)
     */
    @Override
    public Intent getHomeIntent() {
        return getApp().getHomeIntent();
    }

    /**
     * Возвращает истина, если текущая активити является главной
     */
    @Override
    public boolean isMainActivity() {
        return mActivity.getClass().equals(getApp().getMainActivityClass());
    }

    /**
     * Открыть настройки программы. Активити, которая используется для
     * редактирования настроек задается в App.getPreferenceActivityClass()
     */
    @Override
    public void showPreferenceActivity() {
        FbaApplication app = getApp();
        Class<? extends FbaPreferenceActivity> classPref = app.getPreferenceActivityClass();
        if (classPref == null) {
            Dbg.i(TAG,
                    "showPreferenceActivity: not set reference activity class on your App.getPreferenceActivityClass() method");
        } else {
            mActivity.startActivity(new Intent(mActivity, classPref));
        }
    }

    /**
     * Открыть диалог авторизации пользователя при запуске программы
     */
    @Override
    public void showLoginActivity(int requestCode) {

        FbaApplication app = getApp();
        Class<? extends FbaActivity> classLogin = app.getLoginActivityClass();
        if (classLogin == null) {
            Dbg.i(TAG,
                    "startLoginActivity: not set logon activity class on your App.getLoginActivityClass() method");
        } else {
            Intent i = new Intent(mActivity, classLogin);
            mActivity.startActivityForResult(i, requestCode);
        }
    }

    /**
     * Помощник для работы с метаданными объектов определенный для этого
     * приложения fbaApplication.getMetadataHelper()
     */
    @Override
    public MetadataHelper getMetadataHelper() {
        return getApp().getMetadataHelper();
    }

    /**
     * Настройки обмена web-сервисом 1С определенные для этого приложения
     * fbaApplication.getExchangeSettings()
     */
    @Override
    public BaseExchangeSettings getExchangeSettings() {
        return getApp().getExchangeSettings();
    }

    /**
     * Настройки приложения определенные для этого приложения
     * fbaApplication.getAppSettings()
     */
    @Override
    public BaseAppSettings getAppSettings() {
        return getApp().getAppSettings();
    }

    /**
     * Показать всплывающее уведомление
     */
    public void showToast(final String message) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mActivity.getApplicationContext(), message, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void showToast(final int resId) {
        showToast(mActivity.getString(resId));
    }

    /**
     * Показать простой диалог c сообщением и кнопкой ОК
     *
     * @param title   заголовок диалога
     * @param message сообщение
     */
    public void showMessage(String title, String message) {
        AlertDialogFragment.show((FragmentActivity) mActivity,
                AlertDialogFragment.DIALOG_ID_NOT_SPECIFIED, mActivity.getString(R.string.app_name),
                message, mActivity.getString(android.R.string.ok), true);
    }

    /**
     * Показать простой диалог c сообщением и кнопкой ОК
     *
     * @param resIdTitle идентификатор строкового ресурса для заголовка диалога
     * @param message    сообщение
     */
    public void showMessage(int resIdTitle, String message) {
        showMessage(mActivity.getString(resIdTitle), message);
    }

    /*
     * Возвращает истина если требуется запрашивать пароль при запуске
     */
    private boolean isRequirePassword() {
        return getApp().getLoginActivityClass() != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) {
            Dbg.d(TAG, "onCreate, class name:" + this.getClass().getSimpleName());
        }
        if (savedInstanceState == null && isMainActivity()) {
            onCreateNewSession();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.REQUESTCODE_SECURITY_PASSWORD) {
            if (resultCode == Activity.RESULT_OK) {
                SecurityHelper.setCheckPassword(true);
            } else {
                mActivity.finish();
            }
        }
    }

    /**
     * Запущена новая сессия приложения, не поворот экрана
     */
    private void onCreateNewSession() {

        BaseExchangeSettings exSettings = getExchangeSettings();
        BaseAppSettings appSettings = getAppSettings();

        boolean continueRun = true;

        // проверить новую версию и предложить обновится
        if (ApkUpdateHelper.existNewVersion(this)) {
            continueRun = appSettings.isForceUpdateApp();
            ApkUpdateHelper.askUpdateProgram(this);
        }

        if (continueRun) {

            // запросим пароль (если он установлен в настройках)
            // или предложим установить настройки
            if (!exSettings.isSetAutorizeParams()) {

                showToast(R.string.fba_not_set_autorize_params);
                showPreferenceActivity();

            } else {
                if (isRequirePassword()) {
                    SecurityHelper.doRequirePassword(this);
                }
            }

        }

    }

}
