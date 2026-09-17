package com.example.insights.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public record TimeRange(LocalDateTime from, LocalDateTime to) {

    public TimeRange {
        if (from == null || to == null) {
            throw new IllegalArgumentException("both 'from' and 'to' are required");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("'to' is before 'from': " + from + ".." + to);
        }
    }

    public long lengthInDays() {
        return ChronoUnit.DAYS.between(from.toLocalDate(), to.toLocalDate()) + 1;
    }
}
