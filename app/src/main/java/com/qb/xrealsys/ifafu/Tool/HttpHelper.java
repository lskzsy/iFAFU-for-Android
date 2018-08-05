package com.qb.xrealsys.ifafu.Tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sky on 08/02/2018.
 */

public class HttpHelper {

    protected int       mTimeout;

    protected String    mEncode;

    protected URL       mUrl;

    protected static String gDefaultEncode         = "utf-8";

    protected static int    gDefaultTimeout        = 5000;

    protected static int    gDefaultConnectTimeout = 3000;

    public HttpHelper(String urlString,
                      int timeout) throws IOException {
        this(urlString, timeout, gDefaultEncode);
    }

    public HttpHelper(String urlString,
                      String encode) throws IOException {
        this(urlString, gDefaultTimeout, encode);
    }

    public HttpHelper(String urlString) throws IOException {
        this(urlString, gDefaultTimeout, gDefaultEncode);
    }

    public HttpHelper(String urlString,
                      int timeout,
                      String encode) throws IOException {
        try {
            mUrl     = new URL(urlString);
            mTimeout = timeout;
            mEncode  = encode;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String GetAES(String key) throws IOException {
        try {
            Map<String, String> empty = new HashMap<>();
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setConnectTimeout(gDefaultConnectTimeout);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            //  Init connection
            InitConnectionInf(connection, empty, mTimeout);

            //  Get Response
            return GetAESResponse(connection, key);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Bitmap GetHttpGragh() throws IOException {
        try {
            Map<String, String> empty = new HashMap<>();
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setConnectTimeout(gDefaultConnectTimeout);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            //  Init connection
            InitConnectionInf(connection, empty, mTimeout);

            //  Get Response
            return GetGraghResponse(connection);
        } catch (Exception e) {
            return null;
        }
    }

    public HttpResponse GetWithoutRedirect() throws IOException {
        Map<String, String> empty = new HashMap<>();
        try {
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setConnectTimeout(gDefaultConnectTimeout);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);

            //  Init connection
            InitConnectionInf(connection, empty, mTimeout);

            //  Get Response
            return GetResponse(connection, mEncode);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public HttpResponse Get() throws IOException {
        Map<String, String> empty = new HashMap<>();
        return Get(empty);
    }

    public HttpResponse Get(Map<String, String> header) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setConnectTimeout(gDefaultConnectTimeout);
            connection.setRequestMethod("GET");

            //  Init connection
            InitConnectionInf(connection, header, mTimeout);

            //  Get Response
            return GetResponse(connection, mEncode);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public HttpResponse Post(Map<String, String> data) throws IOException {
        Map<String, String> empty = new HashMap<>();
        return Post(empty, data, true);
    }

    public HttpResponse Post(Map<String, String> data, boolean isEncode) throws IOException {
        Map<String, String> empty = new HashMap<>();
        return Post(empty, data, isEncode);
    }

    public HttpResponse Post(Map<String, String> header,
                             Map<String, String> data,
                             boolean isEncode) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setConnectTimeout(gDefaultConnectTimeout);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //  Init connection
            InitConnectionInf(connection, header, mTimeout);

            //  Fill post data
            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            String      buffer = "";
            for (Map.Entry<String, String> object: data.entrySet()) {
                if (isEncode) {
                    buffer += URLEncoder.encode(object.getKey(), mEncode) +
                            "=" +
                            URLEncoder.encode(object.getValue(), mEncode) +
                            "&";
                } else {
                    buffer += object.getKey() + "=" + object.getValue() + "&";
                }

            }
            buffer = buffer.substring(0, buffer.length() - 1);
            writer.write(buffer);
            writer.flush();

            //  Get Response
            return GetResponse(connection, mEncode);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected Bitmap GetGraghResponse(HttpURLConnection connection) throws IOException {
        try {
            int      code = connection.getResponseCode();
            Bitmap bitmap = null;
            if (code == 200) {
                InputStream is = connection.getInputStream();

                byte[] bytes = getBytes(is);
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    protected String GetAESResponse(HttpURLConnection connection, String key) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int      code = connection.getResponseCode();
            String   answer = null;
            if (code == 200) {
                InputStream is = connection.getInputStream();

                answer = AES.decrypt(Base64.decode(
                        Base64.encode(getBytes(is), Base64.DEFAULT), Base64.DEFAULT), key);
            }

            return answer;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        return outstream.toByteArray();
    }

    protected HttpResponse GetResponse(HttpURLConnection connection,
                                       String encode) throws IOException {
        try {
            int    code = connection.getResponseCode();
            String answer = "";
            if (code == 200) {
                InputStream           is   = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                answer = baos.toString(encode);
            }

            HttpResponse response = new HttpResponse(code, answer);
            return response;
        } catch (IOException e) {
            return new HttpResponse(-1, e.getMessage());
        }
    }

    protected void InitConnectionInf(HttpURLConnection connection,
                                     Map<String, String> header,
                                     int timeout) {
        //  Set Http Request Header
        for (Map.Entry<String, String> object: header.entrySet()) {
            connection.setRequestProperty(object.getKey(), object.getValue());
        }

        //  Sey Http Request Timeout
        connection.setReadTimeout(timeout);
    }
}
