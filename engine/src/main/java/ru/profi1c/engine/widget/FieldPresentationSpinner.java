package ru.profi1c.engine.widget;

import android.content.Context;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.IPresentation;

/**
 * Отображение реквизита объекта ссылочного типа (или перечисления) с
 * возможностью редактирования в выпадающем списке. Значения списка отображаются
 * как коллекция IPresentation, для подчиненных справочников отбор по владельцу
 * производится автоматически. Реквизит объекта, связанный со списком,
 * обновляется автоматически при выборе значения.
 */
public class FieldPresentationSpinner extends NoDefaultSpinner
        implements IFieldPresentationSpinner, Observer {

    private SpinnerFieldDelegate mDelegate;

    public FieldPresentationSpinner(Context context) {
        super(context);
        init();
    }

    public FieldPresentationSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldPresentationSpinner(Context context, AttributeSet attrs, int defStyle) {
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
     * Если текущее редактируемое значение не задано по умолчанию
     * устанавливается первое значение из списка. Чтобы отключить авто. выбор,
     * установите истина, будет отображено предложение выбрать элемент из списка
     *
     * @param selectRequest
     */
    @Override
    public void setSelectRequest(boolean selectRequest) {
        mDelegate.setSelectRequest(selectRequest);
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
