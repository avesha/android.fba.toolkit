package com.sample.app2;

import com.sample.app2.test_action.ITestAction;
import com.sample.app2.test_action.ITestActionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TAPAdapters implements ITestActionProvider {

    @Override
    public List<ITestAction> getActions() {
        List<ITestAction> lst = new ArrayList<>();
        lst.add(new TAPresentationAdapter.TestActionShow());
        lst.add(new TASimpleHierarchyAdapter.TestActionShow());
        lst.add(new TAHierarchyAdapter.TestActionShow());
        lst.add(new TAMetaArrayAdapter.TestActionShow());
        lst.add(new TAMetaCursorAdapter.TestActionShow());
        lst.add(new TAMetaExpandableListAdapter.TestActionShow());
        lst.add(new TAParentExpandableCursorAdapter.TestActionShow());
        return Collections.unmodifiableList(lst);
    }

    @Override
    public String getDescription() {
        return "Adapters";
    }
}
