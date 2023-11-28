package com.group11.assignment5.service;

import com.group11.assignment5.message.OtherMessageType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Service
@AllArgsConstructor
public class SchedulerServiceImpl implements SchedulerService{
    @Autowired
    private final TaskScheduler taskScheduler; //this is from config/SchedulerConfig

    private static ScheduledFuture<?> scheduleFuture;
    private final Set<ScheduledFuture<?>> scheduledTasks = new HashSet<>();

    private static Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Override
    public synchronized String[] scheduleTask(Runnable task, Duration interval) {
        cancelFutureScheduler();

        if(scheduleFuture != null && scheduleFuture.isCancelled()) {
            scheduleFuture.cancel(true);
        }
        scheduleFuture = taskScheduler.scheduleAtFixedRate(task, interval);
        scheduledTasks.add(scheduleFuture);
        return new String[] {String.valueOf(interval), String.valueOf(task.getClass())};
    }
    @Override
    public void cancelFutureScheduler() {
        scheduledTasks.forEach((future) -> {
            future.cancel(false);
        });


    }
}
