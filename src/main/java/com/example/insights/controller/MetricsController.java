package com.example.insights.controller;

import com.example.insights.controller.helper.MetricsRequestMapper;
import com.example.insights.controller.helper.MetricsResponseMapper;
import com.example.insights.security.TenantContext;
import com.example.insights.controller.dto.MetricsResponse;
import com.example.insights.controller.dto.MetricsWindow;
import com.example.insights.domain.Metric;
import com.example.insights.domain.MetricSeries;
import com.example.insights.domain.MetricsQuery;
import com.example.insights.services.MetricsQueryService;
import com.example.insights.util.QueryLimitUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Metrics read endpoints. The tenant always comes from the bearer token. */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Campaign metrics", description = "Impressions, clicks and click-to-basket")
public class MetricsController {

    private final MetricsQueryService metrics;

    @Operation(summary = "All three metrics over a date range, each with its event count and unique customer count")
    @GetMapping("/v1/campaigns/{campaignId}/metrics")
    public ResponseEntity<MetricsResponse> metrics(
        @PathVariable String campaignId,
        @RequestParam(name = "metrics", defaultValue = "impressions,clicks,click_to_basket")
        String metricNames,
        @ParameterObject MetricsWindow window) {

        return process(campaignId, metricNames, window);
    }

    @Operation(summary = "Impressions over the date range, with the unique customer count")
    @GetMapping("/ad/{campaignId}/impressions")
    public ResponseEntity<MetricsResponse> impressions(@PathVariable String campaignId,
                                                       @ParameterObject MetricsWindow window) {

        return process(campaignId, Metric.IMPRESSIONS.metricName(), window);
    }

    @Operation(summary = "Clicks over the date range, with the unique customer count")
    @GetMapping("/ad/{campaignId}/clicks")
    public ResponseEntity<MetricsResponse> clicks(@PathVariable String campaignId,
                                                  @ParameterObject MetricsWindow window) {

        return process(campaignId, Metric.CLICKS.metricName(), window);
    }

    @Operation(summary = "Click-to-basket events over the date range, with the unique customer count")
    @GetMapping("/ad/{campaignId}/clickToBasket")
    public ResponseEntity<MetricsResponse> clickToBasket(@PathVariable String campaignId,
                                                         @ParameterObject MetricsWindow window) {

        return process(campaignId, Metric.CLICK_TO_BASKET.metricName(), window);
    }


    private ResponseEntity<MetricsResponse> process(String campaignId, String metricNames, MetricsWindow window) {

        log.info("received request to get campaign metrics, campaignId: {}, metrics: {}, window: {}",
                campaignId, metricNames, window);

        MetricsQuery query = MetricsRequestMapper.toQuery(TenantContext.require(), campaignId, metricNames, window);

        QueryLimitUtils.check(query);

        MetricSeries series = metrics.execute(query);

        log.info("returning campaign metrics, campaignId: {}, rows: {}",
                campaignId, series.periods().size());

        return ResponseEntity.ok(MetricsResponseMapper.toResponse(series));
    }
}
