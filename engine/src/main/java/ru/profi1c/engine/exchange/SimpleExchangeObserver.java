package ru.profi1c.engine.exchange;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import ru.profi1c.engine.app.FbaActivityDialog;
import ru.profi1c.engine.app.FbaApplication;

/**
 * Простой наблюдатель за процедурой обмена. Отображает уведомления с установкой
 * действия по клику: <br>
 * При получении новой версии программы — нет действия по клику на уведомлении; <br>
 * При ошибке — по клику отображает диалог с ошибкой; <br>
 * При успешном обмене — по клику откроется главная активность (Activity)
 * программы. <br>
 * <br>
 * Звуки для уведомлений устанавливаются на основании настроек из
 * FbaApplication.getExchangeSettings().
 */
public class SimpleExchangeObserver extends NotificationExchangeObserver {

    private final FbaApplication mApp;
    private final BaseExchangeSettings mExchangeSettings;
    private String mErrorMsg;

    public SimpleExchangeObserver(Context context, Handler handler) {
        super(context, handler);
        mApp = FbaApplication.from(context);
        mExchangeSettings = mApp.getExchangeSettings();
    }

    @Override
    public Uri getSoundDownloadedApk() {
        return mExchangeSettings.getSoundDownloadedApk();
    }

    @Override
    public Uri getSoundExchangeError() {
        return mExchangeSettings.getSoundExchangeError();
    }

    @Override
    public Uri getSoundExchangeSuccess() {
        return mExchangeSettings.getSoundExchangeSuccess();
    }

    @Override
    public Intent getIntentOnSuccess() {
        return mApp.getHomeIntent();
    }

    @Override
    public Intent getIntentOnError() {
        return FbaActivityDialog.getStartIntent(getContext(), mErrorMsg);
    }

    @Override
    public void onStart(ExchangeVariant variant) {

    }

    @Override
    public void onBuild() {

    }

    @Override
    public void onStepInfo(String msg) {

    }

    @Override
    public void onError(String msg) {
        mErrorMsg = msg;
    }

}
