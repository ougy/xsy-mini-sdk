package com.rkhd.platform.sdk.service;

import com.rkhd.platform.sdk.exception.LockServiceException;

public class LockService {
    private String name;

    public static LockService newLock(String lockName) {
        return new LockService(lockName);
    }

    private LockService(String lockName) {
        this.name = this.name;
    }

    public boolean lock() throws LockServiceException {
        return true;
    }

    public boolean lock(int timeout) throws LockServiceException {
        return true;
    }

    public boolean unLock() {
        return true;
    }
}