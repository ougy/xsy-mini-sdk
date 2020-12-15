package com.rkhd.platform.sdk.service;

import com.rkhd.platform.sdk.exception.BatchJobException;
import com.rkhd.platform.sdk.task.BatchJob;

import java.util.UUID;

public class BatchJobService {
    private static final int DEFAULT_SUB_JOB_DATA_SIZE = 100;
    private static final int MIN_SUB_JOB_DATA_SIZE = 50;
    private static final int MAX_SUB_JOB_DATA_SIZE = 500;
    private static BatchJobService batchJobService = new BatchJobService();

    public static BatchJobService instance() {
        return batchJobService;
    }

    public String addBatchJob(Class<? extends BatchJob> batchJobClass) throws BatchJobException {
        return addBatchJob(batchJobClass, 100);
    }

    public String addBatchJob(Class<? extends BatchJob> batchJobClass, int subJobDataSize) throws BatchJobException {
        if (batchJobClass == null) {
            throw new BatchJobException("class can not be null");
        }
        if (batchJobClass.getName().equals(BatchJob.class.getName())) {
            throw new BatchJobException("class can not be 'BatchJob.class'");
        }
        if (subJobDataSize < 50) {
            throw new BatchJobException("subJobDataSize must greater than 50");
        }
        if (subJobDataSize > 500) {
            throw new BatchJobException("subJobDataSize must less than 500");
        }
        return UUID.randomUUID().toString();
    }
}