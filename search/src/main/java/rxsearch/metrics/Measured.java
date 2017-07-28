package rxsearch.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;

public interface Measured {

    String SEARCHER_METRICS = "searcher.metrics";

    default MetricRegistry getMetricRegistry() {
        return SharedMetricRegistries.getOrCreate(SEARCHER_METRICS);
    }

    @NotNull
    default String metricName(Object... query) {
        return "searcher." + Joiner.on(".").join(query);
    }
}
