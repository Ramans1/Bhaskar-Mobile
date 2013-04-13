/**********************************************************************************
 * @author Bhaskar Reddy
 * @desc   This class is to perform API calls between device and server.
 * @date   22-06-2012           
 * 
 * *********************************************************************************
 */

package com.qsr.foods;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;

import org.apache.cordova.DroidGap;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.webkit.WebView;

public class CustomHttpClient {
    DroidGap mGap;
    WebView mWebView;
    
    // time out in milliseconds to get connection with server
    final int timeoutConnection = 4000;
    
    // timeout in milliseconds to get data from server
    final int timeoutSocket = 5000;
    
    /****************************************************
     * @desc This is a public constructor
     * @param view
     *            **************************************************
     */
    public CustomHttpClient(WebView view) {
        mWebView = view;
    }
    
    /**
     * 
     * @param url
     * @return encoded string
     * @throws UnsupportedEncodingException
     */

    public String getEnCodedString(String url)
            throws UnsupportedEncodingException {

        String urls[] = url.split("\\?");
        return urls[0] + "?" + URLEncoder.encode(url, "utf-8");

    }
    
    /**
     * @desc This method to send request to server and get response from server.
     *       This method will called from javascript. If the request successful
     *       returns server response as string , otherwise it returns "fail".
     * @param url
     * @return String
     */
    public String nativeAJAXCall(String url) {

        HttpResponse response = null;
        String response_str = null;
        HttpClient client = null;
        String status = "success";
        try {
            // Thread.sleep(4000);
            
            HttpPost post = new HttpPost(ChimpUtilities.MAIN_URL + url);
            HttpParams httpParameters = new BasicHttpParams();

            // connection timeout
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // socket timeout
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            client = getNewHttpClient();
            response = client.execute(post);
            response_str = getResponseBody(response);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            response_str = e.getMessage()
                    + ".\n Please Check Your Network Connection";
            status = "error";
        } catch (IOException e) {
            e.printStackTrace();
            response_str = e.getMessage()
                    + ".\n Please Check Your Network Connection";
            status = "error";
        } catch (IllegalArgumentException e) {
            response_str = e.getMessage();
            status = "error";
            e.printStackTrace();
        } catch (Exception e) {
            response_str = e.getMessage();
            status = "error";
            e.printStackTrace();
        } finally {
            if (client != null)
                client.getConnectionManager().shutdown();

        }
        System.out.println(status + "=*="
                + response_str.trim().replace("\\/", "/"));
        return status + "=*=" + response_str.trim().replace("\\/", "/");
    }
    
    /**
     * @desc This method to get entity of the body called by nativeAJAXCall
     * @param response
     * @return String
     */

    public static String getResponseBody(HttpResponse response)
            throws IllegalArgumentException {
        String response_text = null;
        HttpEntity entity = null;
        try {
            entity = response.getEntity();
            response_text = _getResponseBody(entity);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e1) {
                }
            }
        }
        return response_text;
    }
    
    /**
     * @desc This method to read response data, and this method called by
     *       getResponseBody();
     * @param entity
     * @return String
     * @throws IOException
     * @throws ParseException
     */

    public static String _getResponseBody(final HttpEntity entity)
            throws IllegalArgumentException, IOException, ParseException {
        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        InputStream instream = entity.getContent();
        if (instream == null) {
            return "";
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "HTTP entity too large to be buffered in memory");
        }
        String charset = getContentCharSet(entity);
        if (charset == null) {
            charset = "SetUTF-8";
            // charset = HTTP.DEFAULT_CONTENT_CHARSET;
        }
        System.out.println("Char Set" + charset);
        Reader reader = new InputStreamReader(instream, charset);
        StringBuilder buffer = new StringBuilder();
        try {
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            reader.close();
        }
        return buffer.toString();
    }
    
    /**
     * @Desc This method to get charset of content.
     * 
     * @param entity
     * @return String
     * @throws ParseException
     */
    public static String getContentCharSet(final HttpEntity entity)
            throws ParseException, IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        String charset = null;
        if (entity.getContentType() != null) {
            HeaderElement values[] = entity.getContentType().getElements();
            if (values.length > 0) {
                NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
        }

        return charset;
    }
    
    /**
     * @Desc This method will return new HttpClient object for SSL connection
     * @return HttpClient
     */
    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);
            
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);
            
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
    
    public String getBaseURL() {
        return ChimpUtilities.MAIN_URL;
    }

}
