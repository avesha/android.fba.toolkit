package ru.profi1c.engine.exchange;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;

import ru.profi1c.engine.Dbg;

/**
 * Обертка ExchangeTask для выполнения как AsyncTask
 */
public abstract class BasExchangeAsyncTask extends AsyncTask<Void, String, Boolean> {

    private final BaseExchangeTask mTask;

    private ExchangeObserver mWSExchangeObserver;
    private Handler mExchangeHandler;

    /**
     * Подготовить выполнение задачи обмена в асинхронном режиме
     *
     * @param exchangeTask задача обмена по определенным правилам
     */
    public BasExchangeAsyncTask(BaseExchangeTask exchangeTask) {
        this.mTask = exchangeTask;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mExchangeHandler = new Handler();
        mWSExchangeObserver = new WSSimpleExchangeObserver(mExchangeHandler);
        ResponseHandler.register(mWSExchangeObserver);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            return mTask.call();
        } catch (Exception e) {
            Dbg.printStackTrace(e);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        ResponseHandler.unregister(mWSExchangeObserver);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        ResponseHandler.cancel();
        ResponseHandler.unregister(mWSExchangeObserver);
        if (mTask.getListener() != null) {
            mTask.getListener().onCancel();
        }
    }

    /**
     * Этот обработчик вызывается при начале обмена в основном потоке (потоке
     * пользовательского интерфейса)
     */
    public abstract void onStart();

    /**
     * Этот обработчик вызывается при подключении к уже запущенному обмену.
     * Вызов в основном потоке (потоке пользовательского интерфейса)
     */
    public abstract void onBuild();

    /**
     * Уведомление о начале выполнения операции обмена. Вызов в основном потоке
     * (потоке пользовательского интерфейса)
     *
     * @param msg
     */
    public abstract void onStepInfo(String msg);

    /**
     * Сообщение об ошибке. Вызов в основном потоке (потоке пользовательского
     * интерфейса)
     *
     * @param msg текст сообщения, может быть null
     */
    public abstract void onError(String msg);

    /**
     * Этот обработчик вызывается по окончании обмена. Вызов в основном потоке
     * (потоке пользовательского интерфейса)
     *
     * @param success флаг успешности обмена
     */
    public abstract void onFinish(boolean success);

    /**
     * Наблюдатель за процедурой обмена
     */
    private class WSSimpleExchangeObserver extends ExchangeObserver {

        public WSSimpleExchangeObserver(Handler handler) {
            super(mTask.getContext(), handler);
        }

        @Override
        public void onStart(ExchangeVariant variant) {
            if (!isCancelled()) {
                mExchangeHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BasExchangeAsyncTask.this.onStart();
                    }
                });
            }
        }

        @Override
        public void onBuild() {
            if (!isCancelled()) {
                mExchangeHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BasExchangeAsyncTask.this.onBuild();
                    }
                });
            }
        }

        @Override
        public boolean onDownloadNewVersionApp(File fApk) {
            return true;
        }

        @Override
        public void onStepInfo(final String msg) {
            if (!isCancelled()) {
                mExchangeHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BasExchangeAsyncTask.this.onStepInfo(msg);
                    }
                });
            }
        }

        @Override
        public void onError(final String msg) {
            mExchangeHandler.post(new Runnable() {
                @Override
                public void run() {
                    BasExchangeAsyncTask.this.onError(msg);
                }
            });
        }

        @Override
        public void onFinish(final boolean success) {
            mExchangeHandler.post(new Runnable() {
                @Override
                public void run() {
                    BasExchangeAsyncTask.this.onFinish(success);
                }
            });
        }
    }

}
