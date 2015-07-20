package ru.profi1c.engine.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MetadataObject {

    public static final String TYPE_CONSTANT = "Константы";
    public static final String TYPE_CATALOG = "Справочник";
    public static final String TYPE_DOCUMENT = "Документ";
    public static final String TYPE_ENUM = "Перечисление";
    public static final String TYPE_INFORMATION_REGISTER = "РегистрСведений";
    public static final String TYPE_EXTERNAL_TABLE = "ВнешняяТаблица";
    public static final String TYPE_TABLE_PART = "ТабличнаяЧасть";

    public static final String DEFAULT_STRING = "";

    /**
     * Тип метаданных:
     * Константы,Справочник,Документ,Перечисление,РегистрСведений,ВнешняяТаблица
     *
     * @return
     */
    String type() default DEFAULT_STRING;

    /**
     * Имя объекта метаданных, например 'Номенклатура', 'ЗаказПокупателя' (для
     * таблицы констант не имеет смысла
     *
     * @return
     */
    String name() default DEFAULT_STRING;

    /**
     * Если это свойство установлено в значение Истина, то справочник имеет
     * иерархическую структуру. Актуально только для справочников
     *
     * @return
     */
    boolean hierarchical() default false;

}
