package ru.profi1c.engine.app;

import ru.profi1c.engine.meta.DBOpenHelper;

public interface IFbaAppInfoProvider extends IFbaSettingsProvider {

    /**
     * Класс основной активности (Activity) вашего приложения. Как правило, это
     * Activity первой отображаемая при запуске вашего приложения .
     */
    Class<? extends FbaActivity> getMainActivityClass();

    /**
     * Класс активити используемой для редактирования настроек программы,
     * например {@link ru.profi1c.engine.app.FbaSimplePreferenceActivity}
     */
    Class<? extends FbaPreferenceActivity> getPreferenceActivityClass();

    /**
     * Класс активити используемой для авторизации пользователя при входе в
     * программу, например {@link ru.profi1c.engine.app.FbaSecurityNumericActivity} Вы можете сделать
     * свою реализацию окна авторизации, основное условие — ваша активити должна
     * устанавливать setResult(RESULT_OK) если процедура проверки завершена
     * успешно. Если null - пароль не будет запрашиваться
     */
    Class<? extends FbaActivity> getLoginActivityClass();

    /**
     * Класс в котором реализована работа с базой данных Sqlite программы
     */
    Class<? extends DBOpenHelper> getDBHelperClass();

}
