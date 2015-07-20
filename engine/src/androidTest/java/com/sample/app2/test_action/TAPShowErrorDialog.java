package com.sample.app2.test_action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sample.app2.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.app.FbaActivityDialog;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.util.NotificationHelper;

public class TAPShowErrorDialog implements ITestActionProvider {

    @Override
    public List<ITestAction> getActions() {
        List<ITestAction> lst = new ArrayList<ITestAction>();
        lst.add(new TestActionShowErrorActivityShort());
        lst.add(new TestActionShowErrorActivityLong());
        lst.add(new TestActionShowErrorActivityNotify());
        return Collections.unmodifiableList(lst);
    }

    @Override
    public String getDescription() {
        return "Show error dialog";
    }

    private static class TestActionShowErrorActivityShort implements ITestAction {

        @Override
        public void run(Context context) {
            Intent i = FbaActivityDialog.getStartIntent(context,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi pretium elit in tellus fringilla pulvinar.");
            context.startActivity(i);
        }

        @Override
        public String getDescription() {
            return "Show error activity (short message)";
        }
    }

    private static class TestActionShowErrorActivityLong implements ITestAction {

        @Override
        public void run(Context context) {
            Intent i = FbaActivityDialog.getStartIntent(context,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi pretium elit in tellus fringilla pulvinar. " +
                    "Phasellus eleifend nibh nec dictum tristique. Curabitur cursus hendrerit tellus, at congue massa tincidunt scelerisque. " +
                    "Vestibulum justo tellus, laoreet et elit tristique, ornare tincidunt urna. Quisque dictum eros porttitor nisi tristique pharetra. " +
                    "Nulla facilisi. Ut fringilla ante et vestibulum consectetur. Proin sollicitudin condimentum suscipit. " +
                    "Duis augue est, dapibus euismod tincidunt eu, venenatis a felis. " +
                    "Aliquam pellentesque sem quam, ac cursus quam finibus sed. Vivamus eget turpis in ipsum molestie tristique.\n" +
                    "\n" +
                    "Proin eu enim dui. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum justo nulla, congue at placerat in, " +
                    "viverra in tortor. Phasellus turpis odio, consequat eu justo vel, auctor luctus arcu. Proin et justo tempus tortor commodo " +
                    "tincidunt. Nunc tempus ac dolor sed dictum. Vivamus faucibus a nisl et imperdiet. Vivamus ac ornare lorem, vel bibendum nulla. " +
                    "Curabitur finibus blandit mi, vel feugiat lacus blandit id. Nunc eu fringilla arcu, vel ultrices arcu.");
            context.startActivity(i);
        }

        @Override
        public String getDescription() {
            return "Show error activity (long message)";
        }
    }

    private static class TestActionShowErrorActivityNotify implements ITestAction {

        @Override
        public void run(Context context) {
            FbaApplication app = App.from(context);

            Uri uriSound = app.getExchangeSettings().getSoundExchangeSuccess();
            Intent iStart = FbaActivityDialog.getStartIntent(context,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi pretium elit in tellus fringilla pulvinar.");
            NotificationHelper.showExchangeError(context, Const.NOTIFICATION_ID_FINISH_EXCHANGE,
                    iStart, uriSound);
        }

        @Override
        public String getDescription() {
            return "Show error dialog (notification)";
        }
    }
}
