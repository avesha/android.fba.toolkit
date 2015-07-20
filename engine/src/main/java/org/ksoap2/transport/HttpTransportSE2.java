/**
 *  Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. 
 *
 * Contributor(s): John D. Beatty, Dave Dash, F. Hunter, Alexander Krebs, 
 *                 Lars Mehrmann, Sean McDaniel, Thomas Strang, Renaud Tognelli 
 * */
package org.ksoap2.transport;

import android.util.Base64;
import android.util.Base64InputStream;

import org.apache.commons.io.IOUtils;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.util.IOHelper;

/**
 * Доработанный вариант HttpTransport, с учетом парсинга из результата больших значений
 */
public class HttpTransportSE2 extends Transport {
    private static final String TAG = HttpTransportSE2.class.getSimpleName();

    private ServiceConnection mConnection;

    /**
     * Creates instance of HttpTransportSE2 with set url
     *
     * @param url the destination to POST SOAP data
     */
    public HttpTransportSE2(String url) {
        super(null, url);
    }

    /**
     * Creates instance of HttpTransportSE2 with set url and defines a
     * proxy server to use to access it
     *
     * @param proxy Proxy information or <code>null</code> for direct access
     * @param url   The destination to POST SOAP data
     */
    public HttpTransportSE2(Proxy proxy, String url) {
        super(proxy, url);
    }

    /**
     * Creates instance of HttpTransportSE2 with set url
     *
     * @param url     the destination to POST SOAP data
     * @param timeout timeout for mConnection and Read Timeouts (milliseconds)
     */
    public HttpTransportSE2(String url, int timeout) {
        super(url, timeout);
    }

    public HttpTransportSE2(Proxy proxy, String url, int timeout) {
        super(proxy, url, timeout);
    }

    /**
     * set the desired soapAction header field
     *
     * @param soapAction the desired soapAction
     * @param envelope   the envelope containing the information for the soap call.
     * @throws IOException
     * @throws XmlPullParserException
     */
    public void call(String soapAction, SoapEnvelope envelope)
            throws IOException, XmlPullParserException {
        call(soapAction, envelope, null);
    }

    /**
     * Вызов функций web-сервиса который возвращают большие значения (более 2 мб)
     * закодированные по base64.
     *
     * @param soapAction желаемый soapAction
     * @param envelope   конверт, содержащий информацию для вызова soap.
     * @param file       файл в который декорируется и сохраняется результат
     * @return ссылку на файл с результатом в случае успеха или null
     * @throws IOException
     */
    public boolean callLarge(String soapAction, SoapEnvelope envelope, File file)
            throws IOException {

        boolean isOk = false;

        if (soapAction == null) {
            soapAction = "\"\"";
        }

        byte[] requestData = createRequestData(envelope);

        mConnection = getServiceConnection();
        mConnection.setRequestProperty("User-Agent", USER_AGENT);
        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            mConnection.setRequestProperty("SOAPAction", soapAction);
        }

        if (envelope.version == SoapSerializationEnvelope.VER12) {
            mConnection.setRequestProperty("Content-Type", CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            mConnection.setRequestProperty("Content-Type", CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        mConnection.setRequestProperty("Connection", "close");
        mConnection.setRequestProperty("Content-Length", "" + requestData.length);

        mConnection.setRequestMethod("POST");
        mConnection.connect();


        OutputStream os = mConnection.openOutputStream();

        os.write(requestData, 0, requestData.length);
        os.flush();
        os.close();
        InputStream is = null;

        try {
            mConnection.connect();
            is = mConnection.openInputStream();
            isOk = parseXml(is, file);
        } catch (IOException e) {
            is = mConnection.getErrorStream();

            if (is == null) {
                mConnection.disconnect();
                throw (e);
            }
        } finally {
            IOHelper.close(is);
        }
        return isOk;
    }

    /**
     * Парсинг входящего потока полученного от web-сервиса
     *
     * @param is
     * @param file
     * @return
     */
    private boolean parseXml(InputStream is, File file) {
        boolean complete = false;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            SAXParser parser = factory.newSAXParser();
            SaxHandler saxHandler = new SaxHandler(file);

            parser.parse(is, saxHandler);
            complete = saxHandler.isComplete();

        } catch (ParserConfigurationException e) {
            Dbg.printStackTrace(e);
        } catch (SAXException e) {
            Dbg.printStackTrace(e);
        } catch (IOException e) {
            Dbg.printStackTrace(e);
        }
        return complete;
    }

    /**
     * set the desired soapAction header field
     *
     * @param soapAction the desired soapAction
     * @param envelope   the envelope containing the information for the soap call.
     * @param headers    a list of HeaderProperties to be http header properties when establishing the mConnection
     * @return <code>CookieJar</code> with any cookies sent by the server
     * @throws IOException
     * @throws XmlPullParserException
     */
    @SuppressWarnings("unchecked")
    public List call(String soapAction, SoapEnvelope envelope, List headers)
            throws IOException, XmlPullParserException {

        if (soapAction == null) {
            soapAction = "\"\"";
        }

        byte[] requestData = createRequestData(envelope);

        requestDump = debug ? new String(requestData) : null;
        responseDump = null;

        mConnection = getServiceConnection();

        mConnection.setRequestProperty("User-Agent", USER_AGENT);
        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            mConnection.setRequestProperty("SOAPAction", soapAction);
        }

        if (envelope.version == SoapSerializationEnvelope.VER12) {
            mConnection.setRequestProperty("Content-Type", CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            mConnection.setRequestProperty("Content-Type", CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        mConnection.setRequestProperty("Connection", "close");
        mConnection.setRequestProperty("Content-Length", "" + requestData.length);

        // Pass the headers provided by the user along with the call
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                HeaderProperty hp = (HeaderProperty) headers.get(i);
                mConnection.setRequestProperty(hp.getKey(), hp.getValue());
            }
        }

        mConnection.setRequestMethod("POST");
        mConnection.connect();


        OutputStream os = mConnection.openOutputStream();

        os.write(requestData, 0, requestData.length);
        os.flush();
        os.close();
        InputStream is;
        List retHeaders = null;

        try {
            mConnection.connect();
            is = mConnection.openInputStream();
            retHeaders = mConnection.getResponseProperties();
        } catch (IOException e) {
            is = mConnection.getErrorStream();

            if (is == null) {
                mConnection.disconnect();
                throw (e);
            }
        }

        if (debug) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];

            while (true) {
                int rd = is.read(buf, 0, 256);
                if (rd == -1) {
                    break;
                }
                bos.write(buf, 0, rd);
            }

            bos.flush();
            buf = bos.toByteArray();
            responseDump = new String(buf);
            is.close();
            is = new ByteArrayInputStream(buf);
        }

        parseResponse(envelope, is);
        return retHeaders;
    }

    public ServiceConnection getConnection() {
        return (ServiceConnectionSE) mConnection;
    }

    protected ServiceConnection getServiceConnection() throws IOException {
        return new ServiceConnectionSE(proxy, url, timeout);
    }

    public String getHost() {

        String retVal = null;

        try {
            retVal = new URL(url).getHost();
        } catch (MalformedURLException e) {
            Dbg.printStackTrace(e);
        }

        return retVal;
    }

    public int getPort() {

        int retVal = -1;

        try {
            retVal = new URL(url).getPort();
        } catch (MalformedURLException e) {
            Dbg.printStackTrace(e);
        }

        return retVal;
    }

    public String getPath() {

        String retVal = null;

        try {
            retVal = new URL(url).getPath();
        } catch (MalformedURLException e) {
            Dbg.printStackTrace(e);
        }

        return retVal;
    }

    /**
     * Обработчик парсинга больших xml файлов
     */
    private static class SaxHandler extends DefaultHandler {
        private static final boolean DEBUG = Dbg.DEBUG;

        public static final String DECODING_TMP_FILE_NAME = "decoding.tmp";
        private static final String TAG_NAME = "return";
        public static final int INFORM_WRITE_SIZE = 10240;

        private final File mOutFile;
        private final File mTmpFile;

        private FileOutputStream mFOut;
        private boolean mReading, mComplete;

        private int mTotalWrite;

        public SaxHandler(File outFile) {
            this.mOutFile = outFile;

            mTmpFile = new File(outFile.getParentFile(), DECODING_TMP_FILE_NAME);
            if (mTmpFile.exists()) {
                mTmpFile.delete();
            }
        }

        public void startDocument() throws SAXException {
            if (DEBUG) {
                Dbg.d(TAG, "SaxHandler, start parsing document");
            }
        }

        public void endDocument() throws SAXException {
            if (DEBUG) {
                Dbg.d(TAG, "SaxHandler, end parsing document");
            }
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {

            if (TAG_NAME.equals(localName)) {
                if (DEBUG) {
                    Dbg.d(TAG, "SaxHandler, start element: " + qName);
                }
                try {
                    mFOut = new FileOutputStream(mTmpFile);
                    mReading = true;
                    mTotalWrite = 0;
                } catch (FileNotFoundException e) {
                    Dbg.printStackTrace(e);
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (TAG_NAME.equals(localName)) {
                if (DEBUG) {
                    Dbg.d(TAG, "SaxHandler, end element: " + qName);
                }
                mReading = false;

                if (mFOut != null) {
                    try {
                        mFOut.close();
                    } catch (IOException e) {
                        Dbg.printStackTrace(e);
                    }
                    if (mTmpFile.exists()) {
                        if (mTmpFile.length() > 0) {
                            mComplete = decodeStream();
                        }
                        mTmpFile.delete();
                    }

                }
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {

            if (mReading) {
                String str = new String(ch, start, length);
                try {
                    mFOut.write(str.getBytes());
                    mTotalWrite += length;

                    if (DEBUG) {
                        //по 10 кб - информируем
                        if (mTotalWrite % INFORM_WRITE_SIZE == 0) {
                            Dbg.d(TAG, "SaxHandler, write to outfile length: " + mTotalWrite);
                        }
                    }

                } catch (IOException e) {
                    Dbg.printStackTrace(e);
                }
            }
        }

        /**
         * Декодировать файл base64
         *
         * @return
         */
        public boolean decodeStream() {
            boolean isOk = false;

            if (mOutFile.exists()) {

            }
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                fis = new FileInputStream(mTmpFile);
                fos = new FileOutputStream(mOutFile);

                Base64InputStream base64Is = new Base64InputStream(fis, Base64.NO_WRAP);
                IOUtils.copy(base64Is, fos);

                isOk = true;
            } catch (FileNotFoundException e) {
                Dbg.printStackTrace(e);
            } catch (IOException e) {
                Dbg.printStackTrace(e);
            } finally {
                IOHelper.close(fis);
                IOHelper.close(fos);
            }
            return isOk;
        }

        public boolean isComplete() {
            return mComplete;
        }

    }
}
