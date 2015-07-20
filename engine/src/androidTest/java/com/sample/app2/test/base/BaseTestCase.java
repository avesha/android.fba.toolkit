package com.sample.app2.test.base;

import com.sample.app2.App;
import com.sample.app2.test.TestUtils;

import java.io.File;
import java.io.IOException;

public abstract class BaseTestCase extends AndroidTestCase {

    private App mApp;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mApp = (App) getTargetContext().getApplicationContext();
        assertNotNull("App is null", mApp);
    }

    public App getApp() {
        return mApp;
    }

    protected File extractAssert(String assertPath, String fPath) throws IOException {
        return TestUtils.extractAssert(getTargetContext(), assertPath, fPath);
    }
}
