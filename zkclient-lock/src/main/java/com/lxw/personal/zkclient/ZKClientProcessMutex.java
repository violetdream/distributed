package com.lxw.personal.zkclient;

import org.I0Itec.zkclient.ZkClient;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2019/7/16/0016
 * Create by 刘仙伟
 */
public class ZKClientProcessMutex extends BaseDistributedLock {
    private final String basePath;
    private final ConcurrentMap<Thread, LockData> threadData=new ConcurrentHashMap();
    private static final String LOCK_NAME = "lock-";

    public ZKClientProcessMutex(ZkClient zkClient, String basePath) {
        super(zkClient, basePath, LOCK_NAME);
        this.basePath=basePath;
    }

    /**
     * 获取锁，不设置超时间
     * @throws Exception
     */
    public void acquire() throws Exception {
        if (!this.internalLock(-1L, (TimeUnit)null)) {
            throw new IOException("Lost connection while trying to acquire lock: " + this.basePath);
        }
    }

    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return this.internalLock(time, unit);
    }

    private boolean internalLock(long time, TimeUnit unit) throws Exception {
        Thread currentThread = Thread.currentThread();
        ZKClientProcessMutex.LockData lockData = (ZKClientProcessMutex.LockData)this.threadData.get(currentThread);
        if (lockData != null) {
            lockData.lockCount.incrementAndGet();
            return true;
        } else {
            String lockPath = attemptLock(time, unit);
            if (lockPath != null) {
                ZKClientProcessMutex.LockData newLockData = new ZKClientProcessMutex.LockData(currentThread, lockPath);
                this.threadData.put(currentThread, newLockData);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 释放锁
     * @throws Exception
     */
    public void release() throws Exception {
        Thread currentThread = Thread.currentThread();
        ZKClientProcessMutex.LockData lockData = (ZKClientProcessMutex.LockData)this.threadData.get(currentThread);
        if (lockData == null) {
            throw new IllegalMonitorStateException("You do not own the lock: " + this.basePath);
        } else {
            int newLockCount = lockData.lockCount.decrementAndGet();
            if (newLockCount <= 0) {
                if (newLockCount < 0) {
                    throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + this.basePath);
                } else {
                    try {
                        releaseLock(lockData.lockPath);
                    } finally {
                        this.threadData.remove(currentThread);
                    }
                }
            }
        }
    }

    private static class LockData {
        final Thread owningThread;
        final String lockPath;
        final AtomicInteger lockCount;

        private LockData(Thread owningThread, String lockPath) {
            this.lockCount = new AtomicInteger(1);
            this.owningThread = owningThread;
            this.lockPath = lockPath;
        }
    }
}
