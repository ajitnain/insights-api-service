package com.example.insights.domain;

/** One metric value for one interval. */
public record IntervalMetric(CustomerSketch customerSketch, long eventCount) {

    /** Distinct customers, capped at the event count. */
    public CustomerCount customers() {
        CustomerCount counted = customerSketch.toCount();

        return counted.value() <= eventCount ? counted : CustomerCount.of(eventCount, counted.relativeError());
    }

    public static IntervalMetric zero() {
        return new IntervalMetric(CustomerSketch.empty(), 0);
    }
}
