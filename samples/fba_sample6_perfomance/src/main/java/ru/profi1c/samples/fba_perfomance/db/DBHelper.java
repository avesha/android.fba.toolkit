/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.util.IOHelper;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Помощник для работы с базой данных Sqlite (создание, обновление, открытие, чтение данных и т.п.)
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 *
 */
public class DBHelper extends DBOpenHelper {

	/*
	 * Имя файла вашей базы данных
	 */
	protected static final String DATABASE_NAME = "database.db";
	
	/*
	 * Номер версии вашей базы данных, если вы вносите изменения в объекты базы
	 * данных, вам, возможно, так же придется увеличить версию базы данных.
	 */
	protected static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context,DATABASE_NAME,DATABASE_VERSION);
	}

	private static File getDBFile(Context context) {
        return new File(Environment.getDataDirectory() + "/data/" + context.getPackageName()
				+ "/databases/" + DATABASE_NAME);
	}

    public static boolean backupToExternalStorage(Context context) {

        boolean success = false;
        File backupDir = FbaApplication.from(context).getAppSettings().getBackupDir();
        if (backupDir != null) {

            if(!backupDir.exists()){
                backupDir.mkdirs();
            }

            File backupDbFile = new File(backupDir, String.format("backup_%d_%s",
                    System.currentTimeMillis(), DATABASE_NAME));
            File dbFile = getDBFile(context);
            try {
                IOHelper.copyFile(dbFile, backupDbFile);
                success = true;
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            }
        }
        return success;
    }

}
