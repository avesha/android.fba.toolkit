package ru.profi1c.engine.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ru.profi1c.engine.Dbg;

/**
 * A helper class generic functions for works application info
 */
public final class AppHelper {
    private static final String TAG = AppHelper.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;
    private static final String UNKNOWN_APP = "(unknown)";

    /**
     * debug function - print hashes my signature keys
     *
     * @param context
     */
    public static void debugPrintSignatures(Context context) {
        try {
            if (DEBUG) {
                Signature[] sigs = context.getPackageManager()
                                          .getPackageInfo(context.getPackageName(),
                                                          PackageManager.GET_SIGNATURES).signatures;
                for (Signature sig : sigs) {
                    Dbg.d(TAG, "Signature hashcode : " + sig.hashCode());
                }
            }
        } catch (NameNotFoundException e) {
            Dbg.printStackTrace(e);
        }
    }

    /**
     * Return has code of developer key
     */
    public static int getSignaturesHashCode(Context context) {
        int hashCode = 0;
        try {
            Signature[] signs = context.getPackageManager().getPackageInfo(context.getPackageName(),
                                                                           PackageManager.GET_SIGNATURES).signatures;
            if (signs != null && signs.length > 0) {
                hashCode = signs[0].hashCode();
            }
        } catch (NameNotFoundException e) {
            Dbg.printStackTrace(e);
        }
        return hashCode;
    }

    /**
     * Return label of application
     */
    public static String getAppLabel(Context context) {
        String applicationName = UNKNOWN_APP;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            if (ai != null) {
                applicationName = (String) pm.getApplicationLabel(ai);
            }

        } catch (final NameNotFoundException e) {
            Dbg.printStackTrace(e);
        }

        return applicationName;
    }

    /**
     * Returns the name of the current version of the application
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {

        String currVersionName = "";
        try {
            PackageInfo pInfo = AppHelper.getPackageInfo(context);
            currVersionName = pInfo.versionName;
        } catch (NameNotFoundException e) {
            Dbg.printStackTrace(e);
        }
        return currVersionName;
    }

    /**
     * Return the version number of this package, as specified by the manifest
     * tag's versionCode attribute.
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int currVersionCode = 0;
        try {
            PackageInfo pInfo = AppHelper.getPackageInfo(context);
            currVersionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            Dbg.printStackTrace(e);
        }
        return currVersionCode;
    }

    /**
     * Returns the code of the apk archive of the application
     *
     * @param context
     * @return
     */
    public static int getApkAppVersionCode(Context context, String apkFullPath) {
        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkFullPath, 0);
        return info.versionCode;
    }

    /**
     * Returns information about the current application (version, etc.)
     *
     * @param context
     * @return PackageInfo of current application
     * @throws NameNotFoundException
     */
    public static PackageInfo getPackageInfo(Context context) throws NameNotFoundException {

        final String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        return pm.getPackageInfo(packageName, 0);
    }

    /**
     * Get package name for external apk-file
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getPackageNameFromApk(Context context, String fileName) {

        PackageManager appInfo = context.getPackageManager();
        PackageInfo pkgInfo = appInfo.getPackageArchiveInfo(fileName, 0);
        if (pkgInfo != null) {
            pkgInfo.applicationInfo.sourceDir = fileName;
            pkgInfo.applicationInfo.publicSourceDir = fileName;
            return (String) pkgInfo.applicationInfo.loadLabel(appInfo);
        }
        return null;
    }

    /**
     * Get build time stamp from current apk
     *
     * @param context
     * @return
     * @throws NameNotFoundException
     */
    public static long getApkBuildDate(Context context) throws NameNotFoundException {

        long time = 0;

        ApplicationInfo ai =
                context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        ZipFile zf = null;
        try {
            zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            time = ze.getTime();

        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            IOHelper.close(zf);
        }
        return time;
    }

    /**
     * Returns true if this application install to SDCard
     *
     * @param context
     * @return
     */
    public static boolean isAppInstalledToSDCard(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            return context.getApplicationInfo().sourceDir.matches("^/data/app/.*");
        } else {
            // flag ApplicationInfo.FLAG_EXTERNAL_STORAGE added on API Level 8

            PackageManager pm = context.getPackageManager();
            try {
                int extStorage = ApplicationInfo.FLAG_EXTERNAL_STORAGE;
                String packageName = context.getPackageName();
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, extStorage);
                return extStorage == (extStorage & appInfo.flags);
            } catch (NameNotFoundException e) {
                Dbg.printStackTrace(e);
                return false;
            }
        }

    }

    public static void removeApplication(Context context, String pkgName) {
        // We are not able to delete applications by our own.
        // Therefore, asking Android to remove the application
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri data = Uri.fromParts("package", pkgName, null);
        intent.setData(data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Dbg.printStackTrace(e);
        }
    }

    public static void installApplication(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Dbg.printStackTrace(e);
        }
    }

    private static ApplicationInfo findInstalledApp(Context context, String targetPackage) {
        ApplicationInfo appInfo = null;
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage)) {
                appInfo = packageInfo;
                break;
            }
        }
        return appInfo;
    }

    public static boolean isPackageExisted(Context context, String targetPackage) {
        return findInstalledApp(context, targetPackage) != null;
    }

    private AppHelper() {
    }
}
