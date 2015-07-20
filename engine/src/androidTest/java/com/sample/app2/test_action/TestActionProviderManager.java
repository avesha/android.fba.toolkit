package com.sample.app2.test_action;

import android.content.Context;
import android.content.Intent;

import com.sample.app2.TAPAdapters;
import com.sample.app2.TAPFiledView;
import com.sample.app2.TAPObjectView;
import com.sample.app2.TAPSimpleCatalogFragment;
import com.sample.app2.TAPSimpleCatalogListActivity;
import com.sample.app2.TAPSimpleDocumentFragment;
import com.sample.app2.TAPSimpleDocumentListActivity;
import com.sample.app2.reports.TAReports;
import com.sample.app2.util.TAPInputDialog;
import com.sample.app2.util.TAPNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TestActionProviderManager {

    private static List<ITestActionProvider> sListActions;

    public static List<ITestActionProvider> getActionProviders() {
        if (sListActions == null) {
            List<ITestActionProvider> lst = new ArrayList<ITestActionProvider>();
            lst.add(new TAPAdapters());
            lst.add(new TAPSimpleCatalogListActivity());
            lst.add(new TAPSimpleDocumentListActivity());
            lst.add(new TAPSimpleCatalogFragment());
            lst.add(new TAPSimpleDocumentFragment());
            lst.add(new TAPObjectView());
            lst.add(new TAPFiledView());
            lst.add(new TAReports.TAPReports());
            lst.add(new TAPShowErrorDialog());
            lst.add(new TAPNotification());
            lst.add(new TAPInputDialog());
            sListActions = Collections.unmodifiableList(lst);
        }
        return sListActions;
    }

    public static ITestActionProvider getActionProvider(int index) {
        return getActionProviders().get(index);
    }

    public static void showActions(Context context, ITestActionProvider testActionProvider) {
        int index = getActionProviders().indexOf(testActionProvider);
        Intent i = TestActionProviderActivity.getStartIntent(context, index);
        context.startActivity(i);
    }

    private TestActionProviderManager() {

    }

    public static class TestActionShowActions implements ITestAction {

        private final ITestActionProvider mTestActionProvider;

        public TestActionShowActions(ITestActionProvider testActionProvider) {
            mTestActionProvider = testActionProvider;
        }

        @Override
        public void run(Context context) {
            showActions(context, mTestActionProvider);
        }

        @Override
        public String getDescription() {
            return mTestActionProvider.getDescription();
        }
    }
}
