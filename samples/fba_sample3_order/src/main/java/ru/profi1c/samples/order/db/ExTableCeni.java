/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.TableEx;

/**
 * Внешняя таблица 'Цены'
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = ExTableCeni.TABLE_NAME, daoClass = ExTableCeniDao.class)
@MetadataObject(type = MetadataObject.TYPE_EXTERNAL_TABLE, name = "Цены")
public class ExTableCeni extends TableEx {
    public static final String TABLE_NAME = "ExTableCeni";

    private static final long serialVersionUID = 1L;


    /**
     * Имя поля 'ТипЦен' в таблице базы данных
     */
    public static final String FIELD_NAME_TIP_CEN = "tipCen";

    /**
     * ТипЦен
     */
    @DatabaseField(columnName = FIELD_NAME_TIP_CEN, index = true, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ТипЦен", description = "ТипЦен")
    public CatalogTipiCenNomenklaturi tipCen;

    /**
     * Имя поля 'Номенклатура' в таблице базы данных
     */
    public static final String FIELD_NAME_NOMENKLATURA = "nomenklatura";

    /**
     * Номенклатура
     */
    @DatabaseField(columnName = FIELD_NAME_NOMENKLATURA, index = true, foreign = true, foreignAutoRefresh = true)
    @MetadataField(type = MetadataFieldType.REF, name = "Номенклатура", description = "Номенклатура")
    public CatalogNomenklatura nomenklatura;

    /**
     * Имя поля 'ХарактеристикаНоменклатуры' в таблице базы данных
     */
    public static final String FIELD_NAME_HARAKTERISTIKA_NOMENKLATURI =
            "harakteristikaNomenklaturi";

    /**
     * ХарактеристикаНоменклатуры
     */
    @DatabaseField(columnName = FIELD_NAME_HARAKTERISTIKA_NOMENKLATURI, index = true, foreign = true, foreignAutoRefresh = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ХарактеристикаНоменклатуры", description = "ХарактеристикаНоменклатуры")
    public CatalogHarakteristikiNomenklaturi harakteristikaNomenklaturi;

    /**
     * Имя поля 'Цена' в таблице базы данных
     */
    public static final String FIELD_NAME_CENA = "cena";

    /**
     * Цена
     */
    @DatabaseField(columnName = FIELD_NAME_CENA)
    @MetadataField(type = MetadataFieldType.DOUBLE, name = "Цена", description = "Цена")
    public double cena;

    /**
     * Имя поля 'ЕдиницаИзмерения' в таблице базы данных
     */
    public static final String FIELD_NAME_EDINICA_IZMERENIYA = "edinicaIzmereniya";

    /**
     * ЕдиницаИзмерения
     */
    @DatabaseField(columnName = FIELD_NAME_EDINICA_IZMERENIYA, foreign = true, foreignAutoRefresh = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ЕдиницаИзмерения", description = "ЕдиницаИзмерения")
    public CatalogEdiniciIzmereniya edinicaIzmereniya;

    //Используется только как кэш введенного пользователем значения, в базе данных не храним.
    public int kolvo;

    @Override
    public String createRecordKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.createRecordKey());
        sb.append(tipCen);
        sb.append(nomenklatura);
        sb.append(harakteristikaNomenklaturi);

        return sb.toString();
    }

}
