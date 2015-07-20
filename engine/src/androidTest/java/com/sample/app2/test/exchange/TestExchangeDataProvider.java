package com.sample.app2.test.exchange;

import android.content.Context;
import android.text.TextUtils;

import com.sample.app2.App;
import com.sample.app2.test.base.AndroidTestCase;
import com.sample.app2.test.db.Test01ExchangeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.exchange.ExchangeDataProviderException;
import ru.profi1c.engine.exchange.IExchangeDataProvider;
import ru.profi1c.engine.exchange.JSONProvider;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.util.IOHelper;

public class TestExchangeDataProvider implements IExchangeDataProvider {
    private static final String TAG = TestExchangeDataProvider.class.getSimpleName();
    private static final String EXTRA_FOLDER_NAME = "json_sample";

    private final BaseExchangeSettings mSettings;
    private final List<Class> mAllDbClasses;
    private final JSONProvider mJson;
    private int mConnectionTimeout = 5000;

    private HashMap<String, String> mInZip;

    TestExchangeDataProvider(BaseExchangeSettings settings) {
        mSettings = settings;
        MetadataHelper metadataHelper = getApp().getMetadataHelper();
        mAllDbClasses = metadataHelper.getAllObjectClasses();
        mJson = new JSONProvider();

        mInZip = new HashMap<String, String>();
        mInZip.put("RegAdresniiKlassifikator.json", "RegAdresniiKlassifikator.zip");
    }

    private Context getContext() {
        return mSettings.getContext();
    }

    private App getApp() {
        return (App) App.from(getContext().getApplicationContext());
    }

    private Class<?> findMetaClass(final String metaType, final String metaName) {
        for (Class<?> clazz : mAllDbClasses) {
            String type = MetadataHelper.getMetadataType(clazz);
            String name = MetadataHelper.getMetadataName(clazz);

            if (metaType.equals(type) && metaName.equals(name)) {
                return clazz;
            }
        }
        return null;
    }

    private String getAssertFileName(final String metaType, final String metaName) {
        Class<?> clazz = findMetaClass(metaType, metaName);
        if (clazz != null) {
            String assertName = MetadataHelper.getDatabaseTableName(clazz);
            if (!TextUtils.isEmpty(assertName)) {
                return assertName + ".json";
            }
        }
        return null;
    }

    private File extractAsserts(String assertNames, String fPath) throws IOException {
        final Context context = getContext();
        File file = new File(fPath);
        if (!file.exists()) {
            if (mInZip.containsKey(assertNames)) {

                String zipName = mInZip.get(assertNames);
                File fZip = new File(file.getParentFile(), zipName);
                boolean extract =
                        IOHelper.saveAssetsData(context, EXTRA_FOLDER_NAME + "/" + zipName, fZip);
                if (extract) {
                    IOHelper.unZipFirst(fZip);
                } else {
                    file = null;
                }

            } else {
                String data =
                        IOHelper.getAssetsData(context, EXTRA_FOLDER_NAME + "/" + assertNames);
                IOHelper.writeToFile(data, file.getAbsolutePath());
            }
        }
        return file;
    }

    private File extractAssert(String assertPath, String fPath) throws IOException {
        final Context context = getContext();
        File file = new File(fPath);
        if (!file.exists()) {
            if (IOHelper.saveAssetsData(context, assertPath, file)) {
                return file;
            }
        }
        return null;
    }

    @Override
    public int getConnectionTimeout() {
        return mConnectionTimeout;
    }

    @Override
    public void setConnectionTimeout(int ms) {
        mConnectionTimeout = ms;
    }

    @Override
    public boolean login(String appId, String deviceId, String userName, String password)
            throws ExchangeDataProviderException {
        return true;
    }

    @Override
    public boolean isWorkingVersionApp(String appId, int appVersion, String deviceId)
            throws ExchangeDataProviderException {
        return true;
    }

    @Override
    public File getApp(String appId, int appVersion, String deviceId, String fPath)
            throws ExchangeDataProviderException {
        return null;
    }

    @Override
    public File getData(String appId, String deviceId, boolean all, String metaType,
            String metaName, String addonParams, String fPath)
            throws ExchangeDataProviderException {

        String assertName = getAssertFileName(metaType, metaName);
        if (!TextUtils.isEmpty(assertName)) {
            try {
                return extractAsserts(assertName, fPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean registerDataReceipt(String appId, String deviceId, String addonParams)
            throws ExchangeDataProviderException {
        return true;
    }

    @Override
    public boolean writeData(String appId, String deviceId, String metaType, String metaName,
            String fPath, String addonParams) throws ExchangeDataProviderException {

        Class<?> clazz = findMetaClass(metaType, metaName);
        if (clazz == null) {
            throw new ExchangeDataProviderException("Meta class not found!");
        }
        File file = new File(fPath);
        if (file.exists()) {
            List<?> lst = mJson.fromJsonArray(clazz, file.getAbsoluteFile());
            if (lst == null) {
                throw new ExchangeDataProviderException(
                        "Error deserialize file: " + file.getAbsolutePath());
            }
        } else {
            throw new ExchangeDataProviderException("Not found file " + file.getAbsolutePath());
        }
        return true;
    }

    @Override
    public File getLargeData(String appId, String deviceId, String id, String ref,
            String addonParams, String fPath) throws ExchangeDataProviderException {

        if (Test01ExchangeManager.TEST_GET_LARGE_DATA_ID.equals(id)) {
            File file = new File(getApp().getAppSettings().getCacheDir(),
                                 Test01ExchangeManager.TEST_IMAGE_JPG);
            try {
                return extractAssert("large_data/" + Test01ExchangeManager.TEST_IMAGE_JPG,
                                     file.getAbsolutePath());
            } catch (IOException e) {
                throw new ExchangeDataProviderException(e);
            }
        }
        return null;
    }

    @Override
    public boolean writeLargeData(String appId, String deviceId, String id, String ref,
            String fPath, String addonParams) throws ExchangeDataProviderException {
        boolean result = false;
        if (Test01ExchangeManager.TEST_WRITE_LARGE_DATA_ID.equals(id)) {
            File file = new File(fPath);
            //emule, not send file
            return file.exists();
        }
        return result;
    }

    @Override
    public String getShortData(String appId, String deviceId, String id, String addonParams)
            throws ExchangeDataProviderException {
        if (Test01ExchangeManager.TEST_GET_SHORT_DATA_ID.equals(id)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Test01ExchangeManager.GET_KEY_INT,
                               Test01ExchangeManager.GET_TEST_INT_VALUE);
                jsonObject.put(Test01ExchangeManager.GET_KEY_STRING,
                               Test01ExchangeManager.GET_TEST_STRING_VALUE);
                jsonObject.put(Test01ExchangeManager.GET_KEY_DATA, DateHelper.date(2014, 0, 1));
                jsonObject
                        .put(Test01ExchangeManager.GET_KEY_STRUCT, "custom struct data as string");

                return jsonObject.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    public boolean writeShortData(String appId, String deviceId, String id, String strJson,
            String addonParams) throws ExchangeDataProviderException {

        boolean result = false;

        if (Test01ExchangeManager.TEST_WRITE_SHORT_DATA_ID.equals(id)) {
            try {

                JSONObject jsonObject = new JSONObject(strJson);
                boolean bValue = jsonObject.getBoolean(Test01ExchangeManager.WRITE_KEY_BOOL);
                double lat = jsonObject.getDouble(Test01ExchangeManager.WRITE_KEY_LAT);
                double lng = jsonObject.getDouble(Test01ExchangeManager.WRITE_KEY_LNG);
                JSONArray jArr = jsonObject.getJSONArray(Test01ExchangeManager.WRITE_KEY_ARRAY);
                int iValue = jsonObject.getInt(Test01ExchangeManager.WRITE_KEY_INT);

                StringBuilder sb = new StringBuilder();
                sb.append(Test01ExchangeManager.WRITE_KEY_BOOL).append("=").append(bValue)
                  .append(",");
                sb.append(Test01ExchangeManager.WRITE_KEY_LAT).append("=").append(lat).append(",");
                sb.append(Test01ExchangeManager.WRITE_KEY_LNG).append("=").append(lng).append(",");
                sb.append(Test01ExchangeManager.WRITE_KEY_ARRAY).append("=").append(jArr.toString())
                  .append(",");
                sb.append(Test01ExchangeManager.WRITE_KEY_INT).append("=").append(iValue)
                  .append(",");

                AndroidTestCase.log(TAG, "writeShortData: " + sb.toString());
                if (bValue == Test01ExchangeManager.WRITE_TEST_BOOL_VALUE &&
                    lat == Test01ExchangeManager.WRITE_TEST_LAT_VALUE &&
                    lng == Test01ExchangeManager.WRITE_TEST_LNG_VALUE &&
                    jArr.getString(0).equals(Test01ExchangeManager.WRITE_TEST_ARRAY_VALUE_1) &&
                    jArr.getString(1).equals(Test01ExchangeManager.WRITE_TEST_ARRAY_VALUE_2) &&
                    iValue == Test01ExchangeManager.WRITE_TEST_INT_VALUE) {
                    result = true;
                }

            } catch (JSONException e) {
                throw new ExchangeDataProviderException(e);
            }
        }
        return result;
    }
}
