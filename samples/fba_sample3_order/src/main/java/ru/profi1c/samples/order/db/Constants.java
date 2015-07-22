/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import ru.profi1c.engine.meta.ConstTable;
import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Константы
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = Constants.TABLE_NAME, daoClass = ConstantsDao.class)
@MetadataObject(type = MetadataObject.TYPE_CONSTANT)
public final class Constants extends ConstTable {
    public static final String TABLE_NAME = "Constants";

    private static final long serialVersionUID = 1L;


    /**
     * Имя поля 'Основная организация' в таблице базы данных
     */
    public static final String FIELD_NAME_OSNOVNAYA_ORGANIZACIYA = "osnovnayaOrganizaciya";

    /**
     * Основная организация
     */
    @DatabaseField(columnName = FIELD_NAME_OSNOVNAYA_ORGANIZACIYA, foreign = true, foreignAutoRefresh = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ОсновнаяОрганизация", description = "Основная организация")
    public CatalogOrganizacii osnovnayaOrganizaciya;

    /**
     * Имя поля 'Основной тип цен продажи' в таблице базы данных
     */
    public static final String FIELD_NAME_OSNOVNOI_TIP_CEN_PRODAZHI = "osnovnoiTipCenProdazhi";

    /**
     * Основной тип цен продажи
     */
    @DatabaseField(columnName = FIELD_NAME_OSNOVNOI_TIP_CEN_PRODAZHI, foreign = true, foreignAutoRefresh = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ОсновнойТипЦенПродажи", description = "Основной тип цен продажи")
    public CatalogTipiCenNomenklaturi osnovnoiTipCenProdazhi;

}
