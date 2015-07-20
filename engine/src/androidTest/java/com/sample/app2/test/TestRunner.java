package com.sample.app2.test;

import android.test.InstrumentationTestRunner;
import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.TestSuite;

public class TestRunner extends InstrumentationTestRunner {

    @Override
    public TestSuite getAllTests() {

        //check run arguments
        //Bundle arguments = getArguments();
        //String licenseLicenseTestName = arguments.getString("LicenseTest", "");

        TestSuite suite = null;

        TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(TestRunner.class.getName(),
                                                                 getTargetContext()
                                                                         .getClassLoader());
        // add all tests in application
        testSuiteBuilder.includePackages("");
        suite = testSuiteBuilder.build();

        // add specific tests whose names do not begin to 'test'
        //suite.addTest(new LicenseTest("perDeviceTest"));

        return suite;
    }
}
