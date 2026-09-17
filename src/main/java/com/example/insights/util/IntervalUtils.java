package com.example.insights.util;

import com.example.insights.domain.Interval;
import com.example.insights.domain.TimeRange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public final class IntervalUtils {

    private IntervalUtils() {
    }

    /** Row starts for the range, rounded out to whole intervals. */
    public static List<Instant> intervalStarts(TimeRange range, Interval interval, ZoneId zone) {
        Instant start = intervalStart(range.from(), interval, zone);
        Instant last = intervalStart(range.to(), interval, zone);
        List<Instant> starts = new ArrayList<>();

        while (!start.isAfter(last)) {
            starts.add(start);
            start = intervalEnd(start, interval, zone);
        }
        return starts;
    }

    public static Instant intervalStart(LocalDateTime local, Interval interval, ZoneId zone) {
        LocalDateTime rounded = switch (interval) {
            case HOUR -> local.truncatedTo(ChronoUnit.HOURS);
            case DAY -> local.toLocalDate().atStartOfDay();
        };
        return rounded.atZone(zone).toInstant();
    }

    public static Instant intervalEnd(Instant start, Interval interval, ZoneId zone) {
        return switch (interval) {
            case HOUR -> start.plus(1, ChronoUnit.HOURS);
            // Use the next local midnight so DST changes still land on midnight.
            case DAY -> start.atZone(zone).toLocalDate().plusDays(1).atStartOfDay(zone).toInstant();
        };
    }
}
