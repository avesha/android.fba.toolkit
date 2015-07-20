package com.sample.app2.util;

import android.content.Context;
import android.content.Intent;

import com.sample.app2.App;
import com.sample.app2.test_action.ITestAction;
import com.sample.app2.test_action.ITestActionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.util.NotificationHelper;

public class TAPNotification implements ITestActionProvider {
    @Override
    public List<ITestAction> getActions() {
        List<ITestAction> lst = new ArrayList<>();
        lst.add(new NotificationAction("Download new version", NotificationAction.Type.DownloadNewVersion));
        lst.add(new NotificationAction("Exchange success", NotificationAction.Type.ExchangeSuccess));
        lst.add(new NotificationAction("Exchange error", NotificationAction.Type.ExchangeError));
        return Collections.unmodifiableList(lst);
    }

    @Override
    public String getDescription() {
        return "Notifications";
    }

    private static class NotificationAction implements ITestAction {

        private enum Type {DownloadNewVersion, ExchangeSuccess, ExchangeError}

        private final String mDescription;
        private final Type mType;

        private NotificationAction(String desc, Type type) {
            mDescription = desc;
            mType = type;
        }

        @Override
        public void run(Context context) {
            App app = (App) context.getApplicationContext();
            BaseExchangeSettings settings = app.getExchangeSettings();

            switch (mType) {
                case DownloadNewVersion:
                    NotificationHelper.showDownloadNewVersion(context, 1,
                            settings.getSoundDownloadedApk());
                    break;
                case ExchangeSuccess:
                    NotificationHelper.showExchangeSuccess(context, 2, app.getHomeIntent(),
                            settings.getSoundExchangeSuccess());
                    break;
                case ExchangeError:
                    NotificationHelper.showExchangeError(context, 3, new Intent(),
                            settings.getSoundExchangeError());
                    break;

            }
        }

        @Override
        public String getDescription() {
            return mDescription;
        }
    }
}
