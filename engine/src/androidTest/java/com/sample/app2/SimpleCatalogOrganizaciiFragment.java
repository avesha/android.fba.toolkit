package com.sample.app2;

import com.sample.app2.db.CatalogOrganizacii;

import ru.profi1c.engine.app.SimpleCatalogFragment;
import ru.profi1c.engine.meta.Ref;

public class SimpleCatalogOrganizaciiFragment extends SimpleCatalogFragment<CatalogOrganizacii> {

    public static SimpleCatalogOrganizaciiFragment newInstance(Ref ref) {
        SimpleCatalogOrganizaciiFragment fragment = new SimpleCatalogOrganizaciiFragment();
        if (ref != null) {
            fragment.setArguments(SimpleCatalogOrganizaciiFragment.toBundle(ref));
        }
        return fragment;
    }
}
