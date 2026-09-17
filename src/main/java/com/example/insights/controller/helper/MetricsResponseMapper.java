package com.example.insights.controller.helper;

import com.example.insights.controller.dto.MetricsResponse;
import com.example.insights.controller.dto.PeriodResponse;
import com.example.insights.controller.dto.ValueResponse;
import com.example.insights.domain.Metric;
import com.example.insights.domain.MetricPeriod;
import com.example.insights.domain.MetricSeries;
import com.example.insights.domain.IntervalMetric;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Maps service results to the API response. */
public final class MetricsResponseMapper {

    private MetricsResponseMapper() {
    }

    public static MetricsResponse toResponse(MetricSeries series) {
        ZoneId zone = series.zone();

        List<PeriodResponse> rows = series.periods().stream().map(period -> toPeriod(period, zone)).toList();
        List<ValueResponse> totals = toValues(series.totals());

        return new MetricsResponse(
                series.campaignId().value(),
                zone.getId(),
                series.interval().intervalName(),
                series.asOf(),
                rows,
                totals);
    }

    private static PeriodResponse toPeriod(MetricPeriod period, ZoneId zone) {
        double error = period.customerCountEstimateError();
        OffsetDateTime start = period.start().atZone(zone).toOffsetDateTime();
        List<ValueResponse> metrics = toValues(period.values());

        return new PeriodResponse(start, roundTo4dp(error), metrics);
    }

    private static List<ValueResponse> toValues(Map<Metric, IntervalMetric> values) {
        List<ValueResponse> response = new ArrayList<>(values.size());

        values.forEach((metric, value) -> response.add(
                new ValueResponse(metric.metricName(), value.customers().value(), value.eventCount())));

        return response;
    }

    private static double roundTo4dp(double value) {
        return Math.round(value * 10_000d) / 10_000d;
    }
}
