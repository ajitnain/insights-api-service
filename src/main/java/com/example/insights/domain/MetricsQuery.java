package com.example.insights.domain;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Set;

public record MetricsQuery(TenantId tenantId,
                           CampaignId campaignId,
                           TimeRange range,
                           Interval interval,
                           Set<Metric> metrics,
                           ZoneId zone) {

    public MetricsQuery {
        if (tenantId == null || campaignId == null || range == null
                || interval == null || zone == null) {
            throw new IllegalArgumentException(
                    "tenant, campaign, range, interval and zone are required");
        }
        if (metrics.isEmpty()) {
            throw new IllegalArgumentException("at least one metric must be requested");
        }
        metrics = EnumSet.copyOf(metrics);
    }
}
