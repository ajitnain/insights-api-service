package com.example.insights.services;

import com.example.insights.domain.*;
import com.example.insights.repository.MetricsRepository;
import com.example.insights.util.IntervalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Answers one metrics request, filling in empty intervals with zeroes. */
@Service
@RequiredArgsConstructor
public class MetricsQueryService {

    private final MetricsRepository metrics;

    public MetricSeries execute(MetricsQuery query) {
        MetricRows rows = metrics.fetchMetrics(query);

        List<MetricPeriod> periods = toPeriods(query, rows.byInterval());

        return new MetricSeries(query.campaignId(), query.zone(), query.interval(),
                OffsetDateTime.now(query.zone()), periods, rows.totals());
    }

    private List<MetricPeriod> toPeriods(MetricsQuery query,
                                         Map<Instant, Map<Metric, IntervalMetric>> metricsByInterval) {

        Interval interval = query.interval();
        ZoneId zone = query.zone();
        List<MetricPeriod> periods = new ArrayList<>();

        for (Instant start : IntervalUtils.intervalStarts(query.range(), interval, zone)) {
            Map<Metric, IntervalMetric> found = metricsByInterval.getOrDefault(start, Map.of());
            Map<Metric, IntervalMetric> values = new LinkedHashMap<>();

            for (Metric metric : query.metrics()) {
                values.put(metric, found.getOrDefault(metric, IntervalMetric.zero()));
            }

            periods.add(new MetricPeriod(start, values));
        }
        return periods;
    }
}
