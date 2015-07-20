package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.sample.app2.db.CatalogKontragenti;
import com.sample.app2.db.CatalogKontragentiDao;
import com.sample.app2.db.EnumStavkiNDS;
import com.sample.app2.db.EnumUsloviyaSkidkiNacenki;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.meta.IPresentation;
import ru.profi1c.engine.widget.PresentationAdapter;

/**
 * Активизит для тестирования PresentationAdapter, не включать в релиз
 */
public class TAPresentationAdapter extends FbaDBActivity {

    private Spinner mSpinStavkaNds;
    private Spinner mSpinSkidki;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TAPresentationAdapter.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ta_presentation_adapter);
        setTitle(TestActionShow.DESCRIPTION);
        try {
            init();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void init() throws SQLException {

        //1. выбор значения перечисления
        mSpinStavkaNds = (Spinner) findViewById(R.id.spinEnum1);
        mSpinStavkaNds.setAdapter(new PresentationAdapter(this, EnumStavkiNDS.values()));
        mSpinStavkaNds.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EnumStavkiNDS value = (EnumStavkiNDS) parent.getAdapter().getItem(position);
                showToast(String.format("Выбранное значение: %s", value));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinSkidki = (Spinner) findViewById(R.id.spinEnum2);
        mSpinSkidki.setPrompt("Условия скидки наценки");
        mSpinSkidki.setAdapter(new PresentationAdapter(this, EnumUsloviyaSkidkiNacenki.values()));
        mSpinSkidki.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EnumUsloviyaSkidkiNacenki value = (EnumUsloviyaSkidkiNacenki) parent.getAdapter()
                        .getItem(position);
                showToast(String.format("Выбранное значение: %s", value));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //2. Выбор элемента справочника.
        //Для отображения элементов в выпадающем списке используется кастомный макет (layout).
        //Показан обработчик выбора элемента.

        final Spinner spi2 = (Spinner) findViewById(R.id.spinCatalog1);

        CatalogKontragentiDao dao = getHelper().getDao(CatalogKontragenti.class);
        List<IPresentation> lst = dao.toPresentationList(dao.select());

        //свой макет
        PresentationAdapter adapter = new PresentationAdapter(this, R.layout.test_row_layout,
                R.id.textView1, lst);
        adapter.getFilter().filter("ооо");

        spi2.setAdapter(adapter);
        spi2.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                CatalogKontragenti catalog = (CatalogKontragenti) spi2.getAdapter()
                        .getItem(position);
                showToast(String.format("Выбранное значение: %s", catalog));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public EnumStavkiNDS getSelectedStavkaNds() {
        return (EnumStavkiNDS) mSpinStavkaNds.getSelectedItem();
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "PresentationAdapter";
        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
