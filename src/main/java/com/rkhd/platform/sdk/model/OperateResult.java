package com.rkhd.platform.sdk.model;

public class OperateResult {
    private Long code;
    private Long dataId;
    private String errorMessage;
    private Boolean success;

    public Long getDataId() {
        return this.dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getCode() {
        return this.code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}