package ru.profi1c.samples.fba_perfomance.exchange;

import java.io.File;

import ru.profi1c.engine.exchange.ExchangeDataProviderException;
import ru.profi1c.engine.exchange.IExchangeDataProvider;

class MockDataProvider implements IExchangeDataProvider {

    @Override
    public int getConnectionTimeout() {
        return 1000;
    }

    @Override
    public void setConnectionTimeout(int ms) {
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
        return true;
    }

    @Override
    public File getLargeData(String appId, String deviceId, String id, String ref,
            String addonParams, String fPath) throws ExchangeDataProviderException {
        return null;
    }

    @Override
    public boolean writeLargeData(String appId, String deviceId, String id, String ref,
            String fPath, String addonParams) throws ExchangeDataProviderException {
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
