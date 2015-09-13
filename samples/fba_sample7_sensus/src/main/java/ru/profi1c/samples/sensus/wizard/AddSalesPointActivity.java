package ru.profi1c.samples.sensus.wizard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.app.ui.AlertDialogFragment;
import ru.profi1c.engine.app.ui.BaseDialogFragment;
import ru.profi1c.engine.app.ui.IDescription;
import ru.profi1c.samples.sensus.App;
import ru.profi1c.samples.sensus.Const;
import ru.profi1c.samples.sensus.Dbg;
import ru.profi1c.samples.sensus.R;
import ru.profi1c.samples.sensus.db.CatalogExtraStorage;
import ru.profi1c.samples.sensus.db.CatalogExtraStorageDao;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.CatalogSalesPointsDao;
import ru.profi1c.samples.sensus.db.DBHelper;

public class AddSalesPointActivity extends FbaDBActivity {
    private static final String TAG = AddSalesPointActivity.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final int ID_DIALOG_PROMPT_SAVE = AddSalesPointActivity.class.hashCode();

    private CatalogSalesPointsDao mSalesPointsDao;
    private CatalogExtraStorageDao mExtraStorageDao;
    private CatalogSalesPoints mSalesPoint;

    private CoordinatorLayout mRootLayout;
    private ViewPager mPager;
    private SalesPointPagerAdapter mPagerAdapter;

    public static Intent getStartIntent(Context context) {
        Intent i = new Intent(context, AddSalesPointActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales_point);
        try {
            initData();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
        initControls();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_sales_point, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if(id == R.id.action_save) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSalesPoint.isModified()) {
            promptSave();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Для SelectPhotoHelper не работает возврат результата, если вызов производится из фрагмента, эмулируем вручную
        BaseSalesPointPage page = mPagerAdapter.getCurrentPage();
        if(page!=null){
            page.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initData() throws SQLException {
        DBHelper helper = (DBHelper) getHelper();
        mSalesPointsDao = helper.getDao(CatalogSalesPoints.class);
        mExtraStorageDao = helper.getDao(CatalogExtraStorage.class);
        mSalesPoint = mSalesPointsDao.newItem();
    }

    CatalogSalesPoints getSalesPoint() {
        return mSalesPoint;
    }

    private void initControls() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mRootLayout = (CoordinatorLayout) findViewById(R.id.clRoot);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SalesPointPagerAdapter(getSupportFragmentManager(), mPager.getId());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(mPagerAdapter.getCount());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                SalesPointPagerAdapter adapter = (SalesPointPagerAdapter) mPager.getAdapter();
                BaseSalesPointPage page = adapter.getCurrentPage();
                if(page!=null){
                    page.onPageSelected();
                }
            }
        });
    }

    private void promptSave() {
        AlertDialogFragment.show(this, ID_DIALOG_PROMPT_SAVE, mSalesPoint.getPresentation(),
                                 getString(R.string.msg_confirm_add_sales_point),
                                 getString(android.R.string.yes), getString(android.R.string.no),
                                 false);
    }

    @Override
    public void onDialogFragmentResult(BaseDialogFragment dialog, Object data) {
        super.onDialogFragmentResult(dialog, data);
        if (dialog instanceof AlertDialogFragment) {
            int id = ((AlertDialogFragment) dialog).getDialogId();
            if (id == ID_DIALOG_PROMPT_SAVE) {
                int which = (Integer) data;
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (isDetailsCorrectlyFilled()) {
                        save();
                    }
                } else {
                    finish();
                }

            }
        }
    }

    private boolean isDetailsCorrectlyFilled() {
        boolean filled = true;
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            BaseSalesPointPage page = mPagerAdapter.getItem(i);
            filled = page.onSaveSalesPoint(mSalesPoint);
            if (!filled) {
                mPager.setCurrentItem(i, true);
                break;
            }
        }
        return filled;
    }

    private void save() {
        try {
            if (mSalesPoint.isModified()) {
                mSalesPointsDao.create(mSalesPoint);
                if (DEBUG) {
                	Dbg.d(TAG, "add new sales point: %s", mSalesPoint.getRef());
                }

                mSalesPoint.setModified(false);
                sendBroadcast(new Intent(Const.ACTION_SALES_POINT_CHANGED));
                finish();
            }
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    public enum PageTab implements IDescription {

        Photo(R.string.page_photo_title),
        Location(R.string.page_location_title),
        Info(R.string.page_info_title);

        private final int mResIdDesc;

        PageTab(int resIdDesc) {
            mResIdDesc = resIdDesc;
        }

        @Override
        public String getDescription() {
            return App.getContext().getString(mResIdDesc);
        }
    }

    private static final class SalesPointPagerAdapter extends FragmentStatePagerAdapter {
        private final int mContainerId;
        private final Map<PageTab, BaseSalesPointPage> mCache;

        private BaseSalesPointPage mCurrentPage;

        public SalesPointPagerAdapter(FragmentManager fm, int containerId) {
            super(fm);
            mContainerId = containerId;
            mCache = new WeakHashMap<PageTab, BaseSalesPointPage>(PageTab.values().length);
        }

        public BaseSalesPointPage getCurrentPage() {
            return mCurrentPage;
        }

        @Override
        public BaseSalesPointPage getItem(int position) {
            PageTab tab = PageTab.values()[position];
            BaseSalesPointPage fragment = mCache.get(tab);
            if (fragment == null) {
                switch (tab) {
                    case Photo:
                        fragment = new PhotoPage();
                        break;
                    case Location:
                        fragment = new LocationPage();
                        break;
                    case Info:
                        fragment = new InfoPage();
                        break;
                    default:
                        throw new IllegalArgumentException("Not supported tab.");
                }
                mCache.put(tab, fragment);
            }
            return fragment;

        }

        @Override
        public int getCount() {
            return PageTab.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            PageTab tab = PageTab.values()[position];
            return tab.getDescription();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentPage = (BaseSalesPointPage) object;
        }

        private String getFragmentName(PageTab tab) {
            return makeFragmentName(mContainerId, tab.ordinal());
        }

        //http://stackoverflow.com/questions/14035090/how-to-get-existing-fragments-when-using-fragmentpageradapter
        private static String makeFragmentName(int viewId, int position) {
            return "android:switcher:" + viewId + ":" + position;
        }

    }
}
