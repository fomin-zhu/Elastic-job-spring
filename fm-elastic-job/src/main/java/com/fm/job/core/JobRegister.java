package com.fm.job.core;

import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.fm.job.annotation.ElasticScheduledJob;
import com.fm.job.api.AbsSingleJob;
import com.fm.job.config.JobConfig;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fomin
 * @date 2019-11-02
 */
public class JobRegister implements ApplicationListener<ContextRefreshedEvent> {
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Autowired
    private CoordinatorRegistryCenter registryCenter;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialized.compareAndSet(false, true)) {
            initialize((ConfigurableApplicationContext) event.getApplicationContext());
        }
    }

    /**
     * 加载所有job, 从 ApplicationContext 中扫描 scheduler
     */
    private void initialize(ConfigurableApplicationContext applicationContext) {
        for (Object bean : applicationContext.getBeansWithAnnotation(ElasticScheduledJob.class).values()) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
            ElasticScheduledJob annotation = AnnotationUtils.findAnnotation(targetClass, ElasticScheduledJob.class);

            if (annotation != null) {
                JobConfig jobConfig = JobConfig.newSingle(annotation.cron(), (AbsSingleJob) bean);
                JobScheduler scheduler = buildScheduler(jobConfig);
                scheduler.init();
                applicationContext.getBeanFactory().registerSingleton(targetClass.getCanonicalName() + "Scheduler", scheduler);
            }
        }
    }

    private JobScheduler buildScheduler(JobConfig jobConfig) {
        return NewJobBuilder.newJobBuilder()
                .setRegCenter(registryCenter)
                .build(jobConfig);
    }
}
