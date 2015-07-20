/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tasks;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.engine.widget.MetaArrayAdapter;
import ru.profi1c.samples.tasks.db.ExTableTasks;
import ru.profi1c.samples.tasks.db.ExTableTasksDao;

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

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getFbaActivityNavigation().isMainActivity()) {
            onCreateNewSession();
        }

        setContentView(R.layout.activity_main);
        try {
            init();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void init() throws SQLException {

        list = (ListView) findViewById(R.id.listView1);
        list.setAdapter(createAdapter());
    }

    private ListAdapter createAdapter() throws SQLException {

        // создать менеджер для работы с таблицей задач
        ExTableTasksDao dao = getHelper().getDao(ExTableTasks.class);

        // фильтр: выбрать только незавершенные задачи
        HashMap<String, Object> mapFilter = new HashMap<String, Object>();
        mapFilter.put(ExTableTasks.FIELD_NAME_COMPLETE, false);

        // Выбрать с учетом фильтрации и сортировки по сроку исполнения
        List<ExTableTasks> data = dao.select(mapFilter, ExTableTasks.FIELD_NAME_DEADLINE);

        // имена колонок, которые буду отображены в списке
        String[] from = new String[]{ExTableTasks.FIELD_NAME_DEADLINE, ExTableTasks.FIELD_NAME_TASK,
                ExTableTasks.FIELD_NAME_COMPLETE};

        // массив идентификаторов View которые используются для отображения
        // значений
        int[] to = new int[]{R.id.deadline, R.id.task, R.id.complete};

        // Форматер значений: установим свой только для дат, которые со временем
        // – будут показаны с отсечением времени и года
        FieldFormatter ff = new FieldFormatter.Builder().setDateTimeFormat("dd MMMM").create();

        // Создадим построитель View и назначим ему наш форматер
        MetaAdapterViewBinder metaBinder = new MetaAdapterViewBinder(this, ExTableTasks.class, from,
                to);
        metaBinder.setFieldFormatter(ff);

        // создать адаптер для отображения данных таблицы
        MetaArrayAdapter<ExTableTasks> adapter = new MetaArrayAdapter<ExTableTasks>(data,
                R.layout.list_item, metaBinder);

        return adapter;
    }

    @SuppressWarnings("unchecked")
    private void saveChangesToDB() {

        try {
            // создать менеджер для работы с таблицей задач
            ExTableTasksDao dao = getHelper().getDao(ExTableTasks.class);

            MetaArrayAdapter<ExTableTasks> adapter = (MetaArrayAdapter<ExTableTasks>) list.getAdapter();
            final int count = adapter.getCount();

            // Перебор все элементов в списке и сохранение изменённых в
            // локальной базе данных
            for (int i = 0; i < count; i++) {
                ExTableTasks task = adapter.getItem(i);
                if (task.isModified()) {
                    dao.update(task);
                }
            }

        } catch (SQLException e) {
            Dbg.printStackTrace(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveChangesToDB();
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

            saveChangesToDB();
            startExchange(ExchangeVariant.FULL, true);
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

        // восстановить задание обмена в планировщике если требуется
        if (AppHelper.isAppInstalledToSDCard(ctx)) {
            ExchangeReceiver.createSchedulerTasks(ctx);
        }

    }

    /*
     * Наблюдатель за процедурой обмена, расширим базовый из
     * FbaDBExchangeActivity
     */
    private class MyExchangeObserver extends WSSimpleExchangeObserver {

        public MyExchangeObserver(FbaActivity activity, Handler handler) {
            super(activity, handler);
        }

        @Override
        public void onFinish(boolean success) {
            super.onFinish(success);
            if (success) {

                // Здесь вызов в отдельном потоке обязателен, если обновляются
                // визуальные элементы
                getHandler().post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            onFinishExchangeSuccess();
                        } catch (SQLException e) {
                            Dbg.printStackTrace(e);
                        }
                    }
                });
            }
        }
    }

    public void onFinishExchangeSuccess() throws SQLException {

        // создать менеджер для работы с таблицей задач
        ExTableTasksDao dao = getHelper().getDao(ExTableTasks.class);

        // удалить из локальной базы выполненные задачи
        DeleteBuilder<ExTableTasks, String> delBuilder = dao.deleteBuilder();
        delBuilder.where().eq(ExTableTasks.FIELD_NAME_COMPLETE, true);
        dao.delete(delBuilder.prepare());

        // перечитать данные
        list.setAdapter(createAdapter());
    }

}