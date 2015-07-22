/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.report.head.db;

import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.ConstTable;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Table;
import ru.profi1c.engine.meta.TableInfReg;

/**
 * Класс-помощник для работы с метаданными объектов (соответствие объектов 1С и классов Java)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class MetaHelper extends MetadataHelper {

    //Singleton variant: Double Checked Locking & volatile
    private static volatile MetaHelper instance;

    private static Class<? extends ConstTable> constTableClass;
    private static List<Class<? extends Catalog>> lstCatalogClasses;
    private static List<Class<? extends Document>> lstDocumentClasses;
    private static List<Class<? extends TableInfReg>> lstRegClasses;
    private static List<Class<? extends Table>> lstExtTableClasses;

    static {
        //константы
        constTableClass = null;

        //справочники
        lstCatalogClasses = null;

        //документы
        lstDocumentClasses = null;

        //регистры сведений
        lstRegClasses = null;

        //внешние таблицы
        lstExtTableClasses = new ArrayList<Class<? extends Table>>();
        lstExtTableClasses.add(ExTableRaportRukovoditelyu.class);

    }

    private MetaHelper() {
    }

    public static MetaHelper getInstance() {
        MetaHelper localInstance = instance;

        if (localInstance == null) {
            synchronized (MetaHelper.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MetaHelper();
                }
            }
        }

        return localInstance;
    }

    @Override
    public Class<? extends ConstTable> getConstClass() {
        return constTableClass;
    }

    @Override
    public List<Class<? extends Catalog>> getCatalogClasses() {
        return lstCatalogClasses;
    }

    @Override
    public List<Class<? extends Document>> getDocumentClasses() {
        return lstDocumentClasses;
    }

    @Override
    public List<Class<? extends TableInfReg>> getRegClasses() {
        return lstRegClasses;
    }

    @Override
    public List<Class<? extends Table>> getExtTableClasses() {
        return lstExtTableClasses;
    }
}
