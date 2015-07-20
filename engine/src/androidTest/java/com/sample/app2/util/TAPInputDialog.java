package com.sample.app2.util;

import android.content.Context;
import android.widget.Toast;

import com.sample.app2.test_action.ITestAction;
import com.sample.app2.test_action.ITestActionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.util.InputDialogHelper;

public class TAPInputDialog implements ITestActionProvider {
    private static final String TAG = TAPInputDialog.class.getSimpleName();

    @Override
    public List<ITestAction> getActions() {
        List<ITestAction> lst = new ArrayList<>();
        lst.add(new InputDialogAction("Ввести строку", InputDialogAction.Type.String));
        lst.add(new InputDialogAction("Ввести дробное число", InputDialogAction.Type.Double));
        lst.add(new InputDialogAction("Ввести целое число", InputDialogAction.Type.Int));
        return Collections.unmodifiableList(lst);
    }

    @Override
    public String getDescription() {
        return "Input dialog";
    }

    private static class InputDialogAction implements ITestAction {
        private enum Type {String, Int, Double}

        private final String mDesc;
        private final Type mType;

        InputDialogAction(String desc, Type type) {
            mDesc = desc;
            mType = type;
        }

        @Override
        public void run(final Context context) {
            switch (mType) {
                case Double:

                    InputDialogHelper.inputDouble(context, 100.123,
                            new InputDialogHelper.OnCompleteListener() {

                                @Override
                                public void onInputValue(Object value) {
                                    Dbg.d(TAG, "onInputValue, double = " + value);
                                    Toast.makeText(context,
                                            String.format("Введенное значение: %s", value),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                    break;

                case Int:
                    InputDialogHelper.inputInt(context, "Введите целое число", "от 1 до 10", 5,
                            new InputDialogHelper.OnCompleteListener() {

                                @Override
                                public void onInputValue(Object value) {
                                    Dbg.d(TAG, "onInputValue, int = " + value);
                                    Toast.makeText(context,
                                            String.format("Введенное значение: %d", (Integer) value),
                                            Toast.LENGTH_SHORT).show();

                                }
                            });
                    break;
                case String:

                    InputDialogHelper.inputString(context, "string 1",
                            new InputDialogHelper.OnCompleteListener() {

                                @Override
                                public void onInputValue(Object value) {
                                    Dbg.d(TAG, "onInputValue, string = " + value);
                                    Toast.makeText(context,
                                            String.format("Введенное значение: %s", value),
                                            Toast.LENGTH_SHORT).show();

                                }

                            });
                    break;
            }
        }

        @Override
        public String getDescription() {
            return mDesc;
        }
    }
}
