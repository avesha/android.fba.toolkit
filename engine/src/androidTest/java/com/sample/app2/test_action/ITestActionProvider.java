package com.sample.app2.test_action;

import java.util.List;

import ru.profi1c.engine.app.ui.IDescription;

public interface ITestActionProvider extends IDescription {
    List<ITestAction> getActions();
}
