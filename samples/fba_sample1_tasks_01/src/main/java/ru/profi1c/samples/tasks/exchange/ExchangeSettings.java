/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tasks.exchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.util.PhoneHelper;
import ru.profi1c.samples.tasks.R;

/**
 * Настройки обмена с web-сервисом 1С: адрес сервера, данные авторизации и
 * прочее
 * 
 * @author ООО "Сфера" (support@sfera.ru)
 * 
 */
public class ExchangeSettings extends BaseExchangeSettings {

	// Singleton variant: Double Checked Locking & volatile
	private static volatile ExchangeSettings instance;

	protected ExchangeSettings(Context ctx) {
		super(ctx);

		// set default server ip from settings
		if (TextUtils.isEmpty(getServerIP())) {
			setServerIP(ctx.getString(R.string.fba_ws_server));
		}

	}

	public static ExchangeSettings getInstance(Context context) {
		ExchangeSettings localInstance = instance;

		if (localInstance == null) {
			synchronized (ExchangeSettings.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new ExchangeSettings(context);
				}
			}
		}

		return localInstance;
	}

	@Override
	public String getDeviceId() {
		/*
		 * Внимание! Для получения IMEI необходимо указать в манифесте
		 * разрешение READ_PHONE_STATE
		 */
		return PhoneHelper.getSerialNumber(getContext(),
				PhoneHelper.DeviceIdType.IMEI);
	}

	@Override
	public String getAppId() {
		return getContext().getString(R.string.app_id);
	}

	@Override
	protected int getAppVersion() {
		return AppHelper.getAppVersionCode(getContext());
	}

	@Override
	protected String getAppSpace() {
		return getContext().getString(R.string.fba_ws_app_namespace);
	}

	@Override
	protected String getServiceName() {
		return getContext().getString(R.string.fba_ws_service_name);
	}

	@Override
	protected int getConnectionTimeout() {
		return DEFAULT_TIMEOUT;
	}

	@Override
	protected void onSave(Editor editor) {

	}

	@Override
	protected void onRead(SharedPreferences preferences) {

	}
	
}