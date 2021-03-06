package com.rkhd.platform.sdk.http;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {
    private static final long serialVersionUID = 9012329275989964471L;
    private Map<String, String[]> parameter = (Map) new HashMap<>();
    private StringBuffer reader = new StringBuffer();

    public void putParameter(String key, String[] value) {
        if (StringUtils.isNotBlank(key)) {
            if (value != null) {
                this.parameter.put(key, value);
            } else {
                this.parameter.put(key, null);
            }
        }
    }

    public void setReader(StringBuffer readerString) {
        this.reader = readerString;
    }

    public String getParameter(String s) {
        String[] values = this.parameter.get(s);
        return (values == null || values.length == 0) ? null : values[0];
    }

    public String[] getParameterValues(String s) {
        String[] values = this.parameter.get(s);
        return (values == null || values.length == 0) ? null : values;
    }

    public Map<String, String[]> getParameterMap() {
        return this.parameter;
    }

    public StringBuffer getReader() {
        return this.reader;
    }

    public static void main(String[] args) {
        Object object1 = Integer.valueOf(111);
        Object object2 = Integer.valueOf(2);
        System.out.println(Integer.valueOf(object1.toString()).compareTo(Integer.valueOf(object2.toString())));
        System.out.println(object1.toString().compareTo(object2.toString()));
    }
}