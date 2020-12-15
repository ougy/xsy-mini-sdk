package com.rkhd.platform.sdk.model;

import java.util.List;

public class BatchOperateResult {
    private Long code;
    private Boolean success;
    private String errorMessage;
    private List<OperateResult> operateResults;

    public Long getCode() {
        return this.code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

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

    public List<OperateResult> getOperateResults() {
        return this.operateResults;
    }

    public void setOperateResults(List<OperateResult> operateResults) {
        this.operateResults = operateResults;
    }
}