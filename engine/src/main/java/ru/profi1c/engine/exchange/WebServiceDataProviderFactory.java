package ru.profi1c.engine.exchange;

import android.content.Context;
import android.text.TextUtils;

import ru.profi1c.engine.R;

public class WebServiceDataProviderFactory implements IExchangeDataProviderFactory {

    @Override
    public IExchangeDataProvider create(BaseExchangeSettings settings) {
        checkSettings(settings);
        return createDataProvider(settings);
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

    /**
     * Инициализация web-сервиса
     */
    private WebService createDataProvider(BaseExchangeSettings settings) {
        WebService ws;

        if (TextUtils.isEmpty(settings.getServiceName())) {
            ws = new WebService(settings.getServerIP(), settings.getAppSpace());
        } else {
            ws = new WebService(settings.getServerIP(), settings.getAppSpace(),
                                settings.getServiceName());
        }
        ws.setConnectionTimeout(settings.getConnectionTimeout());
        return ws;
    }
}
