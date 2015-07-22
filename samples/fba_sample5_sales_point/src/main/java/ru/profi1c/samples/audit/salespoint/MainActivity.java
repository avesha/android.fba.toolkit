/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.audit.salespoint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.widget.PresentationAdapter;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getFbaActivityNavigation().isMainActivity()) {
            onCreateNewSession();
        }

		setContentView(R.layout.activity_main);
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

		} else if (id == R.id.menu_map) {

			startActivity(new Intent(this, MapsforgeRouteMap.class));

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

		//восстановить задание обмена в планировщике если требуется
		if(AppHelper.isAppInstalledToSDCard(ctx)) {
			ExchangeReceiver.createSchedulerTasks(ctx);
		}

	}

	/*
	 * Интерактивный выбор варианта и запуск обмена
	 */
	private void doSelectStartExchange() {

		// адаптер для отображения значений перечислений в диалоге выбора
		PresentationAdapter adapter = new PresentationAdapter(this,
				android.R.layout.simple_spinner_dropdown_item,
				ExchangeVariant.values());

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

}