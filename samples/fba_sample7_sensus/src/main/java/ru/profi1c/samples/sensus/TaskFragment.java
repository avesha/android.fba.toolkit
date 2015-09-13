package ru.profi1c.samples.sensus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Date;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.SimpleCatalogFragment;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.CatalogTasks;
import ru.profi1c.samples.sensus.db.EnumTaskStatuses;
import ru.profi1c.samples.sensus.util.CalendarHelper;

public class TaskFragment extends SimpleCatalogFragment<CatalogTasks> {

    private TableRow mTrSalesPoint;
    private Button mBtnInWork;
    private Button mBtnCompleted;
    private TextView mTvImportance;
    private FieldEditText mEtComment;

    public static TaskFragment newInstance(Ref ref) {
        TaskFragment fragment = new TaskFragment();
        if (ref != null) {
            fragment.setArguments(TaskFragment.toBundle(ref));
        }
        return fragment;
    }

    @Override
    protected int getResIdLayout() {
        return R.layout.fragment_task;
    }

    @Override
    protected String[] getFields() {
        return new String[]{CatalogTasks.FIELD_NAME_DESCRIPTION,
                CatalogTasks.FIELD_NAME_SALES_POINT, CatalogTasks.FIELD_NAME_IMPORTANCE,
                CatalogTasks.FIELD_NAME_DATE_BEGIN, CatalogTasks.FIELD_NAME_DATE_COMPLETION,
                CatalogTasks.FIELD_NAME_COMMENT};
    }

    @Override
    protected int[] getIds() {
        return new int[]{R.id.tvDesc, R.id.tvSalesPint, R.id.tvImportance, R.id.tvDateBegin,
                R.id.tvDateCompletion, R.id.etComment};
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FieldFormatter ff = new FieldFormatter.Builder().setDateFormat("dd MMM")
                                                        .setDateTimeFormat("dd MMM HH:mm").create();
        setFieldFormatter(ff);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        initControls(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.nav_drawer_item_task);
        inflateData();
    }

    @Override
    public void onDestroy() {
        doSaveTask();
        super.onDestroy();
    }

    private void initControls(View root) {
        mBtnInWork = (Button) root.findViewById(R.id.btnInWork);
        mBtnInWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogTasks task = getObject();
                task.status = EnumTaskStatuses.InWork;
                task.setModified(true);
                refreshStatusControl();
            }
        });

        mBtnCompleted = (Button) root.findViewById(R.id.btnCompleted);
        mBtnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogTasks task = getObject();
                task.status = EnumTaskStatuses.Completed;
                task.dateCompletion = new Date(System.currentTimeMillis());
                task.setModified(true);
                //rebuild controls
                getObjectView().build(task, getHelper(), getFields(), getIds());
                refreshStatusControl();
            }
        });

        Button btn = (Button) root.findViewById(R.id.btnInCalendar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarHelper.promptFromTask(getActivity(), getObject());
            }
        });

        mTvImportance = (TextView) root.findViewById(R.id.tvImportance);
        mEtComment = (FieldEditText) root.findViewById(R.id.etComment);
        mTrSalesPoint = (TableRow) root.findViewById(R.id.trSalesPoint);
    }

    private void inflateData() {
        CatalogTasks task = getObject();
        inflateViewImportance();
        refreshStatusControl();
        mTrSalesPoint.setVisibility(CatalogSalesPoints.isEmpty(task.salesPoint)? View.GONE: View.VISIBLE);
        task.setModified(false);
    }

    private void inflateViewImportance() {
        CatalogTasks task = getObject();
        int idResColor = R.color.primary_text;
        switch (task.importance) {
            case High:
                idResColor = R.color.task_high_importance;
                break;
            case Midle:
                idResColor = R.color.task_midle_importance;
        }
        mTvImportance.setTextColor(getResources().getColor(idResColor));
    }

    private void refreshStatusControl() {
        CatalogTasks task = getObject();
        mBtnInWork.setEnabled(task.status == EnumTaskStatuses.Appointed);
        mBtnCompleted.setEnabled(task.status == EnumTaskStatuses.InWork);
        mEtComment.setEnabled(task.status == EnumTaskStatuses.Appointed ||
                              task.status == EnumTaskStatuses.InWork);
    }

    private void doSaveTask() {
        try {
            CatalogTasks task = getObject();
            if (task !=null && task.isModified()) {
                save();
                Activity activity = getActivity();
                if(activity!=null){
                    activity.sendBroadcast(new Intent(Const.ACTION_TASK_CHANGED));
                }
            }
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }
}
