/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.report.head;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeStrategy;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.report.IReport;
import ru.profi1c.engine.report.ReportListAdapter;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.samples.report.head.exchange.MyExchangeTask;

/*
 * Эта Activity является основной и будет первой отображаться при запуске
 * приложения.
 *
 * Предупреждение! FBA использует библиотеку 'AppCompat',
 * взаимодействие с панелью действий обрабатывается с помощью функции getSupportActionBar() вместо getActionBar().
 * 
 * @author ООО "Сфера" (support@sfera.ru)
 * 
 */
public class MainActivity extends FbaDBExchangeActivity {

    private ListView mListView;

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
        mListView = (ListView) findViewById(android.R.id.list);

        // Создать адаптер для отображения списка отчетов
        ReportListAdapter adapter = new ReportListAdapter(this, createReportList());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Показываем отчет в диалоге по клику на нем
                IReport report = (IReport) mListView.getItemAtPosition(position);
                report.onShow(MainActivity.this);
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

            startExchangeMyRules(ExchangeVariant.FULL, true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Если диалог открыт, здесь может быть утечка памяти. Сообщаем отчетам
        // об уничтожении, они должны освободить используемые ресурсы.
        ReportListAdapter adapter = (ReportListAdapter) mListView.getAdapter();
        if (adapter != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                adapter.getItem(i).onDestroy();
            }
        }

    }

    @Override
    protected ExchangeObserver getExchangeObserver() {
        return null;
    }

    /**
     * Запущена новая сессия приложения т.н запуск, не поворот экрана
     */
    private void onCreateNewSession() {

        Context ctx = getApplicationContext();

        // восстановить задание обмена в планировщике если требуется
        if (AppHelper.isAppInstalledToSDCard(ctx)) {
            ExchangeReceiver.createSchedulerTasks(ctx);
        }

    }

    /*
     * Запуск обмена по моим правилам. Обратите внимание, что в планировщике
     * (если установлен) обмен остался по стандартным правилам
     */
    private void startExchangeMyRules(ExchangeVariant variant, boolean cancelable) {

        // хелпер для вызова методов web-сервиса
        ExchangeStrategy strategy = new ExchangeStrategy(
                getFbaSettingsProvider().getExchangeSettings());
        MyExchangeTask task = new MyExchangeTask(variant, strategy, getHelper());

        startExchange(task, cancelable);
    }

    /*
     * Подготовить список отчетов
     */
    private List<IReport> createReportList() {

        List<IReport> lst = new ArrayList<IReport>();

        // Создать первый отчет, временный каталог где сохранен файл берем из
        // настроек
        IReport report = new ProductsInStokReport(
                new File(getFbaSettingsProvider().getAppSettings().getCacheDir(),
                        ProductsInStokReport.REPORT_FILE_NAME));
        lst.add(report);

        // Создать и добавить в список прочие отчеты
        lst.add(new ToChiefReport());
        lst.add(GoogleChartReport.buildTestReport());
        lst.add(new FlotReport());
        lst.add(new PdfReport());
        return lst;
    }

}