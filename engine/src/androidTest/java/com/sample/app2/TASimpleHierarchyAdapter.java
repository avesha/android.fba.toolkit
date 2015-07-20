package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;

import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaDBListActivity;
import ru.profi1c.engine.widget.SimpleHierarchyAdapter;

public class TASimpleHierarchyAdapter extends FbaDBListActivity {

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TASimpleHierarchyAdapter.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.ta_simple_hierarchy_adapter);
        init();
    }

    private void init() {

        try {
            //Иерархический вывод групп справочника «Номенклатура»
            CatalogNomenklaturaDao daoSku = getHelper().getDao(CatalogNomenklatura.class);
            SimpleHierarchyAdapter<CatalogNomenklatura> adapter = new SimpleHierarchyAdapter<CatalogNomenklatura>(
                    this, daoSku);
            setListAdapter(adapter);

            Spinner spin = (Spinner) findViewById(R.id.spin1);
            spin.setAdapter(adapter);

        } catch (SQLException e) {
            Dbg.printStackTrace(e);
        }
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "SimpleHierarchyAdapter";

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
