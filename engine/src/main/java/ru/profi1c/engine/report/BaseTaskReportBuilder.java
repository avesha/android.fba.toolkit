package ru.profi1c.engine.report;

import android.content.Context;

import ru.profi1c.engine.R;
import ru.profi1c.engine.util.task.BaseProgressAsyncTask;

/**
 * Построитель отчета в отдельном потоке с отображением сообщения во время
 * выполнения.
 */
public abstract class BaseTaskReportBuilder implements IReportBuilder {

    private String mProgressMessage;
    private boolean mCancelable;

    private Context mContext;
    private IReportBuilderResult mBuilderResult;

    /**
     * Построение отчета в отдельном потоке
     *
     * @param context основной контекст
     * @return отчет, строка или uri-ссылка на внешний скомпилированный отчет.
     * null не считается как ошибка, а нет исходных данных для
     * построения отчета
     */
    protected abstract Object doInBackground(Context context);

    @Override
    public void build(Context context, IReportBuilderResult builderResult) {
        mContext = context;
        mBuilderResult = builderResult;

        if (mProgressMessage == null)
            mProgressMessage = mContext.getString(R.string.fba_report_build);
        ;

        ProgressAsyncTask task = new ProgressAsyncTask(context, mProgressMessage, mCancelable);
        task.execute();
    }

    /**
     * Возможность отмены пользователем задачи интерактивно
     */
    public void setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
    }

    /**
     * Установить сообщение диалога ожидания
     */
    public void setProgressMessage(String message) {
        this.mProgressMessage = message;
    }

    /**
     * Сообщить об ошибке во построения отчета, дальнейшее построение отчета
     * невозможно
     *
     * @param msg описание ошибки
     */
    protected void onError(String msg) {
        mBuilderResult.onError(msg);
    }

    private class ProgressAsyncTask extends BaseProgressAsyncTask<Void, Object> {

        public ProgressAsyncTask(Context context, String progressMessage, boolean cancelable) {
            super(context, progressMessage, cancelable);
        }

        @Override
        protected Object doInBackground(Void... params) {
            return BaseTaskReportBuilder.this.doInBackground(mContext);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            mBuilderResult.onComplete(result);
        }

    }
}
