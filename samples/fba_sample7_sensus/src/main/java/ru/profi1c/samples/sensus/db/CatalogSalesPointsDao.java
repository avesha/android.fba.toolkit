/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.sensus.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.meta.CatalogDao;

/**
 * Менеджер для работы с элементами справочника 'Торговые точки' (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class CatalogSalesPointsDao extends CatalogDao<CatalogSalesPoints> {

    public CatalogSalesPointsDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CatalogSalesPoints.class);
    }

    /**
     * Выбрать маршрут посещения. В этом демо-приложении просто выберем все торговые точки, отсортированные по наименованию.
     *
     * @param refreshPhoto если 'true' будет так же считано основное фото
     * @return
     */
    public List<CatalogSalesPoints> selectRoute(boolean refreshPhoto) throws SQLException {
        List<CatalogSalesPoints> lst = select(null, CatalogSalesPoints.FIELD_NAME_DESCRIPTION);
        if (refreshPhoto) {
            CatalogExtraStorageDao extraDao = new CatalogExtraStorageDao(getConnectionSource());
            for (CatalogSalesPoints sp : lst) {
                CatalogExtraStorage extraStorage = sp.foto;
                if (!CatalogExtraStorage.isEmpty(extraStorage)) {
                    extraDao.refresh(extraStorage);
                }
            }
        }
        return lst;
    }
}