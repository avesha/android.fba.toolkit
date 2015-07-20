package com.sample.app2.test.db;

import com.sample.app2.exchange.ExchangeManager;
import com.sample.app2.exchange.ExchangeSettings;
import com.sample.app2.test.exchange.TestDataProviderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import ru.profi1c.engine.exchange.ExchangeDataProviderException;
import ru.profi1c.engine.exchange.ExchangeVariant;

public class Test01ExchangeManager extends DBTestCase {
    private static final String TAG = Test01ExchangeManager.class.getSimpleName();

    public static final String TEST_GET_SHORT_DATA_ID = "test_get_short_data_id";
    public static final String TEST_WRITE_SHORT_DATA_ID = "test_write_short_data_id";
    public static final String TEST_GET_LARGE_DATA_ID = "test_get_large_data_id";
    public static final String TEST_WRITE_LARGE_DATA_ID = "test_write_large_data_id";

    public static final String GET_KEY_INT = "ЧисловойКлюч";
    public static final String GET_KEY_STRING = "СтроковыйКлюч";
    public static final String GET_KEY_DATA = "Параметр_Дата";
    public static final String GET_KEY_STRUCT = "ЗаписьСтруктура";
    public static final String GET_TEST_STRING_VALUE = "Строка данных";
    public static final String WRITE_KEY_BOOL = "test_key";
    public static final String WRITE_KEY_LAT = "lat";
    public static final String WRITE_KEY_LNG = "lng";
    public static final String WRITE_KEY_ARRAY = "test_array";
    public static final String WRITE_KEY_INT = "test_int";
    public static final double WRITE_TEST_LAT_VALUE = 45.4788;
    public static final double WRITE_TEST_LNG_VALUE = 37.4568;
    public static final boolean WRITE_TEST_BOOL_VALUE = true;
    public static final int WRITE_TEST_INT_VALUE = 100;
    public static final String WRITE_TEST_ARRAY_VALUE_1 = "value1";
    public static final String WRITE_TEST_ARRAY_VALUE_2 = "value2";
    public static final int GET_TEST_INT_VALUE = 100;

    public static final String TEST_IMAGE_JPG = "test_image.jpg";
    public static final String TEST_PDF_REPORT = "test_report.pdf";

    private ExchangeSettings mExchangeSettings;
    private ExchangeManager mExchangeManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mExchangeSettings = ExchangeSettings.getInstance(getTargetContext());
        mExchangeSettings.setExchangeDataProviderFactory(new TestDataProviderFactory());
        mExchangeSettings.setUserName("test_user_name");
        mExchangeManager = new ExchangeManager(mExchangeSettings);
    }

    public void testStartExchange() throws Exception {
        boolean result = mExchangeManager.startExchange(ExchangeVariant.INIT);
        assertTrue(result);
    }

    public void testGetShortData() throws ExchangeDataProviderException {

        String strJson =
                mExchangeManager.getExchangeStrategy().getShortData(TEST_GET_SHORT_DATA_ID, "");
        assertNotNull(strJson);

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(strJson);
            log(TAG, "testGetShortData: " + jsonObject.toString());

            assertEquals(jsonObject.getInt(GET_KEY_INT), GET_TEST_INT_VALUE);
            assertTrue(jsonObject.getString(GET_KEY_STRING).equals(GET_TEST_STRING_VALUE));
            assertNotNull(jsonObject.get(GET_KEY_DATA));
            assertNotNull(jsonObject.get(GET_KEY_STRUCT));

        } catch (JSONException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    public void testWriteShortData() throws ExchangeDataProviderException {

        String strJson = null;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(WRITE_KEY_BOOL, WRITE_TEST_BOOL_VALUE);
            jsonObject.put(WRITE_KEY_LAT, WRITE_TEST_LAT_VALUE);
            jsonObject.put(WRITE_KEY_LNG, WRITE_TEST_LNG_VALUE);
            JSONArray jArr = new JSONArray();

            jArr.put(WRITE_TEST_ARRAY_VALUE_1);
            jArr.put(WRITE_TEST_ARRAY_VALUE_2);

            jsonObject.put(WRITE_KEY_ARRAY, jArr);
            jsonObject.put(WRITE_KEY_INT, WRITE_TEST_INT_VALUE);

            strJson = jsonObject.toString();
            log(TAG, "testWriteShortData strJson =  " + strJson);

        } catch (JSONException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertNotNull(strJson);

        boolean result = mExchangeManager.getExchangeStrategy()
                                         .writeShortData(TEST_WRITE_SHORT_DATA_ID, strJson, "");
        assertTrue(result);
        log(TAG, "testWriteShortData result =  " + strJson);

    }

    public void testGetLargeData() throws ExchangeDataProviderException {
        File fTmpPic = new File(getApp().getAppSettings().getBackupDir(), TEST_IMAGE_JPG);
        if (fTmpPic.exists()) {
            File fResult = mExchangeManager.getExchangeStrategy()
                                           .getLargeData(TEST_GET_LARGE_DATA_ID, null, null,
                                                         fTmpPic.getAbsolutePath());
            assertNotNull(fResult);
            assertTrue(fResult.exists());
        }
    }

    public void testWriteLargeData() throws ExchangeDataProviderException {
        File f = new File(getApp().getAppSettings().getCacheDir(), TEST_PDF_REPORT);
        try {
            File file = extractAssert("large_data/" + TEST_PDF_REPORT, f.getAbsolutePath());
            if (file != null) {
                boolean result = mExchangeManager.getExchangeStrategy()
                                                 .writeLargeData(TEST_WRITE_LARGE_DATA_ID, null,
                                                                 file.getAbsolutePath(), null);
                assertTrue(result);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
