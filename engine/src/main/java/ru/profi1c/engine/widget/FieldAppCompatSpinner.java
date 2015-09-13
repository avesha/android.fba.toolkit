package ru.profi1c.engine.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.IPresentation;

public class FieldAppCompatSpinner extends AppCompatSpinner
        implements IFieldPresentationSpinner, Observer {

    private SpinnerFieldDelegate mDelegate;

    public FieldAppCompatSpinner(Context context) {
        super(context);
        init();
    }

    public FieldAppCompatSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldAppCompatSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            mDelegate = new SpinnerFieldDelegate(this);
            super.setOnItemSelectedListener(mDelegate.getDefaultOnItemSelectedListener());
        }
    }

    @Override
    public void setOnItemSelectedListener(
            android.widget.AdapterView.OnItemSelectedListener listener) {
        mDelegate.setOnItemSelectedListener(listener);
    }

    /**
     * Установить обработчик образного вызова на создание адаптера, может
     * использоваться для кэширования однотипных адаптеров
     *
     * @param cacheAdapterRequest
     */
    @Override
    public void setCacheAdapter(SpinnerAdapterRequest cacheAdapterRequest) {
        mDelegate.setCacheAdapter(cacheAdapterRequest);
    }

    /**
     * Если истина, заголовок диалога выбора значения будет устанавливаться
     * автоматически на основании аннотации MetadataField.description
     * установленной для данного поля класса
     *
     * @param setAuto
     */
    @Override
    public void setAutoPrompt(boolean setAuto) {
        mDelegate.setAutoPrompt(setAuto);
    }

    /**
     * Не поддерживается, вызов метода игнорируется
     *
     * @param selectRequest
     */
    @Override
    @Deprecated
    public void setSelectRequest(boolean selectRequest) {

    }

    public void setPromptSelect(String msg) {
        mDelegate.setPromptSelect(msg);
    }

    @Override
    protected void onDetachedFromWindow() {
        mDelegate.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj       Изменяемый объект метаданных (справочник, документ, запись
     *                  регистра)
     * @param fieldName Имя поля класса связанного реквизита
     * @param dbHelper  Помощник для работы с базой данных
     */
    @Override
    public void build(Object obj, String fieldName, DBOpenHelper dbHelper) {
        mDelegate.build(obj, fieldName, dbHelper);
    }

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj      Изменяемый объект метаданных (справочник, документ, запись
     *                 регистра)
     * @param field    Поле класса связанного реквизита
     * @param value    Начальное значение
     * @param dbHelper Помощник для работы с базой данных
     */
    @Override
    public void build(Object obj, Field field, IPresentation value, DBOpenHelper dbHelper) {
        mDelegate.build(obj, field, value, dbHelper);
    }

    /**
     * Возвращает объект метаданных поле которого изменяется данным виджетом
     */
    @Override
    public Object getObject() {
        return mDelegate.getObject();
    }

    /**
     * Возвращает текущее (редактируемое) значение поля класса
     */
    @Override
    public IPresentation getValue() {
        return mDelegate.getValue();
    }

    @Override
    public void update(Observable observable, Object data) {
        mDelegate.update(observable, data);
    }

}
