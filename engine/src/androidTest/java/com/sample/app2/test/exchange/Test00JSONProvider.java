package com.sample.app2.test.exchange;

import android.content.Context;

import com.sample.app2.db.CatalogCenovieGruppi;
import com.sample.app2.db.CatalogDogovoriKontragentov;
import com.sample.app2.db.CatalogEdiniciIzmereniya;
import com.sample.app2.db.CatalogFizicheskieLica;
import com.sample.app2.db.CatalogHarakteristikiNomenklaturi;
import com.sample.app2.db.CatalogHranilischeDopolnitelnoiInformacii;
import com.sample.app2.db.CatalogInformacionnieKarti;
import com.sample.app2.db.CatalogKachestvo;
import com.sample.app2.db.CatalogKlassifikatorEdinicIzmereniya;
import com.sample.app2.db.CatalogKlassifikatorStranMira;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogNomenklaturnieGruppi;
import com.sample.app2.db.CatalogNomeraGTD;
import com.sample.app2.db.CatalogOrganizacii;
import com.sample.app2.db.CatalogPodrazdeleniya;
import com.sample.app2.db.CatalogPolzovateli;
import com.sample.app2.db.CatalogSeriiNomenklaturi;
import com.sample.app2.db.CatalogSeriinieNomera;
import com.sample.app2.db.CatalogSkladi;
import com.sample.app2.db.CatalogStatiZatrat;
import com.sample.app2.db.CatalogTipiCenNomenklaturi;
import com.sample.app2.db.CatalogUsloviyaProdazh;
import com.sample.app2.db.CatalogValyuti;
import com.sample.app2.db.CatalogVidiNomenklaturi;
import com.sample.app2.db.CatalogVneshnieObrabotki;
import com.sample.app2.db.Constants;
import com.sample.app2.db.DocumentAvansoviiOtchet;
import com.sample.app2.db.DocumentSobitie;
import com.sample.app2.db.DocumentVnutrenniiZakaz;
import com.sample.app2.db.DocumentVozvratTovarovOtPokupatelya;
import com.sample.app2.db.DocumentZakazPokupatelya;
import com.sample.app2.db.ExTableGeoDannie;
import com.sample.app2.db.ExTableTestTablica2;
import com.sample.app2.db.RegAdresniiKlassifikator;
import com.sample.app2.db.RegKursiValyut;
import com.sample.app2.db.RegMestaHraneniyaNomenklaturi;
import com.sample.app2.test.base.BaseTestCase;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.exchange.JSONProvider;
import ru.profi1c.engine.util.IOHelper;

public class Test00JSONProvider extends BaseTestCase {
    private static final String TAG = Test00JSONProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final String EXTRA_FOLDER_NAME = "json_sample";
    private JSONProvider mJson;

    private File mDir;

    private final String strJsonSampleConst = "Constants.json";
    private HashMap<String, Class> mMapCatalogs;
    private HashMap<String, Class> mMapDocuments;
    private HashMap<String, Class> mMapExtTables;
    private HashMap<String, String> mInZip;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mJson = new JSONProvider();
        assertNotNull("JSONProvider is null", mJson);

        //catalogs
        mMapCatalogs = new HashMap<String, Class>();
        mMapCatalogs.put("CatalogCenovieGruppi.json", CatalogCenovieGruppi.class);
        mMapCatalogs.put("CatalogDogovoriKontragentov.json", CatalogDogovoriKontragentov.class);
        mMapCatalogs.put("CatalogEdiniciIzmereniya.json", CatalogEdiniciIzmereniya.class);
        mMapCatalogs.put("CatalogFizicheskieLica.json", CatalogFizicheskieLica.class);
        mMapCatalogs.put("CatalogHarakteristikiNomenklaturi.json",
                        CatalogHarakteristikiNomenklaturi.class);
        mMapCatalogs.put("CatalogHranilischeDopolnitelnoiInformacii.json",
                        CatalogHranilischeDopolnitelnoiInformacii.class);
        mMapCatalogs.put("CatalogInformacionnieKarti.json", CatalogInformacionnieKarti.class);
        mMapCatalogs.put("CatalogKachestvo.json", CatalogKachestvo.class);
        mMapCatalogs.put("CatalogKlassifikatorEdinicIzmereniya.json",
                        CatalogKlassifikatorEdinicIzmereniya.class);
        mMapCatalogs.put("CatalogKlassifikatorStranMira.json", CatalogKlassifikatorStranMira.class);
        mMapCatalogs.put("CatalogNomenklatura.json", CatalogNomenklatura.class);
        mMapCatalogs.put("CatalogNomenklaturnieGruppi.json", CatalogNomenklaturnieGruppi.class);
        mMapCatalogs.put("CatalogNomeraGTD.json", CatalogNomeraGTD.class);
        mMapCatalogs.put("CatalogOrganizacii.json", CatalogOrganizacii.class);
        mMapCatalogs.put("CatalogPodrazdeleniya.json", CatalogPodrazdeleniya.class);
        mMapCatalogs.put("CatalogPolzovateli.json", CatalogPolzovateli.class);
        mMapCatalogs.put("CatalogSeriinieNomera.json", CatalogSeriinieNomera.class);
        mMapCatalogs.put("CatalogSeriiNomenklaturi.json", CatalogSeriiNomenklaturi.class);
        mMapCatalogs.put("CatalogSkladi.json", CatalogSkladi.class);
        mMapCatalogs.put("CatalogStatiZatrat.json", CatalogStatiZatrat.class);
        mMapCatalogs.put("CatalogTipiCenNomenklaturi.json", CatalogTipiCenNomenklaturi.class);
        mMapCatalogs.put("CatalogUsloviyaProdazh.json", CatalogUsloviyaProdazh.class);
        mMapCatalogs.put("CatalogValyuti.json", CatalogValyuti.class);
        mMapCatalogs.put("CatalogVidiNomenklaturi.json", CatalogVidiNomenklaturi.class);
        mMapCatalogs.put("CatalogVneshnieObrabotki.json", CatalogVneshnieObrabotki.class);

        //documents
        mMapDocuments = new HashMap<String, Class>();
        mMapDocuments.put("DocumentAvansoviiOtchet.json", DocumentAvansoviiOtchet.class);
        mMapDocuments.put("DocumentSobitie.json", DocumentSobitie.class);
        mMapDocuments.put("DocumentVnutrenniiZakaz.json", DocumentVnutrenniiZakaz.class);
        mMapDocuments.put("DocumentVozvratTovarovOtPokupatelya.json",
                         DocumentVozvratTovarovOtPokupatelya.class);
        mMapDocuments.put("DocumentZakazPokupatelya.json", DocumentZakazPokupatelya.class);

        //extra tables
        mMapExtTables = new HashMap<String, Class>();
        mMapExtTables.put("ExTableGeoDannie.json", ExTableGeoDannie.class);
        mMapExtTables.put("ExTableTestTablica2.json", ExTableTestTablica2.class);
        mMapExtTables.put("RegAdresniiKlassifikator.json", RegAdresniiKlassifikator.class);
        mMapExtTables.put("RegKursiValyut.json", RegKursiValyut.class);
        mMapExtTables.put("RegMestaHraneniyaNomenklaturi.json", RegMestaHraneniyaNomenklaturi.class);

        //in fim arhive
        mInZip = new HashMap<String, String>();
        mInZip.put("RegAdresniiKlassifikator.json", "RegAdresniiKlassifikator.zip");

        mDir = getExtractDir();
        if (!mDir.exists()) {
            mDir.mkdirs();
        }

        extractAllAssets();
    }

    private File getExtractDir() {
        return new File(getApp().getAppSettings().getCacheDir(), EXTRA_FOLDER_NAME);
    }

    private void extractAllAssets() throws IOException {

        extractAsserts(new String[]{strJsonSampleConst});

        String[] array = mMapCatalogs.keySet().toArray(new String[0]);
        extractAsserts(array);

        array = mMapDocuments.keySet().toArray(new String[0]);
        extractAsserts(array);

        array = mMapExtTables.keySet().toArray(new String[0]);
        extractAsserts(array);
    }

    private void extractAsserts(String[] assertNames) throws IOException {
        final Context context = getTargetContext();
        for (String name : assertNames) {
            File file = new File(mDir, name);
            if (!file.exists()) {
                if (mInZip.containsKey(name)) {
                    String zipName = mInZip.get(name);
                    File fZip = new File(mDir, zipName);
                    boolean extract =
                            IOHelper.saveAssetsData(context, EXTRA_FOLDER_NAME + "/" + zipName,
                                                    fZip);
                    assertTrue("Error extract " + zipName, extract);
                    IOHelper.unZipFirst(fZip);

                } else {
                    String data = IOHelper.getAssetsData(context, EXTRA_FOLDER_NAME + "/" + name);
                    IOHelper.writeToFile(data, file.getAbsolutePath());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean deserializeSingle(Class clazz, String fromFile) {

        boolean complete = false;
        String className = clazz.getSimpleName();
        File fileFrom = new File(mDir, fromFile);
        File fileTo = new File(mDir, clazz.getSimpleName() + "_to.json");

        Object obj = mJson.fromJson(clazz, fileFrom);
        assertNotNull(obj);
        log(TAG, " - " + className + " 'file->fromJson (object)' obj.class = " +
                 obj.getClass());

        String strJson = mJson.toJson(obj);
        assertNotNull(strJson);
        log(TAG, " - " + className + " 'toJson(Object)->string' result = " + strJson);

        Object obj2 = mJson.fromJson(clazz, strJson);
        assertNotNull(obj2);
        log(TAG, " - " + className + " 'string->fromJson (object)' obj.class = " + obj2.getClass());

        complete = mJson.toJson(obj, fileTo);
        assertTrue(complete);
        log(TAG, " - " + className + " toJson(object)->file result = " + complete);
        return complete;
    }

    private <T> boolean deserializeArray(Class<T> clazz, String fromFile) {

        boolean complete = false;
        String className = clazz.getSimpleName();
        File fileFrom = new File(mDir, fromFile);
        File fileTo = new File(mDir, clazz.getSimpleName() + "_to.json");

        List<T> lstData = mJson.fromJsonArray(clazz, fileFrom);
        assertNotNull(lstData);

        log(TAG, " - " + className + " 'fromJsonArray->file' deserialize count = " +
                 lstData.size());
        if (lstData.size() > 0) {
            Class clazzRes = lstData.get(0).getClass();
            log(TAG, " - " + className + " 'fromJsonArray->file' class src = class res?: " +
                     clazz.equals(clazzRes));

            if (lstData.size() < 100) {

                String strJson = mJson.toJsonArray(lstData);
                assertNotNull(strJson);
                log(TAG, " - " + className + " 'toJson(Array)->string' result = " + strJson);


                List<T> lstData2 = mJson.fromJsonArray(clazz, strJson);
                assertNotNull(lstData2);
                if (lstData2.size() > 0) {
                    Class clazzRes2 = lstData2.get(0).getClass();
                    log(TAG,
                        " - " + className + " 'fromJsonArray->string' class src = class res?: " +
                        clazz.equals(clazzRes2));
                }
            }
            complete = mJson.toJsonArray(lstData, fileTo);
            assertTrue(complete);
            log(TAG, " - " + className + " toJson(Array)->file result =" + complete);
        }
        return complete;

    }

    @SuppressWarnings("unchecked")
    private void deserializeCollection(HashMap<String, Class> map, String type) {

        int count = 0;
        for (String s : map.keySet()) {
            Class clazz = map.get(s);
            try {
                if (deserializeArray(clazz, s)) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log(TAG, type + " error for class = " + clazz.getSimpleName());
            }
        }
        log(TAG, type + " deserialize " + count + " of " + map.size());
        assertEquals(map.size(), count);
    }

    public void testConst() {
        deserializeArray(Constants.class, strJsonSampleConst);
    }

    public void testCatalogs() {
        deserializeCollection(mMapCatalogs, "Catalogs");
    }

    public void testDocuments() {
        deserializeCollection(mMapDocuments, "Documents");
    }

    public void testExtraTables() {
        deserializeCollection(mMapExtTables, "ExtTables");
    }

}