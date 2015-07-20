package com.sample.app2.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sample.app2.R;
import com.sample.app2.test.TestUtils;
import com.sample.app2.test_action.ITestAction;
import com.sample.app2.test_action.ITestActionProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.report.IReport;
import ru.profi1c.engine.report.ReportListAdapter;
import ru.profi1c.engine.report.SimpleCompiledHtmlReport;
import ru.profi1c.engine.report.SimpleHtmlReport;
import ru.profi1c.engine.report.SimpleMapHtmlReport;
import ru.profi1c.engine.util.DateHelper;

public class TAReports extends FbaActivity {

    private static final String REPORT_NAME_PDF = "test_report.pdf";
    private static final String ASSET_REPORT_NAME_PDF = "large_data/" + REPORT_NAME_PDF;
    private static final String ASSET_REPORT_NAME_HTML = "reports/report3.html";

    private ListView mList;
    private File mFilePdfReport;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAReports.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ta_reports);
        try {
            init();
        } catch (IOException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void init() throws IOException {
        FbaApplication app = FbaApplication.from(this);
        File file = new File(app.getExternalCacheDir(), REPORT_NAME_PDF);
        mFilePdfReport =
                TestUtils.extractAssert(this, ASSET_REPORT_NAME_PDF, file.getAbsolutePath());
        if (mFilePdfReport == null) {
            throw new NullPointerException("Error extract pfd sample report");
        }

        mList = (ListView) findViewById(android.R.id.list);
        ReportListAdapter adapter = new ReportListAdapter(this, createReportList());
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                IReport report = (IReport) mList.getItemAtPosition(position);
                report.onShow(TAReports.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ReportListAdapter adapter = (ReportListAdapter) mList.getAdapter();
        if (adapter != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                adapter.getItem(i).onDestroy();
            }
        }

    }

    private List<IReport> createReportList() {
        List<IReport> lst = new ArrayList<IReport>();

        IReport report = new PdfReport(mFilePdfReport);
        lst.add(report);

        report = new ReportNotFound(new File("/mnt/sdcard/not_exists_report2.html"));
        lst.add(report);

        report = new ReportInnerHtml(ASSET_REPORT_NAME_HTML);
        lst.add(report);

        MyHtmlReport1 myReport = new MyHtmlReport1();
        myReport.setBackgroundColor(getResources().getColor(android.R.color.black));
        myReport.setHeaderTextColor(getResources().getColor(android.R.color.holo_blue_light));
        myReport.setTextColor(getResources().getColor(android.R.color.white));
        myReport.setHeader1("Это заголовок отчета");
        myReport.setText(
                "Это произвольный текст отчета. Допускается простейшее форматирование, например <strong>жирный текст</strong> и т.п");
        lst.add(myReport);

        MyMapHtmlReport myMapReport = new MyMapHtmlReport();
        myMapReport.setHeaderTextColor(getResources().getColor(android.R.color.holo_blue_light));
        myMapReport.setHeader2("Наличие товаров на складах");
        myMapReport.setHeader3(
                "На дату: " + DateHelper.formatShortDate(new Date(System.currentTimeMillis())));
        myMapReport.setTableHeader("Товар", "Количество");

        Map<Object, Object> mapData = new LinkedHashMap<Object, Object>();
        mapData.put("Женские босоножки", 2d);
        mapData.put("Ботинки женские натуральная кожа", 100.00);
        mapData.put("Ботинки женские демисезонные", 123.45);
        mapData.put("-------------------", "-------");
        mapData.put("Комбайн кухонный BINATONE FP 67", 0d);
        mapData.put("Кофеварка BRAUN KF22R", 3d);

        myMapReport.setTableData(mapData);
        lst.add(myMapReport);

        lst.add(GoogleChartReport.buildTestReport());

        return lst;
    }

    private static class ReportNotFound extends SimpleCompiledHtmlReport {

        public ReportNotFound(File file) {
            super(file);
        }

        @Override
        public int getResIdIcon() {
            return R.drawable.report_2;
        }

        @Override
        public int getResIdTitle() {
            return R.string.report_not_found_source;
        }

    }

    private static class ReportInnerHtml extends SimpleCompiledHtmlReport {

        public ReportInnerHtml(String pathToAsset) {
            super(pathToAsset);
        }

        @Override
        public int getResIdIcon() {
            return R.drawable.report_3;
        }

        @Override
        public int getResIdTitle() {
            return R.string.report_inner_file_source;
        }

    }

    private static class MyHtmlReport1 extends SimpleHtmlReport {

        @Override
        public int getResIdIcon() {
            return R.drawable.report_1;
        }

        @Override
        public int getResIdTitle() {
            return R.string.report_simple_html;
        }

    }

    private static class MyMapHtmlReport extends SimpleMapHtmlReport {

        @Override
        public int getResIdIcon() {
            return R.drawable.report_2;
        }

        @Override
        public int getResIdTitle() {
            return R.string.report_html_map_report;
        }

    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "Show list of reports";

        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }

    public static class TAPReports implements ITestActionProvider {

        public static final String DESCRIPTION = "Reports";

        @Override
        public List<ITestAction> getActions() {
            List<ITestAction> lst = new ArrayList<>();
            lst.add(new TestActionShow());
            return Collections.unmodifiableList(lst);
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
