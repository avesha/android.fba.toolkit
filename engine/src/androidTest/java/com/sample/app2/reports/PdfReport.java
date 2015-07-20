package com.sample.app2.reports;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.sample.app2.R;

import java.io.File;

import ru.profi1c.engine.report.IReport;

/**
 * Пример просмотра скомпилированного PDF-отчета во внешнем приложении
 */
public class PdfReport implements IReport {

    private static final String MIME_TYPE_PDF = "application/pdf";
    private final File mFile;

    public PdfReport(File file) {
        mFile = file;
    }

    @Override
    public int getResIdIcon() {
        return R.drawable.report_pdf;
    }

    @Override
    public int getResIdTitle() {
        return R.string.report_ext_file_source;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onShow(Context context) {

        if (mFile.exists()) {
            Uri path = Uri.fromFile(mFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, MIME_TYPE_PDF);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, R.string.msg_err_view_pdf_reports, Toast.LENGTH_SHORT)
                     .show();
            }
        }
    }

}