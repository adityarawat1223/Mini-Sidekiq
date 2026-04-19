package com.api_gateway.minisidekiq.Worker;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Scheduled(fixedDelay = 60000)
    public void Janitor() {

        java.util.List<String> activeJobs = redis.opsForList().range("JobsQueue", 0, -1);

        if (activeJobs != null) {
            for (String jobId : activeJobs) {
                Object statusObj = redis.opsForHash().get(jobId, "status");
                Object timeObj = redis.opsForHash().get(jobId, "timestamp");

                if (statusObj == null || timeObj == null) continue;

                String status = statusObj.toString();
                long timestamp = Long.parseLong(timeObj.toString());

                if (status.equals("Running") && (java.time.Instant.now().getEpochSecond() - timestamp >= 3600)) {
                    redis.opsForList().leftPush("Jobs", jobId);
                    redis.opsForList().remove("JobsQueue", 1, jobId);
                    redis.opsForHash().put(jobId, "status", "Pending");
                }
            }
        }
    }

    @Async("workerExecutor")
    public void workerThread() {
        while (true) {
            String jobId = redis.opsForList()
                    .rightPopAndLeftPush("Jobs", "JobsQueue", 1, TimeUnit.SECONDS);

            if (jobId == null) continue;
            try {
                redis.opsForHash().put(jobId, "status", "Running");
                redis.opsForHash().put(jobId, "timestamp", String.valueOf(java.time.Instant.now().getEpochSecond()));


                redis.opsForHash().put(jobId, "status", "Completed");
                redis.opsForList().remove("JobsQueue", 1, jobId);
            } catch (Exception e) {
                redis.opsForHash().put(jobId, "status", "Failed");
                redis.opsForHash().put(jobId, "response", e.getMessage());
                redis.opsForList().remove("JobsQueue", 1, jobId);
            }
        }
    }
}