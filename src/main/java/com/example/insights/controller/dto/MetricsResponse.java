package com.example.insights.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

/** API response with row starts in the caller's time zone. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MetricsResponse(String campaignId,
                              String timezone,
                              String interval,
                              OffsetDateTime asOf,
                              List<PeriodResponse> series,
                              List<ValueResponse> totals) {}
