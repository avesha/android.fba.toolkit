package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;
import java.util.HashMap;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.engine.widget.ParentExpandableCursorAdapter;

public class TAParentExpandableCursorAdapter extends FbaDBActivity {
    private static final String TAG = TAParentExpandableCursorAdapter.class.getSimpleName();

    private ExpandableListView mExpandableListView;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAParentExpandableCursorAdapter.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.ta_parent_expandable_cursor_adapter);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        try {
            //Из справочника «Номенклатура» выбрать только группы верхнего уровня
            CatalogNomenklaturaDao daoSku = getHelper().getDao(CatalogNomenklatura.class);
            HashMap<String, Object> filter = new HashMap<String, Object>();
            filter.put(Catalog.FIELD_NAME_LEVEL, 0);
            filter.put(Catalog.FIELD_NAME_FOLDER, true);

            //Курсор для групп верхнего уровня
            Cursor cursorParent = daoSku.selectCursor(filter, Catalog.FIELD_NAME_DESCRIPTION);
            startManagingCursor(cursorParent);

            //Выводим только наименование, макет стандартный для раскрывающихся списков
            MetaAdapterViewBinder groupBinder = new MetaAdapterViewBinder(this,
                    CatalogNomenklatura.class,
                    new String[]{CatalogNomenklatura.FIELD_NAME_DESCRIPTION},
                    new int[]{android.R.id.text1});

            ParentExpandableCursorAdapter<CatalogNomenklatura> peca = new ParentExpandableCursorAdapter<CatalogNomenklatura>(
                    this, daoSku, cursorParent, android.R.layout.simple_expandable_list_item_1,
                    groupBinder);

            peca.setOrderChild(Catalog.FIELD_NAME_DESCRIPTION);

            mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
            mExpandableListView.setAdapter(peca);

            //обработчики выбора
            mExpandableListView.setOnGroupClickListener(mOnGroupClickListener);
            mExpandableListView.setOnChildClickListener(mOnChildClickListener);

        } catch (SQLException e) {
            Dbg.printStackTrace(e);
        }
    }

    /*
     * Обработчик нажатия на группу верхнего уровня
     */
    private OnGroupClickListener mOnGroupClickListener = new OnGroupClickListener() {

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            CursorTreeAdapter adapter = (CursorTreeAdapter) parent.getExpandableListAdapter();
            Cursor c = adapter.getCursor();
            handleItem(c, id);
            return false;
        }
    };

    /*Обработчик нажатия на дочерний элемент
     */
    private OnChildClickListener mOnChildClickListener = new OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                int childPosition, long id) {
            CursorTreeAdapter adapter = (CursorTreeAdapter) parent.getExpandableListAdapter();
            Cursor c = adapter.getChild(groupPosition, childPosition);
            handleItem(c, id);
            return true;
        }
    };

    protected void handleItem(Cursor c, long id) {
        int columnIndex = c.getColumnIndex(Catalog.FIELD_NAME_REF);
        if (columnIndex != -1) {
            String ref = c.getString(columnIndex);
            Dbg.d(TAG, " SKU ref = " + ref);
            showToast("SKU ref = " + ref);
            // Теперь по идентификатору например можно найти элемент b выполнить
            // какие-либо действия с ним
        }
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "ParentExpandableCursorAdapter";

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
