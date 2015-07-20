package com.sample.app2.test.db;

import android.content.Context;

import com.sample.app2.R;
import com.sample.app2.db.CatalogFizicheskieLica;
import com.sample.app2.db.CatalogHarakteristikiNomenklaturi;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogPodrazdeleniya;
import com.sample.app2.db.DocumentSobitie;
import com.sample.app2.db.DocumentVnutrenniiZakaz;
import com.sample.app2.db.DocumentVnutrenniiZakazTPTovari;
import com.sample.app2.db.DocumentVnutrenniiZakazTPVozvratnayaTara;
import com.sample.app2.db.DocumentZakazPokupatelya;
import com.sample.app2.db.EnumVidiVnutrennegoZakaza;

import java.sql.SQLException;
import java.util.Date;

import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.util.DateHelper;

public class Test05DocumentCreate extends DBTestCase {

    public void testCreateDocumentZakazPokupatelya() throws SQLException {
        final String prefix = "ТК";
        int lenNumber = dao.daoDocumentZakazPokupatelya.getNumberLength(prefix);
        if (lenNumber == 0) {
            lenNumber = DOCUMENT_NUMBER_LENGTH;
        }
        int number = dao.daoDocumentZakazPokupatelya.getNextNumber(prefix);
        for (int i = 0; i < 10; i++) {
            DocumentZakazPokupatelya doc = dao.daoDocumentZakazPokupatelya.newItem();
            doc.setNumber(dao.daoDocumentZakazPokupatelya.formatNumber(prefix, number++, lenNumber));
            doc.setDate(DateHelper.addDays(new Date(System.currentTimeMillis()), i));
            doc.setPosted(i % 2 == 0);
            dao.daoDocumentZakazPokupatelya.create(doc);
        }
    }

    public void testCreateDocumentSobitie() throws SQLException {
        int lenNumber = dao.daoDocumentSobitie.getNumberLength();
        if (lenNumber == 0) {
            lenNumber = DOCUMENT_NUMBER_LENGTH;
        }
        int number = dao.daoDocumentSobitie.getNextNumber();
        for (int i = 0; i < 20; i++) {
            DocumentSobitie doc = dao.daoDocumentSobitie.newItem();
            doc.setNumber(dao.daoDocumentSobitie.formatNumber(number++, lenNumber));
            doc.setDate(DateHelper.addDays(new Date(System.currentTimeMillis()), i));
            doc.setPosted(i % 3 == 0);
            dao.daoDocumentSobitie.create(doc);
        }
    }

    public void testCreateDocumentVnutrenniiZakaz() throws SQLException {
            final Context context = getTargetContext();
            int lenNumber = dao.daoDocumentVnutrenniiZakaz.getNumberLength();
            if(lenNumber==0) {
                lenNumber = DOCUMENT_NUMBER_LENGTH;
            }
            int number = dao.daoDocumentVnutrenniiZakaz.getNextNumber("АА");
            for(int i=0; i< 10;i++){
                DocumentVnutrenniiZakaz doc = dao.daoDocumentVnutrenniiZakaz.newItem();
                doc.setNumber(dao.daoDocumentVnutrenniiZakaz.formatNumber("АА",number++, lenNumber));
                doc.setDate(DateHelper.addDays(new Date(System.currentTimeMillis()), 1));
                doc.setPosted(i%3==0);
                if(i%2==0){
                    doc.dataOtgruzki = DateHelper.addDays(new Date(System.currentTimeMillis()),7);
                    doc.ispolnitel = Test02CatalogCreate.findOrCreateFio(context, dao);
                    doc.podrazdelenie =Test02CatalogCreate.findOrCreatePodtazdelenie(context, dao);
                    doc.podrazdelenieIspolnitel = (CatalogPodrazdeleniya) Ref.emptyRef(
                            CatalogPodrazdeleniya.class);
                }
                if(i%3==0){
                    doc.dokumentOsnovanie = selectDocOsnovanie();
                    doc.ispolnitel = (CatalogFizicheskieLica) Ref.emptyRef(CatalogFizicheskieLica.class);
                }
                doc.kommentarii = "create from fba " + System.currentTimeMillis();
                doc.organizaciya = Test02CatalogCreate.findOrCreateOrganization(context, dao);
                doc.otvetstvennii = Test02CatalogCreate.findOrCreatePolzovatel(context, dao);
                doc.vidZakaza = EnumVidiVnutrennegoZakaza.values()[rnd.nextInt(EnumVidiVnutrennegoZakaza.values().length)];

                //записать новый документа
                dao.daoDocumentVnutrenniiZakaz.create(doc);

                //табличная часть товары
                int countT = rnd.nextInt(20);
                for(int j = 0; j< countT; j++){
                    addVnutrenniiZakazTablePartTovar(j+1,doc);
                }

                //табличная часть возврат
                countT = rnd.nextInt(10);
                for(int k = 0; k< countT;k++){
                    addVnutrenniiZakazTablePartTara(k+1,doc);
                }
            }
    }

    private void addVnutrenniiZakazTablePartTara(int lineNumber,
            DocumentVnutrenniiZakaz owner) throws SQLException {
        CatalogNomenklatura nomen = findCreateNomenclatura();
        DocumentVnutrenniiZakazTPVozvratnayaTara row = dao.daoDocumentVnutrenniiZakazTPVozvratnayaTara.newItem(
                owner, lineNumber);
        row.kolichestvo = rnd.nextInt(100);
        row.nomenklatura = nomen;
        dao.daoDocumentVnutrenniiZakazTPVozvratnayaTara.create(row);
    }

    private void addVnutrenniiZakazTablePartTovar(int lineNumber,
            DocumentVnutrenniiZakaz owner) throws SQLException {
        CatalogNomenklatura nomen = findCreateNomenclatura();
        DocumentVnutrenniiZakazTPTovari row = dao.daoDocumentVnutrenniiZakazTPTovari.newItem(owner, lineNumber);
        row.nomenklatura = nomen;
        row.edinicaIzmereniya = nomen.edinicaHraneniyaOstatkov;
        row.edinicaIzmereniyaMest = nomen.edinicaIzmereniyaMest;
        row.harakteristikaNomenklaturi = (CatalogHarakteristikiNomenklaturi) Ref.emptyRef(CatalogHarakteristikiNomenklaturi.class);
        row.koefficient = Test02CatalogCreate.roundMoney(rnd.nextDouble() * 10, 2);
        row.kolichestvo = Test02CatalogCreate.roundMoney(rnd.nextDouble() * 100, 2);
        row.kolichestvoMest = (lineNumber%2==0)? 1:0;
        dao.daoDocumentVnutrenniiZakazTPTovari.create(row);
    }

    private CatalogNomenklatura findCreateNomenclatura() throws SQLException {
        String[] sku_descr = getTargetContext().getResources().getStringArray(R.array.sku_descr);
        String desc = sku_descr[rnd.nextInt(sku_descr.length)];
        CatalogNomenklatura ref = dao.daoCatalogNomenklatura.findByDescription(desc);
        if(CatalogNomenklatura.isEmpty(ref)){
            ref = Test02CatalogCreate.createNomenclatura(getTargetContext(), dao, rnd.nextInt(10),
                    desc);
        }
        return ref;
    }

    private DocumentSobitie selectDocOsnovanie() throws SQLException {
        int num = rnd.nextInt(100);
        int len = dao.daoDocumentSobitie.getNumberLength();
        String number = dao.daoDocumentSobitie.formatNumber(num, len);
        return dao.daoDocumentSobitie.findByNumber(number);
    }

}
