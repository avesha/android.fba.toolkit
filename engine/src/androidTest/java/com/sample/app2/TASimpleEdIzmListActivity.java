package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.app.SimpleCatalogListActivity;
import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.meta.RowDao;

public class TASimpleEdIzmListActivity extends SimpleCatalogListActivity<CatalogEdiniciIzmereniya> {

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TASimpleEdIzmListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.fba_include_simple_list_layout);
        try {
            setContentListView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<CatalogEdiniciIzmereniya> select(RowDao<CatalogEdiniciIzmereniya> dao)
            throws SQLException {

        CatalogNomenklaturaDao ownerDao = getHelper().getDao(CatalogNomenklatura.class);
        CatalogNomenklatura owner = ownerDao.findByDescription("Вентилятор 3302");

        CatalogDao<CatalogEdiniciIzmereniya> catalogDao = (CatalogDao<CatalogEdiniciIzmereniya>) dao;
        return catalogDao.select(null, owner);

    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "CatalogEdiniciIzmereniya (by owner)";

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
