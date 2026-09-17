package com.example.insights.domain;

import java.util.Arrays;
import java.util.Optional;

/** Row size and maximum range for that size. */
public enum Interval {

    HOUR("hour", 7),
    DAY("day", 90);

    private final String intervalName;
    private final int maxSpanInDays;

    Interval(String intervalName, int maxSpanInDays) {
        this.intervalName = intervalName;
        this.maxSpanInDays = maxSpanInDays;
    }

    public String intervalName() {
        return intervalName;
    }

    public int maxSpanInDays() {
        return maxSpanInDays;
    }

    public static Optional<Interval> fromIntervalName(String name) {
        return Arrays.stream(values())
                .filter(interval -> interval.intervalName.equalsIgnoreCase(name))
                .findFirst();
    }
}
