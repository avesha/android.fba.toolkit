package com.sample.app2.reports;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.sample.app2.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.report.IReportBuilder;
import ru.profi1c.engine.report.IReportBuilderResult;
import ru.profi1c.engine.report.ReportBundle;
import ru.profi1c.engine.report.SimpleReport;
import ru.profi1c.engine.util.IOHelper;

/**
 * Пример отчета, который использует для построения графиков Google Chart Tools
 * https://google-developers.appspot.com/chart/interactive/docs/quick_start
 * (нужен доступ в интернет)
 */
public class GoogleChartReport extends SimpleReport implements IReportBuilder {

    // Имя шаблона отчета в ресурсах
    private static final String ASSERT_REPORT_NAME = "reports/google_chart_tools.html";
    private static final String GOOGLE_CHART_REPORT_HTML = "GoogleChartReport.html";

    /*
     * Пример установки данных отчета
     */
    public static GoogleChartReport buildTestReport() {
        GoogleChartReport report = new GoogleChartReport();

        report.setTitle("Производительность компании");
        report.addSeries(new ReportSeries(2010, 1012.34, 400));
        report.addSeries(new ReportSeries(2011, 1170.50, 460));
        report.addSeries(new ReportSeries(2012, 660, 1120));
        report.addSeries(new ReportSeries(2013, 1030, 540));
        return report;
    }

    private HashMap<String, Object> mParams;
    private List<ReportSeries> seriess;
    private File mTmpReportFile;

    public GoogleChartReport() {
        mParams = new HashMap<String, Object>();
        seriess = new ArrayList<ReportSeries>();
    }

    /*
     * Установить заголовок для второй диаграммы в отчете
     */
    public void setTitle(String value) {
        mParams.put("chart_line_title", value);
    }

    /*
     * Добавить серию в диаграмму
     */
    public void addSeries(ReportSeries series) {
        seriess.add(series);
    }

    @Override
    public int getResIdIcon() {
        return R.drawable.report_chart;
    }

    @Override
    public int getResIdTitle() {
        return R.string.report_name_google_chart_tools;
    }

    @Override
    public IReportBuilder getReportBuilder() {
        return this;
    }

    @Override
    public void build(Context context, IReportBuilderResult builderResult) {

        ReportBundle bundle = ReportBundle.fromAsset(context, ASSERT_REPORT_NAME);
        if (bundle != null) {
            buildChartLineData();
            bundle.setParams(mParams);

            String data = bundle.build();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                builderResult.onComplete(data);
            } else {
                //WebView.loadDataWithBaseURL() not work if use Google Chart API on Android 5.0.
                //Workaround: write data to local file and open as url
                mTmpReportFile = new File(context.getExternalCacheDir(), GOOGLE_CHART_REPORT_HTML);
                try {
                    IOHelper.writeToFile(data, mTmpReportFile.getAbsolutePath());
                    Uri uri = Uri.fromFile(mTmpReportFile);
                    builderResult.onComplete(uri);
                } catch (IOException e) {
                    Dbg.printStackTrace(e);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTmpReportFile != null) {
            mTmpReportFile.delete();
        }
    }

    /*
         * Серии преобразовать к форматированной строке и установить как значение
         * переменной <!--chart_line_data-->
         */
    private void buildChartLineData() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("['Год', 'Продажи', 'Расходы']");
        for (ReportSeries series : seriess) {
            sb.append(",");
            sb.append(series.toString());
        }
        sb.append("]");
        mParams.put("chart_line_data", sb.toString());
    }

    /*
     * Серия отчета
     */
    public static final class ReportSeries {
        private int year;
        private double sales;
        private double expenses;

        public ReportSeries(int year, double sales, double expenses) {
            this.year = year;
            this.sales = sales;
            this.expenses = expenses;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("'").append(year).append("',");
            sb.append(sales).append(",").append(expenses);
            sb.append("]");
            return sb.toString();
        }
    }
}