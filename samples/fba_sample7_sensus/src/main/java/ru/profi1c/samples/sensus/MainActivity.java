/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBExchangeActivity;
import ru.profi1c.engine.app.FbaFragment;
import ru.profi1c.engine.exchange.ExchangeObserver;
import ru.profi1c.engine.exchange.ExchangeReceiver;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.widget.PresentationAdapter;
import ru.profi1c.samples.sensus.db.CatalogSalesAgents;
import ru.profi1c.samples.sensus.db.CatalogSalesAgentsDao;
import ru.profi1c.samples.sensus.db.CatalogTasks;
import ru.profi1c.samples.sensus.db.CatalogTasksDao;
import ru.profi1c.samples.sensus.db.DBHelper;

/*
 * Эта Activity является основной и будет первой отображаться при запуске
 * приложения.
 *
 * Предупреждение! FBA использует библиотеку 'AppCompat',
 * взаимодействие с панелью действий обрабатывается с помощью функции getSupportActionBar() вместо getActionBar().
 * 
 * @author ООО "Сфера" (support@sfera.ru)
 * 
 */
public class MainActivity extends FbaDBExchangeActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final int ID_NAV_ITEM_SALESPOINT = 1;
    private static final int ID_NAV_ITEM_LOCATION = 2;
    private static final int ID_NAV_ITEM_TASKS = 3;
    private static final int ID_NAV_ITEM_EXCHANGE = 4;
    private static final int ID_NAV_ITEM_SETTINGS = 5;
    private static final int ID_NAV_ITEM_HELP = 6;

    private static final String ACTIVE_FRAGMENT_TAG = "active_fragment";
    private static final String EXTRA_CURRENT_ID_FRAGMENT = "current_id_fragment";
    private static final int MAX_COUNT_FRAGMENTS = 3;

    private CatalogSalesAgents mSalesAgent;
    private CatalogSalesAgentsDao mAgentsDao;

    private CatalogTasksDao mCatalogTasksDao;
    private int mCountAllTask;
    private int mCountNowTask;

    private Drawer mDrawer;

    private FaqReport mFaqReport;

    private HashMap<Integer, BaseFragment> mCacheFragments;
    private int mCurrentIdFragment = Const.NOT_SPECIFIED;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState==null && getFbaActivityNavigation().isMainActivity()){
			onCreateNewSession();
		}
		setContentView(R.layout.activity_main);
        try {
            initData();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new FbaRuntimeException(e);
        }
        initControls();
        buildData(savedInstanceState);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFaqReport!=null){
            mFaqReport.onDestroy();
        }
        clearCacheFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CURRENT_ID_FRAGMENT, mCurrentIdFragment);
    }

    private void initData() throws SQLException {
        mCacheFragments = new HashMap<>(MAX_COUNT_FRAGMENTS);

        DBHelper helper = (DBHelper) getHelper();
        mAgentsDao = helper.getDao(CatalogSalesAgents.class);

        //согласно схеме обмена с 1С, в таблице можеть быть только одна запись
        List<CatalogSalesAgents> lst = mAgentsDao.select();
        if (lst.size() > 0) {
            mSalesAgent = lst.get(0);
        }

        mCatalogTasksDao = helper.getDao(CatalogTasks.class);
        Pair<Integer, Integer> pair = mCatalogTasksDao.getCountTasksInfo();
        mCountAllTask = pair.first;
        mCountNowTask = pair.second;

    }

    private void clearCacheFragments(){
        mCacheFragments.clear();
    }

    private void initControls() {
        initNavDrawer();
    }

    private void initNavDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.nav_drawer_header)
                .withAccountHeader(createAccountHeader())
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.nav_drawer_item_salespoints).withIcon(R.drawable.ic_nav_drawer_salespoint).withIdentifier(ID_NAV_ITEM_SALESPOINT),
                        new PrimaryDrawerItem().withName(R.string.nav_drawer_item_location).withIcon(R.drawable.ic_nav_drawer_location).withIdentifier(ID_NAV_ITEM_LOCATION),
                        new PrimaryDrawerItem().withName(R.string.nav_drawer_item_tasks).withIcon(R.drawable.ic_nav_drawer_tasks).withBadge(formatCountTasks()).withIdentifier(ID_NAV_ITEM_TASKS),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.nav_drawer_item_exchange).withIcon(R.drawable.ic_nav_drawer_exchange).withCheckable(false).withIdentifier(ID_NAV_ITEM_EXCHANGE),
                        new PrimaryDrawerItem().withName(R.string.nav_drawer_item_settings).withIcon(R.drawable.ic_nav_drawer_settings).withCheckable(false).withIdentifier(ID_NAV_ITEM_SETTINGS),
                        new PrimaryDrawerItem().withName(R.string.nav_drawer_item_help).withIcon(R.drawable.ic_nav_drawer_help).withCheckable(false).withIdentifier(ID_NAV_ITEM_HELP)
                        )
                .withOnDrawerItemClickListener(mOnDrawerItemClickListener)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        Utils.hideSoftKeyboard(MainActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View view) {

                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .build();
    }

    private void buildData(Bundle savedInstanceState) {
        int idFragment = ID_NAV_ITEM_SALESPOINT;
        if (savedInstanceState != null) {
            idFragment = savedInstanceState.getInt(EXTRA_CURRENT_ID_FRAGMENT, ID_NAV_ITEM_SALESPOINT);
        }
        mDrawer.setSelectionByIdentifier(idFragment, true);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
	protected ExchangeObserver getExchangeObserver() {
		return null;
	}

	/**
	 * Запущена новая сессия приложения т.н запуск, не поворот экрана
	 */
	private void onCreateNewSession() {

		Context ctx = getApplicationContext();
		
		//восстановить задание обмена в планировщике если требуется
		if(AppHelper.isAppInstalledToSDCard(ctx)) {
			ExchangeReceiver.createSchedulerTasks(ctx);
		}

	}

    private AccountHeader createAccountHeader() {
        AccountHeaderBuilder b = new AccountHeaderBuilder();
        b.withActivity(this)
         .withHeaderBackground(R.mipmap.nav_drawer_header)
         .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
             @Override
             public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                 return false;
             }
         });
        ProfileDrawerItem profile = new ProfileDrawerItem();
        if (mSalesAgent != null) {
            profile.withName(mSalesAgent.getDescription());
            if (!TextUtils.isEmpty(mSalesAgent.email)) {
                profile.withEmail(mSalesAgent.email);
            } else {
                profile.withEmail(mSalesAgent.phone);
            }
            Bitmap photo = mSalesAgent.foto.toBitmap();
            if (photo != null) {
                profile. withIcon(photo);
            }
        } else {
            profile.withName(getString(R.string.nav_mock_account_name))
                   .withEmail(getString(R.string.nav_mock_account_mail))
                   .withIcon(getResources().getDrawable(R.mipmap.ic_mock_contact_picture));
        }

        b.addProfiles(profile);
        return b.build();
    }

    private Drawer.OnDrawerItemClickListener mOnDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id,
                IDrawerItem iDrawerItem) {
            if (DEBUG) {
            	Dbg.d(TAG, "OnDrawerItemClickListener: id = %d", id);
            }
            int identifier = iDrawerItem.getIdentifier();
            switch(identifier){
                case ID_NAV_ITEM_EXCHANGE:
                    doSelectStartExchange();
                    break;
                case ID_NAV_ITEM_SETTINGS:
                    getFbaActivityNavigation().showPreferenceActivity();
                    break;
                case ID_NAV_ITEM_HELP:
                    doShowHelp();
                    break;
                case ID_NAV_ITEM_SALESPOINT:
                case ID_NAV_ITEM_LOCATION:
                case ID_NAV_ITEM_TASKS:
                    selectFragmentItem(identifier);
                    break;
            }
            return false;
        }
    };

    private String formatCountTasks() {
        if (mCountAllTask + mCountNowTask > 0) {
            return String.format("%d/%d", mCountAllTask, mCountNowTask);
        }
        return "";
    }

    /*
    * Интерактивный выбор варианта и запуск обмена
    */
    private void doSelectStartExchange() {

        // адаптер для отображения значений перечислений в диалоге выбора
        PresentationAdapter adapter = new PresentationAdapter(this,
                                                              android.R.layout.simple_spinner_dropdown_item, ExchangeVariant.values());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExchangeVariant variant = ExchangeVariant.values()[which];
                startExchange(variant, true);
                dialog.dismiss();

            }
        });
        builder.setTitle(R.string.exchange_select_variant);
        builder.create().show();

    }

    /*
     * Отобразить краткую справу об использовании программы
     */
    private void doShowHelp() {
        mFaqReport = new FaqReport();
        mFaqReport.onShow(this);
    }

    /*
    * Insert the fragment by replacing any existing fragment
    */
    void replaceMainContent(FbaFragment fragment, boolean stack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!stack) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                               android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_frame, fragment, ACTIVE_FRAGMENT_TAG);
        if (stack) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
        }
        ft.commitAllowingStateLoss();
    }

    protected BaseFragment getActiveFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        return (BaseFragment) fragmentManager.findFragmentByTag(ACTIVE_FRAGMENT_TAG);
    }

    private BaseFragment getFragment(int idFragment) {
        BaseFragment fragment = mCacheFragments.get(idFragment);
        if (fragment == null) {
            fragment = createFragment(idFragment);
            mCacheFragments.put(idFragment, fragment);
        }
        return fragment;
    }

    private BaseFragment createFragment(int idFragment) {

        switch (idFragment) {
            case ID_NAV_ITEM_SALESPOINT:
                return SalesPointFragment.newInstance();
            case ID_NAV_ITEM_LOCATION:
                return LocationFragment.newInstance();
            case ID_NAV_ITEM_TASKS:
                return TasksFragment.newInstance();
            default:
                throw new IllegalArgumentException("Wrong type of 'NavigationAction'.");
        }
    }

    private void selectFragmentItem(int idFragment) {

        if (mCurrentIdFragment != idFragment) {
            if (DEBUG) {
                Dbg.d(TAG, "select action, idFragment = " + idFragment);
            }
            BaseFragment fragment = getFragment(idFragment);
            if (fragment != null) {
                replaceMainContent(fragment, false);
            }
            mCurrentIdFragment = idFragment;
        }
    }

}