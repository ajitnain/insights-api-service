package com.example.insights.controller.dto;

import java.time.OffsetDateTime;
import java.util.List;

/** One row of the series. */
public record PeriodResponse(OffsetDateTime start,
                             double customerCountEstimateError,
                             List<ValueResponse> metrics) {}
