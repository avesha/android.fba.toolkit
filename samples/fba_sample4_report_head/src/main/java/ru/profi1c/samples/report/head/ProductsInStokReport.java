package ru.profi1c.samples.report.head;

import java.io.File;

import ru.profi1c.engine.report.SimpleCompiledHtmlReport;

/**
 * Пример скомпилированного отчета, просто выводит Html файл. В этом примере
 * данных файл сгенерирован в 1С и передан мобильному клиенту во время обмена
 */
public class ProductsInStokReport extends SimpleCompiledHtmlReport {

    // Имя, под которым сохраняется полученный файл от 1с (см. процедуру обмена
    // MyExchangeTask)
    public static final String REPORT_FILE_NAME = "products_in_stok.html";

    public ProductsInStokReport(File file) {
        super(file);
    }

    @Override
    public int getResIdIcon() {
        return R.mipmap.report_02;
    }

    @Override
    public int getResIdTitle() {
        return R.string.report_name_products_in_stok;
    }

}
