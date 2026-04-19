package com.api_gateway.minisidekiq.Worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Manager {
    private  final Processor processor;
    private final StringRedisTemplate redis;

    public Manager(Processor processor, StringRedisTemplate redis) {
        this.processor = processor;
        this.redis = redis;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWorkers() {
        System.out.println("starting workers");

        for (int i = 0; i < 5; i++) {
            processor.runWorkerLoop();
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

}
