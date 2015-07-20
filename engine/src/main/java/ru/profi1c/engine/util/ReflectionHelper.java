package ru.profi1c.engine.util;

import android.content.res.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.profi1c.engine.Dbg;

/**
 * A helper class for using Java Reflection
 */
public final class ReflectionHelper {

    private static final String CLASS_NAME_ANDROID_STYLABLE = "android.R$styleable";
    private static final String CLASS_NAME_ANDROID_ATTR = "android.R$attr";
    private static final HashMap<String, Integer> CASHE_ANDROID_ATTRS;

    static {
        CASHE_ANDROID_ATTRS = new HashMap<String, Integer>();
    }

    public static int NOT_FOUND = -1;

    private static void printStackTrace(Exception e) {
        Dbg.printStackTrace(e);
    }

    public static Object getFieldValue(final Object obj, final String fieldName) {

        Object value = null;

        Field f = getField(obj, fieldName);
        if (f != null) {
            try {
                value = f.get(obj);
            } catch (IllegalArgumentException e) {
                printStackTrace(e);
            } catch (IllegalAccessException e) {
                printStackTrace(e);
            }
        }

        return value;

    }

    public static Object getStaticFieldValue(final String className, final String fieldName) {

        Object value = null;

        Field f = getClassField(className, fieldName);
        if (f != null) {
            try {
                value = f.get(null);
            } catch (IllegalArgumentException e) {
                printStackTrace(e);
            } catch (IllegalAccessException e) {
                printStackTrace(e);
            }
        }

        return value;

    }

    public static boolean setFieldValue(final Object obj, final String fieldName,
            final Object fieldValue) {

        boolean isSet = false;

        Field f = getField(obj, fieldName);
        if (f != null) {
            try {
                f.set(obj, fieldValue);
                isSet = true;
            } catch (IllegalArgumentException e) {
                printStackTrace(e);
            } catch (IllegalAccessException e) {
                printStackTrace(e);
            }
        }

        return isSet;
    }

    public static boolean setStaticFieldValue(final String className, final String fieldName,
            final Object fieldValue) {

        boolean isSet = false;

        Field f = getClassField(className, fieldName);
        if (f != null) {
            try {
                f.set(null, fieldValue);
                isSet = true;
            } catch (IllegalArgumentException e) {
                printStackTrace(e);
            } catch (IllegalAccessException e) {
                printStackTrace(e);
            }
        }

        return isSet;
    }

    public static void callMethod(final Object obj, final String methodName) {
        callMethod(obj, methodName, null);
    }

    public static void callStaticMethod(final String className, final String methodName) {
        callStaticMethod(className, methodName, null);
    }

    @SuppressWarnings("rawtypes")
    public static Object callMethod(final Object obj, final String methodName,
            Class[] parameterTypes, Object... args) {

        Object returnValue = null;

        Method method = getMethod(obj, methodName, parameterTypes);
        if (method != null) {
            try {
                returnValue = method.invoke(obj, args);
            } catch (IllegalArgumentException e) {
                printStackTrace(e);
            } catch (IllegalAccessException e) {
                printStackTrace(e);
            } catch (InvocationTargetException e) {
                printStackTrace(e);
            }
        }

        return returnValue;
    }

    @SuppressWarnings("rawtypes")
    public static Object callStaticMethod(final String className, String methodName,
            Class[] parameterTypes, Object... args) {

        Object returnValue = null;

        Method method = getClassMethod(className, methodName, parameterTypes);
        if (method != null) {
            try {
                returnValue = method.invoke(null, args);
            } catch (IllegalArgumentException e) {
                printStackTrace(e);
            } catch (IllegalAccessException e) {
                printStackTrace(e);
            } catch (InvocationTargetException e) {
                printStackTrace(e);
            }
        }

        return returnValue;

    }

    /**
     * Swap field values (fiends only this class, not super)
     *
     * @param src
     * @param dest
     */
    @SuppressWarnings("rawtypes")
    public static void swapField(final Object src, final Object dest) {

        if (!src.getClass().equals(dest.getClass())) {
            throw new IllegalArgumentException(
                    "Objects 'src' and 'dest' must belong to the same class");
        }

        try {
            Class clazz = Class.forName(src.getClass().getName());
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                Object srcValue = f.get(src);
                Object destValue = f.get(dest);

                f.set(src, destValue);
                f.set(dest, srcValue);
            }
        } catch (ClassNotFoundException e) {
            printStackTrace(e);
        } catch (IllegalArgumentException e) {
            printStackTrace(e);
        } catch (IllegalAccessException e) {
            printStackTrace(e);
        }

    }

    /**
     * Copying fields (all, include super) of classes of one object to another. Match by name and type fields
     */
    @SuppressWarnings("rawtypes")
    public static void copyFields(final Object src, final Object dest) {

        try {
            Class clazz = Class.forName(src.getClass().getName());
            Collection<Field> fields = getFields(clazz);

            Class clazzDest = Class.forName(dest.getClass().getName());
            Collection<Field> fieldsDest = getFields(clazzDest);

            for (Field f : fields) {
                //not copy static files
                int modifiers = f.getModifiers();
                if (java.lang.reflect.Modifier.isStatic(modifiers) ||
                    java.lang.reflect.Modifier.isTransient(modifiers) ||
                    !isExistField(fieldsDest, f)) {
                    continue;
                }

                f.setAccessible(true);
                Object srcValue = f.get(src);
                f.set(dest, srcValue);
            }
        } catch (ClassNotFoundException e) {
            printStackTrace(e);
        } catch (IllegalArgumentException e) {
            printStackTrace(e);
        } catch (IllegalAccessException e) {
            printStackTrace(e);
        }

    }

    private static boolean isExistField(Collection<Field> fields, Field field) {
        boolean exists = false;
        String name = field.getName();
        Class<?> type = field.getType();
        for (Field f : fields) {
            if (f.getName().equals(name) && f.getType().equals(type)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    /**
     * Get all fields of a class.
     *
     * @param clazz The class.
     * @return All fields of a class.
     */
    public static Collection<Field> getFields(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<String, Field>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!fields.containsKey(field.getName())) {
                    field.setAccessible(true); // Additional line
                    fields.put(field.getName(), field);
                }
            }

            clazz = clazz.getSuperclass();
        }

        Collection<Field> returnCollection = fields.values();
        return returnCollection;
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

        for (Field f : fields) {
            if ((ignoreCase && f.getName().equalsIgnoreCase(name)) ||
                (!ignoreCase && f.getName().equals(name))) {
                return f;
            }
        }
        return null;
    }

    private static int[] getInternalResources(final String className, final String name) {
        Object filedValue = getStaticFieldValue(className, name);
        if (filedValue != null) {
            return (int[]) filedValue;
        }
        return new int[0];
    }

    public static int[] getInternalStyles(final String name) {
       return getInternalResources(CLASS_NAME_ANDROID_STYLABLE, name);
    }

    public static int[] getInternalAttributes(final String name) {
        return getInternalResources(CLASS_NAME_ANDROID_ATTR, name);
    }

    public static int getInternalResource(final String className, final String name) {

        int res = NOT_FOUND;
        final String key = className + name;

        if (CASHE_ANDROID_ATTRS.containsKey(key)) {
            res = CASHE_ANDROID_ATTRS.get(key);
        } else {
            Object filedValue = getStaticFieldValue(className, name);
            if (filedValue != null) {
                res = (Integer) filedValue;
            }
            CASHE_ANDROID_ATTRS.put(key, res);
        }
        return res;
    }

    public static int getInternalStyle(final String name) {
        return getInternalResource(CLASS_NAME_ANDROID_STYLABLE, name);
    }

    public static int getInternalAttribute(final String name) {
        return getInternalResource(CLASS_NAME_ANDROID_ATTR, name);
    }

    public static String getInternalString(final String name) {
        int id = Resources.getSystem().getIdentifier(name, "string", "android");
        return Resources.getSystem().getString(id);
    }


    private static Field getField(final Object obj, final String fieldName) {
        return getClassField(obj.getClass().getName(), fieldName);
    }

    @SuppressWarnings("rawtypes")
    private static Method getMethod(final Object obj, final String methodName,
            Class... parameterTypes) {
        return getClassMethod(obj.getClass().getName(), methodName, parameterTypes);
    }

    @SuppressWarnings("rawtypes")
    private static Field getClassField(final String className, final String fieldName) {

        Field field = null;
        try {
            Class clazz = Class.forName(className);
            Collection<Field> fields = getFields(clazz);
            field = findField(fields, fieldName, true);
            if (field != null) {
                field.setAccessible(true);
            }
        } catch (ClassNotFoundException e) {
            printStackTrace(e);
        }
        return field;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Method getClassMethod(final String className, final String methodName,
            Class... parameterTypes) {

        Method method = null;

        try {
            Class clazz = Class.forName(className);
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
            if (method != null) {
                method.setAccessible(true);
            }
        } catch (ClassNotFoundException e) {
            printStackTrace(e);
        } catch (NoSuchMethodException e) {
            printStackTrace(e);
        }

        return method;
    }

}
