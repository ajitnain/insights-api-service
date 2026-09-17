package com.example.insights.domain;

import java.util.Arrays;
import java.util.Optional;


public enum Metric {

    IMPRESSIONS("impressions"),
    CLICKS("clicks"),
    CLICK_TO_BASKET("click_to_basket");

    private final String metricName;

    Metric(String metricName) {
        this.metricName = metricName;
    }

    public String metricName() {
        return metricName;
    }

    public static Optional<Metric> fromMetricName(String name) {
        return Arrays.stream(values())
                .filter(metric -> metric.metricName.equalsIgnoreCase(name))
                .findFirst();
    }
}
