/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import ru.profi1c.engine.app.FbaActivity;

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
public class MainActivity extends FbaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        TextView tvStatus = (TextView) findViewById(R.id.status);
        tvStatus.setText(
                "Текущий статус: " + (LocationService.isRunning() ? "Включен" : "Выключен"));

        Button bnt = (Button) findViewById(R.id.btnStart);
        bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LocationService.start(MainActivity.this, Const.LOCATION_MIN_TIME,
                        Const.LOCATION_MIN_DISTANCE);
            }
        });

        bnt = (Button) findViewById(R.id.btnStop);
        bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LocationService.stop(MainActivity.this);
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

        }
        return super.onOptionsItemSelected(item);
    }

}