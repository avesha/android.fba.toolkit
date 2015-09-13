package ru.profi1c.samples.sensus;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.util.BroadcastManagerHelper;
import ru.profi1c.engine.widget.BaseViewHolder;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.samples.sensus.db.CatalogExtraStorage;
import ru.profi1c.samples.sensus.db.CatalogExtraStorageDao;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.CatalogSalesPointsDao;
import ru.profi1c.samples.sensus.db.DBHelper;
import ru.profi1c.samples.sensus.util.MyRecyclerScroll;
import ru.profi1c.samples.sensus.wizard.AddSalesPointActivity;

public class SalesPointFragment extends BaseFragment implements BroadcastManagerHelper.OnReceiveListener {
    private static final String TAG = SalesPointFragment.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private Random mRnd;
    private int[] mRndColors;
    private Animation mAnimationFab;

    private CatalogSalesPointsDao mSalesPointDao;
    private CatalogExtraStorageDao mExtraStorageDao;

    private RecyclerView mRecyclerView;
    private MetaArrayRecyclerAdapter mAdapter;

    private StaggeredGridLayoutManager mLayoutManager;
    //TODO: Для планшетов можно увеличить количество колонок
    private final int mCountColumnsInGrid = 2;
    private final int mCountColumnsInList = 1;
    private FloatingActionButton mFab;
    private int mFabMargin;
    private View mEmptyHolder;
    private MetaAdapterViewBinder mAdapterViewBinder;
    private int mCurrPosition = Const.NOT_SPECIFIED;

    private MenuItem mMenuSwitchStyle;

    private BroadcastManagerHelper mBroadcastManagerHelper;

    public static SalesPointFragment newInstance() {
        return new SalesPointFragment();
    }

    private static void openAddSalesPointWizard(Context context) {
        context.startActivity(AddSalesPointActivity.getStartIntent(context));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRnd = new Random();
        mRndColors = getResources().getIntArray(R.array.random_colors);
        mAnimationFab = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_grow);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.fab_base_margin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_salespoints, container, false);
        initControls(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.nav_drawer_item_salespoints);
        try {
            inflateDate();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_sales_point, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mMenuSwitchStyle  = menu.findItem(R.id.action_switch_style);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if(id == R.id.action_switch_style) {
            item.setChecked(!item.isChecked());
            boolean asGrid = item.isChecked();
            item.setIcon(asGrid? R.mipmap.ab_ic_view_agenda: R.mipmap.ab_ic_view_dashboard);
            switchGridStyle(asGrid);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initControls(View root) {
        final Context context = root.getContext();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new StaggeredGridLayoutManager(mCountColumnsInList, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                mFab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2))
                    .start();
            }

            @Override
            public void hide() {
                mFab.animate().translationY(mFab.getHeight() + mFabMargin)
                    .setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });

        mEmptyHolder = root.findViewById(R.id.llEmptyHolder);

        mFab = (FloatingActionButton) root.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onFabButtonClick();
            }
        });
        mFab.startAnimation(mAnimationFab);

        mAdapterViewBinder = new MetaAdapterViewBinder(getActivity(), CatalogSalesPoints.class,
                                                       new String[]{
                                                               CatalogSalesPoints.FIELD_NAME_DESCRIPTION,
                                                               CatalogSalesPoints.FIELD_NAME_ADDRESS,
                                                               CatalogSalesPoints.FIELD_NAME_FOTO},
                                                       new int[]{R.id.tvDescription, R.id.tvAddress,
                                                               R.id.ivPhoto});
        mAdapterViewBinder.setViewBinder(mRowBinder);
    }

    private void inflateDate() throws SQLException {
        DBHelper helper = (DBHelper) getHelper();
        mSalesPointDao = helper.getDao(CatalogSalesPoints.class);
        mExtraStorageDao = helper.getDao(CatalogExtraStorage.class);

        List<CatalogSalesPoints> lst = mSalesPointDao.selectRoute(true);

        mAdapter = new MetaArrayRecyclerAdapter(lst, mAdapterViewBinder, R.layout.row_sales_point);
        mRecyclerView.setAdapter(mAdapter);
        mEmptyHolder.setVisibility(lst.size() > 0 ? View.GONE : View.VISIBLE);

        // global message handler
        mBroadcastManagerHelper = new BroadcastManagerHelper.Builder(getActivity())
                .addAction(Const.ACTION_SALES_POINT_CHANGED)
                .setListener(this).create();
        mBroadcastManagerHelper.registerReceiver();
    }

    private void refreshAdapter() throws SQLException {

        List<CatalogSalesPoints> lst =  mSalesPointDao.selectRoute(true);
        mAdapter.setData(lst);
        mEmptyHolder.setVisibility(lst.size() > 0 ? View.GONE : View.VISIBLE);

        if (mCurrPosition != Const.NOT_SPECIFIED) {
            mRecyclerView.getAdapter().notifyItemChanged(mCurrPosition);
            mCurrPosition = Const.NOT_SPECIFIED;
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void performShowSalesPointInWithTransition(CatalogSalesPoints salesPoint, View view) {
        final Activity activity = getActivity();
        Intent intent = SalesPointActivity
                .getStartIntent(activity, salesPoint, ((ColorDrawable) view.getBackground()).getColor(), false);
        String transitionName = getString(R.string.transition_sales_point_photo);
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, transitionName);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void onFabButtonClick() {
        if (DEBUG) {
        	Dbg.d(TAG, "onFabButtonClick");
        }
        final Activity activity = getActivity();
        if(AppSettings.isShowAddPointRules(activity)) {
            showAddPointActivityRules(activity);
        } else {
            openAddSalesPointWizard(activity);
        }
    }

    private void switchGridStyle(boolean asGrid) {
        mLayoutManager.setSpanCount(asGrid ? mCountColumnsInGrid : mCountColumnsInList);
    }

    private RowViewHolder.OnRowViewClickListener mOnRowViewClickListener = new RowViewHolder.OnRowViewClickListener() {
        @Override
        public void onRowViewClick(View view, int position) {
            if (DEBUG) {
                Dbg.d(TAG, "onRowViewClick, position = %d", position);
            }
            mCurrPosition = position;
            CatalogSalesPoints salesPoint = ((MetaArrayRecyclerAdapter) mRecyclerView.getAdapter()).getItem(
                    position);
            performShowSalesPointInWithTransition(salesPoint,  view.findViewById(R.id.ivPhoto));
        }
    };

    private MetaAdapterViewBinder.ViewBinder mRowBinder = new MetaAdapterViewBinder.ViewBinder() {

        @Override
        public BaseViewHolder createViewHolder(View root) {
            RowViewHolder holder = new RowViewHolder(root, mOnRowViewClickListener);
            return holder;
        }

        @Override
        public void onBind(BaseViewHolder viewHolder, int position) {
            ((RowViewHolder) viewHolder).position = position;
        }

        @Override
        public boolean setViewValue(View view, Object item, Field field) {
            CatalogSalesPoints salesPoint = (CatalogSalesPoints) item;
            final String name = field.getName();
            if (CatalogSalesPoints.FIELD_NAME_FOTO.equals(name)) {

                boolean setPhoto = false;
                CatalogExtraStorage extraStorage = salesPoint.foto;
                if (!CatalogExtraStorage.isEmpty(extraStorage)) {
                    Bitmap bmp = extraStorage.storage.toBitmap();
                    if (bmp != null) {
                        ((ImageView) view).setImageBitmap(bmp);
                        ((ImageView) view).setScaleType(ImageView.ScaleType.CENTER_CROP);
                        setPhoto = true;
                    }
                }

                if(!setPhoto){
                    ((ImageView) view).setImageResource(R.mipmap.ic_shopping_cart_white);
                    ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                    view.setBackgroundColor(mRndColors[mRnd.nextInt(mRndColors.length - 1)]);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean setViewValue(View view, Cursor cursor, Field field) {
            return false;
        }
    };

    @Override
    public void onReceive(String action, Intent data) {
        if (Const.ACTION_SALES_POINT_CHANGED.equals(action)) {
            try {
                if (getActivity() != null) {
                    refreshAdapter();
                }
            } catch (SQLException e) {
                throw new FbaRuntimeException(e);
            }
        }
    }

    private void showAddPointActivityRules(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_add_sales_point);

        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.dlg_before_add_salespoint, null);
        final CheckBox cbDontAsk = (CheckBox) view.findViewById(R.id.cbDontAsk);
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cbDontAsk.isChecked()) {
                    AppSettings.setPrefShowAppPointRiles(context, false);
                }
                openAddSalesPointWizard(context);
            }
        });
        builder.create().show();
    }

    /*
     * ViewHolder паттерн для эффективной работы со строками адаптеров
     */
    public static class RowViewHolder extends BaseViewHolder implements View.OnClickListener {

        private final OnRowViewClickListener mOnRowViewClickListener;

        private CardView cvCard;
        private TextView tvDescription;
        private TextView tvAddress;
        private ImageView ivPhoto;

        int position;

        public RowViewHolder(View root, OnRowViewClickListener listener) {
            super(root);
            mOnRowViewClickListener = listener;
            cvCard = (CardView) root.findViewById(R.id.cvCard);
            cvCard.setOnClickListener(this);
            tvDescription = (TextView) root.findViewById(R.id.tvDescription);
            tvAddress = (TextView) root.findViewById(R.id.tvAddress);
            ivPhoto = (ImageView) root.findViewById(R.id.ivPhoto);
        }

        @Override
        public View getViewById(int id) {
            switch (id) {
                case R.id.tvDescription:
                    return tvDescription;
                case R.id.tvAddress:
                    return tvAddress;
                case R.id.ivPhoto:
                    return ivPhoto;
            }
            return null;
        }

        @Override
        public void onClick(View view) {
            if(mOnRowViewClickListener!=null){
                mOnRowViewClickListener.onRowViewClick(view, position);
            }
        }

        public interface OnRowViewClickListener {
            void onRowViewClick(View view, int position);
        }
    }

    /*
     * Обертка – адаптер для использования RecyclerView вместо ListView
     */
    private static class MetaArrayRecyclerAdapter
            extends RecyclerView.Adapter<MetaArrayRecyclerAdapter.ViewHolder> {

        private List<CatalogSalesPoints> mData;
        private final MetaAdapterViewBinder mViewBinder;
        private final int mResIdLayout;

        MetaArrayRecyclerAdapter(List<CatalogSalesPoints> data, MetaAdapterViewBinder viewBinder,
                int resIdLayout) {
            mData = data;
            mViewBinder = viewBinder;
            mResIdLayout = resIdLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mViewBinder.newView(-1, null, parent, mResIdLayout);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            mViewBinder.bindView(position, mData.get(position), viewHolder.itemView);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public CatalogSalesPoints getItem(int position) {
            return mData.get(position);
        }

        public void setData(List<CatalogSalesPoints> lst) {
            mData = lst;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View root) {
                super(root);
            }
        }
    }
}
