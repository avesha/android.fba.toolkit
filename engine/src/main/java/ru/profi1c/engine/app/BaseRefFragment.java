package ru.profi1c.engine.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.SQLException;
import java.util.UUID;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.R;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.RefDao;
import ru.profi1c.engine.widget.FieldCheckBox;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.engine.widget.FieldFormatter;
import ru.profi1c.engine.widget.FieldPresentationSpinner;
import ru.profi1c.engine.widget.ObjectView;

/**
 * Базовый класс для редактирования реквизитов ссылочного объекта через Fragment
 * API. Смотрите реализации {@link SimpleCatalogFragment},
 * {@link SimpleDocumentFragment}
 *
 * @param <T>
 */
public abstract class BaseRefFragment<T extends Ref> extends FbaDBFragment {
    private static final String EXTRA_OBJECT_ID = "extra_object_id";

    private FieldFormatter mFieldFormatter;
    private boolean mAutoText, mAutoHint, mAutoPrompt;
    private ObjectView mObjectView;
    private T mRef;

    /**
     * Получить идентификатор ресурса макета используемого для отображения
     * объекта. Предполагается, что этот макет содержит {@link ObjectView} с
     * предопределенным идентификатором {@link @id/fba_object_view}
     */
    protected abstract int getResIdLayout();

    /**
     * Имена полей класса отображаемые на данном фрагменте
     */
    protected abstract String[] getFields();

    /**
     * идентификаторы дочерних View используемых для отображения полей
     */
    protected abstract int[] getIds();

    /**
     * Событие перед началом отображения/редактирования объекта. Здесь можно,
     * например, установить новый номер для документа или код для справочника
     *
     * @param dao   Менеджер данных данного объекта
     * @param obj   Редактируемый объект
     * @param isNew Флаг «новый объект»
     */
    protected abstract void onRefresh(RefDao<T> dao, T obj, boolean isNew);

    public static Bundle toBundle(Ref ref) {
        Bundle args = new Bundle();
        args.putString(EXTRA_OBJECT_ID, ref.getRef().toString());
        return args;
    }

    public void setFieldFormatter(FieldFormatter formatter) {
        this.mFieldFormatter = formatter;
    }

    /**
     * Если истина, текстовое представление для дочерних элементов
     * {@link FieldCheckBox} будет устанавливаться автоматически на основании
     * аннотации MetadataField.description установленной для данного поля класса
     */
    public void setChildCheckBoxAutoText(boolean autoText) {
        mAutoText = autoText;
    }

    /**
     * Если истина, подсказка для дочерних элементов {@link FieldEditText} будет
     * устанавливаться автоматически на основании аннотации
     * MetadataField.description установленной для данного поля класса
     */
    public void setChildTextAutoHint(boolean autoHint) {
        mAutoHint = autoHint;
    }

    /**
     * Если истина, то для дочерних элементов {@link FieldPresentationSpinner}
     * заголовок диалога выбора значения будет устанавливаться автоматически на
     * основании аннотации MetadataField.description установленной для данного
     * поля класса
     */
    public void setChildSpinAutoPrompt(boolean autoPrompt) {
        mAutoPrompt = autoPrompt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(getResIdLayout(), container, false);
        mObjectView = (ObjectView) root.findViewById(R.id.fba_object_view);
        if (mObjectView == null) {
            throw new FbaRuntimeException(
                    "Your content must have a ObjectView whose id attribute is " +
                    "'R.id.fba_object_view'");
        }
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Извлечь аргументы, перечитать объект по ссылке
        try {
            extractFromArguments(getArguments());
            buildView();

        } catch (SQLException e) {
            Dbg.printStackTrace(e);
            throw new FbaRuntimeException(e.getMessage());
        }
    }

    /**
     * Извлечь аргументы, перечитать объект из локальной базы данных. Вызвать
     * после создания основной активити
     */
    protected void extractFromArguments(Bundle args) throws SQLException {

        Class<T> classOfE = getMetaClass();
        RefDao<T> dao = getHelper().getDao(classOfE);

        if (args != null && args.containsKey(EXTRA_OBJECT_ID)) {
            String uuid = args.getString(EXTRA_OBJECT_ID);
            mRef = newInstance();
            mRef.setRef(UUID.fromString(uuid));
            dao.refresh(mRef);
            onRefresh(dao, mRef, false);
        } else {
            // создать новый
            mRef = dao.newItem();
            onRefresh(dao, mRef, true);
        }

    }

    private void buildView() {
        if (mFieldFormatter != null) {
            mObjectView.setFieldFormatter(mFieldFormatter);
        }

        mObjectView.setChildCheckBoxAutoText(mAutoText);
        mObjectView.setChildTextAutoHint(mAutoHint);
        mObjectView.setChildSpinAutoPrompt(mAutoPrompt);

        mObjectView.build(mRef, getHelper(), getFields(), getIds());
    }

    /**
     * Получить view используемый для отображения объекта, может быть null, если
     * объект еще инициализирован (onCreateView не вызывался)
     */
    public ObjectView getObjectView() {
        return mObjectView;
    }

    public T getObject() {
        return mRef;
    }

    /**
     * Сохранить изменения в локальной базе данных
     */
    public void save() throws SQLException {
        Class<T> classOfE = getMetaClass();
        RefDao<T> dao = getHelper().getDao(classOfE);
        dao.createOrUpdate(mRef);
    }

    /**
     * Класс метаданных объект которого отображается данным фрагментом
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getMetaClass() {
        return (Class<T>) MetadataHelper.getGenericSuperclass(getClass());
    }

    /**
     * Создает новый объект
     */
    protected T newInstance() {
        try {
            return getMetaClass().newInstance();
        } catch (java.lang.InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return null;
    }
}
