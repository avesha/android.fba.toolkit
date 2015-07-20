package com.sample.app2.test.db;

import com.sample.app2.db.CatalogHarakteristikiNomenklaturi;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogTipiCenNomenklaturi;
import com.sample.app2.db.RegCeniNomenklaturi;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.Ref;

public class Test07ExtTablesCreate extends DBTestCase {

    public void testAddRefCeniNomenclatuturi() throws SQLException {

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(Catalog.FIELD_NAME_FOLDER, false);

        List<CatalogNomenklatura> lstSku = dao.daoCatalogNomenklatura.select(null, filter);
        List<CatalogTipiCenNomenklaturi> lstTypePrice = dao.daoCatalogTipiCen.select();

        CatalogHarakteristikiNomenklaturi emptyHark = new CatalogHarakteristikiNomenklaturi();
        emptyHark.setRef(Ref.emptyUUID());

        Date date = new Date(System.currentTimeMillis());
        for(CatalogNomenklatura sku : lstSku) {

            for(CatalogTipiCenNomenklaturi typePrice : lstTypePrice) {
                RegCeniNomenklaturi row = dao.daoRegCeni.newItem();
                row.setPeriod(date);
                row.nomenklatura = sku;
                row.tipCen = typePrice;
                row.harakteristikaNomenklaturi = emptyHark;
                row.cena = Test02CatalogCreate.roundMoney((rnd.nextDouble() * 100 + rnd.nextInt(50)), 2);
                dao.daoRegCeni.create(row);
            }

        }
    }
}
