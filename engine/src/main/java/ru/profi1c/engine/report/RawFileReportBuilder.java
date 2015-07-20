package ru.profi1c.engine.report;

import android.content.Context;
import android.net.Uri;

import java.io.File;

import ru.profi1c.engine.R;

/**
 * Построитель отчета по внешнему файлу, просто возвращает URI ссылку на
 * исходный файл отчета
 */
public class RawFileReportBuilder implements IReportBuilder {

    private final File mFile;
    private String mErrMessage;

    public RawFileReportBuilder(File fSource) {
        mFile = fSource;
    }

    /**
     * Установить текстовое сообщение об ошибке в случае отсутствия файла отчета
     */
    public void setErrorMessage(String message) {
        this.mErrMessage = message;
    }

    @Override
    public void build(Context context, IReportBuilderResult builderResult) {
        if (mFile.exists()) {
            Uri uri = Uri.fromFile(mFile);
            builderResult.onComplete(uri);
        } else {
            builderResult.onError(mErrMessage == null ? context
                    .getString(R.string.fba_file_not_found) : mErrMessage);
        }
    }

}
