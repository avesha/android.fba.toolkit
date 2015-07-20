package com.sample.app2.test.db;

import com.sample.app2.db.DBHelper;
import com.sample.app2.test.base.BaseTestCase;

import java.util.Random;

import ru.profi1c.engine.Const;

public abstract class DBTestCase extends BaseTestCase {

    public static final int CATALOG_CODE_LENGTH = Const.DEFAULT_CATALOG_CODE_LENGTH + 3;
    public static final int LARGE_CATALOG_CODE_LENGTH = CATALOG_CODE_LENGTH + 5;
    public static final int DOCUMENT_NUMBER_LENGTH = Const.DEFAULT_DOCUMENT_NUMBER_LENGTH;
    public static Dao dao;
    private DBHelper mDBHelper;
    public Random rnd;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDBHelper = new DBHelper(getTargetContext());
        assertNotNull("DBHelper is null", mDBHelper);
        Dao.init(this);
        rnd = new Random();
    }

    protected DBHelper getDBHelper() {
        return mDBHelper;
    }

}
