package ru.profi1c.samples.sensus;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDialogFragment;
import ru.profi1c.engine.util.BroadcastManagerHelper;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.widget.BaseViewHolder;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.engine.widget.MetaAdapterViewBinder;
import ru.profi1c.engine.widget.MetaArrayAdapter;
import ru.profi1c.samples.sensus.db.CatalogTasks;
import ru.profi1c.samples.sensus.db.CatalogTasksDao;
import ru.profi1c.samples.sensus.db.DBHelper;
import ru.profi1c.samples.sensus.db.EnumImportance;

public class TasksFragment extends BaseFragment
        implements BroadcastManagerHelper.OnReceiveListener, DatePickerDialog.OnDateSetListener {

    private ListView mListView;
    private View mEmptyHolder;
    private MetaAdapterViewBinder mAdapterViewBinder;

    private CatalogTasksDao mCatalogTasksDao;
    private List<CatalogTasks> mTasks;
    private Date mDate;

    private BroadcastManagerHelper mBroadcastManagerHelper;

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        initControls(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            inflateDate();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_drawer_item_tasks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastManagerHelper != null) {
            mBroadcastManagerHelper.unregisterReceiver();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_tasks, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_select_date) {
            DatePickerFragment.select(getFragmentManager(), this,
                                      mDate == null ? new Date(System.currentTimeMillis()) : mDate);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initControls(View root) {
        mListView = (ListView) root.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MetaArrayAdapter<CatalogTasks> maa =
                        (MetaArrayAdapter<CatalogTasks>) parent.getAdapter();
                CatalogTasks task = maa.getItem(position);
                TaskFragment fragment = TaskFragment.newInstance(task);
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.replaceMainContent(fragment, true);
                }
            }
        });
        mEmptyHolder = root.findViewById(R.id.llEmptyHolder);

        // Кастомный построитель и форматтер для элементов строки
        FieldFormatter ff = new FieldFormatter.Builder().setDateFormat("dd MMM").create();
        mAdapterViewBinder = new MetaAdapterViewBinder(getActivity(), CatalogTasks.class,
                                                       new String[]{
                                                               CatalogTasks.FIELD_NAME_DESCRIPTION,
                                                               CatalogTasks.FIELD_NAME_SALES_POINT,
                                                               CatalogTasks.FIELD_NAME_DATE_BEGIN},
                                                       new int[]{R.id.tvDesc, R.id.tvSalesPoint,
                                                               R.id.tvDateBegin});
        mAdapterViewBinder.setViewBinder(mRowBinder);
        mAdapterViewBinder.setFieldFormatter(ff);
    }

    private void inflateDate() throws SQLException {
        DBHelper helper = (DBHelper) getHelper();
        mCatalogTasksDao = helper.getDao(CatalogTasks.class);
        selectTasks();

        // global message handler
        mBroadcastManagerHelper = new BroadcastManagerHelper.Builder(getActivity())
                .addAction(Const.ACTION_TASK_CHANGED).setListener(this).create();
        mBroadcastManagerHelper.registerReceiver();

    }

    private void selectTasks() throws SQLException {
        HashMap<String, Object> filter = null;
        if (mDate != null) {
            filter = new HashMap<>();
            filter.put(CatalogTasks.FIELD_NAME_DATE_BEGIN, mDate);
        }
        mTasks = mCatalogTasksDao.select(filter, CatalogTasks.FIELD_NAME_DATE_BEGIN);
        mEmptyHolder.setVisibility(mTasks.size() > 0 ? View.GONE : View.VISIBLE);
        refreshAdapter();
    }

    private void refreshAdapter() {
        MetaArrayAdapter<CatalogTasks> maa =
                new MetaArrayAdapter<CatalogTasks>(mTasks, R.layout.row_taks_item,
                                                   mAdapterViewBinder);
        mListView.setAdapter(maa);
    }

    private MetaAdapterViewBinder.ViewBinder mRowBinder = new MetaAdapterViewBinder.ViewBinder() {

        @Override
        public BaseViewHolder createViewHolder(View root) {
            return new RowViewHolder(root);
        }

        @Override
        public void onBind(BaseViewHolder viewHolder, int position) {

        }

        @Override
        public boolean setViewValue(View view, Object item, Field field) {
            CatalogTasks task = (CatalogTasks) item;
            final String name = field.getName();

            if (CatalogTasks.FIELD_NAME_DESCRIPTION.equals(name)) {
                ((TextView) view).setTextColor(getResources().getColor(task.importance ==
                                                                       EnumImportance.High ? R.color.task_high_importance : R.color.primary_text));

            } else if (CatalogTasks.FIELD_NAME_DATE_BEGIN.equals(name)) {
                int idResColor = android.R.color.transparent;
                switch (task.status) {
                    case Appointed:
                        idResColor = R.color.color_blue;
                        break;
                    case InWork:
                        idResColor = R.color.color_green;
                        break;
                    case Completed:
                        idResColor = R.color.color_red;
                        break;
                    case Closed:
                        idResColor = R.color.color_gray;
                        break;
                }
                ((TextView) view).setBackgroundColor(getResources().getColor(idResColor));
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
        if (Const.ACTION_TASK_CHANGED.equals(action)) {
            try {
                if (getActivity() != null) {
                    selectTasks();
                }
            } catch (SQLException e) {
                throw new FbaRuntimeException(e);
            }
        }
    }

    /*
      * ViewHolder паттерн для эффективной работы со строками адаптеров
      */
    private static class RowViewHolder extends BaseViewHolder {

        private TextView mDesc;
        private TextView mSalesPoint;
        private TextView mDateBegin;

        public RowViewHolder(View root) {
            super(root);
            mDesc = (TextView) root.findViewById(R.id.tvDesc);
            mSalesPoint = (TextView) root.findViewById(R.id.tvSalesPoint);
            mDateBegin = (TextView) root.findViewById(R.id.tvDateBegin);
        }

        @Override
        public View getViewById(int id) {
            switch (id) {
                case R.id.tvDesc:
                    return mDesc;
                case R.id.tvSalesPoint:
                    return mSalesPoint;
                case R.id.tvDateBegin:
                    return mDateBegin;
            }
            return null;
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDate = DateHelper.date(year, month, day);
        try {
            selectTasks();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    public static final class DatePickerFragment extends FbaDialogFragment {

        private static final String EXTRA_DEFAULT_DATE = "default_date";
        private static final String FRAGMENT_TAG = "datePicker";

        public static void select(FragmentManager fm, TasksFragment targetFragment, Date date) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setTargetFragment(targetFragment,0);
            Bundle args = new Bundle();
            args.putLong(EXTRA_DEFAULT_DATE, date.getTime());
            fragment.setArguments(args);
            fragment.show(fm, FRAGMENT_TAG);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            long ms = getArguments().getLong(EXTRA_DEFAULT_DATE);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ms);
            return new DatePickerDialog(getActivity(),
                                        (DatePickerDialog.OnDateSetListener) getTargetFragment(),
                                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH));
        }

    }

}
