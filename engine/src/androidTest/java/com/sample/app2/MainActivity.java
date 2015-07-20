/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sample.app2.db.RegAdresniiKlassifikator;
import com.sample.app2.test.exchange.TestDataProviderFactory;
import com.sample.app2.test_action.ITestActionProvider;
import com.sample.app2.test_action.TestActionProviderManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.app.ui.DescriptionAdapter;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeStrategy;
import ru.profi1c.engine.exchange.ExchangeTask;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.exchange.IExchangeCallbackListener;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.widget.PresentationAdapter;

/*
 * Эта Activity является основной и будет первой отображаться при запуске
 * приложения.
 *
 * Предупреждение! FBA использует библиотеку 'AppCompat',
 * взаимодействие с панелью действий обрабатывается с помощью функции getSupportActionBar() вместо getActionBar().
 *
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
public class MainActivity extends FbaDBExchangeActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = true;

    private ListView mList;
    private boolean mUseTestExchange = true;
    private boolean mSkipLargeTable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getFbaActivityNavigation().isMainActivity()) {
            onCreateNewSession();
        }
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        final Context context = this;
        mList = (ListView) findViewById(android.R.id.list);
        DescriptionAdapter adapter = new DescriptionAdapter(context,
                android.R.layout.simple_list_item_1,
                TestActionProviderManager.getActionProviders());
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ITestActionProvider provider = (ITestActionProvider) parent.getAdapter()
                        .getItem(position);
                TestActionProviderManager.showActions(context, provider);
            }
        });
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

            doSelectVariant();
            return true;

        } else if (id == R.id.action_menu_test_exchange) {
            item.setChecked(!item.isChecked());
            mUseTestExchange = item.isChecked();
            return true;

        } else if (id == R.id.action_menu_skip_large_table) {
            item.setChecked(!item.isChecked());
            mSkipLargeTable = item.isChecked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected ExchangeObserver getExchangeObserver() {
        return null;
    }

    /**
     * Запущена новая сессия приложения т.н запуск, не поворот экрана
     */
    private void onCreateNewSession() {
        Context context = getApplicationContext();
        //восстановить задание обмена в планировщике если требуется
        if (AppHelper.isAppInstalledToSDCard(context)) {
            ExchangeReceiver.createSchedulerTasks(context);
        }
    }

    private void doSelectVariant() {
        PresentationAdapter adapter = new PresentationAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, ExchangeVariant.values());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExchangeVariant variant = ExchangeVariant.values()[which];
                Dbg.d(TAG, "start exchange variant: " + variant.getPresentation());

                App app = (App) getApplication();
                if (mUseTestExchange) {
                    app.getExchangeSettings()
                            .setExchangeDataProviderFactory(new TestDataProviderFactory());
                } else {
                    //use default
                    app.getExchangeSettings().setExchangeDataProviderFactory(null);
                }
                ExchangeTask task = new ExchangeTask(variant,
                        new ExchangeStrategy(app.getExchangeSettings()), getHelper());
                task.setListener(mCustomExchangeRules);

                startExchange(task, true);
                dialog.dismiss();

            }
        });
        builder.setTitle("Выбор варианта обмена");
        builder.create().show();
    }

    private IExchangeCallbackListener mCustomExchangeRules = new IExchangeCallbackListener() {
        @Override
        public boolean onSerializeTable(Class<?> classOf, List<Object> values) {
            if (mSkipLargeTable && classOf.equals(RegAdresniiKlassifikator.class)) {
                return false;
            }
            return true;
        }

        @Override
        public void onDeserializeJson(HashMap<Class<?>, File> mapJsonData) {
            if (mSkipLargeTable) {
                mapJsonData.remove(RegAdresniiKlassifikator.class);
            }
        }

        @Override
        public void onDeniedDataSavedOnServer(Class<?> clazz, List<Object> values) {

        }

        @Override
        public boolean onUpdateTable(Class<?> classOf, List<Object> newValues) {
            if (mSkipLargeTable && classOf.equals(RegAdresniiKlassifikator.class)) {
                return false;
            }
            return true;
        }

        @Override
        public void onError(String event, String msg) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onComplete(boolean result) {

        }
    };
}