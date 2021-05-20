package com.budwk.starter.job;

import com.budwk.starter.common.constant.RedisConstant;
import org.nutz.integration.jedis.RedisService;
import org.nutz.lang.random.R;

/**
 * 判断是否可被执行,防止多实例重复执行
 *
 * @author wizzer@qq.com
 */
public abstract class JobService {

    protected RedisService redisService;
    protected String instanceId;

    public void init(RedisService redisService) {
        this.redisService = redisService;
        this.instanceId = R.UU32();
    }

    public boolean canExecute(String name, String jobId) {
        String redisValue = redisService.get(RedisConstant.JOB_EXECUTE + name);
        if (redisValue == null) {
            redisService.setex(RedisConstant.JOB_EXECUTE + name, 1, instanceId + "_" + jobId);
        }
        redisValue = redisService.get(RedisConstant.JOB_EXECUTE + name);
        if ((instanceId + "_" + jobId).equalsIgnoreCase(redisValue)) {
            return true;
        }
        return false;
    }

}
