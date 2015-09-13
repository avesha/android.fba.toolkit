package ru.profi1c.samples.sensus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.app.ui.AlertDialogFragment;
import ru.profi1c.engine.app.ui.BaseDialogFragment;
import ru.profi1c.engine.util.MediaHelper;
import ru.profi1c.engine.widget.ObjectView;
import ru.profi1c.samples.sensus.db.CatalogExtraStorage;
import ru.profi1c.samples.sensus.db.CatalogExtraStorageDao;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.CatalogSalesPointsDao;
import ru.profi1c.samples.sensus.db.DBHelper;
import ru.profi1c.samples.sensus.util.SelectPhotoHelper;

public class SalesPointActivity extends FbaDBActivity
        implements SelectPhotoHelper.SelectPhotoListener {
    private static final String TAG = SalesPointActivity.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final int ID_DIALOG_PROMPT_SAVE = SalesPointActivity.class.hashCode();
    private static final String EXTRA_EMPTY_PHOTO_HOLDER = "empty-photo-holder";
    private static final String EXTRA_REF = "ref";
    private static final String EXTRA_MANUAL_ANIMATION = "manual-animation";
    private static final String DIVIDER_VIEW_MARKER = "DIVIDER";

    private SelectPhotoHelper mSelectPhotoHelper;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FloatingActionButton mFab;
    private ProgressBar mProgressBar;
    private MenuItem mMenuItemSwitchMode;

    private String mSalesPointId;
    private int mEmptyPhotoColor;

    private ViewSwitcher mSalesPointViewMode;
    private ImageView mPhoto;

    private CatalogSalesPointsDao mSalesPointsDao;
    private CatalogExtraStorageDao mExtraStorageDao;
    private CatalogSalesPoints mSalesPoint;
    private Bitmap mMainPhoto;
    private ObjectView mObjectView;

    private Map<String,View> mRowMaps;

    public static Intent getStartIntent(Context context, CatalogSalesPoints salesPoints,
            int emptyPhotoColorHolder, boolean manualAnimation) {
        Intent i = new Intent(context, SalesPointActivity.class);
        i.putExtra(EXTRA_REF, salesPoints.getRef().toString());
        i.putExtra(EXTRA_EMPTY_PHOTO_HOLDER, emptyPhotoColorHolder);
        i.putExtra(EXTRA_MANUAL_ANIMATION, manualAnimation);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
            getIntent().getBooleanExtra(EXTRA_MANUAL_ANIMATION, false)) {
            Window w = getWindow();
            w.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            Explode explode = new Explode();
            w.setEnterTransition(explode);
            w.setExitTransition(explode);
        }

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_sales_point);
        getIntentExtra(getIntent());
        try {
            initData();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sales_point, menu);
        mMenuItemSwitchMode = menu.findItem(R.id.action_switch_mode);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_switch_mode) {
            mMenuItemSwitchMode.setChecked(!mMenuItemSwitchMode.isChecked());
            boolean inEditMode = mMenuItemSwitchMode.isChecked();
            mMenuItemSwitchMode
                    .setTitle(inEditMode ? R.string.action_save : R.string.action_switch_mode);
            mMenuItemSwitchMode.setIcon(inEditMode ? R.mipmap.ab_ic_save : R.mipmap.ab_ic_edit);
            switchSalesPointViewMode(inEditMode);
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSelectPhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mSalesPoint.isModified()) {
            promptSave();
            return;
        }
        super.onBackPressed();
    }

    private void getIntentExtra(Intent intent) {
        mSalesPointId = intent.getStringExtra(EXTRA_REF);
        mEmptyPhotoColor = intent.getIntExtra(EXTRA_EMPTY_PHOTO_HOLDER,
                                              getResources().getColor(R.color.primary));
    }

    private void initData() throws SQLException {

        mSelectPhotoHelper = new SelectPhotoHelper(this, this);

        DBHelper helper = (DBHelper) getHelper();
        mSalesPointsDao = helper.getDao(CatalogSalesPoints.class);
        mExtraStorageDao = helper.getDao(CatalogExtraStorage.class);

        mSalesPoint = mSalesPointsDao.queryForId(mSalesPointId);

        CatalogExtraStorage extraStorage = mSalesPoint.foto;
        if (!CatalogExtraStorage.isEmpty(extraStorage)) {
            mExtraStorageDao.refresh(extraStorage);
            mMainPhoto = extraStorage.storage.toBitmap();
        }

        mRowMaps = new HashMap<String, View>();
    }

    private void initControls() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        updateTitle();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });

        mSalesPointViewMode = (ViewSwitcher) findViewById(R.id.vsSalesPointViewMode);
        mSalesPointViewMode.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.abc_fade_in));
        mSalesPointViewMode
                .setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.abc_fade_out));

        mPhoto = (ImageView) findViewById(R.id.ivPhoto);
        mPhoto.setBackgroundColor(mEmptyPhotoColor);
        if (mMainPhoto != null) {
            setPhoto(mMainPhoto);
        }

        mObjectView = (ObjectView) findViewById(R.id.ovSalesPoint);

        /* Построить отображение реквизитов справочника сразу на двух панелях.
         * На первой только просмотр реквизитов, на второй – редактирование.
         * При изменении реквизитов, обновление отображения на первой панели производится
         * автоматически т.к используются ‘FieldTextView’
         */
        mObjectView.build(mSalesPoint, getHelper(),
                          new String[]{CatalogSalesPoints.FIELD_NAME_DESCRIPTION,
                                  CatalogSalesPoints.FIELD_NAME_ADDRESS,
                                  CatalogSalesPoints.FIELD_NAME_ADDRESS,
                                  CatalogSalesPoints.FIELD_NAME_PHONE,
                                  CatalogSalesPoints.FIELD_NAME_PHONE,
                                  CatalogSalesPoints.FIELD_NAME_SITE,
                                  CatalogSalesPoints.FIELD_NAME_SITE,
                                  CatalogSalesPoints.FIELD_NAME_STATUS,
                                  CatalogSalesPoints.FIELD_NAME_STATUS,
                                  CatalogSalesPoints.FIELD_NAME_COMMENT,
                                  CatalogSalesPoints.FIELD_NAME_COMMENT},
                          new int[]{R.id.etDescription, R.id.tvAddress, R.id.etAddress,
                                  R.id.tvPhone, R.id.etPhone, R.id.tvSite, R.id.etSite,
                                  R.id.tvStatus, R.id.spinStatus, R.id.tvComment, R.id.etComment});

        /*
         * Временно выключить флаг модификации если он выл выставлен ранее.
         * Этот флаг будет установлен автоматически при изменении любого связного реквизита в этой форме.
         */
        mSalesPoint.setModified(false);

        TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
        if (mSalesPoint.lat != 0 && mSalesPoint.lng != 0) {
            tvLocation.setText(String.format(getString(R.string.hint_location_frm), mSalesPoint.lat,
                                             mSalesPoint.lng));
        }

        /*
         * Коллекция скрываемых элементов управления (если не заполнен соответствующий реквизит)
         */
        mRowMaps.put(CatalogSalesPoints.FIELD_NAME_PHONE, findViewById(R.id.trViewPhone));
        mRowMaps.put(CatalogSalesPoints.FIELD_NAME_SITE, findViewById(R.id.trViewSite));
        mRowMaps.put(CatalogSalesPoints.FIELD_NAME_STATUS, findViewById(R.id.trViewStatus));
        mRowMaps.put(CatalogSalesPoints.FIELD_NAME_COMMENT, findViewById(R.id.trViewComment));
        mRowMaps.put(DIVIDER_VIEW_MARKER, findViewById(R.id.trViewDivider));
        switchRowVisible();
    }

    private void setPhoto(Bitmap photo) {
        mPhoto.setImageBitmap(photo);
        Palette.from(photo).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                applyPalette(palette);
            }
        });
    }

    private void applyPalette(Palette palette) {

        int lightVibrantColor =
                palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));
        mFab.setRippleColor(lightVibrantColor);
        mFab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));

        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        mCollapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        supportStartPostponedEnterTransition();
    }

    private void switchRowVisible() {
        View v = mRowMaps.get(CatalogSalesPoints.FIELD_NAME_PHONE);
        v.setVisibility(TextUtils.isEmpty(mSalesPoint.phone) ? View.GONE : View.VISIBLE);

        v = mRowMaps.get(CatalogSalesPoints.FIELD_NAME_SITE);
        v.setVisibility(TextUtils.isEmpty(mSalesPoint.site) ? View.GONE : View.VISIBLE);

        v = mRowMaps.get(CatalogSalesPoints.FIELD_NAME_STATUS);
        v.setVisibility(mSalesPoint.status == null ? View.GONE : View.VISIBLE);

        v = mRowMaps.get(CatalogSalesPoints.FIELD_NAME_COMMENT);
        v.setVisibility(TextUtils.isEmpty(mSalesPoint.comment) ? View.GONE : View.VISIBLE);

        v = mRowMaps.get(DIVIDER_VIEW_MARKER);
        v.setVisibility(TextUtils.isEmpty(mSalesPoint.comment) && mSalesPoint.status == null? View.GONE : View.VISIBLE);
    }

    private void switchSalesPointViewMode(boolean inEditMode) {
        if (!inEditMode) {
            if (mSalesPoint.isModified()) {
                updateTitle();
                switchRowVisible();
                save();
            }
        }
        mSalesPointViewMode.showNext();
    }

    private void updateTitle() {
        mCollapsingToolbarLayout.setTitle(mSalesPoint.getPresentation());
    }

    private void promptSave() {
        AlertDialogFragment.show(this, ID_DIALOG_PROMPT_SAVE, mSalesPoint.getPresentation(),
                                 getString(R.string.msg_prompt_save_changes),
                                 getString(android.R.string.yes), getString(android.R.string.no),
                                 false);
    }

    private void save() {
        try {
            if (mSalesPoint.isModified()) {
                mSalesPointsDao.createOrUpdate(mSalesPoint);
                mSalesPoint.setModified(false);
                sendBroadcast(new Intent(Const.ACTION_SALES_POINT_CHANGED));
            }
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    @Override
    public void onDialogFragmentResult(BaseDialogFragment dialog, Object data) {
        super.onDialogFragmentResult(dialog, data);
        if (dialog instanceof AlertDialogFragment) {
            int id = ((AlertDialogFragment) dialog).getDialogId();
            if (id == ID_DIALOG_PROMPT_SAVE) {
                int which = (Integer) data;
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    save();
                }
                finishActivity();
            }
        }
    }

    private void onFabClick() {
        if (CatalogExtraStorage.isEmpty(mSalesPoint.foto)) {
            mSelectPhotoHelper.createPhotoFile();
        } else {
            mSelectPhotoHelper.pickPhoto();
        }
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
        try {
            switchProgressBarVisibility(View.VISIBLE);

            final Bitmap bmp =
                    MediaHelper.scaleBitmap(this, new File(fName), Const.PHOTO_MAX_WIDTH);
            if (bmp != null) {
                CatalogExtraStorage extraStorage =
                        mExtraStorageDao.savePhotoToStorage(mSalesPoint, bmp);
                mSalesPoint.foto = extraStorage;
                mSalesPoint.setModified(true);
                mSalesPoint.createThumb(getFbaSettingsProvider().getAppSettings().getCacheDir());

                runOnUiThread(new Runnable() {
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

    private void switchProgressBarVisibility(final int visibility) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(visibility);
            }
        });
    }

    private void finishActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            finishAfterTransition();
        }
    }
}
