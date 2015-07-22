package ru.profi1c.samples.report.head;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ru.profi1c.engine.report.IReport;
import ru.profi1c.engine.util.IOHelper;

/**
 * Пример просмотра скомпилированного PDF-отчета во внешнем приложении
 */
public class PdfReport implements IReport {

    private static final String MIME_TYPE_PDF = "application/pdf";

    @Override
    public int getResIdIcon() {
        return R.mipmap.report_05;
    }

    @Override
    public int getResIdTitle() {
        return R.string.report_name_pdf_sample;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onShow(Context context) {

        App app = (App) context.getApplicationContext();

        // Извлечь образец отчета из приложения. Файл должен быть доступен для
        // внешних приложений, поэтому помещаем его на карточку
        File file = new File(app.getExternalCacheDir(), "report_05.pdf");
        if (!file.exists()) {
            try {
                IOHelper.saveAssetsData(context, "reports/report_05.pdf", file);
            } catch (IOException e) {
                Toast.makeText(context,
                        "Ошибка извлечения демо-отчета",
                        Toast.LENGTH_SHORT).show();
            }
        }

        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, MIME_TYPE_PDF);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context,
                        "Не установлено приложение для просмотра отчетов в PDF формате",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

}
