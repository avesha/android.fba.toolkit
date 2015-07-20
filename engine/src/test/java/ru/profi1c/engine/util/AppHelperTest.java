package ru.profi1c.engine.util;

import org.junit.Test;

import ru.profi1c.engine.test.RobolectricTestCase;

import static org.junit.Assert.*;

public class AppHelperTest extends RobolectricTestCase {

    @Test
    public void testGetSignaturesHashCode() throws Exception {
        int hash = AppHelper.getSignaturesHashCode(getTargetContext());
        assertEquals(0, hash);
    }

    @Test
    public void testGetAppLabel() throws Exception {
        //library does not have app label
        final String label = AppHelper.getAppLabel(getTargetContext());
        assertNull(label);
    }

    @Test
    public void testGetAppVersion() throws Exception {
        final String version = AppHelper.getAppVersion(getTargetContext());
        assertEquals(getProperty("versionName"), version);
    }

    @Test
    public void testGetAppVersionCode() throws Exception {
        final int code = AppHelper.getAppVersionCode(getTargetContext());
        assertEquals(Integer.parseInt(getProperty("versionCode")), code);
    }

}