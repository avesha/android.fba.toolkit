package ru.profi1c.samples.sensus.exchange;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.exchange.ExchangeStrategy;
import ru.profi1c.engine.exchange.ExchangeTask;
import ru.profi1c.engine.exchange.ExchangeVariant;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.CatalogSalesPointsDao;

/**
 * Расширим стандартную процедуру обмена для выполнения дополнительных обработчиков по окончании обмена в сервером
 */
public class CustomExchangeTask extends ExchangeTask {

    public CustomExchangeTask(ExchangeVariant exchangeVariant, ExchangeStrategy exchangeStrategy,
            DBOpenHelper dbOpenHelper) {
        super(exchangeVariant, exchangeStrategy, dbOpenHelper);
    }

    @Override
    protected void onComplete(boolean success, Object result) {
        super.onComplete(success, result);
        if (success) {
            try {
                createThumbForSalesPoints();
            } catch (SQLException e) {
                throw new FbaRuntimeException(e);
            }
        }
    }

    private void createThumbForSalesPoints() throws SQLException {

        final File cacheDir = mAppSettings.getCacheDir();
        final CatalogSalesPointsDao dao = mDbHelper.getDao(CatalogSalesPoints.class);

        List<CatalogSalesPoints> lst = dao.selectRoute(true);
        for (CatalogSalesPoints sp : lst) {
            sp.createThumb(cacheDir);
        }
    }
}
