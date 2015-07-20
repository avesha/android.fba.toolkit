package ru.profi1c.engine.report;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.util.IOHelper;

/**
 * Простой скомпилированный HTML отчет. Источником может выступать внешний файл
 * или assert - ресурс, результат выводится в html – диалог
 */
public abstract class SimpleCompiledHtmlReport extends SimpleReport {

    private File mFile;
    private String mPathToAsset;

    /**
     * Создать отчет на основании внешнего файла
     */
    public SimpleCompiledHtmlReport(File file) {
        mFile = file;
    }

    /**
     * Создать отчет на основании внутреннего актива приложения
     *
     * @param pathToAsset Путь к ресурсу в Assets (исключая сам корневой каталог Assets)
     */
    public SimpleCompiledHtmlReport(String pathToAsset) {
        mPathToAsset = pathToAsset;
    }

    @Override
    public IReportBuilder getReportBuilder() {
        if (mFile != null) {
            return new RawFileReportBuilder(mFile);
        }
        return new AssertReportBuilder();
    }

    private class AssertReportBuilder implements IReportBuilder {

        @Override
        public void build(Context context, IReportBuilderResult builderResult) {
            try {
                String data = IOHelper.getAssetsData(context, mPathToAsset);
                builderResult.onComplete(data);
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            }
        }

    }
}
