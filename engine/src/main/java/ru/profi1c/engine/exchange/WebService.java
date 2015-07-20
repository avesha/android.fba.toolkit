package ru.profi1c.engine.exchange;

import android.text.TextUtils;

import org.apache.commons.io.FileUtils;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE2;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;
import ru.profi1c.engine.app.FbaApplication;
import ru.profi1c.engine.util.IOHelper;

/**
 * Реализация вызовов web-сервиса обмена данными с 1С по протоколу SOAP.
 */
public class WebService implements IExchangeDataProvider {
    private static final String TAG = WebService.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private static final String NAMESPACE = "http://www.profi1c.ru/fbaEngine";
    private static final String SERVICE_NAME = "fbaService.1cws";

    // имена методов так же как они заданы для web сервиса в 1С
    private static final String LOGIN = "Login";
    private static final String IS_WORKING_VERSION_APP = "IsWorkingVersionApp";
    private static final String GET_APP = "GetApp";
    private static final String GET_DATA = "GetData";
    private static final String REGISTER_DATA_RECEIPT = "RegisterDataReceipt";
    private static final String WRITE_DATA = "WriteData";
    private static final String GET_LARGE_DATA = "GetLargeData";
    private static final String WRITE_LARGE_DATA = "WriteLargeData";
    private static final String GET_SHORT_DATA = "GetShortData";
    private static final String WRITE_SHORT_DATA = "WriteShortData";

    // имена параметров
    private static final String APP_ID = "AppId";
    private static final String APP_VERSION = "AppVersion";
    private static final String DEVICE_ID = "DeviceId";
    private static final String USER_NAME = "UserName";
    private static final String PASSWORD = "Password";
    private static final String ALL = "All";
    private static final String META_TYPE = "MetaType";
    private static final String META_NAME = "MetaName";
    private static final String ADDON_PARAMS = "AddonParams";
    private static final String DATA = "Data";
    private static final String ID = "Id";
    private static final String REF = "Ref";

    /*
     * Таймаут по умолчанию большой, как правило, изменяется в настройках
     * соединения
     */
    private static final int DEFAULT_TIMEOUT = 15 * 60000;
    private static final String EXTENSION_ZIP = ".zip";

    private String mBaseUrl;
    private int mTimeout;

    private WebService(int timeout, String serverIP, String appSpace, String serviceName) {

        mTimeout = timeout;

        StringBuilder sb = new StringBuilder("http://");
        sb.append(serverIP).append("/");
        if (!TextUtils.isEmpty(appSpace)) {
            sb.append(appSpace).append("/");
        }
        sb.append(serviceName);
        mBaseUrl = sb.toString();
    }

    /**
     * Инициализация менеджера обмена с базой 1С
     *
     * @param serverIP    ip-адрес на сервера на котором развернут web-сервис
     * @param appSpace    подкаталог приложения на сервере в котором опубликован
     *                    web-сервис
     * @param serviceName имя опубликованного web-сервиса
     */
    public WebService(String serverIP, String appSpace, String serviceName) {
        this(DEFAULT_TIMEOUT, serverIP, appSpace, serviceName);
    }

    /**
     * Инициализация менеджера обмена с базой 1С.Имя для опубликованного
     * web-сервиса, используется по умолчанию ‘fbaService.1cws’.
     *
     * @param serverIP ip-адрес на сервера на котором развернут web-сервис
     * @param appSpace подкаталог приложения на сервере в котором опубликован
     *                 web-сервис.
     */
    public WebService(String serverIP, String appSpace) {
        this(DEFAULT_TIMEOUT, serverIP, appSpace, SERVICE_NAME);
    }

    /**
     * Инициализация менеджера обмена с базой 1С.
     *
     * @param baseUrl путь к опубликованному сервису, например
     *                "http://127.0.0.1/demoTrade/fbaService.1cws"
     */
    public WebService(String baseUrl) {
        mTimeout = DEFAULT_TIMEOUT;
        this.mBaseUrl = baseUrl;
    }

    @Override
    public int getConnectionTimeout() {
        return mTimeout;
    }

    @Override
    public void setConnectionTimeout(int ms) {
        mTimeout = ms;
    }

    @Override
    public boolean login(String appId, String deviceId, String userName, String password)
            throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG,
                  "login, appId: " + appId + " deviceId: " + deviceId + " userName: " + userName +
                  " password: " + password);
        }

        boolean isLogin = false;

        PropertyInfo[] params = new PropertyInfo[4];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putStringParam(DEVICE_ID, deviceId);
        params[2] = putStringParam(USER_NAME, userName);
        params[3] = putStringParam(PASSWORD, password);

        SoapObject response = callSoapMethod(LOGIN, params);
        if (response != null) {
            isLogin = Boolean.parseBoolean(response.getProperty(0).toString());
        }

        return isLogin;
    }

    @Override
    public boolean isWorkingVersionApp(String appId, int appVersion, String deviceId)
            throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "isWorkingVersionApp, appId: " + appId + " deviceId: " + deviceId +
                       " appVersion: " + appVersion);
        }

        boolean isTopical = false;

        PropertyInfo[] params = new PropertyInfo[3];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putIntParam(APP_VERSION, appVersion);
        params[2] = putStringParam(DEVICE_ID, deviceId);

        SoapObject response = callSoapMethod(IS_WORKING_VERSION_APP, params);
        if (response != null) {
            isTopical = Boolean.parseBoolean(response.getProperty(0).toString());
        }

        return isTopical;
    }

    @Override
    public File getApp(String appId, int appVersion, String deviceId, String fPath)
            throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "getApp, appId: " + appId + " deviceId: " + deviceId + " appVersion: " +
                       appVersion + " fPath: " + fPath);
        }

        PropertyInfo[] params = new PropertyInfo[3];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putIntParam(APP_VERSION, appVersion);
        params[2] = putStringParam(DEVICE_ID, deviceId);

        File f = new File(fPath);

        boolean complete = callSoapMethod(GET_APP, params, f);
        if (DEBUG) {
            Dbg.d(TAG, "getApp complete:" + complete);
        }
        if (complete) {
            return f;
        }

        return null;
    }

    @Override
    public File getData(String appId, String deviceId, boolean all, String metaType,
            String metaName, String addonParams, String fPath) throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "getData, appId: " + appId + " deviceId: " + deviceId + " all: " + all +
                       " metaType: " + metaType + " metaName: " + metaName + " fPath: " + fPath);
        }

        PropertyInfo[] params = new PropertyInfo[6];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putStringParam(DEVICE_ID, deviceId);
        params[2] = putBooleanParam(ALL, all);
        params[3] = putStringParam(META_TYPE, metaType);
        params[4] = putStringParam(META_NAME, metaName);
        params[5] = putStringParam(ADDON_PARAMS, addonParams);

        return callSoapMethodAndExtract(GET_DATA, params, fPath);
    }

    @Override
    public boolean registerDataReceipt(String appId, String deviceId, String addonParams)
            throws WebServiceException {
        boolean isOk = true;

        if (DEBUG) {
            Dbg.d(TAG, "registerDataReceipt, appId: " + appId + " deviceId: " + deviceId);
        }

        PropertyInfo[] params = new PropertyInfo[3];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putStringParam(DEVICE_ID, deviceId);
        params[2] = putStringParam(ADDON_PARAMS, addonParams);

        SoapObject response = callSoapMethod(REGISTER_DATA_RECEIPT, params);
        if (response != null) {
            isOk = Boolean.parseBoolean(response.getProperty(0).toString());
        }

        return isOk;
    }

    @Override
    public boolean writeData(String appId, String deviceId, String metaType, String metaName,
            String fPath, String addonParams) throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "writeData, appId: " + appId + " deviceId: " + deviceId + " metaType: " +
                       metaType + " metaName: " + metaName + " fPath: " + fPath);
        }

        boolean isOk = true;

        String data = zipAndEncodeFile(fPath);
        if (!TextUtils.isEmpty(data)) {

            PropertyInfo[] params = new PropertyInfo[6];
            params[0] = putStringParam(APP_ID, appId);
            params[1] = putStringParam(DEVICE_ID, deviceId);
            params[2] = putStringParam(META_TYPE, metaType);
            params[3] = putStringParam(META_NAME, metaName);
            params[4] = putStringParam(DATA, data);
            params[5] = putStringParam(ADDON_PARAMS, addonParams);

            SoapObject response = callSoapMethod(WRITE_DATA, params);
            if (response != null) {
                isOk = Boolean.parseBoolean(response.getProperty(0).toString());
            }
        }
        return isOk;
    }

    @Override
    public File getLargeData(String appId, String deviceId, String id, String ref,
            String addonParams, String fPath) throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "getLargeData, appId: " + appId + " deviceId: " + deviceId + " id: " + id +
                       " ref: " + ref + " fPath: " + fPath);
        }

        PropertyInfo[] params = new PropertyInfo[5];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putStringParam(DEVICE_ID, deviceId);
        params[2] = putStringParam(ID, id);
        params[3] = putStringParam(REF, ref);
        params[4] = putStringParam(ADDON_PARAMS, addonParams);

        return callSoapMethodAndExtract(GET_LARGE_DATA, params, fPath);
    }

    @Override
    public boolean writeLargeData(String appId, String deviceId, String id, String ref,
            String fPath, String addonParams) throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "writeLargeData, appId: " + appId + " deviceId: " + deviceId + " id: " + id +
                       " ref: " + ref + " fPath: " + fPath);
        }

        boolean isOk = true;
        String data = zipAndEncodeFile(fPath);
        if (!TextUtils.isEmpty(data)) {

            PropertyInfo[] params = new PropertyInfo[6];
            params[0] = putStringParam(APP_ID, appId);
            params[1] = putStringParam(DEVICE_ID, deviceId);
            params[2] = putStringParam(ID, id);
            params[3] = putStringParam(REF, ref);
            params[4] = putStringParam(DATA, data);
            params[5] = putStringParam(ADDON_PARAMS, addonParams);

            SoapObject response = callSoapMethod(WRITE_LARGE_DATA, params);
            if (response != null) {
                isOk = Boolean.parseBoolean(response.getProperty(0).toString());
            }
        }
        return isOk;
    }

    @Override
    public String getShortData(String appId, String deviceId, String id, String addonParams)
            throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "getShortData, appId: " + appId + " deviceId: " + deviceId + " id: ");
        }

        String result = null;

        PropertyInfo[] params = new PropertyInfo[4];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putStringParam(DEVICE_ID, deviceId);
        params[2] = putStringParam(ID, id);
        params[3] = putStringParam(ADDON_PARAMS, addonParams);

        SoapObject response = callSoapMethod(GET_SHORT_DATA, params);
        if (response != null) {
            byte[] data = Base64.decode(response.getProperty(0).toString());
            result = new String(data);
        }

        return result;
    }

    @Override
    public boolean writeShortData(String appId, String deviceId, String id, String strJson,
            String addonParams) throws WebServiceException {

        if (DEBUG) {
            Dbg.d(TAG, "writeShortData, appId: " + appId + " deviceId: " + deviceId + " id: ");
        }
        return doWriteShortData(appId, deviceId, id, strJson, addonParams);
    }

    /*
     * Передать строку json на сервер
     */
    private boolean doWriteShortData(String appId, String deviceId, String id, String data,
            String addonParams) throws WebServiceException {

        boolean isOk = true;

        String data64 = Base64.encode(data.getBytes());

        PropertyInfo[] params = new PropertyInfo[5];
        params[0] = putStringParam(APP_ID, appId);
        params[1] = putStringParam(DEVICE_ID, deviceId);
        params[2] = putStringParam(ID, id);
        params[3] = putStringParam(DATA, data64);
        params[4] = putStringParam(ADDON_PARAMS, addonParams);

        SoapObject response = callSoapMethod(WRITE_SHORT_DATA, params);
        if (response != null) {
            isOk = Boolean.parseBoolean(response.getProperty(0).toString());
        }

        return isOk;
    }

    private PropertyInfo putStringParam(String name, String value) {
        PropertyInfo param = new PropertyInfo();
        param.setName(name);
        param.setValue(value);
        return param;
    }

    private PropertyInfo putIntParam(String name, int value) {
        PropertyInfo param = new PropertyInfo();
        param.setName(name);
        param.setValue(value);
        return param;
    }

    private PropertyInfo putBooleanParam(String name, boolean value) {
        PropertyInfo param = new PropertyInfo();
        param.setName(name);
        param.setValue(value);
        return param;
    }

    /**
     * Вызов метода web-сервиса, из полученного архива извлекается первый файл и
     * сохраняется под указанным именем
     *
     * @param name   name имя метода
     * @param params метода
     * @param fPath  путь к файлу в котором будет сохранен результат
     * @return
     * @throws WebServiceException
     */
    private File callSoapMethodAndExtract(String name, PropertyInfo params[], String fPath)
            throws WebServiceException {

        File newFile = null;

        String tmpZip = fPath.substring(0, fPath.lastIndexOf('.')) + EXTENSION_ZIP;
        File fZip = new File(tmpZip);

        boolean complete = callSoapMethod(name, params, fZip);
        if (DEBUG) {
            Dbg.d(TAG, "callSoapMethodAndExtract complete:" + complete);
        }
        if (complete) {
            try {
                File f = IOHelper.unZipFirst(fZip);

                newFile = new File(fPath);
                f.renameTo(newFile);

            } catch (IOException e) {
                Dbg.printStackTrace(e);
                throw new WebServiceException(e.getMessage());
            } finally {
                fZip.delete();
            }
        }
        return newFile;
    }

    /**
     * Вызов метода web-сервиса
     *
     * @param name   имя метода
     * @param params параметры метода
     * @return
     * @throws WebServiceException
     */
    private SoapObject callSoapMethod(String name, PropertyInfo params[])
            throws WebServiceException {

        SoapObject response = null;
        String SOAP_ACTION = NAMESPACE + "/" + name;

        SoapObject request = new SoapObject(NAMESPACE, name);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                request.addProperty(params[i]);
            }
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.setOutputSoapObject(request);
        HttpTransportSE2 androidHttpTransport = new HttpTransportSE2(mBaseUrl, mTimeout);
        androidHttpTransport.debug = Dbg.DEBUG;

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);

            if (androidHttpTransport.debug) {
                Dbg.d(TAG + "callSoapMethod, request = " + androidHttpTransport.requestDump);
                Dbg.d(TAG + "callSoapMethod, response = " + androidHttpTransport.responseDump);
            }

            if (envelope.bodyIn != null) {
                if (envelope.bodyIn.getClass() == SoapFault12.class) {
                    SoapFault soapFault = (SoapFault12) envelope.bodyIn;
                    throw new WebServiceException(soapFault.getMessage());

                } else if (envelope.bodyIn.getClass() == SoapObject.class) {
                    response = (SoapObject) envelope.bodyIn;
                }
            }

        } catch (IOException e) {
            Dbg.printStackTrace(e);

            if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                throw new WebServiceException(
                        FbaApplication.getContext().getString(R.string.fba_ws_exception_timeout));
            }
            throw new WebServiceException(e.getMessage());

        } catch (XmlPullParserException e) {
            Dbg.printStackTrace(e);
            String err = FbaApplication.getContext().getString(R.string.fba_ws_exception_format_xml,
                                                               e.getMessage());
            throw new WebServiceException(err);
        }

        return response;
    }

    /**
     * Вызов метода, возвращающего большой объем данных закодированных в base64.
     * Пасинг и сохранение результата в файл
     *
     * @param name   имя метода
     * @param params параметры метода
     * @param file   файл, в который сохраняется результат
     * @return true, если вызов произведен успешно и результат сохранен
     * @throws WebServiceException
     */
    private boolean callSoapMethod(String name, PropertyInfo params[], File file)
            throws WebServiceException {
        boolean complete = false;

        String SOAP_ACTION = NAMESPACE + "/" + name;

        SoapObject request = new SoapObject(NAMESPACE, name);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                request.addProperty(params[i]);
            }
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);

        envelope.setOutputSoapObject(request);
        HttpTransportSE2 androidHttpTransport = new HttpTransportSE2(mBaseUrl, mTimeout);

        androidHttpTransport.debug = Dbg.DEBUG;

        try {
            complete = androidHttpTransport.callLarge(SOAP_ACTION, envelope, file);

        } catch (IOException e) {
            Dbg.printStackTrace(e);
            if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                throw new WebServiceException(
                        FbaApplication.getContext().getString(R.string.fba_ws_exception_timeout));
            }
            throw new WebServiceException(e.getMessage());
        }
        return complete;
    }

    /**
     * Упаковать файл в zip и полученный архив прочитать как строку 64
     *
     * @param fPath
     * @return
     * @throws WebServiceException
     */
    private String zipAndEncodeFile(String fPath) {
        String encoded = null;

        String tmpZip = fPath.substring(0, fPath.lastIndexOf('.')) + EXTENSION_ZIP;
        File fZip = new File(tmpZip);
        File fName = new File(fPath);

        try {
            if (IOHelper.zipFile(fZip, fName)) {
                encoded = encodeFile(fZip);
            }
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        } finally {
            fZip.delete();
        }
        return encoded;
    }

    /**
     * Преобразовать содержимое файла как base64 - строка
     *
     * @param file
     * @return
     */
    private String encodeFile(File file) {
        String encoded = null;
        if (file.exists()) {
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                encoded = Base64.encode(bytes);
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            }
        }
        return encoded;
    }
}
