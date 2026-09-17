package com.example.insights.domain;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;


public record MetricSeries(CampaignId campaignId,
                           ZoneId zone,
                           Interval interval,
                           OffsetDateTime asOf,
                           List<MetricPeriod> periods,
                           Map<Metric, IntervalMetric> totals) {

    public MetricSeries {
        periods = List.copyOf(periods);
        totals = Map.copyOf(totals);
    }
}
