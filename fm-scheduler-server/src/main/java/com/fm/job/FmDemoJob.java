package com.fm.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.fm.job.annotation.ElasticScheduledJob;
import com.fm.job.api.AbsSingleJob;

/**
 *
 * @author fomin
 * @date 2019-11-02
 */
@ElasticScheduledJob(cron = "0 3 0/1 * * ? ")
public class FmDemoJob extends AbsSingleJob {


    @Override
    protected void process(ShardingContext shardingContext) {

    }
}
