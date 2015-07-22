package ru.profi1c.samples.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.SimpleDocumentListActivity;
import ru.profi1c.engine.meta.RowDao;
import ru.profi1c.samples.order.db.CatalogKontragentiDao;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelya;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelyaDao;

/*
 * Список документов «Заказ покупателя»
 */
public class DocZakazListActivity extends SimpleDocumentListActivity<DocumentZakazPokupatelya> {
    private static final String TAG = DocZakazListActivity.class.getSimpleName();

    private DocumentZakazPokupatelyaDao mDocDao;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.doc_zakaz_list);
        try {
            init();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }

        //Подписка на уведомления
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_UPDATE_ITEM);
        intentFilter.addCategory(Const.CATEGORY_CHANGED_DOC_ZAKAZ);
        registerReceiver(UpdateItemReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(UpdateItemReceiver);
    }

    /*
     * Возвращает идентификатор макета, используемый для заголовков колонок
     */
    @Override
    protected int getHeaderLayoutResource() {
        return R.layout.doc_zakaz_list_header;
    }

    /*
     * Возвращает идентификатор макета, используемый для для строк списка
     */
    @Override
    protected int getRowLayoutResource() {
        return R.layout.doc_zakaz_list_row;
    }

    /*
     * Поля класса (реквизиты объекта в терминах 1с) отображаемые в списке
     */
    @Override
    protected String[] getFieldNames() {
        return new String[]{DocumentZakazPokupatelya.FIELD_NAME_NUMBER,
                DocumentZakazPokupatelya.FIELD_NAME_KONTRAGENT,
                DocumentZakazPokupatelya.FIELD_NAME_SUMMA};
    }

    /*
     * Идентификаторы view-элементов для отражения значений полей
     */
    @Override
    protected int[] getFieldIds() {
        return new int[]{R.id.tvNumber, R.id.tvKontragent, R.id.tvSumma};
    }

    @Override
    protected List<DocumentZakazPokupatelya> select(RowDao<DocumentZakazPokupatelya> dao)
            throws SQLException {

        // Обновление ссылочного поля «Контрагент»
        CatalogKontragentiDao kontrDao = new CatalogKontragentiDao(getConnectionSource());

        List<DocumentZakazPokupatelya> lst = super.select(dao);
        for (DocumentZakazPokupatelya doc : lst) {
            kontrDao.refresh(doc.kontragent);
        }

        return lst;

        // 03. Выбрать 3 не проведённых документа, у которых дата >= 01/05/13
        // QueryBuilder<DocumentZakazPokupatelya, String> builder = dao
        // .queryBuilder();
        // builder.where().ge(DocumentZakazPokupatelya.FIELD_NAME_DATE,
        // DateHelper.date(2013, Calendar.MAY, 1));
        // builder.orderBy(DocumentZakazPokupatelya.FIELD_NAME_DATE, false);
        // builder.limit(3L);
        // return dao.query(builder.prepare());

        // 02. выбрать только проведенные
        // HashMap<String,Object> filter = new HashMap<String, Object>();
        // filter.put(DocumentZakazPokupatelya.FIELD_NAME_POSTED, true);
        // return dao.select(filter);

        // 01. Сортировка по дате в обратном порядке
        // return dao. select(null, DocumentZakazPokupatelya.FIELD_NAME_DATE +
        // " DESC");
    }

    private void init() throws SQLException {

        Button btn = (Button) findViewById(R.id.btnOne);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newDoc();
            }
        });

        setContentListView();

        ListView list = getListView();
        list.setLongClickable(true);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        list.setOnItemClickListener(onDocItemClickListener);
        list.setOnItemLongClickListener(mOnDocItemLongClickListener);

        // DAO-менеджер (используется для удаления документа)
        mDocDao = new DocumentZakazPokupatelyaDao(getConnectionSource());
    }

    /*
     * Обработчик выбора элемента списка
     */
    private OnItemClickListener onDocItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            DocumentZakazPokupatelya doc =
                    (DocumentZakazPokupatelya) getListAdapter().getItem(position);
            Log.i(TAG, "Выбран документ, ref = " + doc.getRef());

        }
    };

    /*
     * Контекстное меню для отмеченного элемента в списке (доступно на Android
     * 11 и выше)
     */
    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
            mode = null;
            ListView list = getListView();
            list.clearChoices();
            list.requestLayout();
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(R.string.menu_title_actions);
            mode.getMenuInflater().inflate(R.menu.activity_zakaz_list, menu);
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int position = getListView().getCheckedItemPosition();
            DocumentZakazPokupatelya doc =
                    (DocumentZakazPokupatelya) getListAdapter().getItem(position);

            int id = item.getItemId();
            switch (id) {
                case R.id.menu_delete:
                    deleteDoc(doc);
                    break;
                case R.id.menu_edit:
                    editDoc(doc);
                    break;
            }

            mode.finish();
            return true;
        }
    };

    /*
     * По долгому нажатию на элемент в списке – открываем панель действий
     */
    private OnItemLongClickListener mOnDocItemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            if (mActionMode != null) {
                mActionMode.finish();
            }

            ListView list = (ListView) parent;
            list.clearChoices();
            list.setItemChecked(position, true);

            mActionMode = startActionMode(modeCallBack);
            return true;
        }
    };

    /*
     * Удалить документ, заново создать адаптер и установить его как источник
     * для списка
     */
    private void deleteDoc(DocumentZakazPokupatelya doc) {
        try {
            mDocDao.delete(doc);
            refreshListData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*
     * создать адаптер и установить его как источник для списка
     */
    private void refreshListData() {
        try {
            setListAdapter(createAdapter());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Изменить документ
     */
    protected void editDoc(DocumentZakazPokupatelya doc) {
        Intent i = new Intent(this, DocZakazItemActivity.class);
        i.putExtra(DocZakazItemActivity.EXTRA_REF, doc.getRef().toString());
        startActivity(i);
    }

    /*
     * Новый документ
     */
    protected void newDoc() {
        Intent i = new Intent(this, DocZakazItemActivity.class);
        startActivity(i);
    }


    /*
     * Получатель широковещательного уведомления при изменении документа из списка
     */
    private BroadcastReceiver UpdateItemReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();
            if (extras != null) {
                String ref = extras.getString(DocZakazItemActivity.EXTRA_REF);
                Log.i(TAG, "Изменен документ, ref = " + ref);

                refreshListData();
            }

        }

    };
}
