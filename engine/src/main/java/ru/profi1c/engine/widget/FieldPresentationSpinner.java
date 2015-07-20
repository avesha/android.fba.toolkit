package ru.profi1c.engine.widget;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.R;
import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.DocumentDao;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;

/**
 * Отображение реквизита объекта ссылочного типа (или перечисления) с
 * возможностью редактирования в выпадающем списке. Значения списка отображаются
 * как коллекция IPresentation, для подчиненных справочников отбор по владельцу
 * производится автоматически. Реквизит объекта, связанный со списком,
 * обновляется автоматически при выборе значения.
 */
public class FieldPresentationSpinner extends NoDefaultSpinner implements Observer {
    private static final String TAG = FieldPresentationSpinner.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private DBOpenHelper mHelper;
    private WeakReference<Object> mReferenceObj;
    private Field mField;
    private IPresentation mFieldValue;
    private CacheAdapterRequest mCacheAdapter;
    private boolean mAutoSetPrompt;
    private boolean mSelectRequest;
    private String mPromptSelect;

    private boolean mBuilded;
    private OnItemSelectedListener mCustomOnItemSelectedListener;

    /**
     * Оптимизация создания адаптера
     */
    public interface CacheAdapterRequest {

        /**
         * Получить кеш-адаптер для отображения полного списка значений.
         * Оптимизация отображения однотипных данных в списках т. е. чтобы не
         * создавать заново адаптер например для перечислений можно взять из
         * кеш.
         *
         * @param classOfValue класс, значения которого отображаются в адаптере
         * @return
         */
        SpinnerAdapter getCachedAdapter(Class<?> classOfValue);

        /**
         * Создан новый адаптер, можно сохранить в кеш, если требуется
         *
         * @param classOfValue класс, значения которого отображаются в адаптере
         * @param adapter      адаптер с данными
         */
        void onNewAdapter(Class<?> classOfValue, SpinnerAdapter adapter);
    }

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
            super.setOnItemSelectedListener(mOnSpinnerItemSelectListener);
        }
    }

    @Override
    public void setOnItemSelectedListener(
            android.widget.AdapterView.OnItemSelectedListener listener) {
        mCustomOnItemSelectedListener = listener;
    }

    /**
     * Установить обработчик образного вызова на создание адаптера, может
     * использоваться для кэширования однотипных адаптеров
     *
     * @param cacheAdapterRequest
     */
    public void setCacheAdapter(CacheAdapterRequest cacheAdapterRequest) {
        this.mCacheAdapter = cacheAdapterRequest;
    }

    /**
     * Если истина, заголовок диалога выбора значения будет устанавливаться
     * автоматически на основании аннотации MetadataField.description
     * установленной для данного поля класса
     *
     * @param setAuto
     */
    public void setAutoPrompt(boolean setAuto) {
        this.mAutoSetPrompt = setAuto;
    }

    /**
     * Если текущее редактируемое значение не задано по умолчанию
     * устанавливается первое значение из списка. Чтобы отключить авто. выбор,
     * установите истина, будет отображено предложение выбрать элемент из списка
     *
     * @param selectRequest
     */
    public void setSelectRequest(boolean selectRequest) {
        this.mSelectRequest = selectRequest;
    }

    public void setPromptSelect(String msg) {
        this.mPromptSelect = msg;
        this.mSelectRequest = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterObserver();
        mCacheAdapter = null;
    }

    private OnItemSelectedListener mOnSpinnerItemSelectListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (mBuilded) {
                if (DEBUG) {
                    Dbg.d(TAG, "onItemSelected, pos = %d", position);
                }
                IPresentation newItem = (IPresentation) getAdapter().getItem(position);
                setObjectFieldValue(newItem);
            }

            if (mCustomOnItemSelectedListener != null) {
                mCustomOnItemSelectedListener.onItemSelected(parent, view, position, id);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (mCustomOnItemSelectedListener != null) {
                mCustomOnItemSelectedListener.onNothingSelected(parent);
            }
        }
    };

    /*
     * Создать адаптер и установить текущее значение
     */
    private void createAndSetAdapter() {

        try {
            SpinnerAdapter adapter = createAdapter();
            if (adapter != null) {

                if (mSelectRequest) {
                    String prompt = mPromptSelect;
                    if (TextUtils.isEmpty(prompt)) {
                        prompt = MetadataHelper.getMetadataFieldDescription(mField);
                        if (TextUtils.isEmpty(prompt)) {
                            prompt = getContext().getString(R.string.fba_spinner_select_prompt);
                        }
                    }
                    setAdapterPromptSelect(adapter, prompt);
                } else {
                    setAdapter(adapter);
                }

                setCurrentSelection();

                if (mAutoSetPrompt) {
                    setPromptAnnotation();
                }
            }
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }

    }

    /*
     * установить заголовок диалога на основании аннотации поля
     */
    private void setPromptAnnotation() {
        String desc = MetadataHelper.getMetadataFieldDescription(mField);
        if (desc != null) {
            setPrompt(desc);
        }
    }

    /**
     * Создать адаптер и заполнить его данными
     *
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    private SpinnerAdapter createAdapter() throws SQLException {

        SpinnerAdapter adapter = null;
        // mFieldValue может быть null
        Class<?> classOfValues = mField.getType();

        if (MetadataHelper.extendsOfClass(classOfValues, Enum.class)) {
            if (mCacheAdapter != null) {
                adapter = mCacheAdapter.getCachedAdapter(classOfValues);
            }

            if (adapter == null) {
                adapter = new PresentationAdapter(getContext(), (IPresentation[]) classOfValues
                        .getEnumConstants());

                if (mCacheAdapter != null) {
                    mCacheAdapter.onNewAdapter(classOfValues, adapter);
                }
            }

        } else if (MetadataHelper.extendsOfClass(classOfValues, Catalog.class)) {

            CatalogDao<Catalog> dao = (CatalogDao<Catalog>) mHelper.getDao(classOfValues);
            Catalog owner = null;

			/*
             * Если значение задано, владельца получаем от него, если нет и
			 * справочник подчиненный – считаем, что подчинен этому объекту
			 * (справочнику)
			 */
            if (mFieldValue != null) {
                Catalog catalog = (Catalog) mFieldValue;
                dao.refresh((Catalog) catalog);
                owner = catalog.getOwner();
            } else {
                if (MetadataHelper.isSlaveCatalog(classOfValues)) {
                    Object obj = getObject();
                    if (obj != null && obj instanceof Catalog) {
                        owner = (Catalog) getObject();
                    }
                }
            }
            List<IPresentation> lstData = dao.toPresentationList(dao.select(null, owner));
            adapter = new PresentationAdapter(getContext(), lstData);

            if (mCacheAdapter != null && owner == null) {
                mCacheAdapter.onNewAdapter(classOfValues, adapter);
            }

        } else if (mFieldValue instanceof Document) {

            if (mCacheAdapter != null) {
                adapter = mCacheAdapter.getCachedAdapter(classOfValues);
            }

            if (adapter == null) {

                DocumentDao<Document> dao = (DocumentDao<Document>) mHelper.getDao(classOfValues);
                List<IPresentation> lstData = dao.toPresentationList(dao.select());
                adapter = new PresentationAdapter(getContext(), lstData);

                if (mFieldValue != null) {
                    dao.refresh((Document) mFieldValue);
                }

                if (mCacheAdapter != null) {
                    mCacheAdapter.onNewAdapter(classOfValues, adapter);
                }
            }

        } else {
            throw new FbaRuntimeException(String.format(
                    "The values ​​of class '%s' b can not display as FieldPresentationSpinner view!",
                    mFieldValue.getClass()));
        }

        return adapter;
    }

    /**
     * Получить позицию этого элемента в адаптере
     *
     * @return
     */
    private int getCurrentItemPosOnAdapter() {
        SpinnerAdapter adapter = getAdapter();
        if (adapter != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                if (adapter.getItem(i).equals(mFieldValue)) {
                    return i;
                }
            }
        }
        return Const.NOT_SPECIFIED;
    }

    private void setCurrentSelection() {
        setSelection(getCurrentItemPosOnAdapter());
    }

    /**
     * Инициализация видждета, форматирование и отображение значения
     *
     * @param obj       Изменяемый объект метаданных (справочник, документ, запись
     *                  регистра)
     * @param fieldName Имя поля класса связанного реквизита
     * @param dbHelper  Помощник для работы с базой данных
     */
    public void build(Object obj, String fieldName, DBOpenHelper dbHelper) {

        if (obj == null) {
            throw new NullPointerException();
        }

        setReferenceObj(obj);
        mHelper = dbHelper;
        Collection<Field> mFields = MetadataHelper.getFields(obj.getClass());
        mField = MetadataHelper.findField(mFields, fieldName, true);
        if (mField == null) {
            throw new FbaRuntimeException(
                    String.format("Not found a field of class '%s' named '%s'", obj.getClass(),
                                  fieldName));
        }

        mFieldValue = getObjectFieldValue();
        createAndSetAdapter();

        mBuilded = true;
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
    public void build(Object obj, Field field, IPresentation value, DBOpenHelper dbHelper) {

        if (obj == null) {
            throw new NullPointerException();
        }

        setReferenceObj(obj);
        mHelper = dbHelper;
        mField = field;
        mFieldValue = value;
        createAndSetAdapter();

        mBuilded = true;
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

    /**
     * Возвращает объект метаданных поле которого изменяется данным виджетом
     */
    public Object getObject() {
        if (mReferenceObj != null) {
            return mReferenceObj.get();
        }
        return null;
    }

    /**
     * Возвращает текущее (редактируемое) значение поля класса
     */
    public IPresentation getValue() {
        return mFieldValue;
    }

    /*
     * Получить значение поля от объекта
     */
    protected IPresentation getObjectFieldValue() {
        IPresentation value = null;
        Object obj = getObject();
        if (obj != null && mField != null) {
            try {
                value = (IPresentation) mField.get(obj);
            } catch (IllegalArgumentException e) {
                Dbg.printStackTrace(e);
            } catch (IllegalAccessException e) {
                Dbg.printStackTrace(e);
            }
        }
        return value;
    }

    /*
     * Установить значение поле объекта
     */
    protected void setObjectFieldValue(IPresentation newValue) {

        Object obj = getObject();
        if (obj != null && mField != null) {

            try {
                mField.set(obj, newValue);
                mFieldValue = newValue;

                if (obj instanceof Row) {
                    Row row = (Row) obj;
                    row.setModified(true);

                    Message msg = Row.makeObserverMessage(this.hashCode(), mField.getName());
                    row.notifyObservers(msg);
                }

            } catch (IllegalArgumentException e) {
                Dbg.printStackTrace(e);
            } catch (IllegalAccessException e) {
                Dbg.printStackTrace(e);
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {

        Object objCurrent = getObject();
        Message msg = (Message) data;

        if (objCurrent != null && msg != null && msg.what == Row.ID_OBSERVER_NOTIFY &&
            msg.arg1 != this.hashCode()) {
            if (mField.getName().equals(msg.obj)) {

                mFieldValue = getObjectFieldValue();
                setCurrentSelection();
            }
        }
    }

}
