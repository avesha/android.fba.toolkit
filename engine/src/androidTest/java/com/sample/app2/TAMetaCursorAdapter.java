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

import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.db.CatalogOrganizacii;
import com.sample.app2.db.CatalogOrganizaciiDao;
import com.sample.app2.test_action.ITestAction;

import java.lang.reflect.Field;
import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.widget.BaseViewHolder;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.engine.widget.MetaCursorAdapter;

public class TAMetaCursorAdapter extends FbaDBActivity {
    private static final String TAG = TAMetaCursorAdapter.class.getSimpleName();

    private static final int MAX_COUNT_ADAPTERS = 2;
    ListView mListView;
    int mCurrAdapterIndex = 0;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAMetaCursorAdapter.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.ta_meta_cursor_adapter);
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

        MetaCursorAdapter adapter = null;
        try {
            switch (mCurrAdapterIndex) {
                case 0:
                    adapter = getNomenklaturaAdapter();
                    break;
                case 1:
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

    //Весь список организаций, отображается только код. Как макет строки использует предопределеный макет
    //из android.R.layout.simple_list_item_1 и предопределенный идентификатор android.R.id.text1
    private MetaCursorAdapter getOrganizaciiAdapter() throws SQLException {
        MetaCursorAdapter mca = null;
        CatalogOrganizaciiDao dao = getHelper().getDao(CatalogOrganizacii.class);
        Cursor cursor = dao.selectCursor();
        startManagingCursor(cursor);

        if (cursor != null) {

            mca = new MetaCursorAdapter(this, CatalogOrganizacii.class, cursor,
                    android.R.layout.simple_list_item_1, new String[]{"code"},
                    new int[]{android.R.id.text1});
        }
        return mca;
    }

    //Справочник «Номенклатура» с отбором по услугам.
    //Отображаются 3 реквизита: «Наименование», «Ставка НДС» и признак услуги как флажок.
    //Используется кастомный макет для отображения, переопределяется вывод наименования и устанавливается свой обработчик на флажок.
    private MetaCursorAdapter getNomenklaturaAdapter() throws SQLException {
        MetaCursorAdapter mca = null;
        CatalogNomenklaturaDao daoSku = getHelper().getDao(CatalogNomenklatura.class);
        CatalogNomenklatura parent = daoSku.findByDescription("Услуги");
        Cursor cursor = daoSku.selectCursor(parent);
        startManagingCursor(cursor);

        //Кастомный построитель /форматтер для элементов строки
        MetaAdapterViewBinder adapterBinder = new MetaAdapterViewBinder(this,
                CatalogNomenklatura.class, new String[]{CatalogNomenklatura.FIELD_NAME_DESCRIPTION,
                CatalogNomenklatura.FIELD_NAME_STAVKA_NDS, CatalogNomenklatura.FIELD_NAME_USLUGA,},
                new int[]{R.id.tvCaption, R.id.tvOther, R.id.cbUsluga});
        adapterBinder.setViewBinder(mSkuViewBinder);

        mca = new MetaCursorAdapter(this, cursor, R.layout.test_row_sku,
                adapterBinder);
        return mca;
    }

    /*
     * кастомное фоматирование поля,связь элементов отображения с данными (обработчики событий измемения)
     */
    private MetaAdapterViewBinder.ViewBinder mSkuViewBinder = new MetaAdapterViewBinder.ViewBinder() {

        //кастомное фоматирование поля
        @Override
        public boolean setViewValue(View v, Cursor cursor, Field field) {

            final String name = field.getName();
            if (CatalogNomenklatura.FIELD_NAME_DESCRIPTION.equalsIgnoreCase(name)) {

                int level = 0;
                String desc = "";
                //Получить индексы  колонок и значение по ним
                int colIndex = cursor.getColumnIndex(CatalogNomenklatura.FIELD_NAME_LEVEL);
                if (colIndex != -1) {
                    level = cursor.getInt(colIndex);
                }

                colIndex = cursor.getColumnIndex(CatalogNomenklatura.FIELD_NAME_DESCRIPTION);
                if (colIndex != -1) {
                    desc = cursor.getString(colIndex);
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < level; i++)
                    sb.append("-");

                ((TextView) v).setText(sb.toString() + " " + desc);
                return true;
            }
            return false;
        }

        @Override
        public boolean setViewValue(View v, Object item, Field field) {
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
                    Cursor c = (Cursor) mListView.getItemAtPosition(pos);

                    int columnIndex = c.getColumnIndex(CatalogNomenklatura.FIELD_NAME_REF);
                    if (columnIndex != -1) {
                        String ref = c.getString(columnIndex);
                        Dbg.d(TAG , " SKU ref = " + ref);
                        // Теперь по идентификатору например можно найти элемент
                        // в справочнике и обновить его

                        // Курсор автоматически не обновляется, при обновлении
                        // данных вы должны обновить его самостоятельно
                    }

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
            super(root);
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

        public static final String DESCRIPTION = "MetaCursorAdapter";

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
