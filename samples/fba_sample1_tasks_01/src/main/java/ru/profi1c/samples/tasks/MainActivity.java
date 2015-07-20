/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tasks;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.util.AppHelper;
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
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private void init() throws SQLException {

		list = (ListView) findViewById(R.id.listView1);

		// создать менеджер для работы с таблицей задач
		ExTableTasksDao dao = getHelper().getDao(ExTableTasks.class);

		// выбрать все задачи из локальной базы данных
		List<ExTableTasks> data = dao.select();

		// имена колонок, которые буду отображены в списке
		String[] from = new String[] { ExTableTasks.FIELD_NAME_DEADLINE,
				ExTableTasks.FIELD_NAME_TASK, ExTableTasks.FIELD_NAME_COMPLETE };

		// массив идентификаторов View которые используются для отображения
		// значений
		int[] to = new int[] { R.id.deadline, R.id.task, R.id.complete };

		// создать адаптер для отображения данных таблицы
		MetaArrayAdapter<ExTableTasks> adapter = new MetaArrayAdapter<ExTableTasks>(
				this, ExTableTasks.class, data, R.layout.list_item, from, to);

		list.setAdapter(adapter);
	}

	@SuppressWarnings("unchecked")
	private void saveChangesToDB() {

		try {
			// создать менеджер для работы с таблицей задач
			ExTableTasksDao dao = getHelper().getDao(ExTableTasks.class);

			MetaArrayAdapter<ExTableTasks> adapter = (MetaArrayAdapter<ExTableTasks>) list
					.getAdapter();
			final int count = adapter.getCount();

			// Перебор все элементов в списке и сохранение изменённых в
			// локальной базе данных
			for (int i = 0; i < count; i++) {
				ExTableTasks task = adapter.getItem(i);
				if (task.isModified())
					dao.update(task);
			}

		} catch (SQLException e) {
			e.printStackTrace();
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

}