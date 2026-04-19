package com.api_gateway.minisidekiq.Controller;

import com.api_gateway.minisidekiq.dto.WorkInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class WorkerApi {

    private final StringRedisTemplate redis;

    public WorkerApi(StringRedisTemplate redis){
        this.redis = redis;
    }

    @PostMapping("/pushwork")
    public String pushApi(@RequestBody WorkInfo work) {

        String jobId = UUID.randomUUID().toString();

        Map<String, String> data = new HashMap<>();
        data.put("status", "Pending");
        data.put("timestamp", String.valueOf(java.time.Instant.now().getEpochSecond()));
        data.put("info", work.toString());

        redis.opsForList().leftPush("jobs", jobId);
        redis.opsForHash().putAll(jobId, data);
        redis.expire( jobId, 1, TimeUnit.DAYS);

        return jobId;
    }
}
