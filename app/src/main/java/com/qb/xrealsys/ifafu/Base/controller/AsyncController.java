package com.qb.xrealsys.ifafu.Base.controller;

import java.util.concurrent.ExecutorService;

/**
 * Created by sky on 16/04/2018.
 */

public class AsyncController {

    protected ExecutorService threadPool;

    public AsyncController(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
