package ru.profi1c.samples.sensus.exchange;

import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.exchange.IExchangeDataProvider;
import ru.profi1c.engine.exchange.IExchangeDataProviderFactory;

public class MockDataProviderFactory implements IExchangeDataProviderFactory {
    @Override
    public IExchangeDataProvider create(BaseExchangeSettings settings) {
        return new MockDataProvider((ExchangeSettings) settings);
    }
}
