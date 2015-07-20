/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2;

import com.sample.app2.db.DBHelper;
import com.sample.app2.db.MetaHelper;
import com.sample.app2.exchange.ExchangeSettings;

import ru.profi1c.engine.app.BaseAppSettings;
import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.app.FbaPreferenceActivity;
import ru.profi1c.engine.app.FbaSecurityNumericActivity;
import ru.profi1c.engine.app.FbaSimplePreferenceActivity;
import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.MetadataHelper;

/**
 * Основной класс приложения (имя указано в AndroidManifest.xml),
 * устанавливаются основные настройки компоненты 'FBA'
 * 
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 * 
 */
public class App extends FbaApplication {

	/**
	 * Возвращает класс основной активности (Activity) вашего приложения. Как правило, это
	 * Activity первой отображаемая при запуске вашего приложения .
	 */
	@Override
	public Class<? extends FbaActivity> getMainActivityClass() {
		return MainActivity.class;
	}
	
	/**
	 * Возвращает класс активити, используемой для редактирования настроек программы,
	 * например {@link FbaSimplePreferenceActivity}
	 */
	@Override
	public Class<? extends FbaPreferenceActivity> getPreferenceActivityClass() {
		return FbaSimplePreferenceActivity.class;
	}

	/**
	 * Возвращает класс активити, используемой для авториазции пользователя при входе в
	 * программу, например FbaSecurityNumericActivity. Вы можете сделать
	 * свою реализацию окна авторизации, основное условие — ваша активити должна
	 * устанавливать setResult(RESULT_OK) если процедура проверки завершена
	 * успешно. Если null - пароль не будет запрашиватся
	 */
	@Override
	public Class<? extends FbaActivity> getLoginActivityClass() {
		return FbaSecurityNumericActivity.class;
	}

	/**
	 * Возвращает класс, в котором реализована работа с базой данных Sqlite программы
	 */
	@Override
	public Class<? extends DBOpenHelper> getDBHelperClass() {
		return DBHelper.class;
	}

	/**
	 * Возвращает помощник для работы с метаданными объектов
	 */
	@Override
	public MetadataHelper getMetadataHelper() {
		return MetaHelper.getInstance();
	}

	/**
	 * Возвращает настройки обмена  web-сервисом 1С
	 */
	@Override
	public BaseExchangeSettings getExchangeSettings() {
		return ExchangeSettings.getInstance(getApplicationContext());
	}

	/**
	 * Возвращает настройки приложения
	 */
	@Override
	public BaseAppSettings getAppSettings() {
		return AppSettings.getInstance(getApplicationContext());
	}
	
}
