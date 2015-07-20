package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.SimpleCatalogListActivity;

public class TASimpleSkuListActivity extends SimpleCatalogListActivity<CatalogNomenklatura> {

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TASimpleSkuListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.fba_include_simple_list_layout);
        try {
            setContentListView();
        } catch (SQLException e) {
            Dbg.printStackTrace(e);
        }
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "CatalogNomenklatura (default)";

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
