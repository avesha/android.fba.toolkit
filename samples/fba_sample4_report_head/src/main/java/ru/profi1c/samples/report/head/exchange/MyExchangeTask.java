package ru.profi1c.samples.report.head.exchange;

import java.io.File;

import ru.profi1c.engine.exchange.ExchangeStrategy;
import ru.profi1c.engine.exchange.ExchangeTask;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.samples.report.head.ProductsInStokReport;

/**
 * Стандартная процедура обмена дополненная пользовательскими правилами.
 * Дополнительно получается один скомпилированный файл – отчет.
 */
public class MyExchangeTask extends ExchangeTask {

    // Идентификатор отчета как он задан в 1С
    private static final String ID_REPORT_PRODUCTS_IN_STOK = "REPORT_PRODUCTS_IN_STOK";

    public MyExchangeTask(ExchangeVariant exchangeVariant, ExchangeStrategy strategy,
            DBOpenHelper dbOpenHelper) {
        super(exchangeVariant, strategy, dbOpenHelper);
    }

    @Override
    protected boolean doExecute() throws Exception {

        // Выполнить шаги обмена по предопределенным правилам
        boolean success = super.doExecute();
        if (success) {
            // Получить произвольные данные - наш 2-ой отчет
            onStepInfo("Получаю отчеты…");

            String fPath = mAppSettings.getCacheDir().getAbsolutePath() + "/" +
                           ProductsInStokReport.REPORT_FILE_NAME;
            File f = mExchangeStrategy.getLargeData(ID_REPORT_PRODUCTS_IN_STOK, null, "", fPath);
            success = (f != null);
        }

        return success;
    }

}
