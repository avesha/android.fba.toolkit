package ru.profi1c.samples.sensus.wizard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.util.MediaHelper;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.samples.sensus.Const;
import ru.profi1c.samples.sensus.Dbg;
import ru.profi1c.samples.sensus.R;
import ru.profi1c.samples.sensus.db.CatalogExtraStorage;
import ru.profi1c.samples.sensus.db.CatalogExtraStorageDao;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.DBHelper;
import ru.profi1c.samples.sensus.util.SelectPhotoHelper;

public class PhotoPage extends BaseSalesPointPage implements SelectPhotoHelper.SelectPhotoListener {

    private SelectPhotoHelper mSelectPhotoHelper;
    private CatalogExtraStorageDao mExtraStorageDao;

    private ImageView mIvPhoto;
    private ProgressBar mProgressBar;
    private TextInputLayout mTilDescription;
    private FieldEditText mEtDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectPhotoHelper = new SelectPhotoHelper(getFbaActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_page_photo, container, false);
        initControls(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            initData();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
        inflateData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSelectPhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void initControls(View root) {
        mIvPhoto = (ImageView) root.findViewById(R.id.ivPhoto);
        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        mTilDescription = (TextInputLayout) root.findViewById(R.id.tilDescription);
        mEtDescription = (FieldEditText) root.findViewById(R.id.etDescription);
        mEtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mTilDescription.setErrorEnabled(false);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });
    }

    private void initData() throws SQLException {
        DBHelper helper = (DBHelper) getHelper();
        mExtraStorageDao = helper.getDao(CatalogExtraStorage.class);
    }

    private void inflateData() {
        CatalogSalesPoints point = getSalesPoint();
        if (point != null) {
            //Связать поле 'Наименование' торговой точки с элементам управления
            mEtDescription.build(point, CatalogSalesPoints.FIELD_NAME_DESCRIPTION, null);

            //Отобразить фото
            if (CatalogExtraStorage.isEmpty(point.foto)) {
                onFabClick();
            } else {
                Bitmap photo = point.foto.storage.toBitmap();
                if (photo != null) {
                    setPhoto(photo);
                }
            }
        }
    }

    private void setPhoto(Bitmap photo) {
        mIvPhoto.setImageBitmap(photo);
    }

    private void onFabClick() {
        mSelectPhotoHelper.createPhotoFile();
    }

    @Override
    public void onSelectPhoto(final String fName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doUpdatePhoto(fName);
            }
        }).start();
    }

    private void doUpdatePhoto(String fName) {

        final FbaActivity activity = getFbaActivity();
        final CatalogSalesPoints point = getSalesPoint();
        if (activity != null && point != null) {

            try {
                switchProgressBarVisibility(View.VISIBLE);

                final Bitmap bmp =
                        MediaHelper.scaleBitmap(activity, new File(fName), Const.PHOTO_MAX_WIDTH);

                if (bmp != null) {
                    CatalogExtraStorage extraStorage =
                            mExtraStorageDao.savePhotoToStorage(point, bmp);
                    point.foto = extraStorage;
                    point.setModified(true);
                    point.createThumb(
                            activity.getFbaSettingsProvider().getAppSettings().getCacheDir());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setPhoto(bmp);
                        }
                    });

                }
            } catch (FileNotFoundException e) {
                Dbg.printStackTrace(e);
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
            } finally {
                switchProgressBarVisibility(View.GONE);
            }
        }
    }

    private void switchProgressBarVisibility(final int visibility) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(visibility);
            }
        });
    }

    @Override
    void onPageSelected() {

    }

    @Override
    boolean onSaveSalesPoint(CatalogSalesPoints salesPoint) {
        if(TextUtils.isEmpty(salesPoint.getDescription())){
            mTilDescription.setError(getString(R.string.err_description_is_empty));
            return false;
        }
        return true;
    }
}
