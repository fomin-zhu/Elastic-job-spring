package com.fm.job;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author fomin
 * @date 2019-11-02
 */
@SpringCloudApplication
public class SchedulerServer {
    public static void main(String[] args) {
        SpringApplication.run(SchedulerServer.class, args);
    }
}
