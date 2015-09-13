package ru.profi1c.engine.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import ru.profi1c.engine.R;

class StyleableAttrHelper {

    static boolean readReadOnlyAttribute(Context context, AttributeSet attrs) {
        boolean readOnly = false;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FbaFieldView);
            readOnly = typedArray.getBoolean(R.styleable.FbaFieldView_readOnly, readOnly);
            typedArray.recycle();
        }
        return readOnly;
    }

    private StyleableAttrHelper() {
    }
}
