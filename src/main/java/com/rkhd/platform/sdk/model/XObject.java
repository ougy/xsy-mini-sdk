package com.rkhd.platform.sdk.model;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XObject implements Serializable {
    private static final long serialVersionUID = -7434863464422069186L;
    protected Long id;
    private String apiKey;
    private Map<String, Object> _updateProperties = new HashMap<>();

    private Map<String, Object> _properties = new HashMap<>();

    protected XObject(String apiKey) {
        this.apiKey = apiKey;
    }

    public <T> T getAttribute(String apiKey) {
        apiKey = StringUtils.trim(apiKey);
        return (T) this._properties.get(apiKey);
    }

    public void setAttribute(String apiKey, Object value) {
        apiKey = StringUtils.trim(apiKey);
        this._properties.put(apiKey, value);
        this._updateProperties.put(apiKey, value);
    }

    Map<String, Object> _getUpdateProperties() {
        return this._updateProperties;
    }

    void _clearUpdateProperties() {
        this._updateProperties.clear();
    }

    void _initProperties(Map<String, Object> properties) {
        this._properties = properties;
    }

    Map<String, Object> _getProperties() {
        return this._properties;
    }

    public Long getId() {
        return getAttribute("id");
    }

    public void setId(Long id) {
        setAttribute("id", id);
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public Set<String> getAttributeNames() {
        return this._properties.keySet();
    }

    public String toString() {
        return JSON.toJSONString(this._properties);
    }
}