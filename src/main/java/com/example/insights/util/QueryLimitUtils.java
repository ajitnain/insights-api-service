package com.example.insights.util;

import com.example.insights.domain.MetricsQuery;
import com.example.insights.domain.TimeRange;

import java.time.LocalDate;


public final class QueryLimitUtils {

    public static final int RETENTION_IN_DAYS = 90;

    private QueryLimitUtils() {
    }

    public static void check(MetricsQuery query) {
        TimeRange range = query.range();

        LocalDate earliest = LocalDate.now(query.zone()).minusDays(RETENTION_IN_DAYS - 1);

        if (range.from().toLocalDate().isBefore(earliest)) {
            throw new IllegalArgumentException("data is kept for " + RETENTION_IN_DAYS + " days, so from cannot be before " + earliest);
        }
        if (range.lengthInDays() > query.interval().maxSpanInDays()) {
            throw new IllegalArgumentException(query.interval().intervalName() + " interval allows at most "
                    + query.interval().maxSpanInDays() + " days, asked for " + range.lengthInDays());
        }
    }
}
