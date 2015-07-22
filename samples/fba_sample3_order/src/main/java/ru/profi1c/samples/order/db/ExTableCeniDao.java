/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.order.db;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ru.profi1c.engine.meta.TableExDao;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Менеджер для работы c записями таблицы 'Цены' (создание, удаление, поиск)
 *
 * @author ООО "Сфера" (support@sfera.ru)
 */
public class ExTableCeniDao extends TableExDao<ExTableCeni> {

    public ExTableCeniDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ExTableCeni.class);
    }

    /**
     * Получить запись из таблицы цен (без учета характеристики)
     *
     * @param tipCen       тип цен
     * @param nomenklatura номеклатура
     * @return
     * @throws SQLException
     */
    public ExTableCeni findPriceRow(CatalogTipiCenNomenklaturi tipCen,
            CatalogNomenklatura nomenklatura) throws SQLException {
        ExTableCeni priceRow = null;

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(ExTableCeni.FIELD_NAME_TIP_CEN, tipCen);
        filter.put(ExTableCeni.FIELD_NAME_NOMENKLATURA, nomenklatura);

        List<ExTableCeni> lst = select(filter);
        if (lst.size() > 0)
            priceRow = lst.get(0);

        return priceRow;
    }

    /**
     * Возвращает прайс-лист отсортированный по цене и представлению номенклатуры
     *
     * @param type тип цены
     * @return
     * @throws SQLException
     */
    public List<ExTableCeni> getPriceOfType(CatalogTipiCenNomenklaturi type) throws SQLException {

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(ExTableCeni.FIELD_NAME_TIP_CEN, type);

        List<ExTableCeni> data = select(filter);
        Collections.sort(data, sortedByPrice);
        return data;
    }

    /*
     * Сортировка по цене и представлению номенклатуры
     */
    private Comparator<ExTableCeni> sortedByPrice = new Comparator<ExTableCeni>() {

        @Override
        public int compare(ExTableCeni lhs, ExTableCeni rhs) {
            if (lhs.cena > rhs.cena)
                return 1;
            else if (lhs.cena < rhs.cena)
                return -1;
            else if (CatalogNomenklatura.isEmpty(lhs.nomenklatura))
                return -1;
            else
                return lhs.nomenklatura.compareTo(rhs.nomenklatura);
        }

    };
}