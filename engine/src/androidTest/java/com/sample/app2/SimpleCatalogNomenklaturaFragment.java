package com.sample.app2;

import android.os.Bundle;

import com.sample.app2.db.CatalogNomenklatura;

import ru.profi1c.engine.app.SimpleCatalogFragment;
import ru.profi1c.engine.meta.Ref;

public class SimpleCatalogNomenklaturaFragment extends SimpleCatalogFragment<CatalogNomenklatura> {

    public static SimpleCatalogNomenklaturaFragment newInstance(Ref ref) {
        SimpleCatalogNomenklaturaFragment fragment = new SimpleCatalogNomenklaturaFragment();
        if (ref != null) {
            fragment.setArguments(SimpleCatalogNomenklaturaFragment.toBundle(ref));
        }
        return fragment;
    }

    @Override
    protected int getResIdLayout() {
        return R.layout.fragment_catalog_nomenklatura;
    }

    @Override
    protected String[] getFields() {
        return new String[]{CatalogNomenklatura.FIELD_NAME_CODE,
                CatalogNomenklatura.FIELD_NAME_DESCRIPTION,
                CatalogNomenklatura.FIELD_NAME_STATYA_ZATRAT,
                CatalogNomenklatura.FIELD_NAME_STAVKA_NDS,
                CatalogNomenklatura.FIELD_NAME_USLUGA,
                CatalogNomenklatura.FIELD_NAME_STRANA_PROISHOZHDENIYA,
                CatalogNomenklatura.FIELD_NAME_VESTI_UCHET_PO_HARAKTERISTIKAM,
                CatalogNomenklatura.FIELD_NAME_VESTI_PARTIONNII_UCHET_PO_SERIYAM};
    }

    @Override
    protected int[] getIds() {
        return new int[]{R.id.fba_code, R.id.fba_description, R.id.spinStatyaZatrat,
                R.id.spinStavkaNds, R.id.cbUsluga, R.id.spinStrana, R.id.switchUchetToHarakteristam,
                R.id.switchUchetToSeriyam};
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildCheckBoxAutoText(true);
        setChildSpinAutoPrompt(true);
        setChildTextAutoHint(true);
    }
}
