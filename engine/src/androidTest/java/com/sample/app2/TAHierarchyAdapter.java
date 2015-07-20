package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.widget.HierarchyAdapter;

public class TAHierarchyAdapter extends FbaDBActivity {
    private static final String TAG = TAHierarchyAdapter.class.getSimpleName();

    private ListView list;
    private EditText etFilter;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAHierarchyAdapter.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.ta_hierarhy_adapter);
        init();
    }

    private void init() {

        Button btn = (Button) findViewById(R.id.btnFilter);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onFilterClick();
            }
        });

        btn = (Button) findViewById(R.id.btnUp);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onUpClick();
            }

        });

        etFilter = (EditText) findViewById(R.id.editText1);

        list = (ListView) findViewById(R.id.listView1);
        //Установить обработчик выбора элемент в списке
        list.setOnItemClickListener(onListItemClickListener);

        try {
            initAdapter1();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initAdapter1() throws SQLException {

        //Из всего справочника «Номенклатура» отбираются только дочерние группы и элементы по группе «Обувь».
        //Сортировка: группы сверху, по наименованию
        CatalogNomenklaturaDao dao = getHelper().getDao(CatalogNomenklatura.class);
        CatalogNomenklatura parent = dao.findByDescription("Обувь");
        String order = Catalog.FIELD_NAME_PARENT + " DESC, " + Catalog.FIELD_NAME_DESCRIPTION;

        HierarchyAdapter<CatalogNomenklatura> adapter = new HierarchyAdapter<CatalogNomenklatura>(
                this, dao, parent, null, order);
        //Группы выделить жирным шифтом
        adapter.setSelectedFolderBold(true);
        list.setAdapter(adapter);
    }

    /*
     * Обработчик выбора элемент в списке. Если выбрана группа – проваливаемся ниже.
     */
    private OnItemClickListener onListItemClickListener = new OnItemClickListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Dbg.d(TAG, "onItemClick, pos = " + position);

            CatalogNomenklatura item = (CatalogNomenklatura) list.getItemAtPosition(position);
            if (item.isFolder()) {
                try {
                    ((HierarchyAdapter<CatalogNomenklatura>) list.getAdapter()).push(item);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /*
     * Обработчик нажатия на кнопку «UP»
     */
    @SuppressWarnings("unchecked")
    private void onUpClick() {
        try {
            ((HierarchyAdapter<CatalogNomenklatura>) list.getAdapter()).pop();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Снять/установить дополнительный отбор по представлению
     */
    @SuppressWarnings("unchecked")
    private void onFilterClick() {
        String pattern = etFilter.getText().toString();
        ((HierarchyAdapter<CatalogNomenklatura>) list.getAdapter()).getFilter().filter(pattern);
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "HierarchyAdapter";

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
