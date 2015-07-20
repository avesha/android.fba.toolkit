package ru.profi1c.engine.report;

import java.util.HashMap;

abstract class BaseSimpleHtmlReport extends SimpleReport implements IReportBuilder {

    protected static final String REPORT_TITLE = "report_title";
    protected static final String REPORT_BACKGROUND_COLOR = "report_background_color";
    protected static final String REPORT_TEXT_COLOR = "report_text_color";
    protected static final String REPORT_HEADER_TEXT_COLOR = "report_header_text_color";
    protected static final String REPORT_FOOTER_TEXT_COLOR = "report_footer_text_color";
    protected static final String HEADER_1 = "header_1";
    protected static final String HEADER_2 = "header_2";
    protected static final String HEADER_3 = "header_3";
    protected static final String TEXT = "text";
    protected static final String FOOTER = "footer";

    protected HashMap<String, Object> mParams;

    protected BaseSimpleHtmlReport() {
        mParams = new HashMap<String, Object>();
    }

    protected void setColorParam(String name, int value) {
        String strColor = String.format("#%06X", 0xFFFFFF & value);
        mParams.put(name, strColor);
    }

    public void setReportTitle(String value) {
        mParams.put(REPORT_TITLE, value);
    }

    public void setBackgroundColor(int color) {
        setColorParam(REPORT_BACKGROUND_COLOR, color);
    }

    public void setTextColor(int color) {
        setColorParam(REPORT_TEXT_COLOR, color);
    }

    public void setHeaderTextColor(int color) {
        setColorParam(REPORT_HEADER_TEXT_COLOR, color);
    }

    public void setFooterTextColor(int color) {
        setColorParam(REPORT_FOOTER_TEXT_COLOR, color);
    }

    public void setHeader1(String value) {
        mParams.put(HEADER_1, value);
    }

    public void setHeader2(String value) {
        mParams.put(HEADER_2, value);
    }

    public void setHeader3(String value) {
        mParams.put(HEADER_3, value);
    }

    public void setFooter(String value) {
        mParams.put(FOOTER, value);
    }

    @Override
    public IReportBuilder getReportBuilder() {
        return this;
    }

}
