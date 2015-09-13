package ru.profi1c.samples.fba_perfomance;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.engine.util.IOHelper;
import ru.profi1c.samples.fba_perfomance.db.CatalogNomenklatura;
import ru.profi1c.samples.fba_perfomance.db.CatalogNomenklaturaDao;
import ru.profi1c.samples.fba_perfomance.db.DBHelper;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihod;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihodDao;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihodTPTovari;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihodTPTovariDao;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCen;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCenDao;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCenTPTovari;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCenTPTovariDao;
import ru.profi1c.samples.fba_perfomance.db.MetaHelper;
import ru.profi1c.samples.fba_perfomance.db.RegCeniNomenklaturi;
import ru.profi1c.samples.fba_perfomance.db.RegCeniNomenklaturiDao;
import ru.profi1c.samples.fba_perfomance.db.RegShtrihkod;
import ru.profi1c.samples.fba_perfomance.db.RegShtrihkodDao;

public class TestHandler implements Runnable {
    private static final String TAG = TestHandler.class.getSimpleName();

    public interface TestHandlerCallback {
        void onFinish(Profiler profiler);
    }

    public static final int ID_MESSAGE = 100;

    private final Handler mHandler;
    private final MeasureType mMeasureType;
    private final DBHelper mDBHelper;
    private final MetaHelper mMetaHelper;
    private final Profiler mProfiler;
    private final DaoManager mDaoManager;
    private final List<BaseTestAction> mActions;
    private final boolean mOptimize;
    private TestHandlerCallback mTestHandlerCallback;
    private RawSqlHandler mRawSqlHandler;
    private HashMap<String, CatalogNomenklatura> mMapNom = new HashMap<>();

    public static String formatDurationInHHMMSS(long millis) {
        if(millis < 1000) {
            int msec = (int) millis;
            int seconds = 0 ;
            int minutes = 0;
            int hours = 0;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", hours, minutes, seconds, msec);
        } else {
            int msec = (int)millis%1000;
            int seconds = (int) (millis / 1000) % 60 ;
            int minutes = (int) ((millis / (1000 * 60)) % 60);
            int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
            return String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", hours, minutes, seconds, msec);
        }
    }

    public TestHandler(Handler handler, MeasureType measureType, DBHelper dbHelper,
            MetaHelper metaHelper, boolean optimize) throws SQLException {
        mHandler = handler;
        mMeasureType = measureType;
        mDBHelper = dbHelper;
        mMetaHelper = metaHelper;
        mOptimize = optimize;
        mProfiler = new Profiler();
        mDaoManager = new DaoManager(mDBHelper);

        if(mOptimize) {
            mRawSqlHandler = new RawSqlHandler(handler, measureType, dbHelper, metaHelper, mProfiler );
        }

        mActions = new ArrayList<>();
        mActions.add(new BaseTestAction("Очистка базы данных") {
            @Override
            void doAction() throws SQLException {
                clearDb();
            }
        });
        mActions.add(new BaseTestAction("Создание номенклатуры") {
            @Override
            void doAction() throws SQLException {
                createNomenclatura();
            }
        });
        mActions.add(new BaseTestAction("Создание штрихкодов") {
            @Override
            void doAction() throws SQLException {
                createShtrihCode();
            }
        });
        mActions.add(new BaseTestAction("Создание документов 'Приход'") {
            @Override
            void doAction() throws SQLException {
                createDocPrihod();
            }
        });
        mActions.add(new BaseTestAction("Создание документов 'Установка цен'") {
            @Override
            void doAction() throws SQLException {
                createDocPrice();
            }
        });
        mActions.add(new BaseTestAction("Заполнение рег. 'Цены номенклатуры'") {
            @Override
            void doAction() throws SQLException {
                fullRegCeniNomenklaturi();
            }
        });
        mActions.add(new BaseTestAction("Срез первых по 1 номенклатуре'") {
            @Override
            void doAction() throws SQLException {
                regPriceGetFirst();
            }
        });
        mActions.add(new BaseTestAction("Срез последних по 1 номенклатуре'") {
            @Override
            void doAction() throws SQLException {
                regPriceGetLast();
            }
        });
        mActions.add(new BaseTestAction("Пометка на удаление всех док. 'Приход' (курсором)") {
            @Override
            void doAction() throws SQLException {
                deleteMarkDocPrihodByOneCursor();
            }
        });
        mActions.add(new BaseTestAction("Пометка на удаление всех док. 'Установка цен' (выборка)") {
            @Override
            void doAction() throws SQLException {
                deleteMarkDocPriceByOneSelect();
            }
        });
        mActions.add(new BaseTestAction("Удалить всю номенклатуру") {
            @Override
            void doAction() throws SQLException {
                deleteAllNomenclatura();
            }
        });
        mActions.add(new BaseTestAction("Очистить регистр штрихкодов") {
            @Override
            void doAction() throws SQLException {
                deleteAllShtrihCodes();
            }
        });
    }

    public void setTestHandlerCallback(TestHandlerCallback testHandlerCallback) {
        mTestHandlerCallback = testHandlerCallback;
    }

    @Override
    public void run() {
        sendMessage("Запуск теста по: " + mMeasureType.getDescription());
        for (BaseTestAction action : mActions) {
            action.run();
        }
        sendMessage("Тест завершен");
        if (mTestHandlerCallback != null) {
            mTestHandlerCallback.onFinish(mProfiler);
        }
    }

    private void sendMessage(String text) {
        Message msg = mHandler.obtainMessage(ID_MESSAGE, text);
        mHandler.sendMessage(msg);
    }

    private void clearDb() throws SQLException {
        mDBHelper.clearTables(mMetaHelper);
    }

    private void createNomenclatura() throws SQLException {
        final int lenCode = 9;
        final int count  = mMeasureType.getCountOfNumenclatura();
        sendMessage("кол-во: " + count);
        if (mOptimize) {
            //don't use dao, recommended only for very large amounts of data
            mRawSqlHandler.createNomenclatura();
        } else {
            CatalogNomenklaturaDao dao = mDaoManager.catalogNomenklaturaDao;
            for (int i = 0; i < count; i++) {
                int number = i + 1;
                CatalogNomenklatura item = dao.newItem();
                String strCode = dao.formatNumber(number, lenCode);
                item.setCode(strCode);
                item.setDescription("Номенклатура " + number);
                dao.create(item);
            }
        }
    }

    private void createShtrihCode() throws SQLException {
        final int lenCode = 9;
        final int count = mMeasureType.getCountOfNumenclatura() * mMeasureType.getCountCodes();
        sendMessage("кол-во: " + count);
        if (mOptimize) {
            //don't use dao, recommended only for very large amounts of data
            mRawSqlHandler.createShtrihCode();
        } else {
            RegShtrihkodDao dao = mDaoManager.regShtrihkodDao;
            for (int i = 0; i < mMeasureType.getCountOfNumenclatura(); i++) {
                int number = i + 1;

                for (int j = 0; j < mMeasureType.getCountCodes(); j++) {
                    String shtrichCode = String.format("AAA-00%d%d", i, j);

                    RegShtrihkod row = dao.newItem();
                    row.shtrihkod = shtrichCode;
                    row.nomenklatura = findNomenclatura("Номенклатура " + number);
                    dao.create(row);
                }
            }

        }
    }

    private void createDocPrihod() throws SQLException {
        final int lenCode = 9;
        final int count = mMeasureType.getCountDocPrihod();
        final int countPos = mMeasureType.getCountPosOfDocPrihod();
        sendMessage(String.format("кол-во: %d, записей в ТЧ: %d", count, countPos));

        if (mOptimize) {
            //don't use dao, recommended only for very large amounts of data
            mRawSqlHandler.createDocPrihod();
        } else {
            DocumentPrihodDao docDao = mDaoManager.documentPrihodDao;
            DocumentPrihodTPTovariDao tpDao = mDaoManager.documentPrihodTPTovariDao;
            for(int i = 0; i < count; i++){
                int number = i + 1;
                DocumentPrihod doc =  docDao.newItem();
                String strCode = docDao.formatNumber(number, lenCode);
                doc.setNumber(strCode);
                doc.setDate(new Date(System.currentTimeMillis()));
                doc.setPosted(true);

                docDao.create(doc);

                for (int j = 0; j < countPos; j++) {
                    DocumentPrihodTPTovari row = tpDao.newItem(doc, j + 1);
                    row.nomenklatura = findNomenclatura("Номенклатура " + number);
                    row.kolichestvo = (10 + i) * (j + 1);
                    tpDao.create(row);
                }
            }
        }
    }

    private void createDocPrice() throws SQLException {
        final int lenCode = 9;
        final int count = mMeasureType.getCountDocPrice();
        final int countPos = mMeasureType.getCountPosOfDocPrice();
        sendMessage(String.format("кол-во: %d, записей в ТЧ: %d", count, countPos));

        if (mOptimize) {
            //don't use dao, recommended only for very large amounts of data
            mRawSqlHandler.createDocPrice();
        } else {
            DocumentUstanovkaCenDao docDao = mDaoManager.documentUstanovkaCenDao;
            DocumentUstanovkaCenTPTovariDao tpDao = mDaoManager.dcumentUstanovkaCenTPTovariDao;
            for(int i = 0; i < count; i++){
                int number = i + 1;
                DocumentUstanovkaCen doc =  docDao.newItem();
                String strCode = docDao.formatNumber(number, lenCode);
                doc.setNumber(strCode);
                doc.setDate(new Date(System.currentTimeMillis()));
                doc.setPosted(true);

                docDao.create(doc);

                for(int j=0; j < countPos; j++){
                    DocumentUstanovkaCenTPTovari row = tpDao.newItem(doc, j+1);
                    row.nomenklatura = findNomenclatura("Номенклатура " + number);
                    row.cena = (10 + i) * j;
                    tpDao.create(row);
                }
            }
        }
    }

    private void fullRegCeniNomenklaturi() throws SQLException {
        final int count = mMeasureType.getCountOfNumenclatura();
        sendMessage("кол-во: " + count);
        if (mOptimize) {
            //don't use dao, recommended only for very large amounts of data
            mRawSqlHandler.fullRegCeniNomenklaturi();
        } else {
            RegCeniNomenklaturiDao dao = mDaoManager.regCeniNomenklaturiDao;
            Date period = DateHelper.beginOfDay(new Date(System.currentTimeMillis()));
            for (int i = 0; i < count; i++) {
                int number = i + 1;
                RegCeniNomenklaturi row = dao.newItem();
                row.setPeriod(period);
                row.cena = 1000d / (i + 1);
                row.nomenklatura = findNomenclatura("Номенклатура " + number);
                dao.create(row);
            }
        }
    }

    private void regPriceGetFirst() throws SQLException {
        CatalogNomenklatura sku = findNomenclatura("Номенклатура " + 1);
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegCeniNomenklaturi.FIELD_NAME_NOMENKLATURA, sku);
        Date period = DateHelper.beginOfDay(new Date(System.currentTimeMillis()));

        RegCeniNomenklaturi row = mDaoManager.regCeniNomenklaturiDao.getFirst(period, filter);
        Dbg.i(TAG, "regPriceGetFirst: sku = %s, price = %s, period = %s", row.nomenklatura.toString(), String.valueOf(row.cena), row.getPeriod().toString());
    }

    private void regPriceGetLast() throws SQLException {
        CatalogNomenklatura sku = findNomenclatura("Номенклатура " + 2);
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(RegCeniNomenklaturi.FIELD_NAME_NOMENKLATURA, sku);
        Date period = DateHelper.beginOfDay(new Date(System.currentTimeMillis()));

        RegCeniNomenklaturi row = mDaoManager.regCeniNomenklaturiDao.getLast(period, filter);
        Dbg.i(TAG, "regPriceGetLast: sku = %s, price = %s, period = %s", row.nomenklatura.toString(), String.valueOf(row.cena), row.getPeriod().toString());
    }

    private void deleteMarkDocPrihodByOneCursor() throws SQLException {

        if (mOptimize) {
            mRawSqlHandler.deleteMarkDocPrihodForAll();
        } else {
            Cursor c = null;
            try {
                c = mDaoManager.documentPrihodDao.selectCursor();
                if(c!=null){
                    sendMessage("кол-во: " + c.getCount());
                    int index = c.getColumnIndex(DocumentPrihod.FIELD_NAME_REF);
                    while(c.moveToNext()) {
                        String id = c.getString(index);
                        DocumentPrihod doc = mDaoManager.documentPrihodDao.queryForId(id);
                        doc.setDeletionMark(true);
                        mDaoManager.documentPrihodDao.update(doc);
                    }
                }
            } finally {
                IOHelper.close(c);
            }
        }
    }

    private void deleteMarkDocPriceByOneSelect() throws SQLException {

        if (mOptimize) {
            mRawSqlHandler.deleteMarkDocPriceForAll();
        } else {
            List<DocumentUstanovkaCen> lst = mDaoManager.documentUstanovkaCenDao.select();
            sendMessage("кол-во: " + lst.size());
            for(DocumentUstanovkaCen doc: lst) {
                doc.setDeletionMark(true);
                mDaoManager.documentUstanovkaCenDao.update(doc);
            }
        }
    }

    private void deleteAllNomenclatura() throws SQLException {
        mDBHelper.clearTable(CatalogNomenklatura.class);
    }

    private void deleteAllShtrihCodes() throws SQLException {
        mDBHelper.clearTable(RegShtrihkod.class);
    }

    private CatalogNomenklatura findNomenclatura(String desc) throws SQLException {
        CatalogNomenklatura ref = mMapNom.get(desc);
        if(ref == null) {
            ref = mDaoManager.catalogNomenklaturaDao.findByDescription(desc);
            mMapNom.put(desc, ref);
        }
        return ref;
    }
    private abstract class BaseTestAction implements Runnable {

        private final String mActionDesc;

        abstract void doAction() throws SQLException;

        BaseTestAction(String actionDesc) {
            mActionDesc = actionDesc;
        }

        @Override
        public void run() {
            sendMessage("Begin: " + mActionDesc);
            mProfiler.start(mActionDesc);
            try {
                doAction();
            } catch (SQLException e) {
                sendMessage("Error: " + e.getMessage());
            } catch(RuntimeException e) {
                sendMessage("Error: " + e.getMessage());
            }
            long time = mProfiler.stop(mActionDesc);
            final String endAction = String.format("End: %s", mActionDesc);
            Dbg.i(TAG, String.format("%s, time = %d msec", endAction, time));
            String msg = String.format("%s, time = %s sec", endAction, formatDurationInHHMMSS(time));
            sendMessage(msg);
        }
    }

}
