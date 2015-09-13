/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.app.ui.DescriptionAdapter;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.widget.PresentationAdapter;
import ru.profi1c.samples.fba_perfomance.db.DBHelper;
import ru.profi1c.samples.fba_perfomance.db.MetaHelper;

/*
 * Эта Activity является основной и будет первой отображаться при запуске
 * приложения.
 *
 * Предупреждение! FBA использует библиотеку 'AppCompat',
 * взаимодействие с панелью действий обрабатывается с помощью функции getSupportActionBar() вместо getActionBar().
 * 
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 * 
 */
public class MainActivity extends FbaDBExchangeActivity
        implements Handler.Callback, TestHandler.TestHandlerCallback {

    private Handler mHandler;
    private TestHandler mCurrTestHandler;
    private Spinner mSpinnerTestVariant;
    private TextView mtvTestResult;
    private Button mBtnRun;
    private CheckBox mCheckBoxOptimize;

    private final Profiler mExchangeProfiler = new Profiler();
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getFbaActivityNavigation().isMainActivity()) {
            onCreateNewSession();
        }
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdownNow();
    }

    private void init() {
        mHandler = new Handler(this);
        mSpinnerTestVariant = (Spinner) findViewById(R.id.spinnerVariant);
        mCheckBoxOptimize = (CheckBox) findViewById(R.id.checkBoxOptimize);
        mBtnRun = (Button) findViewById(R.id.btnRun);
        mBtnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRunTest();
            }
        });

        Button btn = (Button) findViewById(R.id.btnBackupDb);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackupDb();
            }
        });

        DescriptionAdapter adapter = new DescriptionAdapter(this, MeasureType.values());
        mSpinnerTestVariant.setAdapter(adapter);

        mtvTestResult = (TextView) findViewById(R.id.tvTestResult);
        mtvTestResult.setMovementMethod(new ScrollingMovementMethod());
    }

    private void onBackupDb() {
        if (DBHelper.backupToExternalStorage(getApplication())) {
            showToast("Резеревирование базы выполнено успешно");
        } else {
            showToast("Ошибка резервирования базы данных");
        }
    }

    private void doRunTest() {
        MeasureType type = (MeasureType) mSpinnerTestVariant.getSelectedItem();
        try {
            mCurrTestHandler = new TestHandler(mHandler, type, (DBHelper) getHelper(),
                    (MetaHelper) getFbaSettingsProvider().getMetadataHelper(), mCheckBoxOptimize.isChecked());
            mCurrTestHandler.setTestHandlerCallback(this);
            appendTextToResult("-------------------");

            mBtnRun.setEnabled(false);
            mExecutorService.submit(mCurrTestHandler);

        } catch (SQLException e) {
            e.printStackTrace();
            showToast(e.getMessage());
            mBtnRun.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.fba_menu_settings) {

            getFbaActivityNavigation().showPreferenceActivity();
            return true;

        } else if (id == R.id.fba_menu_exchange) {

            doSelectStartExchange();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected ExchangeObserver getExchangeObserver() {
        return new MyExchangeObserver(this, new Handler());
    }

    /**
     * Запущена новая сессия приложения т.н запуск, не поворот экрана
     */
    private void onCreateNewSession() {

        Context ctx = getApplicationContext();

        //восстановить задание обмена в планировщике если требуется
        if (AppHelper.isAppInstalledToSDCard(ctx)) {
            ExchangeReceiver.createSchedulerTasks(ctx);
        }

    }

    /*
     * Интерактивный выбор варианта и запуск обмена
     */
    private void doSelectStartExchange() {

        // адаптер для отображения значений перечислений в диалоге выбора
        PresentationAdapter adapter = new PresentationAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, ExchangeVariant.values());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExchangeVariant variant = ExchangeVariant.values()[which];
                startExchange(variant, true);
                dialog.dismiss();

            }
        });
        builder.setTitle("Выбор варианта обмена");
        builder.create().show();

    }

    private void appendTextToResult(final String text) {
        String before = mtvTestResult.getText().toString();
        mtvTestResult.setText(before + "\n" + text);

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == TestHandler.ID_MESSAGE) {
            appendTextToResult(msg.obj.toString());
        }
        return false;
    }

    @Override
    public void onFinish(final Profiler profiler) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnRun.setEnabled(true);

                long allTime = profiler.getResultTime();
                String msg = String.format("Тестирование окончено, общее время = %s (%d баллов)",
                        TestHandler.formatDurationInHHMMSS(allTime), allTime);
                appendTextToResult(msg);
                showMessage("Fba perfomance", msg);
            }
        });

    }

    /*
    * Наблюдатель за процедурой обмена, расширим базовый из
    * FbaDBExchangeActivity
    */
    private class MyExchangeObserver extends WSSimpleExchangeObserver {

        private String performanceTag;

        public MyExchangeObserver(FbaActivity activity, Handler handler) {
            super(activity, handler);
        }

        @Override
        public void onStart(ExchangeVariant variant) {
            super.onStart(variant);
            performanceTag = "Обмен с сервером по варианту: " + variant.getPresentation();
            mExchangeProfiler.start(performanceTag);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appendTextToResult("Begin: " + performanceTag);
                }
            });
        }

        @Override
        public void onFinish(boolean success) {
            super.onFinish(success);
            mExchangeProfiler.stop(performanceTag);
            MainActivity.this.onFinish(mExchangeProfiler);
        }
    }
}