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
    public void PushApi(@RequestBody WorkInfo Work) {
        String JobId = UUID.randomUUID().toString();
        Map<String, String> HashMap = new HashMap<>();
        redis.opsForList().leftPush("Jobs",JobId);
        HashMap.put("status" ,"Pending");
        HashMap.put("timestamps"  , Long.toString( java.time.Instant.now().getEpochSecond()));
        HashMap.put("Info",Work.toString());
        redis.opsForHash().putAll(JobId,HashMap);
        //Dead Man switch
        redis.expire(JobId,1, TimeUnit.DAYS);
    }
}
