package de.hanbei.rxsearch.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;

public interface Measured {

    String SEARCHER_METRICS = "searcher.metrics";

    default MetricRegistry getMetricRegistry() {
        return SharedMetricRegistries.getOrCreate(SEARCHER_METRICS);
    }
}
