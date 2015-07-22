package ru.profi1c.engine.report;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Map;

import ru.profi1c.engine.R;
import ru.profi1c.engine.app.FbaDialog;
import ru.profi1c.engine.util.ActionHelper;
import ru.profi1c.engine.util.ReflectionHelper;

/**
 * Вывод страницы HTML в диалоге
 */
public class WebViewDialog extends FbaDialog {

    private static final FrameLayout.LayoutParams FILL;

    static {
        FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private static final int DEF_WINDOWS_BACKGROUND_COLOR = 0x70000000;

    private String mReportData;
    private String mUrl;
    private ProgressDialog mSpinner;
    private ImageView mCrossImage;
    private WebView mWebView;
    private FrameLayout mContent;
    private int mBackgroundColor = 0xCC000000;
    private String mProgressMessage;
    private Map<Object, String> mJavaScriptInterface;

    public WebViewDialog(Context context) {
        super(context, android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar);
    }

    public void setReportUrl(String url) {
        mUrl = url;
    }

    public void setReportData(String data) {
        mReportData = data;
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setProgressMessage(String message) {
        this.mProgressMessage = message;
    }

    public void setJavaScriptInterfaces(Map<Object, String> mJavaScriptInterface) {
        this.mJavaScriptInterface = mJavaScriptInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // blur/burn in the background
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(DEF_WINDOWS_BACKGROUND_COLOR));

        mSpinner = new ProgressDialog(getContext(), (Build.VERSION.SDK_INT <
                                                     Build.VERSION_CODES.LOLLIPOP) ? 0 : android.R.style.Theme_DeviceDefault_Dialog);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setCancelable(false);
        mSpinner.setCanceledOnTouchOutside(false);
        if (mProgressMessage == null) {
            mSpinner.setMessage(getContext().getString(R.string.fba_report_output));
        } else {
            mSpinner.setMessage(mProgressMessage);
        }

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mContent = new FrameLayout(getContext());

		/*
         * Create the 'x' image, but don't add to the mContent layout yet at
		 * this point, we only need to know its drawable width and height to
		 * place the webview
		 */
        createCrossImage();

		/*
         * Now we know 'x' drawable width and height, layout the webivew and add
		 * it the mContent layout
		 */
        int crossWidth = mCrossImage.getDrawable().getIntrinsicWidth();
        setUpWebView(crossWidth / 2);

		/*
         * Finally add the 'x' image to the mContent layout and add mContent to
		 * the Dialog view
		 */
        FrameLayout.LayoutParams paramPross =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramPross.gravity = Gravity.TOP | Gravity.LEFT;

        mContent.addView(mCrossImage, paramPross);
        addContentView(mContent,
                       new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //AppCompatDialog throws NullPointerException on dismiss, fix:
        // - set mHasActionBar = false
        // - or set for dialog theme where not have actionBar
        ReflectionHelper.setFieldValue(getDelegate(), "mHasActionBar", false);
    }

    private void createCrossImage() {
        mCrossImage = new ImageView(getContext());
        // Dismiss the dialog when user click on the 'x'
        mCrossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewDialog.this.dismiss();
            }
        });
        Drawable crossDrawable = getContext().getResources().getDrawable(R.mipmap.fba_dialog_close);
        mCrossImage.setImageDrawable(crossDrawable);
        /*
		 * 'x' should not be visible while webview is loading make it visible
		 * only after webview has fully loaded
		 */
        mCrossImage.setVisibility(View.INVISIBLE);

    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void setUpWebView(int margin) {
        LinearLayout webViewContainer = new LinearLayout(getContext());

        mWebView = new WebView(getContext());

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewDialog.ReportWebViewClient());
        mWebView.setBackgroundColor(mBackgroundColor);

        final WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);

        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCachePath("/data/data/" + getContext().getPackageName() + "/cache");
        settings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            settings.setDisplayZoomControls(true);
        }

        if (mJavaScriptInterface != null) {
            for (Object obj : mJavaScriptInterface.keySet()) {
                if (obj != null) {
                    mWebView.addJavascriptInterface(obj, mJavaScriptInterface.get(obj));
                }
            }
        }

        if (!TextUtils.isEmpty(mReportData)) {
            mWebView.loadDataWithBaseURL("x-data://base", mReportData, "text/html", "UTF-8", null);
        } else if (mUrl != null) {
            mWebView.loadUrl(mUrl);
        } else
            throw new IllegalStateException("Not sets report data or url!");

        mWebView.setLayoutParams(FILL);
        mWebView.setVisibility(View.INVISIBLE);

        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(mWebView);
        mContent.addView(webViewContainer);
    }

    private class ReportWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mSpinner.dismiss();
			/*
			 * Once webview is fully loaded, set the mContent background to be
			 * transparent and make visible the 'x' image.
			 */
            mContent.setBackgroundColor(Color.TRANSPARENT);
            mWebView.setVisibility(View.VISIBLE);
            mCrossImage.setVisibility(View.VISIBLE);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            ActionHelper.actionUrl(getContext(), url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }


}
