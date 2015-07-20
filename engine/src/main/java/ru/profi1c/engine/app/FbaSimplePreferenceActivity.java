package ru.profi1c.engine.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.profi1c.engine.R;
import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.widget.TimePreference;

/**
 * Простая реализация редактирования предпочтений (настроек программы) .
 * Возможность указать: <br>
 * - имя пользователя и пароль, используемые для входа в программу и для
 * авторизации на web-сервисе 1С; <br>
 * - ip-адрес web-сервиса; <br>
 * - расписание (время и дни недели) для выполнения задания обмена в фоновом
 * режиме; <br>
 * Эта активити должна быть указан в манифесте вашей программы.
 */
public class FbaSimplePreferenceActivity extends FbaPreferenceActivity {

    private static final String PREF_KEY_USER_NAME = "fba_key_user_name";
    private static final String PREF_KEY_USER_PASSWORD = "fba_key_user_password";
    private static final String PREF_KEY_SERVER_IP = "fba_key_server_ip";
    private static final String PREF_KEY_DEVICE_ID = "fba_key_device_id";
    private static final String PREF_KEY_ENABLE_EXCHANGE_SCHEDULE = "fba_key_enable_exchange_schedule";
    private static final String PREF_KEY_EXCHANGE_SCHEDULE_TIME = "fba_key_exchange_schedule_time";
    private static final String PREF_KEY_EXCHANGE_SCHEDULE_DAYS = "fba_key_exchange_schedule_days";
    private static final String PREF_KEY_SOUND_SUCCESS = "fba_key_sound_success";
    private static final String PREF_KEY_SOUND_ERROR = "fba_key_sound_error";
    private static final String PREF_KEY_SOUND_NEW_APP = "fba_key_sound_new_app";
    private static final String PREF_KEY_ABOUT = "fba_key_about";

    private BaseExchangeSettings mExchangeSettings;

    private TimePreference mExchangeScheduleTime;
    private Preference mExchangeScheduleDays;

    private String[] mWeekDays, mShortWeekDays;
    private int[] mIndexWeekDays;
    private boolean[] mCheckedWeekDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fba_simple_preference);
        mExchangeSettings = getFbaSettingsProvider().getExchangeSettings();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExchangeSettings.save();
    }

    private void init() {
        initBaseExchangeSettings();
        initAbout();
        initExchangeScheduleScreen();
    }

    private void initBaseExchangeSettings() {
        // имя пользователя
        EditTextPreference etPref = (EditTextPreference) findPreference(PREF_KEY_USER_NAME);
        etPref.setText(mExchangeSettings.getUserName());
        etPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mExchangeSettings.setUserName((String) newValue);
                return true;
            }
        });

        // пароль
        etPref = (EditTextPreference) findPreference(PREF_KEY_USER_PASSWORD);
        etPref.setText(mExchangeSettings.getPassword());
        etPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mExchangeSettings.setPassword((String) newValue);
                return true;
            }
        });

        // адрес сервера
        etPref = (EditTextPreference) findPreference(PREF_KEY_SERVER_IP);
        etPref.setText(mExchangeSettings.getServerIP());
        etPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mExchangeSettings.setServerIP((String) newValue);
                return true;
            }
        });

        // серийный номер этого устройства
        Preference pref = findPreference(PREF_KEY_DEVICE_ID);
        pref.setSummary(mExchangeSettings.getDeviceId());

    }

    /**
     * инициализация данных для расписания автоматической загрузки
     */
    private void initExchangeScheduleScreen() {

        CheckBoxPreference enableSchedule = (CheckBoxPreference) findPreference(
                PREF_KEY_ENABLE_EXCHANGE_SCHEDULE);
        enableSchedule.setChecked(mExchangeSettings.isEnableSchedule());
        enableSchedule.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isEnable = newValue.equals(true);
                mExchangeSettings.setEnableSchedule(isEnable);
                changeUpdateSchedule();
                return true;
            }
        });

        // установка времени
        mExchangeScheduleTime = (TimePreference) findPreference(PREF_KEY_EXCHANGE_SCHEDULE_TIME);
        mExchangeScheduleTime.setLastHour(mExchangeSettings.getExchangeTimeH());
        mExchangeScheduleTime.setLastMinute(mExchangeSettings.getExchangeTimeM());
        formatExchangeTimeSummary(mExchangeSettings.getExchangeTimeH(),
                mExchangeSettings.getExchangeTimeM());

        mExchangeScheduleTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String time = (String) newValue;
                int hour = TimePreference.getHour(time);
                int minute = TimePreference.getMinute(time);

                mExchangeSettings.setExchangeTimeH(hour);
                mExchangeSettings.setExchangeTimeM(minute);

                changeUpdateSchedule();

                formatExchangeTimeSummary(hour, minute);
                return true;
            }
        });

        // выбор дней
        mExchangeScheduleDays = findPreference(PREF_KEY_EXCHANGE_SCHEDULE_DAYS);
        mExchangeScheduleDays.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                setExchangeScheduleDays();
                return false;
            }
        });

        mWeekDays = DateHelper.getWeekdays();
        mShortWeekDays = DateHelper.getShortWeekdays();
        mIndexWeekDays = DateHelper.getIndexWeekdays();
        mCheckedWeekDays = new boolean[7];

        for (int i = 0; i < mWeekDays.length; i++) {
            mCheckedWeekDays[i] = mExchangeSettings.isExchangeWeekDays(mIndexWeekDays[i]);
        }

        formatSummaryExchangeDays();

        // звуки
        RingtonePreference ringPref = (RingtonePreference) findPreference(PREF_KEY_SOUND_SUCCESS);
        String soundUri = null;
        if (mExchangeSettings.getSoundExchangeSuccess() != null) {
            soundUri = mExchangeSettings.getSoundExchangeSuccess().toString();
            ringPref.setDefaultValue(soundUri);
        }
        formatSoundSummary(ringPref, soundUri);

        ringPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String uri = (String) newValue;

                mExchangeSettings.setSoundSuccess(uri);
                formatSoundSummary((RingtonePreference) preference, uri);
                return true;
            }
        });

        ringPref = (RingtonePreference) findPreference(PREF_KEY_SOUND_ERROR);
        soundUri = null;
        if (mExchangeSettings.getSoundExchangeError() != null) {
            soundUri = mExchangeSettings.getSoundExchangeError().toString();
            ringPref.setDefaultValue(soundUri);
        }
        formatSoundSummary(ringPref, soundUri);

        ringPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String uri = (String) newValue;

                mExchangeSettings.setSoundError(uri);
                formatSoundSummary((RingtonePreference) preference, uri);
                return true;
            }
        });

        ringPref = (RingtonePreference) findPreference(PREF_KEY_SOUND_NEW_APP);
        soundUri = null;
        if (mExchangeSettings.getSoundDownloadedApk() != null) {
            soundUri = mExchangeSettings.getSoundDownloadedApk().toString();
            ringPref.setDefaultValue(soundUri);
        }
        formatSoundSummary(ringPref, soundUri);

        ringPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String uri = (String) newValue;

                mExchangeSettings.setSoundNewApp(uri);
                formatSoundSummary((RingtonePreference) preference, uri);
                return true;
            }
        });

    }

    protected void formatExchangeTimeSummary(int hour, int minute) {
        mExchangeScheduleTime.setSummary(TimePreference.formatTime(hour, minute));
    }

    /**
     * Показать сокращенные названия дней недели
     */
    protected void formatSummaryExchangeDays() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mCheckedWeekDays.length; i++) {
            if (mCheckedWeekDays[i]) {
                sb.append(mShortWeekDays[i]).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        mExchangeScheduleDays.setSummary(sb.toString());
    }

    /**
     * Выбор дней в диалоге
     */
    protected void setExchangeScheduleDays() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.fba_days_of_week);
        builder.setMultiChoiceItems(mWeekDays, mCheckedWeekDays, new OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                mCheckedWeekDays[position] = isChecked;
                mExchangeSettings.setExchangeWeekDays(mIndexWeekDays, mCheckedWeekDays);

                changeUpdateSchedule();

                formatSummaryExchangeDays();

            }
        });
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    /**
     * В резюме показать наименование мелодии
     */
    protected void formatSoundSummary(RingtonePreference ringPref, String uriString) {
        String summary = getString(R.string.fba_mute);
        if (!TextUtils.isEmpty(uriString)) {
            Uri uri = Uri.parse(uriString);
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            summary = ringtone.getTitle(this);
        }
        ringPref.setSummary(summary);
    }

    /**
     * о программе
     */
    private void initAbout() {

        Preference prefAbout = findPreference(PREF_KEY_ABOUT);
        prefAbout.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                final Context ctx = FbaSimplePreferenceActivity.this;

                LayoutInflater li = LayoutInflater.from(ctx);
                View view = li.inflate(R.layout.fba_simple_about_dialog_layout, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setView(view);

                int idResIcon = getFbaSettingsProvider().getAppSettings().getIdResIconLauncher();
                if (idResIcon == 0) {
                    idResIcon = R.mipmap.fba_ic_launcher;
                }

                builder.setIcon(idResIcon);
                builder.setTitle(R.string.app_name);

                Resources res = ctx.getResources();
                final String ab_info = res.getString(R.string.app_descr);

                // set name version of app
                TextView tvVersionName = (TextView) view.findViewById(R.id.fba_version_name);
                String versionName = AppHelper.getAppVersion(ctx);

                StringBuilder sb = new StringBuilder();
                sb.append("v ").append(versionName);
                tvVersionName.setText(sb.toString());

                // format the description as html
                TextView tvAboutInfo = (TextView) view.findViewById(R.id.fba_about_info);
                tvAboutInfo.setText(Html.fromHtml(ab_info));

                builder.setNegativeButton(R.string.fba_ok, null);

                AlertDialog ad = builder.create();
                ad.setCanceledOnTouchOutside(true);
                ad.show();

                return true;
            }
        });
    }

    /**
     * Запустить/остановить планировщик обмена согласно настроек.
     */
    protected void changeUpdateSchedule() {
        ExchangeReceiver.createSchedulerTasks(getApplicationContext());
    }

    public static final class AppSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.fba_simple_preference);
        }
    }
}
