package ru.profi1c.engine.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

@TargetApi(8)
public final class StorageHelper {

    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    /**
     * Returns True if the external storage is connected and available for write data
     *
     * @return
     */
    public static boolean isExternalStorageWriteable() {

        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageWriteable = false;
        }
        return mExternalStorageWriteable;
    }

    /**
     * Returns true if the external file storage is read
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Return root directory app on internal storage persistent data.
     */
    public static File getInternalAppPath(Context context) {
        return context.getFilesDir();
    }

    /**
     * Returns the absolute path to the application specific cache directory on the file system.
     * This simple Context.getCacheDir() wrapper.
     */
    public static File getInternalAppCashe(Context context) {
        return context.getCacheDir();
    }

    /**
     * Return root directory app on external storage
     */
    public static File getExternalAppPath(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            return getEclairExternalStorageDirectory(context, "files");
        } else {
            // Environment.DIRECTORY_DOWNLOADS or Environment.DIRECTORY_MUSIC
            // etc, if null - return root directory
            File f = context.getExternalFilesDir(null);

            // WARNING! Bag on 2.2
            // https://groups.google.com/forum/?fromgroups=#!topic/android-developers/to1AsfE-Et8
            // getExternalFilesDir may return null even if external storage
            // writable and exists permission on manifest,
            // workaround - use getExternalStorageDirectory()
            if (f == null) {
                f = getEclairExternalStorageDirectory(context, "files");
            }
            return f;
        }
    }

    private static File getEclairExternalStorageDirectory(Context ctx, String dirName) {
        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/" +
                      ctx.getPackageName() + File.separatorChar + dirName;
        return new File(path);
    }

    /**
     * Return cash directory on external storage
     */
    public static File getExternalAppCashe(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            return getEclairExternalStorageDirectory(context, "cache");
        } else {
            // Environment.DIRECTORY_DOWNLOADS or Environment.DIRECTORY_MUSIC
            // etc, if null - return root directory
            return context.getExternalCacheDir();
        }
    }

    /**
     * Check if media is mounted or storage is built-in, if so, try and use external cache dir
     * otherwise use internal cache dir
     */
    public static File getExternalAppCashe(Context context, String uniqueName) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return new File(cacheDir, uniqueName);
    }


    /**
     * Return custom backup directory (not auto delete after delete application):
     * [sdcard]/backup/your.app.package.name or null if external storage not available
     */
    public static File getAppBackup(Context context) {
        if (isExternalStorageAvailable()) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/backup/" +
                          context.getPackageName();
            return new File(path);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

    private StorageHelper() {
    }
}
