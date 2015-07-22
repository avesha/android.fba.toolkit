/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import ru.profi1c.engine.meta.CatalogDao;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы с элементами справочника 'Номенклатура' (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class CatalogNomenklaturaDao extends CatalogDao<CatalogNomenklatura> {

    public CatalogNomenklaturaDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CatalogNomenklatura.class);
    }

    /*
     * Пример указания сортировки с помощью построителя запроса OrmLite:
     * помеченные на удаление элементы будут внизу списка
     */
    public List<CatalogNomenklatura> selectBuilderRaw() throws SQLException {
        QueryBuilder<CatalogNomenklatura, String> builder = queryBuilder();
        builder.orderByRaw(CatalogNomenklatura.FIELD_NAME_DELETIONMARK + " DESC");
        return query(builder.prepare());
    }

    public List<CatalogNomenklatura> selectRaw() throws SQLException {

        //Составление текста запроса: выборка только необходимых полей и указание сортировки
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ").append(CatalogNomenklatura.FIELD_NAME_REF).append(",")
          .append(CatalogNomenklatura.FIELD_NAME_CODE).append(",")
          .append(CatalogNomenklatura.FIELD_NAME_DESCRIPTION);
        sb.append(" FROM ").append(CatalogNomenklatura.TABLE_NAME);
        sb.append(" ORDER BY ").append(CatalogNomenklatura.FIELD_NAME_DESCRIPTION + " DESC");

        //Выборка данных с преобразованием результата  к коллекции объектов
        GenericRawResults<CatalogNomenklatura> rawResults =
                queryRaw(sb.toString(), new RawRowMapper<CatalogNomenklatura>() {

                             @Override
                             public CatalogNomenklatura mapRow(String[] columnNames,
                                     String[] resultColumns) throws SQLException {

                                 CatalogNomenklatura item = new CatalogNomenklatura();
                                 item.setRef(UUID.fromString(resultColumns[0]));
                                 item.setCode(resultColumns[1]);
                                 item.setDescription(resultColumns[2]);

                                 return item;
                             }
                         });

        //результат выборки как список
        return rawResults.getResults();
    }
}