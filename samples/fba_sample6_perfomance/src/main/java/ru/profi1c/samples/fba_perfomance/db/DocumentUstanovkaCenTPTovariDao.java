/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package ru.profi1c.samples.fba_perfomance.db;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

import ru.profi1c.engine.meta.TablePartDao;

/**
 * Менеджер для работы со строками табличной части 'Товары' документа 'Установка цен'
 * (создание, удаление, поиск)
 * @author ООО “Мобильные решения” (support@profi1c.ru)
 * 
 */
public class DocumentUstanovkaCenTPTovariDao extends TablePartDao<DocumentUstanovkaCenTPTovari> {

	public DocumentUstanovkaCenTPTovariDao(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, DocumentUstanovkaCenTPTovari.class);
	}

}
