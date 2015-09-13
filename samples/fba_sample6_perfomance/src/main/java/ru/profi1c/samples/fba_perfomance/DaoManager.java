package ru.profi1c.samples.fba_perfomance;

import java.sql.SQLException;

import ru.profi1c.samples.fba_perfomance.db.CatalogNomenklatura;
import ru.profi1c.samples.fba_perfomance.db.CatalogNomenklaturaDao;
import ru.profi1c.samples.fba_perfomance.db.DBHelper;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihod;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihodDao;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihodTPTovari;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihodTPTovariDao;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCen;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCenDao;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCenTPTovari;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCenTPTovariDao;
import ru.profi1c.samples.fba_perfomance.db.RegCeniNomenklaturi;
import ru.profi1c.samples.fba_perfomance.db.RegCeniNomenklaturiDao;
import ru.profi1c.samples.fba_perfomance.db.RegShtrihkod;
import ru.profi1c.samples.fba_perfomance.db.RegShtrihkodDao;

public class DaoManager {

    CatalogNomenklaturaDao catalogNomenklaturaDao;
    DocumentPrihodDao documentPrihodDao;
    DocumentPrihodTPTovariDao documentPrihodTPTovariDao;
    DocumentUstanovkaCenDao documentUstanovkaCenDao;
    DocumentUstanovkaCenTPTovariDao dcumentUstanovkaCenTPTovariDao;
    RegCeniNomenklaturiDao regCeniNomenklaturiDao;
    RegShtrihkodDao regShtrihkodDao;

    public DaoManager(DBHelper helper) throws SQLException {
        catalogNomenklaturaDao = helper.getDao(CatalogNomenklatura.class);
        documentPrihodDao = helper.getDao(DocumentPrihod.class);
        documentPrihodTPTovariDao = helper.getDao(DocumentPrihodTPTovari.class);
        documentUstanovkaCenDao = helper.getDao(DocumentUstanovkaCen.class);
        dcumentUstanovkaCenTPTovariDao = helper.getDao(DocumentUstanovkaCenTPTovari.class);
        regCeniNomenklaturiDao = helper.getDao(RegCeniNomenklaturi.class);
        regShtrihkodDao = helper.getDao(RegShtrihkod.class);
    }
}
