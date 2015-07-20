package com.sample.app2.test.db;

import com.j256.ormlite.dao.ForeignCollection;
import com.sample.app2.R;
import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogHarakteristikiNomenklaturi;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.DocumentAvansoviiOtchet;
import com.sample.app2.db.DocumentSobitie;
import com.sample.app2.db.DocumentVnutrenniiZakaz;
import com.sample.app2.db.DocumentVnutrenniiZakazTPTovari;
import com.sample.app2.db.DocumentVnutrenniiZakazTPVozvratnayaTara;
import com.sample.app2.db.DocumentVozvratTovarovOtPokupatelya;
import com.sample.app2.db.DocumentZakazPokupatelya;
import com.sample.app2.db.EnumVidiVnutrennegoZakaza;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.util.DateHelper;

public class Test06DocumentChange extends DBTestCase {
    private static final String TAG = Test06DocumentChange.class.getSimpleName();

    public void testMarkAsDeleteDocumentZakazPokupatelya() throws SQLException {

        int lenNumber = dao.daoDocumentZakazPokupatelya.getNumberLength();
        if (lenNumber == 0) {
            lenNumber = DOCUMENT_NUMBER_LENGTH;
        }

        int num = dao.daoDocumentZakazPokupatelya.getNextNumber("ТК") - 1;
        String number = dao.daoDocumentZakazPokupatelya.formatNumber("ТК", num, lenNumber);
        DocumentZakazPokupatelya doc = dao.daoDocumentZakazPokupatelya.findByNumber(number);
        if (!DocumentZakazPokupatelya.isEmpty(doc)) {
            doc.setDeletionMark(true);
            doc.setPosted(false); //помеченный на удаление не может быть проведенным
            doc.setModified(true);

            dao.daoDocumentZakazPokupatelya.update(doc);
        } else {
            fail("not found last doc");
        }
    }

    public void testDeleteDocumentSobitie() throws SQLException {

        int lenNumber = dao.daoDocumentSobitie.getNumberLength();
        if (lenNumber == 0) {
            lenNumber = DOCUMENT_NUMBER_LENGTH;
        }

        final int need_change = 5;
        int lastNumber = dao.daoDocumentSobitie.getNextNumber();
        int count = 0;
        for (int i = 1; i < lastNumber; i++) {
            String number = dao.daoDocumentSobitie.formatNumber(i, lenNumber);
            DocumentSobitie doc = dao.daoDocumentSobitie.findByNumber(number);
            if (!DocumentSobitie.isEmpty(doc)) {
                doc.setDeletionMark(true);
                doc.setPosted(false); //помеченный на удаление не может быть проведенным
                doc.setModified(true);
                dao.daoDocumentSobitie.update(doc);
                count++;
                if (count == need_change) {
                    break;
                }
            }
        }
        assertEquals(need_change, count);
    }

    public void testCahngeDocumentVnutrenniiZakaz() throws SQLException {

        final String prefix = "АА";
        int lenNumber = dao.daoDocumentVnutrenniiZakaz.getNumberLength(prefix);
        if (lenNumber == 0) {
            lenNumber = DOCUMENT_NUMBER_LENGTH;
        }

        final int need_change = 10;
        int lastNumber = dao.daoDocumentVnutrenniiZakaz.getNextNumber(prefix);
        int count = 0;
        for (int i = 1; i < lastNumber; i++) {
            String number = dao.daoDocumentVnutrenniiZakaz.formatNumber(prefix, i, lenNumber);

            DocumentVnutrenniiZakaz doc = dao.daoDocumentVnutrenniiZakaz.findByNumber(number);
            if (!DocumentVnutrenniiZakaz.isEmpty(doc)) {

                if (i % 2 == 0) {
                    doc.setDeletionMark(true);
                    doc.setPosted(false);
                }

                if (i % 3 == 0) {
                    doc.kommentarii = "изменен только этот реквизит";
                }

                if (i % 4 == 0) {
                    //Выборка строк табличной части
                    ForeignCollection<DocumentVnutrenniiZakazTPTovari> tpTovati = doc.tovari;
                    for (DocumentVnutrenniiZakazTPTovari row : tpTovati) {
                        row.kolichestvo = row.kolichestvo + 1;
                        row.koefficient = 0;
                        tpTovati.update(row);
                    }
                }

                if (i % 5 == 0) {
                    //Очистка табличной части документа
                    ForeignCollection<DocumentVnutrenniiZakazTPVozvratnayaTara> tpTara = doc.vozvratnayaTara;
                    if (tpTara.size() > 0) {
                        tpTara.clear();
                    }
                }

                if (i % 6 == 0) {
                    //Добавление строки в табличную часть
                    ForeignCollection<DocumentVnutrenniiZakazTPTovari> tpTovati = doc.tovari;
                    int lineNumber = tpTovati.size() + 1;
                    DocumentVnutrenniiZakazTPTovari row = newDocumentVnutrenniiZakazTPTovari(doc,
                            lineNumber);
                    tpTovati.add(row);

                }
                doc.setModified(true);
                dao.daoDocumentVnutrenniiZakaz.update(doc);
                count++;
                if (count == need_change) {
                    break;
                }
            }

        }
        assertEquals(need_change, count);
    }

    private DocumentVnutrenniiZakazTPTovari newDocumentVnutrenniiZakazTPTovari(
            DocumentVnutrenniiZakaz owner, int lineNumber) throws SQLException {

        CatalogNomenklatura nomen = findCreateNomenclatura();

        DocumentVnutrenniiZakazTPTovari row = dao.daoDocumentVnutrenniiZakazTPTovari.newItem(owner,
                lineNumber);
        row.nomenklatura = nomen;
        row.edinicaIzmereniya = nomen.edinicaHraneniyaOstatkov;
        row.edinicaIzmereniyaMest = nomen.edinicaIzmereniyaMest;
        row.harakteristikaNomenklaturi = (CatalogHarakteristikiNomenklaturi) Ref.emptyRef(
                CatalogHarakteristikiNomenklaturi.class);
        row.koefficient = Test02CatalogCreate.roundMoney(rnd.nextDouble() * 10, 2);
        row.kolichestvo = Test02CatalogCreate.roundMoney(rnd.nextDouble() * 100, 2);
        row.kolichestvoMest = (lineNumber % 2 == 0) ? 1 : 0;

        return row;
    }

    private CatalogNomenklatura findCreateNomenclatura() throws SQLException {

        String[] sku_descr = getTargetContext().getResources().getStringArray(R.array.sku_descr);
        String desc = sku_descr[rnd.nextInt(sku_descr.length)];

        CatalogNomenklatura ref = dao.daoCatalogNomenklatura.findByDescription(desc);
        if (CatalogNomenklatura.isEmpty(ref)) {
            ref = Test02CatalogCreate.createNomenclatura(getTargetContext(), dao, rnd.nextInt(10),
                    desc);
        }
        return ref;
    }

    public void testFindByNumber() throws SQLException {
        //Найти по номеру
        DocumentAvansoviiOtchet doc = dao.daoDocumentAvansoviiOtchet.findByNumber("ССН00000001");
        assertNotNull(doc);
        assertEquals(doc.getNumber(), "ССН00000001");
    }

    public void testFindByNumberInterval() throws SQLException {
        //Найти по номеру в интервале дат
        Date dtBegin = DateHelper.date(2007, Calendar.MARCH, 1);
        Date dtEnd = DateHelper.date(2007, Calendar.MARCH, 30);

        DocumentSobitie doc = dao.daoDocumentSobitie.findByNumber("00000000047", dtBegin, dtEnd);
        assertNotNull(doc);
        assertEquals(doc.getNumber(), "00000000047");
    }

    public void testFindByAttribyte() throws SQLException {
        //Найти по значению атрибута
        DocumentVnutrenniiZakaz doc = dao.daoDocumentVnutrenniiZakaz.findByAttribute(
                DocumentVnutrenniiZakaz.FIELD_NAME_VID_ZAKAZA,
                EnumVidiVnutrennegoZakaza.VPodrazdelenie);
        assertNotNull(doc);
        assertEquals(doc.vidZakaza, EnumVidiVnutrennegoZakaza.VPodrazdelenie);
    }

    public void testSelect() throws SQLException {
        //Выбрать все документы
        List<DocumentAvansoviiOtchet> lst = dao.daoDocumentAvansoviiOtchet.select();
        assertNotNull(lst);
        assertTrue(lst.size() > 4);
        for (DocumentAvansoviiOtchet doc : lst) {
            log(TAG, "testSelect doc = " + doc.getPresentation());
        }
    }

    public void testSelectInterval() throws SQLException {
        //Выбрать все документы за интервал
        Date dtBegin = DateHelper.date(2007, Calendar.MAY, 5);
        Date dtEnd = DateHelper.endOfDay(dtBegin);

        List<DocumentSobitie> lst = dao.daoDocumentSobitie.select(dtBegin, dtEnd);
        assertNotNull(lst);
        assertEquals(lst.size(), 1);

        for (DocumentSobitie doc : lst) {
            log(TAG, " testSelectInterval doc = " + doc.getPresentation());
        }
    }

    public void testSelectIntervalAndFilder() throws SQLException {
        //Выбрать все за интервал с дополнительным отбором по реквизиту
        Date dtBegin = DateHelper.date(2007, Calendar.FEBRUARY, 2);
        Date dtEnd = DateHelper.endOfDay(dtBegin);

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(DocumentVnutrenniiZakaz.FIELD_NAME_VID_ZAKAZA,
                EnumVidiVnutrennegoZakaza.NaSklad);

        List<DocumentVnutrenniiZakaz> lst = dao.daoDocumentVnutrenniiZakaz.select(dtBegin, dtEnd,
                filter);
        assertNotNull(lst);
        assertEquals(lst.size(), 1);

        for (DocumentVnutrenniiZakaz doc : lst) {
            log(TAG, "testSelectIntervalAndFilder doc = " + doc.getPresentation());
        }
    }

    public void testSelectFilter() throws SQLException {

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(DocumentVnutrenniiZakaz.FIELD_NAME_VID_ZAKAZA,
                EnumVidiVnutrennegoZakaza.VPodrazdelenie);
        filter.put(Document.FIELD_NAME_DELETIONMARK, false);

        List<DocumentVnutrenniiZakaz> lst = dao.daoDocumentVnutrenniiZakaz.select(null, null,
                filter);
        assertNotNull(lst);
        assertTrue(lst.size() > 0);

        for (DocumentVnutrenniiZakaz doc : lst) {
            log(TAG, "testSelectFilder doc = " + doc.getPresentation());
        }

    }

    public void testSelectOrder() throws SQLException {

        List<DocumentVozvratTovarovOtPokupatelya> lst = dao.daoDocumentVozvratTovarovOtPokupatelya.select(
                null, null, null, Document.FIELD_NAME_DATE + " DESC");
        assertNotNull(lst);

        for (DocumentVozvratTovarovOtPokupatelya doc : lst) {
            log(TAG + ".testSelectOrder doc = " + doc.getPresentation());
        }
    }

    public void testCreateItem() throws SQLException {
        /*
         * Простой пример создания 2-х документов, заполняются только дата и номер.
		 */
        int lenCode = DOCUMENT_NUMBER_LENGTH;

        int numerator = dao.daoDocumentSobitie.getNextNumber();

        // 1
        String strNumber = dao.daoDocumentSobitie.formatNumber(numerator++, lenCode);
        DocumentSobitie doc = dao.daoDocumentSobitie.newItem();
        doc.setDate(new Date(System.currentTimeMillis()));
        doc.setNumber(strNumber);
        dao.daoDocumentSobitie.create(doc);

        // 2
        strNumber = dao.daoDocumentSobitie.formatNumber(numerator++, lenCode);
        doc = dao.daoDocumentSobitie.newItem();
        doc.setDate(new Date(System.currentTimeMillis()));
        doc.setNumber(strNumber);
        dao.daoDocumentSobitie.create(doc);
    }

    public void testCreateItemTP() throws SQLException {
        /*
         * Пример создания документа, добавление 2 строк в табличную часть.
		 */
        int lenCode = DOCUMENT_NUMBER_LENGTH;


        int numerator = dao.daoDocumentVnutrenniiZakaz.getNextNumber("ТК");
        Date now = new Date(System.currentTimeMillis());

        String strNumber = dao.daoDocumentVnutrenniiZakaz.formatNumber("ТК", numerator++, lenCode);

        DocumentVnutrenniiZakaz doc = dao.daoDocumentVnutrenniiZakaz.newItem();
        doc.setDate(now);
        doc.setNumber(strNumber);

        doc.dataOtgruzki = DateHelper.addDays(now, 1);
        doc.dokumentOsnovanie = (DocumentSobitie) Ref.emptyRef(DocumentSobitie.class);
        doc.ispolnitel = dao.daoCatalogFizicheskieLica.findByDescription("Волков А. И.");
        doc.kommentarii = "здесь текст комметария";
        doc.organizaciya = dao.daoCatalogOrganizacii.findByCode("000000001");
        doc.otvetstvennii = dao.daoCatalogPolzovateli.findByDescription(
                "Сидорова Надежда Петровна");
        doc.vidZakaza = EnumVidiVnutrennegoZakaza.NaSklad;

        //записать док. в базу данных
        dao.daoDocumentVnutrenniiZakaz.create(doc);

        //табличная часть "Товары"
        int lineNumber = dao.daoDocVnZakazTPTovati.getNextLineNumber(doc);

        //строка 1
        DocumentVnutrenniiZakazTPTovari row = dao.daoDocVnZakazTPTovati.newItem(doc, lineNumber++);
        CatalogNomenklatura sku = dao.daoCatalogNomenklatura.findByDescription("BOSCH");
        row.nomenklatura = sku;
        row.edinicaIzmereniya = sku.edinicaHraneniyaOstatkov;    //нужна только ссылка
        row.edinicaIzmereniyaMest = (CatalogEdiniciIzmereniya) Ref.emptyRef(
                CatalogEdiniciIzmereniya.class);
        row.harakteristikaNomenklaturi = (CatalogHarakteristikiNomenklaturi) Ref.emptyRef(
                CatalogHarakteristikiNomenklaturi.class);
        row.kolichestvo = 10;
        row.koefficient = 1;
        //сохранить строку 1
        dao.daoDocVnZakazTPTovati.create(row);

        row = dao.daoDocVnZakazTPTovati.newItem(doc, lineNumber++);
        sku = dao.daoCatalogNomenklatura.findByDescription("Кофеварка JACOBS (Австрия)");
        CatalogEdiniciIzmereniya ed = sku.edinicaHraneniyaOstatkov;
        //прочитать значения ссылки т.к будем брать коэффициент от единицы «через точку»
        dao.daoCatalogEdiniciIzmereniya.refresh(ed);

        row.nomenklatura = sku;
        row.edinicaIzmereniya = ed;
        row.kolichestvo = 99;
        row.koefficient = ed.koefficient;  //Единица должна быть считана

        //сохранить строку 2
        dao.daoDocVnZakazTPTovati.create(row);
    }

    public void testCreateTPForeignCollection() throws SQLException {

        int lenCode = DOCUMENT_NUMBER_LENGTH;

        int numerator = dao.daoDocumentVnutrenniiZakaz.getNextNumber("FC");
        Date now = new Date(System.currentTimeMillis());

        String strNumber = dao.daoDocumentVnutrenniiZakaz.formatNumber("FC", numerator++, lenCode);

        DocumentVnutrenniiZakaz doc = dao.daoDocumentVnutrenniiZakaz.newItem();
        doc.setDate(now);
        doc.setNumber(strNumber);

        doc.dataOtgruzki = DateHelper.addDays(now, 2);
        doc.dokumentOsnovanie = (DocumentSobitie) Ref.emptyRef(DocumentSobitie.class);
        doc.ispolnitel = dao.daoCatalogFizicheskieLica.findByDescription("Копытин Ф. И.");
        doc.kommentarii = "create as foregain connection";
        doc.organizaciya = dao.daoCatalogOrganizacii.findByCode("000000002");
        doc.otvetstvennii = dao.daoCatalogPolzovateli.findByDescription("Масюк Динара Викторовна");
        doc.vidZakaza = EnumVidiVnutrennegoZakaza.VPodrazdelenie;

        //табличная часть Товары-------------------------------
        int lineNumber = dao.daoDocVnZakazTPTovati.getNextLineNumber(doc);

        //СНАЧАЛА записать док. в базу данных, иначе строки ТЧ за запишутся т.к проверяется вторичный ключ
        dao.daoDocumentVnutrenniiZakaz.create(doc);

        //пустая табличная часть
        doc.tovari = dao.daoDocumentVnutrenniiZakaz.getEmptyForeignCollection("tovari");

        //строка 1
        DocumentVnutrenniiZakazTPTovari row = dao.daoDocVnZakazTPTovati.newItem(doc, lineNumber++);
        CatalogNomenklatura sku = dao.daoCatalogNomenklatura.findByDescription("BOSCH");
        row.nomenklatura = sku;
        row.edinicaIzmereniya = sku.edinicaHraneniyaOstatkov;    //ref
        row.edinicaIzmereniyaMest = (CatalogEdiniciIzmereniya) Ref.emptyRef(
                CatalogEdiniciIzmereniya.class);
        row.harakteristikaNomenklaturi = (CatalogHarakteristikiNomenklaturi) Ref.emptyRef(
                CatalogHarakteristikiNomenklaturi.class);
        row.kolichestvo = 888;
        row.koefficient = 1;

        //сохранить строку 1 ТЧ в базу
        doc.tovari.add(row);

        //строка 2
        row = dao.daoDocVnZakazTPTovati.newItem(doc, lineNumber++);
        sku = dao.daoCatalogNomenklatura.findByDescription("Комбайн кухонный BINATONE FP 67");
        CatalogEdiniciIzmereniya ed = sku.edinicaHraneniyaOstatkov;
        //прочитать значения ссылки
        dao.daoCatalogEdiniciIzmereniya.refresh(ed);
        row.nomenklatura = sku;
        row.edinicaIzmereniya = ed;
        row.kolichestvo = 2;
        row.koefficient = ed.koefficient;

        //сохранить строку 2 ТЧ в базу
        doc.tovari.add(row);
    }

    public void testCascadeDelete() throws SQLException {
        // Удаление документа (если включено каскадное удаление, табличная
        // часть так же будет удалена)
        DocumentVnutrenniiZakaz doc = dao.daoDocumentVnutrenniiZakaz.findByNumber("ТК000000007");
        dao.daoDocumentVnutrenniiZakaz.delete(doc);
    }

    public void testSetModifiedAllOff() throws SQLException {
        dao.daoDocumentAvansoviiOtchet.setModified(false);
    }

    public void testSetModifiedAllOn() throws SQLException {
        dao.daoDocumentVnutrenniiZakaz.setModified(true);
    }

    public void testSetModifiedAllOnFilter() throws SQLException {
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(DocumentSobitie.FIELD_NAME_NUMBER, "00000000045");
        dao.daoDocumentSobitie.setModified(true, filter);
    }

}
