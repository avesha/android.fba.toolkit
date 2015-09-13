package ru.profi1c.samples.sensus.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.util.StorageHelper;
import ru.profi1c.samples.sensus.Const;
import ru.profi1c.samples.sensus.Dbg;
import ru.profi1c.samples.sensus.R;

/**
 * Helper class to selecting photo from gallery (or creating and selecting from camera)
 */
public final class SelectPhotoHelper {

    public interface SelectPhotoListener {
        void onSelectPhoto(String fName);
    }

    private static final String TAG = SelectPhotoHelper.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final String IMAGE_MIME_TYPE = "image/*";

    private static final String DISK_CACHE_SUBDIR = "tmp-photo";
    private static final String OUTPUT_CAPTURE_IMAGE_EXTENSION = ".png";

    private final WeakReference<FbaActivity> mRefActivity;
    private final WeakReference<SelectPhotoListener> mRefListener;

    private final File mTmpDir;
    private File mLastCaptureFile;

    /**
     * Get pick image intent
     */
    public static Intent getIntentPickImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(IMAGE_MIME_TYPE);
        return i;
    }

    public static String extractPath(Context context, Uri uri) {
        String realPath = null;
        if (uri != null) {

            realPath = ContentProviderExtractor.getPath(context, uri);
            if (TextUtils.isEmpty(realPath)) {
                // file, not scheme file:// ?
                try {
                    final String tmpPath = uri.getPath();
                    if (!TextUtils.isEmpty(tmpPath)) {
                        File f = new File(tmpPath);
                        if (f.exists()) {
                            realPath = tmpPath;
                        }
                    }
                } catch (Exception e) {
                    Dbg.printStackTrace(e);
                }
            }
        }
        return realPath;
    }

    public static String extractSelectedContentUri(Context context, Intent data) {
        return extractPath(context, data.getData());
    }

    public SelectPhotoHelper(FbaActivity activity, SelectPhotoListener listener) {
        mRefActivity = new WeakReference<FbaActivity>(activity);
        mRefListener = new WeakReference<SelectPhotoListener>(listener);
        mTmpDir = StorageHelper.getExternalAppCashe(activity, DISK_CACHE_SUBDIR);
        if (!mTmpDir.exists()) {
            mTmpDir.mkdirs();
        }
    }

    private File formatOutputCaptureFile() {
        return new File(mTmpDir, String.format("tmp_capture_%d_%s", System.currentTimeMillis(),
                                               OUTPUT_CAPTURE_IMAGE_EXTENSION));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Activity.RESULT_OK == resultCode && Const.REQUESTCODE_SELECT_VIDEO == requestCode) {
            final FbaActivity activity = getActivity();
            if (activity != null) {

                String fName = null;
                if (data != null) {
                    fName = extractSelectedContentUri(activity, data);
                }

                if (fName == null && mLastCaptureFile != null) {
                    fName = mLastCaptureFile.getAbsolutePath();
                    mLastCaptureFile = null;
                }

                if (!TextUtils.isEmpty(fName)) {
                    if (DEBUG) {
                        Dbg.d(TAG, "select photo: " + fName);
                    }

                    SelectPhotoListener listener = getListener();
                    if (listener != null) {
                        listener.onSelectPhoto(fName);
                    }
                } else {
                    activity.showToast(activity.getString(R.string.msg_cannot_select_photo));
                }
            }
        }
    }

    public void createPhotoFile() {
        final FbaActivity activity = getActivity();
        if (activity != null) {

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                       ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            mLastCaptureFile = formatOutputCaptureFile();
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mLastCaptureFile));

            try {
                activity.startActivityForResult(i, Const.REQUESTCODE_SELECT_VIDEO);
            } catch (ActivityNotFoundException e) {
                Dbg.printStackTrace(e);
                activity.showMessage(R.string.app_name,
                                     activity.getString(R.string.msg_not_found_app_make_photo));
            }
        }

    }

    public void pickPhoto() {
        final FbaActivity activity = getActivity();
        if (activity != null) {
            Intent i = getIntentPickImage();
            try {
                activity.startActivityForResult(i, Const.REQUESTCODE_SELECT_VIDEO);
            } catch (ActivityNotFoundException e) {
                Dbg.printStackTrace(e);
                activity.showMessage(R.string.app_name,
                                     activity.getString(R.string.msg_not_found_app_make_photo));
            }
        }
    }

    private FbaActivity getActivity() {
        return mRefActivity.get();
    }

    private SelectPhotoListener getListener() {
        return mRefListener.get();
    }

}
