package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.db.CatalogOrganizacii;
import com.sample.app2.db.CatalogOrganizaciiDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.app.SimpleCatalogFragment;
import ru.profi1c.engine.meta.Catalog;

public class TASimpleCatalogFragment extends FbaDBActivity {
    private static final String TAG = TASimpleCatalogFragment.class.getSimpleName();
    private static final String EXTRA_SHOW_MODE = "show-mode";

    private enum ShowMode {Organization, Sku}

    private ShowMode mShowMode;
    private SimpleCatalogFragment mFragment;
    private CatalogOrganizaciiDao mDaoOrg;
    private CatalogNomenklaturaDao mDaoSku;

    private static Intent getStartIntent(Context context, ShowMode showMode) {
        Intent i = new Intent(context, TASimpleCatalogFragment.class);
        i.putExtra(EXTRA_SHOW_MODE, showMode.ordinal());
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowMode = ShowMode.values()[getIntent().getIntExtra(EXTRA_SHOW_MODE, 0)];
        if (mShowMode == ShowMode.Sku) {
            setTitle(TestActionShowSku.DESCRIPTION);
        } else {
            setTitle(TestActionShowOrganization.DESCRIPTION);
        }
        setContentView(R.layout.ta_simple_cataloig_fragment);
        try {
            init();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void init() throws SQLException {

        Button bnt = (Button) findViewById(R.id.bntSave);
        bnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSave();
            }
        });

        bnt = (Button) findViewById(R.id.bntNew);
        bnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onNew();
            }
        });

        bnt = (Button) findViewById(R.id.bntEdit);
        bnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEdit();
            }
        });

        mDaoOrg = getHelper().getDao(CatalogOrganizacii.class);
        mDaoSku = getHelper().getDao(CatalogNomenklatura.class);
    }

    void inflateFragment(Catalog ref) {
        switch (mShowMode) {
            case Organization:
                mFragment = SimpleCatalogOrganizaciiFragment.newInstance(ref);
                break;
            case Sku:
                mFragment = SimpleCatalogNomenklaturaFragment.newInstance(ref);
                break;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder, mFragment);
        ft.commit();
    }

    protected void onEdit() {
        try {
            switch (mShowMode) {
                case Organization:
                    inflateFragment(mDaoOrg.findByCode("000000002"));
                    break;
                case Sku:
                    inflateFragment(mDaoSku.findByCode("00000000065"));
                    break;
            }

        } catch (SQLException e) {
            Dbg.printStackTrace(e);
        }

    }

    protected void onNew() {
        inflateFragment(null);
    }

    protected void onSave() {
        int resIdMsg = R.string.msg_no_created_object;

        if (mFragment != null) {
            try {
                mFragment.save();
                resIdMsg = R.string.msg_success_save_object;
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
                resIdMsg = R.string.msg_err_save_object;
            }
        }
        showToast(resIdMsg);
    }

    public static class TestActionShowOrganization implements ITestAction {

        public static final String DESCRIPTION = "View one organization";

        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context, ShowMode.Organization));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }

    public static class TestActionShowSku implements ITestAction {

        public static final String DESCRIPTION = "View one sku";

        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context, ShowMode.Sku));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
