package com.fm.job.listener;

import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fomin
 * @date 2019-11-02
 */
public class JobListeners {
    @Getter
    private List<ElasticJobListener> listeners = new ArrayList<>();

    /**
     * 添加
     */
    public JobListeners addListener(ElasticJobListener elasticJobListener) {
        listeners.add(elasticJobListener);
        return this;
    }
}
