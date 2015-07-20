package ru.profi1c.engine.app;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.R;
import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.RowDao;

/**
 * Простой список справочника, отображает все записи. Поля "Код" и
 * "Наименование"
 *
 * @param <T>
 */
public abstract class SimpleCatalogListActivity<T extends Catalog> extends BaseRowListActivity<T> {

    @Override
    protected int getHeaderLayoutResource() {
        return R.layout.fba_simple_catalog_item_header;
    }

    @Override
    protected int getRowLayoutResource() {
        return R.layout.fba_simple_catalog_item;
    }

    @Override
    protected String[] getFieldNames() {
        return new String[]{Catalog.FIELD_NAME_CODE, Catalog.FIELD_NAME_DESCRIPTION};
    }

    @Override
    protected int[] getFieldIds() {
        return new int[]{R.id.fba_code, R.id.fba_description};
    }

    @Override
    protected List<T> select(RowDao<T> dao) throws SQLException {
        return dao.select();
    }

}
