package com.api_gateway.minisidekiq.Worker;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class Worker {

    private final StringRedisTemplate redis;

    public Worker(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @PostConstruct
    public void startWorkers() {
        for (int i = 0; i < 5; i++) {
            workerThread();
        }
    }

    @Async("workerExecutor")
    public void workerThread() {
        while (true) {
            String job = redis.opsForList()
                    .rightPop("jobs", 1, TimeUnit.SECONDS);

            if (job == null) continue;

            System.out.println(
                    Thread.currentThread().getName() + " processed: " + job
            );
        }
    }
}
