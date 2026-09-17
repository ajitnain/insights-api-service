package com.example.insights.security;

import com.example.insights.domain.TenantId;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix = "insights")
public record DemoTokens(List<DemoToken> demoTokens) {

    public Caller caller(String token) {
        for (DemoToken demoToken : demoTokens) {
            if (demoToken.token().equals(token)) {
                return new Caller(new TenantId(demoToken.tenant()));
            }
        }
        return null;
    }
}
