package ru.profi1c.samples.sensus.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.profi1c.engine.widget.FieldAppCompatSpinner;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.samples.sensus.R;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;

public class InfoPage extends BaseSalesPointPage {

    private FieldEditText mEtPhone;
    private FieldEditText mEtSite;
    private FieldAppCompatSpinner mSpinStatus;
    private FieldEditText mEtComment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_page_info, container, false);
        initControls(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inflateData();
    }

    private void initControls(View root) {
        mEtPhone = (FieldEditText) root.findViewById(R.id.etPhone);
        mEtSite = (FieldEditText) root.findViewById(R.id.etSite);
        mSpinStatus = (FieldAppCompatSpinner) root.findViewById(R.id.spinStatus);
        mEtComment = (FieldEditText) root.findViewById(R.id.etComment);
    }



    private void inflateData() {
        CatalogSalesPoints point = getSalesPoint();
        if (point != null) {
            //Связать редактируемые поля торговой точки с элементами управления на странице
            mEtPhone.build(point, CatalogSalesPoints.FIELD_NAME_PHONE, null);
            mEtSite.build(point, CatalogSalesPoints.FIELD_NAME_SITE, null);
            mSpinStatus.build(point, CatalogSalesPoints.FIELD_NAME_STATUS, getHelper());
            mEtComment.build(point, CatalogSalesPoints.FIELD_NAME_COMMENT, null);
        }
    }

    @Override
    void onPageSelected() {

    }

    @Override
    boolean onSaveSalesPoint(CatalogSalesPoints salesPoint) {
        return true;
    }
}
