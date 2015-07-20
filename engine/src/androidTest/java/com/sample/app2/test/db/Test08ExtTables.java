package com.sample.app2.test.db;

import com.sample.app2.db.CatalogKachestvo;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogTipiCenNomenklaturi;
import com.sample.app2.db.CatalogValyuti;
import com.sample.app2.db.Constants;
import com.sample.app2.db.EnumStavkiNDS;
import com.sample.app2.db.ExTableGeoDannie;
import com.sample.app2.db.ExTableTestTablica2;
import com.sample.app2.db.RegAdresniiKlassifikator;
import com.sample.app2.db.RegCeniNomenklaturi;
import com.sample.app2.db.RegKursiValyut;
import com.sample.app2.db.RegMestaHraneniyaNomenklaturi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.ValueStorage;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.util.IOHelper;

public class Test08ExtTables extends DBTestCase {
    private static final String TAG = Test08ExtTables.class.getSimpleName();
    public static final String ASSETS_SAMPLE_IMAGE_SMALL = "images/sample_image_small.jpg";

    public void testConst() throws SQLException {
        //Прочитать весь набор констант
        Constants constans = dao.daoConst.read();
        assertNotNull(constans);

        //Установить значение константы
        constans.dlinaKodaVesovogoTovara = 66;

        //Сохранить значение константы типа ValueStorage во внешний файл
        if (constans.fbaHranilische != null) {
            File file = new File(dao.app.getAppSettings().getCacheDir(), "tmpImg.jpg");
            boolean complete = constans.fbaHranilische.writeToFile(file);
            assertTrue(complete);
        }
        //Сохранить изменения (записать набор констант)
        dao.daoConst.save();
        log(TAG, "testConst recordKey = " + constans.getRecordKey());

        //check------------------
        constans = dao.daoConst.read();
        assertNotNull(constans);

        assertEquals(66, constans.dlinaKodaVesovogoTovara);

    }

    public void testWriteValueStorage() throws SQLException, IOException {
        Constants constans = dao.daoConst.read();
        assertNotNull(constans);

       InputStream in = null;
        try {
            in = getTargetContext().getAssets().open(ASSETS_SAMPLE_IMAGE_SMALL);
            ValueStorage storage = new ValueStorage(in);
            constans.fbaHranilische = storage;
        } finally {
            IOHelper.close(in);
        }
     
        dao.daoConst.save();
    }

    public void testGetFirst() throws SQLException {

        //Получает значения ресурсов наиболее ранней записи регистра
        Date dtBegin = DateHelper.date(2007, Calendar.APRIL, 1);
        CatalogValyuti usd = dao.daoCatalogValyuti.findByCode("840");
        assertFalse(CatalogValyuti.isEmpty(usd));

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegKursiValyut.FIELD_NAME_VALYUTA, usd);

        RegKursiValyut row = dao.daoRegKursiValyut.getFirst(dtBegin, filter);
        assertNotNull(row);

        log(TAG, "testGetFirst kurs = " + row.kurs + " valuta = " + row.valyuta +
                 " date = " + row.getPeriod());
    }

    public void testGetLast() throws SQLException {

        //Получает значения ресурсов наиболее поздней записи регистра,
        Date dtEnd = DateHelper.date(2007, Calendar.APRIL, 1);
        CatalogValyuti usd = dao.daoCatalogValyuti.findByDescription("USD");
        assertFalse(CatalogValyuti.isEmpty(usd));

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegKursiValyut.FIELD_NAME_VALYUTA, usd);

        RegKursiValyut row = dao.daoRegKursiValyut.getLast(dtEnd, filter);
        assertNotNull(row);

        log(TAG, "testGetLast kurs = " + row.kurs + " valuta = " + row.valyuta +
                 " date = " + row.getPeriod());
    }

    public void testSelect() throws SQLException {

        //Выборка всех записей из регистра
        List<RegMestaHraneniyaNomenklaturi> lst = dao.daoRegMestaHraneniyaNomenklaturi.select();
        assertNotNull(lst);
        for (RegMestaHraneniyaNomenklaturi row : lst) {
            assertNotNull(row);
            log(TAG, "testSelect recordKey = " + row.getRecordKey() + " prioritet = " +
                     row.prioritet + " nomenklatura = " + row.nomenklatura);
        }

        List<RegMestaHraneniyaNomenklaturi> lstAll =
                dao.daoRegMestaHraneniyaNomenklaturi.queryForAll();
        assertEquals(lstAll.size(), lst.size());

    }

    public void testSelectFilter() throws SQLException {

        //Выборка записей с отбором
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegAdresniiKlassifikator.FIELD_NAME_NAIMENOVANIE, "Москва");
        filter.put(RegAdresniiKlassifikator.FIELD_NAME_SOKRASCHENIE, "г           ");

        List<RegAdresniiKlassifikator> lst = dao.daoRegAdresniiKlassifikator.select(filter);
        assertNotNull(lst);
        assertEquals(lst.size(), 1);
        for (RegAdresniiKlassifikator row : lst) {
            assertNotNull(row);
            log(TAG, "testSelectFilter recordKey = " + row.getRecordKey() + " naimenovanie = " +
                     row.naimenovanie);
        }

    }

    public void testSelectFilterOrder() throws SQLException {

        CatalogValyuti usd = dao.daoCatalogValyuti.findByDescription("USD");
        assertFalse(CatalogValyuti.isEmpty(usd));

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegKursiValyut.FIELD_NAME_VALYUTA, usd);

        List<RegKursiValyut> lst =
                dao.daoRegKursiValyut.select(filter, RegKursiValyut.FIELD_NAME_PERIOD + " DESC");
        assertNotNull(lst);

        for (RegKursiValyut row : lst) {
            assertNotNull(row);
            log(TAG, "testSelectFilterOrder recordKey = " + row.getRecordKey() + " date = " +
                     row.getPeriod());
        }

    }

    public void testNewItemExTableGeoDannie() throws SQLException {
        //Добавление записи в таблицу
        ExTableGeoDannie row = dao.daoExTableGeoDannie.newItem();
        row.lat = 45.12345;
        row.lng = 37.124;
        row.userName = "new user " + rnd.nextInt(100000);

        dao.daoExTableGeoDannie.create(row);

        //not create instance - clear all filed and rewrite
        row.lat = 100;
        row.lng = 200;
        row.userName = "new user " + rnd.nextInt(100000);
        row.setRecordKey(row.createRecordKey());

        dao.daoExTableGeoDannie.create(row);

    }

    public void testNewItem2() throws SQLException {

        CatalogKachestvo kBrack = dao.daoCatalogKachestvo.findByDescription("Брак");
        assertFalse(CatalogKachestvo.isEmpty(kBrack));
        CatalogKachestvo kNew = dao.daoCatalogKachestvo.findByDescription("Новый");
        assertFalse(CatalogKachestvo.isEmpty(kNew));

        ExTableTestTablica2 row = dao.daoExTableTestTablica2.newItem();
        row.dataNachala = new Date(System.currentTimeMillis());
        row.imyaPolzovatelya = "new user " + System.currentTimeMillis();
        row.kachestvo = kBrack;
        row.stavkaNDS = EnumStavkiNDS.BezNDS;
        row.ogranichivat = true;

        dao.daoExTableTestTablica2.create(row);

        row = dao.daoExTableTestTablica2.newItem();
        row.dataNachala = new Date(System.currentTimeMillis());
        row.imyaPolzovatelya = "new user " + System.currentTimeMillis();
        row.kachestvo = kNew;
        row.stavkaNDS = EnumStavkiNDS.NDS0;
        row.ogranichivat = false;

        dao.daoExTableTestTablica2.create(row);

    }

    public void testSelectChangedExTableGeoDannie() throws SQLException {

        List<ExTableGeoDannie> lst = dao.daoExTableGeoDannie.selectChanged();
        assertNotNull(lst);

        for (ExTableGeoDannie row : lst) {
            assertNotNull(row);
            log(TAG, "test99SelectChangedExTableGeoDannie recordKey = " + row.getRecordKey());
        }

    }

    public void testSetModifiedAllExTableGeoDannie() throws SQLException {
        int allRows = dao.daoExTableGeoDannie.queryForAll().size();

        dao.daoExTableGeoDannie.setModified(true);
        int changed = dao.daoExTableGeoDannie.selectChanged().size();
        assertEquals(allRows, changed);

        dao.daoExTableGeoDannie.setModified(false);
        changed = dao.daoExTableGeoDannie.selectChanged().size();
        assertEquals(0, changed);
    }

    public void testSetModifiedAllRegKursiValyut() throws SQLException {
        int allRows = dao.daoRegKursiValyut.queryForAll().size();

        dao.daoRegKursiValyut.setModified(true);
        int changed = dao.daoRegKursiValyut.selectChanged().size();
        assertEquals(allRows, changed);

        dao.daoRegKursiValyut.setModified(false);
        changed = dao.daoRegKursiValyut.selectChanged().size();
        assertEquals(0, changed);
    }

    public void testSetModifiedAllOnFilter() throws SQLException {

        dao.daoRegAdresniiKlassifikator.setModified(false);

        final String naimenovanie = "Москва";
        final String sokraschenie = "г           ";
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegAdresniiKlassifikator.FIELD_NAME_NAIMENOVANIE, naimenovanie);
        filter.put(RegAdresniiKlassifikator.FIELD_NAME_SOKRASCHENIE, sokraschenie);

        dao.daoRegAdresniiKlassifikator.setModified(true, filter);

        List<RegAdresniiKlassifikator> lst = dao.daoRegAdresniiKlassifikator.select(filter);
        assertNotNull(lst);
        assertEquals(lst.size(), 1);
        for (RegAdresniiKlassifikator row : lst) {
            assertNotNull(row);
            assertTrue(row.isModified());
        }

        lst = dao.daoRegAdresniiKlassifikator.queryForAll();
        assertNotNull(lst);
        for (RegAdresniiKlassifikator row : lst) {
            assertNotNull(row);
            if (row.isModified()) {
                assertEquals(naimenovanie, row.naimenovanie);
                assertEquals(sokraschenie, row.sokraschenie);
            }
        }
    }

    public void testRegCeniNomenklaturi() throws SQLException {


        CatalogNomenklatura nomen =
                dao.daoCatalogNomenklatura.findByDescription("Женские ботфорты коричневые");
        assertNotNull(nomen);

        CatalogTipiCenNomenklaturi tipCen = dao.daoCatalogTipiCen.findByDescription("Розничная");
        assertNotNull(tipCen);

        Date dtEnd = new Date(System.currentTimeMillis());
        Date dtBegin = DateHelper.addDays(dtEnd, - 60);

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegCeniNomenklaturi.FIELD_NAME_NOMENKLATURA, nomen);
        filter.put(RegCeniNomenklaturi.FIELD_NAME_TIP_CEN, tipCen);
        filter.put(RegCeniNomenklaturi.FIELD_NAME_HARAKTERISTIKA_NOMENKLATURI, Catalog.EMPTY_REF);

        RegCeniNomenklaturi row1 = dao.daoRegCeni.getFirst(dtBegin, filter);
        assertNotNull(row1);
        log(TAG, "testRegRegistrator row1 cena = " + row1.cena);
        assertTrue(row1.cena > 0);

        RegCeniNomenklaturi row2 = dao.daoRegCeni.getLast(dtEnd, filter);
        assertNotNull(row2);
        log(TAG, "testRegRegistrator row2 cena = " + row2.cena);
        assertTrue(row2.cena > 0);

    }

}
