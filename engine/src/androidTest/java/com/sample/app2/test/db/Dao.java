package com.sample.app2.test.db;

import com.sample.app2.App;
import com.sample.app2.db.CatalogCenovieGruppi;
import com.sample.app2.db.CatalogDogovoriKontragentov;
import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogFizicheskieLica;
import com.sample.app2.db.CatalogHarakteristikiNomenklaturi;
import com.sample.app2.db.CatalogKachestvo;
import com.sample.app2.db.CatalogKlassifikatorEdinicIzmereniya;
import com.sample.app2.db.CatalogKontragenti;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturnieGruppi;
import com.sample.app2.db.CatalogOrganizacii;
import com.sample.app2.db.CatalogPodrazdeleniya;
import com.sample.app2.db.CatalogPolzovateli;
import com.sample.app2.db.CatalogStatiZatrat;
import com.sample.app2.db.CatalogTipiCenNomenklaturi;
import com.sample.app2.db.CatalogTipiCenNomenklaturiDao;
import com.sample.app2.db.CatalogValyuti;
import com.sample.app2.db.Constants;
import com.sample.app2.db.DBHelper;
import com.sample.app2.db.DocumentAvansoviiOtchet;
import com.sample.app2.db.DocumentSobitie;
import com.sample.app2.db.DocumentVnutrenniiZakaz;
import com.sample.app2.db.DocumentVnutrenniiZakazTPTovari;
import com.sample.app2.db.DocumentVnutrenniiZakazTPVozvratnayaTara;
import com.sample.app2.db.DocumentVozvratTovarovOtPokupatelya;
import com.sample.app2.db.DocumentZakazPokupatelya;
import com.sample.app2.db.ExTableGeoDannie;
import com.sample.app2.db.ExTableTestTablica2;
import com.sample.app2.db.RegAdresniiKlassifikator;
import com.sample.app2.db.RegCeniNomenklaturi;
import com.sample.app2.db.RegCeniNomenklaturiDao;
import com.sample.app2.db.RegKursiValyut;
import com.sample.app2.db.RegMestaHraneniyaNomenklaturi;
import com.sample.app2.exchange.ExchangeManager;
import com.sample.app2.test.base.AndroidTestCase;

import java.sql.SQLException;

import ru.profi1c.engine.meta.CatalogDao;
import ru.profi1c.engine.meta.ConstDao;
import ru.profi1c.engine.meta.DocumentDao;
import ru.profi1c.engine.meta.TableExDao;
import ru.profi1c.engine.meta.TableInfRegDao;
import ru.profi1c.engine.meta.TableInfRegPeriodicDao;
import ru.profi1c.engine.meta.TablePartDao;

public class Dao {
    private static final String TAG = Dao.class.getSimpleName();

    static DBHelper dbHelper;
    static App app;
    static ExchangeManager exManager;

    static CatalogDao<CatalogKachestvo> daoCatalogKachestvo;
    static CatalogDao<CatalogOrganizacii> daoCatalogOrganizacii;
    static CatalogDao<CatalogNomenklatura> daoCatalogNomenklatura;
    static CatalogDao<CatalogFizicheskieLica> daoCatalogFizicheskieLica;
    static CatalogDao<CatalogHarakteristikiNomenklaturi> daoCatalogHarakteristikiNomenklaturi;
    static CatalogDao<CatalogEdiniciIzmereniya> daoCatalogEdiniciIzmereniya;
    static CatalogDao<CatalogCenovieGruppi> daoCatalogCenovieGruppi;
    static CatalogDao<CatalogDogovoriKontragentov> daoCatalogDogovoriKontragentov;
    static CatalogDao<CatalogKontragenti> daoCatalogKontragenti;
    static CatalogDao<CatalogValyuti> daoCatalogValyuti;
    static CatalogDao<CatalogStatiZatrat> daoCatalogStatiZatrat;
    static CatalogDao<CatalogKlassifikatorEdinicIzmereniya> daoCatalogKlassifikatorEdinicIzmereniya;
    static CatalogDao<CatalogNomenklaturnieGruppi> daoCatalogNomenklaturnieGruppi;
    static CatalogDao<CatalogPolzovateli> daoCatalogPolzovateli;
    static CatalogDao<CatalogPodrazdeleniya> daoCatalogPodrazdeleniya;
    static CatalogTipiCenNomenklaturiDao daoCatalogTipiCen;

    static DocumentDao<DocumentAvansoviiOtchet> daoDocumentAvansoviiOtchet;
    static DocumentDao<DocumentSobitie> daoDocumentSobitie;
    static DocumentDao<DocumentVozvratTovarovOtPokupatelya> daoDocumentVozvratTovarovOtPokupatelya;
    static DocumentDao<DocumentVnutrenniiZakaz> daoDocumentVnutrenniiZakaz;
    static DocumentDao<DocumentZakazPokupatelya> daoDocumentZakazPokupatelya;

    static TablePartDao<DocumentVnutrenniiZakazTPTovari> daoDocumentVnutrenniiZakazTPTovari;
    static TablePartDao<DocumentVnutrenniiZakazTPVozvratnayaTara> daoDocumentVnutrenniiZakazTPVozvratnayaTara;
    static TablePartDao<DocumentVnutrenniiZakazTPTovari> daoDocVnZakazTPTovati;


    static ConstDao<Constants> daoConst;
    static TableExDao<ExTableGeoDannie> daoExTableGeoDannie;
    static TableExDao<ExTableTestTablica2> daoExTableTestTablica2;
    static TableInfRegDao<RegMestaHraneniyaNomenklaturi> daoRegMestaHraneniyaNomenklaturi;
    static TableInfRegDao<RegAdresniiKlassifikator> daoRegAdresniiKlassifikator;
    static TableInfRegPeriodicDao<RegKursiValyut> daoRegKursiValyut;

    static RegCeniNomenklaturiDao daoRegCeni;

    public static void init(DBTestCase test) throws SQLException {

        if (dbHelper == null) {

            app = test.getApp();
            exManager = new ExchangeManager(app.getExchangeSettings());
            dbHelper = new DBHelper(app.getApplicationContext());
            createDao();
        }

    }

    private static void createDao() throws SQLException {
        long msBegin = System.currentTimeMillis();

        //справочники
        daoCatalogKachestvo = dbHelper.getDao(CatalogKachestvo.class);
        daoCatalogOrganizacii = dbHelper.getDao(CatalogOrganizacii.class);
        daoCatalogNomenklatura = dbHelper.getDao(CatalogNomenklatura.class);
        daoCatalogFizicheskieLica = dbHelper.getDao(CatalogFizicheskieLica.class);
        daoCatalogHarakteristikiNomenklaturi = dbHelper.getDao(
                CatalogHarakteristikiNomenklaturi.class);
        daoCatalogEdiniciIzmereniya = dbHelper.getDao(CatalogEdiniciIzmereniya.class);
        daoCatalogCenovieGruppi = dbHelper.getDao(CatalogCenovieGruppi.class);
        daoCatalogDogovoriKontragentov = dbHelper.getDao(CatalogDogovoriKontragentov.class);
        daoCatalogKontragenti = dbHelper.getDao(CatalogKontragenti.class);
        daoCatalogValyuti = dbHelper.getDao(CatalogValyuti.class);
        daoCatalogStatiZatrat = dbHelper.getDao(CatalogStatiZatrat.class);
        daoCatalogKlassifikatorEdinicIzmereniya = dbHelper.getDao(
                CatalogKlassifikatorEdinicIzmereniya.class);
        daoCatalogNomenklaturnieGruppi = dbHelper.getDao(CatalogNomenklaturnieGruppi.class);
        daoCatalogPolzovateli = dbHelper.getDao(CatalogPolzovateli.class);
        daoCatalogPodrazdeleniya = dbHelper.getDao(CatalogPodrazdeleniya.class);
        daoCatalogTipiCen = dbHelper.getDao(CatalogTipiCenNomenklaturi.class);

        //док
        daoDocumentAvansoviiOtchet = dbHelper.getDao(DocumentAvansoviiOtchet.class);
        daoDocumentSobitie = dbHelper.getDao(DocumentSobitie.class);
        daoDocumentVozvratTovarovOtPokupatelya = dbHelper.getDao(
                DocumentVozvratTovarovOtPokupatelya.class);
        daoDocumentVnutrenniiZakaz = dbHelper.getDao(DocumentVnutrenniiZakaz.class);
        daoDocumentZakazPokupatelya = dbHelper.getDao(DocumentZakazPokupatelya.class);

        daoDocumentVnutrenniiZakazTPTovari = dbHelper.getDao(DocumentVnutrenniiZakazTPTovari.class);
        daoDocumentVnutrenniiZakazTPVozvratnayaTara = dbHelper.getDao(
                DocumentVnutrenniiZakazTPVozvratnayaTara.class);
        daoDocVnZakazTPTovati = dbHelper.getDao(DocumentVnutrenniiZakazTPTovari.class);


        //рег
        daoConst = dbHelper.getDao(Constants.class);
        daoExTableGeoDannie = dbHelper.getDao(ExTableGeoDannie.class);
        daoExTableTestTablica2 = dbHelper.getDao(ExTableTestTablica2.class);
        daoRegKursiValyut = dbHelper.getDao(RegKursiValyut.class);
        daoRegMestaHraneniyaNomenklaturi = dbHelper.getDao(RegMestaHraneniyaNomenklaturi.class);
        daoRegAdresniiKlassifikator = dbHelper.getDao(RegAdresniiKlassifikator.class);
        daoRegCeni = dbHelper.getDao(RegCeniNomenklaturi.class);

        int sec = (int) ((System.currentTimeMillis() - msBegin) / 1000);
        AndroidTestCase.log(TAG, "init dao, sec :" + sec);
    }

}
