package com.api_gateway.minisidekiq.Worker;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class Processor {
    private final StringRedisTemplate redis;

    public Processor(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Async("workerExecutor")
    public void runWorkerLoop() {
        while (true) {
            String jobId = redis.opsForList()
                    .rightPopAndLeftPush("jobs", "JobsQueue", 1, TimeUnit.SECONDS);

            if (jobId == null) continue;
            System.out.println(jobId);
            try {
                redis.opsForHash().put(jobId, "status", "Running");
                redis.opsForHash().put(jobId, "timestamp", String.valueOf(java.time.Instant.now().getEpochSecond()));


                redis.opsForHash().put(jobId, "status", "Completed");
                System.out.println(Thread.currentThread().getName() + " processed " + jobId);

                redis.opsForList().remove("JobsQueue", 1, jobId);
            } catch (Exception e) {
                redis.opsForHash().put(jobId, "status", "Failed");
                redis.opsForHash().put(jobId, "response", e.getMessage());
                redis.opsForList().remove("JobsQueue", 1, jobId);
            }
        }

    }
}
