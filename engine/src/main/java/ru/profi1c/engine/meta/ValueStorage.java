package ru.profi1c.engine.meta;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.util.IOHelper;

/**
 * Хранилище значения
 */
public final class ValueStorage implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String FIELD_NAME_RECORD_KEY = "RecordKey";

    public byte[] data;

    public ValueStorage() {

    }

    public ValueStorage(byte[] bytes) {
        data = bytes;
    }

    public ValueStorage(InputStream in) throws IOException {
        data = IOUtils.toByteArray(in);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * Сохранить данные в файл
     *
     * @param file
     * @return
     */
    public boolean writeToFile(File file) {

        boolean complete = false;

        if (data.length > 0) {

            file.delete();
            FileOutputStream out = null;

            try {
                out = new FileOutputStream(file);
                out.write(data);
                complete = true;
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            } finally {
                IOHelper.close(out);
            }
        }
        return complete;
    }

    /**
     * Преобразовать как Bitmap
     *
     * @return Bitmap или null если декодирование не удалось
     */
    public Bitmap toBitmap() {
        if (data != null) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }
}
