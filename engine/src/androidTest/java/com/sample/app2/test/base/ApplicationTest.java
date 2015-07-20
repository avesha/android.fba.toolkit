package com.sample.app2.test.base;

import android.test.ApplicationTestCase;

import com.sample.app2.App;

import ru.profi1c.engine.app.IFbaSettingsProvider;

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
        assertNotNull("App is null", mApp);
    }

    public void testSettings() {
        assertNotNull(mApp.getMainActivityClass());
        assertNotNull(mApp.getPreferenceActivityClass());
        assertNotNull(mApp.getLoginActivityClass());
        assertNotNull(mApp.getDBHelperClass());
        assertNotNull(mApp.getMetadataHelper());
        assertNotNull(mApp.getAppSettings());
        assertNotNull(mApp.getExchangeSettings());
    }

    public void testSettingProvider() {
        IFbaSettingsProvider provider = mApp.getFbaSettingsProvider();
        assertNotNull(provider);

        assertEquals(mApp.getExchangeSettings(), provider.getExchangeSettings());
        assertEquals(mApp.getAppSettings(), provider.getAppSettings());
        assertEquals(mApp.getMetadataHelper(), provider.getMetadataHelper());
      }
}