package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogEdiniciIzmereniyaDao;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.widget.FieldCheckBox;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.engine.widget.FieldTextView;
import ru.profi1c.engine.widget.FieldToggleButton;

public class TAFieldView2 extends FbaDBActivity {
    private static final String TAG = TAFieldView2.class.getSimpleName();

    private FieldCheckBox mCheckBox1, mCheckBox2, mCheckBox3, mCheckBox4;
    private FieldEditText mEditText1, mEditText2;
    private FieldToggleButton mToggleButton1, mToggleButton2, mToggleButton3, mToggleButton4;
    private FieldTextView mFieldTextView1, mFieldTextView2, mFieldTextView3;

    private CatalogNomenklaturaDao mDaoSku;
    private CatalogNomenklatura mSku;

    private CatalogEdiniciIzmereniyaDao mDaoEdIzm;
    private CatalogEdiniciIzmereniya mEdIzmdIzm;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAFieldView2.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_field_view2);
        try {
            initCheckBoxView();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }

    }

    private void initCheckBoxView() throws SQLException {
        mCheckBox1 = (FieldCheckBox) findViewById(R.id.checkBox1);
        mCheckBox2 = (FieldCheckBox) findViewById(R.id.checkBox2);
        mCheckBox2.setAutoText(true);
        mCheckBox3 = (FieldCheckBox) findViewById(R.id.checkBox3);
        mCheckBox4 = (FieldCheckBox) findViewById(R.id.checkBox4);

        mEditText1 = (FieldEditText) findViewById(R.id.editText1);
        mEditText1.setAutoHint(true);
        mEditText2 = (FieldEditText) findViewById(R.id.editText2);
        mEditText2.setAutoHint(true);

        mToggleButton1 = (FieldToggleButton) findViewById(R.id.toggleButton1);
        mToggleButton2 = (FieldToggleButton) findViewById(R.id.toggleButton2);
        mToggleButton3 = (FieldToggleButton) findViewById(R.id.toggleButton3);
        mToggleButton4 = (FieldToggleButton) findViewById(R.id.toggleButton4);
        mToggleButton4.setAutoText(true);

        mFieldTextView1 = (FieldTextView) findViewById(R.id.fieldTextView1);
        mFieldTextView2 = (FieldTextView) findViewById(R.id.fieldTextView2);
        mFieldTextView3 = (FieldTextView) findViewById(R.id.fieldTextView3);

        Button btn = (Button) findViewById(R.id.btnSave);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    saveSku();
                } catch (SQLException e) {
                    showToast(e.getMessage());
                }
            }
        });

        FieldFormatter formatter = null;

        mDaoSku = getHelper().getDao(CatalogNomenklatura.class);
        mSku = mDaoSku.findByDescription("Вентилятор настольный");
        if (mSku != null) {

            mCheckBox1.build(mSku, CatalogNomenklatura.FIELD_NAME_VESOVOI, null);
            mCheckBox2.build(mSku, CatalogNomenklatura.FIELD_NAME_VESOVOI, null);
            mCheckBox3.build(mSku, CatalogNomenklatura.FIELD_NAME_NABOR, null);
            mCheckBox4.build(mSku, CatalogNomenklatura.FIELD_NAME_NABOR, null);

            mToggleButton1.build(mSku, CatalogNomenklatura.FIELD_NAME_VESTI_PARTIONNII_UCHET_PO_SERIYAM, null);
            mToggleButton2.build(mSku, CatalogNomenklatura.FIELD_NAME_VESTI_SERIINIE_NOMERA, null);
            mToggleButton3.build(mSku, CatalogNomenklatura.FIELD_NAME_VESTI_SERIINIE_NOMERA, null);
            mToggleButton4.build(mSku, CatalogNomenklatura.FIELD_NAME_VESTI_UCHET_PO_HARAKTERISTIKAM, null);

            mEditText1.build(mSku, CatalogNomenklatura.FIELD_NAME_KOMMENTARII, null);
            mEditText2.build(mSku, CatalogNomenklatura.FIELD_NAME_ARTIKUL, null);

            mFieldTextView1.build(mSku, CatalogNomenklatura.FIELD_NAME_ARTIKUL, formatter);
            mFieldTextView1.setHint("Артикул");
            mFieldTextView2.build(mSku,
                    CatalogNomenklatura.FIELD_NAME_VESOVOI_KOEFFICIENT_VHOZHDENIYA, formatter);
            mFieldTextView2.setDialogTitle("Коэффициент");


        }

        mDaoEdIzm = getHelper().getDao(CatalogEdiniciIzmereniya.class);
        mEdIzmdIzm = mDaoEdIzm.findByCode("000000004");
        if (mEdIzmdIzm != null) {
            mFieldTextView3.build(mEdIzmdIzm, CatalogEdiniciIzmereniya.FIELD_NAME_KOEFFICIENT,
                    formatter);
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

    public static class TestActionShowCustomFields implements ITestAction {

        public static final String DESCRIPTION = "Custom fields of item";

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
