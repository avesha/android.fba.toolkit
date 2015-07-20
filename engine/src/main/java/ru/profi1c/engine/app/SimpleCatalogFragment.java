package ru.profi1c.engine.app;

import java.sql.SQLException;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;
import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.meta.RefDao;

/**
 * Простая «форма элемента» справочника, доступны для редактирования 'код' и
 * 'наименование'
 *
 * @param <T>
 */
public abstract class SimpleCatalogFragment<T extends Catalog> extends BaseRefFragment<T> {
    /**
     * Длина кода справочника по умолчанию
     */
    private static final int DEF_CODE_LENGTH = Const.DEFAULT_CATALOG_CODE_LENGTH;

    @Override
    protected int getResIdLayout() {
        return R.layout.fba_simple_catalog_fragment;
    }

    @Override
    protected String[] getFields() {
        return new String[]{Catalog.FIELD_NAME_CODE, Catalog.FIELD_NAME_DESCRIPTION};
    }

    @Override
    protected int[] getIds() {
        return new int[]{R.id.fba_code, R.id.fba_description};
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildTextAutoHint(true);
    }

    @Override
    protected void onRefresh(RefDao<T> dao, T obj, boolean isNew) {

        if (isNew) {
            try {

                CatalogDao<T> catalogDao = (CatalogDao<T>) dao;
                String code = getNewCode(catalogDao);
                obj.setCode(code);

            } catch (SQLException e) {
                Dbg.printStackTrace(e);
            }
        }

    }

    protected String getNewCode(CatalogDao<T> catalogDao) throws SQLException {

        int lenCode = catalogDao.getCodeLength();
        if (lenCode == 0) {
            lenCode = DEF_CODE_LENGTH;
        }

        int newCode = catalogDao.getNextCode();
        return catalogDao.formatNumber(newCode, lenCode);
    }

}
