package ru.profi1c.samples.report.head;

import android.content.Context;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.profi1c.engine.report.IReportBuilderResult;
import ru.profi1c.engine.report.SimpleMapHtmlReport;
import ru.profi1c.samples.report.head.db.DBHelper;
import ru.profi1c.samples.report.head.db.ExTableRaportRukovoditelyu;
import ru.profi1c.samples.report.head.db.ExTableRaportRukovoditelyuDao;

/**
 * Пример построения отчета по данным из локальной базы sqlite. Источником
 * данных выступает внешняя таблица «Рапорт руководителю»
 */
public class ToChiefReport extends SimpleMapHtmlReport {

    @Override
    public int getResIdIcon() {
        return R.mipmap.report_01;
    }

    @Override
    public int getResIdTitle() {
        return R.string.report_name_to_chief;
    }

    @Override
    public void build(Context context, IReportBuilderResult builderResult) {

        try {
            makeReport(context);
        } catch (SQLException e) {
            e.printStackTrace();
            setHeader1("Упс, отчет не создан!");
        }

        super.build(context, builderResult);
    }

    private void makeReport(Context context) throws SQLException {

        DBHelper helper = new DBHelper(context);

        // Заголовок отчета и таблицы
        setHeader2("Финансовые показатели");
        setTableHeader("Наименование ", "Сумма, руб.");

        // Строки таблицы будут в порядке добавления
        Map<Object, Object> mapData = new LinkedHashMap<Object, Object>();

        // Выборка данных из локальной базы данных sqlite, внешняя таблица
        // «Рапорт руководителю»
        ExTableRaportRukovoditelyuDao dao = helper.getDao(ExTableRaportRukovoditelyu.class);
        List<ExTableRaportRukovoditelyu> rows = dao.select();
        for (ExTableRaportRukovoditelyu row : rows) {
            mapData.put(row.pokazatel, row.znachenie);
        }
        setTableData(mapData);

    }


}
