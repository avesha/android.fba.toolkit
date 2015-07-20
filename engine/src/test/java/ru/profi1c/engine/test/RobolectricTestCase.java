package ru.profi1c.engine.test;

import android.content.Context;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

@RunWith(CustomRobolectricRunner.class)
public abstract class RobolectricTestCase {

    private static final String TEST_TAG = "fba-toolkit-test";
    private static final String TEST_PROPERTIES = "test.properties";

    private HashMap<String, String> mProperties;

    public static void log(String TAG, String text) {
        Log.d(TEST_TAG, String.format("%s.%s", TAG, text));
    }

    @Before
    public void setUp() throws Exception {
        mProperties = new HashMap<String, String>();
        readTestProperties();

        System.setProperty("robolectric.logging", "stdout");
    }

    @After
    public void tearDown() throws Exception {

    }

    private void readTestProperties() throws IOException {
        URL url = this.getClass().getClassLoader().getResource(TEST_PROPERTIES);
        File file = new File(url.getFile());
        if (file.exists()) {
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                String value = properties.getProperty(key);
                mProperties.put(key, value);
            }
        }
    }

    protected String getProperty(final String key) {
        return mProperties.get(key);
    }

    protected Context getContext() {
        //TODO: get test context
        return RuntimeEnvironment.application;
    }

    protected Context getTargetContext() {
        return RuntimeEnvironment.application;
    }


}
