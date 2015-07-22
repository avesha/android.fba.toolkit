/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.report.head.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableEx;

/**
 * Внешняя таблица 'РапортРуководителю'
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = ExTableRaportRukovoditelyu.TABLE_NAME, daoClass = ExTableRaportRukovoditelyuDao.class)
@MetadataObject(type = MetadataObject.TYPE_EXTERNAL_TABLE, name = "РапортРуководителю")
public class ExTableRaportRukovoditelyu extends TableEx {
    public static final String TABLE_NAME = "ExTableRaportRukovoditelyu";

    private static final long serialVersionUID = 1L;


    /**
     * Имя поля 'Показатель' в таблице базы данных
     */
    public static final String FIELD_NAME_POKAZATEL = "pokazatel";

    /**
     * Показатель
     */
    @DatabaseField(columnName = FIELD_NAME_POKAZATEL, index = true)
    @MetadataField(type = MetadataFieldType.STRING, name = "Показатель", description = "Показатель")
    public String pokazatel;

    /**
     * Имя поля 'Значение' в таблице базы данных
     */
    public static final String FIELD_NAME_ZNACHENIE = "znachenie";

    /**
     * Значение
     */
    @DatabaseField(columnName = FIELD_NAME_ZNACHENIE)
    @MetadataField(type = MetadataFieldType.DOUBLE, name = "Значение", description = "Значение")
    public double znachenie;

    @Override
    public String createRecordKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.createRecordKey());
        sb.append(pokazatel);

        return sb.toString();
    }

}
