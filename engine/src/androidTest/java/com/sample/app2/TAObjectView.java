package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sample.app2.db.CatalogCenovieGruppi;
import com.sample.app2.db.CatalogCenovieGruppiDao;
import com.sample.app2.db.CatalogKlassifikatorStranMira;
import com.sample.app2.db.CatalogKlassifikatorStranMiraDao;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.db.Constants;
import com.sample.app2.db.ConstantsDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.widget.ObjectView;

public class TAObjectView extends FbaDBActivity {

    private static final String EXTRA_SHOW_MODE = "show-mode";
    private static final String EXTRA_TITLE = "title";

    private ObjectView mObjectView;

    private CatalogNomenklaturaDao mDaoSku;
    private CatalogNomenklatura mSku;

    private static Intent getStartIntent(Context context, String mode, String desc) {
        Intent i = new Intent(context, TAObjectView.class);
        i.putExtra(EXTRA_SHOW_MODE, mode);
        i.putExtra(EXTRA_TITLE, desc);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mode = getIntent().getStringExtra(EXTRA_SHOW_MODE);
        setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        try {
            if (mode.equals("sku")) {
                setContentView(R.layout.ta_object_view_sku);
                initSku();
            } else {
                setContentView(R.layout.ta_object_view_constants);
                initConst();
            }
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void initConst() throws SQLException {
        ConstantsDao dao = getHelper().getDao(Constants.class);
        Constants constants = dao.read();

        String[] fields = new String[]{Constants.FIELD_NAME_FBA_HRANILISCHE};

        int[] ids = new int[]{R.id.ivTest};

        mObjectView = (ObjectView) findViewById(R.id.objectView);
        mObjectView.build(constants, getHelper(), fields, ids);
    }

    private void initSku() throws SQLException {

        String[] fields = new String[]{CatalogNomenklatura.FIELD_NAME_VESOVOI,
                CatalogNomenklatura.FIELD_NAME_VESTI_PARTIONNII_UCHET_PO_SERIYAM,
                CatalogNomenklatura.FIELD_NAME_VESTI_UCHET_PO_HARAKTERISTIKAM,
                CatalogNomenklatura.FIELD_NAME_ARTIKUL, CatalogNomenklatura.FIELD_NAME_KOMMENTARII,
                CatalogNomenklatura.FIELD_NAME_ARTIKUL,
                CatalogNomenklatura.FIELD_NAME_VESOVOI_KOEFFICIENT_VHOZHDENIYA,
                CatalogNomenklatura.FIELD_NAME_STATYA_ZATRAT,
                CatalogNomenklatura.FIELD_NAME_BAZOVAYA_EDINICA_IZMERENIYA,
                CatalogNomenklatura.FIELD_NAME_STAVKA_NDS,
                CatalogNomenklatura.FIELD_NAME_STRANA_PROISHOZHDENIYA,
                CatalogNomenklatura.FIELD_NAME_CENOVAYA_GRUPPA};

        int[] ids = new int[]{R.id.cbVesovoi,        //FieldCheckBox
                R.id.tbVestiPoSeriayam,             //FieldToggleButton
                R.id.cbPoHarakteristic,             //CheckBox
                R.id.tvArtikul,                     //TextView
                R.id.etKomment,                     //EditText
                R.id.fetArtikul,                    //FieldEditText
                R.id.fetVesovoiKoefficient,         //FieldEditText
                R.id.spinStatyaZatrat,              //FieldPresentationSpinner
                R.id.spinBazovayaEdinica,           //FieldPresentationSpinner
                R.id.spinStavkaNDS,                 //FieldPresentationSpinner
                R.id.tvStrana,                      //TextView
                R.id.tvSenovayaGrupa                //TextView
        };

        mObjectView = (ObjectView) findViewById(R.id.objectViewSku);
        mObjectView.setChildCheckBoxAutoText(true);
        mObjectView.setChildTextAutoHint(true);
        mObjectView.setChildSpinAutoPrompt(true);

        Button btn = (Button) findViewById(R.id.btnSave);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    saveSku();
                } catch (SQLException e) {
                    Dbg.printStackTrace(e);
                }

            }
        });

        mDaoSku = getHelper().getDao(CatalogNomenklatura.class);
        mSku = mDaoSku.findByDescription("Грабли");
        if (mSku != null) {

            //нужно обновить те ссылочные поля, которые отображаются на стандартных View,
            //FieldView обновятся автоматически
            CatalogKlassifikatorStranMiraDao daoStran = getHelper().getDao(
                    CatalogKlassifikatorStranMira.class);
            daoStran.refresh(mSku.stranaProishozhdeniya);

            CatalogCenovieGruppiDao daoCen = getHelper().getDao(CatalogCenovieGruppi.class);
            daoCen.refresh(mSku.cenovayaGruppa);

            mObjectView.build(mSku, getHelper(), fields, ids);
        }

    }

    protected void saveSku() throws SQLException {

        int resIdMsg = R.string.msg_no_created_object;

        if (mSku != null) {
            try {
                mDaoSku.update(mSku);
                resIdMsg = R.string.msg_success_save_object;
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
                resIdMsg = R.string.msg_err_save_object;
            }
        }
        showToast(resIdMsg);
    }


    public static class TestActionShowSku implements ITestAction {

        public static final String DESCRIPTION = "Edit CatalogNomenklatura item";

        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context, "sku", DESCRIPTION));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }

    public static class TestActionShowConstants implements ITestAction {

        public static final String DESCRIPTION = "Show constants (image)";

        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context, "const", DESCRIPTION));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
