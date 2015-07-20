package ru.profi1c.engine.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import ru.profi1c.engine.Dbg;

/**
 * Static auxiliary class for working with media resources
 */
public final class MediaHelper {
    private static final String TAG = MediaHelper.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;
    private static final int IN_SAMPLE_IMAGE_SIZE = 4;
    private static final String IMAGE_MIME_TYPE = "image/jpeg";

    /**
     * Draw text on center of Drawable and return result as BitmapDrawable
     *
     * @param context       current context
     * @param resIdDrawable resource identifier
     * @param text          text to write
     * @param textSize      text size
     * @return
     */
    public static BitmapDrawable drawTextOnDrawable(Context context, int resIdDrawable, String text,
            int textSize) {

        Resources res = context.getResources();
        Bitmap bm = BitmapFactory.decodeResource(res, resIdDrawable)
                                 .copy(Bitmap.Config.ARGB_8888, true);

        return drawTextOnDrawable(context, bm, text, textSize);
    }

    /**
     * Draw text on center of Drawable and return result as BitmapDrawable
     *
     * @param context  current context
     * @param drawable Bitmap drawable
     * @param text     text to write
     * @param textSize text size
     * @return
     */
    public static BitmapDrawable drawTextOnDrawable(Context context, BitmapDrawable drawable,
            String text, int textSize) {
        Bitmap bm = drawable.getBitmap();
        return drawTextOnDrawable(context, bm, text, textSize);
    }

    private static BitmapDrawable drawTextOnDrawable(Context context, Bitmap bitmap, String text,
            int textSize) {
        Resources res = context.getResources();
        if (bitmap != null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setTextSize(textSize);
            paint.setAlpha(128);

            final float densityMultiplier = res.getDisplayMetrics().density;
            final float scaledPx = textSize * densityMultiplier;
            paint.setTextSize(scaledPx);
            final float widthText = paint.measureText(text);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(text, bitmap.getWidth() / 2 - widthText / 2, bitmap.getHeight() / 2 + 2,
                            paint);

            return new BitmapDrawable(res, bitmap);
        }
        return null;
    }

    /**
     * Scaling of the image from a file to scale pixels in width or height
     */
    public static Bitmap scaleBitmap(Context context, File fPath, int scale)
            throws FileNotFoundException {
        InputStream photoStream = new FileInputStream(fPath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = IN_SAMPLE_IMAGE_SIZE;
        Bitmap photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
        return scaleBmp(photoBitmap, scale);
    }

    /**
     * Image scaling to scale the points in width or height
     */
    public static Bitmap scaleBitmap(Bitmap photoBitmap, int scale) {
        return scaleBmp(photoBitmap, scale);
    }

    private static Bitmap scaleBmp(Bitmap photoBitmap, int scale) {
        int h = photoBitmap.getHeight();
        int w = photoBitmap.getWidth();
        if ((w > h) && (w > scale)) {
            double ratio = ((double) scale) / w;
            w = scale;
            h = (int) (ratio * h);
        } else if ((h > w) && (h > scale)) {
            double ratio = ((double) scale) / h;
            h = scale;
            w = (int) (ratio * w);
        }
        return Bitmap.createScaledBitmap(photoBitmap, w, h, true);
    }

    /**
     * Save the picture in the file
     */
    static public boolean saveImageToFile(Bitmap bmp, String fName, Bitmap.CompressFormat format,
            int quality) throws IOException {
        boolean result = false;
        FileOutputStream rtFOS = null;

        File rtNew = new File(fName);
        if (rtNew.exists()) {
            rtNew.delete();
        }

        try {
            rtFOS = new FileOutputStream(rtNew);
            result = bmp.compress(format, quality, rtFOS);
            rtFOS.flush();
        } finally {
            IOHelper.close(rtFOS);
        }
        return result;
    }

    /**
     * Read image from file
     */
    public static Bitmap loadBmpFromFile(String fName) throws IOException {
        Bitmap bmp = null;

        File fNew = new File(fName);
        if (fNew.exists()) {

            FileInputStream is = null;
            try {
                is = new FileInputStream(fName);
                bmp = BitmapFactory.decodeStream(is);
            } finally {
                IOHelper.close(is);
            }
        }
        return bmp;
    }

    /**
     * Load bitmap from local file,previously scaled for view
     *
     * @param context
     * @param fName     fName The local file path
     * @param dstWidth
     * @param dstHeight
     * @param one2one   scale size to one to one the specified size, otherwise the nearest
     * @return
     * @throws IOException
     */
    public static Bitmap loadBmpFormFileScalable(Context context, String fName, int dstWidth,
            int dstHeight, boolean one2one) throws IOException {
        Bitmap bmp = null;

        File fNew = new File(fName);
        if (fNew.exists()) {
            FileInputStream is = null;
            int inWidth = 0;
            int inHeight = 0;

            try {
                is = new FileInputStream(fName);

                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, o);
                is.close();
                is = null;
                System.gc();

                // save width and height
                inWidth = o.outWidth;
                inHeight = o.outHeight;

                // decode full image pre-resized
                is = new FileInputStream(fName);
                o = new BitmapFactory.Options();
                // calc rought re-size (this is no exact resize)
                o.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);

                // decode full image
                bmp = BitmapFactory.decodeStream(is, null, o);
                System.gc();

                if (one2one) {
                    // calc exact destination size
                    Matrix m = new Matrix();
                    RectF inRect = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
                    RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
                    m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
                    float[] values = new float[9];
                    m.getValues(values);

                    // resize bitmap
                    bmp = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * values[0]),
                                                    (int) (bmp.getHeight() * values[4]), true);
                    System.gc();
                }

            } finally {
                IOHelper.close(is);
            }

        }
        return bmp;
    }

    /**
     * Rotating pictures on a specified number of degrees. The new image will be created, the source
     * bitmap if the new bitmap is created, source bitmap - released from memory
     *
     * @param b
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
                if (DEBUG) {
                    Dbg.d(TAG, "rotate bitmap: OutOfMemoryError");
                }
            }
        }
        return b;
    }

    /**
     * Make an Bitmap to have rounded corners
     *
     * @param bitmap - source bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels, int w,
            int h, boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR) {

        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        // make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels * densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        // draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (squareTR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }
        if (squareBL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (squareBR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }

    /**
     * Returns the path of his Uri
     */
    public static String getRealPathFromURI(Context ctx, Uri contentUri) {
        String RealPath = null;

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                RealPath = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return RealPath;
    }

    /**
     * Add link of image to shared media storage
     */
    public static Uri saveImageToSharedMedia(Context ctx, File imagePath, String title,
            String description, String bucket) {

        ContentValues v = new ContentValues();
        v.put(Images.Media.TITLE, title);

        if (!TextUtils.isEmpty(description)) {
            v.put(Images.Media.DESCRIPTION, description);
        }

        v.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
        v.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
        v.put(Images.Media.DATE_MODIFIED, System.currentTimeMillis());
        v.put(Images.Media.MIME_TYPE, IMAGE_MIME_TYPE);

        if (TextUtils.isEmpty(bucket)) {

            File parent = imagePath.getParentFile();
            String path = parent.toString().toLowerCase(Locale.getDefault());
            String name = parent.getName().toLowerCase(Locale.getDefault());

            v.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
            v.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);

        } else {

            v.put(Images.ImageColumns.BUCKET_ID, bucket.hashCode());
            v.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, bucket);
        }

        v.put(Images.Media.SIZE, imagePath.length());
        v.put("_data", imagePath.getAbsolutePath());

        ContentResolver c = ctx.getContentResolver();
        return c.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
    }

    private MediaHelper() {
    }
}
