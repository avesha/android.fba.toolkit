package ru.profi1c.samples.sensus.exchange;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.profi1c.engine.app.IFbaAppInfoProvider;
import ru.profi1c.engine.exchange.ExchangeDataProviderException;
import ru.profi1c.engine.exchange.IExchangeDataProvider;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.util.IOHelper;
import ru.profi1c.samples.sensus.App;
import ru.profi1c.samples.sensus.Dbg;
import ru.profi1c.samples.sensus.R;

class MockDataProvider implements IExchangeDataProvider {

    private static final String EXTRA_FOLDER_NAME = "mock_data";

    private final ExchangeSettings mExchangeSettings;
    private final List<Class> mAllDbClasses;

    MockDataProvider(ExchangeSettings settings) {
        mExchangeSettings = settings;
        MetadataHelper metadataHelper = getApp().getMetadataHelper();
        mAllDbClasses = metadataHelper.getAllObjectClasses();
    }

    private Context getContext() {
        return mExchangeSettings.getContext();
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
                return assertName + ".zip";
            }
        }
        return null;
    }

    private File extractAssert(String assetName, String fPath) throws IOException {
        final Context context = getContext();
        File file = new File(fPath);
        if (!file.exists()) {
            File fZip = new File(file.getParentFile(), assetName);
            boolean extract =
                    IOHelper.saveAssetsData(context, EXTRA_FOLDER_NAME + "/" + assetName, fZip);
            if (extract) {
                File unZip = IOHelper.unZipFirst(fZip);
                if (!unZip.renameTo(file)) {
                    file = null;
                }
            } else {
                file = null;
            }
        }
        return file;
    }

    @Override
    public int getConnectionTimeout() {
        return 0;
    }

    @Override
    public void setConnectionTimeout(int ms) {

    }

    @Override
    public boolean login(String appId, String deviceId, String userName, String password)
            throws ExchangeDataProviderException {

        final Context context = getContext();
        if (TextUtils.isEmpty(userName)) {
            throw new ExchangeDataProviderException(context.getString(
                   R.string.fba_msg_err_user_name_device_not_specified));
        }

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
                return extractAssert(assertName, fPath);
            } catch (IOException e) {
                throw new ExchangeDataProviderException(e);
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
        return false;
    }

    @Override
    public File getLargeData(String appId, String deviceId, String id, String ref,
            String addonParams, String fPath) throws ExchangeDataProviderException {
        return null;
    }

    @Override
    public boolean writeLargeData(String appId, String deviceId, String id, String ref,
            String fPath, String addonParams) throws ExchangeDataProviderException {

        String idTraceLog = getContext().getString(ru.profi1c.engine.R.string.fba_id_trace_log);
        if(id.equals(idTraceLog)) {
            IFbaAppInfoProvider provider = getApp();
            File fDest = new File(provider.getAppSettings().getBackupDir(), String.format("stacktrace_%d.log", System.currentTimeMillis()));
            try {
                IOHelper.copyFile(new File(fPath), fDest);
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            }
        }
        return false;
    }

    @Override
    public String getShortData(String appId, String deviceId, String id, String addonParams)
            throws ExchangeDataProviderException {
        return null;
    }

    @Override
    public boolean writeShortData(String appId, String deviceId, String id, String strJson,
            String addonParams) throws ExchangeDataProviderException {
        return false;
    }
}
