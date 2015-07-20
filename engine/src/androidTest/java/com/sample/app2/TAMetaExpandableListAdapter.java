package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.test_action.ITestAction;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.util.tree.GenericTree;
import ru.profi1c.engine.util.tree.GenericTreeNode;
import ru.profi1c.engine.widget.BaseViewHolder;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.engine.widget.MetaExpandableListAdapter;

public class TAMetaExpandableListAdapter extends FbaDBActivity {
    private static final String TAG = TAMetaExpandableListAdapter.class.getSimpleName();

    private static final int MAX_COUNT_ADAPTERS = 2;
    private ExpandableListView mExpandableListView;
    private int mCurrAdapterIndex = 0;

    private ArrayList<CatalogNomenklatura> mGroupData;
    private HashMap<CatalogNomenklatura, List<CatalogNomenklatura>> mChildData;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAMetaExpandableListAdapter.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.ta_meta_expandable_list_adapter);
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

        //Инициализация данных для примера
        //Выбрать иерархически все элементы справочника
        CatalogNomenklaturaDao daoSku = getHelper().getDao(CatalogNomenklatura.class);
        GenericTree<CatalogNomenklatura> tree = daoSku.selectHierarchically();

        //список групп верхнего уровня
        mGroupData = new ArrayList<CatalogNomenklatura>();
        //‘Соответствие’ группа -> список дочерних элементов
        mChildData = new HashMap<CatalogNomenklatura, List<CatalogNomenklatura>>();

        //перебор только одного уровня
        GenericTreeNode<CatalogNomenklatura> root = tree.getRoot();
        for (GenericTreeNode<CatalogNomenklatura> folder : root.getChildren()) {

            //пустой список дочерних элементов
            ArrayList<CatalogNomenklatura> lstChild = new ArrayList<CatalogNomenklatura>(0);

            if (folder.hasChildren()) {
                for (GenericTreeNode<CatalogNomenklatura> child : folder.getChildren()) {
                    lstChild.add(child.getData());
                }
                mChildData.put(folder.getData(), lstChild);
                mGroupData.add(folder.getData());
            }
        }

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        onNextAdapter();
    }

    private void onNextAdapter() {

        MetaExpandableListAdapter adapter = null;
        try {
            switch (mCurrAdapterIndex) {
                case 0:
                    adapter = getNomenklaturaAdapter();
                    break;
                case 1:
                    adapter = getNomenklaturaFormatedAdapter();
                    break;
            }
        } catch (SQLException e) {
            Dbg.printStackTrace(e);
        }

        if (adapter != null) {
            mExpandableListView.setAdapter(adapter);
        }

        mCurrAdapterIndex++;
        if (mCurrAdapterIndex >= MAX_COUNT_ADAPTERS) {
            mCurrAdapterIndex = 0;
        }
    }

    //Отображает только группы справочника верхнего уровня и подчинённые элементы по ним (только один уровень вниз).
    //Используется предопределенный макет и предопределённые идентификаторы для дочерних элементов
    private MetaExpandableListAdapter getNomenklaturaAdapter() throws SQLException {
        return new MetaExpandableListAdapter<CatalogNomenklatura>(this, CatalogNomenklatura.class,
                mGroupData, android.R.layout.simple_expandable_list_item_1,
                new String[]{"Description"}, new int[]{android.R.id.text1}, mChildData,
                android.R.layout.simple_expandable_list_item_2, new String[]{"Description"},
                new int[]{android.R.id.text1});
    }

    //Верхний уровень групп как и в примере 1, но с форматированием,
    //для дочерних элементов кастомное оформление
    private MetaExpandableListAdapter getNomenklaturaFormatedAdapter() throws SQLException {
        MetaAdapterViewBinder groupBinder = new MetaAdapterViewBinder(this,
                CatalogNomenklatura.class, new String[]{CatalogNomenklatura.FIELD_NAME_DESCRIPTION},
                new int[]{android.R.id.text1});
        groupBinder.setFieldFormatter(new FieldFormatter.Builder().setStringFormat(":%s").create());

        MetaAdapterViewBinder childBinder = new MetaAdapterViewBinder(this,
                CatalogNomenklatura.class, new String[]{CatalogNomenklatura.FIELD_NAME_DESCRIPTION,
                CatalogNomenklatura.FIELD_NAME_VID_NOMENKLATURI,
                CatalogNomenklatura.FIELD_NAME_FOLDER},
                new int[]{R.id.tvCaption, R.id.tvOther, R.id.checkBox1});
        childBinder.setViewBinder(mSkuViewBinder);
        childBinder.setFieldFormatter(new FieldFormatter.Builder().setNullFormat("<>").create());

        return new MetaExpandableListAdapter<CatalogNomenklatura>(mGroupData,
                android.R.layout.simple_expandable_list_item_1, groupBinder, mChildData,
                R.layout.test_row_sku2, childBinder);
    }

    /*
     * кастомное фоматирование поля,связь элементов отображения с данными (обработчики событий изменения)
     */
    private MetaAdapterViewBinder.ViewBinder mSkuViewBinder = new MetaAdapterViewBinder.ViewBinder() {

        @Override
        public boolean setViewValue(View v, Cursor cursor, Field field) {
            return false;
        }

        @Override
        public boolean setViewValue(View v, Object item, Field field) {

            CatalogNomenklatura ref = (CatalogNomenklatura) item;
            //Покажем код и в скобках наименование
            final String name = field.getName();
            if (CatalogNomenklatura.FIELD_NAME_DESCRIPTION.equalsIgnoreCase(name)) {
                ((TextView) v).setText(ref.getCode() + " (" + ref.getDescription() + ")");
                return true;
            }
            return false;
        }

        @Override
        public BaseViewHolder createViewHolder(View root) {

            SkuViewHolder holder = new SkuViewHolder(root);
            holder.checkBox1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;

                    int pos = (Integer) cb.getTag();
                    Dbg.d(TAG + "checkBox1.OnClick, pos = " + pos);
                    //добавить обработчик по клику
                }
            });

            return holder;
        }

        @Override
        public void onBind(BaseViewHolder viewHolder, int position) {
            SkuViewHolder holder = (SkuViewHolder) viewHolder;
            holder.checkBox1.setTag(position);
        }
    };

    /*
     * ViewHolder паттерн для эффективной работы со строками адаптеров
     */
    private static class SkuViewHolder extends BaseViewHolder {

        private TextView tvCaption;
        private TextView tvOther;
        private CheckBox checkBox1;

        public SkuViewHolder(View root) {
            super(root);
            tvCaption = (TextView) root.findViewById(R.id.tvCaption);
            tvOther = (TextView) root.findViewById(R.id.tvOther);
            checkBox1 = (CheckBox) root.findViewById(R.id.checkBox1);
        }

        @Override
        public View getViewById(int id) {
            switch (id) {
                case R.id.tvCaption:
                    return tvCaption;
                case R.id.tvOther:
                    return tvOther;
                case R.id.checkBox1:
                    return checkBox1;
            }
            return null;
        }
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "MetaExpandableListAdapter";

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
