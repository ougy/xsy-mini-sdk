package com.rkhd.platform.sdk.model;

import java.util.ArrayList;
import java.util.List;

public class QueryResult<T> {
    private Long code;
    private Boolean success;
    private String errorMessage;
    private Long totalCount;
    private Long currentCount;
    private List<T> records = new ArrayList<>();

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getCurrentCount() {
        return this.currentCount;
    }

    public void setCurrentCount(Long currentCount) {
        this.currentCount = currentCount;
    }

    public List<T> getRecords() {
        return this.records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getCode() {
        return this.code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}