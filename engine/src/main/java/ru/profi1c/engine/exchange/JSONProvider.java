package ru.profi1c.engine.exchange;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import org.kobjects.base64.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.Table;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.meta.ValueStorage;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.util.IOHelper;

public class JSONProvider {

    //TODO: Добавить установку признака «исключаемое из обмена поле» через аннотацию (см. ниже от Gson @Expose)
    private static final String FILED_NAME_OBSERVERS = "observers";
    private static final String FILED_NAME_CHANGED = "changed";

    private static final String UTF_8 = "UTF-8";
    private static final String DATE_SERIALIZE_FORMAT = DateHelper.DATE_FORMAT_ISO8601;

    /*
     * Адаптеры для типов данных
    */
    private HashMap<String, TypeAdapter<?>> mTypeAdapters;

    /*
     * Кеш полей классов
     */
    private HashMap<Class<?>, Collection<Field>> mMapClassFields;

    /*
     * Имена исключаемых из сериализации/десериализации полей
     */
    private HashSet<String> mExludedDeserializeFields;
    private HashSet<String> mExludedSerializeFields;

    /*
     * Исключаемые поля с такими модификаторами доступа
     */
    private int mExludedModifiers;

    /*
     * Сериализовать ли null значения
     */
    private boolean mSerializeNull;

    /*
     * форматирование: отступ в блоке json по умолчанию
     */
    private String mIndentation = "";

    public JSONProvider() {
        mSerializeNull = false;

        // Имена исключаемых при десериализации из json
        mExludedDeserializeFields = new HashSet<String>();
        mExludedDeserializeFields.add(Ref.FIELD_NAME_MODIFIED.toLowerCase());
        mExludedDeserializeFields.add(Ref.FIELD_NAME_NEW_ITEM.toLowerCase());
        mExludedDeserializeFields.add(Table.FILED_NAME_RECORD_KEY.toLowerCase());
        mExludedDeserializeFields.add(Ref.FIELD_NAME_TABLE_PART_DATA.toLowerCase());

        // + при сериализации в json так же пропускаются
        mExludedSerializeFields = new HashSet<String>();
        mExludedSerializeFields.add(Catalog.FIELD_NAME_LEVEL.toLowerCase());
        mExludedSerializeFields.add(Catalog.FIELD_NAME_PREDEFINED.toLowerCase());
        // Row.class ext of java.util.Observable
        mExludedSerializeFields.add(FILED_NAME_OBSERVERS.toLowerCase());
        mExludedSerializeFields.add(FILED_NAME_CHANGED.toLowerCase());

        // Исключаемые поля с такими модификаторами доступа
        mExludedModifiers =
                Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.NATIVE | Modifier.STATIC |
                Modifier.TRANSIENT | Modifier.VOLATILE;

        mIndentation = "  ";
    }

    /**
     * Десериализовать Json данные из файла в объект заданного типа.
     *
     * @param classOfT класс метаданных 1С (наследник от Ref или Table), объект
     *                 которого ожидается в json
     * @param file     содержимое считывается в потоке FileInputStream чтобы избежать
     *                 загрузки полного документа в память
     * @return объект заданного типа или null если десериализация не удалась
     */
    public <T> T fromJson(Class<T> classOfT, File file) {

        T obj = null;

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            obj = readJsonObject(classOfT, in);
        } catch (FileNotFoundException e) {
            Dbg.printStackTrace(e);
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            IOHelper.close(in);
        }
        return obj;
    }

    /**
     * Десериализовать Json данные из строки в объект заданного типа.
     *
     * @param classOfT класс метаданных 1С (наследник от Ref или Table), объект
     *                 которого ожидается в json
     * @param value    JSON представление объекта
     * @return объект заданного типа или null если десериализация не удалась
     */
    public <T> T fromJson(Class<T> classOfT, String value) {

        T obj = null;
        InputStream in = null;

        try {
            in = new ByteArrayInputStream(value.getBytes());
            obj = readJsonObject(classOfT, in);
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            IOHelper.close(in);
        }
        return obj;
    }

    /**
     * Десериализовать Json данные из файла в коллекцию объектов
     * заданного типа.
     *
     * @param <T>
     * @param classOfT Класс метаданных 1С (наследник от Ref или Table), массив
     *                 объектов которого ожидается в json
     * @param file     Содержимое считывается в потоке FileInputStream чтобы избежать
     *                 загрузки полного документа в память
     * @return Коллекция объектов заданного типа или null если десериализация не
     * удалась
     */
    public <T> List<T> fromJsonArray(Class<T> classOfT, File file) {

        List<T> lst = null;
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            lst = readJsonArray(classOfT, in);
        } catch (FileNotFoundException e) {
            Dbg.printStackTrace(e);
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            IOHelper.close(in);
        }
        return lst;
    }

    /**
     * Десериализовать Json данные из строки в коллекцию объектов
     * заданного типа.
     *
     * @param classOfT Класс метаданных 1С (наследник от Ref или Table), массив
     *                 объектов которого ожидается в json
     * @param value    JSON представление массива объектов
     * @return
     */
    public <T> List<T> fromJsonArray(Class<T> classOfT, String value) {

        List<T> lst = null;
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(value.getBytes());
            lst = readJsonArray(classOfT, in);
        } catch (FileNotFoundException e) {
            Dbg.printStackTrace(e);
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            IOHelper.close(in);
        }
        return lst;
    }

    /**
     * Сериализовать объект в JSON
     *
     * @param src сериализуемый объект (класс метаданных 1С, наследник от Ref
     *            или Table)
     * @return Json представление src.
     */
    public String toJson(Object src) {
        String strJson = null;
        OutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            writeJsonObject(src, out);
            strJson = out.toString();
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            IOHelper.close(out);
        }
        return strJson;
    }

    /**
     * Сериализовать объект в JSON, данные сохраняются в файл по
     * указанному пути
     *
     * @param src  сериализуемый объект (класс метаданных 1С, наследник от Ref
     *             или Table)
     * @param file Файл в котором будет сохранен результат
     * @return Истина если сериализация в файл прошла успешно, Ложь в противном
     * случае
     */
    public boolean toJson(Object src, File file) {
        boolean complete = false;
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            writeJsonObject(src, out);
            complete = true;
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            IOHelper.close(out);
        }
        return complete;
    }

    /**
     * Сериализовать коллекцию однотипных объектов в JSON представление
     *
     * @param src сериализуемая коллекция объектов метаданных 1с (Ref или Table)
     * @return Json представление src
     */
    public String toJsonArray(List<? extends Object> src) {
        String strJson = null;
        if (src.size() > 0) {
            OutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                writeJsonArray(src, out);
                strJson = out.toString();
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            } finally {
                IOHelper.close(out);
            }
        }
        return strJson;
    }

    /**
     * Сериализовать коллекцию однотипных объектов в JSON
     * представление,данные сохраняются в файл по указанному пути
     *
     * @param src  сериализуемая коллекция объектов метаданных 1с (Ref или Table)
     * @param file Файл в котором будет сохранен результат
     * @return Истина если сериализация в файл прошла успешно, Ложь в противном
     * случае
     */
    public boolean toJsonArray(List<? extends Object> src, File file) {

        boolean complete = false;
        if (src.size() > 0) {
            OutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(file));
                writeJsonArray(src, out);
                complete = true;
            } catch (FileNotFoundException e) {
                Dbg.printStackTrace(e);
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            } finally {
                IOHelper.close(out);
            }
        }
        return complete;
    }

    /**
     * Читать JSON документ содержащий коллекцию объектов заданного типа
     *
     * @param <T>
     * @param classOfT Класс метаданных 1С (наследник от Ref или Table), массив
     *                 объектов которого ожидается в json
     * @param in       Входящий поток данных
     * @return Коллекция объектов заданного типа или null если десериализация не
     * удалась
     * @throws IOException
     */
    private <T> List<T> readJsonArray(Class<T> classOfT, InputStream in) throws IOException {

        JsonReader reader = new JsonReader(new InputStreamReader(in, UTF_8));
        reader.setLenient(true);

        List<T> lst = new ArrayList<T>();
        reader.beginArray();
        while (reader.hasNext()) {
            T obj = readJsonObject(classOfT, reader);
            lst.add(obj);
        }
        reader.endArray();
        reader.close();

        return lst;
    }

    /**
     * Читать JSON содержащий объект заданного типа
     *
     * @param classOfT Класс метаданных 1С (наследник от Ref или Table), массив
     *                 объектов которого ожидается в json
     * @param in       Входящий поток данных
     * @return объект заданного типа или null если десериализация не удалась
     * @throws IOException
     */
    private <T> T readJsonObject(Class<T> classOfT, InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, UTF_8));
        reader.setLenient(true);
        T obj = readJsonObject(classOfT, reader);
        reader.close();
        return obj;
    }

    /**
     * Сериализовать коллекцию однотипных данных в JSON
     *
     * @param src Коллекция объектов заданного типа
     * @param out Исходящий поток данных
     */
    private void writeJsonArray(List<? extends Object> src, OutputStream out) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, UTF_8));
        writer.setIndent(mIndentation);
        writeJsonArray(writer, src);
        writer.close();
    }

    /**
     * Сериализовать объект в JSON
     *
     * @param src сериализуемый объект
     * @param out Исходящий поток данных
     * @throws IOException
     */
    private void writeJsonObject(Object src, OutputStream out) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, UTF_8));
        writer.setIndent(mIndentation);
        writeJsonObject(src.getClass(), src, writer);
        writer.close();
    }

    /**
     * Записать коллекцию в JSON поток
     *
     * @param writer поток для записи JSON
     * @param src    Коллекция объектов заданного типа
     * @throws IOException
     */
    private void writeJsonArray(JsonWriter writer, List<? extends Object> src) throws IOException {
        writer.beginArray();
        for (Object obj : src) {
            writeJsonObject(obj.getClass(), obj, writer);
        }
        writer.endArray();
    }

    /**
     * Получить коллекцию всех полей класса (включая родительские)
     */
    private Collection<Field> getClassField(Class<?> clazz) {
        if (mMapClassFields == null) {
            mMapClassFields = new HashMap<Class<?>, Collection<Field>>();
        }

        if (mMapClassFields.containsKey(clazz)) {
            return mMapClassFields.get(clazz);
        } else {
            Collection<Field> collection = MetadataHelper.getFields(clazz);
            mMapClassFields.put(clazz, collection);

            return collection;
        }
    }

    /**
     * Найти поле в коллекции по его имени
     *
     * @param name
     * @param toJson true - это сериализация, иначе десериализация
     * @return
     */
    private Field findFieldOfCollection(Collection<Field> collection, String name, boolean toJson) {
        Field f = null;
        String lowName = name.toLowerCase();
        for (Field field : collection) {
            if (field.getName().equalsIgnoreCase(lowName)) {
                if (!isExcludedField(field, toJson)) {
                    f = field;
                    break;
                }
            }
        }
        return f;
    }

    /**
     * Возвращает истина если поле исключается из сериализации
     *
     * @param field
     * @param toJson true - это сериализация, иначе десериализация
     * @return
     */
    private boolean isExcludedField(Field field, boolean toJson) {

        final String lowName = field.getName().toLowerCase();

        // это поле класса не в списке исключений по имени
        // не исключаемое по модификатору
        if (isExcludedModifier(field, toJson)) {
            return true;
        }

        if (mExludedDeserializeFields.contains(lowName)) {
            return true;
        }

        if (toJson && mExludedSerializeFields.contains(lowName)) {
            return true;
        }

        return false;
    }

    /**
     * Возвращает истина, если модификатор доступа поля присутствует в списке
     * исключаемых
     *
     * @param field
     * @return
     */
    private boolean isExcludedModifier(Field field, boolean serialize) {

        if ((mExludedModifiers & field.getModifiers()) != 0) {
            return true;
        }

        if (field.isSynthetic()) {
            return true;
        }

        if (isInnerClass(field.getType())) {
            return true;
        }

        if (isAnonymousOrLocal(field.getType())) {
            return true;
        }

        // аннотация gson @Expose
        /*
         * Expose annotation = field.getAnnotation(Expose.class); if (annotation
		 * != null && (serialize ? !annotation.serialize() : !annotation
		 * .deserialize())) { return true; }
		 */

        return false;
    }

    private boolean isAnonymousOrLocal(Class<?> clazz) {
        return !Enum.class.isAssignableFrom(clazz) &&
               (clazz.isAnonymousClass() || clazz.isLocalClass());
    }

    private boolean isInnerClass(Class<?> clazz) {
        return clazz.isMemberClass() && !isStatic(clazz);
    }

    private boolean isStatic(Class<?> clazz) {
        return (clazz.getModifiers() & Modifier.STATIC) != 0;
    }

    /**
     * Считывает объект из потока
     *
     * @param classOfT Класс, объект которого ожидается в json
     * @param reader   поток чтения
     * @return объект заданного типа или null если десериализация не удалась
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private <T> T readJsonObject(Class<T> classOfT, JsonReader reader) throws IOException {

        T obj = createInstance(classOfT);
        if (obj != null) {

            Collection<Field> allFields = getClassField(classOfT);

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();

                Field f = findFieldOfCollection(allFields, name, false);
                if (f != null) {

                    Class<?> classOfF = f.getType();
                    Class<?> classOfElements = null;
                    boolean isTablePart = false;

                    if (f.isAnnotationPresent(ForeignCollectionField.class)) {
                        ParameterizedType elementType = (ParameterizedType) f.getGenericType();
                        classOfElements = (Class<?>) elementType.getActualTypeArguments()[0];
                        isTablePart = true;
                    }
                    TypeAdapter<T> adapter =
                            (TypeAdapter<T>) getTypeAdapter(classOfF, classOfT, classOfElements);
                    if (adapter != null) {
                        T value = adapter.read(reader);
                        if (value != null)
                            try {
                                if (isTablePart) {
                                    ((Ref) obj).setTmpTablePartData(name,
                                                                    (List<? extends TablePart>) value);
                                } else {
                                    f.set(obj, value);
                                }

                            } catch (IllegalArgumentException e) {
                                Dbg.printStackTrace(e);
                            } catch (IllegalAccessException e) {
                                Dbg.printStackTrace(e);
                            }
                    } else {
                        reader.skipValue();
                    }

                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
        }

        return obj;
    }

    /**
     * Сериализовать объект в JSON
     *
     * @param <T>
     * @param obj    сериализуемый объект
     * @param writer поток записи
     * @throws IOException
     */
    private <T> void writeJsonObject(Class<T> classOfT, Object obj, JsonWriter writer)
            throws IOException {

        Collection<Field> allFields = getClassField(obj.getClass());
        writer.beginObject();
        for (Field f : allFields) {
            if (!isExcludedField(f, true)) {

                String name = f.getName();
                Class<?> classOfF = f.getType();
                Class<?> classOfElements = null;
                boolean isTablePart = false;

                if (f.isAnnotationPresent(ForeignCollectionField.class)) {
                    ParameterizedType elementType = (ParameterizedType) f.getGenericType();
                    classOfElements = (Class<?>) elementType.getActualTypeArguments()[0];
                    isTablePart = true;
                }

                TypeAdapter<T> adapter =
                        (TypeAdapter<T>) getTypeAdapter(classOfF, classOfT, classOfElements);
                if (adapter != null) {
                    T value = null;
                    try {
                        value = (T) f.get(obj);
                    } catch (IllegalArgumentException e) {
                        Dbg.printStackTrace(e);
                    } catch (IllegalAccessException e) {
                        Dbg.printStackTrace(e);
                    }

                    if (isTablePart) {
                        List<? extends TablePart> lstTablePart =
                                getTablePartCollection((Ref) obj, name,
                                                       (ForeignCollection<? extends TablePart>) value);
                        if (lstTablePart != null) {
                            writer.name(name);
                            ((TablePartCollectionAdapter) adapter).write(writer, lstTablePart);
                        } else if (mSerializeNull)
                            writer.name(name).nullValue();

                    } else {
                        // default
                        if (value != null) {
                            writer.name(name);
                            adapter.write(writer, value);
                        } else if (mSerializeNull)
                            writer.name(name).nullValue();

                    }

                }
            }
        }
        writer.endObject();
    }

    /**
     * Создать объект класса
     *
     * @param classOfT
     * @return
     */
    private <T> T createInstance(Class<T> classOfT) {
        T obj = null;
        try {
            obj = classOfT.newInstance();
        } catch (InstantiationException e) {
            Dbg.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Dbg.printStackTrace(e);
        }
        return obj;
    }

    /**
     * Получить адаптер для парсинга значения данного класса
     *
     * @param <E>
     * @param classOfF        класс поля для которого нужен адаптер
     * @param classOfParent   класс родителя
     * @param classOfElements класс для элементов коллекции (если это коллекция)
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T, E> TypeAdapter<T> getTypeAdapter(Class<T> classOfF, Class<?> classOfParent,
            Class<E> classOfElements) {

        TypeAdapter<T> adapter;

        if (mTypeAdapters == null) {
            mTypeAdapters = new HashMap<String, TypeAdapter<?>>();
        }

        String className = classOfF.getName();
        if (classOfElements != null) {
            className = className + classOfElements.getName();
        }

        if (mTypeAdapters.containsKey(className)) {
            adapter = (TypeAdapter<T>) mTypeAdapters.get(className);
        } else {
            adapter = createTypeAdapter(classOfF, classOfParent, classOfElements);
            if (adapter != null) {
                mTypeAdapters.put(className, adapter);
            }
        }
        return adapter;
    }

    /**
     * Создать адаптер для парсинга значения заданного класса
     *
     * @param classOfT        класс поля для которого создается адаптер
     * @param classOfParent   класс родителя
     * @param classOfElements класс для элементов коллекции (если это коллекция)
     * @return
     */
    private <T> TypeAdapter<T> createTypeAdapter(Class<T> classOfT, Class<?> classOfParent,
            Class<?> classOfElements) {

        if (MetadataHelper.extendsOfClass(classOfT, Ref.class)) {

            return new RefTypeAdapter<T>(classOfT);

        } else if (MetadataHelper.extendsOfClass(classOfT, TablePart.class)) {

            return new TablePartAdapter<T>(classOfT, classOfParent);

        } else if (classOfT.equals(ValueStorage.class)) {

            return new ValueStorageAdapter<T>();

        } else if (classOfT.isEnum() && classOfT.isAnnotationPresent(MetadataObject.class)) {

            return new EnumTypeAdapter<T>(classOfT);

        } else if (classOfT.equals(ForeignCollection.class)) {

            TypeAdapter<?> elementAdapter = getTypeAdapter(classOfElements, classOfParent, null);
            TypeAdapter<T> adapter =
                    (TypeAdapter<T>) newTablePartCollectionAdapter(classOfParent, classOfElements,
                                                                   elementAdapter);
            return adapter;

        } else if (classOfT.equals(Date.class)) {

            return (TypeAdapter<T>) new DateAdapter();

        } else {
            return (TypeAdapter<T>) new SimpleTypeAdapter(classOfT);
        }

    }

    /**
     * Сериализатор и десериализатор 'Хранилища значения'
     *
     * @param <T>
     */
    private class ValueStorageAdapter<T> extends TypeAdapter<T> {

        @SuppressWarnings("unchecked")
        @Override
        public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else {
                String strValue = reader.nextString();
                if (!TextUtils.isEmpty(strValue)) {
                    ValueStorage vs = new ValueStorage();
                    vs.data = Base64.decode(strValue);
                    return (T) vs;
                }
                return null;
            }
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                ValueStorage vs = (ValueStorage) value;
                out.value(Base64.encode(vs.data));
            }
        }

    }

    /**
     * Сериализатор и десериализатор перечислений 1С (в json передается как
     * значение на русском языке, в java это в аннотации @MetadataField(name)
     *
     * @param <T>
     */
    private class EnumTypeAdapter<T> extends TypeAdapter<T> {

        private final Class<T> clazz;
        private final HashMap<String, T> mapFromRu;
        private final HashMap<T, String> mapToRu;

        @SuppressWarnings({"unchecked", "rawtypes"})
        public EnumTypeAdapter(Class<T> rawType) {
            clazz = rawType;

            mapFromRu = new HashMap<String, T>();
            mapToRu = new HashMap<T, String>();

            for (Field f : clazz.getFields()) {

                if (f.isAnnotationPresent(MetadataField.class)) {
                    String nameRu = f.getAnnotation(MetadataField.class).name();
                    String nameEn = f.getName();

                    T value = (T) Enum.valueOf((Class<Enum>) clazz, nameEn);

                    mapFromRu.put(nameRu, value);
                    mapToRu.put(value, nameRu);

                }
            }

        }

        @Override
        public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else {
                String nameRu = reader.nextString();
                if (!TextUtils.isEmpty(nameRu)) {
                    return mapFromRu.get(nameRu);
                }
                return null;
            }
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(mapToRu.get(value));
            }
        }

    }

    /**
     * Сериализатор и десериализатор классов наследников Ref
     *
     * @param <T>
     */
    private class RefTypeAdapter<T> extends TypeAdapter<T> {

        private final Class<T> clazz;

        public RefTypeAdapter(Class<T> rawType) {
            clazz = rawType;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else {
                try {
                    String strValue = reader.nextString();
                    if (!TextUtils.isEmpty(strValue)) {
                        Ref ref = (Ref) clazz.newInstance();
                        ref.setRef(UUID.fromString(strValue));
                        return (T) ref;
                    }
                } catch (InstantiationException e) {
                    Dbg.printStackTrace(e);
                } catch (IllegalAccessException e) {
                    Dbg.printStackTrace(e);
                }

                return null;
            }
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                Ref ref = (Ref) value;
                out.value(ref.getRef().toString());
            }

        }

    }

    /**
     * Сериализатор и десериализатор коллекции строк табличных частей
     *
     * @param <E>
     */
    private class TablePartCollectionAdapter<E> extends TypeAdapter<List<E>> {

        private final TypeAdapter<E> elementAdapter;

        public TablePartCollectionAdapter(TypeAdapter<E> elementAdapter) {
            this.elementAdapter = elementAdapter;
        }

        public void write(JsonWriter out, List<E> value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginArray();
            for (E entry : value) {
                elementAdapter.write(out, entry);
            }
            out.endArray();
        }

        public List<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            List<E> list = new ArrayList<E>();
            in.beginArray();
            while (in.hasNext()) {
                E element = elementAdapter.read(in);
                list.add(element);
            }
            in.endArray();
            return list;
        }

    }

    /**
     * Создать сериализатор и десериализатор строк табличных частей
     *
     * @param elementAdapter
     * @return
     */
    private <E> TablePartCollectionAdapter<List<E>> newTablePartCollectionAdapter(
            Class<?> classOfParent, Class<?> classOfElements, TypeAdapter<E> elementAdapter) {
        return new TablePartCollectionAdapter<List<E>>((TypeAdapter<List<E>>) elementAdapter);
    }

    /**
     * Читать коллекцию табличной части из кеш-а или из базы данных
     *
     * @param obj
     * @param fieldName
     * @param fieldValue
     * @return
     */
    @SuppressWarnings("unchecked")
    private <E> List<E> getTablePartCollection(Ref obj, String fieldName,
            ForeignCollection<E> fieldValue) {

        List<E> lstTablePart = null;
        List<? extends TablePart> lstTmpTablePartData = obj.getTmpTablePartData(fieldName);
        if (lstTmpTablePartData != null) {
            lstTablePart = (List<E>) lstTmpTablePartData;
        } else if (fieldValue != null) {

            lstTablePart = new ArrayList<E>();
            // читаем из базы данных и добавляем во временную коллекцию
            for (E e : fieldValue) {
                lstTablePart.add(e);
            }
        }
        return lstTablePart;
    }

    /**
     * Создать сериализатор и десериализатор табличных частей
     *
     * @param <T>
     */
    private class TablePartAdapter<T> extends TypeAdapter<T> {

        private final Class<T> classOfT;
        private final Class<?> classOfParent;

        public TablePartAdapter(Class<T> classOfT, Class<?> classOfParent) {
            this.classOfT = classOfT;
            this.classOfParent = classOfParent;
        }

        @Override
        public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            T obj = readJsonObject(classOfT, reader);
            return obj;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            } else {
                writeJsonObject(classOfT, value, out);
            }
        }

    }

    /**
     * Сериализатор и десериализатор даты
     */
    @SuppressLint("SimpleDateFormat")
    private class DateAdapter extends TypeAdapter<Date> {

        private final SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_SERIALIZE_FORMAT);

        @Override
        public Date read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else {
                try {
                    String strValue = reader.nextString();
                    if (!TextUtils.isEmpty(strValue)) {
                        return mDateFormat.parse(strValue);
                    }
                } catch (ParseException e) {
                    Dbg.printStackTrace(e);
                }
                return null;
            }
        }

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(mDateFormat.format(value));
            }
        }

    }

    /**
     * Сериализатор и десериализатор прочих примитивных типов (String, boolean,
     * int,long,double, float)
     */
    private class SimpleTypeAdapter extends TypeAdapter<Object> {

        private final Class<?> classOfT;

        public SimpleTypeAdapter(Class<?> classOfT) {
            this.classOfT = classOfT;
        }

        @Override
        public Object read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else {
                Object obj = null;
                if (classOfT.equals(String.class) || classOfT.equals(char.class)) {
                    obj = reader.nextString();
                } else if (classOfT.equals(UUID.class)) {
                    obj = UUID.fromString(reader.nextString());
                } else if (classOfT.equals(Boolean.class) || classOfT.equals(boolean.class)) {
                    obj = reader.nextBoolean();
                } else if (classOfT.equals(Double.class) || classOfT.equals(double.class) ||
                           classOfT.equals(Float.class) || classOfT.equals(float.class)) {
                    obj = reader.nextDouble();
                } else if (classOfT.equals(Integer.class) || classOfT.equals(int.class) ||
                           classOfT.equals(Short.class) || classOfT.equals(short.class) ||
                           classOfT.equals(Byte.class) || classOfT.equals(byte.class)) {
                    obj = reader.nextInt();
                } else if (classOfT.equals(BigInteger.class)) {
                    String strValue = reader.nextString();
                    if (!TextUtils.isEmpty(strValue)) {
                        obj = new BigInteger(strValue);
                    }
                } else if (classOfT.equals(Long.class) || classOfT.equals(long.class)) {
                    obj = reader.nextLong();
                }
                return obj;
            }
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                if (classOfT.equals(String.class) || classOfT.equals(char.class) ||
                    classOfT.equals(UUID.class)) {
                    out.value(value.toString());
                } else if (classOfT.equals(Boolean.class) || classOfT.equals(boolean.class)) {
                    out.value((Boolean) value);
                } else if (classOfT.equals(Double.class) || classOfT.equals(double.class) ||
                           classOfT.equals(Float.class) || classOfT.equals(float.class)) {
                    out.value((Double) value);
                } else if (classOfT.equals(Integer.class) || classOfT.equals(int.class) ||
                           classOfT.equals(Short.class) || classOfT.equals(short.class) ||
                           classOfT.equals(Byte.class) || classOfT.equals(byte.class)) {
                    out.value((Integer) value);
                } else if (classOfT.equals(BigInteger.class)) {
                    out.value(value.toString());
                } else if (classOfT.equals(Long.class) || classOfT.equals(long.class)) {
                    out.value((Long) value);
                }

            }

        }

    }
}
