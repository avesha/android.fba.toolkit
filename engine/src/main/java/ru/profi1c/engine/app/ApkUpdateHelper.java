package ru.profi1c.engine.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;

import java.io.File;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;
import ru.profi1c.engine.util.AppHelper;

/**
 * Помощник обновления программы
 */
public class ApkUpdateHelper {
    private static final String TAG = ApkUpdateHelper.class.getSimpleName();
    private static final String NEW_APK_FILENAME = "newVersion.apk";

    /**
     * Возвращает путь к файлу по которому будет сохранена новая версия
     * программы
     *
     * @param appSettings настройки программы
     * @return
     */
    public static File getNewVersionPath(BaseAppSettings appSettings) {
        return new File(appSettings.getBackupDir(), NEW_APK_FILENAME);
    }

    /**
     * Возвращает истина, если скачана новая версия программы и ее следует
     * обновить
     *
     * @param activityHelper
     * @return
     */
    public static boolean existNewVersion(FbaActivityHelper activityHelper) {

        boolean exist = false;

        File fApk = getNewVersionPath(activityHelper.getAppSettings());
        if (fApk.exists()) {

            int curCode = AppHelper.getAppVersionCode(activityHelper.getActivity());
            int apkCode = AppHelper.getApkAppVersionCode(activityHelper.getActivity(),
                    fApk.getAbsolutePath());
            exist = (apkCode > curCode);
        }
        return exist;
    }

    /**
     * Предложить обновить программу. Текущий процесс может быть завершен, если
     * это установлено в настройках, см
     * {@link BaseAppSettings#isForceUpdateApp()}
     *
     * @param activityHelper
     */
    public static void askUpdateProgram(FbaActivityHelper activityHelper) {

        final Activity activity = activityHelper.getActivity();
        final BaseAppSettings settings = activityHelper.getAppSettings();

        final String appName = AppHelper.getAppLabel(activity);
        final String message = activity.getString(R.string.fba_get_app_success);

        final File fApk = getNewVersionPath(settings);
        AlertDialog.Builder builder = new Builder(activity);
        builder.setTitle(appName);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                AppHelper.installApplication(activity, fApk);

                if (settings.isForceUpdateApp()) {
                    Dbg.i(TAG,
                            "askUpdateProgram: finish main activity because flag 'AppSettings.isForceUpdateApp()' is true");
                    activity.finish();
                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (settings.isForceUpdateApp()) {
                    Dbg.i(TAG,
                            "askUpdateProgram: finish main activity because flag 'AppSettings.isForceUpdateApp()' is true");
                    activity.finish();
                }

            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

}
