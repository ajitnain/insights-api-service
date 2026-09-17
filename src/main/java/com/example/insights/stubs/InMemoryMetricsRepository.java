package com.example.insights.stubs;

import com.example.insights.domain.*;
import com.example.insights.repository.MetricsRepository;
import com.example.insights.util.IntervalUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

/** Fake Druid repository backed by the hourly traffic below. */
@Repository
public class InMemoryMetricsRepository implements MetricsRepository {

    private static final String EVENTS = """
            {
              "impressions":     { "events_per_hour": 5000, "customers_per_hour": 2500 },
              "clicks":          { "events_per_hour":  100, "customers_per_hour":   50 },
              "click_to_basket": { "events_per_hour":   25, "customers_per_hour":   12 }
            }
            """;

    private static final long STILL_ACTIVE_CUSTOMER_MINUTES = 10;

    private final Map<Metric, HourlyEvents> events = new EnumMap<>(Metric.class);

    public InMemoryMetricsRepository(ObjectMapper json) {
        Map<String, HourlyEvents> byMetricName = metricVsEvents(json);

        for (Metric metric : Metric.values()) {
            HourlyEvents hourly = byMetricName.get(metric.metricName());
            if (hourly == null) {
                throw new IllegalStateException("no traffic set up for " + metric.metricName());
            }
            events.put(metric, hourly);
        }
    }

    private static Map<String, HourlyEvents> metricVsEvents(ObjectMapper json) {
        try {
            return json.readValue(EVENTS, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("the traffic above is not valid json", e);
        }
    }

    @Override
    public MetricRows fetchMetrics(MetricsQuery query) {
        Interval interval = query.interval();
        ZoneId zone = query.zone();

        Instant now = Instant.now();

        Map<Instant, Map<Metric, IntervalMetric>> rows = new HashMap<>();

        for (Instant start : IntervalUtils.intervalStarts(query.range(), interval, zone)) {
            Instant end = IntervalUtils.intervalEnd(start, interval, zone);

            Instant readTo = end.isAfter(now) ? now : end;
            if (!start.isBefore(readTo)) {
                continue;
            }

            Map<Metric, IntervalMetric> row = new LinkedHashMap<>();
            for (Metric metric : query.metrics()) {
                CustomerSketch sketch = CustomerSketch.of(customerIds(metric, start, readTo));
                row.put(metric, new IntervalMetric(sketch, events(metric, start, readTo)));
            }
            rows.put(start, row);
        }
        return new MetricRows(rows, totals(query, rows));
    }

    // Totals are the same rows without time grouping. Events add up; customer sketches merge.
    private static Map<Metric, IntervalMetric> totals(MetricsQuery query,
                                                      Map<Instant, Map<Metric, IntervalMetric>> rows) {

        // A single row is already its own total.
        if (rows.size() == 1) {
            return rows.values().iterator().next();
        }

        Map<Metric, IntervalMetric> totals = new LinkedHashMap<>();

        for (Metric metric : query.metrics()) {
            List<CustomerSketch> sketches = new ArrayList<>(rows.size());
            long events = 0;

            for (Map<Metric, IntervalMetric> row : rows.values()) {
                sketches.add(row.get(metric).customerSketch());
                events += row.get(metric).eventCount();
            }
            totals.put(metric, new IntervalMetric(CustomerSketch.merge(sketches), events));
        }
        return totals;
    }

    private long events(Metric metric, Instant fromInclusive, Instant toExclusive) {
        return events.get(metric).eventsPerHour() * minutes(fromInclusive, toExclusive) / 60;
    }

    // Use stable customer ids so overlapping windows share people across interval edges.
    private List<String> customerIds(Metric metric, Instant fromInclusive, Instant toExclusive) {
        long perHour = events.get(metric).customersPerHour();

        long firstCustomer = perHour * minutes(Instant.EPOCH, fromInclusive) / 60;
        long customers = perHour * minutes(fromInclusive, toExclusive) / 60;
        long stillActive = Math.min(perHour * STILL_ACTIVE_CUSTOMER_MINUTES / 60, customers);

        List<String> ids = new ArrayList<>();
        for (long i = firstCustomer - stillActive; i < firstCustomer + customers; i++) {
            ids.add(metric + "-" + i);
        }
        return ids;
    }

    private static long minutes(Instant fromInclusive, Instant toExclusive) {
        return Duration.between(fromInclusive, toExclusive).toMinutes();
    }
}
