package ru.profi1c.engine.report;

import android.content.Context;

import java.util.Map;

import ru.profi1c.engine.R;
import ru.profi1c.engine.widget.FieldFormatter;

/**
 * Простой HTML-отчет в виде таблицы с двумя колонками, строится по макету RAW
 * ресурса fba_report_simple_map.html. Содержит три секции заголовков
 * (эквивалент HTML h4,h5,h6), секцию таблицы и секцию подвала. Есть возможность
 * задать цвет: фона отчета, теста и заголовков. </p> <strong>Пример:</strong>
 * <p/>
 * <pre>
 * {@code
 * MyMapHtmlReport myMapReport = new MyMapHtmlReport();
 * myMapReport.setHeader2("Наличие товаров на складах");
 * myMapReport.setHeader3("На дату: " + DateHelper.formatShortDate(new Date(System.currentTimeMillis())));
 * myMapReport.setTableHeader("Товар", "Количество");
 *
 * Map<Object,Object> mapData = new LinkedHashMap<Object, Object>();
 * mapData.put("Женские босоножки", 2d);
 * mapData.put("Ботинки женские натуральная кожа", 100.00);
 * mapData.put("Ботинки женские демисезонные", 123.45);
 * mapData.put("-------------------", "-------");
 * mapData.put("Комбайн кухонный BINATONE FP 67", 0d);
 * mapData.put("Кофеварка BRAUN KF22R", 3d);
 *
 * myMapReport.setTableData(mapData);
 * myMapReport.show(this);
 *
 * и класс отчета:
 * private static class MyMapHtmlReport extends SimpleMapHtmlReport{
 *
 * 	public int getResIdIcon() {
 * 		return R.drawable.report_2;
 * 	}
 *
 * 	public int getResIdTitle() {
 * 		return R.string.my_html_map_report;
 * 	}
 *
 * }
 * </pre>
 */
public abstract class SimpleMapHtmlReport extends BaseSimpleHtmlReport {

    private static final String TABLE_ROW_TEMPLATE =
            "<tr><td class=\"value\"><!--table_value_1--></td>" +
            "<td class=\"value\"><!--table_value_2--></td></tr>";

    public static final int DEF_TABLE_BORDER_COLOR = 0xCCCCCC;
    public static final int DEF_TABLE_HEADER_BACKGROUND_COLOR = 0xf8f8f8;

    public static final String TABLE_BORDER_COLOR = "table_border_color";
    public static final String TABLE_HEADER_BACKGROUND_COLOR = "table_header_background_color";
    public static final String TABLE_HEADER_VALUE_1 = "table_header_value_1";
    public static final String TABLE_HEADER_VALUE_2 = "table_header_value_2";
    public static final String TABLE_ROWS = "table_rows";
    public static final String TABLE_VALUE_1 = "table_value_1";
    public static final String TABLE_VALUE_2 = "table_value_2";

    private Map<Object, Object> mTableData;
    private FieldFormatter mFormatter;

    public SimpleMapHtmlReport() {
        super();
        setTableBorderColor(DEF_TABLE_BORDER_COLOR);
        setTableHeaderBackgroundColor(DEF_TABLE_HEADER_BACKGROUND_COLOR);
    }

    public void setFieldFormatter(FieldFormatter fieldFormatter) {
        this.mFormatter = fieldFormatter;
    }

    public void setTableBorderColor(int color) {
        setColorParam(TABLE_BORDER_COLOR, color);
    }

    public void setTableHeaderBackgroundColor(int color) {
        setColorParam(TABLE_HEADER_BACKGROUND_COLOR, color);
    }

    public void setTableHeader(String header1, String header2) {
        mParams.put(TABLE_HEADER_VALUE_1, header1);
        mParams.put(TABLE_HEADER_VALUE_2, header2);
    }

    public void setTableData(Map<Object, Object> data) {
        mTableData = data;
    }

    @Override
    public void build(Context context, IReportBuilderResult builderResult) {

        ReportBundle bundle = ReportBundle.fromRaw(context, R.raw.fba_report_simple_map);
        if (bundle != null) {

            bundle.setParams(mParams);

            if (mTableData != null) {
                String tableRows = buildTableRowsSegment();
                bundle.putParam(TABLE_ROWS, tableRows);
            }

            String data = bundle.build();
            builderResult.onComplete(data);

        }

    }

    private String buildTableRowsSegment() {

        ReportBundle bundle = new ReportBundle(TABLE_ROW_TEMPLATE);
        if (mFormatter != null) {
            bundle.setFieldFormatter(mFormatter);
        }

        StringBuilder sb = new StringBuilder();

        for (Object key : mTableData.keySet()) {
            Object value = mTableData.get(key);

            bundle.putParam(TABLE_VALUE_1, key);
            bundle.putParam(TABLE_VALUE_2, value);

            sb.append(bundle.build());
        }
        return sb.toString();
    }

}
