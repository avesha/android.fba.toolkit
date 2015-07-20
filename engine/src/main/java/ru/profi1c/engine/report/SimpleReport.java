package ru.profi1c.engine.report;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.TypedValue;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import ru.profi1c.engine.R;

/**
 * Простой отчет, результат выводится в html - диалог, см {@link WebViewDialog}
 */
public abstract class SimpleReport extends BaseReport {

    private Reference<WebViewDialog> mReferenceDlg;
    private Map<Object, String> mJavaScriptInterface;

    @Override
    public void onDestroy() {
        if (mReferenceDlg != null) {
            WebViewDialog dlg = mReferenceDlg.get();
            if (dlg != null && dlg.isShowing()) {
                dlg.dismiss();
            }
        }
    }

    /**
     * @see android.webkit.WebView#addJavascriptInterface()
     */
    public void addJavascriptInterface(Object javaScriptInterface, String name) {
        if (mJavaScriptInterface == null) {
            mJavaScriptInterface = new WeakHashMap<Object, String>();
        }
        mJavaScriptInterface.put(javaScriptInterface, name);
    }

    @Override
    public void onComplete(Object data) {

        final Context context = getContext();
        if (context != null) {

            if (data == null) {
                showMessage(context, context.getString(R.string.fba_report_empty));
            } else {

                WebViewDialog dlg = new WebViewDialog(context);
                mReferenceDlg = new WeakReference<WebViewDialog>(dlg);

                dlg.setJavaScriptInterfaces(mJavaScriptInterface);

                if (data instanceof String) {
                    dlg.setReportData((String) data);
                } else if (data instanceof Uri) {
                    dlg.setReportUrl(data.toString());
                } else {
                    throw new IllegalStateException("An unexpected result of Report Builder!");
                }

                Resources.Theme theme = context.getTheme();
                TypedValue styleID = new TypedValue();
                if (theme.resolveAttribute(R.attr.reportDlgBgrColor, styleID, true)) {
                    dlg.setBackgroundColor(styleID.data);
                }

                dlg.show();
            }

        }
    }

    @Override
    public void onError(String msg) {
        Context context = getContext();
        if (context != null) {
            showMessage(context, msg);
        }
    }

    public void showMessage(Context ctx, String message) {
        AlertDialog.Builder builder = new Builder(ctx);
        builder.setTitle(getResIdTitle());
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }
}
