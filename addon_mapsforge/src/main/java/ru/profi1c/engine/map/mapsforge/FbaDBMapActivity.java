package ru.profi1c.engine.map.mapsforge;

import com.j256.ormlite.support.ConnectionSource;

import ru.profi1c.engine.app.FbaDBActivity;

/**
 * Базовый класс для использования
 * <code>mapsforge.android.maps.MapActivity</code> c доступом к базе данных. Вы
 * можете просто вызвать {@link #getHelper()}, чтобы получить ваш класс
 * помощника, или {@link #getConnectionSource()}, чтобы получить
 * {@link ConnectionSource}.
 */
public abstract class FbaDBMapActivity extends FbaDBActivity {

}
