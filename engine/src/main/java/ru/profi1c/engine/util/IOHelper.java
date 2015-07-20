package ru.profi1c.engine.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import ru.profi1c.engine.Dbg;


/**
 * Assistant to work with files and streams
 */
public final class IOHelper {

    private static String readString(InputStream is) throws IOException {
        String text = null;
        try {

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);

        } finally {
            close(is);
        }
        return text;
    }

    /**
     * Reading a file from the resources of Assets
     *
     * @param context - application context
     * @param fName   - path to the file directory Assets are not required, for example:
     *                "flot / html / basechart.html"
     * @return string or null, if there was an error reading
     * @throws IOException
     */
    public static String getAssetsData(Context context, String fName) throws IOException {
        return readString(context.getAssets().open(fName));
    }

    /**
     * Reading text data from raw resource
     *
     * @param context  current context
     * @param resIdRaw raw resource id
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String getRawData(Context context, int resIdRaw)
            throws NotFoundException, IOException {
        return readString(context.getResources().openRawResource(resIdRaw));
    }

    /**
     * Reading a file from the resources of Assets
     *
     * @param context   application context
     * @param assetName assetName path to the file directory Assets are not required,
     *                  for example: "flot / html / basechart.html"
     * @param file      File in which the asset content will be saved
     * @return
     * @throws IOException
     */
    public static boolean saveAssetsData(Context context, String assetName, File file)
            throws IOException {
        boolean complete = false;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getAssets().open(assetName);
            out = new FileOutputStream(file);
            IOUtils.copy(in, out);
            out.flush();
            complete = true;

        } finally {
            close(in);
            close(out);
        }
        return complete;
    }


    /**
     * copy the file
     *
     * @param src - source
     * @param dst - destination
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            inChannel = in.getChannel();
            outChannel = out.getChannel();

            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            close(inChannel);
            close(in);
            close(outChannel);
            close(out);
        }
    }

    /**
     * Save text to a file
     *
     * @param text  - string text to save
     * @param fName - path to file
     * @throws IOException
     */
    public static final void writeToFile(String text, String fName) throws IOException {
        BufferedWriter bos = null;
        try {
            File f = new File(fName);
            if (f.exists()) {
                f.delete();
            }

            bos = new BufferedWriter(new FileWriter(fName));
            bos.write(text);
            bos.flush();
        } finally {
            close(bos);
        }
    }

    /**
     * Reading a text file
     *
     * @param fName - path to file
     * @return string or null, if there was an error reading
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static String getFileData(String fName) throws FileNotFoundException, IOException {
        return readString(new FileInputStream(fName));
    }

    /**
     * Delete files
     *
     * @param files
     */
    public static void deleteFiles(String path, String[] files) {
        if (files != null) {
            for (String fName : files) {
                File f = new File(path, fName);
                if (f.exists()) {
                    f.delete();
                }

            }
        }
    }

    /**
     * Create the directory if it does not
     *
     * @param Path
     */
    public static void createDir(String Path) {
        File dir = new File(Path);
        if (!dir.exists()) {
            makeDirs(dir);
        }
    }

    /**
     * Create the directory if it does not
     *
     * @param fPath
     */
    public static void createDir(File fPath) {
        if (fPath != null && !fPath.exists()) {
            makeDirs(fPath);
        }
    }

    private static void makeDirs(File fPath) {
        try {
            fPath.mkdirs();
        } catch (Exception e) {
            Dbg.printStackTrace(e);
        }
    }

    /**
     * Delete a single file named
     *
     * @param fName
     */
    public static void deleteFile(String fName) {
        File f = new File(fName);
        if (f.exists()) {
            f.delete();
        }
    }

    public static boolean removeDirectory(File directory) {
        if (directory == null)
            return false;
        if (!directory.exists())
            return true;
        if (!directory.isDirectory())
            return false;

        String[] list = directory.list();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(directory, list[i]);

                if (entry.isDirectory()) {
                    if (!removeDirectory(entry)) {
                        return false;
                    }

                } else {
                    if (!entry.delete()) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }

    /**
     * From a single file from the archive (first)
     */
    public static File unZipFirst(File fZip) throws IOException {
        File fResult = null;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(fZip);
            ZipEntry entry = null;

            // only the first and not the directory
            for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements(); ) {
                entry = (ZipEntry) e.nextElement();
                if (entry != null && !entry.isDirectory()) {
                    break;
                }
            }

            if (entry != null) {

                fResult = new File(fZip.getParent(), entry.getName());

                BufferedInputStream inputStream =
                        new BufferedInputStream(zipfile.getInputStream(entry));
                BufferedOutputStream outputStream =
                        new BufferedOutputStream(new FileOutputStream(fResult));

                try {
                    IOUtils.copy(inputStream, outputStream);
                } finally {
                    close(outputStream);
                    close(inputStream);
                }

            }

        } finally {
            if (zipfile != null) {
                zipfile.close();
            }
        }
        return fResult;
    }

    /**
     * Pack one file into archive
     *
     * @param fName
     * @return
     * @throws IOException
     */
    public static boolean zipFile(File output, File fName) throws IOException {
        boolean isOk = false;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        try {

            if (fName.exists() && fName.canRead()) {

                zipOut = new ZipOutputStream(new FileOutputStream(output));
                zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

                zipOut.putNextEntry(new ZipEntry(fName.getName()));

                fis = new FileInputStream(fName);

                byte[] buffer = new byte[4092];
                int byteCount = 0;
                while ((byteCount = fis.read(buffer)) != -1) {
                    zipOut.write(buffer, 0, byteCount);
                }

                zipOut.closeEntry();
                zipOut.flush();

                isOk = true;
            }

        } finally {
            close(fis);
            close(zipOut);
        }
        return isOk;
    }

    /**
     * Closes the closeable object with exception handling.
     *
     * @param closeable object to close
     * @return true, if successfully closed
     */
    public static boolean close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            Dbg.printStackTrace(e);
            // ignoring exception, can't do anything
            return false;
        }
        return true;
    }

    /**
     * Closes the Cursor object with exception handling. On Api 15 and below Cursor does not
     * implements closable see https://github.com/haku/Onosendai/issues/​85
     *
     * @param cursor object to close
     * @return true, if successfully closed
     */
    public static boolean close(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Dbg.printStackTrace(e);
            // ignoring exception, can't do anything
            return false;
        }
        return true;
    }

    /**
     * Returns the file extension for the given file name, or the empty string if the file has no
     * extension. The result does not include the '{@code .}'.
     */
    public static String getFileExtension(String fullName) {
        return getFileExtension(new File(fullName));
    }

    /**
     * Returns the file extension for the given file name, or the empty string if the file has no
     * extension. The result does not include the '{@code .}'.
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * calculate the checksum of the first few bytes of the file
     *
     * @param fName        the path to the file
     * @param checkSumSize the size in bytes of the checksum if the file size is less than- use it for 10
     *                     k-checksum number 9-10 characters
     */
    public static long calculateCheckSum(String fName, long checkSumSize) throws IOException {
        long checkSum = 0;
        FileInputStream fis = null;
        CheckedInputStream cis = null;
        Adler32 adler = null;
        long sizeOfFile = 0;

        try {

            fis = new FileInputStream(fName);
            adler = new Adler32();
            cis = new CheckedInputStream(fis, adler);
            sizeOfFile = new File(fName).length();

            if (sizeOfFile < checkSumSize) {
                checkSumSize = sizeOfFile;
            }

            byte[] buffer = new byte[(int) checkSumSize];
            if (cis.read(buffer) >= 0) {
                checkSum = cis.getChecksum().getValue();
            }

        } finally {
            close(fis);
            close(cis);
        }
        return checkSum;
    }

    private IOHelper() {
    }
}
