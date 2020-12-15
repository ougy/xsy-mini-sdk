package com.rkhd.platform.sdk.service;

import com.rkhd.platform.sdk.exception.AsyncTaskException;
import com.rkhd.platform.sdk.task.FutureTask;

import java.util.UUID;

public class FutureTaskService {
    private static FutureTaskService service = new FutureTaskService();

    public static FutureTaskService instance() {
        return service;
    }

    public String addFutureTask(Class<? extends FutureTask> futrureClass, String param) throws AsyncTaskException {
        return UUID.randomUUID().toString();
    }
}