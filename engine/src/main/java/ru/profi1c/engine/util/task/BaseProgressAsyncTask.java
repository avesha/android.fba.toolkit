package ru.profi1c.engine.util.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ru.profi1c.engine.R;

/**
 * Базовый класс AsyncTask с отображением прогресса ({@link ProgressDialog}) во
 * время выполнения. Связь с задачей не восстанавливается автоматически при
 * повороте экрана, повторно связаться в задачей можно вызвав метод
 * {@link #link(Context, boolean)}
 */
public abstract class BaseProgressAsyncTask<Params, Result>
        extends AsyncTask<Params, String, Result> implements OnCancelListener {

    private WeakReference<ProgressDialog> mRefDlg;
    private String mProgressMessage;
    private boolean mCancelable;
    private boolean mCanceled;

    /**
     * Создать задачу, пользователь может выполнение интерактивно
     *
     * @param context Текущий контекст
     */
    public BaseProgressAsyncTask(Context context) {
        init(context, context.getString(R.string.fba_progress_dlg_wait), true);
    }

    /**
     * Создать задачу, пользователь может выполнение интерактивно
     *
     * @param context         Текущий контекст
     * @param progressMessage Начальное сообщение диалога ожидания
     */
    public BaseProgressAsyncTask(Context context, String progressMessage) {
        init(context, progressMessage, true);
    }

    /**
     * Создать задачу
     *
     * @param context         Текущий контекст
     * @param progressMessage Начальное сообщение диалога ожидания
     * @param cancelable      Возможность отмены пользователем задачи интерактивно
     */
    public BaseProgressAsyncTask(Context context, String progressMessage, boolean cancelable) {
        init(context, progressMessage, cancelable);
    }

    /**
     * Подключится к задаче, например после поворота экрана
     *
     * @param context Текущий контекст
     * @param force   Если истина, диалог прогресса выполнения задачи будет
     *                отображен сразу (с последним сообщением) иначе, как только
     *                будет получено onProgressUpdate. Задача при этом не должна
     *                быть отмена или завершена
     */
    public void link(Context context, boolean force) {
        ProgressDialog dlg = createDlg(context);
        mRefDlg = new WeakReference<ProgressDialog>(dlg);
        if (force && getStatus() != Status.FINISHED && !mCanceled) {
            updateProgressDlg(mProgressMessage);
        }
    }

    private void init(Context context, String progressMessage, boolean canselable) {
        mProgressMessage = progressMessage;
        mCancelable = canselable;
        link(context, false);
    }

    private ProgressDialog createDlg(Context context) {
        ProgressDialog dlg = new ProgressDialog(context);
        dlg.setIndeterminate(true);
        dlg.setCancelable(mCancelable);
        dlg.setOnCancelListener(this);
        dlg.setMessage(mProgressMessage);
        return dlg;
    }

    private void dismissDlg() {
        if (mRefDlg != null) {
            ProgressDialog dlg = mRefDlg.get();
            if (dlg != null && dlg.isShowing())
                dlg.dismiss();
        }
    }

    private void updateProgressDlg(String message) {
        if (mRefDlg != null) {
            ProgressDialog dlg = mRefDlg.get();
            if (dlg != null) {
                if (!dlg.isShowing()) {
                    dlg.show();
                }
                dlg.setMessage(message);
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mProgressMessage = values[0];
        updateProgressDlg(mProgressMessage);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // отмена "через диалог" например кнопкой back
        mCanceled = true;
        dismissDlg();
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        // отмена задачи
        mCanceled = true;
        dismissDlg();
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        dismissDlg();
    }
}
