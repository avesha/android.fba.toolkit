package com.sample.app2;

import com.sample.app2.test_action.ITestAction;
import com.sample.app2.test_action.ITestActionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TAPFiledView implements ITestActionProvider {
    @Override
    public List<ITestAction> getActions() {
        List<ITestAction> lst = new ArrayList<>();
        lst.add(new TAFieldView.TestActionShowBaseSample());
        lst.add(new TAFieldView2.TestActionShowCustomFields());
        return Collections.unmodifiableList(lst);
    }

    @Override
    public String getDescription() {
        return "FieldView";
    }
}
