package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SpinnerAdapter;

import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogEdiniciIzmereniyaDao;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturaDao;
import com.sample.app2.db.DocumentVnutrenniiZakaz;
import com.sample.app2.db.DocumentVnutrenniiZakazDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;
import java.util.WeakHashMap;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.widget.FieldDateView;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.engine.widget.FieldPresentationSpinner;
import ru.profi1c.engine.widget.FieldTimeView;
import ru.profi1c.engine.widget.SpinnerAdapterRequest;

public class TAFieldView extends FbaDBActivity {
    private static final String TAG = TAFieldView.class.getSimpleName();

    private FieldDateView mFvDate;
    private FieldTimeView mFvTime;

    private DocumentVnutrenniiZakazDao mDaoZakaz;
    private DocumentVnutrenniiZakaz mDocZakaz, mDocSpinTest;

    private FieldEditText mEtDesc, mEtAsIntValue, mEtAsDouble;

    private CatalogNomenklaturaDao mDaoSku;
    private CatalogNomenklatura mSku, mSkuSpinTest;

    private CatalogEdiniciIzmereniyaDao mDaoEdIzm;
    private CatalogEdiniciIzmereniya mEdIzm;

    private FieldPresentationSpinner mSpinEnum1, mSpinEnum2, mSpinCatalog1, mSpinCatalog2, mSpinDoc;

    private WeakHashMap<Class<?>, SpinnerAdapter> mCacheAdapters = new WeakHashMap<Class<?>, SpinnerAdapter>();

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAFieldView.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ta_field_view);
        try {
            initDao();
            initDateView();
            initDocSpin();
            initSampleSomeFieldOfCatalog();
            initSpinnerView();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void initDao() throws SQLException {
        mDaoZakaz = new DocumentVnutrenniiZakazDao(getConnectionSource());
        mDaoSku = getHelper().getDao(CatalogNomenklatura.class);
        mDaoEdIzm = getHelper().getDao(CatalogEdiniciIzmereniya.class);
    }

    private void initSpinnerView() throws SQLException {

        Button btn = (Button) findViewById(R.id.btnSaveCatalogTestSpin);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveCatalogTestSpin();
            }
        });

        mSpinEnum1 = (FieldPresentationSpinner) findViewById(R.id.spinEnum1);
        mSpinEnum1.setCacheAdapter(mCacheAdapterRequest);
        mSpinEnum1.setAutoPrompt(true);
        mSpinEnum1.setPromptSelect("<Кликни здесь>");

        mSpinEnum2 = (FieldPresentationSpinner) findViewById(R.id.spinEnum2);
        mSpinEnum2.setCacheAdapter(mCacheAdapterRequest);
        mSpinEnum2.setSelectRequest(true);

        View v = findViewById(R.id.btnClear);
        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSpinCatalog1.setSelection(-1);
                mSpinEnum1.setSelection(-1);
            }
        });

        mSpinCatalog1 = (FieldPresentationSpinner) findViewById(R.id.spinCatalog1);
        mSpinCatalog1.setCacheAdapter(mCacheAdapterRequest);
        mSpinCatalog1.setSelectRequest(true);

        mSpinCatalog2 = (FieldPresentationSpinner) findViewById(R.id.spinCatalog2);
        mSpinCatalog2.setCacheAdapter(mCacheAdapterRequest);
        mSpinCatalog2.setSelectRequest(true);

        //данные
        mSkuSpinTest = mDaoSku.findByDescription("Принц (печенье)");
        if (mSkuSpinTest != null) {

            //перечисление, будет на двух спиннерах
            mSpinEnum1.build(mSkuSpinTest, CatalogNomenklatura.FIELD_NAME_STAVKA_NDS, getHelper());
            mSpinEnum2.build(mSkuSpinTest, CatalogNomenklatura.FIELD_NAME_STAVKA_NDS, getHelper());

            //независимый справочник
            mSpinCatalog1.build(mSkuSpinTest, CatalogNomenklatura.FIELD_NAME_STATYA_ZATRAT,
                               getHelper());
            //подчиненный
            mSpinCatalog2.build(mSkuSpinTest, CatalogNomenklatura.FIELD_NAME_EDINICA_DLYA_OTCHETOV,
                               getHelper());

        }

    }

    private void initDateView() throws SQLException {

        mFvDate = (FieldDateView) findViewById(R.id.fvDate);
        mFvTime = (FieldTimeView) findViewById(R.id.fvTime);

        Button btn = (Button) findViewById(R.id.btnSave);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveDoc();
            }
        });

        mDaoZakaz = new DocumentVnutrenniiZakazDao(getConnectionSource());
        mDocZakaz = mDaoZakaz.findByNumber("ТК000000001");
        if (mDocZakaz != null) {

            mFvDate.build(mDocZakaz, DocumentVnutrenniiZakaz.FIELD_NAME_DATA_OTGRUZKI, null);
            mFvTime.build(mDocZakaz, DocumentVnutrenniiZakaz.FIELD_NAME_DATA_OTGRUZKI, null);
        }

    }

    private void initDocSpin() throws SQLException {

        Button btn = (Button) findViewById(R.id.btnSaveDoc2);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveDocumentTestSpin();
            }
        });

        mSpinDoc = (FieldPresentationSpinner) findViewById(R.id.spinDoc);
        mSpinDoc.setCacheAdapter(mCacheAdapterRequest);
        mSpinDoc.setSelectRequest(true);

        mDocSpinTest = mDaoZakaz.findByNumber("ТК000000002");
        if (mDocSpinTest != null) {
            mSpinDoc.build(mDocSpinTest, DocumentVnutrenniiZakaz.FIELD_NAME_DOKUMENT_OSNOVANIE,
                    getHelper());
        }
    }

    private void initSampleSomeFieldOfCatalog() throws SQLException {

        mEtDesc = (FieldEditText) findViewById(R.id.etDesc);
        mEtDesc.setAutoHint(true);
        mEtAsIntValue = (FieldEditText) findViewById(R.id.etAsIntValue);
        mEtAsDouble = (FieldEditText) findViewById(R.id.etAsDouble);

        FieldFormatter formatter =
                new FieldFormatter.Builder()
                        .setStringFormat("custom: %s")
                        .setIntFormat("%d -шт") //для этого отобразит, но при редактировани надо будет снача убрать символы
                        .setDoubleFormat("###,##0.00 'р.'").create();

        Button btn = (Button) findViewById(R.id.btnSaveCatalog);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveEditSomeFieldOfCatalog();
            }
        });

        mSku = mDaoSku.findByDescription("Белочка (конфеты)");
        if (mSku != null) {
            mEtDesc.build(mSku, CatalogNomenklatura.FIELD_NAME_KOMMENTARII, formatter);
            mEtAsIntValue
                    .build(mSku, CatalogNomenklatura.FIELD_NAME_VESOVOI_KOEFFICIENT_VHOZHDENIYA,
                           formatter);
        }

        mEdIzm = mDaoEdIzm.findByCode("000000004");
        if (mEdIzm != null) {
            mEtAsDouble.build(mEdIzm, CatalogEdiniciIzmereniya.FIELD_NAME_KOEFFICIENT, formatter);
        }
    }

    /*
   * кеширование адаптеров
   */
    SpinnerAdapterRequest mCacheAdapterRequest = new SpinnerAdapterRequest() {

        @Override
        public SpinnerAdapter getCachedAdapter(Class<?> classOfValues) {
            return mCacheAdapters.get(classOfValues);
        }

        @Override
        public void onNewAdapter(Class<?> classOfValues, SpinnerAdapter adapter) {
            mCacheAdapters.put(classOfValues, adapter);
        }

    };

    protected void saveDoc() {

        int resIdMsg = R.string.msg_no_created_object;

        if (mDocZakaz != null) {
            try {
                mDaoZakaz.update(mDocZakaz);
                resIdMsg = R.string.msg_success_save_object;
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
                resIdMsg = R.string.msg_err_save_object;
            }
        }
        showToast(resIdMsg);

    }

    private void saveDocumentTestSpin() {

        Dbg.d(TAG, "doc osnovanie from spin = " + mSpinDoc.getValue());
        int resIdMsg = R.string.msg_no_created_object;

        if (mDocSpinTest != null) {
            Dbg.d(TAG, " docSpinTest. doc osnovanie from doc = " + mDocSpinTest.dokumentOsnovanie);
            try {
                mDaoZakaz.update(mDocSpinTest);
                resIdMsg = R.string.msg_success_save_object;
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
                resIdMsg = R.string.msg_err_save_object;
            }
        }
        showToast(resIdMsg);

    }

    protected void saveEditSomeFieldOfCatalog() {

        Dbg.d(TAG, " etDesc.value = " + mEtDesc.getValue());
        Dbg.d(TAG, " etAsIntValue.value = " + mEtAsIntValue.getValue());
        Dbg.d(TAG, " etAsDouble.value = " + mEtAsDouble.getValue());

        int resIdMsg = R.string.msg_no_created_object;

        if (mDaoSku != null && mDaoEdIzm !=null) {
            try {
                mDaoSku.update(mSku);
                mDaoEdIzm.update(mEdIzm);
                resIdMsg = R.string.msg_success_save_object;
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
                resIdMsg = R.string.msg_err_save_object;
            }
        }
        showToast(resIdMsg);
    }

    protected void saveCatalogTestSpin() {

        Dbg.d(TAG, " skuSpinTest.spinEnum1 = " + mSpinEnum1.getValue());
        Dbg.d(TAG, " skuSpinTest.spinEnum2 = " + mSpinEnum2.getValue());
        Dbg.d(TAG, " skuSpinTest.spinCatalog1 = " + mSpinCatalog1.getValue());
        Dbg.d(TAG, " skuSpinTest.spinCatalog2 = " + mSpinCatalog2.getValue());

        int resIdMsg = R.string.msg_no_created_object;

        if (mSkuSpinTest != null) {

            Dbg.d(TAG, " skuSpinTest.spinEnum1(2) from catalog = " + mSkuSpinTest.stavkaNDS);
            Dbg.d(TAG, " skuSpinTest.spinCatalog1 from catalog = " + mSkuSpinTest.statyaZatrat);
            Dbg.d(TAG, " skuSpinTest.spinCatalog2 from catalog = " + mSkuSpinTest.edinicaDlyaOtchetov);

            try {
                mDaoSku.update(mSkuSpinTest);
                resIdMsg = R.string.msg_success_save_object;
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
                resIdMsg = R.string.msg_err_save_object;
            }
        }
        showToast(resIdMsg);
    }

    public static class TestActionShowBaseSample implements ITestAction {

        public static final String DESCRIPTION = "Base sample";

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
