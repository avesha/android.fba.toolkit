package ru.profi1c.samples.sensus;

import ru.profi1c.engine.report.SimpleCompiledHtmlReport;

public class FaqReport extends SimpleCompiledHtmlReport {

    private static final String ASSERT_REPORT_NAME = "reports/app_faq.html";

    public FaqReport() {
        super(ASSERT_REPORT_NAME);
    }

    @Override
    public int getResIdIcon() {
        return 0;
    }

    @Override
    public int getResIdTitle() {
        return 0;
    }
}
