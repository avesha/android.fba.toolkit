package ru.profi1c.engine.exchange;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.Calendar;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.app.FbaDBIntentService;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.util.NetHelper;

/**
 * Сервис обмена, выполняет обмен с базой 1С через web-сервис по фиксированным
 * правилам. Это сервис должен быть указан в манифесте вашей программы.
 */
public class ExchangeService extends FbaDBIntentService {
    private static final String TAG = ExchangeService.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final String EXTRA_VARIANT_ORDINAL = "ex_ordinal";
    private static final String EXTRA_FORCE = "ex_force";

    private BaseExchangeSettings mExchangeSettings;
    private ExchangeVariant mExchangeVariant;
    private boolean mForce;
    private BaseExchangeTask mExchangeTask;

    private ExchangeServiceBinder mServiceBinder = new ExchangeServiceBinder();
    private ExchangeObserver mExchangeObserver;

    /**
     * Получить намерение, используемое для запуска сервиса. Процедура обмена
     * выполняется по фиксированным правилам определенным в {@link ExchangeTask}
     *
     * @param context
     * @param variant вариант обмена
     * @return
     */
    public static Intent getIntent(Context context, ExchangeVariant variant) {
        Intent i = new Intent(context, ExchangeService.class);
        i.putExtra(EXTRA_VARIANT_ORDINAL, variant.ordinal());
        return i;
    }

    /**
     * Запустить сервис обмена по фиксированным правилам определенным в
     * {@link ExchangeTask}
     *
     * @param context
     */
    public static void start(Context context, ExchangeVariant variant) {
        context.startService(getIntent(context, variant));
    }

    /**
     * Запустить сервис обмена по фиксированным правилам определенным в
     * {@link ExchangeTask}. Настройки планировщика игнорируются.
     *
     * @param context
     */
    public static void startForce(Context context, ExchangeVariant variant) {
        Intent i = getIntent(context, variant);
        i.putExtra(EXTRA_FORCE, true);
        context.startService(i);
    }

    /**
     * Остановить сервис обмена
     *
     * @param context
     */
    public static void stop(Context context) {
        context.stopService(new Intent(context, ExchangeService.class));
    }

    public ExchangeService() {
        super(TAG);
    }

    /**
     * Возвращает наблюдатель (установленный или 'по умолчанию')
     *
     * @return
     */
    private ExchangeObserver getCurrentExchangeObserver() {
        if (mExchangeObserver == null) {
            mExchangeObserver = new SimpleExchangeObserver(this, new Handler());
        }
        return mExchangeObserver;
    }

    /**
     * Установить наблюдателя за работой сервиса. Если не задан, то используется
     * наблюдатель по умолчанию, который создает уведомления по завершению
     * обмена или при возникновении ошибки. <br>
     * Метод доступен при подключении к сервису через связующий класс
     * {@link ExchangeServiceBinder}
     *
     * @param exchangeObserver
     */
    public void setExchangeObserver(ExchangeObserver exchangeObserver) {
        mExchangeObserver = exchangeObserver;
    }

    /**
     * Установить задачу, которая выполняет обмен с сервером 1С. Если не
     * установлена, то обмен выполняется по фиксированным правилам определенным
     * в {@link ExchangeTask} <br>
     * Метод доступен при подключении к сервису через связующий класс
     * {@link ExchangeServiceBinder}
     *
     * @param exchangeTask
     */
    public void setExchangeTask(BaseExchangeTask exchangeTask) {
        mExchangeTask = exchangeTask;
    }

    public void setForce(boolean force) {
        this.mForce = force;
    }

    /**
     * Установить вариант обмена для процедуры обмена по фиксированным правилам
     * определенным в {@link ExchangeTask}). Метод доступен при подключении к
     * сервису через связующий класс {@link ExchangeServiceBinder}
     *
     * @param exchangeVariant
     */
    public void setExchangeVariant(ExchangeVariant exchangeVariant) {
        mExchangeVariant = exchangeVariant;
    }

    /**
     * Получить задачу, которая выполняет обмен с сервером 1С. Если не
     * установлена, то обмен выполняется по фиксированным правилам определенным
     * в {@link ExchangeTask}
     *
     * @param intent Намерение с которым запускается этот сервис
     * @return
     */
    protected BaseExchangeTask getExchangeTask(Intent intent) {

        BaseExchangeTask runTask;
        if (mExchangeTask != null) {
            runTask = mExchangeTask;

        } else {

            // выполнять обмен по фиксированным правилам
            // получить текущий вариант обмена
            ExchangeVariant variant = getExchangeVariant(intent);
            if (variant == null) {
                throw new IllegalStateException(
                        "ExchangeVariant is not specified when starting a service!");
            }

            runTask = mExchangeSettings.getDefaultExchangeTask(variant, getHelper());
            if (runTask == null) {
                ExchangeStrategy exchangeStrategy = new ExchangeStrategy(mExchangeSettings);
                runTask = new ExchangeTask(variant, exchangeStrategy, getHelper());
            }
        }
        return runTask;
    }

    /**
     * Возвращает текущий вариант обмена, который может быть установлен через
     * передачу значения намерению @see
     * {@link ExchangeService#getIntent(Context, ExchangeVariant)}} или при
     * подключении к сервису через связующий класс {@link ExchangeServiceBinder}
     */
    protected ExchangeVariant getExchangeVariant(Intent startIntent) {
        if (mExchangeVariant == null) {
            // в сервису не подключались через Binder, значить вариант передан
            // через намерение
            int ex_ordinal = startIntent.getIntExtra(EXTRA_VARIANT_ORDINAL, 0);
            mExchangeVariant = ExchangeVariant.values()[ex_ordinal];
        }
        return mExchangeVariant;
    }

    /**
     * Связующий класс для (локального) доступа к сервису
     */
    public class ExchangeServiceBinder extends Binder {
        public ExchangeService getService() {
            return ExchangeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return mServiceBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final Context context = getApplicationContext();
            mExchangeSettings = FbaApplication.from(context).getExchangeSettings();

            if (NetHelper.isConnected(context)) {

                boolean doRun = true;
                if (intent.hasExtra(EXTRA_FORCE)) {
                    mForce = intent.getBooleanExtra(EXTRA_FORCE, false);
                }

                //Не принудительный запуск — проверить расписание
                if (!mForce && mExchangeSettings.isEnableSchedule()) {
                    if (!todayIsWorkDay()) {
                        doRun = false;
                        Dbg.i(TAG, " not work day.");
                    }
                }

                if (doRun) {
                    handleStart(intent);
                }

            } else {
                Dbg.i(TAG, " no connect!");
            }

        }
    }

    private boolean todayIsWorkDay() {
        Calendar cal = Calendar.getInstance();
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        return mExchangeSettings.isExchangeWeekDays(weekDay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doEndExchange();
    }

    private void doStartExchange() {
        ResponseHandler.register(getCurrentExchangeObserver());
    }

    private void doEndExchange() {
        ResponseHandler.unregister(mExchangeObserver);
    }

    /*
     * Начать процедуру обмена
     */
    private void handleStart(Intent intent) {
        try {
            doStartExchange();
            BaseExchangeTask exTask = getExchangeTask(intent);
            exTask.call();
        } catch (Exception e) {
            Dbg.printStackTrace(e);
        } finally {
            doEndExchange();
        }
    }

    /**
     * Создать задание для автоматического запуска обновления по расписанию.
     * Процедура обмена выполняется по фиксированным правилам определенным в
     * {@link ExchangeTask}
     *
     * @param context
     * @param variant
     */
    public static final void createScheduleUpdate(Context context, BaseExchangeSettings exSetting,
            ExchangeVariant variant) {
        setRepeatingAlarm(context, exSetting, variant);
    }

    private static void setRepeatingAlarm(Context context, BaseExchangeSettings exSetting,
            ExchangeVariant variant) {

		/*
         * Расписание ставим на запуск каждый день, а совпадает ли день недели
		 * проверим непосредственно при запуске
		 */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, exSetting.getExchangeTimeH());
        cal.set(Calendar.MINUTE, exSetting.getExchangeTimeM());
        cal.set(Calendar.SECOND, 0);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        long nextAlarm = cal.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pi = createPendingIntent(context, variant);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextAlarm, AlarmManager.INTERVAL_DAY,
                pi);
        if (DEBUG) {
            Dbg.d(TAG, "setRepeatingAlarm: next alarm = " + DateHelper.format(cal.getTime()));
        }
    }


    /**
     * Отменить задание автоматического обмена
     *
     * @param context
     */
    public static final void cancelSchedule(Context context, ExchangeVariant variant) {
        if (DEBUG) {
            Dbg.d(TAG, "cancelSchedule");
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = createPendingIntent(context, variant);
        alarmManager.cancel(pi);
    }

    /**
     * Получить 'отложенное намерение' для запуска сервиса из планировщика
     *
     * @param context
     * @param variant
     * @return
     */
    private static PendingIntent createPendingIntent(Context context, ExchangeVariant variant) {
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                ExchangeService.getIntent(context, variant), PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}
