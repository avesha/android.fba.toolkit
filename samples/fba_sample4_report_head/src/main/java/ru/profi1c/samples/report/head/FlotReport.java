package ru.profi1c.samples.report.head;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.Random;

import ru.profi1c.engine.report.IReportBuilder;
import ru.profi1c.engine.report.IReportBuilderResult;
import ru.profi1c.engine.report.ReportBundle;
import ru.profi1c.engine.report.SimpleReport;

/**
 * Пример отчета, который использует для построения графиков FLOT - JavaScript
 * plotting library for jQuery http://www.flotcharts.org/ (НЕ нужен доступ в
 * интернет, библиотека добавлена в ресурсы )
 */
public class FlotReport extends SimpleReport implements IReportBuilder {
    private static final String TAG = "PlotRealtimeReport";

    // Имя шаблона отчета в ресурсах
    private static final String ASSERT_REPORT_NAME = "reports/jquery_flot.html";

    // Поставщик данных для JavaScript
    @SuppressWarnings("unused")
    private DataBroker dataBroker;

    @Override
    public int getResIdIcon() {
        return R.mipmap.report_04;
    }

    @Override
    public int getResIdTitle() {
        return R.string.report_name_plot_jquery;
    }

    @Override
    public IReportBuilder getReportBuilder() {
        return this;
    }

    @Override
    public void build(Context context, IReportBuilderResult builderResult) {
        // Методы этого объекта будут доступны из JavaScript:
        // windows.plotDataBroker.getChartData()
        dataBroker = new DataBroker();
        addJavascriptInterface(new DataBroker(), "plotDataBroker");

        ReportBundle bundle = ReportBundle.fromAsset(context, ASSERT_REPORT_NAME);
        String data = bundle.build();
        builderResult.onComplete(data);
    }

    @Override
    public void onDestroy() {
        dataBroker = null;
        super.onDestroy();
    }

    /*
     * Поставщик данных для JavaScript
     */
    public class DataBroker {

        @JavascriptInterface
        public String getChartData() {
            Log.i(TAG, "DataBroker.getChartData");
            StringBuilder sb = new StringBuilder("[");

            Random rnd = new Random();
            for (int i = 0; i < 10; i++) {
                sb.append("[").append(i).append(",").append(rnd.nextInt(10)).append("],");
            }

            sb.setLength(sb.length() - 1);
            sb.append("]");

            return sb.toString();

        }
    }
}
