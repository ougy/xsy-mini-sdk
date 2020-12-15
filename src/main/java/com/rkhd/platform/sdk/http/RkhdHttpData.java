package com.rkhd.platform.sdk.http;


import com.rkhd.platform.sdk.common.OauthConfig;

import java.util.HashMap;
import java.util.Map;

public class RkhdHttpData {
    private String call_type;
    private String body = "";
    private String callString;
    private HashMap<String, Object> formData = new HashMap<>();
    private Map<String, String> headerMap = new HashMap<>();
    private OauthConfig domainNameConfig = new OauthConfig();

    public RkhdHttpData(String call_type, String body, String callString) {
        this.call_type = call_type;
        this.body = body;
        if (callString.startsWith("/")) {
            this.callString = this.domainNameConfig.getDomain() + callString;
        } else {
            this.callString = this.domainNameConfig.getDomain() + "/" + callString;
        }
    }

    public String getCall_type() {
        return this.call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCallString() {
        return this.callString;
    }

    public void setCallString(String callString) {
        if (callString.startsWith("/")) {
            this.callString = this.domainNameConfig.getDomain() + callString;
        } else {
            this.callString = this.domainNameConfig.getDomain() + "/" + callString;
        }
    }

    public Map<String, Object> getFormData() {
        return this.formData;
    }

    public void putFormData(String key, Object value) {
        this.formData.put(key, value);
    }

    public void setFormData(HashMap<String, Object> formData) {
        this.formData = formData;
    }

    public Map<String, String> getHeaderMap() {
        return this.headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public void putHeader(String name, String value) {
        this.headerMap.put(name, value);
    }

    public String toString() {
        return "call_type:" + this.call_type + " callString:" + this.callString + " body:" + this.body + " formData:" + this.formData
                .toString();
    }

    public static class Builder {
        private String call_type;
        private String body = "";
        private String callString;
        private HashMap<String, Object> formData = new HashMap<>();
        private Map<String, String> headerMap = new HashMap<>();

        public Builder callType(String callType) {
            this.call_type = callType;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder callString(String callString) {
            this.callString = callString;
            return this;
        }

        public Builder header(String name, String value) {
            this.headerMap.put(name, value);
            return this;
        }

        public Builder header(Map<String, String> headerMap) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Builder formData(String name, Object value) {
            this.formData.put(name, value);
            return this;
        }

        public Builder formData(Map<String, Object> formData) {
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                formData(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public RkhdHttpData build() {
            return new RkhdHttpData(this.call_type, this.body, this.callString, this.formData, this.headerMap);
        }
    }

    private RkhdHttpData(String call_type, String body, String callString, HashMap<String, Object> formData, Map<String, String> headerMap) {
        this.call_type = call_type;
        this.body = body;
        this.callString = this.domainNameConfig.getDomain() + callString;
        this.formData = formData;
        this.headerMap = headerMap;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public RkhdHttpData() {
    }
}