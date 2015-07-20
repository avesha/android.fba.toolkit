/*
 * This text is generated automatically, do not delete it. 'FBA Toolkit', www.profi1c.ru
 */
package com.sample.app2.db;

import java.util.ArrayList;
import java.util.List;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.ConstTable;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.MetadataHelper;
import ru.profi1c.engine.meta.Table;
import ru.profi1c.engine.meta.TableInfReg;

/**
 * Класс-помощник для работы с метаданными объектов (соответствие объектов 1С и классов Java)
 * @author Сидоров Сидор Петрович (sidor_sidoroff@mail.ru)
 *
 */
public class MetaHelper extends MetadataHelper {

	//Singleton variant: Double Checked Locking & volatile 
	private static volatile MetaHelper instance;

	private static Class<? extends ConstTable> constTableClass;
	private static List<Class<? extends Catalog>> lstCatalogClasses;
	private static List<Class<? extends Document>> lstDocumentClasses;
	private static List<Class<? extends TableInfReg>> lstRegClasses;
	private static List<Class<? extends Table>> lstExtTableClasses;
	
	static{
		//константы
		constTableClass = Constants.class;

		//справочники
		lstCatalogClasses = new ArrayList<Class<? extends Catalog>>();
		lstCatalogClasses.add(CatalogPolzovateli.class);
		lstCatalogClasses.add(CatalogValyuti.class);
		lstCatalogClasses.add(CatalogNomenklatura.class);
		lstCatalogClasses.add(CatalogEdiniciIzmereniya.class);
		lstCatalogClasses.add(CatalogNomenklaturnieGruppi.class);
		lstCatalogClasses.add(CatalogKlassifikatorEdinicIzmereniya.class);
		lstCatalogClasses.add(CatalogVneshnieObrabotki.class);
		lstCatalogClasses.add(CatalogKlassifikatorStranMira.class);
		lstCatalogClasses.add(CatalogKontragenti.class);
		lstCatalogClasses.add(CatalogStatiZatrat.class);
		lstCatalogClasses.add(CatalogNomeraGTD.class);
		lstCatalogClasses.add(CatalogVidiNomenklaturi.class);
		lstCatalogClasses.add(CatalogHranilischeDopolnitelnoiInformacii.class);
		lstCatalogClasses.add(CatalogCenovieGruppi.class);
		lstCatalogClasses.add(CatalogUsloviyaProdazh.class);
		lstCatalogClasses.add(CatalogSeriinieNomera.class);
		lstCatalogClasses.add(CatalogDogovoriKontragentov.class);
		lstCatalogClasses.add(CatalogOrganizacii.class);
		lstCatalogClasses.add(CatalogFizicheskieLica.class);
		lstCatalogClasses.add(CatalogSkladi.class);
		lstCatalogClasses.add(CatalogHarakteristikiNomenklaturi.class);
		lstCatalogClasses.add(CatalogSeriiNomenklaturi.class);
		lstCatalogClasses.add(CatalogKachestvo.class);
		lstCatalogClasses.add(CatalogPodrazdeleniya.class);
		lstCatalogClasses.add(CatalogInformacionnieKarti.class);
		lstCatalogClasses.add(CatalogTipiCenNomenklaturi.class);
		lstCatalogClasses.add(CatalogMestaHraneniya.class);
				
		//документы
		lstDocumentClasses = new ArrayList<Class<? extends Document>>();
		lstDocumentClasses.add(DocumentVozvratTovarovOtPokupatelya.class);
		lstDocumentClasses.add(DocumentZakazPokupatelya.class);
		lstDocumentClasses.add(DocumentVnutrenniiZakaz.class);
		lstDocumentClasses.add(DocumentSobitie.class);
		lstDocumentClasses.add(DocumentPrihodniiOrderNaTovari.class);
		lstDocumentClasses.add(DocumentAvansoviiOtchet.class);
		
		//регистры сведений
		lstRegClasses = new ArrayList<Class<? extends TableInfReg>>();
		lstRegClasses.add(RegAdresniiKlassifikator.class);
		lstRegClasses.add(RegKursiValyut.class);
		lstRegClasses.add(RegMestaHraneniyaNomenklaturi.class);
		lstRegClasses.add(RegCeniNomenklaturi.class);
		
		//внешние таблицы
		lstExtTableClasses = new ArrayList<Class<? extends Table>>();
		lstExtTableClasses.add(ExTableGeoDannie.class);
		lstExtTableClasses.add(ExTableTestTablica2.class);

	}
	
	private MetaHelper(){}
	
	public static MetaHelper getInstance() {
		MetaHelper localInstance = instance;
		
		if (localInstance == null) {
			synchronized (MetaHelper.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new MetaHelper();
				}
			}
		}
		
		return localInstance;
	}

	@Override
	public Class<? extends ConstTable> getConstClass() {
		return constTableClass;
	}

	@Override
	public List<Class<? extends Catalog>> getCatalogClasses() {
		return lstCatalogClasses;
	}

	@Override
	public List<Class<? extends Document>> getDocumentClasses() {
		return lstDocumentClasses;
	}

	@Override
	public List<Class<? extends TableInfReg>> getRegClasses() {
		return lstRegClasses;
	}

	@Override
	public List<Class<? extends Table>> getExtTableClasses() {
		return lstExtTableClasses;
	}
}
