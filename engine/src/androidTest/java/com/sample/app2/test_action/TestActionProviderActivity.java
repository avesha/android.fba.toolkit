package com.sample.app2.test_action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.sample.app2.R;

import ru.profi1c.engine.app.FbaListActivity;
import ru.profi1c.engine.app.ui.DescriptionAdapter;

public class TestActionProviderActivity extends FbaListActivity {

    private static final String EXTRA_INDEX = "extra-index";
    private ITestActionProvider mTestActionProvider;

    static Intent getStartIntent(Context context, int index) {
        Intent i = new Intent(context, TestActionProviderActivity.class);
        i.putExtra(EXTRA_INDEX, index);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_provider);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        if (index != -1) {
            mTestActionProvider = TestActionProviderManager.getActionProvider(index);
            setTitle(mTestActionProvider.getDescription());
            init();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        final Context context = this;
        DescriptionAdapter adapter =
                new DescriptionAdapter(context, android.R.layout.simple_list_item_1,
                                       mTestActionProvider.getActions());
        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ITestAction) parent.getAdapter().getItem(position)).run(context);
            }
        });
    }

}
