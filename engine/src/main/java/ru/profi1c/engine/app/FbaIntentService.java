package ru.profi1c.engine.app;

import android.app.IntentService;

/**
 * Базовый класс для служб работающих в отдельном потоке (в очереди) при
 * использовании библиотеки 'FBA'.
 */
public abstract class FbaIntentService extends IntentService {

    public FbaIntentService(String name) {
        super(name);
    }

}
