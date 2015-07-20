package ru.profi1c.engine.app;

import android.text.TextUtils;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.exchange.BaseExchangeSettings;

/**
 * Проверить пароль при запуске программы если требуется
 */
public class SecurityHelper {

    private static boolean sCheckPassword;

    public static void doRequirePassword(FbaActivityHelper activityHelper) {

        if (!isCheckPassword()) {
            BaseExchangeSettings exSettings = activityHelper.getExchangeSettings();
            if (!TextUtils.isEmpty(exSettings.getPassword())) {
                activityHelper.showLoginActivity(Const.REQUESTCODE_SECURITY_PASSWORD);
            }
        }

    }

    public static boolean isCheckPassword() {
        return sCheckPassword;
    }

    public static void setCheckPassword(boolean value) {
        sCheckPassword = value;
    }
}
