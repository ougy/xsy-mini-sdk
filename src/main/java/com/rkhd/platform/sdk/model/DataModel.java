package com.rkhd.platform.sdk.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataModel implements Serializable {
    private Map<String, Object> dataMap;

    public DataModel(Map<String, Object> dataMap) {
        if (dataMap == null) {
            dataMap = new HashMap<>();
        }
        this.dataMap = dataMap;
    }

    public void setAttribute(String key, Object value) {
        this.dataMap.put(key, value);
    }

    public Object getAttribute(String key) {
        return this.dataMap.get(key);
    }

    public Set<String> getAttributeNames() {
        return this.dataMap.keySet();
    }

    public Boolean getBoolean(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : Boolean.valueOf(Boolean.parseBoolean(obj.toString()));
    }

    public Byte getByte(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : Byte.valueOf(Byte.parseByte(obj.toString()));
    }

    public Short getShort(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : Short.valueOf(Short.parseShort(obj.toString()));
    }

    public Integer getInteger(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : Integer.valueOf(Integer.parseInt(obj.toString()));
    }

    public Float getFloat(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : Float.valueOf(Float.parseFloat(obj.toString()));
    }

    public Long getLong(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : Long.valueOf(Long.parseLong(obj.toString()));
    }

    public Double getDouble(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : Double.valueOf(Double.parseDouble(obj.toString()));
    }

    public String getString(String key) {
        Object obj = this.dataMap.get(key);
        return (obj == null) ? null : obj.toString();
    }

    public String toString() {
        return JSON.toJSONString(this.dataMap);
    }
}