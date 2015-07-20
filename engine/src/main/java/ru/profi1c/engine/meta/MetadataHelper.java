package ru.profi1c.engine.meta;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.util.ReflectionHelper;

/**
 * Помощник для работы с метаданными объектов (определенных через аннотации)
 */
public abstract class MetadataHelper {

    /**
     * Класс в котором реализована работа с константами 1С
     *
     * @return
     */
    public abstract Class<? extends ConstTable> getConstClass();

    /**
     * Список всех классов, в которых реализована работа объектами 1С типа
     * ‘Справочник’
     *
     * @return
     */
    public abstract List<Class<? extends Catalog>> getCatalogClasses();

    /**
     * Список всех классов, в которых реализована работа объектами 1С типа
     * ‘Документ’
     *
     * @return
     */
    public abstract List<Class<? extends Document>> getDocumentClasses();

    /**
     * Список всех классов, в которых реализована работа объектами 1С типа
     * ‘Регистр сведений’
     *
     * @return
     */
    public abstract List<Class<? extends TableInfReg>> getRegClasses();

    /**
     * Список всех классов, в которых реализована работа с произвольными
     * коллекциями данных (внешние таблицы) не имеющие объектного соответствия в
     * 1С
     *
     * @return
     */
    public abstract List<Class<? extends Table>> getExtTableClasses();

    /**
     * Возвращает список всех классов базы данных (константы, справочники,
     * документы их табличные части, регистры сведений и не объектные таблицы)
     *
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings("rawtypes")
    public List<Class> getAllDBClasses() throws InstantiationException, IllegalAccessException {
        List<Class> list = new ArrayList<Class>();

        if (getConstClass() != null) {
            list.add(getConstClass());
        }

        if (getCatalogClasses() != null) {
            for (Class<? extends Catalog> clazz : getCatalogClasses()) {

                list.add(clazz);

                // TODO: метаданные, перенести здесь табличные части в метаданные, а не получать от объекта?
                Catalog cat = clazz.newInstance();
                List<Class<? extends TablePart>> lstTablePart = cat.getTabularSections();
                if (lstTablePart != null) {
                    list.addAll(lstTablePart);
                }

            }
        }

        if (getDocumentClasses() != null) {
            for (Class<? extends Document> clazz : getDocumentClasses()) {

                list.add(clazz);

                Document doc = clazz.newInstance();
                List<Class<? extends TablePart>> lstTablePart = doc.getTabularSections();
                if (lstTablePart != null) {
                    list.addAll(lstTablePart);
                }

            }
        }

        if (getRegClasses() != null) {
            list.addAll(getRegClasses());
        }

        if (getExtTableClasses() != null) {
            list.addAll(getExtTableClasses());
        }

        return list;
    }

    /**
     * Возвращает список всех объектных классов верхнего уровня (без табличных
     * частей)
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<Class> getAllObjectClasses() {

        List<Class> list = new ArrayList<Class>();

        if (getConstClass() != null) {
            list.add(getConstClass());
        }

        if (getCatalogClasses() != null) {
            list.addAll(getCatalogClasses());
        }

        if (getDocumentClasses() != null) {
            list.addAll(getDocumentClasses());
        }

        if (getRegClasses() != null) {
            list.addAll(getRegClasses());
        }

        if (getExtTableClasses() != null) {
            list.addAll(getExtTableClasses());
        }

        return list;
    }

    /**
     * Извлечь тип метаданных назначенный для данного класса через аннотацию
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static String getMetadataType(Class clazz) {
        String type = null;

        if (clazz.isAnnotationPresent(MetadataObject.class)) {
            MetadataObject mo = ((MetadataObject) clazz.getAnnotation(MetadataObject.class));
            type = mo.type();
        }

        return type;
    }

    /**
     * Извлечь имя объекта метаданных назначенное для данного класса через
     * аннотацию
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static String getMetadataName(Class clazz) {
        String name = null;

        if (clazz.isAnnotationPresent(MetadataObject.class)) {
            MetadataObject mo = ((MetadataObject) clazz.getAnnotation(MetadataObject.class));
            name = mo.name();
        }
        return name;
    }

    /**
     * Извлечь имя поля метаданных назначенное через аннотацию
     *
     * @param field
     * @return
     */
    public static String getMetadataFieldName(Field field) {
        if (field.isAnnotationPresent(MetadataField.class)) {
            return ((MetadataField) field.getAnnotation(MetadataField.class)).name();
        }
        return null;
    }

    /**
     * Извлечь пользовательское представление поля метаданных назначенное через
     * аннотацию
     *
     * @param field
     * @return
     */
    public static String getMetadataFieldDescription(Field field) {
        String desc = null;
        if (field.isAnnotationPresent(MetadataField.class)) {
            final String annotation = ((MetadataField) field.getAnnotation(
                    MetadataField.class)).description();
            if (!TextUtils.isEmpty(annotation)) {
                if (annotation.startsWith(Const.META_PREFIX)) {
                    String resName = annotation.replace(Const.META_PREFIX, "");
                    final Context context = FbaApplication.getContext();
                    int resId = context.getResources()
                            .getIdentifier(resName, "string", context.getPackageName());
                    if (resId != 0) {
                        desc = context.getString(resId);
                    }
                } else {
                    desc = annotation;
                }
            }
        }
        return desc;
    }

    /**
     * Извлечь тип поля назначенный через аннотацию
     *
     * @param field
     * @return
     */
    public static MetadataFieldType getMetadataFieldType(Field field) {
        if (field.isAnnotationPresent(MetadataField.class)) {
            return ((MetadataField) field.getAnnotation(MetadataField.class)).type();
        }
        return null;
    }

    /**
     * Возвращает имя колонки в таблице базы данных для этого поля
     *
     * @param field
     * @return
     */
    public static String getDBColumnName(Field field) {
        if (field.isAnnotationPresent(DatabaseField.class)) {
            return field.getAnnotation(DatabaseField.class).columnName();
        }
        return null;
    }

    /**
     * Возвращает имя таблицы базы данных для этого класса
     *
     * @param clazz
     * @return
     */
    public static String getDatabaseTableName(Class clazz) {
        if (clazz.isAnnotationPresent(DatabaseTable.class)) {
            return ((DatabaseTable) clazz.getAnnotation(DatabaseTable.class)).tableName();
        }
        return null;
    }

    /**
     * Возвращает истина, если этот класс принадлежит к подчиненным справочникам
     *
     * @param clazz
     * @return
     */
    public static boolean isSlaveCatalog(Class<?> clazz) {

        boolean isSlave = false;

        String type = getMetadataType(clazz);
        if (MetadataObject.TYPE_CATALOG.equals(type)) {

            // если есть аннотация @MetadataField(name=Catalog.FIELD_NAME_OWNER)
            Collection<Field> lst = getFields(clazz);
            for (Field f : lst) {
                if (f.isAnnotationPresent(MetadataField.class) &&
                    Catalog.FIELD_NAME_OWNER.equals(f.getAnnotation(MetadataField.class).name())) {
                    isSlave = true;
                    break;
                }
            }
        }
        return isSlave;
    }

    /**
     * Get all fields of a class.
     *
     * @param clazz The class.
     * @return All fields of a class.
     */
    public static Collection<Field> getFields(Class<?> clazz) {
        return ReflectionHelper.getFields(clazz);
    }

    /**
     * Найти поле класса в коллекции
     *
     * @param fields     коллекция полей класса
     * @param name       имя поля
     * @param ignoreCase если true,то поиск будет произведен без учета регистра
     *                   символов
     * @return
     */
    public static Field findField(Collection<Field> fields, String name, boolean ignoreCase) {
        return ReflectionHelper.findField(fields, name, ignoreCase);
    }

    /**
     * Возвращает истина если класс типа 'Булево'
     */
    public static boolean isBooleanClass(Class<?> classOfF) {
        return (classOfF.equals(Boolean.class) || classOfF.equals(boolean.class));
    }

    /**
     * Возвращает истина если класс типа 'Short'
     */
    public static boolean isShortClass(Class<?> classOfF) {
        return (classOfF.equals(Short.class) || classOfF.equals(short.class));
    }

    /**
     * Возвращает истина если класс типа 'Integer'
     */
    public static boolean isIntegerClass(Class<?> classOfF) {
        return (classOfF.equals(Integer.class) || classOfF.equals(int.class));
    }

    /**
     * Возвращает истина если класс типа 'BigInteger'
     */
    public static boolean isBigIntegerClass(Class<?> classOfF) {
        return (classOfF.equals(BigInteger.class));
    }

    /**
     * Возвращает истина если класс типа 'Long'
     */
    public static boolean isLongClass(Class<?> classOfF) {
        return (classOfF.equals(Long.class) || classOfF.equals(long.class));
    }

    /**
     * Возвращает истина если класс типа 'Float'
     *
     * @param classOfF
     * @return
     */
    public static boolean isFloatClass(Class<?> classOfF) {
        return (classOfF.equals(Float.class) || classOfF.equals(float.class));
    }

    /**
     * Возвращает истина если класс типа 'Double'
     *
     * @param classOfF
     * @return
     */
    public static boolean isDoubleClass(Class<?> classOfF) {
        return (classOfF.equals(Double.class) || classOfF.equals(double.class));
    }

    /**
     * Возвращает истина если класс типа строковый
     */
    public static boolean isDataClass(Class<?> classOfF) {
        return (classOfF.equals(Date.class));
    }

    /**
     * Возвращает истина если класс типа строковый
     */
    public static boolean isStringClass(Class<?> classOfF) {
        return (classOfF.equals(String.class) || classOfF.equals(char.class));
    }

    /**
     * Возвращает истина если класса типа 'Хранилище значения'
     */
    public static boolean isBlobClass(Class<?> classOfF) {
        return (classOfF.equals(ValueStorage.class) || classOfF.equals(byte[].class));
    }

    /**
     * Возвращает истина если класс является наследником от classSuper
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean extendsOfClass(Class clazz, Class classSuper) {
        boolean isExtend = false;

        Class parent = clazz;
        while (!isExtend && parent != null) {

            if (parent.equals(classSuper)) {
                isExtend = true;
                break;
            }
            parent = parent.getSuperclass();

        }
        return isExtend;
    }

    /**
     * Возвращает супер класс генерика
     *
     * @param generic класс - генерик
     * @return
     */
    public static Class<?> getGenericSuperclass(Class<?> generic) {
        Type genericSuperclass = generic.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return null;
    }
}
