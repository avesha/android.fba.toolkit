package ru.profi1c.engine.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотации для соответствия файла обмена и полей классов
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MetadataField {

    /**
     * Имя поля
     *
     * @return
     */
    String name() default "";

    /**
     * Описание поля (пользовательское представление)
     *
     * @return
     */
    String description() default "";

    /**
     * Тип поля
     *
     * @return
     */
    MetadataFieldType type() default MetadataFieldType.UNKNOWN;

}
