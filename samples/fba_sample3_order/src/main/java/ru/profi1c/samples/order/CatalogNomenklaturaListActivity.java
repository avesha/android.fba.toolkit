package ru.profi1c.samples.order;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.SimpleCatalogListActivity;
import ru.profi1c.engine.meta.RowDao;
import ru.profi1c.samples.order.db.CatalogNomenklatura;

/*
 * Форма списка справочника «Номенклатура»
 */
public class CatalogNomenklaturaListActivity
        extends SimpleCatalogListActivity<CatalogNomenklatura> {
    private static final String TAG = CatalogNomenklaturaListActivity.class.getSimpleName();

    private MenuItem mSearchMenu;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fba_include_simple_list_layout);
        try {
            setContentListView();
        } catch (SQLException e) {
            new FbaRuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_nomenklatura_list, menu);

        //Кнопка поиска в залоговке
        mSearchMenu = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenu);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        // Get the search close button image view
        ImageView closeButton = (ImageView) mSearchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et = (EditText) findViewById(R.id.search_src_text);
                et.setText("");

                mSearchView.setQuery("", false);
                mSearchView.onActionViewCollapsed();
                mSearchMenu.collapseActionView();

                filterData(null);
            }
        });
        return true;
    }

    @Override
    protected List<CatalogNomenklatura> select(RowDao<CatalogNomenklatura> dao)
            throws SQLException {

		/*
         * Выбрать все записи (не установлен отбор первым параметром)
		 * отсортированные по наименованию
		 */
        return dao.select(null, CatalogNomenklatura.FIELD_NAME_DESCRIPTION);
    }

    /*
     * Установит отбор данных (по частичному совпадению представления)
     */
    protected void filterData(String query) {
        Log.i(TAG, "filterData, query: " + query);
        ListAdapter adapter = getListAdapter();
        if (adapter instanceof Filterable) {
            Filterable filterable = (Filterable) adapter;
            filterable.getFilter().filter(query);
        }
    }

}
