package ru.profi1c.engine.exchange;

public interface IExchangeDataProviderFactory {
    IExchangeDataProvider create(BaseExchangeSettings settings);
}
