package com.fm.job.config;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.fm.job.api.AbsSingleJob;
import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.framework.AopProxyUtils;

/**
 * @author fomin
 * @date 2019-11-02
 */
@Getter
@Setter
public final class JobConfig {
    public static final String TYPE_SINGLE = "single";

    private String type;
    private String cron;
    private ElasticJob elasticJob;
    private String name;
    private String description;
    private String subClass;
    private int shardingCount;
    private String shardingParameters;
    private boolean stream;

    // lite config
    private boolean monitorExecution = true;
    private int maxTimeDiffSeconds = -1;
    private int monitorPort = -1;
    private String jobShardingStrategyClass = "";
    private boolean disabled;
    private boolean overwrite;
    private int reconcileIntervalMinutes = 10;

    private JobConfig() {}

    /** (non-Javadoc) */
    public static JobConfig newSingle(String cron, Class<? extends AbsSingleJob> clz) {
        JobConfig jobConfig = new JobConfig();
        jobConfig.type = TYPE_SINGLE;
        jobConfig.cron = cron;
        jobConfig.subClass = clz.getCanonicalName();
        return jobConfig;
    }

    /** (non-Javadoc) */
    public static <T extends AbsSingleJob> JobConfig newSingle(String cron, T job) {
        JobConfig jobConfig = new JobConfig();
        jobConfig.type = TYPE_SINGLE;
        jobConfig.cron = cron;
        jobConfig.elasticJob = job;
        if (jobConfig.getElasticJob() != null) {
            jobConfig.setSubClass(AopProxyUtils.ultimateTargetClass(job).getCanonicalName());
        }
        return jobConfig;
    }
}
