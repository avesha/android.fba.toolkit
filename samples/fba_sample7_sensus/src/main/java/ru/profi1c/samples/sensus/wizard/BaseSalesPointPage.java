package ru.profi1c.samples.sensus.wizard;

import android.app.Activity;

import ru.profi1c.engine.app.FbaDBFragment;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;

abstract class BaseSalesPointPage extends FbaDBFragment {

    abstract void onPageSelected();
    abstract boolean onSaveSalesPoint(CatalogSalesPoints salesPoint);

    CatalogSalesPoints getSalesPoint() {
        Activity activity = getActivity();
        if (activity instanceof AddSalesPointActivity) {
            return ((AddSalesPointActivity) activity).getSalesPoint();
        }
        return null;
    }

}
