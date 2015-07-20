package com.sample.map.mapsforge;

import ru.profi1c.engine.app.BaseAppSettings;
import ru.profi1c.engine.app.FbaActivity;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.app.FbaPreferenceActivity;
import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.MetadataHelper;

public class App extends FbaApplication {

    @Override
    public Class<? extends FbaActivity> getMainActivityClass() {
        return TAMapsforgeRouteMap.class;
    }

    @Override
    public Class<? extends FbaPreferenceActivity> getPreferenceActivityClass() {
        return null;
    }

    @Override
    public Class<? extends FbaActivity> getLoginActivityClass() {
        return null;
    }

    @Override
    public Class<? extends DBOpenHelper> getDBHelperClass() {
        return DBHelper.class;
    }

    @Override
    public BaseExchangeSettings getExchangeSettings() {
        return ExchangeSettings.getInstance(this);
    }

    @Override
    public BaseAppSettings getAppSettings() {
        return AppSettings.getInstance(this);
    }

    @Override
    public MetadataHelper getMetadataHelper() {
        return null;
    }
}
