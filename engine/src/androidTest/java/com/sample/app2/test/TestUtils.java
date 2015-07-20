package com.sample.app2.test;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import ru.profi1c.engine.util.IOHelper;

public final class TestUtils {

    public static File extractAssert(Context context, String assertPath, String fPath)
            throws IOException {
        File file = new File(fPath);
        if (!file.exists()) {
            if (IOHelper.saveAssetsData(context, assertPath, file)) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    private TestUtils() {

    }
}
