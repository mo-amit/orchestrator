package com.ingestyon.orchestrator.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BatchScheduler extends BaseScheduler{


     public ScheduledFuture schedule(Runnable inRunnable, long numSeconds){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        return scheduledExecutorService.scheduleAtFixedRate(inRunnable,0,numSeconds, TimeUnit.SECONDS);

    }
    public ScheduledFuture schedule(Runnable inRunnable, long numSeconds, int numThreads){
        scheduledExecutorService = Executors.newScheduledThreadPool(numThreads);
        return scheduledExecutorService.scheduleAtFixedRate(inRunnable,0,numSeconds, TimeUnit.SECONDS);

    }



}
