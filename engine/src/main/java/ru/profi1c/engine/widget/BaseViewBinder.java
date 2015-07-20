package ru.profi1c.engine.widget;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.ValueStorage;
import ru.profi1c.engine.widget.FieldPresentationSpinner.CacheAdapterRequest;

/**
 * Базовый класс для построения отображения дочерних View (как связанных с
 * объектом метаданных так и нет)
 */
public abstract class BaseViewBinder {

    private FieldFormatter mFormatter;
    private DBOpenHelper mHelper;
    private WeakHashMap<Class<?>, SpinnerAdapter> mCacheAdapters;

    private boolean mChildCheckBoxAutoText;
    private boolean mChildTextAutoHint;
    private boolean mChildSpinAutoPrompt;

    public BaseViewBinder() {
        mFormatter = new FieldFormatter.Builder().create();
        mCacheAdapters = new WeakHashMap<Class<?>, SpinnerAdapter>();
    }

    public void setFieldFormatter(FieldFormatter formatter) {
        mFormatter = formatter;
    }

    public FieldFormatter getFieldFormatter() {
        return mFormatter;
    }

    public DBOpenHelper getHelper() {
        return mHelper;
    }

    public void setHelper(DBOpenHelper mHelper) {
        this.mHelper = mHelper;
    }

    /**
     * Если истина, текстовое представление для дочерних элементов
     * {@link FieldCheckBox} будет устанавливаться автоматически на основании
     * аннотации MetadataField.description установленной для данного поля класса
     *
     * @param autoText
     */
    public void setChildCheckBoxAutoText(boolean autoText) {
        this.mChildCheckBoxAutoText = autoText;
    }

    /**
     * Если истина, подсказка для дочерних элементов {@link FieldEditText} будет
     * устанавливаться автоматически на основании аннотации
     * MetadataField.description установленной для данного поля класса
     *
     * @param autoHint
     */
    public void setChildTextAutoHint(boolean autoHint) {
        this.mChildTextAutoHint = autoHint;
    }

    /**
     * Если истина, то для дочерних элементов {@link FieldPresentationSpinner}
     * заголовок диалога выбора значения будет устанавливаться автоматически на
     * основании аннотации MetadataField.description установленной для данного
     * поля класса
     *
     * @param autoPrompt
     */
    public void setChildSpinAutoPrompt(boolean autoPrompt) {
        this.mChildSpinAutoPrompt = autoPrompt;
    }

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj        Изменяемый объект метаданных (справочник, документ, запись
     *                   регистра), если null то режим ‘только отображение’
     * @param field      Поле класса связанного реквизита
     * @param fieldValue Начальное значение
     * @param v          виджет, на котором будет отображаться значение
     */
    public void buildView(Object obj, Field field, Object fieldValue, View v) {

        if (v instanceof IFieldView) {

            if (v instanceof FieldEditText) {
                ((FieldEditText) v).setAutoHint(mChildTextAutoHint);
            } else if (v instanceof IAutoTextFieldView) {
                ((IAutoTextFieldView) v).setAutoText(mChildCheckBoxAutoText);
            }

            // объект может быть не задан
            ((IFieldView) v).build(obj, field, fieldValue, mFormatter);

        } else if (v instanceof FieldPresentationSpinner) {

            // объект обязателен
            if (obj == null) {
                throw new NullPointerException(
                        "For 'FieldPresentationSpinner' widget is required the object!");
            }

            FieldPresentationSpinner fpSpin = (FieldPresentationSpinner) v;
            fpSpin.setCacheAdapter(mCacheAdapterRequest);
            fpSpin.setSelectRequest(true);
            fpSpin.setAutoPrompt(mChildSpinAutoPrompt);

            IPresentation presentation = (IPresentation) fieldValue;
            fpSpin.build(obj, field, presentation, mHelper);

        } else if (v instanceof ObjectView) {
            throw new IllegalStateException("Nested 'ObjectView' are not allowed!");
        } else {
            setViewValue(field.getType(), fieldValue, v);
        }

    }

    /*
     * кеширование адаптеров
     */
    private FieldPresentationSpinner.CacheAdapterRequest mCacheAdapterRequest =
            new CacheAdapterRequest() {

                @Override
                public SpinnerAdapter getCachedAdapter(Class<?> classOfValues) {
                    return mCacheAdapters.get(classOfValues);
                }

                @Override
                public void onNewAdapter(Class<?> classOfValues, SpinnerAdapter adapter) {
                    mCacheAdapters.put(classOfValues, adapter);
                }

            };

    /*
     * Установить значение поля объекта на View, внимание value можеть быть null
     */
    protected void setViewValue(Class<?> classOfValue, Object value, View v) {

        if (v instanceof Checkable) {

            setCheckableView((Checkable) v, classOfValue, value);

        } else if (v instanceof TextView) {

            // Note: keep the instanceof TextView check at the bottom of these
            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
            setViewText((TextView) v, classOfValue, value);

        } else if (v instanceof ImageView) {

            setViewImage((ImageView) v, classOfValue, value);

        } else {
            throw new FbaRuntimeException(v.getClass().getName() + " is not a " +
                                            " view that can be bounds by this ViewBinder");
        }

    }

    private void setViewImage(ImageView v, Class<?> classOfValue, Object value) {
        Bitmap bmp = castAsBitmap(classOfValue, value);
        v.setImageBitmap(bmp);
    }

    protected void setViewText(TextView v, Class<?> classOfValue, Object value) {
        v.setText(castAsString(classOfValue, value));
    }

    protected void setCheckableView(Checkable v, Class<?> classOfValue, Object value) {
        v.setChecked(castAsBoolean(classOfValue, value));
    }

    protected Boolean castAsBoolean(Class<?> classOfValue, Object value) {
        if (value != null) {

            if (MetadataHelper.isBooleanClass(classOfValue)) {
                return (Boolean) value;
            } else if (MetadataHelper.isIntegerClass(classOfValue) ||
                       MetadataHelper.isShortClass(classOfValue)) {
                return ((Integer) value > 0) ? true : false;
            } else if (MetadataHelper.isStringClass(classOfValue)) {
                return Boolean.parseBoolean(value.toString());
            }
        }
        return false;
    }

    protected String castAsString(Class<?> classOfValue, Object value) {
        if (value instanceof IPresentation) {
            String str = ((IPresentation) value).getPresentation();
            return mFormatter.format(str);
        } else {
            return mFormatter.format(value);
        }
    }

    protected Bitmap castAsBitmap(Class<?> classOfValue, Object value) {
        Bitmap bmp = null;
        if (value != null) {
            if (ValueStorage.class.isAssignableFrom(classOfValue)) {
                ValueStorage vStorage = (ValueStorage) value;
                bmp = vStorage.toBitmap();
            }
        }
        return bmp;
    }

}
