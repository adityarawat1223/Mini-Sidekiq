package com.api_gateway.minisidekiq.Controller;

import com.api_gateway.minisidekiq.dto.WorkInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkerApi {

    private final StringRedisTemplate redis;

    public WorkerApi(StringRedisTemplate redis){
        this.redis = redis;
    }

    @PostMapping("/pushwork")
    public void PushApi(@RequestBody WorkInfo Work) {

        redis.opsForList().rightPush("jobs" , Integer.toString(Work.WorkId));
    };
}
