package com.fm.job.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fomin
 * @date 2019-11-02
 */
@Getter
@Setter
public final class ZkConfig {
    /**
     * 连接Zookeeper服务器的列表.
     * 包括IP地址和端口号.
     * 多个地址用逗号分隔.
     * 如: host1:2181,host2:2181
     */
    private String serverLists = "127.0.0.1:2181";

    /**
     * 命名空间.
     */
    private String namespace = "elastic-scheduler";

    /**
     * 等待重试的间隔时间的初始值.
     * 单位毫秒.
     */
    private int baseSleepTimeMilliseconds = 1000;

    /**
     * 等待重试的间隔时间的最大值.
     * 单位毫秒.
     */
    private int maxSleepTimeMilliseconds = 3000;

    /**
     * 最大重试次数.
     */
    private int maxRetries = 3;

    /**
     * 会话超时时间.
     * 单位毫秒.
     */
    private int sessionTimeoutMilliseconds;

    /**
     * 连接超时时间.
     * 单位毫秒.
     */
    private int connectionTimeoutMilliseconds;

    /**
     * 连接Zookeeper的权限令牌.
     * 缺省为不需要权限验证.
     */
    private String digest;

    /** 创建一个 ZookeeperConfiguration */
    public ZookeeperConfiguration build() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(serverLists, namespace);
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);
        zookeeperConfiguration.setDigest(digest);
        zookeeperConfiguration.setMaxRetries(maxRetries);
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
        zookeeperConfiguration.setSessionTimeoutMilliseconds(sessionTimeoutMilliseconds);
        return zookeeperConfiguration;
    }
}
