package ru.profi1c.engine.app;

import ru.profi1c.engine.exchange.BaseExchangeSettings;
import ru.profi1c.engine.meta.MetadataHelper;

public interface IFbaSettingsProvider {
    /**
     * Настройки обмена web-сервисом 1С
     */
    BaseExchangeSettings getExchangeSettings();

    /**
     * Настройки приложения
     */
    BaseAppSettings getAppSettings();

    /**
     * Помощник для работы с метаданными объектов
     */
    MetadataHelper getMetadataHelper();
}
