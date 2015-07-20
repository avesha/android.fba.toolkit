package ru.profi1c.engine.app;

import java.sql.SQLException;
import java.util.List;

import ru.profi1c.engine.R;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.RowDao;

/**
 * Простой список документов, отображает все записи. Поля: «Проведен», «Дата» и
 * «Номер»
 * 
 * @param <T>
 */
public abstract class SimpleDocumentListActivity<T extends Document> extends
		BaseRowListActivity<T> {

	@Override
	protected int getHeaderLayoutResource() {
		return R.layout.fba_simple_document_item_header;
	}

	@Override
	protected int getRowLayoutResource() {
		return R.layout.fba_simple_document_item;
	}

	@Override
	protected String[] getFieldNames() {
		return new String[] { Document.FIELD_NAME_POSTED,
				Document.FIELD_NAME_DATE, Document.FIELD_NAME_NUMBER };
	}

	@Override
	protected int[] getFieldIds() {
		return new int[] { R.id.fba_posted, R.id.fba_data, R.id.fba_number };
	}

	@Override
	protected List<T> select(RowDao<T> dao) throws SQLException {
		return dao.select();
	}

}
