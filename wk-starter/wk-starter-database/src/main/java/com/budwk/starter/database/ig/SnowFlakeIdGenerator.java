package com.budwk.starter.database.ig;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.budwk.starter.common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Tasks;
import org.nutz.lang.random.R;
import org.nutz.ioc.Ioc;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
@Slf4j
public class SnowFlakeIdGenerator implements IdGenerator {
    @Inject("refer:$ioc")
    private Ioc ioc;
    private RedisService redisService;
    private final int WorkerIdBitLength = 10;
    // 2的WorkerIdBitLength 次方
    private final int MaxWorkerId = (int) Math.pow(2, WorkerIdBitLength) - 1;

    public void init() {
        String uuid = R.UU32();
        // 定时任务获取唯一 workerId
        Tasks.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                setInitWorkerId(uuid);
            }
        }, 10,30, TimeUnit.SECONDS);
    }

    public void setInitWorkerId(String uuid) {
        try {
            if(redisService == null) {
                redisService = ioc.get(RedisService.class);
            }
            long workerId = Long.parseLong(Strings.sNull(redisService.get(RedisConstant.IG_WORKERID + uuid), "0"));
            if (workerId >= 1) {
                redisService.setex(RedisConstant.IG_WORKERID + uuid, 60, "" + workerId);
            } else {
                workerId = redisService.incr(RedisConstant.IG_WORKERID + "workerid");
                if (workerId >= MaxWorkerId) {
                    redisService.set(RedisConstant.IG_WORKERID + "workerid", "1");
                    workerId = 1;
                }
                redisService.setex(RedisConstant.IG_WORKERID + uuid, 60, "" + workerId);
                IdGeneratorOptions options = new IdGeneratorOptions((short) workerId);
                options.WorkerIdBitLength = (byte) WorkerIdBitLength;
                YitIdHelper.setIdGenerator(options);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String next() {
        return Strings.alignRight(YitIdHelper.nextId(), 16, '0');
    }

    @Override
    public Object run(List<Object> fetchParam) {
        return next();
    }

    @Override
    public String fetchSelf() {
        return "snowflake";
    }
}
