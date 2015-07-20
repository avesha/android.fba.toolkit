package ru.profi1c.engine.report;

import android.content.Context;

import ru.profi1c.engine.R;

/**
 * Простой HTML – отчет, строится по макету из RAW ресурса
 * fba_report_simple.html.
 * <p>
 * Содержит три секции заголовков (эквивалент HTML h4,h5,h6), секцию данных и
 * секцию подвала. Есть возможность задать цвет: фона отчета, теста и
 * заголовков.
 * </p>
 * <strong>Пример:</strong>
 * <p/>
 * <pre>
 * {@code
 *  MyHtmlReport1 myReport = new MyHtmlReport1();
 *  myReport.setBackgroundColor(getResources().getColor(android.R.color.black));
 *  myReport.setHeaderTextColor(getResources().getColor(android.R.color.holo_blue_light));
 *  myReport.setTextColor(getResources().getColor(android.R.color.white));
 *  myReport.setHeader1("Это заголовок отчета");
 *  myReport.setText("Это произвольный текст отчета. Допускается простейшее форматирование, например <strong>жирный текст</strong> и т.п");
 *
 *  и класс отчета:
 *  private static class MyHtmlReport1 extends SimpleHtmlReport{
 *
 *  	public int getResIdIcon() {
 *  		return R.drawable.report_1;
 *  	}
 *
 *  	public int getResIdTitle() {
 *  		return R.string.my_html_report1;
 *  	}
 *
 *  }
 * </pre>
 */
public abstract class SimpleHtmlReport extends BaseSimpleHtmlReport {

    public SimpleHtmlReport() {
        super();
    }

    public void setText(String value) {
        mParams.put(TEXT, value);
    }

    @Override
    public void build(Context context, IReportBuilderResult builderResult) {

        ReportBundle bundle = ReportBundle.fromRaw(context, R.raw.fba_report_simple);
        if (bundle != null) {
            bundle.setParams(mParams);

            String data = bundle.build();
            builderResult.onComplete(data);
        }

    }

}
