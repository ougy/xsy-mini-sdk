package com.rkhd.platform.sdk.bulkapi.bulkapicallback;

import com.rkhd.platform.sdk.exception.ScriptBusinessException;

public interface BulkApiCallBack {
    void execute(BulkApiCallBackRequest paramBulkApiCallBackRequest) throws ScriptBusinessException;
}