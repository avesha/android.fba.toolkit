package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListAdapter;

import com.sample.app2.db.CatalogKachestvo;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.app.SimpleCatalogListActivity;

public class TASimpleKachestroListActivity extends SimpleCatalogListActivity<CatalogKachestvo> {

    EditText edFilter;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TASimpleKachestroListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TestActionShow.DESCRIPTION);
        setContentView(R.layout.ta_simple_kachestvo_list);
        try {
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void init() throws SQLException {

        edFilter = (EditText) findViewById(R.id.edFilter);
        Button btn = (Button) findViewById(R.id.btnFilter);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                filterData();
            }
        });

        setContentListView();
    }

    protected void filterData() {
        String str = edFilter.getText().toString();

        ListAdapter adapter = getListAdapter();
        if (adapter instanceof Filterable) {
            Filterable filterable = (Filterable) adapter;
            filterable.getFilter().filter(str);
        }
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "CatalogKachestvo (filter)";

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
