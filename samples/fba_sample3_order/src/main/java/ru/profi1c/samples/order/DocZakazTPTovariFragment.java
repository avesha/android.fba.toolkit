package ru.profi1c.samples.order;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import ru.profi1c.engine.app.FbaDBFragment;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.widget.BaseViewHolder;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.engine.widget.MetaArrayAdapter;
import ru.profi1c.samples.order.db.CatalogHarakteristikiNomenklaturi;
import ru.profi1c.samples.order.db.CatalogNomenklatura;
import ru.profi1c.samples.order.db.CatalogTipiCenNomenklaturi;
import ru.profi1c.samples.order.db.Constants;
import ru.profi1c.samples.order.db.ConstantsDao;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelya;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelyaTPTovari;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelyaTPTovariDao;
import ru.profi1c.samples.order.db.ExTableCeni;
import ru.profi1c.samples.order.db.ExTableCeniDao;

/*
 * Страница "Товары": соответ. табличная часть документа «Заказа покупателя». Отображаются не строки табличной части,
 * а полный список номенклатуры по внешней таблице «Цены» с возможностью указать количество заказанного.
 */
public class DocZakazTPTovariFragment extends FbaDBFragment {

    private static final String EXTRA_OWNER_ID = "extra_owner_id";
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("###,##0.00");

    private Ref mOwner;

    private ListView mListView;
    private MetaArrayAdapter<ExTableCeni> maAdapter;

    private ExTableCeniDao mExTableCeniDao;
    private CatalogTipiCenNomenklaturi mDefTipCen;

    private DocumentZakazPokupatelyaTPTovariDao mTPTovariDao;

    private TextView mTvSumma;

    public static DocZakazTPTovariFragment newInstance(Ref owner) {
        DocZakazTPTovariFragment fragment = new DocZakazTPTovariFragment();
        if (owner != null) {
            fragment.setArguments(toBundle(owner));
        }
        return fragment;
    }

    /*
     * Передача параметров во фрагмент: ссылка на документ
     */
    private static Bundle toBundle(Ref ref) {
        Bundle args = new Bundle();
        args.putString(EXTRA_OWNER_ID, ref.getRef().toString());
        return args;
    }

    /*
     * Восстановление параметров фрагмента: ссылка на документ (только ссылка,
     * данные из базы не считываются)
     */
    private Ref fromBundle(Bundle args) {

        Ref ref = null;

        if (args != null && args.containsKey(EXTRA_OWNER_ID)) {
            String uuid = args.getString(EXTRA_OWNER_ID);
            ref = new DocumentZakazPokupatelya();
            ref.setRef(UUID.fromString(uuid));
        }
        return ref;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            initData();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.doc_zakaz_tp_tovari, container, false);
        initControl(root);
        return root;
    }

    private void initData() throws SQLException {

        //ссылка на владелца
        mOwner = fromBundle(getArguments());

        DBOpenHelper helper = getHelper();
        mTPTovariDao = helper.getDao(DocumentZakazPokupatelyaTPTovari.class);
        mExTableCeniDao = new ExTableCeniDao(getConnectionSource());

        ConstantsDao costDao = helper.getDao(Constants.class);
        Constants constants = costDao.read();

        //Тип цен возможен один, возьмём из констант
        mDefTipCen = constants.osnovnoiTipCenProdazhi;

        //Внимание! Может быть большой объем данных
        List<ExTableCeni> data = mExTableCeniDao.getPriceOfType(mDefTipCen);

        //табличная часть сохраненного документа: обновить количество в списке
        if (mOwner != null) {
            List<DocumentZakazPokupatelyaTPTovari> lstTablePart = mTPTovariDao.getTablePart(mOwner);
            updateListOnTablePart(data, lstTablePart);
        }

        //Кастомный построитель /форматтер для элементов строки
        MetaAdapterViewBinder adapterBinder =
                new MetaAdapterViewBinder(getActivity(), ExTableCeni.class,
                                          new String[]{ExTableCeni.FIELD_NAME_NOMENKLATURA,
                                                  ExTableCeni.FIELD_NAME_CENA, "kolvo"},
                                          new int[]{R.id.tvDescription, R.id.tvPrice,
                                                  R.id.fetKolvo});
        adapterBinder.setViewBinder(mPriceViewBinder);

        //Нули в поле ввода количества не отображать
        adapterBinder.setFieldFormatter(new FieldFormatter.Builder().setZeroFormat("").create());

        maAdapter = new MetaArrayAdapter<ExTableCeni>(data, R.layout.doc_zakaz_tovar_row,
                                                      adapterBinder);

    }

    /*
     * Обновить список по табличной части сохраненного документа
     */
    private void updateListOnTablePart(List<ExTableCeni> lst,
            List<DocumentZakazPokupatelyaTPTovari> lstTablePart) {

        for (DocumentZakazPokupatelyaTPTovari tpRow : lstTablePart) {
            ExTableCeni row = findByGoog(lst, tpRow.nomenklatura, tpRow.harakteristika);
            if (row != null) {
                row.kolvo = (int) tpRow.kolichestvo;
                row.setModified(true);
            }
        }

    }

    /*
     * Найти в списке элемент по номенклатуре и характеристике
     */
    private ExTableCeni findByGoog(List<ExTableCeni> lst, CatalogNomenklatura nomenklatura,
            CatalogHarakteristikiNomenklaturi harakteristika) {

        ExTableCeni row = null;
        for (ExTableCeni rw : lst) {
            if (rw.nomenklatura.equals(nomenklatura) &&
                rw.harakteristikaNomenklaturi.equals(harakteristika)) {
                row = rw;
                break;
            }
        }
        return row;
    }

    private void initControl(View root) {
        mListView = (ListView) root.findViewById(R.id.lvTovari);
        mListView.setAdapter(maAdapter);

        mTvSumma = (TextView) root.findViewById(R.id.tvSumma);
        calcSum();
    }

    /*
     * кастомное форматирование поля,связь элементов отображения с данными
     * (обработчики событий изменения)
     */
    private MetaAdapterViewBinder.ViewBinder mPriceViewBinder =
            new MetaAdapterViewBinder.ViewBinder() {

                @Override
                public BaseViewHolder createViewHolder(View root) {
                    PriceViewHolder holder = new PriceViewHolder(root);
                    return holder;
                }

                @Override
                public void onBind(BaseViewHolder viewHolder, int position) {
                }

                @Override
                public boolean setViewValue(View v, Object item, Field field) {

                    ExTableCeni row = (ExTableCeni) item;
                    final String name = field.getName();

                    if (ExTableCeni.FIELD_NAME_NOMENKLATURA.equalsIgnoreCase(name)) {

                        //С номенклатурой  выведем характеристику (если есть)
                        String harakteristika = row.harakteristikaNomenklaturi.getDescription();
                        if (!TextUtils.isEmpty(harakteristika)) {
                            ((TextView) v).setText(
                                    row.nomenklatura.getDescription() + " (" + harakteristika +
                                    ")");
                            return true;
                        }
                    } else if (ExTableCeni.FIELD_NAME_CENA.equalsIgnoreCase(name)) {

                        //Форматирование цены + ед. измерения
                        String edIzm = row.edinicaIzmereniya.getDescription();
                        String result =
                                String.format("%s руб./%s", PRICE_FORMAT.format(row.cena), edIzm);

                        ((TextView) v).setText(result);
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean setViewValue(View v, Cursor c, Field field) {
                    return false;
                }
            };

    /*
     * ViewHolder паттерн для эффективной работы со строками адаптеров
     */
    private class PriceViewHolder extends BaseViewHolder {

        TextView tvDescription, tvPrice;
        FieldEditText fetKolvo;

        public PriceViewHolder(View root) {
            super(root);
            tvDescription = (TextView) root.findViewById(R.id.tvDescription);
            tvPrice = (TextView) root.findViewById(R.id.tvPrice);
            fetKolvo = (FieldEditText) root.findViewById(R.id.fetKolvo);

            //Обработчик изменения теста в поле ввода
            fetKolvo.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    calcSum();
                }
            });
        }

        @Override
        public View getViewById(int id) {
            switch (id) {
                case R.id.tvDescription:
                    return tvDescription;
                case R.id.tvPrice:
                    return tvPrice;
                case R.id.fetKolvo:
                    return fetKolvo;
            }
            return null;
        }
    }

    /*
     * Рассчитать сумму и отобразить в подвале документа
     */
    private void calcSum() {
        mTvSumma.setText(PRICE_FORMAT.format(getSumTotal()));
    }

    /**
     * Рассчитать сумму заказа
     */
    public double getSumTotal() {
        double sum = 0;
        final int count = maAdapter.getCount();
        for (int i = 0; i < count; i++) {
            ExTableCeni row = maAdapter.getItem(i);
            sum += row.kolvo * row.cena;
        }
        return sum;
    }

    /**
     * Сохранить в локальной базе табличную часть документа
     *
     * @throws SQLException
     */
    public void save(Ref doc) throws SQLException {
        mOwner = doc;

        //удалить все стары записи, если есть
        mTPTovariDao.clearTable(mOwner);

        final int count = maAdapter.getCount();
        int lineNumber = 1;
        for (int i = 0; i < count; i++) {
            ExTableCeni row = (ExTableCeni) maAdapter.getItem(i);
            if (row.kolvo > 0) {
                DocumentZakazPokupatelyaTPTovari tpRow = mTPTovariDao.newItem(mOwner, lineNumber++);
                tpRow.nomenklatura = row.nomenklatura;
                tpRow.harakteristika = row.harakteristikaNomenklaturi;
                tpRow.edinicaIzmereniya = row.edinicaIzmereniya;
                tpRow.kolichestvo = row.kolvo;
                tpRow.cena = row.cena;
                tpRow.summa = row.kolvo * row.cena;

                mTPTovariDao.create(tpRow);
            }
        }
    }


}
