package com.api_gateway.minisidekiq.Worker;
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

    @Async("workerExecutor")
    public void workerThread() {
        while (true) {
            String job = redis.opsForList()
                    .rightPop("jobs", 0, TimeUnit.SECONDS);

            System.out.println("Job processed " + job);
        }
    }
}
