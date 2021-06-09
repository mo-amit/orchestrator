package com.ingestyon.orchestrator.scheduler;

import com.ingestyon.orchestrator.processor.BatchProcessor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class BaseScheduler {

    protected ScheduledExecutorService scheduledExecutorService = null;

    public abstract  ScheduledFuture schedule(Runnable inRunnable, long numSeconds);
    public abstract  ScheduledFuture schedule(Runnable inRunnable, long numSeconds, int numThreads);



}
