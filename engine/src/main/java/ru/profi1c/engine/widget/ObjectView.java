package ru.profi1c.engine.widget;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;

/**
 * Отображение объекта метаданных (справочник, документ, запись регистра
 * сведений) с возможностью редактирования реквизитов объекта через дочерние
 * View ({@link FieldCheckBox},{@link FieldDateView},{@link FieldEditText},
 * {@link FieldPresentationSpinner}, {@link FieldTextView},{@link FieldTimeView}
 * ,{@link FieldToggleButton})
 * <p>
 * или в режиме 'Только отображение значения' если дочерние View один из
 * стандартных: {@link Checkable}, {@link TextView}, {@link ImageView}
 * </p>
 * <p>
 * Внимание! Проверка типа реквизита и используемого для его отображения View не
 * производится, если привидение типа невозможно будет ошибка исполнения. <br>
 * Например, реквизит типа Date может быть отображен как строка на любом
 * наследнике от TextView (в т.ч с возможностью изменения через FieldDateView
 * или FieldTimeView). А реквизит типа String не может быть отображен через
 * FieldDateView т.к будет ошибка преобразования строки к дате.
 * </p>
 */
public class ObjectView extends RelativeLayout implements Observer {

    /**
     * Пользовательская реализация установки значения для дочерних View объекта
     */
    public interface ChildViewBinder {
        /**
         * Установить значение реквизита
         *
         * @param obj        объект
         * @param view       элемент управления в который выводится значение
         * @param childValue выводимое значение дочернего реквизита объекта
         * @param field      поле класса
         * @return Истина если данные установлены, если Ложь будет произведена
         * установка значения с форматирование по умолчанию
         */
        boolean setChildViewValue(Object obj, View view, Object childValue, Field field);
    }

    private WeakReference<Object> mReferenceObj;
    private Collection<Field> mFields;

    private DBOpenHelper mHelper;

    private String[] mFrom;
    private int[] mTo;
    private FieldFormatter mFieldFormatter;

    private ChildViewBinder mChildViewBinder;

    private SimpleBaseViewBinder mSimpleBaseViewBinder = new SimpleBaseViewBinder();

    public ObjectView(Context context) {
        super(context);
    }

    public ObjectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObjectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFieldFormatter(FieldFormatter formatter) {
        mFieldFormatter = formatter;
    }

    /**
     * Установка пользовательской реализации привязки дочерних значений
     * (реквизитов объекта) к элементам управления
     *
     * @param viewBinder
     */
    public void setChildViewBinder(ChildViewBinder viewBinder) {
        mChildViewBinder = viewBinder;
    }

    /**
     * Если истина, текстовое представление для дочерних элементов
     * {@link FieldCheckBox} будет устанавливаться автоматически на основании
     * аннотации MetadataField.description установленной для данного поля класса
     *
     * @param autoText
     */
    public void setChildCheckBoxAutoText(boolean autoText) {
        mSimpleBaseViewBinder.setChildCheckBoxAutoText(autoText);
    }

    /**
     * Если истина, подсказка для дочерних элементов {@link FieldEditText} будет
     * устанавливаться автоматически на основании аннотации
     * MetadataField.description установленной для данного поля класса
     *
     * @param autoHint
     */
    public void setChildTextAutoHint(boolean autoHint) {
        mSimpleBaseViewBinder.setChildTextAutoHint(autoHint);
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
        mSimpleBaseViewBinder.setChildSpinAutoPrompt(autoPrompt);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterObserver();
    }

    /**
     * Инициализация виджета, форматирование и отображение дочерних элементов
     *
     * @param obj      Изменяемый объект метаданных (справочник, документ, запись
     *                 регистра), если null то режим 'только отображение'
     * @param dbHelper помощник работы с базой данных
     * @param from     имена полей класса
     * @param to       идентификаторы дочерних View используемых для отображения
     *                 полей
     */
    public void build(Row obj, DBOpenHelper dbHelper, String[] from, int[] to) {

        if (obj == null) {
            throw new NullPointerException();
        }
        setReferenceObj(obj);

        mFields = MetadataHelper.getFields(obj.getClass());
        mHelper = dbHelper;
        mFrom = from;
        mTo = to;

        buildChild();
    }

    private void buildChild() {

        Object obj = getObject();
        if (obj != null) {

            final String[] from = mFrom;
            final int[] to = mTo;
            final int count = from.length;

            for (int i = 0; i < count; i++) {
                String fieldName = from[i];

                Field mField = MetadataHelper.findField(mFields, fieldName, true);
                if (mField == null) {
                    throw new FbaRuntimeException(
                            String.format("Not found a field of class '%s' named '%s'",
                                    obj.getClass(), fieldName));
                }

                int id = to[i];
                View v = findViewById(id);
                if (v != null) {
                    Object fieldValue = getObjectFieldValue(obj, mField);

                    boolean bound = false;
                    if (mChildViewBinder != null) {
                        bound = mChildViewBinder.setChildViewValue(obj, v, fieldValue, mField);
                    }

                    if (!bound) {
                        buildChildView(obj, mField, fieldValue, v);
                    }
                }
            }
        }

    }

    private void buildChildView(Object obj, Field field, Object fieldValue, View v) {

        if (mFieldFormatter != null) {
            mSimpleBaseViewBinder.setFieldFormatter(mFieldFormatter);
        }

        mSimpleBaseViewBinder.setHelper(mHelper);
        mSimpleBaseViewBinder.buildView(obj, field, fieldValue, v);
    }

    /*
     * Получить значение поля от объекта
     */
    protected Object getObjectFieldValue(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (IllegalArgumentException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return null;
    }

    public Object getObject() {
        if (mReferenceObj != null) {
            return mReferenceObj.get();
        }
        return null;
    }

    private void setReferenceObj(Object obj) {
        mReferenceObj = new WeakReference<Object>(obj);
        registerObserver(obj);
    }

    private void registerObserver(Object observer) {
        if (observer instanceof Row) {
            ((Row) observer).addObserver(this);
        }
    }

    private void unRegisterObserver() {
        Object observer = getObject();
        if (observer != null && observer instanceof Row) {
            ((Row) observer).deleteObserver(this);
        }
    }

    @Override
    public void update(Observable observable, Object data) {

        Object objCurrent = getObject();
        Message msg = (Message) data;

        if (objCurrent != null && msg != null && msg.what == Row.ID_OBSERVER_NOTIFY &&
            msg.arg1 != this.hashCode()) {

            // объект изменен целиком, а не отдельный реквизит
            if (msg.obj == null) {
                build((Row) objCurrent, mHelper, mFrom, mTo);
            }
        }

    }

    private static class SimpleBaseViewBinder extends BaseViewBinder {
        //empty
    }
}
