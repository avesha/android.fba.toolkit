/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2;

import android.content.Context;

import java.io.File;

import ru.profi1c.engine.app.BaseAppSettings;
import ru.profi1c.engine.util.AppHelper;
import ru.profi1c.engine.util.StorageHelper;

/**
 * Глобальные настройки вашего приложения
 *
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
public class AppSettings extends BaseAppSettings {

	//Singleton variant: Double Checked Locking & volatile
	private static volatile AppSettings instance;

	protected AppSettings(Context context){
		super(context);
	}

	public static AppSettings getInstance(Context context) {
		AppSettings localInstance = instance;

		if (localInstance == null) {
			synchronized (AppSettings.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new AppSettings(context);
				}
			}
		}

		return localInstance;
	}

	/**
	 * Возвращает каталог приложения, приоритет у внешнего хранилища
	 */
	@Override
	public File getAppDir() {

		File dir = null;
		if (AppHelper.isAppInstalledToSDCard(getContext())
				&& StorageHelper.isExternalStorageAvailable()) {

			dir = StorageHelper.getExternalAppPath(getContext());
		}
		if (dir == null)
			dir = getContext().getFilesDir();

		return dir;
	}

	/**
	 * Возвращает каталог для временных файлов приложения, с приоритетом на
	 * внешнее хранилище
	 */
	@Override
	public File getCacheDir() {

		File dir = null;
		if (AppHelper.isAppInstalledToSDCard(getContext())
				&& StorageHelper.isExternalStorageAvailable()) {

			dir = StorageHelper.getExternalAppCashe(getContext());
		}
		if (dir == null)
			dir = getContext().getCacheDir();

		return dir;
	}

	/**
	 * Возвращает каталог для бекапа на внешнее хранилище, или null если
	 * хранилище не подключено
	 */
	@Override
	public File getBackupDir() {
		File dir = null;
		if (StorageHelper.isExternalStorageAvailable()) {
			dir = StorageHelper.getAppBackup(getContext());
		}
		return dir;
	}

	/**
	 * Если установлен, то при возникновении ошибки времени исполнения программа
	 * будет автоматически сохранять стек ошибки в файл. Затем, при процедуре
	 * обмена с сервером, ошибки будут переданы на сервер для анализа.
	 *
	 * @return истина, если используется свой обработчик ошибок.
	 */
	@Override
	public boolean customExceptionHandler() {
		return true;
	}

	/**
	 * Если true, пользователю будет выводится максимально детальное описание
	 * ошибки включая stack trace
	 *
	 * @return
	 */
	@Override
	public boolean isFullErrorStack() {
		return true;
	}

	/**
	 * Идентификатор иконки приложения
	 *
	 * @return
	 */
	@Override
	public int getIdResIconLauncher() {
		return R.drawable.ic_launcher;
	}

	/**
	 * Принудительное обновление. Если при запуске программы
	 * обнаружена новая версия и пользователь откажется выполнять обновление –
	 * программа завершает свою работу.
	 *
	 * @return
	 */
	@Override
	public boolean isForceUpdateApp() {
		return true;
	}

}