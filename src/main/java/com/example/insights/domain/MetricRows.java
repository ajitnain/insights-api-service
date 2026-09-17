package com.example.insights.domain;

import java.time.Instant;
import java.util.Map;

/** Store result: interval rows plus totals for the full range. */
public record MetricRows(Map<Instant, Map<Metric, IntervalMetric>> byInterval, Map<Metric, IntervalMetric> totals) {
}
