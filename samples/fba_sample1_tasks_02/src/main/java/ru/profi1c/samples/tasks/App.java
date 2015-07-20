/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.tasks;

import ru.profi1c.engine.app.BaseAppSettings;
import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.app.FbaPreferenceActivity;
import ru.profi1c.engine.app.FbaSecurityNumericActivity;
import ru.profi1c.engine.app.FbaSimplePreferenceActivity;
import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.MetadataHelper;

import ru.profi1c.samples.tasks.db.DBHelper;
import ru.profi1c.samples.tasks.db.MetaHelper;
import ru.profi1c.samples.tasks.exchange.ExchangeSettings;

/**
 * Основной класс приложения (имя указано в AndroidManifest.xml),
 * устанавливаются основные настройки компоненты 'FBA'
 * 
 * @author ООО "Сфера" (support@sfera.ru)
 * 
 */
public class App extends FbaApplication {

	/**
	 * Класс основной активности (Activity) вашего приложения. Как правило, это
	 * Activity первой отображаемая при запуске вашего приложения .
	 * 
	 * @return
	 */
	@Override
	public Class<? extends FbaActivity> getMainActivityClass() {
		return MainActivity.class;
	}
	
	/**
	 * Класс активити используемой для редактирования настроек программы,
	 * например {@link FbaSimplePreferenceActivity}
	 * 
	 * @return
	 */
	@Override
	public Class<? extends FbaPreferenceActivity> getPreferenceActivityClass() {
		return FbaSimplePreferenceActivity.class;
	}

	/**
	 * Класс активити используемой для авториазции пользователя при входе в
	 * программу, например FbaSecurityNumericActivity.class. Вы можете сделать
	 * свою реализацию окна авторизации, основное условие — ваша активити должна
	 * устанавливать setResult(RESULT_OK) если процедура проверки завершена
	 * успешно. Если null - пароль не будет запрашиватся
	 * 
	 * @return
	 */
	@Override
	public Class<? extends FbaActivity> getLoginActivityClass() {
		return FbaSecurityNumericActivity.class;
	}

	/**
	 * Класс в котором реализована работа с базой данных Sqlite программы
	 * 
	 * @return
	 */
	@Override
	public Class<? extends DBOpenHelper> getDBHelperClass() {
		return DBHelper.class;
	}

	/**
	 * Помощник для работы с метаданными объектов
	 * @return
	 */
	@Override
	public MetadataHelper getMetadataHelper() {
		return MetaHelper.getInstance();
	}

	/**
	 * Настройки обмена  web-сервисом 1С
	 * @return
	 */
	@Override
	public BaseExchangeSettings getExchangeSettings() {
		return ExchangeSettings.getInstance(getApplicationContext());
	}

	/**
	 * Настройки приложения
	 * @return
	 */
	@Override
	public BaseAppSettings getAppSettings() {
		return AppSettings.getInstance(getApplicationContext());
	}
	
}
