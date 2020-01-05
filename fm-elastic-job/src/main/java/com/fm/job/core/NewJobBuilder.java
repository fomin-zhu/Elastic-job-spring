package com.fm.job.core;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.fm.job.config.JobConfig;
import com.fm.job.listener.JobListeners;
import com.fm.job.util.ConditionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author fomin
 * @date 2019-11-02
 */
@Slf4j
public final class NewJobBuilder {
    private JobListeners listeners;
    private CoordinatorRegistryCenter regCenter;
    private JobEventConfiguration jobEventConfiguration;

    private NewJobBuilder() {
    }

    public static NewJobBuilder newJobBuilder() {
        return new NewJobBuilder();
    }

    /**
     * (non-Javadoc)
     */
    public NewJobBuilder setListeners(JobListeners elasticJobListeners) {
        this.listeners = elasticJobListeners;
        return this;
    }

    /**
     * (non-Javadoc)
     */
    public NewJobBuilder setRegCenter(CoordinatorRegistryCenter coordinatorRegistryCenter) {
        this.regCenter = coordinatorRegistryCenter;
        return this;
    }

    /**
     * (non-Javadoc)
     */
    public NewJobBuilder setJobEventConfiguration(JobEventConfiguration jobEventConfiguration) {
        this.jobEventConfiguration = jobEventConfiguration;
        return this;
    }

    private JobConfig checkJobConfig(JobConfig jobConfig) {
        ConditionUtil.assertNotNull(jobConfig, "jobConfig must not be null");
        ConditionUtil.assertNotNullAndEmpty(jobConfig.getCron(), "scheduler.cron must not be null");
        ConditionUtil.assertTrue(jobConfig.getElasticJob() == null || StringUtils.isEmpty(jobConfig.getSubClass()),
                "elastic scheduler or subClass must be set");

        if (StringUtils.isEmpty(jobConfig.getName())) {
            log.warn("reset jobName to {}", jobConfig.getSubClass());
            jobConfig.setName(jobConfig.getSubClass());
        }

        if (jobConfig.getShardingCount() < 1 || jobConfig.getShardingCount() > 10) {
            log.warn("reset shardingCount {} to 10", jobConfig.getShardingCount());
            jobConfig.setShardingCount(10);
        }

        if (jobConfig.getShardingCount() > 1) {
            log.warn("reset shardingCount {} to 1 when the scheduler is simple.", jobConfig.getShardingCount());
            jobConfig.setShardingCount(1);
        }
        return jobConfig;
    }

    /**
     * 构建 JobScheduler
     */
    public JobScheduler build(JobConfig jobConfig) {
        LiteJobConfiguration jobConfiguration = createJobConfiguration(checkJobConfig(jobConfig));
        ElasticJobListener[] l;
        if (listeners != null) {
            l = new ElasticJobListener[listeners.getListeners().size()];
            listeners.getListeners().toArray(l);
        } else {
            l = new ElasticJobListener[0];
        }

        if (jobEventConfiguration == null) {
            return jobConfig.getElasticJob() != null
                    ? new SpringJobScheduler(jobConfig.getElasticJob(), regCenter, jobConfiguration)
                    : new JobScheduler(regCenter, jobConfiguration, l);
        }
        return jobConfig.getElasticJob() != null
                ? new SpringJobScheduler(jobConfig.getElasticJob(), regCenter, jobConfiguration, jobEventConfiguration)
                : new JobScheduler(regCenter, jobConfiguration, jobEventConfiguration, l);
    }

    /**
     * 创建job的config
     */
    private LiteJobConfiguration createJobConfiguration(JobConfig config) {
        JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(config.getName(), config.getCron(), config.getShardingCount())
                .shardingItemParameters(config.getShardingParameters())
                .description(config.getDescription())
                .build();

        return LiteJobConfiguration
                .newBuilder(new SimpleJobConfiguration(coreConfig, config.getSubClass()))
                .jobShardingStrategyClass(config.getJobShardingStrategyClass())
                .maxTimeDiffSeconds(config.getMaxTimeDiffSeconds())
                .disabled(config.isDisabled())
                .monitorExecution(config.isMonitorExecution())
                .monitorPort(config.getMonitorPort())
                .reconcileIntervalMinutes(10)
                .overwrite(true)
                .build();
    }
}
