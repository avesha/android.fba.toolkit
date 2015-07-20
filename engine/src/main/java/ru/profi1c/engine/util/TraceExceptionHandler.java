package ru.profi1c.engine.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaApplication;

/**
 * Error trapping and saving to a text file
 */
public class TraceExceptionHandler implements UncaughtExceptionHandler {

    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("dd-MM-yyyy HHmmss", Locale.ENGLISH);
    private static final String NL = System.getProperty("line.separator").toString();
    private static final String TRACE_FILE_EXT = ".trace";

    /**
     * default exception handler, after our processing call it
     */
    private UncaughtExceptionHandler mDefaultUEH;
    private final Context mContext;

    /**
     * Connect your error handler
     */
    public static void attach(Context context) {
        UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new TraceExceptionHandler(context, defaultUEH));
    }

    public TraceExceptionHandler(Context context, UncaughtExceptionHandler defaultUEH) {
        this.mDefaultUEH = defaultUEH;
        this.mContext = context;
    }

    protected UncaughtExceptionHandler getDefaultUEH() {
        return mDefaultUEH;
    }

    protected Context getContext() {
        return mContext;
    }

    /**
     * Intercept unhandled exceptions
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        traceToFile(e);
        mDefaultUEH.uncaughtException(t, e);
    }

    protected void traceToFile(Throwable e) {
        String timestamp = DATE_FORMATTER.format(new Date(System.currentTimeMillis()));

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        String currVersionName = AppHelper.getAppVersion(mContext);
        printWriter.append("version app: ").append(currVersionName).append(NL);

        e.printStackTrace(printWriter);

        String stacktrace = result.toString();
        printWriter.close();

        File dir = getErrorReportDir(mContext);
        String filename = dir.getAbsolutePath() + File.separatorChar + timestamp + TRACE_FILE_EXT;

        writeToFile(stacktrace, filename);
    }

    /**
     * Keep a description of the error to a text file
     */
    private void writeToFile(String stacktrace, String filename) {
        try {
            IOHelper.writeToFile(stacktrace, filename);
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        }
    }

    /**
     * Get an error log text log files, errors are removed end of processing
     */
    public static String getErrorTraceLog(Context ctx) {

        File dir = getErrorReportDir(ctx);
        final String[] files = getErrorReportFiles(dir);
        StringBuilder sb = new StringBuilder();

        if (files != null && files.length > 0) {

            String currVersionName = AppHelper.getAppVersion(ctx);

            sb.append("firmware version: ").append(Build.VERSION.RELEASE).append(" model: ")
              .append(Build.DEVICE).append(NL);
            sb.append("version app send: ").append(currVersionName).append(NL);
            for (String fName : files) {

                try {
                    String fileText = IOHelper.getFileData(
                            dir.getAbsolutePath() + File.separatorChar + fName);
                    if (!TextUtils.isEmpty(fileText)) {
                        sb.append("file: ").append(fName).append(NL);
                        sb.append(fileText);
                    }
                } catch (FileNotFoundException e) {
                    Dbg.printStackTrace(e);
                } catch (IOException e) {
                    Dbg.printStackTrace(e);
                }
            }
        }

        IOHelper.deleteFiles(dir.getAbsolutePath(), files);
        return sb.toString();
    }

    /**
     * Get a catalog that stores error logs
     */
    private static File getErrorReportDir(Context context) {
        File dir = FbaApplication.from(context).getAppSettings().getCacheDir();
        if (dir == null) {
            dir = context.getCacheDir();
        }
        return dir;
    }

    /**
     * Get error log files
     */
    private static String[] getErrorReportFiles(File dir) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(TRACE_FILE_EXT);
            }
        };
        return dir.list(filter);
    }

    /**
     * Delete log files for errors
     */
    public static void deleteErrorReportFiles(Context ctx) {
        File dir = getErrorReportDir(ctx);
        String[] files = getErrorReportFiles(dir);
        if (files != null && files.length > 0) {
            IOHelper.deleteFiles(dir.getAbsolutePath(), files);
        }
    }
}
