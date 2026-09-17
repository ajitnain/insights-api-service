package com.example.insights.controller.helper;

import com.example.insights.controller.dto.MetricsWindow;
import com.example.insights.domain.*;
import com.example.insights.security.Caller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Validates query parameters and turns them into a service query. */
public final class MetricsRequestMapper {

    private MetricsRequestMapper() {
    }

    public static MetricsQuery toQuery(Caller caller, String campaignId, String metricNames, MetricsWindow window) {

        return new MetricsQuery(caller.tenantId(),
            new CampaignId(campaignId),
            new TimeRange(required(window.from(), "from"), required(window.to(), "to")),
            interval(required(window.interval(), "interval")),
            metrics(metricNames),
            zone(required(window.timezone(), "timezone")));
    }

    private static Interval interval(String name) {
        return Interval.fromIntervalName(name).orElseThrow(() -> new IllegalArgumentException("interval must be hour or day, not " + name));
    }

    private static LocalDateTime required(LocalDateTime value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("missing required parameter: " + name);
        }
        return value;
    }

    private static Set<Metric> metrics(String metricList) {
        Set<Metric> metrics = EnumSet.noneOf(Metric.class);

        for (String name : List.of(metricList.split(","))) {
            String trimmed = name.trim();
            metrics.add(Metric.fromMetricName(trimmed).orElseThrow(
                () -> new IllegalArgumentException("unknown metric: " + trimmed)));
        }
        return metrics;
    }

    private static ZoneId zone(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            throw new IllegalArgumentException("unknown timezone: " + timezone);
        }
    }

    private static String required(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("missing required parameter: " + name);
        }
        return value;
    }
}
