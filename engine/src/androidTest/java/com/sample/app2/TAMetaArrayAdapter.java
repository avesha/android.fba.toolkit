package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogEdiniciIzmereniyaDao;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.db.CatalogOrganizacii;
import com.sample.app2.db.CatalogOrganizaciiDao;
import com.sample.app2.test_action.ITestAction;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.widget.BaseViewHolder;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.engine.widget.MetaArrayAdapter;

public class TAMetaArrayAdapter extends FbaDBActivity {

    private static final int MAX_COUNT_ADAPTERS = 3;
    ListView mListView;
    int mCurrAdapterIndex = 0;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAMetaArrayAdapter.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.ta_meta_array_adapter);
        try {
            init();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void init() throws SQLException {
        Button btn = (Button) findViewById(R.id.btnNext);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextAdapter();
            }
        });

        mListView = (ListView) findViewById(android.R.id.list);
        onNextAdapter();
    }

    private void onNextAdapter() {

        MetaArrayAdapter adapter = null;
        try {
            switch (mCurrAdapterIndex) {
                case 0:
                    adapter = getNomenklaturaAdapter();
                    break;
                case 1:
                    adapter = getEdIzmAdapter();
                    break;
                case 2:
                    adapter = getOrganizaciiAdapter();
                    break;
            }
        } catch (SQLException e) {
            Dbg.printStackTrace(e);
        }

        if (adapter != null) {
            mListView.setAdapter(adapter);
        }

        mCurrAdapterIndex++;
        if (mCurrAdapterIndex >= MAX_COUNT_ADAPTERS) {
            mCurrAdapterIndex = 0;
        }
    }

    /*
    * Список организаций, отображается только реквизит «Код».
    * Макетом для строк выступает предопределенный android.R.layout.simple_list_item_1
    * и TextView на нем с идентификатором  android.R.id.text1
     */
    private MetaArrayAdapter getOrganizaciiAdapter() throws SQLException {
        CatalogOrganizaciiDao dao = getHelper().getDao(CatalogOrganizacii.class);

        MetaArrayAdapter<CatalogOrganizacii> maa = new MetaArrayAdapter<CatalogOrganizacii>(this,
                CatalogOrganizacii.class, dao.select(), android.R.layout.simple_list_item_1,
                new String[]{"code"}, new int[]{android.R.id.text1});
        return maa;
    }

    /*
     * Справочник "Единицы измерения". Как макет строки используется пользовательский макет R.layout.test_row_meta_adapter
     * определенный в ресурсах приложения, содержит три view с идентификаторами R.id.tvCode, R.id.tvDesc, R.id.tvOther
     * Выводятся предопределенные поля справочника "code","description" и пользовательское CatalogEdiniciIzmereniya.FIELD_NAME_KOEFFICIENT
     * (в данном случае имя поля равно имени колонке в таблице)
     */
    private MetaArrayAdapter getEdIzmAdapter() throws SQLException {
        CatalogEdiniciIzmereniyaDao dao2 = getHelper().getDao(CatalogEdiniciIzmereniya.class);
        MetaArrayAdapter<CatalogEdiniciIzmereniya> maa2 = new MetaArrayAdapter<CatalogEdiniciIzmereniya>(
                this, CatalogEdiniciIzmereniya.class, dao2.select(), R.layout.test_row_meta_adapter,
                new String[]{"code", "description",
                        CatalogEdiniciIzmereniya.FIELD_NAME_KOEFFICIENT},
                new int[]{R.id.tvCode, R.id.tvDesc, R.id.tvOther});
        return maa2;
    }

    /*
     * Справочник «Номенклатура» с отбором по услугам.
     * Отображаются 3 реквизита: «Наименование», «Ставка НДС» и признак услуги как флажок.
     * Используется кастомный макет для отображения, переопределяется вывод наименования и устанавливается свой обработчик на флажок.
     */
    private MetaArrayAdapter getNomenklaturaAdapter() throws SQLException {
        CatalogNomenklaturaDao daoSku = getHelper().getDao(CatalogNomenklatura.class);
        CatalogNomenklatura parent = daoSku.findByDescription("Услуги");
        List<CatalogNomenklatura> lst = daoSku.select(parent);

        //Кастомный построитель /форматтер для элементов строки
        MetaAdapterViewBinder adapterBinder = new MetaAdapterViewBinder(this,
                CatalogNomenklatura.class, new String[]{CatalogNomenklatura.FIELD_NAME_DESCRIPTION,
                CatalogNomenklatura.FIELD_NAME_STAVKA_NDS, CatalogNomenklatura.FIELD_NAME_USLUGA,},
                new int[]{R.id.tvCaption, R.id.tvOther, R.id.cbUsluga});
        adapterBinder.setViewBinder(mSkuViewBinder);

        MetaArrayAdapter<CatalogNomenklatura> maaSku = new MetaArrayAdapter<CatalogNomenklatura>(
                lst, R.layout.test_row_sku, adapterBinder);
        maaSku.getFilter().filter("расходы");
        return maaSku;
    }

    /*
     * кастомное форматирование поля,связь элементов отображения с данными (обработчики событий изменения)
     */
    private MetaAdapterViewBinder.ViewBinder mSkuViewBinder = new MetaAdapterViewBinder.ViewBinder() {

        @Override
        public boolean setViewValue(View v, Cursor cursor, Field field) {
            return false;
        }

        @Override
        public boolean setViewValue(View v, Object item, Field field) {

            CatalogNomenklatura ref = (CatalogNomenklatura) item;
            final String name = field.getName();

            if (CatalogNomenklatura.FIELD_NAME_DESCRIPTION.equalsIgnoreCase(name)) {
                StringBuilder sb = new StringBuilder();
                int level = ref.getLevel();
                for (int i = 0; i < level; i++)
                    sb.append("-");

                ((TextView) v).setText(sb.toString() + " " + ref.getDescription());
                return true;
            }
            return false;
        }

        @Override
        public BaseViewHolder createViewHolder(View root) {

            SkuViewHolder holder = new SkuViewHolder(root);

            //по клику на чек-бокс изменяем значение в связанном элементе адаптера
            //внимание! сам элемент здесь не сохраняется в базу данных
            holder.cbUsluga.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;

                    int pos = (Integer) cb.getTag();
                    CatalogNomenklatura ref = (CatalogNomenklatura) mListView.getItemAtPosition(
                            pos);
                    ref.usluga = cb.isChecked();
                    ref.setModified(true);
                }
            });
            return holder;
        }

        @Override
        public void onBind(BaseViewHolder viewHolder, int position) {
            SkuViewHolder holder = (SkuViewHolder) viewHolder;
            holder.cbUsluga.setTag(position);
        }
    };

    /*
     * ViewHolder паттерн для эффективной работы со строками адаптеров
     */
    private static class SkuViewHolder extends BaseViewHolder {

        private TextView tvCaption;
        private TextView tvOther;
        private CheckBox cbUsluga;

        public SkuViewHolder(View root) {
            super(root); //<-не забываем
            tvCaption = (TextView) root.findViewById(R.id.tvCaption);
            tvOther = (TextView) root.findViewById(R.id.tvOther);
            cbUsluga = (CheckBox) root.findViewById(R.id.cbUsluga);
        }

        @Override
        public View getViewById(int id) {
            switch (id) {
                case R.id.tvCaption:
                    return tvCaption;
                case R.id.tvOther:
                    return tvOther;
                case R.id.cbUsluga:
                    return cbUsluga;
            }
            return null;
        }

    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "MetaArrayAdapter";

        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
