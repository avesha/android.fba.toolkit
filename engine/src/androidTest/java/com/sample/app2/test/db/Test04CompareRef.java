package com.sample.app2.test.db;

import com.sample.app2.db.CatalogKachestvo;
import com.sample.app2.db.DocumentSobitie;
import com.sample.app2.test.base.BaseTestCase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.util.DateHelper;

public class Test04CompareRef extends BaseTestCase {
    private static final String TAG = Test04CompareRef.class.getSimpleName();

    private List<CatalogKachestvo> lst;
    private String sortCatalog;
    private List<DocumentSobitie> lstDoc;
    private String sortDoc;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initTestData();
    }

    private void initTestData() {

        final String uuid = UUID.randomUUID().toString();

        lst = new ArrayList<CatalogKachestvo>();
        lst.add(getNew(UUID.randomUUID().toString(), "000"));
        lst.add(getNew(uuid, "-100"));
        lst.add(getNew(Ref.EMPTY_REF, null));
        lst.add(getNew(null, null));
        lst.add(getNew(uuid, "new"));
        lst.add(getNew(UUID.randomUUID().toString(), "010"));
        lst.add(getNew(null, "009"));
        lst.add(getNew(Ref.EMPTY_REF, "012"));

        sortCatalog = "-100#000#009#010#012#new#null#null#";

        final Date dt = new Date(System.currentTimeMillis());
        lstDoc = new ArrayList<DocumentSobitie>();
        lstDoc.add(newDoc(dt, "001"));
        lstDoc.add(newDoc(DateHelper.date(2013, Calendar.DECEMBER, 31), "002"));
        lstDoc.add(newDoc(DateHelper.addDays(dt, -1), "002"));
        lstDoc.add(newDoc(DateHelper.addDays(dt, -2), "004"));
        lstDoc.add(newDoc(null, "006"));
        lstDoc.add(newDoc(DateHelper.addDays(dt, 2), "005"));
        lstDoc.add(newDoc(null, "005"));

        sortDoc = "002#004#002#001#005#005#006#";
    }

    private CatalogKachestvo getNew(String uuid, String desc) {
        CatalogKachestvo ref = new CatalogKachestvo();
        ref.setDescription(desc);
        if (uuid != null) {
            ref.setRef(UUID.fromString(uuid));
        }
        return ref;
    }

    private DocumentSobitie newDoc(Date date, String number) {
        DocumentSobitie doc = new DocumentSobitie();
        doc.setRef(UUID.randomUUID());
        doc.setDate(date);
        doc.setNumber(number);
        return doc;
    }

    public void testSortCatalog() {
        StringBuilder sb = new StringBuilder();
        Collections.sort(lst);
        for (CatalogKachestvo item : lst) {
            log(TAG , "testSortCatalog, item = " + item.getDescription());
            sb.append(item.getDescription()).append("#");
        }
        assertEquals(sortCatalog, sb.toString());
    }

    public void testSortDoc() {
        StringBuilder sb = new StringBuilder();
        Collections.sort(lstDoc);
        for (DocumentSobitie item : lstDoc) {
            log(TAG + "testSortDoc, item = " + item.getPresentation());
            sb.append(item.getNumber()).append("#");
        }
        assertEquals(sortDoc, sb.toString());
    }

}
