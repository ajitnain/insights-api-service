package com.example.insights.repository;

import com.example.insights.domain.MetricRows;
import com.example.insights.domain.MetricsQuery;

/** Reads metrics from the backing store. */
public interface MetricsRepository {

    /**
     * Returns rows keyed by interval start, plus totals for the whole range.
     *
     * <p>The store groups in the caller's time zone. Empty intervals are omitted.
     */
    MetricRows fetchMetrics(MetricsQuery query);
}
