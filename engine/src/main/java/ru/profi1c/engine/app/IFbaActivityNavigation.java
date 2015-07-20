package ru.profi1c.engine.app;

import android.content.Intent;

public interface IFbaActivityNavigation {

    /**
     * Перейти к основной активности (Activity)
     */
    void goHome(FbaActivity fbaActivity);

    /**
     * Помощник доступа к намерению для запуска основной активности (Activity)
     */
    Intent getHomeIntent();

    /**
     * Возвращает истина, если текущая активити является главной
     */
    boolean isMainActivity();

    /**
     * Открыть настройки программы. Активити, которая используется для
     * редактирования настроек задается в App.getPreferenceActivityClass()
     */
    void showPreferenceActivity();

    /**
     * Открыть диалог авторизации пользователя при запуске программы
     */
    void showLoginActivity(int requestCode);
}
