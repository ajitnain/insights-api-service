package com.example.insights.domain;

import java.time.Instant;
import java.util.Map;

/** One row of the answer. */
public record MetricPeriod(Instant start, Map<Metric, IntervalMetric> values) {

    public MetricPeriod {
        values = Map.copyOf(values);
    }

    // Worst error across this row's metrics.
    public double customerCountEstimateError() {
        return values.values().stream()
                .mapToDouble(value -> value.customers().relativeError())
                .max()
                .orElse(0);
    }
}
