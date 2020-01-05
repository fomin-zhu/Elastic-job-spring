package com.fm.job.api;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fomin
 * @date 2019-11-02
 */
public abstract class AbsSingleJob implements SimpleJob {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(ShardingContext shardingContext) {
        String name = getClass().getSimpleName();
        long startTime = System.currentTimeMillis();
        try {
            log.info("Job {} execute start, sharding context = {}", name, shardingContext);
            process(shardingContext);
            log.info("Job {} execute finish after {}ms", name, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Job " + name + " error occur", e);
            handleException();
        }
    }

    /**
     * 捕获异常（需要处理异常可重载handleException方法）和记录执行日志
     */
    protected void handleException() {

    }

    /**
     * 业务逻辑在这里实现
     */
    protected abstract void process(ShardingContext shardingContext);
}
