package com.fm.job.core;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.fm.job.config.ZkConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fomin
 * @date 2019-11-02
 */
@Configuration
@ConditionalOnProperty(prefix = "elastic-job.zk", name = {"namespace", "serverLists"})
public class JobAutoConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "elastic-job.zk")
    public ZkConfig zkConfig() {
        return new ZkConfig();
    }

    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter regCenter(ZkConfig zkConfig) {
        System.out.println(zkConfig);
        return new ZookeeperRegistryCenter(zkConfig.build());
    }

    @Bean
    public JobRegister jobRegistrar() {
        return new JobRegister();
    }
}
