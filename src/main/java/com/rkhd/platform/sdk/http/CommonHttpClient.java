package com.rkhd.platform.sdk.http;

import com.rkhd.platform.sdk.exception.XsyHttpException;
import com.rkhd.platform.sdk.http.handler.ResponseBodyHandler;
import com.rkhd.platform.sdk.util.IOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
public class CommonHttpClient {
    private CloseableHttpClient client;
    private String contentEncoding = "UTF-8";
    private String contentType = "application/json";
    private int socketTimeout = 120000;
    private int connectionTimeout = 120000;
    private RequestConfig config;

    public CommonHttpClient() {
        createClientWithoutSSL();
    }

    public CommonHttpClient(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        createClientWithoutSSL();
    }

    public CommonHttpClient(int socketTimeout, int connectionTimeout) {
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
        createClientWithoutSSL();
    }

    public CommonHttpClient(int socketTimeout, int connectionTimeout, String contentType) {
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
        this.contentType = contentType;
        createClientWithoutSSL();
    }

    public CommonHttpClient(int socketTimeout, int connectionTimeout, String contentEncoding, String contentType) {
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
        this.contentEncoding = contentEncoding;
        this.contentType = contentType;
        createClientWithoutSSL();
    }

    public static CommonHttpClient instance() {
        return new CommonHttpClient();
    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class CommonHttpClientHolder {
        // 静态初始化器，由JVM来保证线程安全
        private static CommonHttpClient instance = new CommonHttpClient();
    }

    //单例模式
    public static CommonHttpClient getInstance() {
        return CommonHttpClientHolder.instance;
    }

    public String performRequest(CommonData data) {
        HttpResult httpResult = execute(data);
        if (httpResult != null) {
            return httpResult.getResult();
        }
        return null;
    }

    public HttpResult execute(CommonData data) {
        HttpResult httpResult = new HttpResult();
        try {
            HttpResponse response = executeRequest(data);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    httpResult.setResult(EntityUtils.toString(entity, StandardCharsets.UTF_8.displayName()));
                }
                httpResult.setHeaders(getHeaders(response.getAllHeaders()));
            }
        } catch (Exception e) {
            log.error("Problem performing request: " + e.getMessage(), e);
            httpResult.setResult(e.getMessage());
        }
        return httpResult;
    }

    private List<HttpHeader> getHeaders(Header[] headers) {
        List<HttpHeader> httpHeaders = new ArrayList<>();
        if (headers != null && headers.length > 0) {
            for (Header header : headers) {
                HttpHeader httpHeader = new HttpHeader();
                httpHeader.setName(header.getName());
                httpHeader.setValue(header.getValue());
                httpHeaders.add(httpHeader);
            }
        }
        return httpHeaders;
    }

    public <T> CommonResponse<T> execute(CommonData commonData, ResponseBodyHandler<T> handler) throws XsyHttpException {
        try {
            HttpResponse httpResponse = executeRequest(commonData);
            CommonResponse<T> response = new CommonResponse<>();
            if (httpResponse != null) {
                response.setCode(httpResponse.getStatusLine().getStatusCode());
                response.setHeaders(getHeaders(httpResponse.getAllHeaders()));
                HttpEntity entity = httpResponse.getEntity();
                String data = null;
                if (entity != null) {
                    data = EntityUtils.toString(entity, StandardCharsets.UTF_8.displayName());
                }
                response.setData((T) handler.handle(data));
            }
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new XsyHttpException(e.getMessage(), Long.valueOf(100000L), e);
        }
    }

    public RkhdFile downFile(CommonData data) throws XsyHttpException {
        try {
            HttpResponse httpResponse = executeRequest(data);
            InputStream is = httpResponse.getEntity().getContent();
            String fileContent = IOUtil.toStringWithLimit(is);
            Header fileNameHeader = httpResponse.getFirstHeader("Content-Disposition");
            String fileName = null;
            if (fileNameHeader != null && fileNameHeader.getValue() != null) {
                fileName = fileNameHeader.getValue().replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
            }
            return new RkhdFile(fileName, fileContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new XsyHttpException(e.getMessage(), Long.valueOf(100000L), e);
        }
    }

    private HttpResponse executeRequest(CommonData data) throws IOException {
        CloseableHttpResponse closeableHttpResponse;
        HttpResponse httpResponse1;
        String urlStr;
        HttpGet get;
        HttpPost post;
        HttpPatch patch;
        HttpPut put;
        HttpDeleteWithEntity delete;
        HttpResponse response = null;
        switch (((CommonData) Optional.<CommonData>of(data).get()).getCall_type().toUpperCase()) {
            case "GET":
                urlStr = data.getCallString();
                get = new HttpGet(urlStr);
                for (HttpHeader httpHeader : data.getHeaderList()) {
                    if ("Authorization".equals(httpHeader.getName())) {
                        get.setHeader(httpHeader.getName(), httpHeader.getValue());
                        continue;
                    }
                    get.addHeader(httpHeader.getName(), httpHeader.getValue());
                }

                return (HttpResponse) this.client.execute((HttpUriRequest) get);
            case "POST":
                urlStr = data.getCallString();
                post = new HttpPost(urlStr);
                httpResponse1 = executeHttpEntityEnclosingRequestBase((HttpEntityEnclosingRequestBase) post, data);

                return httpResponse1;
            case "PATCH":
                urlStr = data.getCallString();
                patch = new HttpPatch(urlStr);
                httpResponse1 = executeHttpEntityEnclosingRequestBase((HttpEntityEnclosingRequestBase) patch, data);
                return httpResponse1;
            case "PUT":
                urlStr = data.getCallString();
                put = new HttpPut(urlStr);
                httpResponse1 = executeHttpEntityEnclosingRequestBase((HttpEntityEnclosingRequestBase) put, data);
                return httpResponse1;
            case "DELETE":
                urlStr = data.getCallString();
                delete = new HttpDeleteWithEntity(urlStr);
                httpResponse1 = executeHttpEntityEnclosingRequestBase(delete, data);
                return httpResponse1;
        }
        String msg = "Unknown call type: [" + data.getCall_type() + "]";
        log.error(msg);
        throw new IOException(msg);
    }

    private HttpResponse executeHttpEntityEnclosingRequestBase(HttpEntityEnclosingRequestBase request, CommonData data) throws IOException {
        for (Map.Entry<String, String> entry : data.getHeaders().entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
        if (data.getFormData().size() != 0) {
            if ("urlEncoded".equals(data.getFormType())) {

                UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(data.getFormData()), this.contentEncoding);
                request.setEntity((HttpEntity) postEntity);
            } else {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                for (Map.Entry<String, Object> entry : data.getFormData().entrySet()) {
                    if (entry.getValue() instanceof RkhdFile) {
                        RkhdFile file = (RkhdFile) entry.getValue();
                        if (file.getFileName() == null) {
                            throw new IOException("RkhdFile name can not be null");
                        }
                        builder.addBinaryBody(entry.getKey(), file.getFileContent().getBytes("UTF-8"), ContentType.create("multipart/form-data"), file.getFileName());
                        continue;
                    }
                    builder.addTextBody(entry.getKey(), entry.getValue().toString());
                }

                request.setEntity(builder.build());
            }
        } else {
            StringEntity se = new StringEntity(data.getBody(), this.contentEncoding);
            se.setContentType(this.contentType);
            se.setContentEncoding((Header) new BasicHeader("Content-Encoding", this.contentEncoding));
            request.setEntity((HttpEntity) se);
        }
        data.setCallString(request.getURI().toString());
        return (HttpResponse) this.client.execute((HttpUriRequest) request);
    }

    private List<NameValuePair> getParam(Map parameterMap) {
        List<NameValuePair> param = new ArrayList<>();
        Iterator<Map.Entry> it = parameterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry parmEntry = it.next();
            Object value = parmEntry.getValue();
            if (value == null) {
                value = "";
            }
            param.add(new BasicNameValuePair((String) parmEntry.getKey(), value
                    .toString()));
        }
        return param;
    }

    public void close() {
        try {
            this.client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createClientWithoutSSL() {
        try {
            this.config = RequestConfig.custom().setConnectTimeout(this.connectionTimeout).setSocketTimeout(this.socketTimeout).build();

            SSLContext sslContext = (new SSLContextBuilder()).loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            this.client = HttpClients.custom().setSSLSocketFactory((LayeredConnectionSocketFactory) sslsf).setDefaultRequestConfig(this.config).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void createSSLClient() {
        this.config = RequestConfig.custom().setConnectTimeout(this.connectionTimeout).setSocketTimeout(this.socketTimeout).build();
        this.client = HttpClients.custom().setDefaultRequestConfig(this.config).build();
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
