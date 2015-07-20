package ru.profi1c.engine;

public final class Const {

    public static final String APP = "fba-toolkit";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * Текущий номер версии фреймворка
     */
    public static final int FBA_VERSION_CODE = 6;

    public static final int NOT_SPECIFIED = -1;

    public static final int DIALOG_ID_ERROR = 1;

    public static final int REQUESTCODE_SECURITY_PASSWORD = 32001;

    public static String ACTION_SINGLE_LOCATION_UPDATE = "ru.profi1c.engine.ACTION_SINGLE_LOCATION_UPDATE_ACTION";

    public static final int DEFAULT_CATALOG_CODE_LENGTH = 6;
    public static final int DEFAULT_DOCUMENT_NUMBER_LENGTH = 9;

    public static final String DEFAULT_WIDGET_DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String DEFAULT_WIDGET_DATE_FORMAT = "dd.MM.yy";
    public static final String DEFAULT_WIDGET_DATE_FULL_YEAR_FORMAT = "dd.MM.yyyy";
    public static final String DEFAULT_WIDGET_TIME_FORMAT = "HH:mm";

    public static final String DEFAULT_WIDGET_NULL_FORMAT = "";
    public static final String DEFAULT_WIDGET_BOOLEAN_FORMAT = "%b";
    public static final String DEFAULT_WIDGET_INT_FORMAT = "%d";
    public static final String DEFAULT_WIDGET_DOUBLE_FORMAT = "###,##0.00";
    public static final String DEFAULT_WIDGET_STRING_FORMAT = "%s";

    /**
     * Таймаут по умолчанию - 30 сек.
     */
    public static final int DEFAULT_EXCHANGE_TIMEOUT = 30000;

    /**
     * Время (минуты) часа для расписания обмена по расписанию
     */
    public static final int DEFAULT_EXCHANGE_TIME_MINUTES = 0;

    /**
     * Время (час) для обмена по расписанию
     */
    public static final int DEFAULT_EXCHANGE_TIME_HOURS = 9;

    /**
     * Индексы дней недели (1 – воскресенье) по которым выполняется обмен в
     * автоматическом режиме
     */
    public static final String DEFAULT_EXCHANGE_DAYS_INDEX = "2,3,4,5,6";

    /**
     * Идентификатор уведомления, которое будет создано при получении новой
     * версии приложение
     */
    public static int NOTIFICATION_ID_DOWNLOAD_APK = 7227;

    /**
     * Идентификатор уведомления, которое будет создано по окончании обмена
     */
    public static int NOTIFICATION_ID_FINISH_EXCHANGE = 7228;

    //Warning: a reference to the name of the resource
    public static final String META_PREFIX = "@string/";

    public static final String META_DESCRIPTION_CODE = META_PREFIX + "fba_meta_code";
    public static final String META_DESCRIPTION_NAME = META_PREFIX + "fba_meta_desc";
    public static final String META_DESCRIPTION_FOLDER = META_PREFIX + "fba_meta_folder";
    public static final String META_DESCRIPTION_PREDEFINED = META_PREFIX + "fba_meta_predefined";
    public static final String META_DESCRIPTION_PARENT = META_PREFIX + "fba_meta_parent";
    public static final String META_DESCRIPTION_LEVEL = META_PREFIX + "fba_meta_level";
    public static final String META_DESCRIPTION_DATA = META_PREFIX + "fba_meta_date";
    public static final String META_DESCRIPTION_NUMBER = META_PREFIX + "fba_meta_number";
    public static final String META_DESCRIPTION_POSTED = META_PREFIX + "fba_meta_posted";
    public static final String META_DESCRIPTION_REF = META_PREFIX + "fba_meta_ref";
    public static final String META_DESCRIPTION_DELETIONMARK = META_PREFIX + "fba_meta_deletionmark";
    public static final String META_DESCRIPTION_NEW_ITEM = META_PREFIX + "fba_meta_new_item";
    public static final String META_DESCRIPTION_MODIFIED = META_PREFIX + "fba_meta_modified";
    public static final String META_DESCRIPTION_RECORD_KEY = META_PREFIX + "fba_meta_record_key";
    public static final String META_DESCRIPTION_RECORDER = META_PREFIX + "fba_meta_recorder";
    public static final String META_DESCRIPTION_PERIOD = META_PREFIX + "fba_meta_period";
    public static final String META_DESCRIPTION_LINE_NUMBER = META_PREFIX + "fba_meta_line_number";


    private Const() {
    }
}
