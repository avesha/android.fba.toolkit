package ru.profi1c.engine.app;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import java.lang.ref.WeakReference;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;
import ru.profi1c.engine.exchange.BaseExchangeTask;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeService;
import ru.profi1c.engine.exchange.ExchangeTask;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.exchange.ResponseHandler;
import ru.profi1c.engine.exchange.SimpleExchangeObserver;

/**
 * Базовый класс активити с наблюдателем за сервисом обмена
 */
public abstract class FbaDBExchangeActivity extends FbaDBActivity implements OnCancelListener {
    private static final String TAG = FbaDBExchangeActivity.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private ExchangeService mExchangeService;
    private Intent mIntentService;
    private ServiceConnection mConn;
    private boolean mBound = false;

    private ExchangeObserver mWSExchangeObserver;
    private Handler mExchangeHandler;

    private WeakReference<ProgressDialog> mRefProgressDialog;
    private boolean mCancelableExchange;
    private boolean mCanceled;

    /**
     * Ваша реализация наблюдателя, если не уставлен — используется внутренний
     * который просто отображает модальный диалог с отображением прогресса
     * обмена
     */
    protected abstract ExchangeObserver getExchangeObserver();

    /**
     * Запустить обмен с web-сервисом. Процедура обмена выполняется по
     * фиксированным правилам определенным в {@link ExchangeTask}
     *
     * @param variant    вариант обмена
     * @param cancelable возможность отмены процедуры обмена
     */
    public void startExchange(ExchangeVariant variant, boolean cancelable) {
        if (mBound) {
            mCancelableExchange = cancelable;
            mCanceled = false;
            ResponseHandler.register(mWSExchangeObserver);

            startExchangeServiсe(variant);
        } else {
            Dbg.i(TAG, "Could not connect to the exchange service!");
        }
    }

    /**
     * Запустить обмен с web-сервисом по вашим правилам
     *
     * @param exchangeTask задача обмена
     * @param cancelable   возможность отмены процедуры обмена
     */
    public void startExchange(BaseExchangeTask exchangeTask, boolean cancelable) {

        if (mBound) {
            mCancelableExchange = cancelable;
            mCanceled = false;
            ResponseHandler.register(mWSExchangeObserver);

            startExchangeServiсe(exchangeTask);
        } else {
            Dbg.i(TAG, "Could not connect to the exchange service!");
        }

    }

    /**
     * Принудительно прервать обмен (не рекомендуется)
     *
     * @param force если истина, сервис принудительно останавливается сразу, иначе
     *              останавливается при первой возможности - по завершении
     *              атомарной операции
     */
    public void cancelExchange(boolean force) {
        mCanceled = true;
        ResponseHandler.cancel();
        ResponseHandler.unregister(mWSExchangeObserver);
        closeProgressExchangeDlg();

        if (force) {
            if (mBound) {
                mExchangeService.stopSelf();
            } else {
                ExchangeService.stop(getApplicationContext());
            }
        }
    }

    /**
     * Возвращает наблюдатель установленный или по умолчанию
     */
    private ExchangeObserver getCurrentExchangeObserver() {
        ExchangeObserver observer = getExchangeObserver();
        if (observer == null) {
            observer = new WSSimpleExchangeObserver(getApplicationContext(), mExchangeHandler);
        }
        return observer;
    }

    /**
     * Запустить сервис обмена
     *
     * @param variant вариант обмена
     */
    private void startExchangeServiсe(ExchangeVariant variant) {
        mExchangeService.setExchangeVariant(variant);
        mExchangeService.setExchangeObserver(getCurrentExchangeObserver());
        // При ручном запуске обмена расписание не учитывается.
        mExchangeService.setForce(true);
        startService(mIntentService);
    }

    /**
     * Запустить сервис обмена
     *
     * @param exchangeTask задача обмена
     */
    private void startExchangeServiсe(BaseExchangeTask exchangeTask) {
        mExchangeService.setExchangeTask(exchangeTask);
        mExchangeService.setExchangeObserver(getCurrentExchangeObserver());
        startService(mIntentService);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExchangeHandler = new Handler();
        mWSExchangeObserver = getCurrentExchangeObserver();

		/*
         * намерение на запуск, все остальные параметры передаются сервису через
		 * подключение непосредственно перед запуском.
		 */
        mIntentService = new Intent(this, ExchangeService.class);

        // подключение к сервису
        mConn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (DEBUG) {
                    Dbg.d(TAG, "onServiceDisconnected: " + name.getClassName());
                }
                mBound = false;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder builder) {
                if (DEBUG) {
                    Dbg.d(TAG, "onServiceConnected: " + name.getClassName());
                }
                mExchangeService = ((ExchangeService.ExchangeServiceBinder) builder).getService();
                mExchangeService.setExchangeObserver(mWSExchangeObserver);
                mBound = true;
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mIntentService, mConn, Context.BIND_AUTO_CREATE);
        ResponseHandler.register(mWSExchangeObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeProgressExchangeDlg();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            mExchangeService.setExchangeObserver(null);
            unbindService(mConn);
            mBound = false;
        }
        ResponseHandler.unregister(mWSExchangeObserver);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        cancelExchange(false);
    }

    private ProgressDialog getProgressDialog() {
        if (mRefProgressDialog != null) {
            return mRefProgressDialog.get();
        }
        return null;
    }

    /**
     * Создать диалог — прогрессор для отображения процесса обмена с
     * web-сервисом
     */
    protected void createProgressExchangeDlg() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog dlg = getProgressDialog();
                if (dlg == null) {
                    dlg = new ProgressDialog(FbaDBExchangeActivity.this);
                    dlg.setIndeterminate(true);
                    dlg.setCancelable(mCancelableExchange);
                    dlg.setCanceledOnTouchOutside(false);
                    dlg.setOnCancelListener(FbaDBExchangeActivity.this);
                    mRefProgressDialog = new WeakReference<ProgressDialog>(dlg);
                }
            }
        });
    }

    protected void onProgress(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog dlg = getProgressDialog();
                if (dlg != null && !isFinishing()) {
                    if (!dlg.isShowing()) {
                        dlg.show();
                    }
                    // Show current message in progress dialog
                    dlg.setMessage(message);
                }
            }
        });
    }

    /**
     * Закрыть диалог отображающий процесс обмена с web-сервисом
     */
    protected void closeProgressExchangeDlg() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog dlg = getProgressDialog();
                if (dlg != null) {
                    dlg.dismiss();
                }
            }
        });
    }

    /**
     * Наблюдатель за процедурой обмена
     */
    protected class WSSimpleExchangeObserver extends SimpleExchangeObserver {

        public WSSimpleExchangeObserver(Context context, Handler handler) {
            super(context, handler);
        }

        @Override
        public void onStart(ExchangeVariant variant) {
            super.onStart(variant);
            if (!mCanceled) {
                createProgressExchangeDlg();
                onProgress(FbaDBExchangeActivity.this.getString(R.string.fba_exchange_start));
            }
        }

        @Override
        public void onBuild() {
            super.onBuild();
            if (!mCanceled) {
                createProgressExchangeDlg();
                onProgress(FbaDBExchangeActivity.this.getString(R.string.fba_exchange_build));
            }
        }

        @Override
        public void onStepInfo(final String msg) {
            super.onStepInfo(msg);
            if (DEBUG) {
                Dbg.d(TAG, "onStepInfo: " + msg);
            }
            if (!mCanceled) {
                onProgress(msg);
            }
        }

        @Override
        public void onFinish(boolean success) {
            super.onFinish(success);
            closeProgressExchangeDlg();
        }

    }

}
