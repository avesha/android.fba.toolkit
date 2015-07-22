/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import ru.profi1c.engine.meta.MetadataField;
import ru.profi1c.engine.meta.MetadataFieldType;
import ru.profi1c.engine.meta.MetadataObject;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.TablePart;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Табличная часть 'Товары' документа 'Заказ покупателя'
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
@DatabaseTable(tableName = DocumentZakazPokupatelyaTPTovari.TABLE_NAME, daoClass = DocumentZakazPokupatelyaTPTovariDao.class)
@MetadataObject(type = MetadataObject.TYPE_TABLE_PART, name = DocumentZakazPokupatelyaTPTovari.META_NAME)
public class DocumentZakazPokupatelyaTPTovari extends TablePart {

    /**
     * Имя таблицы в базе данных
     */
    public static final String TABLE_NAME = "DocumentZakazPokupatelyaTPTovari";

    /**
     * Имя метаданных объекта в 1С (не изменять)
     */
    public static final String META_NAME = "Товары";

    private static final long serialVersionUID = 1L;

    /**
     * Владелец табличной части
     */
    @DatabaseField(columnName = TablePart.FIELD_NAME_REF_ID, canBeNull = false, foreign = true, foreignAutoRefresh = false,
            columnDefinition = "VARCHAR REFERENCES " + DocumentZakazPokupatelya.TABLE_NAME + "(" +
                               Ref.FIELD_NAME_REF + ") ON DELETE CASCADE")
    protected DocumentZakazPokupatelya owner;


    /**
     * Имя поля 'Номенклатура' в таблице базы данных
     */
    public static final String FIELD_NAME_NOMENKLATURA = "nomenklatura";

    /**
     * Номенклатура
     */
    @DatabaseField(columnName = FIELD_NAME_NOMENKLATURA, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "Номенклатура", description = "Номенклатура")
    public CatalogNomenklatura nomenklatura;

    /**
     * Имя поля 'Характеристика' в таблице базы данных
     */
    public static final String FIELD_NAME_HARAKTERISTIKA = "harakteristika";

    /**
     * Характеристика
     */
    @DatabaseField(columnName = FIELD_NAME_HARAKTERISTIKA, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "Характеристика", description = "Характеристика")
    public CatalogHarakteristikiNomenklaturi harakteristika;

    /**
     * Имя поля 'Единица измерения' в таблице базы данных
     */
    public static final String FIELD_NAME_EDINICA_IZMERENIYA = "edinicaIzmereniya";

    /**
     * Единица измерения
     */
    @DatabaseField(columnName = FIELD_NAME_EDINICA_IZMERENIYA, foreign = true)
    @MetadataField(type = MetadataFieldType.REF, name = "ЕдиницаИзмерения", description = "Единица измерения")
    public CatalogEdiniciIzmereniya edinicaIzmereniya;

    /**
     * Имя поля 'Количество' в таблице базы данных
     */
    public static final String FIELD_NAME_KOLICHESTVO = "kolichestvo";

    /**
     * Количество
     */
    @DatabaseField(columnName = FIELD_NAME_KOLICHESTVO)
    @MetadataField(type = MetadataFieldType.DOUBLE, name = "Количество", description = "Количество")
    public double kolichestvo;

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
     * Имя поля 'Сумма' в таблице базы данных
     */
    public static final String FIELD_NAME_SUMMA = "summa";

    /**
     * Сумма
     */
    @DatabaseField(columnName = FIELD_NAME_SUMMA)
    @MetadataField(type = MetadataFieldType.DOUBLE, name = "Сумма", description = "Сумма")
    public double summa;

    @Override
    public Ref getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Ref ref) {
        owner = (DocumentZakazPokupatelya) ref;
    }

    @Override
    public String getMetaName() {
        return META_NAME;
    }
}
