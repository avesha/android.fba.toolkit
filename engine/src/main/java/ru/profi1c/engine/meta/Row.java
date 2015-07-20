package ru.profi1c.engine.meta;

import android.os.Message;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Observable;

import ru.profi1c.engine.Const;

/**
 * Строка таблицы, базовый класс всех сохраняемых в локальной базе данных
 * объектов (справочники, документы, регистры сведений, внешние таблицы,
 * табличные части справочников и документов)
 */
public abstract class Row extends Observable implements Serializable {
    private static final long serialVersionUID = 7081532438847866702L;

    /**
     * Идентификатор сообщения для уведомления подписчиков, см.
     * {@link #makeObserverMessage(int, String)}
     */
    public static final int ID_OBSERVER_NOTIFY = 750804;

    public static final String FIELD_NAME_MODIFIED = "Modified";

    /**
     * Признак изменения строки
     */
    @DatabaseField(columnName = Row.FIELD_NAME_MODIFIED)
    @MetadataField(type = MetadataFieldType.BOOL, name = Row.FIELD_NAME_MODIFIED,
            description = Const.META_DESCRIPTION_MODIFIED)
    protected boolean modified;

    /**
     * Возвращает признак изменения записи (объекта метаданных)
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Установить флаг изменения записи (объекта метаданных)
     *
     * @param modified признак изменения
     */
    public void setModified(boolean modified) {
        this.modified = modified;
        if (modified) {
            setChanged();
        } else {
            clearChanged();
        }
    }

    /**
     * Создать сообщение для уведомления наблюдателей за изменениями объекта
     *
     * @param idSenderObserver Идентификатор инициатора обновления
     * @param fieldName        имя поля измененного инициатором, если не указано, считается
     *                         что обновляется объект целиком
     * @return Сообщение, которое отправляет инициатор вторым параметром в
     * методе notifyObservers и которое обрабатывается подписчиками
     */
    public static Message makeObserverMessage(int idSenderObserver, String fieldName) {
        Message msg = new Message();
        msg.what = ID_OBSERVER_NOTIFY;
        msg.arg1 = idSenderObserver;
        msg.obj = fieldName;
        return msg;
    }
}
