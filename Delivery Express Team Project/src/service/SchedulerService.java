package com.group11.assignment5.service;

import java.time.Duration;

public interface SchedulerService {
    String[] scheduleTask(Runnable task, Duration interval);
    void cancelFutureScheduler();
}
