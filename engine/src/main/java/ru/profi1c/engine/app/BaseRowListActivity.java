package ru.profi1c.engine.app;

import android.view.ViewStub;
import android.widget.ListAdapter;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.R;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Row;
import ru.profi1c.engine.meta.RowDao;
import ru.profi1c.engine.widget.MetaArrayAdapter;

/**
 * Базовый класс для отображения списка ссылочный объектов. Смотрите реализации
 * {@link SimpleCatalogListActivity}, {@link SimpleDocumentListActivity}
 *
 * @param <T>
 */
public abstract class BaseRowListActivity<T extends Row> extends FbaDBListActivity {

    /**
     * Идентификатор макета используемого для отображения заголовка списка.
     * Если заголовок не нужен функция должна возвращать 0
     */
    protected abstract int getHeaderLayoutResource();

    /**
     * Идентификатор макета используемого для отображения записей
     */
    protected abstract int getRowLayoutResource();

    /**
     * Имена полей класса отображаемые в строке
     */
    protected abstract String[] getFieldNames();

    /**
     * Идентификаторы дочерних View используемых для отображения полей
     */
    protected abstract int[] getFieldIds();

    /**
     * Выборка данных, можно установить дополнительную фильтрацию по родителю,
     * владельцу и т.п
     */
    protected abstract List<T> select(RowDao<T> dao) throws SQLException;

    /**
     * Класс метаданных отображаемых в списке
     */
    protected Class<?> getMetaClass() {
        return MetadataHelper.getGenericSuperclass(getClass());
    }

    /**
     * Создать адаптер и установить его как источник для ListView (Используемого
     * по умолчанию или переопределённого в пользовательском макете).
     * <p>
     * Например для создания активити отображающего список с макетом по
     * умолчанию достаточно этого:
     * </p>
     * <p/>
     * <pre>
     * {@code
     * protected void onCreate(Bundle savedInstanceState) {
     * 		super.onCreate(savedInstanceState);
     *
     * 		setContentView(R.layout.fba_include_simple_list_layout);
     * 		try {
     * 			setContentListView();
     * 		} catch (SQLException e) {
     * 			e.printStackTrace();
     * 		}
     * 	}
     * }
     * </pre>
     *
     * @throws SQLException
     */
    protected void setContentListView() throws SQLException {
        inflateHeaderView();
        setListAdapter(createAdapter());
    }

    /**
     * Добавить заголовок списка если установлен. Макет должен сожержать
     * ViewStub с идентификатором fba_stub_list_header, см. ресурс
     * fba_base_row_list_layout.xml
     */
    protected void inflateHeaderView() {
        int resId = getHeaderLayoutResource();
        if (resId != 0) {
            ViewStub stub = (ViewStub) findViewById(R.id.fba_stub_list_header);
            if (stub != null) {
                stub.setLayoutResource(resId);
                stub.inflate();
            }
        }
    }

    /**
     * Создать адаптер на выборку всех записей
     *
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    protected ListAdapter createAdapter() throws SQLException {

        int resIdRow = getRowLayoutResource();

        Class<?> classMeta = getMetaClass();
        List<T> lst = select((RowDao<T>) getHelper().getDao(classMeta));

        MetaArrayAdapter<T> maa = new MetaArrayAdapter<T>(this, (Class<T>) classMeta, lst, resIdRow,
                getFieldNames(), getFieldIds());
        return maa;
    }

}
