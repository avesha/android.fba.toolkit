package com.sample.app2.test.exchange;

import android.content.Context;
import android.text.TextUtils;

import ru.profi1c.engine.R;
import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.exchange.IExchangeDataProvider;
import ru.profi1c.engine.exchange.IExchangeDataProviderFactory;

public class TestDataProviderFactory implements IExchangeDataProviderFactory {

    @Override
    public IExchangeDataProvider create(BaseExchangeSettings settings) {
        checkSettings(settings);
        return new TestExchangeDataProvider(settings);
    }

    /**
     * Проверить настройки
     */
    private void checkSettings(BaseExchangeSettings settings) {
        final Context context = settings.getContext();
        if (TextUtils.isEmpty(settings.getDeviceId())) {
            throw new IllegalStateException(
                    context.getString(R.string.fba_msg_err_device_not_specified));
        }

        if (TextUtils.isEmpty(settings.getAppId())) {
            throw new IllegalStateException(
                    context.getString(R.string.fba_msg_err_application_id_not_specified));
        }

        if (TextUtils.isEmpty(settings.getServerIP())) {
            throw new IllegalStateException(
                    context.getString(R.string.fba_msg_err_ip_address_not_specified));
        }

        if (TextUtils.isEmpty(settings.getUserName())) {
            throw new IllegalStateException(
                    context.getString(R.string.fba_msg_err_user_name_device_not_specified));
        }
    }
}
