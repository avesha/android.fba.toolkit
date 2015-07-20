package com.sample.app2.test.db;

import android.content.Context;

import com.sample.app2.R;
import com.sample.app2.db.CatalogCenovieGruppi;
import com.sample.app2.db.CatalogDogovoriKontragentov;
import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogFizicheskieLica;
import com.sample.app2.db.CatalogKachestvo;
import com.sample.app2.db.CatalogKontragenti;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturnieGruppi;
import com.sample.app2.db.CatalogNomeraGTD;
import com.sample.app2.db.CatalogOrganizacii;
import com.sample.app2.db.CatalogPodrazdeleniya;
import com.sample.app2.db.CatalogPolzovateli;
import com.sample.app2.db.CatalogStatiZatrat;
import com.sample.app2.db.CatalogValyuti;
import com.sample.app2.db.EnumStavkiNDS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

import ru.profi1c.engine.meta.Ref;

public class Test02CatalogCreate extends DBTestCase {

    public static final String VALUTA_USD_CODE = "840";
    public static final String VALUTA_USD_ID = "bd72d8fa-55bc-11d9-848a-00112f43529a";

    public static double roundMoney(double value, int count){
        return new BigDecimal(value).setScale(count, RoundingMode.HALF_UP).doubleValue();
    }

    static CatalogFizicheskieLica findOrCreateFio(Context context, Dao dao) throws SQLException {
        Random rnd = new Random();
        int num = rnd.nextInt(100);
        int len = dao.daoCatalogFizicheskieLica.getCodeLength();
        if (len == 0) {
            len = CATALOG_CODE_LENGTH;
        }

        String code = dao.daoCatalogFizicheskieLica.formatNumber(num, len);
        CatalogFizicheskieLica ref = dao.daoCatalogFizicheskieLica.findByCode(code);
        if(CatalogFizicheskieLica.isEmpty(ref)){

            String[] fio = context.getResources().getStringArray(R.array.fio);
            String desc = fio[rnd.nextInt(fio.length)];

            int newCode = dao.daoCatalogFizicheskieLica.getNextCode();
            code = dao.daoCatalogFizicheskieLica.formatNumber(newCode, len);

            ref = dao.daoCatalogFizicheskieLica.newItem();
            ref.setCode(code);
            ref.setDescription(desc);
            ref.setParent(CatalogFizicheskieLica.emptyUUID());

            dao.daoCatalogFizicheskieLica.create(ref);
        }

        return ref;
    }

    static CatalogPolzovateli findOrCreatePolzovatel(Context context, Dao dao) throws SQLException {
        Random rnd = new Random();
        String[] polzovatel = context.getResources().getStringArray(R.array.polzovatel);
        String desc = polzovatel[rnd.nextInt(polzovatel.length)];

        CatalogPolzovateli ref = dao.daoCatalogPolzovateli.findByDescription(desc);
        if(CatalogPolzovateli.isEmpty(ref)){

            String code = desc.replaceAll(" ", "");
            ref = dao.daoCatalogPolzovateli.newItem();
            ref.setCode(code);
            ref.setDescription(desc);
            ref.setParent(CatalogPolzovateli.emptyUUID());

            dao.daoCatalogPolzovateli.create(ref);
        }
        return ref;
    }

    static CatalogPodrazdeleniya findOrCreatePodtazdelenie(Context context, Dao dao) throws SQLException {
        Random rnd = new Random();
        String[] podrazdeleniya = context.getResources().getStringArray(R.array.podrazdeleniya);
        String desc = podrazdeleniya[rnd.nextInt(podrazdeleniya.length)];

        CatalogPodrazdeleniya ref = dao.daoCatalogPodrazdeleniya.findByDescription(desc);
        if(CatalogPodrazdeleniya.isEmpty(ref)){

            int newCode = dao.daoCatalogPodrazdeleniya.getNextCode();
            int len = dao.daoCatalogPodrazdeleniya.getCodeLength();
            if (len == 0) {
                len = CATALOG_CODE_LENGTH;
            }
            String code = dao.daoCatalogPodrazdeleniya.formatNumber(newCode, len);

            ref = dao.daoCatalogPodrazdeleniya.newItem();
            ref.setCode(code);
            ref.setDescription(desc);
            ref.setParent(CatalogPodrazdeleniya.emptyUUID());

            dao.daoCatalogPodrazdeleniya.create(ref);
        }
        return ref;
    }

    static CatalogOrganizacii findOrCreateOrganization(Context context, Dao dao) throws SQLException {
        Random rnd = new Random();
        int num = rnd.nextInt(10);
        int len = dao.daoCatalogOrganizacii.getCodeLength();
        if (len == 0) {
            len = CATALOG_CODE_LENGTH;
        }
        String code = dao.daoCatalogOrganizacii.formatNumber(num, len);
        CatalogOrganizacii ref = dao.daoCatalogOrganizacii.findByCode(code);
        if(CatalogOrganizacii.isEmpty(ref)){

            int newCode = dao.daoCatalogOrganizacii.getNextCode();
            code = dao.daoCatalogOrganizacii.formatNumber(newCode, len);

            ref = dao.daoCatalogOrganizacii.newItem();
            ref.setCode(code);
            ref.setDescription("Новая организация " + rnd.nextInt());

            Dao.daoCatalogOrganizacii.create(ref);
        }

        return ref;
    }

    static CatalogNomenklaturnieGruppi fingOrcreateNomenklaturnieGruppi(Context context, Dao dao)
            throws SQLException {

        Random rnd = new Random();
        String[] sku_groups = context.getResources().getStringArray(R.array.sku_groups);
        String desc = sku_groups[rnd.nextInt(sku_groups.length)];

        CatalogNomenklaturnieGruppi ref = dao.daoCatalogNomenklaturnieGruppi.findByDescription(desc);
        if (Ref.isEmpty(ref)) {

            int lenCode = dao.daoCatalogNomenklaturnieGruppi.getCodeLength();
            if (lenCode == 0) {
                lenCode = LARGE_CATALOG_CODE_LENGTH;
            }
            int code = dao.daoCatalogNomenklaturnieGruppi.getNextCode();

            ref = dao.daoCatalogNomenklaturnieGruppi.newItem();
            ref.setCode(dao.daoCatalogNomenklaturnieGruppi.formatNumber(code++, lenCode));
            ref.setDescription(desc);
            ref.setParent(Ref.emptyUUID());

            dao.daoCatalogNomenklaturnieGruppi.create(ref);
        }
        return ref;
    }

    static CatalogCenovieGruppi findOrCreateCenovieGruppi(Context context, Dao dao)
            throws SQLException {

        Random rnd = new Random();
        String[] sku_groups = context.getResources().getStringArray(R.array.sku_groups);
        String desc = sku_groups[rnd.nextInt(sku_groups.length)];

        CatalogCenovieGruppi ref = dao.daoCatalogCenovieGruppi.findByDescription(desc);
        if (Ref.isEmpty(ref)) {

            int lenCode = dao.daoCatalogCenovieGruppi.getCodeLength();
            if (lenCode == 0) {
                lenCode = LARGE_CATALOG_CODE_LENGTH;
            }
            int code = dao.daoCatalogCenovieGruppi.getNextCode();

            ref = dao.daoCatalogCenovieGruppi.newItem();

            ref.setCode(dao.daoCatalogCenovieGruppi.formatNumber(code++, lenCode));
            ref.setDescription(desc);
            ref.setParent(Ref.emptyUUID());

            dao.daoCatalogCenovieGruppi.create(ref);
        }
        return ref;
    }

    static CatalogNomenklatura createNomenclatura(Context context, Dao dao, int lineNumber, String desc) throws SQLException {

        int lenCode = dao.daoCatalogNomenklatura.getCodeLength();
        if(lenCode==0) {
            lenCode = LARGE_CATALOG_CODE_LENGTH;
        }
        int code = dao.daoCatalogNomenklatura.getNextCode();

        CatalogNomenklatura nomenklatura = dao.daoCatalogNomenklatura.newItem();
        nomenklatura.setCode(dao.daoCatalogNomenklatura.formatNumber(code++, lenCode));
        nomenklatura.setDescription(desc);
        if(lineNumber%2==0) {
            nomenklatura.statyaZatrat = dao.daoCatalogStatiZatrat.findByDescription(
                    "Представительские расходы");
        }
        else {
            nomenklatura.statyaZatrat = dao.daoCatalogStatiZatrat.findByCode("000000004");
        }
        nomenklatura.artikul = "артикул ЖЛ-" + String.valueOf(lineNumber);
        nomenklatura.bazovayaEdinicaIzmereniya = dao.daoCatalogKlassifikatorEdinicIzmereniya.findByDescription("упак");
        nomenklatura.vesovoi = false;
        nomenklatura.vestiPartionniiUchetPoSeriyam = true;
        nomenklatura.vestiUchetPoSeriyam = (lineNumber%2==0);
        nomenklatura.vestiUchetPoHarakteristikam = (lineNumber%3==0);
        nomenklatura.edinicaDlyaOtchetov = createEdIzm(context, dao, nomenklatura);
        nomenklatura.edinicaHraneniyaOstatkov = createEdIzm(context, dao, nomenklatura);
        nomenklatura.kommentarii = " создано из fba " + System.currentTimeMillis();
        nomenklatura.nabor = (lineNumber%4==0);
        nomenklatura.naimenovaniePolnoe = "Полное наименование: " + desc;
        nomenklatura.cenovayaGruppa = findOrCreateCenovieGruppi(context, dao);
        nomenklatura.nomerGTD = (CatalogNomeraGTD) Ref.emptyRef(CatalogNomeraGTD.class);
        nomenklatura.stavkaNDS = EnumStavkiNDS.NDS18;
        nomenklatura.usluga = (lineNumber%4==0);
        nomenklatura.vestiSeriinieNomera = (lineNumber%3==0);
        nomenklatura.komplekt = (lineNumber%3==0);
        nomenklatura.edinicaIzmereniyaMest = createEdIzm(context, dao, nomenklatura);
        nomenklatura.dopolnitelnoeOpisanieNomenklaturi = "доп. описание " + desc;
        nomenklatura.nomenklaturnayaGruppa = fingOrcreateNomenklaturnieGruppi(context, dao);

        dao.daoCatalogNomenklatura.create(nomenklatura);
        return nomenklatura;
    }

    static CatalogEdiniciIzmereniya createEdIzm(Context context, Dao dao, CatalogNomenklatura owner) throws SQLException{

        Random rnd = new Random();
        String[] classif_ed_name = context.getResources().getStringArray(R.array.classif_ed_name);
        int koeff[] = context.getResources().getIntArray(R.array.koeff);

        int lenCode = dao.daoCatalogEdiniciIzmereniya.getCodeLength();
        if(lenCode==0) {
            lenCode = LARGE_CATALOG_CODE_LENGTH;
        }
        int code = dao.daoCatalogEdiniciIzmereniya.getNextCode("ЦУ");

        CatalogEdiniciIzmereniya item = dao.daoCatalogEdiniciIzmereniya.newItem();
        item.setCode(dao.daoCatalogEdiniciIzmereniya.formatNumber(code++, lenCode));
        String desc = classif_ed_name[rnd.nextInt(classif_ed_name.length)];
        item.setDescription(desc);
        item.setOwner(owner);
        item.edinicaPoKlassifikatoru = dao.daoCatalogKlassifikatorEdinicIzmereniya.findByDescription(desc);
        item.koefficient = roundMoney(Math.random() * 10,3);
        item.obem = roundMoney(Math.random() * 5,3);
        item.ves = koeff[rnd.nextInt(koeff.length)];

        dao.daoCatalogEdiniciIzmereniya.create(item);
        return item;
    }
    public void testCreate00Kachastvo() throws SQLException {

        int lenCode = dao.daoCatalogKachestvo.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }

        String[] val = getTargetContext().getResources().getStringArray(R.array.kachestvo);
        for (String desc : val) {
            CatalogKachestvo ref = dao.daoCatalogKachestvo.findByDescription(desc);
            if (Ref.isEmpty(ref)) {

                int code = dao.daoCatalogKachestvo.getNextCode();

                ref = dao.daoCatalogKachestvo.newItem();
                ref.setCode(dao.daoCatalogKachestvo.formatNumber(code++, lenCode));
                ref.setDescription(desc);
                ref.setParent(Ref.emptyUUID());

                dao.daoCatalogKachestvo.create(ref);
            }
        }

    }

    public void testCreate00Organizacii() throws SQLException {

        int lenCode = dao.daoCatalogOrganizacii.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }

        String[] val = getTargetContext().getResources().getStringArray(R.array.organization);
        for (String desc : val) {
            CatalogOrganizacii ref = dao.daoCatalogOrganizacii.findByDescription(desc);
            if (Ref.isEmpty(ref)) {

                int code = dao.daoCatalogOrganizacii.getNextCode();

                ref = dao.daoCatalogOrganizacii.newItem();
                ref.setCode(dao.daoCatalogOrganizacii.formatNumber(code++, lenCode));
                ref.setDescription(desc);
                ref.setParent(Ref.emptyUUID());

                dao.daoCatalogOrganizacii.create(ref);
            }
        }

    }


    public void testCreate01SkuGroups() throws SQLException {

        int lenCode = dao.daoCatalogNomenklaturnieGruppi.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }

        String[] sku_groups = getTargetContext().getResources().getStringArray(R.array.sku_groups);
        for (String desc : sku_groups) {
            CatalogNomenklaturnieGruppi ref = dao.daoCatalogNomenklaturnieGruppi.findByDescription(desc);
            if (Ref.isEmpty(ref)) {

                int code = dao.daoCatalogNomenklaturnieGruppi.getNextCode();

                ref = dao.daoCatalogNomenklaturnieGruppi.newItem();
                ref.setCode(dao.daoCatalogNomenklaturnieGruppi.formatNumber(code++, lenCode));
                ref.setDescription(desc);
                ref.setParent(Ref.emptyUUID());

                dao.daoCatalogNomenklaturnieGruppi.create(ref);
            }
        }

    }

    public void testCreate02CenovieGruppi() throws SQLException {

        int lenCode = dao.daoCatalogCenovieGruppi.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }
        int code = dao.daoCatalogCenovieGruppi.getNextCode();

        String[] val = getTargetContext().getResources().getStringArray(R.array.cenovie_gruppi);
        for (String desc : val) {
            CatalogCenovieGruppi ref = dao.daoCatalogCenovieGruppi.findByDescription(desc);
            if (Ref.isEmpty(ref)) {
                ref = dao.daoCatalogCenovieGruppi.newItem();
                ref.setCode(dao.daoCatalogCenovieGruppi.formatNumber(code++, lenCode));
                ref.setDescription(desc);
                ref.setParent(Ref.emptyUUID());

                dao.daoCatalogCenovieGruppi.create(ref);
            }

        }
    }

    public void testCreate03Fio() throws SQLException {

        int lenCode = Dao.daoCatalogFizicheskieLica.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }
        String[] val = getTargetContext().getResources().getStringArray(R.array.fio);
        for (String desc : val) {
            CatalogFizicheskieLica ref = dao.daoCatalogFizicheskieLica.findByDescription(desc);
            if (Ref.isEmpty(ref)) {

                int num = rnd.nextInt(100);
                String code = Dao.daoCatalogFizicheskieLica.formatNumber(num, lenCode);

                ref = Dao.daoCatalogFizicheskieLica.newItem();
                ref.setCode(code);
                ref.setDescription(desc);
                ref.setParent(CatalogFizicheskieLica.emptyUUID());

                Dao.daoCatalogFizicheskieLica.create(ref);
            }

        }

    }

    public void testCreate04Polzovatel() throws SQLException {

        String[] val = getTargetContext()
                .getResources()
                .getStringArray(R.array.polzovatel);
        for (String desc : val) {
            CatalogPolzovateli ref = dao.daoCatalogPolzovateli.findByDescription(desc);
            if (Ref.isEmpty(ref)) {

                String code = desc.replaceAll(" ", "");
                ref = Dao.daoCatalogPolzovateli.newItem();
                ref.setCode(code);
                ref.setDescription(desc);
                ref.setParent(CatalogPolzovateli.emptyUUID());

                Dao.daoCatalogPolzovateli.create(ref);
            }

        }

    }

    public void testCreate05Podtazdeleniya() throws SQLException {

        int lenCode = Dao.daoCatalogPodrazdeleniya.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }

        String[] val = getTargetContext()
                .getResources()
                .getStringArray(R.array.podrazdeleniya);
        for (String desc : val) {
            CatalogPodrazdeleniya ref = dao.daoCatalogPodrazdeleniya.findByDescription(desc);
            if (Ref.isEmpty(ref)) {

                int newCode = Dao.daoCatalogPodrazdeleniya.getNextCode();
                String code = Dao.daoCatalogPodrazdeleniya.formatNumber(newCode, lenCode);

                ref = Dao.daoCatalogPodrazdeleniya.newItem();
                ref.setCode(code);
                ref.setDescription(desc);
                ref.setParent(CatalogPodrazdeleniya.emptyUUID());

                Dao.daoCatalogPodrazdeleniya.create(ref);
            }

        }

    }

    public void testCreate06Valuti() throws SQLException {

        String[] val_desc = getTargetContext().getResources().getStringArray(R.array.valuta_desc);
        String[] val_code = getTargetContext().getResources().getStringArray(R.array.valuta_code);

        for (int i = 0; i < val_code.length; i++) {

            CatalogValyuti ref = dao.daoCatalogValyuti.findByCode(val_code[i]);
            if (Ref.isEmpty(ref)) {
                final String code = val_code[i];

                CatalogValyuti catalog = dao.daoCatalogValyuti.newItem();
                catalog.setCode(code);
                if(VALUTA_USD_CODE.equals(code)) {
                    catalog.setRef(UUID.fromString(VALUTA_USD_ID));
                }
                catalog.setDescription(val_desc[i]);
                dao.daoCatalogValyuti.create(catalog);
            }
        }
    }

    public void testCreate07FolderCatalogCenovieGruppi() throws SQLException {

        int lenCode = dao.daoCatalogCenovieGruppi.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }
        int code = dao.daoCatalogCenovieGruppi.getNextCode();

        CatalogCenovieGruppi catalog = dao.daoCatalogCenovieGruppi.newItem();
        catalog.setCode(dao.daoCatalogCenovieGruppi.formatNumber(code++, lenCode));
        catalog.setDescription("Пуст. родитель, группа " + System.currentTimeMillis());
        catalog.setFolder(true);

        dao.daoCatalogCenovieGruppi.create(catalog);
    }

    public void testCreate08StatyaZatrat() throws SQLException {

        int lenCode = dao.daoCatalogStatiZatrat.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }

        String[] val = getTargetContext()
                .getResources()
                .getStringArray(R.array.st_zatrat);
        for (String desc : val) {
            CatalogStatiZatrat ref = dao.daoCatalogStatiZatrat.findByDescription(desc);
            if (Ref.isEmpty(ref)) {

                int newCode = dao.daoCatalogStatiZatrat.getNextCode();
                String code = Dao.daoCatalogPodrazdeleniya.formatNumber(newCode, lenCode);

                ref = Dao.daoCatalogStatiZatrat.newItem();
                ref.setCode(code);
                ref.setDescription(desc);
                ref.setParent(CatalogStatiZatrat.emptyUUID());

                Dao.daoCatalogStatiZatrat.create(ref);
            }

        }
    }

    public void testCreate09KontragentAndDogovors() throws SQLException {

        int lenCode = dao.daoCatalogKontragenti.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }

        int code = dao.daoCatalogKontragenti.getNextCode();

        int lenCodeD = dao.daoCatalogDogovoriKontragentov.getCodeLength();
        if (lenCodeD == 0) {
            lenCodeD = CATALOG_CODE_LENGTH;
        }
        int codeD = dao.daoCatalogDogovoriKontragentov.getNextCode();

        String[] val = getTargetContext().getResources().getStringArray(R.array.kontragents);
        String[] k_generate = getTargetContext().getResources()
                .getStringArray(R.array.kontragents_gererate);
        for (String desc : val) {
            String category = k_generate[rnd.nextInt(k_generate.length)];
            final String fullName = desc + " " + category;

            CatalogKontragenti ref = dao.daoCatalogKontragenti.findByDescription(fullName);
            if (CatalogKontragenti.isEmpty(ref)) {

                ref = dao.daoCatalogKontragenti.newItem();
                ref.setCode(dao.daoCatalogKontragenti.formatNumber(code++, lenCode));
                ref.setDescription(fullName);
                ref.setParent(Ref.emptyUUID());

                dao.daoCatalogKontragenti.create(ref);

                CatalogDogovoriKontragentov dogovor = dao.daoCatalogDogovoriKontragentov.newItem();
                dogovor.setCode(dao.daoCatalogDogovoriKontragentov.formatNumber(codeD++, lenCodeD));
                dogovor.setDescription("Осн. договор : " + desc);
                dogovor.setOwner(ref);

                dao.daoCatalogDogovoriKontragentov.create(dogovor);
            }
        }

    }

    public void testCreate10Nomenclatura() throws SQLException {

        int lenCode = dao.daoCatalogNomenklatura.getCodeLength();
        if (lenCode == 0) {
            lenCode = LARGE_CATALOG_CODE_LENGTH;
        }
        int code = dao.daoCatalogNomenklatura.getNextCode();

        String[] sku_descr = getTargetContext().getResources().getStringArray(R.array.sku_descr);
        String[] sku_category = getTargetContext().getResources().getStringArray(R.array.sku_category);

        for (int i = 0; i < sku_descr.length; i++) {
            String desc = sku_descr[i];
            String category = sku_category[rnd.nextInt(sku_category.length)];
            final String fullName = desc + " " + category;

            Ref ref = dao.daoCatalogNomenklatura.findByDescription(fullName);
            if (Ref.isEmpty(ref)) {

                CatalogNomenklatura nomenklatura = dao.daoCatalogNomenklatura.newItem();
                nomenklatura.setCode(dao.daoCatalogNomenklatura.formatNumber(code++, lenCode));
                nomenklatura.setDescription(fullName);
                if (i % 2 == 0) {
                    nomenklatura.statyaZatrat = dao.daoCatalogStatiZatrat.findByDescription(
                            "Представительские расходы");
                } else {
                    nomenklatura.statyaZatrat = dao.daoCatalogStatiZatrat.findByCode("000002");
                }
                nomenklatura.artikul = "артикул ЖЛ-" + String.valueOf(i);
                nomenklatura.bazovayaEdinicaIzmereniya = dao.daoCatalogKlassifikatorEdinicIzmereniya
                        .findByDescription("упак");
                nomenklatura.vesovoi = false;
                nomenklatura.vestiPartionniiUchetPoSeriyam = true;
                nomenklatura.vestiUchetPoSeriyam = (i % 2 == 0);
                nomenklatura.vestiUchetPoHarakteristikam = (i % 3 == 0);
                nomenklatura.edinicaDlyaOtchetov = createEdIzm(nomenklatura);
                nomenklatura.edinicaHraneniyaOstatkov = createEdIzm(nomenklatura);
                nomenklatura.kommentarii = " создано из fba " + System.currentTimeMillis();
                nomenklatura.nabor = (i % 4 == 0);
                nomenklatura.naimenovaniePolnoe = "Полное наименование: " + desc;
                nomenklatura.cenovayaGruppa = findCenovayaGruppa();
                nomenklatura.nomerGTD = (CatalogNomeraGTD) Ref.emptyRef(CatalogNomeraGTD.class);
                nomenklatura.stavkaNDS = EnumStavkiNDS.NDS18;
                nomenklatura.usluga = (i % 4 == 0);
                nomenklatura.vestiSeriinieNomera = (i % 3 == 0);
                nomenklatura.komplekt = (i % 3 == 0);
                nomenklatura.edinicaIzmereniyaMest = createEdIzm(nomenklatura);
                nomenklatura.dopolnitelnoeOpisanieNomenklaturi = "доп. описание " + desc;
                nomenklatura.nomenklaturnayaGruppa = findSkuGroup();

                dao.daoCatalogNomenklatura.create(nomenklatura);

            }

        }

    }

    private CatalogEdiniciIzmereniya createEdIzm(CatalogNomenklatura owner) throws SQLException {

        Random rnd = new Random();
        String[] classif_ed_name = getTargetContext().getResources()
                .getStringArray(R.array.classif_ed_name);
        int koeff[] = getTargetContext().getResources().getIntArray(R.array.koeff);

        int lenCode = dao.daoCatalogEdiniciIzmereniya.getCodeLength();
        if (lenCode == 0) {
            lenCode = CATALOG_CODE_LENGTH;
        }
        int code = dao.daoCatalogEdiniciIzmereniya.getNextCode();

        CatalogEdiniciIzmereniya item = dao.daoCatalogEdiniciIzmereniya.newItem();
        item.setCode(dao.daoCatalogEdiniciIzmereniya.formatNumber(code++, lenCode));
        String desc = classif_ed_name[rnd.nextInt(classif_ed_name.length)];
        item.setDescription(desc);
        item.edinicaPoKlassifikatoru = dao.daoCatalogKlassifikatorEdinicIzmereniya.findByDescription(
                desc);
        item.koefficient = Math.random() * 10;
        item.obem = Math.random() * 5;
        item.ves = koeff[rnd.nextInt(koeff.length)];

        dao.daoCatalogEdiniciIzmereniya.create(item);

        return item;
    }

    private CatalogNomenklaturnieGruppi findSkuGroup() throws SQLException {

        Random rnd = new Random();
        String[] sku_groups = getTargetContext().getResources().getStringArray(R.array.sku_groups);
        String desc = sku_groups[rnd.nextInt(sku_groups.length)];

        CatalogNomenklaturnieGruppi ref = dao.daoCatalogNomenklaturnieGruppi.findByDescription(
                desc);
        assertFalse(Ref.isEmpty(ref));
        return ref;
    }

    private CatalogCenovieGruppi findCenovayaGruppa() throws SQLException {

        Random rnd = new Random();
        String[] groups = getTargetContext().getResources().getStringArray(R.array.cenovie_gruppi);
        String desc = groups[rnd.nextInt(groups.length)];

        CatalogCenovieGruppi ref = dao.daoCatalogCenovieGruppi.findByDescription(desc);
        assertFalse(Ref.isEmpty(ref));
        return ref;
    }

}