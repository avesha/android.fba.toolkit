package ru.profi1c.samples.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import java.sql.SQLException;

import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.SimpleDocumentFragment;
import ru.profi1c.engine.meta.DBOpenHelper;
import ru.profi1c.engine.meta.DocumentDao;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.RefDao;
import ru.profi1c.engine.widget.FieldPresentationSpinner;
import ru.profi1c.samples.order.db.CatalogDogovoriKontragentov;
import ru.profi1c.samples.order.db.CatalogDogovoriKontragentovDao;
import ru.profi1c.samples.order.db.CatalogKontragenti;
import ru.profi1c.samples.order.db.CatalogValyuti;
import ru.profi1c.samples.order.db.CatalogValyutiDao;
import ru.profi1c.samples.order.db.Constants;
import ru.profi1c.samples.order.db.ConstantsDao;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelya;

/*
 * Страница «Информация»: реквизиты документа «Заказ покупателя»
 * на основе вспомогательного класса SimpleDocumentFragment
 */
public class DocZakazFragment extends SimpleDocumentFragment<DocumentZakazPokupatelya> {

    // Длина и префикс для номера документа
    private static final int DOC_NUMBER_LENGTH = 11;
    private static final String DOC_NUMERATOR = "AND-";

    private int mLastKontragetPosition;
    private CatalogDogovoriKontragentovDao mDogovorDao;
    private FieldPresentationSpinner mSpinKontragent, mSpinDogovor;

    public static DocZakazFragment newInstance(Ref ref) {
        DocZakazFragment fragment = new DocZakazFragment();
        if (ref != null) {
            fragment.setArguments(DocZakazFragment.toBundle(ref));
        }
        return fragment;
    }

    @Override
    protected int getResIdLayout() {
        return R.layout.doc_zakaz_item;
    }

    @Override
    protected String[] getFields() {

        return new String[]{DocumentZakazPokupatelya.FIELD_NAME_DATE,
                DocumentZakazPokupatelya.FIELD_NAME_DATE,
                DocumentZakazPokupatelya.FIELD_NAME_KONTRAGENT,
                DocumentZakazPokupatelya.FIELD_NAME_DOGOVOR_KONTRAGENTA,
                DocumentZakazPokupatelya.FIELD_NAME_SKLAD,
                DocumentZakazPokupatelya.FIELD_NAME_TIP_CEN,
                DocumentZakazPokupatelya.FIELD_NAME_DATA_OTGRUZKI,
                DocumentZakazPokupatelya.FIELD_NAME_KOMMENTARII};
    }

    @Override
    protected int[] getIds() {
        return new int[]{R.id.date, R.id.time, R.id.kontragent, R.id.dogovorKontragenta, R.id.sklad,
                R.id.tipCen, R.id.dataOtgruzki, R.id.kommentarii};
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            initData();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        initControl(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //переопределить обработчик по умолчанию
        mLastKontragetPosition = mSpinKontragent.getSelectedItemPosition();
        mSpinKontragent.setOnItemSelectedListener(onKontragentSelectListener);
    }

    private void initData() throws SQLException {
        mDogovorDao = new CatalogDogovoriKontragentovDao(getConnectionSource());
    }

    private void initControl(View root) {
        setChildSpinAutoPrompt(true);

        mSpinKontragent = (FieldPresentationSpinner) root.findViewById(R.id.kontragent);
        mSpinDogovor = (FieldPresentationSpinner) root.findViewById(R.id.dogovorKontragenta);
    }

    /*
     * Обработчик выбора контрагента из выпадающего списка. Установка договора
     * по умолчанию
     */
    private OnItemSelectedListener onKontragentSelectListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // Если выбрана группа – восстановим предыдущее значение
            CatalogKontragenti newKontragent =
                    (CatalogKontragenti) mSpinKontragent.getSelectedItem();
            if (newKontragent.isFolder()) {
                mSpinKontragent.setSelection(mLastKontragetPosition);
                Toast.makeText(getActivity(), R.string.msg_not_select_folder, Toast.LENGTH_SHORT)
                     .show();
                return;
            }

            mLastKontragetPosition = mSpinKontragent.getSelectedItemPosition();
            CatalogDogovoriKontragentov newDogovor = mDogovorDao.getLast(newKontragent);

            //установить договор по умолчанию
            DocumentZakazPokupatelya doc = getObject();
            doc.dogovorKontragenta = newDogovor;

            // FBA версии 1.0.4.002: Вручную устанавливать значение при наличии
            // обработчика больше нет необходимости
            // doc.kontragent = newKontragent;

            // В списке выбора отобразим все договора по выбранному контрагенту
            mSpinDogovor.build(doc, DocumentZakazPokupatelya.FIELD_NAME_DOGOVOR_KONTRAGENTA,
                               getHelper());

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    @Override
    protected String getNewNumber(DocumentDao<DocumentZakazPokupatelya> docDao)
            throws SQLException {

        //Переопределена установка нового номера – устанавливается префикс
        int numerator = docDao.getNextNumber(DOC_NUMERATOR);
        String strNumber = docDao.formatNumber(DOC_NUMERATOR, numerator, DOC_NUMBER_LENGTH);
        return strNumber;
    }

    @Override
    protected void onRefresh(RefDao<DocumentZakazPokupatelya> dao, DocumentZakazPokupatelya obj,
            boolean isNew) {

        //Для нового автоматом  установит номер и дату
        super.onRefresh(dao, obj, isNew);

        //установить прочие реквизиты нового документа
        if (isNew) {
            try {
                DBOpenHelper helper = getHelper();
                ConstantsDao costDao = helper.getDao(Constants.class);
                Constants constants = costDao.read();

                obj.organizaciya = constants.osnovnayaOrganizaciya;
                obj.tipCen = constants.osnovnoiTipCenProdazhi;

                //Валюта по умолчанию – рубли
                CatalogValyutiDao valutDao = helper.getDao(CatalogValyuti.class);
                obj.valyutaDokumenta = valutDao.findByCode("643");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
