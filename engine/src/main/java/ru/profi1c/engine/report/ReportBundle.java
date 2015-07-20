package ru.profi1c.engine.report;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.util.IOHelper;
import ru.profi1c.engine.widget.FieldFormatter;

/**
 * Область отчета. Компилирует область макета по тестовому шаблону
 */
public class ReportBundle {

    private static final String KEY_PREFIX = "<!--";
    private static final String KEY_POSTFIX = "-->";

    private final String mTemplate;
    private HashMap<String, Object> mMapParams;
    private FieldFormatter mFormatter;

    /**
     * Создать область по шаблону из RAW ресурсов приложения
     *
     * @param context  Текущий контекст
     * @param resIdRaw Идентификатор ресурса
     * @return новую область или null в случае ошибки
     */
    public static ReportBundle fromRaw(Context context, int resIdRaw) {
        try {
            String template = IOHelper.getRawData(context, resIdRaw);
            if (template != null) {
                return new ReportBundle(template);
            }
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        }
        return null;
    }

    /**
     * Создать область по шаблону из Asset ресурсов приложения
     *
     * @param context Текущий контекст
     * @param fName   Путь к файлу шаблона в каталоге Asset, корневой каталог Asset
     *                указывать не надо, например
     *                <code>"flot/html/basechart.html"</code>
     * @return новую область или null в случае ошибки
     */
    public static ReportBundle fromAsset(Context context, String fName) {
        try {
            String template = IOHelper.getAssetsData(context, fName);
            if (template != null) {
                return new ReportBundle(template);
            }
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        }
        return null;
    }

    /**
     * Создать область шаблона по текстовому макету
     *
     * @param template тест шаблона
     */
    public ReportBundle(String template) {
        mTemplate = template;
        mFormatter = new FieldFormatter.Builder().create();
    }

    /**
     * Получить форматер значений, если не установлен, возвращает форматер по
     * умолчанию
     */
    public FieldFormatter getFieldFormatter() {
        return mFormatter;
    }

    /**
     * Установить форматер значений
     */
    public void setFieldFormatter(FieldFormatter fieldFormatter) {
        this.mFormatter = fieldFormatter;
    }

    /**
     * Установить коллекцию параметров отчета
     */
    public void setParams(HashMap<String, Object> params) {
        if (mMapParams == null)
            this.mMapParams = params;
        else {
            params.putAll(mMapParams);
        }
    }

    /**
     * Установить параметр отчета
     *
     * @param key   имя параметра
     * @param value значение параметра
     */
    public void putParam(String key, Object value) {
        if (mMapParams == null) {
            mMapParams = new HashMap<String, Object>();
        }
        mMapParams.put(key, value);
    }

    /**
     * Построить отчет, форматировать и заполнить шаблон согласно установленным
     * параметрам
     */
    public String build() {
        String result = mTemplate;

        if (mMapParams != null) {

            Set<String> keys = mMapParams.keySet();
            Iterator<String> iterate = keys.iterator();
            while (iterate.hasNext()) {
                String key = iterate.next();
                Object value = mMapParams.get(key);
                String formatValue = "";

                if (value instanceof IPresentation) {
                    String str = ((IPresentation) value).getPresentation();
                    formatValue = mFormatter.format(str);
                } else {
                    formatValue = mFormatter.format(value);
                }
                result = result.replaceAll(KEY_PREFIX + key + KEY_POSTFIX, formatValue);
            }
        }
        return result;
    }

}
