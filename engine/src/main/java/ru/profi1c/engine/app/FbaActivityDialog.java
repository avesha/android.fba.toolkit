package ru.profi1c.engine.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.R;
import ru.profi1c.engine.app.ui.AlertDialogFragment;
import ru.profi1c.engine.app.ui.BaseDialogFragment;
import ru.profi1c.engine.util.AppHelper;

/**
 * Activity внешне выглядящая как диалог. Отображает текст сообщения и кнопку
 * закрытия, при нажатии на которую происходит переход к основной Activity
 * вашего приложения.
 */
public class FbaActivityDialog extends FbaActivity {
    public static final String EXTRA_MSG = "msg";

    private static final int DIALOG_ID_ERROR = Const.DIALOG_ID_ERROR;

    public static Intent getStartIntent(Context ctx, String msg) {
        Intent i = new Intent(ctx, FbaActivityDialog.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY |
                   Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.putExtra(EXTRA_MSG, msg);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String msg = getIntent().getExtras().getString(EXTRA_MSG);
        if (TextUtils.isEmpty(msg)) {
            msg = getString(R.string.fba_error_msg_empty);
        }
        AlertDialogFragment.show(this, DIALOG_ID_ERROR, AppHelper.getAppLabel(this), msg,
                getString(R.string.fba_ok), false);
    }

    @Override
    public void onDialogFragmentResult(BaseDialogFragment dialog, Object data) {
        super.onDialogFragmentResult(dialog, data);
        if (dialog instanceof AlertDialogFragment) {
            int idDialog = ((AlertDialogFragment) dialog).getDialogId();
            if (idDialog == DIALOG_ID_ERROR) {
                finish();
                getFbaActivityNavigation().goHome(FbaActivityDialog.this);
            }
        }
    }

}
