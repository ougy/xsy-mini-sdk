package com.rkhd.platform.sdk.http;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.platform.sdk.common.OauthConfig;
import com.rkhd.platform.sdk.common.TokenCache;
import com.rkhd.platform.sdk.exception.XsyHttpException;
import com.rkhd.platform.sdk.http.handler.ResponseBodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class RkhdHttpClient {
    private CommonHttpClient client;
    private String contentEncoding = "UTF-8";
    private String contentType = "application/json";

    private String userName;
    private String password;
    private String securityCode;
    private String clientId;
    private String clientSecret;
    private String accessToken;

    public RkhdHttpClient() {
        constructorImpl();
    }

    private void constructorImpl() {
        OauthConfig oauthConfig = new OauthConfig();
        this.userName = oauthConfig.getUserName();
        this.password = oauthConfig.getPassword();
        this.securityCode = oauthConfig.getSecurityCode();
        this.clientId = oauthConfig.getClientId();
        this.clientSecret = oauthConfig.getClientSecret();

        this.client = new CommonHttpClient(Integer.valueOf(oauthConfig.getSocketTimeout()), Integer.valueOf(oauthConfig.getConnectionTimeout()));
        this.client.setContentEncoding(this.contentEncoding);
        this.client.setContentType(this.contentType);
        String accessToken = TokenCache.getKey("accessToken");
        if (accessToken == null) {
            String oauthUrl = oauthConfig.getOauthUrl() + "?grant_type=password&client_id=" + this.clientId + "&client_secret=" + this.clientSecret + "&username=" + this.userName + "&password=" + this.password + this.securityCode;

            CommonData commonData = new CommonData();
            commonData.setCall_type("GET");
            commonData.setCallString(oauthUrl);
            HttpResult result = this.client.execute(commonData);
            if (result != null && StringUtils.isNotBlank(result.getResult())) {
                JSONObject jsonObject = JSONObject.parseObject(result.getResult());
                if (jsonObject.containsKey("access_token")) {
                    accessToken = jsonObject.getString("access_token");
                    log.debug("从接口获取token:" + accessToken);
                    TokenCache.setKey("accessToken", accessToken);
                    this.accessToken = accessToken;
                } else {
                    if (jsonObject.containsKey("error_description")) {
                        log.error(jsonObject.getString("error_description"));
                    } else {
                        log.error(jsonObject.toString());
                    }
                }
            } else {
                log.error("can not get the accessToken,please check your config");
            }
        }
    }

    public static RkhdHttpClient instance() throws IOException {
        return new RkhdHttpClient();
    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class RkhdHttpClientHolder {
        // 静态初始化器，由JVM来保证线程安全
        private static RkhdHttpClient instance = new RkhdHttpClient();
    }

    //单例模式
    public static RkhdHttpClient getInstance() {
        return RkhdHttpClient.RkhdHttpClientHolder.instance;
    }

    public String performRequest(RkhdHttpData data) throws IOException {
        log.debug(data.toString());
        CommonData commonData = rkhdDataToCommonData(data);
        return this.client.performRequest(commonData);
    }

    public RkhdFile downFile(RkhdHttpData data) throws XsyHttpException {
        log.debug(data.toString());
        CommonData commonData = rkhdDataToCommonData(data);
        return this.client.downFile(commonData);
    }

    public <T> T execute(RkhdHttpData data, ResponseBodyHandler<T> handler) throws XsyHttpException {
        try {
            String res = performRequest(data);
            return (T) handler.handle(res);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new XsyHttpException(e.getMessage(), Long.valueOf(100000L), e);
        }
    }

    private CommonData rkhdDataToCommonData(RkhdHttpData data) {
        CommonData commonData = new CommonData();
        commonData.setCallString(data.getCallString());
        commonData.setCall_type(data.getCall_type());
        commonData.setBody(data.getBody());
        commonData.putFormDataAll(data.getFormData());
        if (data.getFormData().size() == 0) {
            commonData.putHeader("Content-Type", this.contentType);
        }
        commonData.putHeader("Authorization", "Bearer " + this.accessToken);
        Map<String, String> headerMap = data.getHeaderMap();
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                commonData.putHeader(entry.getKey(), entry.getValue());
            }
        }
        return commonData;
    }

    @Deprecated
    public void close() {
        this.client.close();
    }

    @Deprecated
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    @Deprecated
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}