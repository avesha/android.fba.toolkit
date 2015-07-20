package ru.profi1c.engine.map.mapsforge;

import android.test.ApplicationTestCase;

import com.sample.map.mapsforge.App;

public class ApplicationTest extends ApplicationTestCase<App> {

    private App mApp;

    public ApplicationTest() {
        super(App.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        mApp = getApplication();
    }

    public void testPreconditions() {
        assertNotNull("App is null", mApp);
    }

    public void testSettings() {
        assertNotNull(mApp.getDBHelperClass());
        assertNotNull(mApp.getAppSettings());
        assertNotNull(mApp.getExchangeSettings());
    }
}