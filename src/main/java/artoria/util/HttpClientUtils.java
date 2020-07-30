package artoria.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Deprecated
public class HttpClientUtils {
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36";
    private static String defaultCharsetName;
    private static RequestConfig defaultRequestConfig;
    private static List<BasicHeader> defaultHeaders = new ArrayList<BasicHeader>();

    static {
        defaultCharsetName = Charset.defaultCharset().name();
        defaultRequestConfig = RequestConfig.custom()
                // CookieSpecs.BEST_MATCH
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .setSocketTimeout(19000)
                .setConnectTimeout(19000).build();
        defaultHeaders.add(new BasicHeader("User-Agent", DEFAULT_USER_AGENT));
    }

    public static String getDefaultCharsetName() {
        return defaultCharsetName;
    }

    public static void setDefaultCharsetName(String charsetName) {
        if (StringUtils.isBlank(charsetName)) {
            throw new IllegalArgumentException("Default charset name can not be blank. ");
        }
        defaultCharsetName = charsetName;
    }

    public static RequestConfig getDefaultRequestConfig() {
        return defaultRequestConfig;
    }

    public static void setDefaultRequestConfig(RequestConfig requestConfig) {
        if (requestConfig == null) {
            throw new IllegalArgumentException("Default request config can not be blank. ");
        }
        defaultRequestConfig = requestConfig;
    }

    public static void addDefaultHeader(String name, String value) {
        BasicHeader header = new BasicHeader(name, value);
        defaultHeaders.add(header);
    }

    public static void addDefaultHeaders(Map<String, String> headers) {
        List<BasicHeader> list = convertHeaders(headers);
        defaultHeaders.addAll(list);
    }

    public static void setDefaultHeader(String name, String value) {
        BasicHeader header = new BasicHeader(name, value);
        defaultHeaders.clear();
        defaultHeaders.add(header);
    }

    public static void setDefaultHeaders(Map<String, String> headers) {
        List<BasicHeader> list = convertHeaders(headers);
        defaultHeaders.clear();
        defaultHeaders.addAll(list);
    }

    public static CloseableHttpClient createHttpClient() {
        return HttpClients.createDefault();
    }

    public static HttpClientBuilder httpClientBuilder() {
        return HttpClientBuilder.create();
    }

    public static RequestConfig.Builder requestConfigBuilder() {
        return RequestConfig.custom();
    }

    public static RequestBuilder requestBuilder(String method) {
        return RequestBuilder.create(method);
    }

    public static EntityBuilder entityBuilder() {
        return EntityBuilder.create();
    }

    public static MultipartEntityBuilder multipartEntityBuilder() {
        return MultipartEntityBuilder.create();
    }

    public static List<NameValuePair> convertParams(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        return list;
    }

    public static List<BasicHeader> convertHeaders(Map<String, String> headers) {
        List<BasicHeader> list = new ArrayList<BasicHeader>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            list.add(new BasicHeader(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    public static void addHeaders(RequestBuilder builder, Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    public static void addHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    public static byte[] toByteArray(HttpEntity entity) throws IOException {
        return EntityUtils.toByteArray(entity);
    }

    public static String toString(HttpEntity entity) throws IOException, ParseException {
        return EntityUtils.toString(entity);
    }

    public static String toString(HttpEntity entity, String defaultCharset) throws IOException, ParseException {
        return EntityUtils.toString(entity, defaultCharset);
    }

    public static String get(String url)
            throws IOException {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, String> queryParas)
            throws IOException {
        return get(url, queryParas, null);
    }

    public static String get(String url, Map<String, String> queryParas, Map<String, String> headers)
            throws IOException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClientBuilder.create().setDefaultHeaders(defaultHeaders).build();

            if (MapUtils.isNotEmpty(queryParas)) {
                if (!url.contains("?")) { url += "?"; }
                else { url += "&"; }

                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, String> entry : queryParas.entrySet()) {
                    builder.append(entry.getKey());
                    builder.append("=");
                    builder.append(URLEncoder.encode(entry.getValue(), defaultCharsetName));
                    builder.append("&");
                }
                int len = builder.length();
                builder.delete(len - 1, len);

                url += builder.toString();
            }

            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(defaultRequestConfig);
            addHeaders(httpGet, headers);
            response = httpClient.execute(httpGet);
            return toString(response.getEntity(), defaultCharsetName);
        }
        finally {
            CloseUtils.closeQuietly(response);
            CloseUtils.closeQuietly(httpClient);
        }
    }

    public static String post(String url, Map<String, String> formParas)
            throws IOException {
        return post(url, formParas, null, null);
    }

    public static String post(String url, Map<String, String> formParas, String data)
            throws IOException {
        return post(url, formParas, null, data);
    }

    public static String post(String url, Map<String, String> formParas, Map<String, String> headers, String data)
            throws IOException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClientBuilder.create().setDefaultHeaders(defaultHeaders).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(defaultRequestConfig);
            addHeaders(httpPost, headers);
            boolean hasParas = MapUtils.isNotEmpty(formParas);
            boolean hasData = StringUtils.isNotBlank(data);
            if (hasParas || hasData) {
                EntityBuilder builder = entityBuilder();
                if (hasParas) {
                    List<NameValuePair> params = convertParams(formParas);
                    builder.setParameters(params);
                }
                if (hasData) {
                    builder.setText(data);
                }
                httpPost.setEntity(builder.build());
            }
            response = httpClient.execute(httpPost);
            return toString(response.getEntity(), defaultCharsetName);
        }
        finally {
            CloseUtils.closeQuietly(response);
            CloseUtils.closeQuietly(httpClient);
        }
    }

}
