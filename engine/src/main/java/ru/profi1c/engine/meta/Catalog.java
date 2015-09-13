package ru.profi1c.engine.meta;

import com.j256.ormlite.field.DatabaseField;

import java.util.UUID;

import ru.profi1c.engine.Const;

/**
 * Базовый класс "Справочник"
 */
public abstract class Catalog extends Ref implements IPresentation, Comparable<Catalog> {
    private static final long serialVersionUID = 8621365531891040331L;

    /**
     * Имя поля для ссылки на владельца элемента справочника.
     */
    public static final String FIELD_NAME_OWNER = "owner";

    public static final String FIELD_NAME_CODE = "code";
    public static final String FIELD_NAME_DESCRIPTION = "description";
    public static final String FIELD_NAME_FOLDER = "folder";
    public static final String FIELD_NAME_PREDEFINED = "predefined";
    public static final String FIELD_NAME_PARENT = "parent";
    public static final String FIELD_NAME_LEVEL = "level";

    /**
     * Содержит код элемента справочника, всегда только строковый
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_CODE)
    @MetadataField(type = MetadataFieldType.STRING, name = Catalog.FIELD_NAME_CODE,
            description = Const.META_DESCRIPTION_CODE)
    protected String code;

    /**
     * Содержит наименование элемента справочника.
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_DESCRIPTION)
    @MetadataField(type = MetadataFieldType.STRING, name = Catalog.FIELD_NAME_DESCRIPTION,
            description = Const.META_DESCRIPTION_NAME)
    protected String description;

    /**
     * Позволяет определить является ли элемент справочника группой. Только для
     * чтения, не изменяется для существующих групп, в мобильном клиенте
     * допустимо только создание новых
     *
     * @return
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_FOLDER)
    @MetadataField(type = MetadataFieldType.BOOL, name = Catalog.FIELD_NAME_FOLDER,
            description = Const.META_DESCRIPTION_FOLDER)
    private boolean folder;

    /**
     * Указывает, что данный элемент справочника является предопределенным
     * элементом.
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_PREDEFINED)
    @MetadataField(type = MetadataFieldType.BOOL, name = Catalog.FIELD_NAME_PREDEFINED,
            description = Const.META_DESCRIPTION_PREDEFINED)
    private boolean predefined;

    /**
     * Содержит ссылку на родителя элемента справочника. Имеет смысл только для
     * многоуровневых справочников. Только для чтения, не изменяется в мобильном
     * клиенте
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_PARENT)
    @MetadataField(type = MetadataFieldType.STRING, name = Catalog.FIELD_NAME_PARENT,
            description = Const.META_DESCRIPTION_PARENT)
    private String parent;

    /**
     * Уровень элемента справочника.Только для чтения, не изменяется в мобильном
     * клиенте
     */
    @DatabaseField(columnName = Catalog.FIELD_NAME_LEVEL)
    @MetadataField(type = MetadataFieldType.INT, name = Catalog.FIELD_NAME_LEVEL,
            description = Const.META_DESCRIPTION_LEVEL)
    private int level;

    /**
     * Возвращает ссылку на владельца элемента справочника. Поле класса должно
     * быть определено в классе наследнике( с именем поля таблицы
     * 'FIELD_NAME_OWNER'). Имеет смысл только для подчиненных справочников.
     *
     * @return
     */
    public abstract Catalog getOwner();

    /**
     * Установить ссылку на владельца элемента справочника. Имеет смысл только
     * для подчиненных справочников.
     *
     * @param catalogRef
     */
    public abstract void setOwner(Catalog catalogRef);

    /**
     * Получить код элемента справочника
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * Получить наименование элемента справочника
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Позволяет определить является ли элемент справочника группой.
     *
     * @return истина, если этот элемент справочника является группой
     */
    public boolean isFolder() {
        return folder;
    }

    /**
     * Указывает, что данный элемент справочника является предопределенным
     *
     * @return истина, если это предопределенный элемент справочника
     */
    public boolean isPredefined() {
        return predefined;
    }

    /**
     * Возвращает ссылку на родителя элемента справочника. Имеет смысл только
     * для многоуровневых справочников.
     *
     * @return строковое представление ссылки на родительский элемент
     */
    public UUID getParent() {
        return (parent != null) ? UUID.fromString(parent) : null;
    }

    public void setParent(UUID parent) {
        if (parent != null) {
            this.parent = parent.toString();
        }
    }

    /**
     * Установить новый код элемента справочника
     *
     * @param code код
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Установить наименование элемента справочника
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public void setFolder(boolean folder) {
        if (!isNewItem()) {
            throw new IllegalStateException(
                    "Sign 'IsFolder' can not be changed for the objects from the server!");
        }
        this.folder = folder;
    }

    /**
     * Возвращает уровень элемента справочника. Имеет смысл только для
     * многоуровневых справочников. Следует учитывать, что уровень не может
     * меняться в мобильном клиенте (только в 1с). Для элемента, не имеющего
     * родителя, уровень будет равняться 0.
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * Установить уровень элемента справочника (перенос элемента в другую
     * группу, так же следует изменять родителя). Имеет смысл только для
     * многоуровневых справочников.Для элемента, не имеющего родителя, уровень
     * должен равняться 0.
     *
     * @param level
     */
    @Deprecated
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String getMetaType() {
        return MetadataObject.TYPE_CATALOG;
    }

    /**
     * Получает строковое представление справочника. Рекомендуется
     * переопределять в наследнике (в виде кода или наименования)
     */
    @Override
    public String getPresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMetaType()).append(".").append(getMetaName());
        sb.append(": ").append(code).append(" ").append(description);
        return sb.toString();
    }

    /**
     * Сравнение (сортировка) по представлению, см. {@link #getPresentation()}
     */
    @Override
    public int compareTo(Catalog another) {

        if (another == null) {
            return -1;
        }

        if (!IPresentation.class.isInstance(another)) {
            throw new ClassCastException("It is expected instance of a 'IPresentation'!");
        }

        final String aPres = ((IPresentation) another).getPresentation();
        final String pres = getPresentation();

        return aPres == null ? (pres == null ? 0 : -1) : (pres == null ? 1 : pres.compareTo(aPres));
    }

}
