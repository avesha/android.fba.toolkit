package ru.profi1c.samples.fba_perfomance;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.text.TextUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.Document;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.meta.Row;
import ru.profi1c.engine.meta.Table;
import ru.profi1c.engine.meta.TableInfReg;
import ru.profi1c.engine.meta.TablePart;
import ru.profi1c.engine.util.DateHelper;
import ru.profi1c.samples.fba_perfomance.db.CatalogNomenklatura;
import ru.profi1c.samples.fba_perfomance.db.DBHelper;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihod;
import ru.profi1c.samples.fba_perfomance.db.DocumentPrihodTPTovari;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCen;
import ru.profi1c.samples.fba_perfomance.db.DocumentUstanovkaCenTPTovari;
import ru.profi1c.samples.fba_perfomance.db.MetaHelper;
import ru.profi1c.samples.fba_perfomance.db.RegCeniNomenklaturi;
import ru.profi1c.samples.fba_perfomance.db.RegShtrihkod;

public class RawSqlHandler {

    private final Handler mHandler;
    private final MeasureType mMeasureType;
    private final DBHelper mDBHelper;
    private final MetaHelper mMetaHelper;
    private final Profiler mProfiler;
    private final Random rnd = new Random();

    private SQLiteDatabase db;
    private SQLiteStatement mStmFindMomenclatura;

    final HashMap<String, String> mapNom = new HashMap<>();

    RawSqlHandler(Handler handler, MeasureType measureType, DBHelper dbHelper,
            MetaHelper metaHelper, Profiler profiler) {

        mHandler = handler;
        mMeasureType = measureType;
        mDBHelper = dbHelper;
        mMetaHelper = metaHelper;
        mProfiler = profiler;

        init();
    }

    private void init() {
        db = mDBHelper.getWritableDatabase();
        initFindNomenclaturaStatement();
    }

    void createNomenclatura() {
        String sql = String.format("INSERT INTO %s (%s,%s,%s) VALUES (%s,%s,%s)",
                                   CatalogNomenklatura.TABLE_NAME,
                genRowColumns(), genRefColumns(), genCatalogColums(),
                genRowColumnsParams(), genRefColumnsParams(), genCatalogColumnsParams());

        SQLiteStatement statement = db.compileStatement(sql);

        try {

            optimiseBeginInsert(db);
            db.beginTransaction();

            int lenCode = 9;
            for (int i = 0; i < mMeasureType.getCountOfNumenclatura(); i++) {
                int number = i + 1;
                String strCode = formatNumber(number, lenCode);

                statement.clearBindings();
                int ind = 0;
                ind = bindRowValues(statement, ind,  true);
                ind = bindRefValues(statement, ind, generateRef());
                bindCatalogValues(statement, ind, strCode, "Номенклатура " + number);
                statement.executeInsert();
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            optimizeEndInsert(db);
        }
    }

    void createShtrihCode() {
        String sql = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES (%s,%s,%s,%s)",
                RegShtrihkod.TABLE_NAME,
                genRowColumns(), genTableColums(), genTableInfRegColums(), genRegShtrihkodColums(),
                genRowColumnsParams(), genTableColumnsParams(), genTableInfRegColumnsParams(), genRegShtrihkodColumnsParams());

        SQLiteStatement statement = db.compileStatement(sql);

        try {

            optimiseBeginInsert(db);
            db.beginTransaction();

            for(int i = 0; i < mMeasureType.getCountOfNumenclatura(); i++ ){
                int number = i + 1;
                String nomDesc = "Номенклатура " + number;
                String idNom = findNomenclaturaId(nomDesc);

                for(int j = 0; j < mMeasureType.getCountCodes(); j++) {
                    String shtrichCode = String.format("AAA-00%d%d", i,j);
                    String recordKey = idNom + shtrichCode;

                    statement.clearBindings();
                    int ind = 0;
                    ind = bindRowValues(statement, ind, true);
                    ind = bindTableValues(statement, ind, recordKey);
                    ind = bindTableInfRegValues(statement, ind, null);
                    bindRegShtrihkodValues(statement,ind, idNom, shtrichCode);
                    statement.executeInsert();
                }
            }
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            optimizeEndInsert(db);
        }

    }

    void createDocPrihod() {
        String sqlDoc = String.format("INSERT INTO %s (%s,%s,%s) VALUES (%s,%s,%s)",
            DocumentPrihod.TABLE_NAME,
            genRowColumns(), genRefColumns(), genDocumentColums(),
            genRowColumnsParams(), genRefColumnsParams(), genDocumentColumnsParams());

        SQLiteStatement stmDoc = db.compileStatement(sqlDoc);

        String sqlTpDoc = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES (%s,%s,%s,%s)",
                DocumentPrihodTPTovari.TABLE_NAME,
                genRowColumns(), genTableColums(), genTablePartColumns(), genTablePartPrihodTovariColumns(),
                genRowColumnsParams(), genTableColumnsParams(), genTablePartColumnsParams(), genTablePartPrihodTovariColumnsParams());

        SQLiteStatement stmTp = db.compileStatement(sqlTpDoc);

        try {

            optimiseBeginInsert(db);
            db.beginTransaction();

            int lenCode = 9;
            for(int i = 0; i < mMeasureType.getCountDocPrihod(); i++ ){
                int number = i + 1;
                String strNumber = formatNumber(number, lenCode);
                String idDoc = generateRef();

                stmDoc.clearBindings();
                int ind = 0;
                ind = bindRowValues(stmDoc,ind, true);
                ind = bindRefValues(stmDoc, ind,  idDoc);
                bindDocumentValues(stmDoc, ind, new Date(System.currentTimeMillis()), strNumber, true);
                stmDoc.executeInsert();

                for(int j = 0; j < mMeasureType.getCountPosOfDocPrihod(); j++ ){
                    int lineNumber = j + 1;
                    String nomDesc = "Номенклатура " + lineNumber;
                    String idNom = findNomenclaturaId(nomDesc);
                    String recordKey = String.format("%s-%s", lineNumber, idDoc);

                    stmTp.clearBindings();
                    int ind2 = 0;
                    ind2 = bindRowValues(stmTp, ind2, true);
                    ind2 = bindTableValues(stmTp, ind2, recordKey);
                    ind2 = bindTableTartValues(stmTp, ind2, lineNumber);
                    bindTablePartPrihodTovariValues(stmTp, ind2, idDoc, idNom, (10 + i) * j);
                    stmTp.executeInsert();
                }
            }
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            optimizeEndInsert(db);
        }

    }

    void createDocPrice() {
        String sqlDoc = String.format("INSERT INTO %s (%s,%s,%s) VALUES (%s,%s,%s)",
                DocumentUstanovkaCen.TABLE_NAME,
                genRowColumns(), genRefColumns(), genDocumentColums(),
                genRowColumnsParams(), genRefColumnsParams(), genDocumentColumnsParams());

        SQLiteStatement stmDoc = db.compileStatement(sqlDoc);

        String sqlTpDoc = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES (%s,%s,%s,%s)",
                DocumentUstanovkaCenTPTovari.TABLE_NAME,
                genRowColumns(), genTableColums(), genTablePartColumns(), genTablePartPriceTovariColumns(),
                genRowColumnsParams(), genTableColumnsParams(), genTablePartColumnsParams(), genTablePartPriceTovariColumnsParams());

        SQLiteStatement stmTp = db.compileStatement(sqlTpDoc);

        try {

            optimiseBeginInsert(db);
            db.beginTransaction();

            int lenCode = 9;
            for(int i = 0; i < mMeasureType.getCountDocPrice(); i++ ){
                int number = i + 1;
                String strNumber = formatNumber(number, lenCode);
                String idDoc = generateRef();

                stmDoc.clearBindings();
                int ind = 0;
                ind = bindRowValues(stmDoc,ind, true);
                ind = bindRefValues(stmDoc,ind, idDoc);
                bindDocumentValues(stmDoc, ind, new Date(System.currentTimeMillis()), strNumber, true);
                stmDoc.executeInsert();

                for(int j = 0; j < mMeasureType.getCountPosOfDocPrice(); j++ ){
                    int lineNumber = j + 1;
                    String nomDesc = "Номенклатура " + lineNumber;
                    String idNom = findNomenclaturaId(nomDesc);
                    String recordKey = String.format("%s-%s", lineNumber, idDoc);

                    stmTp.clearBindings();
                    int ind2 = 0;
                    ind2 = bindRowValues(stmTp, ind2, true);
                    ind2 = bindTableValues(stmTp, ind2, recordKey);
                    ind2 = bindTableTartValues(stmTp, ind2, lineNumber);
                    bindTablePartPriceTovariValues(stmTp, ind2, idDoc, idNom, (10 + i) * j);
                    stmTp.executeInsert();
                }
            }
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            optimizeEndInsert(db);
        }

    }

    void fullRegCeniNomenklaturi() {
        String sql = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES (%s,%s,%s,%s)",
                RegCeniNomenklaturi.TABLE_NAME,
                genRowColumns(), genTableColums(), genTableInfRegColums(), genRegPriceColums() ,
                genRowColumnsParams(), genTableColumnsParams(), genTableInfRegColumnsParams(), genRegPriceColumnsParams());

        SQLiteStatement statement = db.compileStatement(sql);

        try {

            optimiseBeginInsert(db);
            db.beginTransaction();
            Date period = DateHelper.beginOfDay(new Date(System.currentTimeMillis()));

            for(int i = 0; i < mMeasureType.getCountOfNumenclatura(); i++ ){
                int number = i + 1;
                String nomDesc = "Номенклатура " + number;
                String idNom = findNomenclaturaId(nomDesc);
                double cena = 1000d / (i + 1);
                String recordKey = String.format("%d%s", period.getTime(), idNom);

                statement.clearBindings();
                int ind = 0;
                ind = bindRowValues(statement, ind, true);
                ind = bindTableValues(statement, ind, recordKey);
                ind = bindTableInfRegValues(statement, ind, null);
                bindRegPriceValues(statement, ind, period, idNom, cena);
                statement.executeInsert();
            }
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            optimizeEndInsert(db);
        }
    }

    private void initFindNomenclaturaStatement(){
        String sqlSelectNom = String.format("SELECT %s FROM %s WHERE %s = ? LIMIT 1", CatalogNomenklatura.FIELD_NAME_REF,
                CatalogNomenklatura.TABLE_NAME, CatalogNomenklatura.FIELD_NAME_DESCRIPTION);
        mStmFindMomenclatura = db.compileStatement(sqlSelectNom);
    }

    private String findNomenclaturaId(String desc) {
        String id = mapNom.get(desc);
        if(TextUtils.isEmpty(id)){
            mStmFindMomenclatura.clearBindings();
            mStmFindMomenclatura.bindString(1, desc);

            try {
                id = mStmFindMomenclatura.simpleQueryForString();
            } catch(SQLiteDoneException e){
                id =  generateRef();
            }
            mapNom.put(desc, id);
        }
        return id;
     }

    void deleteMarkDocPrihodForAll() {
        db.execSQL("UPDATE  " + DocumentPrihod.TABLE_NAME + " SET " + DocumentPrihod.FIELD_NAME_DELETIONMARK + " = 1");
    }

    void deleteMarkDocPriceForAll() {
        db.execSQL("UPDATE  " + DocumentUstanovkaCen.TABLE_NAME + " SET " + DocumentPrihod.FIELD_NAME_DELETIONMARK + " = 1");
    }

    private void optimiseBeginInsert(SQLiteDatabase db) throws android.database.SQLException {
        db.execSQL("PRAGMA synchronous = OFF; \n" +
                " PRAGMA journal_mode = OFF;");
    }

    private void optimizeEndInsert(SQLiteDatabase db) throws android.database.SQLException {
        db.execSQL(" PRAGMA synchronous = NORMAL; \n" +
                   " PRAGMA journal_mode = DELETE;");
    }

    private String genRowColumns() {
       return Row.FIELD_NAME_MODIFIED;
    }

    private String genRowColumnsParams() {
        return "?";
    }

    private int bindRowValues(SQLiteStatement statement, int index, boolean modified) {
        statement.bindLong(++index, (modified) ? 1 : 0);
        return index;
    }

    private String genRefColumns() {
        StringBuilder sb = new StringBuilder();
        sb.append(Ref.FIELD_NAME_REF).append(",");
        sb.append(Ref.FIELD_NAME_DELETIONMARK).append(",");
        sb.append(Ref.FIELD_NAME_NEW_ITEM);
        return sb.toString();
    }

    private String genRefColumnsParams() {
        return "?,?,?";
    }

    private int bindRefValues(SQLiteStatement statement, int index, String ref) {
        statement.bindString(++index, ref);
        statement.bindLong(++index, 0);
        statement.bindLong(++index, 1);
        return index;
    }

    private String genCatalogColums() {
        StringBuilder sb = new StringBuilder();
        sb.append(Catalog.FIELD_NAME_CODE).append(",");
        sb.append(Catalog.FIELD_NAME_DESCRIPTION).append(",");
        sb.append(Catalog.FIELD_NAME_FOLDER).append(",");
        sb.append(Catalog.FIELD_NAME_PREDEFINED).append(",");
        sb.append(Catalog.FIELD_NAME_PARENT).append(",");
        sb.append(Catalog.FIELD_NAME_LEVEL);
        return sb.toString();
    }

    private String genCatalogColumnsParams() {
        return "?,?,?,?,?,?";
    }

    private int bindCatalogValues(SQLiteStatement statement, int index, String code,
            String desc) {
        statement.bindString(++index, code);
        statement.bindString(++index, desc);
        statement.bindLong(++index, 0);
        statement.bindLong(++index, 0);
        statement.bindNull(++index);
        statement.bindLong(++index, 0);
        return index;
    }

    private String genDocumentColums() {
        StringBuilder sb = new StringBuilder();
        sb.append(Document.FIELD_NAME_DATE).append(",");
        sb.append(Document.FIELD_NAME_NUMBER).append(",");
        sb.append(Document.FIELD_NAME_POSTED);
        return sb.toString();
    }

    private int bindDocumentValues(SQLiteStatement statement,int index, Date date,
                                   String number, Boolean posted) {
        statement.bindLong(++index, date.getTime());
        statement.bindString(++index, number);
        statement.bindLong(++index, posted ? 1 : 0);
        return index;
    }

    private String genDocumentColumnsParams() {
        return "?,?,?";
    }

    private String genTableColums() {
        return Table.FIELD_NAME_ID;
    }

    private String genTableColumnsParams() {
        return "?";
    }

    private int bindTableValues(SQLiteStatement statement, int index, String recordKey) {
        statement.bindString(++index, recordKey);
        return index;
    }

    private String genTablePartColumns() {
        return TablePart.FIELD_NAME_LINE_NUMBER;
    }

    private String genTablePartColumnsParams() {
        return "?";
    }

    private int bindTableTartValues(SQLiteStatement statement, int index, int number) {
        statement.bindLong(++index, number);
        return index;
    }

    private String genTablePartPrihodTovariColumns() {
        StringBuilder sb = new StringBuilder();
        sb.append(TablePart.FIELD_NAME_REF_ID).append(",");
        sb.append(DocumentPrihodTPTovari.FIELD_NAME_NOMENKLATURA).append(",");
        sb.append(DocumentPrihodTPTovari.FIELD_NAME_KOLICHESTVO);
        return sb.toString();
    }

    private String genTablePartPrihodTovariColumnsParams() {
        return "?,?,?";
    }

    private int bindTablePartPrihodTovariValues(SQLiteStatement statement, int index, String idDoc, String idNom, double kol) {
        statement.bindString(++index, idDoc);
        statement.bindString(++index, idNom);
        statement.bindDouble(++index, kol);
        return index;
    }

    private String genTablePartPriceTovariColumns() {
        StringBuilder sb = new StringBuilder();
        sb.append(TablePart.FIELD_NAME_REF_ID).append(",");
        sb.append(DocumentUstanovkaCenTPTovari.FIELD_NAME_NOMENKLATURA).append(",");
        sb.append(DocumentUstanovkaCenTPTovari.FIELD_NAME_CENA);
        return sb.toString();
    }

    private String genTablePartPriceTovariColumnsParams() {
        return "?,?,?";
    }

    private int bindTablePartPriceTovariValues(SQLiteStatement statement, int index, String idDoc, String idNom, double price) {
        statement.bindString(++index, idDoc);
        statement.bindString(++index, idNom);
        statement.bindDouble(++index, price);
        return index;
    }

    private String genTableInfRegColums() {
        return TableInfReg.FIELD_NAME_RECORDER;
    }

    private String genTableInfRegColumnsParams() {
        return "?";
    }

    private int bindTableInfRegValues(SQLiteStatement statement, int index, String recorder) {
        if (TextUtils.isEmpty(recorder)) {
            statement.bindNull(++index);
        } else {
            statement.bindString(++index, recorder);
        }
        return index;
    }

    private String genRegShtrihkodColums() {
        StringBuilder sb = new StringBuilder();
        sb.append(RegShtrihkod.FIELD_NAME_NOMENKLATURA).append(",");
        sb.append(RegShtrihkod.FIELD_NAME_SHTRIHKOD);
        return sb.toString();
    }

    private String genRegShtrihkodColumnsParams() {
        return "?,?";
    }

    private int bindRegShtrihkodValues(SQLiteStatement statement, int index, String nomenclatura, String shtrichCode) {
        statement.bindString(++index, nomenclatura);
        statement.bindString(++index, shtrichCode);
        return index;
    }

    private String genRegPriceColums() {
        StringBuilder sb = new StringBuilder();
        sb.append(RegCeniNomenklaturi.FIELD_NAME_PERIOD).append(",");
        sb.append(RegCeniNomenklaturi.FIELD_NAME_NOMENKLATURA).append(",");
        sb.append(RegCeniNomenklaturi.FIELD_NAME_CENA);
        return sb.toString();
    }

    private String genRegPriceColumnsParams() {
        return "?,?,?";
    }

    private int bindRegPriceValues(SQLiteStatement statement, int index, Date period, String nomenclatura, double price) {
        statement.bindLong(++index, period.getTime());
        statement.bindString(++index, nomenclatura);
        statement.bindDouble(++index, price);
        return index;
    }

    private String generateRef(){
        //It works much slower
        //UUID.randomUUID().toString()
        return new UUID(rnd.nextLong(), rnd.nextLong()).toString();
    }

    /**
     * Форматировать числовое значение как строку с лидирующими нулями
     *
     * @param value число
     * @param len   длина результирующей строки
     * @return
     */
    public String formatNumber(int value, int len) {
        return formatNumber(null, value, len);
    }

    /**
     * Форматировать числовое как строку с лидирующими нулями
     *
     * @param prefix строковое значение префикса
     * @param value  число
     * @param len    длина результирующей строки (с учетом префикса)
     * @return
     */
    public String formatNumber(String prefix, int value, int len) {
        String frm = "%0" + String.valueOf(len) + "d";
        if (!TextUtils.isEmpty(prefix)) {
            frm = prefix + "%0" + String.valueOf(len - prefix.length()) + "d";
        }
        return String.format(frm, value);
    }
}
