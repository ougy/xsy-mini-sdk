package com.rkhd.platform.sdk.http;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.platform.sdk.common.OauthConfig;
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
    private int socketTimeout = 100000;
    private int connectionTimeout = 100000;

    private String userName;
    private String password;
    private String securityCode;
    private String clientId;
    private String clientSecret;
    private String accessToken;

    @Deprecated
    public RkhdHttpClient() throws IOException {
        this.client = new CommonHttpClient();
        constructorImpl();
    }

    private void constructorImpl() {
        OauthConfig oauthConfig = new OauthConfig();
        this.userName = oauthConfig.getUserName();
        this.password = oauthConfig.getPassword();
        this.securityCode = oauthConfig.getSecurityCode();
        this.clientId = oauthConfig.getClientId();
        this.clientSecret = oauthConfig.getClientSecret();
        this.client.setContentEncoding(this.contentEncoding);
        this.client.setContentType(this.contentType);
        String oauthUrl = oauthConfig.getOauthUrl() + "?grant_type=password&client_id=" + this.clientId + "&client_secret=" + this.clientSecret + "&username=" + this.userName + "&password=" + this.password + this.securityCode;

        CommonData commonData = new CommonData();
        commonData.setCall_type("GET");
        commonData.setCallString(oauthUrl);
        HttpResult result = this.client.execute(commonData);
        if (result != null && StringUtils.isNotBlank(result.getResult())) {
            JSONObject jsonObject = JSONObject.parseObject(result.getResult());
            if (jsonObject.containsKey("access_token")) {
                this.accessToken = jsonObject.getString("access_token");
            } else {
                if (jsonObject.containsKey("error_description")) {
                    log.error(jsonObject.getString("error_description"));
                } else {
                    log.error(jsonObject.toString());
                }
                System.exit(1);
            }
        } else {
            log.error("can not get the accessToken,please check your config");
        }
    }

    public static RkhdHttpClient instance() throws IOException {
        return new RkhdHttpClient();
    }

    @Deprecated
    public String performRequest(RkhdHttpData data) throws IOException {
        log.info(data.toString());
        CommonData commonData = rkhdDataToCommonData(data);
        return this.client.performRequest(commonData);
    }

    public RkhdFile downFile(RkhdHttpData data) throws XsyHttpException {
        log.info(data.toString());
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