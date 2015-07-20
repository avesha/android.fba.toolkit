package ru.profi1c.engine.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import ru.profi1c.engine.R;
import ru.profi1c.engine.exchange.BaseExchangeSettings;

/**
 * Запрос пароля при запуске приложения. Пароль проверяется просто
 * на равенство, если неправильный срабатывает анимация и вибрация, ввод пароля
 * по кнопке 'Далее'
 */
public class FbaSecurityNumericActivity extends FbaActivity {

    private static final int VIBRATE_TIME = 200;

    private EditText mEtPassword;
    private String mPassword;

    private Animation mErrPass;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.fba_activity_security_numeric_layout);

        BaseExchangeSettings exchangeSettings = getFbaSettingsProvider().getExchangeSettings();
        mPassword = exchangeSettings.getPassword();

        mEtPassword = (EditText) findViewById(R.id.fba_password);
        mEtPassword.setOnKeyListener(mOnKeyPasswordListener);

        EditText etUserName = (EditText) findViewById(R.id.fba_user_name);
        etUserName.setText(exchangeSettings.getUserName());

        View btn = findViewById(R.id.fba_next);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });

        mErrPass = AnimationUtils.loadAnimation(this, R.anim.fba_err_pass);
    }

    private boolean isPassOk() {
        boolean isOk = false;

        String inputPass = mEtPassword.getText().toString().toLowerCase();
        if (inputPass.equals(mPassword.toLowerCase())) {
            isOk = true;
        }
        return isOk;
    }

    private void checkPassword() {
        if (isPassOk()) {
            result(RESULT_OK);
        } else {
            mVibrator.vibrate(VIBRATE_TIME);
            mEtPassword.startAnimation(mErrPass);
        }
    }

    private OnKeyListener mOnKeyPasswordListener = new OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent keyevent) {
            if (keyevent.getAction() == KeyEvent.ACTION_DOWN) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                checkPassword();
            } else {
                if (isPassOk()) {
                    result(RESULT_OK);
                }
            }
            return false;
        }
    };

    private void result(int resultCode) {
        setResult(resultCode);
        finish();
    }

}
