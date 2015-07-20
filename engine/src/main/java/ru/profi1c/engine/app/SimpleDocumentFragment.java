package ru.profi1c.engine.app;

import java.sql.SQLException;
import java.util.Date;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.DocumentDao;
import ru.profi1c.engine.meta.RefDao;

/**
 * Простая "форма документа", доступны для редактирования 'Номер' и 'Дата'
 * (время редактируется отдельно)
 *
 * @param <T>
 */
public abstract class SimpleDocumentFragment<T extends Document> extends BaseRefFragment<T> {

    /**
     * Длина номера документа по умолчанию
     */
    private static final int DEF_CODE_LENGTH = Const.DEFAULT_DOCUMENT_NUMBER_LENGTH;

    @Override
    protected int getResIdLayout() {
        return R.layout.fba_simple_document_fragment;
    }

    @Override
    protected String[] getFields() {
        return new String[]{Document.FIELD_NAME_DATE, Document.FIELD_NAME_DATE,
                Document.FIELD_NAME_NUMBER};
    }

    @Override
    protected int[] getIds() {
        return new int[]{R.id.fba_data, R.id.fba_time, R.id.fba_number};
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildTextAutoHint(true);
    }

    @Override
    protected void onRefresh(RefDao<T> dao, T obj, boolean isNew) {

        if (isNew) {
            try {

                DocumentDao<T> docDao = (DocumentDao<T>) dao;
                String number = getNewNumber(docDao);
                obj.setNumber(number);
                obj.setDate(new Date(System.currentTimeMillis()));
                obj.setPosted(false);

            } catch (SQLException e) {
                Dbg.printStackTrace(e);
            }
        }

    }

    protected String getNewNumber(DocumentDao<T> docDao) throws SQLException {

        int lenCode = docDao.getNumberLength();
        if (lenCode == 0) {
            lenCode = DEF_CODE_LENGTH;
        }

        int newCode = docDao.getNextNumber();
        return docDao.formatNumber(newCode, lenCode);

    }
}
