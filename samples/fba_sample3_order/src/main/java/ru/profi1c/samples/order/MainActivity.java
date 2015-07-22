/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.widget.PresentationAdapter;
import ru.profi1c.samples.order.db.CatalogDogovoriKontragentov;
import ru.profi1c.samples.order.db.CatalogDogovoriKontragentovDao;
import ru.profi1c.samples.order.db.CatalogKontragenti;
import ru.profi1c.samples.order.db.CatalogKontragentiDao;
import ru.profi1c.samples.order.db.CatalogNomenklatura;
import ru.profi1c.samples.order.db.CatalogNomenklaturaDao;
import ru.profi1c.samples.order.db.CatalogPolzovateli;
import ru.profi1c.samples.order.db.CatalogPolzovateliDao;
import ru.profi1c.samples.order.db.CatalogSkladi;
import ru.profi1c.samples.order.db.CatalogSkladiDao;
import ru.profi1c.samples.order.db.CatalogTipiCenNomenklaturi;
import ru.profi1c.samples.order.db.CatalogValyuti;
import ru.profi1c.samples.order.db.CatalogValyutiDao;
import ru.profi1c.samples.order.db.Constants;
import ru.profi1c.samples.order.db.ConstantsDao;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelya;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelyaDao;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelyaTPTovari;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelyaTPTovariDao;
import ru.profi1c.samples.order.db.ExTableCeni;
import ru.profi1c.samples.order.db.ExTableCeniDao;

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
    private static final String TAG = MainActivity.class.getSimpleName();

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
        Button bnt = (Button) findViewById(R.id.btnNewOrder);
        bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (DBOpenHelper.isExistsDataBase(getApplicationContext()))
                        createNewOrder();
                    else
                        showToast("База данных не существует. Сначала выполните обмен с 1С!");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        bnt = (Button) findViewById(R.id.btnListOrders);
        bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DocZakazListActivity.class));
            }
        });

        bnt = (Button) findViewById(R.id.btnListNomentklatura);
        bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CatalogNomenklaturaListActivity.class));
            }
        });

    }

    /*
     * Создать новый документ «Заказ покупателя».
     */
    protected void createNewOrder() throws SQLException {

        // Помощник для работы с базой данных
        DBOpenHelper helper = getHelper();

        // Менеджер для работы константами
        ConstantsDao costDao = helper.getDao(Constants.class);

        // Прочитать набор констант
        Constants constants = costDao.read();

        if (constants == null) {
            showToast("Данные отсутствуют  или некорректны, выполните обмен с 1С!");
            return;
        }

        Log.i(TAG, "Основная организация:" + constants.osnovnayaOrganizaciya.getDescription());
        Log.i(TAG,
              "Основной тип цен продажи: " + constants.osnovnoiTipCenProdazhi.getDescription());

        // Найти контрагента по наименованию
        CatalogKontragentiDao kontragentDao = helper.getDao(CatalogKontragenti.class);
        CatalogKontragenti kontragent = kontragentDao.findByDescription("Белявский-частное лицо");
        Log.i(TAG, "Покупатель: " + kontragent.getDescription());

        CatalogDogovoriKontragentovDao dogovorDao =
                helper.getDao(CatalogDogovoriKontragentov.class);

        // Параметры отбора по организации, типу цен и владельцу
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(CatalogDogovoriKontragentov.FIELD_NAME_ORGANIZACIYA,
                   constants.osnovnayaOrganizaciya);
        filter.put(CatalogDogovoriKontragentov.FIELD_NAME_TIP_CEN,
                   constants.osnovnoiTipCenProdazhi);
        filter.put(CatalogDogovoriKontragentov.FIELD_NAME_OWNER, kontragent);

        // Выбрать первый договор контрагента, если есть или пустая ссылка
        CatalogDogovoriKontragentov dogovor = dogovorDao.emptyRef();
        List<CatalogDogovoriKontragentov> lstDogovors = dogovorDao.select(filter);
        if (lstDogovors.size() > 0)
            dogovor = lstDogovors.get(0);
        Log.i(TAG, "Договор: " + dogovor.getDescription());

        // найти главный склад по коду
        CatalogSkladiDao skladiDao = helper.getDao(CatalogSkladi.class);
        CatalogSkladi sklad = skladiDao.findByCode("000000001");
        Log.i(TAG, "Cклад: " + sklad.getDescription());

        // Ответственного
        CatalogPolzovateliDao polzovateliDao = helper.getDao(CatalogPolzovateli.class);
        CatalogPolzovateli polzovatel = polzovateliDao.findByCode("Иванов И.И.");
        Log.i(TAG, "Пользователь: " + polzovatel.getPresentation());

        // Найти валюту по коду
        CatalogValyutiDao valutDao = helper.getDao(CatalogValyuti.class);
        CatalogValyuti valyta = valutDao.findByCode("643");
        Log.i(TAG, "Валюта: " + valyta.getPresentation());

        // длина номера документа
        int DOC_NUMBER_LEN = 11;

        // Менеджер для работы с документами, другой вариант инициализации
        DocumentZakazPokupatelyaDao docDao = new DocumentZakazPokupatelyaDao(getConnectionSource());

        int numerator = docDao.getNextNumber("AND-");
        String strNumber = docDao.formatNumber("AND-", numerator, DOC_NUMBER_LEN);
        Log.i(TAG, "Номер нового документа: " + strNumber);

        // "Документ объект"
        DocumentZakazPokupatelya doc = docDao.newItem();

        doc.setDate(new Date(System.currentTimeMillis()));
        doc.setNumber(strNumber);
        doc.setPosted(false);

        // реквизиты шапки
        doc.organizaciya = constants.osnovnayaOrganizaciya;
        doc.kontragent = kontragent;
        doc.dogovorKontragenta = dogovor;
        doc.sklad = sklad;
        doc.kommentarii = "Создан на Android";
        doc.otvetstvennii = polzovatel;
        doc.tipCen = constants.osnovnoiTipCenProdazhi;
        doc.valyutaDokumenta = valyta;
        // отгрузка через 5 дней
        doc.dataOtgruzki = DateHelper.addDays(doc.getDate(), 5);

        // Подготовить строки табличной части
        List<DocumentZakazPokupatelyaTPTovari> rows =
                makeTablePartRows(doc, constants.osnovnoiTipCenProdazhi,
                                  new String[]{"Мясорубка ЭКМ-3",
                                          "Чайник BINATONE  AEJ-1001,  2,2л", "Телевизор \"JVC\""});

        // Посчитать сумму документа
        double summa = 0;
        for (DocumentZakazPokupatelyaTPTovari row : rows) {
            summa += row.summa;
        }
        doc.summa = summa;

        // запись данных в локальную базу
        // Сначала должен быть записан сам документ
        docDao.create(doc);

        // запись табличной части (вариант через ForeignCollection)
        doc.tovari = docDao.getEmptyForeignCollection("tovari");
        for (DocumentZakazPokupatelyaTPTovari row : rows) {
            // aвтоматическая запись сразу при добавлении
            doc.tovari.add(row);
        }

        showToast("Документ добавлен!");

    }

    /*
     * Ищем номенклатуру по наименованию, получаем цену и добавляем с список
     * строк табличной части
     */
    private List<DocumentZakazPokupatelyaTPTovari> makeTablePartRows(DocumentZakazPokupatelya doc,
            CatalogTipiCenNomenklaturi tipCen, String[] nomenklaturaNames) throws SQLException {

        // Подготовить пустой список строк табличной части
        List<DocumentZakazPokupatelyaTPTovari> rows =
                new ArrayList<DocumentZakazPokupatelyaTPTovari>();

        // Менеджеры данных: табличной части документа, справочника
        // «Номенклатура» и внешней таблицы цен
        DocumentZakazPokupatelyaTPTovariDao TPTovariDao =
                getHelper().getDao(DocumentZakazPokupatelyaTPTovari.class);

        CatalogNomenklaturaDao nomenklaturaDao = getHelper().getDao(CatalogNomenklatura.class);
        ExTableCeniDao ceniDao = getHelper().getDao(ExTableCeni.class);

        int lineNumber = 1;

        for (String name : nomenklaturaNames) {

            CatalogNomenklatura nomenklatura = nomenklaturaDao.findByDescription(name);
            if (!CatalogNomenklatura.isEmpty(nomenklatura)) {

                ExTableCeni priceRow = ceniDao.findPriceRow(tipCen, nomenklatura);

                if (priceRow != null) {

                    // Создать новую строку табличной части
                    DocumentZakazPokupatelyaTPTovari row = TPTovariDao.newItem(doc, lineNumber++);
                    row.nomenklatura = priceRow.nomenklatura;
                    row.harakteristika = priceRow.harakteristikaNomenklaturi;
                    row.edinicaIzmereniya = priceRow.edinicaIzmereniya;
                    row.cena = priceRow.cena;
                    row.kolichestvo = 1;
                    row.summa = row.cena * row.kolichestvo;

                    Log.i(TAG, "строка ТЧ: номенклатура = " + row.nomenklatura + " decr = " +
                               ", цена = " + row.cena);

                    rows.add(row);
                }

            }
        }
        return rows;

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
     * Интерактивный выбор варианта и запуск обмена
     */
    private void doSelectStartExchange() {

        // адаптер для отображения значений перечислений в диалоге выбора
        PresentationAdapter adapter =
                new PresentationAdapter(this, android.R.layout.simple_spinner_dropdown_item,
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