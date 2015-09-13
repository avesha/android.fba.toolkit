package ru.profi1c.engine.meta;

import com.j256.ormlite.field.DatabaseField;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;

/**
 * Базовый класс на все ссылочные объекты
 */
public abstract class Ref extends Row implements IMetadata {
    private static final long serialVersionUID = 7197943528717333264L;

    public static final String EMPTY_REF = "00000000-0000-0000-0000-000000000000";

    /*
     * Если используются отражение таблицы на Cursor, колонка идентификатора
     * должна называться “_id”
     */
    public static final String FIELD_NAME_REF = "_id";

    public static final String FIELD_NAME_DELETIONMARK = "deletionMark";
    public static final String FIELD_NAME_NEW_ITEM = "newItem";
    public static final String FIELD_NAME_TABLE_PART_DATA = "mapTablePartData";

    /**
     * Хранит кеш-данные табличных частей справочника/документа считываемые при
     * json сериализации иначе, если создавать ForeignCollection - то при
     * добавлении в коллекцию сразу происходит запись в базу sql, что не
     * требуется
     */
    private HashMap<String, List<? extends TablePart>> mapTablePartData;
    // имя "mapTablePartData" не менять, используется в парсере как исключаемое

    /**
     * Текстовое представление ссылки, UUID
     */
    @DatabaseField(columnName = Ref.FIELD_NAME_REF, id = true)
    @MetadataField(type = MetadataFieldType.STRING, name = "ref", description = Const.META_DESCRIPTION_REF)
    private String ref;

    /**
     * Пометка удаления
     */
    @DatabaseField(columnName = Ref.FIELD_NAME_DELETIONMARK)
    @MetadataField(type = MetadataFieldType.BOOL, name = Ref.FIELD_NAME_DELETIONMARK,
            description = Const.META_DESCRIPTION_DELETIONMARK)
    protected boolean deletionMark;

    /**
     * Признак того, что это новый элемент (созданный на мобильном клиенте)
     */
    @DatabaseField(columnName = Ref.FIELD_NAME_NEW_ITEM)
    @MetadataField(type = MetadataFieldType.BOOL, name = Ref.FIELD_NAME_NEW_ITEM,
            description = Const.META_DESCRIPTION_NEW_ITEM)
    protected boolean newItem;

    /**
     * @return список классов табличных частей этого объекта или null если
     * табличные части не используются
     */
    public abstract List<Class<? extends TablePart>> getTabularSections();

    public Ref() {
        newItem = false;
    }

    public UUID getRef() {
        return (ref == null) ? null : UUID.fromString(ref);
    }

    public void setRef(UUID ref) {
        this.ref = ref.toString();
    }

    public boolean isDeletionMark() {
        return deletionMark;
    }

    public void setDeletionMark(boolean deletionMark) {
        this.deletionMark = deletionMark;
    }

    public boolean isNewItem() {
        return newItem;
    }

    protected void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Ref other = (Ref) obj;
        if (isEmptyUUID(getRef())) {
            if (other.getRef() != null) {
                return false;
            }
        } else if (!getRef().equals(other.getRef())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (ref == null) ? 0 : ref.hashCode();
    }

    @Override
    public String toString() {
        if (ref != null) {
            return ref.toString();
        }
        return null;
    }

    /**
     * Сравнение по строковому представлению идентификатора (пустые ссылки
     * первыми, null последними)
     */
    public int compareTo(Ref another) {

        if (another == null) {
            return -1;
        }

        if (getClass() != another.getClass()) {
            throw new ClassCastException("It is expected instance of a '" + getClass() + "'!");
        }

        return another.getRef() == null ? (getRef() == null ? 0 : -1) : (
                getRef() == null ? 1 : getRef().toString().compareTo(another.getRef().toString()));
    }

    /**
     * Сохранить табличную часть этого справочника в кеш-данные. Внимание! Этот
     * метод не сохраняет даные в локальную базу, используется при
     * Json-сериализации
     *
     * @param tpFieldName  имя поля класса для табличной части справочника
     *                     (ForeignCollection)
     * @param lstTablePart данные табличной части
     */
    public void setTmpTablePartData(String tpFieldName, List<? extends TablePart> lstTablePart) {
        if (mapTablePartData == null) {
            mapTablePartData = new HashMap<String, List<? extends TablePart>>();
        }
        mapTablePartData.put(tpFieldName, lstTablePart);
    }

    /**
     * Получить кеш-данные табличной части этого справочника
     *
     * @param tpFieldName
     * @return
     */
    public List<? extends TablePart> getTmpTablePartData(String tpFieldName) {
        if (mapTablePartData != null) {
            return mapTablePartData.get(tpFieldName);
        }
        return null;
    }

    /**
     * Очистить кеш-данные табличных частей
     */
    public void clearTmpTablePartData() {
        if (mapTablePartData != null) {
            mapTablePartData.clear();
            mapTablePartData = null;
        }
    }

    /**
     * Возвращает кеш-данные табличных частей этого объекта, используется при
     * Json-сериализации
     *
     * @return
     */
    public HashMap<String, List<? extends TablePart>> getTmpTablePartData() {
        return mapTablePartData;
    }

    /**
     * Создать пустую ссылку указанного класса
     *
     * @param classRef
     * @return
     */
    public static Ref emptyRef(Class<? extends Ref> classRef) {
        try {
            Ref ref = classRef.newInstance();
            ref.setRef(emptyUUID());
            return ref;
        } catch (InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return null;
    }

    /**
     * Создать пустой идентификатор когда класс ссылки не важен
     *
     * @return
     */
    public static UUID emptyUUID() {
        return UUID.fromString(Ref.EMPTY_REF);
    }

    /**
     * Проверка ссылки на пустое значение
     */
    public static boolean isEmptyUUID(UUID ref) {
        return ref == null || EMPTY_REF.equals(ref.toString());
    }

    /**
     * Проверка ссылки на пустое значение
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Ref obj) {
        return obj == null || isEmptyUUID(obj.getRef());
    }

}
